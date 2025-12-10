package com.k2530341.slms.patterns.state;

import com.k2530341.slms.model.book.K2530341_Book;

/**
 * State interface for the State pattern applied to books.
 * Author: M.Y.M. SAJIDH (K2530341)
 */
public interface K2530341_BookState {
    /**
     * Handle borrow operation.
     * @param book The book being borrowed
     */
    void borrow(K2530341_Book book);
    
    /**
     * Handle return operation.
     * @param book The book being returned
     */
    void returnBook(K2530341_Book book);
    
    /**
     * Handle reserve operation.
     * @param book The book being reserved
     */
    void reserve(K2530341_Book book);
    
    /**
     * Get the state name.
     * @return State name
     */
    String getStateName();
}
