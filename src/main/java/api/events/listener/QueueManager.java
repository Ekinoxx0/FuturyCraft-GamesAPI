package api.events.listener;

import api.API;
import api.gui.ScoreboardSign;
import api.interfaces.QueueListener;
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
public class QueueManager implements Listener
{
	private final int maxPlayers;
	private final int minPlayers;
	private final Long countdown;
	private Long launchTime;
	private Long currentTime = -1L;
	private boolean isFinished = false;
	private boolean started = false;
	private int playerCount = 0;
	private int threadId = -1;
	private String scoreboardName = "Unknown";

	private final Map<Player, ScoreboardSign> scoreboards = new HashMap<>();
	private final List<QueueListener> listeners = new ArrayList<>();

	public QueueManager(int maxPlayers, int minPlayers, Long countdown)
	{
		this.maxPlayers = maxPlayers;
		this.minPlayers = minPlayers;
		this.countdown = countdown;
		API.getInstance().getServer().getPluginManager().registerEvents(this, API.getInstance());
		playerCount = Bukkit.getOnlinePlayers().size();
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
		if (!API.getInstance().isUseQueueManager())
			return;
		Bukkit.getOnlinePlayers().forEach(this::updateScoreboard);
		if (playerCount >= this.minPlayers && !started && !isFinished)
		{
			this.launchTime = System.currentTimeMillis();
			threadId = Bukkit.getScheduler().scheduleSyncRepeatingTask(API.getInstance(), () ->
			{
				if (started)
				{
					currentTime = (this.launchTime + this.countdown) - System.currentTimeMillis();

					if (currentTime < 0 || playerCount == this.maxPlayers)
					{
						this.isFinished = true;
						this.currentTime = 0L;
					}

					if (playerCount >= this.minPlayers)
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

			this.started = true;
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
			this.clear();
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
		String time = String.format("%02d:%02d", minutes, seconds);

		s.setLine(0, this.playerCount + "/" + this.maxPlayers);
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
		this.listeners.add(l);
	}
}
