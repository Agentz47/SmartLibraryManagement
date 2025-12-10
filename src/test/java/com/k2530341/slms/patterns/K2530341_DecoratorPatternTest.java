package com.k2530341.slms.patterns;

import com.k2530341.slms.patterns.decorator.*;
import com.k2530341.slms.model.book.*;
import com.k2530341.slms.patterns.builder.K2530341_BookBuilder;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for Decorator pattern implementation.
 * Author: M.Y.M. SAJIDH (K2530341)
 */
class K2530341_DecoratorPatternTest {
    
    @Test
    @DisplayName("Test basic book component")
    void testBasicBookComponent() {
        K2530341_Book book = new K2530341_BookBuilder()
            .setBookId("DEC001")
            .setTitle("Decorator Test")
            .setAuthor("Author")
            .setCategory("Test")
            .setIsbn("123")
            .build();
        
        K2530341_BookComponent component = new K2530341_BasicBook(book);
        
        assertEquals("Decorator Test by Author", component.getDescription());
        assertEquals(0, component.getPriority());
    }
    
    @Test
    @DisplayName("Test featured decorator")
    void testFeaturedDecorator() {
        K2530341_Book book = new K2530341_BookBuilder()
            .setBookId("DEC002")
            .setTitle("Featured Book")
            .setAuthor("Author")
            .setCategory("Test")
            .setIsbn("456")
            .build();
        
        K2530341_BookComponent component = new K2530341_BasicBook(book);
        K2530341_BookComponent featured = new K2530341_FeaturedDecorator(component);
        
        assertTrue(featured.getDescription().contains("FEATURED"));
        assertEquals(10, featured.getPriority());
    }
    
    @Test
    @DisplayName("Test recommended decorator")
    void testRecommendedDecorator() {
        K2530341_Book book = new K2530341_BookBuilder()
            .setBookId("DEC003")
            .setTitle("Recommended Book")
            .setAuthor("Author")
            .setCategory("Test")
            .setIsbn("789")
            .build();
        
        K2530341_BookComponent component = new K2530341_BasicBook(book);
        K2530341_BookComponent recommended = new K2530341_RecommendedDecorator(component);
        
        assertTrue(recommended.getDescription().contains("RECOMMENDED"));
        assertEquals(5, recommended.getPriority());
    }
    
    @Test
    @DisplayName("Test multiple decorators")
    void testMultipleDecorators() {
        K2530341_Book book = new K2530341_BookBuilder()
            .setBookId("DEC004")
            .setTitle("Multi Decorator Book")
            .setAuthor("Author")
            .setCategory("Test")
            .setIsbn("000")
            .build();
        
        K2530341_BookComponent component = new K2530341_BasicBook(book);
        K2530341_BookComponent decorated = new K2530341_FeaturedDecorator(
            new K2530341_RecommendedDecorator(component)
        );
        
        assertTrue(decorated.getDescription().contains("FEATURED"));
        assertTrue(decorated.getDescription().contains("RECOMMENDED"));
        assertEquals(15, decorated.getPriority()); // 0 + 5 + 10
    }
}
