package com.shop.controller;

import com.shop.dao.ProductDAO;
import com.shop.model.Product;
import com.shop.service.Session;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.math.BigDecimal;

public class ProductsController {

    @FXML private TableView<Product> table;
    @FXML private TableColumn<Product, Number> colId;
    @FXML private TableColumn<Product, String> colName;
    @FXML private TableColumn<Product, String> colPrice;
    @FXML private TableColumn<Product, String> colInfo;
    @FXML private TableColumn<Product, String> colDelivery;

    @FXML private TextField nameField;
    @FXML private TextField priceField;
    @FXML private TextArea infoField;
    @FXML private CheckBox deliveryCheck;
    @FXML private Button addButton;
    @FXML private Button updateButton;
    @FXML private Button deleteButton;
    @FXML private Label errorLabel;

    private final ProductDAO productDAO = new ProductDAO();
    private final ObservableList<Product> data = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        colId.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getIdProduct()));
        colName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));
        colPrice.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPrice().toString()));
        colInfo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getInfo()));
        colDelivery.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().isHasDelivery() ? "Да" : "Нет"));
        table.setItems(data);
        table.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> fillForm(sel));

        boolean admin = Session.isAdmin();
        addButton.setVisible(admin);
        updateButton.setVisible(admin);
        deleteButton.setVisible(admin);
        nameField.setEditable(admin);
        priceField.setEditable(admin);
        infoField.setEditable(admin);
        deliveryCheck.setDisable(!admin);

        refresh();
    }

    private void fillForm(Product p) {
        if (p == null) {
            return;
        }
        nameField.setText(p.getName());
        priceField.setText(p.getPrice().toString());
        infoField.setText(p.getInfo());
        deliveryCheck.setSelected(p.isHasDelivery());
    }

    @FXML
    private void onRefresh() {
        refresh();
    }

    private void refresh() {
        try {
            data.setAll(productDAO.getAll());
            errorLabel.setText("");
        } catch (RuntimeException e) {
            errorLabel.setText(e.getMessage());
        }
    }

    @FXML
    private void onAdd() {
        try {
            productDAO.insert(readForm(null));
            refresh();
        } catch (RuntimeException e) {
            errorLabel.setText(e.getMessage());
        }
    }

    @FXML
    private void onUpdate() {
        Product selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            errorLabel.setText("Выберите товар в таблице");
            return;
        }
        try {
            productDAO.update(readForm(selected.getIdProduct()));
            refresh();
        } catch (RuntimeException e) {
            errorLabel.setText(e.getMessage());
        }
    }

    @FXML
    private void onDelete() {
        Product selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            errorLabel.setText("Выберите товар в таблице");
            return;
        }
        try {
            productDAO.delete(selected.getIdProduct());
            refresh();
        } catch (RuntimeException e) {
            errorLabel.setText(e.getMessage());
        }
    }

    private Product readForm(Integer id) {
        String name = nameField.getText().trim();
        String priceText = priceField.getText().trim();
        String info = infoField.getText().trim();
        if (name.isEmpty() || priceText.isEmpty() || info.isEmpty()) {
            throw new RuntimeException("Заполните все поля товара");
        }
        BigDecimal price;
        try {
            price = new BigDecimal(priceText.replace(",", "."));
        } catch (NumberFormatException e) {
            throw new RuntimeException("Цена указана неверно");
        }
        Product p = new Product();
        if (id != null) {
            p.setIdProduct(id);
        }
        p.setName(name);
        p.setPrice(price);
        p.setInfo(info);
        p.setHasDelivery(deliveryCheck.isSelected());
        return p;
    }
}
