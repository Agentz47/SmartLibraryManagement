package com.k2530341.slms.model.reservation;

import java.time.LocalDateTime;

/**
 * Represents a reservation in the library system.
 * Author: M.Y.M. SAJIDH (K2530341)
 */
public class K2530341_Reservation {
    private String reservationId;
    private String bookId;
    private String userId;
    private LocalDateTime reservationDate;
    private LocalDateTime notifiedAt;
    private K2530341_ReservationStatus status;
    
    public K2530341_Reservation(String reservationId, String bookId, String userId, 
                               LocalDateTime reservationDate, K2530341_ReservationStatus status) {
        this.reservationId = reservationId;
        this.bookId = bookId;
        this.userId = userId;
        this.reservationDate = reservationDate;
        this.status = status;
        this.notifiedAt = null;
    }
    
    public K2530341_Reservation(String reservationId, String bookId, String userId, 
                               LocalDateTime reservationDate, LocalDateTime notifiedAt, 
                               K2530341_ReservationStatus status) {
        this.reservationId = reservationId;
        this.bookId = bookId;
        this.userId = userId;
        this.reservationDate = reservationDate;
        this.notifiedAt = notifiedAt;
        this.status = status;
    }
    
    public boolean isExpired() {
        if (status == K2530341_ReservationStatus.NOTIFIED && notifiedAt != null) {
            LocalDateTime expiryTime = notifiedAt.plusHours(48);
            return LocalDateTime.now().isAfter(expiryTime);
        }
        return false;
    }
    
    // Getters and setters
    public String getReservationId() {
        return reservationId;
    }
    
    public void setReservationId(String reservationId) {
        this.reservationId = reservationId;
    }
    
    public String getBookId() {
        return bookId;
    }
    
    public void setBookId(String bookId) {
        this.bookId = bookId;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public LocalDateTime getReservationDate() {
        return reservationDate;
    }
    
    public void setReservationDate(LocalDateTime reservationDate) {
        this.reservationDate = reservationDate;
    }
    
    public LocalDateTime getNotifiedAt() {
        return notifiedAt;
    }
    
    public void setNotifiedAt(LocalDateTime notifiedAt) {
        this.notifiedAt = notifiedAt;
    }
    
    public K2530341_ReservationStatus getStatus() {
        return status;
    }
    
    public void setStatus(K2530341_ReservationStatus status) {
        this.status = status;
    }
    
    @Override
    public String toString() {
        return "Reservation{" +
                "reservationId='" + reservationId + '\'' +
                ", bookId='" + bookId + '\'' +
                ", userId='" + userId + '\'' +
                ", status=" + status +
                '}';
    }
}
