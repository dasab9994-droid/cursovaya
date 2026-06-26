package com.shop.dao;

import com.shop.db.DBConnection;
import com.shop.model.ProductDelivery;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO для product_deliveries. У этой таблицы нет метода update — она представляет собой
 * просто конкретизацию пары товар+доставка, на которую ссылаются заказы, и её
 * единственный смысл — Create/Read/Delete. Изменение пары делается через удаление
 * старой записи и создание новой (findOrCreate).
 */
public class ProductDeliveryDAO {

    public List<ProductDelivery> getAll() {
        List<ProductDelivery> list = new ArrayList<>();
        String sql = "SELECT pd.id_товар_доставка, pd.id_товара, pd.id_доставки, "
                + "t.Название AS product_name, s.Тип AS delivery_type "
                + "FROM product_deliveries pd "
                + "JOIN products t ON t.id_товара = pd.id_товара "
                + "JOIN delivery_methods s ON s.id_доставки = pd.id_доставки "
                + "ORDER BY pd.id_товар_доставка";
        try (PreparedStatement ps = DBConnection.getInstance().getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка получения списка комбинаций товар-доставка: " + e.getMessage(), e);
        }
        return list;
    }

    public ProductDelivery findExisting(int idProduct, int idDelivery) {
        String sql = "SELECT id_товар_доставка, id_товара, id_доставки FROM product_deliveries "
                + "WHERE id_товара = ? AND id_доставки = ? LIMIT 1";
        try (PreparedStatement ps = DBConnection.getInstance().getConnection().prepareStatement(sql)) {
            ps.setInt(1, idProduct);
            ps.setInt(2, idDelivery);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new ProductDelivery(rs.getInt("id_товар_доставка"), rs.getInt("id_товара"), rs.getInt("id_доставки"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка поиска комбинации товар-доставка: " + e.getMessage(), e);
        }
        return null;
    }

    public int insert(int idProduct, int idDelivery) {
        String sql = "INSERT INTO product_deliveries (id_товара, id_доставки) VALUES (?, ?)";
        try (PreparedStatement ps = DBConnection.getInstance().getConnection()
                .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, idProduct);
            ps.setInt(2, idDelivery);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка создания комбинации товар-доставка: " + e.getMessage(), e);
        }
        return -1;
    }

    /** Находит существующую комбинацию товар+доставка либо создаёт новую (используется при оформлении заказа). */
    public int findOrCreate(int idProduct, int idDelivery) {
        ProductDelivery existing = findExisting(idProduct, idDelivery);
        if (existing != null) {
            return existing.getIdProductDelivery();
        }
        return insert(idProduct, idDelivery);
    }

    public boolean delete(int idProductDelivery) {
        String sql = "DELETE FROM product_deliveries WHERE id_товар_доставка = ?";
        try (PreparedStatement ps = DBConnection.getInstance().getConnection().prepareStatement(sql)) {
            ps.setInt(1, idProductDelivery);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Невозможно удалить комбинацию: она используется в существующих заказах", e);
        }
    }

    private ProductDelivery map(ResultSet rs) throws SQLException {
        ProductDelivery pd = new ProductDelivery(rs.getInt("id_товар_доставка"), rs.getInt("id_товара"), rs.getInt("id_доставки"));
        pd.setProductName(rs.getString("product_name"));
        pd.setDeliveryType(rs.getString("delivery_type"));
        return pd;
    }
}
