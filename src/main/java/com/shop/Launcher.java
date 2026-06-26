package com.shop;

/**
 * Отдельный класс-запускник, который НЕ наследует javafx.application.Application.
 * Это стандартный обходной приём: если Main-Class в исполняемом fat-jar сам наследует
 * Application, JVM при запуске через "java -jar" иногда выдаёт ошибку
 * "JavaFX runtime components are missing". Через промежуточный класс этой проблемы не возникает.
 */
public class Launcher {
    public static void main(String[] args) {
        MainApp.main(args);
    }
}
