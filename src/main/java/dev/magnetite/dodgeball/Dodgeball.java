package dev.magnetite.dodgeball;

import dev.magnetite.dodgeball.assemble.Assemble;
import dev.magnetite.dodgeball.assemble.AssembleStyle;
import dev.magnetite.dodgeball.assemble.DodgeballScoreboardAdapter;
import dev.magnetite.dodgeball.commands.DodgeballCommand;
import dev.magnetite.dodgeball.game.ArenaManager;
import dev.magnetite.dodgeball.game.GameManager;
import dev.magnetite.dodgeball.listeners.GameStateListener;
import dev.magnetite.dodgeball.papi.DodgeballExpansion;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * The main class of the plugin
 */
public final class Dodgeball extends JavaPlugin {
    private Assemble assemble;

    /**
     * Called when the plugin is enabled
     */
    @Override
    public void onEnable() {
        final ConfigManager configManager = new ConfigManager(this);
        final GameManager gameManager = new GameManager(configManager);

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new DodgeballExpansion().register();
        }

        Bukkit.getPluginManager().registerEvents(new GameStateListener(), this);
        getCommand("dodgeball").setExecutor(new DodgeballCommand(gameManager, configManager));

        saveDefaultConfig();
        setupAssemble();
        new ArenaManager(this).setupWorld();
    }

    /**
     * Sets up the scoreboard
     */
    private void setupAssemble() {
        assemble = new Assemble(this, new DodgeballScoreboardAdapter());
        assemble.setTicks(10);
        assemble.setAssembleStyle(AssembleStyle.MODERN);
    }

    /**
     * Called when the plugin is disabled
     */
    @Override
    public void onDisable() {
        assemble.cleanup();
    }
}
