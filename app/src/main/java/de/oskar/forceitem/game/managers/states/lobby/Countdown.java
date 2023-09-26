package de.oskar.forceitem.game.managers.states.lobby;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import de.oskar.forceitem.ForceItem;
import de.oskar.forceitem.game.managers.ScoreboardManager;
import de.oskar.forceitem.game.utils.EventHelper;
import de.oskar.forceitem.game.utils.Utils;

public class Countdown extends EventHelper {

    private int leftTime;
    private int taskId;
    private boolean isRunning = false;

    public Countdown() {
        this.leftTime = -1;
    }

    public void updateLevel(Player player) {
        if (leftTime == -1) {
            player.setLevel(0);
            player.setExp(0);
            return;
        }
        player.setLevel(leftTime);
        if (leftTime > 10)
            player.setExp(1);
        else
            player.setExp((float) leftTime / 10);
    }

    public void start() {
        isRunning = true;
        if (taskId != 0)
            Bukkit.getScheduler().cancelTask(taskId);
        taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(ForceItem.getInstance(), () -> {
            if (leftTime == -1)
                return;
            if (leftTime <= 0) {
                Bukkit.getScheduler().cancelTask(taskId);
                if (leftTime == 0) {
                    Utils.broadcast(Utils.generateMessage("Das Spiel startet jetzt!"));
                    call("game_start", (Object[]) null);
                    Bukkit.getOnlinePlayers().forEach((player) -> {
                        Utils.playSimpleSound(player, "entity.player.levelup");
                        updateLevel(player);
                    });
                }
                return;
            }
            ScoreboardManager.getInstance().update();
            if (leftTime % 10 == 0 || leftTime <= 5) {
                Bukkit.getOnlinePlayers().forEach((player) -> {
                    Utils.playSimpleSound(player, "entity.experience_orb.pickup");
                    updateLevel(player);
                });
                Utils.broadcast(Utils.generateMessage("Das Spiel startet in §b" + leftTime + " §7Sekunden!"));
            } else {
                Bukkit.getOnlinePlayers().forEach((player) -> {
                    updateLevel(player);
                });
            }
            leftTime--;
        }, 0, 20);
    }

    public void stop() {
        leftTime = -1;
        isRunning = false;
        Bukkit.getScheduler().cancelTask(taskId);
    }

    public void setLeftTime(int leftTime) {
        this.leftTime = leftTime;
    }

    public int getLeftTime() {
        return leftTime;
    }

    public boolean isRunning() {
        return isRunning;
    }

}
