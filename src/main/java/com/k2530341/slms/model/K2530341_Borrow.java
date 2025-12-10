package com.k2530341.slms.model;

import java.time.LocalDate;

/**
 * Represents a borrow record in the library system.
 * Author: M.Y.M. SAJIDH (K2530341)
 */
public class K2530341_Borrow {
    private String borrowId;
    private String bookId;
    private String userId;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private double finePaid;
    
    public K2530341_Borrow(String borrowId, String bookId, String userId, 
                          LocalDate borrowDate, LocalDate dueDate) {
        this.borrowId = borrowId;
        this.bookId = bookId;
        this.userId = userId;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.returnDate = null;
        this.finePaid = 0.0;
    }
    
    public K2530341_Borrow(String borrowId, String bookId, String userId, 
                          LocalDate borrowDate, LocalDate dueDate, 
                          LocalDate returnDate, double finePaid) {
        this.borrowId = borrowId;
        this.bookId = bookId;
        this.userId = userId;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.finePaid = finePaid;
    }
    
    public boolean isOverdue() {
        if (returnDate != null) {
            return returnDate.isAfter(dueDate);
        }
        return LocalDate.now().isAfter(dueDate);
    }
    
    public long getOverdueDays() {
        LocalDate compareDate = returnDate != null ? returnDate : LocalDate.now();
        if (compareDate.isAfter(dueDate)) {
            return java.time.temporal.ChronoUnit.DAYS.between(dueDate, compareDate);
        }
        return 0;
    }
    
    // Getters and setters
    public String getBorrowId() {
        return borrowId;
    }
    
    public void setBorrowId(String borrowId) {
        this.borrowId = borrowId;
    }
    
    public String getBookId() {
        return bookId;
    }
    
    public void setBookId(String bookId) {
        this.bookId = bookId;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public LocalDate getBorrowDate() {
        return borrowDate;
    }
    
    public void setBorrowDate(LocalDate borrowDate) {
        this.borrowDate = borrowDate;
    }
    
    public LocalDate getDueDate() {
        return dueDate;
    }
    
    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
    
    public LocalDate getReturnDate() {
        return returnDate;
    }
    
    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }
    
    public double getFinePaid() {
        return finePaid;
    }
    
    public void setFinePaid(double finePaid) {
        this.finePaid = finePaid;
    }
    
    @Override
    public String toString() {
        return "Borrow{" +
                "borrowId='" + borrowId + '\'' +
                ", bookId='" + bookId + '\'' +
                ", userId='" + userId + '\'' +
                ", dueDate=" + dueDate +
                '}';
    }
}
