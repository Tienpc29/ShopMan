package dao;

import model.*;
import java.sql.*;
import java.util.*;

public class Invoice3DAO extends DAO {
    public Invoice3DAO() { super(); }
    public Invoice3DAO(ConnectionFactory factory) { super(factory); }

    /** Bước 33–38 module đặt hàng. Giỏ chỉ là dữ liệu tạm; giá và tồn kho đọc lại từ DB. */
    public boolean createInvoice(InvoiceDetail3[] cart, int cusId) throws SQLException {
        if (cart == null || cart.length == 0) throw new IllegalArgumentException("Giỏ hàng đang trống.");
        if (cusId <= 0) throw new IllegalArgumentException("Khách hàng không hợp lệ.");
        // Gộp cùng sản phẩm và khóa theo thứ tự id để giảm nguy cơ deadlock.
        Map<Integer, Integer> quantities = new TreeMap<>();
        for (InvoiceDetail3 d : cart) {
            if (d == null || d.getProduct() == null || d.getProduct().getId() <= 0 || d.getQuantity() <= 0)
                throw new IllegalArgumentException("Chi tiết giỏ hàng không hợp lệ.");
            int id = d.getProduct().getId();
            long sum = (long) quantities.getOrDefault(id, 0) + d.getQuantity();
            if (sum > Integer.MAX_VALUE) throw new IllegalArgumentException("Số lượng mua quá lớn.");
            quantities.put(id, (int) sum);
        }

        try (Connection con = openConnection()) {
            con.setAutoCommit(false);
            try {
                try (PreparedStatement ps = con.prepareStatement("SELECT tblMember3id FROM tblCustomer3 WHERE tblMember3id=?")) {
                    ps.setInt(1, cusId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) throw new IllegalArgumentException("Không tìm thấy khách hàng.");
                    }
                }
                List<InvoiceDetail3> details = new ArrayList<>();
                for (Map.Entry<Integer,Integer> item : quantities.entrySet()) {
                    Product3 p;
                    try (PreparedStatement ps = con.prepareStatement("SELECT * FROM tblProduct3 WHERE id=? FOR UPDATE")) {
                        ps.setInt(1, item.getKey());
                        try (ResultSet rs = ps.executeQuery()) {
                            if (!rs.next()) throw new IllegalArgumentException("Mặt hàng không còn tồn tại.");
                            p = Product3DAO.map(rs);
                        }
                    }
                    if (item.getValue() > p.getStockQuantity())
                        throw new IllegalArgumentException("Mặt hàng " + p.getCode() + " không đủ số lượng tồn kho.");
                    InvoiceDetail3 detail = new InvoiceDetail3();
                    detail.setProduct(p); detail.setQuantity(item.getValue()); detail.setUnitPrice(p.getSalePrice());
                    details.add(detail);
                }

                // Bước 34–37: tạo Invoice3, gắn Customer3 và mảng InvoiceDetail3.
                Customer3 customer = new Customer3(); customer.setId(cusId);
                Invoice3 invoice = new Invoice3();
                invoice.setCustomer(customer); invoice.setCreatAt(new java.util.Date());
                invoice.setInvoiceStatus("CHO_DUYET");
                invoice.setInvoiceDetails(details.toArray(new InvoiceDetail3[0]));
                // Đơn chờ duyệt: salesStaff và deliveryStaff chưa được phân công, để NULL.
                String insert = "INSERT INTO tblInvoice3(creatAt,invoiceStatus,tblCustomer3tblMember3id,tblSalesStaff3id,tblDeliveryStaff3id) VALUES(?,?,?,NULL,NULL)";
                try (PreparedStatement ps = con.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setDate(1, new java.sql.Date(invoice.getCreatAt().getTime()));
                    ps.setString(2, invoice.getInvoiceStatus()); ps.setInt(3, invoice.getCustomer().getId());
                    if (ps.executeUpdate() != 1) throw new SQLException("Không lưu được hóa đơn.");
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (!rs.next()) throw new SQLException("Không lấy được id hóa đơn.");
                        invoice.setId(rs.getInt(1));
                    }
                }
                for (InvoiceDetail3 d : invoice.getInvoiceDetails()) {
                    try (PreparedStatement ps = con.prepareStatement("INSERT INTO tblInvoiceDetail3(quantity,unitPrice,tblInvoice3id,tblProduct3id) VALUES(?,?,?,?)")) {
                        ps.setInt(1, d.getQuantity()); ps.setFloat(2, d.getUnitPrice());
                        ps.setInt(3, invoice.getId()); ps.setInt(4, d.getProduct().getId());
                        if (ps.executeUpdate() != 1) throw new SQLException("Không lưu được chi tiết hóa đơn.");
                    }
                    // Quy ước triển khai: stockQuantity là số lượng còn có thể đặt; giữ hàng ngay khi đặt.
                    try (PreparedStatement ps = con.prepareStatement("UPDATE tblProduct3 SET stockQuantity=stockQuantity-? WHERE id=? AND stockQuantity>=?")) {
                        ps.setInt(1, d.getQuantity()); ps.setInt(2, d.getProduct().getId()); ps.setInt(3, d.getQuantity());
                        if (ps.executeUpdate() != 1) throw new SQLException("Không cập nhật được tồn kho.");
                    }
                }
                con.commit();
                return true;
            } catch (SQLException | RuntimeException e) {
                try { con.rollback(); } catch (SQLException rollback) { e.addSuppressed(rollback); }
                throw e;
            }
        }
    }
}
