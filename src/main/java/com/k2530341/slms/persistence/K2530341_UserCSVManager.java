package com.k2530341.slms.persistence;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.k2530341.slms.model.user.K2530341_User;
import com.k2530341.slms.model.user.K2530341_MembershipType;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * CSV persistence manager for users with atomic write operations.
 * Author: M.Y.M. SAJIDH (K2530341)
 */
public class K2530341_UserCSVManager {
    private static final String CSV_FILE = "data/users.csv";
    private static final String[] HEADER = {
        "userId", "name", "email", "contactNumber", "membershipType", "currentBorrowCount"
    };
    
    /**
     * Load all users from CSV file.
     * @return List of users
     */
    public List<K2530341_User> loadUsers() {
        List<K2530341_User> users = new ArrayList<>();
        File file = new File(CSV_FILE);
        
        if (!file.exists()) {
            return users;
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
                    K2530341_User user = new K2530341_User(
                        line[0], // userId
                        line[1], // name
                        line[2], // email
                        line[3], // contactNumber
                        K2530341_MembershipType.valueOf(line[4]), // membershipType
                        Integer.parseInt(line[5]) // currentBorrowCount
                    );
                    users.add(user);
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading users: " + e.getMessage());
        }
        
        return users;
    }
    
    /**
     * Save all users to CSV file with atomic write operation.
     * @param users List of users to save
     */
    public void saveUsers(List<K2530341_User> users) {
        try {
            // Ensure directory exists
            Files.createDirectories(Paths.get("data"));
            
            // Write to temporary file first
            String tempFile = CSV_FILE + ".tmp";
            try (CSVWriter writer = new CSVWriter(new FileWriter(tempFile))) {
                // Write header
                writer.writeNext(HEADER);
                
                // Write user data
                for (K2530341_User user : users) {
                    String[] line = {
                        user.getUserId(),
                        user.getName(),
                        user.getEmail(),
                        user.getContactNumber(),
                        user.getMembershipType().toString(),
                        String.valueOf(user.getCurrentBorrowCount())
                    };
                    writer.writeNext(line);
                }
            }
            
            // Atomic rename
            Files.move(Paths.get(tempFile), Paths.get(CSV_FILE), 
                      StandardCopyOption.REPLACE_EXISTING, 
                      StandardCopyOption.ATOMIC_MOVE);
                      
        } catch (Exception e) {
            System.err.println("Error saving users: " + e.getMessage());
        }
    }
}
