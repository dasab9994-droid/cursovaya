package com.shop.dao;

import com.shop.db.DBConnection;
import com.shop.model.DeliveryMethod;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DeliveryMethodDAO {

    public List<DeliveryMethod> getAll() {
        List<DeliveryMethod> list = new ArrayList<>();
        String sql = "SELECT id_доставки, Тип, Стоимость, Длительность FROM delivery_methods ORDER BY id_доставки";
        try (PreparedStatement ps = DBConnection.getInstance().getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка получения списка способов доставки: " + e.getMessage(), e);
        }
        return list;
    }

    public DeliveryMethod getById(int id) {
        String sql = "SELECT id_доставки, Тип, Стоимость, Длительность FROM delivery_methods WHERE id_доставки = ?";
        try (PreparedStatement ps = DBConnection.getInstance().getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка получения способа доставки: " + e.getMessage(), e);
        }
        return null;
    }

    public int insert(DeliveryMethod d) {
        String sql = "INSERT INTO delivery_methods (Тип, Стоимость, Длительность) VALUES (?, ?, ?)";
        try (PreparedStatement ps = DBConnection.getInstance().getConnection()
                .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, d.getType());
            ps.setBigDecimal(2, d.getCost());
            ps.setString(3, d.getDuration());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка добавления способа доставки: " + e.getMessage(), e);
        }
        return -1;
    }

    public boolean update(DeliveryMethod d) {
        String sql = "UPDATE delivery_methods SET Тип = ?, Стоимость = ?, Длительность = ? WHERE id_доставки = ?";
        try (PreparedStatement ps = DBConnection.getInstance().getConnection().prepareStatement(sql)) {
            ps.setString(1, d.getType());
            ps.setBigDecimal(2, d.getCost());
            ps.setString(3, d.getDuration());
            ps.setInt(4, d.getIdDelivery());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка обновления способа доставки: " + e.getMessage(), e);
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM delivery_methods WHERE id_доставки = ?";
        try (PreparedStatement ps = DBConnection.getInstance().getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Невозможно удалить способ доставки: вероятно, он используется", e);
        }
    }

    private DeliveryMethod map(ResultSet rs) throws SQLException {
        return new DeliveryMethod(
                rs.getInt("id_доставки"),
                rs.getString("Тип"),
                rs.getBigDecimal("Стоимость"),
                rs.getString("Длительность")
        );
    }
}
