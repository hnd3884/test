package java.awt.peer;

import java.awt.Component;
import java.awt.Window;

public interface KeyboardFocusManagerPeer
{
    void setCurrentFocusedWindow(final Window p0);
    
    Window getCurrentFocusedWindow();
    
    void setCurrentFocusOwner(final Component p0);
    
    Component getCurrentFocusOwner();
    
    void clearGlobalFocusOwner(final Window p0);
}
