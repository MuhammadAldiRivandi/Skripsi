package travel.kiri.backend.algorithm;

import java.util.*;

public class AStar extends ShorestPathStrategy {

    private NodeInfo[] nodeInfoLinks;
    private PriorityQueue<NodeInfo> openSet;

    public AStar(Graph graph, int startNode, int finishNode, double multiplierWalking, double penaltyTransfer) {
        super(graph, startNode, finishNode, multiplierWalking, penaltyTransfer);
        int numOfNodes = graph.size();
        this.nodeInfoLinks = new NodeInfo[numOfNodes];
        this.openSet = new PriorityQueue<>();

        for (int i = 0; i < numOfNodes; i++) {
            nodeInfoLinks[i] = new NodeInfo(i);
        }
    }

    @Override
    public double runAlgorithm(Set<String> trackTypeIdBlacklist) {
        nodeInfoLinks[startNode].g = 0;
        nodeInfoLinks[startNode].f = heuristic(startNode);
        openSet.add(nodeInfoLinks[startNode]);

        while (!openSet.isEmpty()) {
            NodeInfo current = openSet.poll();

            if (current.index == finishNode) {
                return nodeInfoLinks[finishNode].g;
            }

            for (GraphEdge edge : graph.get(current.index).getEdges()) {
                int neighbor = edge.node;
                if (trackTypeIdBlacklist != null
                        && trackTypeIdBlacklist.contains(graph.get(neighbor).getTrack().trackTypeId)) {
                    continue;
                }

                double newCost = current.g + calculateWeight(current, edge);

                if (newCost < nodeInfoLinks[neighbor].g) {
                    nodeInfoLinks[neighbor].g = newCost;
                    nodeInfoLinks[neighbor].f = newCost + heuristic(neighbor);
                    nodeInfoLinks[neighbor].parent = current.index;
                    openSet.add(nodeInfoLinks[neighbor]);
                }
            }
        }

        return Double.POSITIVE_INFINITY;
    }

    @Override
    public int getParent(int node) {
        return nodeInfoLinks[node].parent;
    }

    @Override
    public double getDistance(int node) {
        return nodeInfoLinks[node].g;
    }

    private double heuristic(int node) {
        LatLon currentPos = graph.get(node).getLocation();
        LatLon goalPos = graph.get(finishNode).getLocation();
        return currentPos.distanceTo(goalPos);
    }

    private double calculateWeight(NodeInfo currentNode, GraphEdge edge) {
        Track track1 = graph.get(currentNode.index).getTrack();
        Track track2 = graph.get(edge.node).getTrack();

        if (track1 == null || track2 == null) {
            return multiplierWalking * edge.weight;
        } else if (track1 != track2) {
            return multiplierWalking * (penaltyTransfer + edge.weight);
        } else {
            return edge.weight * track1.penalty;
        }
    }

    private static class NodeInfo implements Comparable<NodeInfo> {
        int index;
        double g = Double.POSITIVE_INFINITY;
        double f = Double.POSITIVE_INFINITY;
        int parent = NULL_NODE;

        NodeInfo(int index) {
            this.index = index;
        }

        @Override
        public int compareTo(NodeInfo other) {
            return Double.compare(this.f, other.f);
        }
    }
}
