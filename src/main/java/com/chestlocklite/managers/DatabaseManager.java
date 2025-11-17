package com.chestlocklite.managers;

import com.chestlocklite.ChestLockLitePlugin;
import com.chestlocklite.utils.PasswordHasher;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class DatabaseManager {

    private final ChestLockLitePlugin plugin;
    private Connection connection;
    private String databasePath;

    public DatabaseManager(ChestLockLitePlugin plugin) {
        this.plugin = plugin;
        this.databasePath = plugin.getDataFolder().getAbsolutePath() + "/" + 
                           plugin.getConfigManager().getDatabaseFilename();
    }

    public void initialize() throws SQLException {
        // Create data folder if it doesn't exist
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        // Connect to SQLite database
        String url = "jdbc:sqlite:" + databasePath;
        connection = DriverManager.getConnection(url);

        // Create tables if they don't exist
        createTables();

        plugin.getLogger().info("Database connected: " + databasePath);
    }

    private void createTables() throws SQLException {
        String createLocksTable = """
            CREATE TABLE IF NOT EXISTS chest_locks (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                world TEXT NOT NULL,
                x INTEGER NOT NULL,
                y INTEGER NOT NULL,
                z INTEGER NOT NULL,
                owner_uuid TEXT NOT NULL,
                owner_name TEXT NOT NULL,
                password TEXT,
                hopper_enabled INTEGER DEFAULT 1,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                UNIQUE(world, x, y, z)
            )
            """;

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createLocksTable);
        }
        
        // Migrate existing databases: Add new columns if they don't exist
        // This handles upgrades from older versions
        migrateDatabase();

        // Create index for faster lookups
        String createIndex = """
            CREATE INDEX IF NOT EXISTS idx_location ON chest_locks(world, x, y, z)
            """;

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createIndex);
        }

        // Create trusted players table
        String createTrustedPlayersTable = """
            CREATE TABLE IF NOT EXISTS trusted_players (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                world TEXT NOT NULL,
                x INTEGER NOT NULL,
                y INTEGER NOT NULL,
                z INTEGER NOT NULL,
                trusted_uuid TEXT NOT NULL,
                trusted_name TEXT NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                UNIQUE(world, x, y, z, trusted_uuid)
            )
            """;

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createTrustedPlayersTable);
        }

        // Create index for faster trusted player lookups
        String createTrustedIndex = """
            CREATE INDEX IF NOT EXISTS idx_trusted_location ON trusted_players(world, x, y, z)
            """;

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createTrustedIndex);
        }

        if (plugin.getConfigManager().isDebug()) {
            plugin.getLogger().info("Database tables created/verified successfully");
        }
    }

    private void migrateDatabase() {
        // Check if columns exist and add them if they don't (for upgrades from older versions)
        try {
            // Check if hopper_enabled column exists
            boolean hasHopperColumn = columnExists("hopper_enabled");
            if (!hasHopperColumn) {
                try (Statement stmt = connection.createStatement()) {
                    stmt.execute("ALTER TABLE chest_locks ADD COLUMN hopper_enabled INTEGER DEFAULT 1");
                    plugin.getLogger().info("Database migrated: Added hopper_enabled column");
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().warning("Error during database migration: " + e.getMessage());
            // Don't fail plugin startup if migration fails - columns might already exist
        }
    }

    private boolean columnExists(String columnName) throws SQLException {
        // SQLite doesn't have a direct way to check if a column exists
        // We'll try to query the column and catch the exception if it doesn't exist
        String sql = "SELECT " + columnName + " FROM chest_locks LIMIT 1";
        try (Statement stmt = connection.createStatement()) {
            stmt.executeQuery(sql);
            return true; // Column exists
        } catch (SQLException e) {
            // Check if error is about missing column
            String errorMsg = e.getMessage().toLowerCase();
            if (errorMsg.contains("no such column") || errorMsg.contains("unknown column")) {
                return false; // Column doesn't exist
            }
            // Some other error (like table doesn't exist) - rethrow
            throw e;
        }
    }

    public void addLock(Location location, UUID ownerUUID, String ownerName) throws SQLException {
        String sql = """
            INSERT OR REPLACE INTO chest_locks (world, x, y, z, owner_uuid, owner_name)
            VALUES (?, ?, ?, ?, ?, ?)
            """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, location.getWorld().getName());
            pstmt.setInt(2, location.getBlockX());
            pstmt.setInt(3, location.getBlockY());
            pstmt.setInt(4, location.getBlockZ());
            pstmt.setString(5, ownerUUID.toString());
            pstmt.setString(6, ownerName);
            pstmt.executeUpdate();
        }

        if (plugin.getConfigManager().isLogActions()) {
            plugin.getLogger().info("Lock added: " + ownerName + " at " + 
                location.getWorld().getName() + " " + location.getBlockX() + "," + 
                location.getBlockY() + "," + location.getBlockZ());
        }
    }

    public void removeLock(Location location) throws SQLException {
        String sql = """
            DELETE FROM chest_locks WHERE world = ? AND x = ? AND y = ? AND z = ?
            """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, location.getWorld().getName());
            pstmt.setInt(2, location.getBlockX());
            pstmt.setInt(3, location.getBlockY());
            pstmt.setInt(4, location.getBlockZ());
            int rows = pstmt.executeUpdate();

            if (rows > 0 && plugin.getConfigManager().isLogActions()) {
                plugin.getLogger().info("Lock removed at " + location.getWorld().getName() + 
                    " " + location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ());
            }
        }
        
        // Also remove all trusted players for this chest
        removeAllTrustedPlayers(location);
    }

    public LockData getLock(Location location) throws SQLException {
        String sql = """
            SELECT owner_uuid, owner_name, password, hopper_enabled FROM chest_locks
            WHERE world = ? AND x = ? AND y = ? AND z = ?
            """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, location.getWorld().getName());
            pstmt.setInt(2, location.getBlockX());
            pstmt.setInt(3, location.getBlockY());
            pstmt.setInt(4, location.getBlockZ());

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                boolean hopperEnabled = rs.getInt("hopper_enabled") == 1;
                // Handle null values for existing databases
                if (rs.wasNull()) {
                    hopperEnabled = true;
                }
                return new LockData(
                    UUID.fromString(rs.getString("owner_uuid")),
                    rs.getString("owner_name"),
                    rs.getString("password"),
                    hopperEnabled
                );
            }
        }

        return null;
    }

    public void setPassword(Location location, String password) throws SQLException {
        String sql = """
            UPDATE chest_locks SET password = ? WHERE world = ? AND x = ? AND y = ? AND z = ?
            """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            // Hash the password before storing
            String hashedPassword = PasswordHasher.hashPassword(password);
            pstmt.setString(1, hashedPassword);
            pstmt.setString(2, location.getWorld().getName());
            pstmt.setInt(3, location.getBlockX());
            pstmt.setInt(4, location.getBlockY());
            pstmt.setInt(5, location.getBlockZ());
            pstmt.executeUpdate();
        }
    }

    public void removePassword(Location location) throws SQLException {
        String sql = """
            UPDATE chest_locks SET password = NULL WHERE world = ? AND x = ? AND y = ? AND z = ?
            """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, location.getWorld().getName());
            pstmt.setInt(2, location.getBlockX());
            pstmt.setInt(3, location.getBlockY());
            pstmt.setInt(4, location.getBlockZ());
            pstmt.executeUpdate();
        }
    }

    public int getChestCount(UUID playerUUID) throws SQLException {
        String sql = "SELECT COUNT(*) FROM chest_locks WHERE owner_uuid = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, playerUUID.toString());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }

        return 0;
    }

    public int clearAllLocksByPlayer(UUID playerUUID) throws SQLException {
        String sql = "DELETE FROM chest_locks WHERE owner_uuid = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, playerUUID.toString());
            int rows = pstmt.executeUpdate();
            
            if (rows > 0 && plugin.getConfigManager().isLogActions()) {
                plugin.getLogger().info("Cleared " + rows + " lock(s) for player UUID: " + playerUUID);
            }
            
            return rows;
        }
    }

    public int clearAllLocksInArea(Location center, int radius) throws SQLException {
        String sql = """
            DELETE FROM chest_locks 
            WHERE world = ? 
            AND x BETWEEN ? AND ? 
            AND y BETWEEN ? AND ? 
            AND z BETWEEN ? AND ?
            """;

        int minX = center.getBlockX() - radius;
        int maxX = center.getBlockX() + radius;
        int minY = center.getBlockY() - radius;
        int maxY = center.getBlockY() + radius;
        int minZ = center.getBlockZ() - radius;
        int maxZ = center.getBlockZ() + radius;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, center.getWorld().getName());
            pstmt.setInt(2, minX);
            pstmt.setInt(3, maxX);
            pstmt.setInt(4, minY);
            pstmt.setInt(5, maxY);
            pstmt.setInt(6, minZ);
            pstmt.setInt(7, maxZ);
            
            int rows = pstmt.executeUpdate();
            
            if (rows > 0 && plugin.getConfigManager().isLogActions()) {
                plugin.getLogger().info("Cleared " + rows + " lock(s) in area around " + 
                    center.getWorld().getName() + " " + center.getBlockX() + "," + 
                    center.getBlockY() + "," + center.getBlockZ() + " (radius: " + radius + ")");
            }
            
            return rows;
        }
    }

    public String getOwnerName(UUID playerUUID) throws SQLException {
        String sql = "SELECT DISTINCT owner_name FROM chest_locks WHERE owner_uuid = ? LIMIT 1";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, playerUUID.toString());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("owner_name");
            }
        }

        return null;
    }

    // Trusted Players Methods

    public void addTrustedPlayer(Location location, UUID trustedUUID, String trustedName) throws SQLException {
        String sql = """
            INSERT OR REPLACE INTO trusted_players (world, x, y, z, trusted_uuid, trusted_name)
            VALUES (?, ?, ?, ?, ?, ?)
            """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, location.getWorld().getName());
            pstmt.setInt(2, location.getBlockX());
            pstmt.setInt(3, location.getBlockY());
            pstmt.setInt(4, location.getBlockZ());
            pstmt.setString(5, trustedUUID.toString());
            pstmt.setString(6, trustedName);
            pstmt.executeUpdate();
        }

        if (plugin.getConfigManager().isLogActions()) {
            plugin.getLogger().info("Added trusted player: " + trustedName + " for chest at " + 
                location.getWorld().getName() + " " + location.getBlockX() + "," + 
                location.getBlockY() + "," + location.getBlockZ());
        }
    }

    public void removeTrustedPlayer(Location location, UUID trustedUUID) throws SQLException {
        String sql = """
            DELETE FROM trusted_players 
            WHERE world = ? AND x = ? AND y = ? AND z = ? AND trusted_uuid = ?
            """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, location.getWorld().getName());
            pstmt.setInt(2, location.getBlockX());
            pstmt.setInt(3, location.getBlockY());
            pstmt.setInt(4, location.getBlockZ());
            pstmt.setString(5, trustedUUID.toString());
            pstmt.executeUpdate();
        }
    }

    public void removeAllTrustedPlayers(Location location) throws SQLException {
        String sql = """
            DELETE FROM trusted_players 
            WHERE world = ? AND x = ? AND y = ? AND z = ?
            """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, location.getWorld().getName());
            pstmt.setInt(2, location.getBlockX());
            pstmt.setInt(3, location.getBlockY());
            pstmt.setInt(4, location.getBlockZ());
            pstmt.executeUpdate();
        }
    }

    public boolean isTrustedPlayer(Location location, UUID playerUUID) throws SQLException {
        String sql = """
            SELECT COUNT(*) FROM trusted_players 
            WHERE world = ? AND x = ? AND y = ? AND z = ? AND trusted_uuid = ?
            """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, location.getWorld().getName());
            pstmt.setInt(2, location.getBlockX());
            pstmt.setInt(3, location.getBlockY());
            pstmt.setInt(4, location.getBlockZ());
            pstmt.setString(5, playerUUID.toString());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }

        return false;
    }

    public List<TrustedPlayerData> getTrustedPlayers(Location location) throws SQLException {
        List<TrustedPlayerData> trustedPlayers = new ArrayList<>();
        String sql = """
            SELECT trusted_uuid, trusted_name FROM trusted_players 
            WHERE world = ? AND x = ? AND y = ? AND z = ?
            """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, location.getWorld().getName());
            pstmt.setInt(2, location.getBlockX());
            pstmt.setInt(3, location.getBlockY());
            pstmt.setInt(4, location.getBlockZ());
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                trustedPlayers.add(new TrustedPlayerData(
                    UUID.fromString(rs.getString("trusted_uuid")),
                    rs.getString("trusted_name")
                ));
            }
        }

        return trustedPlayers;
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                plugin.getLogger().info("Database connection closed");
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Error closing database connection", e);
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void setHopperEnabled(Location location, boolean enabled) throws SQLException {
        String sql = """
            UPDATE chest_locks SET hopper_enabled = ? WHERE world = ? AND x = ? AND y = ? AND z = ?
            """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, enabled ? 1 : 0);
            pstmt.setString(2, location.getWorld().getName());
            pstmt.setInt(3, location.getBlockX());
            pstmt.setInt(4, location.getBlockY());
            pstmt.setInt(5, location.getBlockZ());
            pstmt.executeUpdate();
        }
    }

    // Lock data class
    public static class LockData {
        private final UUID ownerUUID;
        private final String ownerName;
        private final String password;
        private final boolean hopperEnabled;

        public LockData(UUID ownerUUID, String ownerName, String password) {
            this(ownerUUID, ownerName, password, true);
        }

        public LockData(UUID ownerUUID, String ownerName, String password, boolean hopperEnabled) {
            this.ownerUUID = ownerUUID;
            this.ownerName = ownerName;
            this.password = password;
            this.hopperEnabled = hopperEnabled;
        }

        public UUID getOwnerUUID() {
            return ownerUUID;
        }

        public String getOwnerName() {
            return ownerName;
        }

        public String getPassword() {
            return password;
        }

        public boolean hasPassword() {
            return password != null && !password.isEmpty();
        }

        public boolean isHopperEnabled() {
            return hopperEnabled;
        }
    }

    // Trusted player data class
    public static class TrustedPlayerData {
        private final UUID uuid;
        private final String name;

        public TrustedPlayerData(UUID uuid, String name) {
            this.uuid = uuid;
            this.name = name;
        }

        public UUID getUUID() {
            return uuid;
        }

        public String getName() {
            return name;
        }
    }
}

