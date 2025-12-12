package com.k2530341.slms.service;

import com.k2530341.slms.model.book.*;
import com.k2530341.slms.model.user.*;
import com.k2530341.slms.model.*;
import com.k2530341.slms.model.reservation.*;
import com.k2530341.slms.model.notification.*;
import com.k2530341.slms.patterns.strategy.*;
import com.k2530341.slms.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Library Service - handles all the main business operations
 * @author Sajidh (K2530341)
 * This class manages books, users, borrowing, reservations etc.
 */
public class K2530341_LibraryService {
    // max fine limit - user cannot borrow if they exceed this
    public static final double MAX_UNPAID_LIMIT = 1000.0;
    
    // using HashMaps to store all data - easy to lookup by ID
    private final Map<String, K2530341_Book> books = new HashMap<>();
    private final Map<String, K2530341_User> users = new HashMap<>();
    private final Map<String, K2530341_Borrow> borrows = new HashMap<>();
    private final Map<String, K2530341_Reservation> reservations = new HashMap<>();
    private final Map<String, K2530341_Notification> notifications = new HashMap<>();
    
    // CSV managers for loading/saving data
    private final K2530341_BookCSVManager bookCSV = new K2530341_BookCSVManager();
    private final K2530341_UserCSVManager userCSV = new K2530341_UserCSVManager();
    private final K2530341_BorrowCSVManager borrowCSV = new K2530341_BorrowCSVManager();
    private final K2530341_ReservationCSVManager reservationCSV = new K2530341_ReservationCSVManager();
    private final K2530341_NotificationCSVManager notificationCSV = new K2530341_NotificationCSVManager();
    
    // counters for generating IDs
    private int borrowIdCounter = 1;
    private int reservationIdCounter = 1;
    private int notificationIdCounter = 1;
    private int bookIdCounter = 1;
    private int studentIdCounter = 1;
    private int facultyIdCounter = 1;
    private int guestIdCounter = 1;
    
    // initialize method - called when app starts
    public void initialize() {
        loadAllData();
        cleanupExpiredReservations(); // check for any expired reservations
    }
    
    /**
     * Load all data from CSV files.
     */
    private void loadAllData() {
        // Load books
        List<K2530341_Book> loadedBooks = bookCSV.loadBooks();
        for (K2530341_Book book : loadedBooks) {
            books.put(book.getBookId(), book);
            // Update book ID counter
            if (book.getBookId().startsWith("BK-")) {
                try {
                    int id = Integer.parseInt(book.getBookId().substring(3));
                    if (id >= bookIdCounter) {
                        bookIdCounter = id + 1;
                    }
                } catch (NumberFormatException ignored) {}
            }
        }
        
        // Load users
        List<K2530341_User> loadedUsers = userCSV.loadUsers();
        for (K2530341_User user : loadedUsers) {
            users.put(user.getUserId(), user);
            // Update user ID counters based on type
            String userId = user.getUserId();
            try {
                if (userId.startsWith("STU-")) {
                    int id = Integer.parseInt(userId.substring(4));
                    if (id >= studentIdCounter) {
                        studentIdCounter = id + 1;
                    }
                } else if (userId.startsWith("FCL-")) {
                    int id = Integer.parseInt(userId.substring(4));
                    if (id >= facultyIdCounter) {
                        facultyIdCounter = id + 1;
                    }
                } else if (userId.startsWith("GST-")) {
                    int id = Integer.parseInt(userId.substring(4));
                    if (id >= guestIdCounter) {
                        guestIdCounter = id + 1;
                    }
                }
            } catch (NumberFormatException ignored) {}
        }
        }
        
        // Load borrows
        List<K2530341_Borrow> loadedBorrows = borrowCSV.loadBorrows();{
        for (K2530341_Borrow borrow : loadedBorrows) {
            borrows.put(borrow.getBorrowId(), borrow);
            int id = Integer.parseInt(borrow.getBorrowId().replaceAll("\\D", ""));
            borrowIdCounter = Math.max(borrowIdCounter, id + 1);
        }
        
        // Load reservations
        List<K2530341_Reservation> loadedReservations = reservationCSV.loadReservations();
        for (K2530341_Reservation reservation : loadedReservations) {
            reservations.put(reservation.getReservationId(), reservation);
            int id = Integer.parseInt(reservation.getReservationId().replaceAll("\\D", ""));
            reservationIdCounter = Math.max(reservationIdCounter, id + 1);
        }
        
        // Load notifications
        List<K2530341_Notification> loadedNotifications = notificationCSV.loadNotifications();
        for (K2530341_Notification notification : loadedNotifications) {
            notifications.put(notification.getNotificationId(), notification);
            int id = Integer.parseInt(notification.getNotificationId().replaceAll("\\D", ""));
            notificationIdCounter = Math.max(notificationIdCounter, id + 1);
        }
    }
    
    /**
     * Save all data to CSV files.
     */
    public void saveAllData() {
        bookCSV.saveBooks(new ArrayList<>(books.values()));
        userCSV.saveUsers(new ArrayList<>(users.values()));
        borrowCSV.saveBorrows(new ArrayList<>(borrows.values()));
        reservationCSV.saveReservations(new ArrayList<>(reservations.values()));
        notificationCSV.saveNotifications(new ArrayList<>(notifications.values()));
    }
    
    // ========== BOOK MANAGEMENT ==========
    
    public String addBook(K2530341_Book book) {
        if (books.containsKey(book.getBookId())) {
            return "DUPLICATE_ID";
        }
        books.put(book.getBookId(), book);
        // Increment the counter only when book is actually added
        if (book.getBookId().startsWith("BK-")) {
            try {
                int id = Integer.parseInt(book.getBookId().substring(3));
                if (id >= bookIdCounter) {
                    bookIdCounter = id + 1;
                }
            } catch (NumberFormatException ignored) {}
        }
        saveAllData();
        return "SUCCESS";
    }
    
    public String peekNextBookId() {
        return "BK-" + String.format("%03d", bookIdCounter);
    }
    
    public String generateNextBookId() {
        return "BK-" + String.format("%03d", bookIdCounter++);
    }
    
    public void updateBook(K2530341_Book book) {
        books.put(book.getBookId(), book);
        saveAllData();
    }
    
    public void deleteBook(String bookId) {
        books.remove(bookId);
        saveAllData();
    }
    
    public K2530341_Book getBook(String bookId) {
        return books.get(bookId);
    }
    
    public List<K2530341_Book> getAllBooks() {
        return new ArrayList<>(books.values());
    }
    
    public List<K2530341_Book> searchBooks(String query) {
        String lowerQuery = query.toLowerCase();
        return books.values().stream()
            .filter(book -> book.getTitle().toLowerCase().contains(lowerQuery) ||
                           book.getAuthor().toLowerCase().contains(lowerQuery) ||
                           book.getCategory().toLowerCase().contains(lowerQuery))
            .collect(Collectors.toList());
    }
    
    // ========== USER MANAGEMENT ==========
    
    public String addUser(K2530341_User user) {
        if (users.containsKey(user.getUserId())) {
            return "DUPLICATE_ID";
        }
        users.put(user.getUserId(), user);
        // Increment the counter only when user is actually added
        String userId = user.getUserId();
        try {
            if (userId.startsWith("STU-")) {
                int id = Integer.parseInt(userId.substring(4));
                if (id >= studentIdCounter) {
                    studentIdCounter = id + 1;
                }
            } else if (userId.startsWith("FCL-")) {
                int id = Integer.parseInt(userId.substring(4));
                if (id >= facultyIdCounter) {
                    facultyIdCounter = id + 1;
                }
            } else if (userId.startsWith("GST-")) {
                int id = Integer.parseInt(userId.substring(4));
                if (id >= guestIdCounter) {
                    guestIdCounter = id + 1;
                }
            }
        } catch (NumberFormatException ignored) {}
        saveAllData();
        return "SUCCESS";
    }
    
    public String peekNextUserId(K2530341_MembershipType type) {
        switch (type) {
            case STUDENT:
                return "STU-" + String.format("%03d", studentIdCounter);
            case FACULTY:
                return "FCL-" + String.format("%03d", facultyIdCounter);
            case GUEST:
                return "GST-" + String.format("%03d", guestIdCounter);
            default:
                return "STU-" + String.format("%03d", studentIdCounter);
        }
    }
    
    public String generateNextUserId(K2530341_MembershipType type) {
        switch (type) {
            case STUDENT:
                return "STU-" + String.format("%03d", studentIdCounter++);
            case FACULTY:
                return "FCL-" + String.format("%03d", facultyIdCounter++);
            case GUEST:
                return "GST-" + String.format("%03d", guestIdCounter++);
            default:
                return "STU-" + String.format("%03d", studentIdCounter++);
        }
    }
    
    public void updateUser(K2530341_User user) {
        users.put(user.getUserId(), user);
        saveAllData();
    }
    
    public void deleteUser(String userId) {
        users.remove(userId);
        saveAllData();
    }
    
    public K2530341_User getUser(String userId) {
        return users.get(userId);
    }
    
    public List<K2530341_User> getAllUsers() {
        return new ArrayList<>(users.values());
    }
    
    // ========== BORROW/RETURN OPERATIONS ==========
    
    public String borrowBook(String bookId, String userId) {
        K2530341_Book book = books.get(bookId);
        K2530341_User user = users.get(userId);
        
        if (book == null || user == null) {
            return null;
        }
        
        // Check borrow limit
        if (user.getCurrentBorrowCount() >= user.getMembershipType().getBorrowLimit()) {
            return "LIMIT_EXCEEDED";
        }
        
        // Check unpaid fines - IMPORTANT: This blocks borrowing if fines >= LKR 1000
        if (user.getUnpaidFines() >= MAX_UNPAID_LIMIT) {
            return "FINE_LIMIT_EXCEEDED";
        }
        
        // Check if book is available OR reserved for this specific user
        K2530341_AvailabilityStatus status = book.getAvailabilityStatus();
        boolean canBorrow = false;
        
        if (status == K2530341_AvailabilityStatus.AVAILABLE) {
            canBorrow = true;
        } else if (status == K2530341_AvailabilityStatus.RESERVED) {
            // Check if this user has a notified reservation for this book
            K2530341_Reservation userReservation = reservations.values().stream()
                .filter(r -> r.getBookId().equals(bookId) 
                        && r.getUserId().equals(userId)
                        && r.getStatus() == K2530341_ReservationStatus.NOTIFIED)
                .findFirst()
                .orElse(null);
            
            if (userReservation != null) {
                // Check if reservation is not expired (48 hours)
                if (userReservation.getNotifiedAt() != null) {
                    LocalDateTime expiryTime = userReservation.getNotifiedAt().plusHours(48);
                    if (LocalDateTime.now().isBefore(expiryTime)) {
                        canBorrow = true;
                        // Mark reservation as fulfilled when user borrows the book
                        userReservation.setStatus(K2530341_ReservationStatus.FULFILLED);
                    } else {
                        // Reservation expired, release the book
                        userReservation.setStatus(K2530341_ReservationStatus.EXPIRED);
                        book.setAvailabilityStatus(K2530341_AvailabilityStatus.AVAILABLE);
                        
                        // Check for next reservation
                        K2530341_Reservation nextReservation = getNextPendingReservation(bookId);
                        if (nextReservation != null) {
                            nextReservation.setStatus(K2530341_ReservationStatus.NOTIFIED);
                            nextReservation.setNotifiedAt(LocalDateTime.now());
                            book.setAvailabilityStatus(K2530341_AvailabilityStatus.RESERVED);
                            createNotification(nextReservation.getUserId(), K2530341_NotificationType.RESERVATION_READY,
                                "Your reserved book '" + book.getTitle() + "' is now available. Please collect within 48 hours.");
                        }
                        saveAllData();
                        return null; // Current user's reservation expired
                    }
                }
            }
        }
        
        if (!canBorrow) {
            return null;
        }
        
        // Create borrow record with structured ID
        String borrowId = "BR-" + String.format("%04d", borrowIdCounter++);
        LocalDate borrowDate = LocalDate.now();
        LocalDate dueDate = borrowDate.plusDays(user.getMembershipType().getBorrowDurationDays());
        
        K2530341_Borrow borrow = new K2530341_Borrow(borrowId, bookId, userId, borrowDate, dueDate);
        borrows.put(borrowId, borrow);
        
        // Update book and user
        book.setAvailabilityStatus(K2530341_AvailabilityStatus.BORROWED);
        book.incrementBorrowHistory();
        book.performBorrow();
        user.incrementBorrowCount();
        
        saveAllData();
        return borrowId;
    }
    
    public boolean returnBook(String borrowId) {
        K2530341_Borrow borrow = borrows.get(borrowId);
        if (borrow == null || borrow.getReturnDate() != null) {
            return false;
        }
        
        K2530341_Book book = books.get(borrow.getBookId());
        K2530341_User user = users.get(borrow.getUserId());
        
        if (book == null || user == null) {
            return false;
        }
        
        // Set return date
        LocalDate returnDate = LocalDate.now();
        borrow.setReturnDate(returnDate);
        
        // Calculate fine if overdue
        long overdueDays = borrow.getOverdueDays();
        if (overdueDays > 0) {
            K2530341_FineStrategy fineStrategy = getFineStrategy(user.getMembershipType());
            double fine = fineStrategy.calculateFine(overdueDays);
            borrow.setFinePaid(fine);
            user.addFine(fine);
            
            // Create notification
            createNotification(user.getUserId(), K2530341_NotificationType.OVERDUE_ALERT,
                "Fine of LKR " + fine + " for " + overdueDays + " days overdue on book: " + book.getTitle());
        }
        
        // Update user
        user.decrementBorrowCount();
        
        // Check for reservations
        K2530341_Reservation nextReservation = getNextPendingReservation(book.getBookId());
        if (nextReservation != null) {
            // Notify reserver
            nextReservation.setStatus(K2530341_ReservationStatus.NOTIFIED);
            nextReservation.setNotifiedAt(LocalDateTime.now());
            book.setAvailabilityStatus(K2530341_AvailabilityStatus.RESERVED);
            book.performReserve();
            
            createNotification(nextReservation.getUserId(), K2530341_NotificationType.RESERVATION_READY,
                "Your reserved book '" + book.getTitle() + "' is now available. Please collect within 48 hours.");
        } else {
            // Book becomes available
            book.setAvailabilityStatus(K2530341_AvailabilityStatus.AVAILABLE);
            book.performReturn();
        }
        
        saveAllData();
        return true;
    }
    
    // ========== RESERVATION OPERATIONS ==========
    
    public String reserveBook(String bookId, String userId) {
        K2530341_Book book = books.get(bookId);
        K2530341_User user = users.get(userId);
        
        if (book == null || user == null) {
            return null;
        }
        
        // FIX 1: Don't allow reservation of AVAILABLE books - they should be borrowed instead
        if (book.getAvailabilityStatus() == K2530341_AvailabilityStatus.AVAILABLE) {
            return "AVAILABLE"; // Special return value to indicate book is available to borrow
        }
        
        // FIX 2: Don't allow user to reserve a book they currently have borrowed
        boolean alreadyBorrowed = borrows.values().stream()
            .anyMatch(b -> b.getBookId().equals(bookId) 
                    && b.getUserId().equals(userId) 
                    && b.getReturnDate() == null);
        
        if (alreadyBorrowed) {
            return "ALREADY_BORROWED"; // Special return value
        }
        
        // Create reservation with structured ID
        String reservationId = "RES-" + String.format("%04d", reservationIdCounter++);
        K2530341_Reservation reservation = new K2530341_Reservation(
            reservationId, bookId, userId, LocalDateTime.now(), K2530341_ReservationStatus.PENDING
        );
        reservations.put(reservationId, reservation);
        
        // Attach user as observer to book
        book.attach(user);
        
        saveAllData();
        return reservationId;
    }
    
    public boolean cancelReservation(String reservationId) {
        K2530341_Reservation reservation = reservations.get(reservationId);
        if (reservation == null) {
            return false;
        }
        
        reservation.setStatus(K2530341_ReservationStatus.CANCELLED);
        
        // Detach user from book
        K2530341_Book book = books.get(reservation.getBookId());
        K2530341_User user = users.get(reservation.getUserId());
        if (book != null && user != null) {
            book.detach(user);
        }
        
        saveAllData();
        return true;
    }
    
    private K2530341_Reservation getNextPendingReservation(String bookId) {
        return reservations.values().stream()
            .filter(r -> r.getBookId().equals(bookId) && r.getStatus() == K2530341_ReservationStatus.PENDING)
            .min(Comparator.comparing(K2530341_Reservation::getReservationDate))
            .orElse(null);
    }
    
    public List<K2530341_Reservation> getUserReservations(String userId) {
        return reservations.values().stream()
            .filter(r -> r.getUserId().equals(userId))
            .collect(Collectors.toList());
    }
    
    public List<K2530341_Reservation> getAllReservations() {
        return new ArrayList<>(reservations.values());
    }
    
    // ========== NOTIFICATION OPERATIONS ==========
    
    public void createNotification(String userId, K2530341_NotificationType type, String message) {
        String notificationId = "NTF-" + String.format("%04d", notificationIdCounter++);
        K2530341_Notification notification = new K2530341_Notification(
            notificationId, userId, type, message, LocalDate.now(), false
        );
        notifications.put(notificationId, notification);
        saveAllData();
    }
    
    public List<K2530341_Notification> getUserNotifications(String userId) {
        return notifications.values().stream()
            .filter(n -> n.getUserId().equals(userId))
            .sorted(Comparator.comparing(K2530341_Notification::getDate).reversed())
            .collect(Collectors.toList());
    }
    
    //HELPER METHODS
    
    private K2530341_FineStrategy getFineStrategy(K2530341_MembershipType type) {
        switch (type) {
            case STUDENT:
                return new K2530341_StudentFineStrategy();
            case FACULTY:
                return new K2530341_FacultyFineStrategy();
            case GUEST:
                return new K2530341_GuestFineStrategy();
            default:
                return new K2530341_StudentFineStrategy();
        }
    }
    
    public List<K2530341_Borrow> getUserBorrows(String userId) {
        return borrows.values().stream()
            .filter(b -> b.getUserId().equals(userId))
            .collect(Collectors.toList());
    }
    
    public List<K2530341_Borrow> getActiveBorrows(String userId) {
        return borrows.values().stream()
            .filter(b -> b.getUserId().equals(userId) && b.getReturnDate() == null)
            .collect(Collectors.toList());
    }
    
    public List<K2530341_Borrow> getAllBorrows() {
        return new ArrayList<>(borrows.values());
    }
    
    public List<K2530341_Borrow> getOverdueBorrows() {
        return borrows.values().stream()
            .filter(b -> b.getReturnDate() == null && b.isOverdue())
            .collect(Collectors.toList());
    }
    
    /**
     * Clean up expired reservations (older than 48 hours since notification).
     * This releases reserved books back to available or next reservation.
     */
    public void cleanupExpiredReservations() {
        LocalDateTime now = LocalDateTime.now();
        boolean dataChanged = false;
        
        List<K2530341_Reservation> expiredReservations = reservations.values().stream()
            .filter(r -> r.getStatus() == K2530341_ReservationStatus.NOTIFIED 
                    && r.getNotifiedAt() != null
                    && now.isAfter(r.getNotifiedAt().plusHours(48)))
            .collect(Collectors.toList());
        
        for (K2530341_Reservation expired : expiredReservations) {
            expired.setStatus(K2530341_ReservationStatus.EXPIRED);
            K2530341_Book book = books.get(expired.getBookId());
            
            if (book != null && book.getAvailabilityStatus() == K2530341_AvailabilityStatus.RESERVED) {
                // Check for next pending reservation
                K2530341_Reservation nextReservation = getNextPendingReservation(expired.getBookId());
                if (nextReservation != null) {
                    // Notify next person in queue
                    nextReservation.setStatus(K2530341_ReservationStatus.NOTIFIED);
                    nextReservation.setNotifiedAt(now);
                    createNotification(nextReservation.getUserId(), K2530341_NotificationType.RESERVATION_READY,
                        "Your reserved book '" + book.getTitle() + "' is now available. Please collect within 48 hours.");
                } else {
                    // No more reservations, make book available
                    book.setAvailabilityStatus(K2530341_AvailabilityStatus.AVAILABLE);
                }
                dataChanged = true;
            }
        }
        
        if (dataChanged) {
            saveAllData();
        }
    }
    
    /**
     * Calculate the fine for a borrow without actually returning it.
     * Useful for showing fine amount before return.
     * @param borrowId The borrow ID
     * @return Fine amount, or 0 if no fine or invalid borrow
     */
    public double calculateFineForBorrow(String borrowId) {
        K2530341_Borrow borrow = borrows.get(borrowId);
        if (borrow == null || borrow.getReturnDate() != null) {
            return 0.0;
        }
        
        K2530341_User user = users.get(borrow.getUserId());
        if (user == null) {
            return 0.0;
        }
        
        long overdueDays = borrow.getOverdueDays();
        if (overdueDays > 0) {
            K2530341_FineStrategy fineStrategy = getFineStrategy(user.getMembershipType());
            return fineStrategy.calculateFine(overdueDays);
        }
        
        return 0.0;
    }
}
