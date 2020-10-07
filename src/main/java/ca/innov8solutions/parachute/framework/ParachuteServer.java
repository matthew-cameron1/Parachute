package ca.innov8solutions.parachute.framework;

public abstract class ParachuteServer {

    private String name;
    private int port;

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
}
