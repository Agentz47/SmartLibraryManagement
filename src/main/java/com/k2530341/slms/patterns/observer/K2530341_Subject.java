package com.k2530341.slms.patterns.observer;

/**
 * Subject interface for Observer pattern
 * This is the object that gets observed (like Book being observed by Users)
 * @author Sajidh (K2530341)
 */
public interface K2530341_Subject {
    // add an observer
    void attach(K2530341_Observer observer);
    
    // remove an observer
    void detach(K2530341_Observer observer);
    
    // tell all observers about something that happened
    void notifyObservers(String event, String message);
}
