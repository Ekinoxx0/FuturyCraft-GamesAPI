package api.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.internal.Primitives;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;

/**
 * Created by loucass003 on 26/11/16.
 */
public class Config
{
    private String name;
    private String filename;
    private Object configData;
    private Class clazz;
    private Plugin p;

    public Config(String name, Plugin p)
    {
        this.name = name;
        this.filename = name + ".json";
        this.p = p;
    }

    public void setConfigObject(Class classOfT)
    {
        this.clazz = classOfT;
    }

    public void setConfigData(Object o)
    {
        this.configData = o;
    }

    public void loadConfig()
    {
        try
        {
            if(!p.getDataFolder().exists())
                p.getLogger().info("Creation du dossier de config : " + (p.getDataFolder().mkdir() ? "ok" : "erreur") + " !");
            File f = new File(p.getDataFolder(), filename);
            if(!f.exists())
                p.getLogger().info("Creation du ficher de config : " + (f.createNewFile() ? "ok" : "erreur") + " !");
            String json = Utils.readFile(f);
            configData = new Gson().fromJson(json, clazz);
            if(configData == null)
                configData = clazz.newInstance();
        }
        catch (IOException | InstantiationException | IllegalAccessException e)
        {
            e.printStackTrace();
        }
    }

    public void save()
    {
        try
        {
            if(!p.getDataFolder().exists())
                p.getLogger().info("Creation du dossier de config : " + (p.getDataFolder().mkdir() ? "ok" : "erreur") + " !");
            File f = new File(p.getDataFolder(), filename);
            if(!f.exists())
                p.getLogger().info("Creation du ficher de config : " + (f.createNewFile() ? "ok" : "erreur") + " !");
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonParser jp = new JsonParser();
            JsonElement je = jp.parse(gson.toJson(configData));
            String json = gson.toJson(je);
            Utils.writeText(f, json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        return name;
    }

    public <T> T get(Class<T> clazz)
    {
        return Primitives.wrap(clazz).cast(configData);
    }
}