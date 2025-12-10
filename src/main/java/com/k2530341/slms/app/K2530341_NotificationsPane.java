package com.k2530341.slms.app;

import com.k2530341.slms.model.notification.K2530341_Notification;
import com.k2530341.slms.service.K2530341_LibraryService;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.*;

/**
 * Notifications pane for users.
 * Author: M.Y.M. SAJIDH (K2530341)
 */
public class K2530341_NotificationsPane extends VBox {
    private final K2530341_LibraryService libraryService;
    private final String userId;
    private ListView<String> notificationsList;
    
    public K2530341_NotificationsPane(K2530341_LibraryService libraryService, String userId) {
        this.libraryService = libraryService;
        this.userId = userId;
        setupUI();
        refreshList();
    }
    
    private void setupUI() {
        setPadding(new Insets(15));
        setSpacing(10);
        
        Label titleLabel = new Label("My Notifications");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        notificationsList = new ListView<>();
        notificationsList.setPrefHeight(500);
        
        Button refreshBtn = new Button("Refresh");
        refreshBtn.setOnAction(e -> refreshList());
        
        getChildren().addAll(titleLabel, refreshBtn, notificationsList);
        VBox.setVgrow(notificationsList, Priority.ALWAYS);
    }
    
    private void refreshList() {
        List<String> items = new ArrayList<>();
        List<K2530341_Notification> notifications = libraryService.getUserNotifications(userId);
        
        for (K2530341_Notification notification : notifications) {
            String item = String.format("[%s] %s - %s",
                notification.getType(),
                notification.getDate(),
                notification.getMessage());
            items.add(item);
        }
        
        if (items.isEmpty()) {
            items.add("No notifications");
        }
        
        notificationsList.setItems(FXCollections.observableArrayList(items));
    }
}
