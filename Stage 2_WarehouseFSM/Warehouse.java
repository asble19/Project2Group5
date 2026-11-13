import java.io.*;
import java.util.*;

public class Warehouse implements Serializable {
    private static final long serialVersionUID = 1L;
    private static Warehouse warehouse;
    private ClientList clientList = new ClientList();
    private ProductList productList = new ProductList();
    private List<Waitlistitem> waitlist = new ArrayList<Waitlistitem>();
    private int clientCounter = 0;
    private int productCounter = 0;
    private List<Invoice> invoices = new ArrayList<Invoice>();

    private Warehouse() {}

    public static Warehouse instance() {
        if (warehouse == null) {
            return warehouse = new Warehouse();
        } else {
            return warehouse;
        }
    }

    public Client addClient(String name, String address) {
        clientCounter++;
        String clientID = "C" + clientCounter;
        Client client = new Client(clientID, name, address);
        clientList.addClient(client);
        return client;
    }

    public Product addProduct(String name, int qty, double price) {
        productCounter++;
        String productID = "P" + productCounter;
        Product product = new Product(productID, name, qty, price);
        productList.addProduct(product);
        return product;
    }

    public void addProductToWishlist(String clientID, String productID, int qty) {
        Client client = clientList.findClient(clientID);
        Product product = productList.findProduct(productID);
        if (client == null || product == null) {
            System.out.println("Could not add to wishlist (check client/product ID)");
            return;
        }
        client.getWishlist().addProduct(product, qty);
    }

    public void displayAllClients() {
        Iterator<Client> allClients = clientList.getClients();
        while (allClients.hasNext()) {
            System.out.println(allClients.next());
        }
    }

    public void displayAllProducts() {
        Iterator<Product> allProducts = productList.getProducts();
        while (allProducts.hasNext()) {
            System.out.println(allProducts.next());
        }
    }

    public void displayWishlist(String clientID) {
        Client client = clientList.findClient(clientID);
        if (client == null) {
            System.out.println("Client not found");
            return;
        }
        System.out.println("Wishlist for client " + clientID + ":");
        System.out.println(client.getWishlist());
    }

    public boolean save() {
        try {
            ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream("WarehouseData"));
            output.writeObject(warehouse);
            output.close();
            return true;
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return false;
        }
    }

    public static Warehouse retrieve() {
        try {
            ObjectInputStream input = new ObjectInputStream(new FileInputStream("WarehouseData"));
            warehouse = (Warehouse) input.readObject();
            input.close();
            return warehouse;
        } catch (IOException ioe) {
            return null;
        } catch (ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
            return null;
        }
    }

    public void processOrder(String clientID) {
    Client client = clientList.findClient(clientID);

    if (client == null) {
        System.out.println("Client not found.");
        return;
    }

    Wishlist wishlist = client.getWishlist();
    List<Wishlistitem> items =
            new ArrayList<Wishlistitem>(wishlist.getItems());

    double totalCost = 0.0;

    for (Wishlistitem item : items) {
        Product product = item.getProduct();
        int requested = item.getQuantity();
        int available = product.getQuantity();

        System.out.println("\nProduct: " + product.getName());
        System.out.println("Requested quantity: " + requested);
        System.out.println("Available quantity: " + available);

        int ordered = Math.min(requested, product.getQuantity());

        if (ordered > 0) {
            product.reduceQuantity(ordered);

            double cost = ordered * product.getPrice();
            totalCost += cost;

            invoices.add(new Invoice(
                client.getClientID(),
                product.getProductID(),
                product.getName(),
                ordered,
                cost
            ));

            System.out.println("Ordered " + ordered
                    + " unit(s) of " + product.getName());
            System.out.printf("Item cost: $%.2f%n", cost);
            }

        int unfilled = requested - ordered;

        if (unfilled > 0) {
            addToWaitlist(client, product, unfilled);
            System.out.println(unfilled
                    + " unit(s) added to the waitlist.");
            }

        wishlist.removeProduct(product.getProductID());
        }

    client.debit(totalCost);

    System.out.printf("Order processed. Total cost: $%.2f%n", totalCost);
    System.out.printf("Updated client balance: $%.2f%n",
            client.getBalance());
    }

    private void addToWaitlist(Client client, Product product, int quantity) {
    for (Waitlistitem item : waitlist) {
        if (item.getClient().getClientID().equals(client.getClientID())
                && item.getProduct().getProductID()
                        .equals(product.getProductID())) {

            item.setQuantity(item.getQuantity() + quantity);
            return;
            }
        }

    waitlist.add(new Waitlistitem(client, product, quantity));
    }

    public void receiveShipment(String productID, int shipmentQuantity) {
    Product product = productList.findProduct(productID);

    if (product == null) {
        System.out.println("Product not found.");
        return;
    }

    if (shipmentQuantity <= 0) {
        System.out.println("Shipment quantity must be greater than zero.");
        return;
    }

    int remaining = shipmentQuantity;

    Iterator<Waitlistitem> iterator = waitlist.iterator();

    while (iterator.hasNext() && remaining > 0) {
        Waitlistitem item = iterator.next();

        if (item.getProduct().getProductID().equals(productID)) {
            int fulfilled = Math.min(remaining, item.getQuantity());

            double cost = fulfilled * product.getPrice();

        invoices.add(new Invoice(
                item.getClient().getClientID(),
                product.getProductID(),
                product.getName(),
                fulfilled,
                cost
            ));

        item.getClient().debit(cost);

            product.reduceQuantity(0); // No stock change here; shipment is allocated directly.

            System.out.println("Shipment fulfilled " + fulfilled
                    + " unit(s) for client "
                    + item.getClient().getClientID()
                    + " of product " + productID);

            remaining -= fulfilled;
            int stillWaiting = item.getQuantity() - fulfilled;

            if (stillWaiting == 0) {
                iterator.remove();
            } else {
                item.setQuantity(stillWaiting);
                }
            }
        }

    if (remaining > 0) {
        product.addQuantity(remaining);
        System.out.println(remaining + " unit(s) added to stock.");
        }

    System.out.println("Shipment received for " + productID + ".");
    }

    public void displayInvoices(String clientID) {
    boolean found = false;

        for (Invoice invoice : invoices) {
            if (invoice.getClientID().equals(clientID)) {
                System.out.println(invoice);
                found = true;
                }
            }

        if (!found) {
            System.out.println("No invoices found for client " + clientID);
            }
        }

        public void displayWaitlist(String productID) {
        boolean found = false;

        for (Waitlistitem item : waitlist) {
            if (item.getProduct().getProductID().equals(productID)) {
                System.out.println(item);
                found = true;
            }
        }

        if (!found) {
            System.out.println("No waitlist entries for product " + productID);
        }
    }

    public void receivePayment(String clientID, double amount) {
        Client client = clientList.findClient(clientID);

        if (client == null) {
            System.out.println("Client not found.");
            return;
        }

        if (amount <= 0) {
            System.out.println("Payment must be greater than zero.");
            return;
        }

        client.credit(amount);
        System.out.printf("Payment of $%.2f recorded for %s.%n", amount, clientID);
        System.out.printf("Updated client balance: $%.2f%n", client.getBalance());
    }
}