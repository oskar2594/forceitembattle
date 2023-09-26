package de.oskar.forceitem.game.utils;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.World.Environment;
import org.bukkit.generator.BiomeProvider;
import org.codehaus.plexus.util.FileUtils;

import de.oskar.forceitem.ForceItem;
import de.oskar.forceitem.game.Game;

public class WorldReset {

    private static final String[] worlds = { Game.GAMEWORLD_NAME, "world_nether", "world_the_end" };

    public static void deleteWorld() throws IOException {
        for (String worldName : worlds) {
            System.out.println("Deleting world " + worldName + "...");
            // World world = Bukkit.getWorld(worldName);
            // if (world == null)
            // continue;
            // for (Chunk chunk : world.getLoadedChunks()) {
            // if (chunk.unload(false)) {
            // System.out.println("Unloaded chunk " + chunk.getX() + " " + chunk.getZ() +
            // "!");
            // } else {
            // System.out.println("Could not unload chunk " + chunk.getX() + " " +
            // chunk.getZ() + "!");
            // }
            // }
            // System.out.println("FUCKING TRYING TO UNLOOAD WORLD NOW");
            // if (Bukkit.unloadWorld(worldName, false)) {
            // System.out.println("Unloaded world " + worldName + "!");
            // } else {
            // System.out.println("Could not unload world " + worldName + "!");
            // }

            File worldFile = new File(Bukkit.getWorldContainer(), worldName);
            if (worldFile.exists()) {
                FileUtils.deleteDirectory(worldFile);
                System.out.println("Deleted world " + worldName + "!");
            }

            // worldFile.mkdirs();

            // new File(worldFile, "data").mkdirs();
            // new File(worldFile, "region").mkdirs();
            // new File(worldFile, "poi").mkdirs();
            // new File(worldFile, "datapacks").mkdirs();
            // new File(worldFile, "playerdata").mkdirs();
        }
    }

    public static void createNewWorld() {
        World gameWorld = Bukkit.createWorld(
                new WorldCreator(Game.GAMEWORLD_NAME).type(WorldType.NORMAL).seed(Utils.generateRandomSeed()));
        World lobbyWorld = Bukkit.createWorld(
                new WorldCreator(ForceItem.getInstance().getConfig().getString("lobby.world")));
        lobbyWorld.setAutoSave(false);
        preLoadSpawn(gameWorld);
        preLoadSpawn(lobbyWorld);
    }

    private static void preLoadSpawn(World world) {
        Location spawnLocation = world.getSpawnLocation();
        int radius = 2;
        for (int x = -radius; x < radius; x++) {
            for (int z = -radius; z < radius; z++) {
                ForceItem.getInstance().getLogger()
                        .info(world.getName() + " | Loading chunk " + (spawnLocation.getChunk().getX() + x)
                                + " " + (spawnLocation.getChunk().getZ() + z) + "...");
                spawnLocation.getWorld()
                        .getChunkAt(spawnLocation.getChunk().getX() + x, spawnLocation.getChunk().getZ() + z)
                        .load();
            }
        }
    }
}
