package api.events.listener;

import api.API;
import api.gui.ScoreboardSign;
import api.interfaces.QueueListener;
import api.utils.SimpleManager;
import lombok.ToString;
import lombok.extern.java.Log;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by loucass003 on 20/11/16.
 */
@ToString
@Log
public class QueueManager implements Listener, SimpleManager
{
	private final int maxPlayers;
	private final int minPlayers;
	private final long countdown;
	private long launchTime;
	private long currentTime = -1L;
	private boolean isFinished;
	private boolean started;
	private int playerCount;
	private int threadId = -1;
	private String scoreboardName = "Unknown";

	private final Map<Player, ScoreboardSign> scoreboards = new HashMap<>();
	private final List<QueueListener> listeners = new ArrayList<>();

	public QueueManager()
	{
		API api = API.getInstance();
		maxPlayers = api.getMaxPlayers();
		minPlayers = api.getMinPlayers();
		countdown = api.getCountdown();
		playerCount = Bukkit.getOnlinePlayers().size();
	}

	@Override
	public void init()
	{
		API.registerListener(this);
	}

	public static QueueManager instance()
	{
		return API.getInstance().getQueueManager();
	}

	@EventHandler
	private void onPlayerJoin(PlayerJoinEvent e)
	{
		if (isFinished && !started)
			return;
		playerCount++;
		checkPlayers();
	}

	public void checkPlayers()
	{
		Bukkit.getOnlinePlayers().forEach(this::updateScoreboard);
		if (playerCount >= minPlayers && !started && !isFinished)
		{
			launchTime = System.currentTimeMillis();
			threadId = Bukkit.getScheduler().scheduleSyncRepeatingTask(API.getInstance(), () ->
			{
				if (started)
				{
					currentTime = (launchTime + countdown) - System.currentTimeMillis();

					if (currentTime < 0 || playerCount == maxPlayers)
					{
						isFinished = true;
						currentTime = 0L;
					}

					if (playerCount >= minPlayers)
					{
						if (isFinished)
						{
							started = false;
							listeners.forEach(QueueListener::onGameStart);
							scoreboards.values().forEach(ScoreboardSign::destroy);
							Bukkit.getScheduler().cancelTask(threadId);
							threadId = -1;
						}
					}
					else
					{
						started = false;
						currentTime = 0L;
						launchTime = 0L;
					}
					Bukkit.getOnlinePlayers().forEach(this::updateScoreboard);
				}
			}, 0, 20L);

			started = true;
		}
	}

	@EventHandler
	private void onPlayerQuit(PlayerQuitEvent e)
	{
		if (isFinished && !started)
			return;
		playerCount--;
		Bukkit.getOnlinePlayers().forEach(this::updateScoreboard);
		if (playerCount == 0)
			clear();
	}

	private void updateScoreboard(Player p)
	{
		ScoreboardSign s;
		if (scoreboards.containsKey(p))
			s = scoreboards.get(p);
		else
		{
			s = new ScoreboardSign(p, scoreboardName);
			s.create();
			scoreboards.put(p, s);
		}

		int seconds = (int) (currentTime / 1000) % 60;
		int minutes = (int) ((currentTime / (1000 * 60)) % 60);

		s.setLine(0, playerCount + "/" + maxPlayers);
		String time = String.format("%02d:%02d", minutes, seconds);
		s.setLine(1, time);
	}

	public void clear()
	{
		scoreboards.values().forEach(ScoreboardSign::destroy);
		isFinished = false;
		started = false;
		currentTime = -1L;
		if (threadId != -1)
			Bukkit.getServer().getScheduler().cancelTask(threadId);
	}

	public String getScoreboardName()
	{
		return scoreboardName;
	}

	public void setScoreboardName(String scoreboardName)
	{
		this.scoreboardName = scoreboardName;
	}

	public void registerListner(QueueListener l)
	{
		listeners.add(l);
	}
}
