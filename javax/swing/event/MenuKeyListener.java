package javax.swing.event;

import java.util.EventListener;

public interface MenuKeyListener extends EventListener
{
    void menuKeyTyped(final MenuKeyEvent p0);
    
    void menuKeyPressed(final MenuKeyEvent p0);
    
    void menuKeyReleased(final MenuKeyEvent p0);
}
