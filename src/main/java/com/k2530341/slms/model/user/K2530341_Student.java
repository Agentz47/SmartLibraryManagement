package com.k2530341.slms.model.user;

/**
 * Student user type with specific borrowing rules.
 * - Can borrow up to 5 books
 * - Borrow period: 14 days
 * - Fine rate: 50 LKR per day
 * Author: M.Y.M. SAJIDH (K2530341)
 */
public class K2530341_Student extends K2530341_User {
    private static final int BORROW_LIMIT = 5;
    private static final int BORROW_PERIOD_DAYS = 14;
    
    public K2530341_Student(String userId, String name, String email, 
                           String contactNumber, int currentBorrowCount) {
        super(userId, name, email, contactNumber, 
              K2530341_MembershipType.STUDENT, currentBorrowCount);
    }
    
    @Override
    public int getBorrowLimit() {
        return BORROW_LIMIT;
    }
    
    @Override
    public int getBorrowPeriodDays() {
        return BORROW_PERIOD_DAYS;
    }
}
