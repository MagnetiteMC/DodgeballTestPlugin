package dev.magnetite.dodgeball.state.impl;

import dev.magnetite.dodgeball.Dodgeball;
import dev.magnetite.dodgeball.DodgeballPlayer;
import dev.magnetite.dodgeball.game.Game;
import dev.magnetite.dodgeball.state.GameState;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * The post game state is the state that the game is in when it is over.
 */
public class PostGameState extends GameState {

    public PostGameState(Game game) {
        super(game);

        game.getTeam(game.getWinner()).forEach(player -> player.sendTitle(ChatColor.GREEN + "You won!", ChatColor.GRAY + "With " + game.getAliveTeam(DodgeballPlayer.of(player).getTeam()) + " player(s) remaining on your team!", 10, 20, 10));
        game.getTeam(game.getLoser()).forEach(player -> player.sendTitle(ChatColor.RED + "You lost!", ChatColor.GRAY + "Aww :(", 10, 20, 10));

        // Remove the arena asynchronously
        new BukkitRunnable() {
            @Override
            public void run() {
                game.undoBuild();
            }
        }.runTaskLaterAsynchronously(Dodgeball.getPlugin(Dodgeball.class), 60L);

        // Perform the end game tasks
        new BukkitRunnable() {

            @Override
            public void run() {
                game.getTeam(game.getWinner()).forEach(player -> {
                    PostGameState.this.endPlayer(player);
                    game.getConfigManager().getWinnerCmds().forEach(cmd -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                            cmd.replace("%player%", player.getName())));
                });

                game.getTeam(game.getLoser()).forEach(player -> {
                    PostGameState.this.endPlayer(player);
                    game.getConfigManager().getLoserCmds().forEach(cmd -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                            cmd.replace("%player%", player.getName())));
                });
            }
        }.runTaskLater(Dodgeball.getPlugin(Dodgeball.class), 60L);
    }

    private void endPlayer(Player player) {
        final DodgeballPlayer dbPlayer = DodgeballPlayer.of(player);

        dbPlayer.restoreLastInfo();

        dbPlayer.setGame(null);
        dbPlayer.setTeam(null);
    }

    @Override
    public List<String> updateScoreboard(Player player) {
        final String dateFormatted = new SimpleDateFormat("MM/dd/yy").format(new Date());
        final String winningTeam = game.getWinner() == DodgeballPlayer.Team.TEAM_1 ?
                "&c&l" + game.getTeam1Name() : "&9&l" + game.getTeam2Name();

        return Arrays.asList(
                "&7  (" + dateFormatted + ")  ",
                "",
                winningTeam + " WINS!");
    }
}
