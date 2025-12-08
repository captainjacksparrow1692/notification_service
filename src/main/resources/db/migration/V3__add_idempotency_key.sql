-- Добавление колонки idempotency_key для обеспечения идемпотентности
ALTER TABLE notifications
ADD COLUMN idempotency_key VARCHAR(100);

-- Создание уникального индекса для предотвращения дублирования
CREATE UNIQUE INDEX idx_idempotency_key ON notifications(idempotency_key);

-- Комментарий к колонке
COMMENT ON COLUMN notifications.idempotency_key IS 'Уникальный ключ для обеспечения идемпотентности обработки сообщений из Kafka';
