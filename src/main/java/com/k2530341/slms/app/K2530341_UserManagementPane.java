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
        userTable.setItems(FXCollections.observableArrayList(users));
    }
    
    private void showAddUserDialog() {
        Dialog<K2530341_User> dialog = new Dialog<>();
        dialog.setTitle("Add User");
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        TextField idField = new TextField();
        TextField nameField = new TextField();
        TextField emailField = new TextField();
        TextField contactField = new TextField();
        ComboBox<K2530341_MembershipType> typeCombo = new ComboBox<>(
            FXCollections.observableArrayList(K2530341_MembershipType.values()));
        
        grid.add(new Label("User ID:"), 0, 0);
        grid.add(idField, 1, 0);
        grid.add(new Label("Name:"), 0, 1);
        grid.add(nameField, 1, 1);
        grid.add(new Label("Email:"), 0, 2);
        grid.add(emailField, 1, 2);
        grid.add(new Label("Contact:"), 0, 3);
        grid.add(contactField, 1, 3);
        grid.add(new Label("Type:"), 0, 4);
        grid.add(typeCombo, 1, 4);
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK && typeCombo.getValue() != null) {
                return new K2530341_User(
                    idField.getText(),
                    nameField.getText(),
                    emailField.getText(),
                    contactField.getText(),
                    typeCombo.getValue(),
                    0
                );
            }
            return null;
        });
        
        dialog.showAndWait().ifPresent(user -> {
            if (user != null) {
                libraryService.addUser(user);
                refreshTable();
                showAlert("Success", "User added successfully!");
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
        
        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Email:"), 0, 1);
        grid.add(emailField, 1, 1);
        grid.add(new Label("Contact:"), 0, 2);
        grid.add(contactField, 1, 2);
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        dialog.setResultConverter(button -> button == ButtonType.OK);
        
        dialog.showAndWait().ifPresent(result -> {
            if (result) {
                selected.setName(nameField.getText());
                selected.setEmail(emailField.getText());
                selected.setContactNumber(contactField.getText());
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
