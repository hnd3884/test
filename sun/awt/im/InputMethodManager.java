package sun.awt.im;

import java.util.Locale;
import java.awt.Component;

public abstract class InputMethodManager
{
    private static final String threadName = "AWT-InputMethodManager";
    private static final Object LOCK;
    private static InputMethodManager inputMethodManager;
    
    public static final InputMethodManager getInstance() {
        if (InputMethodManager.inputMethodManager != null) {
            return InputMethodManager.inputMethodManager;
        }
        synchronized (InputMethodManager.LOCK) {
            if (InputMethodManager.inputMethodManager == null) {
                final ExecutableInputMethodManager inputMethodManager = new ExecutableInputMethodManager();
                if (inputMethodManager.hasMultipleInputMethods()) {
                    inputMethodManager.initialize();
                    final Thread thread = new Thread(inputMethodManager, "AWT-InputMethodManager");
                    thread.setDaemon(true);
                    thread.setPriority(6);
                    thread.start();
                }
                InputMethodManager.inputMethodManager = inputMethodManager;
            }
        }
        return InputMethodManager.inputMethodManager;
    }
    
    public abstract String getTriggerMenuString();
    
    public abstract void notifyChangeRequest(final Component p0);
    
    public abstract void notifyChangeRequestByHotKey(final Component p0);
    
    abstract void setInputContext(final InputContext p0);
    
    abstract InputMethodLocator findInputMethod(final Locale p0);
    
    abstract Locale getDefaultKeyboardLocale();
    
    abstract boolean hasMultipleInputMethods();
    
    static {
        LOCK = new Object();
    }
}
