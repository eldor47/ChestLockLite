package com.chestlocklite.listeners;

import com.chestlocklite.ChestLockLitePlugin;
import com.chestlocklite.utils.MessageUtils;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockPlaceListener implements Listener {

    private final ChestLockLitePlugin plugin;

    public BlockPlaceListener(ChestLockLitePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        // Check if auto-lock on place is enabled
        if (!plugin.getConfigManager().isAutoLockOnPlace()) {
            return;
        }

        // Check if locking is allowed
        if (!plugin.getConfigManager().isLockingAllowed()) {
            return;
        }

        Player player = event.getPlayer();
        Block block = event.getBlock();

        // Check if placed block is a chest
        if (!plugin.getLockManager().isChest(block)) {
            return;
        }

        // Check if player has permission to lock chests
        if (!player.hasPermission("chestlocklite.lock")) {
            return;
        }

        // Get primary chest location (handles double chests)
        org.bukkit.Location chestLocation = plugin.getLockManager().getPrimaryChestLocation(block);

        // Check if chest is already locked
        if (plugin.getLockManager().getLockInfo(chestLocation) != null) {
            return; // Already locked, skip
        }

        // Check chest limit
        try {
            int currentChestCount = plugin.getDatabaseManager().getChestCount(player.getUniqueId());
            int maxChests = plugin.getConfigManager().getMaxChestsPerPlayer();
            
            if (currentChestCount >= maxChests) {
                player.sendMessage(MessageUtils.colorize(
                    plugin.getConfigManager().getMaxChestsReachedMessage(maxChests)));
                return;
            }
        } catch (java.sql.SQLException e) {
            plugin.getLogger().warning("Error checking chest count for auto-lock: " + e.getMessage());
            return;
        }

        // Auto-lock the chest
        org.bukkit.Bukkit.getScheduler().runTask(plugin, () -> {
            if (plugin.getLockManager().lockChest(chestLocation, player)) {
                player.sendMessage(MessageUtils.colorize(
                    plugin.getConfigManager().getLockSuccessMessage()));
                if (plugin.getConfigManager().isPasswordsAllowed()) {
                    player.sendMessage(MessageUtils.colorize(
                        "&7Tip: Use &e/cl password <password> &7to add password protection!"));
                }
            }
        });
    }
}

