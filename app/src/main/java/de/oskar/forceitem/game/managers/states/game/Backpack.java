package de.oskar.forceitem.game.managers.states.game;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.oskar.forceitem.game.Game;
import de.oskar.forceitem.game.managers.teams.TeamColor;
import de.oskar.forceitem.game.managers.teams.TeamManager;
import de.oskar.forceitem.game.utils.enums.GameState;
import net.kyori.adventure.text.Component;

public class Backpack {

    private static final int MAX_ITEMS = 54;

    private TeamColor teamColor;
    private Inventory inventory;

    public Backpack(TeamColor teamColor) {
        this.teamColor = teamColor;
        this.inventory = Bukkit.createInventory(null, MAX_ITEMS, Component.text("§7Rucksack"));
    }

    public void openInventory(Player player) {
        if(Game.getInstance().getStateManager().isState(GameState.INGAME)) {
            TeamColor playerTeamColor = TeamManager.getInstance().getTeam(player).getColor();
            if(playerTeamColor == teamColor) {
                player.openInventory(inventory);
            } else {
                player.sendMessage("§cDu kannst nur deinen eigenen Rucksack öffnen!");
            }
        }
    }

    public ItemStack getBackPackItem() {
        ItemStack backpackItem = new ItemStack(Material.BUNDLE);
        ItemMeta meta = backpackItem.getItemMeta();
        meta.displayName(Component.text("§7Rucksack"));
        meta.getPersistentDataContainer().set(Game.KEY_BACKPACK_TEAM, Game.KEY_BACKPACK_TEAM_TYPE, teamColor.ordinal());
        backpackItem.setItemMeta(meta);
        return backpackItem;
    }

    public TeamColor getTeamColor() {
        return teamColor;
    }

    public Inventory getInventory() {
        return inventory;
    }



}
