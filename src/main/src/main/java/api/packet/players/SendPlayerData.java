package api.packet.players;

import api.packet.IncPacket;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * Created by loucass003 on 07/12/16.
 */
public class SendPlayerData extends IncPacket
{
	private final String player;

	public SendPlayerData(DataInputStream data) throws IOException
	{
		super(data);

		player = data.readUTF();
	}
}
