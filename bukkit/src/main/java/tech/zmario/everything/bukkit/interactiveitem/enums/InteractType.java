package tech.zmario.everything.bukkit.interactiveitem.enums;

import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public enum InteractType {

    NORMAL(PlayerInteractEvent.class),
    AT_ENTITY(PlayerInteractAtEntityEvent.class),
    ;

    private final Class<? extends PlayerEvent> eventClass;

    InteractType(Class<? extends PlayerEvent> eventClass) {
        this.eventClass = eventClass;
    }

    public Class<? extends PlayerEvent> getEventClass() {
        return eventClass;
    }
}
