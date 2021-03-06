package api.gui;

import lombok.Getter;
import lombok.ToString;
import net.minecraft.server.v1_11_R1.IChatBaseComponent;
import net.minecraft.server.v1_11_R1.PacketPlayOutChat;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

/**
 * Created by loucass003 on 20/11/16.
 */
@ToString
@Getter
class ActionBar
{
	private String text;

	ActionBar(String text)
	{
		this.text = text;
	}

	public ActionBar setText(String text)
	{
		this.text = text;
		return this;
	}

	public void send(Player p)
	{
		PacketPlayOutChat packet = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + text +
				"\"}"), (byte) 2);
		((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
	}

}
