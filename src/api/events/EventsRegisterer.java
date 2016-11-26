package api.events;

import api.API;
import api.events.listener.PlayerEvents;

/**
 * Created by loucass003 on 26/11/16.
 */
public class EventsRegisterer {

    public API main;

    public PlayerEvents playerEvents;

    public EventsRegisterer(API main)
    {
        this.main = main;
    }

    public void init()
    {
        this.main.getPlugin().getServer().getPluginManager().registerEvents(playerEvents = new PlayerEvents(this), this.main.getPlugin());
    }
}
