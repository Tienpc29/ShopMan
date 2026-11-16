package util;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.UUID;

public final class Web {
    private Web() {}
    public static String h(Object value) {
        if (value == null) return "";
        return value.toString().replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
            .replace("\"", "&quot;").replace("'", "&#39;");
    }
    public static String csrf(HttpSession session) {
        synchronized (session) {
            String token = (String) session.getAttribute("csrf");
            if (token == null) { token = UUID.randomUUID().toString(); session.setAttribute("csrf", token); }
            return token;
        }
    }
    public static String take(HttpSession session, String key) {
        Object value = session.getAttribute(key); session.removeAttribute(key);
        return value == null ? null : value.toString();
    }
    public static String money(float value) { return money(new BigDecimal(Float.toString(value))); }
    public static String money(BigDecimal value) { return NumberFormat.getNumberInstance(Locale.forLanguageTag("vi-VN")).format(value) + " đ"; }
}
