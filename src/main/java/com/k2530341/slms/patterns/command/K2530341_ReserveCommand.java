package com.k2530341.slms.patterns.command;

import com.k2530341.slms.service.K2530341_LibraryService;

/**
 * Command for reserving a book.
 * Author: M.Y.M. SAJIDH (K2530341)
 */
public class K2530341_ReserveCommand implements K2530341_Command {
    private final K2530341_LibraryService libraryService;
    private final String bookId;
    private final String userId;
    private String reservationId;
    private boolean executed = false;
    
    public K2530341_ReserveCommand(K2530341_LibraryService libraryService, String bookId, String userId) {
        this.libraryService = libraryService;
        this.bookId = bookId;
        this.userId = userId;
    }
    
    @Override
    public boolean execute() {
        reservationId = libraryService.reserveBook(bookId, userId);
        executed = (reservationId != null && reservationId.startsWith("RES-"));
        System.out.println("[ReserveCommand] User: " + userId + " reserved Book: " + bookId + ". Result: " + (executed ? ("SUCCESS (Reservation ID: " + reservationId + ")") : ("FAILED (" + reservationId + ")")));
        return executed;
    }
    
    public String executeWithResult() {
        reservationId = libraryService.reserveBook(bookId, userId);
        executed = (reservationId != null && reservationId.startsWith("RES-"));
        System.out.println("[ReserveCommand] User: " + userId + " reserved Book: " + bookId + ". Result: " + (executed ? ("SUCCESS (Reservation ID: " + reservationId + ")") : ("FAILED (" + reservationId + ")")));
        return reservationId;
    }
    
    @Override
    public void undo() {
        if (executed && reservationId != null) {
            libraryService.cancelReservation(reservationId);
        }
    }
    
    @Override
    public String getDescription() {
        return "Reserve book " + bookId + " for user " + userId;
    }
}
