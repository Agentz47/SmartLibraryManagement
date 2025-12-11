package com.k2530341.slms.model.user;

/**
 * Guest user type with limited borrowing privileges.
 * - Can borrow up to 2 books
 * - Borrow period: 7 days
 * - Fine rate: 100 LKR per day
 * Author: M.Y.M. SAJIDH (K2530341)
 */
public class K2530341_Guest extends K2530341_User {
    private static final int BORROW_LIMIT = 2;
    private static final int BORROW_PERIOD_DAYS = 7;
    
    public K2530341_Guest(String userId, String name, String email, 
                         String contactNumber, int currentBorrowCount) {
        super(userId, name, email, contactNumber, 
              K2530341_MembershipType.GUEST, currentBorrowCount);
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
