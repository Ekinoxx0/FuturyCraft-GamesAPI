package api;

import api.commands.CommandsManager;
import api.config.GlobalConfigData;
import api.entities.NPCManager;
import api.events.EventsRegisterer;
import api.events.listener.GuiManager;
import api.events.listener.QueueManager;
import api.packet.MessengerConnection;
import api.utils.Config;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by loucass003 on 25/11/16
 */
@Getter @Setter
public class API extends JavaPlugin
{
    private static API instance;
    private GuiManager guiManager;
    private QueueManager queueManager;
    private CommandsManager commandsManager;
    private EventsRegisterer eventsRegisterer;
	private MessengerConnection messenger;
	private KeepAliveService keepAliveService;
    private NPCManager npcManager;

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
        this.keepAliveService = new KeepAliveService(this);
		this.npcManager = new NPCManager();
    }

    @Override
    public void onEnable()
    {
        this.configManager.loadConfig();
        this.globalConfig = configManager.get(GlobalConfigData.class);
        this.npcManager.init();
		this.messenger = new MessengerConnection(globalConfig.getDeployer().getSocketHost(), globalConfig.getDeployer().getSocketPort());
		this.keepAliveService.init();
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
        this.eventsRegisterer.getSpectatorEvents().clearSpectators();
    }

    public static API getInstance()
    {
        return instance;
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

    public static boolean isSpectator(Player p)
    {
        return getInstance().getEventsRegisterer().getSpectatorEvents().getPlayers().contains(p);
    }
}
