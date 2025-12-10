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
        
        getChildren().addAll(titleLabel, searchBox, bookTable, actionBox);
        VBox.setVgrow(bookTable, Priority.ALWAYS);
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
        
        bookTable.getColumns().addAll(titleCol, authorCol, categoryCol, statusCol);
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
            errorMessage = "You have reached your borrow limit (" + user.getMembershipType().getBorrowLimit() + " books).";
        } else if (user.getUnpaidFines() >= K2530341_LibraryService.MAX_UNPAID_LIMIT) {
            errorMessage = "You have unpaid fines of LKR " + user.getUnpaidFines() + ". Maximum limit is LKR " + K2530341_LibraryService.MAX_UNPAID_LIMIT + ".";
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
        
        K2530341_Command reserveCommand = new K2530341_ReserveCommand(libraryService, selected.getBookId(), userId);
        boolean success = commandManager.executeCommand(reserveCommand);
        
        if (success) {
            showAlert("Success", "Book reserved successfully!");
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
