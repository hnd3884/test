package com.sun.java.swing.plaf.motif;

import javax.swing.border.EmptyBorder;
import javax.swing.plaf.ComponentUI;
import javax.swing.JScrollBar;
import java.awt.Component;
import java.beans.PropertyChangeEvent;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import java.beans.PropertyChangeListener;
import javax.swing.border.CompoundBorder;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicScrollPaneUI;

public class MotifScrollPaneUI extends BasicScrollPaneUI
{
    private static final Border vsbMarginBorderR;
    private static final Border vsbMarginBorderL;
    private static final Border hsbMarginBorder;
    private CompoundBorder vsbBorder;
    private CompoundBorder hsbBorder;
    private PropertyChangeListener propertyChangeHandler;
    
    @Override
    protected void installListeners(final JScrollPane scrollPane) {
        super.installListeners(scrollPane);
        scrollPane.addPropertyChangeListener(this.propertyChangeHandler = this.createPropertyChangeHandler());
    }
    
    @Override
    protected void uninstallListeners(final JComponent component) {
        super.uninstallListeners(component);
        component.removePropertyChangeListener(this.propertyChangeHandler);
    }
    
    private PropertyChangeListener createPropertyChangeHandler() {
        return new PropertyChangeListener() {
            @Override
            public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
                if (propertyChangeEvent.getPropertyName().equals("componentOrientation")) {
                    final JScrollPane scrollPane = (JScrollPane)propertyChangeEvent.getSource();
                    final JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
                    if (verticalScrollBar != null && MotifScrollPaneUI.this.vsbBorder != null && verticalScrollBar.getBorder() == MotifScrollPaneUI.this.vsbBorder) {
                        if (MotifGraphicsUtils.isLeftToRight(scrollPane)) {
                            MotifScrollPaneUI.this.vsbBorder = new CompoundBorder(MotifScrollPaneUI.vsbMarginBorderR, MotifScrollPaneUI.this.vsbBorder.getInsideBorder());
                        }
                        else {
                            MotifScrollPaneUI.this.vsbBorder = new CompoundBorder(MotifScrollPaneUI.vsbMarginBorderL, MotifScrollPaneUI.this.vsbBorder.getInsideBorder());
                        }
                        verticalScrollBar.setBorder(MotifScrollPaneUI.this.vsbBorder);
                    }
                }
            }
        };
    }
    
    @Override
    protected void installDefaults(final JScrollPane scrollPane) {
        super.installDefaults(scrollPane);
        final JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
        if (verticalScrollBar != null) {
            if (MotifGraphicsUtils.isLeftToRight(scrollPane)) {
                this.vsbBorder = new CompoundBorder(MotifScrollPaneUI.vsbMarginBorderR, verticalScrollBar.getBorder());
            }
            else {
                this.vsbBorder = new CompoundBorder(MotifScrollPaneUI.vsbMarginBorderL, verticalScrollBar.getBorder());
            }
            verticalScrollBar.setBorder(this.vsbBorder);
        }
        final JScrollBar horizontalScrollBar = scrollPane.getHorizontalScrollBar();
        if (horizontalScrollBar != null) {
            horizontalScrollBar.setBorder(this.hsbBorder = new CompoundBorder(MotifScrollPaneUI.hsbMarginBorder, horizontalScrollBar.getBorder()));
        }
    }
    
    @Override
    protected void uninstallDefaults(final JScrollPane scrollPane) {
        super.uninstallDefaults(scrollPane);
        final JScrollBar verticalScrollBar = this.scrollpane.getVerticalScrollBar();
        if (verticalScrollBar != null) {
            if (verticalScrollBar.getBorder() == this.vsbBorder) {
                verticalScrollBar.setBorder(null);
            }
            this.vsbBorder = null;
        }
        final JScrollBar horizontalScrollBar = this.scrollpane.getHorizontalScrollBar();
        if (horizontalScrollBar != null) {
            if (horizontalScrollBar.getBorder() == this.hsbBorder) {
                horizontalScrollBar.setBorder(null);
            }
            this.hsbBorder = null;
        }
    }
    
    public static ComponentUI createUI(final JComponent component) {
        return new MotifScrollPaneUI();
    }
    
    static {
        vsbMarginBorderR = new EmptyBorder(0, 4, 0, 0);
        vsbMarginBorderL = new EmptyBorder(0, 0, 0, 4);
        hsbMarginBorder = new EmptyBorder(4, 0, 0, 0);
    }
}
