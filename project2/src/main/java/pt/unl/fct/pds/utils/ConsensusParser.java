package pt.unl.fct.pds.utils;

import pt.unl.fct.pds.model.Node;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ConsensusParser {
    String filename;

    public ConsensusParser() {
    }

    public ConsensusParser(String filename) {
        this.filename = filename;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

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

        File file = new File("relays.txt");
        List<Node> relays = new ArrayList<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-ddHH:mm:ss");
        
        try (BufferedReader in = new BufferedReader(new FileReader(file))) {
            
            if (!in.ready()) {

                return null;  
            } 
 
            String line;
            while (!(line = in.readLine()).isEmpty()) {
                
                String[] inputTokens = line.split(" ");

                Node relay = new Node();
                
                switch (inputTokens[0]) {
                    case "r":

                        relay.setNickname(inputTokens[1]);
                        relay.setFingerprint(inputTokens[2]);
                        relay.setDigest(inputTokens[3]);
                        relay.setTimePublished(LocalDateTime.parse(inputTokens[4]+inputTokens[5], formatter));
                        relay.setIpAddress(inputTokens[4]);
                        relay.setOrPort(Integer.parseInt(inputTokens[4]));
                        relay.setDirPort(Integer.parseInt(inputTokens[5]));

                        break;
                    case "a":
                        //TODO
                        break;
                    case "s":

                        List<String> flags = new ArrayList<>();
                        for (String flag : inputTokens){
                            flags.add(flag);
                        }
                        relay.setFlags(flags.toArray(new String[0]));
                
                        break;
                    case "v":
                        relay.setVersion(inputTokens[2]);
                        break;
                    case "pr":
                        //TODO
                        break;
                    case "w":
                        //TODO
                        break;
                    case "p":
                        //TODO
                        break;
                    default:
                        System.out.println("Invalid input.");
                        break;
                }
            }
        } catch (Exception e) {
            System.out.println("File unable to read.");
            e.printStackTrace();
        }

        return null;
    }
}
