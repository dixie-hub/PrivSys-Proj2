package pt.unl.fct.pds;

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
        System.out.println("Welcome to the Circuit Simulator!");

        try {
            ConsensusParser parser = new ConsensusParser();

            SimpleSelector simpleSelector = new SimpleSelector(parser);
            System.out.println("First circuit: " + simpleSelector.selectPath().toString());
            AdvancedSelector advancedSelector = new AdvancedSelector(parser);
            System.out.println("Second circuit: " + advancedSelector.selectPath().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
