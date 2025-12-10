package com.k2530341.slms.persistence;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.k2530341.slms.model.reservation.K2530341_Reservation;
import com.k2530341.slms.model.reservation.K2530341_ReservationStatus;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * CSV persistence manager for reservations with atomic write operations.
 * Author: M.Y.M. SAJIDH (K2530341)
 */
public class K2530341_ReservationCSVManager {
    private static final String CSV_FILE = "data/reservations.csv";
    private static final String[] HEADER = {
        "reservationId", "bookId", "userId", "reservationDateISO", "notifiedAtISO", "status"
    };
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    
    /**
     * Load all reservations from CSV file.
     * @return List of reservations
     */
    public List<K2530341_Reservation> loadReservations() {
        List<K2530341_Reservation> reservations = new ArrayList<>();
        File file = new File(CSV_FILE);
        
        if (!file.exists()) {
            return reservations;
        }
        
        try (CSVReader reader = new CSVReader(new FileReader(file))) {
            String[] line;
            boolean firstLine = true;
            
            while ((line = reader.readNext()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue; // Skip header
                }
                
                if (line.length >= 6) {
                    LocalDateTime notifiedAt = line[4].isEmpty() ? null : LocalDateTime.parse(line[4], DATETIME_FORMATTER);
                    
                    K2530341_Reservation reservation = new K2530341_Reservation(
                        line[0], // reservationId
                        line[1], // bookId
                        line[2], // userId
                        LocalDateTime.parse(line[3], DATETIME_FORMATTER), // reservationDate
                        notifiedAt, // notifiedAt
                        K2530341_ReservationStatus.valueOf(line[5]) // status
                    );
                    reservations.add(reservation);
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading reservations: " + e.getMessage());
        }
        
        return reservations;
    }
    
    /**
     * Save all reservations to CSV file with atomic write operation.
     * @param reservations List of reservations to save
     */
    public void saveReservations(List<K2530341_Reservation> reservations) {
        try {
            // Ensure directory exists
            Files.createDirectories(Paths.get("data"));
            
            // Write to temporary file first
            String tempFile = CSV_FILE + ".tmp";
            try (CSVWriter writer = new CSVWriter(new FileWriter(tempFile))) {
                // Write header
                writer.writeNext(HEADER);
                
                // Write reservation data
                for (K2530341_Reservation reservation : reservations) {
                    String[] line = {
                        reservation.getReservationId(),
                        reservation.getBookId(),
                        reservation.getUserId(),
                        reservation.getReservationDate().format(DATETIME_FORMATTER),
                        reservation.getNotifiedAt() != null ? reservation.getNotifiedAt().format(DATETIME_FORMATTER) : "",
                        reservation.getStatus().toString()
                    };
                    writer.writeNext(line);
                }
            }
            
            // Atomic rename
            Files.move(Paths.get(tempFile), Paths.get(CSV_FILE), 
                      StandardCopyOption.REPLACE_EXISTING, 
                      StandardCopyOption.ATOMIC_MOVE);
                      
        } catch (Exception e) {
            System.err.println("Error saving reservations: " + e.getMessage());
        }
    }
}
