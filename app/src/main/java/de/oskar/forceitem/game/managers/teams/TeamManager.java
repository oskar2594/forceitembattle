package de.oskar.forceitem.game.managers.teams;

import java.util.HashMap;
import java.util.LinkedList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team.Option;
import org.bukkit.scoreboard.Team.OptionStatus;

import de.oskar.forceitem.game.Game;
import de.oskar.forceitem.game.managers.ScoreboardManager;
import de.oskar.forceitem.game.utils.EventHelper;
import de.oskar.forceitem.game.utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class TeamManager extends EventHelper implements Listener {
    private static TeamManager instance;

    private HashMap<TeamColor, Team> teams = new HashMap<>();

    private Scoreboard scoreboard;

    public void init() {
        scoreboard = ScoreboardManager.getInstance().getBukkitScoreboard();

        for (TeamColor color : TeamColor.values()) {
            Team team = new Team(color);
            teams.put(color, team);
            org.bukkit.scoreboard.Team scoreboardTeam = scoreboard.registerNewTeam(color.getDisplayName());
            System.out.println(scoreboardTeam);
            team.setScoreboardTeam(scoreboardTeam);
            scoreboardTeam.prefix(Component.text(color.getDisplayName() + "§7 "));
            scoreboardTeam.color(NamedTextColor.GRAY);
        }
    }

    public void removePlayer(Player player) {
        Team team = getTeam(player);
        if (team != null) {
            team.removePlayer(player);
        }
    }

    public void playerToggleGroup(Player player, TeamColor color) {
        Team team = teams.get(color);
        Team playerTeam = getTeam(player);
        if (team == null)
            return;
        if (playerTeam != null) {
            if (playerTeam.equals(team)) {
                team.removePlayer(player);
                player.sendMessage(
                        Utils.generateMessage("Du hast Team " + team.getColor().getDisplayName() + "§7 verlassen!"));
            } else {
                playerTeam.removePlayer(player);
                tryJoinTeam(player, team);
            }
        } else {
            tryJoinTeam(player, team);
        }
    }

    private void tryJoinTeam(Player player, Team team) {
        if (team.addPlayer(player)) {
            player.sendMessage(
                    Utils.generateMessage("Du bist nun in Team " + team.getColor().getDisplayName() + "§7 !"));
        } else {
            player.sendMessage(Utils.generateMessage("Das Team " + team.getColor().getDisplayName() + "§7 ist voll!"));
        }
    }

    public Team getTeam(Player player) {
        for (Team team : teams.values()) {
            Player[] players = team.getPlayers().toArray(new Player[0]);
            for (Player p : players) {
                if (p.getUniqueId().equals(player.getUniqueId())) {
                    return team;
                }
            }
        }
        return null;
    }

    public HashMap<TeamColor, Team> getTeams() {
        return teams;
    }

    public void updatePlayer(Player player) {
        Team team = getTeam(player);
        call("teams_updated", (Object[]) null);
        if (team == null) {
            player.playerListName(Component.text("§7" + player.getName()));
            ScoreboardManager.getInstance().getDefaultTeam().addEntry(player.getName());
            return;
        }
        updateNametag(player);
        team.updatePlayers();
        team.getCollectableDisplay().addViewer(player);
        player.getPersistentDataContainer().set(Game.KEY_PLAYER_TEAM, Game.KEY_PLAYER_TEAM_TYPE,
                team.getColor().ordinal());
        player.playerListName(Component.text(team.getColor().getDisplayName() + "§7 " + player.getName()));
    }

    private void updateNametag(Player player) {
        OptionStatus visibility = OptionStatus.ALWAYS;
        Team team = getTeam(player);
        if (team == null) {
            visibility = OptionStatus.NEVER;
            return;
        }
        team.getScoreboardTeam().setOption(Option.NAME_TAG_VISIBILITY, visibility);
    }

    public void fillTeams() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            Team team = getTeam(player);
            if (team == null) {
                TeamColor color = getSmallestTeam();
                if (color != null) {
                    teams.get(color).addPlayer(player);
                }
            }
        }
    }

    private TeamColor getSmallestTeam() {
        TeamColor smallest = null;
        for (TeamColor color : teams.keySet()) {
            if (smallest == null) {
                smallest = color;
            } else {
                if (teams.get(color).getPlayers().size() < teams.get(smallest).getPlayers().size()) {
                    smallest = color;
                }
            }
        }
        return smallest;
    }

    public Team[] getTeamsByPointsWithPlayers() {
        Team[] teams = getTeamsWithPlayers();
        for (int i = 0; i < teams.length; i++) {
            for (int j = 0; j < teams.length - i - 1; j++) {
                if (teams[j].getPoints() < teams[j + 1].getPoints()) {
                    Team temp = teams[j];
                    teams[j] = teams[j + 1];
                    teams[j + 1] = temp;
                }
            }
        }
        return teams;
    }

    public Team[] getTeamsWithPlayers() {
        LinkedList<Team> teams = new LinkedList<>();
        for (Team team : this.teams.values()) {
            if (team.getPlayers().size() > 0)
                teams.add(team);
        }
        return teams.toArray(new Team[0]);
    }

    public Team[] getTeamsByPoints() {
        Team[] teams = this.teams.values().toArray(new Team[0]);
        for (int i = 0; i < teams.length; i++) {
            for (int j = 0; j < teams.length - i - 1; j++) {
                if (teams[j].getPoints() < teams[j + 1].getPoints()) {
                    Team temp = teams[j];
                    teams[j] = teams[j + 1];
                    teams[j + 1] = temp;
                }
            }
        }
        return teams;
    }

    public LinkedList<Player> getOnlinePlayersInTeam(TeamColor color) {
        LinkedList<Player> players = new LinkedList<>();
        Team team = teams.get(color);
        if (team == null)
            return players;
        LinkedList<Player> teamPlayers = team.getPlayers();
        teamPlayers.forEach(player -> {
            if (player.isOnline())
                players.add(player);
        });
        return players;
    }

    public LinkedList<Player> getOnlinePlayersInTeams() {
        LinkedList<Player> players = new LinkedList<>();
        for (Team team : teams.values()) {
            LinkedList<Player> teamPlayers = team.getPlayers();
            teamPlayers.forEach(player -> {
                if (player.isOnline())
                    players.add(player);
            });
        }
        return players;
    }

    public Scoreboard getScoreboard() {
        return scoreboard;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        Player p = event.getPlayer();
        updatePlayer(p);
    }

    public static TeamManager getInstance() {
        if (instance == null)
            instance = new TeamManager();
        return instance;
    }
}
