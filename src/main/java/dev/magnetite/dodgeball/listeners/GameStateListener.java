package dev.magnetite.dodgeball.listeners;

import dev.magnetite.dodgeball.DodgeballPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Listeners that are passed to the game state
 */
public class GameStateListener implements Listener {
    @EventHandler
    public void onEntityDamaged(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player player)) return;

        DodgeballPlayer dbPlayer = DodgeballPlayer.of(player);

        if (dbPlayer.getGame() != null)
            dbPlayer.getGame().getState().onPlayerDamaged(e);
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent e) {
        if (!(e.getEntity().getShooter() instanceof Player player)) return;
        DodgeballPlayer dbPlayer = DodgeballPlayer.of(player);

        if (dbPlayer.getGame() != null)
            dbPlayer.getGame().getState().onProjectileHit(e);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        DodgeballPlayer dbPlayer = DodgeballPlayer.of(player);
        dbPlayer.removePlayer();

        if (dbPlayer.getGame() != null)
            dbPlayer.getGame().getState().onPlayerLeave(player);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        DodgeballPlayer dbPlayer = DodgeballPlayer.of(player);
        if (dbPlayer.getGame() != null)
            dbPlayer.getGame().getState().onPlayerMove(e);
    }

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent e) {
        if (!(e.getEntity().getShooter() instanceof Player player)) return;
        DodgeballPlayer dbPlayer = DodgeballPlayer.of(player);

        if (dbPlayer.getGame() != null)
            dbPlayer.getGame().getState().onProjectileLaunch(e);
    }
}
