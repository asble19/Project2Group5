import javax.swing.*;
import java.awt.*;

public class ClientMenuState extends UIState {

    public ClientMenuState(Context context) {
        super(context);
    }

    // Replaces og run() method.
    // Creates a GUI panel instead of printing a numbered menu.
    @Override
    public JPanel createPanel() {

        JPanel panel = new JPanel(new GridBagLayout());

        // Creates a vertical menu layout.
        JPanel menu = new JPanel();
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));

        // Replaces the console heading with a GUI label.
        JLabel title = new JLabel("CLIENT MENU");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Replaces the numbered console options with buttons.
        JButton accountButton = new JButton("Display Account Information");
        JButton productsButton = new JButton("Display All Products");
        JButton transactionsButton = new JButton("Display Transactions");
        JButton addWishlistButton = new JButton("Add Product to Wishlist");
        JButton wishlistButton = new JButton("Display Wishlist");
        JButton orderButton = new JButton("Place Order");
        JButton logoutButton = new JButton("Logout");

        // Each button has its own action listener.
        // This replaces the original switch statement.
        accountButton.addActionListener(e -> displayAccountInformation());

        productsButton.addActionListener(e ->
            showWarehouseOutput(() ->
                context.getWarehouse().displayAllProducts()
            )
        );

        transactionsButton.addActionListener(e -> displayTransactions());

        addWishlistButton.addActionListener(e -> addToWishlist());

        wishlistButton.addActionListener(e -> displayWishlist());

        orderButton.addActionListener(e -> placeOrder());

        logoutButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(
                context.getFrame(),
                "Logging out..."
            );
            context.changeState(Context.LOGOUT);
        });

        // Arranges all components vertically.
        menu.add(title);
        menu.add(Box.createVerticalStrut(20));

        menu.add(accountButton);
        menu.add(Box.createVerticalStrut(10));

        menu.add(productsButton);
        menu.add(Box.createVerticalStrut(10));

        menu.add(transactionsButton);
        menu.add(Box.createVerticalStrut(10));

        menu.add(addWishlistButton);
        menu.add(Box.createVerticalStrut(10));

        menu.add(wishlistButton);
        menu.add(Box.createVerticalStrut(10));

        menu.add(orderButton);
        menu.add(Box.createVerticalStrut(10));

        menu.add(logoutButton);

        panel.add(menu);

        // Returns the completed GUI panel to Context.
        return panel;
    }

    // Retrieves the current client's account information.
    // Displays information in a GUI dialog instead of printing it to the console.
    private void displayAccountInformation() {

        Client client = context.findClient(context.getClientID());

        if (client == null) {
            JOptionPane.showMessageDialog(
                context.getFrame(),
                "Client information could not be found."
            );
            return;
        }

        JOptionPane.showMessageDialog(
            context.getFrame(),
            client.toString(),
            "ACCOUNT INFORMATION",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    // Displays the client's transaction history.
    // Captures backend console output and displays it in a GUI dialog without modifying the backend.
    private void displayTransactions() {

        showWarehouseOutput(() ->
            context.getWarehouse().displayInvoices(context.getClientID())
        );
    }

    // Replaces Scanner input with GUI input dialogs.
    // Checks that the quantity is greater than zero and calls the same warehouse method.
    private void addToWishlist() {

        String productID = JOptionPane.showInputDialog(
            context.getFrame(),
            "Enter Product ID:"
        );

        if (productID == null) {
            return;
        }

        //productID = productID.trim();
        final String enteredProductID = productID.trim();

        String quantityInput = JOptionPane.showInputDialog(
            context.getFrame(),
            "Enter quantity:"
        );

        if (quantityInput == null) {
            return;
        }

        try {
            int quantity = Integer.parseInt(quantityInput.trim());

            if (quantity <= 0) {
                JOptionPane.showMessageDialog(
                    context.getFrame(),
                    "Quantity must be greater than zero."
                );
                return;
            }

            // Adds the requested product to the wishlist.
            showWarehouseOutput(() ->
                context.getWarehouse().addProductToWishlist(
                    context.getClientID(),
                    productID,
                    quantity
                )
            );

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(
                context.getFrame(),
                "Please enter a valid quantity."
            );
        }
    }

    // Displays the current client's wishlist.
    // Displays the backend output in a GUI dialog.
    private void displayWishlist() {

        showWarehouseOutput(() ->
            context.getWarehouse().displayWishlist(context.getClientID())
        );
    }

    // Processes an order using the client's wishlist.
    // Displays the backend output in a GUI dialog.
    private void placeOrder() {

        showWarehouseOutput(() ->
            context.getWarehouse().processOrder(context.getClientID())
        );
    }

    // Helper method for displaying output from existing warehouse methods that use System.out.println().
    private void showWarehouseOutput(Runnable action) {

        String output = context.captureOutput(action);

        JTextArea textArea = new JTextArea(output, 15, 40);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(textArea);

        JOptionPane.showMessageDialog(
            context.getFrame(),
            scrollPane,
            "WAREHOUSE INFORMATION",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    // Identifies this state as CLIENT in the FSM.
    @Override
    public int getStateIndex() {
        return Context.CLIENT;
    }
}