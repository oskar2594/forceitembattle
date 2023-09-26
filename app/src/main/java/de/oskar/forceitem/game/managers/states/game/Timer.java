package de.oskar.forceitem.game.managers.states.game;

import org.bukkit.Bukkit;

import de.oskar.forceitem.ForceItem;
import de.oskar.forceitem.game.GameSettings;
import de.oskar.forceitem.game.utils.EventHelper;
import de.oskar.forceitem.game.utils.Utils;
import net.kyori.adventure.text.Component;

public class Timer extends EventHelper {

    private boolean isCountingDown = true;
    private boolean isPaused = false;
    private int leftTime;

    private int taskID = -1;

    public Timer() {
        updateTime();
        EventHelper.on("settings_updated_time", (event) -> {
            updateTime();
        });
    }

    private void updateTime() {
        isCountingDown = GameSettings.getInstance().getDuration() != -1;
        leftTime = isCountingDown ? GameSettings.getInstance().getDuration() * 60 : leftTime;
    }

    // broadcast Time in Actionbar
    private void broadcastTime() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            player.sendActionBar(Component.text("§e§l" + formatTime(leftTime)));
        });
    }

    private String formatTime(int time) {
        String formattedTime = "";
        // Days
        if (time > 86400) {
            formattedTime += (int) Math.floor(time / 86400) + "d ";
            time %= 86400;
        }
        // Hours
        if (time > 3600) {
            formattedTime += (int) Math.floor(time / 3600) + "h ";
            time %= 3600;
        }
        // Minutes
        if (time > 60) {
            formattedTime += (int) Math.floor(time / 60) + "m ";
            time %= 60;
        }
        // Seconds
        formattedTime += time % 60 + "s";
        return formattedTime;
    }

    public void start() {
        isPaused = false;
        if (taskID != -1)
            Bukkit.getScheduler().cancelTask(taskID);
        taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(ForceItem.getInstance(), () -> {
            if (isCountingDown) {
                leftTime--;
                if (leftTime <= 0) {
                    stop();
                    return;
                }
                if(leftTime <= 60) {
                    if(leftTime % 10 == 0 || leftTime <= 10) {
                        Utils.broadcast(Utils.generateMessage("§e§l" + formatTime(leftTime) + " §7Zeit bis zum Ende"));
                        Utils.broadcastSimpleSound("entity.experience_orb.pickup");
                    }
                }
            } else {
                leftTime++;
            }
            broadcastTime();
        }, 0, 20);
    }

    public void stop() {
        Bukkit.getScheduler().cancelTask(taskID);
        call("timer_ended", (Object[]) null);
    }

    public void pause() {
        isPaused = true;
        Bukkit.getScheduler().cancelTask(taskID);
    }

    public void resume() {
        isPaused = false;
        start();
    }

    public boolean isPaused() {
        return isPaused;
    }

}
