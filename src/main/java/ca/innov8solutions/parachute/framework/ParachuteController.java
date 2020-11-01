package ca.innov8solutions.parachute.framework;

import ca.innov8solutions.parachute.ParachutePlugin;
import ca.innov8solutions.parachute.local.LParachuteServer;
import com.google.inject.Inject;

import java.util.List;

public abstract class ParachuteController {

    private @Inject
    ParachutePlugin plugin;

    private String name;

    private ServerType type;

    public ParachuteController(String name, ServerType type) {
        this.name = name;
        this.type = type;
    }

    public abstract void create();
    public abstract boolean exists();
    public abstract boolean destroy();
    public abstract List<String> getIps();
    public abstract List<ParachuteServer> getServers();

    public ServerType getType() {
        return type;
    }
}
