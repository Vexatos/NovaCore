package nova.core.retention;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * The data class is capable of storing named data.
 *
 * Data types supported:
 * - Boolean
 * - Byte
 * - Short
 * - Integer
 * - Long
 * - Character
 * - Float
 * - Double
 * - String
 *
 * - Enumerator
 * - Storable (Converted into Data)
 * - Data
 * @author Calclavia
 */
//TODO: Add collection and array support
public class Data extends HashMap<String, Object> {

	public static Class<?>[] dataTypes = {
		Boolean.class,
		Byte.class,
		Short.class,
		Integer.class,
		Long.class,
		Character.class,
		Float.class,
		Double.class,
		String.class,
		//Special data types that all convert into Data.
		Enum.class,
		Storable.class,
		Data.class,
		Collection.class,
		Vector3D.class,
		Vector2D.class };

	public String className;

	public Data() {

	}

	public Data(Class clazz) {
		className = clazz.getName();
		super.put("class", className);
	}

	/**
	 * Saves an object, serializing its data.
	 * This map can be reloaded and its class with be reconstructed.
	 * @param obj The object to store.
	 * @return The data of the object with
	 */
	public static Data serialize(Storable obj) {
		Data data = new Data(obj.getClass());
		obj.save(data);
		data.putIfAbsent("class", obj.getClass().getName());
		obj.save(data);
		return data;
	}

	/**
	 * Loads an object from its stored data, with an unknown class.
	 * The class of the object must be stored within the data.
	 * @param data The data
	 * @return The object loaded with given data.
	 */
	public static Object unserialize(Data data) {
		try {
			Class clazz = (Class) Class.forName((String) data.get("class"));
			if (clazz.isEnum()) {
				return Enum.valueOf(clazz, data.get("value"));
			} else if (clazz == Vector3D.class) {
				return new Vector3D(data.get("x"), data.get("y"), data.get("z"));
			} else if (clazz == Vector2D.class) {
				return new Vector2D(data.get("x"), (double) data.get("y"));
			} else {
				return unserialize(clazz, data);
			}
		} catch (Exception e) {
			throw new DataException(e);
		}

	}

	/**
	 * Loads an object from its stored data, given its class.
	 * @param clazz - The class to load
	 * @param data - The data
	 * @return The object loaded with given data.
	 */
	public static <T extends Storable> T unserialize(Class<T> clazz, Data data) {
		try {
			T storableObj = clazz.newInstance();
			storableObj.load(data);
			return storableObj;
		} catch (Exception e) {
			throw new DataException(e);
		}
	}

	@Override
	public void putAll(Map<? extends String, ?> m) {
		//TODO: More efficient way to do this?
		m.forEach(this::put);
	}

	public void putAll(Data m) {
		super.putAll(m);
	}

	@Override
	public Object put(String key, Object value) {
		assert key != null && value != null;
		assert !key.equals("class");
		final Object check = value;
		assert Arrays.stream(dataTypes).anyMatch(clazz -> clazz.isAssignableFrom(check.getClass()));

		if (value instanceof Enum) {
			Data enumData = new Data(value.getClass());
			enumData.put("value", ((Enum) value).name());
			value = enumData;
		} else if (value instanceof Vector3D) {
			Data vectorData = new Data(Vector3D.class);
			vectorData.put("x", ((Vector3D) value).getX());
			vectorData.put("y", ((Vector3D) value).getY());
			vectorData.put("z", ((Vector3D) value).getZ());
			value = vectorData;
		} else if (value instanceof Vector2D) {
			Data vectorData = new Data(Vector2D.class);
			vectorData.put("x", ((Vector2D) value).getX());
			vectorData.put("y", ((Vector2D) value).getY());
			value = vectorData;
		} else if (value instanceof Storable) {
			value = serialize((Storable) value);
		}

		return super.put(key, value);
	}

	/**
	 * A pre-cast version of get.
	 */
	@SuppressWarnings("unchecked")
	public <T> T get(String key) {
		return (T) super.get(key);
	}

	public <T extends Enum<T>> T getEnum(String key) {
		Data enumData = get(key);
		try {
			Class<T> enumClass = (Class) Class.forName(enumData.className);
			return Enum.valueOf(enumClass, enumData.get("value"));
		} catch (Exception e) {
			throw new DataException(e);
		}
	}

	public Vector3D getVector3D(String key) {
		Data data = get(key);
		return new Vector3D(data.get("x"), data.get("y"), data.get("z"));
	}

	public Vector2D getVector2D(String key) {
		Data data = get(key);
		return new Vector2D(data.get("x"), (double) data.get("y"));
	}

	public <T extends Storable> T getStorable(String key) {
		Data storableData = get(key);
		try {
			Class<T> storableClass = (Class) Class.forName(storableData.className);
			T obj = storableClass.newInstance();
			obj.load(storableData);
			return obj;
		} catch (Exception e) {
			throw new DataException(e);
		}
	}

}
