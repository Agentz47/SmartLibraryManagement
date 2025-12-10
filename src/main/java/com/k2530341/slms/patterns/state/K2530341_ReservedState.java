package com.k2530341.slms.patterns.state;

import com.k2530341.slms.model.book.K2530341_Book;
import com.k2530341.slms.model.book.K2530341_AvailabilityStatus;

/**
 * Reserved state for books.
 * Author: M.Y.M. SAJIDH (K2530341)
 */
public class K2530341_ReservedState implements K2530341_BookState {
    
    @Override
    public void borrow(K2530341_Book book) {
        // Only the reserver can borrow
        book.setAvailabilityStatus(K2530341_AvailabilityStatus.BORROWED);
        book.setState(new K2530341_BorrowedState());
    }
    
    @Override
    public void returnBook(K2530341_Book book) {
        System.out.println("Book is reserved, cannot return in this state.");
    }
    
    @Override
    public void reserve(K2530341_Book book) {
        System.out.println("Book is already reserved.");
    }
    
    @Override
    public String getStateName() {
        return "Reserved";
    }
}
