package java.awt.event;

import java.util.EventListener;

public interface WindowListener extends EventListener
{
    void windowOpened(final WindowEvent p0);
    
    void windowClosing(final WindowEvent p0);
    
    void windowClosed(final WindowEvent p0);
    
    void windowIconified(final WindowEvent p0);
    
    void windowDeiconified(final WindowEvent p0);
    
    void windowActivated(final WindowEvent p0);
    
    void windowDeactivated(final WindowEvent p0);
}
