package com.chestlocklite;

import com.chestlocklite.managers.ConfigManager;
import com.chestlocklite.managers.DatabaseManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.sql.SQLException;
import java.util.UUID;

/**
 * Tests for database operations and migration
 */
class DatabaseMigrationTest {

    private ChestLockLitePlugin plugin;
    private DatabaseManager databaseManager;
    private File testDbFile;

    @BeforeEach
    void setUp() throws Exception {
        plugin = mock(ChestLockLitePlugin.class, withSettings().lenient());
        
        // Create temporary database file
        testDbFile = File.createTempFile("test_locks", ".db");
        testDbFile.deleteOnExit();
        
        FileConfiguration config = new YamlConfiguration();
        config.set("database.filename", testDbFile.getName());
        config.set("advanced.debug", true);
        config.set("advanced.log-actions", false);
        
        // Use lenient stubbing for final methods
        lenient().when(plugin.getConfig()).thenReturn(config);
        lenient().when(plugin.getLogger()).thenReturn(java.util.logging.Logger.getLogger("TestLogger"));
        lenient().when(plugin.getDataFolder()).thenReturn(testDbFile.getParentFile());
        
        // Create ConfigManager after stubbing getConfig()
        ConfigManager configManager = new ConfigManager(plugin);
        lenient().when(plugin.getConfigManager()).thenReturn(configManager);
        
        databaseManager = new DatabaseManager(plugin);
        databaseManager.initialize();
    }

    @AfterEach
    void tearDown() {
        if (databaseManager != null) {
            databaseManager.close();
        }
        if (testDbFile != null && testDbFile.exists()) {
            testDbFile.delete();
        }
    }

    @Test
    void testDatabaseInitialization() {
        assertNotNull(databaseManager);
    }

    @Test
    void testAddLock() throws SQLException {
        org.bukkit.Location location = createTestLocation(10, 64, 20);
        UUID ownerUUID = UUID.randomUUID();
        String ownerName = "TestPlayer";
        
        databaseManager.addLock(location, ownerUUID, ownerName);
        
        DatabaseManager.LockData lock = databaseManager.getLock(location);
        assertNotNull(lock);
        assertEquals(ownerUUID, lock.getOwnerUUID());
        assertEquals(ownerName, lock.getOwnerName());
    }

    @Test
    void testRemoveLock() throws SQLException {
        org.bukkit.Location location = createTestLocation(10, 64, 20);
        UUID ownerUUID = UUID.randomUUID();
        
        databaseManager.addLock(location, ownerUUID, "TestPlayer");
        databaseManager.removeLock(location);
        
        DatabaseManager.LockData lock = databaseManager.getLock(location);
        assertNull(lock);
    }

    @Test
    void testSetPassword() throws SQLException {
        org.bukkit.Location location = createTestLocation(10, 64, 20);
        UUID ownerUUID = UUID.randomUUID();
        
        databaseManager.addLock(location, ownerUUID, "TestPlayer");
        databaseManager.setPassword(location, "testPassword123");
        
        DatabaseManager.LockData lock = databaseManager.getLock(location);
        assertNotNull(lock);
        assertTrue(lock.hasPassword());
    }

    @Test
    void testRemovePassword() throws SQLException {
        org.bukkit.Location location = createTestLocation(10, 64, 20);
        UUID ownerUUID = UUID.randomUUID();
        
        databaseManager.addLock(location, ownerUUID, "TestPlayer");
        databaseManager.setPassword(location, "testPassword123");
        databaseManager.removePassword(location);
        
        DatabaseManager.LockData lock = databaseManager.getLock(location);
        assertNotNull(lock);
        assertFalse(lock.hasPassword());
    }

    @Test
    void testHopperDefaults() throws SQLException {
        org.bukkit.Location location = createTestLocation(10, 64, 20);
        UUID ownerUUID = UUID.randomUUID();
        
        databaseManager.addLock(location, ownerUUID, "TestPlayer");
        
        DatabaseManager.LockData lock = databaseManager.getLock(location);
        assertNotNull(lock);
        // New locks should have hoppers enabled by default
        assertTrue(lock.isHopperEnabled());
    }

    @Test
    void testSetHopperEnabled() throws SQLException {
        org.bukkit.Location location = createTestLocation(10, 64, 20);
        UUID ownerUUID = UUID.randomUUID();
        
        databaseManager.addLock(location, ownerUUID, "TestPlayer");
        databaseManager.setHopperEnabled(location, false);
        
        DatabaseManager.LockData lock = databaseManager.getLock(location);
        assertNotNull(lock);
        assertFalse(lock.isHopperEnabled());
        
        databaseManager.setHopperEnabled(location, true);
        lock = databaseManager.getLock(location);
        assertTrue(lock.isHopperEnabled());
    }

    @Test
    void testGetChestCount() throws SQLException {
        UUID ownerUUID = UUID.randomUUID();
        
        // Add multiple locks
        databaseManager.addLock(createTestLocation(10, 64, 20), ownerUUID, "TestPlayer");
        databaseManager.addLock(createTestLocation(20, 64, 30), ownerUUID, "TestPlayer");
        databaseManager.addLock(createTestLocation(30, 64, 40), ownerUUID, "TestPlayer");
        
        int count = databaseManager.getChestCount(ownerUUID);
        assertEquals(3, count);
    }

    private org.bukkit.Location createTestLocation(int x, int y, int z) {
        org.bukkit.World world = mock(org.bukkit.World.class);
        when(world.getName()).thenReturn("test_world");
        return new org.bukkit.Location(world, x, y, z);
    }
}

