package de.oskar.forceitem.game.utils;

import java.time.Duration;

import org.bukkit.Bukkit;

import de.oskar.forceitem.ForceItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.Title.Times;

public class TitleCountdown {

    private int duration;
    private int taskID;

    public TitleCountdown(int duration) {
        this.duration = duration;
    }

    public void performCountdown(EventCallback callback) {
        taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(ForceItem.getInstance(), () -> {
            if (duration <= 0) {
                Bukkit.getScheduler().cancelTask(taskID);
                broadcastCountdown("§eLos!", "entity.player.levelup");
                callback.execute((Object[]) null);
                return;
            }
            broadcastCountdown("§e" + duration, "block.note_block.pling");
            duration--;
        }, 20, 20);
    }

    private void broadcastCountdown(String text, String sound) {
        Bukkit.getOnlinePlayers().forEach(player -> {
            Utils.playSimpleSound(player, sound);
            player.showTitle(Title.title(Component.text(text), Component.text(""),
                    Times.of(Duration.ZERO, Duration.ofSeconds(1), Duration.ZERO)));
        });
    }
}
