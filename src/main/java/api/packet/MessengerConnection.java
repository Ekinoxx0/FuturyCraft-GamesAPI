package api.packet;

import api.API;
import api.utils.SimpleManager;
import api.utils.concurrent.Callback;
import api.utils.concurrent.ThreadLoop;
import api.utils.concurrent.ThreadLoops;
import lombok.AllArgsConstructor;
import lombok.ToString;
import org.bukkit.Bukkit;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

/**
 * Created by SkyBeast on 18/12/2016.
 */
@ToString
public class MessengerConnection implements SimpleManager
{
	private Socket socket;
	private DataInputStream in;
	private DataOutputStream out;
	private final List<PacketListener<?>> listeners = new CopyOnWriteArrayList<>();
	private final BlockingQueue<ToSendPacket> sendBuffer = new ArrayBlockingQueue<>(20);
	private final ThreadLoop sender = setupSenderThreadLoop();
	private final ThreadLoop listener = setupListenerThreadLoop();
	private final AtomicInteger lastTransactionID = new AtomicInteger();
	private final String host;
	private final int port;
	private boolean identified;
	private volatile boolean end;

	public MessengerConnection(String host, int port)
	{
		this.host = host;
		this.port = port;
		if (!API.getInstance().getGlobalConfig().getDeployer().isNoMessenger())
		{
			try
			{
				socket = new Socket(host, port);
				in = new DataInputStream(socket.getInputStream());
				out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
				tryIdentify();
			}
			catch (IOException e)
			{
				API.getInstance().getLogger().log(Level.SEVERE, "Error while creating the ServerSocket (Connection: "
						+ this + ")", e);
				Bukkit.shutdown(); //Stop because this server is not usable now
				throw new IllegalStateException(e);
			}
		}
		else
			System.out.println("no Messenger = true");
	}

	private void tryIdentify()
	{
		new Thread(
				() ->
				{
					try
					{
						out.writeInt(Bukkit.getPort());
						out.flush();

						if (!in.readBoolean())
						{
							throw new IllegalStateException("Server did not accept me ;( Tried to identify with port "
									+ Bukkit
									.getPort());
						}
						identified = true;

						sender.start();
						listener.start();
					}
					catch (Exception e)
					{
						API.getInstance().getLogger().log(Level.SEVERE, "Error while identifying (Connection: " + this
										+ ")",
								e);
					}
				}
		).start();
	}

	private ThreadLoop setupListenerThreadLoop()
	{
		return ThreadLoops.newConditionThreadLoop(
				() -> !socket.isClosed(),
				() ->
				{
					try
					{
						byte id = in.readByte();
						int transactionID = in.readInt();
						short i = in.readShort();
						byte[] data = new byte[i];

						in.readFully(data); //Read all data and store it to the array

						handleData(id, transactionID, data);
					}
					catch (IOException e)
					{
						if (!end)
							API.getInstance().getLogger().log(Level.SEVERE, "Error while reading the socket " +
									"(Connection: "
									+ this + ")", e);
					}
				}
		);
	}


	private ThreadLoop setupSenderThreadLoop()
	{
		return ThreadLoops.newInfiniteThreadLoop(
				() ->
				{
					try
					{
						ToSendPacket toSend = sendBuffer.take();
						out.writeByte(toSend.packetID);
						out.writeInt(toSend.transactionID);
						out.writeShort(toSend.array.size());
						out.write(toSend.array.toByteArray());
						out.flush();
					}
					catch (IOException e)
					{
						API.getInstance().getLogger().log(Level.SEVERE, "Cannot send buffered packet!", e);
					}
				}
		);
	}

	@SuppressWarnings("unchecked")
	private void handleData(byte id, int transactionID, byte[] arrayIn) throws IOException
	{
		DataInputStream data = new DataInputStream(new ByteArrayInputStream(arrayIn)); //Create an InputStream
		// from the byte array, so it can be redistributed

		try
		{
			IncPacket packet = Packets.constructIncomingPacket(id, data);

			if (packet == null)
				throw new IllegalArgumentException("Cannot find packet ID " + id + " (transactionID=" + transactionID
						+ ", in=" + Arrays.toString(arrayIn) + ")");

			listeners.forEach(listener ->
			{
				if (listener.transactionID == transactionID && listener.clazz == packet.getClass())
				{
					((Callback<IncPacket>) listener.callback).response(packet);
					listeners.remove(listener);
				}
			});

			Bukkit.getPluginManager().callEvent(new PacketReceivedEvent(packet, transactionID));
		}
		catch (ReflectiveOperationException e)
		{
			API.getInstance().getLogger().log(Level.SEVERE, "Error while constructing packet id " + id + " with " +
					"data " + Arrays.toString(arrayIn) + "  (Connection: " + this + ")", e);
		}
	}

	public <T extends IncPacket> void listenPacket(Class<T> clazz, short transactionID, Callback<T> callback)
	{
		listeners.add(new PacketListener<>(clazz, callback, transactionID));
	}

	public int sendPacket(OutPacket packet)
	{
		int transactionID = lastTransactionID.getAndIncrement();
		sendPacket(packet, transactionID);
		return transactionID;
	}

	public void sendPacket(OutPacket packet, int transactionID)
	{
		ByteArrayOutputStream array = new ByteArrayOutputStream();
		DataOutputStream data = new DataOutputStream(array);
		try
		{
			packet.write(data);
		}
		catch (IOException e)
		{
			throw new IllegalArgumentException("Cannot serialize packet " + packet, e);
		}

		boolean bool = false;
		try
		{
			bool = sendBuffer.offer(new ToSendPacket(Packets.getID(packet.getClass()), transactionID, array),
					10000,
					TimeUnit.MILLISECONDS);
		}
		catch (InterruptedException e)
		{
			cannotBuffer(e);
		}

		if (!bool)
			cannotBuffer(null);
	}

	private void cannotBuffer(InterruptedException e)
	{
		API.getInstance().getLogger().log(Level.SEVERE, "Cannot buffer packet!");
		if (e != null)
			API.getInstance().getLogger().log(Level.SEVERE, "Exception:", e);
	}

	@Override
	public void stop()
	{
		end = true;
		try
		{
			socket.close();
		}
		catch (IOException ignored) {}
		sender.stop();
		listener.stop();
	}

	@AllArgsConstructor
	@ToString
	private static class PacketListener<T>
	{
		Class<T> clazz;
		Callback<T> callback;
		short transactionID;
	}

	@AllArgsConstructor
	@ToString
	private static class ToSendPacket
	{
		byte packetID;
		int transactionID;
		ByteArrayOutputStream array;
	}
}
