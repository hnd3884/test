package javax.swing.plaf.basic;

import java.awt.Insets;
import javax.swing.SwingUtilities;
import java.awt.Dimension;
import java.awt.FontMetrics;
import javax.swing.Icon;
import java.awt.Component;
import javax.swing.ButtonModel;
import java.awt.Shape;
import javax.swing.text.View;
import sun.swing.SwingUtilities2;
import java.awt.Graphics;
import javax.swing.event.ChangeListener;
import java.beans.PropertyChangeListener;
import java.awt.event.FocusListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseListener;
import javax.swing.plaf.UIResource;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.AbstractButton;
import sun.awt.AppContext;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import java.awt.Rectangle;
import javax.swing.plaf.ButtonUI;

public class BasicButtonUI extends ButtonUI
{
    protected int defaultTextIconGap;
    private int shiftOffset;
    protected int defaultTextShiftOffset;
    private static final String propertyPrefix = "Button.";
    private static final Object BASIC_BUTTON_UI_KEY;
    private static Rectangle viewRect;
    private static Rectangle textRect;
    private static Rectangle iconRect;
    
    public BasicButtonUI() {
        this.shiftOffset = 0;
    }
    
    public static ComponentUI createUI(final JComponent component) {
        final AppContext appContext = AppContext.getAppContext();
        BasicButtonUI basicButtonUI = (BasicButtonUI)appContext.get(BasicButtonUI.BASIC_BUTTON_UI_KEY);
        if (basicButtonUI == null) {
            basicButtonUI = new BasicButtonUI();
            appContext.put(BasicButtonUI.BASIC_BUTTON_UI_KEY, basicButtonUI);
        }
        return basicButtonUI;
    }
    
    protected String getPropertyPrefix() {
        return "Button.";
    }
    
    @Override
    public void installUI(final JComponent component) {
        this.installDefaults((AbstractButton)component);
        this.installListeners((AbstractButton)component);
        this.installKeyboardActions((AbstractButton)component);
        BasicHTML.updateRenderer(component, ((AbstractButton)component).getText());
    }
    
    protected void installDefaults(final AbstractButton abstractButton) {
        final String propertyPrefix = this.getPropertyPrefix();
        this.defaultTextShiftOffset = UIManager.getInt(propertyPrefix + "textShiftOffset");
        if (abstractButton.isContentAreaFilled()) {
            LookAndFeel.installProperty(abstractButton, "opaque", Boolean.TRUE);
        }
        else {
            LookAndFeel.installProperty(abstractButton, "opaque", Boolean.FALSE);
        }
        if (abstractButton.getMargin() == null || abstractButton.getMargin() instanceof UIResource) {
            abstractButton.setMargin(UIManager.getInsets(propertyPrefix + "margin"));
        }
        LookAndFeel.installColorsAndFont(abstractButton, propertyPrefix + "background", propertyPrefix + "foreground", propertyPrefix + "font");
        LookAndFeel.installBorder(abstractButton, propertyPrefix + "border");
        final Object value = UIManager.get(propertyPrefix + "rollover");
        if (value != null) {
            LookAndFeel.installProperty(abstractButton, "rolloverEnabled", value);
        }
        LookAndFeel.installProperty(abstractButton, "iconTextGap", 4);
    }
    
    protected void installListeners(final AbstractButton abstractButton) {
        final BasicButtonListener buttonListener = this.createButtonListener(abstractButton);
        if (buttonListener != null) {
            abstractButton.addMouseListener(buttonListener);
            abstractButton.addMouseMotionListener(buttonListener);
            abstractButton.addFocusListener(buttonListener);
            abstractButton.addPropertyChangeListener(buttonListener);
            abstractButton.addChangeListener(buttonListener);
        }
    }
    
    protected void installKeyboardActions(final AbstractButton abstractButton) {
        final BasicButtonListener buttonListener = this.getButtonListener(abstractButton);
        if (buttonListener != null) {
            buttonListener.installKeyboardActions(abstractButton);
        }
    }
    
    @Override
    public void uninstallUI(final JComponent component) {
        this.uninstallKeyboardActions((AbstractButton)component);
        this.uninstallListeners((AbstractButton)component);
        this.uninstallDefaults((AbstractButton)component);
        BasicHTML.updateRenderer(component, "");
    }
    
    protected void uninstallKeyboardActions(final AbstractButton abstractButton) {
        final BasicButtonListener buttonListener = this.getButtonListener(abstractButton);
        if (buttonListener != null) {
            buttonListener.uninstallKeyboardActions(abstractButton);
        }
    }
    
    protected void uninstallListeners(final AbstractButton abstractButton) {
        final BasicButtonListener buttonListener = this.getButtonListener(abstractButton);
        if (buttonListener != null) {
            abstractButton.removeMouseListener(buttonListener);
            abstractButton.removeMouseMotionListener(buttonListener);
            abstractButton.removeFocusListener(buttonListener);
            abstractButton.removeChangeListener(buttonListener);
            abstractButton.removePropertyChangeListener(buttonListener);
        }
    }
    
    protected void uninstallDefaults(final AbstractButton abstractButton) {
        LookAndFeel.uninstallBorder(abstractButton);
    }
    
    protected BasicButtonListener createButtonListener(final AbstractButton abstractButton) {
        return new BasicButtonListener(abstractButton);
    }
    
    public int getDefaultTextIconGap(final AbstractButton abstractButton) {
        return this.defaultTextIconGap;
    }
    
    @Override
    public void paint(final Graphics graphics, final JComponent component) {
        final AbstractButton abstractButton = (AbstractButton)component;
        final ButtonModel model = abstractButton.getModel();
        final String layout = this.layout(abstractButton, SwingUtilities2.getFontMetrics(abstractButton, graphics), abstractButton.getWidth(), abstractButton.getHeight());
        this.clearTextShiftOffset();
        if (model.isArmed() && model.isPressed()) {
            this.paintButtonPressed(graphics, abstractButton);
        }
        if (abstractButton.getIcon() != null) {
            this.paintIcon(graphics, component, BasicButtonUI.iconRect);
        }
        if (layout != null && !layout.equals("")) {
            final View view = (View)component.getClientProperty("html");
            if (view != null) {
                view.paint(graphics, BasicButtonUI.textRect);
            }
            else {
                this.paintText(graphics, abstractButton, BasicButtonUI.textRect, layout);
            }
        }
        if (abstractButton.isFocusPainted() && abstractButton.hasFocus()) {
            this.paintFocus(graphics, abstractButton, BasicButtonUI.viewRect, BasicButtonUI.textRect, BasicButtonUI.iconRect);
        }
    }
    
    protected void paintIcon(final Graphics graphics, final JComponent component, final Rectangle rectangle) {
        final AbstractButton abstractButton = (AbstractButton)component;
        final ButtonModel model = abstractButton.getModel();
        Icon icon = abstractButton.getIcon();
        Icon icon2 = null;
        if (icon == null) {
            return;
        }
        Icon selectedIcon = null;
        if (model.isSelected()) {
            selectedIcon = abstractButton.getSelectedIcon();
            if (selectedIcon != null) {
                icon = selectedIcon;
            }
        }
        if (!model.isEnabled()) {
            if (model.isSelected()) {
                icon2 = abstractButton.getDisabledSelectedIcon();
                if (icon2 == null) {
                    icon2 = selectedIcon;
                }
            }
            if (icon2 == null) {
                icon2 = abstractButton.getDisabledIcon();
            }
        }
        else if (model.isPressed() && model.isArmed()) {
            icon2 = abstractButton.getPressedIcon();
            if (icon2 != null) {
                this.clearTextShiftOffset();
            }
        }
        else if (abstractButton.isRolloverEnabled() && model.isRollover()) {
            if (model.isSelected()) {
                icon2 = abstractButton.getRolloverSelectedIcon();
                if (icon2 == null) {
                    icon2 = selectedIcon;
                }
            }
            if (icon2 == null) {
                icon2 = abstractButton.getRolloverIcon();
            }
        }
        if (icon2 != null) {
            icon = icon2;
        }
        if (model.isPressed() && model.isArmed()) {
            icon.paintIcon(component, graphics, rectangle.x + this.getTextShiftOffset(), rectangle.y + this.getTextShiftOffset());
        }
        else {
            icon.paintIcon(component, graphics, rectangle.x, rectangle.y);
        }
    }
    
    protected void paintText(final Graphics graphics, final JComponent component, final Rectangle rectangle, final String s) {
        final AbstractButton abstractButton = (AbstractButton)component;
        final ButtonModel model = abstractButton.getModel();
        final FontMetrics fontMetrics = SwingUtilities2.getFontMetrics(component, graphics);
        final int displayedMnemonicIndex = abstractButton.getDisplayedMnemonicIndex();
        if (model.isEnabled()) {
            graphics.setColor(abstractButton.getForeground());
            SwingUtilities2.drawStringUnderlineCharAt(component, graphics, s, displayedMnemonicIndex, rectangle.x + this.getTextShiftOffset(), rectangle.y + fontMetrics.getAscent() + this.getTextShiftOffset());
        }
        else {
            graphics.setColor(abstractButton.getBackground().brighter());
            SwingUtilities2.drawStringUnderlineCharAt(component, graphics, s, displayedMnemonicIndex, rectangle.x, rectangle.y + fontMetrics.getAscent());
            graphics.setColor(abstractButton.getBackground().darker());
            SwingUtilities2.drawStringUnderlineCharAt(component, graphics, s, displayedMnemonicIndex, rectangle.x - 1, rectangle.y + fontMetrics.getAscent() - 1);
        }
    }
    
    protected void paintText(final Graphics graphics, final AbstractButton abstractButton, final Rectangle rectangle, final String s) {
        this.paintText(graphics, (JComponent)abstractButton, rectangle, s);
    }
    
    protected void paintFocus(final Graphics graphics, final AbstractButton abstractButton, final Rectangle rectangle, final Rectangle rectangle2, final Rectangle rectangle3) {
    }
    
    protected void paintButtonPressed(final Graphics graphics, final AbstractButton abstractButton) {
    }
    
    protected void clearTextShiftOffset() {
        this.shiftOffset = 0;
    }
    
    protected void setTextShiftOffset() {
        this.shiftOffset = this.defaultTextShiftOffset;
    }
    
    protected int getTextShiftOffset() {
        return this.shiftOffset;
    }
    
    @Override
    public Dimension getMinimumSize(final JComponent component) {
        final Dimension preferredSize = this.getPreferredSize(component);
        final View view = (View)component.getClientProperty("html");
        if (view != null) {
            final Dimension dimension = preferredSize;
            dimension.width -= (int)(view.getPreferredSpan(0) - view.getMinimumSpan(0));
        }
        return preferredSize;
    }
    
    @Override
    public Dimension getPreferredSize(final JComponent component) {
        final AbstractButton abstractButton = (AbstractButton)component;
        return BasicGraphicsUtils.getPreferredButtonSize(abstractButton, abstractButton.getIconTextGap());
    }
    
    @Override
    public Dimension getMaximumSize(final JComponent component) {
        final Dimension preferredSize = this.getPreferredSize(component);
        final View view = (View)component.getClientProperty("html");
        if (view != null) {
            final Dimension dimension = preferredSize;
            dimension.width += (int)(view.getMaximumSpan(0) - view.getPreferredSpan(0));
        }
        return preferredSize;
    }
    
    @Override
    public int getBaseline(final JComponent component, final int n, final int n2) {
        super.getBaseline(component, n, n2);
        final AbstractButton abstractButton = (AbstractButton)component;
        final String text = abstractButton.getText();
        if (text == null || "".equals(text)) {
            return -1;
        }
        final FontMetrics fontMetrics = abstractButton.getFontMetrics(abstractButton.getFont());
        this.layout(abstractButton, fontMetrics, n, n2);
        return BasicHTML.getBaseline(abstractButton, BasicButtonUI.textRect.y, fontMetrics.getAscent(), BasicButtonUI.textRect.width, BasicButtonUI.textRect.height);
    }
    
    @Override
    public Component.BaselineResizeBehavior getBaselineResizeBehavior(final JComponent component) {
        super.getBaselineResizeBehavior(component);
        if (component.getClientProperty("html") != null) {
            return Component.BaselineResizeBehavior.OTHER;
        }
        switch (((AbstractButton)component).getVerticalAlignment()) {
            case 1: {
                return Component.BaselineResizeBehavior.CONSTANT_ASCENT;
            }
            case 3: {
                return Component.BaselineResizeBehavior.CONSTANT_DESCENT;
            }
            case 0: {
                return Component.BaselineResizeBehavior.CENTER_OFFSET;
            }
            default: {
                return Component.BaselineResizeBehavior.OTHER;
            }
        }
    }
    
    private String layout(final AbstractButton abstractButton, final FontMetrics fontMetrics, final int n, final int n2) {
        final Insets insets = abstractButton.getInsets();
        BasicButtonUI.viewRect.x = insets.left;
        BasicButtonUI.viewRect.y = insets.top;
        BasicButtonUI.viewRect.width = n - (insets.right + BasicButtonUI.viewRect.x);
        BasicButtonUI.viewRect.height = n2 - (insets.bottom + BasicButtonUI.viewRect.y);
        final Rectangle textRect = BasicButtonUI.textRect;
        final Rectangle textRect2 = BasicButtonUI.textRect;
        final Rectangle textRect3 = BasicButtonUI.textRect;
        final Rectangle textRect4 = BasicButtonUI.textRect;
        final int n3 = 0;
        textRect4.height = n3;
        textRect3.width = n3;
        textRect2.y = n3;
        textRect.x = n3;
        final Rectangle iconRect = BasicButtonUI.iconRect;
        final Rectangle iconRect2 = BasicButtonUI.iconRect;
        final Rectangle iconRect3 = BasicButtonUI.iconRect;
        final Rectangle iconRect4 = BasicButtonUI.iconRect;
        final int n4 = 0;
        iconRect4.height = n4;
        iconRect3.width = n4;
        iconRect2.y = n4;
        iconRect.x = n4;
        return SwingUtilities.layoutCompoundLabel(abstractButton, fontMetrics, abstractButton.getText(), abstractButton.getIcon(), abstractButton.getVerticalAlignment(), abstractButton.getHorizontalAlignment(), abstractButton.getVerticalTextPosition(), abstractButton.getHorizontalTextPosition(), BasicButtonUI.viewRect, BasicButtonUI.iconRect, BasicButtonUI.textRect, (abstractButton.getText() == null) ? 0 : abstractButton.getIconTextGap());
    }
    
    private BasicButtonListener getButtonListener(final AbstractButton abstractButton) {
        final MouseMotionListener[] mouseMotionListeners = abstractButton.getMouseMotionListeners();
        if (mouseMotionListeners != null) {
            for (final MouseMotionListener mouseMotionListener : mouseMotionListeners) {
                if (mouseMotionListener instanceof BasicButtonListener) {
                    return (BasicButtonListener)mouseMotionListener;
                }
            }
        }
        return null;
    }
    
    static {
        BASIC_BUTTON_UI_KEY = new Object();
        BasicButtonUI.viewRect = new Rectangle();
        BasicButtonUI.textRect = new Rectangle();
        BasicButtonUI.iconRect = new Rectangle();
    }
}
