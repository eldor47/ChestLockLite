package com.chestlocklite.gui;

import com.chestlocklite.ChestLockLitePlugin;
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

public class PasswordInputGUI implements Listener {

    private final ChestLockLitePlugin plugin;
    private final Map<UUID, PasswordInputData> passwordInputs = new HashMap<>();
    // Track password attempt cooldowns per player (last attempt timestamp)
    private final Map<UUID, Long> passwordCooldowns = new HashMap<>();

    private static class PasswordInputData {
        org.bukkit.Location chestLocation;
        StringBuilder passwordBuilder;
        boolean isSettingPassword; // true = setting password, false = entering password
        
        PasswordInputData(org.bukkit.Location chestLocation, boolean isSettingPassword) {
            this.chestLocation = chestLocation;
            this.passwordBuilder = new StringBuilder();
            this.isSettingPassword = isSettingPassword;
        }
    }

    public PasswordInputGUI(ChestLockLitePlugin plugin) {
        this.plugin = plugin;
    }

    public void openPasswordInput(Player player, org.bukkit.Location chestLocation) {
        openPasswordInput(player, chestLocation, true);
    }
    
    public void openPasswordInput(Player player, org.bukkit.Location chestLocation, boolean isSettingPassword) {
        String title = isSettingPassword ? "&6Set Password" : "&6Enter Password";
        Inventory gui = Bukkit.createInventory(null, 54, MessageUtils.colorize(title));
        
        passwordInputs.put(player.getUniqueId(), new PasswordInputData(chestLocation, isSettingPassword));
        
        updatePasswordDisplay(player, passwordInputs.get(player.getUniqueId()), gui);
        
        player.openInventory(gui);
    }
    
    private void updatePasswordDisplay(Player player, PasswordInputData data, Inventory gui) {
        String currentPassword = data.passwordBuilder.length() > 0 ? data.passwordBuilder.toString() : "(empty)";
        String titleText = data.isSettingPassword ? "Current Password" : "Enter Password";
        
        // Large password display in center (slot 4)
        gui.setItem(4, createItem(Material.NAME_TAG, 
            "&6&l" + titleText, 
            Arrays.asList("&f&l" + currentPassword,
                          "&7",
                          data.isSettingPassword ? 
                              ("&7Length: &e" + data.passwordBuilder.length() + 
                               " &7/ &e" + plugin.getConfigManager().getMaxPasswordLength() +
                               "\n&7Min: &e" + plugin.getConfigManager().getMinPasswordLength() + 
                               " &7Max: &e" + plugin.getConfigManager().getMaxPasswordLength() + " characters" +
                               (data.passwordBuilder.length() >= plugin.getConfigManager().getMinPasswordLength() && 
                                data.passwordBuilder.length() <= plugin.getConfigManager().getMaxPasswordLength() 
                                ? "\n&a✓ Valid password length" 
                                : "\n&c✗ Invalid password length")) :
                              "&7Enter the password to unlock")));
        
        // Character buttons (0-9) - slots 9-18
        for (int i = 0; i < 10; i++) {
            gui.setItem(9 + i, createItem(Material.WHITE_STAINED_GLASS_PANE, 
                "&b" + i, 
                Arrays.asList("&7Click to add &e" + i + " &7to password")));
        }
        
        // Letters A-Z - slots 19-44 (moved from 18 to avoid conflict with number 9)
        char[] letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
        for (int i = 0; i < Math.min(26, 26); i++) {
            int slot = 19 + i;
            if (slot < 45) {
                gui.setItem(slot, createItem(Material.LIGHT_BLUE_STAINED_GLASS_PANE, 
                    "&b" + letters[i], 
                    Arrays.asList("&7Click to add &e" + letters[i] + " &7to password")));
            }
        }
        
        // Control buttons
        gui.setItem(45, createItem(Material.REDSTONE, 
            "&cClear All", 
            Arrays.asList("&7Clear the entire password")));
        
        gui.setItem(46, createItem(Material.FEATHER, 
            "&eBackspace", 
            Arrays.asList("&7Remove last character")));
        
        String confirmText = data.isSettingPassword ? "Set this password" : "Unlock with this password";
        gui.setItem(49, createItem(Material.EMERALD_BLOCK, 
            "&aConfirm Password", 
            Arrays.asList("&7" + confirmText,
                          "&7Current: &e" + (data.passwordBuilder.length() > 0 ? currentPassword : "(empty)"),
                          "&7Length: &e" + data.passwordBuilder.length(),
                          data.isSettingPassword && 
                          data.passwordBuilder.length() >= plugin.getConfigManager().getMinPasswordLength() && 
                          data.passwordBuilder.length() <= plugin.getConfigManager().getMaxPasswordLength() 
                          ? "&a✓ Valid password length" 
                          : (data.isSettingPassword ? "&c✗ Invalid password length" : ""))));
        
        gui.setItem(53, createItem(Material.BARRIER, 
            "&cCancel", 
            Arrays.asList("&7Cancel and close")));
        
        // Fill empty slots
        fillEmptySlots(gui);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        
        String title = event.getView().getTitle();
        if (!title.equals(MessageUtils.colorize("&6Set Password")) && 
            !title.equals(MessageUtils.colorize("&6Enter Password"))) {
            return;
        }

        event.setCancelled(true);
        
        if (!passwordInputs.containsKey(player.getUniqueId())) {
            return;
        }

        PasswordInputData data = passwordInputs.get(player.getUniqueId());
        ItemStack clicked = event.getCurrentItem();

        if (clicked == null || clicked.getType() == Material.AIR) {
            return;
        }

        int slot = event.getSlot();
        
        // Cancel button
        if (slot == 53) {
            player.closeInventory();
            passwordInputs.remove(player.getUniqueId());
            player.sendMessage(MessageUtils.colorize("&7Password input cancelled."));
            return;
        }
        
        // Confirm button
        if (slot == 49) {
            String password = data.passwordBuilder.toString();
            
            if (password.isEmpty()) {
                player.sendMessage(MessageUtils.colorize("&cPassword cannot be empty!"));
                updatePasswordDisplay(player, data);
                return;
            }

            // Check cooldown (only for entering password, not setting)
            if (!data.isSettingPassword) {
                UUID playerId = player.getUniqueId();
                int remainingSeconds = plugin.getPasswordCooldownManager().checkCooldown(playerId);
                
                if (remainingSeconds > 0) {
                    player.sendMessage(MessageUtils.colorize(
                        plugin.getConfigManager().getPasswordCooldownMessage(remainingSeconds)));
                    updatePasswordDisplay(player, data);
                    return;
                }
                
            }

            player.closeInventory();
            passwordInputs.remove(player.getUniqueId());

            if (data.isSettingPassword) {
                // Setting password (owner)
                int minLength = plugin.getConfigManager().getMinPasswordLength();
                int maxLength = plugin.getConfigManager().getMaxPasswordLength();
                
                if (password.length() < minLength || password.length() > maxLength) {
                    player.sendMessage(MessageUtils.colorize(
                        plugin.getConfigManager().getInvalidPasswordLengthMessage(minLength, maxLength)));
                    return;
                }

                if (plugin.getLockManager().setPassword(data.chestLocation, player, password)) {
                    player.sendMessage(MessageUtils.colorize(
                        plugin.getConfigManager().getPasswordSetMessage()));
                } else {
                    player.sendMessage(MessageUtils.colorize("&cFailed to set password!"));
                }
            } else {
                // Entering password (non-owner trying to unlock)
                com.chestlocklite.managers.DatabaseManager.LockData lock = 
                    plugin.getLockManager().getLockInfo(data.chestLocation);
                
                if (lock == null || !lock.hasPassword()) {
                    player.sendMessage(MessageUtils.colorize("&cThis chest doesn't have a password!"));
                    return;
                }
                
                if (com.chestlocklite.utils.PasswordHasher.verifyPassword(password, lock.getPassword())) {
                    // Correct password - cache it and clear cooldown
                    plugin.getChestListener().setPasswordAttempt(player.getUniqueId(), data.chestLocation, password);
                    plugin.getPasswordCooldownManager().clearCooldown(player.getUniqueId());
                    player.sendMessage(MessageUtils.colorize("&aPassword accepted! You can now open this chest."));
                } else {
                    // Wrong password - set cooldown
                    plugin.getPasswordCooldownManager().setCooldown(player.getUniqueId());
                    player.sendMessage(MessageUtils.colorize(
                        plugin.getConfigManager().getWrongPasswordMessage()));
                }
            }
            return;
        }
        
        // Clear button
        if (slot == 45) {
            data.passwordBuilder.setLength(0);
            updatePasswordDisplay(player, data);
            return;
        }
        
        // Backspace button
        if (slot == 46) {
            if (data.passwordBuilder.length() > 0) {
                data.passwordBuilder.setLength(data.passwordBuilder.length() - 1);
            }
            updatePasswordDisplay(player, data);
            return;
        }
        
        // Number buttons (slots 9-18 for numbers 0-9)
        if (slot >= 9 && slot <= 18) {
            int number = slot - 9;
            if (data.passwordBuilder.length() < plugin.getConfigManager().getMaxPasswordLength()) {
                data.passwordBuilder.append(number);
                updatePasswordDisplay(player, data);
            } else {
                player.sendMessage(MessageUtils.colorize("&cPassword is too long!"));
            }
            return;
        }
        
        // Letter buttons (slots 19-44 for letters A-Z)
        if (slot >= 19 && slot < 45) {
            int letterIndex = slot - 19;
            if (letterIndex < 26) {
                char letter = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".charAt(letterIndex);
                if (data.passwordBuilder.length() < plugin.getConfigManager().getMaxPasswordLength()) {
                    data.passwordBuilder.append(letter);
                    updatePasswordDisplay(player, data);
                } else {
                    player.sendMessage(MessageUtils.colorize("&cPassword is too long!"));
                }
            }
            return;
        }
    }

    private void updatePasswordDisplay(Player player, PasswordInputData data) {
        if (!player.getOpenInventory().getTopInventory().getViewers().contains(player)) {
            return;
        }
        
        Inventory gui = player.getOpenInventory().getTopInventory();
        updatePasswordDisplay(player, data, gui);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getInventory().getType() != org.bukkit.event.inventory.InventoryType.CHEST) {
            return;
        }

        String title = event.getView().getTitle();
        if (!title.equals(MessageUtils.colorize("&6Set Password")) && 
            !title.equals(MessageUtils.colorize("&6Enter Password"))) {
            return;
        }

        if (event.getPlayer() instanceof Player) {
            Player player = (Player) event.getPlayer();
            if (passwordInputs.containsKey(player.getUniqueId())) {
                passwordInputs.remove(player.getUniqueId());
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

    private void fillEmptySlots(Inventory inventory) {
        ItemStack filler = createItem(Material.GRAY_STAINED_GLASS_PANE, " ", Arrays.asList());
        
        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, filler);
            }
        }
    }
}
