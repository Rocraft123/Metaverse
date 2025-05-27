package net.Events;

import net.Dimensions.Dimension;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PlayerEnterDimensionEvent extends Event implements Cancellable {

    private static final HandlerList handlerList = new HandlerList();
    private boolean cancelled = false;

    private final Player player;
    private final Location location;
    private final Dimension dimension;

    public PlayerEnterDimensionEvent(Player player, Location location, Dimension dimension) {
        this.player = player;
        this.location = location;
        this.dimension = dimension;
    }

    public Player getPlayer() {
        return player;
    }

    public Location getLocation() {
        return location;
    }


    public Dimension getDimension() {
        return dimension;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }

    public static @NotNull HandlerList getHandlerList() {
        return handlerList;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }
}
