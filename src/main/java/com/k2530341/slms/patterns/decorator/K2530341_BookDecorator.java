package com.k2530341.slms.patterns.decorator;

/**
 * Abstract decorator for book components.
 * Author: M.Y.M. SAJIDH (K2530341)
 */
public abstract class K2530341_BookDecorator implements K2530341_BookComponent {
    protected K2530341_BookComponent wrappedBook;
    
    public K2530341_BookDecorator(K2530341_BookComponent wrappedBook) {
        this.wrappedBook = wrappedBook;
    }
    
    @Override
    public String getDescription() {
        return wrappedBook.getDescription();
    }
    
    @Override
    public int getPriority() {
        return wrappedBook.getPriority();
    }
}
