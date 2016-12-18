package api.events.listener;

import api.API;
import api.events.EventsRegisterer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by loucass003 on 27/11/16.
 */
public class SpectatorEvents implements Listener
{

    public EventsRegisterer registerer;
    public API main;

    public List<Player> players = new ArrayList<>();
    public Team ghost;

    public SpectatorEvents(EventsRegisterer er)
    {
        this.registerer = er;
        this.main = er.main;
        Scoreboard sc = this.main.getServer().getScoreboardManager().getMainScoreboard();
        this.ghost = sc.getTeam("Ghosts");
        if(this.ghost != null)
            this.ghost.unregister();
        this.ghost = sc.registerNewTeam("Ghosts");
        this.ghost.setCanSeeFriendlyInvisibles(true);
        this.ghost.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
        this.ghost.setPrefix(ChatColor.GRAY.toString());
    }

    public void setSpectator(Player p)
    {
        if(players.contains(p))
            return;
        p.setGameMode(GameMode.SURVIVAL);
        p.setAllowFlight(true);
        p.setFlying(true);
        p.setFlySpeed(0.3F);
        p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false, false));
        ghost.addEntry(p.getName());
        players.add(p);
    }

    public void remSpectator(Player p)
    {
        if(!players.contains(p))
            return;
        p.setAllowFlight(false);
        p.setFlying(false);
        p.setFlySpeed(0.1F);
        p.removePotionEffect(PotionEffectType.INVISIBILITY);
        ghost.removeEntry(p.getName());
        players.remove(p);
    }

    public void clearSpectators()
    {
        Bukkit.getOnlinePlayers().forEach(this::remSpectator);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e)
    {
        remSpectator(e.getPlayer());
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e)
    {
        if(API.isSpectator(e.getPlayer()))
            e.setCancelled(true);
    }

    @EventHandler (priority = EventPriority.HIGH)
    public void onPlayerToggleFlight(PlayerToggleFlightEvent e)
    {
        if(API.isSpectator(e.getPlayer()))
            e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerGameModeChange(PlayerGameModeChangeEvent e)
    {
        remSpectator(e.getPlayer());
    }

}
