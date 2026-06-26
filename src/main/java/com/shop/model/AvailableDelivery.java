package com.shop.model;

/** Аналог таблицы Доступные_доставки — какие способы доставки разрешены для товара. */
public class AvailableDelivery {

    private int idProduct;
    private int idDelivery;

    // вспомогательные поля только для отображения в таблице (заполняются JOIN-запросом, в БД не хранятся)
    private String productName;
    private String deliveryType;

    public AvailableDelivery() {
    }

    public AvailableDelivery(int idProduct, int idDelivery) {
        this.idProduct = idProduct;
        this.idDelivery = idDelivery;
    }

    public int getIdProduct() {
        return idProduct;
    }

    public void setIdProduct(int idProduct) {
        this.idProduct = idProduct;
    }

    public int getIdDelivery() {
        return idDelivery;
    }

    public void setIdDelivery(int idDelivery) {
        this.idDelivery = idDelivery;
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
}
