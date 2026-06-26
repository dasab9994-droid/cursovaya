package com.shop.controller;

import com.shop.dao.DeliveryMethodDAO;
import com.shop.model.DeliveryMethod;
import com.shop.service.Session;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.math.BigDecimal;

public class DeliveriesController {

    @FXML private TableView<DeliveryMethod> table;
    @FXML private TableColumn<DeliveryMethod, Number> colId;
    @FXML private TableColumn<DeliveryMethod, String> colType;
    @FXML private TableColumn<DeliveryMethod, String> colCost;
    @FXML private TableColumn<DeliveryMethod, String> colDuration;

    @FXML private TextField typeField;
    @FXML private TextField costField;
    @FXML private TextField durationField;
    @FXML private Button addButton;
    @FXML private Button updateButton;
    @FXML private Button deleteButton;
    @FXML private Label errorLabel;

    private final DeliveryMethodDAO deliveryDAO = new DeliveryMethodDAO();
    private final ObservableList<DeliveryMethod> data = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        colId.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getIdDelivery()));
        colType.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getType()));
        colCost.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCost().toString()));
        colDuration.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDuration()));
        table.setItems(data);
        table.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> fillForm(sel));

        boolean admin = Session.isAdmin();
        addButton.setVisible(admin);
        updateButton.setVisible(admin);
        deleteButton.setVisible(admin);
        typeField.setEditable(admin);
        costField.setEditable(admin);
        durationField.setEditable(admin);

        refresh();
    }

    private void fillForm(DeliveryMethod d) {
        if (d == null) {
            return;
        }
        typeField.setText(d.getType());
        costField.setText(d.getCost().toString());
        durationField.setText(d.getDuration());
    }

    @FXML
    private void onRefresh() {
        refresh();
    }

    private void refresh() {
        try {
            data.setAll(deliveryDAO.getAll());
            errorLabel.setText("");
        } catch (RuntimeException e) {
            errorLabel.setText(e.getMessage());
        }
    }

    @FXML
    private void onAdd() {
        try {
            deliveryDAO.insert(readForm(null));
            refresh();
        } catch (RuntimeException e) {
            errorLabel.setText(e.getMessage());
        }
    }

    @FXML
    private void onUpdate() {
        DeliveryMethod selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            errorLabel.setText("Выберите способ доставки в таблице");
            return;
        }
        try {
            deliveryDAO.update(readForm(selected.getIdDelivery()));
            refresh();
        } catch (RuntimeException e) {
            errorLabel.setText(e.getMessage());
        }
    }

    @FXML
    private void onDelete() {
        DeliveryMethod selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            errorLabel.setText("Выберите способ доставки в таблице");
            return;
        }
        try {
            deliveryDAO.delete(selected.getIdDelivery());
            refresh();
        } catch (RuntimeException e) {
            errorLabel.setText(e.getMessage());
        }
    }

    private DeliveryMethod readForm(Integer id) {
        String type = typeField.getText().trim();
        String costText = costField.getText().trim();
        String duration = durationField.getText().trim();
        if (type.isEmpty() || costText.isEmpty() || duration.isEmpty()) {
            throw new RuntimeException("Заполните все поля способа доставки");
        }
        BigDecimal cost;
        try {
            cost = new BigDecimal(costText.replace(",", "."));
        } catch (NumberFormatException e) {
            throw new RuntimeException("Стоимость указана неверно");
        }
        DeliveryMethod d = new DeliveryMethod();
        if (id != null) {
            d.setIdDelivery(id);
        }
        d.setType(type);
        d.setCost(cost);
        d.setDuration(duration);
        return d;
    }
}
