package models;
public class Model {
    private String name;
    private double price;
    private int[] outletStock; // C60–C69

    public Model(String name, double price, int[] outletStock) {
        this.name = name;
        this.price = price;
        this.outletStock = outletStock;
    }
    
    public void setStockAt(int index, int value) {
        outletStock[index] = value;
    }

    public String getName() {
        return name;
    }
    
    public double getPrice() {
        return price;
    }

    public int getStockAt(int index) {
        return outletStock[index];
    }
}