package com.k2530341.slms.app;

import com.k2530341.slms.model.reservation.K2530341_Reservation;
import com.k2530341.slms.model.book.K2530341_Book;
import com.k2530341.slms.service.K2530341_LibraryService;
import com.k2530341.slms.patterns.command.K2530341_CancelReservationCommand;
import com.k2530341.slms.patterns.command.K2530341_Command;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.*;

/**
 * My reservations pane for users.
 * Author: M.Y.M. SAJIDH (K2530341)
 */
public class K2530341_MyReservationsPane extends VBox {
    private final K2530341_LibraryService libraryService;
    private final String userId;
    private ListView<String> reservationsList;
    private List<K2530341_Reservation> currentReservations;
    
    public K2530341_MyReservationsPane(K2530341_LibraryService libraryService, String userId) {
        this.libraryService = libraryService;
        this.userId = userId;
        setupUI();
        refreshList();
    }
    
    private void setupUI() {
        setPadding(new Insets(15));
        setSpacing(10);
        
        Label titleLabel = new Label("My Reservations");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        reservationsList = new ListView<>();
        reservationsList.setPrefHeight(400);
        
        HBox buttonBox = new HBox(10);
        Button cancelBtn = new Button("Cancel Reservation");
        cancelBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        cancelBtn.setOnAction(e -> cancelSelectedReservation());
        
        Button refreshBtn = new Button("Refresh");
        refreshBtn.setOnAction(e -> refreshList());
        
        buttonBox.getChildren().addAll(cancelBtn, refreshBtn);
        
        getChildren().addAll(titleLabel, reservationsList, buttonBox);
        VBox.setVgrow(reservationsList, Priority.ALWAYS);
    }
    
    private void refreshList() {
        List<String> items = new ArrayList<>();
        currentReservations = libraryService.getUserReservations(userId);
        
        for (K2530341_Reservation reservation : currentReservations) {
            K2530341_Book book = libraryService.getBook(reservation.getBookId());
            if (book != null) {
                String item = String.format("[%s] %s | Date: %s",
                    reservation.getStatus(),
                    book.getTitle(),
                    reservation.getReservationDate().toLocalDate());
                items.add(item);
            }
        }
        
        if (items.isEmpty()) {
            items.add("No reservations");
        }
        
        reservationsList.setItems(FXCollections.observableArrayList(items));
    }
    
    private void cancelSelectedReservation() {
        int selectedIndex = reservationsList.getSelectionModel().getSelectedIndex();
        
        if (selectedIndex < 0 || currentReservations == null || selectedIndex >= currentReservations.size()) {
            showAlert("Warning", "Please select a reservation to cancel.");
            return;
        }
        
        K2530341_Reservation reservation = currentReservations.get(selectedIndex);
        
        // Only allow canceling PENDING reservations
        if (reservation.getStatus() != com.k2530341.slms.model.reservation.K2530341_ReservationStatus.PENDING) {
            showAlert("Cannot Cancel", "Only pending reservations can be cancelled. This reservation is already " + reservation.getStatus());
            return;
        }
        
        // Use Command Pattern to cancel reservation
        K2530341_Command cancelCommand = new K2530341_CancelReservationCommand(libraryService, reservation.getReservationId());
        boolean success = cancelCommand.execute();
        
        if (success) {
            showAlert("Success", "Reservation cancelled successfully!");
            refreshList();
        } else {
            showAlert("Error", "Failed to cancel reservation.");
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
