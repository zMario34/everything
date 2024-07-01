package tech.zmario.everything.bukkit.interactiveitem;

import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;
import tech.zmario.everything.bukkit.interactiveitem.enums.InteractType;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class InteractiveItem {

    private final ItemStack itemStack;
    private final Map<InteractType, Consumer<PlayerEvent>> consumers;

    public InteractiveItem(ItemStack itemStack, Map<InteractType, Consumer<PlayerEvent>> consumers) {
        this.itemStack = itemStack;
        this.consumers = consumers;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Consumer<PlayerEvent> getConsumer(InteractType interactType) {
        return consumers.get(interactType);
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public static class Builder {
        private final Map<InteractType, Consumer<PlayerEvent>> consumers = new HashMap<>();
        private ItemStack itemStack;

        private Builder() {
        }

        public Builder itemStack(ItemStack itemStack) {
            this.itemStack = itemStack;
            return this;
        }

        public Builder onInteract(InteractType interactType, Consumer<PlayerEvent> consumer) {
            consumers.put(interactType, consumer);
            return this;
        }

        public InteractiveItem build() {
            return new InteractiveItem(itemStack, consumers);
        }
    }
}
