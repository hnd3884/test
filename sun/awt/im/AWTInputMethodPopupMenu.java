package sun.awt.im;

import java.awt.event.ActionListener;
import java.awt.CheckboxMenuItem;
import java.awt.MenuItem;
import java.awt.Menu;
import java.awt.Component;
import java.awt.PopupMenu;

class AWTInputMethodPopupMenu extends InputMethodPopupMenu
{
    static PopupMenu delegate;
    
    AWTInputMethodPopupMenu(final String s) {
        synchronized (this) {
            if (AWTInputMethodPopupMenu.delegate == null) {
                AWTInputMethodPopupMenu.delegate = new PopupMenu(s);
            }
        }
    }
    
    @Override
    void show(final Component component, final int n, final int n2) {
        AWTInputMethodPopupMenu.delegate.show(component, n, n2);
    }
    
    @Override
    void removeAll() {
        AWTInputMethodPopupMenu.delegate.removeAll();
    }
    
    @Override
    void addSeparator() {
        AWTInputMethodPopupMenu.delegate.addSeparator();
    }
    
    @Override
    void addToComponent(final Component component) {
        component.add(AWTInputMethodPopupMenu.delegate);
    }
    
    @Override
    Object createSubmenu(final String s) {
        return new Menu(s);
    }
    
    @Override
    void add(final Object o) {
        AWTInputMethodPopupMenu.delegate.add((MenuItem)o);
    }
    
    @Override
    void addMenuItem(final String s, final String s2, final String s3) {
        this.addMenuItem(AWTInputMethodPopupMenu.delegate, s, s2, s3);
    }
    
    @Override
    void addMenuItem(final Object o, final String s, final String actionCommand, final String s2) {
        MenuItem menuItem;
        if (InputMethodPopupMenu.isSelected(actionCommand, s2)) {
            menuItem = new CheckboxMenuItem(s, true);
        }
        else {
            menuItem = new MenuItem(s);
        }
        menuItem.setActionCommand(actionCommand);
        menuItem.addActionListener(this);
        menuItem.setEnabled(actionCommand != null);
        ((Menu)o).add(menuItem);
    }
    
    static {
        AWTInputMethodPopupMenu.delegate = null;
    }
}
