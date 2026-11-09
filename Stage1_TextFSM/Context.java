import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;

public class Context {

    // State indexes
    public static final int OPENING = 0;
    public static final int CLIENT = 1;
    public static final int CLERK = 2;
    public static final int MANAGER = 3;

    // Events
    public static final int LOGIN_CLIENT = 0;
    public static final int LOGIN_CLERK = 1;
    public static final int LOGIN_MANAGER = 2;
    public static final int BECOME_CLIENT = 3;
    public static final int BECOME_CLERK = 4;
    public static final int LOGOUT = 5;

    private static Context context;

    private UIState currentState;
    private UIState[] states;
    private int[][] transitionTable;
    private List<Client> knownClients;

    private Warehouse warehouse;
    private String clientID;

    // Remembers previous roles when switching menus
    private Deque<UIState> returnStates;

    // One Scanner shared by every state
    private Scanner scanner;

    private boolean running;

    private Context() {

        warehouse = Warehouse.instance();
        scanner = new Scanner(System.in);
        returnStates = new ArrayDeque<UIState>();
        knownClients = new ArrayList<Client>();

        states = new UIState[4];

        states[OPENING] = new OpeningState(this);
        states[CLIENT] = new ClientMenuState(this);
        states[CLERK] = new ClerkMenuState(this);
        states[MANAGER] = new ManagerMenuState(this);

        /*
         * Rows represent current states.
         * Columns represent events.
         * -1 means the transition is not allowed.
         */
        transitionTable = new int[][] {

            // LOGIN_CLIENT, LOGIN_CLERK, LOGIN_MANAGER,
            // BECOME_CLIENT, BECOME_CLERK, LOGOUT

            { CLIENT, CLERK, MANAGER, -1, -1, -1 }, // Opening
            { -1, -1, -1, -1, -1, OPENING },       // Client
            { -1, -1, -1, CLIENT, -1, OPENING },   // Clerk
            { -1, -1, -1, -1, CLERK, OPENING }     // Manager
        };

        currentState = states[OPENING];
        running = true;
    }

    public static Context instance() {
        if (context == null) {
            context = new Context();
        }

        return context;
    }

    // Main FSM loop
    public void run() {
        while (running) {
            currentState.run();
        }
    }

    // Centralized state transition
    public void changeState(int event) {

        int currentIndex = currentState.getStateIndex();

        if (event < 0 || event >= transitionTable[currentIndex].length) {
            System.out.println("Invalid transition.");
            return;
        }

        // Handle logout using the saved return path
        if (event == LOGOUT) {

            if (currentIndex == CLIENT || currentIndex == CLERK) {

                if (!returnStates.isEmpty()) {
                    currentState = returnStates.pop();
                } else {
                    currentState = states[OPENING];
                }

            } else {
                currentState = states[OPENING];
                returnStates.clear();
            }

            clientID = null;
            return;
        }

        int nextIndex = transitionTable[currentIndex][event];

        if (nextIndex == -1) {
            System.out.println("That transition is not allowed.");
            return;
        }

        // Remember the previous role when switching menus
        if (event == BECOME_CLIENT || event == BECOME_CLERK) {
            returnStates.push(currentState);
        }

        currentState = states[nextIndex];
    }

    public void setClientID(String clientID) {
        this.clientID = clientID;
    }

    public String getClientID() {
        return clientID;
    }

    public Warehouse getWarehouse() {
        return warehouse;
    }

    public Scanner getScanner() {
        return scanner;
    }

    public void exit() {
        running = false;
    }

    public UIState getCurrentState() {
        return currentState;
    }

    // Keep track of clients created during this session
    public void registerClient(Client client) {
        knownClients.add(client);
    }

    // Find a client by ID
    public Client findClient(String clientID) {

        for (Client client : knownClients) {
            if (client.getClientID().equals(clientID)) {
                return client;
            }
        }

        return null;
    }

    // Get all clients created during this session
    public List<Client> getKnownClients() {
        return knownClients;
    }
    public static void main(String[] args) {
        Context.instance().run();
    }
}