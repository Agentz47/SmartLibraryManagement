package com.k2530341.slms.patterns.observer;

/**
 * Observer interface for the Observer pattern.
 * Author: M.Y.M. SAJIDH (K2530341)
 */
public interface K2530341_Observer {
    /**
     * Update method called when the subject's state changes.
     * @param event The event type
     * @param message The notification message
     */
    void update(String event, String message);
}
