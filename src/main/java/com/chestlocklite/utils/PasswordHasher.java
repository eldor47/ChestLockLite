package com.chestlocklite.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordHasher {
    
    private static final SecureRandom random = new SecureRandom();
    private static final String ALGORITHM = "SHA-256";
    private static final int SALT_LENGTH = 16;
    
    /**
     * Hash a password with a random salt
     * @param password The plain text password
     * @return A string in format: salt:hash (both base64 encoded)
     */
    public static String hashPassword(String password) {
        try {
            // Generate random salt
            byte[] salt = new byte[SALT_LENGTH];
            random.nextBytes(salt);
            
            // Hash password with salt
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            md.update(salt);
            byte[] hash = md.digest(password.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            
            // Encode salt and hash to base64
            String saltBase64 = Base64.getEncoder().encodeToString(salt);
            String hashBase64 = Base64.getEncoder().encodeToString(hash);
            
            return saltBase64 + ":" + hashBase64;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }
    
    /**
     * Verify a password against a stored hash
     * @param password The plain text password to verify
     * @param storedHash The stored hash in format: salt:hash
     * @return true if password matches, false otherwise
     */
    public static boolean verifyPassword(String password, String storedHash) {
        try {
            // Split salt and hash
            String[] parts = storedHash.split(":", 2);
            if (parts.length != 2) {
                // Legacy plain text password - for backwards compatibility
                return password.equals(storedHash);
            }
            
            String saltBase64 = parts[0];
            String hashBase64 = parts[1];
            
            // Decode salt and hash
            byte[] salt = Base64.getDecoder().decode(saltBase64);
            byte[] storedHashBytes = Base64.getDecoder().decode(hashBase64);
            
            // Hash the provided password with the stored salt
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            md.update(salt);
            byte[] computedHash = md.digest(password.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            
            // Compare hashes
            return MessageDigest.isEqual(storedHashBytes, computedHash);
        } catch (Exception e) {
            // Fallback to plain text comparison for backwards compatibility
            return password.equals(storedHash);
        }
    }
    
    /**
     * Check if a stored hash is in the new format (hashed)
     * @param storedHash The stored hash to check
     * @return true if it's a hashed password, false if it's plain text
     */
    public static boolean isHashed(String storedHash) {
        return storedHash != null && storedHash.contains(":") && storedHash.split(":").length == 2;
    }
}

