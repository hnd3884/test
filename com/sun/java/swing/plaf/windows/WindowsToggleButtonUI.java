package com.sun.java.swing.plaf.windows;

import java.awt.Dimension;
import javax.swing.plaf.basic.BasicGraphicsUtils;
import java.awt.Rectangle;
import java.awt.Graphics;
import javax.swing.LookAndFeel;
import java.awt.Component;
import javax.swing.UIManager;
import javax.swing.AbstractButton;
import sun.awt.AppContext;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import java.awt.Color;
import javax.swing.plaf.basic.BasicToggleButtonUI;

public class WindowsToggleButtonUI extends BasicToggleButtonUI
{
    protected int dashedRectGapX;
    protected int dashedRectGapY;
    protected int dashedRectGapWidth;
    protected int dashedRectGapHeight;
    protected Color focusColor;
    private static final Object WINDOWS_TOGGLE_BUTTON_UI_KEY;
    private boolean defaults_initialized;
    private transient Color cachedSelectedColor;
    private transient Color cachedBackgroundColor;
    private transient Color cachedHighlightColor;
    
    public WindowsToggleButtonUI() {
        this.defaults_initialized = false;
        this.cachedSelectedColor = null;
        this.cachedBackgroundColor = null;
        this.cachedHighlightColor = null;
    }
    
    public static ComponentUI createUI(final JComponent component) {
        final AppContext appContext = AppContext.getAppContext();
        WindowsToggleButtonUI windowsToggleButtonUI = (WindowsToggleButtonUI)appContext.get(WindowsToggleButtonUI.WINDOWS_TOGGLE_BUTTON_UI_KEY);
        if (windowsToggleButtonUI == null) {
            windowsToggleButtonUI = new WindowsToggleButtonUI();
            appContext.put(WindowsToggleButtonUI.WINDOWS_TOGGLE_BUTTON_UI_KEY, windowsToggleButtonUI);
        }
        return windowsToggleButtonUI;
    }
    
    @Override
    protected void installDefaults(final AbstractButton abstractButton) {
        super.installDefaults(abstractButton);
        if (!this.defaults_initialized) {
            final String propertyPrefix = this.getPropertyPrefix();
            this.dashedRectGapX = (int)UIManager.get("Button.dashedRectGapX");
            this.dashedRectGapY = (int)UIManager.get("Button.dashedRectGapY");
            this.dashedRectGapWidth = (int)UIManager.get("Button.dashedRectGapWidth");
            this.dashedRectGapHeight = (int)UIManager.get("Button.dashedRectGapHeight");
            this.focusColor = UIManager.getColor(propertyPrefix + "focus");
            this.defaults_initialized = true;
        }
        final XPStyle xp = XPStyle.getXP();
        if (xp != null) {
            abstractButton.setBorder(xp.getBorder(abstractButton, WindowsButtonUI.getXPButtonType(abstractButton)));
            LookAndFeel.installProperty(abstractButton, "opaque", Boolean.FALSE);
            LookAndFeel.installProperty(abstractButton, "rolloverEnabled", Boolean.TRUE);
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
    protected void paintButtonPressed(final Graphics graphics, final AbstractButton abstractButton) {
        if (XPStyle.getXP() == null && abstractButton.isContentAreaFilled()) {
            final Color color = graphics.getColor();
            final Color background = abstractButton.getBackground();
            final Color color2 = UIManager.getColor("ToggleButton.highlight");
            if (background != this.cachedBackgroundColor || color2 != this.cachedHighlightColor) {
                final int red = background.getRed();
                final int red2 = color2.getRed();
                final int green = background.getGreen();
                final int green2 = color2.getGreen();
                final int blue = background.getBlue();
                final int blue2 = color2.getBlue();
                this.cachedSelectedColor = new Color(Math.min(red, red2) + Math.abs(red - red2) / 2, Math.min(green, green2) + Math.abs(green - green2) / 2, Math.min(blue, blue2) + Math.abs(blue - blue2) / 2);
                this.cachedBackgroundColor = background;
                this.cachedHighlightColor = color2;
            }
            graphics.setColor(this.cachedSelectedColor);
            graphics.fillRect(0, 0, abstractButton.getWidth(), abstractButton.getHeight());
            graphics.setColor(color);
        }
    }
    
    @Override
    public void paint(final Graphics graphics, final JComponent component) {
        if (XPStyle.getXP() != null) {
            WindowsButtonUI.paintXPButtonBackground(graphics, component);
        }
        super.paint(graphics, component);
    }
    
    @Override
    protected void paintText(final Graphics graphics, final AbstractButton abstractButton, final Rectangle rectangle, final String s) {
        WindowsGraphicsUtils.paintText(graphics, abstractButton, rectangle, s, this.getTextShiftOffset());
    }
    
    @Override
    protected void paintFocus(final Graphics graphics, final AbstractButton abstractButton, final Rectangle rectangle, final Rectangle rectangle2, final Rectangle rectangle3) {
        graphics.setColor(this.getFocusColor());
        BasicGraphicsUtils.drawDashedRect(graphics, this.dashedRectGapX, this.dashedRectGapY, abstractButton.getWidth() - this.dashedRectGapWidth, abstractButton.getHeight() - this.dashedRectGapHeight);
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
        WINDOWS_TOGGLE_BUTTON_UI_KEY = new Object();
    }
}
