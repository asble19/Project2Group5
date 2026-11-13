import java.io.*;

public class Client implements Serializable {

    private static final long serialVersionUID = 1L;

    private String clientID;
    private String name;
    private String address;
    private Wishlist wishlist;
    private double balance;

    public Client(String clientID, String name, String address) {
        this.clientID = clientID;
        this.name = name;
        this.address = address;
        this.wishlist = new Wishlist();
        this.balance = 0.0;
        
    }

    public String getClientID() {
        return clientID;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public Wishlist getWishlist(){
        return wishlist;
    }

    public double getBalance(){
        return balance;
    }

    @Override
    public String toString() {
        return "Client ID: " + clientID +
               ", Name: " + name +
               ", Address: " + address +
               ", Balance: $" + String.format("%.2f", balance);    }

    public void debit(double amount) {
    balance -= amount;
    }

    public void credit(double amount) {
    balance += amount;
    }
}