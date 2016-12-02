package api.config;

import api.API;

/**
 * Created by loucass003 on 26/11/16.
 */
public class GlobalConfigData
{

    public ConfigLocation spawn;

    public GlobalConfigData()
    {
        spawn = new ConfigLocation(API.getInstance().getServer().getWorlds().get(0).getSpawnLocation());
    }

    public ConfigLocation getSpawn() {
        return spawn;
    }

    public void setSpawn(ConfigLocation spawn) {
        this.spawn = spawn;
    }
}
