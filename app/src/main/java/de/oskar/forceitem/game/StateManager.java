package de.oskar.forceitem.game;

import de.oskar.forceitem.game.utils.EventHelper;
import de.oskar.forceitem.game.utils.Utils;
import de.oskar.forceitem.game.utils.enums.GameState;

public class StateManager extends EventHelper {
    private GameState gameState;

    public StateManager() {
        this.gameState = GameState.LOBBY;
        EventHelper.on("game_start", (args) -> {
            setGameState(GameState.INGAME);
        });
    }

    public GameState getGameState() {
        return gameState;
    }

    public boolean isState(GameState gameState) {
        return this.gameState == gameState;
    }

    public void setGameState(GameState gameState) {
        if (Utils.isNull(this.gameState))
            return;
        if (this.gameState != gameState)
            this.gameState = gameState;
        call("gamestate_changed", gameState);
    }
}
