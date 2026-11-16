-- Chạy trên MySQL 8.x. Tạo mới nếu chưa có; không xóa dữ liệu cũ.
CREATE DATABASE IF NOT EXISTS shopman3 CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE shopman3;
CREATE TABLE IF NOT EXISTS tblMember3 (
 id INT AUTO_INCREMENT PRIMARY KEY, username VARCHAR(255) NOT NULL UNIQUE,
 password VARCHAR(255) NOT NULL, name VARCHAR(255) NOT NULL, address VARCHAR(255),
 email VARCHAR(255), phone VARCHAR(50), role VARCHAR(50) NOT NULL
) ENGINE=InnoDB;
CREATE TABLE IF NOT EXISTS tblCustomer3 (
 tblMember3id INT PRIMARY KEY, FOREIGN KEY(tblMember3id) REFERENCES tblMember3(id)
) ENGINE=InnoDB;
CREATE TABLE IF NOT EXISTS tblEmployee3 (
 tblMember3id INT PRIMARY KEY, position VARCHAR(255), FOREIGN KEY(tblMember3id) REFERENCES tblMember3(id)
) ENGINE=InnoDB;
CREATE TABLE IF NOT EXISTS tblManager3 (
 tblMember3id INT PRIMARY KEY, FOREIGN KEY(tblMember3id) REFERENCES tblEmployee3(tblMember3id)
) ENGINE=InnoDB;
CREATE TABLE IF NOT EXISTS tblSalesStaff3 (
 tblMember3id INT PRIMARY KEY, FOREIGN KEY(tblMember3id) REFERENCES tblEmployee3(tblMember3id)
) ENGINE=InnoDB;
CREATE TABLE IF NOT EXISTS tblDeliveryStaff3 (
 tblMember3id INT PRIMARY KEY, FOREIGN KEY(tblMember3id) REFERENCES tblEmployee3(tblMember3id)
) ENGINE=InnoDB;
CREATE TABLE IF NOT EXISTS tblProduct3 (
 id INT AUTO_INCREMENT PRIMARY KEY, code VARCHAR(25) NOT NULL UNIQUE,
 name VARCHAR(255) NOT NULL, unit VARCHAR(50) NOT NULL, image VARCHAR(255),
 importPrice FLOAT NOT NULL CHECK(importPrice >= 0), salePrice FLOAT NOT NULL CHECK(salePrice >= 0),
 description VARCHAR(255), category VARCHAR(255) NOT NULL,
 stockQuantity INT NOT NULL CHECK(stockQuantity >= 0)
) ENGINE=InnoDB;
CREATE TABLE IF NOT EXISTS tblInvoice3 (
 id INT AUTO_INCREMENT PRIMARY KEY, creatAt DATE NOT NULL, invoiceStatus VARCHAR(255) NOT NULL,
 tblSalesStaff3id INT NULL, tblDeliveryStaff3id INT NULL, tblCustomer3tblMember3id INT NOT NULL,
 FOREIGN KEY(tblSalesStaff3id) REFERENCES tblSalesStaff3(tblMember3id),
 FOREIGN KEY(tblDeliveryStaff3id) REFERENCES tblDeliveryStaff3(tblMember3id),
 FOREIGN KEY(tblCustomer3tblMember3id) REFERENCES tblCustomer3(tblMember3id)
) ENGINE=InnoDB;
CREATE TABLE IF NOT EXISTS tblInvoiceDetail3 (
 id INT AUTO_INCREMENT PRIMARY KEY, quantity INT NOT NULL CHECK(quantity > 0), unitPrice FLOAT NOT NULL CHECK(unitPrice >= 0),
 tblInvoice3id INT NOT NULL, tblProduct3id INT NOT NULL,
 FOREIGN KEY(tblInvoice3id) REFERENCES tblInvoice3(id), FOREIGN KEY(tblProduct3id) REFERENCES tblProduct3(id)
) ENGINE=InnoDB;
