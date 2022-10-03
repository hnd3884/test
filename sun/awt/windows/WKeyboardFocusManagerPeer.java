package sun.awt.windows;

import sun.awt.CausedFocusEvent;
import java.awt.Window;
import java.awt.Component;
import java.awt.peer.ComponentPeer;
import sun.awt.KeyboardFocusManagerPeerImpl;

final class WKeyboardFocusManagerPeer extends KeyboardFocusManagerPeerImpl
{
    private static final WKeyboardFocusManagerPeer inst;
    
    static native void setNativeFocusOwner(final ComponentPeer p0);
    
    static native Component getNativeFocusOwner();
    
    static native Window getNativeFocusedWindow();
    
    public static WKeyboardFocusManagerPeer getInstance() {
        return WKeyboardFocusManagerPeer.inst;
    }
    
    private WKeyboardFocusManagerPeer() {
    }
    
    @Override
    public void setCurrentFocusOwner(final Component component) {
        setNativeFocusOwner((component != null) ? component.getPeer() : null);
    }
    
    @Override
    public Component getCurrentFocusOwner() {
        return getNativeFocusOwner();
    }
    
    @Override
    public void setCurrentFocusedWindow(final Window window) {
        throw new RuntimeException("not implemented");
    }
    
    @Override
    public Window getCurrentFocusedWindow() {
        return getNativeFocusedWindow();
    }
    
    public static boolean deliverFocus(final Component component, final Component component2, final boolean b, final boolean b2, final long n, final CausedFocusEvent.Cause cause) {
        return KeyboardFocusManagerPeerImpl.deliverFocus(component, component2, b, b2, n, cause, getNativeFocusOwner());
    }
    
    static {
        inst = new WKeyboardFocusManagerPeer();
    }
}
