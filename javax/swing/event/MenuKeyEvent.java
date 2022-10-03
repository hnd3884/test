package javax.swing.event;

import java.awt.Component;
import javax.swing.MenuSelectionManager;
import javax.swing.MenuElement;
import java.awt.event.KeyEvent;

public class MenuKeyEvent extends KeyEvent
{
    private MenuElement[] path;
    private MenuSelectionManager manager;
    
    public MenuKeyEvent(final Component component, final int n, final long n2, final int n3, final int n4, final char c, final MenuElement[] path, final MenuSelectionManager manager) {
        super(component, n, n2, n3, n4, c);
        this.path = path;
        this.manager = manager;
    }
    
    public MenuElement[] getPath() {
        return this.path;
    }
    
    public MenuSelectionManager getMenuSelectionManager() {
        return this.manager;
    }
}
