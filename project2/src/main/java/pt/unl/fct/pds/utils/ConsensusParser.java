package pt.unl.fct.pds.utils;

import pt.unl.fct.pds.model.Node;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.io.BufferedReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.AddressNotFoundException;
import com.maxmind.geoip2.model.CityResponse;

import org.torproject.descriptor.*;

public class ConsensusParser {
    private static String FILE_NAME = "relays.txt";
    private final DatabaseReader dbReader;
    private Map<String, List<Node>> flagMap;
    private Path serverDescriptorsPath;
    private static String serverDescriptorsUrl = "http://217.196.147.77/tor/server/all";

    public ConsensusParser() throws IOException, InterruptedException {
        InputStream database = getClass()
            .getClassLoader()
            .getResourceAsStream("GeoLite2-City.mmdb");

        this.dbReader = new DatabaseReader.Builder(database).build();
        this.flagMap = new HashMap<>();
        this.serverDescriptorsPath = Paths.get("server_descriptors.txt");

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(serverDescriptorsUrl)).GET().build();

        HttpResponse<Path> response = client.send(request, HttpResponse.BodyHandlers.ofFile(serverDescriptorsPath));
        
        System.out.println("Request response : " + response.statusCode());
    }

    public List<Node> parseConsensus() {

        DescriptorReader descriptorReader = DescriptorSourceFactory.createDescriptorReader();

        Map<String, List<String>> fingerprintToFamily = new HashMap<>();

        for (Descriptor descriptor : descriptorReader.readDescriptors(serverDescriptorsPath.toFile())) {
            if ((descriptor instanceof RelayServerDescriptor)) {
                RelayServerDescriptor  rsd  = (RelayServerDescriptor) descriptor;
                
                String fingerprintHex = rsd.getFingerprint(); 
                List<String> family = rsd.getFamilyEntries();   

                fingerprintToFamily.put(fingerprintHex, family);
            }
        }

        InputStream file = getClass().getClassLoader().getResourceAsStream(FILE_NAME);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-ddHH:mm:ss");

        try (BufferedReader in = new BufferedReader(new InputStreamReader(file, StandardCharsets.UTF_8))) {

            if (!in.ready()) {
                return null;
            }

            String line = "";
            List<Node> relays = new ArrayList<>();
            Node relay = new Node();

            try {
                
                while (!((line = in.readLine()).isEmpty())) {

                    String[] inputTokens = line.split(" ");

                    switch (inputTokens[0]) {
                        case "r":
                            relay.setNickname(inputTokens[1]);
                            relay.setFingerprint(inputTokens[2]);
                            relay.setDigest(inputTokens[3]);
                            relay.setTimePublished(LocalDateTime.parse(inputTokens[4] + inputTokens[5], formatter));
                            relay.setIpAddress(inputTokens[6]);

                            // Colocar pais de origem
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
                            for (String flag : flags) {
                                List<Node> nodesWithFlag = flagMap.get(flag);
                                if (!flagMap.containsKey(flag))
                                    nodesWithFlag = new ArrayList<>();
                                
                                nodesWithFlag.add(relay);
                                flagMap.put(flag, nodesWithFlag);
                            }
                            break;
                        case "v":
                            relay.setVersion(inputTokens[2]);
                            break;
                        case "pr":
                            // Not needed for the project
                            break;
                        case "w":
                            relay.setBandwidth(Integer.valueOf(inputTokens[1].split("=")[1]));
                            break;
                        case "p":
                            relay.setExitPolicy(inputTokens[1] + inputTokens[2]);

                            //Adicionar a Familia
                            String hexFingerprint = base64ToHexFingerprint(relay.getFingerprint());
                            List<String> family = fingerprintToFamily.get(hexFingerprint);

                            relay.setFamily(family);

                            relays.add(relay);
                            relay = new Node();
                            break;
                        default:
                            break;
                    }
                }

            } catch (AddressNotFoundException e) {
                relay.setCountry("unknown");
            }

            return relays;

        } catch (Exception e) {
            System.out.println("File unable to read.");
            e.printStackTrace();
            return null;
        }
    }

    public List<Node> filterByFlag(String flag) {
        return flagMap.get(flag);
    }

    public boolean sameSubnet(Node first, Node second) {
        String[] firstIP = first.getIpAddress().split("\\.");
        String[] secondIP = second.getIpAddress().split("\\.");

        return firstIP[0].equals(secondIP[0]) && firstIP[1].equals(secondIP[1]);
    }

    private static String base64ToHexFingerprint(String b64) {
        byte[] bytes = Base64.getDecoder().decode(b64);
        StringBuilder sb = new StringBuilder();

        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }

    return sb.toString(); // 40-char uppercase hex
}
}
