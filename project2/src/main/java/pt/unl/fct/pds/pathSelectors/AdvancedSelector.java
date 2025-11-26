package pt.unl.fct.pds.pathSelectors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pt.unl.fct.pds.model.Node;
import pt.unl.fct.pds.utils.ConsensusParser;

public class AdvancedSelector {

    public Node[] relays;
    public ConsensusParser parser;
    private Map<Node, Double> weight;

    private Double alpha; // TODO: parse from document
    private Double beta; // TODO: parse from document

    public AdvancedSelector(Node[] relays, ConsensusParser parser) {
        this.relays = relays;
        this.parser = parser;

        alpha = 0.1;
        beta = 0.1;
    }

    public Node[] selectPath() {

        weight = new HashMap<>();

        Node exit = selectExit();
        Node guard = selectGuard(exit, alpha);
        Node middle = selectMiddle();

        return new Node[] { exit, guard, middle };
    }

    private Node selectExit() {
        List<Node> fastNodes = parser.filterByFlag("Fast");
        // TODO: remove those witout suitable exit policy

        return sampleByWeight(fastNodes, new ArrayList<>());
    }

    private Node selectGuard(Node exit, double alpha) {
        List<Node> guardNodes = parser.filterByFlag("Guard");
        // TODO: remove those with the same family and 16 subnet as exit
        // TODO: Prioritize relays from persistent SAMPLED GUARDS and CONFIRMED GUARDS
        // sets

        List<Node> prioNodes = new ArrayList<>();
        for (Node node : guardNodes) {
            if (!node.getCountry().equals(exit.getCountry()))
                prioNodes.add(node);
        }
        return sampleByWeight(guardNodes, prioNodes);
    }

    private Node selectMiddle() {
        List<Node> fastNodes = parser.filterByFlag("Fast");
        // TODO: Filter relays to remove those with the same family and 16 subnet as the
        // exit and guard

        return sampleByWeight(fastNodes);
    }

    private Node sampleByWeight(List<Node> nodes, List<Node> prio) {
        int totalWeight = 0;
        Double currWeight = 0.0;
        for (Node node : nodes) {
            if (!prio.isEmpty() && prio.contains(node)) 
                currWeight = node.getBandwidth() * (1 + alpha);   
            else 
                currWeight = node.getBandwidth() + 0.0;
            
            totalWeight += currWeight;
            weight.put(node, currWeight);
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
