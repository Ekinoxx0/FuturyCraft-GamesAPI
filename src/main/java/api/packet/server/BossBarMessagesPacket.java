package api.packet.server;

import api.packet.InPacket;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.DataInput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by loucass003 on 2/4/17.
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class BossBarMessagesPacket extends InPacket
{
	private final List<MessageData> messages;

	public BossBarMessagesPacket(DataInput data) throws IOException
	{
		super(data);
		short count = data.readShort();
		messages = new ArrayList<>();
		for (short i = 0; i < count; i++)
			messages.add(new MessageData(data));
	}

	@Data
	@AllArgsConstructor
	public static class MessageData
	{
		private final String message;
		private final int time;

		public MessageData(DataInput data) throws IOException
		{
			message = data.readUTF();
			time = data.readInt();
		}
	}
}
