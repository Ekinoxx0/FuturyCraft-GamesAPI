package api.packet.server;

import api.packet.OutPacket;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by loucass003 on 07/12/16.
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class KeepAlivePacket extends OutPacket
{
	@SuppressWarnings("MismatchedReadAndWriteOfArray")
	private final short[] lastTPS;

	@Override
	public void write(DataOutputStream data) throws IOException
	{
		data.writeShort(lastTPS[0]);
		data.writeShort(lastTPS[1]);
		data.writeShort(lastTPS[2]);
	}
}
