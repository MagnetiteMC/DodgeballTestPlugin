package dev.magnetite.dodgeball.state.impl;

import dev.magnetite.dodgeball.Dodgeball;
import dev.magnetite.dodgeball.DodgeballPlayer;
import dev.magnetite.dodgeball.game.Game;
import dev.magnetite.dodgeball.state.GameState;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static dev.magnetite.dodgeball.DodgeballPlayer.Team;

/**
 * The game state is the state that the game is in.
 */
public class PlayingState extends GameState {
    private final ItemStack snowball;
    private final List<ChatColor> colors = Arrays.asList(ChatColor.DARK_GREEN, ChatColor.GREEN, ChatColor.YELLOW,
            ChatColor.RED, ChatColor.DARK_RED);
    private int countdown = 5;

    public PlayingState(Game game) {
        super(game);

        game.setTeam1Alive(game.getTeam1().size());
        game.setTeam2Alive(game.getTeam2().size());
        game.createArena();

        snowball = new ItemStack(Material.SNOWBALL, game.getConfigManager().getDodgeballAmount());
        ItemMeta snowballMeta = snowball.getItemMeta();
        snowballMeta.setDisplayName(ChatColor.RED + "Dodgeball");
        snowballMeta.setLore(Arrays.asList(
                ChatColor.GRAY + "Throw this at your opponents to",
                ChatColor.GRAY + "knock them out of the game!"
        ));
        snowball.setItemMeta(snowballMeta);

        game.getTeam1().forEach(player -> player.getInventory().addItem(snowball));
        game.getTeam2().forEach(player -> player.getInventory().addItem(snowball));

        new BukkitRunnable() {
            @Override
            public void run() {

                if (countdown == 0) {
                    cancel();
                    game.getPlayers().forEach(player -> player.sendActionBar(ChatColor.GREEN + "THROW!"));
                    return;
                }

                game.getPlayers().forEach(player -> player.sendActionBar("Starting in " + colors.get(countdown - 1) + countdown));
                countdown--;
            }
        }.runTaskTimerAsynchronously(Dodgeball.getPlugin(Dodgeball.class), 0L,20L);
    }

    @Override
    public void onPlayerDamaged(EntityDamageEvent e) {
        e.setCancelled(true);
    }

    @Override
    public void onProjectileHit(ProjectileHitEvent e) {
        // If they hit a block, drop the snowball

        if (e.getHitBlock() != null) {
            Location loc = e.getHitBlock().getLocation().add(0, 1, 0);
            loc.getWorld().dropItemNaturally(loc, snowball.asOne()).setGlowing(true);
            return;
        }

        if (!(e.getHitEntity() instanceof Player player)) {
            return;
        }

        // If they hit a player, kill them and drop the snowball

        final DodgeballPlayer playerDB = DodgeballPlayer.of(player);
        final Team team = playerDB.getTeam();
        final Location loc = player.getLocation().add(0, 1, 0);

        loc.getWorld().dropItemNaturally(loc, snowball.asOne()).setGlowing(true);

        if (team.equals(Team.SPECTATOR) || !(e.getEntity().getShooter() instanceof Player damager))
            return;

        final DodgeballPlayer damagerDB = DodgeballPlayer.of(damager);
        final Team damagerTeam = damagerDB.getTeam();

        if (damagerTeam.equals(team) || game.getDeadPlayers().contains(player))
            return;

        // We now know they're opposing, and alive!

        game.killPlayer(player);
        game.broadcast(ChatColor.RED + player.getName() + " is out! They were hit by " + damager.getName() + "!");
        player.sendTitle(ChatColor.RED + "You're out!", "You were hit by " + damager.getName() + "!", 5, 20, 5);
    }

    @Override
    public void onProjectileLaunch(ProjectileLaunchEvent e) {
        if (countdown > 0) {
            ((Player) e.getEntity().getShooter()).sendActionBar(ChatColor.RED + "You have " + countdown + " seconds before throwing!");
            e.setCancelled(true);
        }
    }

    @Override
    public void onPlayerMove(PlayerMoveEvent e) {
        // Kill the player if they touch obsidian
        final Block b = e.getPlayer().getLocation().subtract(0, 1, 0).getBlock();
        final Block b2 = e.getPlayer().getLocation().subtract(0, 2, 0).getBlock();

        if (b.getType() == Material.OBSIDIAN || (b.getType()).equals(Material.AIR) && b2.getType().equals(Material.OBSIDIAN)) {
            e.getPlayer().sendTitle(ChatColor.RED + "You're out!", "Don't touch the obsidian next time!", 5, 20, 5);
            game.broadcast(ChatColor.RED + e.getPlayer().getName() + " is out! They touched the obsidian!");
            game.killPlayer(e.getPlayer());
        }
    }

    @Override
    public void onPlayerJoin(Player player) {
        // The game has already started, so we add them as a spectator
        game.getSpectators().add(player);
        player.setGameMode(GameMode.SPECTATOR);
    }

    @Override
    public void onPlayerLeave(Player player) {
        final DodgeballPlayer dbPlayer = DodgeballPlayer.of(player);

        game.getTeam(dbPlayer.getTeam()).remove(player);
        game.broadcast(MiniMessage.miniMessage().deserialize(game.getConfigManager().getPrefix() + "<red>" + player.getName() + " has left the game!"));
        game.killPlayer(player);
    }

    @Override
    public List<String> updateScoreboard(Player player) {
        final String dateFormatted = new SimpleDateFormat("MM/dd/yy").format(new Date());
        final boolean isInTeam1 = game.getTeam1().contains(player);

        return Arrays.asList(
                "&7   (" + dateFormatted + ")  ",
                "",
                "&c&l" + game.getTeam1Name() + "&7: &f" + game.getTeam1Alive() + (isInTeam1 ? " &7(You)" : ""),
                "&9&l" + game.getTeam2Name() + "&7: &f" + game.getTeam2Alive() + (isInTeam1 ? "" : " &7(You)"),
                "",
                "&fPlayers left&7: &f" + (game.getTeam1Alive() + game.getTeam2Alive()));
    }
}
