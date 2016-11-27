package api.events.listener;

import api.API;
import api.gui.Title;
import api.interfaces.QueueListener;
import api.gui.ScoreboardSign;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Scoreboard;

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
    public Long launchTime;
    public Long currentTime = -1L;
    public boolean isFinished = false;
    public boolean started = false;
    private int playerCount = 0;
    private int threadId = -1;

    private final Map<Player, ScoreboardSign> scorebords = new HashMap<>();
    private final List<QueueListener> listners = new ArrayList<>();

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
        for(Player p : Bukkit.getOnlinePlayers())
            updateScorebord(p);
        if(playerCount >= this.minPlayers && !started)
        {
            threadId = Bukkit.getScheduler().scheduleSyncRepeatingTask(API.getInstance().getPlugin(), () -> {
                if(started) {
                    currentTime = (this.launchTime + this.countdown) - System.currentTimeMillis();
                    int seconds = (int) (currentTime / 1000) % 60;
                    if(seconds <= 3)
                    {
                        Title title = new Title();
                        title.setFadeIn(2);
                        title.setFadeOut(2);
                        title.setText("Debut dans " + seconds + "...");
                        title.setColor(ChatColor.GOLD);
                        if(seconds <= 3 && seconds >= 2)
                        {
                            title.setFadeIn(0);
                        }
                        if(seconds == 0)
                            title.setText("Go !");

                        for (Player pl : Bukkit.getOnlinePlayers())
                            title.sendTitle(pl);
                    }

                    if (currentTime < 0 || playerCount == this.maxPlayers) {
                        this.isFinished = true;
                        this.currentTime = 0L;
                    }

                    if(playerCount >= this.minPlayers) {

                        for (Player pl : Bukkit.getOnlinePlayers())
                            updateScorebord(pl);
                        if(isFinished)
                        {
                            started = false;
                            for(QueueListener q : listners)
                                q.onGameStart();
                            for (ScoreboardSign s : scorebords.values())
                                s.destroy();
                            Bukkit.getScheduler().cancelTask(threadId);
                        }
                    }
                    else {
                        started = false;
                        currentTime = 0L;
                        launchTime = 0L;
                    }
                }
            }, 0, 20L);
            this.launchTime = System.currentTimeMillis();
            this.started = true;
        }
    }

    @EventHandler
    private void onPlayerQuit(PlayerQuitEvent e)
    {
        playerCount--;
        for(Player p : Bukkit.getOnlinePlayers())
            updateScorebord(p);
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

        int seconds = (int) (currentTime / 1000) % 60 ;
        int minutes = (int) ((currentTime / (1000*60)) % 60);
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
}
