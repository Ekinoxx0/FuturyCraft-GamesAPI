package api.entities;

import api.API;
import api.utils.NMSReflection;
import net.minecraft.server.v1_11_R1.EntityZombie;
import net.minecraft.server.v1_11_R1.PathfinderGoalSelector;
import net.minecraft.server.v1_11_R1.World;
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
		setCustomName("§k§e!!§cBOBY§k§e!!");
		setCustomNameVisible(true);
		setAI(false);
		((CraftLivingEntity) getBukkitEntity()).setRemoveWhenFarAway(true);
	}

	private static final NMSReflection.FieldAccessor<Set<?>> GOAL_SELECTOR_B_FIELD = NMSReflection
			.getFieldAccessor(PathfinderGoalSelector.class, "b");
	private static final NMSReflection.FieldAccessor<Set<?>> GOAL_SELECTOR_C_FIELD = NMSReflection
			.getFieldAccessor(PathfinderGoalSelector.class, "c");

	@Override
	public void setAI(boolean flag)
	{
		if (flag)
		{
			r();
			return;
		}

		GOAL_SELECTOR_B_FIELD.get(goalSelector).clear();
		GOAL_SELECTOR_C_FIELD.get(goalSelector).clear();
		GOAL_SELECTOR_B_FIELD.get(targetSelector).clear();
		GOAL_SELECTOR_C_FIELD.get(targetSelector).clear();
	}

	public void gotoLoc(Location l)
	{
		goalSelector.a(1, new PathfinderGoalWalkToLoc(this, l, 2D));
	}

	@Override
	public void setOnFire(int i)
	{
		super.setOnFire(0);
	}

	public static CustomZombie spawn(Location loc)
	{
		CustomZombie zombie = new CustomZombie(((CraftWorld) loc.getWorld()).getHandle());
		zombie.setPosition(loc.getX(), loc.getY(), loc.getZ());
		if (!zombie.getWorld().addEntity(zombie))
			API.getInstance().getLogger().log(Level.INFO, "Entity " + zombie + " could not be added to the world");
		return zombie;
	}
}
