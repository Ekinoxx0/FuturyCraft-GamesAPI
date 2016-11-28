package api.commands;

import api.API;
import api.config.ConfigLocation;
import api.events.SetSpawnEvent;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;

/**
 * Created by loucass003 on 26/11/16.
 */
public class SetSpawn extends BukkitCommand
{

    public SetSpawn()
    {
        super("setSpawn");
        this.description = "Set the spawn of the world";
        this.usageMessage = "/setspawn";
        this.setPermission("futurycraft.setspawn");
        this.setAliases(new ArrayList<>());
    }

    @Override
    public boolean execute(CommandSender cs, String s, String[] args)
    {

        if(!(cs instanceof Player))
        {
            cs.sendMessage(ChatColor.RED + "The command sender must be a player");
            return false;
        }

        Player sender = (Player)cs;

        if(args.length > 0)
        {
            sender.sendMessage(ChatColor.RED + this.getUsage());
            return false;
        }

        SetSpawnEvent se = new SetSpawnEvent(sender, sender.getLocation());
        API.getInstance().getPlugin().getServer().getPluginManager().callEvent(se);
        if(!se.isCancelled())
        {
            Vector loc = se.getLocation().toVector();
            Vector orr = se.getLocation().getDirection();
            String world = se.getLocation().getWorld().getName();
            API.getInstance().getGlobalConfig().setSpawn(new ConfigLocation(world, loc, orr));
            API.getInstance().getConfigManager().save();
            sender.sendMessage(ChatColor.AQUA + "Le point de spawn a bien été defini");
        }
        return false;
    }
}
