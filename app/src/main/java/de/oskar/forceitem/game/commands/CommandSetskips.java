package de.oskar.forceitem.game.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.oskar.forceitem.game.GameSettings;
import de.oskar.forceitem.game.utils.Utils;

public class CommandSetskips  implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player p = (Player) sender;
        if (Utils.checkPermission(p, "forceitem.setskips")) {
            if (args.length == 1) {
                try {
                    int skips = Integer.parseInt(args[0]);
                    if (skips < 0) {
                        p.sendMessage(Utils.generateMessage("§cDie Anzahl der Skips muss größer oder gleich als 0 sein!"));
                        return true;
                    }
                    GameSettings.getInstance().setSkips(skips);
                    p.sendMessage(Utils.generateMessage("§7Du hast die Anzahl der Skips auf §e" + skips + " §7gesetzt."));
                } catch (NumberFormatException e) {
                    p.sendMessage(Utils.generateMessage("§cDie Anzahl der Skips muss eine Zahl sein!"));
                }
            } else {
                p.sendMessage(Utils.generateMessage("§cBenutze: §e/setskips <Anzahl>"));
            }
        }
        return true;
    }

}