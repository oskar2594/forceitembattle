package de.oskar.forceitem.game;

public enum GameDifficulty {
    EASY(1, "§aEinfach", "§a"),
    MEDIUM(2, "§6Mittel", "§6"),
    HARD(3, "§4Schwer", "§4");

    private int difficulty;
    private String displayName;
    private String color;

    GameDifficulty(int difficulty, String displayName, String color) {
        this.difficulty = difficulty;
        this.displayName = displayName;
        this.color = color;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getColor() {
        return color;
    }
}
