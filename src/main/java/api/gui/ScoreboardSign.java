package api.gui;

import net.minecraft.server.v1_11_R1.*;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;

/**
 * Created by loucass003 on 21/11/16.
 */
public class ScoreboardSign
{

	private boolean created = false;
	private final String[] lines = new String[16];
	private final Player player;
	private String objectiveName;

	public ScoreboardSign(Player player, String objectiveName)
	{
		this.player = player;
		this.objectiveName = objectiveName;
	}

	public void create()
	{
		if (created)
			return;
		PlayerConnection player = getPlayer();
		player.sendPacket(createObjectivePacket(0, objectiveName));
		player.sendPacket(setObjectiveSlot());
		int i = 0;
		while (i < lines.length)
			sendLine(i++);

		created = true;
	}

	public void destroy()
	{
		if (!created)
			return;
		getPlayer().sendPacket(createObjectivePacket(1, null));
		created = false;
	}

	private PlayerConnection getPlayer()
	{
		return ((CraftPlayer) player).getHandle().playerConnection;
	}

	private void sendLine(int line)
	{
		if (line > 15)
			return;
		if (line < 0)
			return;
		if (!created)
			return;

		int score = (line * -1) - 1;
		String val = lines[line];
		getPlayer().sendPacket(sendScore(val, score));
	}

	public void setObjectiveName(String name)
	{
		this.objectiveName = name;
		if (created)
			getPlayer().sendPacket(createObjectivePacket(2, name));
	}

	public void setLine(int line, String value)
	{
		String oldLine = getLine(line);
		if (oldLine != null && created)
			getPlayer().sendPacket(removeLine(oldLine));

		lines[line] = value;
		sendLine(line);
	}

	public void removeLine(int line)
	{
		String oldLine = getLine(line);
		if (oldLine != null && created)
			getPlayer().sendPacket(removeLine(oldLine));
		lines[line] = null;
	}

	private String getLine(int line)
	{
		if (line > 15)
			return null;
		if (line < 0)
			return null;
		return lines[line];
	}

	private PacketPlayOutScoreboardObjective createObjectivePacket(int mode, String displayName)
	{
		PacketPlayOutScoreboardObjective packet = new PacketPlayOutScoreboardObjective();
		try
		{
			Field name = packet.getClass().getDeclaredField("a");
			name.setAccessible(true);
			name.set(packet, player.getName());
			Field modeField = packet.getClass().getDeclaredField("d");
			modeField.setAccessible(true);
			modeField.set(packet, mode);

			if (mode == 0 || mode == 2)
			{
				Field displayNameField = packet.getClass().getDeclaredField("b");
				displayNameField.setAccessible(true);
				displayNameField.set(packet, displayName);

				Field display = packet.getClass().getDeclaredField("c");
				display.setAccessible(true);
				display.set(packet, IScoreboardCriteria.EnumScoreboardHealthDisplay.INTEGER);
			}
		}
		catch (NoSuchFieldException | IllegalAccessException e)
		{
			e.printStackTrace();
		}

		return packet;
	}

	private PacketPlayOutScoreboardDisplayObjective setObjectiveSlot()
	{
		PacketPlayOutScoreboardDisplayObjective packet = new PacketPlayOutScoreboardDisplayObjective();
		try
		{
			Field position = packet.getClass().getDeclaredField("a");
			position.setAccessible(true);
			position.set(packet, 1);
			Field name = packet.getClass().getDeclaredField("b");
			name.setAccessible(true);
			name.set(packet, player.getName());
		}
		catch (NoSuchFieldException | IllegalAccessException e)
		{
			e.printStackTrace();
		}

		return packet;
	}

	private PacketPlayOutScoreboardScore sendScore(String line, int score)
	{
		PacketPlayOutScoreboardScore packet = new PacketPlayOutScoreboardScore(line);
		try
		{
			Field name = packet.getClass().getDeclaredField("b");
			name.setAccessible(true);
			name.set(packet, player.getName());

			Field scoreField = packet.getClass().getDeclaredField("c");
			scoreField.setAccessible(true);
			scoreField.set(packet, score);

			Field action = packet.getClass().getDeclaredField("d");
			action.setAccessible(true);
			action.set(packet, PacketPlayOutScoreboardScore.EnumScoreboardAction.CHANGE);
		}
		catch (NoSuchFieldException | IllegalAccessException e)
		{
			e.printStackTrace();
		}

		return packet;
	}

	private PacketPlayOutScoreboardScore removeLine(String line)
	{
		return new PacketPlayOutScoreboardScore(line);
	}
}