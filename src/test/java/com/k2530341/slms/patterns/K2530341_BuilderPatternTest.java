package com.k2530341.slms.patterns;

import com.k2530341.slms.patterns.builder.K2530341_BookBuilder;
import com.k2530341.slms.model.book.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for Builder pattern implementation.
 * Author: M.Y.M. SAJIDH (K2530341)
 */
class K2530341_BuilderPatternTest {
    
    @Test
    @DisplayName("Test book builder with all fields")
    void testBookBuilderAllFields() {
        K2530341_Book book = new K2530341_BookBuilder()
            .setBookId("BUILD001")
            .setTitle("Builder Test")
            .setAuthor("Test Author")
            .setCategory("Fiction")
            .setIsbn("111-222-333")
            .setOptionalTags("tag1,tag2")
            .setEdition("First Edition")
            .setBorrowHistoryCount(5)
            .build();
        
        assertEquals("BUILD001", book.getBookId());
        assertEquals("Builder Test", book.getTitle());
        assertEquals("Test Author", book.getAuthor());
        assertEquals("Fiction", book.getCategory());
        assertEquals("111-222-333", book.getIsbn());
        assertEquals("tag1,tag2", book.getOptionalTags());
        assertEquals("First Edition", book.getEdition());
        assertEquals(5, book.getBorrowHistoryCount());
    }
    
    @Test
    @DisplayName("Test book builder with minimal fields")
    void testBookBuilderMinimalFields() {
        K2530341_Book book = new K2530341_BookBuilder()
            .setBookId("BUILD002")
            .setTitle("Minimal Book")
            .setAuthor("Author")
            .build();
        
        assertEquals("BUILD002", book.getBookId());
        assertEquals("Minimal Book", book.getTitle());
        assertEquals("Author", book.getAuthor());
        assertEquals("", book.getOptionalTags());
        assertEquals("", book.getEdition());
    }
    
    @Test
    @DisplayName("Test builder fails without required fields")
    void testBuilderFailsWithoutRequired() {
        assertThrows(IllegalStateException.class, () -> {
            new K2530341_BookBuilder()
                .setTitle("No ID Book")
                .build();
        });
    }
}
