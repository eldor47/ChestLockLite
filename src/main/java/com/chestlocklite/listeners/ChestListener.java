package com.chestlocklite.listeners;

import com.chestlocklite.ChestLockLitePlugin;
import com.chestlocklite.utils.MessageUtils;
import com.chestlocklite.utils.PasswordHasher;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.Material;
import java.util.Iterator;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChestListener implements Listener {

    private final ChestLockLitePlugin plugin;
    // Store password attempts per player and chest location (using block coordinates as key)
    private final Map<UUID, Map<String, String>> passwordAttempts = new HashMap<>();

    public ChestListener(ChestLockLitePlugin plugin) {
        this.plugin = plugin;
    }
    
    // Helper method to create a location key from a Location object
    private String getLocationKey(org.bukkit.Location loc) {
        return loc.getWorld().getName() + ":" + loc.getBlockX() + ":" + loc.getBlockY() + ":" + loc.getBlockZ();
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onChestOpen(PlayerInteractEvent event) {
        // Only handle right-click actions
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        // Only handle main hand to avoid double events
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        Player player = event.getPlayer();
        Block block = event.getClickedBlock();

        if (block == null || !plugin.getLockManager().isChest(block)) {
            return;
        }

        // Shift-right-click to open GUI (only if hand is empty)
        if (player.isSneaking() && player.hasPermission("chestlocklite.gui") && 
            player.getInventory().getItemInMainHand().getType() == Material.AIR) {
            org.bukkit.Location chestLocation = plugin.getLockManager().getPrimaryChestLocation(block);
            plugin.getLockGUI().openGUI(player, chestLocation);
            event.setCancelled(true);
            return;
        }

        // Admin bypass
        if (player.hasPermission("chestlocklite.bypass")) {
            return;
        }

        // Get primary chest location (handles double chests)
        org.bukkit.Location chestLocation = plugin.getLockManager().getPrimaryChestLocation(block);

        // Check if locked
        com.chestlocklite.managers.DatabaseManager.LockData lock = 
            plugin.getLockManager().getLockInfo(chestLocation);

        if (lock == null) {
            return; // Not locked, allow access
        }

        // Check if owner
        if (lock.getOwnerUUID().equals(player.getUniqueId())) {
            return; // Owner can always open
        }

        // Check if trusted player
        try {
            if (plugin.getDatabaseManager().isTrustedPlayer(chestLocation, player.getUniqueId())) {
                return; // Trusted player can open
            }
        } catch (java.sql.SQLException e) {
            plugin.getLogger().warning("Error checking trusted player status: " + e.getMessage());
        }

        // Check if password protected
        if (lock.hasPassword()) {
            // Check if player has attempted password for THIS specific chest location
            Map<String, String> playerAttempts = passwordAttempts.get(player.getUniqueId());
            String locationKey = getLocationKey(chestLocation);
            
            if (playerAttempts != null) {
                String attemptedPassword = playerAttempts.get(locationKey);
                
                if (attemptedPassword != null && PasswordHasher.verifyPassword(attemptedPassword, lock.getPassword())) {
                    // Player has correct password cached for this chest, allow access
                    return;
                }
            }

            // Cancel event and show password prompt
            event.setCancelled(true);
            
            // Show password locked message with owner name
            player.sendMessage(MessageUtils.colorize(
                "&cThis chest is password protected by &e" + lock.getOwnerName() + "&c!"));
            player.sendMessage(MessageUtils.colorize(
                "&7Use &e/cl password <password> &7while looking at this chest to unlock it."));
            
            return;
        }

        // Chest is locked without password, only owner can open
        event.setCancelled(true);
        
        player.sendMessage(MessageUtils.colorize(
            plugin.getConfigManager().getChestLockedMessage()));
        
        player.sendMessage(MessageUtils.colorize(
            plugin.getConfigManager().getLockedByOwnerMessage(lock.getOwnerName())));

        // Show particles if enabled
        if (plugin.getConfigManager().isShowParticles()) {
            try {
                org.bukkit.Particle particle = org.bukkit.Particle.valueOf(
                    plugin.getConfigManager().getParticleType());
                player.spawnParticle(particle, block.getLocation().add(0.5, 0.5, 0.5), 
                    10, 0.3, 0.3, 0.3, 0.1);
            } catch (Exception e) {
                // Invalid particle type, ignore
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onChestBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        // Check if block is a chest
        if (!plugin.getLockManager().isChest(block)) {
            return;
        }

        // Get primary chest location (handles double chests)
        org.bukkit.Location chestLocation = plugin.getLockManager().getPrimaryChestLocation(block);

        // Check if locked
        com.chestlocklite.managers.DatabaseManager.LockData lock = 
            plugin.getLockManager().getLockInfo(chestLocation);

        if (lock == null) {
            return; // Not locked, allow breaking
        }

        // Admin bypass permission
        if (player.hasPermission("chestlocklite.bypass")) {
            // Admin breaking - schedule cleanup after break completes
            org.bukkit.Bukkit.getScheduler().runTask(plugin, () -> {
                try {
                    plugin.getDatabaseManager().removeLock(chestLocation);
                    plugin.getLogger().info("Lock removed from database: Admin " + player.getName() + 
                        " broke chest at " + chestLocation.getWorld().getName() + " " + 
                        chestLocation.getBlockX() + "," + chestLocation.getBlockY() + "," + 
                        chestLocation.getBlockZ() + " (Owner: " + lock.getOwnerName() + ")");
                } catch (java.sql.SQLException e) {
                    plugin.getLogger().warning("Error removing lock from database: " + e.getMessage());
                }
            });
            return; // Admins can break locked chests
        }

        // Check if owner
        if (lock.getOwnerUUID().equals(player.getUniqueId())) {
            // Owner breaking - schedule cleanup after break completes
            org.bukkit.Bukkit.getScheduler().runTask(plugin, () -> {
                try {
                    plugin.getDatabaseManager().removeLock(chestLocation);
                    plugin.getLogger().info("Lock removed from database: Owner " + player.getName() + 
                        " broke chest at " + chestLocation.getWorld().getName() + " " + 
                        chestLocation.getBlockX() + "," + chestLocation.getBlockY() + "," + 
                        chestLocation.getBlockZ());
                } catch (java.sql.SQLException e) {
                    plugin.getLogger().warning("Error removing lock from database: " + e.getMessage());
                }
            });
            return; // Owner can break their own chests
        }

        // Cancel event - chest is locked and player is not owner or admin
        event.setCancelled(true);
        player.sendMessage(MessageUtils.colorize(
            plugin.getConfigManager().getCannotBreakChestMessage(lock.getOwnerName())));
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = false)
    public void onEntityExplode(EntityExplodeEvent event) {
        // Check if explosion protection is enabled
        if (!plugin.getConfigManager().isExplosionProtectionEnabled()) {
            return;
        }
        
        // Protect locked chests from entity explosions (creepers, TNT, etc.)
        Iterator<Block> iterator = event.blockList().iterator();
        while (iterator.hasNext()) {
            Block block = iterator.next();
            
            if (!plugin.getLockManager().isChest(block)) {
                continue;
            }
            
            // Get primary chest location (handles double chests)
            org.bukkit.Location chestLocation = plugin.getLockManager().getPrimaryChestLocation(block);
            
            // Check if locked
            com.chestlocklite.managers.DatabaseManager.LockData lock = 
                plugin.getLockManager().getLockInfo(chestLocation);
            
            if (lock != null) {
                // Chest is locked - remove it from explosion list to protect it
                iterator.remove();
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = false)
    public void onBlockExplode(BlockExplodeEvent event) {
        // Check if explosion protection is enabled
        if (!plugin.getConfigManager().isExplosionProtectionEnabled()) {
            return;
        }
        
        // Protect locked chests from block explosions
        Iterator<Block> iterator = event.blockList().iterator();
        while (iterator.hasNext()) {
            Block block = iterator.next();
            
            if (!plugin.getLockManager().isChest(block)) {
                continue;
            }
            
            // Get primary chest location (handles double chests)
            org.bukkit.Location chestLocation = plugin.getLockManager().getPrimaryChestLocation(block);
            
            // Check if locked
            com.chestlocklite.managers.DatabaseManager.LockData lock = 
                plugin.getLockManager().getLockInfo(chestLocation);
            
            if (lock != null) {
                // Chest is locked - remove it from explosion list to protect it
                iterator.remove();
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = false)
    public void onBlockBurn(BlockBurnEvent event) {
        // Check if fire protection is enabled
        if (!plugin.getConfigManager().isFireProtectionEnabled()) {
            return;
        }
        
        // Protect locked chests from fire
        Block block = event.getBlock();
        
        if (!plugin.getLockManager().isChest(block)) {
            return;
        }
        
        // Get primary chest location (handles double chests)
        org.bukkit.Location chestLocation = plugin.getLockManager().getPrimaryChestLocation(block);
        
        // Check if locked
        com.chestlocklite.managers.DatabaseManager.LockData lock = 
            plugin.getLockManager().getLockInfo(chestLocation);
        
        if (lock != null) {
            // Chest is locked - cancel burning
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = false)
    public void onBlockFade(BlockFadeEvent event) {
        // Check if fire protection is enabled
        if (!plugin.getConfigManager().isFireProtectionEnabled()) {
            return;
        }
        
        // Protect locked chests from melting/burning (lava, fire spreading)
        Block block = event.getBlock();
        
        if (!plugin.getLockManager().isChest(block)) {
            return;
        }
        
        // Get primary chest location (handles double chests)
        org.bukkit.Location chestLocation = plugin.getLockManager().getPrimaryChestLocation(block);
        
        // Check if locked
        com.chestlocklite.managers.DatabaseManager.LockData lock = 
            plugin.getLockManager().getLockInfo(chestLocation);
        
        if (lock != null) {
            // Chest is locked - prevent fading/melting
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = false)
    public void onPistonExtend(BlockPistonExtendEvent event) {
        // Check if piston protection is enabled
        if (!plugin.getConfigManager().isPistonProtectionEnabled()) {
            return;
        }
        
        // Prevent pistons from moving locked chests
        List<Block> blocks = event.getBlocks();
        
        for (Block block : blocks) {
            if (!plugin.getLockManager().isChest(block)) {
                continue;
            }
            
            // Get primary chest location (handles double chests)
            org.bukkit.Location chestLocation = plugin.getLockManager().getPrimaryChestLocation(block);
            
            // Check if locked
            com.chestlocklite.managers.DatabaseManager.LockData lock = 
                plugin.getLockManager().getLockInfo(chestLocation);
            
            if (lock != null) {
                // Chest is locked - prevent piston from moving it
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = false)
    public void onPistonRetract(BlockPistonRetractEvent event) {
        // Check if piston protection is enabled
        if (!plugin.getConfigManager().isPistonProtectionEnabled()) {
            return;
        }
        
        // Prevent pistons from retracting locked chests
        List<Block> blocks = event.getBlocks();
        
        for (Block block : blocks) {
            if (!plugin.getLockManager().isChest(block)) {
                continue;
            }
            
            // Get primary chest location (handles double chests)
            org.bukkit.Location chestLocation = plugin.getLockManager().getPrimaryChestLocation(block);
            
            // Check if locked
            com.chestlocklite.managers.DatabaseManager.LockData lock = 
                plugin.getLockManager().getLockInfo(chestLocation);
            
            if (lock != null) {
                // Chest is locked - prevent piston from moving it
                event.setCancelled(true);
                return;
            }
        }
    }

    public void setPasswordAttempt(UUID playerUUID, org.bukkit.Location chestLocation, String password) {
        String locationKey = getLocationKey(chestLocation);
        passwordAttempts.computeIfAbsent(playerUUID, k -> new HashMap<>()).put(locationKey, password);
    }

    public void clearPasswordAttempt(UUID playerUUID, org.bukkit.Location chestLocation) {
        Map<String, String> playerAttempts = passwordAttempts.get(playerUUID);
        if (playerAttempts != null) {
            String locationKey = getLocationKey(chestLocation);
            playerAttempts.remove(locationKey);
            if (playerAttempts.isEmpty()) {
                passwordAttempts.remove(playerUUID);
            }
        }
    }

    public void clearPasswordAttempt(UUID playerUUID) {
        passwordAttempts.remove(playerUUID);
    }
}
