package support;

import java.nio.file.*;
import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;

/** Chỉ dành cho kiểm thử với H2. Chạy thực tế dùng WAR + MySQL. */
public class DemoServer {
    public static Tomcat start(int port, String url) throws Exception {
        DbFixture.configure(url);
        System.setProperty("shopman.upload.dir", Files.createTempDirectory("shopman-images-").toString());
        Tomcat tomcat = new Tomcat(); tomcat.setBaseDir(Files.createTempDirectory("shopman-tomcat-").toString());
        tomcat.setPort(port); tomcat.getConnector();
        Context context = tomcat.addWebapp("/ShopMan", Path.of("src/main/webapp").toAbsolutePath().toString());
        context.setParentClassLoader(DemoServer.class.getClassLoader());
        tomcat.start(); return tomcat;
    }
    public static void main(String[] args) throws Exception {
        Tomcat tomcat = start(18080, DbFixture.create());
        System.out.println("SHOPMAN_DEMO_READY http://localhost:18080/ShopMan/");
        tomcat.getServer().await();
    }
}
