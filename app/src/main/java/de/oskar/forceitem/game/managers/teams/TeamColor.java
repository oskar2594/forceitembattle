package de.oskar.forceitem.game.managers.teams;

import org.bukkit.Material;

public enum TeamColor {
    RED("§cRot", "§c", Material.RED_DYE),
    BLUE("§9Blau", "§9", Material.BLUE_DYE),
    GREEN("§aGrün", "§a", Material.GREEN_DYE),
    YELLOW("§eGelb", "§e", Material.YELLOW_DYE),
    CYAN("§bTürkis", "§b", Material.CYAN_DYE);

    private String displayName;
    private String color;
    private Material item;

    TeamColor(String displayName, String color, Material item) {
        this.displayName = displayName;
        this.color = color;
        this.item = item;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDisplayNameWithoutColor() {
        return displayName.replaceAll("§.", "");
    }

    public String getColor() {
        return color;
    }

    public Material getItem() {
        return item;
    }
}
