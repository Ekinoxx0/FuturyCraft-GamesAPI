package api.utils;

import com.google.gson.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.lang.reflect.Type;

/**
 * Created by loucass003 on 21/12/16.
 */
public class LocationAdapter implements JsonDeserializer<Location>
{
	public static final LocationAdapter INSTANCE = new LocationAdapter();

	public Location deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException
	{
		JsonObject o = json.getAsJsonObject();
		return new Location
				(
						Bukkit.getWorld(o.getAsJsonPrimitive("world").getAsString()),
						o.getAsJsonPrimitive("x").getAsDouble(),
						o.getAsJsonPrimitive("y").getAsDouble(),
						o.getAsJsonPrimitive("z").getAsDouble(),
						o.getAsJsonPrimitive("yaw").getAsFloat(),
						o.getAsJsonPrimitive("pitch").getAsFloat()
				);
	}
}
