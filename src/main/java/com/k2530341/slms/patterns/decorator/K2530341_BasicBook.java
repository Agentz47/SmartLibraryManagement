package com.k2530341.slms.patterns.decorator;

import com.k2530341.slms.model.book.K2530341_Book;

/**
 * Concrete component representing a basic book.
 * Author: M.Y.M. SAJIDH (K2530341)
 */
public class K2530341_BasicBook implements K2530341_BookComponent {
    private final K2530341_Book book;
    
    public K2530341_BasicBook(K2530341_Book book) {
        this.book = book;
    }
    
    @Override
    public String getDescription() {
        return book.getTitle() + " by " + book.getAuthor();
    }
    
    @Override
    public int getPriority() {
        return 0;
    }
    
    public K2530341_Book getBook() {
        return book;
    }
}
