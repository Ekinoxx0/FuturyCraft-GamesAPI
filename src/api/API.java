package api;

import api.commands.CommandsManager;
import api.config.GlobalConfigData;
import api.events.EventsRegisterer;
import api.events.listener.GuiManager;
import api.events.listener.QueueManager;
import api.utils.Config;
import org.bukkit.plugin.Plugin;

/**
 * Created by loucass003 on 25/11/16.
 */
public class API {

    public static API instance;
    private Plugin plugin;
    private GuiManager guiManager;
    private QueueManager queueManager;
    private CommandsManager commandsManager;
    private EventsRegisterer eventsRegisterer;

    private int maxPlayers;
    private int minPlayers;
    private Long countdown;

    private Config configManager;
    private GlobalConfigData globalConfig;

    boolean useQueueManager = true;

    public API(Plugin plugin)
    {
        instance = this;
        this.plugin = plugin;
        this.configManager = new Config("Global");
        this.configManager.setConfigObject(GlobalConfigData.class);
        this.commandsManager = new CommandsManager(this);
        this.eventsRegisterer = new EventsRegisterer(this);
    }

    public void init()
    {
        this.configManager.loadConfig();
        this.globalConfig = configManager.get(GlobalConfigData.class);
        this.commandsManager.registerCommands();
        this.eventsRegisterer.init();
        if(this.useQueueManager)
            this.queueManager = new QueueManager(maxPlayers, minPlayers, countdown);
        this.guiManager = new GuiManager(this);

    }

    public void unload()
    {
        if(this.queueManager != null)
            this.queueManager.clear();
    }

    public Plugin getPlugin() {
        return plugin;
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

    public static API getInstance() {
        return instance;
    }

    public QueueManager getQueueManager() {
        return queueManager;
    }

    public GlobalConfigData getGlobalConfig() {
        return globalConfig;
    }

    public Config getConfigManager() {
        return configManager;
    }

    public EventsRegisterer getEventsRegisterer() {
        return eventsRegisterer;
    }

    public void useQueueManager(boolean useQueueManager) {
        this.useQueueManager = useQueueManager;
    }
}
