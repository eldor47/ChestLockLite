package com.chestlocklite.gui;

import com.chestlocklite.ChestLockLitePlugin;
import com.chestlocklite.managers.DatabaseManager;
import com.chestlocklite.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LockGUI implements Listener {

    private final ChestLockLitePlugin plugin;
    private final Map<UUID, org.bukkit.Location> openGUIs = new HashMap<>();

    public LockGUI(ChestLockLitePlugin plugin) {
        this.plugin = plugin;
    }

    public void openGUI(Player player, org.bukkit.Location chestLocation) {
        DatabaseManager.LockData lock = plugin.getLockManager().getLockInfo(chestLocation);
        
        Inventory gui = Bukkit.createInventory(null, 27, MessageUtils.colorize("&6Chest Lock Menu"));
        
        // Check if chest is locked
        boolean isLocked = lock != null;
        boolean isOwner = isLocked && lock.getOwnerUUID().equals(player.getUniqueId());
        boolean isAdmin = player.hasPermission("chestlocklite.admin");
        
        // Lock/Unlock button
        if (isLocked) {
            if (isOwner || isAdmin) {
                gui.setItem(11, createItem(Material.TRIPWIRE_HOOK, 
                    "&cUnlock Chest", 
                    Arrays.asList("&7Click to unlock this chest", 
                                  "&7Only you can access it")));
            } else {
                gui.setItem(11, createItem(Material.BARRIER, 
                    "&cChest is Locked", 
                    Arrays.asList("&7Owner: &e" + lock.getOwnerName(),
                                  "&7You do not have access")));
            }
        } else {
            gui.setItem(11, createItem(Material.IRON_BLOCK, 
                "&aLock Chest", 
                Arrays.asList("&7Click to lock this chest", 
                              "&7Only you will be able to open it")));
        }
        
        // Password button
        if (isLocked && isOwner) {
            if (lock.hasPassword()) {
                gui.setItem(13, createItem(Material.ENDER_EYE, 
                    "&eRemove Password", 
                    Arrays.asList("&7Click to remove password", 
                                  "&7Current: &aSet")));
            } else {
                gui.setItem(13, createItem(Material.ENDER_PEARL, 
                    "&eSet Password", 
                    Arrays.asList("&7Click to set a password", 
                                  "&7Anyone with password can unlock")));
            }
        } else if (isLocked && !isOwner && lock.hasPassword()) {
            gui.setItem(13, createItem(Material.ENDER_EYE, 
                "&eEnter Password", 
                Arrays.asList("&7Click to enter password", 
                              "&7to unlock this chest")));
        } else if (!isLocked) {
            gui.setItem(13, createItem(Material.GRAY_DYE, 
                "&7Password Protection", 
                Arrays.asList("&7Lock the chest first", 
                              "&7to enable passwords")));
        }
        
        // Trusted Players button (owner only)
        if (isLocked && isOwner) {
            try {
                int trustedCount = plugin.getDatabaseManager().getTrustedPlayers(chestLocation).size();
                gui.setItem(14, createItem(Material.PLAYER_HEAD, 
                    "&eTrusted Players &7(" + trustedCount + ")", 
                    Arrays.asList("&7Click to manage trusted players",
                                  "&7Trusted players can access this chest",
                                  "&7Current: &e" + trustedCount + " player(s)")));
            } catch (java.sql.SQLException e) {
                plugin.getLogger().warning("Error loading trusted players count: " + e.getMessage());
                gui.setItem(14, createItem(Material.PLAYER_HEAD, 
                    "&eTrusted Players", 
                    Arrays.asList("&7Click to manage trusted players",
                                  "&7Trusted players can access this chest")));
            }
        } else if (isLocked && !isOwner) {
            try {
                boolean isTrusted = plugin.getDatabaseManager().isTrustedPlayer(chestLocation, player.getUniqueId());
                if (isTrusted) {
                    gui.setItem(14, createItem(Material.PLAYER_HEAD, 
                        "&aYou are Trusted", 
                        Arrays.asList("&7You have access to this chest",
                                      "&7Owner: &e" + lock.getOwnerName())));
                }
            } catch (java.sql.SQLException e) {
                // Ignore error for non-owners
            }
        }
        
        // Info button
        gui.setItem(15, createItem(Material.BOOK, 
            "&bChest Information", 
            Arrays.asList("&7Click to view lock info")));
        
        // Admin override button (admin only)
        if (isAdmin && isLocked && !isOwner) {
            gui.setItem(22, createItem(Material.COMMAND_BLOCK, 
                "&cAdmin: Force Unlock", 
                Arrays.asList("&7Click to force unlock", 
                              "&7this chest (Admin only)")));
        }
        
        // Close button
        gui.setItem(18, createItem(Material.BARRIER, 
            "&cClose", 
            Arrays.asList("&7Click to close this menu")));
        
        // Fill empty slots
        fillEmptySlots(gui);
        
        player.openInventory(gui);
        openGUIs.put(player.getUniqueId(), chestLocation);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        
        // Check if this is our GUI
        if (!event.getView().getTitle().equals(MessageUtils.colorize("&6Chest Lock Menu"))) {
            return;
        }

        event.setCancelled(true);
        
        if (!openGUIs.containsKey(player.getUniqueId())) {
            return;
        }

        org.bukkit.Location chestLocation = openGUIs.get(player.getUniqueId());
        ItemStack clicked = event.getCurrentItem();

        if (clicked == null || clicked.getType() == Material.AIR) {
            return;
        }

        Material clickedType = clicked.getType();
        
        // Close button
        if (clickedType == Material.BARRIER && event.getSlot() == 18) {
            player.closeInventory();
            return;
        }

        DatabaseManager.LockData lock = plugin.getLockManager().getLockInfo(chestLocation);
        boolean isLocked = lock != null;
        boolean isOwner = isLocked && lock.getOwnerUUID().equals(player.getUniqueId());
        boolean isAdmin = player.hasPermission("chestlocklite.admin");

        // Lock/Unlock button
        if (clickedType == Material.IRON_BLOCK && event.getSlot() == 11) {
            // Lock chest
            if (plugin.getLockManager().lockChest(chestLocation, player)) {
                player.sendMessage(MessageUtils.colorize(
                    plugin.getConfigManager().getLockSuccessMessage()));
                // Refresh GUI to show updated state (now locked)
                refreshGUI(player, chestLocation);
            } else {
                player.sendMessage(MessageUtils.colorize(
                    plugin.getConfigManager().getAlreadyLockedMessage()));
            }
            return;
        }

        if (clickedType == Material.TRIPWIRE_HOOK && event.getSlot() == 11) {
            // Unlock chest
            if (isOwner || isAdmin) {
                if (plugin.getLockManager().unlockChest(chestLocation, player)) {
                    player.sendMessage(MessageUtils.colorize(
                        plugin.getConfigManager().getUnlockSuccessMessage()));
                    // Refresh GUI to show updated state (now unlocked)
                    refreshGUI(player, chestLocation);
                } else {
                    player.sendMessage(MessageUtils.colorize("&cFailed to unlock chest!"));
                }
            }
            return;
        }

        // Password button
        if (clickedType == Material.ENDER_PEARL && event.getSlot() == 13) {
            // Set password
            if (isOwner) {
                player.closeInventory();
                // Open password input GUI
                plugin.getPasswordInputGUI().openPasswordInput(player, chestLocation);
            }
            return;
        }

        if (clickedType == Material.ENDER_EYE && event.getSlot() == 13) {
            if (isOwner) {
                // Owner removing password
                if (plugin.getLockManager().removePassword(chestLocation, player)) {
                    player.sendMessage(MessageUtils.colorize(
                        plugin.getConfigManager().getPasswordRemovedMessage()));
                    // Refresh GUI to show updated state (password removed)
                    refreshGUI(player, chestLocation);
                } else {
                    player.sendMessage(MessageUtils.colorize("&cFailed to remove password!"));
                }
            } else if (isLocked && lock.hasPassword()) {
                // Non-owner entering password to unlock
                player.closeInventory();
                // Open password input GUI for entering password
                plugin.getPasswordInputGUI().openPasswordInput(player, chestLocation, false);
            }
            return;
        }

        // Trusted Players button
        if (clickedType == Material.PLAYER_HEAD && event.getSlot() == 14) {
            if (isOwner && isLocked) {
                // Open trusted players management GUI
                player.closeInventory();
                plugin.getTrustedPlayersGUI().openTrustedPlayersGUI(player, chestLocation);
            }
            return;
        }

        // Info button
        if (clickedType == Material.BOOK && event.getSlot() == 15) {
            player.closeInventory();
            showInfo(player, chestLocation);
            return;
        }

        // Admin force unlock
        if (clickedType == Material.COMMAND_BLOCK && event.getSlot() == 22) {
            if (isAdmin && isLocked && !isOwner) {
                player.closeInventory();
                
                if (plugin.getLockManager().unlockChest(chestLocation, player)) {
                    player.sendMessage(MessageUtils.colorize("&a[Admin] Chest lock cleared!"));
                    plugin.getLogger().info("Admin " + player.getName() + " cleared lock at " + 
                        chestLocation.getWorld().getName() + " " + chestLocation.getBlockX() + "," + 
                        chestLocation.getBlockY() + "," + chestLocation.getBlockZ() + 
                        " (Owner: " + lock.getOwnerName() + ")");
                } else {
                    player.sendMessage(MessageUtils.colorize("&cFailed to clear chest lock!"));
                }
            }
            return;
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getPlayer() instanceof Player) {
            openGUIs.remove(event.getPlayer().getUniqueId());
        }
    }

    private void showInfo(Player player, org.bukkit.Location chestLocation) {
        DatabaseManager.LockData lock = plugin.getLockManager().getLockInfo(chestLocation);
        
        if (lock == null) {
            player.sendMessage(MessageUtils.colorize("&7This chest is &aunlocked&7."));
            return;
        }

        player.sendMessage(MessageUtils.colorize("&6=== Chest Lock Info ==="));
        player.sendMessage(MessageUtils.colorize("&eOwner: &f" + lock.getOwnerName()));
        
        if (lock.getOwnerUUID().equals(player.getUniqueId())) {
            player.sendMessage(MessageUtils.colorize("&7This is your chest."));
            if (lock.hasPassword()) {
                player.sendMessage(MessageUtils.colorize("&7Password: &aSet"));
            } else {
                player.sendMessage(MessageUtils.colorize("&7Password: &cNot set"));
            }
        } else {
            if (lock.hasPassword()) {
                player.sendMessage(MessageUtils.colorize("&7Password Protected: &aYes"));
                player.sendMessage(MessageUtils.colorize("&7Use &e/cl password <password> &7to unlock"));
            } else {
                player.sendMessage(MessageUtils.colorize("&7Password Protected: &cNo"));
                player.sendMessage(MessageUtils.colorize("&cOnly the owner can open this chest."));
            }
        }
    }

    private ItemStack createItem(Material material, String name, java.util.List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(MessageUtils.colorize(name));
            meta.setLore(lore.stream().map(MessageUtils::colorize).collect(java.util.stream.Collectors.toList()));
            item.setItemMeta(meta);
        }
        return item;
    }

    /**
     * Refresh the GUI to reflect current lock state without closing
     */
    private void refreshGUI(Player player, org.bukkit.Location chestLocation) {
        // Only refresh if player has GUI open
        if (!player.getOpenInventory().getTitle().equals(MessageUtils.colorize("&6Chest Lock Menu"))) {
            return;
        }
        
        // Get current lock state
        DatabaseManager.LockData lock = plugin.getLockManager().getLockInfo(chestLocation);
        boolean isLocked = lock != null;
        boolean isOwner = isLocked && lock.getOwnerUUID().equals(player.getUniqueId());
        boolean isAdmin = player.hasPermission("chestlocklite.admin");
        
        Inventory gui = player.getOpenInventory().getTopInventory();
        
        // Update Lock/Unlock button
        if (isLocked) {
            if (isOwner || isAdmin) {
                gui.setItem(11, createItem(Material.TRIPWIRE_HOOK, 
                    "&cUnlock Chest", 
                    Arrays.asList("&7Click to unlock this chest", 
                                  "&7Only you can access it")));
            } else {
                gui.setItem(11, createItem(Material.BARRIER, 
                    "&cChest is Locked", 
                    Arrays.asList("&7Owner: &e" + lock.getOwnerName(),
                                  "&7You do not have access")));
            }
        } else {
            gui.setItem(11, createItem(Material.IRON_BLOCK, 
                "&aLock Chest", 
                Arrays.asList("&7Click to lock this chest", 
                              "&7Only you will be able to open it")));
        }
        
        // Update Password button
        if (isLocked && isOwner) {
            if (lock.hasPassword()) {
                gui.setItem(13, createItem(Material.ENDER_EYE, 
                    "&eRemove Password", 
                    Arrays.asList("&7Click to remove password", 
                                  "&7Current: &aSet")));
            } else {
                gui.setItem(13, createItem(Material.ENDER_PEARL, 
                    "&eSet Password", 
                    Arrays.asList("&7Click to set a password", 
                                  "&7Anyone with password can unlock")));
            }
        } else if (isLocked && !isOwner && lock.hasPassword()) {
            gui.setItem(13, createItem(Material.ENDER_EYE, 
                "&eEnter Password", 
                Arrays.asList("&7Click to enter password", 
                              "&7to unlock this chest")));
        } else if (!isLocked) {
            gui.setItem(13, createItem(Material.GRAY_DYE, 
                "&7Password Protection", 
                Arrays.asList("&7Lock the chest first", 
                              "&7to enable passwords")));
        }
        
        // Update Admin override button
        if (isAdmin && isLocked && !isOwner) {
            gui.setItem(22, createItem(Material.COMMAND_BLOCK, 
                "&cAdmin: Force Unlock", 
                Arrays.asList("&7Click to force unlock", 
                              "&7this chest (Admin only)")));
        } else {
            gui.setItem(22, null);
            fillEmptySlots(gui); // Refill empty slots
        }
    }

    private void fillEmptySlots(Inventory inventory) {
        ItemStack filler = createItem(Material.GRAY_STAINED_GLASS_PANE, " ", Arrays.asList());
        
        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, filler);
            }
        }
    }
}

