package com.k2530341.slms.app;

import com.k2530341.slms.service.K2530341_LibraryService;
import com.k2530341.slms.service.K2530341_ReportService;
import com.k2530341.slms.patterns.command.K2530341_CommandManager;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

/**
 * Main JavaFX application for Smart Library Management System.
 * Author: M.Y.M. SAJIDH (K2530341)
 * Student ID: K2530341
 */
public class K2530341_SLMSApplication extends Application {
    private K2530341_LibraryService libraryService;
    private K2530341_ReportService reportService;
    private K2530341_CommandManager commandManager;
    private Stage primaryStage;
    private String currentUserId;
    private String currentRole;
    
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Smart Library Management System - K2530341");
        
        // Initialize services
        libraryService = new K2530341_LibraryService();
        libraryService.initialize();
        reportService = new K2530341_ReportService(libraryService);
        commandManager = new K2530341_CommandManager();
        
        // Show role selection screen
        showRoleSelection();
        
        // Save data on exit
        primaryStage.setOnCloseRequest(event -> {
            libraryService.saveAllData();
        });
    }
    
    /**
     * Show role selection screen.
     */
    private void showRoleSelection() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(40));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #f5f5f5;");
        
        Label titleLabel = new Label("Smart Library Management System");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        
        Label studentLabel = new Label("Student: M.Y.M. SAJIDH");
        studentLabel.setStyle("-fx-font-size: 14px;");
        
        Label idLabel = new Label("Student ID: K2530341");
        idLabel.setStyle("-fx-font-size: 14px;");
        
        Label selectLabel = new Label("Select Role:");
        selectLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        Button librarianBtn = new Button("Librarian");
        librarianBtn.setPrefSize(200, 50);
        librarianBtn.setStyle("-fx-font-size: 14px;");
        librarianBtn.setOnAction(e -> {
            currentRole = "LIBRARIAN";
            showLibrarianDashboard();
        });
        
        Button userBtn = new Button("User (Student/Faculty/Guest)");
        userBtn.setPrefSize(200, 50);
        userBtn.setStyle("-fx-font-size: 14px;");
        userBtn.setOnAction(e -> showUserSelection());
        
        root.getChildren().addAll(titleLabel, studentLabel, idLabel, 
                                  new Separator(), selectLabel, librarianBtn, userBtn);
        
        Scene scene = new Scene(root, 600, 500);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    /**
     * Show user selection for user role.
     */
    private void showUserSelection() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.CENTER);
        
        Label label = new Label("Enter Your User ID:");
        label.setStyle("-fx-font-size: 16px;");
        
        TextField userIdField = new TextField();
        userIdField.setPromptText("User ID (e.g., U0001)");
        userIdField.setMaxWidth(300);
        
        Button loginBtn = new Button("Login");
        loginBtn.setOnAction(e -> {
            String userId = userIdField.getText().trim();
            if (!userId.isEmpty() && libraryService.getUser(userId) != null) {
                currentUserId = userId;
                currentRole = "USER";
                showUserDashboard();
            } else {
                showAlert("Error", "User not found. Please enter a valid User ID.");
            }
        });
        
        Button backBtn = new Button("Back");
        backBtn.setOnAction(e -> showRoleSelection());
        
        root.getChildren().addAll(label, userIdField, loginBtn, backBtn);
        
        Scene scene = new Scene(root, 500, 300);
        primaryStage.setScene(scene);
    }
    
    /**
     * Show librarian dashboard with tabs.
     */
    private void showLibrarianDashboard() {
        TabPane tabPane = new TabPane();
        
        Tab booksTab = new Tab("Manage Books");
        booksTab.setClosable(false);
        booksTab.setContent(new K2530341_BookManagementPane(libraryService));
        
        Tab usersTab = new Tab("Manage Users");
        usersTab.setClosable(false);
        usersTab.setContent(new K2530341_UserManagementPane(libraryService));
        
        Tab reportsTab = new Tab("Reports");
        reportsTab.setClosable(false);
        reportsTab.setContent(new K2530341_ReportsPane(libraryService, reportService));
        
        Tab overdueTab = new Tab("Overdue Books");
        overdueTab.setClosable(false);
        overdueTab.setContent(new K2530341_OverduePane(libraryService));
        
        tabPane.getTabs().addAll(booksTab, usersTab, reportsTab, overdueTab);
        
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));
        
        Label headerLabel = new Label("Librarian Dashboard - K2530341 SLMS");
        headerLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        Button logoutBtn = new Button("Logout");
        logoutBtn.setOnAction(e -> {
            libraryService.saveAllData();
            showRoleSelection();
        });
        
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().addAll(headerLabel, spacer, logoutBtn);
        
        root.getChildren().addAll(header, tabPane);
        VBox.setVgrow(tabPane, Priority.ALWAYS);
        
        Scene scene = new Scene(root, 1000, 700);
        primaryStage.setScene(scene);
    }
    
    /**
     * Show user dashboard with tabs.
     */
    private void showUserDashboard() {
        TabPane tabPane = new TabPane();
        
        Tab searchTab = new Tab("Search Books");
        searchTab.setClosable(false);
        searchTab.setContent(new K2530341_SearchBooksPane(libraryService, currentUserId, commandManager));
        
        Tab borrowTab = new Tab("My Borrows");
        borrowTab.setClosable(false);
        borrowTab.setContent(new K2530341_MyBorrowsPane(libraryService, currentUserId, commandManager));
        
        Tab reserveTab = new Tab("My Reservations");
        reserveTab.setClosable(false);
        reserveTab.setContent(new K2530341_MyReservationsPane(libraryService, currentUserId));
        
        Tab notificationsTab = new Tab("Notifications");
        notificationsTab.setClosable(false);
        notificationsTab.setContent(new K2530341_NotificationsPane(libraryService, currentUserId));
        
        tabPane.getTabs().addAll(searchTab, borrowTab, reserveTab, notificationsTab);
        
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));
        
        Label headerLabel = new Label("User Dashboard - " + libraryService.getUser(currentUserId).getName());
        headerLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        Button logoutBtn = new Button("Logout");
        logoutBtn.setOnAction(e -> {
            libraryService.saveAllData();
            showRoleSelection();
        });
        
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().addAll(headerLabel, spacer, logoutBtn);
        
        root.getChildren().addAll(header, tabPane);
        VBox.setVgrow(tabPane, Priority.ALWAYS);
        
        Scene scene = new Scene(root, 1000, 700);
        primaryStage.setScene(scene);
    }
    
    /**
     * Show alert dialog.
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
