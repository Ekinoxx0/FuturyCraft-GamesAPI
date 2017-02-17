package api.gui;

import api.interfaces.ActionListener;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by loucass003 on 23/11/16.
 */
@Data
public class Gui
{
	private int lines;
	private String name;
	private Inventory inventory;
	private List<Button> buttonList = new ArrayList<>();
	protected List<ActionListener> actionListeners = new ArrayList<>();

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

	public String getName()
	{
		return name;
	}

	public void openGui(Player p)
	{
		if (lines > 6)
			lines = 6;
		inventory = Bukkit.createInventory(null, lines * 9, getName());
		onLoad();
		for (Button b : buttonList)
			b.draw(inventory);
		p.openInventory(inventory);
	}

	public void close()
	{

	}

	public void addActionListner(ActionListener a)
	{
		actionListeners.add(a);
	}
}
