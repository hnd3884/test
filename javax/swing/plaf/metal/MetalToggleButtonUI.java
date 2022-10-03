package javax.swing.plaf.metal;

import java.awt.FontMetrics;
import sun.swing.SwingUtilities2;
import java.awt.Rectangle;
import javax.swing.ButtonModel;
import java.awt.Component;
import javax.swing.plaf.UIResource;
import java.awt.Graphics;
import javax.swing.UIManager;
import javax.swing.AbstractButton;
import sun.awt.AppContext;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import java.awt.Color;
import javax.swing.plaf.basic.BasicToggleButtonUI;

public class MetalToggleButtonUI extends BasicToggleButtonUI
{
    private static final Object METAL_TOGGLE_BUTTON_UI_KEY;
    protected Color focusColor;
    protected Color selectColor;
    protected Color disabledTextColor;
    private boolean defaults_initialized;
    
    public MetalToggleButtonUI() {
        this.defaults_initialized = false;
    }
    
    public static ComponentUI createUI(final JComponent component) {
        final AppContext appContext = AppContext.getAppContext();
        MetalToggleButtonUI metalToggleButtonUI = (MetalToggleButtonUI)appContext.get(MetalToggleButtonUI.METAL_TOGGLE_BUTTON_UI_KEY);
        if (metalToggleButtonUI == null) {
            metalToggleButtonUI = new MetalToggleButtonUI();
            appContext.put(MetalToggleButtonUI.METAL_TOGGLE_BUTTON_UI_KEY, metalToggleButtonUI);
        }
        return metalToggleButtonUI;
    }
    
    public void installDefaults(final AbstractButton abstractButton) {
        super.installDefaults(abstractButton);
        if (!this.defaults_initialized) {
            this.focusColor = UIManager.getColor(this.getPropertyPrefix() + "focus");
            this.selectColor = UIManager.getColor(this.getPropertyPrefix() + "select");
            this.disabledTextColor = UIManager.getColor(this.getPropertyPrefix() + "disabledText");
            this.defaults_initialized = true;
        }
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
    public void update(final Graphics graphics, final JComponent component) {
        final AbstractButton abstractButton = (AbstractButton)component;
        if (component.getBackground() instanceof UIResource && abstractButton.isContentAreaFilled() && component.isEnabled()) {
            final ButtonModel model = abstractButton.getModel();
            if (!MetalUtils.isToolBarButton(component)) {
                if (!model.isArmed() && !model.isPressed() && MetalUtils.drawGradient(component, graphics, "ToggleButton.gradient", 0, 0, component.getWidth(), component.getHeight(), true)) {
                    this.paint(graphics, component);
                    return;
                }
            }
            else if ((model.isRollover() || model.isSelected()) && MetalUtils.drawGradient(component, graphics, "ToggleButton.gradient", 0, 0, component.getWidth(), component.getHeight(), true)) {
                this.paint(graphics, component);
                return;
            }
        }
        super.update(graphics, component);
    }
    
    @Override
    protected void paintButtonPressed(final Graphics graphics, final AbstractButton abstractButton) {
        if (abstractButton.isContentAreaFilled()) {
            graphics.setColor(this.getSelectColor());
            graphics.fillRect(0, 0, abstractButton.getWidth(), abstractButton.getHeight());
        }
    }
    
    @Override
    protected void paintText(final Graphics graphics, final JComponent component, final Rectangle rectangle, final String s) {
        final AbstractButton abstractButton = (AbstractButton)component;
        final ButtonModel model = abstractButton.getModel();
        final FontMetrics fontMetrics = SwingUtilities2.getFontMetrics(abstractButton, graphics);
        final int displayedMnemonicIndex = abstractButton.getDisplayedMnemonicIndex();
        if (model.isEnabled()) {
            graphics.setColor(abstractButton.getForeground());
        }
        else if (model.isSelected()) {
            graphics.setColor(component.getBackground());
        }
        else {
            graphics.setColor(this.getDisabledTextColor());
        }
        SwingUtilities2.drawStringUnderlineCharAt(component, graphics, s, displayedMnemonicIndex, rectangle.x, rectangle.y + fontMetrics.getAscent());
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
    protected void paintIcon(final Graphics graphics, final AbstractButton abstractButton, final Rectangle rectangle) {
        super.paintIcon(graphics, abstractButton, rectangle);
    }
    
    static {
        METAL_TOGGLE_BUTTON_UI_KEY = new Object();
    }
}
