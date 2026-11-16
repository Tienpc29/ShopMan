package util;
import javax.imageio.ImageIO;
import javax.servlet.http.Part;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.*;
import java.util.UUID;

public final class Images {
    private Images() {}
    public static Path directory() throws IOException {
        String configured = System.getProperty("shopman.upload.dir", System.getenv("SHOPMAN_UPLOAD_DIR"));
        Path dir = configured == null || configured.isBlank()
                ? Paths.get(System.getProperty("user.home"), ".shopman", "uploads") : Paths.get(configured);
        Files.createDirectories(dir);
        return dir;
    }
    public static String save(Part part) throws IOException {
        if (part == null || part.getSize() == 0) return null;
        if (part.getSize() > 2 * 1024 * 1024) throw new IllegalArgumentException("Ảnh tối đa 2 MB.");
        // Đọc ảnh thật rồi mã hóa lại PNG, không lưu trực tiếp tên/nội dung tệp do khách gửi.
        try (InputStream in = part.getInputStream(); javax.imageio.stream.ImageInputStream imageIn = ImageIO.createImageInputStream(in)) {
            java.util.Iterator<javax.imageio.ImageReader> readers = ImageIO.getImageReaders(imageIn);
            if (!readers.hasNext()) throw new IllegalArgumentException("Tệp phải là ảnh PNG, JPG hoặc GIF hợp lệ.");
            javax.imageio.ImageReader reader = readers.next();
            try {
                reader.setInput(imageIn);
                String format = reader.getFormatName();
                if (!format.matches("(?i)png|jpe?g|gif")) throw new IllegalArgumentException("Chỉ nhận ảnh PNG, JPG hoặc GIF.");
                if (reader.getWidth(0) > 4000 || reader.getHeight(0) > 4000)
                    throw new IllegalArgumentException("Kích thước ảnh tối đa 4000 × 4000.");
                BufferedImage image = reader.read(0);
                String filename = UUID.randomUUID() + ".png";
                Path output = directory().resolve(filename);
                if (!ImageIO.write(image, "png", output.toFile())) throw new IOException("Không lưu được ảnh.");
                return filename;
            } finally { reader.dispose(); }
        }
    }
    public static void delete(String name) {
        if (name == null || !name.matches("[a-f0-9-]{36}[.]png")) return;
        try { Files.deleteIfExists(directory().resolve(name)); } catch (IOException ignored) { }
    }
}
