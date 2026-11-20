package dao;
import model.*;
import java.sql.*;
import util.Passwords;

public class Member3DAO extends DAO {
    public Member3DAO() { super(); }
    public Member3DAO(ConnectionFactory factory) { super(factory); }
    public Member3 checkLogin(String username, String password) throws SQLException {
        String sql = "SELECT m.*,c.tblMember3id AS customerId,g.tblMember3id AS managerId,e.position FROM tblMember3 m "
                + "LEFT JOIN tblCustomer3 c ON c.tblMember3id=m.id LEFT JOIN tblManager3 g ON g.tblMember3id=m.id "
                + "LEFT JOIN tblEmployee3 e ON e.tblMember3id=m.id WHERE m.username=?";
        try (Connection con = openConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next() || !Passwords.verify(password, rs.getString("password"))) return null;
                Member3 member;
                if ("MANAGER".equals(rs.getString("role")) && rs.getObject("managerId") != null) {
                    Manager3 manager = new Manager3(); manager.setPosition(rs.getString("position")); member = manager;
                } else if ("CUSTOMER".equals(rs.getString("role")) && rs.getObject("customerId") != null) member = new Customer3();
                else return null;
                member.setId(rs.getInt("id")); member.setUsername(rs.getString("username"));
                member.setName(rs.getString("name")); member.setAddress(rs.getString("address"));
                member.setEmail(rs.getString("email")); member.setPhone(rs.getString("phone")); member.setRole(rs.getString("role"));
                return member;
            }
        }
    }
}
