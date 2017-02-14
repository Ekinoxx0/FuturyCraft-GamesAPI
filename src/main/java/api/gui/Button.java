package api.gui;

import api.enchant.EmptyEnchant;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by loucass003 on 23/11/16.
 */
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
		this.material = m;
		this.name = name;
		this.desc = desc;
		this.enchant = enchant;
		this.x = x;
		this.y = y;

		this.meta = 0;
		this.amount = 1;
	}

	public Button(Material m, String name, boolean enchant, int x, int y)
	{
		this(m, name, null, enchant, x, y);
	}

	public Button(Material m, String name, int x, int y)
	{
		this(m, name, null, false, x, y);
	}

	public int getX()
	{
		return x;
	}

	public int getY()
	{
		return y;
	}

	public Material getMaterial()
	{
		return material;
	}

	public void setX(int x)
	{
		this.x = x;
	}

	public void setY(int y)
	{
		this.y = y;
	}

	public String getName()
	{
		return name;
	}

	public String getDesc()
	{
		return desc;
	}

	public int getAmount()
	{
		return amount;
	}

	public int getMeta()
	{
		return meta;
	}

	public boolean isEnchant()
	{
		return enchant;
	}

	public void setMaterial(Material material)
	{
		this.material = material;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public void setDesc(String desc)
	{
		this.desc = desc;
	}

	public void setEnchant(boolean enchant)
	{
		this.enchant = enchant;
	}

	public void setLines(List<String> lines)
	{
		this.lines = lines;
	}

	public void setAmount(int amount)
	{
		this.amount = amount;
	}

	public void setMeta(int meta)
	{
		this.meta = meta;
	}

	public List<String> getLines()
	{
		return this.lines;
	}

	public int getCase()
	{
		return this.getX() + 9 * this.getY();
	}

	public void draw(Inventory i)
	{
		int c = getCase();

		if (c > i.getContents().length - 1)
			return;
		ItemStack is = new ItemStack(this.getMaterial(), this.amount, (short) this.meta);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(this.getName());
		List<String> lines = getLines();
		if (this.getDesc() != null)
			lines = getLines(this.getDesc(), 20);
		im.setLore(lines);
		if (this.isEnchant())
			im.addEnchant(new EmptyEnchant(c + this.hashCode()), 1, true);
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
