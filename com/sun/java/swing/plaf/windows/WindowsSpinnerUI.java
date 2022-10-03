package com.sun.java.swing.plaf.windows;

import javax.swing.plaf.UIResource;
import javax.swing.UIManager;
import java.awt.Component;
import java.awt.Graphics;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicSpinnerUI;

public class WindowsSpinnerUI extends BasicSpinnerUI
{
    public static ComponentUI createUI(final JComponent component) {
        return new WindowsSpinnerUI();
    }
    
    @Override
    public void paint(final Graphics graphics, final JComponent component) {
        if (XPStyle.getXP() != null) {
            this.paintXPBackground(graphics, component);
        }
        super.paint(graphics, component);
    }
    
    private TMSchema.State getXPState(final JComponent component) {
        TMSchema.State state = TMSchema.State.NORMAL;
        if (!component.isEnabled()) {
            state = TMSchema.State.DISABLED;
        }
        return state;
    }
    
    private void paintXPBackground(final Graphics graphics, final JComponent component) {
        final XPStyle xp = XPStyle.getXP();
        if (xp == null) {
            return;
        }
        xp.getSkin(component, TMSchema.Part.EP_EDIT).paintSkin(graphics, 0, 0, component.getWidth(), component.getHeight(), this.getXPState(component));
    }
    
    @Override
    protected Component createPreviousButton() {
        if (XPStyle.getXP() != null) {
            final XPStyle.GlyphButton glyphButton = new XPStyle.GlyphButton(this.spinner, TMSchema.Part.SPNP_DOWN);
            glyphButton.setPreferredSize(UIManager.getDimension("Spinner.arrowButtonSize"));
            glyphButton.setRequestFocusEnabled(false);
            this.installPreviousButtonListeners(glyphButton);
            return glyphButton;
        }
        return super.createPreviousButton();
    }
    
    @Override
    protected Component createNextButton() {
        if (XPStyle.getXP() != null) {
            final XPStyle.GlyphButton glyphButton = new XPStyle.GlyphButton(this.spinner, TMSchema.Part.SPNP_UP);
            glyphButton.setPreferredSize(UIManager.getDimension("Spinner.arrowButtonSize"));
            glyphButton.setRequestFocusEnabled(false);
            this.installNextButtonListeners(glyphButton);
            return glyphButton;
        }
        return super.createNextButton();
    }
    
    private UIResource getUIResource(final Object[] array) {
        for (int i = 0; i < array.length; ++i) {
            if (array[i] instanceof UIResource) {
                return (UIResource)array[i];
            }
        }
        return null;
    }
}
