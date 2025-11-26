package pt.unl.fct.pds.pathSelectors;

import java.util.List;

import pt.unl.fct.pds.model.Node;
import pt.unl.fct.pds.utils.ConsensusParser;

public class SimpleSelector {

    public ConsensusParser parser;

    private Node exit;
    private Node guard;
    private Node middle;

    public SimpleSelector(ConsensusParser parser) {
        this.parser = parser;
    }

    public Node[] selectPath() {
        exit = selectExit();
        guard = selectGuard();
        middle = selectMiddle();

        return new Node[] { exit, guard, middle };
    }

    private Node selectExit() {
        List<Node> fastNodes = parser.filterByFlag("Fast");
        fastNodes.removeIf(node -> !node.getExitPolicy().contains("accept"));

        return sampleByWeight(fastNodes);
    }

    private Node selectGuard() {
        List<Node> guardNodes = parser.filterByFlag("Guard");
        // TODO: remove those with the same family
        guardNodes.removeIf(node -> parser.sameSubnet(node, exit));
        // TODO: Prioritize relays from persistent SAMPLED GUARDS and CONFIRMED GUARDS
        // sets

        return sampleByWeight(guardNodes);
    }

    private Node selectMiddle() {
        List<Node> fastNodes = parser.filterByFlag("Fast");
        // TODO: Filter relays to remove those with the same family
        fastNodes.removeIf(node -> parser.sameSubnet(node, exit) || parser.sameSubnet(node, guard));

        return sampleByWeight(fastNodes);
    }

    private Node sampleByWeight(List<Node> nodes) {
        int totalWeight = 0;
        for (Node node : nodes) {
            totalWeight += node.getBandwidth();
        }

        Node heaviest = null;
        double bestWeight = 0.0;
        for (Node node : nodes) {
            double currWeight = node.getBandwidth() / totalWeight;
            if (currWeight > bestWeight) {
                bestWeight = currWeight;
                heaviest = node;
            }
        }
        return heaviest;
    }
}
