package com.shop.dao;

import com.shop.db.DBConnection;
import com.shop.model.AvailableDelivery;
import com.shop.model.DeliveryMethod;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AvailableDeliveryDAO {

    public List<AvailableDelivery> getAll() {
        List<AvailableDelivery> list = new ArrayList<>();
        String sql = "SELECT ad.id_товара, ad.id_доставки, t.Название AS product_name, s.Тип AS delivery_type "
                + "FROM available_deliveries ad "
                + "JOIN products t ON t.id_товара = ad.id_товара "
                + "JOIN delivery_methods s ON s.id_доставки = ad.id_доставки "
                + "ORDER BY t.Название, s.Тип";
        try (PreparedStatement ps = DBConnection.getInstance().getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                AvailableDelivery a = new AvailableDelivery(rs.getInt("id_товара"), rs.getInt("id_доставки"));
                a.setProductName(rs.getString("product_name"));
                a.setDeliveryType(rs.getString("delivery_type"));
                list.add(a);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка получения доступных доставок: " + e.getMessage(), e);
        }
        return list;
    }

    /** Способы доставки, разрешённые для конкретного товара — используется при оформлении заказа. */
    public List<DeliveryMethod> getDeliveryMethodsForProduct(int idProduct) {
        List<DeliveryMethod> list = new ArrayList<>();
        String sql = "SELECT s.id_доставки, s.Тип, s.Стоимость, s.Длительность "
                + "FROM available_deliveries ad JOIN delivery_methods s ON s.id_доставки = ad.id_доставки "
                + "WHERE ad.id_товара = ? ORDER BY s.Тип";
        try (PreparedStatement ps = DBConnection.getInstance().getConnection().prepareStatement(sql)) {
            ps.setInt(1, idProduct);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new DeliveryMethod(rs.getInt("id_доставки"), rs.getString("Тип"),
                            rs.getBigDecimal("Стоимость"), rs.getString("Длительность")));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка получения способов доставки для товара: " + e.getMessage(), e);
        }
        return list;
    }

    public boolean exists(int idProduct, int idDelivery) {
        String sql = "SELECT 1 FROM available_deliveries WHERE id_товара = ? AND id_доставки = ?";
        try (PreparedStatement ps = DBConnection.getInstance().getConnection().prepareStatement(sql)) {
            ps.setInt(1, idProduct);
            ps.setInt(2, idDelivery);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка проверки доступности доставки: " + e.getMessage(), e);
        }
    }

    public boolean insert(int idProduct, int idDelivery) {
        String sql = "INSERT INTO available_deliveries (id_товара, id_доставки) VALUES (?, ?)";
        try (PreparedStatement ps = DBConnection.getInstance().getConnection().prepareStatement(sql)) {
            ps.setInt(1, idProduct);
            ps.setInt(2, idDelivery);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка добавления связи товар-доставка: " + e.getMessage(), e);
        }
    }

    public boolean delete(int idProduct, int idDelivery) {
        String sql = "DELETE FROM available_deliveries WHERE id_товара = ? AND id_доставки = ?";
        try (PreparedStatement ps = DBConnection.getInstance().getConnection().prepareStatement(sql)) {
            ps.setInt(1, idProduct);
            ps.setInt(2, idDelivery);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Невозможно удалить связь: вероятно, она уже используется в заказах", e);
        }
    }
}
