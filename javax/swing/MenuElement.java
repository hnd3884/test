package javax.swing;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public interface MenuElement
{
    void processMouseEvent(final MouseEvent p0, final MenuElement[] p1, final MenuSelectionManager p2);
    
    void processKeyEvent(final KeyEvent p0, final MenuElement[] p1, final MenuSelectionManager p2);
    
    void menuSelectionChanged(final boolean p0);
    
    MenuElement[] getSubElements();
    
    Component getComponent();
}
