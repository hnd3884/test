package com.sun.java.swing.plaf.motif;

import javax.swing.MenuSelectionManager;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;

class MotifMenuMouseListener extends MouseAdapter
{
    @Override
    public void mousePressed(final MouseEvent mouseEvent) {
        MenuSelectionManager.defaultManager().processMouseEvent(mouseEvent);
    }
    
    @Override
    public void mouseReleased(final MouseEvent mouseEvent) {
        MenuSelectionManager.defaultManager().processMouseEvent(mouseEvent);
    }
    
    @Override
    public void mouseEntered(final MouseEvent mouseEvent) {
        MenuSelectionManager.defaultManager().processMouseEvent(mouseEvent);
    }
    
    @Override
    public void mouseExited(final MouseEvent mouseEvent) {
        MenuSelectionManager.defaultManager().processMouseEvent(mouseEvent);
    }
}
