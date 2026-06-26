package com.shop.controller;

import com.shop.dao.RoleDAO;
import com.shop.model.Role;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class RolesController {

    @FXML private TableView<Role> table;
    @FXML private TableColumn<Role, Number> colId;
    @FXML private TableColumn<Role, String> colName;
    @FXML private TableColumn<Role, Number> colLevel;

    @FXML private TextField nameField;
    @FXML private TextField levelField;
    @FXML private Label errorLabel;

    private final RoleDAO roleDAO = new RoleDAO();
    private final ObservableList<Role> data = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        colId.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getIdRole()));
        colName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));
        colLevel.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getAccessLevel()));
        table.setItems(data);
        table.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> fillForm(sel));

        refresh();
    }

    private void fillForm(Role r) {
        if (r == null) {
            return;
        }
        nameField.setText(r.getName());
        levelField.setText(String.valueOf(r.getAccessLevel()));
    }

    @FXML
    private void onRefresh() {
        refresh();
    }

    private void refresh() {
        try {
            data.setAll(roleDAO.getAll());
            errorLabel.setText("");
        } catch (RuntimeException e) {
            errorLabel.setText(e.getMessage());
        }
    }

    @FXML
    private void onAdd() {
        try {
            roleDAO.insert(readForm(null));
            refresh();
        } catch (RuntimeException e) {
            errorLabel.setText(e.getMessage());
        }
    }

    @FXML
    private void onUpdate() {
        Role selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            errorLabel.setText("Выберите роль в таблице");
            return;
        }
        try {
            roleDAO.update(readForm(selected.getIdRole()));
            refresh();
        } catch (RuntimeException e) {
            errorLabel.setText(e.getMessage());
        }
    }

    @FXML
    private void onDelete() {
        Role selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            errorLabel.setText("Выберите роль в таблице");
            return;
        }
        try {
            roleDAO.delete(selected.getIdRole());
            refresh();
        } catch (RuntimeException e) {
            errorLabel.setText(e.getMessage());
        }
    }

    private Role readForm(Integer id) {
        String name = nameField.getText().trim();
        String levelText = levelField.getText().trim();
        if (name.isEmpty() || levelText.isEmpty()) {
            throw new RuntimeException("Заполните название и уровень доступа");
        }
        int level;
        try {
            level = Integer.parseInt(levelText);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Уровень доступа должен быть целым числом");
        }
        Role r = new Role();
        if (id != null) {
            r.setIdRole(id);
        }
        r.setName(name);
        r.setAccessLevel(level);
        return r;
    }
}
