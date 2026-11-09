public class ClerkMenuState extends UIState {

    public ClerkMenuState(Context context) {
        super(context);
    }

    @Override
    public void run() {

        System.out.println("\n========== CLERK MENU ==========");
        System.out.println("1. Add Client");
        System.out.println("2. Display All Products");
        System.out.println("3. Display All Clients");
        System.out.println("4. Display Clients with Outstanding Balance");
        System.out.println("5. Record Payment");
        System.out.println("6. Become Client");
        System.out.println("0. Logout");

        System.out.print("Enter choice: ");

        String input = context.getScanner().nextLine();

        try {
            int choice = Integer.parseInt(input);

            switch (choice) {

                case 1:
                    addClient();
                    break;

                case 2:
                    context.getWarehouse().displayAllProducts();
                    break;

                case 3:
                    displayAllClients();
                    break;

                case 4:
                    displayClientsWithBalance();
                    break;

                case 5:
                    recordPayment();
                    break;

                case 6:
                    becomeClient();
                    break;

                case 0:
                    System.out.println("Logging out...");
                    context.changeState(Context.LOGOUT);
                    break;

                default:
                    System.out.println("Invalid choice. Try again.");
            }

        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        }
    }

    // Add a new client
    private void addClient() {

        System.out.print("Enter client name: ");
        String name = context.getScanner().nextLine();

        System.out.print("Enter client address: ");
        String address = context.getScanner().nextLine();

        Client client = context.getWarehouse().addClient(name, address);

        // Register client so the FSM can validate client IDs
        context.registerClient(client);

        System.out.println("Client successfully added:");
        System.out.println(client);
    }

    // Display all clients created during this session
    private void displayAllClients() {

        System.out.println("\n========== ALL CLIENTS ==========");

        if (context.getKnownClients().isEmpty()) {
            System.out.println("No clients found.");
            return;
        }

        for (Client client : context.getKnownClients()) {
            System.out.println(client);
        }
    }

    // Display clients who have an outstanding balance
    private void displayClientsWithBalance() {

        System.out.println("\n===== CLIENTS WITH OUTSTANDING BALANCE =====");

        boolean found = false;

        for (Client client : context.getKnownClients()) {

            // Purchases subtract from the client's balance
            if (client.getBalance() < 0) {
                System.out.println(client);
                found = true;
            }
        }

        if (!found) {
            System.out.println("No clients have an outstanding balance.");
        }
    }

    // Record a payment from a client
    private void recordPayment() {

        System.out.print("Enter Client ID: ");
        String clientID = context.getScanner().nextLine();

        Client client = context.findClient(clientID);

        if (client == null) {
            System.out.println("Invalid Client ID.");
            return;
        }

        System.out.print("Enter payment amount: ");
        String input = context.getScanner().nextLine();

        try {
            double amount = Double.parseDouble(input);

            if (amount <= 0) {
                System.out.println("Payment amount must be greater than zero.");
                return;
            }

            context.getWarehouse().receivePayment(clientID, amount);

        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid payment amount.");
        }
    }

    // Switch from clerk mode to client mode
    private void becomeClient() {

        System.out.print("Enter Client ID: ");
        String clientID = context.getScanner().nextLine();

        if (context.findClient(clientID) == null) {
            System.out.println("Invalid Client ID.");
            return;
        }

        context.setClientID(clientID);
        context.changeState(Context.BECOME_CLIENT);
    }

    @Override
    public int getStateIndex() {
        return Context.CLERK;
    }
}