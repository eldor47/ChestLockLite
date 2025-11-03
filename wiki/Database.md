# Database Management

Guide to managing the ChestLockLite SQLite database.

## Database Location

```
plugins/ChestLockLite/locks.db
```

## Database Schema

### chest_locks Table

Stores all chest lock information.

| Column | Type | Description |
|--------|------|-------------|
| id | INTEGER | Primary key |
| world | TEXT | World name |
| x | INTEGER | X coordinate |
| y | INTEGER | Y coordinate |
| z | INTEGER | Z coordinate |
| owner_uuid | TEXT | Owner UUID |
| owner_name | TEXT | Owner name |
| password_hash | TEXT | Password hash (salt:hash) |
| created_at | TIMESTAMP | Lock creation time |
| updated_at | TIMESTAMP | Last update time |

### trusted_players Table

Stores trusted player relationships.

| Column | Type | Description |
|--------|------|-------------|
| id | INTEGER | Primary key |
| world | TEXT | World name |
| x | INTEGER | X coordinate |
| y | INTEGER | Y coordinate |
| z | INTEGER | Z coordinate |
| trusted_uuid | TEXT | Trusted player UUID |
| trusted_name | TEXT | Trusted player name |
| created_at | TIMESTAMP | Trust creation time |

### Indexes

- `idx_locks_location` on `chest_locks(world, x, y, z)`
- `idx_trusted_location` on `trusted_players(world, x, y, z)`

## Backup

### Automatic Backup

Configure in `config.yml`:

```yaml
database:
  backup-interval: 24  # hours
```

Backups are stored as:
```
plugins/ChestLockLite/backups/locks_YYYY-MM-DD_HH-MM-SS.db
```

### Manual Backup

1. Stop your server
2. Copy `locks.db` to a safe location
3. Restore by replacing the file and restarting

## Database Operations

### Viewing Database

Use SQLite browser tools:
- DB Browser for SQLite (free)
- SQLiteStudio (free)
- Command line: `sqlite3 locks.db`

### Querying Locks

```sql
-- All locks
SELECT * FROM chest_locks;

-- Locks by player
SELECT * FROM chest_locks WHERE owner_name = 'PlayerName';

-- Locks in world
SELECT * FROM chest_locks WHERE world = 'world';
```

### Querying Trusted Players

```sql
-- All trusted relationships
SELECT * FROM trusted_players;

-- Trusted players for a chest
SELECT * FROM trusted_players 
WHERE world = 'world' AND x = 100 AND y = 64 AND z = 200;
```

## Maintenance

### Vacuum Database

To optimize database size:

```sql
VACUUM;
```

### Reindex

To rebuild indexes:

```sql
REINDEX;
```

### Analyze

To update query planner statistics:

```sql
ANALYZE;
```

## Common Tasks

### Remove Orphaned Locks

Locks that reference non-existent chests (manual cleanup):

```sql
-- This requires custom verification - be careful!
-- Verify chests exist before deleting
```

### Find Duplicate Locks

```sql
SELECT world, x, y, z, COUNT(*) 
FROM chest_locks 
GROUP BY world, x, y, z 
HAVING COUNT(*) > 1;
```

### Count Locks by Player

```sql
SELECT owner_name, COUNT(*) as lock_count
FROM chest_locks
GROUP BY owner_name
ORDER BY lock_count DESC;
```

## Performance

### Optimization Tips

1. **Regular Vacuum**: Run `VACUUM` monthly
2. **Index Usage**: Indexes are auto-created and maintained
3. **Query Efficiency**: Use indexed columns (world, x, y, z) in WHERE clauses
4. **Backup Strategy**: Regular backups prevent data loss

### Database Size

- Typical lock record: ~200 bytes
- Typical trusted record: ~150 bytes
- 10,000 locks ≈ 2MB database size
- Efficient for thousands of locks

### When to Optimize

- Database file > 50MB
- Slow query performance
- After bulk deletions
- Monthly maintenance

## Migration

### Upgrading Plugin

The database schema is versioned and auto-migrates on plugin startup. Always backup before upgrading.

### Exporting Data

```bash
# Export to SQL
sqlite3 locks.db .dump > backup.sql

# Export to CSV
sqlite3 -header -csv locks.db "SELECT * FROM chest_locks;" > locks.csv
```

### Importing Data

```bash
# Import from SQL
sqlite3 locks.db < backup.sql
```

## Troubleshooting

**Database locked error?**
- Make sure server is stopped
- Close all SQLite browsers/tools
- Check for .db-shm and .db-wal files (SQLite WAL mode)

**Corrupted database?**
- Restore from backup
- Use SQLite's integrity check: `PRAGMA integrity_check;`
- May need to export data and recreate database

**Performance issues?**
- Run `VACUUM`
- Check index usage with `EXPLAIN QUERY PLAN`
- Verify indexes exist: `.indexes`

**Missing locks?**
- Check database file exists
- Verify file permissions
- Check server logs for errors
- Restore from backup if needed

## Security

### File Permissions

- Set appropriate file permissions on database file
- Restrict access to server admins only
- Protect backup files as well

### Password Storage

- Passwords are hashed (SHA-256 + salt)
- Cannot be recovered from database
- Salt prevents rainbow table attacks

### Backup Security

- Encrypt backups if sensitive
- Store backups securely
- Regular backup rotation

## Advanced Topics

### Custom Queries

The database uses standard SQLite format. You can write custom queries for:
- Analytics
- Reporting
- Bulk operations
- Data export

### Integration

Database can be accessed by:
- Other plugins (via API)
- External tools
- Reporting systems
- Backup systems

## See Also

- [Configuration Reference](Configuration.md)
- [Troubleshooting Guide](Troubleshooting.md)

