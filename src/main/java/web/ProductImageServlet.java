package web;
import javax.servlet.http.*;
import java.io.IOException;
import java.nio.file.*;
import util.Images;

/** Chỉ phục vụ các ảnh sản phẩm đã được kiểm tra và đổi tên. */
public class ProductImageServlet extends HttpServlet {
    @Override protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String path = req.getPathInfo();
        if (path == null || !path.matches("/[a-f0-9-]{36}[.]png")) { res.sendError(404); return; }
        Path file = Images.directory().resolve(path.substring(1));
        if (!Files.isRegularFile(file)) { res.sendError(404); return; }
        res.setContentType("image/png"); res.setHeader("X-Content-Type-Options", "nosniff");
        res.setContentLengthLong(Files.size(file)); Files.copy(file, res.getOutputStream());
    }
}
