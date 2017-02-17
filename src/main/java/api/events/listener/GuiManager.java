package api.events.listener;

import api.API;
import api.gui.Gui;
import api.gui.Source;
import api.interfaces.ActionListener;
import api.utils.SimpleManager;
import lombok.ToString;
import lombok.extern.java.Log;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by loucass003 on 23/11/16.
 */
@ToString
@Log
public class GuiManager implements Listener, SimpleManager
{
	private final List<Gui> menus = new ArrayList<>();

	@Override
	public void init()
	{
		API.registerListener(this);
	}

	public static GuiManager instance()
	{
		return API.getInstance().getGuiManager();
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent e)
	{
		Gui g = getGui(e.getInventory().getName());
		if (g == null)
			return;
		e.setCancelled(true);
		g.getButtonList().stream().filter(b -> b.getCase() == e.getSlot() && e.getClickedInventory().getName().equals(g
				.getName())).forEach(b ->
		{
			for (ActionListener a : g.getActionListeners())
			{
				Source s = new Source(b, e.getInventory(), (Player) e.getWhoClicked());
				a.actionPerformed(s);
			}
		});
	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent e)
	{
		Gui g = getGui(e.getInventory().getName());
		if (g == null)
			return;
		g.onExit();
		menus.remove(g);
	}

	@EventHandler
	public void onInventoryMoveItem(InventoryMoveItemEvent e)
	{
		Gui g = getGui(e.getSource().getName());
		if (g == null)
			return;
		e.setCancelled(true);
	}

	@EventHandler
	public void onInventoryDrag(InventoryDragEvent e)
	{
		Gui g = getGui(e.getInventory().getName());
		if (g == null)
			return;
		e.setCancelled(true);
	}

	@EventHandler
	public void onInventoryPickupItem(InventoryPickupItemEvent e)
	{
		Gui g = getGui(e.getInventory().getName());
		if (g == null)
			return;
		e.setCancelled(true);
	}

	public void openGui(Gui gui, Player p)
	{
		menus.add(gui);
		gui.openGui(p);
	}

	public Gui getGui(String name)
	{
		for (Gui g : menus)
			if (g.getName().equals(name))
				return g;
		return null;
	}

}
