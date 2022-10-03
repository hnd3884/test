package com.sun.java.swing.plaf.motif;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Graphics;
import javax.swing.UIManager;
import javax.swing.AbstractButton;
import sun.awt.AppContext;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import java.awt.Color;
import javax.swing.plaf.basic.BasicRadioButtonUI;

public class MotifRadioButtonUI extends BasicRadioButtonUI
{
    private static final Object MOTIF_RADIO_BUTTON_UI_KEY;
    protected Color focusColor;
    private boolean defaults_initialized;
    
    public MotifRadioButtonUI() {
        this.defaults_initialized = false;
    }
    
    public static ComponentUI createUI(final JComponent component) {
        final AppContext appContext = AppContext.getAppContext();
        MotifRadioButtonUI motifRadioButtonUI = (MotifRadioButtonUI)appContext.get(MotifRadioButtonUI.MOTIF_RADIO_BUTTON_UI_KEY);
        if (motifRadioButtonUI == null) {
            motifRadioButtonUI = new MotifRadioButtonUI();
            appContext.put(MotifRadioButtonUI.MOTIF_RADIO_BUTTON_UI_KEY, motifRadioButtonUI);
        }
        return motifRadioButtonUI;
    }
    
    public void installDefaults(final AbstractButton abstractButton) {
        super.installDefaults(abstractButton);
        if (!this.defaults_initialized) {
            this.focusColor = UIManager.getColor(this.getPropertyPrefix() + "focus");
            this.defaults_initialized = true;
        }
    }
    
    @Override
    protected void uninstallDefaults(final AbstractButton abstractButton) {
        super.uninstallDefaults(abstractButton);
        this.defaults_initialized = false;
    }
    
    protected Color getFocusColor() {
        return this.focusColor;
    }
    
    @Override
    protected void paintFocus(final Graphics graphics, final Rectangle rectangle, final Dimension dimension) {
        graphics.setColor(this.getFocusColor());
        graphics.drawRect(0, 0, dimension.width - 1, dimension.height - 1);
    }
    
    static {
        MOTIF_RADIO_BUTTON_UI_KEY = new Object();
    }
}
