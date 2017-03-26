package api.packet;

import api.API;
import api.utils.SimpleManager;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.rabbitmq.client.*;
import lombok.ToString;
import lombok.extern.java.Log;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;

/**
 * Created by SkyBeast on 18/12/2016.
 */
@ToString
@Log
public class MessengerConnection implements SimpleManager
{
	private static final String BUNGEECORD_QUEUE = "BungeeCord";
	private static final String EXCHANGER = "servers";
	private final String dockerID = API.getInstance().getContainerID();
	private Connection connection;
	private Channel channel;
	private MessageHandler consumer;

	@Override
	public void init() throws IOException, TimeoutException
	{
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		connection = factory.newConnection();
		channel = connection.createChannel();

		channel.queueDeclare(dockerID, false, false, false, null);
		channel.queueBind(dockerID, EXCHANGER, dockerID);

		consumer = new MessageHandler();
		channel.basicConsume(dockerID, true, consumer);
	}

	public static MessengerConnection instance()
	{
		return API.getInstance().getMessengerConnection();
	}

	@Override
	public void stop()
	{
		try
		{
			channel.close();
			connection.close();
		}
		catch (IOException | TimeoutException ignored) {}
	}

	/**
	 * Send a packet.
	 *
	 * @param packet the packet to send
	 */
	public void sendPacket(OutPacket packet)
	{
		try
		{
			send(serializePacket(dockerID, packet));
		}
		catch (IOException e)
		{
			log.log(Level.SEVERE, "Cannot send packet to BungeeCord with packet " + packet + '.', e);
		}
	}

	/**
	 * Serialize a packet with its header.
	 *
	 * @param packet the packet
	 * @return the serialized packet
	 * @throws IOException i/o related method
	 */
	private static byte[] serializePacket(String serverID, OutPacket packet)
			throws IOException
	{
		// Header
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF(serverID);
		out.writeByte(Packets.getId(packet.getClass()));

		// Packet
		byte[] raw = serializeRawPacket(packet);
		out.writeInt(raw.length);
		out.write(raw);

		return out.toByteArray();
	}

	/**
	 * Serialize a raw packet without header.
	 *
	 * @param packet the packet to serialize
	 * @return the serialized packet
	 * @throws IOException i/o related method
	 */
	private static byte[] serializeRawPacket(OutPacket packet)
			throws IOException
	{
		ByteArrayDataOutput packetOut = ByteStreams.newDataOutput();
		packet.write(packetOut);
		return packetOut.toByteArray();
	}

	/**
	 * Send a packet to the BungeeCord.
	 *
	 * @param message the serialized packet
	 * @throws IOException i/o related method
	 */
	private void send(byte[] message)
			throws IOException
	{
		channel.basicPublish(BUNGEECORD_QUEUE, "", null, message);
	}

	/**
	 * Receive packets from BungeeCord.
	 */
	private class MessageHandler extends DefaultConsumer
	{
		MessageHandler()
		{
			super(channel);
		}

		@Override
		public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
		                           byte[] body)
				throws IOException
		{
			ByteArrayDataInput in = ByteStreams.newDataInput(body);

			// Packet
			InPacket packet = deserializePacket(in);

			API.callEvent(new PacketReceivedEvent(packet));
		}
	}

	/**
	 * Deserialize a packet with its header.
	 *
	 * @param in the data of the packet
	 * @return the packet
	 * @throws IOException i/o related method
	 */
	private static InPacket deserializePacket(ByteArrayDataInput in)
			throws IOException
	{
		byte id = in.readByte();
		int len = in.readInt();
		byte[] array = new byte[len];
		in.readFully(array);

		return deserializeRawPacket(id, array);
	}

	/**
	 * Deserialize a raw packet without its header.
	 *
	 * @param id the id of the packet
	 * @param in the data of the packet
	 * @return the packet
	 * @throws IOException i/o related method
	 */
	private static InPacket deserializeRawPacket(byte id, byte[] in)
			throws IOException
	{
		try
		{
			return Packets.constructIncomingPacket(id, ByteStreams.newDataInput(in));
		}
		catch (ReflectiveOperationException e)
		{
			throw new IOException("Cannot read incoming packet with id " + id + " and data " + Arrays.toString(in) +
					'.', e);
		}
	}
}
