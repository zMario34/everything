package tech.zmario.everything.bukkit.builders;

import com.mojang.authlib.GameProfile;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import tech.zmario.everything.bukkit.utils.Utils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Class to build items easily
 */
public class ItemBuilder {

    private final ItemStack itemStack;
    private final ItemMeta itemMeta;

    private ItemBuilder(Material material) {
        this.itemStack = new ItemStack(material);
        this.itemMeta = itemStack.getItemMeta();
    }

    private ItemBuilder(ItemStack itemStack) {
        this.itemStack = itemStack;
        this.itemMeta = itemStack.getItemMeta();
    }

    public static ItemBuilder builder(Material material) {
        return new ItemBuilder(material);
    }

    public static ItemBuilder from(ItemStack itemStack) {
        return new ItemBuilder(itemStack);
    }

    public ItemBuilder name(String name) {
        if (name != null) {
            if (Utils.hasPaperAndAdventure()) {
                itemMeta.displayName(Utils.legacyColorize(name));
            } else {
                itemMeta.setDisplayName(Utils.colorize(name));
            }
        }

        return this;
    }

    public ItemBuilder name(TextComponent name) {
        if (name != null) {
            if (Utils.hasPaperAndAdventure()) {
                itemMeta.displayName(name);
            } else {
                itemMeta.setDisplayName(Utils.colorize(name.content()));
            }
        }

        return this;
    }

    public ItemBuilder amount(int amount) {
        itemStack.setAmount(Math.max(1, amount));

        return this;
    }

    public ItemBuilder lore(List<TextComponent> lore) {
        if (lore != null) {
            if (Utils.hasPaperAndAdventure()) {
                itemMeta.lore(lore.stream()
                        .map(TextComponent::asComponent)
                        .collect(Collectors.toList()));
            } else {
                return lore(lore.stream()
                        .map(TextComponent::content)
                        .collect(Collectors.toList()));
            }
        }

        return this;
    }

    public ItemBuilder lore(String... lore) {
        if (lore != null) lore(Arrays.asList(lore));

        return this;
    }

    public ItemBuilder lore(Collection<String> lore) {
        if (lore != null) {
            itemMeta.setLore(lore.stream()
                    .map(Utils::colorize)
                    .collect(Collectors.toList()));
        }

        return this;
    }

    public ItemBuilder removeLoreLine(int... line) {
        List<String> lore = itemMeta.getLore();

        for (int i : line) lore.remove(i);

        itemMeta.setLore(lore);
        return this;
    }

    public ItemBuilder flags(ItemFlag... flags) {
        itemMeta.addItemFlags(flags);

        return this;
    }

    public ItemBuilder enchant(Enchantment enchantment, int level) {
        itemMeta.addEnchant(enchantment, level, true);

        return this;
    }

    public ItemBuilder skull(String owner) {
        if (owner.length() <= 16) {
            SkullMeta skullMeta = (SkullMeta) itemMeta;

            skullMeta.setOwner(owner);

            itemStack.setItemMeta(skullMeta);
        } else {
            GameProfile profile = new GameProfile(UUID.randomUUID(), "owner");
            profile.getProperties().put("textures", new com.mojang.authlib.properties.Property("textures", owner));

            try {
                SkullMeta skullMeta = (SkullMeta) itemMeta;

                Field profileField = skullMeta.getClass().getDeclaredField("profile");
                profileField.setAccessible(true);
                profileField.set(skullMeta, profile);

                itemStack.setItemMeta(skullMeta);
            } catch (Exception e) {
                throw new IllegalStateException("Unable to set skull texture", e);
            }
        }

        return this;
    }

    public ItemBuilder unbreakable(boolean value) {
        try {
            itemMeta.setUnbreakable(value);
        } catch (NoSuchMethodError e) {
            try { // TODO: test
                Field field = itemMeta.getClass().getDeclaredField("spigot");

                field.setAccessible(true);

                Object spigot = field.get(itemMeta);

                spigot.getClass().getMethod("setUnbreakable", boolean.class).invoke(spigot, value);
                itemStack.setItemMeta(itemMeta);

                field.setAccessible(false);
            } catch (Exception ex) {
                throw new IllegalStateException("Unable to set unbreakable", ex);
            }
        }

        return this;
    }

    public ItemBuilder data(int data) {
        if (data > 0)
            itemStack.setDurability((short) data);

        return this;
    }

    public ItemBuilder armorColor(Color color) {
        if (!(itemMeta instanceof LeatherArmorMeta)) {
            throw new IllegalStateException("ItemMeta is not a LeatherArmorMeta");
        }

        LeatherArmorMeta meta = (LeatherArmorMeta) itemMeta;

        meta.setColor(color);
        unbreakable(true);

        itemStack.setItemMeta(meta);
        return this;
    }

    public ItemBuilder replaceLore(String key, String value) {
        List<String> lore = itemMeta.getLore();

        if (lore != null) {
            lore.replaceAll(s2 -> s2.replace(key, Utils.colorize(value)));

            itemMeta.setLore(lore);
        }

        return this;
    }

    public ItemBuilder enchants(List<String> enchants) {
        enchants.forEach(enchant -> {
            String[] split = enchant.split(":");

            itemMeta.addEnchant(Enchantment.getByName(split[0]), Integer.parseInt(split[1]), true);
        });

        return this;
    }

    public ItemBuilder material(Material material) {
        itemStack.setType(material);
        return this;
    }

    public ItemBuilder glowing(boolean glowing) {
        if (glowing) {
            itemMeta.addEnchant(Enchantment.DURABILITY, 1, true);
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        } else {
            itemMeta.removeEnchant(Enchantment.DURABILITY);
            itemMeta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        return this;
    }

    public ItemBuilder hideAttributes() {
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        return this;
    }

    public ItemBuilder hideEnchants() {
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        return this;
    }

    public ItemBuilder hideUnbreakable() {
        itemMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        return this;
    }

    public ItemBuilder hideDestroys() {
        itemMeta.addItemFlags(ItemFlag.HIDE_DESTROYS);
        return this;
    }

    public ItemStack build() {
        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    public ItemStack buildAndGive(InventoryHolder... holders) {
        ItemStack stack = build();

        for (InventoryHolder holder : holders) holder.getInventory().addItem(stack);

        return stack;
    }
}
