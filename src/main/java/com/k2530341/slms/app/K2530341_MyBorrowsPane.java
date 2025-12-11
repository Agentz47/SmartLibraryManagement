package com.k2530341.slms.app;

import com.k2530341.slms.model.K2530341_Borrow;
import com.k2530341.slms.model.book.K2530341_Book;
import com.k2530341.slms.model.user.K2530341_User;
import com.k2530341.slms.service.K2530341_LibraryService;
import com.k2530341.slms.patterns.command.*;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.*;

/**
 * My borrows pane for users.
 * Author: M.Y.M. SAJIDH (K2530341)
 */
public class K2530341_MyBorrowsPane extends VBox {
    private final K2530341_LibraryService libraryService;
    private final String userId;
    private final K2530341_CommandManager commandManager;
    private ListView<String> borrowsList;
    private Label borrowLimitLabel;
    
    public K2530341_MyBorrowsPane(K2530341_LibraryService libraryService, String userId, K2530341_CommandManager commandManager) {
        this.libraryService = libraryService;
        this.userId = userId;
        this.commandManager = commandManager;
        setupUI();
        refreshList();
    }
    
    private void setupUI() {
        setPadding(new Insets(15));
        setSpacing(10);
        
        Label titleLabel = new Label("My Borrowed Books");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        // Add borrow limit display
        borrowLimitLabel = new Label();
        borrowLimitLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #2196F3;");
        updateBorrowLimitLabel();
        
        borrowsList = new ListView<>();
        borrowsList.setPrefHeight(400);
        
        HBox actionBox = new HBox(10);
        Button returnBtn = new Button("Return Selected");
        Button refreshBtn = new Button("Refresh");
        
        returnBtn.setOnAction(e -> returnSelectedBook());
        refreshBtn.setOnAction(e -> refreshList());
        
        actionBox.getChildren().addAll(returnBtn, refreshBtn);
        
        getChildren().addAll(titleLabel, borrowLimitLabel, borrowsList, actionBox);
        VBox.setVgrow(borrowsList, Priority.ALWAYS);
    }
    
    private void updateBorrowLimitLabel() {
        K2530341_User user = libraryService.getUser(userId);
        if (user != null) {
            int currentBorrows = user.getCurrentBorrowCount();
            int borrowLimit = user.getBorrowLimit();
            int remaining = borrowLimit - currentBorrows;
            
            String limitText = String.format("📚 Borrow Status: %d / %d books borrowed | %d slots remaining", 
                currentBorrows, borrowLimit, remaining);
            
            // Change color based on usage
            if (remaining == 0) {
                borrowLimitLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #F44336; -fx-font-weight: bold;");
            } else if (remaining <= 2) {
                borrowLimitLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #FF9800; -fx-font-weight: bold;");
            } else {
                borrowLimitLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #4CAF50; -fx-font-weight: bold;");
            }
            
            borrowLimitLabel.setText(limitText);
        }
    }
    
    private void refreshList() {
        List<String> items = new ArrayList<>();
        List<K2530341_Borrow> borrows = libraryService.getActiveBorrows(userId);
        
        for (K2530341_Borrow borrow : borrows) {
            K2530341_Book book = libraryService.getBook(borrow.getBookId());
            if (book != null) {
                String status = borrow.isOverdue() ? "OVERDUE" : "Active";
                
                // Calculate fine for display
                double fine = libraryService.calculateFineForBorrow(borrow.getBorrowId());
                String fineText = fine > 0 ? String.format(" | Fine: LKR %.2f", fine) : " | No Fine";
                
                String item = String.format("[%s] %s | Due: %s%s | ID: %s",
                    status, book.getTitle(), borrow.getDueDate(), fineText, borrow.getBorrowId());
                items.add(item);
            }
        }
        
        if (items.isEmpty()) {
            items.add("No active borrows");
        }
        
        borrowsList.setItems(FXCollections.observableArrayList(items));
        updateBorrowLimitLabel(); // Update the limit label when refreshing
    }
    
    private void returnSelectedBook() {
        String selected = borrowsList.getSelectionModel().getSelectedItem();
        if (selected == null || selected.equals("No active borrows")) {
            showAlert("Warning", "Please select a borrow to return.");
            return;
        }
        
        // Extract borrow ID from the string
        int idIndex = selected.lastIndexOf("ID: ");
        if (idIndex == -1) return;
        String borrowId = selected.substring(idIndex + 4);
        
        // Calculate fine before returning
        double fine = libraryService.calculateFineForBorrow(borrowId);
        
        // Show confirmation with fine info
        String fineMessage = fine > 0 
            ? String.format("\n\nFine to be charged: LKR %.2f", fine)
            : "\n\nNo fine - returned on time!";
        
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Return");
        confirmAlert.setHeaderText("Are you sure you want to return this book?");
        confirmAlert.setContentText("Book will be returned." + fineMessage);
        
        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            K2530341_Command returnCommand = new K2530341_ReturnCommand(libraryService, borrowId);
            boolean success = commandManager.executeCommand(returnCommand);
            
            if (success) {
                String successMessage = fine > 0
                    ? String.format("Book returned successfully!\n\nFine charged: LKR %.2f\nPlease pay at the library counter.", fine)
                    : "Book returned successfully!\n\nNo fine - returned on time. Thank you!";
                    
                showAlert("Success", successMessage);
                refreshList();
            } else {
                showAlert("Error", "Cannot return this book.");
            }
        }
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
