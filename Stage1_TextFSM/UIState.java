public abstract class UIState {

    protected Context context;

    public UIState(Context context) {
        this.context = context;
    }

    public abstract void run();

    public abstract int getStateIndex();
}