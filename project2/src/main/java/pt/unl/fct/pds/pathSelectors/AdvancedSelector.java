package pt.unl.fct.pds.pathSelectors;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pt.unl.fct.pds.model.Node;
import pt.unl.fct.pds.utils.ConsensusParser;

public class AdvancedSelector {

    public ConsensusParser parser;
    public Node exit;
    public Node guard;
    public Node middle;
    
    private Map<Node, Double> weight;
    private Double alpha; // TODO: parse from document
    private Double beta; // TODO: parse from document

    public AdvancedSelector(ConsensusParser parser) {
        this.parser = parser;

        alpha = 0.1;
        beta = 0.1;
    }

    public List<Node> selectPath() {

        weight = new HashMap<>();

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

        double totalWeight = 0;
        double currWeight = 0.0;
        for (Node node : fastNodes) {
            currWeight = node.getBandwidth() + 0.0;
            
            totalWeight += currWeight;
            weight.put(node, currWeight);
        }

        return sampleByWeight(fastNodes, totalWeight);
    }

    private Node selectGuard() {
        List<Node> guardNodes = parser.filterByFlag("Guard");
        // TODO: remove those with the same family
        guardNodes.removeIf(node -> parser.sameSubnet(node, exit));

        // TODO: Prioritize relays from persistent SAMPLED GUARDS and CONFIRMED GUARDS
        // sets

        double totalWeight = 0.0;
        double currWeight = 0.0;
        for (Node node : guardNodes) {
            if (!node.getCountry().equals(exit.getCountry())) 
                currWeight = node.getBandwidth() * (1 + alpha);   
            else 
                currWeight = node.getBandwidth() + 0.0;
            
            totalWeight += currWeight;
            weight.put(node, currWeight);
        }

        return sampleByWeight(guardNodes, totalWeight);
    }

    private Node selectMiddle() {
        List<Node> fastNodes = parser.filterByFlag("Fast");
        // TODO: Filter relays to remove those with the same family
        fastNodes.removeIf(node -> parser.sameSubnet(node, exit) || parser.sameSubnet(node, guard));

        double totalWeight = 0.0;
        double currWeight = 0.0;
        int c = 0;
        for (Node node : fastNodes) {
            String guardCountry = guard.getCountry();
            String exitCountry = exit.getCountry();
            String nodeCountry = node.getCountry();

            if (!nodeCountry.equals(guardCountry) && !guardCountry.equals(exitCountry)) //isto esta certo?
                c = 3;
            else if (!nodeCountry.equals(guardCountry) || !nodeCountry.equals(exitCountry))
                c = 2;
            else
                c = 1;

            currWeight = node.getBandwidth() * (1 + (beta * c));
            totalWeight += currWeight;           
            weight.put(node, currWeight);
        }
        return sampleByWeight(fastNodes, totalWeight);
    }

    private Node sampleByWeight(List<Node> nodes, double totalWeight) {
        Node heaviest = null;
        double bestWeight = 0.0;
        for (Node node : nodes) {
            double nodeWeight = weight.get(node) / totalWeight;
            if (nodeWeight > bestWeight) {
                bestWeight = nodeWeight;
                heaviest = node;
            }
        }
        return heaviest;
    }
}
