package de.oskar.forceitem.game.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.oskar.forceitem.game.Game;
import de.oskar.forceitem.game.managers.states.game.GameManager;
import de.oskar.forceitem.game.utils.Utils;
import de.oskar.forceitem.game.utils.enums.GameState;

public class CommandEnd implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player p = (Player) sender;
        if (Utils.checkPermission(p, "forceitem.end")) {
            if (Game.getInstance().getStateManager().isState(GameState.LOBBY)) {
                p.sendMessage(Utils.generateMessage("§cDas Spiel ist noch nicht gestartet!"));
                return true;
            }
            GameManager.getInstance().getTimer().stop();
            Utils.broadcast(Utils.generateMessage("§cDas Spiel wurde von " + p.getName() + " beendet!"));
        }
        return true;
    }

}
