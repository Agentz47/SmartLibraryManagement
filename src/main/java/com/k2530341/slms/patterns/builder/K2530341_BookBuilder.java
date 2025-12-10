package com.k2530341.slms.patterns.builder;

import com.k2530341.slms.model.book.K2530341_Book;
import com.k2530341.slms.model.book.K2530341_AvailabilityStatus;

/**
 * Builder pattern for constructing Book objects with optional fields.
 * Author: M.Y.M. SAJIDH (K2530341)
 */
public class K2530341_BookBuilder {
    private String bookId;
    private String title;
    private String author;
    private String category;
    private String isbn;
    private K2530341_AvailabilityStatus availabilityStatus = K2530341_AvailabilityStatus.AVAILABLE;
    private int borrowHistoryCount = 0;
    private String optionalTags = "";
    private String edition = "";
    
    public K2530341_BookBuilder setBookId(String bookId) {
        this.bookId = bookId;
        return this;
    }
    
    public K2530341_BookBuilder setTitle(String title) {
        this.title = title;
        return this;
    }
    
    public K2530341_BookBuilder setAuthor(String author) {
        this.author = author;
        return this;
    }
    
    public K2530341_BookBuilder setCategory(String category) {
        this.category = category;
        return this;
    }
    
    public K2530341_BookBuilder setIsbn(String isbn) {
        this.isbn = isbn;
        return this;
    }
    
    public K2530341_BookBuilder setAvailabilityStatus(K2530341_AvailabilityStatus availabilityStatus) {
        this.availabilityStatus = availabilityStatus;
        return this;
    }
    
    public K2530341_BookBuilder setBorrowHistoryCount(int borrowHistoryCount) {
        this.borrowHistoryCount = borrowHistoryCount;
        return this;
    }
    
    public K2530341_BookBuilder setOptionalTags(String optionalTags) {
        this.optionalTags = optionalTags;
        return this;
    }
    
    public K2530341_BookBuilder setEdition(String edition) {
        this.edition = edition;
        return this;
    }
    
    public K2530341_Book build() {
        if (bookId == null || title == null || author == null) {
            throw new IllegalStateException("Required fields (bookId, title, author) must be set");
        }
        return new K2530341_Book(bookId, title, author, category, isbn, 
                                availabilityStatus, borrowHistoryCount, 
                                optionalTags, edition);
    }
}
