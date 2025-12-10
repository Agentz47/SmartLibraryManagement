package com.k2530341.slms.patterns.strategy;

/**
 * Guest fine calculation strategy.
 * Fine rate: LKR 100 per day
 * Author: M.Y.M. SAJIDH (K2530341)
 */
public class K2530341_GuestFineStrategy implements K2530341_FineStrategy {
    private static final double FINE_PER_DAY = 100.0;
    
    @Override
    public double calculateFine(long overdueDays) {
        return overdueDays * FINE_PER_DAY;
    }
    
    @Override
    public double getFinePerDay() {
        return FINE_PER_DAY;
    }
}
