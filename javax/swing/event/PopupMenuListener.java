package javax.swing.event;

import java.util.EventListener;

public interface PopupMenuListener extends EventListener
{
    void popupMenuWillBecomeVisible(final PopupMenuEvent p0);
    
    void popupMenuWillBecomeInvisible(final PopupMenuEvent p0);
    
    void popupMenuCanceled(final PopupMenuEvent p0);
}
