package de.oskar.forceitem.game.managers.states.end;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

import de.oskar.forceitem.ForceItem;
import de.oskar.forceitem.database.GameStorer;
import de.oskar.forceitem.game.Game;
import de.oskar.forceitem.game.managers.teams.Team;
import de.oskar.forceitem.game.managers.teams.TeamManager;
import de.oskar.forceitem.game.utils.EventHelper;
import de.oskar.forceitem.game.utils.Utils;
import de.oskar.forceitem.game.utils.enums.GameState;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.Title.Times;

public class EndManager implements Listener {
    private static EndManager instance;

    private static Location spawn;
    private static World world;

    private String resultId = null;

    static {
        FileConfiguration config = ForceItem.getInstance().getConfig();
        world = Bukkit.getWorld(config.getString("lobby.world"));
        if (world == null) {
            throw new RuntimeException("The world '" + config.getString("lobby.world") + "' does not exist!");
        }
        spawn = new Location(world,
                config.getDouble("lobby.spawn.x"), config.getDouble("lobby.spawn.y"), config.getDouble("lobby.spawn.z"),
                (float) config.getDouble("lobby.spawn.yaw"), (float) config.getDouble("lobby.spawn.pitch"));
    }

    public void init() {
        EventHelper.on("timer_ended", (args) -> {
            Game.getInstance().getStateManager().setGameState(GameState.END);
            try {
                performEndSequence();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    private void performEndSequence() throws InterruptedException {
        Bukkit.getOnlinePlayers().forEach(p -> {
            p.teleport(spawn);
            Utils.playSimpleSound(p, "entity.player.levelup");
            p.setGameMode(GameMode.SURVIVAL);
            p.setHealth(20);
            p.setFoodLevel(20);
            p.setLevel(0);
            p.setExp(0);
            p.getInventory().clear();
            p.getInventory().setArmorContents(null);
            p.setFlying(false);
            p.setAllowFlight(false);
        });
        GameStorer gameStorer = new GameStorer();
        Utils.broadcast(Utils.generateMessage("§e§lDas Spiel ist nun zu Ende!"));
        Bukkit.getScheduler().runTaskLater(ForceItem.getInstance(), this::showRanking, 20 * 5);
    }

    public void sendResultId(String id) {
        this.resultId = id;
    }

    private void showRanking() {
        showRankingAsync().thenRun(
                () -> Bukkit.getScheduler().runTaskLater(ForceItem.getInstance(), this::showChatRanking, 2 * 10));
    }

    private CompletableFuture<Void> resetGameAsync() {
        return CompletableFuture.runAsync(() -> {
            Utils.broadcast(Utils.generateMessage("§e§lDas Spiel wird in 1 Minute zurückgesetzt!"));
            try {
                Thread.sleep(20 * 1000);
                Utils.broadcast(Utils.generateMessage("§e§lDas Spiel wird in 30 Sekunden zurückgesetzt!"));
                Thread.sleep(10 * 1000);
                Utils.broadcast(Utils.generateMessage("§e§lDas Spiel wird in 5 Sekunden zurückgesetzt!"));
                Thread.sleep(10 * 1000);
                Utils.broadcast(Utils.generateMessage("§e§lDas Spiel wird jetzt zurückgesetzt!"));
                Bukkit.getScheduler().runTask(ForceItem.getInstance(), new Runnable() {
                    public void run() {
                        Utils.kickAllPlayers("§cDie Welt wird neu generiert...");
                    }
                });
                ForceItem.getInstance().setLoaded(false);
                Thread.sleep(2 * 1000);
                ForceItem.getInstance().getLogger().info("Deleting worlds...");
                ForceItem.getInstance().getLogger().info("Restarting server...");
                Bukkit.getServer().getScheduler().runTaskLater(ForceItem.getInstance(),
                        Bukkit.getServer()::shutdown, 10L * 20L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        });
    }

    private void showChatRanking() {
        Utils.broadcast(Utils.generateMessage("Das ist die finale Rangliste vom §8ForceItemBattle:"));
        Team[] sortedTeams = TeamManager.getInstance().getTeamsByPointsWithPlayers();
        Utils.broadcast(Utils.generateMessage("§e"));
        for (int i = 0; i < sortedTeams.length; i++) {
            Team team = sortedTeams[i];
            int position = i + 1;
            String playerList = getPlayerList(team);
            Utils.broadcast(Utils.generateMessage(
                    getColorForPosition(position) + position + ". " + team.getColor().getDisplayNameWithoutColor()
                            + getColorForPosition(position) + " (" + team.getPoints() + " Punkte)"));
            Utils.broadcast(Utils.generateMessage("§7" + playerList));
            Utils.broadcast(Utils.generateMessage("§e"));
        }
        showResultLink();
        resetGameAsync();
    }

    private void showResultLink() {
        if (resultId == null) {
            Utils.broadcast(Utils.generateMessage("§cEs gibt leider keine Online-Rangliste für dieses Spiel!"));
            return;
        }
        Utils.broadcast(Utils.generateMessage("§eHier ist der Link zur Online-Rangliste:"));
        Component message = Component.text(Utils.generateMessage("§e§l" + Game.RESULT_URL + resultId)).clickEvent(
                ClickEvent.openUrl(Game.RESULT_URL + resultId));
        Bukkit.getOnlinePlayers().forEach(p -> {
            p.sendMessage(message);
        });
    }

    private CompletableFuture<Void> showRankingAsync() {
        return CompletableFuture.runAsync(() -> {
            Team[] sortedTeams = TeamManager.getInstance().getTeamsByPointsWithPlayers();
            // invert
            Team[] invertedTeams = new Team[sortedTeams.length];
            for (int i = 0; i < sortedTeams.length; i++) {
                invertedTeams[i] = sortedTeams[sortedTeams.length - i - 1];
            }
            for (int i = 0; i < invertedTeams.length; i++) {
                Team team = invertedTeams[i];
                int position = invertedTeams.length - i;
                String playerList = getPlayerList(team);
                Bukkit.getOnlinePlayers().forEach(p -> {
                    p.showTitle(Title.title(
                            Component.text(getColorForPosition(position) + position + ". "
                                    + team.getColor().getDisplayNameWithoutColor() + getColorForPosition(position)
                                    + " ("
                                    + team.getPoints() + " Punkte)"),
                            Component.text("§7" + playerList),
                            Times.of(Duration.ZERO, Duration.ofSeconds(4), Duration.ZERO)));
                });
                Utils.broadcastSimpleSound(getSoundForPosition(position));
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        });
    }

    private String getPlayerList(Team team) {
        String playerList = "";
        for (Player player : team.getPlayers()) {
            playerList += team.getColor().getColor() + player.getName() + "§7, ";
        }
        if (playerList.length() > 2)
            playerList = playerList.substring(0, playerList.length() - 2);
        return playerList;
    }

    private String getColorForPosition(int position) {
        switch (position) {
            case 1:
                return "§6";
            case 2:
                return "§7";
            case 3:
                return "§e";
            default:
                return "§f";
        }
    }

    private String getSoundForPosition(int position) {
        return position == 1 ? "entity.player.levelup" : "entity.experience_orb.pickup";
    }

    // Keep Player in Bounds
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!isEnded())
            return;
        FileConfiguration config = ForceItem.getInstance().getConfig();
        double max = config.getDouble("lobby.limits.max");
        double min = config.getDouble("lobby.limits.min");
        Location playerLocation = event.getPlayer().getLocation();
        if (playerLocation.getY() < min || playerLocation.getY() > max) {
            event.getPlayer().teleport(EndManager.spawn);
        }
    }

    // No Block Break
    @EventHandler
    public void onBreakBlock(BlockBreakEvent event) {
        if (!isEnded())
            return;
        event.setCancelled(true);
    }

    // No Block Place
    @EventHandler
    public void onPlaceBlock(BlockPlaceEvent event) {
        if (!isEnded())
            return;
        event.setCancelled(true);
    }

    // No Day/Night Cycle and No Weather
    @EventHandler
    public void onDayCycle(WeatherChangeEvent event) {
        if (!isEnded())
            return;
        event.setCancelled(true);

    }

    // No Mob Spawning
    @EventHandler
    public void onMobSpawn(CreatureSpawnEvent event) {
        if (!isEnded())
            return;
        event.setCancelled(true);

    }

    // No Damage
    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (!isEnded())
            return;
        event.setCancelled(true);
    }

    // No Food loss
    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (!isEnded())
            return;
        event.setCancelled(true);
    }

    // No Item Move
    @EventHandler
    public void onItemMove(InventoryClickEvent event) {
        if (!isEnded())
            return;
        event.setCancelled(true);
    }

    // No Item Pickup
    @EventHandler
    public void onItemPickup(EntityPickupItemEvent event) {
        if (!isEnded())
            return;
        event.setCancelled(true);
    }

    // No Item Drop
    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        if (!isEnded())
            return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onItemConsume(PlayerItemConsumeEvent event) {
        if (!isEnded())
            return;
        event.setCancelled(true);
    }

    public boolean isEnded() {
        return Game.getInstance().getStateManager().isState(GameState.END);
    }

    public static EndManager getInstance() {
        if (instance == null) {
            instance = new EndManager();
        }
        return instance;
    }

}
