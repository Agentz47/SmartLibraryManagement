package com.k2530341.slms.persistence;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.k2530341.slms.model.book.K2530341_Book;
import com.k2530341.slms.model.book.K2530341_AvailabilityStatus;
import com.k2530341.slms.patterns.builder.K2530341_BookBuilder;

import java.io.*;
import java.nio.file.*;
import java.util.*;

 //CSV persistence manager for books with atomic write operations.
public class K2530341_BookCSVManager {
    private static final String CSV_FILE = "data/books.csv";
    private static final String[] HEADER = {
        "bookId", "title", "author", "category", "isbn", 
        "availabilityStatus", "borrowHistoryCount", "optionalTags", "edition"
    };
    
    /**
     * Load all books from CSV file.
     * @return List of books
     */
    public List<K2530341_Book> loadBooks() {
        List<K2530341_Book> books = new ArrayList<>();
        File file = new File(CSV_FILE);
        
        if (!file.exists()) {
            return books;
        }
        
        try (CSVReader reader = new CSVReader(new FileReader(file))) {
            String[] line;
            boolean firstLine = true;
            
            while ((line = reader.readNext()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue; // Skip header
                }
                
                if (line.length >= 9) {
                    K2530341_Book book = new K2530341_BookBuilder()
                        .setBookId(line[0])
                        .setTitle(line[1])
                        .setAuthor(line[2])
                        .setCategory(line[3])
                        .setIsbn(line[4])
                        .setAvailabilityStatus(K2530341_AvailabilityStatus.valueOf(line[5]))
                        .setBorrowHistoryCount(Integer.parseInt(line[6]))
                        .setOptionalTags(line[7])
                        .setEdition(line[8])
                        .build();
                    books.add(book);
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading books: " + e.getMessage());
        }
        
        return books;
    }
    
    /**
     * Save all books to CSV file with atomic write operation.
     * @param books List of books to save
     */
    public void saveBooks(List<K2530341_Book> books) {
        try {
            // Ensure directory exists
            Files.createDirectories(Paths.get("data"));
            
            // Write to temporary file first
            String tempFile = CSV_FILE + ".tmp";
            try (CSVWriter writer = new CSVWriter(new FileWriter(tempFile))) {
                // Write header
                writer.writeNext(HEADER);
                
                // Write book data
                for (K2530341_Book book : books) {
                    String[] line = {
                        book.getBookId(),
                        book.getTitle(),
                        book.getAuthor(),
                        book.getCategory(),
                        book.getIsbn(),
                        book.getAvailabilityStatus().toString(),
                        String.valueOf(book.getBorrowHistoryCount()),
                        book.getOptionalTags(),
                        book.getEdition()
                    };
                    writer.writeNext(line);
                }
            }
            
            // Atomic rename
            Files.move(Paths.get(tempFile), Paths.get(CSV_FILE), 
                      StandardCopyOption.REPLACE_EXISTING, 
                      StandardCopyOption.ATOMIC_MOVE);
                      
        } catch (Exception e) {
            System.err.println("Error saving books: " + e.getMessage());
        }
    }
}
