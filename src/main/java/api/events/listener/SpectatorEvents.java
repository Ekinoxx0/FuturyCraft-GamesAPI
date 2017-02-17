package api.events.listener;

import api.API;
import api.utils.SimpleManager;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.java.Log;
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
@Getter
@ToString
@Log
public class SpectatorEvents implements Listener, SimpleManager
{
	private final List<Player> players = new ArrayList<>();
	private Team ghost;

	@Override
	public void init()
	{
		API.registerListener(this);

		Scoreboard sc = Bukkit.getScoreboardManager().getMainScoreboard();
		ghost = sc.getTeam("Ghosts");
		if (ghost != null)
			ghost.unregister();
		ghost = sc.registerNewTeam("Ghosts");
		ghost.setCanSeeFriendlyInvisibles(true);
		ghost.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.FOR_OWN_TEAM);
		ghost.setPrefix(ChatColor.GRAY.toString());
	}

	public void setSpectator(Player p)
	{
		if (players.contains(p))
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
		if (!players.contains(p))
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
		if (players.contains(e.getPlayer()))
			e.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerToggleFlight(PlayerToggleFlightEvent e)
	{
		if (players.contains(e.getPlayer()))
			e.setCancelled(true);
	}

	@EventHandler
	public void onPlayerGameModeChange(PlayerGameModeChangeEvent e)
	{
		remSpectator(e.getPlayer());
	}

}
