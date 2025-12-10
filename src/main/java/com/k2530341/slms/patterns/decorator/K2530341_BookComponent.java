package com.k2530341.slms.patterns.decorator;

/**
 * Component interface for the Decorator pattern.
 * Author: M.Y.M. SAJIDH (K2530341)
 */
public interface K2530341_BookComponent {
    /**
     * Get the description of the book component.
     * @return Description
     */
    String getDescription();
    
    /**
     * Get the display priority (higher = more prominent).
     * @return Priority value
     */
    int getPriority();
}
