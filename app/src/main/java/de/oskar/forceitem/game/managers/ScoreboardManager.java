package de.oskar.forceitem.game.managers;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

import de.oskar.forceitem.ForceItem;
import de.oskar.forceitem.game.Game;
import de.oskar.forceitem.game.GameSettings;
import de.oskar.forceitem.game.managers.states.lobby.LobbyManager;
import de.oskar.forceitem.game.managers.teams.Team;
import de.oskar.forceitem.game.managers.teams.TeamColor;
import de.oskar.forceitem.game.managers.teams.TeamManager;
import de.oskar.forceitem.game.utils.EventHelper;
import de.oskar.forceitem.game.utils.Utils;
import de.oskar.forceitem.game.utils.enums.GameState;
import de.oskar.forceitem.lib.Board;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class ScoreboardManager {
    private static ScoreboardManager instance;

    private static final String TITLE = "§c§l\u2694 §6§oForce Item Battle §c§l\u2694";
    private Board globalBoard;
    private LinkedList<String> lines;
    private Scoreboard bukkitScoreboard;
    private org.bukkit.scoreboard.Team defaultTeam;

    private ScoreboardManager() {
        bukkitScoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        defaultTeam = bukkitScoreboard.registerNewTeam("default");
        defaultTeam.prefix(Component.text(""));
        defaultTeam.color(NamedTextColor.GRAY);

        globalBoard = new Board(bukkitScoreboard);
        lines = new LinkedList<>();
        EventHelper.on("gamestate_changed", (args) -> {
            this.update();
        });
        EventHelper.on("teams_updated", (args) -> {
            this.update();
        });
        EventHelper.on("settings_updated", (args) -> {
            this.update();
        });
        EventHelper.on("team_points_updated", (args) -> {
            this.update();
        });
        EventHelper.on("team_skips_updated", (args) -> {
            this.update();
        });


    }

    public void update() {
        if (Game.getInstance().getStateManager().isState(GameState.LOBBY)) {
            LinkedList<String> newLines = generateLobbyScoreboard();
            parseLinesToScoreboard(newLines);
        } else if (Game.getInstance().getStateManager().isState(GameState.INGAME)) {
            LinkedList<String> newLines = generateIngameScoreboard();
            parseLinesToScoreboard(newLines);
        } else if (Game.getInstance().getStateManager().isState(GameState.END)) {
            LinkedList<String> newLines = generateIngameScoreboard();
            parseLinesToScoreboard(newLines);
        }
    }

    public void broadcastScoreboard() {
        Bukkit.getOnlinePlayers().forEach((player) -> {
            sendScoreboard(player);
        });
    }

    public void sendScoreboard(Player p) {
        p.setScoreboard(bukkitScoreboard);
    }

    private void parseLinesToScoreboard(LinkedList<String> newLines) {
        if (newLines.size() > 15) {
            throw new RuntimeException("Too many lines for scoreboard!");
        }
        if (lines.size() != newLines.size()) {
            for (int i = 0; i < lines.size(); i++) {
                globalBoard.remove(i);
            }
        }
        globalBoard.title(TITLE);
        for (int i = 0; i < newLines.size(); i++) {
            String line = newLines.get(i);
            globalBoard.line(newLines.size() - i, line);
        }
        lines = newLines;
    }

    private LinkedList<String> generateIngameScoreboard() {
        LinkedList<String> lines = new LinkedList<>();
        lines.add("§e");
        Team[] teamsArray = TeamManager.getInstance().getTeamsByPointsWithPlayers();
        for (Team team : teamsArray) {
            lines.add(team.getColor().getDisplayName() + ": §7"
                    + team.getPoints() + " \u2730 | " + team.getAvailableSkips() + " \u2B8C");
        }
        lines.add("§e");
        lines.add("§7Schwierigkeit: §6" + GameSettings.getInstance().getDifficulty().getDisplayName());
        lines.add("§7Inventar: §6" + ((GameSettings.getInstance().isKeepInventory()) ? "§aBehalten" : "§cVerlieren"));
        lines.add("§e");
        return lines;
    }

    private LinkedList<String> generateLobbyScoreboard() {
        LinkedList<String> lines = new LinkedList<>();
        lines.add("§e");
        lines.add("§7Spieler: §b" + Bukkit.getOnlinePlayers().size() + "§7/§b"
                + (ForceItem.getInstance().getConfig().getInt("teams.size") * TeamColor.values().length));
        lines.add("§e");
        lines.add("§7Spielzeit: §6" + Utils.formatDuration(GameSettings.getInstance().getDuration()));
        lines.add("§7Schwierigkeit: §6" + GameSettings.getInstance().getDifficulty().getDisplayName());
        lines.add("§7Inventar: §6" + ((GameSettings.getInstance().isKeepInventory()) ? "§aBehalten" : "§cVerlieren"));
        lines.add("§e");
        if (LobbyManager.getInstance().getCountdown().isRunning()) {
            lines.add("§7Start in §b" + LobbyManager.getInstance().getCountdown().getLeftTime() + " §7Sekunden");
        } else {
            lines.add("§7Warte auf Spieler...");
        }
        lines.add("§e");
        return lines;
    }

    public Scoreboard getBukkitScoreboard() {
        return bukkitScoreboard;
    }   

    public org.bukkit.scoreboard.Team getDefaultTeam() {
        return defaultTeam;
    }

    public static ScoreboardManager getInstance() {
        if (instance == null)
            instance = new ScoreboardManager();
        return instance;
    }
}
