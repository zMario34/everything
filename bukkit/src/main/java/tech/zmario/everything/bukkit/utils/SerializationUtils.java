package tech.zmario.everything.bukkit.utils;

import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SerializationUtils {

    private SerializationUtils() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    /**
     * Serialize a location to a string
     * Format: world;x;y;z or world;x;y;z;yaw;pitch
     *
     * @param serialized the location to deserialize
     * @return the deserialized location
     */
    public static Location deserializeLocation(String serialized) {
        String[] parts = serialized.split(";");

        if (parts.length >= 4) {
            String world = parts[0];
            World bukkitWorld = Bukkit.getWorld(world);

            double x = Double.parseDouble(parts[1]);
            double y = Double.parseDouble(parts[2]);
            double z = Double.parseDouble(parts[3]);

            if (parts.length == 6) {
                float yaw = Float.parseFloat(parts[4]);
                float pitch = Float.parseFloat(parts[5]);

                return new Location(bukkitWorld, x, y, z, yaw, pitch);
            }

            return new Location(bukkitWorld, x, y, z);
        }

        throw new IllegalArgumentException("Invalid serialized location, correct format: " +
                "world;x;y;z or world;x;y;z;yaw;pitch");
    }

    /**
     * Deserialize a location and set the world
     *
     * @param serialized the serialized location
     * @param world      the world to set
     * @return the deserialized location
     */
    public static Location deserializeLocation(String serialized, World world) {
        Location location = deserializeLocation(serialized);

        if (world == null) throw new IllegalArgumentException("world cannot be null");

        location.setWorld(world);

        return location;
    }

    /**
     * Serialize a location to a string
     *
     * @param location     the location to serialize
     * @param includeWorld whether to include the world in the serialized location
     * @return the serialized location
     */
    public static String serializeLocation(Location location, boolean includeWorld) {
        String world = includeWorld ? location.getWorld().getName() + ";" : "";

        return world + location.getX() + ";" + location.getY() + ";" + location.getZ() + ";" +
                location.getYaw() + ";" + location.getPitch();
    }

    /**
     * Serialize a location and floor it to the nearest center block and round the yaw and pitch
     *
     * @param location     the location to serialize
     * @param includeWorld whether to include the world in the serialized location
     *
     * @return the serialized location
     */
    public static String serializeAndFloorLocation(Location location, boolean includeWorld) {
        Location finalLocation = new Location(location.getWorld(), location.getBlockX(), location.getBlockY(),
                location.getBlockZ(), location.getYaw(), location.getPitch());

        float yaw = Math.round(finalLocation.getYaw() / 45) * 45F;
        float pitch = Math.round(finalLocation.getPitch() / 45) * 45F;

        finalLocation.setYaw(yaw);
        finalLocation.setPitch(pitch);

        finalLocation.setX(location.getBlockX() + 0.5);
        finalLocation.setZ(location.getBlockZ() + 0.5);

        return serializeLocation(finalLocation, includeWorld);
    }

    /**
     * Deserialize a sound and play it to the specified players
     * Format: sound;volume;pitch
     *
     * @param serialized the serialized sound
     * @param players    the players to play the sound to
     */
    public static void deserializeAndPlaySound(String serialized, Player... players) {
        String[] parts = serialized.split(";");

        if (parts.length >= 3) {
            Sound sound = Sound.valueOf(parts[0]);
            float volume = Float.parseFloat(parts[1]);
            float pitch = Float.parseFloat(parts[2]);

            for (Player player : players) player.playSound(player.getLocation(), sound, volume, pitch);
            return;
        }

        throw new IllegalArgumentException("Invalid serialized sound, correct format: sound;volume;pitch");
    }

    /**
     * Serialize a sound to a string
     *
     * @param sound  the sound to serialize
     * @param volume the volume of the sound
     * @param pitch  the pitch of the sound
     */
    public static String serializeSound(Sound sound, float volume, float pitch) {
        return sound.name() + ";" + volume + ";" + pitch;
    }

    public static ItemStack deserializeItemStack(ConfigurationSection section) {
        ItemStack itemStack = new ItemStack(Material.matchMaterial(section.getString("type")), section.getInt("amount", 1));

        if (section.contains("data")) itemStack.setDurability((short) section.getInt("data"));

        if (section.contains("meta")) {
            ConfigurationSection meta = section.getConfigurationSection("meta");
            ItemMeta itemMeta = itemStack.getItemMeta();

            if (meta.contains("display-name")) itemMeta.setDisplayName(Utils.colorize(meta.getString("display-name")));
            if (meta.contains("lore")) itemMeta.setLore(Utils.colorize(meta.getStringList("lore")));

            if (meta.contains("enchantments")) {
                ConfigurationSection enchantments = meta.getConfigurationSection("enchantments");

                for (String key : enchantments.getKeys(false)) {
                    Enchantment enchantment = Enchantment.getByName(key);

                    if (enchantment == null) {
                        Utils.SERVER.getLogger().warning("Invalid enchantment: " + key);
                        continue;
                    }

                    int level = enchantments.getInt(key);

                    itemMeta.addEnchant(enchantment, level, true);
                }
            }

            if (Utils.BUKKIT_VERSION >= 16 && meta.contains("custom-model-data"))
                itemMeta.setCustomModelData(meta.getInt("custom-model-data"));

            if (meta.contains("flags"))
                for (String flag : meta.getStringList("flags")) itemMeta.addItemFlags(ItemFlag.valueOf(flag));

            itemStack.setItemMeta(itemMeta);
        }

        return itemStack;
    }

    public static void serializeItemStack(ConfigurationSection section, ItemStack itemStack) {
        section.set("type", itemStack.getType().name());
        section.set("amount", itemStack.getAmount());

        if (itemStack.getDurability() != 0) section.set("data", itemStack.getDurability());

        ItemMeta itemMeta = itemStack.getItemMeta();

        if (itemMeta.hasDisplayName()) section.set("meta.display-name", itemMeta.getDisplayName());
        if (itemMeta.hasLore()) section.set("meta.lore", itemMeta.getLore());

        if (itemMeta.hasEnchants()) {
            ConfigurationSection enchantments = section.createSection("meta.enchantments");

            for (Enchantment enchantment : itemMeta.getEnchants().keySet())
                enchantments.set(enchantment.getName(), itemMeta.getEnchantLevel(enchantment));
        }

        if (Utils.BUKKIT_VERSION >= 16 && itemMeta.hasCustomModelData())
            section.set("meta.custom-model-data", itemMeta.getCustomModelData());

        if (!itemMeta.getItemFlags().isEmpty()) section.set("meta.flags", itemMeta.getItemFlags());
    }
}
