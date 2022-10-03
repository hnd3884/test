package javax.swing.event;

import java.util.EventListener;

public interface MenuListener extends EventListener
{
    void menuSelected(final MenuEvent p0);
    
    void menuDeselected(final MenuEvent p0);
    
    void menuCanceled(final MenuEvent p0);
}
