package com.shop.controller;

import com.shop.dao.AvailableDeliveryDAO;
import com.shop.dao.DeliveryMethodDAO;
import com.shop.dao.ProductDAO;
import com.shop.model.AvailableDelivery;
import com.shop.model.DeliveryMethod;
import com.shop.model.Product;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class AvailableDeliveriesController {
    @FXML private TableView<AvailableDelivery> table;
    @FXML private TableColumn<AvailableDelivery, String> colProduct;
    @FXML private TableColumn<AvailableDelivery, String> colDelivery;
    @FXML private ComboBox<Product> productCombo;
    @FXML private ComboBox<DeliveryMethod> deliveryCombo;
    @FXML private Label errorLabel;

    private final AvailableDeliveryDAO availableDeliveryDAO = new AvailableDeliveryDAO();
    private final ProductDAO productDAO = new ProductDAO();
    private final DeliveryMethodDAO deliveryMethodDAO = new DeliveryMethodDAO();
    private final ObservableList<AvailableDelivery> data = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        colProduct.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getProductName()));
        colDelivery.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDeliveryType()));
        table.setItems(data);

        productCombo.setItems(FXCollections.observableArrayList(productDAO.getAll()));
        deliveryCombo.setItems(FXCollections.observableArrayList(deliveryMethodDAO.getAll()));

        refresh();
    }

    @FXML
    private void onRefresh() {
        refresh();
    }

    private void refresh() {
        try {
            data.setAll(availableDeliveryDAO.getAll());
            errorLabel.setText("");
        } catch (RuntimeException e) {
            errorLabel.setText(e.getMessage());
        }
    }

    @FXML
    private void onAdd() {
        Product product = productCombo.getValue();
        DeliveryMethod delivery = deliveryCombo.getValue();
        if (product == null || delivery == null) {
            errorLabel.setText("Выберите товар и способ доставки");
            return;
        }
        try {
            if (availableDeliveryDAO.exists(product.getIdProduct(), delivery.getIdDelivery())) {
                errorLabel.setText("Такая связь уже существует");
                return;
            }
            availableDeliveryDAO.insert(product.getIdProduct(), delivery.getIdDelivery());
            refresh();
        } catch (RuntimeException e) {
            errorLabel.setText(e.getMessage());
        }
    }

    @FXML
    private void onDelete() {
        AvailableDelivery selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            errorLabel.setText("Выберите строку в таблице");
            return;
        }
        try {
            availableDeliveryDAO.delete(selected.getIdProduct(), selected.getIdDelivery());
            refresh();
        } catch (RuntimeException e) {
            errorLabel.setText(e.getMessage());
        }
    }
}
