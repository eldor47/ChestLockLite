package com.chestlocklite;

import com.chestlocklite.commands.ChestLockCommand;
import com.chestlocklite.gui.LockGUI;
import com.chestlocklite.gui.PasswordInputGUI;
import com.chestlocklite.gui.TrustedPlayersGUI;
import com.chestlocklite.listeners.BlockPlaceListener;
import com.chestlocklite.listeners.ChestListener;
import com.chestlocklite.managers.ConfigManager;
import com.chestlocklite.managers.DatabaseManager;
import com.chestlocklite.managers.LockManager;
import com.chestlocklite.managers.PasswordCooldownManager;
import org.bukkit.plugin.java.JavaPlugin;

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
        
        // Save default config
        saveDefaultConfig();
        
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
}

