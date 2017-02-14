package api.commands;

import api.API;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.craftbukkit.v1_11_R1.CraftServer;

/**
 * Created by loucass003 on 26/11/16.
 */
public class CommandsManager
{
	public API main;

	public CommandsManager(API main)
	{
		this.main = main;
	}

	public void registerCommands()
	{
		this.addCommand("setspawn", new SetSpawn());
	}

	public void addCommand(String name, BukkitCommand c)
	{
		((CraftServer) main.getServer()).getCommandMap().register(name, c);
	}
}
