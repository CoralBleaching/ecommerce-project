CREATE TABLE IF NOT EXISTS "User" (
    id_user SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    username VARCHAR(50) NOT NULL UNIQUE CHECK (
        username !~ '[^a-zA-Z0-9 ]'
    ), -- Alphanumeric or space, localized
    password VARCHAR(50) NOT NULL CHECK (
        LENGTH(password) >= 8 
        AND password ~ '[a-zA-Z]' -- At least 1 letter
        AND password ~ '[0-9]' -- At least 1 number
    ),
    email VARCHAR(50) NOT NULL CHECK (
        email LIKE '%@%.%'
    ),
    is_admin BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS CreditCard (
    id_user INTEGER NOT NULL,
    number VARCHAR(16) NOT NULL,
    name_on_card VARCHAR(50) NOT NULL,
    ccv VARCHAR(3) NOT NULL,
    expiry_date VARCHAR(4) NOT NULL,
    FOREIGN KEY (id_user) REFERENCES "User" (id_user) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS Country (
    id_country SERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS State (
    id_state SERIAL PRIMARY KEY,
    id_country INTEGER NOT NULL,
    name VARCHAR(50) NOT NULL,
    UNIQUE (id_country, name),
    FOREIGN KEY (id_country) REFERENCES Country (id_country)
);

CREATE TABLE IF NOT EXISTS City (
    id_city SERIAL PRIMARY KEY,
    id_state INTEGER NOT NULL,
    name VARCHAR(50) NOT NULL,
    UNIQUE (id_state, name),
    FOREIGN KEY (id_state) REFERENCES State (id_state)
);

CREATE TABLE IF NOT EXISTS Address (
    id_address SERIAL PRIMARY KEY,
    id_user INTEGER NOT NULL,
    id_city INTEGER NOT NULL,
    street VARCHAR(50) NOT NULL,
    number VARCHAR(50),
    zipcode VARCHAR(50) NOT NULL,
    district VARCHAR(50),
    label VARCHAR(50),
    FOREIGN KEY (id_user) REFERENCES "User" (id_user) ON DELETE CASCADE,
    FOREIGN KEY (id_city) REFERENCES City (id_city)
);

CREATE TABLE IF NOT EXISTS Sale (
    id_sale SERIAL PRIMARY KEY,
    id_user INTEGER NOT NULL,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_user) REFERENCES "User" (id_user) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS Category (
    id_category SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS Subcategory (
    id_subcategory SERIAL PRIMARY KEY,
    id_category INTEGER NOT NULL,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    FOREIGN KEY (id_category) REFERENCES Category (id_category) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS Product (
    id_product SERIAL PRIMARY KEY,
    id_subcategory INTEGER NOT NULL,
    id_picture INTEGER NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    stock INTEGER NOT NULL,
    hotness INTEGER NOT NULL CHECK (hotness >= 1 AND hotness <= 5),
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_subcategory) REFERENCES Subcategory (id_subcategory)
);

CREATE TABLE IF NOT EXISTS Price (
    id_price SERIAL PRIMARY KEY,
    id_product INTEGER NOT NULL,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    value FLOAT NOT NULL,
    FOREIGN KEY (id_product) REFERENCES Product (id_product) ON DELETE CASCADE,
    CONSTRAINT unique_product_timestamp UNIQUE (id_product, timestamp)
);

CREATE TABLE IF NOT EXISTS Sold (
    id_sale INTEGER NOT NULL,
    id_price INTEGER NOT NULL,
    quantity INTEGER NOT NULL,
    FOREIGN KEY (id_sale) REFERENCES Sale (id_sale) ON DELETE CASCADE,
    FOREIGN KEY (id_price) REFERENCES Price (id_price)
);

CREATE TABLE IF NOT EXISTS Evaluation (
    id_product INTEGER NOT NULL,
    id_sale INTEGER NOT NULL,
    timestamp TIMESTAMP NOT NULL,
    review TEXT,
    score FLOAT NOT NULL CHECK (score >= 1 AND score <= 5),
    FOREIGN KEY (id_product) REFERENCES Product (id_product) ON DELETE CASCADE,
    FOREIGN KEY (id_sale) REFERENCES Sale (id_sale) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS Picture (
    id_picture SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    data BYTEA NOT NULL
);

CREATE TABLE IF NOT EXISTS Session (
    id_session VARCHAR(255),
    app_name VARCHAR(255),
    session_data BYTEA,
    last_access_time TIMESTAMP,
    max_inactive_interval INTEGER,
    valid_session BOOLEAN,
    PRIMARY KEY (id_session, app_name)
);
