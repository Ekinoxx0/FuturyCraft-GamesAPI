package api.entities;

import api.utils.SimpleManager;
import lombok.ToString;
import lombok.extern.java.Log;
import net.minecraft.server.v1_11_R1.Entity;
import net.minecraft.server.v1_11_R1.EntityTypes;

import java.lang.reflect.Method;

/**
 * Created by loucass003 on 1/31/17.
 */
@ToString
@Log
public class NPCManager implements SimpleManager
{
	public void init()
	{
		register(CustomZombie.class, "zombie_test", "Zombie", 54);
	}

	public static void register(Class<? extends Entity> entityClass, String name, String displayName, int id)
	{
		try
		{
			Class<EntityTypes> clazz = EntityTypes.class;
			Method method = clazz.getDeclaredMethod("a", int.class, String.class, Class.class, String.class);
			method.setAccessible(true);
			method.invoke(null, id, name, entityClass, displayName);
		}
		catch (ReflectiveOperationException e)
		{
			throw new RuntimeException(e);
		}
	}

}
