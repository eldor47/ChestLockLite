# Permissions Guide

Complete guide to ChestLockLite permissions and how to set them up.

## Permission Overview

ChestLockLite uses a hierarchical permission system for fine-grained access control.

## Player Permissions

### Basic Permissions

| Permission | Description | Default |
|------------|-------------|---------|
| `chestlocklite.use` | Basic chest locking features | `true` |
| `chestlocklite.lock` | Lock chests | `true` |
| `chestlocklite.unlock` | Unlock own chests | `true` |
| `chestlocklite.password` | Set and use passwords | `true` |
| `chestlocklite.trust` | Trust players on your chests | `true` |
| `chestlocklite.gui` | Use GUI menu | `true` |
| `chestlocklite.info` | View lock information | `true` |

### Special Permissions

| Permission | Description | Default |
|------------|-------------|---------|
| `chestlocklite.bypass` | Bypass all chest locks | `false` |

## Admin Permissions

| Permission | Description | Default | Includes |
|------------|-------------|---------|----------|
| `chestlocklite.admin` | All admin commands | `op` | clear, clearall, cleararea, reload |
| `chestlocklite.admin.clear` | Clear individual chest locks | `op` | - |
| `chestlocklite.admin.clearall` | Clear all locks by player | `op` | - |
| `chestlocklite.admin.cleararea` | Clear locks in area | `op` | - |
| `chestlocklite.admin.reload` | Reload configuration | `op` | - |
| `chestlocklite.*` | All permissions | `op` | All |

## Permission Hierarchy

```
chestlocklite.*
├── chestlocklite.use
│   ├── chestlocklite.lock
│   ├── chestlocklite.unlock
│   ├── chestlocklite.password
│   ├── chestlocklite.trust
│   ├── chestlocklite.gui
│   └── chestlocklite.info
├── chestlocklite.bypass
└── chestlocklite.admin
    ├── chestlocklite.admin.clear
    ├── chestlocklite.admin.clearall
    ├── chestlocklite.admin.cleararea
    └── chestlocklite.admin.reload
```

## Setting Up with LuckPerms

### Basic Setup (All Players)

```
/lp group default permission set chestlocklite.use true
```

### VIP Setup (With Passwords)

```
/lp group vip permission set chestlocklite.use true
/lp group vip permission set chestlocklite.password true
```

### Admin Setup (Full Access)

```
/lp group admin permission set chestlocklite.* true
```

### Moderator Setup (Bypass Only)

```
/lp group moderator permission set chestlocklite.bypass true
```

### Custom Setup Example

```
# Trusted group - can lock and trust players
/lp group trusted permission set chestlocklite.use true
/lp group trusted permission set chestlocklite.trust true

# Basic group - can only lock, no passwords
/lp group default permission set chestlocklite.use true
/lp group default permission set chestlocklite.password false
```

## Setting Up with OP

**Note**: OP gives all permissions by default. Not recommended for production servers.

Players with OP automatically have:
- All `chestlocklite.*` permissions
- Can bypass locks
- Can use all admin commands

## Permission Strategies

### Strategy 1: Default Allow

Give everyone basic access, restrict specific features:

```
# Everyone can lock chests
/lp group default permission set chestlocklite.use true

# VIPs get passwords
/lp group vip permission set chestlocklite.password true
```

### Strategy 2: Default Deny

Restrict locking to specific groups:

```
# Default group cannot lock
/lp group default permission set chestlocklite.use false

# Members can lock
/lp group member permission set chestlocklite.use true
```

### Strategy 3: Tiered Access

Different tiers with different features:

```
# Tier 1: Basic locking
/lp group tier1 permission set chestlocklite.lock true

# Tier 2: Basic + Passwords
/lp group tier2 permission set chestlocklite.* true
/lp group tier2 permission set chestlocklite.password false

# Tier 3: Everything
/lp group tier3 permission set chestlocklite.* true
```

## Common Permission Sets

### Server Owner

```
chestlocklite.*
```

### Moderator

```
chestlocklite.bypass
chestlocklite.admin.clear
```

### Trusted Player

```
chestlocklite.use
chestlocklite.trust
```

### Regular Player

```
chestlocklite.use
```

### New Player (Restricted)

```
chestlocklite.info
```

## Troubleshooting

**Player can't lock chests?**
- Check `chestlocklite.use` or `chestlocklite.lock` permission
- Verify permission plugin is working
- Check if OP is overriding permissions

**Admin commands not working?**
- Verify `chestlocklite.admin.*` or specific admin permission
- Check if player has OP (may need to remove)
- Ensure permission plugin is loaded

**Bypass not working?**
- Verify `chestlocklite.bypass` permission
- Check permission plugin configuration
- Ensure no conflicting permissions

## Best Practices

1. **Use Specific Permissions**: Instead of `chestlocklite.*`, grant only needed permissions
2. **Document Changes**: Keep track of permission changes for audit
3. **Test Permissions**: Test with test accounts before applying to players
4. **Regular Review**: Periodically review who has admin permissions
5. **Use Groups**: Organize permissions using permission groups rather than per-player

## Integration with Other Plugins

### WorldGuard Integration

You can use WorldGuard to protect regions where chest locking is allowed/denied.

### GriefPrevention Integration

ChestLockLite permissions work independently of GriefPrevention claims.

### EssentialsX

If using EssentialsX `/lock` command, ChestLockLite will work alongside it without conflicts.

## See Also

- [LuckPerms Setup Guide](../LUCKPERMS_GUIDE.md)
- [Admin Commands](Admin-Commands.md)

