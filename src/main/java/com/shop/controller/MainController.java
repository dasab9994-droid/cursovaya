package com.shop.controller;

import com.shop.MainApp;
import com.shop.model.Customer;
import com.shop.service.Session;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import java.io.IOException;

public class MainController {

    @FXML private Label welcomeLabel;
    @FXML private TabPane tabPane;

    @FXML
    private void initialize() {
        Customer user = Session.getCurrentUser();
        welcomeLabel.setText("Добро пожаловать, " + user.getName() + " (" + user.getRole().getName() + ")");

        addTab("Товары", "/com/shop/view/products_tab.fxml");
        addTab("Способы доставки", "/com/shop/view/deliveries_tab.fxml");
        addTab(Session.isAdmin() ? "Все заказы" : "Мои заказы", "/com/shop/view/orders_tab.fxml");
        addTab("Профиль", "/com/shop/view/profile_tab.fxml");

        if (Session.isAdmin()) {
            addTab("Доступные доставки", "/com/shop/view/available_deliveries_tab.fxml");
            addTab("Заказчики", "/com/shop/view/customers_tab.fxml");
            addTab("Роли", "/com/shop/view/roles_tab.fxml");
        }
    }

    private void addTab(String title, String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent content = loader.load();
            tabPane.getTabs().add(new Tab(title, content));
        } catch (IOException e) {
            throw new RuntimeException("Ошибка загрузки вкладки «" + title + "»: " + e.getMessage(), e);
        }
    }

    @FXML
    private void onLogout() {
        Session.logout();
        try {
            MainApp.showLogin();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
