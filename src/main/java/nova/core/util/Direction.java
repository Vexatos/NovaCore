package nova.core.util;

import nova.core.util.math.Vector3DUtil;
import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import java.util.stream.IntStream;

/**
 * Defines basic directions in world.
 */
public enum Direction {
	DOWN(0, -1, 0),
	UP(0, 1, 0),
	NORTH(0, 0, -1),
	SOUTH(0, 0, 1),
	WEST(-1, 0, 0),
	EAST(1, 0, 0),
	UNKNOWN(0, 0, 0);

	public static final Direction[] DIRECTIONS = new Direction[] {
		DOWN, UP, NORTH, SOUTH, WEST, EAST
	};

	private static final Direction[] values = Direction.values();
	public final int x, y, z;
	public final Rotation rotation;
	private final Vector3D vector;

	Direction(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.vector = new Vector3D(x, y, z);

		if (vector.equals(Vector3D.ZERO)) {
			this.rotation = Rotation.IDENTITY;
		} else {
			this.rotation = new Rotation(Vector3DUtil.FORWARD, vector);
		}
	}

	/**
	 * Turns direction number into Direction.
	 * @param directionID Direction ID / number.
	 * @return Resulting Direction.
	 * @throws IllegalArgumentException if the direction ID is invalid (greater than {@code 6} or less than {@code 0})
	 */
	public static Direction fromOrdinal(int directionID) {
		if (directionID < 0 || directionID >= Direction.values.length) {
			throw new IllegalArgumentException("Direction ID is invalid! The direction ID " + directionID + " is must be between " + (Direction.values.length - 1) + " and 0 inclusive");
		}
		return Direction.values[directionID];
	}

	/**
	 * @param unitVector The unit vector representing the direction.
	 * @return The direction based on a unit vector
	 */
	public static Direction fromVector(Vector3D unitVector) {
		return fromOrdinal(
			IntStream.range(0, 6)
				.boxed()
				.sorted((o1, o2) -> Double.compare(fromOrdinal(o2).toVector().dotProduct(unitVector), fromOrdinal(o1).toVector().dotProduct(unitVector)))
				.findFirst()
				.get()
		);
	}

	/**
	 * @return Direction opposite to this.
	 */
	public Direction opposite() {
		if (this == Direction.UNKNOWN) {
			return this;
		} else {
			return DIRECTIONS[this.ordinal() ^ 1];
		}
	}

	/**
	 * @return This Direction represented as {@link Vector3D}
	 */
	public Vector3D toVector() {
		return vector;
	}
}
