package api;

import api.commands.CommandsManager;
import api.config.GlobalConfigData;
import api.events.EventsRegisterer;
import api.events.listener.GuiManager;
import api.events.listener.QueueManager;
import api.utils.Config;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by loucass003 on 25/11/16
 */
public class API extends JavaPlugin
{

    public static API instance;
    private GuiManager guiManager;
    private QueueManager queueManager;
    private CommandsManager commandsManager;
    private EventsRegisterer eventsRegisterer;

    private int maxPlayers;
    private int minPlayers;
    private Long countdown;

    private Config configManager;
    private GlobalConfigData globalConfig;

    private boolean useQueueManager = false;

    public API()
    {
        instance = this;
        this.configManager = new Config("Global", this);
        this.configManager.setConfigObject(GlobalConfigData.class);
        this.commandsManager = new CommandsManager(this);
        this.eventsRegisterer = new EventsRegisterer(this);
    }

    @Override
    public void onEnable()
    {
        this.configManager.loadConfig();
        this.globalConfig = configManager.get(GlobalConfigData.class);
        this.commandsManager.registerCommands();
        this.eventsRegisterer.init();
        if(this.useQueueManager)
            this.queueManager = new QueueManager(maxPlayers, minPlayers, countdown);
        this.guiManager = new GuiManager(this);
    }

    @Override
    public void onDisable()
    {
        if(this.queueManager != null)
            this.queueManager.clear();
        this.eventsRegisterer.spectatorEvents.clearSpectators();
    }

    public void setCountdown(Long countdown) {
        this.countdown = countdown;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public void setMinPlayers(int minPlayers) {
        this.minPlayers = minPlayers;
    }

    public GuiManager getGuiManager() {
        return guiManager;
    }

    public static API getInstance()
    {
        return instance;
    }

    public QueueManager getQueueManager() {
        return queueManager;
    }

    public GlobalConfigData getGlobalConfig()
    {
        return globalConfig;
    }

    public Config getConfigManager() {
        return configManager;
    }

    public EventsRegisterer getEventsRegisterer() {
        return eventsRegisterer;
    }

    public void useQueueManager(boolean useQueueManager)
    {
        this.useQueueManager = useQueueManager;
        if(!useQueueManager && this.queueManager != null)
        {
            this.queueManager.clear();
            this.queueManager = null;
        }
        else if (useQueueManager && this.queueManager == null)
            this.queueManager = new QueueManager(maxPlayers, minPlayers, countdown);
    }

    public boolean isUseQueueManager() {
        return useQueueManager;
    }

    public static void setSpectator(Player e)
    {
        getInstance().eventsRegisterer.spectatorEvents.setSpectator(e);
    }

    public static void remSpectator(Player e)
    {
        getInstance().eventsRegisterer.spectatorEvents.remSpectator(e);
    }

    public static boolean isSpectator(Player p)
    {
        return getInstance().eventsRegisterer.spectatorEvents.players.contains(p);
    }
}
