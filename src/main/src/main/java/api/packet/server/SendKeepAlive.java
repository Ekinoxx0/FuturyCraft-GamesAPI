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
public class SendKeepAlive extends OutPacket
{
	private final long freeMemory;
	private final long totalMemory;
	private final float processCpuLoad;
	private final byte[] lastTPS;

	@Override
	public void write(DataOutputStream data) throws IOException
	{
		data.writeLong(freeMemory);
		data.writeLong(totalMemory);
		data.writeFloat(processCpuLoad);
		data.write(lastTPS);
	}
}
