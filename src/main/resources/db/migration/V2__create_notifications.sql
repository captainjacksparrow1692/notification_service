-- ТАБЛИЦА МЕРЧАНТОВ
CREATE TABLE merchants
(
    id            BIGSERIAL PRIMARY KEY,
    name          VARCHAR(255) NOT NULL,
    email         VARCHAR(255) NOT NULL UNIQUE,
    webhook       VARCHAR(255),
    login         VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    tax_number    VARCHAR(255),
    created_at    TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- ТАБЛИЦА ТАРИФОВ (ДОРАБОТАНО)
CREATE TABLE prices
(
    id         BIGSERIAL PRIMARY KEY,

    price      BIGINT    NOT NULL CHECK (price >= 0),

    -- текущая активная цена (end_date IS NULL)
    is_active  BOOLEAN   NOT NULL DEFAULT TRUE,

    -- когда создана запись
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),

    -- когда цена начала действовать
    start_date TIMESTAMP NOT NULL DEFAULT NOW(),

    -- когда перестала действовать (NULL = активная)
    end_date   TIMESTAMP NULL
);

-- КАНАЛЫ ДОСТАВКИ (email, sms, push и т.д.)
CREATE TABLE notification_channels
(
    id    BIGSERIAL PRIMARY KEY,
    code  VARCHAR(50)  NOT NULL UNIQUE, -- sms / email / push
    title VARCHAR(100) NOT NULL
);

-- НАСТРОЙКИ КАНАЛОВ ДЛЯ КАЖДОГО МЕРЧАНТА
CREATE TABLE notification_channel_settings
(
    id          BIGSERIAL PRIMARY KEY,
    merchant_id BIGINT REFERENCES merchants (id) ON DELETE CASCADE,
    channel_id  BIGINT REFERENCES notification_channels (id) ON DELETE CASCADE,
    config      JSONB,
    UNIQUE (merchant_id, channel_id)
);

-- ШАБЛОНЫ УВЕДОМЛЕНИЙ (мерчант может создавать свои)
CREATE TABLE notification_templates
(
    id          BIGSERIAL PRIMARY KEY,
    merchant_id BIGINT REFERENCES merchants (id) ON DELETE CASCADE,
    title       VARCHAR(255) NOT NULL,
    body        TEXT         NOT NULL,
    type        VARCHAR(50),
    created_at  TIMESTAMP DEFAULT NOW(),
    updated_at  TIMESTAMP DEFAULT NOW()
);

-- ОСНОВНАЯ ТАБЛИЦА УВЕДОМЛЕНИЙ
CREATE TABLE notifications
(
    id          BIGSERIAL PRIMARY KEY,
    merchant_id BIGINT REFERENCES merchants (id) ON DELETE CASCADE,
    template_id BIGINT       REFERENCES notification_templates (id) ON DELETE SET NULL,
    channel_id  BIGINT       REFERENCES notification_channels (id) ON DELETE SET NULL,
    title       VARCHAR(255) NOT NULL,
    body        TEXT         NOT NULL,
    status      VARCHAR(50)  NOT NULL, -- CREATED / QUEUED / SENT / FAILED
    receiver    VARCHAR(255) NOT NULL,
    price       BIGINT       NOT NULL CHECK (price >= 0),
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- ЛОГИ ОТПРАВОК
CREATE TABLE notification_logs
(
    id              BIGSERIAL PRIMARY KEY,
    notification_id BIGINT REFERENCES notifications (id) ON DELETE CASCADE,
    status          VARCHAR(50) NOT NULL,
    response        TEXT,
    created_at      TIMESTAMP   NOT NULL DEFAULT NOW()
);

-- ИНДЕКСЫ
CREATE INDEX idx_notifications_merchant_id ON notifications (merchant_id);
CREATE INDEX idx_notifications_template_id ON notifications (template_id);
CREATE INDEX idx_notifications_channel_id ON notifications (channel_id);

CREATE INDEX idx_template_merchant_id ON notification_templates (merchant_id);

CREATE INDEX idx_logs_notification_id ON notification_logs (notification_id);

CREATE INDEX idx_channel_settings_merchant_id ON notification_channel_settings (merchant_id);
CREATE INDEX idx_channel_settings_channel_id ON notification_channel_settings (channel_id);

-- ИСТОРИИ ЦЕН — ВАЖНЫЕ ИНДЕКСЫ
CREATE INDEX idx_prices_start_date ON prices (start_date);
CREATE INDEX idx_prices_end_date ON prices (end_date);
CREATE INDEX idx_prices_is_active ON prices (is_active);
