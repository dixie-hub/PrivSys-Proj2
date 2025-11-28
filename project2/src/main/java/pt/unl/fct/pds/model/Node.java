package pt.unl.fct.pds.model;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class Node {
    private String nickname;
    private String fingerprint;
    private String digest;
    private LocalDateTime timePublished;
    private String ipAddress;
    private List<String> family;
    private int orPort;
    private int dirPort;
    private String[] flags;
    private String version;
    private int bandwidth;
    private String country;
    private String exitPolicy;
    private String ipv6Address;

    public Node() {}

    public Node(
                String nickname,
                String fingerprint,
                String digest,
                LocalDateTime timePublished,
                String ipAddress,
                int orPort,
                int dirPort,
                String[] flags,
                String version,
                int bandwidth,
                String country,
                String exitPolicy,
                String ipv6Address,
                List<String> family)
    {
        this.nickname = nickname;
        this.fingerprint = fingerprint;
        this.digest = digest;
        this.timePublished = timePublished;
        this.ipAddress = ipAddress;
        this.orPort = orPort;
        this.dirPort = dirPort;
        this.flags = Arrays.copyOf(flags, flags.length);
        this.version = version;
        this.bandwidth = bandwidth;
        this.country = country;
        this.exitPolicy = exitPolicy;
        this.ipv6Address = ipv6Address;
        this.family = family;
    }

    public String getNickname() {return nickname;}
    public String getFingerprint() {return fingerprint;}
    public String getDigest() {return digest;}
    public LocalDateTime getTimePublished() {return timePublished;}
    public String getIpAddress() {return ipAddress;}
    public int getOrPort() {return orPort;}
    public int getDirPort() {return dirPort;}
    public String[] getFlags() {return flags;}
    public String getVersion() {return version;}
    public int getBandwidth() {return bandwidth;}
    public String getCountry() {return country;}
    public String getExitPolicy() {return exitPolicy;}
    public String getIpv6Address() {return ipv6Address;}
    public List<String> getFamily () {return family;}

    
    public void setNickname(String nickname) {this.nickname = nickname;}
    public void setFingerprint(String fingerprint) {this.fingerprint = fingerprint;}
    public void setDigest(String digest) {this.digest = digest;}
    public void setTimePublished(LocalDateTime timePublished) {this.timePublished =timePublished;}
    public void setIpAddress(String ipAddress) {this.ipAddress = ipAddress;}
    public void setOrPort(int orPort) {this.orPort = orPort;}
    public void setDirPort(int dirPort) {this.dirPort = dirPort;}
    public void setFlags(String[] flags) {this.flags = Arrays.copyOf(flags, flags.length);}
    public void setVersion(String version) {this.version = version;}
    public void setBandwidth(int bandwidth) {this.bandwidth = bandwidth;}
    public void setCountry(String country) {this.country = country;}
    public void setExitPolicy(String exitPolicy) {this.exitPolicy = exitPolicy;}
    public void setIpv6Address(String ipv6Address) {this.ipv6Address = ipv6Address;}
    public void setFamily(List<String> family) {this.family = family;}
}
