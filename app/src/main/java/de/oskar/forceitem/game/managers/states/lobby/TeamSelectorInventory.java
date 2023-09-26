package de.oskar.forceitem.game.managers.states.lobby;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.oskar.forceitem.ForceItem;
import de.oskar.forceitem.game.Game;
import de.oskar.forceitem.game.managers.teams.Team;
import de.oskar.forceitem.game.managers.teams.TeamColor;
import de.oskar.forceitem.game.managers.teams.TeamManager;
import net.kyori.adventure.text.Component;

public class TeamSelectorInventory {

    public TeamSelectorInventory() {

    }

    public void updateInventory(Player player) {
        if (player.getOpenInventory().getTitle().equals("§bTeamauswahl")) {
            Inventory inventory = player.getOpenInventory().getTopInventory();
            Inventory newInventory = getInventory(player);
            inventory.clear();
            inventory.setContents(newInventory.getContents());
        }
    }

    public Inventory getInventory(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 27, Component.text("§bTeamauswahl"));
        HashMap<TeamColor, Team> teams = TeamManager.getInstance().getTeams();
        ItemStack[] items = getTeamItems(teams, player);
        int startSlot = Math.round(13 - items.length / 2);
        for (int i = 0; i < items.length; i++) {
            inventory.setItem(startSlot + i, items[i]);
        }

        return inventory;
    }

    private ItemStack[] getTeamItems(HashMap<TeamColor, Team> teams, Player player) {
        ItemStack[] items = new ItemStack[5];
        for (TeamColor color : TeamColor.values()) {
            Team team = teams.get(color);
            ItemStack item = getTeamItem(team);
            if (team.getPlayers().contains(player)) {
                ItemMeta meta = item.getItemMeta();
                meta.addEnchant(Enchantment.DAMAGE_ALL, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);
                item.setItemMeta(meta);
            }
            items[color.ordinal()] = item;
        }
        return items;
    }

    private ItemStack getTeamItem(Team team) {
        Material material = team.getColor().getItem();
        int size = team.getPlayers().size();
        ItemStack item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text(team.getColor().getDisplayName()));
        List<Component> lore = new ArrayList<>();
        if (size > 0) {
            for (Player p : team.getPlayers()) {
                lore.add(Component.text("§7" + p.getName()));
            }
        }
        lore.add(Component.text(
                "§7" + team.getPlayers().size() + "/" + ForceItem.getInstance().getConfig().getInt("teams.size")));
        meta.lore(lore);

        // set PDC for Interaction
        meta.getPersistentDataContainer().set(Game.KEY_TEAMSELECTOR_ITEM_TEAM, Game.KEY_TEAMSELECTOR_ITEM_TEAM_TYPE,
                team.getColor().ordinal());

        item.setItemMeta(meta);
        return item;
    }

}
