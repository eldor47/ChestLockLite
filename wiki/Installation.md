# Installation Guide

This guide will walk you through installing ChestLockLite on your Spigot/Paper server.

## Requirements

Before installing, ensure you have:

- Java 21 or higher
- Spigot/Paper 1.21.10 or higher
- Server admin access

## Installation Steps

### Step 1: Download

Download the latest `ChestLockLite.jar` from the releases page.

### Step 2: Install the Plugin

1. Stop your Minecraft server
2. Copy `ChestLockLite.jar` into your server's `plugins` folder
3. Start your server

### Step 3: First Run

On first run, the plugin will:
- Create the plugin directory at `plugins/ChestLockLite/`
- Generate the default `config.yml` configuration file
- Create the SQLite database file `locks.db`

### Step 4: Configure (Optional)

1. Stop your server
2. Edit `plugins/ChestLockLite/config.yml` if needed
3. Start your server again

## Verification

To verify the plugin is installed correctly:

1. Check your server console for: `[ChestLockLite] has been enabled!`
2. In-game, try the command: `/cl info` (should show help if no chest is targeted)
3. Check that `plugins/ChestLockLite/` folder exists with `config.yml`

## Next Steps

- See the [Quick Start Guide](Quick-Start.md) to begin using the plugin
- Review the [Configuration Reference](Configuration.md) to customize settings
- Check [Permissions](Permissions.md) to set up player permissions

## Troubleshooting

If you encounter issues:

- Ensure Java 21+ is installed (`java -version`)
- Verify Spigot/Paper version compatibility
- Check server logs in `logs/latest.log`
- See [Troubleshooting Guide](Troubleshooting.md)

