package api;

import api.commands.SetSpawn;
import api.config.GlobalConfigData;
import api.entities.NPCManager;
import api.events.listener.GuiManager;
import api.events.listener.PlayerEvents;
import api.events.listener.QueueManager;
import api.events.listener.SpectatorEvents;
import api.packet.MessengerConnection;
import api.utils.Config;
import api.utils.Utils;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.java.Log;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.craftbukkit.v1_11_R1.CraftServer;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by loucass003 on 25/11/16
 */
@Getter
@ToString
@Log
public class API extends JavaPlugin
{
	@Getter
	private static API instance;
	private final String containerID = Utils.getContainerID();

	private final GuiManager guiManager;
	private final QueueManager queueManager;
	private final MessengerConnection messengerConnection;
	private final KeepAliveService keepAliveService;
	private final NPCManager npcManager;
	private final SpectatorEvents spectatorEvents;
	private final PlayerEvents playerEvents;

	@Setter
	private int maxPlayers;
	@Setter
	private int minPlayers;
	@Setter
	private long countdown;

	private final Config configManager;
	private final GlobalConfigData globalConfig;

	public API()
	{
		instance = this;

		log.info("Container ID is " + containerID);

		configManager = new Config("Global", this)
				.setConfigObject(GlobalConfigData.class);
		globalConfig = configManager.get(GlobalConfigData.class);
		configManager.loadConfig();

		guiManager = new GuiManager();
		queueManager = globalConfig.isUseQueueManager() ? new QueueManager() : null;
		messengerConnection = new MessengerConnection();
		keepAliveService = new KeepAliveService();
		npcManager = new NPCManager();
		spectatorEvents = new SpectatorEvents();
		playerEvents = new PlayerEvents();

		keepAliveService.setServerState(ServerState.STARTED);
	}

	@Override
	public void onEnable()
	{
		guiManager.start();
		queueManager.start();
		messengerConnection.start();
		keepAliveService.start();
		npcManager.start();
		spectatorEvents.start();
		playerEvents.start();

		registerCommand(new SetSpawn());

		keepAliveService.setServerState(ServerState.READY);

	}

	@Override
	public void onDisable()
	{
		keepAliveService.setServerState(ServerState.STOPPING);

		guiManager.stop();
		queueManager.stop();
		messengerConnection.stop();
		keepAliveService.stop();
		npcManager.stop();
		spectatorEvents.stop();
		playerEvents.stop();
	}

	public ServerState getServerState()
	{
		return keepAliveService.getServerState();
	}

	public static <T extends Event> T callEvent(T event)
	{
		Bukkit.getPluginManager().callEvent(event);
		return event;
	}

	public static void registerListener(Listener listener)
	{
		Bukkit.getPluginManager().registerEvents(listener, instance);
	}

	public static void registerCommand(Command command)
	{
		((CraftServer) Bukkit.getServer()).getCommandMap().register(command.getName(), command);
	}
}
