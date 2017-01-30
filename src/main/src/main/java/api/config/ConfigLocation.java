package api.config;

import api.API;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

/**
 * Created by loucass003 on 26/11/16.
 */
public class ConfigLocation
{

    public Vector pos;
    public Vector orientation;
    public String world;

    public ConfigLocation(String world, Vector pos, Vector orientation)
    {
        this.world = world;
        this.pos = pos;
        this.orientation = orientation;
    }

    public ConfigLocation(Location loc)
    {
        this(loc.getWorld().getName(), loc.toVector(), loc.getDirection().toBlockVector());
    }

    public ConfigLocation()
    {
        this(null, null, null);
    }

    public Location getLocation()
    {
        if(this.world == null)
            return null;
        World w = API.getInstance().getServer().getWorld(this.world);
        if(w == null)
            return null;
        Location l = new Location(w, pos.getX(), pos.getY(), pos.getZ());
        if(orientation != null)
            l.setDirection(this.orientation);
        return l;
    }

}
