package dev.magnetite.dodgeball.assemble.events;


import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AssembleBoardCreateEvent extends Event implements Cancellable {

    public static final HandlerList handlerList = new HandlerList();

    private boolean cancelled = false;

    /**
     * Assemble Board Create Event.
     *
     * @param player that the board is being created for.
     */
    public AssembleBoardCreateEvent(Player player) {
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
}
