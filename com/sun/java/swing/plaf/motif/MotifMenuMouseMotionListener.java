package com.sun.java.swing.plaf.motif;

import javax.swing.MenuSelectionManager;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

class MotifMenuMouseMotionListener implements MouseMotionListener
{
    @Override
    public void mouseDragged(final MouseEvent mouseEvent) {
        MenuSelectionManager.defaultManager().processMouseEvent(mouseEvent);
    }
    
    @Override
    public void mouseMoved(final MouseEvent mouseEvent) {
        MenuSelectionManager.defaultManager().processMouseEvent(mouseEvent);
    }
}
