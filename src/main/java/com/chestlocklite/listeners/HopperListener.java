package com.chestlocklite.listeners;

import com.chestlocklite.ChestLockLitePlugin;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class HopperListener implements Listener {

    private final ChestLockLitePlugin plugin;

    public HopperListener(ChestLockLitePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = false)
    public void onInventoryMoveItem(InventoryMoveItemEvent event) {
        // Check if hopper support is enabled
        if (!plugin.getConfigManager().isHopperSupportEnabled()) {
            return;
        }

        Inventory source = event.getSource();
        Inventory destination = event.getDestination();
        
        // Check if source is a locked container
        InventoryHolder sourceHolder = source.getHolder();
        Block sourceBlock = null;
        
        if (sourceHolder instanceof org.bukkit.block.BlockState) {
            sourceBlock = ((org.bukkit.block.BlockState) sourceHolder).getBlock();
        } else if (sourceHolder instanceof org.bukkit.block.DoubleChest) {
            org.bukkit.block.DoubleChest doubleChest = (org.bukkit.block.DoubleChest) sourceHolder;
            if (doubleChest.getLeftSide() instanceof org.bukkit.block.Chest) {
                sourceBlock = ((org.bukkit.block.Chest) doubleChest.getLeftSide()).getBlock();
            }
        }
        
        if (sourceBlock != null && plugin.getLockManager().isLockableContainer(sourceBlock)) {
            org.bukkit.Location containerLocation = plugin.getLockManager().getPrimaryChestLocation(sourceBlock);
            com.chestlocklite.managers.DatabaseManager.LockData lock = 
                plugin.getLockManager().getLockInfo(containerLocation);
            
            if (lock != null && !lock.isHopperEnabled()) {
                // Container is locked and hoppers are disabled - prevent extraction
                event.setCancelled(true);
                return;
            }
        }
        
        // Check if destination is a locked container (prevent hoppers from inserting into locked containers)
        InventoryHolder destHolder = destination.getHolder();
        Block destBlock = null;
        
        if (destHolder instanceof org.bukkit.block.BlockState) {
            destBlock = ((org.bukkit.block.BlockState) destHolder).getBlock();
        } else if (destHolder instanceof org.bukkit.block.DoubleChest) {
            org.bukkit.block.DoubleChest doubleChest = (org.bukkit.block.DoubleChest) destHolder;
            if (doubleChest.getLeftSide() instanceof org.bukkit.block.Chest) {
                destBlock = ((org.bukkit.block.Chest) doubleChest.getLeftSide()).getBlock();
            }
        }
        
        if (destBlock != null && plugin.getLockManager().isLockableContainer(destBlock)) {
            org.bukkit.Location containerLocation = plugin.getLockManager().getPrimaryChestLocation(destBlock);
            com.chestlocklite.managers.DatabaseManager.LockData lock = 
                plugin.getLockManager().getLockInfo(containerLocation);
            
            if (lock != null && !lock.isHopperEnabled()) {
                // Container is locked and hoppers are disabled - prevent insertion
                event.setCancelled(true);
                return;
            }
        }
    }
}

