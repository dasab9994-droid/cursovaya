package com.shop.controller;

import com.shop.dao.CustomerDAO;
import com.shop.model.Customer;
import com.shop.service.PasswordUtil;
import com.shop.service.Session;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class ProfileController {

    @FXML private TextField loginField;
    @FXML private TextField nameField;
    @FXML private TextField phoneField;
    @FXML private TextField contactField;
    @FXML private TextField addressField;
    @FXML private Label profileMessage;

    @FXML private PasswordField oldPasswordField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label passwordMessage;

    private final CustomerDAO customerDAO = new CustomerDAO();

    @FXML
    private void initialize() {
        Customer user = Session.getCurrentUser();
        loginField.setText(user.getLogin());
        nameField.setText(user.getName());
        phoneField.setText(user.getPhone());
        contactField.setText(user.getContactPerson());
        addressField.setText(user.getAddress());
    }

    @FXML
    private void onSaveProfile() {
        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();
        String contact = contactField.getText().trim();
        String address = addressField.getText().trim();
        if (name.isEmpty() || phone.isEmpty() || contact.isEmpty() || address.isEmpty()) {
            profileMessage.setStyle("-fx-text-fill: red;");
            profileMessage.setText("Заполните все поля");
            return;
        }
        try {
            Customer user = Session.getCurrentUser();
            Customer updated = new Customer();
            updated.setIdCustomer(user.getIdCustomer());
            updated.setName(name);
            updated.setPhone(phone);
            updated.setContactPerson(contact);
            updated.setAddress(address);
            customerDAO.updateProfile(updated);

            
            user.setName(name);
            user.setPhone(phone);
            user.setContactPerson(contact);
            user.setAddress(address);

            profileMessage.setStyle("-fx-text-fill: green;");
            profileMessage.setText("Данные успешно сохранены");
        } catch (RuntimeException e) {
            profileMessage.setStyle("-fx-text-fill: red;");
            profileMessage.setText(e.getMessage());
        }
    }

    @FXML
    private void onChangePassword() {
        String oldPassword = oldPasswordField.getText();
        String newPassword = newPasswordField.getText();
        String confirm = confirmPasswordField.getText();

        Customer user = Session.getCurrentUser();
        if (!PasswordUtil.verify(oldPassword, user.getPasswordHash())) {
            passwordMessage.setStyle("-fx-text-fill: red;");
            passwordMessage.setText("Текущий пароль указан неверно");
            return;
        }
        if (!newPassword.equals(confirm)) {
            passwordMessage.setStyle("-fx-text-fill: red;");
            passwordMessage.setText("Новые пароли не совпадают");
            return;
        }
        if (!PasswordUtil.isStrong(newPassword)) {
            passwordMessage.setStyle("-fx-text-fill: red;");
            passwordMessage.setText("Пароль слишком простой: нужно минимум 8 символов, цифра, заглавная буква и спецсимвол");
            return;
        }
        try {
            String newHash = PasswordUtil.hash(newPassword);
            customerDAO.updatePassword(user.getIdCustomer(), newHash);
            user.setPasswordHash(newHash);
            oldPasswordField.clear();
            newPasswordField.clear();
            confirmPasswordField.clear();
            passwordMessage.setStyle("-fx-text-fill: green;");
            passwordMessage.setText("Пароль успешно изменён");
        } catch (RuntimeException e) {
            passwordMessage.setStyle("-fx-text-fill: red;");
            passwordMessage.setText(e.getMessage());
        }
    }
}
