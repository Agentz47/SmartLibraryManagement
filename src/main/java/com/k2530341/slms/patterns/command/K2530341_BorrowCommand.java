package com.k2530341.slms.patterns.command;

import com.k2530341.slms.service.K2530341_LibraryService;

/**
 * Command for borrowing a book.
 * Author: M.Y.M. SAJIDH (K2530341)
 */
public class K2530341_BorrowCommand implements K2530341_Command {
    private final K2530341_LibraryService libraryService;
    private final String bookId;
    private final String userId;
    private String borrowId;
    private boolean executed = false;
    
    public K2530341_BorrowCommand(K2530341_LibraryService libraryService, String bookId, String userId) {
        this.libraryService = libraryService;
        this.bookId = bookId;
        this.userId = userId;
    }
    
    @Override
    public boolean execute() {
        borrowId = libraryService.borrowBook(bookId, userId);
        executed = (borrowId != null);
        return executed;
    }
    
    @Override
    public void undo() {
        if (executed && borrowId != null) {
            libraryService.returnBook(borrowId);
        }
    }
    
    @Override
    public String getDescription() {
        return "Borrow book " + bookId + " for user " + userId;
    }
}
