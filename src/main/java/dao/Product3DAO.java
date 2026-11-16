package dao;

import model.Product3;
import java.sql.*;
import java.util.ArrayList;

public class Product3DAO extends DAO {
    public Product3DAO() { super(); }
    public Product3DAO(ConnectionFactory factory) { super(factory); }

    // Module thêm hàng, bước 4 và 26: đọc dữ liệu, đóng gói Product3[].
    public Product3[] getAllProduct() throws SQLException {
        try (Connection con = openConnection();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM tblProduct3 ORDER BY id DESC");
             ResultSet rs = ps.executeQuery()) {
            ArrayList<Product3> products = new ArrayList<>();
            while (rs.next()) products.add(map(rs));
            return products.toArray(new Product3[0]);
        }
    }

    // Module thêm hàng, bước 17: kiểm tra mã nghiệp vụ code, không kiểm tra id tự sinh.
    public boolean checkExists(String code) throws SQLException {
        try (Connection con = openConnection();
             PreparedStatement ps = con.prepareStatement("SELECT id FROM tblProduct3 WHERE code = ?")) {
            ps.setString(1, code);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        }
    }

    // Module thêm hàng, bước 20. UNIQUE(code) vẫn bảo vệ nếu hai request cùng thêm một mã.
    public boolean addProduct(Product3 p) throws SQLException {
        util.Validation.product(p);
        String sql = "INSERT INTO tblProduct3(code,name,unit,image,importPrice,salePrice,description,category,stockQuantity) VALUES(?,?,?,?,?,?,?,?,?)";
        try (Connection con = openConnection(); PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, p.getCode()); ps.setString(2, p.getName()); ps.setString(3, p.getUnit());
            ps.setString(4, p.getImage()); ps.setFloat(5, p.getImportPrice()); ps.setFloat(6, p.getSalePrice());
            ps.setString(7, p.getDescription()); ps.setString(8, p.getCategory()); ps.setInt(9, p.getStockQuantity());
            if (ps.executeUpdate() != 1) return false;
            try (ResultSet rs = ps.getGeneratedKeys()) { if (rs.next()) p.setId(rs.getInt(1)); }
            return true;
        }
    }

    // Module đặt hàng, bước 9: tìm theo tên; ký tự % và _ của khách được tìm như ký tự thường.
    public Product3[] searchProduct(String name) throws SQLException {
        String keyword = name == null ? "" : name.trim();
        keyword = keyword.replace("!", "!!").replace("%", "!%").replace("_", "!_");
        try (Connection con = openConnection();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM tblProduct3 WHERE LOWER(name) LIKE LOWER(?) ESCAPE '!' ORDER BY name,id")) {
            ps.setString(1, "%" + keyword + "%");
            try (ResultSet rs = ps.executeQuery()) {
                ArrayList<Product3> products = new ArrayList<>();
                while (rs.next()) products.add(map(rs));
                return products.toArray(new Product3[0]);
            }
        }
    }

    // Module đặt hàng, bước 17: liên kết sản phẩm bằng id.
    public Product3 getProductById(int id) throws SQLException {
        try (Connection con = openConnection(); PreparedStatement ps = con.prepareStatement("SELECT * FROM tblProduct3 WHERE id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? map(rs) : null; }
        }
    }

    static Product3 map(ResultSet rs) throws SQLException {
        Product3 p = new Product3();
        p.setId(rs.getInt("id")); p.setCode(rs.getString("code")); p.setName(rs.getString("name"));
        p.setUnit(rs.getString("unit")); p.setImage(rs.getString("image")); p.setImportPrice(rs.getFloat("importPrice"));
        p.setSalePrice(rs.getFloat("salePrice")); p.setDescription(rs.getString("description"));
        p.setCategory(rs.getString("category")); p.setStockQuantity(rs.getInt("stockQuantity"));
        return p;
    }
}
