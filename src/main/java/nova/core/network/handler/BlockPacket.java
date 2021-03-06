package nova.core.network.handler;

import nova.core.block.Block;
import nova.core.entity.Entity;
import nova.core.network.NetworkException;
import nova.core.network.Packet;
import nova.core.network.Syncable;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import java.util.Optional;

/**
 * Handles block packets
 *
 * Packet Structure:
 * 1. PosX
 * 2. PosY
 * 3. PosZ
 * 5. Data
 *
 * @author Calclavia
 */
public class BlockPacket implements PacketHandler<Block> {

	@Override
	public void read(Packet packet) {
		Entity entity = packet.player().entity();
		Vector3D position = new Vector3D(packet.readInt(), packet.readInt(), packet.readInt());

		Optional<Block> opBlock = entity.world().getBlock(position);

		if (opBlock.isPresent()) {
			Block block = opBlock.get();
			if (block instanceof Syncable) {
				((Syncable) block).read(packet);
				return;
			}
		}
		throw new NetworkException("Failed to receive packet to block at " + position);
	}

	@Override
	public void write(Block block, Packet packet) {
		if (block instanceof Syncable) {
			Vector3D position = block.position();
			packet.writeInt((int) position.getX());
			packet.writeInt((int) position.getY());
			packet.writeInt((int) position.getZ());
			((Syncable) block).write(packet);
			return;
		}

		throw new NetworkException("Failed to send packet for block: " + block);
	}

	@Override
	public boolean isHandlerFor(Object block) {
		return block instanceof Block;
	}
}

