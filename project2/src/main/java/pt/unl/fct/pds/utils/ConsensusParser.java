package pt.unl.fct.pds.utils;

import pt.unl.fct.pds.model.Node;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.io.BufferedReader;
import java.io.FileReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;

public class ConsensusParser {
    String filename;
    private final DatabaseReader dbReader;

    public ConsensusParser() throws IOException {
        File database = new File("GeoLite2-City.mmdb");
        this.dbReader = new DatabaseReader.Builder(database).build();
    }

    public ConsensusParser(String filename) throws IOException {
        this.filename = filename;
        File database = new File("GeoLite2-City.mmdb");
        this.dbReader = new DatabaseReader.Builder(database).build();
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public List<Node> parseConsensus() {
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

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-ddHH:mm:ss");
        
        try (BufferedReader in = new BufferedReader(new FileReader(file))) {
            
            if (!in.ready()) {
                return null;  
            } 
 
            String line = "";
            List<Node> relays = new ArrayList<>();
            Node relay = new Node();

            while (!(line = in.readLine()).isEmpty()) {
                
                String[] inputTokens = line.split(" ");
                
                switch (inputTokens[0]) {
                    case "r":
                        relay.setNickname(inputTokens[1]);
                        relay.setFingerprint(inputTokens[2]);
                        relay.setDigest(inputTokens[3]);
                        relay.setTimePublished(LocalDateTime.parse(inputTokens[4]+inputTokens[5], formatter));
                        relay.setIpAddress(inputTokens[6]);
                        
                        //Colocar pais de origem
                        InetAddress ipAddress = InetAddress.getByName(inputTokens[6]);
                        CityResponse response = dbReader.city(ipAddress);
                        relay.setCountry(response.getCountry().getName());

                        relay.setOrPort(Integer.parseInt(inputTokens[7]));
                        relay.setDirPort(Integer.parseInt(inputTokens[8]));
                        break;
                    case "a":
                        relay.setIpv6Address(inputTokens[1]);
                        break;
                    case "s":
                        String[] flags = Arrays.copyOfRange(inputTokens, 1, inputTokens.length);
                        relay.setFlags(flags);
                        break;
                    case "v":
                        relay.setVersion(inputTokens[2]);
                        break;
                    case "pr":
                        //Not needed for the project
                        break;
                    case "w":
                        relay.setBandwidth(Integer.valueOf(inputTokens[1].split("=")[1]));
                        break;
                    case "p":
                        relay.setExitPolicy(inputTokens[1]+inputTokens[2]);
                        relays.add(relay);
                        relay = new Node();
                        break;
                    default:
                        System.out.println("Invalid input.");
                        break;
                }
            }

        return relays;

        } catch (Exception e) {
            System.out.println("File unable to read.");
            e.printStackTrace();
            return null;
        }

    }
}
