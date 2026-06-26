package com.shop.dao;

import com.shop.db.DBConnection;
import com.shop.model.Role;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class RoleDAO {

    public List<Role> getAll() {
        List<Role> roles = new ArrayList<>();
        String sql = "SELECT id_роли, Название, Уровень_доступа FROM roles ORDER BY Уровень_доступа";
        try (PreparedStatement ps = DBConnection.getInstance().getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                roles.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка получения списка ролей: " + e.getMessage(), e);
        }
        return roles;
    }

    public Role getById(int id) {
        String sql = "SELECT id_роли, Название, Уровень_доступа FROM roles WHERE id_роли = ?";
        try (PreparedStatement ps = DBConnection.getInstance().getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка получения роли: " + e.getMessage(), e);
        }
        return null;
    }

    public int insert(Role role) {
        String sql = "INSERT INTO roles (Название, Уровень_доступа) VALUES (?, ?)";
        try (PreparedStatement ps = DBConnection.getInstance().getConnection()
                .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, role.getName());
            ps.setInt(2, role.getAccessLevel());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка добавления роли: " + e.getMessage(), e);
        }
        return -1;
    }

    public boolean update(Role role) {
        String sql = "UPDATE roles SET Название = ?, Уровень_доступа = ? WHERE id_роли = ?";
        try (PreparedStatement ps = DBConnection.getInstance().getConnection().prepareStatement(sql)) {
            ps.setString(1, role.getName());
            ps.setInt(2, role.getAccessLevel());
            ps.setInt(3, role.getIdRole());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка обновления роли: " + e.getMessage(), e);
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM roles WHERE id_роли = ?";
        try (PreparedStatement ps = DBConnection.getInstance().getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Невозможно удалить роль: вероятно, она назначена заказчикам", e);
        }
    }

    private Role map(ResultSet rs) throws SQLException {
        return new Role(rs.getInt("id_роли"), rs.getString("Название"), rs.getInt("Уровень_доступа"));
    }
}
