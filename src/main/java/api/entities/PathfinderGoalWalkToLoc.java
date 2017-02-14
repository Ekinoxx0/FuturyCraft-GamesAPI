package api.entities;

import net.minecraft.server.v1_11_R1.EntityInsentient;
import net.minecraft.server.v1_11_R1.NavigationAbstract;
import net.minecraft.server.v1_11_R1.PathEntity;
import net.minecraft.server.v1_11_R1.PathfinderGoal;
import org.bukkit.Location;

/**
 * Created by loucass003 on 2/1/17.
 */

public class PathfinderGoalWalkToLoc extends PathfinderGoal
{
	private double speed;
	private Location loc;
	private NavigationAbstract navigation;

	public PathfinderGoalWalkToLoc(EntityInsentient entity, Location loc, double speed)
	{
		this.loc = loc;
		this.navigation = entity.getNavigation();
		this.speed = speed;
	}

	public boolean a()
	{
		return true;
	}

	public void c()
	{
		PathEntity pathEntity = this.navigation.a(loc.getX(), loc.getY(), loc.getZ());
		this.navigation.a(pathEntity, speed);
	}
}
