# API Documentation

Developer documentation for integrating with ChestLockLite.

## Getting the Plugin Instance

```java
ChestLockLitePlugin plugin = (ChestLockLitePlugin) Bukkit.getPluginManager().getPlugin("ChestLockLite");
if (plugin == null || !plugin.isEnabled()) {
    // Plugin not loaded
    return;
}
```

## Accessing Managers

### Database Manager

```java
DatabaseManager dbManager = plugin.getDatabaseManager();

// Check if location is locked
boolean isLocked = dbManager.isLocked(location);

// Get lock data
DatabaseManager.LockData lockData = dbManager.getLockData(location);
if (lockData != null) {
    UUID ownerUUID = lockData.getOwnerUUID();
    String ownerName = lockData.getOwnerName();
    boolean hasPassword = lockData.hasPassword();
}
```

### Lock Manager

```java
LockManager lockManager = plugin.getLockManager();

// Check if player can open chest
boolean canOpen = lockManager.canOpenChest(player, location);

// Check if block is a chest
boolean isChest = lockManager.isChest(block);
```

### Config Manager

```java
ConfigManager configManager = plugin.getConfigManager();

// Get configuration values
boolean allowPasswords = configManager.isPasswordEnabled();
int maxChests = configManager.getMaxChestsPerPlayer();
int passwordCooldown = configManager.getPasswordCooldown();
```

## Events

### Custom Events

ChestLockLite may fire custom events for integration:

```java
@EventHandler
public void onChestLock(ChestLockEvent event) {
    Location location = event.getLocation();
    Player player = event.getPlayer();
    // Handle lock event
}

@EventHandler
public void onChestUnlock(ChestUnlockEvent event) {
    Location location = event.getLocation();
    Player player = event.getPlayer();
    // Handle unlock event
}
```

## Checking Lock Status

### Is Location Locked?

```java
DatabaseManager dbManager = plugin.getDatabaseManager();
boolean locked = dbManager.isLocked(location);
```

### Can Player Open?

```java
LockManager lockManager = plugin.getLockManager();
boolean canOpen = lockManager.canOpenChest(player, location);
```

### Is Player Owner?

```java
DatabaseManager dbManager = plugin.getDatabaseManager();
boolean isOwner = dbManager.isOwner(location, player.getUniqueId());
```

### Is Player Trusted?

```java
DatabaseManager dbManager = plugin.getDatabaseManager();
boolean isTrusted = dbManager.isTrustedPlayer(location, player.getUniqueId());
```

## Working with Trusted Players

### Get Trusted Players

```java
DatabaseManager dbManager = plugin.getDatabaseManager();
List<DatabaseManager.TrustedPlayerData> trusted = dbManager.getTrustedPlayers(location);

for (DatabaseManager.TrustedPlayerData data : trusted) {
    UUID uuid = data.getTrustedUUID();
    String name = data.getTrustedName();
}
```

## Password Management

### Check if Has Password

```java
DatabaseManager dbManager = plugin.getDatabaseManager();
DatabaseManager.LockData lockData = dbManager.getLockData(location);
boolean hasPassword = lockData != null && lockData.hasPassword();
```

### Verify Password

```java
PasswordHasher passwordHasher = new PasswordHasher();
String passwordHash = lockData.getPasswordHash();
boolean valid = passwordHasher.verifyPassword(password, passwordHash);
```

## Best Practices

### Performance

- Cache lock data when possible
- Use async operations for database queries
- Don't check locks on every block break (use events)

### Thread Safety

- Database operations should be on main thread (Bukkit API requirement)
- Use Bukkit scheduler for async work
- Lock data is cached, updates may be delayed

### Error Handling

- Always check if plugin is enabled
- Handle null returns from managers
- Verify locations are valid
- Check database connection

## Example Integration

### Check Before Block Break

```java
@EventHandler
public void onBlockBreak(BlockBreakEvent event) {
    Block block = event.getBlock();
    
    ChestLockLitePlugin plugin = (ChestLockLitePlugin) Bukkit.getPluginManager().getPlugin("ChestLockLite");
    if (plugin == null || !plugin.isEnabled()) {
        return;
    }
    
    LockManager lockManager = plugin.getLockManager();
    if (lockManager.isChest(block)) {
        DatabaseManager dbManager = plugin.getDatabaseManager();
        if (dbManager.isLocked(block.getLocation())) {
            DatabaseManager.LockData lockData = dbManager.getLockData(block.getLocation());
            if (!lockData.getOwnerUUID().equals(event.getPlayer().getUniqueId())) {
                event.setCancelled(true);
                event.getPlayer().sendMessage("This chest is locked!");
            }
        }
    }
}
```

## Version Compatibility

- API may change between versions
- Check plugin version before using API
- Test integrations after plugin updates

## Support

For API questions or feature requests:
- Check GitHub issues
- Review plugin source code
- Contact plugin maintainer

## See Also

- Main plugin source code
- [Database Management](Database.md)

