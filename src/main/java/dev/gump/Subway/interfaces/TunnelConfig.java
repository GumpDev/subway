package dev.gump.Subway.interfaces;

public class TunnelConfig {
    private final String ip;
    private final int port;

    private final String password;

    private boolean debug = false;
    private int reconnectTime = 100;

    public TunnelConfig(String ip, int port) {
        this.ip = ip;
        this.port = port;
        this.password = "";
    }
    public TunnelConfig(String ip, int port, String password) {
        this.ip = ip;
        this.port = port;
        this.password = password;
    }
    public TunnelConfig(String ip, int port, boolean debug) {
        this.ip = ip;
        this.port = port;
        this.debug = debug;
        this.password = "";
    }
    public TunnelConfig(String ip, int port, String password, boolean debug) {
        this.ip = ip;
        this.port = port;
        this.debug = debug;
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public boolean isDebug() {
        return debug;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public int getReconnectTime() {
        return reconnectTime;
    }

    public void setReconnectTime(int reconnectTime) {
        this.reconnectTime = reconnectTime;
    }
}
