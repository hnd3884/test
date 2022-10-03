package java.awt.peer;

import java.awt.Rectangle;
import java.awt.MenuBar;

public interface FramePeer extends WindowPeer
{
    void setTitle(final String p0);
    
    void setMenuBar(final MenuBar p0);
    
    void setResizable(final boolean p0);
    
    void setState(final int p0);
    
    int getState();
    
    void setMaximizedBounds(final Rectangle p0);
    
    void setBoundsPrivate(final int p0, final int p1, final int p2, final int p3);
    
    Rectangle getBoundsPrivate();
    
    void emulateActivation(final boolean p0);
}
