package com.k2530341.slms.patterns;

import com.k2530341.slms.patterns.state.*;
import com.k2530341.slms.model.book.*;
import com.k2530341.slms.patterns.builder.K2530341_BookBuilder;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for State pattern implementation.
 * Author: M.Y.M. SAJIDH (K2530341)
 */
class K2530341_StatePatternTest {
    
    @Test
    @DisplayName("Test state transitions")
    void testStateTransitions() {
        K2530341_Book book = new K2530341_BookBuilder()
            .setBookId("STATE001")
            .setTitle("State Test Book")
            .setAuthor("Author")
            .setCategory("Test")
            .setIsbn("789")
            .build();
        
        // Initial state: Available
        assertTrue(book.getState() instanceof K2530341_AvailableState);
        assertEquals(K2530341_AvailabilityStatus.AVAILABLE, book.getAvailabilityStatus());
        
        // Transition to Borrowed
        book.performBorrow();
        assertTrue(book.getState() instanceof K2530341_BorrowedState);
        assertEquals(K2530341_AvailabilityStatus.BORROWED, book.getAvailabilityStatus());
        
        // Transition back to Available
        book.performReturn();
        assertTrue(book.getState() instanceof K2530341_AvailableState);
        assertEquals(K2530341_AvailabilityStatus.AVAILABLE, book.getAvailabilityStatus());
        
        // Transition to Reserved
        book.performReserve();
        assertTrue(book.getState() instanceof K2530341_ReservedState);
        assertEquals(K2530341_AvailabilityStatus.RESERVED, book.getAvailabilityStatus());
    }
}
