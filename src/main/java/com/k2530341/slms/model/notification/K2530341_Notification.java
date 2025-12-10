package com.k2530341.slms.model.notification;

import java.time.LocalDate;

/**
 * Represents a notification in the library system.
 * Author: M.Y.M. SAJIDH (K2530341)
 */
public class K2530341_Notification {
    private String notificationId;
    private String userId;
    private K2530341_NotificationType type;
    private String message;
    private LocalDate date;
    private boolean readFlag;
    
    public K2530341_Notification(String notificationId, String userId, 
                                K2530341_NotificationType type, String message, 
                                LocalDate date, boolean readFlag) {
        this.notificationId = notificationId;
        this.userId = userId;
        this.type = type;
        this.message = message;
        this.date = date;
        this.readFlag = readFlag;
    }
    
    // Getters and setters
    public String getNotificationId() {
        return notificationId;
    }
    
    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public K2530341_NotificationType getType() {
        return type;
    }
    
    public void setType(K2530341_NotificationType type) {
        this.type = type;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public LocalDate getDate() {
        return date;
    }
    
    public void setDate(LocalDate date) {
        this.date = date;
    }
    
    public boolean isReadFlag() {
        return readFlag;
    }
    
    public void setReadFlag(boolean readFlag) {
        this.readFlag = readFlag;
    }
    
    @Override
    public String toString() {
        return "Notification{" +
                "type=" + type +
                ", message='" + message + '\'' +
                ", date=" + date +
                '}';
    }
}
