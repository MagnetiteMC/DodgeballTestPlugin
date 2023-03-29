package dev.magnetite.dodgeball.game;

import dev.magnetite.dodgeball.ConfigManager;
import dev.magnetite.dodgeball.DodgeballPlayer;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * The game manager is responsible for managing all games
 */
public class GameManager {
    private final ConfigManager configManager;
    private final List<Game> games = new ArrayList<>();

    public GameManager(ConfigManager configManager) {
        this.configManager = configManager;
    }

    public List<Game> getGames() {
        return games;
    }

    /**
     * Creates a new game and sends a message to all players
     */
    public void createGame() {
        final String joinCmd = "/dodgeball join " + (games.size());

        games.add(new Game(games.size(), configManager));

        Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(MiniMessage.miniMessage().deserialize(configManager.getPrefix() +
                "<gray>A Dodgeball game is starting! <hover:show_text:\"<green>Run the command: " + joinCmd +
                "\"><click:run_command:" + joinCmd + "><green><underlined>Click here to join!")));
    }

    /**
     * Joins a game with the given ID
     *
     * @param player The player to join the game
     * @param id    The ID of the game to join
     */
    public void joinGame(Player player, int id) {
        if (id >= games.size()) {
            player.sendMessage(MiniMessage.miniMessage().deserialize(configManager.getPrefix()
                    + "<red>Invalid game ID!"));
            return;
        }

        if (DodgeballPlayer.of(player).getGame() != null) {
            player.sendMessage(MiniMessage.miniMessage().deserialize(configManager.getPrefix()
                    + "<red>You are already in a game!"));
            return;
        }

        DodgeballPlayer.of(player).setLastInfo(new DodgeballPlayer.PlayerInfo(player.getLocation(), player.getGameMode(),
                player.getInventory().getContents(), player.getHealth(), player.getFoodLevel()));

        player.sendActionBar(ChatColor.GREEN + "You have joined a Dodgeball game with ID " + id + "!");

        games.get(id).getState().onPlayerJoin(player);
    }

    /**
     * Leaves the game the player is in
     *
     * @param player The player to leave the game
     */
    public void leaveGame(Player player) {
        DodgeballPlayer dbPlayer = DodgeballPlayer.of(player);

        if (dbPlayer.getGame() == null) {
            player.sendMessage(MiniMessage.miniMessage().deserialize(configManager.getPrefix() + "<red>You are not in a game!"));
            return;
        }

        dbPlayer.restoreLastInfo();

        player.sendActionBar(ChatColor.RED + "You have left the Dodgeball game!");

        dbPlayer.getGame().getState().onPlayerLeave(player);
        dbPlayer.setGame(null);
    }
}
