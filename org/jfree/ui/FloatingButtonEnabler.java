package org.jfree.ui;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.AbstractButton;
import java.awt.event.MouseAdapter;

public final class FloatingButtonEnabler extends MouseAdapter
{
    private static FloatingButtonEnabler singleton;
    
    private FloatingButtonEnabler() {
    }
    
    public void addButton(final AbstractButton button) {
        button.addMouseListener(this);
        button.setBorderPainted(false);
    }
    
    public static FloatingButtonEnabler getInstance() {
        if (FloatingButtonEnabler.singleton == null) {
            FloatingButtonEnabler.singleton = new FloatingButtonEnabler();
        }
        return FloatingButtonEnabler.singleton;
    }
    
    public void mouseEntered(final MouseEvent e) {
        if (e.getSource() instanceof AbstractButton) {
            final AbstractButton button = (AbstractButton)e.getSource();
            if (button.isEnabled()) {
                button.setBorderPainted(true);
            }
        }
    }
    
    public void mouseExited(final MouseEvent e) {
        if (e.getSource() instanceof AbstractButton) {
            final AbstractButton button = (AbstractButton)e.getSource();
            button.setBorderPainted(false);
            if (button.getParent() != null) {
                button.getParent().repaint();
            }
        }
    }
    
    public void removeButton(final AbstractButton button) {
        button.addMouseListener(this);
        button.setBorderPainted(true);
    }
}
