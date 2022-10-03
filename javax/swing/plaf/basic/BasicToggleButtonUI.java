package javax.swing.plaf.basic;

import javax.swing.Icon;
import java.awt.Component;
import java.awt.Insets;
import java.awt.FontMetrics;
import java.awt.Dimension;
import javax.swing.ButtonModel;
import java.awt.Shape;
import javax.swing.text.View;
import javax.swing.SwingUtilities;
import java.awt.Rectangle;
import javax.swing.AbstractButton;
import java.awt.Graphics;
import sun.awt.AppContext;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;

public class BasicToggleButtonUI extends BasicButtonUI
{
    private static final Object BASIC_TOGGLE_BUTTON_UI_KEY;
    private static final String propertyPrefix = "ToggleButton.";
    
    public static ComponentUI createUI(final JComponent component) {
        final AppContext appContext = AppContext.getAppContext();
        BasicToggleButtonUI basicToggleButtonUI = (BasicToggleButtonUI)appContext.get(BasicToggleButtonUI.BASIC_TOGGLE_BUTTON_UI_KEY);
        if (basicToggleButtonUI == null) {
            basicToggleButtonUI = new BasicToggleButtonUI();
            appContext.put(BasicToggleButtonUI.BASIC_TOGGLE_BUTTON_UI_KEY, basicToggleButtonUI);
        }
        return basicToggleButtonUI;
    }
    
    @Override
    protected String getPropertyPrefix() {
        return "ToggleButton.";
    }
    
    @Override
    public void paint(final Graphics graphics, final JComponent component) {
        final AbstractButton abstractButton = (AbstractButton)component;
        final ButtonModel model = abstractButton.getModel();
        final Dimension size = abstractButton.getSize();
        final FontMetrics fontMetrics = graphics.getFontMetrics();
        final Insets insets = component.getInsets();
        final Rectangle rectangle2;
        final Rectangle rectangle = rectangle2 = new Rectangle(size);
        rectangle2.x += insets.left;
        final Rectangle rectangle3 = rectangle;
        rectangle3.y += insets.top;
        final Rectangle rectangle4 = rectangle;
        rectangle4.width -= insets.right + rectangle.x;
        final Rectangle rectangle5 = rectangle;
        rectangle5.height -= insets.bottom + rectangle.y;
        final Rectangle rectangle6 = new Rectangle();
        final Rectangle rectangle7 = new Rectangle();
        graphics.setFont(component.getFont());
        final String layoutCompoundLabel = SwingUtilities.layoutCompoundLabel(component, fontMetrics, abstractButton.getText(), abstractButton.getIcon(), abstractButton.getVerticalAlignment(), abstractButton.getHorizontalAlignment(), abstractButton.getVerticalTextPosition(), abstractButton.getHorizontalTextPosition(), rectangle, rectangle6, rectangle7, (abstractButton.getText() == null) ? 0 : abstractButton.getIconTextGap());
        graphics.setColor(abstractButton.getBackground());
        if ((model.isArmed() && model.isPressed()) || model.isSelected()) {
            this.paintButtonPressed(graphics, abstractButton);
        }
        if (abstractButton.getIcon() != null) {
            this.paintIcon(graphics, abstractButton, rectangle6);
        }
        if (layoutCompoundLabel != null && !layoutCompoundLabel.equals("")) {
            final View view = (View)component.getClientProperty("html");
            if (view != null) {
                view.paint(graphics, rectangle7);
            }
            else {
                this.paintText(graphics, abstractButton, rectangle7, layoutCompoundLabel);
            }
        }
        if (abstractButton.isFocusPainted() && abstractButton.hasFocus()) {
            this.paintFocus(graphics, abstractButton, rectangle, rectangle7, rectangle6);
        }
    }
    
    protected void paintIcon(final Graphics graphics, final AbstractButton abstractButton, final Rectangle rectangle) {
        final ButtonModel model = abstractButton.getModel();
        Icon icon = null;
        if (!model.isEnabled()) {
            if (model.isSelected()) {
                icon = abstractButton.getDisabledSelectedIcon();
            }
            else {
                icon = abstractButton.getDisabledIcon();
            }
        }
        else if (model.isPressed() && model.isArmed()) {
            icon = abstractButton.getPressedIcon();
            if (icon == null) {
                icon = abstractButton.getSelectedIcon();
            }
        }
        else if (model.isSelected()) {
            if (abstractButton.isRolloverEnabled() && model.isRollover()) {
                icon = abstractButton.getRolloverSelectedIcon();
                if (icon == null) {
                    icon = abstractButton.getSelectedIcon();
                }
            }
            else {
                icon = abstractButton.getSelectedIcon();
            }
        }
        else if (abstractButton.isRolloverEnabled() && model.isRollover()) {
            icon = abstractButton.getRolloverIcon();
        }
        if (icon == null) {
            icon = abstractButton.getIcon();
        }
        icon.paintIcon(abstractButton, graphics, rectangle.x, rectangle.y);
    }
    
    @Override
    protected int getTextShiftOffset() {
        return 0;
    }
    
    static {
        BASIC_TOGGLE_BUTTON_UI_KEY = new Object();
    }
}
