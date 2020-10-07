package ca.innov8solutions.parachute;

import ca.innov8solutions.parachute.command.DeployCommand;
import ca.innov8solutions.parachute.config.ConfigManager;
import ca.innov8solutions.parachute.config.MainConfig;
import ca.innov8solutions.parachute.framework.ParachuteController;
import ca.innov8solutions.parachute.framework.ServerType;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.Set;

@Singleton
public class ParachutePlugin extends Plugin {

    private MainConfig config;

    private @Inject Set<Command> commandSet;

    @Override
    public void onEnable() {
        ConfigManager<MainConfig> configManager = new ConfigManager<>(this, "servers.json", MainConfig.class);
        configManager.init();
        this.config = configManager.getConfig();

        Injector injector = Guice.createInjector(new ParachuteModule(this));
        injector.injectMembers(this);

        for (Command c : commandSet) {
            getProxy().getPluginManager().registerCommand(this, c);
        }
    }

    ServerType getTypeByName(String name) {
        System.out.println(config == null);
        return config.getTypes().stream().filter(t -> t.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }
}
