package javax.swing;

import java.awt.FocusTraversalPolicy;
import java.awt.DefaultFocusTraversalPolicy;
import java.awt.KeyboardFocusManager;
import java.awt.DefaultKeyboardFocusManager;

public abstract class FocusManager extends DefaultKeyboardFocusManager
{
    public static final String FOCUS_MANAGER_CLASS_PROPERTY = "FocusManagerClassName";
    private static boolean enabled;
    
    public static FocusManager getCurrentManager() {
        final KeyboardFocusManager currentKeyboardFocusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        if (currentKeyboardFocusManager instanceof FocusManager) {
            return (FocusManager)currentKeyboardFocusManager;
        }
        return new DelegatingDefaultFocusManager(currentKeyboardFocusManager);
    }
    
    public static void setCurrentManager(final FocusManager focusManager) throws SecurityException {
        KeyboardFocusManager.setCurrentKeyboardFocusManager((focusManager instanceof DelegatingDefaultFocusManager) ? ((DelegatingDefaultFocusManager)focusManager).getDelegate() : focusManager);
    }
    
    @Deprecated
    public static void disableSwingFocusManager() {
        if (FocusManager.enabled) {
            FocusManager.enabled = false;
            KeyboardFocusManager.getCurrentKeyboardFocusManager().setDefaultFocusTraversalPolicy(new DefaultFocusTraversalPolicy());
        }
    }
    
    @Deprecated
    public static boolean isFocusManagerEnabled() {
        return FocusManager.enabled;
    }
    
    static {
        FocusManager.enabled = true;
    }
}
