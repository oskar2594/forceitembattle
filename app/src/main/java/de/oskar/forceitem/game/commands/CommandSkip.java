package de.oskar.forceitem.game.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.oskar.forceitem.game.Game;
import de.oskar.forceitem.game.managers.teams.Team;
import de.oskar.forceitem.game.managers.teams.TeamManager;
import de.oskar.forceitem.game.utils.Utils;
import de.oskar.forceitem.game.utils.enums.GameState;

public class CommandSkip implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player p = (Player) sender;
        if(!Game.getInstance().getStateManager().isState(GameState.INGAME)) {
            p.sendMessage(Utils.generateMessage("§cDu kannst diesen Befehl nur im Spiel ausführen!"));
            return true;
        }
        Team team = TeamManager.getInstance().getTeam(p);
        if(team == null) {
            p.sendMessage(Utils.generateMessage("§cDu bist in keinem Team!"));
            return true;
        }   
        if(!team.canSkip()) {
            p.sendMessage(Utils.generateMessage("§cDu kannst nicht mehr überspringen!"));
            return true;
        }
        team.performSkip();
        team.broadcastMessage(Utils.generateMessage("§b" + p.getName() + " §7hat ein Item übersprungen! Ihr habt noch §b" + team.getAvailableSkips() + "§7 übrig!"));
        return true;
    }

}
