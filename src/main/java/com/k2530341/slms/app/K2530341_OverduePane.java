package com.k2530341.slms.app;

import com.k2530341.slms.model.K2530341_Borrow;
import com.k2530341.slms.model.book.K2530341_Book;
import com.k2530341.slms.model.user.K2530341_User;
import com.k2530341.slms.model.notification.K2530341_NotificationType;
import com.k2530341.slms.service.K2530341_LibraryService;
import com.k2530341.slms.patterns.strategy.*;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.LocalDate;
import java.util.*;

/**
 * Overdue books pane for librarians.
 * Author: M.Y.M. SAJIDH (K2530341)
 */
public class K2530341_OverduePane extends VBox {
    private final K2530341_LibraryService libraryService;
    private TableView<OverdueItem> overdueTable;
    
    public K2530341_OverduePane(K2530341_LibraryService libraryService) {
        this.libraryService = libraryService;
        setupUI();
        refreshTable();
    }
    
    private void setupUI() {
        setPadding(new Insets(15));
        setSpacing(10);
        
        Label titleLabel = new Label("Overdue Books & Notifications");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        // Table setup
        overdueTable = new TableView<>();
        overdueTable.setPrefHeight(400);
        
        TableColumn<OverdueItem, String> userCol = new TableColumn<>("User");
        userCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().userName));
        userCol.setPrefWidth(120);
        
        TableColumn<OverdueItem, String> bookCol = new TableColumn<>("Book");
        bookCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().bookTitle));
        bookCol.setPrefWidth(200);
        
        TableColumn<OverdueItem, String> dueCol = new TableColumn<>("Due Date");
        dueCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().dueDate.toString()));
        dueCol.setPrefWidth(100);
        
        TableColumn<OverdueItem, String> overdueCol = new TableColumn<>("Days Overdue");
        overdueCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(String.valueOf(data.getValue().overdueDays)));
        overdueCol.setPrefWidth(100);
        
        TableColumn<OverdueItem, String> fineCol = new TableColumn<>("Fine Amount");
        fineCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty("LKR " + data.getValue().fineAmount));
        fineCol.setPrefWidth(100);
        
        overdueTable.getColumns().addAll(userCol, bookCol, dueCol, overdueCol, fineCol);
        
        // Buttons
        HBox buttonBox = new HBox(10);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));
        
        Button refreshBtn = new Button("Refresh");
        refreshBtn.setOnAction(e -> refreshTable());
        
        Button sendDueReminderBtn = new Button("Send Due Reminder to All");
        sendDueReminderBtn.setOnAction(e -> sendDueReminders());
        
        Button sendOverdueAlertBtn = new Button("Send Overdue Alert");
        sendOverdueAlertBtn.setOnAction(e -> sendOverdueAlert());
        
        Button sendFineNotificationBtn = new Button("Send Fine Notification");
        sendFineNotificationBtn.setOnAction(e -> sendFineNotification());
        
        buttonBox.getChildren().addAll(refreshBtn, sendDueReminderBtn, sendOverdueAlertBtn, sendFineNotificationBtn);
        
        getChildren().addAll(titleLabel, overdueTable, buttonBox);
        VBox.setVgrow(overdueTable, Priority.ALWAYS);
    }
    
    private void refreshTable() {
        List<OverdueItem> items = new ArrayList<>();
        List<K2530341_Borrow> overdueBorrows = libraryService.getOverdueBorrows();
        
        for (K2530341_Borrow borrow : overdueBorrows) {
            K2530341_Book book = libraryService.getBook(borrow.getBookId());
            K2530341_User user = libraryService.getUser(borrow.getUserId());
            
            if (book != null && user != null) {
                K2530341_FineStrategy fineStrategy = getFineStrategy(user.getMembershipType());
                double fine = fineStrategy.calculateFine(borrow.getOverdueDays());
                
                items.add(new OverdueItem(
                    borrow.getUserId(),
                    user.getName(),
                    book.getTitle(),
                    borrow.getDueDate(),
                    borrow.getOverdueDays(),
                    fine
                ));
            }
        }
        
        overdueTable.setItems(FXCollections.observableArrayList(items));
    }
    
    private void sendDueReminders() {
        int count = 0;
        LocalDate today = LocalDate.now();
        LocalDate threeDaysFromNow = today.plusDays(3);
        
        // Send reminders for books due within 3 days
        List<K2530341_Borrow> allBorrows = libraryService.getAllBorrows();
        for (K2530341_Borrow borrow : allBorrows) {
            if (borrow.getReturnDate() == null) { // Not returned yet
                LocalDate dueDate = borrow.getDueDate();
                if (!dueDate.isBefore(today) && !dueDate.isAfter(threeDaysFromNow)) {
                    K2530341_Book book = libraryService.getBook(borrow.getBookId());
                    if (book != null) {
                        long daysUntilDue = java.time.temporal.ChronoUnit.DAYS.between(today, dueDate);
                        libraryService.createNotification(
                            borrow.getUserId(),
                            K2530341_NotificationType.DUE_REMINDER,
                            "Reminder: '" + book.getTitle() + "' is due in " + daysUntilDue + " days (Due: " + dueDate + ")"
                        );
                        count++;
                    }
                }
            }
        }
        
        showAlert("Success", count + " due reminders sent to users with books due within 3 days.");
        libraryService.saveAllData();
    }
    
    private void sendOverdueAlert() {
        OverdueItem selected = overdueTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Warning", "Please select an overdue book to send alert.");
            return;
        }
        
        libraryService.createNotification(
            selected.userId,
            K2530341_NotificationType.OVERDUE_ALERT,
            "OVERDUE ALERT: '" + selected.bookTitle + "' is " + selected.overdueDays + " days overdue! " +
            "Fine: LKR " + selected.fineAmount + ". Please return immediately."
        );
        
        showAlert("Success", "Overdue alert sent to " + selected.userName);
        libraryService.saveAllData();
    }
    
    private void sendFineNotification() {
        OverdueItem selected = overdueTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Warning", "Please select an overdue book to send fine notification.");
            return;
        }
        
        libraryService.createNotification(
            selected.userId,
            K2530341_NotificationType.FINE_ALERT,
            "FINE NOTIFICATION: You owe LKR " + selected.fineAmount + " for '" + selected.bookTitle + "' " +
            "(" + selected.overdueDays + " days overdue). Please pay at the library counter."
        );
        
        showAlert("Success", "Fine notification sent to " + selected.userName);
        libraryService.saveAllData();
    }
    
    private K2530341_FineStrategy getFineStrategy(com.k2530341.slms.model.user.K2530341_MembershipType type) {
        switch (type) {
            case STUDENT:
                return new K2530341_StudentFineStrategy();
            case FACULTY:
                return new K2530341_FacultyFineStrategy();
            case GUEST:
                return new K2530341_GuestFineStrategy();
            default:
                return new K2530341_StudentFineStrategy();
        }
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    // Inner class for table data
    private static class OverdueItem {
        String userId;
        String userName;
        String bookTitle;
        LocalDate dueDate;
        long overdueDays;
        double fineAmount;
        
        OverdueItem(String userId, String userName, String bookTitle, LocalDate dueDate, long overdueDays, double fineAmount) {
            this.userId = userId;
            this.userName = userName;
            this.bookTitle = bookTitle;
            this.dueDate = dueDate;
            this.overdueDays = overdueDays;
            this.fineAmount = fineAmount;
        }
    }
}