package pt.unl.fct.pds.pathSelectors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import pt.unl.fct.pds.model.Node;
import pt.unl.fct.pds.utils.ConsensusParser;

public interface Selector {

    public List<Node> selectPath();
}