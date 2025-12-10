package com.k2530341.slms.patterns.strategy;

/**
 * Strategy interface for calculating fines based on membership type.
 * Author: M.Y.M. SAJIDH (K2530341)
 */
public interface K2530341_FineStrategy {
    /**
     * Calculate fine for overdue books.
     * @param overdueDays Number of days overdue
     * @return Fine amount in LKR
     */
    double calculateFine(long overdueDays);
    
    /**
     * Get the fine rate per day.
     * @return Fine rate per day in LKR
     */
    double getFinePerDay();
}
