package com.shop.model;

import java.math.BigDecimal;

/** Аналог таблицы Способы_доставки. */
public class DeliveryMethod {

    private int idDelivery;
    private String type;
    private BigDecimal cost;
    private String duration;

    public DeliveryMethod() {
    }

    public DeliveryMethod(int idDelivery, String type, BigDecimal cost, String duration) {
        this.idDelivery = idDelivery;
        this.type = type;
        this.cost = cost;
        this.duration = duration;
    }

    public int getIdDelivery() {
        return idDelivery;
    }

    public void setIdDelivery(int idDelivery) {
        this.idDelivery = idDelivery;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return type;
    }
}
