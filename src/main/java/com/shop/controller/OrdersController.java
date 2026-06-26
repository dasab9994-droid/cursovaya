package com.shop.controller;

import com.shop.dao.AvailableDeliveryDAO;
import com.shop.dao.DocumentDAO;
import com.shop.dao.ProductDAO;
import com.shop.dao.ProductDeliveryDAO;
import com.shop.model.DeliveryMethod;
import com.shop.model.Document;
import com.shop.model.Product;
import com.shop.service.Session;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

public class OrdersController {

    @FXML private TableView<Document> table;
    @FXML private TableColumn<Document, Number> colId;
    @FXML private TableColumn<Document, String> colProduct;
    @FXML private TableColumn<Document, String> colDelivery;
    @FXML private TableColumn<Document, Number> colQuantity;
    @FXML private TableColumn<Document, Number> colTotalPrice;
    @FXML private TableColumn<Document, String> colCustomer;
    @FXML private TableColumn<Document, String> colDate;

    @FXML private ComboBox<Product> productCombo;
    @FXML private ComboBox<DeliveryMethod> deliveryCombo;
    @FXML private TextField quantityField;
    @FXML private TextField updateQuantityField;
    @FXML private Button updateButton;
    @FXML private Button deleteButton;
    @FXML private Label errorLabel;
    @FXML private Label totalSumLabel;

    private final DocumentDAO documentDAO = new DocumentDAO();
    private final ProductDAO productDAO = new ProductDAO();
    private final AvailableDeliveryDAO availableDeliveryDAO = new AvailableDeliveryDAO();
    private final ProductDeliveryDAO productDeliveryDAO = new ProductDeliveryDAO();
    private final ObservableList<Document> data = FXCollections.observableArrayList();
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    @FXML
    private void initialize() {
        colId.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getIdDocument()));
        colProduct.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getProductName()));
        colDelivery.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDeliveryType()));
        colQuantity.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getQuantity()));
        colTotalPrice.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getTotalPrice().doubleValue()));
        colTotalPrice.setCellFactory(col -> new TableCell<Document, Number>() {
            @Override
            protected void updateItem(Number item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%.2f руб.", item.doubleValue()));
                }
            }
        });
        colCustomer.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCustomerName()));
        colDate.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPurchaseDate().format(DATE_FORMAT)));
        table.setItems(data);

        colCustomer.setVisible(Session.isAdmin());

        productCombo.setItems(FXCollections.observableArrayList(productDAO.getAll()));
        productCombo.valueProperty().addListener((obs, old, selected) -> {
            deliveryCombo.getItems().clear();
            if (selected != null) {
                deliveryCombo.setItems(FXCollections.observableArrayList(
                        availableDeliveryDAO.getDeliveryMethodsForProduct(selected.getIdProduct())));
            }
        });

        refresh();
    }

    @FXML
    private void onRefresh() {
        refresh();
    }

    private void refresh() {
        try {
            if (Session.isAdmin()) {
                data.setAll(documentDAO.getAll());
            } else {
                data.setAll(documentDAO.getByCustomer(Session.getCurrentUser().getIdCustomer()));
            }
            updateTotalSum();
            errorLabel.setText("");
        } catch (RuntimeException e) {
            errorLabel.setText(e.getMessage());
        }
    }

    private void updateTotalSum() {
        BigDecimal total = data.stream()
                .map(Document::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        totalSumLabel.setText("Общая сумма: " + String.format("%.2f", total) + " руб.");
    }

    @FXML
    private void onCreateOrder() {
        Product product = productCombo.getValue();
        DeliveryMethod delivery = deliveryCombo.getValue();
        String quantityText = quantityField.getText().trim();
        if (product == null || delivery == null || quantityText.isEmpty()) {
            errorLabel.setText("Выберите товар, способ доставки и укажите количество");
            return;
        }
        int quantity;
        try {
            quantity = Integer.parseInt(quantityText);
            if (quantity <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            errorLabel.setText("Количество должно быть целым числом больше нуля");
            return;
        }
        try {
            if (!availableDeliveryDAO.exists(product.getIdProduct(), delivery.getIdDelivery())) {
                errorLabel.setText("Выбранный способ доставки недоступен для этого товара");
                return;
            }
            int idProductDelivery = productDeliveryDAO.findOrCreate(product.getIdProduct(), delivery.getIdDelivery());
            documentDAO.insert(idProductDelivery, quantity, Session.getCurrentUser().getIdCustomer());
            quantityField.clear();
            refresh();
        } catch (RuntimeException e) {
            errorLabel.setText(e.getMessage());
        }
    }

    @FXML
    private void onUpdateQuantity() {
        Document selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            errorLabel.setText("Выберите заказ в таблице");
            return;
        }
        if (!canModify(selected)) {
            errorLabel.setText("Недостаточно прав для изменения этого заказа");
            return;
        }
        String text = updateQuantityField.getText().trim();
        int quantity;
        try {
            quantity = Integer.parseInt(text);
            if (quantity <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            errorLabel.setText("Укажите корректное новое количество");
            return;
        }
        try {
            documentDAO.updateQuantity(selected.getIdDocument(), quantity);
            updateQuantityField.clear();
            refresh();
        } catch (RuntimeException e) {
            errorLabel.setText(e.getMessage());
        }
    }

    @FXML
    private void onDelete() {
        Document selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            errorLabel.setText("Выберите заказ в таблице");
            return;
        }
        if (!canModify(selected)) {
            errorLabel.setText("Недостаточно прав для удаления этого заказа");
            return;
        }
        try {
            documentDAO.delete(selected.getIdDocument());
            refresh();
        } catch (RuntimeException e) {
            errorLabel.setText(e.getMessage());
        }
    }

    private boolean canModify(Document document) {
        return Session.isAdmin() || document.getIdCustomer() == Session.getCurrentUser().getIdCustomer();
    }
}