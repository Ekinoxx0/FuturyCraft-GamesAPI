package api.packet.server;

import api.ServerState;
import api.packet.OutPacket;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.DataOutput;
import java.io.IOException;

/**
 * Created by loucass003 on 2/2/17.
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ServerStatePacket extends OutPacket
{
	private final ServerState state;

	@Override
	public void write(DataOutput out) throws IOException
	{
		out.writeByte(state.ordinal());
	}
}
