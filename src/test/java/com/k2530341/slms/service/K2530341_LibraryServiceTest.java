package com.k2530341.slms.service;

import com.k2530341.slms.model.book.*;
import com.k2530341.slms.model.user.*;
import com.k2530341.slms.model.K2530341_Borrow;
import com.k2530341.slms.model.notification.K2530341_Notification;
import com.k2530341.slms.patterns.builder.K2530341_BookBuilder;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Unit tests for LibraryService.
 * Author: M.Y.M. SAJIDH (K2530341)
 */
class K2530341_LibraryServiceTest {
    private K2530341_LibraryService libraryService;
    private K2530341_Book testBook;
    private K2530341_User testStudent;
    private K2530341_User testFaculty;
    private K2530341_User testGuest;
    
    @BeforeEach
    void setUp() {
        libraryService = new K2530341_LibraryService();
        libraryService.initialize();
        
        // Create test book
        testBook = new K2530341_BookBuilder()
            .setBookId("TEST001")
            .setTitle("Test Book")
            .setAuthor("Test Author")
            .setCategory("Fiction")
            .setIsbn("123-456-789")
            .build();
        libraryService.addBook(testBook);
        
        // Create test users
        testStudent = new K2530341_User("STUDENT001", "John Student", 
            "john@test.com", "1234567890", K2530341_MembershipType.STUDENT, 0);
        libraryService.addUser(testStudent);
        
        testFaculty = new K2530341_User("FACULTY001", "Jane Faculty", 
            "jane@test.com", "0987654321", K2530341_MembershipType.FACULTY, 0);
        libraryService.addUser(testFaculty);
        
        testGuest = new K2530341_User("GUEST001", "Bob Guest", 
            "bob@test.com", "5555555555", K2530341_MembershipType.GUEST, 0);
        libraryService.addUser(testGuest);
    }
    
    @Test
    @DisplayName("Test successful book borrowing")
    void testBorrowBookSuccess() {
        String borrowId = libraryService.borrowBook("TEST001", "STUDENT001");
        
        assertNotNull(borrowId, "Borrow ID should not be null");
        assertEquals(K2530341_AvailabilityStatus.BORROWED, testBook.getAvailabilityStatus());
        assertEquals(1, testStudent.getCurrentBorrowCount());
    }
    
    @Test
    @DisplayName("Test borrowing unavailable book fails")
    void testBorrowUnavailableBookFails() {
        // First borrow
        libraryService.borrowBook("TEST001", "STUDENT001");
        
        // Try to borrow again
        String borrowId = libraryService.borrowBook("TEST001", "FACULTY001");
        
        assertNull(borrowId, "Should not be able to borrow unavailable book");
    }
    
    @Test
    @DisplayName("Test return without fine")
    void testReturnBookNoFine() {
        String borrowId = libraryService.borrowBook("TEST001", "STUDENT001");
        boolean returned = libraryService.returnBook(borrowId);
        
        assertTrue(returned, "Return should be successful");
        assertEquals(K2530341_AvailabilityStatus.AVAILABLE, testBook.getAvailabilityStatus());
        assertEquals(0, testStudent.getCurrentBorrowCount());
        assertEquals(0.0, testStudent.getUnpaidFines());
    }
    
    @Test
    @DisplayName("Test return with fine for student")
    void testReturnWithFineStudent() {
        String borrowId = libraryService.borrowBook("TEST001", "STUDENT001");
        K2530341_Borrow borrow = libraryService.getAllBorrows().stream()
            .filter(b -> b.getBorrowId().equals(borrowId))
            .findFirst().orElse(null);
        
        assertNotNull(borrow);
        
        // Simulate overdue by changing due date
        borrow.setDueDate(LocalDate.now().minusDays(5));
        
        libraryService.returnBook(borrowId);
        
        // Student fine: 50 LKR per day * 5 days = 250 LKR
        assertEquals(250.0, testStudent.getUnpaidFines(), 0.01);
    }
    
    @Test
    @DisplayName("Test return with fine for faculty")
    void testReturnWithFineFaculty() {
        String borrowId = libraryService.borrowBook("TEST001", "FACULTY001");
        K2530341_Borrow borrow = libraryService.getAllBorrows().stream()
            .filter(b -> b.getBorrowId().equals(borrowId))
            .findFirst().orElse(null);
        
        assertNotNull(borrow);
        borrow.setDueDate(LocalDate.now().minusDays(10));
        
        libraryService.returnBook(borrowId);
        
        // Faculty fine: 20 LKR per day * 10 days = 200 LKR
        assertEquals(200.0, testFaculty.getUnpaidFines(), 0.01);
    }
    
    @Test
    @DisplayName("Test return with fine for guest")
    void testReturnWithFineGuest() {
        String borrowId = libraryService.borrowBook("TEST001", "GUEST001");
        K2530341_Borrow borrow = libraryService.getAllBorrows().stream()
            .filter(b -> b.getBorrowId().equals(borrowId))
            .findFirst().orElse(null);
        
        assertNotNull(borrow);
        borrow.setDueDate(LocalDate.now().minusDays(3));
        
        libraryService.returnBook(borrowId);
        
        // Guest fine: 100 LKR per day * 3 days = 300 LKR
        assertEquals(300.0, testGuest.getUnpaidFines(), 0.01);
    }
    
    @Test
    @DisplayName("Test borrow limit enforcement for student")
    void testBorrowLimitStudent() {
        // Add more books
        for (int i = 1; i <= 5; i++) {
            K2530341_Book book = new K2530341_BookBuilder()
                .setBookId("BOOK" + i)
                .setTitle("Book " + i)
                .setAuthor("Author")
                .setCategory("Fiction")
                .setIsbn("ISBN" + i)
                .build();
            libraryService.addBook(book);
            libraryService.borrowBook("BOOK" + i, "STUDENT001");
        }
        
        // Should be at limit (5 books)
        assertEquals(5, testStudent.getCurrentBorrowCount());
        
        // Try to borrow one more
        K2530341_Book extraBook = new K2530341_BookBuilder()
            .setBookId("EXTRA")
            .setTitle("Extra Book")
            .setAuthor("Author")
            .setCategory("Fiction")
            .setIsbn("EXTRA")
            .build();
        libraryService.addBook(extraBook);
        
        String borrowId = libraryService.borrowBook("EXTRA", "STUDENT001");
        assertNull(borrowId, "Should not exceed borrow limit");
    }
    
    @Test
    @DisplayName("Test reservation flow")
    void testReservationFlow() {
        // Create a fresh book for this test
        K2530341_Book resBook = new K2530341_BookBuilder()
            .setBookId("RESTEST001")
            .setTitle("Reservation Test Book")
            .setAuthor("Test Author")
            .setCategory("Fiction")
            .setIsbn("RES-123-456")
            .build();
        libraryService.addBook(resBook);
        
        // Borrow the book first
        libraryService.borrowBook("RESTEST001", "STUDENT001");
        
        // Reserve by another user
        String reservationId = libraryService.reserveBook("RESTEST001", "FACULTY001");
        assertNotNull(reservationId, "Reservation should be created");
        
        // Return the book
        String borrowId = libraryService.getActiveBorrows("STUDENT001").stream()
            .filter(b -> b.getBookId().equals("RESTEST001"))
            .findFirst()
            .get()
            .getBorrowId();
        libraryService.returnBook(borrowId);
        
        // Book should now be reserved
        assertEquals(K2530341_AvailabilityStatus.RESERVED, resBook.getAvailabilityStatus());
        
        // Faculty should have notification
        List<K2530341_Notification> notifications = libraryService.getUserNotifications("FACULTY001");
        assertTrue(notifications.size() > 0, "User should receive reservation notification");
    }
    
    @Test
    @DisplayName("Test unpaid fine limit blocks borrowing")
    void testUnpaidFineLimitBlocksBorrowing() {
        testStudent.setUnpaidFines(K2530341_LibraryService.MAX_UNPAID_LIMIT);
        
        String borrowId = libraryService.borrowBook("TEST001", "STUDENT001");
        assertNull(borrowId, "Should not be able to borrow with high unpaid fines");
    }
    
    @Test
    @DisplayName("Test search books functionality")
    void testSearchBooks() {
        K2530341_Book book2 = new K2530341_BookBuilder()
            .setBookId("UNIQUEJAVA001")
            .setTitle("Unique Java Programming Advanced")
            .setAuthor("James Gosling Jr.")
            .setCategory("Advanced Programming")
            .setIsbn("111-222-333-999")
            .build();
        libraryService.addBook(book2);
        
        // Search for unique title - should find at least our book
        List<K2530341_Book> results = libraryService.searchBooks("Unique Java Programming Advanced");
        assertTrue(results.size() >= 1, "Search should find at least the newly added book");
        assertTrue(results.stream().anyMatch(b -> b.getTitle().equals("Unique Java Programming Advanced")),
            "Search results should contain 'Unique Java Programming Advanced'");
        
        // Search for unique author
        results = libraryService.searchBooks("Gosling Jr");
        assertTrue(results.stream().anyMatch(b -> b.getAuthor().contains("Gosling Jr")),
            "Search should find book by author 'Gosling Jr'");
    }
}
