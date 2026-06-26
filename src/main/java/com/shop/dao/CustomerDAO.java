package com.shop.dao;

import com.shop.db.DBConnection;
import com.shop.model.Customer;
import com.shop.model.Role;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {

    private static final String BASE_SELECT =
            "SELECT c.id_заказчика, c.Название, c.Номер_телефона, c.Контактное_лицо, c.Адрес, c.Логин, c.Пароль, "
                    + "r.id_роли, r.Название AS role_name, r.Уровень_доступа "
                    + "FROM customers c JOIN roles r ON r.id_роли = c.id_роли ";

    public List<Customer> getAll() {
        List<Customer> list = new ArrayList<>();
        String sql = BASE_SELECT + "ORDER BY c.id_заказчика";
        try (PreparedStatement ps = DBConnection.getInstance().getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка получения списка заказчиков: " + e.getMessage(), e);
        }
        return list;
    }

    public Customer getById(int id) {
        String sql = BASE_SELECT + "WHERE c.id_заказчика = ?";
        try (PreparedStatement ps = DBConnection.getInstance().getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка получения заказчика: " + e.getMessage(), e);
        }
        return null;
    }

    public Customer findByLogin(String login) {
        String sql = BASE_SELECT + "WHERE c.Логин = ?";
        try (PreparedStatement ps = DBConnection.getInstance().getConnection().prepareStatement(sql)) {
            ps.setString(1, login);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка поиска заказчика по логину: " + e.getMessage(), e);
        }
        return null;
    }

    public boolean loginExists(String login) {
        return findByLogin(login) != null;
    }

    public int insert(Customer c) {
        String sql = "INSERT INTO customers (Название, Номер_телефона, Контактное_лицо, Адрес, Логин, Пароль, id_роли) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = DBConnection.getInstance().getConnection()
                .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, c.getName());
            ps.setString(2, c.getPhone());
            ps.setString(3, c.getContactPerson());
            ps.setString(4, c.getAddress());
            ps.setString(5, c.getLogin());
            ps.setString(6, c.getPasswordHash());
            ps.setInt(7, c.getRole().getIdRole());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка регистрации заказчика: " + e.getMessage(), e);
        }
        return -1;
    }
   public boolean updateProfile(Customer c) {
        String sql = "UPDATE customers SET Название = ?, Номер_телефона = ?, Контактное_лицо = ?, Адрес = ? "
                + "WHERE id_заказчика = ?";
        try (PreparedStatement ps = DBConnection.getInstance().getConnection().prepareStatement(sql)) {
            ps.setString(1, c.getName());
            ps.setString(2, c.getPhone());
            ps.setString(3, c.getContactPerson());
            ps.setString(4, c.getAddress());
            ps.setInt(5, c.getIdCustomer());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка обновления данных заказчика: " + e.getMessage(), e);
        }
    }
  public boolean updateAsAdmin(Customer c) {
        String sql = "UPDATE customers SET Название = ?, Номер_телефона = ?, Контактное_лицо = ?, Адрес = ?, id_роли = ? "
                + "WHERE id_заказчика = ?";
        try (PreparedStatement ps = DBConnection.getInstance().getConnection().prepareStatement(sql)) {
            ps.setString(1, c.getName());
            ps.setString(2, c.getPhone());
            ps.setString(3, c.getContactPerson());
            ps.setString(4, c.getAddress());
            ps.setInt(5, c.getRole().getIdRole());
            ps.setInt(6, c.getIdCustomer());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка обновления заказчика: " + e.getMessage(), e);
        }
    }

    public boolean updatePassword(int idCustomer, String newPasswordHash) {
        String sql = "UPDATE customers SET Пароль = ? WHERE id_заказчика = ?";
        try (PreparedStatement ps = DBConnection.getInstance().getConnection().prepareStatement(sql)) {
            ps.setString(1, newPasswordHash);
            ps.setInt(2, idCustomer);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка обновления пароля: " + e.getMessage(), e);
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM customers WHERE id_заказчика = ?";
        try (PreparedStatement ps = DBConnection.getInstance().getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Невозможно удалить заказчика: у него есть оформленные заказы", e);
        }
    }

    private Customer map(ResultSet rs) throws SQLException {
        Role role = new Role(rs.getInt("id_роли"), rs.getString("role_name"), rs.getInt("Уровень_доступа"));
        return new Customer(
                rs.getInt("id_заказчика"),
                rs.getString("Название"),
                rs.getString("Номер_телефона"),
                rs.getString("Контактное_лицо"),
                rs.getString("Адрес"),
                rs.getString("Логин"),
                rs.getString("Пароль"),
                role
        );
    }
}
