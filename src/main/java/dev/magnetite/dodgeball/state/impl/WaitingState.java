package dev.magnetite.dodgeball.state.impl;

import dev.magnetite.dodgeball.Dodgeball;
import dev.magnetite.dodgeball.DodgeballPlayer;
import dev.magnetite.dodgeball.game.Game;
import dev.magnetite.dodgeball.state.GameState;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


/**
 * The waiting state is the state that the game is in when it is waiting for players to join.
 */
public class WaitingState extends GameState {
    protected final int minPlayers = game.getConfigManager().getMinPlayers();
    private int numDots = 0;

    public WaitingState(Game game) {
        super(game);
        new BukkitRunnable() {
            @Override
            public void run() {
                game.paste(game.getLobbyPaste(), new File(Dodgeball.getPlugin(Dodgeball.class).getDataFolder(), "Lobby.schem"));
            }
        }.runTaskLaterAsynchronously(Dodgeball.getPlugin(Dodgeball.class), 2L);
    }

    @Override
    public void onPlayerDamaged(EntityDamageEvent e) {
        e.setCancelled(true);
    }

    @Override
    public void onPlayerJoin(Player player) {
        game.initPlayer(player);

        if (game.getPlayers().count() >= minPlayers)
            game.setState(new CountdownState(game));
    }

    @Override
    public void onPlayerLeave(Player player) {
        game.getTeam(DodgeballPlayer.of(player).getTeam()).remove(player);
    }

    @Override
    public List<String> updateScoreboard(Player player) {
        numDots = (numDots + 1) % 3;

        final String dateFormatted = new SimpleDateFormat("MM/dd/yy").format(new Date());
        final boolean isInTeam1 = game.getTeam1().contains(player);

        return Arrays.asList(
                "&7       (" + dateFormatted + ")     ",
                "",
                "&c&l" + game.getTeam1Name() + "&7: &f" + game.getTeam1().size() + (isInTeam1 ? " &7(You)" : ""),
                "&9&l" + game.getTeam2Name() + "&7: &f" + game.getTeam2().size() + (isInTeam1 ? "" : " &7(You)"),
                "",
                "Waiting for players" + ".".repeat(numDots + 1));
    }
}
