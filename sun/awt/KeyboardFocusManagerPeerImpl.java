package sun.awt;

import java.awt.peer.ComponentPeer;
import java.awt.Panel;
import java.awt.Scrollbar;
import java.awt.Canvas;
import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Window;
import sun.util.logging.PlatformLogger;
import java.awt.peer.KeyboardFocusManagerPeer;

public abstract class KeyboardFocusManagerPeerImpl implements KeyboardFocusManagerPeer
{
    private static final PlatformLogger focusLog;
    private static AWTAccessor.KeyboardFocusManagerAccessor kfmAccessor;
    public static final int SNFH_FAILURE = 0;
    public static final int SNFH_SUCCESS_HANDLED = 1;
    public static final int SNFH_SUCCESS_PROCEED = 2;
    
    @Override
    public void clearGlobalFocusOwner(final Window window) {
        if (window != null) {
            final Component focusOwner = window.getFocusOwner();
            if (KeyboardFocusManagerPeerImpl.focusLog.isLoggable(PlatformLogger.Level.FINE)) {
                KeyboardFocusManagerPeerImpl.focusLog.fine("Clearing global focus owner " + focusOwner);
            }
            if (focusOwner != null) {
                SunToolkit.postPriorityEvent(new CausedFocusEvent(focusOwner, 1005, false, null, CausedFocusEvent.Cause.CLEAR_GLOBAL_FOCUS_OWNER));
            }
        }
    }
    
    public static boolean shouldFocusOnClick(final Component component) {
        boolean b;
        if (component instanceof Canvas || component instanceof Scrollbar) {
            b = true;
        }
        else if (component instanceof Panel) {
            b = (((Panel)component).getComponentCount() == 0);
        }
        else {
            final ComponentPeer componentPeer = (component != null) ? component.getPeer() : null;
            b = (componentPeer != null && componentPeer.isFocusable());
        }
        return b && AWTAccessor.getComponentAccessor().canBeFocusOwner(component);
    }
    
    public static boolean deliverFocus(Component component, final Component component2, final boolean b, final boolean b2, final long n, final CausedFocusEvent.Cause cause, final Component component3) {
        if (component == null) {
            component = component2;
        }
        Component component4 = component3;
        if (component4 != null && component4.getPeer() == null) {
            component4 = null;
        }
        if (component4 != null) {
            final CausedFocusEvent causedFocusEvent = new CausedFocusEvent(component4, 1005, false, component, cause);
            if (KeyboardFocusManagerPeerImpl.focusLog.isLoggable(PlatformLogger.Level.FINER)) {
                KeyboardFocusManagerPeerImpl.focusLog.finer("Posting focus event: " + causedFocusEvent);
            }
            SunToolkit.postEvent(SunToolkit.targetToAppContext(component4), causedFocusEvent);
        }
        final CausedFocusEvent causedFocusEvent2 = new CausedFocusEvent(component, 1004, false, component4, cause);
        if (KeyboardFocusManagerPeerImpl.focusLog.isLoggable(PlatformLogger.Level.FINER)) {
            KeyboardFocusManagerPeerImpl.focusLog.finer("Posting focus event: " + causedFocusEvent2);
        }
        SunToolkit.postEvent(SunToolkit.targetToAppContext(component), causedFocusEvent2);
        return true;
    }
    
    public static boolean requestFocusFor(final Component component, final CausedFocusEvent.Cause cause) {
        return AWTAccessor.getComponentAccessor().requestFocus(component, cause);
    }
    
    public static int shouldNativelyFocusHeavyweight(final Component component, final Component component2, final boolean b, final boolean b2, final long n, final CausedFocusEvent.Cause cause) {
        return KeyboardFocusManagerPeerImpl.kfmAccessor.shouldNativelyFocusHeavyweight(component, component2, b, b2, n, cause);
    }
    
    public static void removeLastFocusRequest(final Component component) {
        KeyboardFocusManagerPeerImpl.kfmAccessor.removeLastFocusRequest(component);
    }
    
    public static boolean processSynchronousLightweightTransfer(final Component component, final Component component2, final boolean b, final boolean b2, final long n) {
        return KeyboardFocusManagerPeerImpl.kfmAccessor.processSynchronousLightweightTransfer(component, component2, b, b2, n);
    }
    
    static {
        focusLog = PlatformLogger.getLogger("sun.awt.focus.KeyboardFocusManagerPeerImpl");
        KeyboardFocusManagerPeerImpl.kfmAccessor = AWTAccessor.getKeyboardFocusManagerAccessor();
    }
}
