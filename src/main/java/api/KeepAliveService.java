package api;

import api.packet.server.KeepAlivePacket;
import api.packet.server.ServerStatePacket;
import api.utils.SimpleManager;
import api.utils.concurrent.ThreadLoop;
import api.utils.concurrent.ThreadLoops;
import lombok.Getter;
import lombok.ToString;
import net.minecraft.server.v1_11_R1.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_11_R1.CraftServer;

import java.util.concurrent.TimeUnit;

/**
 * Created by loucass003 on 1/30/17.
 */
@ToString
public class KeepAliveService implements SimpleManager
{
	@Getter
	private ServerState serverState;
	private final ThreadLoop keepAliveLoop = setupKeepAliveLoop();

	@Override
	public void init()
	{
		keepAliveLoop.start();
	}

	@Override
	public void stop()
	{
		keepAliveLoop.stop();
	}

	private ThreadLoop setupKeepAliveLoop()
	{
		MinecraftServer mcServer = ((CraftServer) Bukkit.getServer()).getServer();
		return ThreadLoops.newScheduledThreadLoop
				(
						() ->
						{
							double[] recentTPS = mcServer.recentTps;
							short[] out = new short[3];
							for (int i = 0; i < 3; i++)
								out[i] = (short) (recentTPS[i] * 100);

							API.getInstance().getMessenger()
									.sendPacket(new KeepAlivePacket(out));
						},
						1000 * 30,
						TimeUnit.MILLISECONDS
				);
	}

	public void setServerState(ServerState state)
	{
		API.getInstance().getMessenger().sendPacket(new ServerStatePacket(state));
	}
}
