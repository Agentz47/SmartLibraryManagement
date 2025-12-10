package com.k2530341.slms.app;

import com.k2530341.slms.model.reservation.K2530341_Reservation;
import com.k2530341.slms.model.book.K2530341_Book;
import com.k2530341.slms.service.K2530341_LibraryService;
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
        
        Button refreshBtn = new Button("Refresh");
        refreshBtn.setOnAction(e -> refreshList());
        
        getChildren().addAll(titleLabel, reservationsList, refreshBtn);
        VBox.setVgrow(reservationsList, Priority.ALWAYS);
    }
    
    private void refreshList() {
        List<String> items = new ArrayList<>();
        List<K2530341_Reservation> reservations = libraryService.getUserReservations(userId);
        
        for (K2530341_Reservation reservation : reservations) {
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
}
