package api.packet.server;

import api.packet.OutPacket;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * Created by loucass003 on 2/2/17.
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class EndGameData extends OutPacket
{
	private final List<RelativeData> players;

	@Override
	public void write(DataOutputStream data) throws IOException
	{
		data.writeShort(players.size());
		for (RelativeData d : players)
			d.write(data);
	}

	@Data
	private static class RelativeData
	{
		private final UUID uuid;
		private final long earnedFuturyCoins;
		private final long earnedTurfuryCoins;

		public void write(DataOutputStream data) throws IOException
		{
			data.writeLong(uuid.getMostSignificantBits());
			data.writeLong(uuid.getLeastSignificantBits());
			data.writeLong(earnedFuturyCoins);
			data.writeLong(earnedTurfuryCoins);
		}
	}
}
