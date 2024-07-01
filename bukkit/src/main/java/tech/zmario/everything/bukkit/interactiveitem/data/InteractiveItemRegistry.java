package tech.zmario.everything.bukkit.interactiveitem.data;

import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import tech.zmario.everything.bukkit.EverythingBukkit;
import tech.zmario.everything.bukkit.interactiveitem.InteractiveItem;
import tech.zmario.everything.bukkit.interactiveitem.enums.InteractType;
import tech.zmario.everything.bukkit.utils.DataUtils;

import java.util.HashMap;
import java.util.Map;

public class InteractiveItemRegistry implements Listener {

    private final Map<String, InteractiveItem> interactiveItems = new HashMap<>();

    private InteractiveItemRegistry() {
    }

    public static InteractiveItemRegistry create() {
        return new InteractiveItemRegistry();
    }

    public void register(String key, InteractiveItem interactiveItem) {
        interactiveItems.put(key, interactiveItem);
    }

    public void unregister(String key) {
        interactiveItems.remove(key);
    }

    public void unregisterAll() {
        interactiveItems.clear();
    }

    public void init(EverythingBukkit library) {
        for (InteractType interactType : InteractType.values()) {
            library.getPlugin().getServer().getPluginManager().registerEvent(interactType.getEventClass(), this /* ??? */,
                    EventPriority.NORMAL, (listener, event) -> {
                        PlayerEvent playerEvent = (PlayerEvent) event;

                        ItemStack item = playerEvent.getPlayer().getItemInHand();

                        if (playerEvent instanceof PlayerInteractEvent)
                            item = ((PlayerInteractEvent) playerEvent).getItem();

                        String key = DataUtils.getData(item, "everything-interactive", String.class);

                        if (key == null) return;
                        InteractiveItem interactiveItem = interactiveItems.get(key);

                        if (interactiveItem == null) return;

                        interactiveItem.getConsumer(interactType).accept(playerEvent);
                    }, library.getPlugin());
        }
    }

    public ItemStack getItemStack(String key) {
        InteractiveItem interactiveItem = interactiveItems.get(key);

        return interactiveItem == null ? null : interactiveItem.getItemStack();
    }
}
