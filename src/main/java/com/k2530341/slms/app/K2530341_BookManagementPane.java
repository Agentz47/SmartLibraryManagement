package com.k2530341.slms.app;

import com.k2530341.slms.model.book.*;
import com.k2530341.slms.model.user.K2530341_User;
import com.k2530341.slms.model.reservation.K2530341_ReservationStatus;
import com.k2530341.slms.patterns.builder.K2530341_BookBuilder;
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
        
        getChildren().addAll(new Label("Book Management"), buttonBox, bookTable);
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
        
        bookTable.getColumns().addAll(idCol, titleCol, authorCol, categoryCol, isbnCol, statusCol, currentUserCol, borrowCountCol);
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
        bookTable.setItems(FXCollections.observableArrayList(books));
    }
    
    private void showAddBookDialog() {
        Dialog<K2530341_Book> dialog = new Dialog<>();
        dialog.setTitle("Add Book");
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        TextField idField = new TextField();
        TextField titleField = new TextField();
        TextField authorField = new TextField();
        TextField categoryField = new TextField();
        TextField isbnField = new TextField();
        TextField tagsField = new TextField();
        TextField editionField = new TextField();
        
        grid.add(new Label("Book ID:"), 0, 0);
        grid.add(idField, 1, 0);
        grid.add(new Label("Title:"), 0, 1);
        grid.add(titleField, 1, 1);
        grid.add(new Label("Author:"), 0, 2);
        grid.add(authorField, 1, 2);
        grid.add(new Label("Category:"), 0, 3);
        grid.add(categoryField, 1, 3);
        grid.add(new Label("ISBN:"), 0, 4);
        grid.add(isbnField, 1, 4);
        grid.add(new Label("Tags:"), 0, 5);
        grid.add(tagsField, 1, 5);
        grid.add(new Label("Edition:"), 0, 6);
        grid.add(editionField, 1, 6);
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                try {
                    return new K2530341_BookBuilder()
                        .setBookId(idField.getText())
                        .setTitle(titleField.getText())
                        .setAuthor(authorField.getText())
                        .setCategory(categoryField.getText())
                        .setIsbn(isbnField.getText())
                        .setOptionalTags(tagsField.getText())
                        .setEdition(editionField.getText())
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
                libraryService.addBook(book);
                refreshTable();
                showAlert("Success", "Book added successfully!");
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
        
        grid.add(new Label("Title:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Author:"), 0, 1);
        grid.add(authorField, 1, 1);
        grid.add(new Label("Category:"), 0, 2);
        grid.add(categoryField, 1, 2);
        grid.add(new Label("ISBN:"), 0, 3);
        grid.add(isbnField, 1, 3);
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        dialog.setResultConverter(button -> button == ButtonType.OK);
        
        dialog.showAndWait().ifPresent(result -> {
            if (result) {
                selected.setTitle(titleField.getText());
                selected.setAuthor(authorField.getText());
                selected.setCategory(categoryField.getText());
                selected.setIsbn(isbnField.getText());
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
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
