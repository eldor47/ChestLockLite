package com.chestlocklite.managers;

import com.chestlocklite.ChestLockLitePlugin;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for LockManager - Basic unit tests without heavy mocking
 */
class LockManagerTest {

    @Test
    void testLockManagerInitialization() {
        ChestLockLitePlugin plugin = mock(ChestLockLitePlugin.class);
        LockManager lockManager = new LockManager(plugin);
        
        assertNotNull(lockManager);
    }

    @Test
    void testConfigManagerIntegration() {
        ChestLockLitePlugin plugin = mock(ChestLockLitePlugin.class);
        ConfigManager configManager = mock(ConfigManager.class);
        
        when(configManager.isHopperSupportEnabled()).thenReturn(true);
        when(configManager.isFurnaceSupportEnabled()).thenReturn(true);
        when(plugin.getConfigManager()).thenReturn(configManager);
        
        LockManager lockManager = new LockManager(plugin);
        assertNotNull(lockManager);
        
        // Verify config manager is accessible
        assertSame(configManager, plugin.getConfigManager());
    }

    @Test
    void testHopperSupportDisabled() {
        ChestLockLitePlugin plugin = mock(ChestLockLitePlugin.class);
        ConfigManager configManager = mock(ConfigManager.class);
        
        when(configManager.isHopperSupportEnabled()).thenReturn(false);
        when(configManager.isFurnaceSupportEnabled()).thenReturn(true);
        when(plugin.getConfigManager()).thenReturn(configManager);
        
        LockManager lockManager = new LockManager(plugin);
        assertNotNull(lockManager);
    }

    @Test
    void testFurnaceSupportDisabled() {
        ChestLockLitePlugin plugin = mock(ChestLockLitePlugin.class);
        ConfigManager configManager = mock(ConfigManager.class);
        
        when(configManager.isHopperSupportEnabled()).thenReturn(true);
        when(configManager.isFurnaceSupportEnabled()).thenReturn(false);
        when(plugin.getConfigManager()).thenReturn(configManager);
        
        LockManager lockManager = new LockManager(plugin);
        assertNotNull(lockManager);
    }
}

