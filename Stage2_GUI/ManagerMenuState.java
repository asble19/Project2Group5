import javax.swing.*;
import java.awt.*;

public class ManagerMenuState extends UIState {

    public ManagerMenuState(Context context) {
        super(context);
    }

    // Replaces the original run() method.
    // Creates a Swing panel instead of printing a console menu.
    @Override
    public JPanel createPanel() {

        JPanel panel = new JPanel(new GridBagLayout());

        // Creates a vertical layout for the menu.
        JPanel menu = new JPanel();
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));

        // Replaces the console heading with a GUI label.
        JLabel title = new JLabel("MANAGER MENU");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Replaces the numbered menu options with buttons.
        JButton addProductButton = new JButton("Add Product");
        JButton waitlistButton = new JButton("Display Waitlist for Product");
        JButton shipmentButton = new JButton("Receive Shipment");
        JButton becomeClerkButton = new JButton("Become Clerk");
        JButton logoutButton = new JButton("Logout");

        // Each button has its own action listener.
        // This replaces the original switch statement.
        addProductButton.addActionListener(e -> addProduct());

        waitlistButton.addActionListener(e -> displayWaitlist());

        shipmentButton.addActionListener(e -> receiveShipment());

        // Uses the existing FSM transition to Clerk mode.
        becomeClerkButton.addActionListener(e ->
            context.changeState(Context.BECOME_CLERK)
        );

        // Uses the existing FSM logout transition.
        logoutButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(
                context.getFrame(),
                "Logging out..."
            );
            context.changeState(Context.LOGOUT);
        });

        // Arranges the title and buttons vertically.
        menu.add(title);
        menu.add(Box.createVerticalStrut(20));

        menu.add(addProductButton);
        menu.add(Box.createVerticalStrut(10));

        menu.add(waitlistButton);
        menu.add(Box.createVerticalStrut(10));

        menu.add(shipmentButton);
        menu.add(Box.createVerticalStrut(10));

        menu.add(becomeClerkButton);
        menu.add(Box.createVerticalStrut(10));

        menu.add(logoutButton);

        panel.add(menu);

        // Returns the completed GUI panel to Context.
        return panel;
    }

    // Replaces Scanner input with GUI input dialogs.
    // Validates the quantity and price, then uses the existing Warehouse method to add the product.
    private void addProduct() {

        String name = JOptionPane.showInputDialog(
            context.getFrame(),
            "Enter product name:"
        );

        if (name == null) {
            return;
        }

        if (name.trim().isEmpty()) {
            JOptionPane.showMessageDialog(
                context.getFrame(),
                "Product name cannot be empty."
            );
            return;
        }

        String quantityInput = JOptionPane.showInputDialog(
            context.getFrame(),
            "Enter quantity:"
        );

        if (quantityInput == null) {
            return;
        }

        String priceInput = JOptionPane.showInputDialog(
            context.getFrame(),
            "Enter price:"
        );

        if (priceInput == null) {
            return;
        }

        try {
            int quantity = Integer.parseInt(quantityInput.trim());
            double price = Double.parseDouble(priceInput.trim());

            if (quantity < 0 || price < 0) {
                JOptionPane.showMessageDialog(
                    context.getFrame(),
                    "Quantity and price cannot be negative."
                );
                return;
            }

            // Adds the product through the Warehouse backend.
            Product product = context.getWarehouse().addProduct(
                name.trim(),
                quantity,
                price
            );

            // Displays the new product in a GUI dialog.
            JOptionPane.showMessageDialog(
                context.getFrame(),
                "Product successfully added:\n" + product,
                "PRODUCT ADDED",
                JOptionPane.INFORMATION_MESSAGE
            );

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(
                context.getFrame(),
                "Please enter valid numbers."
            );
        }
    }

    // Replaces Scanner input with a GUI input dialog.
    // Displays the waitlist using the existing Warehouse method.
    private void displayWaitlist() {

        String productID = JOptionPane.showInputDialog(
            context.getFrame(),
            "Enter Product ID:"
        );

        if (productID == null) {
            return;
        }

        final String enteredProductID = productID.trim();

        showWarehouseOutput(() ->
            context.getWarehouse().displayWaitlist(enteredProductID)
        );
    }

    // Replaces Scanner input with GUI input dialogs.
    // Validates shipment quantity and calls the same Warehouse receiveShipment() method.
    private void receiveShipment() {

        String productID = JOptionPane.showInputDialog(
            context.getFrame(),
            "Enter Product ID:"
        );

        if (productID == null) {
            return;
        }

        final String enteredProductID = productID.trim();

        String quantityInput = JOptionPane.showInputDialog(
            context.getFrame(),
            "Enter shipment quantity:"
        );

        if (quantityInput == null) {
            return;
        }

        try {
            int quantity = Integer.parseInt(quantityInput.trim());

            if (quantity <= 0) {
                JOptionPane.showMessageDialog(
                    context.getFrame(),
                    "Shipment quantity must be greater than zero."
                );
                return;
            }

            // Receives the shipment through the Warehouse backend.
            showWarehouseOutput(() ->
                context.getWarehouse().receiveShipment(
                    enteredProductID,
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

    // Captures output from existing Warehouse methods that use System.out.println(), without changing backend classes.
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

    // Identifies this state as MANAGER in the FSM.
    @Override
    public int getStateIndex() {
        return Context.MANAGER;
    }
}