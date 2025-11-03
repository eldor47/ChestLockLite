# Configuration Reference

Complete reference for all configuration options in `config.yml`.

## File Location

```
plugins/ChestLockLite/config.yml
```

## Configuration Sections

### Database Settings

```yaml
database:
  filename: "locks.db"
  backup-interval: 24
```

- **filename**: SQLite database file name (default: `locks.db`)
- **backup-interval**: Auto-backup interval in hours (0 to disable, default: 24)

### Lock Settings

```yaml
locks:
  allow-locking: true
  allow-passwords: true
  max-password-length: 32
  min-password-length: 4
  password-cooldown: 3
  max-chests-per-player: 50
  allow-admin-unlock: true
  allow-trusted-players: true
  max-trusted-players-per-chest: 20
  auto-lock-on-place: false
```

- **allow-locking**: Enable/disable chest locking (default: `true`)
- **allow-passwords**: Enable/disable password protection (default: `true`)
- **max-password-length**: Maximum password length (default: `32`)
- **min-password-length**: Minimum password length (default: `4`)
- **password-cooldown**: Cooldown in seconds between password attempts (default: `3`)
- **max-chests-per-player**: Maximum chests a player can own (default: `50`)
- **allow-admin-unlock**: Allow admins to unlock any chest (default: `true`)
- **allow-trusted-players**: Enable trusted players system (default: `true`)
- **max-trusted-players-per-chest**: Maximum trusted players per chest (default: `20`)
- **auto-lock-on-place**: Automatically lock chests when placed (default: `false`)

### Messages

All messages support color codes and placeholders:

```yaml
messages:
  lock-success: "&aChest locked successfully!"
  unlock-success: "&aChest unlocked successfully!"
  password-set: "&aPassword set successfully!"
  password-removed: "&aPassword removed successfully!"
  password-cooldown: "&cPlease wait {seconds} seconds before trying another password!"
  # ... see config.yml for complete list
```

### Visual Settings

```yaml
visual:
  show-particles: true
  particle-type: "VILLAGER_HAPPY"
```

- **show-particles**: Show particles when chest is opened (default: `true`)
- **particle-type**: Particle effect type (default: `VILLAGER_HAPPY`)
  - Common types: `FLAME`, `HEART`, `VILLAGER_HAPPY`, `SMOKE`, `NOTE`

## Reloading Configuration

After editing `config.yml`:

1. Stop your server
2. Edit the file
3. Start your server

Or use the admin command (requires `chestlocklite.admin.reload`):

```
/cl reload
```

## Configuration Tips

- **password-cooldown**: Set to 0 to disable cooldown (not recommended)
- **max-chests-per-player**: Set to -1 for unlimited chests
- **backup-interval**: Set to 0 to disable automatic backups
- **auto-lock-on-place**: Useful for PvP servers where chests should be protected immediately

## Default Values

If you delete or corrupt your `config.yml`, stop the server and delete the file. The plugin will regenerate it with default values on next start.

## Advanced Configuration

For database optimization, see [Database Management](Database.md).

For permission configuration, see [Permissions Guide](Permissions.md).

