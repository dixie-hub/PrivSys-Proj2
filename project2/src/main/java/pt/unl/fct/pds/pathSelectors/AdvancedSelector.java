package pt.unl.fct.pds.pathSelectors;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import pt.unl.fct.pds.model.Node;
import pt.unl.fct.pds.utils.ConsensusParser;

public class AdvancedSelector {

    public ConsensusParser parser;
    public Node exit;
    public Node guard;
    public Node middle;
    private Random random;
    
    private Map<Node, Double> weight;
    private static Double ALPHA = 0.1;
    private static Double BETA = 0.1; 

    public AdvancedSelector(ConsensusParser parser) {
        this.parser = parser;
        this.random = new Random();
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

        double totalWeight = 0.0;
        double currWeight = 0.0;
        for (Node node : fastNodes) {
            currWeight = (double) node.getBandwidth();
            
            totalWeight += currWeight;
            weight.put(node, currWeight);
        }

        return sampleByWeight(fastNodes, totalWeight);
    }

    private Node selectGuard() {
        List<Node> guardNodes = parser.filterByFlag("Guard");
        // TODO: remove those with the same family
        guardNodes.removeIf(node -> parser.sameSubnet(node, exit));
        List<Node> guardNodesFilterd = filterByFamily(exit, guardNodes);



        double totalWeight = 0.0;
        double currWeight = 0.0;
        for (Node node : guardNodesFilterd) {
            if (!node.getCountry().equals(exit.getCountry())) 
                currWeight = node.getBandwidth() * (1 + ALPHA);   
            else 
                currWeight = (double) node.getBandwidth();
            
            totalWeight += currWeight;
            weight.put(node, currWeight);
        }

        return sampleByWeight(guardNodesFilterd, totalWeight);
    }

    private Node selectMiddle() {
        List<Node> fastNodes = parser.filterByFlag("Fast");
        // TODO: Filter relays to remove those with the same family
        fastNodes.removeIf(node -> parser.sameSubnet(node, exit) || parser.sameSubnet(node, guard));

        List<Node> middleFilteredExit = filterByFamily(exit, fastNodes);
        List<Node> middleFilteredGuard = filterByFamily(guard, middleFilteredExit);




        double totalWeight = 0.0;
        double currWeight = 0.0;
        int c = 0;
        String guardCountry = guard.getCountry();
        String exitCountry = exit.getCountry();
        
        for (Node node : middleFilteredGuard) {
            String nodeCountry = node.getCountry();

            if (!nodeCountry.equals(guardCountry) && !guardCountry.equals(exitCountry) && !nodeCountry.equals(exitCountry))
                c = 3;
            else if (!nodeCountry.equals(guardCountry) || !nodeCountry.equals(exitCountry))
                c = 2;
            else
                c = 1;

            currWeight = node.getBandwidth() * (1 + (BETA * c));
            totalWeight += currWeight;           
            weight.put(node, currWeight);
        }
        return sampleByWeight(middleFilteredGuard, totalWeight);
    }

    private Node sampleByWeight(List<Node> nodes, double totalWeight) {
        
        double rWeigth = random.nextDouble(totalWeight);

        double cummWeigth = 0.0;

        for (Node node : nodes){
            cummWeigth += weight.get(node);
            if(cummWeigth > rWeigth){
                return node;
            }
        }
        return null;
    }

    private List<Node> filterByFamily(Node node, List<Node> nodes){ 
        List<String> nodeFamily = node.getFamily();
        List<Node> result = new ArrayList<>();

        for (Node n : nodes){
            List<String> currentFamily = n.getFamily();
            if (currentFamily == null || nodeFamily == null || !nodeFamily.stream().anyMatch(currentFamily::contains)) {
                result.add(n);
            }
        }
        return result;
    }
}
