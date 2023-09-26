package de.oskar.forceitem.game.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import de.oskar.forceitem.ForceItem;
import net.kyori.adventure.text.Component;

public class Utils {

    public static boolean notNull(Object obj) {
        return obj != null;
    }

    public static boolean isNull(Object obj) {
        return obj == null;
    }

    public static void broadcast(String message) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(message);
        }
    }

    public static void broadcast(Component component) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(component);
        }
    }

    public static String generateMessage(String message) {
        return generateMessage(message, "default");
    }

    public static String generateMessage(String message, String color) {
        if (color == null)
            color = "default";
        FileConfiguration config = ForceItem.getInstance().getConfig();
        String prefix = config.getString("format.prefix");
        String colorString = config.getString("format.colors." + color);
        if (colorString == null)
            colorString = config.getString("format.colors.default");
        return prefix + colorString + " " + message;
    }

    public static void broadcastSimpleSound(String string) {
        Bukkit.getOnlinePlayers().forEach(player -> {
            playSimpleSound(player, string);
        });
    }

    public static void playSimpleSound(Player player, String sound) {
        player.playSound(player.getLocation(), sound, 1, 1);
    }

    public static boolean checkPermission(Player player, String permission) {
        if (player.hasPermission(permission))
            return true;
        else {
            player.sendMessage(generateMessage("§cDu hast keine Berechtigung!"));
            return false;
        }
    }

    // duration in minutes
    public static String formatDuration(int duration) {
        int hours = duration / 60;
        int minutes = duration % 60;
        return hours + "h " + minutes + "min";
    }

    public static void kickAllPlayers(String reason) {
        Bukkit.getOnlinePlayers().forEach(player -> {
            player.kick(Component.text(reason));
        });
    }

    public static List<?> cloneList(List<?> list) {
        return new ArrayList<>(list);
    }

    public static long generateRandomSeed() {
        return (long) (Math.random() * Long.MAX_VALUE);
    }

}
