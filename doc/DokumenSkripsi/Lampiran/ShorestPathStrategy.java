package travel.kiri.backend.algorithm;

import java.util.*;

public abstract class ShorestPathStrategy {

    protected List<GraphNode> graph;
    protected int startNode;
    protected int finishNode;
    protected double multiplierWalking;
    protected double penaltyTransfer;
    public static final int NULL_NODE = -1;

    public ShorestPathStrategy(List<GraphNode> graph, int startNode, int finishNode, double multiplierWalking,
            double penaltyTransfer) {
        this.graph = graph;
        this.startNode = startNode;
        this.finishNode = finishNode;
        this.multiplierWalking = multiplierWalking;
        this.penaltyTransfer = penaltyTransfer;
    }

    public abstract double runAlgorithm(Set<String> trackTypeIdBlacklist);

    public abstract int getParent(int node);

    public abstract double getDistance(int node);
}