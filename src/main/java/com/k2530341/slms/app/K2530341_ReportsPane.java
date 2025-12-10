package com.k2530341.slms.app;

import com.k2530341.slms.model.book.K2530341_Book;
import com.k2530341.slms.model.user.K2530341_User;
import com.k2530341.slms.service.K2530341_LibraryService;
import com.k2530341.slms.service.K2530341_ReportService;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Reports pane for librarians.
 * Author: M.Y.M. SAJIDH (K2530341)
 */
public class K2530341_ReportsPane extends VBox {
    private final K2530341_LibraryService libraryService;
    private final K2530341_ReportService reportService;
    
    public K2530341_ReportsPane(K2530341_LibraryService libraryService, K2530341_ReportService reportService) {
        this.libraryService = libraryService;
        this.reportService = reportService;
        setupUI();
    }
    
    private void setupUI() {
        setPadding(new Insets(15));
        setSpacing(15);
        
        Label titleLabel = new Label("Library Reports");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        // Most Borrowed Books
        VBox borrowedBox = new VBox(10);
        Label borrowedLabel = new Label("Most Borrowed Books");
        borrowedLabel.setStyle("-fx-font-weight: bold;");
        
        HBox borrowedControls = new HBox(10);
        TextField borrowedCountField = new TextField("10");
        borrowedCountField.setPrefWidth(60);
        Button showBorrowedBtn = new Button("Show");
        Button exportBorrowedBtn = new Button("Export CSV");
        
        TableView<K2530341_Book> borrowedTable = new TableView<>();
        setupBorrowedBooksTable(borrowedTable);
        borrowedTable.setPrefHeight(200);
        
        showBorrowedBtn.setOnAction(e -> {
            try {
                int count = Integer.parseInt(borrowedCountField.getText());
                List<K2530341_Book> books = reportService.getMostBorrowedBooks(count);
                borrowedTable.setItems(FXCollections.observableArrayList(books));
            } catch (NumberFormatException ex) {
                showAlert("Error", "Invalid number");
            }
        });
        
        exportBorrowedBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Report");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
            File file = fileChooser.showSaveDialog(getScene().getWindow());
            if (file != null) {
                try {
                    int count = Integer.parseInt(borrowedCountField.getText());
                    reportService.exportMostBorrowedBooks(file.getAbsolutePath(), count);
                    showAlert("Success", "Report exported successfully!");
                } catch (Exception ex) {
                    showAlert("Error", "Export failed: " + ex.getMessage());
                }
            }
        });
        
        borrowedControls.getChildren().addAll(
            new Label("Top:"), borrowedCountField, showBorrowedBtn, exportBorrowedBtn);
        borrowedBox.getChildren().addAll(borrowedLabel, borrowedControls, borrowedTable);
        
        // Active Borrowers
        VBox activeBox = new VBox(10);
        Label activeLabel = new Label("Most Active Borrowers");
        activeLabel.setStyle("-fx-font-weight: bold;");
        
        HBox activeControls = new HBox(10);
        TextField activeCountField = new TextField("10");
        activeCountField.setPrefWidth(60);
        Button showActiveBtn = new Button("Show");
        Button exportActiveBtn = new Button("Export CSV");
        
        TableView<K2530341_User> activeTable = new TableView<>();
        setupActiveBorrowersTable(activeTable);
        activeTable.setPrefHeight(200);
        
        showActiveBtn.setOnAction(e -> {
            try {
                int count = Integer.parseInt(activeCountField.getText());
                List<K2530341_User> users = reportService.getActiveBorrowers(count);
                activeTable.setItems(FXCollections.observableArrayList(users));
            } catch (NumberFormatException ex) {
                showAlert("Error", "Invalid number");
            }
        });
        
        exportActiveBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Report");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
            File file = fileChooser.showSaveDialog(getScene().getWindow());
            if (file != null) {
                try {
                    int count = Integer.parseInt(activeCountField.getText());
                    reportService.exportActiveBorrowers(file.getAbsolutePath(), count);
                    showAlert("Success", "Report exported successfully!");
                } catch (Exception ex) {
                    showAlert("Error", "Export failed: " + ex.getMessage());
                }
            }
        });
        
        activeControls.getChildren().addAll(
            new Label("Top:"), activeCountField, showActiveBtn, exportActiveBtn);
        activeBox.getChildren().addAll(activeLabel, activeControls, activeTable);
        
        getChildren().addAll(titleLabel, new Separator(), borrowedBox, new Separator(), activeBox);
    }
    
    private void setupBorrowedBooksTable(TableView<K2530341_Book> table) {
        TableColumn<K2530341_Book, String> idCol = new TableColumn<>("Book ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("bookId"));
        
        TableColumn<K2530341_Book, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        
        TableColumn<K2530341_Book, String> authorCol = new TableColumn<>("Author");
        authorCol.setCellValueFactory(new PropertyValueFactory<>("author"));
        
        TableColumn<K2530341_Book, Integer> countCol = new TableColumn<>("Borrow Count");
        countCol.setCellValueFactory(new PropertyValueFactory<>("borrowHistoryCount"));
        
        table.getColumns().addAll(idCol, titleCol, authorCol, countCol);
    }
    
    private void setupActiveBorrowersTable(TableView<K2530341_User> table) {
        TableColumn<K2530341_User, String> idCol = new TableColumn<>("User ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("userId"));
        
        TableColumn<K2530341_User, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        
        TableColumn<K2530341_User, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("membershipType"));
        
        TableColumn<K2530341_User, Integer> countCol = new TableColumn<>("Current Borrows");
        countCol.setCellValueFactory(new PropertyValueFactory<>("currentBorrowCount"));
        
        table.getColumns().addAll(idCol, nameCol, typeCol, countCol);
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
