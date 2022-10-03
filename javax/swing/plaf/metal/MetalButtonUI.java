package javax.swing.plaf.metal;

import java.awt.FontMetrics;
import sun.swing.SwingUtilities2;
import java.awt.Rectangle;
import java.awt.Dimension;
import javax.swing.ButtonModel;
import java.awt.Component;
import javax.swing.plaf.UIResource;
import java.awt.Graphics;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicButtonListener;
import javax.swing.AbstractButton;
import sun.awt.AppContext;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import java.awt.Color;
import javax.swing.plaf.basic.BasicButtonUI;

public class MetalButtonUI extends BasicButtonUI
{
    protected Color focusColor;
    protected Color selectColor;
    protected Color disabledTextColor;
    private static final Object METAL_BUTTON_UI_KEY;
    
    public static ComponentUI createUI(final JComponent component) {
        final AppContext appContext = AppContext.getAppContext();
        MetalButtonUI metalButtonUI = (MetalButtonUI)appContext.get(MetalButtonUI.METAL_BUTTON_UI_KEY);
        if (metalButtonUI == null) {
            metalButtonUI = new MetalButtonUI();
            appContext.put(MetalButtonUI.METAL_BUTTON_UI_KEY, metalButtonUI);
        }
        return metalButtonUI;
    }
    
    public void installDefaults(final AbstractButton abstractButton) {
        super.installDefaults(abstractButton);
    }
    
    public void uninstallDefaults(final AbstractButton abstractButton) {
        super.uninstallDefaults(abstractButton);
    }
    
    @Override
    protected BasicButtonListener createButtonListener(final AbstractButton abstractButton) {
        return super.createButtonListener(abstractButton);
    }
    
    protected Color getSelectColor() {
        return this.selectColor = UIManager.getColor(this.getPropertyPrefix() + "select");
    }
    
    protected Color getDisabledTextColor() {
        return this.disabledTextColor = UIManager.getColor(this.getPropertyPrefix() + "disabledText");
    }
    
    protected Color getFocusColor() {
        return this.focusColor = UIManager.getColor(this.getPropertyPrefix() + "focus");
    }
    
    @Override
    public void update(final Graphics graphics, final JComponent component) {
        final AbstractButton abstractButton = (AbstractButton)component;
        if (component.getBackground() instanceof UIResource && abstractButton.isContentAreaFilled() && component.isEnabled()) {
            final ButtonModel model = abstractButton.getModel();
            if (!MetalUtils.isToolBarButton(component)) {
                if (!model.isArmed() && !model.isPressed() && MetalUtils.drawGradient(component, graphics, "Button.gradient", 0, 0, component.getWidth(), component.getHeight(), true)) {
                    this.paint(graphics, component);
                    return;
                }
            }
            else if (model.isRollover() && MetalUtils.drawGradient(component, graphics, "Button.gradient", 0, 0, component.getWidth(), component.getHeight(), true)) {
                this.paint(graphics, component);
                return;
            }
        }
        super.update(graphics, component);
    }
    
    @Override
    protected void paintButtonPressed(final Graphics graphics, final AbstractButton abstractButton) {
        if (abstractButton.isContentAreaFilled()) {
            final Dimension size = abstractButton.getSize();
            graphics.setColor(this.getSelectColor());
            graphics.fillRect(0, 0, size.width, size.height);
        }
    }
    
    @Override
    protected void paintFocus(final Graphics graphics, final AbstractButton abstractButton, final Rectangle rectangle, final Rectangle bounds, final Rectangle bounds2) {
        final Rectangle rectangle2 = new Rectangle();
        final String text = abstractButton.getText();
        final boolean b = abstractButton.getIcon() != null;
        if (text != null && !text.equals("")) {
            if (!b) {
                rectangle2.setBounds(bounds);
            }
            else {
                rectangle2.setBounds(bounds2.union(bounds));
            }
        }
        else if (b) {
            rectangle2.setBounds(bounds2);
        }
        graphics.setColor(this.getFocusColor());
        graphics.drawRect(rectangle2.x - 1, rectangle2.y - 1, rectangle2.width + 1, rectangle2.height + 1);
    }
    
    @Override
    protected void paintText(final Graphics graphics, final JComponent component, final Rectangle rectangle, final String s) {
        final AbstractButton abstractButton = (AbstractButton)component;
        final ButtonModel model = abstractButton.getModel();
        final FontMetrics fontMetrics = SwingUtilities2.getFontMetrics(component, graphics);
        final int displayedMnemonicIndex = abstractButton.getDisplayedMnemonicIndex();
        if (model.isEnabled()) {
            graphics.setColor(abstractButton.getForeground());
        }
        else {
            graphics.setColor(this.getDisabledTextColor());
        }
        SwingUtilities2.drawStringUnderlineCharAt(component, graphics, s, displayedMnemonicIndex, rectangle.x, rectangle.y + fontMetrics.getAscent());
    }
    
    static {
        METAL_BUTTON_UI_KEY = new Object();
    }
}
