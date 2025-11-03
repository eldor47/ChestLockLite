package com.chestlocklite.managers;

import com.chestlocklite.ChestLockLitePlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PasswordCooldownManager {

    private final ChestLockLitePlugin plugin;
    private final Map<UUID, Long> passwordCooldowns = new HashMap<>();

    public PasswordCooldownManager(ChestLockLitePlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Check if player is on cooldown for password attempts
     * @param playerId Player UUID
     * @return Remaining seconds on cooldown, or 0 if no cooldown
     */
    public int checkCooldown(UUID playerId) {
        if (!passwordCooldowns.containsKey(playerId)) {
            return 0;
        }

        long currentTime = System.currentTimeMillis();
        long lastAttempt = passwordCooldowns.get(playerId);
        int cooldownSeconds = plugin.getConfigManager().getPasswordCooldown();
        long timeSinceLastAttempt = (currentTime - lastAttempt) / 1000; // Convert to seconds

        if (timeSinceLastAttempt < cooldownSeconds) {
            return (int) (cooldownSeconds - timeSinceLastAttempt);
        }

        return 0; // Cooldown expired
    }

    /**
     * Set cooldown for player after password attempt
     * @param playerId Player UUID
     */
    public void setCooldown(UUID playerId) {
        passwordCooldowns.put(playerId, System.currentTimeMillis());
    }

    /**
     * Clear cooldown for player (useful for admin overrides or correct passwords)
     * @param playerId Player UUID
     */
    public void clearCooldown(UUID playerId) {
        passwordCooldowns.remove(playerId);
    }
}

