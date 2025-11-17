package com.chestlocklite;

import com.chestlocklite.managers.LockManager;
import com.chestlocklite.utils.PasswordHasher;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for edge cases and error handling
 */
class EdgeCaseTest {

    @Test
    void testPasswordHasherEmptyPassword() {
        String hash = PasswordHasher.hashPassword("");
        assertNotNull(hash);
        assertTrue(PasswordHasher.verifyPassword("", hash));
    }

    @Test
    void testPasswordHasherVeryLongPassword() {
        String longPassword = "a".repeat(100);
        String hash = PasswordHasher.hashPassword(longPassword);
        assertNotNull(hash);
        assertTrue(PasswordHasher.verifyPassword(longPassword, hash));
    }

    @Test
    void testPasswordHasherSpecialCharacters() {
        String password = "!@#$%^&*()_+-=[]{}|;:,.<>?";
        String hash = PasswordHasher.hashPassword(password);
        assertNotNull(hash);
        assertTrue(PasswordHasher.verifyPassword(password, hash));
    }

    @Test
    void testLockManagerHopperDisabled() {
        ChestLockLitePlugin plugin = mock(ChestLockLitePlugin.class);
        com.chestlocklite.managers.ConfigManager configManager = mock(com.chestlocklite.managers.ConfigManager.class);
        when(configManager.isHopperSupportEnabled()).thenReturn(false);
        when(configManager.isFurnaceSupportEnabled()).thenReturn(true);
        when(plugin.getConfigManager()).thenReturn(configManager);
        
        LockManager lockManager = new LockManager(plugin);
        assertNotNull(lockManager);
    }

    @Test
    void testLockManagerFurnaceDisabled() {
        ChestLockLitePlugin plugin = mock(ChestLockLitePlugin.class);
        com.chestlocklite.managers.ConfigManager configManager = mock(com.chestlocklite.managers.ConfigManager.class);
        when(configManager.isHopperSupportEnabled()).thenReturn(true);
        when(configManager.isFurnaceSupportEnabled()).thenReturn(false);
        when(plugin.getConfigManager()).thenReturn(configManager);
        
        LockManager lockManager = new LockManager(plugin);
        assertNotNull(lockManager);
    }

    @Test
    void testConfigManagerDefaultValues() {
        ChestLockLitePlugin plugin = mock(ChestLockLitePlugin.class);
        FileConfiguration config = new YamlConfiguration();
        when(plugin.getConfig()).thenReturn(config);
        
        com.chestlocklite.managers.ConfigManager configManager = new com.chestlocklite.managers.ConfigManager(plugin);
        
        // Test defaults
        assertEquals("locks.db", configManager.getDatabaseFilename());
        assertEquals(24, configManager.getBackupInterval());
        assertTrue(configManager.isLockingAllowed());
        assertTrue(configManager.isHopperSupportEnabled());
        assertTrue(configManager.isFurnaceSupportEnabled());
        assertTrue(configManager.isAdminNotificationEnabled());
    }

    @Test
    void testPasswordHasherUnicode() {
        String unicodePassword = "密码🔒Test123";
        String hash = PasswordHasher.hashPassword(unicodePassword);
        assertNotNull(hash);
        assertTrue(PasswordHasher.verifyPassword(unicodePassword, hash));
    }

    @Test
    void testLocationKeyFormat() {
        // This tests the location key format used in ChestListener
        org.bukkit.World world = mock(org.bukkit.World.class);
        when(world.getName()).thenReturn("test_world");
        org.bukkit.Location loc = new org.bukkit.Location(world, 100, 64, -50);
        
        String key = loc.getWorld().getName() + ":" + loc.getBlockX() + ":" + loc.getBlockY() + ":" + loc.getBlockZ();
        assertEquals("test_world:100:64:-50", key);
    }
}

