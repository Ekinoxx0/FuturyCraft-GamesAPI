package api.gui;

import com.google.gson.Gson;
import lombok.ToString;
import net.minecraft.server.v1_11_R1.IChatBaseComponent;
import net.minecraft.server.v1_11_R1.PacketPlayOutTitle;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

/**
 * Created by loucass003 on 20/11/16.
 */
@ToString
public class Title
{
	@ToString
	private static class TitleData
	{
		String text;
		String color;
	}

	private int fadeIn;
	private int fadeOut;
	private int stay;

	private final TitleData data;
	private Title sub;

	private Title(String text, ChatColor color, int fadeIn, int stay, int fadeOut)
	{
		data = new TitleData();
		data.text = text;
		data.color = color.name().toLowerCase();
		this.fadeIn = fadeIn;
		this.stay = stay;
		this.fadeOut = fadeOut;
	}

	public Title(String text, ChatColor color)
	{
		this(text, color, 5, 20, 5);
	}

	public Title(String text)
	{
		this(text, ChatColor.WHITE, 5, 20, 5);
	}

	public Title()
	{
		this("", ChatColor.WHITE, 5, 20, 5);
	}

	public Title setFadeIn(int fadeIn)
	{
		this.fadeIn = fadeIn;
		return this;
	}

	public Title setFadeOut(int fadeOut)
	{
		this.fadeOut = fadeOut;
		return this;
	}

	public Title setStay(int stay)
	{
		this.stay = stay;
		return this;
	}

	public Title setText(String text)
	{
		data.text = text;
		return this;
	}

	public Title setColor(ChatColor color)
	{
		data.color = color.name().toLowerCase();
		return this;
	}

	public Title setSubtitle(Title sub)
	{
		this.sub = sub;
		return this;
	}

	public void sendTitle(Player p)
	{
		if (sub != null)
			sub.sendTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, p);
		sendTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, p);
	}

	private void sendTitle(PacketPlayOutTitle.EnumTitleAction type, Player p)
	{
		IChatBaseComponent chatTitle = IChatBaseComponent.ChatSerializer.a(new Gson().toJson(data));
		PacketPlayOutTitle title = new PacketPlayOutTitle(type, chatTitle);
		PacketPlayOutTitle length = new PacketPlayOutTitle(fadeIn, stay, fadeOut);
		((CraftPlayer) p).getHandle().playerConnection.sendPacket(title);
		if (type == PacketPlayOutTitle.EnumTitleAction.TITLE)
			((CraftPlayer) p).getHandle().playerConnection.sendPacket(length);
	}
}
