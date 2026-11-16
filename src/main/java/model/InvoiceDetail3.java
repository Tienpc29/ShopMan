package model;


/** Thực thể InvoiceDetail3 theo thiết kế chương 4. */
public class InvoiceDetail3 implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private int quantity;
    private float unitPrice;
    private Product3 product;

    public InvoiceDetail3() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public float getUnitPrice() { return unitPrice; }
    public void setUnitPrice(float unitPrice) { this.unitPrice = unitPrice; }

    public Product3 getProduct() { return product; }
    public void setProduct(Product3 product) { this.product = product; }
}
