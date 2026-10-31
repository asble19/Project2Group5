public class ManagerMenuState extends UIState {

    public ManagerMenuState(Context context) {
        super(context);
    }

    @Override
    public void run() {

        System.out.println("\n========== MANAGER MENU ==========");
        System.out.println("1. Add Product");
        System.out.println("2. Display Waitlist for Product");
        System.out.println("3. Receive Shipment");
        System.out.println("4. Become Clerk");
        System.out.println("0. Logout");

        System.out.print("Enter choice: ");

        String input = context.getScanner().nextLine();

        try {
            int choice = Integer.parseInt(input);

            switch (choice) {

                case 1:
                    addProduct();
                    break;

                case 2:
                    displayWaitlist();
                    break;

                case 3:
                    receiveShipment();
                    break;

                case 4:
                    context.changeState(Context.BECOME_CLERK);
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

    // Add a new product
    private void addProduct() {

        System.out.print("Enter product name: ");
        String name = context.getScanner().nextLine();

        try {
            System.out.print("Enter quantity: ");
            int quantity = Integer.parseInt(context.getScanner().nextLine());

            System.out.print("Enter price: ");
            double price = Double.parseDouble(context.getScanner().nextLine());

            if (quantity < 0 || price < 0) {
                System.out.println("Quantity and price cannot be negative.");
                return;
            }

            Product product = context.getWarehouse().addProduct(
                name, quantity, price
            );

            System.out.println("Product successfully added:");
            System.out.println(product);

        } catch (NumberFormatException e) {
            System.out.println("Please enter valid numbers.");
        }
    }

    // Display the waitlist for a product
    private void displayWaitlist() {

        System.out.print("Enter Product ID: ");
        String productID = context.getScanner().nextLine();

        context.getWarehouse().displayWaitlist(productID);
    }

    // Receive a shipment for a product
    private void receiveShipment() {

        System.out.print("Enter Product ID: ");
        String productID = context.getScanner().nextLine();

        try {
            System.out.print("Enter shipment quantity: ");
            int quantity = Integer.parseInt(context.getScanner().nextLine());

            if (quantity <= 0) {
                System.out.println("Shipment quantity must be greater than zero.");
                return;
            }

            context.getWarehouse().receiveShipment(productID, quantity);

        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid quantity.");
        }
    }

    @Override
    public int getStateIndex() {
        return Context.MANAGER;
    }
}
