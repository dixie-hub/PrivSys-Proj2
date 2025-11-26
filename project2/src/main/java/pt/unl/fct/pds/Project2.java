package pt.unl.fct.pds;

import java.util.List;

import pt.unl.fct.pds.model.Node;
import pt.unl.fct.pds.pathSelectors.AdvancedSelector;
import pt.unl.fct.pds.pathSelectors.SimpleSelector;

import pt.unl.fct.pds.utils.ConsensusParser;


/**
 * Application for Tor Path Selection alternatives.
 *
 */
public class Project2 
{

    public static void main( String[] args )
    {
        // Here we write our logic to choose circuits!
        System.out.println("\nWelcome to the Circuit Simulator!\n");

        try {
            ConsensusParser parser = new ConsensusParser();
            List<Node> consensus = parser.parseConsensus();

            SimpleSelector simpleSelector = new SimpleSelector(consensus, parser);

            System.out.println(" First circuit: " );
            List<Node> simpleSelectorRelays = simpleSelector.selectPath();
            for( Node n : simpleSelectorRelays){
                System.out.println("-> " + n.getNickname());
            }
            
            AdvancedSelector advancedSelector = new AdvancedSelector(parser);

            System.out.println("\n Second circuit: " );
            List<Node> advancedSelectorRelays = advancedSelector.selectPath();
            for( Node n : advancedSelectorRelays){
                System.out.println("-> " + n.getNickname());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
