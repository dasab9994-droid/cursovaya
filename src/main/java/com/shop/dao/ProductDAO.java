package com.shop.dao;

import com.shop.db.DBConnection;
import com.shop.model.Product;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    public List<Product> getAll() {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT id_товара, Название, Цена, Информация, Наличие_доставки FROM products ORDER BY id_товара";
        try (PreparedStatement ps = DBConnection.getInstance().getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка получения списка товаров: " + e.getMessage(), e);
        }
        return list;
    }

    public Product getById(int id) {
        String sql = "SELECT id_товара, Название, Цена, Информация, Наличие_доставки FROM products WHERE id_товара = ?";
        try (PreparedStatement ps = DBConnection.getInstance().getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка получения товара: " + e.getMessage(), e);
        }
        return null;
    }

    public int insert(Product p) {
        String sql = "INSERT INTO products (Название, Цена, Информация, Наличие_доставки) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = DBConnection.getInstance().getConnection()
                .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, p.getName());
            ps.setBigDecimal(2, p.getPrice());
            ps.setString(3, p.getInfo());
            ps.setBoolean(4, p.isHasDelivery());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка добавления товара: " + e.getMessage(), e);
        }
        return -1;
    }

    public boolean update(Product p) {
        String sql = "UPDATE products SET Название = ?, Цена = ?, Информация = ?, Наличие_доставки = ? WHERE id_товара = ?";
        try (PreparedStatement ps = DBConnection.getInstance().getConnection().prepareStatement(sql)) {
            ps.setString(1, p.getName());
            ps.setBigDecimal(2, p.getPrice());
            ps.setString(3, p.getInfo());
            ps.setBoolean(4, p.isHasDelivery());
            ps.setInt(5, p.getIdProduct());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка обновления товара: " + e.getMessage(), e);
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM products WHERE id_товара = ?";
        try (PreparedStatement ps = DBConnection.getInstance().getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Невозможно удалить товар: вероятно, он используется в доставках или заказах", e);
        }
    }

    private Product map(ResultSet rs) throws SQLException {
        return new Product(
                rs.getInt("id_товара"),
                rs.getString("Название"),
                rs.getBigDecimal("Цена"),
                rs.getString("Информация"),
                rs.getBoolean("Наличие_доставки")
        );
    }
}
