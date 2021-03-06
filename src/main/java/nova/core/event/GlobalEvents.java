package nova.core.event;

import nova.core.block.Block;
import nova.core.entity.Entity;
import nova.core.world.World;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

/**
 * Global event manager that handles general events that are not object specific.
 * @author Calclavia
 */
public class GlobalEvents {

	public EventBus<Event> events = new EventBus<>();

	public static class ServerStartingEvent extends Event {

	}

	public static class ServerStoppingEvent extends Event {

	}

	public static class BlockEvent extends CancelableEvent {
		//The world
		public final World world;
		//The position of the block
		public final Vector3D position;

		public BlockEvent(World world, Vector3D position) {
			this.world = world;
			this.position = position;
		}
	}

	/**
	 * Called when a block in the world changes.
	 */
	public static class BlockChangeEvent extends BlockEvent {

		//The block that was in this position previously
		public final Block oldBlock;
		//The block that was set to in this position
		public final Block newBlock;

		public BlockChangeEvent(World world, Vector3D position, Block oldBlock, Block newBlock) {
			super(world, position);
			this.newBlock = newBlock;
			this.oldBlock = oldBlock;
		}
	}

	@CancelableEvent.Cancelable
	public static class PlayerInteractEvent extends CancelableEvent {
		public final World world;
		public final Vector3D position;
		public final Entity player;
		public final Action action;
		public Result useBlock = Result.DEFAULT;
		public Result useItem = Result.DEFAULT;

		public PlayerInteractEvent(World world, Vector3D position, Entity player, Action action) {
			this.world = world;
			this.position = position;
			this.player = player;
			this.action = action;
		}

		public enum Action {
			RIGHT_CLICK_AIR,
			RIGHT_CLICK_BLOCK,
			LEFT_CLICK_BLOCK
		}
	}
}
