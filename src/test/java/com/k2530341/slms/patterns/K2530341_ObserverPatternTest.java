package com.k2530341.slms.patterns;

import com.k2530341.slms.patterns.observer.*;
import com.k2530341.slms.model.book.*;
import com.k2530341.slms.model.user.*;
import com.k2530341.slms.patterns.builder.K2530341_BookBuilder;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for Observer pattern implementation.
 * Author: M.Y.M. SAJIDH (K2530341)
 */
class K2530341_ObserverPatternTest {
    
    @Test
    @DisplayName("Test observer attach and notify")
    void testObserverAttachAndNotify() {
        K2530341_Book book = new K2530341_BookBuilder()
            .setBookId("OBS001")
            .setTitle("Observer Test Book")
            .setAuthor("Author")
            .setCategory("Test")
            .setIsbn("123")
            .build();
        
        K2530341_User user = new K2530341_User("U001", "Test User", 
            "test@test.com", "123", K2530341_MembershipType.STUDENT, 0);
        
        book.attach(user);
        book.notifyObservers("TEST_EVENT", "Test message");
        
        assertEquals(1, user.getNotifications().size());
        assertTrue(user.getNotifications().get(0).contains("TEST_EVENT"));
    }
    
    @Test
    @DisplayName("Test observer detach")
    void testObserverDetach() {
        K2530341_Book book = new K2530341_BookBuilder()
            .setBookId("OBS002")
            .setTitle("Observer Test Book 2")
            .setAuthor("Author")
            .setCategory("Test")
            .setIsbn("456")
            .build();
        
        K2530341_User user = new K2530341_User("U002", "Test User 2", 
            "test2@test.com", "456", K2530341_MembershipType.STUDENT, 0);
        
        book.attach(user);
        book.detach(user);
        book.notifyObservers("TEST_EVENT", "Test message");
        
        assertEquals(0, user.getNotifications().size());
    }
}
