package dev.magnetite.dodgeball.game;

import org.bukkit.*;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;

/**
 * This class is used to manage the arena of the Dodgeball plugin.
 */
public class ArenaManager {
    private final JavaPlugin plugin;

    public ArenaManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void setupWorld() {
        final String worldName ="dodgeball";
        World world = Bukkit.getWorld(worldName);

        copySchematic("Dodgeball_Arena.schem");
        copySchematic("Lobby.schem");

        if (world == null) {
            world = Bukkit.createWorld(WorldCreator.name(worldName)
                    .generateStructures(false)
                    .type(WorldType.FLAT)
                    .generator(new ChunkGenerator() {
                        @Override
                        public boolean shouldGenerateNoise() {return false;}
                        @Override
                        public boolean shouldGenerateSurface() {return false;}
                        @Override
                        public boolean shouldGenerateBedrock() {return false;}
                        @Override
                        public boolean shouldGenerateCaves() {return false;}
                        @Override
                        public boolean shouldGenerateDecorations() {return false;}
                        @Override
                        public boolean shouldGenerateMobs() {return false;}
                        @Override
                        public boolean shouldGenerateStructures() {return false;}
                    }));

            final World finalWorld = world;

            Arrays.asList(GameRule.DO_DAYLIGHT_CYCLE, GameRule.DO_WEATHER_CYCLE,
                    GameRule.DO_MOB_SPAWNING, GameRule.DO_TRADER_SPAWNING, GameRule.DO_INSOMNIA)
                    .forEach(rule -> finalWorld.setGameRule(rule, false));
        }
    }

    private void copySchematic(String name) {
        File file = new File(plugin.getDataFolder(), name);

        if (!file.exists())
            try {
                Files.copy(plugin.getResource(name),
                        file.getAbsoluteFile().toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
    }
}
