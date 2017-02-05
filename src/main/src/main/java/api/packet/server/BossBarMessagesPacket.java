package api.packet.server;

import api.packet.IncPacket;
import lombok.Data;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by loucass003 on 2/4/17.
 */
@Data
public class BossBarMessagesPacket extends IncPacket
{
	private final List<MessageData> messages;

	public BossBarMessagesPacket(DataInputStream data) throws IOException
	{
		super(data);
		short count = data.readShort();
		messages = new ArrayList<>();
		for(short i = 0; i < count; i++)
			messages.add(new MessageData(data));
	}

	@Data
	public static class MessageData
	{
		private final String message;
		private final int time;

		public MessageData(String message, int time)
		{
			this.message = message;
			this.time = time;
		}

		public MessageData(DataInputStream data) throws IOException
		{
			message = data.readUTF();
			time = data.readInt();
		}
	}
}
