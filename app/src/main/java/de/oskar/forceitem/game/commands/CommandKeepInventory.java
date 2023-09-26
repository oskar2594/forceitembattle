package de.oskar.forceitem.game.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.oskar.forceitem.game.GameSettings;
import de.oskar.forceitem.game.utils.Utils;

public class CommandKeepInventory implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player p = (Player) sender;
        if (Utils.checkPermission(p, "forceitem.keepinventory")) {
            if (args.length == 1) {
                switch (args[0]) {
                    case "on":
                        GameSettings.getInstance().setKeepInventory(true);
                        Utils.broadcast(Utils.generateMessage("§7Das Inventar wird nun gespeichert!"));
                        break;
                    case "off":
                        GameSettings.getInstance().setKeepInventory(false);
                        Utils.broadcast(Utils.generateMessage("§cDas Inventar wird nun nicht gespeichert!"));
                        break;
                    default:
                        p.sendMessage(Utils.generateMessage("§cVerwendung: /keepinventory <on|off>"));
                        break;
                }
            } else {
                p.sendMessage(Utils.generateMessage("§cVerwendung: /keepinventory <on|off>"));
            }
        }
        return true;
    }

}