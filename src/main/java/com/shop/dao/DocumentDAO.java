package com.shop.dao;

import com.shop.db.DBConnection;
import com.shop.model.Document;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DocumentDAO {

    private static final String BASE_SELECT =
            "SELECT doc.id_документа, doc.id_товар_доставка, doc.количество_товара, doc.id_заказчика, doc.дата_покупки, "
                    + "t.Название AS product_name, t.Цена AS product_price, "
                    + "s.Тип AS delivery_type, c.Название AS customer_name "
                    + "FROM documents doc "
                    + "JOIN product_deliveries pd ON pd.id_товар_доставка = doc.id_товар_доставка "
                    + "JOIN products t ON t.id_товара = pd.id_товара "
                    + "JOIN delivery_methods s ON s.id_доставки = pd.id_доставки "
                    + "JOIN customers c ON c.id_заказчика = doc.id_заказчика ";

    public List<Document> getAll() {
        List<Document> list = new ArrayList<>();
        String sql = BASE_SELECT + "ORDER BY doc.дата_покупки DESC";
        try (PreparedStatement ps = DBConnection.getInstance().getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка получения списка заказов: " + e.getMessage(), e);
        }
        return list;
    }

    public List<Document> getByCustomer(int idCustomer) {
        List<Document> list = new ArrayList<>();
        String sql = BASE_SELECT + "WHERE doc.id_заказчика = ? ORDER BY doc.дата_покупки DESC";
        try (PreparedStatement ps = DBConnection.getInstance().getConnection().prepareStatement(sql)) {
            ps.setInt(1, idCustomer);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка получения заказов заказчика: " + e.getMessage(), e);
        }
        return list;
    }

    public int insert(int idProductDelivery, int quantity, int idCustomer) {
        String sql = "INSERT INTO documents (id_товар_доставка, количество_товара, id_заказчика) VALUES (?, ?, ?)";
        try (PreparedStatement ps = DBConnection.getInstance().getConnection()
                .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, idProductDelivery);
            ps.setInt(2, quantity);
            ps.setInt(3, idCustomer);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка оформления заказа: " + e.getMessage(), e);
        }
        return -1;
    }

    public boolean updateQuantity(int idDocument, int quantity) {
        String sql = "UPDATE documents SET количество_товара = ? WHERE id_документа = ?";
        try (PreparedStatement ps = DBConnection.getInstance().getConnection().prepareStatement(sql)) {
            ps.setInt(1, quantity);
            ps.setInt(2, idDocument);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка обновления заказа: " + e.getMessage(), e);
        }
    }

    public boolean delete(int idDocument) {
        String sql = "DELETE FROM documents WHERE id_документа = ?";
        try (PreparedStatement ps = DBConnection.getInstance().getConnection().prepareStatement(sql)) {
            ps.setInt(1, idDocument);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка удаления заказа: " + e.getMessage(), e);
        }
    }

    public int getOwnerId(int idDocument) {
        String sql = "SELECT id_заказчика FROM documents WHERE id_документа = ?";
        try (PreparedStatement ps = DBConnection.getInstance().getConnection().prepareStatement(sql)) {
            ps.setInt(1, idDocument);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка проверки владельца заказа: " + e.getMessage(), e);
        }
        return -1;
    }

    private Document map(ResultSet rs) throws SQLException {
        Document d = new Document(
                rs.getInt("id_документа"),
                rs.getInt("id_товар_доставка"),
                rs.getInt("количество_товара"),
                rs.getInt("id_заказчика"),
                rs.getTimestamp("дата_покупки").toLocalDateTime()
        );
        d.setProductName(rs.getString("product_name"));
        d.setProductPrice(rs.getBigDecimal("product_price"));
        d.setDeliveryType(rs.getString("delivery_type"));
        d.setCustomerName(rs.getString("customer_name"));
        return d;
    }
}