package ca.innov8solutions.parachute.command;

import ca.innov8solutions.parachute.framework.ParachuteController;
import ca.innov8solutions.parachute.framework.ParachuteServer;
import com.google.inject.Inject;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class StatusCommand extends Command {

    private @Inject ParachuteController controller;



    public StatusCommand(String name) {
        super(name);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission("parachute.admin")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to do this!");
            return;
        }

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("status")) {

            }
        }
    }
}
