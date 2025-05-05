package travel.kiri.backend.algorithm;

import java.util.*;

public class FloydWarshall extends ShorestPathStrategy {

    private final int numOfNodes;
    private double[][] dist;
    private int[][] parent;;

    public FloydWarshall(Graph graph, int startNode, int finishNode, double multiplierWalking, double penaltyTransfer) {
        super(graph, startNode, finishNode, multiplierWalking, penaltyTransfer);
        this.numOfNodes = graph.size();
        System.out.println(numOfNodes);
        dist = new double[numOfNodes][numOfNodes];
        parent = new int[numOfNodes][numOfNodes];
    }

    @Override
    public double runAlgorithm(Set<String> trackTypeIdBlacklist) {
        for (int i = 0; i < numOfNodes; i++) {
            for (int j = 0; j < numOfNodes; j++) {
                if (i == j) {
                    dist[i][j] = 0;
                    parent[i][j] = -1;
                } else {
                    dist[i][j] = Double.POSITIVE_INFINITY;
                    parent[i][j] = -1;
                }
            }

            for (GraphEdge edge : graph.get(i).edges) {
                if (trackTypeIdBlacklist == null || graph.get(edge.node).track == null
                        || !trackTypeIdBlacklist.contains(graph.get(edge.node).track.trackTypeId)) {

                    double weight = calculateWeight(i, edge);
                    dist[i][edge.node] = weight;
                    parent[i][edge.node] = i;
                }
            }
        }

        for (int k = 0; k < numOfNodes; k++) {
            for (int i = 0; i < numOfNodes; i++) {
                for (int j = 0; j < numOfNodes; j++) {
                    if (dist[i][k] + dist[k][j] < dist[i][j]) {
                        dist[i][j] = dist[i][k] + dist[k][j];
                        parent[i][j] = parent[k][j];
                    }
                }
            }
        }

        return dist[startNode][finishNode];
    }

    private double calculateWeight(int fromIndex, GraphEdge edge) {
        Track trackFrom = graph.get(fromIndex).getTrack();
        Track trackTo = graph.get(edge.node).getTrack();

        if (trackFrom == null || trackTo == null) {
            return multiplierWalking * edge.weight;
        } else if (!trackFrom.getTrackId().equals(trackTo.getTrackId())) {
            return multiplierWalking * (penaltyTransfer + edge.weight);
        } else {
            return edge.weight * trackFrom.penalty;
        }
    }

    @Override
    public int getParent(int node) {
        return parent[startNode][node];
    }

    @Override
    public double getDistance(int node) {
        return dist[startNode][node];
    }
}