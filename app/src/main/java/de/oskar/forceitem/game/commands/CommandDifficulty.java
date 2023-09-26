package de.oskar.forceitem.game.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.oskar.forceitem.game.Game;
import de.oskar.forceitem.game.GameDifficulty;
import de.oskar.forceitem.game.GameSettings;
import de.oskar.forceitem.game.utils.Utils;
import de.oskar.forceitem.game.utils.enums.GameState;

public class CommandDifficulty implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player p = (Player) sender;
        if (Utils.checkPermission(p, "forceitem.difficulty")) {
            if(!Game.getInstance().getStateManager().isState(GameState.LOBBY)) {
                p.sendMessage(Utils.generateMessage("§cDie Schwierigkeit kann nur in der Lobby geändert werden!"));
                return true;
            }
            if (args.length == 1) {
                switch (args[0]) {
                    case "easy":
                        GameSettings.getInstance().setDifficulty(GameDifficulty.EASY);
                        break;
                    case "medium":
                        GameSettings.getInstance().setDifficulty(GameDifficulty.MEDIUM);
                        break;
                    case "hard":
                        GameSettings.getInstance().setDifficulty(GameDifficulty.HARD);
                        break;
                    default:
                        p.sendMessage(Utils.generateMessage("§cVerwendung: /difficulty <easy|medium|hard>"));
                        return true;
                }
                broadcastDifficulty(GameSettings.getInstance().getDifficulty());
            } else {
                p.sendMessage(Utils.generateMessage("§cVerwendung: /difficulty <easy|medium|hard>"));
            }
        }
        return true;
    }

    private void broadcastDifficulty(GameDifficulty difficulty) {
        Utils.broadcast(Utils
                .generateMessage("§7Die Schwierigkeit wurde auf §e" + difficulty.getDisplayName() + " §7gesetzt!"));
    }
}
