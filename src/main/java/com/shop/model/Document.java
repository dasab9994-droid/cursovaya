package com.shop.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Document {

    private int idDocument;
    private int idProductDelivery;
    private int quantity;
    private int idCustomer;
    private LocalDateTime purchaseDate;
    private String productName;
    private String deliveryType;
    private String customerName;
    private BigDecimal productPrice;

    public Document() {
    }

    public Document(int idDocument, int idProductDelivery, int quantity, int idCustomer, LocalDateTime purchaseDate) {
        this.idDocument = idDocument;
        this.idProductDelivery = idProductDelivery;
        this.quantity = quantity;
        this.idCustomer = idCustomer;
        this.purchaseDate = purchaseDate;
    }

    public int getIdDocument() {
        return idDocument;
    }

    public void setIdDocument(int idDocument) {
        this.idDocument = idDocument;
    }

    public int getIdProductDelivery() {
        return idProductDelivery;
    }

    public void setIdProductDelivery(int idProductDelivery) {
        this.idProductDelivery = idProductDelivery;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getIdCustomer() {
        return idCustomer;
    }

    public void setIdCustomer(int idCustomer) {
        this.idCustomer = idCustomer;
    }

    public LocalDateTime getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDateTime purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(String deliveryType) {
        this.deliveryType = deliveryType;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public BigDecimal getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(BigDecimal productPrice) {
        this.productPrice = productPrice;
    }

    public BigDecimal getTotalPrice() {
        if (productPrice == null) {
            return BigDecimal.ZERO;
        }
        return productPrice.multiply(BigDecimal.valueOf(quantity));
    }
}