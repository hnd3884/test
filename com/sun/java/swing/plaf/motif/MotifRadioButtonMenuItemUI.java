package com.sun.java.swing.plaf.motif;

import java.awt.Point;
import javax.swing.MenuSelectionManager;
import java.awt.event.MouseEvent;
import javax.swing.LookAndFeel;
import javax.swing.JMenuItem;
import javax.swing.event.ChangeEvent;
import java.io.Serializable;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicRadioButtonMenuItemUI;

public class MotifRadioButtonMenuItemUI extends BasicRadioButtonMenuItemUI
{
    protected ChangeListener changeListener;
    
    public static ComponentUI createUI(final JComponent component) {
        return new MotifRadioButtonMenuItemUI();
    }
    
    @Override
    protected void installListeners() {
        super.installListeners();
        this.changeListener = this.createChangeListener(this.menuItem);
        this.menuItem.addChangeListener(this.changeListener);
    }
    
    @Override
    protected void uninstallListeners() {
        super.uninstallListeners();
        this.menuItem.removeChangeListener(this.changeListener);
    }
    
    protected ChangeListener createChangeListener(final JComponent component) {
        return new ChangeHandler();
    }
    
    @Override
    protected MouseInputListener createMouseInputListener(final JComponent component) {
        return new MouseInputHandler();
    }
    
    protected class ChangeHandler implements ChangeListener, Serializable
    {
        @Override
        public void stateChanged(final ChangeEvent changeEvent) {
            final JMenuItem menuItem = (JMenuItem)changeEvent.getSource();
            LookAndFeel.installProperty(menuItem, "borderPainted", menuItem.isArmed());
        }
    }
    
    protected class MouseInputHandler implements MouseInputListener
    {
        @Override
        public void mouseClicked(final MouseEvent mouseEvent) {
        }
        
        @Override
        public void mousePressed(final MouseEvent mouseEvent) {
            MenuSelectionManager.defaultManager().setSelectedPath(MotifRadioButtonMenuItemUI.this.getPath());
        }
        
        @Override
        public void mouseReleased(final MouseEvent mouseEvent) {
            final MenuSelectionManager defaultManager = MenuSelectionManager.defaultManager();
            final JMenuItem menuItem = (JMenuItem)mouseEvent.getComponent();
            final Point point = mouseEvent.getPoint();
            if (point.x >= 0 && point.x < menuItem.getWidth() && point.y >= 0 && point.y < menuItem.getHeight()) {
                defaultManager.clearSelectedPath();
                menuItem.doClick(0);
            }
            else {
                defaultManager.processMouseEvent(mouseEvent);
            }
        }
        
        @Override
        public void mouseEntered(final MouseEvent mouseEvent) {
        }
        
        @Override
        public void mouseExited(final MouseEvent mouseEvent) {
        }
        
        @Override
        public void mouseDragged(final MouseEvent mouseEvent) {
            MenuSelectionManager.defaultManager().processMouseEvent(mouseEvent);
        }
        
        @Override
        public void mouseMoved(final MouseEvent mouseEvent) {
        }
    }
}
