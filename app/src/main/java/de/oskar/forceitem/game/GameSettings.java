package de.oskar.forceitem.game;

import de.oskar.forceitem.game.utils.EventHelper;

// Difficulties, Dauer, Anzahl der Leben, KeepInventory

public class GameSettings extends EventHelper {
    private static GameSettings instance;

    private GameDifficulty difficulty = GameDifficulty.MEDIUM;
    private boolean keepInventory = false;
    private int duration = 60;
    private int maxSkips = 3;

    public void init() {

    }

    public void update() {
        call("settings_updated", (Object[]) null);
    }

    public void setDifficulty(GameDifficulty difficulty) {
        this.difficulty = difficulty;
        update();
    }

    public GameDifficulty getDifficulty() {
        return difficulty;
    }

    public void setDuration(int duration) {
        this.duration = duration;
        call("settings_updated_time", (Object[]) null);
        update();
    }

    public int getDuration() {
        return duration;
    }

    public boolean isKeepInventory() {
        return keepInventory;
    }

    public void setKeepInventory(boolean keepInventory) {
        this.keepInventory = keepInventory;
        update();
    }

    public static GameSettings getInstance() {
        if (instance == null) {
            instance = new GameSettings();
        }
        return instance;
    }

    public int getSkips() {
        return maxSkips;
    }

    public void setSkips(int skips) {
        this.maxSkips = skips;
        call("settings_updated_skips", (Object[]) null);
        update();
    }

}
