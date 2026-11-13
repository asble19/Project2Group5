import java.io.Serializable;

public class Invoice implements Serializable {
    private static final long serialVersionUID = 1L;

    private String clientID;
    private String productID;
    private String productName;
    private int quantity;
    private double total;

    public Invoice(String clientID, String productID,
                   String productName, int quantity, double total) {
        this.clientID = clientID;
        this.productID = productID;
        this.productName = productName;
        this.quantity = quantity;
        this.total = total;
    }

    public String getClientID() {
        return clientID;
    }

    @Override
    public String toString() {
        return "Client: " + clientID
                + ", Product: " + productID
                + " (" + productName + ")"
                + ", Quantity: " + quantity
                + ", Total: $" + String.format("%.2f", total);
    }
}