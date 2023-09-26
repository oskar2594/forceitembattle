package de.oskar.forceitem.game.collectables;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import de.oskar.forceitem.game.GameDifficulty;
import de.oskar.forceitem.game.GameSettings;
import de.oskar.forceitem.game.utils.EventHelper;
import de.oskar.forceitem.game.utils.Pair;

public class Collectables extends EventHelper {
    private static Collectables instance;

    private LinkedList<Collectable> gameList = new LinkedList<>();

    private Collectables() {
        generateStartingCollectables(3);
    }

    private void generateStartingCollectables(int amount) {
        for (int i = 0; i < amount; i++) {
            gameList.add(generateCollectable());
        }
        call("collectables_create", gameList);
    }

    public void requestNewCollectables() {
        
        List<Collectable> newCollectables = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            newCollectables.add(generateCollectable());
        }
        gameList.addAll(newCollectables);
        call("collectables_new", newCollectables);
    }

    private Collectable generateCollectable() {
        Pair<GameDifficulty, Material> pair = Materials.getRandomMaterial(GameSettings.getInstance().getDifficulty(),
                gameList);
        GameDifficulty difficulty = pair.getLeft();
        Material material = pair.getRight();
        String name = material.name().toLowerCase();
        ItemStack stack = new ItemStack(material, 1);
        String displayName = stack.getItemMeta().hasDisplayName() ? stack.getItemMeta().displayName().toString()
                : stack.getType().name().replace("_", " ").toLowerCase();
        int points = difficulty == GameDifficulty.EASY ? 1 : difficulty == GameDifficulty.MEDIUM ? 2 : 3;

        Collectable collectable = new Collectable(name, displayName, material, difficulty, points);
        return collectable;
    }

    public List<Collectable> getCollectables(int cursor, int amount) {
        if (cursor + amount > gameList.size()) {
            requestNewCollectables();
        }
        return gameList.subList(cursor, cursor + amount);
    }

    public static Collectables getInstance() {
        if (instance == null) {
            instance = new Collectables();
        }
        return instance;
    }

}
