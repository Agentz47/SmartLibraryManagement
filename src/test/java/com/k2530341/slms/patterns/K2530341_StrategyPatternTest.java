package com.k2530341.slms.patterns;

import com.k2530341.slms.patterns.strategy.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for Strategy pattern implementation.
 * Author: M.Y.M. SAJIDH (K2530341)
 */
class K2530341_StrategyPatternTest {
    
    @Test
    @DisplayName("Test student fine strategy")
    void testStudentFineStrategy() {
        K2530341_FineStrategy strategy = new K2530341_StudentFineStrategy();
        
        assertEquals(50.0, strategy.getFinePerDay());
        assertEquals(0.0, strategy.calculateFine(0));
        assertEquals(50.0, strategy.calculateFine(1));
        assertEquals(500.0, strategy.calculateFine(10));
    }
    
    @Test
    @DisplayName("Test faculty fine strategy")
    void testFacultyFineStrategy() {
        K2530341_FineStrategy strategy = new K2530341_FacultyFineStrategy();
        
        assertEquals(20.0, strategy.getFinePerDay());
        assertEquals(0.0, strategy.calculateFine(0));
        assertEquals(20.0, strategy.calculateFine(1));
        assertEquals(200.0, strategy.calculateFine(10));
    }
    
    @Test
    @DisplayName("Test guest fine strategy")
    void testGuestFineStrategy() {
        K2530341_FineStrategy strategy = new K2530341_GuestFineStrategy();
        
        assertEquals(100.0, strategy.getFinePerDay());
        assertEquals(0.0, strategy.calculateFine(0));
        assertEquals(100.0, strategy.calculateFine(1));
        assertEquals(1000.0, strategy.calculateFine(10));
    }
}
