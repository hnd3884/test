package javax.swing.event;

import java.util.EventListener;

public interface MenuDragMouseListener extends EventListener
{
    void menuDragMouseEntered(final MenuDragMouseEvent p0);
    
    void menuDragMouseExited(final MenuDragMouseEvent p0);
    
    void menuDragMouseDragged(final MenuDragMouseEvent p0);
    
    void menuDragMouseReleased(final MenuDragMouseEvent p0);
}
