# ChestLockLite v1.0.1 - Bug Fix Release

## 🐛 Bug Fixes

### GUI Access Fix
- **Fixed:** GUI now only opens when player has an empty hand
- **Issue:** Previously, shift-right-clicking with items (like chests or hoppers) would open the GUI instead of allowing item placement
- **Solution:** Added empty-hand check before opening GUI via shift-right-click
- **Impact:** Prevents accidentally placing chests next to chests or hoppers on chests when trying to open the GUI
- **Note:** The `/cl gui` command still works even when holding items

## 📝 Changes

- Updated GUI access to require empty hand for shift-right-click method
- Improved user experience by preventing accidental item placement
- Documentation updated to reflect the empty-hand requirement

## 🔄 Migration

No migration needed - this is a bug fix that doesn't affect existing data or configuration.

## 📚 Updated Documentation

- README.md - Updated all GUI access instructions
- wiki/GUI-Guide.md - Added empty-hand requirement details
- wiki/Quick-Start.md - Updated GUI instructions

## 📦 Download

**Download**: [ChestLockLite-1.0.1.jar](https://github.com/eldor47/ChestLockLite/releases/download/v1.0.1/ChestLockLite-1.0.1.jar)

## 🔗 Links

- **Report Issues**: [GitHub Issues](https://github.com/eldor47/ChestLockLite/issues)
- **Full Documentation**: [README.md](README.md)
- **Wiki**: [GitHub Wiki](https://github.com/eldor47/ChestLockLite/wiki)

---

**Previous Version**: [v1.0.0 Release Notes](RELEASE_NOTES_v1.0.0.md)

