package com.k2530341.slms.patterns.decorator;

/**
 * Decorator that marks a book as featured.
 * Author: M.Y.M. SAJIDH (K2530341)
 */
public class K2530341_FeaturedDecorator extends K2530341_BookDecorator {
    
    public K2530341_FeaturedDecorator(K2530341_BookComponent wrappedBook) {
        super(wrappedBook);
    }
    
    @Override
    public String getDescription() {
        return "⭐ FEATURED: " + wrappedBook.getDescription();
    }
    
    @Override
    public int getPriority() {
        return wrappedBook.getPriority() + 10;
    }
}
