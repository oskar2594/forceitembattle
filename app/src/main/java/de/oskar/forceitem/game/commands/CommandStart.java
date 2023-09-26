package de.oskar.forceitem.game.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.oskar.forceitem.game.Game;
import de.oskar.forceitem.game.managers.states.lobby.Countdown;
import de.oskar.forceitem.game.managers.states.lobby.LobbyManager;
import de.oskar.forceitem.game.utils.Utils;
import de.oskar.forceitem.game.utils.enums.GameState;

public class CommandStart implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player p = (Player) sender;
        if (Utils.checkPermission(p, "forceitem.start")) {
            Countdown countdown = LobbyManager.getInstance().getCountdown();
            if(!Game.getInstance().getStateManager().isState(GameState.LOBBY) || countdown.isRunning()) {
                p.sendMessage(Utils.generateMessage("§cDas Spiel startet bereits!"));
                return true;
            }
            countdown.setLeftTime(30);
            if(!countdown.isRunning()) countdown.start();
            p.sendMessage(Utils.generateMessage("§7Du hast das Spiel gestartet!"));
        }
        return true;
    }

}
