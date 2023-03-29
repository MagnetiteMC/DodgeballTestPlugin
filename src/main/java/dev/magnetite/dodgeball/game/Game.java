package dev.magnetite.dodgeball.game;

import com.fastasyncworldedit.core.FaweAPI;
import com.google.common.collect.Streams;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.math.BlockVector3;
import dev.magnetite.dodgeball.ConfigManager;
import dev.magnetite.dodgeball.Dodgeball;
import dev.magnetite.dodgeball.DodgeballPlayer;
import dev.magnetite.dodgeball.state.GameState;
import dev.magnetite.dodgeball.state.impl.PostGameState;
import dev.magnetite.dodgeball.state.impl.WaitingState;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static dev.magnetite.dodgeball.DodgeballPlayer.Team;

/**
 * The game class is the class that holds all the information about the game.
 */
public class Game {
    private final int id;

    private final String prefix;
    private final String team1Name;
    private final String team2Name;
    private final ConfigManager configManager;

    private final List<Player> team1 = new ArrayList<>();
    private final List<Player> team2 = new ArrayList<>();
    private final List<Player> spectators = new ArrayList<>();
    private final List<Player> deadPlayers = new ArrayList<>();

    private final int maxPlayers;
    private int team1Alive;
    private int team2Alive;
    private Team loser;
    private GameState state;
    private EditSession lastEditSession;
    private final World world;
    private final Location loc;
    private final Location lobbyPaste;
    private final Location lobbySpawn;

    public Game(int id, ConfigManager configManager) {
        this.id = id;
        this.configManager = configManager;

        prefix = configManager.getPrefix();
        team1Name = configManager.getTeam1Name();
        team2Name = configManager.getTeam2Name();
        maxPlayers = configManager.getMaxPlayers();
        world = Bukkit.getWorld("dodgeball");
        loc = new Location(world, id * 500.0, 100, 0);
        lobbyPaste = new Location(world, (id * 500.0), 150, 0);
        lobbySpawn = lobbyPaste.clone().add(configManager.getLobbyLocation());
        lobbySpawn.setYaw(configManager.getLobbyYaw());
        lobbySpawn.setPitch(configManager.getLobbyPitch());

        state = new WaitingState(this);
    }

    /**
     * Creates the game arena
     */
    public void createArena() {
        paste(loc, new File(Dodgeball.getPlugin(Dodgeball.class).getDataFolder(), "Dodgeball_Arena.schem"));

        final Location team1Spawn = loc.clone().add(configManager.getTeam1Spawn());
        final Location team2Spawn = loc.clone().add(configManager.getTeam2Spawn());

        team1Spawn.setYaw(configManager.getTeam1SpawnYaw());
        team1Spawn.setPitch(configManager.getTeam1SpawnPitch());
        team2Spawn.setYaw(configManager.getTeam2SpawnYaw());
        team2Spawn.setPitch(configManager.getTeam2SpawnPitch());

        getTeam1().forEach(player -> player.teleportAsync(team1Spawn));
        getTeam2().forEach(player -> player.teleportAsync(team2Spawn));

        getSpectators().forEach(player -> player.teleportAsync(loc));
    }

    /**
     * Pastes a schematic at a given location
     *
     * @param location The location to paste the schematic at
     * @param file    The schematic file
     */
    public void paste(Location location, File file) {
        final ClipboardFormat clipboardFormat = ClipboardFormats.findByFile(file);
        final BlockVector3 blockVector3 = BlockVector3.at(location.getBlockX(), location.getBlockY(), location.getBlockZ());

        if (clipboardFormat == null || location.getWorld() == null)
            return;

        try (Clipboard clipboard = FaweAPI.load(file)) {

            lastEditSession = clipboard.paste(FaweAPI.getWorld("dodgeball"), blockVector3,
                    true, false, true, null);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Undoes the last paste to revert the arena pasting
     */
    public void undoBuild() {
        try (EditSession editSession = WorldEdit.getInstance().newEditSession(FaweAPI.getWorld("dodgeball"))) {
            lastEditSession.undo(editSession);
        } catch (WorldEditException e) {
            e.printStackTrace();
        }
    }

    /**
     * Kills a player, removing them from the game
     *
     * @param player The player to kill
     */
    public void killPlayer(Player player) {
        Team team = DodgeballPlayer.of(player).getTeam();
        deadPlayers.add(player);

        if (team == Team.TEAM_1)
            team1Alive--;
        else
            team2Alive--;

        player.setGameMode(GameMode.SPECTATOR);

        if (team1Alive == 0 || team2Alive == 0) {
            loser = team;
            state = new PostGameState(this);
        }
    }

    /**
     * Performs all tasks needed when the player joins the game
     *
     * @param player The player to initalize
     */
    public void initPlayer(Player player) {
        player.teleport(lobbySpawn);
        player.setGameMode(GameMode.ADVENTURE);
        player.setHealth(20);
        player.setFoodLevel(20);
        player.getInventory().clear();

        DodgeballPlayer dbPlayer = DodgeballPlayer.of(player);
        dbPlayer.setGame(this);

        if (getPlayers().count() >= maxPlayers) {
            spectators.add(player);
            player.setGameMode(GameMode.SPECTATOR);
            return;
        }

        if (getTeam1().size() <= getTeam2().size()) {
            team1.add(player);
            dbPlayer.setTeam(Team.TEAM_1);
        }
        else {
            team2.add(player);
            dbPlayer.setTeam(Team.TEAM_2);
        }
        broadcast(MiniMessage.miniMessage().deserialize(prefix + "<yellow>" + player.getName() + " <white>joined the game!" + " (" + getPlayers().count() + "/" + maxPlayers + ")"));
    }

    // Broadcast messages to all players
    public void broadcast(String message) {
        getPlayers().forEach(player -> player.sendMessage(message));
    }
    public void broadcast(Component message) {
        getPlayers().forEach(player -> player.sendMessage(message));
    }
    public void broadcastTitle(String title, String subtitle) {
        getPlayers().forEach(player -> player.sendTitle(title, subtitle, 2, 20, 0));
    }

    // Getters and setters
    public void setState(GameState state) {
        this.state = state;
    }
    public GameState getState() {
        return state;
    }
    public Stream<Player> getPlayers() {
        return Streams.concat(team1.stream(), team2.stream(), spectators.stream());
    }
    public List<Player> getSpectators() {
        return spectators;
    }
    public List<Player> getDeadPlayers() {
        return deadPlayers;
    }
    public List<Player> getTeam1() {
        return team1;
    }
    public List<Player> getTeam2() {
        return team2;
    }
    public String getTeam1Name() {
        return team1Name;
    }
    public String getTeam2Name() {
        return team2Name;
    }
    public Team getLoser() {
        return loser;
    }
    public int getTeam1Alive() {
        return team1Alive;
    }
    public void setTeam1Alive(int team1Alive) {
        this.team1Alive = team1Alive;
    }
    public int getTeam2Alive() {
        return team2Alive;
    }
    public void setTeam2Alive(int team2Alive) {
        this.team2Alive = team2Alive;
    }
    public Location getLobbyPaste() {
        return lobbyPaste;
    }
    public ConfigManager getConfigManager() {
        return configManager;
    }

    public Team getWinner() {
        return loser == Team.TEAM_1 ? Team.TEAM_2 : Team.TEAM_1;
    }

    public List<Player> getTeam(Team team) {
        if (team.equals(Team.TEAM_1))
            return team1;
        if (team.equals(Team.TEAM_2))
            return team2;

        return spectators;
    }

    public int getAliveTeam(Team team) {
        if (team.equals(Team.TEAM_1))
            return team1Alive;
        if (team.equals(Team.TEAM_2))
            return team2Alive;

        return 0;
    }
}
