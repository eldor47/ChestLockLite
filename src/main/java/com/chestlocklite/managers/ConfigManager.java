package com.chestlocklite.managers;

import com.chestlocklite.ChestLockLitePlugin;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {

    private final ChestLockLitePlugin plugin;
    private final FileConfiguration config;

    public ConfigManager(ChestLockLitePlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }

    // Database Settings
    public String getDatabaseFilename() {
        return config.getString("database.filename", "locks.db");
    }

    public int getBackupInterval() {
        return config.getInt("database.backup-interval", 24);
    }

    // Lock Settings
    public boolean isLockingAllowed() {
        return config.getBoolean("locks.allow-locking", true);
    }

    public boolean isAutoLockOnPlace() {
        return config.getBoolean("locks.auto-lock-on-place", false);
    }

    public boolean isPasswordsAllowed() {
        return config.getBoolean("locks.allow-passwords", true);
    }

    public int getMaxPasswordLength() {
        return config.getInt("locks.max-password-length", 32);
    }

    public int getMinPasswordLength() {
        return config.getInt("locks.min-password-length", 4);
    }

    public int getPasswordCooldown() {
        return config.getInt("locks.password-cooldown", 3);
    }

    public int getMaxChestsPerPlayer() {
        return config.getInt("locks.max-chests-per-player", 50);
    }

    public boolean isAdminUnlockAllowed() {
        return config.getBoolean("locks.allow-admin-unlock", true);
    }

    public boolean isExplosionProtectionEnabled() {
        return config.getBoolean("locks.explosion-protection", true);
    }

    public boolean isFireProtectionEnabled() {
        return config.getBoolean("locks.fire-protection", true);
    }

    public boolean isPistonProtectionEnabled() {
        return config.getBoolean("locks.piston-protection", true);
    }

    // Messages
    public String getChestLockedMessage() {
        return config.getString("messages.chest-locked", "&cThis chest is locked!");
    }

    public String getLockedByOwnerMessage(String owner) {
        return config.getString("messages.locked-by-owner", "&eLocked by: &6{owner}")
                .replace("{owner}", owner);
    }

    public String getCannotBreakChestMessage(String owner) {
        return config.getString("messages.cannot-break-chest", "&cYou cannot break this chest! It is locked by &e{owner}&c.")
                .replace("{owner}", owner);
    }

    public String getPasswordProtectedMessage() {
        return config.getString("messages.password-protected", 
                "&eThis chest is password protected. Use &6/cl password <password> &eto unlock.");
    }

    public String getLockSuccessMessage() {
        return config.getString("messages.lock-success", "&aChest locked successfully!");
    }

    public String getUnlockSuccessMessage() {
        return config.getString("messages.unlock-success", "&aChest unlocked successfully!");
    }

    public String getPasswordSetMessage() {
        return config.getString("messages.password-set", "&aPassword set successfully!");
    }

    public String getPasswordRemovedMessage() {
        return config.getString("messages.password-removed", "&aPassword removed successfully!");
    }

    public String getNotLockedMessage() {
        return config.getString("messages.not-locked", "&cThis chest is not locked!");
    }

    public String getAlreadyLockedMessage() {
        return config.getString("messages.already-locked", "&cThis chest is already locked!");
    }

    public String getNotOwnerMessage() {
        return config.getString("messages.not-owner", "&cYou are not the owner of this chest!");
    }

    public String getWrongPasswordMessage() {
        return config.getString("messages.wrong-password", "&cIncorrect password!");
    }

    public String getPasswordCooldownMessage(int seconds) {
        return config.getString("messages.password-cooldown", "&cPlease wait {seconds} seconds before trying another password!")
                .replace("{seconds}", String.valueOf(seconds));
    }

    public String getMaxChestsReachedMessage(int max) {
        return config.getString("messages.max-chests-reached", "&cYou have reached the maximum number of locked chests ({max})!")
                .replace("{max}", String.valueOf(max));
    }

    public String getNoChestTargetMessage() {
        return config.getString("messages.no-chest-target", "&cYou must be looking at a chest!");
    }

    public String getInvalidPasswordLengthMessage(int min, int max) {
        return config.getString("messages.invalid-password-length", 
                "&cPassword must be between {min} and {max} characters!")
                .replace("{min}", String.valueOf(min))
                .replace("{max}", String.valueOf(max));
    }

    // Visual Settings
    public boolean isShowParticles() {
        return config.getBoolean("visual.show-particles", true);
    }

    public String getParticleType() {
        return config.getString("visual.particle-type", "VILLAGER_HAPPY");
    }

    public boolean isShowActionbar() {
        return config.getBoolean("visual.show-actionbar", true);
    }

    // Advanced Settings
    public boolean isDebug() {
        return config.getBoolean("advanced.debug", false);
    }

    public boolean isLogActions() {
        return config.getBoolean("advanced.log-actions", true);
    }
}

