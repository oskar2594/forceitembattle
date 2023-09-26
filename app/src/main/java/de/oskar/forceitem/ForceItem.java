package de.oskar.forceitem;

import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Enderman;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import de.oskar.forceitem.database.MongoDB;
import de.oskar.forceitem.game.Game;
import de.oskar.forceitem.game.Listeners;
import de.oskar.forceitem.game.commands.CommandDifficulty;
import de.oskar.forceitem.game.commands.CommandDuration;
import de.oskar.forceitem.game.commands.CommandEnd;
import de.oskar.forceitem.game.commands.CommandKeepInventory;
import de.oskar.forceitem.game.commands.CommandPause;
import de.oskar.forceitem.game.commands.CommandSetskips;
import de.oskar.forceitem.game.commands.CommandSkip;
import de.oskar.forceitem.game.commands.CommandStart;
import de.oskar.forceitem.game.managers.states.end.EndManager;
import de.oskar.forceitem.game.managers.states.game.GameManager;
import de.oskar.forceitem.game.managers.states.lobby.LobbyManager;
import de.oskar.forceitem.game.managers.states.lobby.TeamSelector;
import de.oskar.forceitem.game.managers.teams.TeamManager;
import de.oskar.forceitem.game.utils.WorldReset;

public final class ForceItem extends JavaPlugin {

    private static ForceItem instance;
    private boolean isLoaded = false;

    @Override
    public void onLoad() {
        instance = this;
        saveDefaultConfig();
        try {
            WorldReset.deleteWorld();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onEnable() {
        WorldReset.createNewWorld();
        isLoaded = true;
        PluginManager manager = Bukkit.getPluginManager();
        Game.getInstance();
        MongoDB.getInstance().connect();

        manager.registerEvents(new Listeners(), this);
        manager.registerEvents(GameManager.getInstance(), this);
        manager.registerEvents(TeamSelector.getInstance(), this);
        manager.registerEvents(TeamManager.getInstance(), this);
        manager.registerEvents(LobbyManager.getInstance(), this);
        manager.registerEvents(EndManager.getInstance(), this);

        this.getCommand("start").setExecutor(new CommandStart());
        this.getCommand("gameDifficulty").setExecutor(new CommandDifficulty());
        this.getCommand("duration").setExecutor(new CommandDuration());
        this.getCommand("pause").setExecutor(new CommandPause());
        this.getCommand("end").setExecutor(new CommandEnd());
        this.getCommand("keepinventory").setExecutor(new CommandKeepInventory());
        this.getCommand("skip").setExecutor(new CommandSkip());
        this.getCommand("setskips").setExecutor(new CommandSetskips());
    }

    @Override
    public void onDisable() {
        MongoDB.getInstance().disconnect();
        isLoaded = false;
    }

    public static ForceItem getInstance() {
        return instance;
    }

    public boolean isLoaded() {
        return isLoaded;
    }

    public void setLoaded(boolean isLoaded) {
        this.isLoaded = isLoaded;
    }
}