package dev.magnetite.dodgeball;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.List;

/**
 * This class is used to store data about the configuration of the Dodgeball plugin.
 */
public class ConfigManager {
    private final JavaPlugin plugin;
    private FileConfiguration config;

    private String prefix;

    private String team1Name;
    private String team2Name;

    private int minPlayers;
    private int maxPlayers;
    private int dodgeballAmount;
    private int countdown;

    private Vector lobbyLocation;
    private float lobbyYaw;
    private float lobbyPitch;

    private Vector team1Spawn;
    private float team1SpawnYaw;
    private float team1SpawnPitch;

    private Vector team2Spawn;
    private float team2SpawnYaw;
    private float team2SpawnPitch;

    private List<String> winnerCmds;
    private List<String> loserCmds;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        loadConfig();
    }

    public void loadConfig() {
        config = plugin.getConfig();
        prefix = config.getString("prefix");

        team1Name = config.getString("game-options.team-one-name");
        team2Name = config.getString("game-options.team-two-name");

        minPlayers = config.getInt("game-options.min-players");
        maxPlayers = config.getInt("game-options.max-players");
        dodgeballAmount = config.getInt("game-options.dodgeball-amount");
        countdown = config.getInt("game-options.countdown");

        lobbyLocation = new Vector(config.getInt("lobby-spawn.x"), config.getInt("lobby-spawn.y"),
                config.getInt("lobby-spawn.z"));
        lobbyYaw = (float) config.getDouble("lobby-spawn.yaw");
        lobbyPitch = (float) config.getDouble("lobby-spawn.pitch");

        team1Spawn = new Vector(config.getInt("team-one-spawn.x"), config.getInt("team-one-spawn.y"),
                config.getInt("team-one-spawn.z"));

        team1SpawnYaw = (float) config.getDouble("team-one-spawn.yaw");
        team1SpawnPitch = (float) config.getDouble("team-one-spawn.pitch");

        team2Spawn = new Vector(config.getInt("team-two-spawn.x"), config.getInt("team-two-spawn.y"),
                config.getInt("team-two-spawn.z"));

        team2SpawnYaw = (float) config.getDouble("team-two-spawn.yaw");
        team2SpawnPitch = (float) config.getDouble("team-two-spawn.pitch");

        winnerCmds = config.getStringList("game-end-commands.winners");
        loserCmds = config.getStringList("game-end-commands.losers");
    }

    public void reloadConfig() {
        Dodgeball.getPlugin(Dodgeball.class).reloadConfig();
        config = Dodgeball.getPlugin(Dodgeball.class).getConfig();
        loadConfig();
    }

    public String getPrefix() {
        return prefix;
    }

    public String getTeam1Name() {
        return team1Name;
    }

    public String getTeam2Name() {
        return team2Name;
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public int getDodgeballAmount() {
        return dodgeballAmount;
    }

    public int getCountdown() {
        return countdown;
    }

    public Vector getLobbyLocation() {
        return lobbyLocation;
    }

    public float getLobbyYaw() {
        return lobbyYaw;
    }

    public float getLobbyPitch() {
        return lobbyPitch;
    }

    public Vector getTeam1Spawn() {
        return team1Spawn;
    }

    public float getTeam1SpawnYaw() {
        return team1SpawnYaw;
    }

    public float getTeam1SpawnPitch() {
        return team1SpawnPitch;
    }

    public Vector getTeam2Spawn() {
        return team2Spawn;
    }

    public float getTeam2SpawnYaw() {
        return team2SpawnYaw;
    }

    public float getTeam2SpawnPitch() {
        return team2SpawnPitch;
    }

    public List<String> getWinnerCmds() {
        return winnerCmds;
    }

    public List<String> getLoserCmds() {
        return loserCmds;
    }
}
