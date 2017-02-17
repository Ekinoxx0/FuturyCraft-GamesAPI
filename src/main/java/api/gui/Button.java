package api.gui;

import api.enchant.FakeEnchant;
import lombok.Data;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by loucass003 on 23/11/16.
 */
@Data
public class Button
{
	private int x;
	private int y;
	private Material material;
	private int meta;
	private int amount;
	private String name;
	private String desc;
	private boolean enchant;
	private List<String> lines = new ArrayList<>();

	public Button(Material m, String name, String desc, boolean enchant, int x, int y)
	{
		material = m;
		this.name = name;
		this.desc = desc;
		this.enchant = enchant;
		this.x = x;
		this.y = y;

		meta = 0;
		amount = 1;
	}

	public Button(Material m, String name, boolean enchant, int x, int y)
	{
		this(m, name, null, enchant, x, y);
	}

	public Button(Material m, String name, int x, int y)
	{
		this(m, name, null, false, x, y);
	}

	public int getCase()
	{
		return x + 9 * y;
	}

	public void draw(Inventory i)
	{
		int c = getCase();

		if (c > i.getContents().length - 1)
			return;
		ItemStack is = new ItemStack(material, amount, (short) meta);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(name);
		List<String> lines = getLines();
		if (desc != null)
			lines = getLines(desc, 20);
		im.setLore(lines);
		if (enchant)
			im.addEnchant(new FakeEnchant(c + hashCode()), 1, true);
		is.setItemMeta(im);
		ItemStack[] ims = i.getContents();
		ims[c] = is;
		i.setContents(ims);
	}

	private List<String> getLines(String in, int width)
	{
		List<String> out = new ArrayList<>();
		while (in.length() > width)
		{
			boolean found = false;
			for (int i = width - 1; i >= 0; --i)
			{
				if (Character.isWhitespace(in.charAt(i)))
				{
					while (i > 0 && Character.isWhitespace(in.charAt(i)))
						i--;
					String s = in.substring(0, Character.isWhitespace(in.charAt(i)) ? i : i + 1);
					if (s.trim().length() < width)
					{
						out.add(s.trim());
						in = in.substring(s.length());
						found = true;
					}
				}
				if (found)
					break;
			}
		}

		if (!in.isEmpty())
			out.add(in.trim());
		return out;
	}
}
