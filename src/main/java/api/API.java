package api;

import api.commands.CommandsManager;
import api.config.GlobalConfigData;
import api.entities.NPCManager;
import api.events.EventsRegisterer;
import api.events.listener.GuiManager;
import api.events.listener.QueueManager;
import api.packet.MessengerConnection;
import api.utils.Config;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by loucass003 on 25/11/16
 */
@Getter
@Setter
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

	private boolean useQueueManager;

	public API()
	{
		this.globalConfig = configManager.get(GlobalConfigData.class);
		this.configManager.loadConfig();

		this.messenger = new MessengerConnection(globalConfig.getDeployer().getSocketHost(),
				globalConfig.getDeployer().getSocketPort());

		instance = this;
		this.configManager = new Config("Global", this);
		this.configManager.setConfigObject(GlobalConfigData.class);
		this.commandsManager = new CommandsManager(this);
		this.eventsRegisterer = new EventsRegisterer(this);
		this.keepAliveService = new KeepAliveService();
		this.npcManager = new NPCManager();

		keepAliveService.setServerState(ServerState.STARTED);
	}

	@Override
	public void onEnable()
	{
		this.npcManager.init();

		this.keepAliveService.init();
		this.commandsManager.registerCommands();
		this.eventsRegisterer.init();
		if (this.useQueueManager)
			this.queueManager = new QueueManager(maxPlayers, minPlayers, countdown);
		this.guiManager = new GuiManager(this);

		keepAliveService.setServerState(ServerState.READY);
	}

	@Override
	public void onDisable()
	{
		if (this.queueManager != null)
			this.queueManager.clear();
		this.eventsRegisterer.getSpectatorEvents().clearSpectators();

		messenger.stop();
	}

	public static ServerState getServerState()
	{
		return instance.keepAliveService.getServerState();
	}

	public static API getInstance()
	{
		return instance;
	}

	public void useQueueManager(boolean useQueueManager)
	{
		this.useQueueManager = useQueueManager;
		if (!useQueueManager && this.queueManager != null)
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