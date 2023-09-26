package de.oskar.forceitem.game.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.oskar.forceitem.game.Game;
import de.oskar.forceitem.game.managers.states.game.GameManager;
import de.oskar.forceitem.game.utils.Utils;
import de.oskar.forceitem.game.utils.enums.GameState;

public class CommandPause implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player p = (Player) sender;
        if (Utils.checkPermission(p, "forceitem.pause")) {
            if (Game.getInstance().getStateManager().isState(GameState.LOBBY)) {
                p.sendMessage(Utils.generateMessage("§cDas Spiel ist noch nicht gestartet!"));
                return true;
            }
            if (GameManager.getInstance().getTimer().isPaused()) {
                GameManager.getInstance().getTimer().resume();
                Utils.broadcast(Utils.generateMessage("§7Der Timer wurde fortgesetzt!"));
            } else {
                GameManager.getInstance().getTimer().pause();
                Utils.broadcast(Utils.generateMessage("§7Der Timer wurde pausiert!"));
            }
        }
        return true;
    }

}
