-- Chỉ chạy một lần trên CSDL vừa tạo. Đây là tài khoản minh họa, không dùng mật khẩu thật.
USE shopman3;
INSERT INTO tblMember3(id,username,password,name,address,email,phone,role) VALUES
(1,'quanly','pbkdf2$210000$c2hvcG1hbi1tYW5hZ2VyMw==$Kv69eCey4Wpoalj/A/ZBuomZeDlZ2B334ujjLucHeQk=','Nguyễn Văn A','Hà Nội','quanly@example.com','0900000001','MANAGER'),
(2,'khachhang','pbkdf2$210000$c2hvcG1hbi1jdXN0b21lcjM=$wkvHN/16A6sqUs2u6X51yAs9EJqXWUe2WlcysGPu5Bg=','Trần Thị B','171 Lê Lợi, Hà Đông, Hà Nội','khach@example.com','0900000002','CUSTOMER');
INSERT INTO tblEmployee3(tblMember3id,position) VALUES(1,'Quản lý');
INSERT INTO tblManager3(tblMember3id) VALUES(1);
INSERT INTO tblCustomer3(tblMember3id) VALUES(2);
INSERT INTO tblProduct3(id,code,name,unit,image,importPrice,salePrice,description,category,stockQuantity) VALUES
(1,'SP04','Sữa đậu nành Fami 200 ml','Hộp',NULL,5000,7000,'Sữa đậu nành nguyên chất đóng hộp','Sữa và sản phẩm từ sữa',500),
(2,'SP05','Mì ăn liền','Gói',NULL,3000,4000,'Mì ăn liền gói 75 g','Thực phẩm khô',100),
(3,'SP06','Nước suối 500 ml','Chai',NULL,3000,5000,'Nước uống đóng chai','Đồ uống',200);
