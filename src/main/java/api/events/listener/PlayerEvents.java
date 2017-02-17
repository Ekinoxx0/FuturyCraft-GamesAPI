package api.events.listener;

import api.API;
import api.utils.SimpleManager;
import lombok.ToString;
import lombok.extern.java.Log;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Created by loucass003 on 26/11/16.
 */
@ToString
@Log
public class PlayerEvents implements Listener, SimpleManager
{
	@Override
	public void init() {
		API.registerListener(this);
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e)
	{
		Location spawn = API.getInstance().getGlobalConfig().getSpawn();
		if (spawn == null)
			return;
		e.getPlayer().teleport(spawn);
	}
}
