package de.oskar.forceitem.game.collectables;

import org.bukkit.Material;

import de.oskar.forceitem.game.GameDifficulty;
import de.oskar.forceitem.game.utils.TextIconsConverter;

public class Collectable {
    
    private String name;
    private String displayName;
    private Character characterIcon = null;
    private Material material;
    private GameDifficulty difficulty;
    private int points = 1;
    private int minAmount = 1;

    private boolean finished = false;
    private boolean skipped = false;
    private boolean active = false;

    public Collectable(String name, String displayName, Material material, GameDifficulty difficulty) {
        this.name = name;
        this.displayName = displayName;
        this.material = material;
        this.difficulty = difficulty;
        generateIcon();
    }

    public Collectable(String name, String displayName, Material material, GameDifficulty difficulty, int points) {
        this.name = name;
        this.displayName = displayName;
        this.material = material;
        this.difficulty = difficulty;
        this.points = points;
        generateIcon();
    }

    public Collectable(String name, String displayName, Material material, GameDifficulty difficulty, int points,
            int minAmount) {
        this.name = name;
        this.displayName = displayName;
        this.material = material;
        this.difficulty = difficulty;
        this.points = points;
        this.minAmount = minAmount;
        generateIcon();
    }

    private void generateIcon() {
        this.characterIcon = TextIconsConverter.getTextIcon(displayName);
    }

    public Collectable clone() {
        Collectable collectable = new Collectable(name, displayName, material, difficulty, points, minAmount);
        collectable.setActive(active);
        collectable.setFinished(finished);
        collectable.setSkipped(skipped);
        return collectable;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Material getMaterial() {
        return material;
    }

    public GameDifficulty getDifficulty() {
        return difficulty;
    }

    public int getPoints() {
        return points;
    }

    public int getMinAmount() {
        return minAmount;
    }

    public boolean isFinished() {
        return finished;
    }

    public boolean isSkipped() {
        return skipped;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public void setSkipped(boolean skipped) {
        this.skipped = skipped;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Character getCharacterIcon() {
        return characterIcon;
    }
}
