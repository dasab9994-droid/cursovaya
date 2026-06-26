package com.shop.controller;

import com.shop.MainApp;
import com.shop.dao.CustomerDAO;
import com.shop.dao.RoleDAO;
import com.shop.model.Customer;
import com.shop.model.Role;
import com.shop.service.PasswordUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

public class RegisterController {

    @FXML private TextField nameField;
    @FXML private TextField phoneField;
    @FXML private TextField contactField;
    @FXML private TextField addressField;
    @FXML private TextField loginField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmField;
    @FXML private Label errorLabel;

    private final CustomerDAO customerDAO = new CustomerDAO();
    private final RoleDAO roleDAO = new RoleDAO();

    @FXML
    private void onRegister() {
        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();
        String contact = contactField.getText().trim();
        String address = addressField.getText().trim();
        String login = loginField.getText().trim();
        String password = passwordField.getText();
        String confirm = confirmField.getText();

        if (name.isEmpty() || phone.isEmpty() || contact.isEmpty() || address.isEmpty() || login.isEmpty()) {
            errorLabel.setText("Заполните все поля");
            return;
        }
        if (!password.equals(confirm)) {
            errorLabel.setText("Пароли не совпадают");
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
            Role clientRole = findClientRole();
            if (clientRole == null) {
                errorLabel.setText("В системе не настроена роль обычного пользователя. Обратитесь к администратору.");
                return;
            }
            Customer customer = new Customer();
            customer.setName(name);
            customer.setPhone(phone);
            customer.setContactPerson(contact);
            customer.setAddress(address);
            customer.setLogin(login);
            customer.setPasswordHash(PasswordUtil.hash(password));
            customer.setRole(clientRole);
            customerDAO.insert(customer);
            MainApp.showLogin();
        } catch (IOException e) {
            errorLabel.setText("Ошибка загрузки интерфейса: " + e.getMessage());
        } catch (RuntimeException e) {
            errorLabel.setText(e.getMessage());
        }
    }

    /** Регистрация всегда создаёт пользователя с ролью минимального уровня доступа (обычный клиент). */
    private Role findClientRole() {
        List<Role> roles = roleDAO.getAll();
        return roles.stream()
                .min(Comparator.comparingInt(Role::getAccessLevel))
                .orElse(null);
    }

    @FXML
    private void onBackToLogin() {
        try {
            MainApp.showLogin();
        } catch (IOException e) {
            errorLabel.setText("Ошибка загрузки интерфейса: " + e.getMessage());
        }
    }
}
