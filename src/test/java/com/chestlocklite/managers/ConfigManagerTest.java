package com.chestlocklite.managers;

import com.chestlocklite.ChestLockLitePlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for ConfigManager
 */
class ConfigManagerTest {

    private ChestLockLitePlugin plugin;
    private FileConfiguration config;

    @BeforeEach
    void setUp() {
        // Use lenient mocking to handle final methods
        plugin = mock(ChestLockLitePlugin.class, withSettings().lenient());
        config = new YamlConfiguration();
        
        // Set default values
        config.set("database.filename", "test.db");
        config.set("database.backup-interval", 24);
        config.set("locks.allow-locking", true);
        config.set("locks.allow-hoppers", true);
        config.set("locks.allow-furnaces", true);
        config.set("locks.admin-notification", true);
        config.set("locks.max-chests-per-player", 50);
        config.set("locks.min-password-length", 4);
        config.set("locks.max-password-length", 32);
        
        // Use lenient stubbing for final methods
        lenient().when(plugin.getConfig()).thenReturn(config);
    }

    @Test
    void testGetDatabaseFilename() {
        ConfigManager configManager = new ConfigManager(plugin);
        assertEquals("test.db", configManager.getDatabaseFilename());
    }

    @Test
    void testGetBackupInterval() {
        ConfigManager configManager = new ConfigManager(plugin);
        assertEquals(24, configManager.getBackupInterval());
    }

    @Test
    void testIsLockingAllowed() {
        ConfigManager configManager = new ConfigManager(plugin);
        assertTrue(configManager.isLockingAllowed());
    }

    @Test
    void testIsHopperSupportEnabled() {
        ConfigManager configManager = new ConfigManager(plugin);
        assertTrue(configManager.isHopperSupportEnabled());
    }

    @Test
    void testIsFurnaceSupportEnabled() {
        ConfigManager configManager = new ConfigManager(plugin);
        assertTrue(configManager.isFurnaceSupportEnabled());
    }

    @Test
    void testIsAdminNotificationEnabled() {
        ConfigManager configManager = new ConfigManager(plugin);
        assertTrue(configManager.isAdminNotificationEnabled());
    }

    @Test
    void testGetMaxChestsPerPlayer() {
        ConfigManager configManager = new ConfigManager(plugin);
        assertEquals(50, configManager.getMaxChestsPerPlayer());
    }

    @Test
    void testGetPasswordLengthLimits() {
        ConfigManager configManager = new ConfigManager(plugin);
        assertEquals(4, configManager.getMinPasswordLength());
        assertEquals(32, configManager.getMaxPasswordLength());
    }

    @Test
    void testDefaultValues() {
        // Test with minimal config
        ChestLockLitePlugin minimalPlugin = mock(ChestLockLitePlugin.class, withSettings().lenient());
        FileConfiguration minimalConfig = new YamlConfiguration();
        lenient().when(minimalPlugin.getConfig()).thenReturn(minimalConfig);
        
        ConfigManager configManager = new ConfigManager(minimalPlugin);
        
        // Should use defaults
        assertEquals("locks.db", configManager.getDatabaseFilename());
        assertEquals(24, configManager.getBackupInterval());
        assertTrue(configManager.isLockingAllowed());
    }
}

