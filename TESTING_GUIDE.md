# Testing Guide for ChestLockLite

## Quick Minecraft Testing Checklist

### Pre-Testing Setup
1. **Backup your server** (if testing on production)
2. **Use a test server** (recommended)
3. **Download the JAR**: `target/ChestLockLite-1.1.0.jar`
4. **Place in plugins folder** and start server

### Essential Tests (5 minutes)

#### 1. Basic Locking
- [ ] Place a chest → `/cl lock` → Verify only you can open
- [ ] Place a barrel → `/cl lock` → Verify only you can open
- [ ] Place a hopper → `/cl lock` → Verify only you can open (if enabled)
- [ ] Place a furnace → `/cl lock` → Verify only you can open (if enabled)

#### 2. Password GUI Fix
- [ ] Lock a chest → Open GUI → Set Password
- [ ] Click "A" in password GUI → Verify it adds "A" (not "9")
- [ ] Click all letters A-Z → Verify all work correctly
- [ ] Click all numbers 0-9 → Verify all work correctly

#### 3. GUI Empty Hand Requirement
- [ ] Hold an item → Shift-right-click chest → GUI should NOT open
- [ ] Empty hand → Shift-right-click chest → GUI should open
- [ ] `/cl gui` command → Should work even with items

#### 4. Hopper Extraction Prevention
- [ ] Lock a chest → Open GUI → Disable hoppers
- [ ] Place hopper below chest → Verify hopper cannot extract items
- [ ] Re-enable hoppers in GUI → Verify hopper can extract again

#### 5. Admin Notification
- [ ] As admin with bypass → Open locked chest
- [ ] Verify message: "[Admin] This chest is locked by [Owner]"

### Extended Testing (15 minutes)

#### Container Types
- [ ] Test all chest types (regular, trapped, copper)
- [ ] Test barrel locking
- [ ] Test hopper locking (if enabled)
- [ ] Test furnace, blast furnace, smoker (if enabled)

#### GUI Features
- [ ] Test hopper toggle button (slot 20)
- [ ] Test furnace toggle button (slot 24)
- [ ] Verify toggles only visible to owners
- [ ] Test password input GUI thoroughly

#### Edge Cases
- [ ] Double chests - verify both sides lock together
- [ ] Double chests - verify hopper settings apply to both
- [ ] Lock multiple containers in same area
- [ ] Test with password protection + hopper toggles
- [ ] Test trusted players + hopper toggles

## Automated Java Tests

### Running Tests

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=PasswordHasherTest

# Skip tests (not recommended for deployment)
mvn package -DskipTests
```

**Note**: Some tests use Mockito which may have compatibility issues with Java 25. The test configuration includes `-Dnet.bytebuddy.experimental=true` to help with this. If you encounter issues, consider running tests with Java 21 or 22.

### Test Coverage

The test suite covers:

1. **PasswordHasherTest** - Password hashing and verification
   - Basic hashing
   - Password verification
   - Uniqueness (salting)
   - Case sensitivity
   - Edge cases (null, empty, unicode)

2. **MessageUtilsTest** - Message colorization
   - Color code translation
   - Plain text handling
   - Null/empty handling

3. **LockManagerTest** - Container detection
   - Chest detection
   - Barrel detection
   - Hopper detection (when enabled)
   - Furnace detection (when enabled)
   - Location handling

4. **ConfigManagerTest** - Configuration management
   - Config value retrieval
   - Default values
   - New config options

5. **DatabaseMigrationTest** - Database operations
   - Lock creation/removal
   - Password management
   - Hopper/furnace settings
   - Migration compatibility

6. **EdgeCaseTest** - Edge cases and error handling
   - Long passwords
   - Special characters
   - Unicode passwords
   - Disabled features
   - Location key formatting

### Test Execution in CI/CD

Tests run automatically before packaging:
- `mvn clean package` - Runs tests, fails build if tests fail
- `mvn clean install` - Runs tests, installs to local repo
- Tests must pass before JAR is created

### Adding New Tests

1. Create test class in `src/test/java/com/chestlocklite/`
2. Use JUnit 5 (`@Test`, `@BeforeEach`, `@AfterEach`)
3. Use MockBukkit for Bukkit API mocking
4. Follow naming: `*Test.java`
5. Run `mvn test` to verify

### Test Best Practices

- **Isolate tests** - Each test should be independent
- **Clean up** - Use `@AfterEach` to clean up resources
- **Mock dependencies** - Mock plugin, config, etc.
- **Test edge cases** - Null, empty, very long inputs
- **Test both success and failure** - Verify error handling

## Pre-Deployment Checklist

Before deploying to production:

- [ ] All Java tests pass (`mvn test`)
- [ ] Plugin builds successfully (`mvn clean package`)
- [ ] Tested in Minecraft (at least essential tests)
- [ ] Database migration tested (if upgrading)
- [ ] Config options verified
- [ ] No console errors on startup
- [ ] All features work as expected

## Troubleshooting Tests

### Tests Fail to Run
- Check Java version (requires Java 21+)
- Verify Maven dependencies downloaded
- Check MockBukkit version compatibility

### MockBukkit Issues
- Ensure MockBukkit version matches Minecraft version
- Check that `@BeforeEach` and `@AfterEach` are properly set up
- Verify server mocking is correct

### Database Test Issues
- Tests use temporary files (auto-deleted)
- Ensure SQLite JDBC is available
- Check file permissions

---

**Note**: While automated tests are helpful, always test in a real Minecraft server before deploying to production!

