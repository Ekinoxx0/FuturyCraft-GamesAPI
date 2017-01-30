package api.events;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created by loucass003 on 26/11/16.
 */
public class SetSpawnEvent extends Event implements Cancellable
{
    private final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private Player player;
    private Location location;

    public SetSpawnEvent(Player p, Location l)
    {
        this.player = p;
        this.location = l;
    }

    public Location getLocation() {
        return location;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        cancelled = b;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
