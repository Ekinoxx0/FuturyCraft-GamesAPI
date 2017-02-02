package api.events.listener;

import api.API;
import api.config.ConfigLocation;
import api.events.EventsRegisterer;
import lombok.Data;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Created by loucass003 on 26/11/16.
 */
@Data
public class PlayerEvents implements Listener
{

    private EventsRegisterer registerer;
    private API main;

    public PlayerEvents(EventsRegisterer er)
    {
        this.registerer = er;
        this.main = er.getMain();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e)
    {
        ConfigLocation spawn = API.getInstance().getGlobalConfig().getSpawn();
        if(spawn == null)
            return;
        e.getPlayer().teleport(spawn.getLocation());
    }

}
