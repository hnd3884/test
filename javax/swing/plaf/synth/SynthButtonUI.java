package javax.swing.plaf.synth;

import java.beans.PropertyChangeEvent;
import java.awt.Dimension;
import javax.swing.Icon;
import java.awt.Graphics;
import java.awt.FontMetrics;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.text.View;
import java.awt.Rectangle;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import java.awt.Insets;
import javax.swing.plaf.UIResource;
import javax.swing.LookAndFeel;
import javax.swing.AbstractButton;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import java.beans.PropertyChangeListener;
import javax.swing.plaf.basic.BasicButtonUI;

public class SynthButtonUI extends BasicButtonUI implements PropertyChangeListener, SynthUI
{
    private SynthStyle style;
    
    public static ComponentUI createUI(final JComponent component) {
        return new SynthButtonUI();
    }
    
    @Override
    protected void installDefaults(final AbstractButton abstractButton) {
        this.updateStyle(abstractButton);
        LookAndFeel.installProperty(abstractButton, "rolloverEnabled", Boolean.TRUE);
    }
    
    @Override
    protected void installListeners(final AbstractButton abstractButton) {
        super.installListeners(abstractButton);
        abstractButton.addPropertyChangeListener(this);
    }
    
    void updateStyle(final AbstractButton abstractButton) {
        final SynthContext context = this.getContext(abstractButton, 1);
        final SynthStyle style = this.style;
        this.style = SynthLookAndFeel.updateStyle(context, this);
        if (this.style != style) {
            if (abstractButton.getMargin() == null || abstractButton.getMargin() instanceof UIResource) {
                Insets empty_UIRESOURCE_INSETS = (Insets)this.style.get(context, this.getPropertyPrefix() + "margin");
                if (empty_UIRESOURCE_INSETS == null) {
                    empty_UIRESOURCE_INSETS = SynthLookAndFeel.EMPTY_UIRESOURCE_INSETS;
                }
                abstractButton.setMargin(empty_UIRESOURCE_INSETS);
            }
            final Object value = this.style.get(context, this.getPropertyPrefix() + "iconTextGap");
            if (value != null) {
                LookAndFeel.installProperty(abstractButton, "iconTextGap", value);
            }
            final Object value2 = this.style.get(context, this.getPropertyPrefix() + "contentAreaFilled");
            LookAndFeel.installProperty(abstractButton, "contentAreaFilled", (value2 != null) ? value2 : Boolean.TRUE);
            if (style != null) {
                this.uninstallKeyboardActions(abstractButton);
                this.installKeyboardActions(abstractButton);
            }
        }
        context.dispose();
    }
    
    @Override
    protected void uninstallListeners(final AbstractButton abstractButton) {
        super.uninstallListeners(abstractButton);
        abstractButton.removePropertyChangeListener(this);
    }
    
    @Override
    protected void uninstallDefaults(final AbstractButton abstractButton) {
        final SynthContext context = this.getContext(abstractButton, 1);
        this.style.uninstallDefaults(context);
        context.dispose();
        this.style = null;
    }
    
    @Override
    public SynthContext getContext(final JComponent component) {
        return this.getContext(component, this.getComponentState(component));
    }
    
    SynthContext getContext(final JComponent component, final int n) {
        return SynthContext.getContext(component, this.style, n);
    }
    
    private int getComponentState(final JComponent component) {
        int n = 1;
        if (!component.isEnabled()) {
            n = 8;
        }
        if (SynthLookAndFeel.getSelectedUI() == this) {
            return SynthLookAndFeel.getSelectedUIState() | 0x1;
        }
        final AbstractButton abstractButton = (AbstractButton)component;
        final ButtonModel model = abstractButton.getModel();
        if (model.isPressed()) {
            if (model.isArmed()) {
                n = 4;
            }
            else {
                n = 2;
            }
        }
        if (model.isRollover()) {
            n |= 0x2;
        }
        if (model.isSelected()) {
            n |= 0x200;
        }
        if (component.isFocusOwner() && abstractButton.isFocusPainted()) {
            n |= 0x100;
        }
        if (component instanceof JButton && ((JButton)component).isDefaultButton()) {
            n |= 0x400;
        }
        return n;
    }
    
    @Override
    public int getBaseline(final JComponent component, final int n, final int n2) {
        if (component == null) {
            throw new NullPointerException("Component must be non-null");
        }
        if (n < 0 || n2 < 0) {
            throw new IllegalArgumentException("Width and height must be >= 0");
        }
        final AbstractButton abstractButton = (AbstractButton)component;
        final String text = abstractButton.getText();
        if (text == null || "".equals(text)) {
            return -1;
        }
        final Insets insets = abstractButton.getInsets();
        final Rectangle rectangle = new Rectangle();
        final Rectangle rectangle2 = new Rectangle();
        final Rectangle rectangle3 = new Rectangle();
        rectangle.x = insets.left;
        rectangle.y = insets.top;
        rectangle.width = n - (insets.right + rectangle.x);
        rectangle.height = n2 - (insets.bottom + rectangle.y);
        final SynthContext context = this.getContext(abstractButton);
        final FontMetrics fontMetrics = context.getComponent().getFontMetrics(context.getStyle().getFont(context));
        context.getStyle().getGraphicsUtils(context).layoutText(context, fontMetrics, abstractButton.getText(), abstractButton.getIcon(), abstractButton.getHorizontalAlignment(), abstractButton.getVerticalAlignment(), abstractButton.getHorizontalTextPosition(), abstractButton.getVerticalTextPosition(), rectangle, rectangle3, rectangle2, abstractButton.getIconTextGap());
        final View view = (View)abstractButton.getClientProperty("html");
        int htmlBaseline;
        if (view != null) {
            htmlBaseline = BasicHTML.getHTMLBaseline(view, rectangle2.width, rectangle2.height);
            if (htmlBaseline >= 0) {
                htmlBaseline += rectangle2.y;
            }
        }
        else {
            htmlBaseline = rectangle2.y + fontMetrics.getAscent();
        }
        context.dispose();
        return htmlBaseline;
    }
    
    @Override
    public void update(final Graphics graphics, final JComponent component) {
        final SynthContext context = this.getContext(component);
        SynthLookAndFeel.update(context, graphics);
        this.paintBackground(context, graphics, component);
        this.paint(context, graphics);
        context.dispose();
    }
    
    @Override
    public void paint(final Graphics graphics, final JComponent component) {
        final SynthContext context = this.getContext(component);
        this.paint(context, graphics);
        context.dispose();
    }
    
    protected void paint(final SynthContext synthContext, final Graphics graphics) {
        final AbstractButton abstractButton = (AbstractButton)synthContext.getComponent();
        graphics.setColor(synthContext.getStyle().getColor(synthContext, ColorType.TEXT_FOREGROUND));
        graphics.setFont(this.style.getFont(synthContext));
        synthContext.getStyle().getGraphicsUtils(synthContext).paintText(synthContext, graphics, abstractButton.getText(), this.getIcon(abstractButton), abstractButton.getHorizontalAlignment(), abstractButton.getVerticalAlignment(), abstractButton.getHorizontalTextPosition(), abstractButton.getVerticalTextPosition(), abstractButton.getIconTextGap(), abstractButton.getDisplayedMnemonicIndex(), this.getTextShiftOffset(synthContext));
    }
    
    void paintBackground(final SynthContext synthContext, final Graphics graphics, final JComponent component) {
        if (((AbstractButton)component).isContentAreaFilled()) {
            synthContext.getPainter().paintButtonBackground(synthContext, graphics, 0, 0, component.getWidth(), component.getHeight());
        }
    }
    
    @Override
    public void paintBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        synthContext.getPainter().paintButtonBorder(synthContext, graphics, n, n2, n3, n4);
    }
    
    protected Icon getDefaultIcon(final AbstractButton abstractButton) {
        final SynthContext context = this.getContext(abstractButton);
        final Icon icon = context.getStyle().getIcon(context, this.getPropertyPrefix() + "icon");
        context.dispose();
        return icon;
    }
    
    protected Icon getIcon(final AbstractButton abstractButton) {
        final Icon icon = abstractButton.getIcon();
        final ButtonModel model = abstractButton.getModel();
        Icon icon2;
        if (!model.isEnabled()) {
            icon2 = this.getSynthDisabledIcon(abstractButton, icon);
        }
        else if (model.isPressed() && model.isArmed()) {
            icon2 = this.getPressedIcon(abstractButton, this.getSelectedIcon(abstractButton, icon));
        }
        else if (abstractButton.isRolloverEnabled() && model.isRollover()) {
            icon2 = this.getRolloverIcon(abstractButton, this.getSelectedIcon(abstractButton, icon));
        }
        else if (model.isSelected()) {
            icon2 = this.getSelectedIcon(abstractButton, icon);
        }
        else {
            icon2 = this.getEnabledIcon(abstractButton, icon);
        }
        if (icon2 == null) {
            return this.getDefaultIcon(abstractButton);
        }
        return icon2;
    }
    
    private Icon getIcon(final AbstractButton abstractButton, final Icon icon, final Icon icon2, final int n) {
        Icon synthIcon = icon;
        if (synthIcon == null) {
            if (icon2 instanceof UIResource) {
                synthIcon = this.getSynthIcon(abstractButton, n);
                if (synthIcon == null) {
                    synthIcon = icon2;
                }
            }
            else {
                synthIcon = icon2;
            }
        }
        return synthIcon;
    }
    
    private Icon getSynthIcon(final AbstractButton abstractButton, final int n) {
        return this.style.getIcon(this.getContext(abstractButton, n), this.getPropertyPrefix() + "icon");
    }
    
    private Icon getEnabledIcon(final AbstractButton abstractButton, Icon synthIcon) {
        if (synthIcon == null) {
            synthIcon = this.getSynthIcon(abstractButton, 1);
        }
        return synthIcon;
    }
    
    private Icon getSelectedIcon(final AbstractButton abstractButton, final Icon icon) {
        return this.getIcon(abstractButton, abstractButton.getSelectedIcon(), icon, 512);
    }
    
    private Icon getRolloverIcon(final AbstractButton abstractButton, final Icon icon) {
        Icon icon2;
        if (abstractButton.getModel().isSelected()) {
            icon2 = this.getIcon(abstractButton, abstractButton.getRolloverSelectedIcon(), icon, 514);
        }
        else {
            icon2 = this.getIcon(abstractButton, abstractButton.getRolloverIcon(), icon, 2);
        }
        return icon2;
    }
    
    private Icon getPressedIcon(final AbstractButton abstractButton, final Icon icon) {
        return this.getIcon(abstractButton, abstractButton.getPressedIcon(), icon, 4);
    }
    
    private Icon getSynthDisabledIcon(final AbstractButton abstractButton, final Icon icon) {
        Icon icon2;
        if (abstractButton.getModel().isSelected()) {
            icon2 = this.getIcon(abstractButton, abstractButton.getDisabledSelectedIcon(), icon, 520);
        }
        else {
            icon2 = this.getIcon(abstractButton, abstractButton.getDisabledIcon(), icon, 8);
        }
        return icon2;
    }
    
    private int getTextShiftOffset(final SynthContext synthContext) {
        final AbstractButton abstractButton = (AbstractButton)synthContext.getComponent();
        final ButtonModel model = abstractButton.getModel();
        if (model.isArmed() && model.isPressed() && abstractButton.getPressedIcon() == null) {
            return synthContext.getStyle().getInt(synthContext, this.getPropertyPrefix() + "textShiftOffset", 0);
        }
        return 0;
    }
    
    @Override
    public Dimension getMinimumSize(final JComponent component) {
        if (component.getComponentCount() > 0 && component.getLayout() != null) {
            return null;
        }
        final AbstractButton abstractButton = (AbstractButton)component;
        final SynthContext context = this.getContext(component);
        final Dimension minimumSize = context.getStyle().getGraphicsUtils(context).getMinimumSize(context, context.getStyle().getFont(context), abstractButton.getText(), this.getSizingIcon(abstractButton), abstractButton.getHorizontalAlignment(), abstractButton.getVerticalAlignment(), abstractButton.getHorizontalTextPosition(), abstractButton.getVerticalTextPosition(), abstractButton.getIconTextGap(), abstractButton.getDisplayedMnemonicIndex());
        context.dispose();
        return minimumSize;
    }
    
    @Override
    public Dimension getPreferredSize(final JComponent component) {
        if (component.getComponentCount() > 0 && component.getLayout() != null) {
            return null;
        }
        final AbstractButton abstractButton = (AbstractButton)component;
        final SynthContext context = this.getContext(component);
        final Dimension preferredSize = context.getStyle().getGraphicsUtils(context).getPreferredSize(context, context.getStyle().getFont(context), abstractButton.getText(), this.getSizingIcon(abstractButton), abstractButton.getHorizontalAlignment(), abstractButton.getVerticalAlignment(), abstractButton.getHorizontalTextPosition(), abstractButton.getVerticalTextPosition(), abstractButton.getIconTextGap(), abstractButton.getDisplayedMnemonicIndex());
        context.dispose();
        return preferredSize;
    }
    
    @Override
    public Dimension getMaximumSize(final JComponent component) {
        if (component.getComponentCount() > 0 && component.getLayout() != null) {
            return null;
        }
        final AbstractButton abstractButton = (AbstractButton)component;
        final SynthContext context = this.getContext(component);
        final Dimension maximumSize = context.getStyle().getGraphicsUtils(context).getMaximumSize(context, context.getStyle().getFont(context), abstractButton.getText(), this.getSizingIcon(abstractButton), abstractButton.getHorizontalAlignment(), abstractButton.getVerticalAlignment(), abstractButton.getHorizontalTextPosition(), abstractButton.getVerticalTextPosition(), abstractButton.getIconTextGap(), abstractButton.getDisplayedMnemonicIndex());
        context.dispose();
        return maximumSize;
    }
    
    protected Icon getSizingIcon(final AbstractButton abstractButton) {
        Icon icon = this.getEnabledIcon(abstractButton, abstractButton.getIcon());
        if (icon == null) {
            icon = this.getDefaultIcon(abstractButton);
        }
        return icon;
    }
    
    @Override
    public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
        if (SynthLookAndFeel.shouldUpdateStyle(propertyChangeEvent)) {
            this.updateStyle((AbstractButton)propertyChangeEvent.getSource());
        }
    }
}
