package com.k2530341.slms.patterns.command;

import com.k2530341.slms.service.K2530341_LibraryService;

/**
 * Command for returning a book.
 * Author: M.Y.M. SAJIDH (K2530341)
 */
public class K2530341_ReturnCommand implements K2530341_Command {
    private final K2530341_LibraryService libraryService;
    private final String borrowId;
    private boolean executed = false;
    
    public K2530341_ReturnCommand(K2530341_LibraryService libraryService, String borrowId) {
        this.libraryService = libraryService;
        this.borrowId = borrowId;
    }
    
    @Override
    public boolean execute() {
        executed = libraryService.returnBook(borrowId);
        return executed;
    }
    
    @Override
    public void undo() {
        // Return cannot be easily undone without complex state tracking
        System.out.println("Return command cannot be undone");
    }
    
    @Override
    public String getDescription() {
        return "Return borrow " + borrowId;
    }
}
