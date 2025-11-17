package com.chestlocklite.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for PasswordHasher utility
 */
class PasswordHasherTest {

    @Test
    void testHashPassword() {
        String password = "testPassword123";
        String hash = PasswordHasher.hashPassword(password);
        
        assertNotNull(hash);
        assertFalse(hash.isEmpty());
        assertNotEquals(password, hash); // Should be hashed, not plain text
        assertTrue(hash.contains(":")); // Should contain salt:hash format
    }

    @Test
    void testVerifyPassword() {
        String password = "testPassword123";
        String hash = PasswordHasher.hashPassword(password);
        
        // Correct password should verify
        assertTrue(PasswordHasher.verifyPassword(password, hash));
        
        // Wrong password should not verify
        assertFalse(PasswordHasher.verifyPassword("wrongPassword", hash));
    }

    @Test
    void testHashPasswordUniqueness() {
        String password = "samePassword";
        String hash1 = PasswordHasher.hashPassword(password);
        String hash2 = PasswordHasher.hashPassword(password);
        
        // Each hash should be unique due to salt
        assertNotEquals(hash1, hash2);
        
        // But both should verify
        assertTrue(PasswordHasher.verifyPassword(password, hash1));
        assertTrue(PasswordHasher.verifyPassword(password, hash2));
    }

    @Test
    void testVerifyPasswordCaseSensitive() {
        String password = "TestPassword123";
        String hash = PasswordHasher.hashPassword(password);
        
        assertTrue(PasswordHasher.verifyPassword("TestPassword123", hash));
        assertFalse(PasswordHasher.verifyPassword("testpassword123", hash));
        assertFalse(PasswordHasher.verifyPassword("TESTPASSWORD123", hash));
    }

    @Test
    void testVerifyPasswordWithNull() {
        String password = "testPassword";
        String hash = PasswordHasher.hashPassword(password);
        
        // PasswordHasher.verifyPassword doesn't handle null password - it will throw NullPointerException
        assertThrows(NullPointerException.class, () -> {
            PasswordHasher.verifyPassword(null, hash);
        });
        
        // Null hash falls back to plain text comparison (backwards compatibility)
        // This will return false since password != null
        assertFalse(PasswordHasher.verifyPassword(password, null));
    }
}

