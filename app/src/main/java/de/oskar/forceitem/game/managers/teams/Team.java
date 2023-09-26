package de.oskar.forceitem.game.managers.teams;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Scoreboard;

import de.oskar.forceitem.ForceItem;
import de.oskar.forceitem.game.Game;
import de.oskar.forceitem.game.GameSettings;
import de.oskar.forceitem.game.collectables.Collectable;
import de.oskar.forceitem.game.collectables.Collectables;
import de.oskar.forceitem.game.managers.ScoreboardManager;
import de.oskar.forceitem.game.managers.states.game.Backpack;
import de.oskar.forceitem.game.managers.states.game.CollectableDisplay;
import de.oskar.forceitem.game.utils.EventHelper;
import de.oskar.forceitem.game.utils.Utils;
import net.kyori.adventure.text.Component;

public class Team extends EventHelper {
    private TeamColor color;
    private LinkedList<Player> players;
    private Backpack backpack;

    private CollectableDisplay collectableDisplay;
    private LinkedList<Collectable> collectables = new LinkedList<Collectable>();
    private int activeCollectableIndex = 0;
    private Collectable activeCollectable;

    private int maxTeamSize;
    private int points = 0;
    private int availableSkips = 0;

    private org.bukkit.scoreboard.Team scoreboardTeam;

    public Team(TeamColor color) {
        this.color = color;
        backpack = new Backpack(color);
        collectableDisplay = new CollectableDisplay(this);
        maxTeamSize = ForceItem.getInstance().getConfig().getInt("teams.size");
        players = new LinkedList<Player>();
        availableSkips = GameSettings.getInstance().getSkips();

        EventHelper.on("collectables_create", (Object... args) -> {
            LinkedList<Collectable> firstCollectables = (LinkedList<Collectable>) args[0];
            collectableDisplay.create();
            firstCollectables.forEach(collectable -> {
                collectables.add(collectable.clone());
            });
            collectables.get(activeCollectableIndex).setActive(true);
            activeCollectable = collectables.get(activeCollectableIndex);
            collectableDisplay.update(getDisplayCollectables());
        });

        EventHelper.on("collectables_new", (Object... args) -> {

            List<Collectable> newCollectables = (List<Collectable>) args[0];
            newCollectables.forEach(collectable -> {
                collectables.add(collectable.clone());
            });
            collectableDisplay.update(getDisplayCollectables());
        });

        EventHelper.on("settings_updated_skips", (Object... args) -> {
            availableSkips = GameSettings.getInstance().getSkips();
        });
    }

    public void setScoreboardTeam(org.bukkit.scoreboard.Team scoreboardTeam) {
        this.scoreboardTeam = scoreboardTeam;
    }

    public boolean addPlayer(Player player) {
        if (players.size() >= maxTeamSize) {
            return false;
        }
        players.add(player);
        player.getPersistentDataContainer().set(Game.KEY_PLAYER_TEAM, Game.KEY_PLAYER_TEAM_TYPE, color.ordinal());
        collectableDisplay.addViewer(player);
        scoreboardTeam.addEntry(player.getName());
        TeamManager.getInstance().updatePlayer(player);
        return true;
    }

    public boolean removePlayer(Player player) {
        players.remove(player);
        player.getPersistentDataContainer().set(Game.KEY_PLAYER_TEAM, Game.KEY_PLAYER_TEAM_TYPE, -1);
        collectableDisplay.removeViewer(player);
        scoreboardTeam.removeEntry(player.getName());
        ScoreboardManager.getInstance().getDefaultTeam().addEntry(player.getName());
        TeamManager.getInstance().updatePlayer(player);
        return true;
    }

    public void updatePlayers() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            if(TeamManager.getInstance().getTeam(player) == this) {
                if(!players.contains(player)) {
                    System.out.println(player.getUniqueId());
                    for(Player p : players) {
                        if(p.getUniqueId().toString().equals(player.getUniqueId().toString())) {
                            players.remove(p);
                            players.add(player);
                            break;
                        }
                    }
                }
            }
        });
    }

    public TeamColor getColor() {
        return color;
    }

    public LinkedList<Player> getPlayers() {
        return players;
    }

    public Backpack getBackpack() {
        return backpack;
    }

    public LinkedList<Collectable> getCollectables() {
        return collectables;
    }

    public List<Collectable> getDisplayCollectables() {
        if (collectables.size() < 3)
            return collectables;
        if (activeCollectableIndex == 0) {
            return collectables.subList(0, 3);
        } else if (activeCollectableIndex == collectables.size() - 1) {
            return collectables.subList(collectables.size() - 3, collectables.size());
        } else {
            return collectables.subList(activeCollectableIndex - 1, activeCollectableIndex + 2);
        }
    }

    public int getMaxTeamSize() {
        return maxTeamSize;
    }

    public CollectableDisplay getCollectableDisplay() {
        return collectableDisplay;
    }

    public Collectable getActiveCollectable() {
        return activeCollectable;
    }

    public void performCollect(ItemStack item) {
        if (activeCollectable == null)
            return;
        if (activeCollectable.getMaterial() == item.getType()) {
            nextItem(false);
        }
    }

    public void broadcastMessage(String message) {
        players.forEach(player -> {
            player.sendMessage(message);
        });
    }

    public void broadcastMessage(Component component) {
        players.forEach(player -> {
            player.sendMessage(component);
        });
    }

    public void performSkip() {
        availableSkips--;
        call("team_skips_updated", (Object[]) null);
        nextItem(true);
    }

    private void nextItem(boolean skipped) {
        activeCollectable.setActive(false);
        if (!skipped)
            activeCollectable.setFinished(true);
        activeCollectableIndex++;
        if (!skipped)
            addPoints(activeCollectable.getPoints());
        broadcastMessage(Utils.generateMessage("§7Ihr habt das Item §e" + activeCollectable.getDisplayName()
                + "§7 gesammelt! " + (skipped ? "§c+0" : "§a+" + activeCollectable.getPoints() + " Punkte")));
        if (activeCollectableIndex >= collectables.size() - 2) {
            Collectables.getInstance().requestNewCollectables();
        }
        activeCollectable = collectables.get(activeCollectableIndex);
        activeCollectable.setActive(true);
        collectableDisplay.update(getDisplayCollectables());
        collectableDisplay.progressSound();
        broadcastMessage(
                Utils.generateMessage("§7Das nächste Item ist §e" + activeCollectable.getDisplayName() + "§7!"));
    }

    public int getPoints() {
        return points;
    }

    public void addPoints(int points) {
        this.points += points;
        call("team_points_updated", (Object[]) null);
    }

    public boolean canSkip() {
        return availableSkips > 0;
    }

    public int getAvailableSkips() {
        return availableSkips;
    }

    public org.bukkit.scoreboard.Team getScoreboardTeam() {
        return scoreboardTeam;
    }
}
