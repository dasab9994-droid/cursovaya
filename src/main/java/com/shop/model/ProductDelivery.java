package com.shop.model;

/** Аналог таблицы Товары_доставки. */
public class ProductDelivery {

    private int idProductDelivery;
    private int idProduct;
    private int idDelivery;

    // вспомогательные поля только для отображения (заполняются JOIN-запросом)
    private String productName;
    private String deliveryType;

    public ProductDelivery() {
    }

    public ProductDelivery(int idProductDelivery, int idProduct, int idDelivery) {
        this.idProductDelivery = idProductDelivery;
        this.idProduct = idProduct;
        this.idDelivery = idDelivery;
    }

    public int getIdProductDelivery() {
        return idProductDelivery;
    }

    public void setIdProductDelivery(int idProductDelivery) {
        this.idProductDelivery = idProductDelivery;
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
