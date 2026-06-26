package com.shop.controller;

import com.shop.dao.CustomerDAO;
import com.shop.dao.RoleDAO;
import com.shop.model.Customer;
import com.shop.model.Role;
import com.shop.service.PasswordUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class CustomersController {

    @FXML private TableView<Customer> table;
    @FXML private TableColumn<Customer, String> colId;
    @FXML private TableColumn<Customer, String> colName;
    @FXML private TableColumn<Customer, String> colPhone;
    @FXML private TableColumn<Customer, String> colContact;
    @FXML private TableColumn<Customer, String> colAddress;
    @FXML private TableColumn<Customer, String> colLogin;
    @FXML private TableColumn<Customer, String> colRole;

    @FXML private TextField nameField;
    @FXML private TextField phoneField;
    @FXML private TextField contactField;
    @FXML private TextField addressField;
    @FXML private TextField loginField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<Role> roleCombo;
    @FXML private Label errorLabel;

    private final CustomerDAO customerDAO = new CustomerDAO();
    private final RoleDAO roleDAO = new RoleDAO();
    private final ObservableList<Customer> data = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        colId.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getIdCustomer())));
        colName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));
        colPhone.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPhone()));
        colContact.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getContactPerson()));
        colAddress.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getAddress()));
        colLogin.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getLogin()));
        colRole.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getRole().getName()));
        table.setItems(data);
        table.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> fillForm(sel));

        roleCombo.setItems(FXCollections.observableArrayList(roleDAO.getAll()));

        refresh();
    }

    private void fillForm(Customer c) {
        if (c == null) {
            return;
        }
        nameField.setText(c.getName());
        phoneField.setText(c.getPhone());
        contactField.setText(c.getContactPerson());
        addressField.setText(c.getAddress());
        loginField.setText(c.getLogin());
        passwordField.clear();
        roleCombo.setValue(c.getRole());
    }

    @FXML
    private void onRefresh() {
        refresh();
    }

    private void refresh() {
        try {
            data.setAll(customerDAO.getAll());
            errorLabel.setText("");
        } catch (RuntimeException e) {
            errorLabel.setText(e.getMessage());
        }
    }

    @FXML
    private void onAdd() {
        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();
        String contact = contactField.getText().trim();
        String address = addressField.getText().trim();
        String login = loginField.getText().trim();
        String password = passwordField.getText();
        Role role = roleCombo.getValue();

        if (name.isEmpty() || phone.isEmpty() || contact.isEmpty() || address.isEmpty()
                || login.isEmpty() || password.isEmpty() || role == null) {
            errorLabel.setText("Для создания заказчика заполните все поля, включая логин/пароль/роль");
            return;
        }
        if (!PasswordUtil.isStrong(password)) {
            errorLabel.setText("Пароль слишком простой: нужно минимум 8 символов, цифра, заглавная буква и спецсимвол");
            return;
        }
        try {
            if (customerDAO.loginExists(login)) {
                errorLabel.setText("Такой логин уже занят");
                return;
            }
            Customer c = new Customer();
            c.setName(name);
            c.setPhone(phone);
            c.setContactPerson(contact);
            c.setAddress(address);
            c.setLogin(login);
            c.setPasswordHash(PasswordUtil.hash(password));
            c.setRole(role);
            customerDAO.insert(c);
            refresh();
        } catch (RuntimeException e) {
            errorLabel.setText(e.getMessage());
        }
    }

    @FXML
    private void onUpdate() {
        Customer selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            errorLabel.setText("Выберите заказчика в таблице");
            return;
        }
        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();
        String contact = contactField.getText().trim();
        String address = addressField.getText().trim();
        Role role = roleCombo.getValue();
        if (name.isEmpty() || phone.isEmpty() || contact.isEmpty() || address.isEmpty() || role == null) {
            errorLabel.setText("Заполните все поля и выберите роль");
            return;
        }
        try {
            Customer c = new Customer();
            c.setIdCustomer(selected.getIdCustomer());
            c.setName(name);
            c.setPhone(phone);
            c.setContactPerson(contact);
            c.setAddress(address);
            c.setRole(role);
            customerDAO.updateAsAdmin(c);
            refresh();
        } catch (RuntimeException e) {
            errorLabel.setText(e.getMessage());
        }
    }

    @FXML
    private void onDelete() {
        Customer selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            errorLabel.setText("Выберите заказчика в таблице");
            return;
        }
        try {
            customerDAO.delete(selected.getIdCustomer());
            refresh();
        } catch (RuntimeException e) {
            errorLabel.setText(e.getMessage());
        }
    }
}
