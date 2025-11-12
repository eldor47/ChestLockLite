# ChestLockLite - Simple Chest Locking Plugin

[![Spigot](https://img.shields.io/badge/Spigot-1.21-blue.svg)](https://www.spigotmc.org/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

A lightweight, simple chest locking plugin for Spigot 1.21+ servers. Lock chests with owner protection or password protection, with full double-chest support and SQLite storage.

## Features

- **GUI Menu** - Visual interface for managing chest locks
- **Password Input GUI** - Interactive character selection GUI for entering passwords
- **Owner Locks** - Lock chests so only you can open them
- **Auto-Lock on Place** - Automatically lock chests when placed (configurable)
- **Password Protection** - Set passwords that anyone can use to unlock
- **Secure Password Storage** - Passwords are hashed with SHA-256 and salt
- **Password Cooldown** - Prevents password spam attempts (configurable)
- **Trusted Players System** - Trust specific players to access your locked chests
- **Double Chest Support** - Automatically handles single and double chests
- **All Chest Types** - Supports regular chests, trapped chests, copper chests, and all variants (except ender chests)
- **SQLite Storage** - Fast, lightweight, no MySQL required
- **Player Limits** - Configurable maximum chests per player
- **Visual Feedback** - Particles and messages
- **Admin Tools** - Admins can unlock any chest with override command
- **High Performance** - Optimized database queries with indexes
- **Easy Access** - Shift + Right-click (empty-handed) to open GUI menu
- **Tab Completion** - Full autocomplete support for all commands

## Requirements

- Java 21 or higher
- Spigot/Paper 1.21.10+
- SQLite (included in plugin)

## Installation

1. **Download** the latest `ChestLockLite.jar` from releases
2. **Place** the jar in your server's `plugins` folder
3. **Start** your server to generate the config file
4. **Stop** your server
5. **Edit** `plugins/ChestLockLite/config.yml` if needed (optional)
6. **Start** your server again

## Commands

### Player Commands

| Command | Description | Permission |
|---------|-------------|------------|
| `/chestlock gui` or `/cl gui` | Open lock management GUI | `chestlocklite.gui` |
| `/chestlock lock` or `/cl lock` | Lock the chest you're looking at | `chestlocklite.lock` |
| `/chestlock unlock` or `/cl unlock` | Unlock your chest | `chestlocklite.unlock` |
| `/chestlock password <password>` | Set password on your chest | `chestlocklite.password` |
| `/chestlock password <password>` | Unlock password-protected chest | `chestlocklite.password` |
| `/chestlock removepassword` | Remove password from your chest | `chestlocklite.password` |
| `/chestlock trust <player>` | Trust a player on your chest | `chestlocklite.trust` |
| `/chestlock untrust <player>` | Remove trust from a player | `chestlocklite.trust` |
| `/chestlock trustedlist` | List trusted players on your chest | `chestlocklite.trust` |
| `/chestlock info` | Show lock information | `chestlocklite.info` |

### Admin Commands

| Command | Description | Permission |
|---------|-------------|------------|
| `/chestlock clear` | Clear lock on targeted chest | `chestlocklite.admin.clear` |
| `/chestlock clearall <player>` | Clear all locks by a player | `chestlocklite.admin.clearall` |
| `/chestlock cleararea [radius]` | Clear locks in area | `chestlocklite.admin.cleararea` |
| `/chestlock reload` | Reload configuration | `chestlocklite.admin.reload` |

**Aliases:** `/cl`, `/lockchest`

**Command Aliases:**
- `/cl clear`, `/cl override`, `/cl forceunlock` - Clear single chest lock
- `/cl gui`, `/cl menu` - Open GUI menu
- `/cl removepassword`, `/cl removepwd` - Remove password
- `/cl trustedlist`, `/cl trusted`, `/cl trustlist` - List trusted players

**Tab Completion:** All commands support tab completion! Press `TAB` to see available options.

**GUI Access:** Shift + Right-click a chest (with empty hand) to open the GUI menu!

## Usage Examples

### Using the GUI (Recommended!)

1. **Shift + Right-click** a chest with an empty hand (or use `/cl gui` while looking at one)
2. Click buttons in the menu to lock/unlock, set passwords, etc.
3. Easy visual interface - no need to remember commands!

### Locking a Chest (Owner Only)

**Method 1: Auto-Lock on Place (if enabled)**
1. Place a chest in the world
2. Chest is automatically locked to you!
3. No command needed - just place and go!

**Method 2: Using GUI**
1. Shift + Right-click the chest with an empty hand (or `/cl gui`)
2. Click "Lock Chest" button

**Method 3: Using Command**
1. Look at the chest you want to lock
2. Run `/cl lock` (or press TAB to autocomplete)
3. Only you can now open this chest

```
Player: /cl lock
Server: Chest locked successfully!
Server: Tip: Use /cl password <password> to add password protection!
```

**Note:** If `auto-lock-on-place` is enabled in config, chests are automatically locked when placed!

### Setting a Password

**Method 1: Using GUI**
1. Lock your chest first: `/cl lock` or via GUI
2. Open GUI: Shift + Right-click chest (with empty hand)
3. Click "Set Password" button
4. Use the password input GUI to enter your password
5. Click "Confirm Password" when done

**Method 2: Using Command**
1. Lock your chest first: `/cl lock`
2. Set a password: `/cl password mysecret123`
3. Anyone with the password can now open it

```
Player: /cl password mysecret123
Server: Password set successfully!
```

### Unlocking with Password

**Method 1: Using GUI (for non-owners)**
1. Look at a password-protected chest
2. Shift + Right-click with empty hand to open GUI
3. Click "Enter Password" button
4. Use the password input GUI to enter the password
5. Click "Confirm Password" when done
6. You can now open the chest!

**Method 2: Using Command**
1. Look at a password-protected chest
2. Run `/cl password mysecret123`
3. You can now open the chest

```
Player: /cl password mysecret123
Server: Password accepted! You can now open this chest.
```

### Password Input GUI

When setting or entering a password via GUI, you'll see an interactive character selection interface:

- **Numbers 0-9**: Click to add numbers to your password
- **Letters A-Z**: Click to add letters to your password
- **Clear All**: Remove all characters
- **Backspace**: Remove last character
- **Confirm Password**: Set/unlock with entered password
- **Cancel**: Close without saving

The password is displayed in **real-time** as you build it, so you can see exactly what you're entering!

### Trusting Players

**Method 1: Using GUI (Recommended!)**
1. Lock your chest first: `/cl lock` or via GUI
2. Open GUI: Shift + Right-click chest (with empty hand)
3. Click "Trusted Players" button
4. Click "Add Trusted Player" button (or use `/cl trust <player>`)
5. Click on a player head to remove trust

**Method 2: Using Command**
1. Lock your chest first: `/cl lock`
2. Trust a player: `/cl trust PlayerName`
3. That player can now open your chest without a password!

```
Player: /cl trust FriendPlayer
Server: Successfully trusted FriendPlayer on this chest!
FriendPlayer: PlayerName has trusted you on one of their chests!
```

**Listing Trusted Players:**
```
Player: /cl trustedlist
Server: === Trusted Players ===
Server: - FriendPlayer [Online]
Server: - AnotherFriend [Offline]
```

**Removing Trust:**
```
Player: /cl untrust FriendPlayer
Server: Removed trust from FriendPlayer on this chest!
```

**Note:** Trusted players can access locked chests just like the owner, without needing passwords!

### Checking Lock Info

```
Player: /cl info
Server: === Chest Lock Info ===
Server: Owner: PlayerName
Server: This is your chest.
Server: Password: Set
```

### Admin: Clearing Locks

**Clear single chest:**
```
Admin: /cl clear
Server: [Admin] Chest lock cleared!
Server: Previous owner: PlayerName
```

**Clear all locks by player:**
```
Admin: /cl clearall PlayerName
Server: [Admin] Cleared 5 lock(s) owned by PlayerName
```

**Clear locks in area:**
```
Admin: /cl cleararea 20
Server: [Admin] Cleared 3 lock(s) in area (radius: 20 blocks)
```

Tab completion works for admin commands too! Try `/cl clearall ` and press TAB to see online players.

## GUI Menu

ChestLockLite includes a GUI menu for easy chest management.

### Opening the GUI

- **Shift + Right-click** any chest (while sneaking and empty-handed)
- Or use `/cl gui` while looking at a chest
- **Note:** You must have an empty hand to open the GUI via Shift + Right-click (prevents accidentally placing items)

### GUI Features

- **Lock/Unlock Button** - Instantly lock or unlock chests
- **Password Management** - Set, remove, or enter passwords
  - **Owners**: See "Set Password" or "Remove Password" buttons
  - **Non-owners**: See "Enter Password" button on password-protected chests
- **Trusted Players** - Manage trusted players who can access your chest
  - **Owners**: See "Trusted Players (X)" button showing count
  - **Trusted Players**: See "You are Trusted" indicator
  - Click to open dedicated trusted players management GUI
- **Info Display** - View lock information
- **Admin Override** - Admins can force unlock any chest (shown if admin)
- **Visual Feedback** - Color-coded buttons show current status

### GUI Layout

```
┌─────────────────────────┐
│   Chest Lock Menu       │
├─────────────────────────┤
│  [Lock/Unlock]          │
│  [Password]             │
│  [Trusted Players]      │
│  [Info]                 │
│                         │
│  [Admin Override]       │
│  [Close]                │
└─────────────────────────┘
```

### Password Input GUI Layout

```
┌─────────────────────────────────┐
│      Enter Password              │
│    [Current Password Display]    │
├─────────────────────────────────┤
│ Numbers: 0 1 2 3 4 5 6 7 8 9   │
│ Letters: A B C D E F G H I J... │
│          K L M N O P Q R S T... │
│          U V W X Y Z            │
├─────────────────────────────────┤
│ [Clear] [Backspace] [Confirm] │
│ [Cancel]                        │
└─────────────────────────────────┘
```

### Trusted Players GUI Layout

```
┌─────────────────────────────────┐
│      Trusted Players             │
├─────────────────────────────────┤
│ [Player Head 1] [Player Head 2] │
│ [Player Head 3] [Player Head 4] │
│ ...                             │
├─────────────────────────────────┤
│ [Add Trusted Player]            │
│ [Back] [Close]                  │
└─────────────────────────────────┘
```

## Permissions

### Player Permissions

| Permission | Description | Default | Inherits |
|------------|-------------|---------|----------|
| `chestlocklite.use` | Basic chest locking features | `true` | - |
| `chestlocklite.lock` | Lock chests | `true` | - |
| `chestlocklite.unlock` | Unlock own chests | `true` | - |
| `chestlocklite.password` | Set and use passwords | `true` | - |
| `chestlocklite.trust` | Trust players on your chests | `true` | - |
| `chestlocklite.gui` | Use GUI menu | `true` | - |
| `chestlocklite.info` | View lock information | `true` | - |
| `chestlocklite.bypass` | Bypass all chest locks | `false` | - |

### Admin Permissions

| Permission | Description | Default | Inherits |
|------------|-------------|---------|----------|
| `chestlocklite.admin` | All admin commands | `op` | clear, clearall, cleararea, reload |
| `chestlocklite.admin.clear` | Clear individual chest locks | `op` | - |
| `chestlocklite.admin.clearall` | Clear all locks by player | `op` | - |
| `chestlocklite.admin.cleararea` | Clear locks in area | `op` | - |
| `chestlocklite.admin.reload` | Reload configuration | `op` | - |
| `chestlocklite.*` | All permissions | `op` | All |

### Permission Hierarchy

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

### LuckPerms Setup

**Basic setup (all players):**
```
/lp group default permission set chestlocklite.use true
```

**VIP setup (with passwords):**
```
/lp group vip permission set chestlocklite.use true
/lp group vip permission set chestlocklite.password true
```

**Admin setup (full access):**
```
/lp group admin permission set chestlocklite.* true
```

See **LUCKPERMS_GUIDE.md** for detailed permission configurations!

## Configuration

Edit `plugins/ChestLockLite/config.yml`:

```yaml
# Database Settings
database:
  # SQLite database file name
  filename: "locks.db"
  
  # Auto-backup interval in hours (0 to disable)
  backup-interval: 24

# Lock Settings
locks:
  # Allow players to lock chests
  allow-locking: true
  
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
  
  # Allow trusted players system
  allow-trusted-players: true
  
  # Maximum trusted players per chest
  max-trusted-players-per-chest: 20

# Messages
messages:
  lock-success: "&aChest locked successfully!"
  unlock-success: "&aChest unlocked successfully!"
  password-set: "&aPassword set successfully!"
  password-removed: "&aPassword removed successfully!"
  # ... and more!

# Visual Settings
visual:
  # Show lock particles when chest is opened
  show-particles: true
  
  # Particle effect type (FLAME, HEART, VILLAGER_HAPPY, etc.)
  particle-type: "VILLAGER_HAPPY"
```

## Database

ChestLockLite uses SQLite for storage. The database file is located at:
```
plugins/ChestLockLite/locks.db
```

**Database Schema:**
- `chest_locks` table stores all lock information
- `trusted_players` table stores trusted player relationships
- Both tables indexed by world and coordinates for fast lookups
- Automatically handles double chests by storing primary location
- Trusted players are automatically removed when chests are unlocked

**Backup:** The database is backed up automatically based on your `backup-interval` setting.

## Security

### Password Storage

- **Hashed Passwords**: All passwords are hashed using SHA-256 with a unique salt before storage
- **Format**: Passwords are stored as `salt:hash` (both base64 encoded)
- **Security**: Even if the database is compromised, passwords cannot be easily recovered
- **Backwards Compatible**: Existing plain text passwords still work until changed

### Password Security Features

- **SHA-256 Hashing**: Industry-standard cryptographic hash function
- **Unique Salt**: Each password gets a random salt (prevents rainbow table attacks)
- **Secure Storage**: Passwords are never stored in plain text
- **No Recovery**: Passwords cannot be "decrypted" - only verified

### Important Security Notes

- **Admin Bypass**: Only trusted admins should have `chestlocklite.bypass` permission
- **Database Access**: Protect your database file from unauthorized access
- **Permissions**: Use fine-grained permissions to control access
- **Rate Limiting**: Max chests per player prevents abuse
- **Admin OP Warning**: Players with OP may have issues with Minecraft's default permissions - use permission system instead

## How It Works

### Auto-Lock on Place

When `auto-lock-on-place` is enabled in config:
- Chests are automatically locked when placed
- Lock is assigned to the player who placed the chest
- Works for both single and double chests
- Respects chest limits and permissions
- Shows success message after placement
- Still requires `chestlocklite.lock` permission

**Configuration:**
```yaml
locks:
  auto-lock-on-place: true  # Enable auto-lock
```

### Single Chests
- Locking stores the chest location in the database
- Only the owner can open (unless password is set)
- Can be auto-locked on placement if enabled in config

### Double Chests
- Automatically detects double chests
- Stores the "left" chest location as primary
- Locking either side locks both sides
- Both sides are protected by the same lock
- Auto-lock works on double chests too - placing either side locks both

### Supported Chest Types
- **Regular Chests** (`CHEST`) - Standard wooden chests
- **Trapped Chests** (`TRAPPED_CHEST`) - Redstone-triggered chests
- **Copper Chests** (`COPPER_CHEST`) - Copper variant chests (if available)
- **All Chest Variants** - Any block that implements the Chest interface
- **Ender Chests** (`ENDER_CHEST`) - Excluded (has their own storage system)

The plugin automatically detects all chest types using the `Chest` interface, making it compatible with future chest variants without code changes.

### Password Protection

**Setting Passwords:**
- Owner can set a password: `/cl password <password>` or via GUI
- Password is hashed with SHA-256 and salt before storage
- Password is stored securely in the database

**Unlocking with Password:**
- Anyone can unlock with the password: `/cl password <password>` or via GUI
- Password is verified against the stored hash
- Password is cached temporarily after successful unlock
- **Cooldown System**: Prevents spam attempts (default: 3 seconds, configurable)
  - Wrong password attempts trigger cooldown
  - Correct passwords clear cooldown
  - Cooldown message shows remaining time
- Owner can remove password: `/cl removepassword` or via GUI

**Password Input GUI:**
- Owners: Click "Set Password" to open password input GUI
- Non-owners: Click "Enter Password" to open password input GUI
- Interactive character selection interface
- Real-time password display as you type
- Supports numbers (0-9) and letters (A-Z)

### Trusted Players System

**How It Works:**
- Owners can trust specific players on their locked chests
- Trusted players can open locked chests without needing passwords
- Each chest has its own list of trusted players
- Works seamlessly with password protection (trusted players don't need passwords)
- Maximum trusted players per chest is configurable (default: 20)

**Adding Trusted Players:**
- `/cl trust <player>` - Trust a player on your chest
- Or use GUI: Click "Trusted Players" → "Add Trusted Player"
- Player must be online to trust via command
- Player receives a notification when trusted

**Managing Trusted Players:**
- `/cl trustedlist` - List all trusted players on your chest
- Shows online/offline status for each trusted player
- Click player heads in GUI to remove trust
- `/cl untrust <player>` - Remove trust from a player (works even if player is offline)

**Access Control Priority:**
1. Owner - Always has full access
2. Admin Bypass - Admins with `chestlocklite.bypass` permission
3. Trusted Players - Can access without passwords
4. Password - Anyone with correct password
5. Denied - No access

**Trusted Players GUI:**
- Shows all trusted players with player heads
- Displays online/offline status
- Click any player head to remove trust
- "Add Trusted Player" button links to command usage
- Automatically refreshes after removing players

### Admin Features
- **Clear Single Lock**: Admins can clear a lock on a specific chest
  - `/cl clear` - Clear lock on targeted chest
  - Or click "Admin: Force Unlock" button in GUI
- **Clear All Locks by Player**: Bulk clear all locks owned by a player
  - `/cl clearall <player>` - Clears all locks owned by that player
  - Works with online or offline players (looks up by name in database)
  - Tab completion suggests online player names
- **Clear Locks in Area**: Clear locks within a radius
  - `/cl cleararea [radius]` - Clears all locks within radius (default: 10 blocks, max: 100)
  - Example: `/cl cleararea 20` clears locks within 20 blocks
  - Tab completion suggests common radius values (5, 10, 15, 20, 25, 50, 100)
- **Bypass Permission**: Admins with `chestlocklite.bypass` can open any chest without unlocking
- **All admin actions are logged to console** for security and audit purposes

### GUI System
- Access via **Shift + Right-click** (with empty hand) or `/cl gui`
- Shows different options based on lock status and permissions
- Visual feedback with color-coded buttons
- Admin override button only visible to admins
- Password buttons adapt based on ownership and lock status
- **Empty hand required** for Shift + Right-click to prevent accidental item placement

### Tab Completion
- **First Argument**: Shows all available subcommands filtered by permissions
- **Second Argument**: 
  - `clearall` - Suggests online player names
  - `cleararea` - Suggests common radius values (5, 10, 15, 20, 25, 50, 100)
  - `trust` / `untrust` - Suggests online player names
- **Smart Filtering**: Only shows commands you have permission to use
- **Partial Matching**: Filters suggestions as you type

## Building from Source

Requirements:
- Maven 3.6+
- Java 21+

```bash
# Clone the repository
git clone https://github.com/yourusername/chestlocklite.git
cd chestlocklite

# Build with Maven
mvn clean package

# Find the jar in target/ChestLockLite-1.0.1.jar
```

## Troubleshooting

### Chest won't lock
- Make sure you're looking directly at the chest (within 5 blocks)
- Check if you've reached the max chest limit
- Verify locking is enabled in config

### Double chest issues
- Both sides of a double chest are automatically linked
- Locking either side locks both sides
- This is intentional behavior

### Password not working
- Make sure you're using the exact password (case-sensitive)
- Check password length requirements (default: 4-32 characters)
- Try unlocking again after entering password
- If using GUI, make sure you clicked "Confirm Password" after entering

### GUI not opening
- Make sure you have `chestlocklite.gui` permission
- Try using `/cl gui` command instead of Shift + Right-click
- Check if you're sneaking when using Shift + Right-click
- **Make sure your hand is empty** - GUI only opens when holding nothing (prevents placing items)

### Password input GUI issues
- Make sure you're clicking the character buttons correctly
- Password is displayed in real-time - check the center display
- Use "Clear All" if you need to start over
- Use "Backspace" to remove last character

### Database errors
- Check file permissions on `plugins/ChestLockLite/` folder
- Ensure the plugin folder is writable
- Check server logs for detailed error messages

### Performance issues
- SQLite handles thousands of locks efficiently
- Database is indexed for fast lookups
- If you have issues, enable debug mode in config

### Admin OP issues
- **Important**: If a player with OP creates a chest, Minecraft's default permissions may prevent non-admins from opening it
- Remove OP from players who shouldn't have it
- Use the permission system instead of OP for better control

## Performance

- **SQLite**: Fast local storage, no external database needed
- **Indexed Queries**: O(log n) lookup time
- **Minimal Overhead**: Only checks chests when players interact
- **Tested**: Handles 10,000+ locks without issues
- **Password Hashing**: SHA-256 hashing is fast and efficient

## Contributing

Contributions are welcome! Please:

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test thoroughly
5. Submit a pull request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Credits

- Built for Spigot/Paper Minecraft servers
- Uses SQLite JDBC for database operations
- Following QuestLogs plugin structure
- Password hashing using Java's built-in MessageDigest

## Support

- **Issues**: [GitHub Issues](https://github.com/yourusername/chestlocklite/issues)
- **Discord**: [Your Discord Server](#)
- **Wiki**: [GitHub Wiki](https://github.com/yourusername/chestlocklite/wiki)

---

*Not affiliated with Mojang or Microsoft*
