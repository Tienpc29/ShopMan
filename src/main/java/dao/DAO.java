package dao;

import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class DAO {
    @FunctionalInterface
    public interface ConnectionFactory { Connection open() throws SQLException; }
    private final ConnectionFactory factory;

    public DAO() { this(DAO::configuredConnection); }
    protected DAO(ConnectionFactory factory) { this.factory = factory; }
    protected Connection openConnection() throws SQLException { return factory.open(); }

    private static Connection configuredConnection() throws SQLException {
        Properties p = new Properties();
        try (InputStream in = DAO.class.getResourceAsStream("/db.properties")) {
            if (in != null) p.load(in);
            Class.forName(value(p, "db.driver", "SHOPMAN_DB_DRIVER"));
            return DriverManager.getConnection(value(p, "db.url", "SHOPMAN_DB_URL"),
                    value(p, "db.user", "SHOPMAN_DB_USER"), value(p, "db.password", "SHOPMAN_DB_PASSWORD"));
        } catch (SQLException e) { throw e; }
          catch (Exception e) { throw new SQLException("Không mở được kết nối CSDL.", e); }
    }

    private static String value(Properties p, String key, String env) {
        String system = System.getProperty(key);
        if (system != null) return system;
        String external = System.getenv(env);
        return external != null ? external : p.getProperty(key, "");
    }
}
