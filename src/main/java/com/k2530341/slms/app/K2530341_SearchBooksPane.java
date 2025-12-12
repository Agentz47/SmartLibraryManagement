package com.k2530341.slms.app;

import com.k2530341.slms.model.book.K2530341_Book;
import com.k2530341.slms.model.book.K2530341_AvailabilityStatus;
import com.k2530341.slms.model.user.K2530341_User;
import com.k2530341.slms.model.reservation.K2530341_ReservationStatus;
import com.k2530341.slms.service.K2530341_LibraryService;
import com.k2530341.slms.patterns.command.*;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

import java.util.List;

/**
 * Search books pane for users.
 * Author: M.Y.M. SAJIDH (K2530341)
 */
public class K2530341_SearchBooksPane extends VBox {
    private final K2530341_LibraryService libraryService;
    private final String userId;
    private final K2530341_CommandManager commandManager;
    private TableView<K2530341_Book> bookTable;
    private TextField searchField;
    private Label fineStatusLabel;
    
    public K2530341_SearchBooksPane(K2530341_LibraryService libraryService, String userId, K2530341_CommandManager commandManager) {
        this.libraryService = libraryService;
        this.userId = userId;
        this.commandManager = commandManager;
        setupUI();
        loadAllBooks();
    }
    
    private void setupUI() {
        setPadding(new Insets(15));
        setSpacing(10);
        
        Label titleLabel = new Label("Search & Borrow Books");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        // Add fine status label
        fineStatusLabel = new Label();
        updateFineStatus();
        
        HBox searchBox = new HBox(10);
        searchField = new TextField();
        searchField.setPromptText("Search by title, author, or category...");
        searchField.setPrefWidth(400);
        Button searchBtn = new Button("Search");
        Button showAllBtn = new Button("Show All");
        
        searchBtn.setOnAction(e -> performSearch());
        showAllBtn.setOnAction(e -> loadAllBooks());
        
        searchBox.getChildren().addAll(searchField, searchBtn, showAllBtn);
        
        bookTable = new TableView<>();
        setupTable();
        
        HBox actionBox = new HBox(10);
        Button borrowBtn = new Button("Borrow Selected");
        Button reserveBtn = new Button("Reserve Selected");
        
        borrowBtn.setOnAction(e -> borrowSelectedBook());
        reserveBtn.setOnAction(e -> reserveSelectedBook());
        
        actionBox.getChildren().addAll(borrowBtn, reserveBtn);
        
        getChildren().addAll(titleLabel, fineStatusLabel, searchBox, bookTable, actionBox);
        VBox.setVgrow(bookTable, Priority.ALWAYS);
    }
    
    private void updateFineStatus() {
        K2530341_User user = libraryService.getUser(userId);
        if (user != null) {
            double unpaidFines = user.getUnpaidFines();
            
            if (unpaidFines >= K2530341_LibraryService.MAX_UNPAID_LIMIT) {
                fineStatusLabel.setText(String.format("⚠️ WARNING: Unpaid Fines: LKR %.2f (LIMIT EXCEEDED - Cannot borrow books!)", unpaidFines));
                fineStatusLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #F44336; -fx-font-weight: bold; -fx-padding: 5px; -fx-background-color: #FFEBEE; -fx-background-radius: 5px;");
            } else if (unpaidFines > 0) {
                fineStatusLabel.setText(String.format("💰 Unpaid Fines: LKR %.2f / LKR %.2f limit", unpaidFines, K2530341_LibraryService.MAX_UNPAID_LIMIT));
                fineStatusLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #FF9800; -fx-font-weight: bold; -fx-padding: 5px; -fx-background-color: #FFF3E0; -fx-background-radius: 5px;");
            } else {
                fineStatusLabel.setText("✅ No Unpaid Fines - Good Standing");
                fineStatusLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #4CAF50; -fx-font-weight: bold; -fx-padding: 5px; -fx-background-color: #E8F5E9; -fx-background-radius: 5px;");
            }
        }
    }
    
    private void setupTable() {
        TableColumn<K2530341_Book, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        titleCol.setPrefWidth(200);
        
        TableColumn<K2530341_Book, String> authorCol = new TableColumn<>("Author");
        authorCol.setCellValueFactory(new PropertyValueFactory<>("author"));
        authorCol.setPrefWidth(150);
        
        TableColumn<K2530341_Book, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));
        
        TableColumn<K2530341_Book, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("availabilityStatus"));
        
        // Decorator Pattern: Show if book is Featured/Recommended
        TableColumn<K2530341_Book, String> decorationsCol = new TableColumn<>("Decorations");
        decorationsCol.setCellValueFactory(cellData -> {
            String tags = cellData.getValue().getOptionalTags();
            StringBuilder decorations = new StringBuilder();
            if (tags != null) {
                if (tags.contains("FEATURED")) decorations.append("⭐ Featured ");
                if (tags.contains("RECOMMENDED")) decorations.append("👍 Recommended");
            }
            return new javafx.beans.property.SimpleStringProperty(
                decorations.length() > 0 ? decorations.toString().trim() : "-"
            );
        });
        decorationsCol.setPrefWidth(150);
        
        // Builder Pattern: Show optional metadata
        TableColumn<K2530341_Book, String> tagsCol = new TableColumn<>("Tags");
        tagsCol.setCellValueFactory(cellData -> {
            String tags = cellData.getValue().getOptionalTags();
            // Remove FEATURED/RECOMMENDED from display (shown in Decorations column)
            if (tags != null) {
                String displayTags = tags.replaceAll("FEATURED,?", "")
                                        .replaceAll("RECOMMENDED,?", "")
                                        .replaceAll("^,+|,+$", "")
                                        .replaceAll(",+", ",")
                                        .trim();
                return new javafx.beans.property.SimpleStringProperty(
                    !displayTags.isEmpty() ? displayTags : "-"
                );
            }
            return new javafx.beans.property.SimpleStringProperty("-");
        });
        tagsCol.setPrefWidth(120);
        
        TableColumn<K2530341_Book, String> editionCol = new TableColumn<>("Edition");
        editionCol.setCellValueFactory(cellData -> {
            String edition = cellData.getValue().getEdition();
            return new javafx.beans.property.SimpleStringProperty(
                edition != null && !edition.isEmpty() ? edition : "-"
            );
        });
        editionCol.setPrefWidth(100);
        
        bookTable.getColumns().addAll(titleCol, authorCol, categoryCol, statusCol, decorationsCol, tagsCol, editionCol);
    }
    
    private void performSearch() {
        String query = searchField.getText().trim();
        if (query.isEmpty()) {
            loadAllBooks();
            return;
        }
        
        List<K2530341_Book> results = libraryService.searchBooks(query);
        bookTable.setItems(FXCollections.observableArrayList(results));
    }
    
    private void loadAllBooks() {
        List<K2530341_Book> books = libraryService.getAllBooks();
        bookTable.setItems(FXCollections.observableArrayList(books));
    }
    
    private void borrowSelectedBook() {
        K2530341_Book selected = bookTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Warning", "Please select a book to borrow.");
            return;
        }
        
        // Check why borrowing might fail and provide specific feedback
        K2530341_User user = libraryService.getUser(userId);
        String errorMessage = null;
        
        if (user.getCurrentBorrowCount() >= user.getMembershipType().getBorrowLimit()) {
            errorMessage = String.format("You have reached your borrow limit of %d books.\n\n" +
                "Please return some books before borrowing more.", user.getMembershipType().getBorrowLimit());
        } else if (user.getUnpaidFines() >= K2530341_LibraryService.MAX_UNPAID_LIMIT) {
            errorMessage = String.format("You have unpaid fines of LKR %.2f which exceeds the limit of LKR %.2f.\n\n" +
                "Please clear your fines at the library counter before borrowing more books.",
                user.getUnpaidFines(), K2530341_LibraryService.MAX_UNPAID_LIMIT);
        } else if (selected.getAvailabilityStatus() == K2530341_AvailabilityStatus.BORROWED) {
            errorMessage = "This book is currently borrowed. You can reserve it instead.";
        } else if (selected.getAvailabilityStatus() == K2530341_AvailabilityStatus.RESERVED) {
            // Check if reserved for this user
            boolean reservedForUser = libraryService.getUserReservations(userId).stream()
                .anyMatch(r -> r.getBookId().equals(selected.getBookId()) 
                        && r.getStatus() == K2530341_ReservationStatus.NOTIFIED);
            if (!reservedForUser) {
                errorMessage = "This book is reserved for another user.";
            }
        }
        
        if (errorMessage != null) {
            showAlert("Cannot Borrow", errorMessage);
            return;
        }
        
        K2530341_Command borrowCommand = new K2530341_BorrowCommand(libraryService, selected.getBookId(), userId);
        boolean success = commandManager.executeCommand(borrowCommand);
        
        if (success) {
            showAlert("Success", "Book borrowed successfully!");
            loadAllBooks();
            updateFineStatus(); // Refresh fine status after borrowing
        } else {
            showAlert("Error", "Cannot borrow this book. Please check with the librarian.");
        }
    }
    
    private void reserveSelectedBook() {
        K2530341_Book selected = bookTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Warning", "Please select a book to reserve.");
            return;
        }
        
        // Direct check for availability - if available, guide user to borrow instead
        if (selected.getAvailabilityStatus() == K2530341_AvailabilityStatus.AVAILABLE) {
            showAlert("Book Available", "This book is currently available to borrow now. Please use the 'Borrow Selected' button instead of reserving it.");
            return;
        }
        
        K2530341_Command reserveCommand = new K2530341_ReserveCommand(libraryService, selected.getBookId(), userId);
        String result = ((K2530341_ReserveCommand)reserveCommand).executeWithResult();
        
        if (result != null && result.startsWith("RES-")) {
            showAlert("Success", "Book reserved successfully! You will be notified when it becomes available.");
            loadAllBooks();
        } else if ("AVAILABLE".equals(result)) {
            showAlert("Book Available", "This book is currently available to borrow now. Please use the 'Borrow Selected' button instead of reserving it.");
        } else if ("ALREADY_BORROWED".equals(result)) {
            showAlert("Already Borrowed", "You currently have this book borrowed. You cannot reserve a book you already have.");
        } else {
            showAlert("Error", "Cannot reserve this book.");
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
