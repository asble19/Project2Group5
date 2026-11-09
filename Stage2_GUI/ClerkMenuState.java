import javax.swing.*;
import java.awt.*;

public class ClerkMenuState extends UIState {

    public ClerkMenuState(Context context) {
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
        JLabel title = new JLabel("CLERK MENU");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Replaces the numbered menu options with buttons.
        JButton addClientButton = new JButton("Add Client");
        JButton productsButton = new JButton("Display All Products");
        JButton clientsButton = new JButton("Display All Clients");
        JButton balanceButton =
            new JButton("Display Clients with Outstanding Balance");
        JButton paymentButton = new JButton("Record Payment");
        JButton becomeClientButton = new JButton("Become Client");
        JButton logoutButton = new JButton("Logout");

        // Each button has its own action listener.
        addClientButton.addActionListener(e -> addClient());

        productsButton.addActionListener(e ->
            showWarehouseOutput(() ->
                context.getWarehouse().displayAllProducts()
            )
        );

        clientsButton.addActionListener(e -> displayAllClients());

        balanceButton.addActionListener(e -> displayClientsWithBalance());

        paymentButton.addActionListener(e -> recordPayment());

        becomeClientButton.addActionListener(e -> becomeClient());

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

        menu.add(addClientButton);
        menu.add(Box.createVerticalStrut(10));

        menu.add(productsButton);
        menu.add(Box.createVerticalStrut(10));

        menu.add(clientsButton);
        menu.add(Box.createVerticalStrut(10));

        menu.add(balanceButton);
        menu.add(Box.createVerticalStrut(10));

        menu.add(paymentButton);
        menu.add(Box.createVerticalStrut(10));

        menu.add(becomeClientButton);
        menu.add(Box.createVerticalStrut(10));

        menu.add(logoutButton);

        panel.add(menu);

        // Returns the completed GUI panel to Context.
        return panel;
    }

    // Replaces Scanner input with GUI input dialogs.
    // Adds the client through the existing Warehouse method and registers the client with Context.
    private void addClient() {

        String name = JOptionPane.showInputDialog(
            context.getFrame(),
            "Enter client name:"
        );

        if (name == null) {
            return;
        }

        String address = JOptionPane.showInputDialog(
            context.getFrame(),
            "Enter client address:"
        );

        if (address == null) {
            return;
        }

        if (name.trim().isEmpty() || address.trim().isEmpty()) {
            JOptionPane.showMessageDialog(
                context.getFrame(),
                "Name and address cannot be empty."
            );
            return;
        }

        Client client = context.getWarehouse().addClient(
            name.trim(),
            address.trim()
        );

        // Registers the new client so the FSM can validate IDs.
        context.registerClient(client);

        // Displays the new client in a GUI dialog.
        JOptionPane.showMessageDialog(
            context.getFrame(),
            "Client successfully added:\n" + client,
            "CLIENT ADDED",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    // Displays all clients created during this session.
    // Uses a GUI text area instead of System.out.println().
    private void displayAllClients() {

        StringBuilder output = new StringBuilder();

        if (context.getKnownClients().isEmpty()) {
            output.append("No clients found.");
        } else {
            for (Client client : context.getKnownClients()) {
                output.append(client).append("\n\n");
            }
        }

        showTextOutput("ALL CLIENTS", output.toString());
    }

    // Finds clients with a negative balance.
    // Displays the results in a GUI dialog.
    private void displayClientsWithBalance() {

        StringBuilder output = new StringBuilder();
        boolean found = false;

        for (Client client : context.getKnownClients()) {

            // Purchases subtract from the client's balance.
            if (client.getBalance() < 0) {
                output.append(client).append("\n\n");
                found = true;
            }
        }

        if (!found) {
            output.append("No clients have an outstanding balance.");
        }

        showTextOutput(
            "CLIENTS WITH OUTSTANDING BALANCE",
            output.toString()
        );
    }

    // Replaces Scanner input with GUI input dialogs.
    // Validates the client ID and payment amount.
    private void recordPayment() {

        String clientID = JOptionPane.showInputDialog(
            context.getFrame(),
            "Enter Client ID:"
        );

        if (clientID == null) {
            return;
        }

        //clientID = clientID.trim();
        final String enteredClientID = clientID.trim();

        Client client = context.findClient(clientID);

        if (client == null) {
            JOptionPane.showMessageDialog(
                context.getFrame(),
                "Invalid Client ID."
            );
            return;
        }

        String input = JOptionPane.showInputDialog(
            context.getFrame(),
            "Enter payment amount:"
        );

        if (input == null) {
            return;
        }

        try {
            double amount = Double.parseDouble(input.trim());

            if (amount <= 0) {
                JOptionPane.showMessageDialog(
                    context.getFrame(),
                    "Payment amount must be greater than zero."
                );
                return;
            }

            // Uses the existing Warehouse payment method.
            showWarehouseOutput(() ->
                context.getWarehouse().receivePayment(clientID, amount)
            );

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(
                context.getFrame(),
                "Please enter a valid payment amount."
            );
        }
    }

    // Replaces Scanner input with a GUI input dialog.
    // Validates the client ID and uses the existing BECOME_CLIENT transition in the FSM.
    private void becomeClient() {

        String clientID = JOptionPane.showInputDialog(
            context.getFrame(),
            "Enter Client ID:"
        );

        if (clientID == null) {
            return;
        }

        clientID = clientID.trim();

        if (context.findClient(clientID) == null) {
            JOptionPane.showMessageDialog(
                context.getFrame(),
                "Invalid Client ID."
            );
            return;
        }

        context.setClientID(clientID);
        context.changeState(Context.BECOME_CLIENT);
    }

    // Displays output from existing Warehouse methods that print information using System.out.println().
    private void showWarehouseOutput(Runnable action) {

        String output = context.captureOutput(action);

        showTextOutput("WAREHOUSE INFORMATION", output);
    }

    // Reusable helper for displaying text in a GUI dialog.
    private void showTextOutput(String title, String output) {

        JTextArea textArea = new JTextArea(output, 15, 40);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(textArea);

        JOptionPane.showMessageDialog(
            context.getFrame(),
            scrollPane,
            title,
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    // Identifies this state as CLERK in the FSM.
    @Override
    public int getStateIndex() {
        return Context.CLERK;
    }
}
