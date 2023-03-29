package dev.magnetite.dodgeball.assemble.events;

import dev.magnetite.dodgeball.assemble.AssembleBoard;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AssembleBoardCreatedEvent extends Event {

    public static final HandlerList handlerList = new HandlerList();

    private final boolean cancelled = false;

    /**
     * Assemble Board Created Event.
     *
     * @param board of player.
     */
    public AssembleBoardCreatedEvent(AssembleBoard board) {
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
