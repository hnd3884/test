package javax.swing.plaf.synth;

import javax.swing.plaf.basic.BasicHTML;
import java.beans.PropertyChangeEvent;
import java.awt.FontMetrics;
import java.awt.Font;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Shape;
import java.awt.Rectangle;
import javax.swing.text.View;
import java.awt.Graphics;
import java.awt.Component;
import javax.swing.JToolTip;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import java.beans.PropertyChangeListener;
import javax.swing.plaf.basic.BasicToolTipUI;

public class SynthToolTipUI extends BasicToolTipUI implements PropertyChangeListener, SynthUI
{
    private SynthStyle style;
    
    public static ComponentUI createUI(final JComponent component) {
        return new SynthToolTipUI();
    }
    
    @Override
    protected void installDefaults(final JComponent component) {
        this.updateStyle(component);
    }
    
    private void updateStyle(final JComponent component) {
        final SynthContext context = this.getContext(component, 1);
        this.style = SynthLookAndFeel.updateStyle(context, this);
        context.dispose();
    }
    
    @Override
    protected void uninstallDefaults(final JComponent component) {
        final SynthContext context = this.getContext(component, 1);
        this.style.uninstallDefaults(context);
        context.dispose();
        this.style = null;
    }
    
    @Override
    protected void installListeners(final JComponent component) {
        component.addPropertyChangeListener(this);
    }
    
    @Override
    protected void uninstallListeners(final JComponent component) {
        component.removePropertyChangeListener(this);
    }
    
    @Override
    public SynthContext getContext(final JComponent component) {
        return this.getContext(component, this.getComponentState(component));
    }
    
    private SynthContext getContext(final JComponent component, final int n) {
        return SynthContext.getContext(component, this.style, n);
    }
    
    private int getComponentState(final JComponent component) {
        final JComponent component2 = ((JToolTip)component).getComponent();
        if (component2 != null && !component2.isEnabled()) {
            return 8;
        }
        return SynthLookAndFeel.getComponentState(component);
    }
    
    @Override
    public void update(final Graphics graphics, final JComponent component) {
        final SynthContext context = this.getContext(component);
        SynthLookAndFeel.update(context, graphics);
        context.getPainter().paintToolTipBackground(context, graphics, 0, 0, component.getWidth(), component.getHeight());
        this.paint(context, graphics);
        context.dispose();
    }
    
    @Override
    public void paintBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        synthContext.getPainter().paintToolTipBorder(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paint(final Graphics graphics, final JComponent component) {
        final SynthContext context = this.getContext(component);
        this.paint(context, graphics);
        context.dispose();
    }
    
    protected void paint(final SynthContext synthContext, final Graphics graphics) {
        final JToolTip toolTip = (JToolTip)synthContext.getComponent();
        final Insets insets = toolTip.getInsets();
        final View view = (View)toolTip.getClientProperty("html");
        if (view != null) {
            view.paint(graphics, new Rectangle(insets.left, insets.top, toolTip.getWidth() - (insets.left + insets.right), toolTip.getHeight() - (insets.top + insets.bottom)));
        }
        else {
            graphics.setColor(synthContext.getStyle().getColor(synthContext, ColorType.TEXT_FOREGROUND));
            graphics.setFont(this.style.getFont(synthContext));
            synthContext.getStyle().getGraphicsUtils(synthContext).paintText(synthContext, graphics, toolTip.getTipText(), insets.left, insets.top, -1);
        }
    }
    
    @Override
    public Dimension getPreferredSize(final JComponent component) {
        final SynthContext context = this.getContext(component);
        final Insets insets = component.getInsets();
        final Dimension dimension = new Dimension(insets.left + insets.right, insets.top + insets.bottom);
        final String tipText = ((JToolTip)component).getTipText();
        if (tipText != null) {
            final View view = (component != null) ? ((View)component.getClientProperty("html")) : null;
            if (view != null) {
                final Dimension dimension2 = dimension;
                dimension2.width += (int)view.getPreferredSpan(0);
                final Dimension dimension3 = dimension;
                dimension3.height += (int)view.getPreferredSpan(1);
            }
            else {
                final Font font = context.getStyle().getFont(context);
                final FontMetrics fontMetrics = component.getFontMetrics(font);
                final Dimension dimension4 = dimension;
                dimension4.width += context.getStyle().getGraphicsUtils(context).computeStringWidth(context, font, fontMetrics, tipText);
                final Dimension dimension5 = dimension;
                dimension5.height += fontMetrics.getHeight();
            }
        }
        context.dispose();
        return dimension;
    }
    
    @Override
    public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
        if (SynthLookAndFeel.shouldUpdateStyle(propertyChangeEvent)) {
            this.updateStyle((JComponent)propertyChangeEvent.getSource());
        }
        final String propertyName = propertyChangeEvent.getPropertyName();
        if (propertyName.equals("tiptext") || "font".equals(propertyName) || "foreground".equals(propertyName)) {
            final JToolTip toolTip = (JToolTip)propertyChangeEvent.getSource();
            BasicHTML.updateRenderer(toolTip, toolTip.getTipText());
        }
    }
}
