package dev.magnetite.dodgeball.papi;

import dev.magnetite.dodgeball.DodgeballPlayer;
import dev.magnetite.dodgeball.game.Game;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Placeholder expansion for PlaceholderAPI
 */
public class DodgeballExpansion extends PlaceholderExpansion {

    @Override
    public @NotNull String getIdentifier() {
        return "dodgeball";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Magnetite";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public String onRequest(OfflinePlayer oPlayer, @NotNull String params) {
        if (!(oPlayer instanceof Player player))
            return "Not in game";

        final Game game = DodgeballPlayer.of(player).getGame();

        if (game == null)
            return "Not in game";

        if (params.equalsIgnoreCase("team1_alive")){
            return String.valueOf(game.getTeam1Alive());
        }

        if (params.equalsIgnoreCase("team2_alive")) {
            return String.valueOf(game.getTeam2Alive());
        }

        return null; // Placeholder is unknown by the Expansion
    }
}
