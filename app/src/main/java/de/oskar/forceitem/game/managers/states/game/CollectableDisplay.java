package de.oskar.forceitem.game.managers.states.game;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import de.oskar.forceitem.game.collectables.Collectable;
import de.oskar.forceitem.game.managers.teams.Team;
import de.oskar.forceitem.game.utils.EventHelper;
import de.oskar.forceitem.game.utils.Utils;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.bossbar.BossBar.Color;
import net.kyori.adventure.bossbar.BossBar.Overlay;
import net.kyori.adventure.text.Component;

public class CollectableDisplay {

    private Team team;
    private BossBar bossBar;
    private Component text = Component.text("§7Warte auf Items...");

    private boolean ready = false;

    public CollectableDisplay(Team team) {
        this.team = team;
    }

    public void create() {
        ready = true;
        bossBar = BossBar.bossBar(text, 1f, Color.WHITE, Overlay.PROGRESS);
        if (team.getPlayers() == null || team.getPlayers().size() == 0)
            return;
        team.getPlayers().forEach(player -> {
            bossBar.addViewer(player);
        });
        EventHelper.on("timer_ended", (args) -> {
            Bukkit.getOnlinePlayers().forEach(player -> {
                bossBar.removeViewer(player);
            });
        });
    }

    public void update(List<Collectable> collectables) {
        if (!ready)
            return;
        String text = "";
        for (Collectable collectable : collectables) {
            String displayName = collectable.getDisplayName();
            // if (collectable.getCharacterIcon() != null) {
            //     displayName = collectable.getCharacterIcon() + " " + displayName;
            // }
            text += collectable.getDifficulty().getColor() + (collectable.isActive() ? "§l" : "")
                    + displayName + "§7 \u21D2 ";
        }
        text = text.substring(0, text.length() - 4);
        bossBar.name(Component.text(text));
    }

    public BossBar getBossBar() {
        return bossBar;
    }

    public void addViewer(Player player) {
        if (!ready)
            return;
        bossBar.addViewer(player);
    }

    public void removeViewer(Player player) {
        if (!ready)
            return;
        bossBar.removeViewer(player);
    }

    public void progressSound() {
        if (!ready)
            return;
        for (Player players : team.getPlayers()) {
            Utils.playSimpleSound(players, "entity.player.levelup");
        }
    }

}
