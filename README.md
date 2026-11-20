# ShopMan — đề số 33

Mã nguồn chương 5 theo bản thiết kế `33B23DCCN823.docx` và các tên đã thống nhất trong cuộc trao đổi.

**Đã cài đặt:** thêm thông tin mặt hàng; đặt mua hàng trực tuyến; đăng nhập/đăng xuất phục vụ hai module. Giao diện dành cho máy tính, tông đen trắng, biểu mẫu và bảng đơn giản.

**Phạm vi:** đăng nhập/đăng xuất; quản lý xem danh sách và thêm mặt hàng; khách hàng tìm mặt hàng, xem chi tiết, thêm/xóa khỏi giỏ và đặt hàng trực tuyến. Mô hình dữ liệu gồm 9 lớp thực thể và 9 bảng; các tham chiếu nhân viên bán/giao hàng của hóa đơn được giữ theo thiết kế, chưa có chức năng phân công hoặc xử lý đơn.

## Cách chạy dự án

### 1. Chuẩn bị môi trường

- JDK 17 hoặc 21.
- Maven 3.9+ và Git nếu tải mã nguồn bằng lệnh clone.
- MySQL Server 8.x đang chạy.
- Apache Tomcat **9.0.x** để chạy ứng dụng JSP/Servlet dùng `javax.servlet`.

Nếu chưa có mã nguồn trên máy:

```powershell
git clone https://github.com/Tienpc29/ShopMan.git
cd ShopMan
```

Mở thư mục `ShopMan` chứa `pom.xml` trong VS Code hoặc NetBeans.

### 2. Chuẩn bị CSDL và cấu hình kết nối

Nếu tạo CSDL mới, mở MySQL Workbench và chạy lần lượt:

1. `database/01_schema.sql`.
2. `database/02_seed.sql` để tạo tài khoản và mặt hàng mẫu.

Nếu đã có CSDL đang sử dụng thì dùng lại, không chạy lại seed.

Tạo file `src/main/resources/db.properties` với nội dung sau, rồi sửa tài khoản, mật khẩu và cổng MySQL theo máy:

```properties
db.driver=com.mysql.cj.jdbc.Driver
db.url=jdbc:mysql://localhost:3306/shopman3?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Ho_Chi_Minh&characterEncoding=UTF-8
db.user=root
db.password=MAT_KHAU_MYSQL
```

`MAT_KHAU_MYSQL` là mật khẩu kết nối MySQL, khác mật khẩu tài khoản đăng nhập ShopMan. Nếu MySQL không đặt mật khẩu thì để `db.password=`. File này được bỏ qua trong `.gitignore`, nên người clone dự án cần tự tạo và không đưa mật khẩu thật lên GitHub.

### 3. Chạy bằng VS Code và PowerShell

Trong VS Code, chọn **Terminal → New Terminal**. Chọn **PowerShell** ở menu mũi tên cạnh dấu `+`; dấu nhắc có dạng `PS ...>`. Chạy các lệnh bên dưới trong cùng terminal, tại thư mục chứa `pom.xml`.

Đặt đường dẫn JDK và build dự án; sửa đường dẫn JDK nếu máy cài ở nơi khác:

```powershell
$env:JAVA_HOME = 'C:\Program Files\Java\jdk-17'
mvn clean verify
```

Chờ xuất hiện **BUILD SUCCESS**. File ứng dụng được tạo tại `target/ShopMan.war`.

Dừng Tomcat đang chạy trong NetBeans hoặc terminal cũ trước khi khởi động lần mới. Đặt đường dẫn Tomcat, chép WAR và chạy server:

```powershell
# Thay duong dan nay bang thu muc Tomcat 9 tren may ban.
$env:CATALINA_HOME = 'D:\Apache Software Foundation\apache-tomcat-9.0.124-windows-x64\apache-tomcat-9.0.124'
$env:CATALINA_BASE = $env:CATALINA_HOME

Copy-Item '.\target\ShopMan.war' "$env:CATALINA_HOME\webapps\ShopMan.war" -Force

& "$env:CATALINA_HOME\bin\catalina.bat" run
```

`CATALINA_HOME` phải trỏ đến thư mục có các thư mục `bin`, `conf`, `webapps`, ví dụ bên trong có `bin/catalina.bat`. Các biến `$env:...` có hiệu lực trong terminal hiện tại; mở terminal mới thì cần đặt lại.

Khi server khởi động xong, mở **<http://localhost:8080/ShopMan/>**. Giữ terminal chạy server; nhấn **Ctrl+C** khi muốn dừng.

Sau khi sửa code: dừng Tomcat, chạy lại `mvn clean verify`, chép WAR và khởi động lại bằng các lệnh trên. Nhấn **Ctrl+F5** trên trình duyệt để tải giao diện mới.

### 4. Chạy bằng NetBeans

1. Chọn **File → Open Project**, mở thư mục `ShopMan` có `pom.xml`.
2. Trong **Services → Servers**, thêm Apache Tomcat 9 nếu chưa có.
3. Trong **Project Properties → Run**, chọn Tomcat 9 và context `/ShopMan`; chọn JDK 17 hoặc 21 cho dự án.
4. Chọn **Clean and Build**, sau đó **Run**. Mở <http://localhost:8080/ShopMan/> nếu trình duyệt chưa tự mở.

Cần hoàn thành cấu hình MySQL ở bước 2 trước khi đăng nhập.

### Lỗi thường gặp khi chạy

| Hiện tượng | Cách xử lý |
|---|---|
| `Copy-Item: command not found` hoặc lỗi ở ký tự `&` | Đang dùng Git Bash; chuyển terminal sang PowerShell để chạy các lệnh trên. |
| `CATALINA_HOME environment variable is not defined correctly` | Đặt `$env:CATALINA_HOME` đúng thư mục Tomcat trước khi gọi `catalina.bat`. |
| `mvn` không được nhận diện | Thêm thư mục `bin` của Maven vào PATH rồi mở lại VS Code. |
| Cổng 8080 đang được sử dụng | Dừng Tomcat cũ trước khi chạy server mới. |
| Không kết nối được CSDL | Kiểm tra MySQL đang chạy và nội dung `src/main/resources/db.properties`, sau đó build và triển khai lại WAR. |

### Tài khoản mẫu

| Vai trò    | Tên đăng nhập mẫu | Mật khẩu mẫu |
| ---------- | ----------------- | ------------ |
| Quản lý    | `quanly`          | `Quanly@123` |
| Khách hàng | `khachhang`       | `Khach@123`  |

Hai tài khoản được tạo trong SQL mẫu, mật khẩu lưu bằng PBKDF2. Cổng MySQL mặc định là `3306`; nếu dùng cổng khác, sửa `db.url`.

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
