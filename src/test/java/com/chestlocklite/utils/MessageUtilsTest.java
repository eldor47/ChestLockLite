package com.chestlocklite.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for MessageUtils
 */
class MessageUtilsTest {

    @Test
    void testColorizeBasic() {
        String input = "&aHello &bWorld";
        String result = MessageUtils.colorize(input);
        
        assertNotNull(result);
        assertNotEquals(input, result); // Should be different after colorization
        assertTrue(result.contains("Hello"));
        assertTrue(result.contains("World"));
    }

    @Test
    void testColorizeNoColorCodes() {
        String input = "Plain text without color codes";
        String result = MessageUtils.colorize(input);
        
        assertNotNull(result);
        assertEquals(input, result); // Should be the same without color codes
    }

    @Test
    void testColorizeNull() {
        // MessageUtils.colorize doesn't handle null - it will throw IllegalArgumentException
        // This test verifies the actual behavior
        assertThrows(IllegalArgumentException.class, () -> {
            MessageUtils.colorize(null);
        });
    }

    @Test
    void testColorizeEmpty() {
        String result = MessageUtils.colorize("");
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testColorizeMultipleCodes() {
        String input = "&aGreen &cRed &eYellow &bBlue";
        String result = MessageUtils.colorize(input);
        
        assertNotNull(result);
        assertNotEquals(input, result);
        assertTrue(result.contains("Green"));
        assertTrue(result.contains("Red"));
        assertTrue(result.contains("Yellow"));
        assertTrue(result.contains("Blue"));
    }
}

