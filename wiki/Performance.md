# Performance Tips

Optimization guide for ChestLockLite on high-traffic servers.

## Database Optimization

### Regular Maintenance

Run these SQLite commands monthly:

```sql
VACUUM;
REINDEX;
ANALYZE;
```

Or use SQLite browser tools to optimize.

### Database Size Management

- Monitor database file size
- Remove orphaned locks periodically
- Clean up old trusted player entries
- Archive old locks if needed

## Configuration Optimization

### Limit Chest Counts

```yaml
locks:
  max-chests-per-player: 50  # Lower for performance
  max-trusted-players-per-chest: 10  # Lower for performance
```

### Disable Unused Features

```yaml
locks:
  allow-passwords: false  # If not used
  allow-trusted-players: false  # If not used
```

### Reduce Backup Frequency

```yaml
database:
  backup-interval: 48  # Less frequent backups
```

## Server Performance

### TPS Considerations

- Each lock operation is a database write
- Password verification is CPU-intensive (hashing)
- GUI operations are lightweight
- Trusted player checks are fast (indexed queries)

### Best Practices

1. **Limit Max Chests**: Prevent players from creating thousands of chests
2. **Regular Cleanup**: Remove abandoned locks periodically
3. **Monitor Database**: Check database size regularly
4. **Optimize Periodically**: Run VACUUM monthly

## Scaling Tips

### Small Servers (< 100 players)

- Default settings work well
- No optimization needed typically

### Medium Servers (100-500 players)

- Consider limiting max chests per player
- Run database optimization monthly
- Monitor database size

### Large Servers (500+ players)

- Lower max chests per player (20-30)
- Limit trusted players per chest (5-10)
- Regular database optimization (weekly)
- Monitor database size closely
- Consider periodic cleanup of old locks

## Monitoring

### Key Metrics

- Database file size
- Number of locks in database
- Average locks per player
- Database query performance

### Tools

- SQLite browser for database inspection
- Server performance monitoring plugins
- Database query analyzers

## Optimization Checklist

- [ ] Database optimized (VACUUM)
- [ ] Indexes verified
- [ ] Max chest limits set appropriately
- [ ] Unused features disabled
- [ ] Regular maintenance scheduled
- [ ] Database size monitored
- [ ] Backup strategy in place

## See Also

- [Database Management](Database.md)
- [Configuration Reference](Configuration.md)

