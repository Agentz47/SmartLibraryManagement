package com.k2530341.slms.app;

import com.k2530341.slms.model.book.*;
import com.k2530341.slms.model.user.K2530341_User;
import com.k2530341.slms.model.reservation.K2530341_ReservationStatus;
import com.k2530341.slms.patterns.builder.K2530341_BookBuilder;
import com.k2530341.slms.patterns.decorator.*;
import com.k2530341.slms.service.K2530341_LibraryService;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

import java.util.List;

/**
 * Book management pane for librarians.
 * Author: M.Y.M. SAJIDH (K2530341)
 */
public class K2530341_BookManagementPane extends VBox {
    private final K2530341_LibraryService libraryService;
    private TableView<K2530341_Book> bookTable;
    
    public K2530341_BookManagementPane(K2530341_LibraryService libraryService) {
        this.libraryService = libraryService;
        setupUI();
        refreshTable();
    }
    
    private void setupUI() {
        setPadding(new Insets(15));
        setSpacing(10);
        
        // Table
        bookTable = new TableView<>();
        setupTable();
        
        // Buttons
        HBox buttonBox = new HBox(10);
        Button addBtn = new Button("Add Book");
        Button editBtn = new Button("Edit Book");
        Button deleteBtn = new Button("Delete Book");
        Button refreshBtn = new Button("Refresh");
        
        addBtn.setOnAction(e -> showAddBookDialog());
        editBtn.setOnAction(e -> showEditBookDialog());
        deleteBtn.setOnAction(e -> deleteSelectedBook());
        refreshBtn.setOnAction(e -> refreshTable());
        
        buttonBox.getChildren().addAll(addBtn, editBtn, deleteBtn, refreshBtn);
        
        // Decorator Pattern buttons
        HBox decoratorBox = new HBox(10);
        decoratorBox.setStyle("-fx-padding: 5px; -fx-background-color: #E3F2FD; -fx-background-radius: 5px;");
        Label decorLabel = new Label("Decorator Pattern:");
        decorLabel.setStyle("-fx-font-weight: bold;");
        Button featuredBtn = new Button("⭐ Mark as Featured");
        featuredBtn.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white;");
        Button recommendedBtn = new Button("👍 Mark as Recommended");
        recommendedBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        Button viewDecorBtn = new Button("View Decorations");
        
        featuredBtn.setOnAction(e -> markBookAsFeatured());
        recommendedBtn.setOnAction(e -> markBookAsRecommended());
        viewDecorBtn.setOnAction(e -> viewBookDecorations());
        
        decoratorBox.getChildren().addAll(decorLabel, featuredBtn, recommendedBtn, viewDecorBtn);
        
        getChildren().addAll(new Label("Book Management"), buttonBox, decoratorBox, bookTable);
        VBox.setVgrow(bookTable, Priority.ALWAYS);
    }
    
    private void setupTable() {
        TableColumn<K2530341_Book, String> idCol = new TableColumn<>("Book ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("bookId"));
        idCol.setPrefWidth(80);
        
        TableColumn<K2530341_Book, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        titleCol.setPrefWidth(150);
        
        TableColumn<K2530341_Book, String> authorCol = new TableColumn<>("Author");
        authorCol.setCellValueFactory(new PropertyValueFactory<>("author"));
        authorCol.setPrefWidth(120);
        
        TableColumn<K2530341_Book, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));
        categoryCol.setPrefWidth(100);
        
        TableColumn<K2530341_Book, String> isbnCol = new TableColumn<>("ISBN");
        isbnCol.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        isbnCol.setPrefWidth(100);
        
        TableColumn<K2530341_Book, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("availabilityStatus"));
        statusCol.setPrefWidth(90);
        
        // NEW: Current Borrower/Reserver column
        TableColumn<K2530341_Book, String> currentUserCol = new TableColumn<>("Current Borrower/Reserver");
        currentUserCol.setCellValueFactory(cellData -> {
            K2530341_Book book = cellData.getValue();
            String displayText = getCurrentBookUser(book);
            return new javafx.beans.property.SimpleStringProperty(displayText);
        });
        currentUserCol.setPrefWidth(180);
        
        TableColumn<K2530341_Book, Integer> borrowCountCol = new TableColumn<>("Borrow Count");
        borrowCountCol.setCellValueFactory(new PropertyValueFactory<>("borrowHistoryCount"));
        borrowCountCol.setPrefWidth(100);
        
        // Builder Pattern: Optional metadata columns
        TableColumn<K2530341_Book, String> tagsCol = new TableColumn<>("Tags (Builder)");
        tagsCol.setCellValueFactory(cellData -> {
            String tags = cellData.getValue().getOptionalTags();
            return new javafx.beans.property.SimpleStringProperty(tags != null && !tags.isEmpty() ? tags : "-");
        });
        tagsCol.setPrefWidth(150);
        
        TableColumn<K2530341_Book, String> editionCol = new TableColumn<>("Edition (Builder)");
        editionCol.setCellValueFactory(cellData -> {
            String edition = cellData.getValue().getEdition();
            return new javafx.beans.property.SimpleStringProperty(edition != null && !edition.isEmpty() ? edition : "-");
        });
        editionCol.setPrefWidth(120);
        
        bookTable.getColumns().addAll(idCol, titleCol, authorCol, categoryCol, isbnCol, statusCol, currentUserCol, borrowCountCol, tagsCol, editionCol);
    }
    
    private String getCurrentBookUser(K2530341_Book book) {
        if (book.getAvailabilityStatus() == K2530341_AvailabilityStatus.BORROWED) {
            // Find who borrowed this book
            return libraryService.getAllBorrows().stream()
                .filter(b -> b.getBookId().equals(book.getBookId()) && b.getReturnDate() == null)
                .findFirst()
                .map(b -> {
                    K2530341_User user = libraryService.getUser(b.getUserId());
                    return user != null ? user.getName() + " (Borrowed)" : "Unknown";
                })
                .orElse("-");
        } else if (book.getAvailabilityStatus() == K2530341_AvailabilityStatus.RESERVED) {
            // Find who has notified reservation for this book
            return libraryService.getAllReservations().stream()
                .filter(r -> r.getBookId().equals(book.getBookId()) 
                        && r.getStatus() == K2530341_ReservationStatus.NOTIFIED)
                .findFirst()
                .map(r -> {
                    K2530341_User user = libraryService.getUser(r.getUserId());
                    return user != null ? user.getName() + " (Reserved)" : "Unknown";
                })
                .orElse("-");
        }
        return "-";
    }
    
    private void refreshTable() {
        List<K2530341_Book> books = libraryService.getAllBooks();
        bookTable.getItems().clear();
        bookTable.setItems(FXCollections.observableArrayList(books));
    }
    
    private void showAddBookDialog() {
        Dialog<K2530341_Book> dialog = new Dialog<>();
        dialog.setTitle("Add Book (Builder Pattern)");
        dialog.setHeaderText("Create a book with optional metadata using Builder Pattern");
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        // Auto-generate ID and make it read-only - use peek to avoid incrementing
        TextField idField = new TextField(libraryService.peekNextBookId());
        idField.setEditable(false);
        idField.setStyle("-fx-background-color: #f0f0f0;");
        
        TextField titleField = new TextField();
        TextField authorField = new TextField();
        TextField categoryField = new TextField();
        TextField isbnField = new TextField();
        TextField tagsField = new TextField();
        tagsField.setPromptText("e.g., Programming, Object-Oriented, Design Patterns");
        TextField editionField = new TextField();
        editionField.setPromptText("e.g., 1st Edition, Revised 2024");
        
        // Highlight optional fields for Builder Pattern
        Label tagsLabel = new Label("Tags (Optional - Builder Pattern):");
        tagsLabel.setStyle("-fx-text-fill: #2196F3; -fx-font-style: italic;");
        Label editionLabel = new Label("Edition (Optional - Builder Pattern):");
        editionLabel.setStyle("-fx-text-fill: #2196F3; -fx-font-style: italic;");
        
        grid.add(new Label("Book ID:"), 0, 0);
        grid.add(idField, 1, 0);
        grid.add(new Label("Title:*"), 0, 1);
        grid.add(titleField, 1, 1);
        grid.add(new Label("Author:*"), 0, 2);
        grid.add(authorField, 1, 2);
        grid.add(new Label("Category:*"), 0, 3);
        grid.add(categoryField, 1, 3);
        grid.add(new Label("ISBN:*"), 0, 4);
        grid.add(isbnField, 1, 4);
        grid.add(tagsLabel, 0, 5);
        grid.add(tagsField, 1, 5);
        grid.add(editionLabel, 0, 6);
        grid.add(editionField, 1, 6);
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                // Validate required fields
                if (titleField.getText().trim().isEmpty() || 
                    authorField.getText().trim().isEmpty() || 
                    categoryField.getText().trim().isEmpty() || 
                    isbnField.getText().trim().isEmpty()) {
                    showAlert("Validation Error", "Please fill in all required fields (marked with *).");
                    return null;
                }
                
                try {
                    return new K2530341_BookBuilder()
                        .setBookId(idField.getText())
                        .setTitle(titleField.getText().trim())
                        .setAuthor(authorField.getText().trim())
                        .setCategory(categoryField.getText().trim())
                        .setIsbn(isbnField.getText().trim())
                        .setOptionalTags(tagsField.getText().trim())
                        .setEdition(editionField.getText().trim())
                        .build();
                } catch (Exception e) {
                    showAlert("Error", "Invalid input: " + e.getMessage());
                    return null;
                }
            }
            return null;
        });
        
        dialog.showAndWait().ifPresent(book -> {
            if (book != null) {
                String result = libraryService.addBook(book);
                if ("SUCCESS".equals(result)) {
                    refreshTable();
                    showAlert("Success", "Book added successfully!");
                } else if ("DUPLICATE_ID".equals(result)) {
                    showAlert("Error", "A book with ID '" + book.getBookId() + "' already exists.");
                } else {
                    showAlert("Error", "Failed to add book.");
                }
            }
        });
    }
    
    private void showEditBookDialog() {
        K2530341_Book selected = bookTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Warning", "Please select a book to edit.");
            return;
        }
        
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setTitle("Edit Book");
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        TextField titleField = new TextField(selected.getTitle());
        TextField authorField = new TextField(selected.getAuthor());
        TextField categoryField = new TextField(selected.getCategory());
        TextField isbnField = new TextField(selected.getIsbn());
        
        grid.add(new Label("Title:*"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Author:*"), 0, 1);
        grid.add(authorField, 1, 1);
        grid.add(new Label("Category:*"), 0, 2);
        grid.add(categoryField, 1, 2);
        grid.add(new Label("ISBN:*"), 0, 3);
        grid.add(isbnField, 1, 3);
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        dialog.setResultConverter(button -> button == ButtonType.OK);
        
        dialog.showAndWait().ifPresent(result -> {
            if (result) {
                // Validate required fields
                if (titleField.getText().trim().isEmpty() || 
                    authorField.getText().trim().isEmpty() || 
                    categoryField.getText().trim().isEmpty() || 
                    isbnField.getText().trim().isEmpty()) {
                    showAlert("Validation Error", "Please fill in all required fields (marked with *).");
                    return;
                }
                
                selected.setTitle(titleField.getText().trim());
                selected.setAuthor(authorField.getText().trim());
                selected.setCategory(categoryField.getText().trim());
                selected.setIsbn(isbnField.getText().trim());
                libraryService.updateBook(selected);
                refreshTable();
                showAlert("Success", "Book updated successfully!");
            }
        });
    }
    
    private void deleteSelectedBook() {
        K2530341_Book selected = bookTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Warning", "Please select a book to delete.");
            return;
        }
        
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete book: " + selected.getTitle());
        confirm.setContentText("Are you sure?");
        
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                libraryService.deleteBook(selected.getBookId());
                refreshTable();
                showAlert("Success", "Book deleted successfully!");
            }
        });
    }
    
    private void markBookAsFeatured() {
        K2530341_Book selected = bookTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Warning", "Please select a book to mark as Featured.");
            return;
        }
        
        // Add "FEATURED" to tags using Decorator Pattern
        String currentTags = selected.getOptionalTags();
        if (currentTags.contains("FEATURED")) {
            showAlert("Already Featured", "This book is already marked as Featured!");
            return;
        }
        
        String newTags = currentTags.isEmpty() ? "FEATURED" : currentTags + ", FEATURED";
        selected.setOptionalTags(newTags);
        libraryService.updateBook(selected);
        refreshTable();
        
        // Show decorator pattern demonstration
        K2530341_BookComponent component = new K2530341_ConcreteBook(selected);
        K2530341_BookComponent decorated = new K2530341_FeaturedDecorator(component);
        
        showAlert("Success - Decorator Pattern Applied", 
            "Book marked as Featured!\n\n" +
            "Decorator Pattern Demonstration:\n" +
            "Original: " + component.getDescription() + "\n" +
            "Decorated: " + decorated.getDescription() + "\n" +
            "Priority: " + component.getPriority() + " → " + decorated.getPriority());
    }
    
    private void markBookAsRecommended() {
        K2530341_Book selected = bookTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Warning", "Please select a book to mark as Recommended.");
            return;
        }
        
        // Add "RECOMMENDED" to tags using Decorator Pattern
        String currentTags = selected.getOptionalTags();
        if (currentTags.contains("RECOMMENDED")) {
            showAlert("Already Recommended", "This book is already marked as Recommended!");
            return;
        }
        
        String newTags = currentTags.isEmpty() ? "RECOMMENDED" : currentTags + ", RECOMMENDED";
        selected.setOptionalTags(newTags);
        libraryService.updateBook(selected);
        refreshTable();
        
        // Show decorator pattern demonstration
        K2530341_BookComponent component = new K2530341_ConcreteBook(selected);
        K2530341_BookComponent decorated = new K2530341_RecommendedDecorator(component);
        
        showAlert("Success - Decorator Pattern Applied", 
            "Book marked as Recommended!\n\n" +
            "Decorator Pattern Demonstration:\n" +
            "Original: " + component.getDescription() + "\n" +
            "Decorated: " + decorated.getDescription() + "\n" +
            "Priority: " + component.getPriority() + " → " + decorated.getPriority());
    }
    
    private void viewBookDecorations() {
        K2530341_Book selected = bookTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Warning", "Please select a book to view decorations.");
            return;
        }
        
        // Create component and apply all decorations found in tags
        K2530341_BookComponent component = new K2530341_ConcreteBook(selected);
        String tags = selected.getOptionalTags();
        
        K2530341_BookComponent decorated = component;
        boolean hasFeatured = tags.contains("FEATURED");
        boolean hasRecommended = tags.contains("RECOMMENDED");
        
        if (hasFeatured) {
            decorated = new K2530341_FeaturedDecorator(decorated);
        }
        if (hasRecommended) {
            decorated = new K2530341_RecommendedDecorator(decorated);
        }
        
        String message = "Book: " + selected.getTitle() + "\n\n";
        message += "Current Decorations:\n";
        if (hasFeatured) message += "⭐ Featured (+10 priority)\n";
        if (hasRecommended) message += "👍 Recommended (+5 priority)\n";
        if (!hasFeatured && !hasRecommended) message += "No decorations applied\n";
        
        message += "\nDecorator Pattern Chain:\n";
        message += "Base: " + component.getDescription() + " (Priority: " + component.getPriority() + ")\n";
        if (hasFeatured || hasRecommended) {
            message += "Final: " + decorated.getDescription() + " (Priority: " + decorated.getPriority() + ")\n";
        }
        
        message += "\nOptional Metadata (Builder Pattern):\n";
        message += "Tags: " + (selected.getOptionalTags().isEmpty() ? "None" : selected.getOptionalTags()) + "\n";
        message += "Edition: " + (selected.getEdition().isEmpty() ? "None" : selected.getEdition());
        
        showAlert("Book Decorations & Metadata", message);
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
