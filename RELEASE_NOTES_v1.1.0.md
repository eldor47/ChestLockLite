# ChestLockLite v1.1.0 - Major Feature Update

## 🎉 New Features

### Container Support Expansion
- **Barrel Support** - You can now lock barrels! Full support for all barrel locking features.
- **Hopper Support** - Lock hoppers to protect your items (configurable, default: enabled)
- **Furnace Support** - Lock furnaces, blast furnaces, and smokers (configurable, default: enabled)
- All new container types support the same features as chests: passwords, trusted players, GUI, etc.

### Hopper & Furnace Control
- **Per-Container Toggle** - Control hopper and furnace access per locked container via GUI
- **Server-Wide Config** - Enable/disable hopper and furnace support globally in config.yml
- **Hopper Extraction Prevention** - Prevent hoppers from extracting items from locked containers when disabled
- **GUI Toggles** - Easy-to-use buttons in the lock GUI to enable/disable hopper/furnace access

### Admin Enhancements
- **Admin Notifications** - Admins with bypass permission now see a notification when opening locked containers
- Shows: "[Admin] This chest is locked by [Owner]"
- Configurable via `admin-notification` setting in config.yml

## 🐛 Bug Fixes

### Password GUI Fix
- **Fixed:** Clicking "A" in password GUI was adding "9" instead
- **Cause:** Slot conflict between number buttons (9-18) and letter buttons (starting at 18)
- **Solution:** Moved letter buttons to start at slot 19, numbers remain in slots 9-18
- All letters and numbers now work correctly!

### GUI Access Fix
- **Fixed:** Shift-right-click with items would open GUI instead of allowing item placement
- **Solution:** GUI now only opens when player has empty hand (prevents placing chests/hoppers accidentally)
- `/cl gui` command still works even with items in hand

## 🔧 Configuration Changes

### New Config Options
```yaml
locks:
  # Support for hoppers (allow locking hoppers)
  allow-hoppers: true
  
  # Support for furnaces (allow locking furnaces, blast furnaces, smokers)
  allow-furnaces: true
  
  # Show notification to admins when they open locked chests
  admin-notification: true
```

## 📦 Database Changes

### Schema Updates
- Added `hopper_enabled` column (default: 1, enabled)
- Added `furnace_enabled` column (default: 1, enabled)
- **Automatic Migration** - Existing databases are automatically migrated on upgrade
- All existing locks default to hoppers and furnaces enabled

## 🎮 GUI Updates

### New GUI Buttons
- **Hopper Toggle** (slot 20) - Owner only, enables/disables hopper access per container
- **Furnace Toggle** (slot 24) - Owner only, enables/disables furnace access per container
- Buttons only appear when:
  - Container is locked
  - Player is the owner
  - Feature is enabled in server config

## 🔄 Migration Guide

### For Server Admins
1. **Backup your database** (recommended but not required)
2. Update to v1.1.0
3. Start your server
4. Migration runs automatically - check console for confirmation
5. All existing locks will have hoppers/furnaces enabled by default
6. Configure new settings in `config.yml` if needed

### For Players
- No action required!
- All existing locks continue to work
- New features are available immediately
- Use GUI to toggle hopper/furnace settings per container

## 📋 Full Changelog

### Added
- Support for locking barrels
- Support for locking hoppers (configurable)
- Support for locking furnaces, blast furnaces, smokers (configurable)
- Per-container hopper access toggle in GUI
- Per-container furnace access toggle in GUI
- Hopper extraction prevention system
- Admin notification when opening locked containers
- Automatic database migration for upgrades
- New config options: `allow-hoppers`, `allow-furnaces`, `admin-notification`

### Fixed
- Password GUI: Clicking "A" now correctly adds "A" instead of "9"
- GUI access: Shift-right-click now requires empty hand to prevent accidental item placement
- Slot conflict between number and letter buttons in password GUI

### Changed
- `LockManager.isChest()` still exists for chest-specific operations
- New `LockManager.isLockableContainer()` method for all container types
- Database schema updated with new columns
- Version bumped to 1.1.0

### Technical Details
- Added `HopperListener` for inventory move event handling
- Enhanced `DatabaseManager` with migration system
- Updated `LockGUI` with new toggle buttons
- Improved `ConfigManager` with new config getters

## 🚀 Upgrade Instructions

1. **Stop your server**
2. **Backup your database** (optional but recommended):
   - Location: `plugins/ChestLockLite/locks.db`
3. **Replace the plugin JAR** with v1.1.0
4. **Start your server**
5. **Check console** for migration messages:
   - Should see: "Database migrated: Added hopper_enabled column"
   - Should see: "Database migrated: Added furnace_enabled column"
6. **Configure** new settings in `config.yml` if desired
7. **Reload** config with `/cl reload` or restart server

## ⚠️ Breaking Changes

**None!** This update is fully backward compatible. All existing functionality continues to work.

## 📝 Notes

- Hoppers and furnaces are enabled by default for all existing locks
- You can disable them per-container via GUI or server-wide via config
- The password GUI fix resolves the issue where some letters were mapped incorrectly
- Admin notifications help admins understand which containers are locked

## 🔗 Related

- **Previous Version**: [v1.0.1 Release Notes](RELEASE_NOTES_v1.0.1.md)
- **Initial Release**: [v1.0.0 Release Notes](RELEASE_NOTES_v1.0.0.md)

---

**Download**: [ChestLockLite-1.1.0.jar](https://github.com/eldor47/ChestLockLite/releases/download/v1.1.0/ChestLockLite-1.1.0.jar)

**Report Issues**: [GitHub Issues](https://github.com/eldor47/ChestLockLite/issues)

**Full Documentation**: [README.md](README.md)

