package com.k2530341.slms.model.user;

/**
 * Faculty user type with extended borrowing privileges.
 * - Can borrow up to 10 books
 * - Borrow period: 30 days
 * - Fine rate: 20 LKR per day
 * Author: M.Y.M. SAJIDH (K2530341)
 */
public class K2530341_Faculty extends K2530341_User {
    private static final int BORROW_LIMIT = 10;
    private static final int BORROW_PERIOD_DAYS = 30;
    
    public K2530341_Faculty(String userId, String name, String email, 
                           String contactNumber, int currentBorrowCount) {
        super(userId, name, email, contactNumber, 
              K2530341_MembershipType.FACULTY, currentBorrowCount);
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
