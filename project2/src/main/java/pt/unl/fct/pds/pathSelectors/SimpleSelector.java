package pt.unl.fct.pds.pathSelectors;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import pt.unl.fct.pds.model.Node;
import pt.unl.fct.pds.utils.ConsensusParser;

public class SimpleSelector implements Selector {

    public ConsensusParser parser;
    private List<Node> consensus;

    private Node exit;
    private Node guard;
    private Node middle;
    private Random random;

    public SimpleSelector(List<Node> consensus, ConsensusParser parser) {
        this.consensus = consensus;
        this.parser = parser;
        this.random = new Random();
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
        List<Node> fastNodes = new ArrayList(parser.filterByFlag("Fast"));
        fastNodes.removeIf(node -> !node.getExitPolicy().contains("accept"));
        return sampleByWeight(fastNodes);
    }

    private Node selectGuard() {
        List<Node> guardNodes = new ArrayList(parser.filterByFlag("Guard"));
        guardNodes.removeIf(node -> parser.sameSubnet(node, exit));
        List<Node> guardNodesFilterd = filterByFamily(exit, guardNodes);

        return sampleByWeight(guardNodesFilterd);
    }

    private Node selectMiddle() {
        List<Node> fastNodes = new ArrayList(parser.filterByFlag("Fast"));
        fastNodes.removeIf(node -> parser.sameSubnet(node, exit) || parser.sameSubnet(node, guard));
        List<Node> middleFilteredExit = filterByFamily(exit, fastNodes);
        List<Node> middleFilteredGuard = filterByFamily(guard, middleFilteredExit);

        return sampleByWeight(middleFilteredGuard);
    }

    private Node sampleByWeight(List<Node> nodes) {
        int totalWeight = 0;
        for (Node node : nodes) {
            totalWeight += node.getBandwidth();
        }

        int rWeigth = random.nextInt(totalWeight);

        int cummWeigth = 0;

        for (Node node : nodes){
            cummWeigth += node.getBandwidth();
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
