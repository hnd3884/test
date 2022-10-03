package com.sun.java.swing.plaf.motif;

import java.awt.Point;
import java.awt.Container;
import javax.swing.JMenuBar;
import javax.swing.MenuSelectionManager;
import java.awt.event.MouseEvent;
import javax.swing.JMenuItem;
import javax.swing.event.ChangeEvent;
import javax.swing.event.MouseInputListener;
import javax.swing.JPopupMenu;
import javax.swing.MenuElement;
import javax.swing.JMenu;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicMenuUI;

public class MotifMenuUI extends BasicMenuUI
{
    public static ComponentUI createUI(final JComponent component) {
        return new MotifMenuUI();
    }
    
    @Override
    protected ChangeListener createChangeListener(final JComponent component) {
        return new MotifChangeHandler((JMenu)component, this);
    }
    
    private boolean popupIsOpen(final JMenu menu, final MenuElement[] array) {
        final JPopupMenu popupMenu = menu.getPopupMenu();
        for (int i = array.length - 1; i >= 0; --i) {
            if (array[i].getComponent() == popupMenu) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    protected MouseInputListener createMouseInputListener(final JComponent component) {
        return new MouseInputHandler();
    }
    
    public class MotifChangeHandler extends ChangeHandler
    {
        public MotifChangeHandler(final JMenu menu, final MotifMenuUI motifMenuUI) {
            super(menu, motifMenuUI);
        }
        
        @Override
        public void stateChanged(final ChangeEvent changeEvent) {
            final JMenuItem menuItem = (JMenuItem)changeEvent.getSource();
            if (menuItem.isArmed() || menuItem.isSelected()) {
                menuItem.setBorderPainted(true);
            }
            else {
                menuItem.setBorderPainted(false);
            }
            super.stateChanged(changeEvent);
        }
    }
    
    protected class MouseInputHandler implements MouseInputListener
    {
        @Override
        public void mouseClicked(final MouseEvent mouseEvent) {
        }
        
        @Override
        public void mousePressed(final MouseEvent mouseEvent) {
            final MenuSelectionManager defaultManager = MenuSelectionManager.defaultManager();
            final JMenu menu = (JMenu)mouseEvent.getComponent();
            if (menu.isEnabled()) {
                if (menu.isTopLevelMenu()) {
                    if (menu.isSelected()) {
                        defaultManager.clearSelectedPath();
                    }
                    else {
                        final Container parent = menu.getParent();
                        if (parent != null && parent instanceof JMenuBar) {
                            defaultManager.setSelectedPath(new MenuElement[] { (MenuElement)parent, menu });
                        }
                    }
                }
                final MenuElement[] path = MotifMenuUI.this.getPath();
                if (path.length > 0) {
                    final MenuElement[] selectedPath = new MenuElement[path.length + 1];
                    System.arraycopy(path, 0, selectedPath, 0, path.length);
                    selectedPath[path.length] = menu.getPopupMenu();
                    defaultManager.setSelectedPath(selectedPath);
                }
            }
        }
        
        @Override
        public void mouseReleased(final MouseEvent mouseEvent) {
            final MenuSelectionManager defaultManager = MenuSelectionManager.defaultManager();
            final JMenuItem menuItem = (JMenuItem)mouseEvent.getComponent();
            final Point point = mouseEvent.getPoint();
            if (point.x < 0 || point.x >= menuItem.getWidth() || point.y < 0 || point.y >= menuItem.getHeight()) {
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
