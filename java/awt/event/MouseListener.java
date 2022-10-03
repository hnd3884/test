package java.awt.event;

import java.util.EventListener;

public interface MouseListener extends EventListener
{
    void mouseClicked(final MouseEvent p0);
    
    void mousePressed(final MouseEvent p0);
    
    void mouseReleased(final MouseEvent p0);
    
    void mouseEntered(final MouseEvent p0);
    
    void mouseExited(final MouseEvent p0);
}
