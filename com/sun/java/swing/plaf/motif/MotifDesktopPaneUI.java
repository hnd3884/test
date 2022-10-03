package com.sun.java.swing.plaf.motif;

import java.awt.Dimension;
import javax.swing.JDesktopPane;
import java.awt.Point;
import java.awt.Container;
import javax.swing.JInternalFrame;
import java.awt.Component;
import java.awt.Rectangle;
import javax.swing.SwingUtilities;
import javax.swing.JLayeredPane;
import javax.swing.plaf.UIResource;
import java.io.Serializable;
import javax.swing.DefaultDesktopManager;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicDesktopPaneUI;

public class MotifDesktopPaneUI extends BasicDesktopPaneUI
{
    public static ComponentUI createUI(final JComponent component) {
        return new MotifDesktopPaneUI();
    }
    
    @Override
    protected void installDesktopManager() {
        this.desktopManager = this.desktop.getDesktopManager();
        if (this.desktopManager == null) {
            this.desktopManager = new MotifDesktopManager();
            this.desktop.setDesktopManager(this.desktopManager);
            ((MotifDesktopManager)this.desktopManager).adjustIcons(this.desktop);
        }
    }
    
    public Insets getInsets(final JComponent component) {
        return new Insets(0, 0, 0, 0);
    }
    
    private class DragPane extends JComponent
    {
        @Override
        public void paint(final Graphics graphics) {
            graphics.setColor(Color.darkGray);
            graphics.drawRect(0, 0, this.getWidth() - 1, this.getHeight() - 1);
        }
    }
    
    private class MotifDesktopManager extends DefaultDesktopManager implements Serializable, UIResource
    {
        JComponent dragPane;
        boolean usingDragPane;
        private transient JLayeredPane layeredPaneForDragPane;
        int iconWidth;
        int iconHeight;
        
        private MotifDesktopManager() {
            this.usingDragPane = false;
        }
        
        @Override
        public void setBoundsForFrame(final JComponent component, final int n, final int n2, final int n3, final int n4) {
            if (!this.usingDragPane) {
                final boolean b = component.getWidth() != n3 || component.getHeight() != n4;
                final Rectangle bounds = component.getBounds();
                component.setBounds(n, n2, n3, n4);
                SwingUtilities.computeUnion(n, n2, n3, n4, bounds);
                component.getParent().repaint(bounds.x, bounds.y, bounds.width, bounds.height);
                if (b) {
                    component.validate();
                }
            }
            else {
                final Rectangle bounds2 = this.dragPane.getBounds();
                this.dragPane.setBounds(n, n2, n3, n4);
                SwingUtilities.computeUnion(n, n2, n3, n4, bounds2);
                this.dragPane.getParent().repaint(bounds2.x, bounds2.y, bounds2.width, bounds2.height);
            }
        }
        
        @Override
        public void beginDraggingFrame(final JComponent component) {
            this.usingDragPane = false;
            if (component.getParent() instanceof JLayeredPane) {
                if (this.dragPane == null) {
                    this.dragPane = new DragPane();
                }
                (this.layeredPaneForDragPane = (JLayeredPane)component.getParent()).setLayer(this.dragPane, Integer.MAX_VALUE);
                this.dragPane.setBounds(component.getX(), component.getY(), component.getWidth(), component.getHeight());
                this.layeredPaneForDragPane.add(this.dragPane);
                this.usingDragPane = true;
            }
        }
        
        @Override
        public void dragFrame(final JComponent component, final int n, final int n2) {
            this.setBoundsForFrame(component, n, n2, component.getWidth(), component.getHeight());
        }
        
        @Override
        public void endDraggingFrame(final JComponent component) {
            if (this.usingDragPane) {
                this.layeredPaneForDragPane.remove(this.dragPane);
                this.usingDragPane = false;
                if (component instanceof JInternalFrame) {
                    this.setBoundsForFrame(component, this.dragPane.getX(), this.dragPane.getY(), this.dragPane.getWidth(), this.dragPane.getHeight());
                }
                else if (component instanceof JInternalFrame.JDesktopIcon) {
                    this.adjustBoundsForIcon((JInternalFrame.JDesktopIcon)component, this.dragPane.getX(), this.dragPane.getY());
                }
            }
        }
        
        @Override
        public void beginResizingFrame(final JComponent component, final int n) {
            this.usingDragPane = false;
            if (component.getParent() instanceof JLayeredPane) {
                if (this.dragPane == null) {
                    this.dragPane = new DragPane();
                }
                final JLayeredPane layeredPane = (JLayeredPane)component.getParent();
                layeredPane.setLayer(this.dragPane, Integer.MAX_VALUE);
                this.dragPane.setBounds(component.getX(), component.getY(), component.getWidth(), component.getHeight());
                layeredPane.add(this.dragPane);
                this.usingDragPane = true;
            }
        }
        
        @Override
        public void resizeFrame(final JComponent component, final int n, final int n2, final int n3, final int n4) {
            this.setBoundsForFrame(component, n, n2, n3, n4);
        }
        
        @Override
        public void endResizingFrame(final JComponent component) {
            if (this.usingDragPane) {
                component.getParent().remove(this.dragPane);
                this.usingDragPane = false;
                this.setBoundsForFrame(component, this.dragPane.getX(), this.dragPane.getY(), this.dragPane.getWidth(), this.dragPane.getHeight());
            }
        }
        
        @Override
        public void iconifyFrame(final JInternalFrame internalFrame) {
            final JInternalFrame.JDesktopIcon desktopIcon = internalFrame.getDesktopIcon();
            final Point location = desktopIcon.getLocation();
            this.adjustBoundsForIcon(desktopIcon, location.x, location.y);
            super.iconifyFrame(internalFrame);
        }
        
        protected void adjustIcons(final JDesktopPane desktopPane) {
            final Dimension preferredSize = new JInternalFrame.JDesktopIcon(new JInternalFrame()).getPreferredSize();
            this.iconWidth = preferredSize.width;
            this.iconHeight = preferredSize.height;
            final JInternalFrame[] allFrames = desktopPane.getAllFrames();
            for (int i = 0; i < allFrames.length; ++i) {
                final JInternalFrame.JDesktopIcon desktopIcon = allFrames[i].getDesktopIcon();
                final Point location = desktopIcon.getLocation();
                this.adjustBoundsForIcon(desktopIcon, location.x, location.y);
            }
        }
        
        protected void adjustBoundsForIcon(final JInternalFrame.JDesktopIcon desktopIcon, int n, int n2) {
            final JDesktopPane desktopPane = desktopIcon.getDesktopPane();
            final int height = desktopPane.getHeight();
            final int iconWidth = this.iconWidth;
            final int iconHeight = this.iconHeight;
            desktopPane.repaint(n, n2, iconWidth, iconHeight);
            n = ((n < 0) ? 0 : n);
            n2 = ((n2 < 0) ? 0 : n2);
            n2 = ((n2 >= height) ? (height - 1) : n2);
            final int n3 = n / iconWidth * iconWidth;
            final int n4 = height % iconHeight;
            final int n5 = (n2 - n4) / iconHeight * iconHeight + n4;
            final int n6 = n - n3;
            final int n7 = n2 - n5;
            for (n = ((n6 < iconWidth / 2) ? n3 : (n3 + iconWidth)), n2 = ((n7 < iconHeight / 2) ? n5 : ((n5 + iconHeight < height) ? (n5 + iconHeight) : n5)); this.getIconAt(desktopPane, desktopIcon, n, n2) != null; n += iconWidth) {}
            if (n > desktopPane.getWidth()) {
                return;
            }
            if (desktopIcon.getParent() != null) {
                this.setBoundsForFrame(desktopIcon, n, n2, iconWidth, iconHeight);
            }
            else {
                desktopIcon.setLocation(n, n2);
            }
        }
        
        protected JInternalFrame.JDesktopIcon getIconAt(final JDesktopPane desktopPane, final JInternalFrame.JDesktopIcon desktopIcon, final int n, final int n2) {
            final Component[] components = desktopPane.getComponents();
            for (int i = 0; i < components.length; ++i) {
                final Component component = components[i];
                if (component instanceof JInternalFrame.JDesktopIcon && component != desktopIcon) {
                    final Point location = component.getLocation();
                    if (location.x == n && location.y == n2) {
                        return (JInternalFrame.JDesktopIcon)component;
                    }
                }
            }
            return null;
        }
    }
}
