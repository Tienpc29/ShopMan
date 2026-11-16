package util;
import model.Product3;

public final class Validation {
    private Validation() {}
    public static String text(String value, String label, int max) {
        String result = value == null ? "" : value.trim();
        if (result.isEmpty() || result.length() > max) throw new IllegalArgumentException(label + " không được trống và tối đa " + max + " ký tự.");
        return result;
    }
    public static int integer(String value, String label, int min) {
        try { int n = Integer.parseInt(value); if (n < min) throw new NumberFormatException(); return n; }
        catch (Exception e) { throw new IllegalArgumentException(label + " phải là số nguyên từ " + min + " trở lên."); }
    }
    public static float money(String value, String label) {
        try { float n = Float.parseFloat(value); if (!Float.isFinite(n) || n < 0 || n > 1_000_000_000) throw new NumberFormatException(); return n; }
        catch (Exception e) { throw new IllegalArgumentException(label + " phải là số từ 0 đến 1 tỷ."); }
    }
    public static void product(Product3 p) {
        if (p == null) throw new IllegalArgumentException("Thiếu mặt hàng.");
        p.setCode(text(p.getCode(), "Mã mặt hàng", 25));
        if (!p.getCode().matches("[A-Z0-9_-]{1,25}")) throw new IllegalArgumentException("Mã chỉ gồm chữ IN HOA, số, dấu gạch ngang hoặc gạch dưới.");
        p.setName(text(p.getName(), "Tên mặt hàng", 255));
        p.setUnit(text(p.getUnit(), "Đơn vị tính", 50)); p.setCategory(text(p.getCategory(), "Loại hàng", 255));
        if (p.getDescription() != null && p.getDescription().length() > 255) throw new IllegalArgumentException("Mô tả tối đa 255 ký tự.");
        money(String.valueOf(p.getImportPrice()), "Giá nhập"); money(String.valueOf(p.getSalePrice()), "Giá bán");
        if (p.getStockQuantity() < 0) throw new IllegalArgumentException("Số lượng không được âm.");
    }
}
