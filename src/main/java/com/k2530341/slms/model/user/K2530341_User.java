package com.k2530341.slms.model.user;

import com.k2530341.slms.patterns.observer.K2530341_Observer;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base class for all users in the library system.
 * Implements the Observer pattern to receive notifications.
 * Author: M.Y.M. SAJIDH (K2530341)
 */
public abstract class K2530341_User implements K2530341_Observer {
    private String userId;
    private String name;
    private String email;
    private String contactNumber;
    private K2530341_MembershipType membershipType;
    private int currentBorrowCount;
    private double unpaidFines;
    
    private final List<String> notifications = new ArrayList<>();
    
    public K2530341_User(String userId, String name, String email, String contactNumber, 
                         K2530341_MembershipType membershipType, int currentBorrowCount) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.contactNumber = contactNumber;
        this.membershipType = membershipType;
        this.currentBorrowCount = currentBorrowCount;
        this.unpaidFines = 0.0;
    }
    
    @Override
    public void update(String event, String message) {
        notifications.add("[" + event + "] " + message);
    }
    
    public void incrementBorrowCount() {
        this.currentBorrowCount++;
    }
    // Prevent negative borrow count
    public void decrementBorrowCount() {
        if (this.currentBorrowCount > 0) {
            this.currentBorrowCount--;
        }
    }
    
    public void addFine(double amount) {
        this.unpaidFines += amount;
    }
    // Prevent negative fines
    public void payFine(double amount) {
        this.unpaidFines -= amount;
        if (this.unpaidFines < 0) {
            this.unpaidFines = 0;
        }
    }
    
    public List<String> getNotifications() {
        return new ArrayList<>(notifications);
    }
    
    public void clearNotifications() {
        notifications.clear();
    }
    
    /**
     * Abstract method to get borrow limit based on user type.
     * @return Maximum number of books that can be borrowed
     */
    public abstract int getBorrowLimit();
    
    /**
     * Abstract method to get borrow period in days.
     * @return Number of days books can be borrowed
     */
    public abstract int getBorrowPeriodDays();
    
    // Getters and setters
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getContactNumber() {
        return contactNumber;
    }
    
    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }
    
    public K2530341_MembershipType getMembershipType() {
        return membershipType;
    }
    
    public void setMembershipType(K2530341_MembershipType membershipType) {
        this.membershipType = membershipType;
    }
    
    public int getCurrentBorrowCount() {
        return currentBorrowCount;
    }
    
    public void setCurrentBorrowCount(int currentBorrowCount) {
        this.currentBorrowCount = currentBorrowCount;
    }
    
    public double getUnpaidFines() {
        return unpaidFines;
    }
    
    public void setUnpaidFines(double unpaidFines) {
        this.unpaidFines = unpaidFines;
    }
    
    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                ", type=" + membershipType +
                ", borrows=" + currentBorrowCount +
                '}';
    }
}
