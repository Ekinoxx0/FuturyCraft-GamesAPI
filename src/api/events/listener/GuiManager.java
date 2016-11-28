package api.events.listener;

import api.API;
import api.gui.Gui;
import api.gui.Source;
import api.interfaces.ActionListener;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by loucass003 on 23/11/16.
 */
public class GuiManager implements Listener
{

    private List<Gui> menus = new ArrayList<>();
    private API main;

    public GuiManager(API main)
    {
        this.main = main;
        API.instance.getPlugin().getServer().getPluginManager().registerEvents(this, API.instance.getPlugin());
    }

    public void addGui(Gui g)
    {
        this.menus.add(g);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e)
    {
        Gui g = getGui(e.getInventory().getName());
        if(g == null)
            return;
        e.setCancelled(true);
        g.buttonList.stream().filter(b -> b.getCase() == e.getSlot()).forEach(b -> {
            for (ActionListener a : g.getActionListners()) {
                Source s = new Source(b, e.getInventory(), (Player)e.getWhoClicked());
                a.actionPerformed(s);
            }
        });
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e)
    {
        Gui g = getGui(e.getInventory().getName());
        if(g == null)
            return;
        g.onExit();
    }

    @EventHandler
    public void onInventoryMoveItem(InventoryMoveItemEvent e)
    {
        Gui g = getGui(e.getSource().getName());
        if(g == null)
            return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent e)
    {
        Gui g = getGui(e.getInventory().getName());
        if(g == null)
            return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onInventoryPickupItem(InventoryPickupItemEvent e)
    {
        Gui g = getGui(e.getInventory().getName());
        if(g == null)
            return;
        e.setCancelled(true);
    }

    public void openGui(String name, Player p)
    {
        menus.stream().filter(g -> g.getName().equals(name)).forEach(g -> g.openGui(p));
    }

    public Gui getGui(String name)
    {
        for(Gui g : menus)
            if(g.getName().equals(name))
                return g;
        return null;
    }

}
