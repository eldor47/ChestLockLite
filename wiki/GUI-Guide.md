# GUI Guide

Complete guide to using the ChestLockLite GUI interface.

## Opening the GUI

### Method 1: Sneak + Right-Click

1. **Shift + Right-click** any chest (while sneaking)
2. GUI opens automatically

### Method 2: Command

```
/cl gui
```

You must be looking at a chest (within 5 blocks).

## Main GUI Menu

### Lock/Unlock Button

- **Locked**: Shows "Unlock Chest" button (green)
- **Unlocked**: Shows "Lock Chest" button (red)
- Click to toggle lock status
- Only owners can unlock their chests

### Password Button

Changes based on context:

- **Owner, No Password**: "Set Password"
- **Owner, Has Password**: "Remove Password"
- **Non-Owner, Password Protected**: "Enter Password"
- **Non-Owner, No Password**: Button hidden

### Trusted Players Button

- **Owner**: Shows "Trusted Players (X)" with count
- **Trusted Player**: Shows "You are Trusted"
- **Other**: Button hidden
- Click to open Trusted Players management GUI

### Info Button

Displays lock information:
- Owner name
- Password status
- Lock status

### Admin Override Button

- Only visible to admins with `chestlocklite.admin.clear` permission
- Click to force unlock any chest
- Logged to console for security

### Close Button

Closes the GUI without making changes.

## Password Input GUI

When setting or entering passwords:

### Character Selection

- **Numbers 0-9**: Click to add to password
- **Letters A-Z**: Click to add to password
- Password displayed in real-time at top

### Controls

- **Clear All**: Remove all characters
- **Backspace**: Remove last character
- **Confirm Password**: Submit password
- **Cancel**: Close without saving

### Tips

- Password shown as you type
- Use Clear All if you make mistakes
- Password length shown in display

## Trusted Players GUI

Access from main GUI by clicking "Trusted Players" button.

### Player List

- Shows all trusted players as player heads
- Displays online/offline status
- Click any head to remove trust
- Automatically refreshes after changes

### Actions

- **Add Trusted Player**: Opens info about using `/cl trust <player>`
- **Back**: Returns to main GUI
- **Close**: Closes all GUIs

## GUI Behavior

### Auto-Refresh

- GUI stays open after locking/unlocking
- Updates button states automatically
- No need to close and reopen

### Permission-Based

- Buttons only show if you have permission
- Admin buttons only visible to admins
- Trusted player indicator only for trusted players

### Context-Aware

- Buttons change based on lock status
- Different options for owners vs non-owners
- Adapts to chest state automatically

## Tips

- Use GUI instead of commands for easier management
- GUI stays open so you can make multiple changes
- Visual feedback helps understand current state
- Color-coded buttons make status clear

## Keyboard Shortcuts

While GUI is open:
- `ESC` or `E` - Close GUI
- `Shift + Q` - Drop item (normal Minecraft behavior)

## Troubleshooting

**GUI won't open?**
- Make sure you're sneaking when Shift + Right-clicking
- Try `/cl gui` command instead
- Check `chestlocklite.gui` permission
- Verify you're looking at a chest

**Buttons not showing?**
- Check permissions for missing features
- Verify you're the owner for owner-only buttons
- Make sure chest is locked for some buttons

**Password GUI issues?**
- Click character buttons to build password
- Use Clear All to start over
- Password appears in real-time display

See also: [Troubleshooting Guide](Troubleshooting.md)

