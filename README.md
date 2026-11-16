# ShopMan — đề số 33

Mã nguồn chương 5 theo bản thiết kế `33B23DCCN823.docx` và các tên đã thống nhất trong cuộc trao đổi.

**Đã cài đặt:** thêm thông tin mặt hàng; đặt mua hàng trực tuyến; đăng nhập/đăng xuất phục vụ hai module. Giao diện dành cho máy tính, tông đen trắng, biểu mẫu và bảng đơn giản.

**Phạm vi:** đăng nhập/đăng xuất; quản lý xem danh sách và thêm mặt hàng; khách hàng tìm mặt hàng, xem chi tiết, thêm/xóa khỏi giỏ và đặt hàng trực tuyến. Mô hình dữ liệu gồm 9 lớp thực thể và 9 bảng; các tham chiếu nhân viên bán/giao hàng của hóa đơn được giữ theo thiết kế, chưa có chức năng phân công hoặc xử lý đơn.

## Chạy trên máy

1. Cài JDK 17 hoặc 21, Maven 3.9+, MySQL 8.x và Apache Tomcat **9.0.x**.
2. Chạy `database/01_schema.sql`, rồi `database/02_seed.sql` trên một CSDL mới.
3. Sửa `src/main/resources/db.properties` theo tài khoản MySQL trên máy.
4. Trong thư mục có `pom.xml`, chạy `mvn clean verify`.
5. Chép `target/ShopMan.war` vào thư mục `webapps` của Tomcat 9 và khởi động Tomcat.
6. Mở <http://localhost:8080/ShopMan/>.

| Vai trò | Tên đăng nhập mẫu | Mật khẩu mẫu |
|---|---|---|
| Quản lý | `quanly` | `Quanly@123` |
| Khách hàng | `khachhang` | `Khach@123` |

Hai tài khoản được tạo trong SQL mẫu, mật khẩu lưu bằng PBKDF2. Cổng MySQL mặc định là `3306`; nếu dùng cổng khác, sửa `db.url`.

Hướng dẫn từng bước và nội dung viết báo cáo nằm trong [docs/CHUONG_5_CAI_DAT.md](docs/CHUONG_5_CAI_DAT.md). Bảng đối chiếu từng bước tuần tự nằm trong [docs/DOI_CHIEU_TUAN_TU.md](docs/DOI_CHIEU_TUAN_TU.md).

## Tên đã chốt

- Thực thể: `Product3`, `Invoice3`, `InvoiceDetail3`, `Member3`, …
- DAO: tên thực thể cộng `DAO`, ví dụ `Product3DAO`, `Invoice3DAO`. Lớp kết nối chung tên `DAO`.
- JSP dùng đúng hoa/thường trong mã nguồn: `gdChinhQL.jsp`, `gdQuanLySP.jsp`, `gdThemSP.jsp`, `doThemSP.jsp`, `gdChinhKH.jsp`, `gdDatHang.jsp`, `gdTimSP.jsp`, `gdChiTietSP.jsp`, `gdGioHang.jsp`, `doLuuDonHang.jsp`.
- Kiểm tra trùng khi thêm hàng: `checkExists(code)`; chọn hàng và liên kết đơn: `getProductById(id)`.
- Giữ tên `creatAt` đúng sơ đồ hiện có. Cột FK khách hàng dùng `tblCustomer3tblMember3id` đúng hình CSDL.

## Quy ước triển khai cần thể hiện trong báo cáo

`stockQuantity` biểu diễn số lượng còn có thể đặt. `createInvoice(cart, cusId)` kiểm tra giá/tồn kho tại CSDL, lưu đơn `CHO_DUYET`, lưu chi tiết và trừ tồn kho trong cùng transaction. Tài liệu chưa chỉ rõ thời điểm giữ hàng; đây là lựa chọn triển khai của bộ code này. Khi bổ sung nghiệp vụ hủy/từ chối đơn, cần hoàn lại số lượng đã giữ đúng một lần.

Không cần `InvoiceDetail3DAO` riêng cho hai module: sơ đồ yêu cầu `Invoice3DAO.createInvoice(...)` chịu trách nhiệm lưu cả đơn và chi tiết. Không tạo các DAO trống chỉ để đủ tên.

## Kiểm thử

`mvn test` chạy 13 ca DAO và 4 ca web. Bộ kiểm thử dùng H2 ở chế độ MySQL và Tomcat 9 nhúng; H2 chỉ là dependency kiểm thử và không nằm trong WAR chạy thật. Các ca web biên dịch JSP thật và thực hiện cả hai luồng qua HTTP, gồm tải/đọc ảnh.

Kết quả đã chạy trong môi trường tạo mã nguồn nằm ở `docs/KET_QUA_KIEM_THU.md`. Kết nối MySQL trên máy của bạn cần được kiểm tra sau khi sửa cấu hình; không coi việc test H2 là bằng chứng đã chạy trên MySQL của bạn.

## Tài liệu nền tảng

- [Apache Tomcat 9 — các đặc tả Servlet/JSP](https://tomcat.apache.org/migration-9.html)
- [MySQL Connector/J — khai báo Maven](https://dev.mysql.com/doc/connector-j/en/connector-j-installing-maven.html)

Các phiên bản được ghim trong `pom.xml` để việc build có thể lặp lại; không cần đổi sang phiên bản mới hơn để làm bài.
