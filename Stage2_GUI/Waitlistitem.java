import java.io.Serializable;

public class Waitlistitem implements Serializable {
    private static final long serialVersionUID = 1L;

    private Client client;
    private Product product;
    private int quantity;

    public Waitlistitem(Client client, Product product, int quantity) {
        this.client = client;
        this.product = product;
        this.quantity = quantity;
    }

    public Client getClient() {
        return client;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "Client: " + client.getClientID()
                + ", Product: " + product.getProductID()
                + ", Quantity: " + quantity;
    }
}
