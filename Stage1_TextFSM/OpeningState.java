public class OpeningState extends UIState {

    public OpeningState(Context context) {
        super(context);
    }

    @Override
    public void run() {

        System.out.println("\n========== WAREHOUSE LOGIN ==========");
        System.out.println("1. Login as Client");
        System.out.println("2. Login as Clerk");
        System.out.println("3. Login as Manager");
        System.out.println("0. Exit");

        System.out.print("Enter choice: ");

        String input = context.getScanner().nextLine();
        int choice;
        try {
            choice = Integer.parseInt(input.trim());
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
            return;
        }

        switch (choice) {

            case 1:
                loginClient();
                break;

            case 2:
                context.changeState(Context.LOGIN_CLERK);
                break;

            case 3:
                context.changeState(Context.LOGIN_MANAGER);
                break;

            case 0:
                System.out.println("Exiting Warehouse System.");
                context.exit();
                break;

            default:
                System.out.println("Invalid choice. Try again.");
        }
    }

    private void loginClient() {

        System.out.print("Enter Client ID: ");
        String clientID = context.getScanner().nextLine().trim();

        // Validate that the client exists
        if (context.findClient(clientID) == null) {
            System.out.println("Invalid Client ID. Please try again.");
            return;
        }

        context.setClientID(clientID);
        context.changeState(Context.LOGIN_CLIENT);
    }

    @Override
    public int getStateIndex() {
        return Context.OPENING;
    }
}