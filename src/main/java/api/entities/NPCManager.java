package api.entities;

import api.utils.NMSReflection;
import api.utils.SimpleManager;
import lombok.ToString;
import lombok.extern.java.Log;
import net.minecraft.server.v1_11_R1.Entity;
import net.minecraft.server.v1_11_R1.EntityTypes;

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

	public static final NMSReflection.MethodAccessor<Void> REGISTER_ENTITY_METHOD =
			NMSReflection.getMethodAccessor(EntityTypes.class, "a", int.class, String.class, Class.class,
					String.class);

	public static void register(Class<? extends Entity> entityClass, String name, String displayName, int id)
	{
		REGISTER_ENTITY_METHOD.invoke(null, id, name, entityClass, displayName);
	}

}
