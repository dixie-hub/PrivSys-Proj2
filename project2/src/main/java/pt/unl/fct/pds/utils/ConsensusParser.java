package pt.unl.fct.pds.utils;

import pt.unl.fct.pds.model.Node;
import java.util.Arrays;

public class ConsensusParser {
    String filename;
    
    public ConsensusParser() {}
    public ConsensusParser(String filename) {this.filename = filename;}

    public String getFilename() {return filename;}
    public void setFilename(String filename) {this.filename = filename;}

    public Node[] parseConsensus() {
        //TODO: Implement! For now just returning null.

        /* STEP 1 
         *      -> Ler o document e retirar as informações importantes
         * STEP 2        
         *      -> Usar uma função de geo location para retirar o pais do relay observado
         * STEP 3
         *      -> Criar um objeto Node para guardar as informações dos relays
         * STEP 4
         *      -> Colocar tudo numa lista de devolver essa lista
         */
        
        return null;
    }
}
