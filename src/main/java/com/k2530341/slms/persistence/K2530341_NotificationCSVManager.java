package com.k2530341.slms.persistence;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.k2530341.slms.model.notification.K2530341_Notification;
import com.k2530341.slms.model.notification.K2530341_NotificationType;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * CSV persistence manager for notifications with atomic write operations.
 * Author: M.Y.M. SAJIDH (K2530341)
 */
public class K2530341_NotificationCSVManager {
    private static final String CSV_FILE = "data/notifications.csv";
    private static final String[] HEADER = {
        "notificationId", "userId", "type", "message", "dateISO", "readFlag"
    };
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    /**
     * Load all notifications from CSV file.
     * @return List of notifications
     */
    public List<K2530341_Notification> loadNotifications() {
        List<K2530341_Notification> notifications = new ArrayList<>();
        File file = new File(CSV_FILE);
        
        if (!file.exists()) {
            return notifications;
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
                    K2530341_Notification notification = new K2530341_Notification(
                        line[0], // notificationId
                        line[1], // userId
                        K2530341_NotificationType.valueOf(line[2]), // type
                        line[3], // message
                        LocalDate.parse(line[4], DATE_FORMATTER), // date
                        Boolean.parseBoolean(line[5]) // readFlag
                    );
                    notifications.add(notification);
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading notifications: " + e.getMessage());
        }
        
        return notifications;
    }
    
    /**
     * Save all notifications to CSV file with atomic write operation.
     * @param notifications List of notifications to save
     */
    public void saveNotifications(List<K2530341_Notification> notifications) {
        try {
            // Ensure directory exists
            Files.createDirectories(Paths.get("data"));
            
            // Write to temporary file first
            String tempFile = CSV_FILE + ".tmp";
            try (CSVWriter writer = new CSVWriter(new FileWriter(tempFile))) {
                // Write header
                writer.writeNext(HEADER);
                
                // Write notification data
                for (K2530341_Notification notification : notifications) {
                    String[] line = {
                        notification.getNotificationId(),
                        notification.getUserId(),
                        notification.getType().toString(),
                        notification.getMessage(),
                        notification.getDate().format(DATE_FORMATTER),
                        String.valueOf(notification.isReadFlag())
                    };
                    writer.writeNext(line);
                }
            }
            
            // Atomic rename
            Files.move(Paths.get(tempFile), Paths.get(CSV_FILE), 
                      StandardCopyOption.REPLACE_EXISTING, 
                      StandardCopyOption.ATOMIC_MOVE);
                      
        } catch (Exception e) {
            System.err.println("Error saving notifications: " + e.getMessage());
        }
    }
}
