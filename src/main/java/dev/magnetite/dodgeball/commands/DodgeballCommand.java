package dev.magnetite.dodgeball.commands;

import dev.magnetite.dodgeball.ConfigManager;
import dev.magnetite.dodgeball.game.GameManager;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

// No need to use a framework for a plugin this small
public class DodgeballCommand implements CommandExecutor, TabExecutor {
    private final GameManager gameManager;
    private final ConfigManager configManager;
    private final List<String> subCmds = Arrays.asList("join", "create", "leave", "help", "reload");

    public DodgeballCommand(GameManager gameManager, ConfigManager configManager) {
        this.gameManager = gameManager;
        this.configManager = configManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        // Annoying no command library things
        final String noPermission = configManager.getPrefix() + "<red>You don't have permission to do that!";

        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            if (!sender.hasPermission("dodgeball.help")) {
                sender.sendMessage(MiniMessage.miniMessage().deserialize(noPermission));
                return true;
            }

            sender.sendMessage(MiniMessage.miniMessage().deserialize(Arrays.asList(
                    " <newline>",
                    " <gradient:#b92b27:#1565C0><bold>Dodgeball</bold></gradient> <#1565C0><bold>Help</bold></#1565C0> <newline>",
                    " <white>All commands for the Dodgeball Plugin. Click on a command to insert in chat!<newline>",
                    " <newline>",
                    " <dark_gray><bold>-</bold></dark_gray> <#1565C0><hover:show_text:\"<red>Permission: <gray>dodgeball.create\"><click:suggest_command:/dodgeball create>/dodgeball create</click></#1565C0> <dark_gray>- <gray>Creates a new game. <newline>",
                    " <dark_gray><bold>-</bold></dark_gray> <#1565C0><hover:show_text:\"<red>Permission: <gray>dodgeball.join\"><click:suggest_command:/dodgeball join >/dodgeball join (id)</click></#1565C0> <dark_gray>- <gray>Joins a game with the specified ID. <newline>",
                    " <dark_gray><bold>-</bold></dark_gray> <#1565C0><hover:show_text:\"<red>Permission: <gray>dodgeball.leave\"><click:suggest_command:/dodgeball leave>/dodgeball leave</click></#1565C0> <dark_gray>- <gray>Leaves the game you're currently in. <newline>",
                    " <dark_gray><bold>-</bold></dark_gray> <#1565C0><hover:show_text:\"<red>Permission: <gray>dodgeball.reload\"><click:suggest_command:/dodgeball reload>/dodgeball reload</click></#1565C0> <dark_gray>- <gray>Reloads the config file. <newline>",
                    " <dark_gray><bold>-</bold></dark_gray> <#1565C0><hover:show_text:\"<red>Permission: <gray>dodgeball.help\"><click:suggest_command:/dodgeball help>/dodgeball help</click></#1565C0> <dark_gray>- <gray>Shows this help menu. <newline>"
                    ).toString().replace("[", "").replace("]", "").replace(",", "")));
            return true;
        }

        final String subCmd = args[0];

        if (!subCmds.contains(subCmd)) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize(configManager.getPrefix() + "<red>Unknown subcommand! Use: /" + label + " help"));
            return true;
        }

        if (subCmd.equalsIgnoreCase("create")) {
            if (!sender.hasPermission("dodgeball.create")) {
                sender.sendMessage(MiniMessage.miniMessage().deserialize(noPermission));
                return false;
            }

            sender.sendMessage(MiniMessage.miniMessage().deserialize(configManager.getPrefix() + "<green>Game created!"));
            gameManager.createGame();
            return true;
        }

        if (subCmd.equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("dodgeball.reload")) {
                sender.sendMessage(MiniMessage.miniMessage().deserialize(noPermission));
                return false;
            }

            configManager.reloadConfig();
            sender.sendMessage(MiniMessage.miniMessage().deserialize(configManager.getPrefix() + "<green>Config reloaded!"));
            return true;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "You must be a player to execute this command!");
            return true;
        }

        if (subCmd.equalsIgnoreCase("join")) {
            if (!sender.hasPermission("dodgeball.join")) {
                sender.sendMessage(MiniMessage.miniMessage().deserialize(noPermission));
                return false;
            }

            if (args.length != 2) {
                sender.sendMessage(MiniMessage.miniMessage().deserialize(configManager.getPrefix() + "<red>Invalid usage! Use: /" + label + " join [id]"));
                return true;
            }

            int id;

            try {
                id = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                sender.sendMessage(MiniMessage.miniMessage().deserialize(configManager.getPrefix() + "<red>Invalid game ID!"));
                return true;
            }

            gameManager.joinGame(player, id);
            return true;
        }

        if (subCmd.equalsIgnoreCase("leave")) {
            if (!sender.hasPermission("dodgeball.leave")) {
                sender.sendMessage(MiniMessage.miniMessage().deserialize(noPermission));
                return false;
            }

            gameManager.leaveGame(player);
            return true;
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 0 || args.length == 1)
            StringUtil.copyPartialMatches(args[0], subCmds.stream().sorted().toList(), completions);
        else if (args.length == 2 && args[0].equalsIgnoreCase("join"))
            StringUtil.copyPartialMatches(args[1], IntStream.range(0, gameManager.getGames().size()).mapToObj(String::valueOf).toList(), completions);

        return completions;
    }
}
