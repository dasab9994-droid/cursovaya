package com.shop.db;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class DBConnection {

    private static volatile DBConnection instance;

    private final String url;
    private final String user;
    private final String password;
    private Connection connection;

    private DBConnection() {
        Properties props = new Properties();
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("db.properties")) {
            if (in == null) {
                throw new RuntimeException("Файл db.properties не найден в classpath приложения");
            }
            props.load(in);
        } catch (IOException e) {
            throw new RuntimeException("Не удалось прочитать db.properties: " + e.getMessage(), e);
        }

        String host = props.getProperty("db.host", "localhost");
        String port = props.getProperty("db.port", "3306");
        String name = props.getProperty("db.name", "companyy");
        this.user = props.getProperty("db.user", "root");
        this.password = props.getProperty("db.password", "");
        this.url = "jdbc:mysql://" + host + ":" + port + "/" + name
                + "?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC&allowPublicKeyRetrieval=true&useSSL=false";

        connect();
    }

    public static DBConnection getInstance() {
        if (instance == null) {
            synchronized (DBConnection.class) {
                if (instance == null) {
                    instance = new DBConnection();
                }
            }
        }
        return instance;
    }

    private void connect() {
        try {
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            throw new RuntimeException(
                    "Не удалось подключиться к базе данных. Проверьте db.properties и доступность MySQL-сервера.\n"
                            + e.getMessage(), e);
        }
    }

    public synchronized Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connect();
            }
        } catch (SQLException e) {
            connect();
        }
        return connection;
    }

    public synchronized void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException ignored) {
            // соединение закрывается при завершении работы приложения — ошибку можно игнорировать
        }
    }
}
