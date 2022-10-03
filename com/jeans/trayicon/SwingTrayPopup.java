package com.jeans.trayicon;

import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.awt.Component;
import javax.swing.JDialog;
import java.awt.Dimension;
import java.awt.Point;
import javax.swing.JMenu;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import java.awt.event.MouseListener;
import javax.swing.JPopupMenu;

public class SwingTrayPopup extends JPopupMenu
{
    WindowsTrayIcon m_Icon;
    MouseListener m_Listener;
    
    public void setTrayIcon(final WindowsTrayIcon icon) {
        if (icon != null) {
            this.m_Icon = icon;
            final WindowsTrayIcon icon2 = this.m_Icon;
            WindowsTrayIcon.initJAWT();
            final WindowsTrayIcon icon3 = this.m_Icon;
            WindowsTrayIcon.initHook();
            this.m_Listener = new ActivateListener();
            this.m_Icon.addMouseListener(this.m_Listener);
        }
        else if (this.m_Icon != null) {
            this.m_Icon.removeMouseListener(this.m_Listener);
            this.m_Icon = null;
        }
    }
    
    public void showMenu(final int n, final int n2) {
        SwingUtilities.invokeLater(new InvokeMenu(n, n2));
    }
    
    private boolean componentContains(final JComponent component, final int n, final int n2) {
        if (!component.isVisible()) {
            return false;
        }
        final Point locationOnScreen = component.getLocationOnScreen();
        final Dimension size = component.getSize();
        if (n > locationOnScreen.x && n < locationOnScreen.x + size.width && n2 > locationOnScreen.y && n2 < locationOnScreen.y + size.height) {
            return true;
        }
        for (int i = 0; i < component.getComponentCount(); ++i) {
            final JComponent component2 = (JComponent)component.getComponent(i);
            if (component2 instanceof JMenu && this.componentContains(((JMenu)component2).getPopupMenu(), n, n2)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean menuContains(final int n, final int n2) {
        return this.componentContains(this, n, n2);
    }
    
    public class ShowThread extends Thread
    {
        int m_Xp;
        int m_Yp;
        
        public ShowThread(final int xp, final int yp) {
            this.m_Xp = xp;
            this.m_Yp = yp;
        }
        
        public void run() {
            final Dimension preferredSize = SwingTrayPopup.this.getPreferredSize();
            final JDialog dialog = new JDialog();
            dialog.setUndecorated(true);
            dialog.setSize(0, 0);
            dialog.setVisible(true);
            final Point locationOnScreen = dialog.getLocationOnScreen();
            SwingTrayPopup.this.show(dialog, this.m_Xp - preferredSize.width - locationOnScreen.x, this.m_Yp - preferredSize.height - locationOnScreen.y);
            SwingTrayPopup.this.updateUI();
        }
    }
    
    private class ClickListener extends MouseAdapter
    {
        public void mousePressed(final MouseEvent mouseEvent) {
            if (!SwingTrayPopup.this.menuContains(mouseEvent.getX(), mouseEvent.getY())) {
                SwingTrayPopup.this.setVisible(false);
                WindowsTrayIcon.setMouseClickHook(null);
            }
        }
    }
    
    private class ActivateListener extends MouseAdapter
    {
        public void mousePressed(final MouseEvent mouseEvent) {
            if (mouseEvent.isPopupTrigger() && (mouseEvent.getModifiers() & 0x8) != 0x0 && mouseEvent.getClickCount() == 1) {
                SwingTrayPopup.this.showMenu(mouseEvent.getX(), mouseEvent.getY());
            }
        }
    }
    
    private class InvokeMenu implements Runnable
    {
        int m_Xp;
        int m_Yp;
        
        public InvokeMenu(final int xp, final int yp) {
            this.m_Xp = xp;
            this.m_Yp = yp;
        }
        
        public void run() {
            final TrayDummyComponent dummyComponent = WindowsTrayIcon.getDummyComponent();
            WindowsTrayIcon.setMouseClickHook(new ClickListener());
            final Dimension preferredSize = SwingTrayPopup.this.getPreferredSize();
            SwingTrayPopup.this.show(dummyComponent, this.m_Xp - preferredSize.width, this.m_Yp - preferredSize.height);
            WindowsTrayIcon.setAlwaysOnTop(dummyComponent, true);
            SwingTrayPopup.this.updateUI();
        }
    }
}
