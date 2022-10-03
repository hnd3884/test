package javax.swing.plaf.synth;

import java.beans.PropertyChangeEvent;
import java.awt.Graphics;
import java.awt.Component;
import javax.swing.JRootPane;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicRootPaneUI;

public class SynthRootPaneUI extends BasicRootPaneUI implements SynthUI
{
    private SynthStyle style;
    
    public static ComponentUI createUI(final JComponent component) {
        return new SynthRootPaneUI();
    }
    
    @Override
    protected void installDefaults(final JRootPane rootPane) {
        this.updateStyle(rootPane);
    }
    
    @Override
    protected void uninstallDefaults(final JRootPane rootPane) {
        final SynthContext context = this.getContext(rootPane, 1);
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
        return SynthLookAndFeel.getComponentState(component);
    }
    
    private void updateStyle(final JComponent component) {
        final SynthContext context = this.getContext(component, 1);
        final SynthStyle style = this.style;
        this.style = SynthLookAndFeel.updateStyle(context, this);
        if (this.style != style && style != null) {
            this.uninstallKeyboardActions((JRootPane)component);
            this.installKeyboardActions((JRootPane)component);
        }
        context.dispose();
    }
    
    @Override
    public void update(final Graphics graphics, final JComponent component) {
        final SynthContext context = this.getContext(component);
        SynthLookAndFeel.update(context, graphics);
        context.getPainter().paintRootPaneBackground(context, graphics, 0, 0, component.getWidth(), component.getHeight());
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
    }
    
    @Override
    public void paintBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        synthContext.getPainter().paintRootPaneBorder(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
        if (SynthLookAndFeel.shouldUpdateStyle(propertyChangeEvent)) {
            this.updateStyle((JComponent)propertyChangeEvent.getSource());
        }
        super.propertyChange(propertyChangeEvent);
    }
}
