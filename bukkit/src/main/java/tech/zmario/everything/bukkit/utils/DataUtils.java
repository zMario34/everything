package tech.zmario.everything.bukkit.utils;

import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class DataUtils {

    // "Lasciate ogne speranza, voi ch'intrate" - Dante Alighieri, Inferno (aka this class)

    private static final boolean HAS_NBT = Utils.BUKKIT_VERSION < 14;

    private DataUtils() {
    }

    /**
     * Set data to an item stack
     *
     * @param itemStack the item stack to set the data to
     * @param key       the key of the data
     * @param value     the value of the data (Integer, Double, Boolean, Byte, Long, Short, Float, String)
     * @see NBTItem for more information, if the server version is below 1.14
     */
    public static void setData(ItemStack itemStack, String key, Object value) {
        if (HAS_NBT) {
            NBTItem nbtItem = new NBTItem(itemStack);

            if (value instanceof String) {
                nbtItem.setString(key, (String) value);
            } else if (value instanceof Integer) {
                nbtItem.setInteger(key, (Integer) value);
            } else if (value instanceof Double) {
                nbtItem.setDouble(key, (Double) value);
            } else if (value instanceof Boolean) {
                nbtItem.setBoolean(key, (Boolean) value);
            } else if (value instanceof Byte) {
                nbtItem.setByte(key, (Byte) value);
            } else if (value instanceof Long) {
                nbtItem.setLong(key, (Long) value);
            } else if (value instanceof Short) {
                nbtItem.setShort(key, (Short) value);
            } else if (value instanceof Float) {
                nbtItem.setFloat(key, (Float) value);
            } else {
                nbtItem.setString(key, value.toString());
            }
        } else {
            PersistentDataContainer container = itemStack.getItemMeta().getPersistentDataContainer();
            NamespacedKey namespacedKey = NamespacedKey.minecraft(key);

            if (value instanceof Integer) {
                container.set(namespacedKey, PersistentDataType.INTEGER, (int) value);
            } else if (value instanceof Double) {
                container.set(namespacedKey, PersistentDataType.DOUBLE, (double) value);
            } else if (value instanceof Boolean) {
                if (Boolean.TRUE.equals(value)) {
                    value = (byte) 1;
                } else {
                    value = (byte) 0;
                }

                container.set(namespacedKey, PersistentDataType.BYTE, (byte) value);
            } else if (value instanceof Byte) {
                container.set(namespacedKey, PersistentDataType.BYTE, (byte) value);
            } else if (value instanceof Long) {
                container.set(namespacedKey, PersistentDataType.LONG, (long) value);
            } else if (value instanceof Short) {
                container.set(namespacedKey, PersistentDataType.SHORT, (short) value);
            } else if (value instanceof Float) {
                container.set(namespacedKey, PersistentDataType.FLOAT, (float) value);
            } else {
                container.set(namespacedKey, PersistentDataType.STRING, value.toString());
            }
        }
    }

    /**
     * Get data from an item stack
     *
     * @param itemStack the item stack to get the data from
     * @param key       the key of the data
     * @param type      the type of the data (Integer, Double, Boolean, Byte, Long, Short, Float, String)
     * @return T
     */
    public static <T> T getData(ItemStack itemStack, String key, Class<T> type) {
        Object value;

        if (HAS_NBT) {
            NBTItem nbtItem = new NBTItem(itemStack);

            if (type == Integer.class) {
                value = nbtItem.getInteger(key);
            } else if (type == Double.class) {
                value = nbtItem.getDouble(key);
            } else if (type == Boolean.class) {
                value = nbtItem.getBoolean(key);
            } else if (type == Byte.class) {
                value = nbtItem.getByte(key);
            } else if (type == Long.class) {
                value = nbtItem.getLong(key);
            } else if (type == Short.class) {
                value = nbtItem.getShort(key);
            } else if (type == Float.class) {
                value = nbtItem.getFloat(key);
            } else {
                value = nbtItem.getString(key);
            }
        } else {
            PersistentDataContainer container = itemStack.getItemMeta().getPersistentDataContainer();
            NamespacedKey namespacedKey = NamespacedKey.minecraft(key);

            if (type == Integer.class) {
                value = container.get(namespacedKey, PersistentDataType.INTEGER);
            } else if (type == Double.class) {
                value = container.get(namespacedKey, PersistentDataType.DOUBLE);
            } else if (type == Boolean.class) {
                value = container.get(namespacedKey, PersistentDataType.BYTE) == 1;
            } else if (type == Byte.class) {
                value = container.get(namespacedKey, PersistentDataType.BYTE);
            } else if (type == Long.class) {
                value = container.get(namespacedKey, PersistentDataType.LONG);
            } else if (type == Short.class) {
                value = container.get(namespacedKey, PersistentDataType.SHORT);
            } else if (type == Float.class) {
                value = container.get(namespacedKey, PersistentDataType.FLOAT);
            } else {
                value = container.get(namespacedKey, PersistentDataType.STRING);
            }
        }

        return type.cast(value);
    }

    /**
     * Check if an item stack has a specific data
     *
     * @param item the item to check
     * @param key  the key of the data
     * @return boolean if the item has the data
     */
    public static boolean hasData(ItemStack item, String key) {
        if (HAS_NBT) {
            return new NBTItem(item).hasTag(key);
        }

        return item.getItemMeta().getPersistentDataContainer().has(NamespacedKey.minecraft(key), PersistentDataType.STRING);
    }
}
