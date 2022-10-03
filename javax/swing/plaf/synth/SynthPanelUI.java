package javax.swing.plaf.synth;

import java.beans.PropertyChangeEvent;
import java.awt.Graphics;
import java.awt.Component;
import javax.swing.JPanel;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import java.beans.PropertyChangeListener;
import javax.swing.plaf.basic.BasicPanelUI;

public class SynthPanelUI extends BasicPanelUI implements PropertyChangeListener, SynthUI
{
    private SynthStyle style;
    
    public static ComponentUI createUI(final JComponent component) {
        return new SynthPanelUI();
    }
    
    @Override
    public void installUI(final JComponent component) {
        final JPanel panel = (JPanel)component;
        super.installUI(component);
        this.installListeners(panel);
    }
    
    @Override
    public void uninstallUI(final JComponent component) {
        this.uninstallListeners((JPanel)component);
        super.uninstallUI(component);
    }
    
    protected void installListeners(final JPanel panel) {
        panel.addPropertyChangeListener(this);
    }
    
    protected void uninstallListeners(final JPanel panel) {
        panel.removePropertyChangeListener(this);
    }
    
    @Override
    protected void installDefaults(final JPanel panel) {
        this.updateStyle(panel);
    }
    
    @Override
    protected void uninstallDefaults(final JPanel panel) {
        final SynthContext context = this.getContext(panel, 1);
        this.style.uninstallDefaults(context);
        context.dispose();
        this.style = null;
    }
    
    private void updateStyle(final JPanel panel) {
        final SynthContext context = this.getContext(panel, 1);
        this.style = SynthLookAndFeel.updateStyle(context, this);
        context.dispose();
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
    
    @Override
    public void update(final Graphics graphics, final JComponent component) {
        final SynthContext context = this.getContext(component);
        SynthLookAndFeel.update(context, graphics);
        context.getPainter().paintPanelBackground(context, graphics, 0, 0, component.getWidth(), component.getHeight());
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
        synthContext.getPainter().paintPanelBorder(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
        if (SynthLookAndFeel.shouldUpdateStyle(propertyChangeEvent)) {
            this.updateStyle((JPanel)propertyChangeEvent.getSource());
        }
    }
}
