package javax.swing.plaf.synth;

import java.beans.PropertyChangeEvent;
import java.awt.Dimension;
import javax.swing.Icon;
import java.awt.Graphics;
import java.awt.FontMetrics;
import java.awt.Insets;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.text.View;
import java.awt.Rectangle;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicLabelUI;

public class SynthLabelUI extends BasicLabelUI implements SynthUI
{
    private SynthStyle style;
    
    public static ComponentUI createUI(final JComponent component) {
        return new SynthLabelUI();
    }
    
    @Override
    protected void installDefaults(final JLabel label) {
        this.updateStyle(label);
    }
    
    void updateStyle(final JLabel label) {
        final SynthContext context = this.getContext(label, 1);
        this.style = SynthLookAndFeel.updateStyle(context, this);
        context.dispose();
    }
    
    @Override
    protected void uninstallDefaults(final JLabel label) {
        final SynthContext context = this.getContext(label, 1);
        this.style.uninstallDefaults(context);
        context.dispose();
        this.style = null;
    }
    
    @Override
    public SynthContext getContext(final JComponent component) {
        return this.getContext(component, this.getComponentState(component));
    }
    
    private SynthContext getContext(final JComponent component, final int n) {
        return SynthContext.getContext(component, this.style, n);
    }
    
    private int getComponentState(final JComponent component) {
        int componentState = SynthLookAndFeel.getComponentState(component);
        if (SynthLookAndFeel.getSelectedUI() == this && componentState == 1) {
            componentState = (SynthLookAndFeel.getSelectedUIState() | 0x1);
        }
        return componentState;
    }
    
    @Override
    public int getBaseline(final JComponent component, final int n, final int n2) {
        if (component == null) {
            throw new NullPointerException("Component must be non-null");
        }
        if (n < 0 || n2 < 0) {
            throw new IllegalArgumentException("Width and height must be >= 0");
        }
        final JLabel label = (JLabel)component;
        final String text = label.getText();
        if (text == null || "".equals(text)) {
            return -1;
        }
        final Insets insets = label.getInsets();
        final Rectangle rectangle = new Rectangle();
        final Rectangle rectangle2 = new Rectangle();
        final Rectangle rectangle3 = new Rectangle();
        rectangle.x = insets.left;
        rectangle.y = insets.top;
        rectangle.width = n - (insets.right + rectangle.x);
        rectangle.height = n2 - (insets.bottom + rectangle.y);
        final SynthContext context = this.getContext(label);
        final FontMetrics fontMetrics = context.getComponent().getFontMetrics(context.getStyle().getFont(context));
        context.getStyle().getGraphicsUtils(context).layoutText(context, fontMetrics, label.getText(), label.getIcon(), label.getHorizontalAlignment(), label.getVerticalAlignment(), label.getHorizontalTextPosition(), label.getVerticalTextPosition(), rectangle, rectangle3, rectangle2, label.getIconTextGap());
        final View view = (View)label.getClientProperty("html");
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
        context.getPainter().paintLabelBackground(context, graphics, 0, 0, component.getWidth(), component.getHeight());
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
        final JLabel label = (JLabel)synthContext.getComponent();
        final Icon icon = label.isEnabled() ? label.getIcon() : label.getDisabledIcon();
        graphics.setColor(synthContext.getStyle().getColor(synthContext, ColorType.TEXT_FOREGROUND));
        graphics.setFont(this.style.getFont(synthContext));
        synthContext.getStyle().getGraphicsUtils(synthContext).paintText(synthContext, graphics, label.getText(), icon, label.getHorizontalAlignment(), label.getVerticalAlignment(), label.getHorizontalTextPosition(), label.getVerticalTextPosition(), label.getIconTextGap(), label.getDisplayedMnemonicIndex(), 0);
    }
    
    @Override
    public void paintBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        synthContext.getPainter().paintLabelBorder(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public Dimension getPreferredSize(final JComponent component) {
        final JLabel label = (JLabel)component;
        final Icon icon = label.isEnabled() ? label.getIcon() : label.getDisabledIcon();
        final SynthContext context = this.getContext(component);
        final Dimension preferredSize = context.getStyle().getGraphicsUtils(context).getPreferredSize(context, context.getStyle().getFont(context), label.getText(), icon, label.getHorizontalAlignment(), label.getVerticalAlignment(), label.getHorizontalTextPosition(), label.getVerticalTextPosition(), label.getIconTextGap(), label.getDisplayedMnemonicIndex());
        context.dispose();
        return preferredSize;
    }
    
    @Override
    public Dimension getMinimumSize(final JComponent component) {
        final JLabel label = (JLabel)component;
        final Icon icon = label.isEnabled() ? label.getIcon() : label.getDisabledIcon();
        final SynthContext context = this.getContext(component);
        final Dimension minimumSize = context.getStyle().getGraphicsUtils(context).getMinimumSize(context, context.getStyle().getFont(context), label.getText(), icon, label.getHorizontalAlignment(), label.getVerticalAlignment(), label.getHorizontalTextPosition(), label.getVerticalTextPosition(), label.getIconTextGap(), label.getDisplayedMnemonicIndex());
        context.dispose();
        return minimumSize;
    }
    
    @Override
    public Dimension getMaximumSize(final JComponent component) {
        final JLabel label = (JLabel)component;
        final Icon icon = label.isEnabled() ? label.getIcon() : label.getDisabledIcon();
        final SynthContext context = this.getContext(component);
        final Dimension maximumSize = context.getStyle().getGraphicsUtils(context).getMaximumSize(context, context.getStyle().getFont(context), label.getText(), icon, label.getHorizontalAlignment(), label.getVerticalAlignment(), label.getHorizontalTextPosition(), label.getVerticalTextPosition(), label.getIconTextGap(), label.getDisplayedMnemonicIndex());
        context.dispose();
        return maximumSize;
    }
    
    @Override
    public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
        super.propertyChange(propertyChangeEvent);
        if (SynthLookAndFeel.shouldUpdateStyle(propertyChangeEvent)) {
            this.updateStyle((JLabel)propertyChangeEvent.getSource());
        }
    }
}
