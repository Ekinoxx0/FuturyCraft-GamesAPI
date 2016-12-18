package api.packet;

import api.API;
import org.bukkit.Bukkit;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

/**
 * Created by SkyBeast on 18/12/2016.
 */
public class MessengerConnection
{
	private final Socket socket;
	private final DataInputStream in;
	private final DataOutputStream out;
	private final List<PacketListener<?>> listeners = new ArrayList<>();
	private short lastTransactionID = 0x0000;
	private final String host;
	private final int port;
	private Thread connectionListener;
	private boolean identified = false;
	private volatile boolean end = false;

	public MessengerConnection(String host, int port)
	{
		this.host = host;
		this.port = port;

		try
		{
			socket = new Socket(host, port);
			in = new DataInputStream(socket.getInputStream());
			out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
			setupConnectionListener();
		}
		catch (IOException e)
		{
			API.getInstance().getLogger().log(Level.SEVERE, "Error while creating the ServerSocket (Connection: " + this
					+ ")", e);

			Bukkit.shutdown(); //Stop because this server is not usable now

			throw new IllegalStateException(e);
		}
	}

	private void setupConnectionListener()
	{
		connectionListener = new Thread(() ->
		{
			try
			{
				out.writeInt(Bukkit.getPort());
				out.flush();

				if (!in.readBoolean())
				{
					throw new IllegalStateException("Server did not accept me ;( Tried to identify with port " + Bukkit
							.getPort());
				}
				identified = true;
			}
			catch (Exception e)
			{
				API.getInstance().getLogger().log(Level.SEVERE, "Error while identifying (Connection: " + this + ")", e);
			}

			//Identified

			while (!socket.isClosed())
			{
				try
				{
					byte id = in.readByte();
					short transactionID = in.readShort();
					short i = in.readShort();
					byte[] data = new byte[i];

					in.readFully(data); //Read all data and store it to the array

					handleData(id, transactionID, data);
				}
				catch (IOException e)
				{
					if (!end)
						API.getInstance().getLogger().log(Level.SEVERE, "Error while reading the socket (Connection: " + this + ")", e);
				}
				catch (Exception e)
				{
					API.getInstance().getLogger().log(Level.SEVERE, "Error while reading the socket (Connection: " + this + ")", e);
				}
			}
		}
		);

		connectionListener.start();
	}

	@SuppressWarnings("unchecked")
	private void handleData(byte id, short transactionID, byte[] arrayIn) throws IOException
	{
		DataInputStream data = new DataInputStream(new ByteArrayInputStream(arrayIn)); //Create an InputStream
		// from the byte array, so it can be redistributed

		try
		{
			IncPacket packet = Packets.constructIncomingPacket(id, data);

			if (packet == null)
				throw new IllegalArgumentException("Cannot find packet ID " + id + " (transactionID=" + transactionID
						+ ", in=" + Arrays.toString(arrayIn) + ")");

			synchronized (listeners)
			{
				listeners.forEach(listener ->
				{
					if (listener.transactionID == transactionID && listener.clazz == packet.getClass())
					{
						((Callback<IncPacket>) listener.callback).response(packet);
						listeners.remove(listener);
					}
				});
			}

			Bukkit.getPluginManager().callEvent(new PacketReceivedEvent(packet, transactionID));
		}
		catch (ReflectiveOperationException e)
		{
			API.getInstance().getLogger().log(Level.SEVERE, "Error while constructing packet id " + id + " with " +
					"data " + Arrays.toString(arrayIn) + "  (Connection: " + this + ")", e);
		}
	}

	public <T extends IncPacket> void listenPacket(Class<T> clazz, int transactionID, Callback<T> callback)
	{
		synchronized (listeners)
		{
			listeners.add(new PacketListener<>(clazz, callback, transactionID));
		}
	}

	public short sendPacket(OutPacket packet) throws IOException
	{
		short transactionID = lastTransactionID++;
		sendPacket(packet, transactionID);
		return transactionID;
	}

	public void sendPacket(OutPacket packet, short transactionID) throws IOException
	{
		ByteArrayOutputStream array = new ByteArrayOutputStream();
		DataOutputStream data = new DataOutputStream(new ByteArrayOutputStream());
		packet.write(data);

		synchronized (out)
		{
			out.write(Packets.getID(packet.getClass()));
			out.write(transactionID);
			out.write(array.size());
			out.write(array.toByteArray());
			out.flush();
		}
	}

	public void disconnect()
	{
		end = true;
		try
		{
			socket.close();
		}
		catch (IOException ignored) {}
	}

	private static class PacketListener<T>
	{
		Class<T> clazz;
		Callback<T> callback;
		int transactionID;

		PacketListener(Class<T> clazz, Callback<T> callback, int transactionID)
		{
			this.clazz = clazz;
			this.callback = callback;
			this.transactionID = transactionID;
		}
	}
}
