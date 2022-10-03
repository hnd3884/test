package java.awt.peer;

import java.awt.Dialog;

public interface WindowPeer extends ContainerPeer
{
    void toFront();
    
    void toBack();
    
    void updateAlwaysOnTopState();
    
    void updateFocusableWindowState();
    
    void setModalBlocked(final Dialog p0, final boolean p1);
    
    void updateMinimumSize();
    
    void updateIconImages();
    
    void setOpacity(final float p0);
    
    void setOpaque(final boolean p0);
    
    void updateWindow();
    
    void repositionSecurityWarning();
}
