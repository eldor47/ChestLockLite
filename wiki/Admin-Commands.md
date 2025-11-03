# Admin Commands

Complete reference for all administrative commands.

## Permission Required

All admin commands require one of:
- `chestlocklite.admin.*` - All admin commands
- Specific permission (see each command)
- OP status (default permission)

## Clear Single Lock

### Command

```
/cl clear
/cl override
/cl forceunlock
```

### Permission

`chestlocklite.admin.clear`

### Usage

1. Look at the locked chest
2. Run `/cl clear`
3. Lock is removed from database

### Result

- Chest becomes unlocked
- Previous owner information logged to console
- Useful for abandoned chests or griefing cleanup

## Clear All Locks by Player

### Command

```
/cl clearall <player>
```

### Permission

`chestlocklite.admin.clearall`

### Usage

```
/cl clearall PlayerName
```

Works with online or offline players. Looks up player by name in database.

### Result

- All chests owned by that player are unlocked
- Count of cleared locks is shown
- Useful when removing players or cleaning up

### Tab Completion

Press TAB after `clearall` to see online player names.

## Clear Locks in Area

### Command

```
/cl cleararea [radius]
```

### Permission

`chestlocklite.admin.cleararea`

### Usage

```
/cl cleararea 20
```

Clears all locks within radius blocks. Default radius is 10 if not specified. Maximum radius is 100.

### Result

- All locked chests in area are unlocked
- Count of cleared locks is shown
- Useful for region cleanup or map resets

### Tab Completion

Press TAB after `cleararea` to see suggested radius values (5, 10, 15, 20, 25, 50, 100).

## Reload Configuration

### Command

```
/cl reload
```

### Permission

`chestlocklite.admin.reload`

### Usage

1. Edit `config.yml` file
2. Run `/cl reload`
3. Configuration is reloaded without restart

### Result

- Configuration reloaded from disk
- No need to restart server
- Useful for testing config changes

### Note

Some changes (like database settings) may require a server restart.

## Admin Bypass

Admins with `chestlocklite.bypass` permission can:
- Open any chest without unlocking
- No need to use admin commands for access
- Useful for inspections and moderation

## Security Considerations

### Logging

All admin actions are logged to console:
- Clear operations show previous owner
- Clear all operations show player and count
- Clear area operations show radius and count

### Best Practices

- Only grant admin permissions to trusted staff
- Review logs regularly for unauthorized actions
- Use specific permissions instead of `chestlocklite.admin.*`
- Document all admin actions for audit trail

### Permission Recommendations

**Junior Admins:**
```
chestlocklite.admin.clear
```

**Senior Admins:**
```
chestlocklite.admin.*
```

**Moderators (read-only):**
```
chestlocklite.bypass
```

## Command Aliases

All commands support these aliases:

| Command | Aliases |
|---------|---------|
| `/cl clear` | `/cl override`, `/cl forceunlock` |
| `/cl clearall` | None |
| `/cl cleararea` | None |
| `/cl reload` | None |

## Examples

**Clear abandoned chest:**
```
/cl clear
```

**Remove all locks from banned player:**
```
/cl clearall BannedPlayer
```

**Clear locks in spawn area:**
```
/cl cleararea 50
```

**Reload config after changes:**
```
/cl reload
```

## Troubleshooting

**Command not working?**
- Check admin permissions
- Verify you're looking at a chest (for clear command)
- Check server console for error messages
- Ensure plugin is enabled

**Can't find player?**
- Use exact player name (case-sensitive)
- Try online players first with TAB completion
- Check database if player is offline

**Clear area not working?**
- Verify radius is within max (100 blocks)
- Make sure you're in the correct world
- Check console for error messages

See also: [Permissions Guide](Permissions.md)

