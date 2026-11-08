import javax.swing.*;
import java.awt.*;

public class OpeningState extends UIState {

    public OpeningState(Context context) {
        super(context);
    }

    // Replaced old run() method with createPanel().
    // Instead of printing a menu to the console, creates and returns a Swing panel for the GUI.
    @Override
    public JPanel createPanel() {

        // Creates the main panel and centers the login menu.
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        // Creates a vertical layout for the title and buttons.
        JPanel menu = new JPanel();
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));

        // Replaces the console heading with a GUI label.
        JLabel title = new JLabel("WAREHOUSE LOGIN");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Replaces the numbered console menu options with buttons.
        JButton clientButton = new JButton("Login as Client");
        JButton clerkButton = new JButton("Login as Clerk");
        JButton managerButton = new JButton("Login as Manager");
        JButton exitButton = new JButton("Exit");

        // Each button gets its own action listener.
        // This replaces original switch statement that checked which numbered option the user entered.
        clientButton.addActionListener(e -> loginClient());

        clerkButton.addActionListener(e ->
            context.changeState(Context.LOGIN_CLERK)
        );

        managerButton.addActionListener(e ->
            context.changeState(Context.LOGIN_MANAGER)
        );

        // Replaces exit message and context.exit() call with a GUI button action.
        exitButton.addActionListener(e -> context.exit());

        // Arranges the title and buttons vertically.
        menu.add(title);
        menu.add(Box.createVerticalStrut(25));
        menu.add(clientButton);
        menu.add(Box.createVerticalStrut(10));
        menu.add(clerkButton);
        menu.add(Box.createVerticalStrut(10));
        menu.add(managerButton);
        menu.add(Box.createVerticalStrut(10));
        menu.add(exitButton);

        panel.add(menu);

        // Returns the completed GUI panel to Context.
        return panel;
    }

    // The original loginClient() used Scanner to read the Client ID from the console. It now uses a GUI input dialog.
    private void loginClient() {

        String clientID = JOptionPane.showInputDialog(
            context.getFrame(),
            "Enter Client ID:"
        );

        // Allows the user to cancel the input dialog.
        if (clientID == null) {
            return;
        }

        clientID = clientID.trim();

        // Still checks whether the entered Client ID exists.
        // Error message now appears in a GUI dialog instead of being printed to the console.
        if (context.findClient(clientID) == null) {
            JOptionPane.showMessageDialog(
                context.getFrame(),
                "Invalid Client ID. Please try again."
            );
            return;
        }

        // Stores the Client ID and changes FSM state.
        context.setClientID(clientID);
        context.changeState(Context.LOGIN_CLIENT);
    }

    // Identifies this state as OPENING in the FSM.
    @Override
    public int getStateIndex() {
        return Context.OPENING;
    }
}