package com.k2530341.slms.model.book;

import com.k2530341.slms.patterns.state.K2530341_BookState;
import com.k2530341.slms.patterns.state.K2530341_AvailableState;
import com.k2530341.slms.patterns.observer.K2530341_Subject;
import com.k2530341.slms.patterns.observer.K2530341_Observer;
import java.util.ArrayList;
import java.util.List;

/**
 * Book class - represents a book in the library
 * Implements Subject interface for observer pattern (to notify users)
 * Uses State pattern to manage book availability status
 * @author Sajidh (K2530341)
 */
public class K2530341_Book implements K2530341_Subject {
    // basic book info
    private String bookId;
    private String title;
    private String author;
    private String category;
    private String isbn;
    private K2530341_AvailabilityStatus availabilityStatus;
    private int borrowHistoryCount; // how many times borrowed
    private String optionalTags;
    private String edition;
    
    // for state pattern - manages what operations are allowed
    private K2530341_BookState currentState;
    
    // for observer pattern - list of users watching this book
    private final List<K2530341_Observer> observers = new ArrayList<>();
    
    // constructor
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
        // start with available state by default
        this.currentState = new K2530341_AvailableState();
    }
    
    // change the state of the book
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
