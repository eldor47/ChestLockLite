package com.chestlocklite.gui;

import com.chestlocklite.ChestLockLitePlugin;
import com.chestlocklite.managers.DatabaseManager;
import com.chestlocklite.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class TrustedPlayersGUI implements Listener {

    private final ChestLockLitePlugin plugin;
    private final Map<UUID, org.bukkit.Location> openGUIs = new HashMap<>();

    public TrustedPlayersGUI(ChestLockLitePlugin plugin) {
        this.plugin = plugin;
    }

    public void openTrustedPlayersGUI(Player player, org.bukkit.Location chestLocation) {
        DatabaseManager.LockData lock = plugin.getLockManager().getLockInfo(chestLocation);
        
        if (lock == null) {
            player.sendMessage(MessageUtils.colorize("&cThis chest is not locked!"));
            return;
        }

        if (!lock.getOwnerUUID().equals(player.getUniqueId())) {
            player.sendMessage(MessageUtils.colorize("&cYou are not the owner of this chest!"));
            return;
        }

        Inventory gui = Bukkit.createInventory(null, 54, MessageUtils.colorize("&6Trusted Players"));

        try {
            List<DatabaseManager.TrustedPlayerData> trustedPlayers = 
                plugin.getDatabaseManager().getTrustedPlayers(chestLocation);

            int slot = 0;
            for (DatabaseManager.TrustedPlayerData trusted : trustedPlayers) {
                if (slot >= 45) break; // Max 45 slots for players (5 rows)

                boolean isOnline = Bukkit.getOfflinePlayer(trusted.getUUID()).isOnline();
                String status = isOnline ? "&a[Online]" : "&7[Offline]";
                
                ItemStack skull = createPlayerHead(trusted.getUUID(), trusted.getName());
                ItemMeta meta = skull.getItemMeta();
                if (meta != null) {
                    meta.setDisplayName(MessageUtils.colorize("&e" + trusted.getName() + " " + status));
                    meta.setLore(Arrays.asList(
                        "&7Click to remove trust",
                        "&7This player can access your chest"
                    ).stream().map(MessageUtils::colorize).collect(Collectors.toList()));
                    skull.setItemMeta(meta);
                }
                
                gui.setItem(slot, skull);
                slot++;
            }
        } catch (java.sql.SQLException e) {
            plugin.getLogger().warning("Error loading trusted players: " + e.getMessage());
            player.sendMessage(MessageUtils.colorize("&cError loading trusted players!"));
            return;
        }

        // Add Trust Player button
        gui.setItem(45, createItem(Material.EMERALD, 
            "&aAdd Trusted Player", 
            Arrays.asList("&7Click to add a player",
                          "&7to your trusted list",
                          "&7",
                          "&7Use command:",
                          "&e/cl trust <player>")));

        // Back button
        gui.setItem(49, createItem(Material.ARROW, 
            "&7Back to Main Menu", 
            Arrays.asList("&7Click to return to",
                          "&7the chest lock menu")));

        // Close button
        gui.setItem(53, createItem(Material.BARRIER, 
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
        if (!event.getView().getTitle().equals(MessageUtils.colorize("&6Trusted Players"))) {
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
        int slot = event.getSlot();

        // Close button
        if (clickedType == Material.BARRIER && slot == 53) {
            player.closeInventory();
            return;
        }

        // Back button
        if (clickedType == Material.ARROW && slot == 49) {
            player.closeInventory();
            plugin.getLockGUI().openGUI(player, chestLocation);
            return;
        }

        // Add Trust Player button
        if (clickedType == Material.EMERALD && slot == 45) {
            player.closeInventory();
            player.sendMessage(MessageUtils.colorize("&7Use &e/cl trust <player> &7to add a trusted player!"));
            player.sendMessage(MessageUtils.colorize("&7Or Shift + Right-click the chest and use the GUI."));
            return;
        }

        // Remove trusted player (player head)
        if (clickedType == Material.PLAYER_HEAD && slot < 45) {
            ItemMeta meta = clicked.getItemMeta();
            if (meta != null && meta.getDisplayName() != null) {
                String displayName = meta.getDisplayName();
                // Extract player name from display name (format: "&ePlayerName &a[Online]")
                String playerName = ChatColor.stripColor(displayName).split(" ")[0];
                
                Player targetPlayer = Bukkit.getPlayer(playerName);
                UUID targetUUID = null;
                
                if (targetPlayer != null) {
                    targetUUID = targetPlayer.getUniqueId();
                } else {
                    // Try to find UUID from database
                    try {
                        List<DatabaseManager.TrustedPlayerData> trustedPlayers = 
                            plugin.getDatabaseManager().getTrustedPlayers(chestLocation);
                        for (DatabaseManager.TrustedPlayerData trusted : trustedPlayers) {
                            if (trusted.getName().equalsIgnoreCase(playerName)) {
                                targetUUID = trusted.getUUID();
                                break;
                            }
                        }
                    } catch (java.sql.SQLException e) {
                        plugin.getLogger().warning("Error finding trusted player: " + e.getMessage());
                    }
                }

                if (targetUUID == null) {
                    player.sendMessage(MessageUtils.colorize("&cCould not find player!"));
                    return;
                }

                try {
                    plugin.getDatabaseManager().removeTrustedPlayer(chestLocation, targetUUID);
                    player.sendMessage(MessageUtils.colorize("&aRemoved trust from &e" + playerName + "&a!"));
                    
                    // Refresh GUI
                    player.closeInventory();
                    openTrustedPlayersGUI(player, chestLocation);
                    
                    if (targetPlayer != null) {
                        targetPlayer.sendMessage(MessageUtils.colorize("&e" + player.getName() + " &chas removed your trust on one of their chests."));
                    }
                } catch (java.sql.SQLException e) {
                    plugin.getLogger().warning("Error removing trusted player: " + e.getMessage());
                    player.sendMessage(MessageUtils.colorize("&cAn error occurred. Please try again."));
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

    private ItemStack createItem(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(MessageUtils.colorize(name));
            meta.setLore(lore.stream().map(MessageUtils::colorize).collect(Collectors.toList()));
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack createPlayerHead(UUID uuid, String name) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        if (meta != null) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
            meta.setOwningPlayer(offlinePlayer);
            skull.setItemMeta(meta);
        }
        return skull;
    }

    private void fillEmptySlots(Inventory inventory) {
        ItemStack glass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = glass.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(" ");
            glass.setItemMeta(meta);
        }
        
        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, glass);
            }
        }
    }
}

