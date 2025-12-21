package com.chestlocklite;

import com.chestlocklite.commands.ChestLockCommand;
import com.chestlocklite.gui.LockGUI;
import com.chestlocklite.gui.PasswordInputGUI;
import com.chestlocklite.gui.TrustedPlayersGUI;
import com.chestlocklite.listeners.BlockPlaceListener;
import com.chestlocklite.listeners.ChestListener;
import com.chestlocklite.listeners.HopperListener;
import com.chestlocklite.managers.ConfigManager;
import com.chestlocklite.managers.DatabaseManager;
import com.chestlocklite.managers.LockManager;
import com.chestlocklite.managers.PasswordCooldownManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;

public class ChestLockLitePlugin extends JavaPlugin {

    private static ChestLockLitePlugin instance;
    private ConfigManager configManager;
    private DatabaseManager databaseManager;
    private LockManager lockManager;
    private PasswordCooldownManager passwordCooldownManager;
    private ChestListener chestListener;
    private LockGUI lockGUI;
    private PasswordInputGUI passwordInputGUI;
    private TrustedPlayersGUI trustedPlayersGUI;

    @Override
    public void onEnable() {
        instance = this;
        
        // Save default config with fallback
        createDefaultConfig();
        
        // Initialize managers
        this.configManager = new ConfigManager(this);
        
        // Initialize database
        try {
            this.databaseManager = new DatabaseManager(this);
            this.databaseManager.initialize();
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Failed to initialize database", e);
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        
        // Initialize lock manager
        this.lockManager = new LockManager(this);
        
        // Initialize password cooldown manager
        this.passwordCooldownManager = new PasswordCooldownManager(this);
        
        // Register listeners
        this.chestListener = new ChestListener(this);
        getServer().getPluginManager().registerEvents(chestListener, this);
        
        // Register block place listener for auto-lock feature
        getServer().getPluginManager().registerEvents(new BlockPlaceListener(this), this);
        
        // Register hopper listener for hopper extraction prevention
        getServer().getPluginManager().registerEvents(new HopperListener(this), this);
        
        // Initialize and register GUI
        this.lockGUI = new LockGUI(this);
        getServer().getPluginManager().registerEvents(lockGUI, this);
        
        // Initialize and register Password Input GUI
        this.passwordInputGUI = new PasswordInputGUI(this);
        getServer().getPluginManager().registerEvents(passwordInputGUI, this);
        
        // Initialize and register Trusted Players GUI
        this.trustedPlayersGUI = new TrustedPlayersGUI(this);
        getServer().getPluginManager().registerEvents(trustedPlayersGUI, this);
        
        // Register commands
        ChestLockCommand commandHandler = new ChestLockCommand(this);
        getCommand("chestlock").setExecutor(commandHandler);
        getCommand("chestlock").setTabCompleter(commandHandler);
        
        getLogger().info("ChestLockLite has been enabled!");
        getLogger().info("Database initialized successfully!");
    }

    @Override
    public void onDisable() {
        // Close database connection
        if (databaseManager != null) {
            databaseManager.close();
        }
        
        getLogger().info("ChestLockLite has been disabled!");
    }

    public void reload() {
        reloadConfig();
        configManager = new ConfigManager(this);
        
        if (databaseManager != null) {
            databaseManager.close();
        }
        
        try {
            databaseManager = new DatabaseManager(this);
            databaseManager.initialize();
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Failed to reload database", e);
        }
        
        lockManager = new LockManager(this);
        
        getLogger().info("ChestLockLite configuration reloaded!");
    }

    public static ChestLockLitePlugin getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public LockManager getLockManager() {
        return lockManager;
    }

    public ChestListener getChestListener() {
        return chestListener;
    }

    public LockGUI getLockGUI() {
        return lockGUI;
    }

    public PasswordInputGUI getPasswordInputGUI() {
        return passwordInputGUI;
    }

    public PasswordCooldownManager getPasswordCooldownManager() {
        return passwordCooldownManager;
    }

    public TrustedPlayersGUI getTrustedPlayersGUI() {
        return trustedPlayersGUI;
    }
    
    /**
     * Creates the default config file with fallback if embedded resource is not found.
     * This handles issues with some hosting providers where JAR resources may not be accessible.
     */
    private void createDefaultConfig() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }
        
        File configFile = new File(getDataFolder(), "config.yml");
        if (configFile.exists()) {
            return; // Config already exists
        }
        
        // Try to use the embedded resource first
        try (InputStream resource = getResource("config.yml")) {
            if (resource != null) {
                saveResource("config.yml", false);
                getLogger().info("Created config.yml from embedded resource");
                return;
            }
        } catch (Exception e) {
            getLogger().warning("Could not load embedded config.yml: " + e.getMessage());
        }
        
        // Fallback: create config programmatically
        getLogger().info("Creating default config.yml programmatically...");
        try (FileWriter writer = new FileWriter(configFile)) {
            writer.write(getDefaultConfigContent());
            getLogger().info("Created config.yml successfully");
        } catch (IOException e) {
            getLogger().severe("Failed to create config.yml: " + e.getMessage());
        }
    }
    
    /**
     * Returns the default configuration content as a string.
     */
    private String getDefaultConfigContent() {
        return """
# ChestLockLite Configuration File

# Database Settings
database:
  # SQLite database file name (stored in plugin data folder)
  filename: "locks.db"
  
  # Auto-backup interval in hours (0 to disable)
  backup-interval: 24

# Lock Settings
locks:
  # Allow players to lock chests
  allow-locking: true
  
  # Automatically lock chests when placed
  auto-lock-on-place: false
  
  # Allow password-protected chests
  allow-passwords: true
  
  # Maximum password length
  max-password-length: 32
  
  # Minimum password length
  min-password-length: 4
  
  # Password input cooldown in seconds (prevents spam)
  password-cooldown: 3
  
  # Maximum number of chests a player can own
  max-chests-per-player: 50
  
  # Allow admins to unlock any chest
  allow-admin-unlock: true
  
  # Protect locked chests from explosions (creepers, TNT, etc.)
  explosion-protection: true
  
  # Protect locked chests from fire and lava
  fire-protection: true
  
  # Prevent pistons from moving locked chests
  piston-protection: true
  
  # Allow trusted players system
  allow-trusted-players: true
  
  # Support for hoppers (allow locking hoppers)
  allow-hoppers: true
  
  # Support for furnaces (allow locking furnaces, blast furnaces, smokers)
  allow-furnaces: true
  
  # Show notification to admins when they open locked chests
  admin-notification: true
  
  # Maximum trusted players per chest
  max-trusted-players-per-chest: 20

# Messages
# Use {type} placeholder to automatically replace with container type (chest, barrel, hopper, furnace, etc.)
messages:
  # Message when chest is locked
  chest-locked: "&cThis {type} is locked!"
  
  # Message when locked by owner
  locked-by-owner: "&eLocked by: &6{owner}"
  
  # Message when trying to break locked chest
  cannot-break-chest: "&cYou cannot break this {type}! It is locked by &e{owner}&c."
  
  # Message when password protected
  password-protected: "&eThis {type} is password protected. Use &6/cl password <password> &eto unlock."
  
  # Success messages
  lock-success: "&a{type} locked successfully!"
  unlock-success: "&a{type} unlocked successfully!"
  password-set: "&aPassword set successfully!"
  password-removed: "&aPassword removed successfully!"
  
  # Error messages
  not-locked: "&cThis {type} is not locked!"
  already-locked: "&cThis {type} is already locked!"
  not-owner: "&cYou are not the owner of this {type}!"
  wrong-password: "&cIncorrect password!"
  password-cooldown: "&cPlease wait {seconds} seconds before trying another password!"
  max-chests-reached: "&cYou have reached the maximum number of locked chests ({max})!"
  no-chest-target: "&cYou must be looking at a chest!"
  invalid-password-length: "&cPassword must be between {min} and {max} characters!"

# Visual Settings
visual:
  # Show lock particles when chest is opened
  show-particles: true
  
  # Particle effect type (FLAME, HEART, VILLAGER_HAPPY, etc.)
  particle-type: "VILLAGER_HAPPY"
  
  # Show action bar messages
  show-actionbar: true

# Advanced Settings
advanced:
  # Enable debug mode
  debug: false
  
  # Log all lock/unlock actions
  log-actions: true
""";
    }
}

