package com.k2530341.slms.persistence;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.k2530341.slms.model.K2530341_Borrow;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * CSV persistence manager for borrow records with atomic write operations.
 * Author: M.Y.M. SAJIDH (K2530341)
 */
public class K2530341_BorrowCSVManager {
    private static final String CSV_FILE = "data/borrows.csv";
    private static final String[] HEADER = {
        "borrowId", "bookId", "userId", "borrowDateISO", "dueDateISO", "returnDateISO", "finePaid"
    };
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    /**
     * Load all borrow records from CSV file.
     * @return List of borrow records
     */
    public List<K2530341_Borrow> loadBorrows() {
        List<K2530341_Borrow> borrows = new ArrayList<>();
        File file = new File(CSV_FILE);
        
        if (!file.exists()) {
            return borrows;
        }
        
        try (CSVReader reader = new CSVReader(new FileReader(file))) {
            String[] line;
            boolean firstLine = true;
            
            while ((line = reader.readNext()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue; // Skip header
                }
                
                if (line.length >= 7) {
                    LocalDate returnDate = line[5].isEmpty() ? null : LocalDate.parse(line[5], DATE_FORMATTER);
                    
                    K2530341_Borrow borrow = new K2530341_Borrow(
                        line[0], // borrowId
                        line[1], // bookId
                        line[2], // userId
                        LocalDate.parse(line[3], DATE_FORMATTER), // borrowDate
                        LocalDate.parse(line[4], DATE_FORMATTER), // dueDate
                        returnDate, // returnDate
                        Double.parseDouble(line[6]) // finePaid
                    );
                    borrows.add(borrow);
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading borrows: " + e.getMessage());
        }
        
        return borrows;
    }
    
    /**
     * Save all borrow records to CSV file with atomic write operation.
     * @param borrows List of borrow records to save
     */
    public void saveBorrows(List<K2530341_Borrow> borrows) {
        try {
            // Ensure directory exists
            Files.createDirectories(Paths.get("data"));
            
            // Write to temporary file first
            String tempFile = CSV_FILE + ".tmp";
            try (CSVWriter writer = new CSVWriter(new FileWriter(tempFile))) {
                // Write header
                writer.writeNext(HEADER);
                
                // Write borrow data
                for (K2530341_Borrow borrow : borrows) {
                    String[] line = {
                        borrow.getBorrowId(),
                        borrow.getBookId(),
                        borrow.getUserId(),
                        borrow.getBorrowDate().format(DATE_FORMATTER),
                        borrow.getDueDate().format(DATE_FORMATTER),
                        borrow.getReturnDate() != null ? borrow.getReturnDate().format(DATE_FORMATTER) : "",
                        String.valueOf(borrow.getFinePaid())
                    };
                    writer.writeNext(line);
                }
            }
            
            // Atomic rename
            Files.move(Paths.get(tempFile), Paths.get(CSV_FILE), 
                      StandardCopyOption.REPLACE_EXISTING, 
                      StandardCopyOption.ATOMIC_MOVE);
                      
        } catch (Exception e) {
            System.err.println("Error saving borrows: " + e.getMessage());
        }
    }
}
