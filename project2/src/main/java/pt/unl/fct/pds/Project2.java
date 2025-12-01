package pt.unl.fct.pds;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jfree.chart.JFreeChart;

import pt.unl.fct.pds.model.Node;
import pt.unl.fct.pds.pathSelectors.AdvancedSelector;
import pt.unl.fct.pds.pathSelectors.SimpleSelector;
import pt.unl.fct.pds.pathSelectors.Selector;

import pt.unl.fct.pds.utils.ConsensusParser;
import pt.unl.fct.pds.utils.LineChart;

/**
 * Application for Tor Path Selection alternatives.
 *
 */
public class Project2 {

    public static List<Integer> simpleBandwidth;
    public static List<Integer> advancedBandwidth;

    public static void main(String[] args) {
        // Here we write our logic to choose circuits!
        System.out.println("\nWelcome to the Circuit Simulator!\n");

        try {
            ConsensusParser parser = new ConsensusParser();
            List<Node> consensus = parser.parseConsensus();

            int nrTests = 1000;

            simpleBandwidth = new ArrayList<>();
            advancedBandwidth = new ArrayList<>();

            for (int i = 0; i < 2; i++) {
                Selector selector = null;
                String text = "";
                
                if (i == 0){
                    selector = new SimpleSelector(consensus, parser);
                    text = "Given entropy values for the simple relay selector: ";
                }

                if (i == 1){
                    selector = new AdvancedSelector(parser);
                    text = "Given entropy values for the advanced relay selector: ";
                }
                    
                List<Double> entropies = getShannonEntropy(selector, nrTests);

                System.out.println(text);
                for (int f = 0; f < 4; f++) {
                    if (f == 0) {
                        System.out.println("TOTAL ENTROPY");
                        System.out.println("-> " + entropies.get(f));
                    }
                    if (f == 1) {
                        System.out.println("GUARD ENTROPY");
                        System.out.println("-> " + entropies.get(f));
                    }
                    if (f == 2) {
                        System.out.println("MIDDLE ENTROPY");
                        System.out.println("-> " + entropies.get(f));
                    }
                    if (f == 3) {
                        System.out.println("EXIT ENTROPY");
                        System.out.println("-> " + entropies.get(f));
                    }
                }

            }

            LineChart chartCreator = new LineChart();
            
            JFreeChart chart = chartCreator.createLineChart(simpleBandwidth, advancedBandwidth);
            chartCreator.convertPNG(chart, "bandwidth-chart.png");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Integer zeroOrNull(Integer value) {
        return value == null ? 0 : value;
    }

    private static List<Double> shannonEntropy(Map<Node, Integer> total, Map<Node, Integer> guard,
            Map<Node, Integer> middle,
            Map<Node, Integer> exit, int nrCircuits) {

        Map<Node, Double> probTotal = new HashMap<>();
        Map<Node, Double> probGuard = new HashMap<>();
        Map<Node, Double> probMiddle = new HashMap<>();
        Map<Node, Double> probExit = new HashMap<>();

        for (Node n : total.keySet()) {
            probTotal.put(n, total.get(n) / (3.0 * nrCircuits));

            if (guard.containsKey(n))
                probGuard.put(n, guard.get(n) / (double) nrCircuits);
            if (middle.containsKey(n))
                probMiddle.put(n, middle.get(n) / (double) nrCircuits);
            if (exit.containsKey(n))
                probExit.put(n, exit.get(n) / (double) nrCircuits);
        }

        double sum;
        List<Double> entropy = new ArrayList<>(4);

        entropy.add(calculateEntropy(probTotal));
        entropy.add(calculateEntropy(probGuard));
        entropy.add(calculateEntropy(probMiddle));
        entropy.add(calculateEntropy(probExit));

        return entropy;
    }

    private static double calculateEntropy(Map<Node, Double> probs) {

        double sum = 0.0;

        for (Node n : probs.keySet()) {
            double prob = probs.get(n);
            sum += (prob * (Math.log(prob) / Math.log(2)));
        }

        return sum * -1;
    }

    private static List<Double> getShannonEntropy(Selector selector, int nrTests) {
        List<Node> circuit;

        Map<Node, Integer> nrTimesChosenTotal = new HashMap<>();
        Map<Node, Integer> nrTimesChosenGuard = new HashMap<>();
        Map<Node, Integer> nrTimesChosenMiddle = new HashMap<>();
        Map<Node, Integer> nrTimesChosenExit = new HashMap<>();

        for (int i = 0; i < nrTests; i++) {
            circuit = selector.selectPath();

            if (selector instanceof SimpleSelector)
                simpleBandwidth.add(lowestBandwidth(circuit));
            if (selector instanceof AdvancedSelector)
                advancedBandwidth.add(lowestBandwidth(circuit)); 
            
            for (int f = 0; f < 3; f++) {
                Node curr = circuit.get(f);
                if (f == 0)
                    nrTimesChosenExit.put(curr, zeroOrNull(nrTimesChosenExit.get(curr)) + 1);
                if (f == 1)
                    nrTimesChosenGuard.put(curr, zeroOrNull(nrTimesChosenGuard.get(curr)) + 1);
                if (f == 2)
                    nrTimesChosenMiddle.put(curr, zeroOrNull(nrTimesChosenMiddle.get(curr)) + 1);

                nrTimesChosenTotal.put(curr, zeroOrNull(nrTimesChosenTotal.get(curr)) + 1);
            }
        }

        return shannonEntropy(nrTimesChosenTotal, nrTimesChosenGuard, nrTimesChosenMiddle, nrTimesChosenExit, nrTests);
    }

    private static int lowestBandwidth(List<Node> nodes) {
        int lowest = Integer.MAX_VALUE;
        for (Node n : nodes) {
            int currBandwidth = n.getBandwidth();
            if (currBandwidth < lowest)
                lowest = currBandwidth;
        }
        return lowest;
    }
}
