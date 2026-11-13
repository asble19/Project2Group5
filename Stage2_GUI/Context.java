import java.util.ArrayDeque;
import java.util.Deque;
import java.util.ArrayList;
import java.util.List;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

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

    // Remembers previous roles when switching menus.
    private Deque<UIState> returnStates;

    // ADDED: Main Swing application window.
    private JFrame frame;

    private Context() {

        warehouse = Warehouse.instance();
        returnStates = new ArrayDeque<UIState>();
        knownClients = new ArrayList<Client>();

        // Creates the four FSM states.
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

        // Opening is the initial state.
        currentState = states[OPENING];

        // ADDED: Creates the main GUI window.
        frame = new JFrame("Warehouse Management System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 500);
        frame.setLocationRelativeTo(null);
    }

    // Singleton instance.
    public static Context instance() {

        if (context == null) {
            context = new Context();
        }

        return context;
    }

    // Replaces the original console FSM loop.
    // Opens the window and displays the initial state.
    public void run() {

        showCurrentState();
        frame.setVisible(true);
    }

    // ADDED: Displays the current state's GUI panel.
    private void showCurrentState() {

        frame.setContentPane(currentState.createPanel());

        frame.revalidate();
        frame.repaint();
    }

    // Centralized FSM transition method.
    // Refreshes the GUI after a successful transition.
    public void changeState(int event) {

        int currentIndex = currentState.getStateIndex();

        if (event < 0 || event >= transitionTable[currentIndex].length) {
            JOptionPane.showMessageDialog(
                frame,
                "Invalid transition."
            );
            return;
        }

        // Handles logout using the saved return path.
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

            // ADDED: Updates the GUI after logging out.
            showCurrentState();
            return;
        }

        int nextIndex = transitionTable[currentIndex][event];

        if (nextIndex == -1) {
            JOptionPane.showMessageDialog(
                frame,
                "That transition is not allowed."
            );
            return;
        }

        // Remembers the previous role when switching menus.
        if (event == BECOME_CLIENT || event == BECOME_CLERK) {
            returnStates.push(currentState);
        }

        currentState = states[nextIndex];

        // ADDED: Displays the new state's GUI panel.
        showCurrentState();
    }

    // Stores the current Client ID.
    public void setClientID(String clientID) {
        this.clientID = clientID;
    }

    public String getClientID() {
        return clientID;
    }

    // Provides access to the Warehouse backend.
    public Warehouse getWarehouse() {
        return warehouse;
    }

    // ADDED: Provides access to the main GUI window.
    // Used as the parent for JOptionPane dialogs.
    public JFrame getFrame() {
        return frame;
    }

    // ADDED: Captures console output from existing backend methods.
    // This allows the GUI to display backend results without modifying the Warehouse classes.
    public String captureOutput(Runnable action) {

        PrintStream originalOutput = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        try (PrintStream capture = new PrintStream(output)) {

            System.setOut(capture);

            action.run();

            capture.flush();

        } finally {
            // Restores the original console output.
            System.setOut(originalOutput);
        }

        return output.toString();
    }

    // Closes the GUI instead of stopping a console loop.
    public void exit() {
        frame.dispose();
        System.exit(0);
    }

    public UIState getCurrentState() {
        return currentState;
    }

    // Keeps track of clients created during this session.
    public void registerClient(Client client) {
        knownClients.add(client);
    }

    // Finds a client by ID.
    public Client findClient(String clientID) {

        for (Client client : knownClients) {
            if (client.getClientID().equals(clientID)) {
                return client;
            }
        }

        return null;
    }

    // Returns all clients created during this session.
    public List<Client> getKnownClients() {
        return knownClients;
    }

    // ADDED: Starts the Swing application on the Event Dispatch Thread.
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            Context.instance().run();
        });
    }
}
