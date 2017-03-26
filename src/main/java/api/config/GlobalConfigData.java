package api.config;

import api.API;
import lombok.Data;
import org.bukkit.Location;

/**
 * Created by loucass003 on 26/11/16.
 */
@Data
public class GlobalConfigData
{
	private ConfigDeployer deployer;
	private Location spawn;
	private boolean useQueueManager;

	public GlobalConfigData()
	{
		deployer = new ConfigDeployer();
		useQueueManager = false;
	}

	public void onEnable()
	{
		spawn = API.getInstance().getServer().getWorlds().get(0).getSpawnLocation();
	}
}
