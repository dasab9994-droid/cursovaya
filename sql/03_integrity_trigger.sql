USE companyy;

-- Этот скрипт ОПЦИОНАЛЕН и не обязателен для работы приложения — оно само проверяет это правило
-- на уровне Java-кода (см. AvailableDeliveryDAO.exists(...) в OrdersController перед оформлением
-- заказа). Но если вы хотите продемонстрировать в задании дополнительное обеспечение целостности
-- данных НА УРОВНЕ САМОЙ БАЗЫ ДАННЫХ (а не только на уровне приложения), этот триггер не даст
-- вставить в product_deliveries запись с парой "товар + способ доставки", которая не была явно
-- разрешена администратором в таблице available_deliveries.

DELIMITER $$

CREATE TRIGGER trg_товары_доставки_check_доступность
BEFORE INSERT ON product_deliveries
FOR EACH ROW
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM available_deliveries
        WHERE id_товара = NEW.id_товара AND id_доставки = NEW.id_доставки
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Этот способ доставки не разрешён для данного товара (нет записи в available_deliveries)';
    END IF;
END$$

DELIMITER ;
