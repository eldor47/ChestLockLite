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

    public boolean isHopperSupportEnabled() {
        return config.getBoolean("locks.allow-hoppers", true);
    }

    public boolean isFurnaceSupportEnabled() {
        return config.getBoolean("locks.allow-furnaces", true);
    }

    public boolean isAdminNotificationEnabled() {
        return config.getBoolean("locks.admin-notification", true);
    }

    // Messages
    public String getChestLockedMessage(org.bukkit.block.Block block) {
        String containerType = getContainerTypeName(block);
        String defaultMessage = "&cThis " + containerType + " is locked!";
        String message = config.getString("messages.chest-locked", defaultMessage);
        // Replace {type} placeholder if present, otherwise use default
        if (message.contains("{type}")) {
            return message.replace("{type}", containerType);
        }
        // If config doesn't have {type}, use the default with correct type
        return defaultMessage;
    }
    
    public String getContainerTypeName(org.bukkit.block.Block block) {
        org.bukkit.Material type = block.getType();
        
        // Handle barrels
        if (type == org.bukkit.Material.BARREL) {
            return "barrel";
        }
        
        // Handle hoppers
        if (type == org.bukkit.Material.HOPPER) {
            return "hopper";
        }
        
        // Handle furnaces
        if (type == org.bukkit.Material.FURNACE) {
            return "furnace";
        }
        if (type == org.bukkit.Material.BLAST_FURNACE) {
            return "blast furnace";
        }
        if (type == org.bukkit.Material.SMOKER) {
            return "smoker";
        }
        
        // Handle chest types
        if (type == org.bukkit.Material.CHEST) {
            return "chest";
        }
        if (type == org.bukkit.Material.TRAPPED_CHEST) {
            return "trapped chest";
        }
        
        // Try to handle COPPER_CHEST if it exists (may not be available in all versions)
        try {
            org.bukkit.Material copperChest = org.bukkit.Material.valueOf("COPPER_CHEST");
            if (type == copperChest) {
                return "copper chest";
            }
        } catch (IllegalArgumentException e) {
            // COPPER_CHEST doesn't exist in this version, ignore
        }
        
        // Default fallback for any other chest-like blocks
        return "chest";
    }
    
    private String replaceTypePlaceholder(String message, org.bukkit.block.Block block) {
        if (message == null) {
            return null;
        }
        String containerType = getContainerTypeName(block);
        return message.replace("{type}", containerType);
    }

    public String getLockedByOwnerMessage(String owner) {
        return config.getString("messages.locked-by-owner", "&eLocked by: &6{owner}")
                .replace("{owner}", owner);
    }

    public String getCannotBreakChestMessage(org.bukkit.block.Block block, String owner) {
        String message = config.getString("messages.cannot-break-chest", "&cYou cannot break this {type}! It is locked by &e{owner}&c.");
        message = replaceTypePlaceholder(message, block);
        return message.replace("{owner}", owner);
    }

    public String getPasswordProtectedMessage(org.bukkit.block.Block block) {
        String message = config.getString("messages.password-protected", 
                "&eThis {type} is password protected. Use &6/cl password <password> &eto unlock.");
        return replaceTypePlaceholder(message, block);
    }

    public String getLockSuccessMessage(org.bukkit.block.Block block) {
        String message = config.getString("messages.lock-success", "&a{type} locked successfully!");
        return replaceTypePlaceholder(message, block);
    }

    public String getUnlockSuccessMessage(org.bukkit.block.Block block) {
        String message = config.getString("messages.unlock-success", "&a{type} unlocked successfully!");
        return replaceTypePlaceholder(message, block);
    }

    public String getPasswordSetMessage() {
        return config.getString("messages.password-set", "&aPassword set successfully!");
    }

    public String getPasswordRemovedMessage() {
        return config.getString("messages.password-removed", "&aPassword removed successfully!");
    }

    public String getNotLockedMessage(org.bukkit.block.Block block) {
        String message = config.getString("messages.not-locked", "&cThis {type} is not locked!");
        return replaceTypePlaceholder(message, block);
    }

    public String getAlreadyLockedMessage(org.bukkit.block.Block block) {
        String message = config.getString("messages.already-locked", "&cThis {type} is already locked!");
        return replaceTypePlaceholder(message, block);
    }

    public String getNotOwnerMessage(org.bukkit.block.Block block) {
        String message = config.getString("messages.not-owner", "&cYou are not the owner of this {type}!");
        return replaceTypePlaceholder(message, block);
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
        // This message is generic, doesn't need container type
        return config.getString("messages.no-chest-target", "&cYou must be looking at a container!");
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

