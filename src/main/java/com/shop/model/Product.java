package com.shop.model;

import java.math.BigDecimal;

/** Аналог таблицы Товары. */
public class Product {

    private int idProduct;
    private String name;
    private BigDecimal price;
    private String info;
    private boolean hasDelivery;

    public Product() {
    }

    public Product(int idProduct, String name, BigDecimal price, String info, boolean hasDelivery) {
        this.idProduct = idProduct;
        this.name = name;
        this.price = price;
        this.info = info;
        this.hasDelivery = hasDelivery;
    }

    public int getIdProduct() {
        return idProduct;
    }

    public void setIdProduct(int idProduct) {
        this.idProduct = idProduct;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public boolean isHasDelivery() {
        return hasDelivery;
    }

    public void setHasDelivery(boolean hasDelivery) {
        this.hasDelivery = hasDelivery;
    }

    @Override
    public String toString() {
        return name;
    }
}
