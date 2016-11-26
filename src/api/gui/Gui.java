package api.gui;

import api.interfaces.ActionListner;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by loucass003 on 23/11/16.
 */
public class Gui {

    public int lines;
    public String name;
    public Inventory inventory;
    public List<Button> buttonList = new ArrayList<>();
    protected List<ActionListner> actionListners = new ArrayList<>();

    public Gui(String name, int lines)
    {
        this.name = name;
        this.lines = lines;
    }

    public Gui(String name)
    {
        this(name, 6);
    }

    public void onLoad()
    {
        buttonList.clear();
    }

    public void onExit() {}

    public String getName() {
        return name;
    }

    public void openGui(Player p)
    {
        if(lines > 6)
            lines = 6;
        inventory = Bukkit.createInventory(null, this.lines * 9, this.getName());
        this.onLoad();
        for(Button b : buttonList)
            b.draw(inventory);
        p.openInventory(inventory);
    }

    public void close()
    {

    }

    public Inventory getInventory() {
        return inventory;
    }

    public List<Button> getButtonList() {
        return buttonList;
    }

    public List<ActionListner> getActionListners() {
        return actionListners;
    }

    public int getLines() {
        return lines;
    }

    public void addActionListner(ActionListner a)
    {
        this.actionListners.add(a);
    }
}
