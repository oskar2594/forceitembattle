package de.oskar.forceitem.game.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.oskar.forceitem.game.Game;
import de.oskar.forceitem.game.GameSettings;
import de.oskar.forceitem.game.utils.Utils;
import de.oskar.forceitem.game.utils.enums.GameState;

public class CommandDuration implements CommandExecutor {

    private static final int MIN_DURATION = 1;
    private static final int MAX_DURATION = 300;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player p = (Player) sender;
        if (Utils.checkPermission(p, "forceitem.duration")) {
            if(Game.getInstance().getStateManager().isState(GameState.END)) {
                p.sendMessage(Utils.generateMessage("§cDie Dauer kann nicht mehr geändert werden!"));
                return true;
            }
            if(args.length == 1) {
                try {
                    int duration = Integer.parseInt(args[0]);
                    if(duration < MIN_DURATION || duration > MAX_DURATION) {
                        GameSettings.getInstance().setDuration(-1);
                        Utils.broadcast(Utils.generateMessage("§7Die begrenzte Spielzeit wurde §cdeaktiviert§7!"));
                        p.sendMessage(Utils.generateMessage("§7Die gegebene Dauer liegt außerhalb der Grenze, weswegen sie deaktiviert wurde§7!"));
                    } else {
                        GameSettings.getInstance().setDuration(duration);
                        Utils.broadcast(Utils.generateMessage("§7Die Spielzeit wurde auf §e" + Utils.formatDuration(duration) + " §7gesetzt!"));
                    }
                } catch (NumberFormatException e) {
                    p.sendMessage(Utils.generateMessage("§cVerwendung: /duration <Minuten>"));
                }
            } else {
                p.sendMessage(Utils.generateMessage("§cVerwendung: /duration <Minuten>"));
            }
        }
        return true;
    }

}