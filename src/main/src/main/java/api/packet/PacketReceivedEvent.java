package api.packet;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created by SkyBeast on 18/12/2016.
 */
public class PacketReceivedEvent extends Event
{
	protected final IncPacket packet;
	private final short transactionID;

	public PacketReceivedEvent(IncPacket packet, short transactionID)
	{
		this.packet = packet;
		this.transactionID = transactionID;
	}

	public IncPacket getPacket()
	{
		return packet;
	}

	public short getTransactionID()
	{
		return transactionID;
	}

	@Override
	public String toString()
	{
		return "PacketReceivedEvent{" +
				"packet=" + packet +
				", transactionID=" + transactionID +
				'}';
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		PacketReceivedEvent that = (PacketReceivedEvent) o;

		if (transactionID != that.transactionID)
			return false;
		return packet != null ? packet.equals(that.packet) : that.packet == null;
	}

	@Override
	public int hashCode()
	{
		int result = packet != null ? packet.hashCode() : 0;
		result = 31 * result + (int) transactionID;
		return result;
	}

	private static final HandlerList handlers = new HandlerList();

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
