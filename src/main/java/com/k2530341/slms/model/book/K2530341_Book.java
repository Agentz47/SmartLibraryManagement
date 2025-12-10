package com.k2530341.slms.model.book;

import com.k2530341.slms.patterns.state.K2530341_BookState;
import com.k2530341.slms.patterns.state.K2530341_AvailableState;
import com.k2530341.slms.patterns.observer.K2530341_Subject;
import com.k2530341.slms.patterns.observer.K2530341_Observer;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Book in the library system with state management and observer pattern.
 * Author: M.Y.M. SAJIDH (K2530341)
 */
public class K2530341_Book implements K2530341_Subject {
    private String bookId;
    private String title;
    private String author;
    private String category;
    private String isbn;
    private K2530341_AvailabilityStatus availabilityStatus;
    private int borrowHistoryCount;
    private String optionalTags;
    private String edition;
    
    // State pattern
    private K2530341_BookState currentState;
    
    // Observer pattern
    private final List<K2530341_Observer> observers = new ArrayList<>();
    
    public K2530341_Book(String bookId, String title, String author, String category, 
                         String isbn, K2530341_AvailabilityStatus availabilityStatus, 
                         int borrowHistoryCount, String optionalTags, String edition) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.category = category;
        this.isbn = isbn;
        this.availabilityStatus = availabilityStatus;
        this.borrowHistoryCount = borrowHistoryCount;
        this.optionalTags = optionalTags;
        this.edition = edition;
        this.currentState = new K2530341_AvailableState();
    }
    
    // State pattern methods
    public void setState(K2530341_BookState state) {
        this.currentState = state;
    }
    
    public K2530341_BookState getState() {
        return currentState;
    }
    
    public void performBorrow() {
        currentState.borrow(this);
    }
    
    public void performReturn() {
        currentState.returnBook(this);
    }
    
    public void performReserve() {
        currentState.reserve(this);
    }
    
    // Observer pattern methods
    @Override
    public void attach(K2530341_Observer observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }
    
    @Override
    public void detach(K2530341_Observer observer) {
        observers.remove(observer);
    }
    
    @Override
    public void notifyObservers(String event, String message) {
        for (K2530341_Observer observer : observers) {
            observer.update(event, message);
        }
    }
    
    public void incrementBorrowHistory() {
        this.borrowHistoryCount++;
    }
    
    // Getters and setters
    public String getBookId() {
        return bookId;
    }
    
    public void setBookId(String bookId) {
        this.bookId = bookId;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getAuthor() {
        return author;
    }
    
    public void setAuthor(String author) {
        this.author = author;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public String getIsbn() {
        return isbn;
    }
    
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }
    
    public K2530341_AvailabilityStatus getAvailabilityStatus() {
        return availabilityStatus;
    }
    
    public void setAvailabilityStatus(K2530341_AvailabilityStatus availabilityStatus) {
        this.availabilityStatus = availabilityStatus;
    }
    
    public int getBorrowHistoryCount() {
        return borrowHistoryCount;
    }
    
    public void setBorrowHistoryCount(int borrowHistoryCount) {
        this.borrowHistoryCount = borrowHistoryCount;
    }
    
    public String getOptionalTags() {
        return optionalTags;
    }
    
    public void setOptionalTags(String optionalTags) {
        this.optionalTags = optionalTags;
    }
    
    public String getEdition() {
        return edition;
    }
    
    public void setEdition(String edition) {
        this.edition = edition;
    }
    
    @Override
    public String toString() {
        return "Book{" +
                "bookId='" + bookId + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", status=" + availabilityStatus +
                '}';
    }
}
