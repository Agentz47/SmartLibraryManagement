package com.k2530341.slms.patterns.state;

import com.k2530341.slms.model.book.K2530341_Book;
import com.k2530341.slms.model.book.K2530341_AvailabilityStatus;

/**
 * Available state for books.
 * Author: M.Y.M. SAJIDH (K2530341)
 */
public class K2530341_AvailableState implements K2530341_BookState {
    
    @Override
    public void borrow(K2530341_Book book) {
        book.setAvailabilityStatus(K2530341_AvailabilityStatus.BORROWED);
        book.setState(new K2530341_BorrowedState());
    }
    
    @Override
    public void returnBook(K2530341_Book book) {
        // Already available, no action needed
        System.out.println("Book is already available.");
    }
    
    @Override
    public void reserve(K2530341_Book book) {
        book.setAvailabilityStatus(K2530341_AvailabilityStatus.RESERVED);
        book.setState(new K2530341_ReservedState());
    }
    
    @Override
    public String getStateName() {
        return "Available";
    }
}
