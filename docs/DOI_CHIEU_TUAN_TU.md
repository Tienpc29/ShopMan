# Đối chiếu tuần tự với mã nguồn

Các số bước dưới đây lấy từ phần kịch bản chữ trong bản Word đang gửi. Module thêm có 30 bước trong kịch bản, trong khi hình hiện dừng ở bước 22; phần quay về danh sách được cài thêm theo kịch bản. Module đặt hàng có 39 bước. Các lời gọi kỹ thuật như kiểm tra phiên, validate, CSRF, transaction không làm thay tên các lời gọi nghiệp vụ.

## A. Thêm thông tin mặt hàng

| Bước | Tệp | Câu lệnh hoặc cách hiện thực |
|---|---|---|
| 1–2 | `gdChinhQL.jsp` | Link `href="gdQuanLySP.jsp"` |
| 3–4 | `gdQuanLySP.jsp` | `new Product3DAO().getAllProduct()` |
| 5–6 | `Product3DAO.java` | `map(rs)` tạo `Product3`, gọi setter; `toArray(new Product3[0])` |
| 7–8 | `gdQuanLySP.jsp` | Nhận mảng và vòng `for (Product3 p : products)` để hiển thị |
| 9–10 | `gdQuanLySP.jsp` | Link `href="gdThemSP.jsp"` |
| 11–12 | `gdThemSP.jsp` | Form mã/tên/loại/đơn vị/giá/số lượng/ảnh/mô tả |
| 13 | `gdThemSP.jsp` | `action="doThemSP.jsp" method="post"` |
| 14–15 | `doThemSP.jsp` | `Product3 p = new Product3()` và các setter |
| 16–18 | `doThemSP.jsp` → `Product3DAO.java` | `productDAO.checkExists(p.getCode())`; SQL `WHERE code = ?`; trả boolean |
| 19–21 | `doThemSP.jsp` → `Product3DAO.java` | `productDAO.addProduct(p)`; INSERT, lấy ID tự sinh; trả boolean |
| 22 | `doThemSP.jsp` | Hiển thị “Thêm mặt hàng thành công” sau khi lưu |
| 23–24 | `doThemSP.jsp` | Nút/link OK về `gdQuanLySP.jsp` |
| 25–26 | `gdQuanLySP.jsp` | Gọi lại `getAllProduct()` |
| 27–29 | `Product3DAO.java` | Đọc dữ liệu đã cập nhật, đóng gói và trả `Product3[]` |
| 30 | `gdQuanLySP.jsp` | Hiển thị danh sách mới |

Nhánh lỗi: mã trùng, giá/số lượng sai, tệp ảnh sai hoặc SQL lỗi → thông báo và quay về form. UNIQUE(code) ở CSDL là lớp bảo vệ cuối cùng khi hai request cùng kiểm tra thấy mã chưa tồn tại.

## B. Đặt mua hàng trực tuyến

| Bước | Tệp | Câu lệnh hoặc cách hiện thực |
|---|---|---|
| 1–2 | `gdChinhKH.jsp` | Link `href="gdDatHang.jsp"` |
| 3 | `gdDatHang.jsp` | Hiển thị hai nút tìm hàng và xem giỏ |
| 4–5 | `gdDatHang.jsp` | Link `href="gdTimSP.jsp"` |
| 6–7 | `gdTimSP.jsp` | Form GET tự gửi về trang, tham số `name` |
| 8–9 | `gdTimSP.jsp` | `new Product3DAO().searchProduct(name)` |
| 10–12 | `Product3DAO.java` | Truy vấn LIKE theo tên, `map(rs)`, trả `Product3[]` |
| 13–15 | `gdTimSP.jsp` | Bảng kết quả; link `gdChiTietSP.jsp?id=<id>` |
| 16–17 | `gdChiTietSP.jsp` | `new Product3DAO().getProductById(id)` |
| 18–20 | `Product3DAO.java` | SELECT theo ID, `map(rs)`, trả `Product3` |
| 21–22 | `gdChiTietSP.jsp` | Thông tin sản phẩm và form số lượng, POST về chính trang |
| 23–24 | `gdChiTietSP.jsp` | `new InvoiceDetail3()`; gán `product`, `quantity`, `unitPrice` |
| 25 | `gdChiTietSP.jsp` | Thêm/gộp chi tiết trong `session.cart`, redirect `gdDatHang.jsp` |
| 26 | `gdDatHang.jsp` | Hiển thị thông báo thêm vào giỏ thành công; có thể lặp 4–26 |
| 27–28 | `gdDatHang.jsp` | Link `href="gdGioHang.jsp"`; dùng cùng session |
| 29–30 | `gdGioHang.jsp` | Hiển thị giỏ và khách; nút Đặt hàng |
| 31 | `gdGioHang.jsp` | POST `action="doLuuDonHang.jsp"` |
| 32–33 | `doLuuDonHang.jsp` | `new Invoice3DAO().createInvoice(cart.toArray(new InvoiceDetail3[0]), cus.getId())` |
| 34 | `Invoice3DAO.java` | `new Invoice3()`, gán khách/ngày/`CHO_DUYET` |
| 35–37 | `Invoice3DAO.java` | `invoice.setInvoiceDetails(...)`; INSERT đơn và từng chi tiết |
| 38 | `Invoice3DAO.java` | `con.commit(); return true;`; lỗi thì rollback và ném exception |
| 39 | `doLuuDonHang.jsp` → `gdDatHang.jsp` | Lưu thông báo vào session, redirect và hiển thị thông báo chờ duyệt |

Giỏ và ID khách không được gửi toàn bộ dưới dạng hidden field: giỏ lấy từ `session.cart`, khách lấy từ `session.member`. Form chỉ mang token và ID mặt hàng ở các bước chọn hàng. Đơn giá thực tế luôn được DAO đọc lại từ CSDL khi lưu đơn.

## Các hàm DAO dùng trong hai module

| DAO | Hàm | Kiểu trả về |
|---|---|---|
| `Product3DAO` | `getAllProduct()` | `Product3[]` |
| `Product3DAO` | `checkExists(String code)` | `boolean` |
| `Product3DAO` | `addProduct(Product3 p)` | `boolean` |
| `Product3DAO` | `searchProduct(String name)` | `Product3[]` |
| `Product3DAO` | `getProductById(int id)` | `Product3` hoặc `null` |
| `Invoice3DAO` | `createInvoice(InvoiceDetail3[] cart, int cusId)` | `boolean` |

Các constructor không tham số trùng đúng tên lớp: `Product3DAO()`, `Invoice3DAO()`. Constructor nhận `ConnectionFactory` là điểm tiêm kết nối phục vụ kiểm thử; các JSP chỉ dùng constructor không tham số.
