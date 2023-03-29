package dev.magnetite.dodgeball.state.impl;

import dev.magnetite.dodgeball.Dodgeball;
import dev.magnetite.dodgeball.DodgeballPlayer;
import dev.magnetite.dodgeball.game.Game;
import dev.magnetite.dodgeball.state.GameState;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * The countdown state is the state that the game is in when it is counting down before starting.
 */
public class CountdownState extends GameState {
    private final int maxPlayers = game.getConfigManager().getMaxPlayers();
    private final int minPlayers = game.getConfigManager().getMinPlayers();

    private final List<ChatColor> colors = Arrays.asList(ChatColor.GREEN, ChatColor.YELLOW, ChatColor.RED);
    private int countdown = game.getConfigManager().getCountdown();
    private final BukkitTask task;

    public CountdownState(Game game) {
        super(game);

        task = new BukkitRunnable() {
            @Override
            public void run() {
                countdown--;

                if (countdown == 0) {
                    cancel();
                    game.broadcastTitle(ChatColor.GREEN + "DODGEBALL!", "");
                    game.undoBuild();
                    game.setState(new PlayingState(game));
                    return;
                }

                if (countdown <= 3) {
                    game.broadcastTitle("Starting in...", colors.get(countdown - 1) + String.valueOf(countdown));
                }
            }
        }.runTaskTimerAsynchronously(Dodgeball.getPlugin(Dodgeball.class), 20L,20L);
    }

    @Override
    public void onPlayerDamaged(EntityDamageEvent e) {
        e.setCancelled(true);
    }

    @Override
    public void onPlayerJoin(Player player) {
        game.initPlayer(player);
    }

    @Override
    public void onPlayerLeave(Player player) {
        game.getTeam(DodgeballPlayer.of(player).getTeam()).remove(player);
        game.broadcast(ChatColor.YELLOW + player.getName() + " left the game!" + ChatColor.GRAY + " (" + game.getPlayers().count() + "/" + maxPlayers + ")");

        if (game.getPlayers().count() < minPlayers) {
            game.setState(new WaitingState(game));
            task.cancel();
        }
    }

    @Override
    public List<String> updateScoreboard(Player player) {
        final String dateFormatted = new SimpleDateFormat("MM/dd/yy").format(new Date());
        final boolean isInTeam1 = game.getTeam1().contains(player);

        return Arrays.asList(
                "&7   (" + dateFormatted + ")   ",
                "",
                "&c&l" + game.getTeam1Name() + "&7: &f" + game.getTeam1().size() + (isInTeam1 ? " &7(You)" : ""),
                "&9&l" + game.getTeam2Name() + "&7: &f" + game.getTeam2().size() + (isInTeam1 ? "" : " &7(You)"),
                "",
                "Starting in" + ChatColor.GRAY + ": " + ChatColor.GREEN + countdown + ChatColor.GRAY + " seconds");
    }
}
