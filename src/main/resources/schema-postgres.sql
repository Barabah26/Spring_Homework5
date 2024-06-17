DROP TABLE IF EXISTS customer_employer;
DROP TABLE IF EXISTS accounts;
DROP TABLE IF EXISTS customers;
DROP TABLE IF EXISTS employers;

CREATE TABLE customers
(
    id                 SERIAL PRIMARY KEY,
    name               VARCHAR(250) NOT NULL,
    email              VARCHAR(250) NOT NULL,
    age                INT          NOT NULL,
    password           VARCHAR(250) NOT NULL,
    phone_number       VARCHAR(250) NOT NULL,
    creation_date      TIMESTAMP NULL,
    last_modified_date TIMESTAMP NULL
);

CREATE TABLE employers
(
    id      SERIAL PRIMARY KEY,
    name    VARCHAR(250) NOT NULL,
    address VARCHAR(250) NOT NULL,
    creation_date      TIMESTAMP NULL,
    last_modified_date TIMESTAMP NULL
);

CREATE TABLE accounts
(
    id          SERIAL PRIMARY KEY,
    number      VARCHAR(36) NOT NULL,
    currency    VARCHAR(10) NOT NULL,
    balance     NUMERIC     NOT NULL,
    creation_date      TIMESTAMP NULL,
    last_modified_date TIMESTAMP NULL,
    customer_id BIGINT,
    CONSTRAINT fk_customer FOREIGN KEY (customer_id) REFERENCES customers (id) ON DELETE CASCADE
);

CREATE TABLE EMPLOYERS_CUSTOMERS
(
    customer_id INT,
    employer_id INT,
    PRIMARY KEY (customer_id, employer_id),
    CONSTRAINT fk_customer_employer_customer FOREIGN KEY (customer_id) REFERENCES customers (id) ON DELETE CASCADE,
    CONSTRAINT fk_customer_employer_employer FOREIGN KEY (employer_id) REFERENCES employers (id) ON DELETE CASCADE
);

CREATE TABLE users (
                       user_id INT AUTO_INCREMENT PRIMARY KEY,
                       user_name VARCHAR(36) NOT NULL,
                       encrypted_password VARCHAR(128) NOT NULL,
                       enabled BOOLEAN NOT NULL
);

CREATE TABLE roles (
                       role_id INT AUTO_INCREMENT PRIMARY KEY,
                       role_name VARCHAR(30),
                       user_id INT,
                       FOREIGN KEY (user_id) REFERENCES users(user_id)
);

