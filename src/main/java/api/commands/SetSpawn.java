package api.commands;

import api.API;
import api.events.SetSpawnEvent;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

import java.util.Collections;

/**
 * Created by loucass003 on 26/11/16.
 */
public class SetSpawn extends BukkitCommand
{

	public SetSpawn()
	{
		super("setSpawn");
		description = "Set the spawn of the world";
		usageMessage = "/setspawn";
		setPermission("futurycraft.setspawn");
		setAliases(Collections.emptyList());
	}

	@Override
	public boolean execute(CommandSender cs, String s, String[] args)
	{

		if (!(cs instanceof Player))
		{
			cs.sendMessage(ChatColor.RED + "The command sender must be a player");
			return false;
		}

		Player sender = (Player) cs;

		if (args.length > 0)
		{
			sender.sendMessage(ChatColor.RED + getUsage());
			return false;
		}

		SetSpawnEvent se = new SetSpawnEvent(sender, sender.getLocation());
		API.getInstance().getServer().getPluginManager().callEvent(se);
		if (!se.isCancelled())
		{
			API.getInstance().getGlobalConfig().setSpawn(se.getLocation());
			API.getInstance().getConfigManager().save();
			sender.sendMessage(ChatColor.AQUA + "Le point de spawn a bien été defini");
		}
		return false;
	}
}
