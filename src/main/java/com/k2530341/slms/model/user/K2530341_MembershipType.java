package com.k2530341.slms.model.user;

/**
 * Enum representing the membership type of a user.
 * Author: M.Y.M. SAJIDH (K2530341)
 */
public enum K2530341_MembershipType {
    STUDENT(14, 5, 50.0),
    FACULTY(30, 10, 20.0),
    GUEST(7, 2, 100.0);
    
    private final int borrowDurationDays;
    private final int borrowLimit;
    private final double finePerDay;
    
    K2530341_MembershipType(int borrowDurationDays, int borrowLimit, double finePerDay) {
        this.borrowDurationDays = borrowDurationDays;
        this.borrowLimit = borrowLimit;
        this.finePerDay = finePerDay;
    }
    
    public int getBorrowDurationDays() {
        return borrowDurationDays;
    }
    
    public int getBorrowLimit() {
        return borrowLimit;
    }
    
    public double getFinePerDay() {
        return finePerDay;
    }
}
