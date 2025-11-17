package com.chestlocklite.commands;

import com.chestlocklite.ChestLockLitePlugin;
import com.chestlocklite.managers.DatabaseManager;
import com.chestlocklite.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ChestLockCommand implements CommandExecutor, TabCompleter {

    private final ChestLockLitePlugin plugin;

    public ChestLockCommand(ChestLockLitePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(MessageUtils.colorize("&cThis command can only be used by players!"));
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            sendHelp(player);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "lock":
                if (!player.hasPermission("chestlocklite.lock")) {
                    player.sendMessage(MessageUtils.colorize("&cYou don't have permission to lock chests!"));
                    return true;
                }
                handleLock(player);
                break;
            case "unlock":
                if (!player.hasPermission("chestlocklite.unlock")) {
                    player.sendMessage(MessageUtils.colorize("&cYou don't have permission to unlock chests!"));
                    return true;
                }
                handleUnlock(player);
                break;
            case "password":
                if (!player.hasPermission("chestlocklite.password")) {
                    player.sendMessage(MessageUtils.colorize("&cYou don't have permission to use passwords!"));
                    return true;
                }
                handlePassword(player, args);
                break;
            case "removepassword":
            case "removepwd":
                if (!player.hasPermission("chestlocklite.password")) {
                    player.sendMessage(MessageUtils.colorize("&cYou don't have permission to use passwords!"));
                    return true;
                }
                handleRemovePassword(player);
                break;
            case "info":
                if (!player.hasPermission("chestlocklite.info")) {
                    player.sendMessage(MessageUtils.colorize("&cYou don't have permission to view lock info!"));
                    return true;
                }
                handleInfo(player);
                break;
            case "gui":
            case "menu":
                if (!player.hasPermission("chestlocklite.gui")) {
                    player.sendMessage(MessageUtils.colorize("&cYou don't have permission to use the GUI!"));
                    return true;
                }
                handleGUI(player);
                break;
            case "override":
            case "forceunlock":
            case "clear":
                if (!player.hasPermission("chestlocklite.admin.clear")) {
                    player.sendMessage(MessageUtils.colorize("&cYou don't have permission to clear locks!"));
                    return true;
                }
                handleAdminOverride(player);
                break;
            case "clearall":
                if (!player.hasPermission("chestlocklite.admin.clearall")) {
                    player.sendMessage(MessageUtils.colorize("&cYou don't have permission to clear all locks!"));
                    return true;
                }
                handleClearAll(player, args);
                break;
            case "cleararea":
                if (!player.hasPermission("chestlocklite.admin.cleararea")) {
                    player.sendMessage(MessageUtils.colorize("&cYou don't have permission to clear locks in areas!"));
                    return true;
                }
                handleClearArea(player, args);
                break;
            case "reload":
                if (!player.hasPermission("chestlocklite.admin.reload")) {
                    player.sendMessage(MessageUtils.colorize("&cYou don't have permission to reload the plugin!"));
                    return true;
                }
                handleReload(player);
                break;
            case "trust":
                if (!player.hasPermission("chestlocklite.trust")) {
                    player.sendMessage(MessageUtils.colorize("&cYou don't have permission to trust players!"));
                    return true;
                }
                handleTrust(player, args);
                break;
            case "untrust":
                if (!player.hasPermission("chestlocklite.trust")) {
                    player.sendMessage(MessageUtils.colorize("&cYou don't have permission to untrust players!"));
                    return true;
                }
                handleUntrust(player, args);
                break;
            case "trustedlist":
            case "trusted":
            case "trustlist":
                if (!player.hasPermission("chestlocklite.trust")) {
                    player.sendMessage(MessageUtils.colorize("&cYou don't have permission to view trusted players!"));
                    return true;
                }
                handleTrustedList(player);
                break;
            default:
                sendHelp(player);
                break;
        }

        return true;
    }

    private void sendHelp(Player player) {
        player.sendMessage(MessageUtils.colorize("&6=== ChestLockLite Commands ==="));
        player.sendMessage(MessageUtils.colorize("&e/cl gui &7- Open lock management GUI"));
        player.sendMessage(MessageUtils.colorize("&e/cl lock &7- Lock the chest you're looking at"));
        player.sendMessage(MessageUtils.colorize("&e/cl unlock &7- Unlock your chest"));
        if (plugin.getConfigManager().isPasswordsAllowed()) {
            player.sendMessage(MessageUtils.colorize("&e/cl password <password> &7- Set password on your chest"));
            player.sendMessage(MessageUtils.colorize("&e/cl removepassword &7- Remove password from your chest"));
            player.sendMessage(MessageUtils.colorize("&e/cl password <password> (on locked chest) &7- Unlock with password"));
        }
        player.sendMessage(MessageUtils.colorize("&e/cl info &7- Show lock information"));
        if (player.hasPermission("chestlocklite.trust")) {
            player.sendMessage(MessageUtils.colorize("&e/cl trust <player> &7- Trust a player on your chest"));
            player.sendMessage(MessageUtils.colorize("&e/cl untrust <player> &7- Remove trust from a player"));
            player.sendMessage(MessageUtils.colorize("&e/cl trustedlist &7- List trusted players"));
        }
        if (player.hasPermission("chestlocklite.admin")) {
            player.sendMessage(MessageUtils.colorize("&c=== Admin Commands ==="));
            player.sendMessage(MessageUtils.colorize("&e/cl clear &7- Clear lock on targeted chest"));
            player.sendMessage(MessageUtils.colorize("&e/cl clearall <player> &7- Clear all locks by a player"));
            player.sendMessage(MessageUtils.colorize("&e/cl cleararea <radius> &7- Clear locks in area"));
            player.sendMessage(MessageUtils.colorize("&e/cl reload &7- Reload configuration"));
        }
    }

    private void handleLock(Player player) {
        if (!plugin.getConfigManager().isLockingAllowed()) {
            player.sendMessage(MessageUtils.colorize("&cLocking is currently disabled!"));
            return;
        }

        Block targetBlock = getTargetChest(player);
        if (targetBlock == null) {
            player.sendMessage(MessageUtils.colorize(
                plugin.getConfigManager().getNoChestTargetMessage()));
            return;
        }

        org.bukkit.Location chestLocation = plugin.getLockManager().getPrimaryChestLocation(targetBlock);

        // Check if already locked
        DatabaseManager.LockData existingLock = plugin.getLockManager().getLockInfo(chestLocation);
        if (existingLock != null) {
            if (existingLock.getOwnerUUID().equals(player.getUniqueId())) {
                player.sendMessage(MessageUtils.colorize("&cThis chest is already locked by you!"));
            } else {
                player.sendMessage(MessageUtils.colorize(
                    plugin.getConfigManager().getAlreadyLockedMessage(targetBlock)));
            }
            return;
        }

        // Check chest limit
        try {
            int chestCount = plugin.getDatabaseManager().getChestCount(player.getUniqueId());
            if (chestCount >= plugin.getConfigManager().getMaxChestsPerPlayer()) {
                player.sendMessage(MessageUtils.colorize(
                    plugin.getConfigManager().getMaxChestsReachedMessage(
                        plugin.getConfigManager().getMaxChestsPerPlayer())));
                return;
            }
        } catch (Exception e) {
            plugin.getLogger().severe("Error checking chest count: " + e.getMessage());
            player.sendMessage(MessageUtils.colorize("&cAn error occurred. Please try again."));
            return;
        }

        // Lock the chest
        if (plugin.getLockManager().lockChest(chestLocation, player)) {
            player.sendMessage(MessageUtils.colorize(
                plugin.getConfigManager().getLockSuccessMessage(targetBlock)));
            
            if (plugin.getConfigManager().isPasswordsAllowed()) {
                player.sendMessage(MessageUtils.colorize(
                    "&7Tip: Use &e/cl password <password> &7to add password protection!"));
            }
        } else {
            player.sendMessage(MessageUtils.colorize("&cFailed to lock chest!"));
        }
    }

    private void handleUnlock(Player player) {
        Block targetBlock = getTargetChest(player);
        if (targetBlock == null) {
            player.sendMessage(MessageUtils.colorize(
                plugin.getConfigManager().getNoChestTargetMessage()));
            return;
        }

        org.bukkit.Location chestLocation = plugin.getLockManager().getPrimaryChestLocation(targetBlock);

        DatabaseManager.LockData lock = plugin.getLockManager().getLockInfo(chestLocation);
        if (lock == null) {
            player.sendMessage(MessageUtils.colorize(
                plugin.getConfigManager().getNotLockedMessage(targetBlock)));
            return;
        }

        // Check if player is owner or admin
        if (!lock.getOwnerUUID().equals(player.getUniqueId()) && 
            !player.hasPermission("chestlocklite.admin")) {
            player.sendMessage(MessageUtils.colorize(
                plugin.getConfigManager().getNotOwnerMessage(targetBlock)));
            return;
        }

        if (plugin.getLockManager().unlockChest(chestLocation, player)) {
            player.sendMessage(MessageUtils.colorize(
                plugin.getConfigManager().getUnlockSuccessMessage(targetBlock)));
        } else {
            player.sendMessage(MessageUtils.colorize("&cFailed to unlock chest!"));
        }
    }

    private void handlePassword(Player player, String[] args) {
        if (!plugin.getConfigManager().isPasswordsAllowed()) {
            player.sendMessage(MessageUtils.colorize("&cPassword protection is disabled!"));
            return;
        }

        Block targetBlock = getTargetChest(player);
        if (targetBlock == null) {
            player.sendMessage(MessageUtils.colorize(
                plugin.getConfigManager().getNoChestTargetMessage()));
            return;
        }

        org.bukkit.Location chestLocation = plugin.getLockManager().getPrimaryChestLocation(targetBlock);

        DatabaseManager.LockData lock = plugin.getLockManager().getLockInfo(chestLocation);

        if (lock == null) {
            // Chest not locked, can't set password
            player.sendMessage(MessageUtils.colorize(
                plugin.getConfigManager().getNotLockedMessage(targetBlock)));
            return;
        }

        if (args.length < 2) {
            player.sendMessage(MessageUtils.colorize("&cUsage: /cl password <password>"));
            return;
        }

        String password = args[1];

        // Validate password length
        int minLength = plugin.getConfigManager().getMinPasswordLength();
        int maxLength = plugin.getConfigManager().getMaxPasswordLength();
        if (password.length() < minLength || password.length() > maxLength) {
            player.sendMessage(MessageUtils.colorize(
                plugin.getConfigManager().getInvalidPasswordLengthMessage(minLength, maxLength)));
            return;
        }

        // Check if player is owner (setting password) or trying to unlock
        if (lock.getOwnerUUID().equals(player.getUniqueId())) {
            // Owner setting/changing password
            if (plugin.getLockManager().setPassword(chestLocation, player, password)) {
                player.sendMessage(MessageUtils.colorize(
                    plugin.getConfigManager().getPasswordSetMessage()));
            } else {
                player.sendMessage(MessageUtils.colorize("&cFailed to set password!"));
            }
        } else {
            // Player trying to unlock with password
            if (!lock.hasPassword()) {
                player.sendMessage(MessageUtils.colorize("&cThis chest doesn't have a password set!"));
                return;
            }
            
            // Check cooldown
            UUID playerId = player.getUniqueId();
            int remainingSeconds = plugin.getPasswordCooldownManager().checkCooldown(playerId);
            
            if (remainingSeconds > 0) {
                player.sendMessage(MessageUtils.colorize(
                    plugin.getConfigManager().getPasswordCooldownMessage(remainingSeconds)));
                return;
            }
            
            // Compare passwords using secure hash verification
            if (com.chestlocklite.utils.PasswordHasher.verifyPassword(password, lock.getPassword())) {
                // Correct password - cache it for this specific chest location and clear cooldown
                plugin.getChestListener().setPasswordAttempt(player.getUniqueId(), chestLocation, password);
                plugin.getPasswordCooldownManager().clearCooldown(playerId);
                
                player.sendMessage(MessageUtils.colorize("&aPassword accepted! You can now open this chest."));
            } else {
                // Wrong password - set cooldown
                plugin.getPasswordCooldownManager().setCooldown(playerId);
                player.sendMessage(MessageUtils.colorize(
                    plugin.getConfigManager().getWrongPasswordMessage()));
            }
        }
    }

    private void handleRemovePassword(Player player) {
        if (!plugin.getConfigManager().isPasswordsAllowed()) {
            player.sendMessage(MessageUtils.colorize("&cPassword protection is disabled!"));
            return;
        }

        Block targetBlock = getTargetChest(player);
        if (targetBlock == null) {
            player.sendMessage(MessageUtils.colorize(
                plugin.getConfigManager().getNoChestTargetMessage()));
            return;
        }

        org.bukkit.Location chestLocation = plugin.getLockManager().getPrimaryChestLocation(targetBlock);

        DatabaseManager.LockData lock = plugin.getLockManager().getLockInfo(chestLocation);
        if (lock == null) {
            player.sendMessage(MessageUtils.colorize(
                plugin.getConfigManager().getNotLockedMessage(targetBlock)));
            return;
        }

        if (!lock.getOwnerUUID().equals(player.getUniqueId())) {
            player.sendMessage(MessageUtils.colorize(
                plugin.getConfigManager().getNotOwnerMessage(targetBlock)));
            return;
        }

        if (!lock.hasPassword()) {
            player.sendMessage(MessageUtils.colorize("&cThis chest doesn't have a password!"));
            return;
        }

        if (plugin.getLockManager().removePassword(chestLocation, player)) {
            player.sendMessage(MessageUtils.colorize(
                plugin.getConfigManager().getPasswordRemovedMessage()));
        } else {
            player.sendMessage(MessageUtils.colorize("&cFailed to remove password!"));
        }
    }

    private void handleInfo(Player player) {
        Block targetBlock = getTargetChest(player);
        if (targetBlock == null) {
            player.sendMessage(MessageUtils.colorize(
                plugin.getConfigManager().getNoChestTargetMessage()));
            return;
        }

        org.bukkit.Location chestLocation = plugin.getLockManager().getPrimaryChestLocation(targetBlock);

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

    private void handleGUI(Player player) {
        Block targetBlock = getTargetChest(player);
        if (targetBlock == null) {
            player.sendMessage(MessageUtils.colorize(
                plugin.getConfigManager().getNoChestTargetMessage()));
            return;
        }

        org.bukkit.Location chestLocation = plugin.getLockManager().getPrimaryChestLocation(targetBlock);
        plugin.getLockGUI().openGUI(player, chestLocation);
    }

    private void handleAdminOverride(Player player) {
        if (!player.hasPermission("chestlocklite.admin")) {
            player.sendMessage(MessageUtils.colorize("&cYou don't have permission!"));
            return;
        }

        Block targetBlock = getTargetChest(player);
        if (targetBlock == null) {
            player.sendMessage(MessageUtils.colorize(
                plugin.getConfigManager().getNoChestTargetMessage()));
            return;
        }

        org.bukkit.Location chestLocation = plugin.getLockManager().getPrimaryChestLocation(targetBlock);

        DatabaseManager.LockData lock = plugin.getLockManager().getLockInfo(chestLocation);
        if (lock == null) {
            player.sendMessage(MessageUtils.colorize("&cThis chest is not locked!"));
            return;
        }

        // Force unlock
        if (plugin.getLockManager().unlockChest(chestLocation, player)) {
            player.sendMessage(MessageUtils.colorize("&a[Admin] Chest force unlocked!"));
            player.sendMessage(MessageUtils.colorize("&7Previous owner: &e" + lock.getOwnerName()));
            plugin.getLogger().info("Admin " + player.getName() + " force unlocked chest at " + 
                chestLocation.getWorld().getName() + " " + chestLocation.getBlockX() + "," + 
                chestLocation.getBlockY() + "," + chestLocation.getBlockZ() + 
                " (Owner: " + lock.getOwnerName() + ")");
        } else {
            player.sendMessage(MessageUtils.colorize("&cFailed to force unlock chest!"));
        }
    }

    private void handleClearAll(Player player, String[] args) {
        if (!player.hasPermission("chestlocklite.admin")) {
            player.sendMessage(MessageUtils.colorize("&cYou don't have permission!"));
            return;
        }

        if (args.length < 2) {
            player.sendMessage(MessageUtils.colorize("&cUsage: /cl clearall <player>"));
            player.sendMessage(MessageUtils.colorize("&7Example: /cl clearall PlayerName"));
            return;
        }

        String targetName = args[1];
        Player targetPlayer = Bukkit.getPlayer(targetName);
        UUID targetUUID;

        if (targetPlayer != null) {
            targetUUID = targetPlayer.getUniqueId();
        } else {
            // Try to find UUID from database
            try {
                // Search for player's locks to get their UUID
                String sql = "SELECT DISTINCT owner_uuid FROM chest_locks WHERE owner_name = ? LIMIT 1";
                try (java.sql.PreparedStatement pstmt = plugin.getDatabaseManager().getConnection().prepareStatement(sql)) {
                    pstmt.setString(1, targetName);
                    java.sql.ResultSet rs = pstmt.executeQuery();
                    if (rs.next()) {
                        targetUUID = UUID.fromString(rs.getString("owner_uuid"));
                    } else {
                        player.sendMessage(MessageUtils.colorize("&cPlayer not found: " + targetName));
                        return;
                    }
                }
            } catch (SQLException e) {
                plugin.getLogger().severe("Error finding player UUID: " + e.getMessage());
                player.sendMessage(MessageUtils.colorize("&cError finding player. Please use online player name."));
                return;
            }
        }

        try {
            String ownerName = plugin.getDatabaseManager().getOwnerName(targetUUID);
            if (ownerName == null) {
                ownerName = targetName;
            }

            int cleared = plugin.getDatabaseManager().clearAllLocksByPlayer(targetUUID);
            
            if (cleared > 0) {
                player.sendMessage(MessageUtils.colorize("&a[Admin] Cleared &e" + cleared + " &alock(s) owned by &e" + ownerName));
                plugin.getLogger().info("Admin " + player.getName() + " cleared " + cleared + 
                    " lock(s) owned by " + ownerName + " (UUID: " + targetUUID + ")");
            } else {
                player.sendMessage(MessageUtils.colorize("&cNo locks found for player: " + ownerName));
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Error clearing locks: " + e.getMessage());
            player.sendMessage(MessageUtils.colorize("&cError clearing locks: " + e.getMessage()));
        }
    }

    private void handleClearArea(Player player, String[] args) {
        if (!player.hasPermission("chestlocklite.admin")) {
            player.sendMessage(MessageUtils.colorize("&cYou don't have permission!"));
            return;
        }

        int radius = 10; // Default radius
        if (args.length >= 2) {
            try {
                radius = Integer.parseInt(args[1]);
                if (radius < 1 || radius > 100) {
                    player.sendMessage(MessageUtils.colorize("&cRadius must be between 1 and 100 blocks!"));
                    return;
                }
            } catch (NumberFormatException e) {
                player.sendMessage(MessageUtils.colorize("&cInvalid radius: " + args[1]));
                player.sendMessage(MessageUtils.colorize("&7Usage: /cl cleararea [radius]"));
                return;
            }
        }

        org.bukkit.Location center = player.getLocation();

        try {
            int cleared = plugin.getDatabaseManager().clearAllLocksInArea(center, radius);
            
            if (cleared > 0) {
                player.sendMessage(MessageUtils.colorize("&a[Admin] Cleared &e" + cleared + 
                    " &alock(s) in area (radius: " + radius + " blocks)"));
                plugin.getLogger().info("Admin " + player.getName() + " cleared " + cleared + 
                    " lock(s) in area around " + center.getWorld().getName() + " " + 
                    center.getBlockX() + "," + center.getBlockY() + "," + center.getBlockZ() + 
                    " (radius: " + radius + ")");
            } else {
                player.sendMessage(MessageUtils.colorize("&7No locks found in area (radius: " + radius + " blocks)"));
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Error clearing locks in area: " + e.getMessage());
            player.sendMessage(MessageUtils.colorize("&cError clearing locks: " + e.getMessage()));
        }
    }

    private void handleReload(Player player) {
        if (!player.hasPermission("chestlocklite.admin")) {
            player.sendMessage(MessageUtils.colorize("&cYou don't have permission!"));
            return;
        }

        plugin.reload();
        player.sendMessage(MessageUtils.colorize("&aConfiguration reloaded!"));
    }

    private void handleTrust(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(MessageUtils.colorize("&cUsage: /cl trust <player>"));
            return;
        }

        Block targetBlock = getTargetChest(player);
        if (targetBlock == null) {
            player.sendMessage(MessageUtils.colorize(
                plugin.getConfigManager().getNoChestTargetMessage()));
            return;
        }

        org.bukkit.Location chestLocation = plugin.getLockManager().getPrimaryChestLocation(targetBlock);
        DatabaseManager.LockData lock = plugin.getLockManager().getLockInfo(chestLocation);
        
        if (lock == null) {
            player.sendMessage(MessageUtils.colorize("&cThis chest is not locked! Lock it first with /cl lock"));
            return;
        }

        if (!lock.getOwnerUUID().equals(player.getUniqueId())) {
            player.sendMessage(MessageUtils.colorize(
                plugin.getConfigManager().getNotOwnerMessage(targetBlock)));
            return;
        }

        String targetPlayerName = args[1];
        Player targetPlayer = Bukkit.getPlayer(targetPlayerName);
        
        if (targetPlayer == null) {
            player.sendMessage(MessageUtils.colorize("&cPlayer &e" + targetPlayerName + " &cis not online!"));
            return;
        }

        if (targetPlayer.getUniqueId().equals(player.getUniqueId())) {
            player.sendMessage(MessageUtils.colorize("&cYou cannot trust yourself!"));
            return;
        }

        try {
            // Check if already trusted
            if (plugin.getDatabaseManager().isTrustedPlayer(chestLocation, targetPlayer.getUniqueId())) {
                player.sendMessage(MessageUtils.colorize("&cPlayer &e" + targetPlayerName + " &cis already trusted on this chest!"));
                return;
            }

            plugin.getDatabaseManager().addTrustedPlayer(chestLocation, targetPlayer.getUniqueId(), targetPlayer.getName());
            player.sendMessage(MessageUtils.colorize("&aSuccessfully trusted &e" + targetPlayerName + " &aon this chest!"));
            targetPlayer.sendMessage(MessageUtils.colorize("&e" + player.getName() + " &ahas trusted you on one of their chests!"));
        } catch (SQLException e) {
            plugin.getLogger().severe("Error adding trusted player: " + e.getMessage());
            player.sendMessage(MessageUtils.colorize("&cAn error occurred. Please try again."));
        }
    }

    private void handleUntrust(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(MessageUtils.colorize("&cUsage: /cl untrust <player>"));
            return;
        }

        Block targetBlock = getTargetChest(player);
        if (targetBlock == null) {
            player.sendMessage(MessageUtils.colorize(
                plugin.getConfigManager().getNoChestTargetMessage()));
            return;
        }

        org.bukkit.Location chestLocation = plugin.getLockManager().getPrimaryChestLocation(targetBlock);
        DatabaseManager.LockData lock = plugin.getLockManager().getLockInfo(chestLocation);
        
        if (lock == null) {
            player.sendMessage(MessageUtils.colorize("&cThis chest is not locked!"));
            return;
        }

        if (!lock.getOwnerUUID().equals(player.getUniqueId())) {
            player.sendMessage(MessageUtils.colorize(
                plugin.getConfigManager().getNotOwnerMessage(targetBlock)));
            return;
        }

        String targetPlayerName = args[1];
        Player targetPlayer = Bukkit.getPlayer(targetPlayerName);
        UUID targetUUID = null;
        
        if (targetPlayer != null) {
            targetUUID = targetPlayer.getUniqueId();
        } else {
            // Try to find UUID from database
            try {
                List<DatabaseManager.TrustedPlayerData> trustedPlayers = 
                    plugin.getDatabaseManager().getTrustedPlayers(chestLocation);
                for (DatabaseManager.TrustedPlayerData trusted : trustedPlayers) {
                    if (trusted.getName().equalsIgnoreCase(targetPlayerName)) {
                        targetUUID = trusted.getUUID();
                        break;
                    }
                }
            } catch (SQLException e) {
                plugin.getLogger().severe("Error checking trusted players: " + e.getMessage());
            }
        }

        if (targetUUID == null) {
            player.sendMessage(MessageUtils.colorize("&cPlayer &e" + targetPlayerName + " &cnot found or not trusted!"));
            return;
        }

        try {
            if (!plugin.getDatabaseManager().isTrustedPlayer(chestLocation, targetUUID)) {
                player.sendMessage(MessageUtils.colorize("&cPlayer &e" + targetPlayerName + " &cis not trusted on this chest!"));
                return;
            }

            plugin.getDatabaseManager().removeTrustedPlayer(chestLocation, targetUUID);
            player.sendMessage(MessageUtils.colorize("&aRemoved trust from &e" + targetPlayerName + " &aon this chest!"));
            
            if (targetPlayer != null) {
                targetPlayer.sendMessage(MessageUtils.colorize("&e" + player.getName() + " &chas removed your trust on one of their chests."));
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Error removing trusted player: " + e.getMessage());
            player.sendMessage(MessageUtils.colorize("&cAn error occurred. Please try again."));
        }
    }

    private void handleTrustedList(Player player) {
        Block targetBlock = getTargetChest(player);
        if (targetBlock == null) {
            player.sendMessage(MessageUtils.colorize(
                plugin.getConfigManager().getNoChestTargetMessage()));
            return;
        }

        org.bukkit.Location chestLocation = plugin.getLockManager().getPrimaryChestLocation(targetBlock);
        DatabaseManager.LockData lock = plugin.getLockManager().getLockInfo(chestLocation);
        
        if (lock == null) {
            player.sendMessage(MessageUtils.colorize("&cThis chest is not locked!"));
            return;
        }

        if (!lock.getOwnerUUID().equals(player.getUniqueId())) {
            player.sendMessage(MessageUtils.colorize(
                plugin.getConfigManager().getNotOwnerMessage(targetBlock)));
            return;
        }

        try {
            List<DatabaseManager.TrustedPlayerData> trustedPlayers = 
                plugin.getDatabaseManager().getTrustedPlayers(chestLocation);
            
            if (trustedPlayers.isEmpty()) {
                player.sendMessage(MessageUtils.colorize("&7No trusted players on this chest."));
                return;
            }

            player.sendMessage(MessageUtils.colorize("&6=== Trusted Players ==="));
            for (DatabaseManager.TrustedPlayerData trusted : trustedPlayers) {
                String status = Bukkit.getOfflinePlayer(trusted.getUUID()).isOnline() ? "&a[Online]" : "&7[Offline]";
                player.sendMessage(MessageUtils.colorize("&e- " + trusted.getName() + " " + status));
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Error loading trusted players: " + e.getMessage());
            player.sendMessage(MessageUtils.colorize("&cAn error occurred. Please try again."));
        }
    }

    private Block getTargetChest(Player player) {
        Block targetBlock = player.getTargetBlock(null, 5);
        
        if (targetBlock == null || !plugin.getLockManager().isLockableContainer(targetBlock)) {
            return null;
        }

        return targetBlock;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        // If no args, return all available subcommands (filtered by permissions)
        if (args.length == 1) {
            String partial = args[0].toLowerCase();
            
            // Basic commands
            if (sender.hasPermission("chestlocklite.lock") && "lock".startsWith(partial)) {
                completions.add("lock");
            }
            if (sender.hasPermission("chestlocklite.unlock") && "unlock".startsWith(partial)) {
                completions.add("unlock");
            }
            if (sender.hasPermission("chestlocklite.password")) {
                if ("password".startsWith(partial)) {
                    completions.add("password");
                }
                if ("removepassword".startsWith(partial)) {
                    completions.add("removepassword");
                }
                if ("removepwd".startsWith(partial)) {
                    completions.add("removepwd");
                }
            }
            if (sender.hasPermission("chestlocklite.info") && "info".startsWith(partial)) {
                completions.add("info");
            }
            if (sender.hasPermission("chestlocklite.gui")) {
                if ("gui".startsWith(partial)) {
                    completions.add("gui");
                }
                if ("menu".startsWith(partial)) {
                    completions.add("menu");
                }
            }
            
            // Admin commands
            if (sender.hasPermission("chestlocklite.admin.clear")) {
                if ("override".startsWith(partial)) {
                    completions.add("override");
                }
                if ("forceunlock".startsWith(partial)) {
                    completions.add("forceunlock");
                }
                if ("clear".startsWith(partial)) {
                    completions.add("clear");
                }
            }
            if (sender.hasPermission("chestlocklite.admin.clearall") && "clearall".startsWith(partial)) {
                completions.add("clearall");
            }
            if (sender.hasPermission("chestlocklite.admin.cleararea") && "cleararea".startsWith(partial)) {
                completions.add("cleararea");
            }
            if (sender.hasPermission("chestlocklite.admin.reload") && "reload".startsWith(partial)) {
                completions.add("reload");
            }
            
            // Trust commands
            if (sender.hasPermission("chestlocklite.trust")) {
                if ("trust".startsWith(partial)) {
                    completions.add("trust");
                }
                if ("untrust".startsWith(partial)) {
                    completions.add("untrust");
                }
                if ("trustedlist".startsWith(partial)) {
                    completions.add("trustedlist");
                }
                if ("trusted".startsWith(partial)) {
                    completions.add("trusted");
                }
                if ("trustlist".startsWith(partial)) {
                    completions.add("trustlist");
                }
            }
            
            return completions;
        }

        // For second argument
        if (args.length == 2) {
            String subCommand = args[0].toLowerCase();
            
            switch (subCommand) {
                case "clearall":
                    // Suggest online player names
                    if (sender.hasPermission("chestlocklite.admin.clearall")) {
                        String partial = args[1].toLowerCase();
                        completions.addAll(Bukkit.getOnlinePlayers().stream()
                            .map(Player::getName)
                            .filter(name -> name.toLowerCase().startsWith(partial))
                            .collect(Collectors.toList()));
                    }
                    break;
                    
                case "trust":
                case "untrust":
                    // Suggest online player names
                    if (sender.hasPermission("chestlocklite.trust")) {
                        String partial = args[1].toLowerCase();
                        completions.addAll(Bukkit.getOnlinePlayers().stream()
                            .map(Player::getName)
                            .filter(name -> name.toLowerCase().startsWith(partial))
                            .collect(Collectors.toList()));
                    }
                    break;
                case "cleararea":
                    // Suggest common radius values
                    if (sender.hasPermission("chestlocklite.admin.cleararea")) {
                        String partial = args[1].toLowerCase();
                        List<String> radiusSuggestions = Arrays.asList("5", "10", "15", "20", "25", "50", "100");
                        completions.addAll(radiusSuggestions.stream()
                            .filter(radius -> radius.startsWith(partial))
                            .collect(Collectors.toList()));
                    }
                    break;
                    
                case "password":
                    // Don't suggest anything for password input
                    break;
            }
            
            return completions;
        }

        // No autocomplete for additional arguments
        return completions;
    }
}

