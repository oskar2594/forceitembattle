package de.oskar.forceitem.game;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import io.papermc.paper.event.player.AsyncChatEvent;
import io.papermc.paper.event.player.ChatEvent;
import net.kyori.adventure.text.Component;

import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.event.world.WorldLoadEvent;

import de.oskar.forceitem.ForceItem;
import de.oskar.forceitem.game.managers.ScoreboardManager;
import de.oskar.forceitem.game.managers.teams.Team;
import de.oskar.forceitem.game.managers.teams.TeamColor;
import de.oskar.forceitem.game.managers.teams.TeamManager;
import de.oskar.forceitem.game.utils.Utils;
import de.oskar.forceitem.game.utils.enums.GameState;

public class Listeners implements Listener {

    @EventHandler
    public void onPrePlayerLogin(AsyncPlayerPreLoginEvent event) {
        // preload player data
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        ScoreboardManager.getInstance().update();
        ScoreboardManager.getInstance().broadcastScoreboard();
        event.joinMessage(null);
        Utils.broadcast(Utils.generateMessage(event.getPlayer().getName() + " ist dem Spiel beigetreten!"));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        ScoreboardManager.getInstance().update();
        event.quitMessage(null);
        Utils.broadcast(Utils.generateMessage(event.getPlayer().getName() + " hat das Spiel verlassen!"));
    }

    @EventHandler
    public void onChatMessage(AsyncChatEvent event) {
        event.setCancelled(true);
        if (Game.getInstance().getStateManager().isState(GameState.INGAME)) {
            Team team = TeamManager.getInstance().getTeam(event.getPlayer());
            if (team == null)
                return;
            team.broadcastMessage(Component.text("TC > §7[" + event.getPlayer().getName() + "§7]: §f")
                    .append(event.message().asComponent()));
            return;
        }
        Utils.broadcast(
                Component.text("§7[" + event.getPlayer().getName() + "§7]: §f").append(event.message().asComponent()));
    }

    @EventHandler
    public void onServerPing(ServerListPingEvent event) {
        int maxPlayers = ForceItem.getInstance().getConfig().getInt("teams.size") * TeamColor.values().length;
        event.motd(Component.text("§6ForceItemBattle von Oskar :)"));
        event.setMaxPlayers(maxPlayers);
    }

    @EventHandler
    public void onPreJoinAsync(AsyncPlayerPreLoginEvent event) {
        if (!ForceItem.getInstance().isLoaded()) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                    Component.text("§cDas Spiel ist noch nicht geladen!"));
        }
    }

    @EventHandler
    public void onServerLoaded(ServerLoadEvent event) {
        System.out.println("World loaded!");
        // if (event.getWorld().getName().equals("world"))
        ForceItem.getInstance().setLoaded(true);
    }
}
