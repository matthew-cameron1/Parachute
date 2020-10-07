package ca.innov8solutions.parachute;

import ca.innov8solutions.parachute.command.DeployCommand;
import ca.innov8solutions.parachute.config.ConfigManager;
import ca.innov8solutions.parachute.config.MainConfig;
import ca.innov8solutions.parachute.framework.ParachuteController;
import ca.innov8solutions.parachute.local.LParachuteController;
import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Names;
import net.md_5.bungee.api.plugin.Command;

import java.io.File;

public class ParachuteModule extends AbstractModule {

    private ParachutePlugin plugin;

    public ParachuteModule(ParachutePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    protected void configure() {

        bind(ParachutePlugin.class).toInstance(plugin);

        bind(ParachuteController.class).toInstance(new LParachuteController("game", plugin.getTypeByName("game")));

        bind(File.class).annotatedWith(Names.named("appDir")).toInstance(new File("/home/parachute/"));

        Multibinder<Command> commandMultibinder = Multibinder.newSetBinder(binder(), Command.class);
        commandMultibinder.addBinding().toInstance(new DeployCommand("deploy"));

        requestInjection(plugin);
    }
}
