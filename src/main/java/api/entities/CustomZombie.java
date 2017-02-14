package api.entities;

import api.API;
import api.utils.Utils;
import net.minecraft.server.v1_11_R1.*;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_11_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftLivingEntity;
import java.util.Set;
import java.util.logging.Level;

/**
 * Created by loucass003 on 2/1/17.
 */
public class CustomZombie extends EntityZombie
{
	public CustomZombie(World world)
	{
		super(world);
		this.setCustomName(ChatColor.RED + "boby !");
		this.setCustomNameVisible(true);
		this.setAI(false);
		((CraftLivingEntity) this.getBukkitEntity()).setRemoveWhenFarAway(true);
	}

	@Override
	public void setAI(boolean flag)
	{
		if(flag)
	 		this.r();
		else
		{
			Set gb = Utils.getField("b", PathfinderGoalSelector.class, goalSelector, Set.class, true);
			Set gc = Utils.getField("c", PathfinderGoalSelector.class, goalSelector, Set.class, true);
			Set tb = Utils.getField("b", PathfinderGoalSelector.class, targetSelector, Set.class, true);
			Set tc = Utils.getField("c", PathfinderGoalSelector.class, targetSelector, Set.class, true);

			gb.clear();
			gc.clear();
			tb.clear();
			tc.clear();
		}
	}

	public void gotoLoc(Location l)
	{
		this.goalSelector.a(1, new PathfinderGoalWalkToLoc(this, l, 2D));
	}

	@Override
	public void setOnFire(int i)
	{
		super.setOnFire(0);
	}

	public static CustomZombie spawn(Location loc){
		CustomZombie zombie = new CustomZombie(((CraftWorld) loc.getWorld()).getHandle());
		zombie.setPosition(loc.getX(), loc.getY(), loc.getZ());
		if (!zombie.getWorld().addEntity(zombie))
			API.getInstance().getLogger().log(Level.INFO, "Entity " + zombie + " could not be added to the world");
		return zombie;
	}
}