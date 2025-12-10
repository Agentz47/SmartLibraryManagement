package com.k2530341.slms.patterns.state;

import com.k2530341.slms.model.book.K2530341_Book;
import com.k2530341.slms.model.book.K2530341_AvailabilityStatus;

/**
 * Borrowed state for books.
 * Author: M.Y.M. SAJIDH (K2530341)
 */
public class K2530341_BorrowedState implements K2530341_BookState {
    
    @Override
    public void borrow(K2530341_Book book) {
        System.out.println("Book is already borrowed.");
    }
    
    @Override
    public void returnBook(K2530341_Book book) {
        book.setAvailabilityStatus(K2530341_AvailabilityStatus.AVAILABLE);
        book.setState(new K2530341_AvailableState());
    }
    
    @Override
    public void reserve(K2530341_Book book) {
        // Cannot reserve while borrowed, but reservation can be added to queue
        System.out.println("Book is borrowed. Adding to reservation queue.");
    }
    
    @Override
    public String getStateName() {
        return "Borrowed";
    }
}
