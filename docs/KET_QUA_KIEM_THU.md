# Kết quả kiểm thử bản giao

Ngày thực hiện: 14/11/2026.

## Kết quả tự động

| Bộ kiểm thử | Số ca | Thất bại | Lỗi | Bỏ qua |
|---|---:|---:|---:|---:|
| `dao.ShopManDAOTest` | 13 | 0 | 0 | 0 |
| `web.ShopManWebTest` | 4 | 0 | 0 | 0 |
| **Tổng** | **17** | **0** | **0** | **0** |

Đã chạy thành công vòng `mvn verify`: biên dịch mã Java, chạy kiểm thử, đóng gói WAR. Bản build kiểm tra dùng OpenJDK 21.0.13 và Maven 3.9.9; bytecode sản phẩm đặt target Java 17.

DAO được kiểm tra trên H2 2.3.232 ở chế độ MySQL. Web được chạy trên Tomcat 9.0.123 nhúng, JSP được biên dịch và thực hiện qua HTTP. Đã kiểm tra tải ảnh PNG qua form multipart, lưu tên ảnh vào CSDL và đọc lại ảnh qua servlet.

## Các hành vi đã xác nhận

- Các lời gọi công khai trong hai module khớp bảng đối chiếu tuần tự.
- Mã trùng bị chặn bởi kiểm tra ở DAO và ràng buộc UNIQUE ở CSDL.
- Tìm kiếm theo tên dùng tham số SQL, xử lý ký tự `%`, `_` như dữ liệu tìm kiếm thông thường.
- Đăng nhập tạo đúng `Manager3` hoặc `Customer3`; người chưa đăng nhập và sai vai trò bị chặn.
- Giỏ tạm lưu trong session; xác nhận mới lưu đơn và chi tiết.
- Đơn giá lấy lại từ CSDL, bỏ qua đơn giá giả trong đối tượng giỏ thử nghiệm.
- Đơn mới có trạng thái `CHO_DUYET`, đúng khách hàng; nhân viên bán/giao hàng chưa phân công là NULL.
- Lỗi khi lưu chi tiết làm rollback cả đơn, chi tiết đã chèn và phần tồn đã thay đổi.
- Hai yêu cầu cùng mua 4 sản phẩm khi tồn 5 chỉ có một đơn thành công, tồn còn 1.
- Gửi lặp POST xác nhận cùng giỏ không tạo đơn thứ hai.
- WAR không chứa H2, JUnit hoặc Tomcat nhúng; các thư viện này chỉ phục vụ kiểm thử.

## Rà soát giao diện bổ sung

Đã rà soát 11 trang giao diện bằng Chrome headless trên máy tính. Phạm vi giao diện sử dụng bố cục máy tính, nền trắng, chữ và nút chính đen, viền xám, bo góc nhẹ. Bảng dài có thanh cuộn ngang khi cần.

| Trang | Nội dung đã rà soát |
|---|---|
| `gdDangNhap.jsp` | Form đăng nhập và nhãn nhập liệu |
| `gdChinhQL.jsp` | Trang chủ quản lý, điều hướng theo vai trò |
| `gdChinhKH.jsp` | Trang chủ khách hàng, liên kết đặt mua |
| `gdQuanLySP.jsp` | Bảng giá và tồn kho |
| `gdThemSP.jsp` | Form hai cột, gửi kèm ảnh |
| `doThemSP.jsp` | Kết quả thêm thành công, nút OK và thêm tiếp |
| `gdDatHang.jsp` | Liên kết tìm hàng, số dòng giỏ và thông báo đặt thành công |
| `gdTimSP.jsp` | Chưa tìm, có kết quả và không có kết quả |
| `gdChiTietSP.jsp` | Có/không có ảnh, sản phẩm hết hàng, thêm vào giỏ |
| `gdGioHang.jsp` | Giỏ trống/có hàng, thông tin khách, tổng tiền và xác nhận |
| `loi.jsp` | Thông báo lỗi và liên kết về trang chủ |

## Giới hạn xác minh

Chưa kết nối đến MySQL thực tế trên máy người dùng. Cần chạy SQL, chỉnh tài khoản/cổng kết nối và làm các bước kiểm tra thủ công trong hướng dẫn trước khi chụp ảnh nộp báo cáo. Kết quả H2 không thay thế việc kiểm tra trên MySQL 8.x.

Bộ code triển khai đăng nhập/đăng xuất, quản lý thêm mặt hàng và khách hàng đặt hàng trực tuyến. Các kết quả trên chỉ xác nhận những chức năng trong phạm vi này.

## Kiểm tra sau khi thu gọn phạm vi

Mô hình hiện tại có 9 lớp thực thể và SQL tạo 9 bảng cho tài khoản, mặt hàng và đơn hàng. Ca DAO đăng nhập chuẩn bị mật khẩu `12345678` trong CSDL H2 riêng của ca đó, giữ nguyên mật khẩu tài khoản trong SQL mẫu và CSDL đang sử dụng.

Kết quả sau thu gọn: 13 ca DAO và 4 ca web đều đạt; build WAR thành công. Đã kiểm tra WAR chỉ còn 9 lớp model và bản `dist/ShopMan.war` khớp bản build mới. File SQL tạo mới đã thu gọn; không chạy lệnh xóa bảng trên CSDL MySQL hiện có.

## Ảnh sản phẩm trong tìm kiếm và giỏ hàng

Đã thêm ảnh cạnh tên mặt hàng trong từng dòng của hai bảng. Ảnh có khung 72 × 72 px và hiển thị đầy đủ bằng `object-fit: contain`. Hàng chưa có ảnh hoặc không tải được ảnh hiển thị ô “Chưa có ảnh”.

Đã kiểm tra bằng Chrome ở độ rộng 1280 px trên dữ liệu H2 riêng, gồm sản phẩm có ảnh tải lên, sản phẩm không có ảnh và đường dẫn ảnh không tồn tại. Dòng bảng cao khoảng 105 px, không có lỗi JavaScript. Phần JavaScript chỉ xử lý ảnh không tải được; bố cục dùng CSS cho máy tính.

## Bản giao diện máy tính

Đã dọn các tùy chỉnh ngoài phạm vi giao diện máy tính và đầu ra kiểm tra cũ, giữ ảnh sản phẩm 72 × 72 px cùng dòng bảng lớn. Chạy `mvn clean verify`: 17/17 ca kiểm thử đạt. Đã kiểm tra nội dung WAR và cập nhật bản `dist/ShopMan.war`.
