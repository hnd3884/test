package com.sun.java.swing.plaf.windows;

import javax.swing.border.CompoundBorder;
import java.awt.Insets;
import javax.swing.border.Border;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JToolBar;
import javax.swing.JRadioButton;
import javax.swing.JCheckBox;
import java.awt.Dimension;
import javax.swing.plaf.basic.BasicGraphicsUtils;
import java.awt.Graphics;
import javax.swing.LookAndFeel;
import java.awt.Component;
import javax.swing.UIManager;
import javax.swing.AbstractButton;
import sun.awt.AppContext;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import java.awt.Rectangle;
import java.awt.Color;
import javax.swing.plaf.basic.BasicButtonUI;

public class WindowsButtonUI extends BasicButtonUI
{
    protected int dashedRectGapX;
    protected int dashedRectGapY;
    protected int dashedRectGapWidth;
    protected int dashedRectGapHeight;
    protected Color focusColor;
    private boolean defaults_initialized;
    private static final Object WINDOWS_BUTTON_UI_KEY;
    private Rectangle viewRect;
    
    public WindowsButtonUI() {
        this.defaults_initialized = false;
        this.viewRect = new Rectangle();
    }
    
    public static ComponentUI createUI(final JComponent component) {
        final AppContext appContext = AppContext.getAppContext();
        WindowsButtonUI windowsButtonUI = (WindowsButtonUI)appContext.get(WindowsButtonUI.WINDOWS_BUTTON_UI_KEY);
        if (windowsButtonUI == null) {
            windowsButtonUI = new WindowsButtonUI();
            appContext.put(WindowsButtonUI.WINDOWS_BUTTON_UI_KEY, windowsButtonUI);
        }
        return windowsButtonUI;
    }
    
    @Override
    protected void installDefaults(final AbstractButton abstractButton) {
        super.installDefaults(abstractButton);
        if (!this.defaults_initialized) {
            final String propertyPrefix = this.getPropertyPrefix();
            this.dashedRectGapX = UIManager.getInt(propertyPrefix + "dashedRectGapX");
            this.dashedRectGapY = UIManager.getInt(propertyPrefix + "dashedRectGapY");
            this.dashedRectGapWidth = UIManager.getInt(propertyPrefix + "dashedRectGapWidth");
            this.dashedRectGapHeight = UIManager.getInt(propertyPrefix + "dashedRectGapHeight");
            this.focusColor = UIManager.getColor(propertyPrefix + "focus");
            this.defaults_initialized = true;
        }
        final XPStyle xp = XPStyle.getXP();
        if (xp != null) {
            abstractButton.setBorder(xp.getBorder(abstractButton, getXPButtonType(abstractButton)));
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
    protected void paintText(final Graphics graphics, final AbstractButton abstractButton, final Rectangle rectangle, final String s) {
        WindowsGraphicsUtils.paintText(graphics, abstractButton, rectangle, s, this.getTextShiftOffset());
    }
    
    @Override
    protected void paintFocus(final Graphics graphics, final AbstractButton abstractButton, final Rectangle rectangle, final Rectangle rectangle2, final Rectangle rectangle3) {
        final int width = abstractButton.getWidth();
        final int height = abstractButton.getHeight();
        graphics.setColor(this.getFocusColor());
        BasicGraphicsUtils.drawDashedRect(graphics, this.dashedRectGapX, this.dashedRectGapY, width - this.dashedRectGapWidth, height - this.dashedRectGapHeight);
    }
    
    @Override
    protected void paintButtonPressed(final Graphics graphics, final AbstractButton abstractButton) {
        this.setTextShiftOffset();
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
    
    @Override
    public void paint(final Graphics graphics, final JComponent component) {
        if (XPStyle.getXP() != null) {
            paintXPButtonBackground(graphics, component);
        }
        super.paint(graphics, component);
    }
    
    static TMSchema.Part getXPButtonType(final AbstractButton abstractButton) {
        if (abstractButton instanceof JCheckBox) {
            return TMSchema.Part.BP_CHECKBOX;
        }
        if (abstractButton instanceof JRadioButton) {
            return TMSchema.Part.BP_RADIOBUTTON;
        }
        return (abstractButton.getParent() instanceof JToolBar) ? TMSchema.Part.TP_BUTTON : TMSchema.Part.BP_PUSHBUTTON;
    }
    
    static TMSchema.State getXPButtonState(final AbstractButton abstractButton) {
        final TMSchema.Part xpButtonType = getXPButtonType(abstractButton);
        final ButtonModel model = abstractButton.getModel();
        TMSchema.State state = TMSchema.State.NORMAL;
        switch (xpButtonType) {
            case BP_RADIOBUTTON:
            case BP_CHECKBOX: {
                if (!model.isEnabled()) {
                    state = (model.isSelected() ? TMSchema.State.CHECKEDDISABLED : TMSchema.State.UNCHECKEDDISABLED);
                    break;
                }
                if (model.isPressed() && model.isArmed()) {
                    state = (model.isSelected() ? TMSchema.State.CHECKEDPRESSED : TMSchema.State.UNCHECKEDPRESSED);
                    break;
                }
                if (model.isRollover()) {
                    state = (model.isSelected() ? TMSchema.State.CHECKEDHOT : TMSchema.State.UNCHECKEDHOT);
                    break;
                }
                state = (model.isSelected() ? TMSchema.State.CHECKEDNORMAL : TMSchema.State.UNCHECKEDNORMAL);
                break;
            }
            case BP_PUSHBUTTON:
            case TP_BUTTON: {
                if (abstractButton.getParent() instanceof JToolBar) {
                    if (model.isArmed() && model.isPressed()) {
                        state = TMSchema.State.PRESSED;
                        break;
                    }
                    if (!model.isEnabled()) {
                        state = TMSchema.State.DISABLED;
                        break;
                    }
                    if (model.isSelected() && model.isRollover()) {
                        state = TMSchema.State.HOTCHECKED;
                        break;
                    }
                    if (model.isSelected()) {
                        state = TMSchema.State.CHECKED;
                        break;
                    }
                    if (model.isRollover()) {
                        state = TMSchema.State.HOT;
                        break;
                    }
                    if (abstractButton.hasFocus()) {
                        state = TMSchema.State.HOT;
                        break;
                    }
                    break;
                }
                else {
                    if ((model.isArmed() && model.isPressed()) || model.isSelected()) {
                        state = TMSchema.State.PRESSED;
                        break;
                    }
                    if (!model.isEnabled()) {
                        state = TMSchema.State.DISABLED;
                        break;
                    }
                    if (model.isRollover() || model.isPressed()) {
                        state = TMSchema.State.HOT;
                        break;
                    }
                    if (abstractButton instanceof JButton && ((JButton)abstractButton).isDefaultButton()) {
                        state = TMSchema.State.DEFAULTED;
                        break;
                    }
                    if (abstractButton.hasFocus()) {
                        state = TMSchema.State.HOT;
                        break;
                    }
                    break;
                }
                break;
            }
            default: {
                state = TMSchema.State.NORMAL;
                break;
            }
        }
        return state;
    }
    
    static void paintXPButtonBackground(final Graphics graphics, final JComponent component) {
        final AbstractButton abstractButton = (AbstractButton)component;
        final XPStyle xp = XPStyle.getXP();
        final TMSchema.Part xpButtonType = getXPButtonType(abstractButton);
        if (abstractButton.isContentAreaFilled() && xp != null) {
            final XPStyle.Skin skin = xp.getSkin(abstractButton, xpButtonType);
            final TMSchema.State xpButtonState = getXPButtonState(abstractButton);
            final Dimension size = component.getSize();
            int n = 0;
            int n2 = 0;
            int width = size.width;
            int height = size.height;
            final Border border = component.getBorder();
            Insets insets;
            if (border != null) {
                insets = getOpaqueInsets(border, component);
            }
            else {
                insets = component.getInsets();
            }
            if (insets != null) {
                n += insets.left;
                n2 += insets.top;
                width -= insets.left + insets.right;
                height -= insets.top + insets.bottom;
            }
            skin.paintSkin(graphics, n, n2, width, height, xpButtonState);
        }
    }
    
    private static Insets getOpaqueInsets(final Border border, final Component component) {
        if (border == null) {
            return null;
        }
        if (border.isBorderOpaque()) {
            return border.getBorderInsets(component);
        }
        if (!(border instanceof CompoundBorder)) {
            return null;
        }
        final CompoundBorder compoundBorder = (CompoundBorder)border;
        final Insets opaqueInsets = getOpaqueInsets(compoundBorder.getOutsideBorder(), component);
        if (opaqueInsets == null || !opaqueInsets.equals(compoundBorder.getOutsideBorder().getBorderInsets(component))) {
            return opaqueInsets;
        }
        final Insets opaqueInsets2 = getOpaqueInsets(compoundBorder.getInsideBorder(), component);
        if (opaqueInsets2 == null) {
            return opaqueInsets;
        }
        return new Insets(opaqueInsets.top + opaqueInsets2.top, opaqueInsets.left + opaqueInsets2.left, opaqueInsets.bottom + opaqueInsets2.bottom, opaqueInsets.right + opaqueInsets2.right);
    }
    
    static {
        WINDOWS_BUTTON_UI_KEY = new Object();
    }
}
