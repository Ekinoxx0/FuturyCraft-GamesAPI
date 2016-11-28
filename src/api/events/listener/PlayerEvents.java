package api.events.listener;

import api.API;
import api.events.EventsRegisterer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Created by loucass003 on 26/11/16.
 */
public class PlayerEvents implements Listener
{

    public EventsRegisterer registerer;
    public API main;

    public PlayerEvents(EventsRegisterer er)
    {
        this.registerer = er;
        this.main = er.main;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e)
    {
        e.getPlayer().teleport(API.getInstance().getGlobalConfig().getSpawn().getLocation());
    }

}
