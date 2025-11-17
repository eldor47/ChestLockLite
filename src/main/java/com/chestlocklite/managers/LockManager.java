package com.chestlocklite.managers;

import com.chestlocklite.ChestLockLitePlugin;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;

import java.sql.SQLException;

public class LockManager {

    private final ChestLockLitePlugin plugin;

    public LockManager(ChestLockLitePlugin plugin) {
        this.plugin = plugin;
    }

    public boolean isChest(Block block) {
        // Exclude ender chests - they have their own storage system
        if (block.getType() == Material.ENDER_CHEST) {
            return false;
        }
        
        // Check if block state is a Chest instance
        // This covers: CHEST, TRAPPED_CHEST, COPPER_CHEST, and any other chest variants
        BlockState state = block.getState();
        return state instanceof Chest;
    }

    public boolean isLockableContainer(Block block) {
        Material type = block.getType();
        
        // Check for chests (excluding ender chests)
        if (isChest(block)) {
            return true;
        }
        
        // Check for barrels
        if (type == Material.BARREL) {
            return true;
        }
        
        // Check for hoppers (if enabled in config)
        if (type == Material.HOPPER && plugin.getConfigManager().isHopperSupportEnabled()) {
            return true;
        }
        
        // Check for furnaces (if enabled in config)
        if ((type == Material.FURNACE || type == Material.BLAST_FURNACE || 
             type == Material.SMOKER) && plugin.getConfigManager().isFurnaceSupportEnabled()) {
            return true;
        }
        
        return false;
    }

    public Chest getChest(Block block) {
        if (!isChest(block)) {
            return null;
        }

        BlockState state = block.getState();
        if (state instanceof Chest) {
            return (Chest) state;
        }

        return null;
    }

    public Block getDoubleChestPartner(Block chestBlock) {
        Chest chest = getChest(chestBlock);
        if (chest == null) {
            return null;
        }

        Chest doubleChest = null;
        try {
            doubleChest = chest.getInventory().getHolder() instanceof org.bukkit.block.DoubleChest
                    ? (Chest) ((org.bukkit.block.DoubleChest) chest.getInventory().getHolder()).getLeftSide()
                    : null;
        } catch (Exception e) {
            // Not a double chest or error getting partner
            return null;
        }

        if (doubleChest != null && doubleChest.getLocation().equals(chestBlock.getLocation())) {
            doubleChest = (Chest) ((org.bukkit.block.DoubleChest) chest.getInventory().getHolder()).getRightSide();
        }

        return doubleChest != null ? doubleChest.getBlock() : null;
    }

    public Location getPrimaryChestLocation(Block chestBlock) {
        // For non-chest containers, just return the block location
        if (!isChest(chestBlock)) {
            return chestBlock.getLocation();
        }
        
        Chest chest = getChest(chestBlock);
        if (chest == null) {
            return chestBlock.getLocation();
        }

        try {
            if (chest.getInventory().getHolder() instanceof org.bukkit.block.DoubleChest) {
                org.bukkit.block.DoubleChest doubleChest = 
                    (org.bukkit.block.DoubleChest) chest.getInventory().getHolder();
                Chest leftChest = (Chest) doubleChest.getLeftSide();
                return leftChest.getLocation();
            }
        } catch (Exception e) {
            // Fallback to single chest location
        }

        return chestBlock.getLocation();
    }

    public boolean lockChest(Location location, Player player) {
        try {
            // Check if already locked
            if (plugin.getDatabaseManager().getLock(location) != null) {
                return false;
            }

            // Check player chest limit
            int chestCount = plugin.getDatabaseManager().getChestCount(player.getUniqueId());
            if (chestCount >= plugin.getConfigManager().getMaxChestsPerPlayer()) {
                return false;
            }

            // Add lock
            plugin.getDatabaseManager().addLock(location, player.getUniqueId(), player.getName());
            return true;
        } catch (SQLException e) {
            plugin.getLogger().severe("Error locking chest: " + e.getMessage());
            return false;
        }
    }

    public boolean unlockChest(Location location, Player player) {
        try {
            DatabaseManager.LockData lock = plugin.getDatabaseManager().getLock(location);
            if (lock == null) {
                return false;
            }

            // Check if player is owner or admin
            if (!lock.getOwnerUUID().equals(player.getUniqueId()) && 
                !player.hasPermission("chestlocklite.admin")) {
                return false;
            }

            plugin.getDatabaseManager().removeLock(location);
            return true;
        } catch (SQLException e) {
            plugin.getLogger().severe("Error unlocking chest: " + e.getMessage());
            return false;
        }
    }

    public boolean canOpenChest(Location location, Player player, String password) {
        try {
            DatabaseManager.LockData lock = plugin.getDatabaseManager().getLock(location);
            if (lock == null) {
                return true; // Not locked, can open
            }

            // Admin bypass
            if (player.hasPermission("chestlocklite.bypass")) {
                return true;
            }

            // Check if owner
            if (lock.getOwnerUUID().equals(player.getUniqueId())) {
                return true;
            }

            // Check if trusted player
            if (plugin.getDatabaseManager().isTrustedPlayer(location, player.getUniqueId())) {
                return true;
            }

            // Check password if provided
            if (password != null && lock.hasPassword()) {
                return com.chestlocklite.utils.PasswordHasher.verifyPassword(password, lock.getPassword());
            }

            // Check if password required
            if (lock.hasPassword()) {
                return false; // Password required but not provided
            }

            // Only owner or trusted players can open if no password
            return false;
        } catch (SQLException e) {
            plugin.getLogger().severe("Error checking chest access: " + e.getMessage());
            return false;
        }
    }

    public boolean setPassword(Location location, Player player, String password) {
        try {
            DatabaseManager.LockData lock = plugin.getDatabaseManager().getLock(location);
            if (lock == null) {
                return false; // Chest not locked
            }

            // Check if player is owner
            if (!lock.getOwnerUUID().equals(player.getUniqueId())) {
                return false;
            }

            plugin.getDatabaseManager().setPassword(location, password);
            return true;
        } catch (SQLException e) {
            plugin.getLogger().severe("Error setting password: " + e.getMessage());
            return false;
        }
    }

    public boolean removePassword(Location location, Player player) {
        try {
            DatabaseManager.LockData lock = plugin.getDatabaseManager().getLock(location);
            if (lock == null) {
                return false; // Chest not locked
            }

            // Check if player is owner
            if (!lock.getOwnerUUID().equals(player.getUniqueId())) {
                return false;
            }

            plugin.getDatabaseManager().removePassword(location);
            return true;
        } catch (SQLException e) {
            plugin.getLogger().severe("Error removing password: " + e.getMessage());
            return false;
        }
    }

    public DatabaseManager.LockData getLockInfo(Location location) {
        try {
            return plugin.getDatabaseManager().getLock(location);
        } catch (SQLException e) {
            plugin.getLogger().severe("Error getting lock info: " + e.getMessage());
            return null;
        }
    }
}

