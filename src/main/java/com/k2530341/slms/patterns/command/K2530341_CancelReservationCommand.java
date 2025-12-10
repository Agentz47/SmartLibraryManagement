package com.k2530341.slms.patterns.command;

import com.k2530341.slms.service.K2530341_LibraryService;

/**
 * Command for cancelling a reservation.
 * Author: M.Y.M. SAJIDH (K2530341)
 */
public class K2530341_CancelReservationCommand implements K2530341_Command {
    private final K2530341_LibraryService libraryService;
    private final String reservationId;
    private boolean executed = false;
    
    public K2530341_CancelReservationCommand(K2530341_LibraryService libraryService, String reservationId) {
        this.libraryService = libraryService;
        this.reservationId = reservationId;
    }
    
    @Override
    public boolean execute() {
        executed = libraryService.cancelReservation(reservationId);
        return executed;
    }
    
    @Override
    public void undo() {
        // Cancellation cannot be easily undone
        System.out.println("Cancel reservation command cannot be undone");
    }
    
    @Override
    public String getDescription() {
        return "Cancel reservation " + reservationId;
    }
}
