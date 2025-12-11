package com.k2530341.slms.patterns.observer;
/**
 * Observer interface - objects that want to be notified of changes
 * Part of Observer design pattern
 * @author Sajidh (K2530341)
 */
public interface K2530341_Observer {
    // this gets called when something changes in the subject
    void update(String event, String message);
}
