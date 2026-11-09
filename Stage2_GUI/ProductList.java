import java.io.*;
import java.util.*;

public class ProductList implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<Product> products = new ArrayList<Product>();

    public void addProduct(Product product) {
        products.add(product);
    }

    public Product findProduct(String productID) {
        for (Product product : products) {
            if (product.getProductID().equals(productID)) {
                return product;
            }
        }
        return null;
    }

    public Iterator<Product> getProducts() {
        return products.iterator();
    }
}