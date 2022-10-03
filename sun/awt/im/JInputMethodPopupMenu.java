package sun.awt.im;

import java.awt.event.ActionListener;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JMenu;
import java.awt.Component;
import javax.swing.JPopupMenu;

class JInputMethodPopupMenu extends InputMethodPopupMenu
{
    static JPopupMenu delegate;
    
    JInputMethodPopupMenu(final String s) {
        synchronized (this) {
            if (JInputMethodPopupMenu.delegate == null) {
                JInputMethodPopupMenu.delegate = new JPopupMenu(s);
            }
        }
    }
    
    @Override
    void show(final Component component, final int n, final int n2) {
        JInputMethodPopupMenu.delegate.show(component, n, n2);
    }
    
    @Override
    void removeAll() {
        JInputMethodPopupMenu.delegate.removeAll();
    }
    
    @Override
    void addSeparator() {
        JInputMethodPopupMenu.delegate.addSeparator();
    }
    
    @Override
    void addToComponent(final Component component) {
    }
    
    @Override
    Object createSubmenu(final String s) {
        return new JMenu(s);
    }
    
    @Override
    void add(final Object o) {
        JInputMethodPopupMenu.delegate.add((JMenuItem)o);
    }
    
    @Override
    void addMenuItem(final String s, final String s2, final String s3) {
        this.addMenuItem(JInputMethodPopupMenu.delegate, s, s2, s3);
    }
    
    @Override
    void addMenuItem(final Object o, final String s, final String actionCommand, final String s2) {
        JMenuItem menuItem;
        if (InputMethodPopupMenu.isSelected(actionCommand, s2)) {
            menuItem = new JCheckBoxMenuItem(s, true);
        }
        else {
            menuItem = new JMenuItem(s);
        }
        menuItem.setActionCommand(actionCommand);
        menuItem.addActionListener(this);
        menuItem.setEnabled(actionCommand != null);
        if (o instanceof JMenu) {
            ((JMenu)o).add(menuItem);
        }
        else {
            ((JPopupMenu)o).add(menuItem);
        }
    }
    
    static {
        JInputMethodPopupMenu.delegate = null;
    }
}
