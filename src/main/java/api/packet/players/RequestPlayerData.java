package api.packet.players;

import api.packet.Callback;
import api.packet.MessengerConnection;
import api.packet.OutPacket;
import api.utils.Wrapper;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by loucass003 on 07/12/16.
 */
public class RequestPlayerData extends OutPacket
{
	private final UUID uuid;

	public RequestPlayerData(UUID uuid)
	{
		this.uuid = uuid;
	}

	@Override
	public void write(DataOutputStream data) throws IOException
	{
		data.writeLong(uuid.getLeastSignificantBits());
		data.writeLong(uuid.getMostSignificantBits());
	}

	// UTILS

	private static final Lock lock = new ReentrantLock();
	private static final MessengerConnection CONNECTION = null;

	public static void request(Callback<SendPlayerData> callback, UUID uuid) throws IOException
	{
		short transaction = CONNECTION.sendPacket(new RequestPlayerData(uuid));
		CONNECTION.listenPacket(SendPlayerData.class, transaction, callback);
	}

	public static SendPlayerData request(UUID uuid) throws IOException, InterruptedException
	{
		return request(uuid, 0, null);
	}

	public static SendPlayerData request(UUID uuid, long timeout, TimeUnit unit) throws IOException,
			InterruptedException
	{
		short transaction = CONNECTION.sendPacket(new RequestPlayerData(uuid));
		Condition condition = lock.newCondition();
		Wrapper<SendPlayerData> out = new Wrapper<>();

		CONNECTION.listenPacket(SendPlayerData.class, transaction, sent ->
		{
			out.set(sent);
			condition.signalAll();
		});

		if (timeout == 0) //Wait until the packet arrived
			condition.await();
		else
			condition.await(timeout, unit);

		return out.get();
	}
}
