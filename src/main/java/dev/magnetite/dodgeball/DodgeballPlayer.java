package dev.magnetite.dodgeball;

import dev.magnetite.dodgeball.game.Game;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

/**
 * This class is used to store data about a player that is specific to the Dodgeball plugin.
 */
public class DodgeballPlayer {
    private static final HashMap<Player, DodgeballPlayer> players = new HashMap<>();
    private final Player player;
    private PlayerInfo lastInfo; // If this was more than a test plugin, I would use persistent data storage instead of keeping in memory.
    private Team team;
    private Game game;

    public static DodgeballPlayer of(Player player) {
        return players.computeIfAbsent(player, player1 -> new DodgeballPlayer(player));
    }

    private DodgeballPlayer(Player player) {
        this.player = player;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public void setLastInfo(PlayerInfo lastInfo) {
        this.lastInfo = lastInfo;
    }

    public void restoreLastInfo() {
        player.teleport(lastInfo.loc());
        player.setGameMode(lastInfo.gameMode());
        player.getInventory().setContents(lastInfo.inv());
        player.setHealth(lastInfo.health());
        player.setFoodLevel(lastInfo.hunger());
    }

    public void removePlayer() {
        players.remove(player);
    }

    public enum Team {
        TEAM_1,
        TEAM_2,
        SPECTATOR
    }

    public record PlayerInfo(Location loc, GameMode gameMode, ItemStack[] inv, double health, int hunger) {}
}
