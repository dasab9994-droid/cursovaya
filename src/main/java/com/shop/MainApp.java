package com.shop;

import com.shop.db.DBConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApp extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        primaryStage.setTitle("Shop App — управление магазином");

        // Инициируем подключение к БД сразу при старте, чтобы быстро увидеть ошибку конфигурации,
        // если db.properties настроен неверно.
        DBConnection.getInstance();

        showLogin();
        primaryStage.show();
    }

    public static void showLogin() throws IOException {
        setScene("/com/shop/view/login.fxml", 420, 340);
    }

    public static void showRegister() throws IOException {
        setScene("/com/shop/view/register.fxml", 460, 520);
    }

    public static void showMain() throws IOException {
        setScene("/com/shop/view/main.fxml", 980, 640);
    }

    private static void setScene(String fxmlPath, double width, double height) throws IOException {
        FXMLLoader loader = new FXMLLoader(MainApp.class.getResource(fxmlPath));
        Parent root = loader.load();
        primaryStage.setScene(new Scene(root, width, height));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
