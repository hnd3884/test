package javax.swing.plaf.synth;

import java.beans.PropertyChangeEvent;
import java.awt.Graphics;
import java.awt.Component;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import java.beans.PropertyChangeListener;
import javax.swing.plaf.ViewportUI;

public class SynthViewportUI extends ViewportUI implements PropertyChangeListener, SynthUI
{
    private SynthStyle style;
    
    public static ComponentUI createUI(final JComponent component) {
        return new SynthViewportUI();
    }
    
    @Override
    public void installUI(final JComponent component) {
        super.installUI(component);
        this.installDefaults(component);
        this.installListeners(component);
    }
    
    @Override
    public void uninstallUI(final JComponent component) {
        super.uninstallUI(component);
        this.uninstallListeners(component);
        this.uninstallDefaults(component);
    }
    
    protected void installDefaults(final JComponent component) {
        this.updateStyle(component);
    }
    
    private void updateStyle(final JComponent component) {
        final SynthContext context = this.getContext(component, 1);
        final SynthStyle style = SynthLookAndFeel.getStyle(context.getComponent(), context.getRegion());
        final SynthStyle style2 = context.getStyle();
        if (style != style2) {
            if (style2 != null) {
                style2.uninstallDefaults(context);
            }
            context.setStyle(style);
            style.installDefaults(context);
        }
        this.style = style;
        context.dispose();
    }
    
    protected void installListeners(final JComponent component) {
        component.addPropertyChangeListener(this);
    }
    
    protected void uninstallListeners(final JComponent component) {
        component.removePropertyChangeListener(this);
    }
    
    protected void uninstallDefaults(final JComponent component) {
        final SynthContext context = this.getContext(component, 1);
        this.style.uninstallDefaults(context);
        context.dispose();
        this.style = null;
    }
    
    @Override
    public SynthContext getContext(final JComponent component) {
        return this.getContext(component, SynthLookAndFeel.getComponentState(component));
    }
    
    private SynthContext getContext(final JComponent component, final int n) {
        return SynthContext.getContext(component, this.style, n);
    }
    
    private Region getRegion(final JComponent component) {
        return SynthLookAndFeel.getRegion(component);
    }
    
    @Override
    public void update(final Graphics graphics, final JComponent component) {
        final SynthContext context = this.getContext(component);
        SynthLookAndFeel.update(context, graphics);
        context.getPainter().paintViewportBackground(context, graphics, 0, 0, component.getWidth(), component.getHeight());
        this.paint(context, graphics);
        context.dispose();
    }
    
    @Override
    public void paintBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
    }
    
    @Override
    public void paint(final Graphics graphics, final JComponent component) {
        final SynthContext context = this.getContext(component);
        this.paint(context, graphics);
        context.dispose();
    }
    
    protected void paint(final SynthContext synthContext, final Graphics graphics) {
    }
    
    @Override
    public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
        if (SynthLookAndFeel.shouldUpdateStyle(propertyChangeEvent)) {
            this.updateStyle((JComponent)propertyChangeEvent.getSource());
        }
    }
}
