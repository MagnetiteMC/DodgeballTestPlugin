package dev.magnetite.dodgeball.assemble;

import dev.magnetite.dodgeball.assemble.events.AssembleBoardCreateEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Assemble {

	private final JavaPlugin plugin;

	private final AssembleAdapter adapter;
	private AssembleThread thread;
	private AssembleListener listeners;
	private AssembleStyle assembleStyle = AssembleStyle.MODERN;

	private final Map<UUID, AssembleBoard> boards;

	private long ticks = 2;

	private final ChatColor[] chatColorCache = ChatColor.values();

	/**
	 * Assemble.
	 *
	 * @param plugin instance.
	 * @param adapter that is being provided.
	 */
	public Assemble(JavaPlugin plugin, AssembleAdapter adapter) {
		if (plugin == null) {
			throw new RuntimeException("Assemble can not be instantiated without a plugin instance!");
		}

		this.plugin = plugin;
		this.adapter = adapter;
		this.boards = new ConcurrentHashMap<>();

		this.setup();
	}

	/**
	 * Setup Assemble.
	 */
	public void setup() {
		// Register Events.
		listeners = new AssembleListener(this);
		plugin.getServer().getPluginManager().registerEvents(listeners, this.plugin);

		// Ensure that the thread has stopped running.
		if (thread != null) {
			thread.setActive(false);
			thread = null;
		}

		// Register new boards for existing online players.
		for (Player player : this.getPlugin().getServer().getOnlinePlayers()) {
			// Call Events if enabled.
			if (this.isCallEvents()) {
				AssembleBoardCreateEvent createEvent = new AssembleBoardCreateEvent(player);

				Bukkit.getPluginManager().callEvent(createEvent);
				if (createEvent.isCancelled()) {
					continue;
				}
			}

			getBoards().putIfAbsent(player.getUniqueId(), new AssembleBoard(player, this));
		}

		// Start Thread.
		this.thread = new AssembleThread(this);
	}

	/**
	 * Cleanup Assemble.
	 */
	public void cleanup() {
		// Stop thread.
		if (this.thread != null) {
			thread.setActive(false);
			this.thread = null;
		}

		// Unregister listeners.
		if (listeners != null) {
			HandlerList.unregisterAll(listeners);
			listeners = null;
		}

		// Destroy player scoreboards.
		for (UUID uuid : getBoards().keySet()) {
			Player player = Bukkit.getPlayer(uuid);

			if (player == null || !player.isOnline()) {
				continue;
			}

			getBoards().remove(uuid);
			player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
		}
	}

	public JavaPlugin getPlugin() {
		return plugin;
	}

	public Map<UUID, AssembleBoard> getBoards() {
		return boards;
	}

	public boolean isCallEvents() {
		return true;
	}

	public ChatColor[] getChatColorCache() {
		return chatColorCache;
	}

	public boolean isHook() {
		return false;
	}

	public AssembleAdapter getAdapter() {
		return adapter;
	}

	public long getTicks() {
		return ticks;
	}

	public AssembleStyle getAssembleStyle() {
		return assembleStyle;
	}

	public void setTicks(long ticks) {
		this.ticks = ticks;
	}

	public void setAssembleStyle(AssembleStyle assembleStyle) {
		this.assembleStyle = assembleStyle;
	}
}
