public class ClientMenuState extends UIState {

    public ClientMenuState(Context context) {
        super(context);
    }

    @Override
    public void run() {

        System.out.println("\n========== CLIENT MENU ==========");
        System.out.println("1. Display Account Information");
        System.out.println("2. Display All Products");
        System.out.println("3. Display Transactions");
        System.out.println("4. Add Product to Wishlist");
        System.out.println("5. Display Wishlist");
        System.out.println("6. Place Order");
        System.out.println("0. Logout");

        System.out.print("Enter choice: ");

        String input = context.getScanner().nextLine();

        try {
            int choice = Integer.parseInt(input);

            switch (choice) {

                case 1:
                    displayAccountInformation();
                    break;

                case 2:
                    context.getWarehouse().displayAllProducts();
                    break;

                case 3:
                    displayTransactions();
                    break;

                case 4:
                    addToWishlist();
                    break;

                case 5:
                    displayWishlist();
                    break;

                case 6:
                    placeOrder();
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

    // Display the current client's account information
    private void displayAccountInformation() {

        Client client = context.findClient(context.getClientID());

        if (client == null) {
            System.out.println("Client information could not be found.");
            return;
        }

        System.out.println("\n========== ACCOUNT INFORMATION ==========");
        System.out.println(client);
    }

    // Display the client's transaction history
    private void displayTransactions() {

        System.out.println("\n========== TRANSACTIONS ==========");

        context.getWarehouse().displayInvoices(context.getClientID());
    }

    // Add a product to the client's wishlist
    private void addToWishlist() {

        System.out.print("Enter Product ID: ");
        String productID = context.getScanner().nextLine();

        try {
            System.out.print("Enter quantity: ");
            int quantity = Integer.parseInt(context.getScanner().nextLine());

            if (quantity <= 0) {
                System.out.println("Quantity must be greater than zero.");
                return;
            }

            context.getWarehouse().addProductToWishlist(
                context.getClientID(),
                productID,
                quantity
            );

        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid quantity.");
        }
    }

    // Display the client's wishlist
    private void displayWishlist() {

        System.out.println("\n========== YOUR WISHLIST ==========");

        context.getWarehouse().displayWishlist(context.getClientID());
    }

    // Place an order using the client's wishlist
    private void placeOrder() {

        System.out.println("\n========== PLACE ORDER ==========");

        context.getWarehouse().processOrder(context.getClientID());
    }

    @Override
    public int getStateIndex() {
        return Context.CLIENT;
    }
}