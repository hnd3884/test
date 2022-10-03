package com.sun.java.swing.plaf.motif;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Shape;
import javax.swing.SwingUtilities;
import java.awt.Component;
import javax.swing.border.AbstractBorder;
import java.awt.Rectangle;
import java.awt.Graphics;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicButtonListener;
import javax.swing.AbstractButton;
import sun.awt.AppContext;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import java.awt.Color;
import javax.swing.plaf.basic.BasicButtonUI;

public class MotifButtonUI extends BasicButtonUI
{
    protected Color selectColor;
    private boolean defaults_initialized;
    private static final Object MOTIF_BUTTON_UI_KEY;
    
    public MotifButtonUI() {
        this.defaults_initialized = false;
    }
    
    public static ComponentUI createUI(final JComponent component) {
        final AppContext appContext = AppContext.getAppContext();
        MotifButtonUI motifButtonUI = (MotifButtonUI)appContext.get(MotifButtonUI.MOTIF_BUTTON_UI_KEY);
        if (motifButtonUI == null) {
            motifButtonUI = new MotifButtonUI();
            appContext.put(MotifButtonUI.MOTIF_BUTTON_UI_KEY, motifButtonUI);
        }
        return motifButtonUI;
    }
    
    @Override
    protected BasicButtonListener createButtonListener(final AbstractButton abstractButton) {
        return new MotifButtonListener(abstractButton);
    }
    
    public void installDefaults(final AbstractButton abstractButton) {
        super.installDefaults(abstractButton);
        if (!this.defaults_initialized) {
            this.selectColor = UIManager.getColor(this.getPropertyPrefix() + "select");
            this.defaults_initialized = true;
        }
        LookAndFeel.installProperty(abstractButton, "opaque", Boolean.FALSE);
    }
    
    @Override
    protected void uninstallDefaults(final AbstractButton abstractButton) {
        super.uninstallDefaults(abstractButton);
        this.defaults_initialized = false;
    }
    
    protected Color getSelectColor() {
        return this.selectColor;
    }
    
    @Override
    public void paint(final Graphics graphics, final JComponent component) {
        this.fillContentArea(graphics, (AbstractButton)component, component.getBackground());
        super.paint(graphics, component);
    }
    
    @Override
    protected void paintIcon(final Graphics graphics, final JComponent component, final Rectangle rectangle) {
        final Shape clip = graphics.getClip();
        final Rectangle interiorRectangle = AbstractBorder.getInteriorRectangle(component, component.getBorder(), 0, 0, component.getWidth(), component.getHeight());
        final Rectangle bounds = clip.getBounds();
        graphics.setClip(SwingUtilities.computeIntersection(bounds.x, bounds.y, bounds.width, bounds.height, interiorRectangle));
        super.paintIcon(graphics, component, rectangle);
        graphics.setClip(clip);
    }
    
    @Override
    protected void paintFocus(final Graphics graphics, final AbstractButton abstractButton, final Rectangle rectangle, final Rectangle rectangle2, final Rectangle rectangle3) {
    }
    
    @Override
    protected void paintButtonPressed(final Graphics graphics, final AbstractButton abstractButton) {
        this.fillContentArea(graphics, abstractButton, this.selectColor);
    }
    
    protected void fillContentArea(final Graphics graphics, final AbstractButton abstractButton, final Color color) {
        if (abstractButton.isContentAreaFilled()) {
            final Insets margin = abstractButton.getMargin();
            final Insets insets = abstractButton.getInsets();
            final Dimension size = abstractButton.getSize();
            graphics.setColor(color);
            graphics.fillRect(insets.left - margin.left, insets.top - margin.top, size.width - (insets.left - margin.left) - (insets.right - margin.right), size.height - (insets.top - margin.top) - (insets.bottom - margin.bottom));
        }
    }
    
    static {
        MOTIF_BUTTON_UI_KEY = new Object();
    }
}
