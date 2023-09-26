package de.oskar.forceitem.game.managers.states.lobby;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

import de.oskar.forceitem.ForceItem;
import de.oskar.forceitem.game.Game;
import de.oskar.forceitem.game.managers.DayCycleManager;
import de.oskar.forceitem.game.managers.teams.TeamColor;
import de.oskar.forceitem.game.utils.Utils;
import de.oskar.forceitem.game.utils.enums.GameState;
import net.kyori.adventure.text.Component;

public class LobbyManager implements Listener {
    private static LobbyManager instance;

    private DayCycleManager dayCycleManager;

    private Countdown countdown;

    private static Location spawn;
    private static World world;

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
        countdown = new Countdown();

        // Disable Day/Night Cycle
        dayCycleManager = new DayCycleManager(world);
        dayCycleManager.disableDayCycle();
        dayCycleManager.setWantedTime(6000);

        world.setClearWeatherDuration(999999);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!isInLobby())
            return;
        Player p = event.getPlayer();
        if (ForceItem.getInstance().getConfig().getInt("minPlayers") <= Bukkit.getOnlinePlayers().size()
                && !countdown.isRunning()) {
            countdown.setLeftTime(60);
            countdown.start();

        }
        p.teleport(LobbyManager.spawn);
        Utils.playSimpleSound(p, "entity.player.levelup");
        p.setGameMode(GameMode.SURVIVAL);
        p.setHealth(20);
        p.setFoodLevel(20);
        countdown.updateLevel(p);
        p.getInventory().setArmorContents(null);
        p.setFlying(false);
        p.setAllowFlight(false);
    }

    @EventHandler
    public void onPreJoin(AsyncPlayerPreLoginEvent event) {
        if (!isInLobby())
            return;
        
        if (Bukkit.getOnlinePlayers().size() >= ForceItem.getInstance().getConfig().getInt("teams.size")
                * TeamColor.values().length) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, Component.text("§cDas Spiel ist voll!"));
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        if (!isInLobby())
            return;
        Player p = event.getPlayer();
        p.getInventory().clear();
        if (ForceItem.getInstance().getConfig().getInt("minPlayers") > Bukkit.getOnlinePlayers().size() - 1) {
            countdown.stop();
        }
    }

    // Keep Player in Bounds
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!isInLobby())
            return;
        FileConfiguration config = ForceItem.getInstance().getConfig();
        double max = config.getDouble("lobby.limits.max");
        double min = config.getDouble("lobby.limits.min");
        Location playerLocation = event.getPlayer().getLocation();
        if (playerLocation.getY() < min || playerLocation.getY() > max) {
            event.getPlayer().teleport(LobbyManager.spawn);
        }
    }

    // No Block Break
    @EventHandler
    public void onBreakBlock(BlockBreakEvent event) {
        if (!isInLobby())
            return;
        event.setCancelled(true);
    }

    // No Block Place
    @EventHandler
    public void onPlaceBlock(BlockPlaceEvent event) {
        if (!isInLobby())
            return;
        event.setCancelled(true);
    }

    // No Day/Night Cycle and No Weather
    @EventHandler
    public void onDayCycle(WeatherChangeEvent event) {
        if (!isInLobby())
            return;
        event.setCancelled(true);

    }

    // No Mob Spawning
    @EventHandler
    public void onMobSpawn(CreatureSpawnEvent event) {
        if (!isInLobby())
            return;
        event.setCancelled(true);

    }

    // No Damage
    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (!isInLobby())
            return;
        event.setCancelled(true);
    }

    // No Food loss
    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (!isInLobby())
            return;
        event.setCancelled(true);
    }

    // No Item Move
    @EventHandler
    public void onItemMove(InventoryClickEvent event) {
        if (!isInLobby())
            return;
        event.setCancelled(true);
    }

    // No Item Pickup
    @EventHandler
    public void onItemPickup(EntityPickupItemEvent event) {
        if (!isInLobby())
            return;
        event.setCancelled(true);
    }

    // No Item Drop
    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        if (!isInLobby())
            return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onItemConsume(PlayerItemConsumeEvent event) {
        if (!isInLobby())
            return;
        event.setCancelled(true);
    }

    private boolean isInLobby() {
        return Game.getInstance().getStateManager().isState(GameState.LOBBY);
    }

    public Countdown getCountdown() {
        return countdown;
    }

    public static LobbyManager getInstance() {
        if (instance == null)
            instance = new LobbyManager();
        return instance;
    }
}
