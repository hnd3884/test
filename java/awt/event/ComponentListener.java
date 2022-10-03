package java.awt.event;

import java.util.EventListener;

public interface ComponentListener extends EventListener
{
    void componentResized(final ComponentEvent p0);
    
    void componentMoved(final ComponentEvent p0);
    
    void componentShown(final ComponentEvent p0);
    
    void componentHidden(final ComponentEvent p0);
}
