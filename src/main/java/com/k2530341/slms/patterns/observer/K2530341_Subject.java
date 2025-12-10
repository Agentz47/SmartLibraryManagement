package com.k2530341.slms.patterns.observer;

/**
 * Subject interface for the Observer pattern.
 * Author: M.Y.M. SAJIDH (K2530341)
 */
public interface K2530341_Subject {
    /**
     * Attach an observer to this subject.
     * @param observer The observer to attach
     */
    void attach(K2530341_Observer observer);
    
    /**
     * Detach an observer from this subject.
     * @param observer The observer to detach
     */
    void detach(K2530341_Observer observer);
    
    /**
     * Notify all attached observers of an event.
     * @param event The event type
     * @param message The notification message
     */
    void notifyObservers(String event, String message);
}
