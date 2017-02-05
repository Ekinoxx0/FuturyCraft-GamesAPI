package api.entities;

import net.minecraft.server.v1_11_R1.Entity;
import net.minecraft.server.v1_11_R1.EntityTypes;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by loucass003 on 1/31/17.
 */
public class NPCManager
{
	public void init()
	{
		NPCManager.register(CustomZombie.class, "zombie_test", "Zombie", 54);
	}

	public static void register(Class<? extends Entity> entityClass , String name, String displayname, int id)
	{
		try
		{
			Class<EntityTypes> clazz = EntityTypes.class;
			Method method = clazz.getDeclaredMethod("a", int.class, String.class, Class.class, String.class);
			method.setAccessible(true);
			method.invoke(null, id, name, entityClass, displayname);
		}
		catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e)
		{
			e.printStackTrace();
		}
	}

}
