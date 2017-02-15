package api.packet;

import api.packet.server.*;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * Created by loucass003 on 07/12/16.
 */
public enum Packets
{
	// OUTGOING - Bungee-bound
	KEEP_ALIVE((byte) 0x00, false, KeepAlivePacket.class),
	SERVER_STATE_PACKET((byte) 0x01, false, ServerStatePacket.class),
	END_GAME_PACKET((byte) 0x02, false, EndGameData.class),
	REQUEST_BB_MESSAGES((byte) 0x03, false, RequestBossBarMessages.class),

	// INCOMING - Spigot-bound
	//REQUEST_PLAYER_DATA((byte) 0x00, true, RequestPlayerData.class);

	BB_MESSAGES((byte) 0x02, true, BossBarMessagesPacket.class);

	private final byte id;
	private final boolean in;
	private final Class<? extends Packet> clazz;

	Packets(byte id, boolean in, Class<? extends Packet> clazz)
	{
		this.id = id;
		this.in = in;
		this.clazz = clazz;
	}

	static IncPacket constructIncomingPacket(byte id, DataInputStream dis)
			throws IOException, ReflectiveOperationException
	{
		for (Packets p : values())
			if (p.in && id == p.id)
				return (IncPacket) p.clazz.getConstructor(DataInputStream.class).newInstance(dis);
		return null;
	}

	static byte getID(Class<? extends Packet> clazz)
	{
		for (Packets p : values())
			if (clazz == p.clazz)
				return p.id;
		throw new IllegalArgumentException("ID not found"); //Should never happen
	}

	public byte getId()
	{
		return id;
	}

	public boolean isServerBound()
	{
		return in;
	}

	public Class<? extends Packet> getPacketClass()
	{
		return clazz;
	}
}
