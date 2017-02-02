package api;

import api.packet.server.SendKeepAlive;
import api.utils.concurrent.ThreadLoop;
import api.utils.concurrent.ThreadLoops;
import com.sun.management.OperatingSystemMXBean;
import lombok.Data;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 * Created by loucass003 on 1/30/17.
 */
@Data
public class KeepAliveService
{
	private final API main;
	private ThreadLoop keepAliveLoop;

	public void init()
	{
		this.keepAliveLoop = ThreadLoops.newScheduledThreadLoop(() ->
		{
			OperatingSystemMXBean mxBean = (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

			Long freeMemory = Runtime.getRuntime().freeMemory();
			Long totalMemory = Runtime.getRuntime().totalMemory();
			float cpuLoad = (float)mxBean.getProcessCpuLoad();

			try
			{
				getMain().getMessenger().sendPacket(new SendKeepAlive(freeMemory, totalMemory, cpuLoad, new byte[]{-0x01,-0x01,-0x01}));
			}
			catch (IOException e)
			{
				getMain().getLogger().log(Level.SEVERE, "unable to send Keep alive packet", e);
			}
		},
		1000 * 30,
		TimeUnit.MILLISECONDS);
		this.keepAliveLoop.start();
	}
}
