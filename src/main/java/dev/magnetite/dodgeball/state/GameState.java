package dev.magnetite.dodgeball.state;

import dev.magnetite.dodgeball.game.Game;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.List;

/**
 * The game state is the state that the game is in. An implementation of the State pattern
 */
public abstract class GameState {
    protected final Game game;

    protected GameState(Game game) {
        this.game = game;
    }

    public void onPlayerDamaged(EntityDamageEvent e) {}
    public void onProjectileLaunch(ProjectileLaunchEvent e) {}
    public void onProjectileHit(ProjectileHitEvent e) {}
    public void onPlayerMove(PlayerMoveEvent e) {}
    public void onPlayerJoin(Player player) {}
    public void onPlayerLeave(Player player) {}

    public abstract List<String> updateScoreboard(Player player);



}
