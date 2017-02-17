package api.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;

/**
 * Created by loucass003 on 26/11/16.
 */
public class Config
{
	private final String name;
	private final String filename;
	private Object configData;
	private Class<?> clazz;
	private final Plugin p;

	public Config(String name, Plugin p)
	{
		this.name = name;
		filename = name + ".json";
		this.p = p;
	}

	public Config setConfigObject(Class<?> classOfT)
	{
		clazz = classOfT;
		return this;
	}

	public void setConfigData(Object o)
	{
		configData = o;
	}

	public void loadConfig()
	{
		try
		{
			if (!p.getDataFolder().exists())
				p.getLogger().info("Creation du dossier de config: " + (p.getDataFolder().mkdir() ? "ok" : "erreur")
						+ " !");
			File f = new File(p.getDataFolder(), filename);
			if (!f.exists())
				p.getLogger().info("Creation du ficher de config: " + (f.createNewFile() ? "ok" : "erreur") + " !");
			String json = Utils.readFile(f);
			configData = new Gson().fromJson(json, clazz);
			if (configData == null)
				configData = clazz.getConstructor().newInstance();
		}
		catch (IOException | ReflectiveOperationException e)
		{
			throw new ConfigException("Cannot load config", e);
		}
	}

	public void save()
	{
		try
		{
			if (!p.getDataFolder().exists())
				p.getLogger().info("Creation du dossier de config: " + (p.getDataFolder().mkdir() ? "ok" : "erreur")
						+ " !");

			File f = new File(p.getDataFolder(), filename);
			if (!f.exists())
				p.getLogger().info("Creation du ficher de config: " + (f.createNewFile() ? "ok" : "erreur") + " !");

			Gson gson = new GsonBuilder().setPrettyPrinting()
					.registerTypeHierarchyAdapter(Location.class, LocationAdapter.INSTANCE).create();

			JsonParser jp = new JsonParser();
			JsonElement je = jp.parse(gson.toJson(configData));

			String json = gson.toJson(je);
			Utils.writeText(f, json);
		}
		catch (IOException e)
		{
			throw new ConfigException("Cannot save config", e);
		}
	}

	public String getName()
	{
		return name;
	}

	public <T> T get(Class<T> clazz)
	{
		return clazz.cast(configData);
	}
}