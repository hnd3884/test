package com.sun.java.swing.plaf.windows;

import javax.swing.plaf.basic.BasicGraphicsUtils;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Graphics;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.AbstractButton;
import sun.awt.AppContext;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import java.awt.Color;
import javax.swing.plaf.basic.BasicRadioButtonUI;

public class WindowsRadioButtonUI extends BasicRadioButtonUI
{
    private static final Object WINDOWS_RADIO_BUTTON_UI_KEY;
    protected int dashedRectGapX;
    protected int dashedRectGapY;
    protected int dashedRectGapWidth;
    protected int dashedRectGapHeight;
    protected Color focusColor;
    private boolean initialized;
    
    public WindowsRadioButtonUI() {
        this.initialized = false;
    }
    
    public static ComponentUI createUI(final JComponent component) {
        final AppContext appContext = AppContext.getAppContext();
        WindowsRadioButtonUI windowsRadioButtonUI = (WindowsRadioButtonUI)appContext.get(WindowsRadioButtonUI.WINDOWS_RADIO_BUTTON_UI_KEY);
        if (windowsRadioButtonUI == null) {
            windowsRadioButtonUI = new WindowsRadioButtonUI();
            appContext.put(WindowsRadioButtonUI.WINDOWS_RADIO_BUTTON_UI_KEY, windowsRadioButtonUI);
        }
        return windowsRadioButtonUI;
    }
    
    public void installDefaults(final AbstractButton abstractButton) {
        super.installDefaults(abstractButton);
        if (!this.initialized) {
            this.dashedRectGapX = (int)UIManager.get("Button.dashedRectGapX");
            this.dashedRectGapY = (int)UIManager.get("Button.dashedRectGapY");
            this.dashedRectGapWidth = (int)UIManager.get("Button.dashedRectGapWidth");
            this.dashedRectGapHeight = (int)UIManager.get("Button.dashedRectGapHeight");
            this.focusColor = UIManager.getColor(this.getPropertyPrefix() + "focus");
            this.initialized = true;
        }
        if (XPStyle.getXP() != null) {
            LookAndFeel.installProperty(abstractButton, "rolloverEnabled", Boolean.TRUE);
        }
    }
    
    @Override
    protected void uninstallDefaults(final AbstractButton abstractButton) {
        super.uninstallDefaults(abstractButton);
        this.initialized = false;
    }
    
    protected Color getFocusColor() {
        return this.focusColor;
    }
    
    @Override
    protected void paintText(final Graphics graphics, final AbstractButton abstractButton, final Rectangle rectangle, final String s) {
        WindowsGraphicsUtils.paintText(graphics, abstractButton, rectangle, s, this.getTextShiftOffset());
    }
    
    @Override
    protected void paintFocus(final Graphics graphics, final Rectangle rectangle, final Dimension dimension) {
        graphics.setColor(this.getFocusColor());
        BasicGraphicsUtils.drawDashedRect(graphics, rectangle.x, rectangle.y, rectangle.width, rectangle.height);
    }
    
    @Override
    public Dimension getPreferredSize(final JComponent component) {
        final Dimension preferredSize = super.getPreferredSize(component);
        final AbstractButton abstractButton = (AbstractButton)component;
        if (preferredSize != null && abstractButton.isFocusPainted()) {
            if (preferredSize.width % 2 == 0) {
                final Dimension dimension = preferredSize;
                ++dimension.width;
            }
            if (preferredSize.height % 2 == 0) {
                final Dimension dimension2 = preferredSize;
                ++dimension2.height;
            }
        }
        return preferredSize;
    }
    
    static {
        WINDOWS_RADIO_BUTTON_UI_KEY = new Object();
    }
}
