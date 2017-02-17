package api;

import api.packet.server.ServerStatePacket;
import api.utils.SimpleManager;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.java.Log;

/**
 * Created by loucass003 on 1/30/17.
 */
@ToString
@Log
public class KeepAliveService implements SimpleManager
{
	@Getter
	private ServerState serverState;

	public static KeepAliveService instance()
	{
		return API.getInstance().getKeepAliveService();
	}

	public void setServerState(ServerState state)
	{
		API.getInstance().getMessengerConnection().sendPacket(new ServerStatePacket(state));
	}
}
