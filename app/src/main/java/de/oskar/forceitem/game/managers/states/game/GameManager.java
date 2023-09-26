package de.oskar.forceitem.game.managers.states.game;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

import de.oskar.forceitem.ForceItem;
import de.oskar.forceitem.game.Game;
import de.oskar.forceitem.game.GameSettings;
import de.oskar.forceitem.game.collectables.Collectable;
import de.oskar.forceitem.game.collectables.Collectables;
import de.oskar.forceitem.game.managers.teams.Team;
import de.oskar.forceitem.game.managers.teams.TeamManager;
import de.oskar.forceitem.game.utils.EventHelper;
import de.oskar.forceitem.game.utils.TitleCountdown;
import de.oskar.forceitem.game.utils.enums.GameState;
import net.kyori.adventure.text.Component;

public class GameManager implements Listener {
    private static GameManager instance;

    private boolean isCountingDown = true;
    private Timer timer;

    public void init() {
        timer = new Timer();
        EventHelper.on("game_start", (args) -> {

            startGame();
            isCountingDown = true;
            TitleCountdown countdown = new TitleCountdown(5);
            countdown.performCountdown((args2) -> {
                isCountingDown = false;
                timer.start();
            });
        });
    }

    private void startGame() {
        TeamManager.getInstance().fillTeams();
        Collectables.getInstance();
        Bukkit.getWorld(Game.GAMEWORLD_NAME).setTime(0);
        preparePlayers();
    }

    private void preparePlayers() {
        TeamManager.getInstance().getOnlinePlayersInTeams().forEach(player -> {
            preparePlayer(player, true);
        });
    }

    private void preparePlayer(Player player, boolean initalPrepare) {
        Team team = TeamManager.getInstance().getTeam(player);
        ItemStack backpackItem = team.getBackpack().getBackPackItem();
        player.setGameMode(GameMode.SURVIVAL);
        player.getInventory().clear();
        player.setHealth(20);
        player.setFoodLevel(20);
        player.setSaturation(20);
        player.setLevel(0);
        player.setExp(0);
        player.teleport(Bukkit.getWorld(Game.GAMEWORLD_NAME).getSpawnLocation());
        player.getInventory().setItem(8, backpackItem);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (!isInGame())
            return;
        Player player = event.getEntity();
        event.setKeepInventory(GameSettings.getInstance().isKeepInventory());
        event.setKeepLevel(GameSettings.getInstance().isKeepInventory());
        Team team = TeamManager.getInstance().getTeam(player);
        ItemStack backpackItem = team.getBackpack().getBackPackItem();

        // remove backpack from player drop
        event.getDrops().removeIf(item -> {
            if(GameSettings.getInstance().isKeepInventory()) return true;
            if (item.getItemMeta() == null)
                return false;
            if (item.getItemMeta().getPersistentDataContainer().has(Game.KEY_BACKPACK_TEAM)) {
                return true;
            }
            return false;
        });
        if (!GameSettings.getInstance().isKeepInventory()) {
            player.getInventory().clear();
            event.setKeepInventory(true);
            player.getInventory().addItem(backpackItem);
            return;
        }
        player.getInventory().remove(backpackItem);
        player.getInventory().addItem(backpackItem);
    }

    // Reference to Backpack.java - Backpack#openInventory(Player player)
    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        if (!isInGame())
            return;
        Player p = event.getPlayer();
        Team team = TeamManager.getInstance().getTeam(p);
        if (team == null)
            return;
        ItemStack item = event.getItem();
        if (item == null)
            return;
        if (event.getAction().name().contains("RIGHT_CLICK")) {
            if (!item.getItemMeta().getPersistentDataContainer().has(Game.KEY_BACKPACK_TEAM))
                return;
            if (item.getItemMeta().getPersistentDataContainer().get(Game.KEY_BACKPACK_TEAM,
                    Game.KEY_BACKPACK_TEAM_TYPE) == team.getColor().ordinal()) {
                team.getBackpack().openInventory(p);
            }
        }
    }

    @EventHandler
    public void onCraft(CraftItemEvent event) {
        if (!isInGame())
            return;
        if (event.getWhoClicked() instanceof Player) {
            Player p = (Player) event.getWhoClicked();
            ItemStack item = event.getRecipe().getResult();
            checkForCollectable(p, item);
        }
    }

    @EventHandler
    public void onItemPickup(EntityPickupItemEvent event) {
        if (!isInGame())
            return;
        if (event.getEntity() instanceof Player) {
            Player p = (Player) event.getEntity();
            ItemStack item = event.getItem().getItemStack();
            checkForCollectable(p, event.getItem().getItemStack());

            // Bundle
            if (!item.getItemMeta().getPersistentDataContainer().has(Game.KEY_BACKPACK_TEAM))
                return;
            if (item.getType().name().contains("BUNDLE") && item
                    .getItemMeta().getPersistentDataContainer()
                    .get(Game.KEY_BACKPACK_TEAM, Game.KEY_BACKPACK_TEAM_TYPE) != null) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        if (!isInGame())
            return;
        ItemStack item = event.getItemDrop().getItemStack();

        if (!item.getItemMeta().getPersistentDataContainer().has(Game.KEY_BACKPACK_TEAM))
            return;
        if (item.getType().name().contains("BUNDLE")
                && item.getItemMeta().getPersistentDataContainer()
                        .get(Game.KEY_BACKPACK_TEAM, Game.KEY_BACKPACK_TEAM_TYPE) != null) {
            event.setCancelled(true);
        }
    }

    private void checkForCollectable(Player player, ItemStack item) {
        if (item == null || player == null)
            return;
        Team team = TeamManager.getInstance().getTeam(player);
        if (team == null)
            return;
        Collectable collectable = team.getActiveCollectable();
        if (collectable == null)
            return;
        if (item.getType() == collectable.getMaterial()) {
            team.performCollect(item);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!isInGame())
            return;
        if (event.getClickedInventory() != null && event.getClickedInventory().getType().name().contains("PLAYER")) {
            checkForCollectable((Player) event.getWhoClicked(), event.getCurrentItem());
        }
        if (event.getCursor() != null && event.getCursor().getItemMeta() != null && event.getClick().isRightClick()
                && event.getCursor().getItemMeta().getPersistentDataContainer().has(Game.KEY_BACKPACK_TEAM)) {
            event.setCancelled(true);
            return;
        }
        if (event.getClickedInventory() == null || event.getCurrentItem() == null
                || event.getCurrentItem().getItemMeta() == null)
            return;
        if (!event.getCurrentItem().getItemMeta().getPersistentDataContainer().has(Game.KEY_BACKPACK_TEAM))
            return;
        if (event.getClick().isRightClick()) {
            event.setCancelled(true);
        } else if (event.getView().title().equals(Component.text("§7Rucksack")))
            event.setCancelled(true);

    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (!isInGame())
            return;
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            event.setCancelled(true);
        }
    }

    // Cancel Events in Countdown Phase
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!isInGame())
            return;
        if (isCountingDown) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBreakBlock(BlockBreakEvent event) {
        if (!isInGame())
            return;
        if (isCountingDown) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!isInGame())
            return;

        Player p = event.getPlayer();
        if (TeamManager.getInstance().getTeam(p) == null) {
            p.teleport(Bukkit.getWorld(Game.GAMEWORLD_NAME).getSpawnLocation());
            Bukkit.getScheduler().runTaskLater(ForceItem.getInstance(), new Runnable() {
                @Override
                public void run() {
                    p.setGameMode(GameMode.SPECTATOR);
                }
            }, 3L);
        }
    }

    private boolean isInGame() {
        return Game.getInstance().getStateManager().isState(GameState.INGAME);
    }

    public Timer getTimer() {
        return timer;
    }

    public static GameManager getInstance() {
        if (instance == null)
            instance = new GameManager();
        return instance;
    }

}
