package com.k2530341.slms.app;

import com.k2530341.slms.model.user.*;
import com.k2530341.slms.service.K2530341_LibraryService;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

import java.util.List;

/**
 * User management pane for librarians.
 * Author: M.Y.M. SAJIDH (K2530341)
 */
public class K2530341_UserManagementPane extends VBox {
    private final K2530341_LibraryService libraryService;
    private TableView<K2530341_User> userTable;
    
    public K2530341_UserManagementPane(K2530341_LibraryService libraryService) {
        this.libraryService = libraryService;
        setupUI();
        refreshTable();
    }
    
    private void setupUI() {
        setPadding(new Insets(15));
        setSpacing(10);
        
        userTable = new TableView<>();
        setupTable();
        
        HBox buttonBox = new HBox(10);
        Button addBtn = new Button("Add User");
        Button editBtn = new Button("Edit User");
        Button deleteBtn = new Button("Delete User");
        Button refreshBtn = new Button("Refresh");
        
        addBtn.setOnAction(e -> showAddUserDialog());
        editBtn.setOnAction(e -> showEditUserDialog());
        deleteBtn.setOnAction(e -> deleteSelectedUser());
        refreshBtn.setOnAction(e -> refreshTable());
        
        buttonBox.getChildren().addAll(addBtn, editBtn, deleteBtn, refreshBtn);
        
        getChildren().addAll(new Label("User Management"), buttonBox, userTable);
        VBox.setVgrow(userTable, Priority.ALWAYS);
    }
    
    private void setupTable() {
        TableColumn<K2530341_User, String> idCol = new TableColumn<>("User ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("userId"));
        
        TableColumn<K2530341_User, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        
        TableColumn<K2530341_User, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        
        TableColumn<K2530341_User, String> contactCol = new TableColumn<>("Contact");
        contactCol.setCellValueFactory(new PropertyValueFactory<>("contactNumber"));
        
        TableColumn<K2530341_User, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("membershipType"));
        
        TableColumn<K2530341_User, Integer> borrowsCol = new TableColumn<>("Current Borrows");
        borrowsCol.setCellValueFactory(new PropertyValueFactory<>("currentBorrowCount"));
        
        userTable.getColumns().addAll(idCol, nameCol, emailCol, contactCol, typeCol, borrowsCol);
    }
    
    private void refreshTable() {
        List<K2530341_User> users = libraryService.getAllUsers();
        userTable.getItems().clear();
        userTable.setItems(FXCollections.observableArrayList(users));
    }
    
    private void showAddUserDialog() {
        Dialog<K2530341_User> dialog = new Dialog<>();
        dialog.setTitle("Add User");
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        // User ID will be auto-generated after type selection
        TextField idField = new TextField("[Select type first]");
        idField.setEditable(false);
        idField.setStyle("-fx-background-color: #f0f0f0;");
        
        TextField nameField = new TextField();
        TextField emailField = new TextField();
        TextField contactField = new TextField();
        ComboBox<K2530341_MembershipType> typeCombo = new ComboBox<>(
            FXCollections.observableArrayList(K2530341_MembershipType.values()));
        
        // Update ID when type is selected
        typeCombo.setOnAction(e -> {
            if (typeCombo.getValue() != null) {
                idField.setText(libraryService.peekNextUserId(typeCombo.getValue()));
            }
        });
        
        grid.add(new Label("User Type:*"), 0, 0);
        grid.add(typeCombo, 1, 0);
        grid.add(new Label("User ID:"), 0, 1);
        grid.add(idField, 1, 1);
        grid.add(new Label("Name:*"), 0, 2);
        grid.add(nameField, 1, 2);
        grid.add(new Label("Email:*"), 0, 3);
        grid.add(emailField, 1, 3);
        grid.add(new Label("Contact:*"), 0, 4);
        grid.add(contactField, 1, 4);
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                // Validate required fields
                // Validate user input before processing
                if (typeCombo.getValue() == null) {
                    showAlert("Validation Error", "Please select a user type.");
                    return null;
                }
                // Check for empty fields
                if (nameField.getText().trim().isEmpty() || 
                    emailField.getText().trim().isEmpty() || 
                    contactField.getText().trim().isEmpty()) {
                    showAlert("Validation Error", "Please fill in all required fields (marked with *).");
                    return null;
                }
                
                // Create appropriate user subclass based on type
                K2530341_MembershipType type = typeCombo.getValue();
                K2530341_User user;
                
                switch (type) {
                    case STUDENT:
                        user = new K2530341_Student(
                            idField.getText(),
                            nameField.getText().trim(),
                            emailField.getText().trim(),
                            contactField.getText().trim(),
                            0
                        );
                        break;
                    case FACULTY:
                        user = new K2530341_Faculty(
                            idField.getText(),
                            nameField.getText().trim(),
                            emailField.getText().trim(),
                            contactField.getText().trim(),
                            0
                        );
                        break;
                    case GUEST:
                        user = new K2530341_Guest(
                            idField.getText(),
                            nameField.getText().trim(),
                            emailField.getText().trim(),
                            contactField.getText().trim(),
                            0
                        );
                        break;
                    default:
                        return null;
                }
                
                return user;
            }
            return null;
        });
        
        dialog.showAndWait().ifPresent(user -> {
            if (user != null) {
                String result = libraryService.addUser(user);
                if ("SUCCESS".equals(result)) {
                    refreshTable();
                    showAlert("Success", "User added successfully!");
                } else if ("DUPLICATE_ID".equals(result)) {
                    showAlert("Error", "A user with ID '" + user.getUserId() + "' already exists.");
                } else {
                    showAlert("Error", "Failed to add user.");
                }
            }
        });
    }
    
    private void showEditUserDialog() {
        K2530341_User selected = userTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Warning", "Please select a user to edit.");
            return;
        }
        
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setTitle("Edit User");
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        TextField nameField = new TextField(selected.getName());
        TextField emailField = new TextField(selected.getEmail());
        TextField contactField = new TextField(selected.getContactNumber());
        
        grid.add(new Label("Name:*"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Email:*"), 0, 1);
        grid.add(emailField, 1, 1);
        grid.add(new Label("Contact:*"), 0, 2);
        grid.add(contactField, 1, 2);
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        dialog.setResultConverter(button -> button == ButtonType.OK);
        
        dialog.showAndWait().ifPresent(result -> {
            if (result) {
                // Validate required fields
                if (nameField.getText().trim().isEmpty() || 
                    emailField.getText().trim().isEmpty() || 
                    contactField.getText().trim().isEmpty()) {
                    showAlert("Validation Error", "Please fill in all required fields (marked with *).");
                    return;
                }
                
                selected.setName(nameField.getText().trim());
                selected.setEmail(emailField.getText().trim());
                selected.setContactNumber(contactField.getText().trim());
                libraryService.updateUser(selected);
                refreshTable();
                showAlert("Success", "User updated successfully!");
            }
        });
    }
    
    private void deleteSelectedUser() {
        K2530341_User selected = userTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Warning", "Please select a user to delete.");
            return;
        }
        
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete user: " + selected.getName());
        confirm.setContentText("Are you sure?");
        
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                libraryService.deleteUser(selected.getUserId());
                refreshTable();
                showAlert("Success", "User deleted successfully!");
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
