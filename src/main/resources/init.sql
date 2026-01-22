-- Drop tables if they exist (in correct order)
DROP TABLE IF EXISTS Product_Order CASCADE;
DROP TABLE IF EXISTS Product_Stock CASCADE;
DROP TABLE IF EXISTS "order" CASCADE;
DROP TABLE IF EXISTS Customer CASCADE;
DROP TABLE IF EXISTS Employee CASCADE;
DROP TABLE IF EXISTS Contract CASCADE;
DROP TABLE IF EXISTS Store CASCADE;
DROP TABLE IF EXISTS Stock CASCADE;
DROP TABLE IF EXISTS Product CASCADE;

-- Customer table
CREATE TABLE Customer (
                          customer_id      SERIAL PRIMARY KEY,
                          full_name        VARCHAR(255) NOT NULL,
                          address          VARCHAR(255),
                          phone            VARCHAR(50)
);

-- Store table
CREATE TABLE Store (
                       store_id         SERIAL PRIMARY KEY,
                       address          VARCHAR(255) NOT NULL,
                       phone            VARCHAR(50)
);

-- Contract table
CREATE TABLE Contract (
                          contract_id      SERIAL PRIMARY KEY,
                          contract_type    VARCHAR(100) NOT NULL,
                          signing_date     DATE NOT NULL,
                          employee_id      INTEGER
);


-- Product table
CREATE TABLE Product (
                         product_id       SERIAL PRIMARY KEY,
                         name             VARCHAR(255) NOT NULL,
                         article              VARCHAR(100) NOT NULL,
                         price            INTEGER NOT NULL CHECK (price >= 0),
                         weight           INTEGER,
                         description      VARCHAR(1000),
                         gender           VARCHAR(20),
                         size             INTEGER
);

-- Employee table
CREATE TABLE Employee (
                          employee_id      SERIAL PRIMARY KEY,
                          full_name        VARCHAR(255) NOT NULL,
                          phone            VARCHAR(50),
                          store_id         INTEGER NOT NULL,
                          contract_id      INTEGER,
                          CONSTRAINT fk_employee_store
                              FOREIGN KEY (store_id)
                                  REFERENCES Store (store_id)
                                  ON DELETE CASCADE,
                          CONSTRAINT fk_employee_contract
                              FOREIGN KEY (contract_id)
                                  REFERENCES Contract (contract_id)
                                  ON DELETE CASCADE
);

-- Update contract foreign key on employee if needed
ALTER TABLE Contract
    ADD CONSTRAINT fk_contract_employee
        FOREIGN KEY (employee_id)
            REFERENCES Employee (employee_id)
            ON DELETE CASCADE;

-- Order table
CREATE TABLE "Order" (
                         order_id             SERIAL PRIMARY KEY,
                         order_date           DATE NOT NULL,
                         order_status         VARCHAR(50) NOT NULL,
                         total_amount         INTEGER NOT NULL CHECK (total_amount >= 0),
                         weight               INTEGER,
                         item_count           INTEGER,
                         customer_id          INTEGER NOT NULL,
                         CONSTRAINT fk_order_customer
                             FOREIGN KEY (customer_id)
                                 REFERENCES Customer (customer_id)
                                 ON DELETE CASCADE
);


-- Product_Order table (order items)
CREATE TABLE Product_Order (
                               product_id       INTEGER NOT NULL,
                               order_id         INTEGER NOT NULL,
                               quantity         INTEGER NOT NULL CHECK (quantity > 0),
                               PRIMARY KEY (product_id, order_id),
                               CONSTRAINT fk_product_order_product
                                   FOREIGN KEY (product_id)
                                       REFERENCES Product (product_id)
                                       ON DELETE CASCADE,
                               CONSTRAINT fk_product_order_order
                                   FOREIGN KEY (order_id)
                                       REFERENCES "order" (order_id)
                                       ON DELETE CASCADE
);

-------------------------------------------------------
-- Indexes for speeding up typical queries
-------------------------------------------------------

-- Search products by sku and name
CREATE UNIQUE INDEX idx_product_sku ON Product (article);
CREATE INDEX idx_product_name ON Product (name);

-- Fast access to orders by customer and date
CREATE INDEX idx_order_customer ON "order" (customer_id);
CREATE INDEX idx_order_date ON "order" (order_date);

-- Quick access to order items
CREATE INDEX idx_product_order_order ON Product_Order (order_id);
CREATE INDEX idx_product_order_product ON Product_Order (product_id);


-- Search employees by store and contract
CREATE INDEX idx_employee_store ON Employee (store_id);
CREATE INDEX idx_employee_contract ON Employee (contract_id);

-- Search contracts by employee
CREATE INDEX idx_contract_employee ON Contract (employee_id);

-- Search customers by full name
CREATE INDEX idx_customer_full_name ON Customer (full_name);

-- Search stores and stocks by address
CREATE INDEX idx_store_address ON Store (address);


CREATE TABLE Product_Store (
                               product_id  INTEGER NOT NULL,
                               store_id    INTEGER NOT NULL,
                               quantity    INTEGER NOT NULL CHECK (quantity >= 0),
                               PRIMARY KEY (product_id, store_id),
                               CONSTRAINT fk_product_store_product
                                   FOREIGN KEY (product_id) REFERENCES Product (product_id) ON DELETE CASCADE,
                               CONSTRAINT fk_product_store_store
                                   FOREIGN KEY (store_id) REFERENCES Store (store_id) ON DELETE CASCADE
);
CREATE INDEX idx_product_store_store ON Product_Store (store_id);
CREATE INDEX idx_product_store_product ON Product_Store (product_id);

CREATE TABLE Product_GlobalStock (
                                     product_id  INTEGER PRIMARY KEY,
                                     quantity    INTEGER NOT NULL CHECK (quantity >= 0),
                                     CONSTRAINT fk_product_global_product
                                         FOREIGN KEY (product_id) REFERENCES Product (product_id) ON DELETE CASCADE
);
CREATE INDEX idx_product_global_product ON Product_GlobalStock (product_id);

CREATE TABLE Category (
                          category_id   SERIAL PRIMARY KEY,
                          name          VARCHAR(255) NOT NULL,
                          parent_id     INTEGER,
                          CONSTRAINT fk_category_parent
                              FOREIGN KEY (parent_id)
                                  REFERENCES Category (category_id)
                                  ON DELETE SET NULL
);


ALTER TABLE Product
    ADD COLUMN category_id INTEGER;

ALTER TABLE Product
    ADD CONSTRAINT fk_product_category
        FOREIGN KEY (category_id)
            REFERENCES Category (category_id)
            ON DELETE SET NULL;

CREATE INDEX idx_category_name ON Category (name);
CREATE INDEX idx_product_category ON Product (category_id);
