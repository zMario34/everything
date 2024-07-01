package tech.zmario.everything.api.manager;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * CooldownsManager is a class that manages cooldowns for users.
 * <p>
 * It uses a Table to store the cooldowns, with the user's UUID as the row key, the cooldown key as the column key,
 * and the cooldown time as the value.
 * The cooldown time is stored in milliseconds.
 */
public class CooldownsManager {

    private final Table<UUID, String, Long> cooldowns = HashBasedTable.create();

    private CooldownsManager() {
    }

    /**
     * Creates a new instance of CooldownsManager.
     *
     * @return a new instance of CooldownsManager
     */
    public static CooldownsManager create() {
        return new CooldownsManager();
    }

    /**
     * Sets a cooldown for a user.
     *
     * @param user  the user's UUID
     * @param key   the cooldown key
     * @param delay the delay
     * @param unit  the time unit
     */
    public void setCooldown(UUID user, String key, long delay, TimeUnit unit) {
        long time = System.currentTimeMillis() + unit.toMillis(delay);
        cooldowns.put(user, key, time);
    }

    /**
     * Gets the time when the cooldown will expire.
     *
     * @param user the user's UUID
     * @param key  the cooldown key
     */
    public long getCooldown(UUID user, String key) {
        return this.cooldowns.contains(user, key) ? this.cooldowns.get(user, key) : 0;
    }

    /**
     * Gets the remaining time for a cooldown.
     *
     * @param user the user's UUID
     * @param key  the cooldown key
     * @return the remaining time in milliseconds (can be negative or equal to 0 if the cooldown has expired)
     */
    public long getRemainingTime(UUID user, String key) {
        return getCooldown(user, key) - System.currentTimeMillis();
    }

    /**
     * Checks if a user has a cooldown.
     *
     * @param user the user's UUID
     * @param key  the cooldown key
     */
    public boolean hasCooldown(UUID user, String key) {
        return getCooldown(user, key) > System.currentTimeMillis();
    }

    /**
     * Gets all the cooldowns for a user.
     *
     * @param user the user's UUID
     * @return a map with the cooldown keys and the remaining time in milliseconds
     */
    public Map<String, Long> getCooldowns(UUID user) {
        return cooldowns.row(user);
    }

    /**
     * Gets the table with all the cooldowns.
     *
     * @return the table with the cooldowns
     */
    public Table<UUID, String, Long> getCooldowns() {
        return cooldowns;
    }
}