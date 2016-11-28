package api.events.listener;

import api.API;
import api.events.EventsRegisterer;
import api.utils.GhostManager;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;

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
    public GhostManager ghostManager;


    public SpectatorEvents(EventsRegisterer er)
    {
        this.registerer = er;
        this.main = er.main;
        ghostManager = new GhostManager(main.getPlugin());
    }

    public void setSpectator(Player p)
    {
        if(players.contains(p))
            return;
        p.setGameMode(GameMode.SURVIVAL);
        p.setAllowFlight(true);
        p.setFlying(true);
        p.setFlySpeed(0.3F);
        ghostManager.setGhost(p, true);
        players.add(p);
    }

    public void remSpectator(Player p)
    {
        if(!players.contains(p))
            return;
        p.setAllowFlight(false);
        p.setFlying(false);
        ghostManager.setGhost(p, false);
        players.remove(p);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e)
    {
        if(API.isSpectator(e.getPlayer()))
            e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerToggleFlight(PlayerToggleFlightEvent e)
    {
        if(API.isSpectator(e.getPlayer()))
            e.setCancelled(true);
    }

    public void onPlayerGameModeChange(PlayerGameModeChangeEvent e)
    {
        remSpectator(e.getPlayer());
    }

}
