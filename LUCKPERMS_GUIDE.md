# ChestLockLite - LuckPerms Permission Guide

This guide shows you how to set up ChestLockLite permissions using LuckPerms.

## Quick Setup

### Basic Setup (All Players)
Give all players basic chest locking permissions:
```
/lp group default permission set chestlocklite.use true
```

This gives players:
- `chestlocklite.lock` - Lock chests
- `chestlocklite.unlock` - Unlock their own chests
- `chestlocklite.gui` - Use GUI menu
- `chestlocklite.info` - View lock information

### VIP Setup (With Passwords)
Give VIP players password protection:
```
/lp group vip permission set chestlocklite.use true
/lp group vip permission set chestlocklite.password true
```

### Moderator Setup
Give moderators ability to clear locks:
```
/lp group moderator permission set chestlocklite.use true
/lp group moderator permission set chestlocklite.admin.clear true
/lp group moderator permission set chestlocklite.admin.clearall true
```

### Admin Setup (Full Access)
Give admins all permissions:
```
/lp group admin permission set chestlocklite.* true
```

Or individually:
```
/lp group admin permission set chestlocklite.use true
/lp group admin permission set chestlocklite.admin true
/lp group admin permission set chestlocklite.bypass true
```

## Permission Structure

### Player Permissions

| Permission | Description | Default |
|------------|-------------|---------|
| `chestlocklite.use` | Grants basic use permissions (includes lock, unlock, gui, info) | `true` |
| `chestlocklite.lock` | Lock chests | `true` |
| `chestlocklite.unlock` | Unlock own chests | `true` |
| `chestlocklite.password` | Set and use passwords | `true` |
| `chestlocklite.gui` | Use GUI menu | `true` |
| `chestlocklite.info` | View lock information | `true` |
| `chestlocklite.bypass` | Bypass all locks (open any chest) | `false` |

### Admin Permissions

| Permission | Description | Default |
|------------|-------------|---------|
| `chestlocklite.admin` | All admin permissions | `op` |
| `chestlocklite.admin.clear` | Clear individual chest locks | `op` |
| `chestlocklite.admin.clearall` | Clear all locks by player | `op` |
| `chestlocklite.admin.cleararea` | Clear locks in area | `op` |
| `chestlocklite.admin.reload` | Reload plugin configuration | `op` |

### Wildcard Permission

| Permission | Description | Default |
|------------|-------------|---------|
| `chestlocklite.*` | All permissions | `op` |

## Example Configurations

### Configuration 1: Basic Server
**Default players:** Can lock/unlock chests
**VIP:** Can also use passwords
**Admin:** Full access

```bash
# Default players
/lp group default permission set chestlocklite.use true

# VIP players
/lp group vip permission set chestlocklite.use true
/lp group vip permission set chestlocklite.password true

# Admins
/lp group admin permission set chestlocklite.* true
```

### Configuration 2: Restricted Server
**Default players:** Cannot lock chests
**VIP:** Can lock chests
**Moderator:** Can clear locks
**Admin:** Full access

```bash
# Default players (no permissions)
# (Don't grant chestlocklite.use)

# VIP players
/lp group vip permission set chestlocklite.use true
/lp group vip permission set chestlocklite.password true

# Moderators
/lp group moderator permission set chestlocklite.use true
/lp group moderator permission set chestlocklite.admin.clear true
/lp group moderator permission set chestlocklite.admin.clearall true

# Admins
/lp group admin permission set chestlocklite.* true
```

### Configuration 3: Open Server
**All players:** Can lock/unlock and use passwords
**Moderators:** Can clear locks
**Admins:** Full access + bypass

```bash
# All players
/lp group default permission set chestlocklite.use true
/lp group default permission set chestlocklite.password true

# Moderators
/lp group moderator permission set chestlocklite.use true
/lp group moderator permission set chestlocklite.password true
/lp group moderator permission set chestlocklite.admin.clear true
/lp group moderator permission set chestlocklite.admin.clearall true

# Admins
/lp group admin permission set chestlocklite.* true
/lp group admin permission set chestlocklite.bypass true
```

## Permission Inheritance

ChestLockLite uses permission inheritance:

- `chestlocklite.use` includes:
  - `chestlocklite.lock`
  - `chestlocklite.unlock`
  - `chestlocklite.gui`
  - `chestlocklite.info`

- `chestlocklite.admin` includes:
  - `chestlocklite.admin.clear`
  - `chestlocklite.admin.clearall`
  - `chestlocklite.admin.cleararea`
  - `chestlocklite.admin.reload`

- `chestlocklite.*` includes everything

## Per-Player Permissions

You can also set permissions for individual players:

```bash
# Give player "Steve" password access
/lp user Steve permission set chestlocklite.password true

# Give player "Bob" admin clear permission
/lp user Bob permission set chestlocklite.admin.clear true

# Remove player "Alice" ability to lock chests
/lp user Alice permission set chestlocklite.lock false
```

## Checking Permissions

Check what permissions a player has:
```bash
/lp user <player> info
/lp group <group> info
```

Check if a specific permission is granted:
```bash
/lp user <player> haspermission chestlocklite.admin
```

## Troubleshooting

### Player can't lock chests
- Check: `/lp user <player> haspermission chestlocklite.lock`
- Grant: `/lp user <player> permission set chestlocklite.use true`

### Player can't use GUI
- Check: `/lp user <player> haspermission chestlocklite.gui`
- Grant: `/lp user <player> permission set chestlocklite.gui true`

### Admin can't clear locks
- Check: `/lp user <player> haspermission chestlocklite.admin.clear`
- Grant: `/lp group admin permission set chestlocklite.admin true`

### Permission not working
1. Check LuckPerms is loaded: `/lp info`
2. Reload LuckPerms: `/lp reload`
3. Check player's effective permissions: `/lp user <player> listperms`
4. Check plugin is loaded: `/plugins`

## Advanced: Using LuckPerms Contexts

You can restrict permissions by world or server:

```bash
# Only allow locking in survival world
/lp group default permission set chestlocklite.use true world=survival

# Disable locking in creative world
/lp group default permission set chestlocklite.use false world=creative

# Only allow admin commands on specific server
/lp group admin permission set chestlocklite.admin true server=lobby
```

## Migration from Other Permission Plugins

If you're migrating from PermissionsEx or another plugin:

1. Export your permissions list
2. Convert to LuckPerms format:
   - `pex.*` → `chestlocklite.*`
   - `pex.use` → `chestlocklite.use`
   - etc.

3. Import into LuckPerms or manually set using commands

## Need Help?

- Check LuckPerms wiki: https://luckperms.net/wiki/
- Check LuckPerms commands: `/lp`
- Check plugin permissions: `/lp group <group> listperms`
- Check player permissions: `/lp user <player> listperms`

