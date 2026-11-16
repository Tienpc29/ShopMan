package model;


/** Thực thể Product3 theo thiết kế chương 4. */
public class Product3 implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private String code;
    private String name;
    private String unit;
    private String image;
    private float importPrice;
    private float salePrice;
    private String description;
    private String category;
    private int stockQuantity;

    public Product3() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public float getImportPrice() { return importPrice; }
    public void setImportPrice(float importPrice) { this.importPrice = importPrice; }

    public float getSalePrice() { return salePrice; }
    public void setSalePrice(float salePrice) { this.salePrice = salePrice; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public int getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(int stockQuantity) { this.stockQuantity = stockQuantity; }
}
