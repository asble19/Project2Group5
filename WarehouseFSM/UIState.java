import javax.swing.JPanel;

public abstract class UIState {

    protected Context context;

    public UIState(Context context) {
        this.context = context;
    }

    // Each state builds and returns its own GUI panel
    public abstract JPanel createPanel();

    public abstract int getStateIndex();
}