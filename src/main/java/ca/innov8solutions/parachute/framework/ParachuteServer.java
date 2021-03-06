package ca.innov8solutions.parachute.framework;

import net.md_5.bungee.api.config.ServerInfo;

public abstract class ParachuteServer {

    private String name;
    private int port;
    private ServerInfo info;

    public ParachuteServer(String name, int port) {
        this.name = name;
        this.port = port;
    }

    public String getName() {
        return name;
    }

    public int getPort() {
        return port;
    }

    public ServerInfo getInfo() {
        return info;
    }

    public void setInfo(ServerInfo info) {
        this.info = info;
    }
}
