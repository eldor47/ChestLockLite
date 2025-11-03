# Trusted Players System

The trusted players system allows chest owners to grant specific players access to their locked chests without needing passwords.

## Overview

- Trusted players can open locked chests without passwords
- Each chest has its own list of trusted players
- Maximum trusted players per chest is configurable (default: 20)
- Works seamlessly with password protection

## Adding Trusted Players

### Method 1: Using Command

```
/cl trust PlayerName
```

The player must be online when you trust them via command. They will receive a notification.

### Method 2: Using GUI

1. Lock your chest: `/cl lock` or via GUI
2. Open GUI: Shift + Right-click chest
3. Click "Trusted Players" button
4. Click "Add Trusted Player" button
5. Run `/cl trust <player>` when prompted

## Viewing Trusted Players

### Command

```
/cl trustedlist
```

Shows a list of all trusted players with their online/offline status:

```
=== Trusted Players ===
- FriendPlayer [Online]
- AnotherFriend [Offline]
```

### GUI

1. Open GUI: Shift + Right-click chest
2. Click "Trusted Players" button
3. View all trusted players in the list

## Removing Trust

### Method 1: Using GUI

1. Open Trusted Players GUI
2. Click on any player head to remove trust

### Method 2: Using Command

```
/cl untrust PlayerName
```

Works even if the player is offline. Looks up the player by name in the database.

## Access Control Priority

When accessing a chest, the system checks in this order:

1. **Owner** - Always has full access
2. **Admin Bypass** - Admins with `chestlocklite.bypass` permission
3. **Trusted Players** - Can access without passwords
4. **Password** - Anyone with correct password
5. **Denied** - No access

## Trusted Players GUI

The Trusted Players GUI shows:

- All trusted players with player heads
- Online/offline status for each player
- Click any player head to remove trust
- "Add Trusted Player" button (links to command usage)
- Automatically refreshes after removing players

## Configuration

### Enable/Disable

In `config.yml`:

```yaml
locks:
  allow-trusted-players: true
```

### Maximum Trusted Players

```yaml
locks:
  max-trusted-players-per-chest: 20
```

Set to -1 for unlimited (not recommended for performance).

## Use Cases

- **Shared Storage**: Trust teammates or friends for shared resources
- **No Password Sharing**: Grant access without sharing passwords
- **Temporary Access**: Trust and untrust players as needed
- **Guild Chests**: Trust multiple guild members

## Best Practices

- Trust only players you know well
- Regularly review your trusted players list
- Use passwords for public chests instead
- Consider limiting max trusted players per chest
- Remove trust when no longer needed

## Troubleshooting

**Can't trust a player?**
- Make sure the chest is locked first
- Verify the player name is correct
- Check if you've reached max trusted players limit
- Ensure trusted players system is enabled in config

**Trusted player can't access?**
- Verify they're on the trusted list: `/cl trustedlist`
- Check access control priority (passwords may still be required if configured)
- Ensure the chest is still locked

See also: [Troubleshooting Guide](Troubleshooting.md)

