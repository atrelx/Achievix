CREATE DATABASE IF NOT EXISTS achievix;

USE achievix;

CREATE TABLE users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       password_hash VARCHAR(255) NOT NULL,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE goals (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       user_id BIGINT NOT NULL,
                       title VARCHAR(255) NOT NULL,
                       target_value INT NOT NULL,
                       current_value INT DEFAULT 0,
                       deadline DATE,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE tasks (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       goal_id BIGINT NOT NULL,
                       title VARCHAR(255) NOT NULL,
                       completed BOOLEAN DEFAULT FALSE,
                       deadline DATE,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       FOREIGN KEY (goal_id) REFERENCES goals(id) ON DELETE CASCADE
);