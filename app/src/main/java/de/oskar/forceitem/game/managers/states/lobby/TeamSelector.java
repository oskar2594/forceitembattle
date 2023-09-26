package de.oskar.forceitem.game.managers.states.lobby;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.oskar.forceitem.game.Game;
import de.oskar.forceitem.game.managers.teams.TeamColor;
import de.oskar.forceitem.game.managers.teams.TeamManager;
import de.oskar.forceitem.game.utils.EventHelper;
import de.oskar.forceitem.game.utils.Utils;
import de.oskar.forceitem.game.utils.enums.GameState;
import net.kyori.adventure.text.Component;

public class TeamSelector implements Listener {
    private static TeamSelector instance;

    public static final ItemStack teamSelectorItem;
    private TeamSelectorInventory inventory;

    static {
        teamSelectorItem = new ItemStack(Material.RED_BED, 1);
        ItemMeta meta = teamSelectorItem.getItemMeta();
        meta.displayName(Component.text("§bTeamauswahl"));
        meta.lore(List.of(Component.text("§7Rechtsklick um ein Team auszuwählen")));
        teamSelectorItem.setItemMeta(meta);
    }

    public void init() {
        inventory = new TeamSelectorInventory();

        // Update Inventory when teams are updated
        EventHelper.on("teams_updated", (args) -> {
            Bukkit.getOnlinePlayers().forEach((player) -> {
                if (player.getOpenInventory().getTitle().equals("§bTeamauswahl"))
                    inventory.updateInventory(player);
            });
        });

    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        if (!Game.getInstance().getStateManager().isState(GameState.LOBBY))
            return;
        Player p = event.getPlayer();
        if (event.getItem() == null || event.getItem().getItemMeta() == null)
            return;
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (event.getItem().getItemMeta().displayName().equals(teamSelectorItem.getItemMeta().displayName())) {
                p.openInventory(inventory.getInventory(p));
            }
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!Game.getInstance().getStateManager().isState(GameState.LOBBY))
            return;
        if (event.getView().getTitle().equals("§bTeamauswahl")) {
            event.setCancelled(true);
        }
        ItemStack item = event.getCurrentItem();
        if (item == null || item.getItemMeta() == null)
            return;
        if (!item.getItemMeta().getPersistentDataContainer().has(Game.KEY_TEAMSELECTOR_ITEM_TEAM))
            return;
        int teamId = item.getItemMeta().getPersistentDataContainer().get(Game.KEY_TEAMSELECTOR_ITEM_TEAM, Game.KEY_TEAMSELECTOR_ITEM_TEAM_TYPE);
        TeamColor color = TeamColor.values()[teamId];
        if (color == null) {
            event.getWhoClicked().sendMessage(Utils.generateMessage("§cDieses Team existiert nicht!"));
            return;
        }
        Utils.playSimpleSound((Player) event.getWhoClicked(), "ui.button.click");
        TeamManager.getInstance().playerToggleGroup((Player) event.getWhoClicked(), color);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        
        if (!Game.getInstance().getStateManager().isState(GameState.LOBBY))
            return;
        
        Player p = event.getPlayer();
        p.getInventory().clear();
        p.getInventory().setItem(8, teamSelectorItem);

        p.getPersistentDataContainer().set(Game.KEY_PLAYER_TEAM, Game.KEY_PLAYER_TEAM_TYPE, -1);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent evnet) {
        if (!Game.getInstance().getStateManager().isState(GameState.LOBBY))
            return;
        Player p = evnet.getPlayer();
        TeamManager.getInstance().removePlayer(p);
    }

    public static TeamSelector getInstance() {
        if (instance == null)
            instance = new TeamSelector();
        return instance;
    }
}
