package pt.unl.fct.pds;

import pt.unl.fct.pds.model.Node;
import pt.unl.fct.pds.pathSelectors.SimpleSelector;

import java.io.IOException;

import pt.unl.fct.pds.model.Circuit;
import pt.unl.fct.pds.utils.ConsensusParser;


/**
 * Application for Tor Path Selection alternatives.
 *
 */
public class Project2 
{
    private static Node[] relays;

    public static void main( String[] args )
    {
        // Here we write our logic to choose circuits!
        System.out.println("Welcome to the Circuit Simulator!");

        try {
            ConsensusParser parser = new ConsensusParser();
            relays = (Node[]) parser.parseConsensus().toArray();

            SimpleSelector selector = new SimpleSelector(relays, parser);
            System.out.println("First circuit: " + selector.selectPath().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
