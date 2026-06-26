package com.shop.controller;

import com.shop.MainApp;
import com.shop.dao.CustomerDAO;
import com.shop.model.Customer;
import com.shop.service.PasswordUtil;
import com.shop.service.Session;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

public class LoginController {

    @FXML private TextField loginField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private final CustomerDAO customerDAO = new CustomerDAO();

    @FXML
    private void onLogin() {
        String login = loginField.getText().trim();
        String password = passwordField.getText();
        if (login.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Введите логин и пароль");
            return;
        }
        try {
            Customer customer = customerDAO.findByLogin(login);
            if (customer == null || !PasswordUtil.verify(password, customer.getPasswordHash())) {
                errorLabel.setText("Неверный логин или пароль");
                return;
            }
            Session.login(customer);
            MainApp.showMain();
        } catch (IOException e) {
            errorLabel.setText("Ошибка загрузки интерфейса: " + e.getMessage());
        } catch (RuntimeException e) {
            errorLabel.setText(e.getMessage());
        }
    }

    @FXML
    private void onShowRegister() {
        try {
            MainApp.showRegister();
        } catch (IOException e) {
            errorLabel.setText("Ошибка загрузки интерфейса: " + e.getMessage());
        }
    }
}
