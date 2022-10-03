package javax.swing.plaf.metal;

import javax.swing.Icon;
import java.awt.Insets;
import java.awt.FontMetrics;
import java.awt.Font;
import java.awt.Dimension;
import javax.swing.ButtonModel;
import java.awt.Shape;
import javax.swing.text.View;
import java.awt.Component;
import javax.swing.SwingUtilities;
import java.awt.Rectangle;
import sun.swing.SwingUtilities2;
import java.awt.Graphics;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.AbstractButton;
import sun.awt.AppContext;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import java.awt.Color;
import javax.swing.plaf.basic.BasicRadioButtonUI;

public class MetalRadioButtonUI extends BasicRadioButtonUI
{
    private static final Object METAL_RADIO_BUTTON_UI_KEY;
    protected Color focusColor;
    protected Color selectColor;
    protected Color disabledTextColor;
    private boolean defaults_initialized;
    
    public MetalRadioButtonUI() {
        this.defaults_initialized = false;
    }
    
    public static ComponentUI createUI(final JComponent component) {
        final AppContext appContext = AppContext.getAppContext();
        MetalRadioButtonUI metalRadioButtonUI = (MetalRadioButtonUI)appContext.get(MetalRadioButtonUI.METAL_RADIO_BUTTON_UI_KEY);
        if (metalRadioButtonUI == null) {
            metalRadioButtonUI = new MetalRadioButtonUI();
            appContext.put(MetalRadioButtonUI.METAL_RADIO_BUTTON_UI_KEY, metalRadioButtonUI);
        }
        return metalRadioButtonUI;
    }
    
    public void installDefaults(final AbstractButton abstractButton) {
        super.installDefaults(abstractButton);
        if (!this.defaults_initialized) {
            this.focusColor = UIManager.getColor(this.getPropertyPrefix() + "focus");
            this.selectColor = UIManager.getColor(this.getPropertyPrefix() + "select");
            this.disabledTextColor = UIManager.getColor(this.getPropertyPrefix() + "disabledText");
            this.defaults_initialized = true;
        }
        LookAndFeel.installProperty(abstractButton, "opaque", Boolean.TRUE);
    }
    
    @Override
    protected void uninstallDefaults(final AbstractButton abstractButton) {
        super.uninstallDefaults(abstractButton);
        this.defaults_initialized = false;
    }
    
    protected Color getSelectColor() {
        return this.selectColor;
    }
    
    protected Color getDisabledTextColor() {
        return this.disabledTextColor;
    }
    
    protected Color getFocusColor() {
        return this.focusColor;
    }
    
    @Override
    public synchronized void paint(final Graphics graphics, final JComponent component) {
        final AbstractButton abstractButton = (AbstractButton)component;
        final ButtonModel model = abstractButton.getModel();
        final Dimension size = component.getSize();
        final int width = size.width;
        final int height = size.height;
        final Font font = component.getFont();
        graphics.setFont(font);
        final FontMetrics fontMetrics = SwingUtilities2.getFontMetrics(component, graphics, font);
        final Rectangle rectangle = new Rectangle(size);
        final Rectangle rectangle2 = new Rectangle();
        final Rectangle rectangle3 = new Rectangle();
        final Insets insets = component.getInsets();
        final Rectangle rectangle4 = rectangle;
        rectangle4.x += insets.left;
        final Rectangle rectangle5 = rectangle;
        rectangle5.y += insets.top;
        final Rectangle rectangle6 = rectangle;
        rectangle6.width -= insets.right + rectangle.x;
        final Rectangle rectangle7 = rectangle;
        rectangle7.height -= insets.bottom + rectangle.y;
        Icon icon = abstractButton.getIcon();
        final String layoutCompoundLabel = SwingUtilities.layoutCompoundLabel(component, fontMetrics, abstractButton.getText(), (icon != null) ? icon : this.getDefaultIcon(), abstractButton.getVerticalAlignment(), abstractButton.getHorizontalAlignment(), abstractButton.getVerticalTextPosition(), abstractButton.getHorizontalTextPosition(), rectangle, rectangle2, rectangle3, abstractButton.getIconTextGap());
        if (component.isOpaque()) {
            graphics.setColor(abstractButton.getBackground());
            graphics.fillRect(0, 0, size.width, size.height);
        }
        if (icon != null) {
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
            icon.paintIcon(component, graphics, rectangle2.x, rectangle2.y);
        }
        else {
            this.getDefaultIcon().paintIcon(component, graphics, rectangle2.x, rectangle2.y);
        }
        if (layoutCompoundLabel != null) {
            final View view = (View)component.getClientProperty("html");
            if (view != null) {
                view.paint(graphics, rectangle3);
            }
            else {
                final int displayedMnemonicIndex = abstractButton.getDisplayedMnemonicIndex();
                if (model.isEnabled()) {
                    graphics.setColor(abstractButton.getForeground());
                }
                else {
                    graphics.setColor(this.getDisabledTextColor());
                }
                SwingUtilities2.drawStringUnderlineCharAt(component, graphics, layoutCompoundLabel, displayedMnemonicIndex, rectangle3.x, rectangle3.y + fontMetrics.getAscent());
            }
            if (abstractButton.hasFocus() && abstractButton.isFocusPainted() && rectangle3.width > 0 && rectangle3.height > 0) {
                this.paintFocus(graphics, rectangle3, size);
            }
        }
    }
    
    @Override
    protected void paintFocus(final Graphics graphics, final Rectangle rectangle, final Dimension dimension) {
        graphics.setColor(this.getFocusColor());
        graphics.drawRect(rectangle.x - 1, rectangle.y - 1, rectangle.width + 1, rectangle.height + 1);
    }
    
    static {
        METAL_RADIO_BUTTON_UI_KEY = new Object();
    }
}
