package com.chestlocklite.utils;

import org.bukkit.ChatColor;

public class MessageUtils {

    public static String colorize(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static String formatError(String message) {
        return ChatColor.RED + "✗ " + message;
    }

    public static String formatSuccess(String message) {
        return ChatColor.GREEN + "✓ " + message;
    }

    public static String formatInfo(String message) {
        return ChatColor.YELLOW + "ℹ " + message;
    }
}

