# CHƯƠNG 5: CÀI ĐẶT HỆ THỐNG SHOPMAN

Tài liệu này hướng dẫn triển khai hai module được mô tả chi tiết trong đề số 33: thêm thông tin mặt hàng và đặt mua hàng trực tuyến. Phần tổ chức theo mục 5.1, 5.2 và 5.3 của tài liệu bài giảng. Mã nguồn có thêm đăng nhập để chạy hai module với đúng vai trò.

## 5.1. Tổ chức dự án

### 5.1.1. Công nghệ sử dụng

| Thành phần | Lựa chọn trong bộ mã nguồn |
|---|---|
| Ngôn ngữ | Java 17; có thể build và chạy bằng JDK 21 |
| Giao diện | JSP, HTML, CSS thuần, nền trắng chữ đen |
| Truy cập dữ liệu | JDBC, các lớp DAO |
| CSDL thực tế | MySQL 8.x, database `shopman3` |
| Máy chủ ứng dụng | Apache Tomcat 9.0.x, sử dụng `javax.servlet` |
| Quản lý dự án | Maven, đóng gói WAR |
| Kiểm thử | JUnit 5, H2 chế độ MySQL và Tomcat nhúng |

Tomcat 9 chạy Servlet 4.0 và JSP 2.3, phù hợp namespace `javax.servlet` trong mã nguồn. Không chọn Tomcat 10/11 cho nguyên bộ code này vì các phiên bản đó chuyển sang `jakarta.servlet`. Nguồn đối chiếu: [Apache Tomcat](https://tomcat.apache.org/migration-9.html).

### 5.1.2. Cấu trúc mã nguồn

| Thư mục/tệp | Trách nhiệm |
|---|---|
| `pom.xml` | Khai báo phiên bản Java, thư viện JDBC, Servlet và kiểm thử; cấu hình WAR |
| `src/main/java/model/` | Các lớp thực thể và quan hệ kế thừa/thành phần |
| `src/main/java/dao/DAO.java` | Mở kết nối JDBC theo cấu hình |
| `src/main/java/dao/Product3DAO.java` | Danh sách, kiểm tra mã, thêm, tìm và lấy sản phẩm |
| `src/main/java/dao/Invoice3DAO.java` | Lưu hóa đơn, chi tiết và cập nhật tồn kho trong transaction |
| `src/main/java/dao/Member3DAO.java` | Đăng nhập và đóng gói người dùng thành `Manager3` hoặc `Customer3` |
| `src/main/java/util/` | Kiểm tra dữ liệu, mã hóa HTML, mật khẩu và ảnh |
| `src/main/java/web/` | Bộ lọc phiên đăng nhập/quyền và servlet đọc ảnh sản phẩm |
| `src/main/resources/db.properties` | Thông tin kết nối CSDL |
| `src/main/webapp/` | Các trang JSP đúng tên đã chốt |
| `src/main/webapp/assets/style.css` | Giao diện đen trắng, bảng có đường kẻ, form dễ đọc |
| `src/main/webapp/WEB-INF/web.xml` | Khai báo filter, multipart cho JSP nhận ảnh, phiên đăng nhập |
| `database/01_schema.sql` | Tạo 9 bảng, khóa chính, khóa ngoại và các ràng buộc |
| `database/02_seed.sql` | Hai tài khoản và ba mặt hàng minh họa |
| `src/test/java/` | Kiểm thử DAO và luồng web |
| `docs/DOI_CHIEU_TUAN_TU.md` | Đối chiếu bước trong kịch bản với tệp và câu lệnh |

Maven dùng `src/main/java` và `src/main/webapp`, tương ứng hai vùng Java Resources và Web Content trong ví dụ Eclipse của bài giảng. Không phải đổi tên lớp để chuyển sang cấu trúc Maven.

### 5.1.3. Danh sách thực thể

| Lớp | Quan hệ hoặc dữ liệu chính |
|---|---|
| `Member3` | `id`, `username`, `password`, `name`, `address`, `email`, `phone`, `role` |
| `Customer3` | Kế thừa `Member3` |
| `Employee3` | Kế thừa `Member3`, thêm `position` |
| `Manager3`, `SalesStaff3`, `DeliveryStaff3` | Kế thừa `Employee3` |
| `Product3` | `id`, **`code`**, `name`, `unit`, `image`, `importPrice`, `salePrice`, `description`, `category`, `stockQuantity` |
| `Invoice3` | `id`, `creatAt`, `invoiceStatus`, `customer`, `salesStaff`, `deliveryStaff`, `invoiceDetails` |
| `InvoiceDetail3` | `id`, `quantity`, `unitPrice`, `product` |

Các lớp có constructor không tham số, getter và setter. Lớp con sử dụng `id` kế thừa, không khai báo thêm một `id` riêng. Giữ `float` cho các thuộc tính giá để khớp sơ đồ; phần tính tổng hiển thị sử dụng `BigDecimal`. Giữ `Date` và cách viết `creatAt` của `Invoice3` đúng tài liệu, không tự đổi sang `createdAt`.

`code` là mã nhân viên nhập như `SP04`, có ràng buộc `NOT NULL UNIQUE`. `id` là khóa chính tự sinh. Hóa đơn tham chiếu sản phẩm bằng `tblProduct3id`, vì vậy thay đổi mã hiển thị không làm thay đổi liên kết của đơn.

## Các bước cài đặt trên máy

### Bước 1. Chuẩn bị môi trường

Cài JDK 17 hoặc 21, Maven 3.9+, MySQL Server 8.x, MySQL Workbench và Tomcat 9.0.x. Có thể dùng NetBeans hoặc Eclipse để mở dự án.

Mở CMD và kiểm tra:

```bat
java -version
javac -version
mvn -version
```

Nếu không nhận `mvn`, bổ sung thư mục `bin` của Maven vào PATH rồi mở CMD mới. Nếu máy đã có Maven tích hợp trong IDE, có thể dùng Clean and Build thay lệnh Maven trong CMD.

### Bước 2. Tạo CSDL

Mở MySQL Workbench, kết nối MySQL đang chạy. Mở và chạy toàn bộ `database/01_schema.sql`, sau đó chạy `database/02_seed.sql` **một lần** trên CSDL vừa tạo.

```sql
USE shopman3;
SHOW TABLES;
SELECT id, code, name, stockQuantity FROM tblProduct3;
```

Kết quả ban đầu có 9 bảng và 3 mặt hàng. Seed dùng `SP04` cho sản phẩm Fami. Khi thử thêm mới phải dùng mã chưa tồn tại, chẳng hạn `SP07`; nhập `SP04` dùng để kiểm tra nhánh trùng mã.

SQL không có `DROP DATABASE`. Nếu đã có một CSDL ShopMan cũ, sao lưu và đối chiếu cấu trúc trước khi dùng. `CREATE TABLE IF NOT EXISTS` không tự bổ sung cột vào bảng cũ; cách dễ tái hiện bộ demo là dùng một CSDL mới và chỉnh tên trong script/cấu hình đồng nhất. Không chạy lại seed trên dữ liệu đang sử dụng.

Tên khóa ngoại được lấy theo hình CSDL: các bảng kế thừa dùng `tblMember3id`; hóa đơn dùng `tblCustomer3tblMember3id`. Bảng chi tiết hóa đơn giữ `tblInvoice3id`, `tblProduct3id` đúng hình. Cấu trúc hiện tại có 9 bảng phục vụ mô hình tài khoản, mặt hàng và đơn hàng.

### Bước 3. Cấu hình JDBC

Mở `src/main/resources/db.properties`:

```properties
db.driver=com.mysql.cj.jdbc.Driver
db.url=jdbc:mysql://localhost:3306/shopman3?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Ho_Chi_Minh&characterEncoding=UTF-8
db.user=root
db.password=
```

Điền mật khẩu của tài khoản MySQL trên máy; nếu MySQL dùng cổng 3307 thì đổi `3306` thành `3307`. Đây là mật khẩu MySQL, không phải mật khẩu đăng nhập ShopMan.

Có thể ghi đè bằng biến môi trường `SHOPMAN_DB_URL`, `SHOPMAN_DB_USER`, `SHOPMAN_DB_PASSWORD`, `SHOPMAN_DB_DRIVER` trước khi khởi động Tomcat. Giá trị `-Ddb.*` của JVM có ưu tiên cao nhất, tiếp đến biến môi trường và cuối cùng `db.properties`. H2 không dùng trong cấu hình chạy thực tế.

### Bước 4. Mở trong NetBeans và build

1. Giải nén, giữ nguyên thư mục `ShopMan` có `pom.xml`.
2. Chọn **File → Open Project**, chọn chính thư mục `ShopMan`.
3. Chờ Maven tải dependency. Không tạo một Java Application thường rồi chép JSP vào.
4. Chọn JDK 17 hoặc 21 cho dự án.
5. Chọn **Clean and Build**, hoặc chạy CMD tại thư mục dự án:

```bat
mvn clean verify
```

Maven tự thêm MySQL Connector/J theo `pom.xml`; không cần chép thêm một phiên bản JDBC khác vào Libraries. File đóng gói nằm tại `target/ShopMan.war`. Các dependency Tomcat nhúng, H2 và JUnit chỉ dùng để test, không được đưa vào WAR.

### Bước 5. Triển khai Tomcat

Trong NetBeans, vào **Services → Servers → Add Server → Apache Tomcat** và chọn thư mục Tomcat 9. Trong Properties của dự án, phần Run chọn Tomcat vừa thêm, context path `/ShopMan`, sau đó Run. Tên mục có thể khác đôi chút giữa các bản NetBeans.

Nếu NetBeans chưa có hỗ trợ máy chủ web, triển khai bằng WAR:

1. Dừng Tomcat nếu đang chạy bản ShopMan cũ.
2. Chép `target/ShopMan.war` vào `<thu_muc_Tomcat>\webapps\`.
3. Mở CMD tại thư mục `bin` của Tomcat và chạy:

```bat
catalina.bat run
```

4. Mở <http://localhost:8080/ShopMan/>. Nếu cấu hình Tomcat dùng cổng khác thì thay `8080` bằng cổng đó.

Mỗi lần sửa Java, cấu hình hoặc JSP, nên build lại và triển khai lại WAR để chắc chắn máy chủ dùng đúng bản. Khi thay WAR thủ công, dừng server và xóa riêng thư mục giải nén cũ `webapps/ShopMan` nếu Tomcat không tự cập nhật; không xóa các ứng dụng khác.

### Bước 6. Chạy thử đúng hai vai trò

| Vai trò | Tài khoản | Mật khẩu minh họa | Đường đi |
|---|---|---|---|
| Quản lý | `quanly` | `Quanly@123` | Trang chủ → quản lý mặt hàng → thêm mặt hàng |
| Khách hàng | `khachhang` | `Khach@123` | Trang chủ → đặt mua → tìm → chi tiết → giỏ → đặt hàng |

Đăng xuất trước khi đổi vai trò; hoặc dùng hai cửa sổ trình duyệt riêng. Tài khoản quản lý không được mở trực tiếp trang đặt hàng dành cho khách và ngược lại.

Ảnh sản phẩm lưu ngoài WAR, mặc định ở thư mục `.shopman/uploads` của tài khoản đang chạy Tomcat. Có thể đặt `SHOPMAN_UPLOAD_DIR` thành đường dẫn như `C:\shopman-data\uploads` trước khi khởi động Tomcat. Ảnh được kiểm tra, đổi tên và lưu lại dạng PNG; cột `image` lưu tên tệp, không lưu đường dẫn máy khách. Cần giữ nguyên thư mục này khi triển khai lại để ảnh cũ tiếp tục hiển thị.

## 5.2. Cài đặt các module

### 5.2.1. Lớp kết nối và đăng nhập hỗ trợ

`DAO.openConnection()` mở kết nối theo `db.properties`. Mỗi thao tác dùng một kết nối riêng trong `try-with-resources`, tự đóng khi kết thúc. Không dùng `static Connection` chung cho mọi khách vì hai request có thể tác động lên cùng transaction.

`gdDangNhap.jsp` gửi tên đăng nhập và mật khẩu đến `doDangNhap.jsp`. Trang xử lý gọi `Member3DAO.checkLogin(username, password)`. DAO tra cứu tên đăng nhập bằng `PreparedStatement`, kiểm tra mật khẩu PBKDF2 rồi tạo đúng `Manager3` hoặc `Customer3`. Phiên đăng nhập lưu đối tượng ở thuộc tính `member`.

`AuthFilter` kiểm tra phiên và vai trò trước các JSP. Các form POST có mã CSRF để tránh gửi yêu cầu ngoài biểu mẫu của phiên hiện tại. Đây là phần kỹ thuật hỗ trợ; các lời gọi nghiệp vụ của hai module vẫn nằm trong JSP như thiết kế.

### 5.2.2. Module thêm thông tin mặt hàng

Trình tự xử lý:

1. `gdChinhQL.jsp` mở `gdQuanLySP.jsp`.
2. `gdQuanLySP.jsp` gọi `new Product3DAO().getAllProduct()` và hiển thị mảng `Product3[]` trong bảng.
3. Nút thêm mở `gdThemSP.jsp`. Form gồm mã, tên, loại, đơn vị, giá nhập, số lượng ban đầu, giá bán, ảnh và mô tả.
4. Form POST multipart đến **`doThemSP.jsp`**.
5. Trang tạo `Product3 p`, gọi setter để đóng gói dữ liệu và kiểm tra tính hợp lệ.
6. Gọi `productDAO.checkExists(p.getCode())`. Nếu `true`, báo trùng và quay lại form, giữ các giá trị đã nhập; tệp ảnh cần chọn lại.
7. Nếu mã chưa có, xử lý ảnh rồi gọi `productDAO.addProduct(p)`.
8. DAO dùng INSERT để lưu; `id` do CSDL sinh và được gán lại vào `p`.
9. Trang thông báo thêm thành công; quản lý bấm **OK** về `gdQuanLySP.jsp` và trang gọi lại `getAllProduct()`.

Chữ ký hàm công khai:

```java
public Product3[] getAllProduct() throws SQLException;
public boolean checkExists(String code) throws SQLException;
public boolean addProduct(Product3 p) throws SQLException;
```

Các hàm vẫn có kiểu trả về và tham số đúng thiết kế. Khai báo `throws SQLException` bổ sung cơ chế báo lỗi CSDL, không thay đổi giá trị trả về thành một kiểu khác. Bước 17 kiểm tra **`code`**, không phải `id`.

### 5.2.3. Module đặt mua hàng trực tuyến

Trình tự xử lý:

1. `gdChinhKH.jsp` mở `gdDatHang.jsp`.
2. Khách chọn tìm hàng, mở `gdTimSP.jsp`.
3. Nhập tên và tìm: trang gọi `Product3DAO.searchProduct(name)`, hiển thị `Product3[]`.
4. Chọn một hàng: link chuyển **`id`** sang `gdChiTietSP.jsp`.
5. Trang gọi `Product3DAO.getProductById(id)` và hiển thị mặt hàng.
6. Khách nhập số lượng, POST lại chính `gdChiTietSP.jsp`. Trang đọc lại mặt hàng để không tin giá từ trình duyệt.
7. Tạo `InvoiceDetail3`, gán `product`, `quantity`, `unitPrice`, thêm vào `List<InvoiceDetail3>` tại `session.cart`. Cùng sản phẩm thì cộng số lượng; tổng không được vượt tồn kho đang đọc.
8. Quay lại `gdDatHang.jsp` để khách có thể tiếp tục chọn. Đến đây **chưa INSERT hóa đơn hoặc chi tiết đơn**.
9. Mở `gdGioHang.jsp` để xem hàng, thành tiền, tổng tiền và khách hàng. Nút xóa loại dòng khỏi giỏ tạm.
10. Khách bấm đặt hàng, form POST đến **`doLuuDonHang.jsp`**.
11. Trang lấy khách từ `session.member`, chuyển giỏ thành `InvoiceDetail3[]` và gọi **`Invoice3DAO.createInvoice(cart, cusId)`**.
12. DAO tạo `Invoice3`, gắn `Customer3`, ngày tạo, `CHO_DUYET`, mảng chi tiết; nhân viên bán/giao hàng để `NULL` vì chưa phân công.
13. Trong cùng transaction: khóa các sản phẩm theo thứ tự `id`, kiểm tra tồn và lấy đơn giá hiện hành, INSERT hóa đơn, INSERT các chi tiết, trừ số lượng còn có thể đặt; sau đó commit. Nếu lỗi ở bất kỳ bước nào thì rollback.
14. Thành công mới xóa giỏ và mã gửi đơn, chuyển về trang đặt hàng kèm thông báo chờ duyệt. Nếu lỗi thì giữ giỏ.

Chữ ký hàm công khai:

```java
public Product3[] searchProduct(String name) throws SQLException;
public Product3 getProductById(int id) throws SQLException;
public boolean createInvoice(InvoiceDetail3[] cart, int cusId) throws SQLException;
```

`createInvoice` trả `boolean` đúng sơ đồ. Không đổi thành `int` trả ID và không thêm lời gọi `InvoiceDetail3DAO` ở giữa. Việc lưu các bảng chi tiết nằm trong `Invoice3DAO` để bảo đảm toàn bộ đơn thuộc một transaction.

Mã CSRF và `checkoutToken` là hai mã khác nhau: CSRF xác nhận form thuộc phiên; `checkoutToken` chống gửi lặp cùng giỏ. Đoạn lưu đơn được đồng bộ trên phiên; bấm lại yêu cầu cũ không tạo đơn thứ hai. Việc chuyển hướng sau POST tránh thao tác refresh trình duyệt gửi lại form.

### 5.2.4. Các quy ước bổ sung và nhãn cần đồng bộ chương 4

| Vị trí | Nội dung thống nhất |
|---|---|
| Tên DAO | `Product3DAO`, `Invoice3DAO`; không dùng `ProductDAO` |
| Ô DAO module thêm | Đổi nhãn còn ghi `Product3.jsp` thành `Product3DAO` |
| Lớp và bảng sản phẩm | Bổ sung `code: String` và `code VARCHAR(25) NOT NULL UNIQUE` |
| Kịch bản chữ bước 17 thêm hàng | Đổi `checkExists(id)` thành `checkExists(code)`; hình tuần tự mới đã dùng code |
| Chi tiết module đặt hàng | Thống nhất `getProductById`, đúng hoa/thường |
| DAO đơn hàng | Thống nhất `createInvoice`, sửa chỗ còn ghi `creatInvoice` |
| Tên trang | Đúng tên trong bảng thư mục và URL; thống nhất `doLuuDonHang.jsp` |
| Khóa ngoại khách hàng | Dùng `tblCustomer3tblMember3id` theo hình CSDL, sửa phần chữ nếu còn `tblCustomer3id` |
| Giữ hàng | Thêm ghi chú quy ước `stockQuantity` là số lượng có thể đặt; giữ ngay khi lưu đơn chờ duyệt |
| Kiểu thời gian | Giữ `creatAt: Date` và cột DATE theo bản thiết kế; không có giờ/phút trong cột này |

Trong sơ đồ, “đóng gói đối tượng” và “trả đối tượng” không đòi hỏi tạo một hàm có tên giống dòng mô tả đó. Trong Java, việc này được cài bằng `new Product3()`, các setter, gán biến và `return`. Tương tự, kết quả tìm kiếm trả mảng `Product3[]`; giỏ tạm dùng List để thêm/xóa thuận tiện rồi chuyển thành mảng khi gọi DAO.

Quy ước giữ hàng và các kiểm tra kỹ thuật là phần triển khai bổ sung, không phải yêu cầu được khẳng định sẵn trong đề. Khi thêm chức năng hủy/từ chối đơn sau này, phải hoàn lại tồn đúng một lần. Phạm vi giao diện gồm đăng nhập, quản lý thêm mặt hàng và khách hàng đặt hàng trực tuyến.

## 5.3. Kiểm thử đơn vị và tích hợp

Chạy:

```bat
mvn test
```

| Nhóm | Số ca | Nội dung |
|---|---:|---|
| `ShopManDAOTest` | 13 | Danh sách/ID; thêm/kiểm tra mã; mã trùng; dữ liệu sai; tìm tên/ký tự đặc biệt; đăng nhập; đơn/chi tiết/giá; gộp dòng; giỏ rỗng/số lượng sai; thiếu tồn; khách/sản phẩm không tồn tại; rollback sau lỗi chi tiết; hai đơn đồng thời |
| `ShopManWebTest` | 4 | Chặn truy cập/sai vai trò/CSRF; thêm qua multipart có ảnh và mã trùng; đặt hàng qua các JSP và chống gửi lặp; số lượng sai và đăng xuất |

Các báo cáo tự động nằm tại `target/surefire-reports/`. Bộ test xử lý SQL trên H2 ở chế độ MySQL, không giả lập DAO bằng mock; phần web dùng Tomcat 9 thực sự biên dịch JSP. Điều này kiểm tra hành vi và luồng, nhưng vẫn cần chạy lại trên MySQL của máy triển khai để xác nhận kết nối, quyền tài khoản và cấu hình charset của môi trường đó.

### Kịch bản kiểm tra thủ công khi báo cáo

1. Đăng nhập quản lý, mở danh sách; thêm `SP07` với tên, giá và số lượng hợp lệ; kiểm tra danh sách có hàng vừa thêm và ảnh hiển thị khi khách xem chi tiết.
2. Thử thêm `SP04`; hệ thống báo mã tồn tại, không xuất hiện bản ghi trùng.
3. Thử giá âm, thiếu tên hoặc chọn tệp không phải ảnh; không lưu dữ liệu không hợp lệ.
4. Đăng nhập khách, tìm `Fami`, chọn Fami `SP04`, mua 14 hộp; giỏ ban đầu có tổng 98.000 đồng.
5. Trước khi bấm đặt hàng, kiểm tra DB chưa có hóa đơn mới từ giỏ đó. Sau khi đặt, có một hóa đơn `CHO_DUYET`, một chi tiết số lượng 14 và FK đúng khách/sản phẩm; tồn Fami từ 500 xuống 486 nếu chưa có đơn thử khác.
6. Tải lại hoặc gửi lại POST đặt hàng cũ; không có hóa đơn trùng.
7. Chọn vượt tồn hoặc làm sản phẩm thiếu tồn sau khi đã thêm giỏ; hệ thống từ chối, không lưu đơn dở dang và giữ giỏ để kiểm tra lại.

Các truy vấn minh họa:

```sql
SELECT id, code, name, stockQuantity FROM tblProduct3 ORDER BY id;
SELECT id, creatAt, invoiceStatus, tblCustomer3tblMember3id FROM tblInvoice3 ORDER BY id DESC;
SELECT d.tblInvoice3id, p.code, p.name, d.quantity, d.unitPrice
FROM tblInvoiceDetail3 d JOIN tblProduct3 p ON p.id = d.tblProduct3id
ORDER BY d.tblInvoice3id DESC, d.id;
```

## Những lỗi môi trường thường gặp

| Hiện tượng | Cách xử lý |
|---|---|
| `Communications link failure` | Kiểm tra MySQL đã bật, cổng và `db.url` |
| `Access denied for user` | Sửa `db.user`/`db.password`, kiểm tra quyền trên `shopman3` |
| `Unknown column code` | Đang dùng bảng cũ; đối chiếu với `01_schema.sql`, không chỉ chạy lại CREATE IF NOT EXISTS |
| `ClassNotFoundException com.mysql.cj.jdbc.Driver` | Build bằng Maven và triển khai WAR vừa build; kiểm tra có Connector/J trong `WEB-INF/lib` |
| `javax.servlet` không tìm thấy | Chọn đúng Tomcat 9 và mở dự án Maven; không chạy bằng Java Application |
| Trang 404 | Kiểm tra context `/ShopMan`, đúng tên JSP và hoa/thường |
| Tomcat cổng 8080 đang dùng | Dừng server cũ hoặc đổi cổng Tomcat rồi đổi URL truy cập |
| Ảnh không còn sau chuyển máy | Chép cả thư mục upload cùng CSDL; ảnh không nằm bên trong WAR |

Khi viết báo cáo, dùng ảnh chụp từ phiên bản chạy trên máy sau khi cấu hình MySQL. Có thể trình bày các tệp theo thứ tự: model → DAO kết nối → Product3DAO → các JSP thêm hàng → Invoice3DAO → các JSP đặt hàng → kiểm thử. Không đưa mật khẩu MySQL thật vào ảnh hoặc nội dung báo cáo.
