package de.oskar.forceitem.game.managers;

import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import de.oskar.forceitem.ForceItem;

public class DayCycleManager {

    private World world;
    
    private boolean isDayCycleEnabled = true;
    private int wantedTime = 6000;

    public DayCycleManager(World world) {
        this.world = world;
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!isDayCycleEnabled) {
                    if(world.getTime() != wantedTime) {
                        world.setTime(wantedTime);
                    }
                }
            }
        }.runTaskTimer(ForceItem.getInstance(), 0, 1L);
    }

    public void setWantedTime(int wantedTime) {
        this.wantedTime = wantedTime;
    }

    public void disableDayCycle() {
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        isDayCycleEnabled = false;
    }

    public void enableDayCycle() {
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);
        isDayCycleEnabled = true;
    }

    public boolean isDayCycleEnabled() {
        return isDayCycleEnabled;
    }
}
