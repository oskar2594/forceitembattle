package de.oskar.forceitem.game;

import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataType;

import de.oskar.forceitem.ForceItem;
import de.oskar.forceitem.game.managers.ScoreboardManager;
import de.oskar.forceitem.game.managers.states.end.EndManager;
import de.oskar.forceitem.game.managers.states.game.GameManager;
import de.oskar.forceitem.game.managers.states.lobby.LobbyManager;
import de.oskar.forceitem.game.managers.states.lobby.TeamSelector;
import de.oskar.forceitem.game.managers.teams.TeamManager;

public class Game {

    private static Game instance;

    public static NamespacedKey KEY_PLAYER_TEAM = new NamespacedKey(ForceItem.getInstance(), "playerTeam");
    public static PersistentDataType<Integer, Integer> KEY_PLAYER_TEAM_TYPE = PersistentDataType.INTEGER;
    public static NamespacedKey KEY_TEAMSELECTOR_ITEM_TEAM = new NamespacedKey(ForceItem.getInstance(),
            "teamselectorItemTeam");
    public static PersistentDataType<Integer, Integer> KEY_TEAMSELECTOR_ITEM_TEAM_TYPE = PersistentDataType.INTEGER;
    public static NamespacedKey KEY_BACKPACK_TEAM = new NamespacedKey(ForceItem.getInstance(), "backpackTeam");
    public static PersistentDataType<Integer, Integer> KEY_BACKPACK_TEAM_TYPE = PersistentDataType.INTEGER;

    public static final String GAMEWORLD_NAME = "world";

    public static final String RESULT_URL = "https://fib.oskar.run/result/";

    private StateManager stateManager;

    public Game() {
        super();
        this.stateManager = new StateManager();

        LobbyManager.getInstance().init();
        GameManager.getInstance().init();
        TeamManager.getInstance().init();
        TeamSelector.getInstance().init();
        GameSettings.getInstance().init();
        EndManager.getInstance().init();
        ScoreboardManager.getInstance();
    }

    public StateManager getStateManager() {
        return stateManager;
    }

    public static Game getInstance() {
        if (instance == null) {
            instance = new Game();
        }
        return instance;
    }


}
