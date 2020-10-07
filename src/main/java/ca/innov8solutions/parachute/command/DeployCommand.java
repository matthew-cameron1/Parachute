package ca.innov8solutions.parachute.command;

import ca.innov8solutions.parachute.ParachutePlugin;
import ca.innov8solutions.parachute.framework.ParachuteController;
import com.google.inject.Inject;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class DeployCommand extends Command {

    private @Inject
    ParachuteController controller;

    public DeployCommand(String name) {
        super(name);
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (commandSender instanceof ProxiedPlayer)
            return;

        controller.create();
    }
}
