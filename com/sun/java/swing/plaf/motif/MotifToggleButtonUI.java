package com.sun.java.swing.plaf.motif;

import javax.swing.border.Border;
import java.awt.Component;
import java.awt.Insets;
import java.awt.Dimension;
import javax.swing.plaf.UIResource;
import java.awt.Graphics;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.AbstractButton;
import sun.awt.AppContext;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import java.awt.Color;
import javax.swing.plaf.basic.BasicToggleButtonUI;

public class MotifToggleButtonUI extends BasicToggleButtonUI
{
    private static final Object MOTIF_TOGGLE_BUTTON_UI_KEY;
    protected Color selectColor;
    private boolean defaults_initialized;
    
    public MotifToggleButtonUI() {
        this.defaults_initialized = false;
    }
    
    public static ComponentUI createUI(final JComponent component) {
        final AppContext appContext = AppContext.getAppContext();
        MotifToggleButtonUI motifToggleButtonUI = (MotifToggleButtonUI)appContext.get(MotifToggleButtonUI.MOTIF_TOGGLE_BUTTON_UI_KEY);
        if (motifToggleButtonUI == null) {
            motifToggleButtonUI = new MotifToggleButtonUI();
            appContext.put(MotifToggleButtonUI.MOTIF_TOGGLE_BUTTON_UI_KEY, motifToggleButtonUI);
        }
        return motifToggleButtonUI;
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
    protected void paintButtonPressed(final Graphics graphics, final AbstractButton abstractButton) {
        if (abstractButton.isContentAreaFilled()) {
            final Color color = graphics.getColor();
            final Dimension size = abstractButton.getSize();
            final Insets insets = abstractButton.getInsets();
            final Insets margin = abstractButton.getMargin();
            if (abstractButton.getBackground() instanceof UIResource) {
                graphics.setColor(this.getSelectColor());
            }
            graphics.fillRect(insets.left - margin.left, insets.top - margin.top, size.width - (insets.left - margin.left) - (insets.right - margin.right), size.height - (insets.top - margin.top) - (insets.bottom - margin.bottom));
            graphics.setColor(color);
        }
    }
    
    public Insets getInsets(final JComponent component) {
        final Border border = component.getBorder();
        return (border != null) ? border.getBorderInsets(component) : new Insets(0, 0, 0, 0);
    }
    
    static {
        MOTIF_TOGGLE_BUTTON_UI_KEY = new Object();
    }
}
