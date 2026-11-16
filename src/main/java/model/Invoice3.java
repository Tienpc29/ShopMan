package model;

import java.util.Date;

/** Thực thể Invoice3 theo thiết kế chương 4. */
public class Invoice3 implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private Date creatAt;
    private String invoiceStatus;
    private SalesStaff3 salesStaff;
    private DeliveryStaff3 deliveryStaff;
    private InvoiceDetail3[] invoiceDetails;
    private Customer3 customer;

    public Invoice3() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Date getCreatAt() { return creatAt; }
    public void setCreatAt(Date creatAt) { this.creatAt = creatAt; }

    public String getInvoiceStatus() { return invoiceStatus; }
    public void setInvoiceStatus(String invoiceStatus) { this.invoiceStatus = invoiceStatus; }

    public SalesStaff3 getSalesStaff() { return salesStaff; }
    public void setSalesStaff(SalesStaff3 salesStaff) { this.salesStaff = salesStaff; }

    public DeliveryStaff3 getDeliveryStaff() { return deliveryStaff; }
    public void setDeliveryStaff(DeliveryStaff3 deliveryStaff) { this.deliveryStaff = deliveryStaff; }

    public InvoiceDetail3[] getInvoiceDetails() { return invoiceDetails; }
    public void setInvoiceDetails(InvoiceDetail3[] invoiceDetails) { this.invoiceDetails = invoiceDetails; }

    public Customer3 getCustomer() { return customer; }
    public void setCustomer(Customer3 customer) { this.customer = customer; }
}
