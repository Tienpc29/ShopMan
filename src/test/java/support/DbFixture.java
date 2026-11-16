package support;

import dao.DAO;
import java.io.*;
import java.nio.file.*;
import java.sql.*;
import java.util.UUID;
import org.h2.tools.RunScript;

public final class DbFixture {
    public static String create() throws Exception {
        String url = "jdbc:h2:mem:shopman_" + UUID.randomUUID().toString().replace("-", "")
                + ";MODE=MySQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1;LOCK_TIMEOUT=5000";
        Class.forName("org.h2.Driver");
        String schema = Files.readString(Path.of("database/01_schema.sql"))
                .replaceAll("(?m)^CREATE DATABASE.*;", "").replace("USE shopman3;", "")
                .replace("ENGINE=InnoDB", "");
        String seed = Files.readString(Path.of("database/02_seed.sql")).replace("USE shopman3;", "");
        try (Connection con = DriverManager.getConnection(url, "sa", "")) {
            RunScript.execute(con, new StringReader(schema));
            RunScript.execute(con, new StringReader(seed));
        }
        return url;
    }
    public static DAO.ConnectionFactory factory(String url) {
        return () -> DriverManager.getConnection(url, "sa", "");
    }
    public static void configure(String url) {
        System.setProperty("db.driver", "org.h2.Driver"); System.setProperty("db.url", url);
        System.setProperty("db.user", "sa"); System.setProperty("db.password", "");
    }
    public static int scalar(String url, String sql) throws Exception {
        try (Connection con = factory(url).open(); Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            rs.next(); return rs.getInt(1);
        }
    }
}
