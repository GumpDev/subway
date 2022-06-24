package dev.gump.Subway.interfaces;

public class SubwayConfig {
    private final String host;
    private final int port;
    private final String password;
    private boolean debug;
    private int threads;

    public SubwayConfig(String host, int port) {
        this.host = host;
        this.port = port;
        this.password = "";
    }
    public SubwayConfig(String host, int port, boolean debug) {
        this.host = host;
        this.port = port;
        this.debug = debug;
        this.password = "";
    }
    public SubwayConfig(String host, int port, String password) {
        this.host = host;
        this.port = port;
        this.password = password;
    }
    public SubwayConfig(String host, int port, String password, boolean debug) {
        this.host = host;
        this.port = port;
        this.password = password;
        this.debug = debug;
    }

    public SubwayConfig(String host, int port, String password, int threads) {
        this.host = host;
        this.port = port;
        this.password = password;
        this.threads = threads;
    }

    public SubwayConfig(String host, int port, String password, int threads, boolean debug) {
        this.host = host;
        this.port = port;
        this.password = password;
        this.debug = debug;
        this.threads = threads;
    }

    public String getPassword() {
        return password;
    }

    public int getPort() {
        return port;
    }

    public boolean isDebug() {
        return debug;
    }

    public String getHost() {
        return host;
    }

    public int getThreads() {
        return threads;
    }
}
