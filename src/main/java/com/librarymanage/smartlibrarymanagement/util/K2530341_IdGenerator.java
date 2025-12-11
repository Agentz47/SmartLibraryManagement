package com.librarymanage.smartlibrarymanagement.util;

public class K2530341_IdGenerator {
    // Generates next Book ID in format BK-XXXX
    public static String getNextBookId(int currentMax) {
        return String.format("BK-%03d", currentMax + 1);
    }

    // Generates next Student ID in format STU-XXXX
    public static String getNextStudentId(int currentMax) {
        return String.format("STU-%03d", currentMax + 1);
    }

    // Generates next Faculty ID in format FCL-XXXX
    public static String getNextFacultyId(int currentMax) {
        return String.format("FCL-%03d", currentMax + 1);
    }

    // Generates next Guest ID in format GST-XXXX
    public static String getNextGuestId(int currentMax) {
        return String.format("GST-%03d", currentMax + 1);
    }

    // Generates next Borrow ID in format BR-XXXX
    public static String getNextBorrowId(int currentMax) {
        return String.format("BR-%04d", currentMax + 1);
    }

    // Generates next Reservation ID in format RES-XXXX
    public static String getNextReservationId(int currentMax) {
        return String.format("RES-%04d", currentMax + 1);
    }

    // Generates next Notification ID in format NTF-XXXX
    public static String getNextNotificationId(int currentMax) {
        return String.format("NTF-%04d", currentMax + 1);
    }
}
