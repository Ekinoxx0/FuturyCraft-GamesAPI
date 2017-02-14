package api.config;

import api.API;
import lombok.Data;

/**
 * Created by loucass003 on 26/11/16.
 */
@Data
public class GlobalConfigData
{
	private ConfigDeployer deployer;
	private ConfigLocation spawn;

	public GlobalConfigData()
	{
		deployer = new ConfigDeployer("localhost", 5555, false);
		spawn = new ConfigLocation(API.getInstance().getServer().getWorlds().get(0).getSpawnLocation());
	}
}
