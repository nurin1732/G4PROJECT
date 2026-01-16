package units;

public class SaleItem {
    private String model;
    private int quantity;
    private double unitPrice;

    public SaleItem(String model, int quantity, double unitPrice) {
        this.model = model;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public String getModel() { return model; }
    public int getQuantity() { return quantity; }
    public double getUnitPrice() { return unitPrice; }
    public double getSubtotal() { return quantity * unitPrice; }
}