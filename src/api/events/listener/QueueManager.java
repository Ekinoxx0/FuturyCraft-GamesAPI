package api.events.listener;

import api.API;
import api.interfaces.QueueListener;
import api.gui.ScoreboardSign;
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
public class QueueManager implements Listener {

    private final int maxPlayers;
    private final int minPlayers;

    private final Long countdown;
    private Long launchTime = -1L;
    private Long counter = -1L;

    private int playerCount = 0;
    private int threadId = -1;

    private final Map<Player, ScoreboardSign> scorebords = new HashMap<>();
    private final List<QueueListener> listners = new ArrayList<>();

    private boolean isStarted = false;

    public QueueManager(int maxPlayers, int minPlayers, Long countdown)
    {
        this.maxPlayers = maxPlayers;
        this.minPlayers = minPlayers;
        this.countdown = countdown;
        API.instance.getPlugin().getServer().getPluginManager().registerEvents(this, API.instance.getPlugin());
        playerCount = Bukkit.getOnlinePlayers().size();
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent e)
    {
        playerCount++;
        if(playerCount >= this.minPlayers && this.launchTime == -1L)
        {
            this.launchTime = System.currentTimeMillis();
            this.threadId = Bukkit.getScheduler().scheduleSyncRepeatingTask(API.instance.getPlugin(), () -> {
                counter = (this.launchTime + this.countdown) - System.currentTimeMillis();
                Bukkit.getOnlinePlayers().forEach(this::updateScorebord);
                if(playerCount >= this.minPlayers && counter <= 0 || playerCount >= this.maxPlayers)
                {
                    listners.forEach(QueueListener::onGameStart);
                    scorebords.values().forEach(ScoreboardSign::destroy);
                    scorebords.clear();
                    isStarted = true;
                    Bukkit.getServer().getScheduler().cancelTask(threadId);
                    threadId = -1;
                }
            }, 0L, 20L);
        }
        Bukkit.getOnlinePlayers().forEach(this::updateScorebord);
    }

    @EventHandler
    private void onPlayerQuit(PlayerQuitEvent e)
    {
        playerCount--;
        if(playerCount <= 0 && threadId != -1)
            Bukkit.getServer().getScheduler().cancelTask(threadId);
        if(scorebords.containsKey(e.getPlayer()))
            scorebords.remove(e.getPlayer());
        Bukkit.getOnlinePlayers().forEach(this::updateScorebord);
    }

    private void updateScorebord(Player p)
    {
        ScoreboardSign s;
        if(scorebords.containsKey(p))
            s = scorebords.get(p);
        else
        {
            s = new ScoreboardSign(p, API.instance.getPlugin().getName());
            s.create();
            scorebords.put(p, s);
        }

        if(counter < 0L)
            counter = 0L;

        int seconds = (int) (counter / 1000) % 60 ;
        int minutes = (int) ((counter / (1000*60)) % 60);
        String time = String.format("%02d:%02d", minutes, seconds);

        s.setLine(0, this.playerCount + "/" + this.maxPlayers);
        s.setLine(1, time);
    }

    public void clear()
    {
        scorebords.values().forEach(ScoreboardSign::destroy);
        scorebords.clear();
        listners.clear();
        if(threadId != -1)
            Bukkit.getServer().getScheduler().cancelTask(threadId);
    }


    public void registerListner(QueueListener l)
    {
        this.listners.add(l);
    }

    public boolean isStarted() {
        return isStarted;
    }
}
