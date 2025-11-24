package pt.unl.fct.pds.pathSelectors;

import java.util.List;

import pt.unl.fct.pds.model.Node;
import pt.unl.fct.pds.utils.ConsensusParser;

public class SimpleSelector {

    public Node[] relays;
    public ConsensusParser parser;

    public SimpleSelector(Node[] relays, ConsensusParser parser) {
        this.relays = relays;
        this.parser = parser;
    }

    public Node[] selectPath() {
        Node exit = selectExit();
        Node guard = selectGuard();
        Node middle = selectMiddle();

        return new Node[] { exit, guard, middle };
    }

    private Node selectExit() {
        List<Node> fastNodes = parser.filterByFlag("Fast");
        //TODO: remove those witout suitable exit policy

        return sampleByWeight(fastNodes);
    }

    private Node selectGuard() {
        List<Node> guardNodes = parser.filterByFlag("Guard");
        //TODO: remove those with the same family and 16 subnet as exit
        //TODO: Prioritize relays from persistent SAMPLED GUARDS and CONFIRMED GUARDS sets
        
        return sampleByWeight(guardNodes);
    }

    private Node selectMiddle() {
        List<Node> fastNodes = parser.filterByFlag("Fast");
        //TODO: Filter relays to remove those with the same family and 16 subnet as the exit and guard

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
