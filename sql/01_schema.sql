-- Исходная схема БД companyy.
-- Это копия структуры, уже созданной и используемой вами в MySQL/phpMyAdmin — приведена здесь
-- для полноты проекта (например, для восстановления БД на другой машине или для Git-репозитория).
-- Если база данных companyy у вас уже создана и заполнена — этот файл можно не запускать.

CREATE DATABASE IF NOT EXISTS companyy;
USE companyy;

CREATE TABLE products (
    id_товара INT PRIMARY KEY AUTO_INCREMENT,
    Название VARCHAR(100) NOT NULL UNIQUE,
    Цена DECIMAL(10, 2) NOT NULL,
    Информация TEXT NOT NULL UNIQUE,
    Наличие_доставки BOOLEAN DEFAULT FALSE
);

CREATE TABLE delivery_methods (
    id_доставки INT PRIMARY KEY AUTO_INCREMENT,
    Тип VARCHAR(50) NOT NULL UNIQUE,
    Стоимость DECIMAL(10, 2) NOT NULL,
    Длительность VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE available_deliveries (
    id_товара INT NOT NULL,
    id_доставки INT NOT NULL,
    PRIMARY KEY (id_товара, id_доставки),
    FOREIGN KEY (id_товара) REFERENCES products(id_товара),
    FOREIGN KEY (id_доставки) REFERENCES delivery_methods(id_доставки)
);

CREATE TABLE product_deliveries (
    id_товар_доставка INT PRIMARY KEY AUTO_INCREMENT,
    id_товара INT NOT NULL,
    id_доставки INT NOT NULL,
    FOREIGN KEY (id_товара) REFERENCES products(id_товара),
    FOREIGN KEY (id_доставки) REFERENCES delivery_methods(id_доставки)
);

CREATE TABLE roles (
    id_роли INT PRIMARY KEY AUTO_INCREMENT,
    Название VARCHAR(50) NOT NULL UNIQUE,
    Уровень_доступа INT NOT NULL UNIQUE
);

CREATE TABLE customers (
    id_заказчика INT PRIMARY KEY AUTO_INCREMENT,
    Название VARCHAR(100) NOT NULL UNIQUE,
    Номер_телефона VARCHAR(20) NOT NULL UNIQUE,
    Контактное_лицо VARCHAR(100) NOT NULL,
    Адрес VARCHAR(200) NOT NULL UNIQUE,
    Логин VARCHAR(50) NOT NULL UNIQUE,
    Пароль VARCHAR(255) NOT NULL,
    id_роли INT NOT NULL,
    FOREIGN KEY (id_роли) REFERENCES roles(id_роли)
);

CREATE TABLE documents (
    id_документа INT PRIMARY KEY AUTO_INCREMENT,
    id_товар_доставка INT NOT NULL,
    количество_товара INT NOT NULL CHECK (количество_товара > 0),
    id_заказчика INT NOT NULL,
    дата_покупки DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_товар_доставка) REFERENCES product_deliveries(id_товар_доставка),
    FOREIGN KEY (id_заказчика) REFERENCES customers(id_заказчика)
);

CREATE INDEX idx_заказчики_роль ON customers(id_роли);
CREATE INDEX idx_документы_заказчик ON documents(id_заказчика);
CREATE INDEX idx_документы_товар_доставка ON documents(id_товар_доставка);
CREATE INDEX idx_товары_доставки_товар ON product_deliveries(id_товара);
CREATE INDEX idx_товары_доставки_доставка ON product_deliveries(id_доставки);
CREATE INDEX idx_доступные_доставки_товар ON available_deliveries(id_товара);
CREATE INDEX idx_доступные_доставки_доставка ON available_deliveries(id_доставки);
