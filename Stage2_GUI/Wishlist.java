import java.io.*;
import java.util.*;

public class Wishlist implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<Wishlistitem> items = new ArrayList<Wishlistitem>();

    public void addProduct(Product product, int quantity) {
        for (Wishlistitem item : items) {
            if (item.getProduct().getProductID().equals(product.getProductID())) {
                item.setQuantity(quantity);
                return;
            }
        }
        items.add(new Wishlistitem(product, quantity));
    }

    public List<Wishlistitem> getItems() {
        return items;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Wishlistitem item : items) {
            sb.append(item.toString()).append("\n");
        }
        return sb.toString();
    }

    public void removeProduct(String productID) {
    Iterator<Wishlistitem> iterator = items.iterator();

    while (iterator.hasNext()) {
        Wishlistitem item = iterator.next();

        if (item.getProduct().getProductID().equals(productID)) {
            iterator.remove();
            return;
            }
        }
    }
}