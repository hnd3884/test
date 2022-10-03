package java.awt.event;

import java.util.EventListener;

public interface MouseMotionListener extends EventListener
{
    void mouseDragged(final MouseEvent p0);
    
    void mouseMoved(final MouseEvent p0);
}
