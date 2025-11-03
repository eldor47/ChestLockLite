# Troubleshooting Guide

Common issues and solutions for ChestLockLite.

## General Issues

### Plugin Won't Load

**Symptoms:**
- Plugin not showing in `/plugins`
- Error in console on startup

**Solutions:**
- Check Java version (requires Java 21+): `java -version`
- Verify Spigot/Paper version (requires 1.21.10+)
- Check server logs for detailed error messages
- Ensure all dependencies are present
- Verify plugin file is not corrupted

### Plugin Disables on Startup

**Symptoms:**
- Plugin loads then disables
- Error message in console

**Solutions:**
- Check database file permissions
- Verify `plugins/ChestLockLite/` folder is writable
- Check config.yml syntax (valid YAML)
- Review server logs for specific errors

## Locking Issues

### Can't Lock Chest

**Symptoms:**
- `/cl lock` command doesn't work
- No error message

**Solutions:**
- Ensure you're looking directly at chest (within 5 blocks)
- Check `chestlocklite.lock` or `chestlocklite.use` permission
- Verify locking is enabled in config: `allow-locking: true`
- Check if you've reached max chest limit: `max-chests-per-player`
- Try `/cl info` to see if chest is already locked

### Chest Auto-Unlocks

**Symptoms:**
- Chest locks but immediately unlocks
- Lock doesn't persist

**Solutions:**
- Check database file permissions
- Verify SQLite database is working
- Check server logs for database errors
- Ensure database file is not corrupted

### Double Chest Issues

**Symptoms:**
- Only one side locks
- Both sides not protected

**Solutions:**
- This is normal - locking one side locks both
- Both sides share the same lock
- Check if other side is a different chest type
- Verify chests are actually connected

## Password Issues

### Password Not Working

**Symptoms:**
- Can't unlock with correct password
- Password command doesn't work

**Solutions:**
- Verify password is correct (case-sensitive)
- Check password length (default: 4-32 characters)
- Ensure password is set: `/cl info`
- Try unlocking again after entering password
- Check if on cooldown: `password-cooldown` in config
- Use GUI to enter password instead

### Can't Set Password

**Symptoms:**
- `/cl password` doesn't set password
- No error message

**Solutions:**
- Ensure chest is locked first
- Check `chestlocklite.password` permission
- Verify passwords are enabled: `allow-passwords: true`
- Check password length requirements
- Ensure you're the owner of the chest

### Password Cooldown Issues

**Symptoms:**
- Cooldown too long
- Can't enter password due to cooldown

**Solutions:**
- Adjust `password-cooldown` in config (default: 3 seconds)
- Set to 0 to disable (not recommended)
- Wait for cooldown to expire
- Successful password clears cooldown

## GUI Issues

### GUI Won't Open

**Symptoms:**
- Shift + Right-click does nothing
- `/cl gui` doesn't work

**Solutions:**
- Ensure you're sneaking (holding Shift)
- Try `/cl gui` command instead
- Check `chestlocklite.gui` permission
- Verify you're looking at a chest (within 5 blocks)
- Check if chest is an ender chest (not supported)

### GUI Buttons Missing

**Symptoms:**
- Some buttons don't show
- Admin buttons missing

**Solutions:**
- Check specific permissions for missing features
- Verify you're the owner for owner-only buttons
- Admin buttons require admin permissions
- Some buttons only show when applicable

### Password Input GUI Issues

**Symptoms:**
- Can't enter characters
- Password not displaying

**Solutions:**
- Click character buttons to build password
- Check center display for current password
- Use "Clear All" to start over
- Use "Backspace" to remove last character
- Click "Confirm Password" when done

## Trusted Players Issues

### Can't Trust Player

**Symptoms:**
- `/cl trust` command fails
- Player not added to list

**Solutions:**
- Ensure chest is locked first
- Verify `chestlocklite.trust` permission
- Check if max trusted players reached: `max-trusted-players-per-chest`
- Player must be online for command (or use GUI)
- Verify trusted players enabled: `allow-trusted-players: true`

### Trusted Player Can't Access

**Symptoms:**
- Trusted player still can't open chest
- Access denied message

**Solutions:**
- Verify player is on trusted list: `/cl trustedlist`
- Check if chest is still locked
- Verify player UUID matches (name changes don't affect UUID)
- Ensure trusted players system is enabled
- Try re-adding player to trust list

## Permission Issues

### Permission Denied

**Symptoms:**
- Commands say "no permission"
- Features don't work

**Solutions:**
- Check permission node in permission plugin
- Verify permission plugin is loaded
- Check group/player has correct permission
- Test with OP temporarily to isolate issue
- Review permission plugin configuration

### Admin Commands Not Working

**Symptoms:**
- Admin commands don't work
- "No permission" error

**Solutions:**
- Verify `chestlocklite.admin.*` or specific admin permission
- Check if OP is interfering (may need to remove OP)
- Ensure permission plugin recognizes permission
- Test with direct permission grant

## Database Issues

### Database Errors

**Symptoms:**
- Database locked errors
- Data not saving

**Solutions:**
- Ensure server is stopped before accessing database
- Close all SQLite browser tools
- Check database file permissions
- Verify disk space available
- Check for .db-shm and .db-wal files

### Lost Locks

**Symptoms:**
- Locks disappear
- Chests become unlocked unexpectedly

**Solutions:**
- Restore from backup
- Check database integrity: `PRAGMA integrity_check;`
- Verify database file is not corrupted
- Check server logs for database errors
- Review recent admin commands in logs

## Performance Issues

### Server Lag

**Symptoms:**
- Server performance drops
- TPS decreases

**Solutions:**
- Check database size (run `VACUUM` if large)
- Reduce `max-chests-per-player` if too high
- Limit `max-trusted-players-per-chest`
- Optimize database indexes
- Check for database locks/contention

### Slow Commands

**Symptoms:**
- Commands take time to execute
- GUI slow to open

**Solutions:**
- Optimize database (vacuum, reindex)
- Check database file size
- Verify indexes are being used
- Check server performance overall
- Review database query performance

## Configuration Issues

### Config Not Loading

**Symptoms:**
- Config changes don't apply
- Default values always used

**Solutions:**
- Use `/cl reload` or restart server
- Verify YAML syntax is correct
- Check config file permissions
- Ensure config file is in correct location
- Review server logs for config errors

### Config Values Wrong

**Symptoms:**
- Settings don't match config
- Behavior unexpected

**Solutions:**
- Restart server after config changes
- Or use `/cl reload` command
- Verify YAML indentation (spaces, not tabs)
- Check for typos in config keys
- Compare with default config

## Getting Help

### Information to Provide

When reporting issues, include:
- Server version (Spigot/Paper and Minecraft version)
- Java version
- ChestLockLite version
- Error messages from console/logs
- Steps to reproduce issue
- Configuration file (sanitized)
- Permission setup

### Log Files

Check these log files:
- `logs/latest.log` - General server logs
- `logs/debug.log` - Debug information (if enabled)
- Console output - Real-time errors

### Debug Mode

Enable debug mode in config (if available) for more detailed logging.

## See Also

- [Configuration Reference](Configuration.md)
- [Database Management](Database.md)
- [Permissions Guide](Permissions.md)

