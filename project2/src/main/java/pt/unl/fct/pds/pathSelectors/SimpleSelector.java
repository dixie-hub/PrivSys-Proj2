package pt.unl.fct.pds.pathSelectors;

import java.util.ArrayList;
import java.util.List;

import pt.unl.fct.pds.model.Node;
import pt.unl.fct.pds.utils.ConsensusParser;

public class SimpleSelector {

    public ConsensusParser parser;
    private List<Node> consensus;

    private Node exit;
    private Node guard;
    private Node middle;

    public SimpleSelector(List<Node> consensus, ConsensusParser parser) {
        this.consensus = consensus;
        this.parser = parser;
    }

    public List<Node> selectPath() {
        List<Node> pathSelected = new ArrayList<>();

        exit = selectExit();
        pathSelected.add(exit);

        guard = selectGuard();
        pathSelected.add(guard);

        middle = selectMiddle();
        pathSelected.add(middle);


        return pathSelected;
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
            double currWeight = (double) node.getBandwidth() / (double) totalWeight;
            if (currWeight > bestWeight) {
                bestWeight = currWeight;
                heaviest = node;
            }
        }
        return heaviest;
    }
}
