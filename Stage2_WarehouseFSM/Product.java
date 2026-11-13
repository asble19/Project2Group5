import java.io.*;

public class Product implements Serializable {

    private static final long serialVersionUID = 1L;

    private String productID;
    private String name;
    private int quantity;
    private double price;

    public Product(String productID, String name, int quantity, double price) {
        this.productID = productID;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
    }

    public String getProductID() {
        return productID;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public String toString() {
        return "Product ID: " + productID +
               ", Name: " + name +
               ", Quantity: " + quantity +
               ", Price: $" + price;
    }

    public void reduceQuantity(int amount) {
    quantity -= amount;
    }

    public void addQuantity(int amount) {
    quantity += amount;
    }
}