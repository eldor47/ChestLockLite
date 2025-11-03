# Password Protection

Learn how to use password protection to share chest access securely.

## Overview

Password protection allows chest owners to set passwords that anyone can use to unlock their chests. Passwords are securely hashed and stored in the database.

## Setting a Password

### Method 1: Using GUI

1. Lock your chest: `/cl lock` or via GUI
2. Open GUI: Shift + Right-click chest
3. Click "Set Password"
4. Use the password input GUI to enter your password
5. Click "Confirm Password"

### Method 2: Using Command

```
/cl password mypassword123
```

The password must be between 4-32 characters (configurable in config).

## Unlocking with Password

### For Non-Owners

#### Method 1: Using GUI

1. Look at a password-protected chest
2. Shift + Right-click to open GUI
3. Click "Enter Password"
4. Use the password input GUI to enter the password
5. Click "Confirm Password"
6. You can now open the chest

#### Method 2: Using Command

```
/cl password mypassword123
```

Once unlocked, you can open the chest normally.

## Password Input GUI

The password input GUI provides an interactive way to enter passwords:

- **Numbers 0-9**: Click to add numbers
- **Letters A-Z**: Click to add letters
- **Clear All**: Remove all characters
- **Backspace**: Remove last character
- **Confirm Password**: Submit the password
- **Cancel**: Close without saving

The password is displayed in real-time as you build it.

## Password Security

### How Passwords Are Stored

- Passwords are **never** stored in plain text
- Each password is hashed using SHA-256 with a unique salt
- Format: `salt:hash` (both base64 encoded)
- Even server admins cannot recover passwords

### Password Verification

- When you enter a password, it's hashed and compared to the stored hash
- The original password cannot be recovered from the hash
- If you forget a password, it cannot be recovered (you must remove it)

## Removing a Password

### Method 1: Using GUI

1. Open GUI: Shift + Right-click chest
2. Click "Remove Password"

### Method 2: Using Command

```
/cl removepassword
```

Only the chest owner can remove passwords.

## Password Cooldown

To prevent spam attempts, there's a cooldown between password tries:

- Default: 3 seconds (configurable)
- Applies to failed password attempts
- Successful passwords clear the cooldown
- Cooldown message shows remaining time

## Tips

- Use strong passwords (mix of letters and numbers)
- Share passwords securely with trusted players
- Consider using [Trusted Players](Trusted-Players.md) for regular access instead
- Passwords are case-sensitive
- Password length must be between 4-32 characters by default

## Troubleshooting

**Password not working?**
- Check if password is case-sensitive
- Verify password length requirements
- Make sure you're using `/cl password <password>` correctly
- Try using the GUI instead

**Forgot password?**
- Only the owner can remove passwords
- If you're the owner, use `/cl removepassword` then set a new one
- Passwords cannot be recovered

See also: [Troubleshooting Guide](Troubleshooting.md)

