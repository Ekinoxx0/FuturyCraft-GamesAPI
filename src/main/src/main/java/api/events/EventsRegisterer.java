package api.events;

import api.API;
import api.events.listener.PlayerEvents;
import api.events.listener.SpectatorEvents;
import lombok.Data;
import org.bukkit.Server;
import org.bukkit.plugin.PluginManager;

/**
 * Created by loucass003 on 26/11/16.
 */
@Data
public class EventsRegisterer
{

    private API main;
    private PlayerEvents playerEvents;
    private SpectatorEvents spectatorEvents;

    public EventsRegisterer(API main)
    {
        this.main = main;
    }

    public void init()
    {
        PluginManager pm = this.main.getServer().getPluginManager();
        pm.registerEvents(playerEvents = new PlayerEvents(this), this.main);
        pm.registerEvents(spectatorEvents = new SpectatorEvents(this), this.main);
    }
}
