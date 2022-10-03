package javax.swing.plaf.synth;

import javax.swing.JColorChooser;
import java.beans.PropertyChangeEvent;
import java.awt.Graphics;
import java.awt.Component;
import javax.swing.colorchooser.ColorChooserComponentFactory;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import java.beans.PropertyChangeListener;
import javax.swing.plaf.basic.BasicColorChooserUI;

public class SynthColorChooserUI extends BasicColorChooserUI implements PropertyChangeListener, SynthUI
{
    private SynthStyle style;
    
    public static ComponentUI createUI(final JComponent component) {
        return new SynthColorChooserUI();
    }
    
    @Override
    protected AbstractColorChooserPanel[] createDefaultChoosers() {
        final SynthContext context = this.getContext(this.chooser, 1);
        AbstractColorChooserPanel[] defaultChooserPanels = (AbstractColorChooserPanel[])context.getStyle().get(context, "ColorChooser.panels");
        context.dispose();
        if (defaultChooserPanels == null) {
            defaultChooserPanels = ColorChooserComponentFactory.getDefaultChooserPanels();
        }
        return defaultChooserPanels;
    }
    
    @Override
    protected void installDefaults() {
        super.installDefaults();
        this.updateStyle(this.chooser);
    }
    
    private void updateStyle(final JComponent component) {
        final SynthContext context = this.getContext(component, 1);
        this.style = SynthLookAndFeel.updateStyle(context, this);
        context.dispose();
    }
    
    @Override
    protected void uninstallDefaults() {
        final SynthContext context = this.getContext(this.chooser, 1);
        this.style.uninstallDefaults(context);
        context.dispose();
        this.style = null;
        super.uninstallDefaults();
    }
    
    @Override
    protected void installListeners() {
        super.installListeners();
        this.chooser.addPropertyChangeListener(this);
    }
    
    @Override
    protected void uninstallListeners() {
        this.chooser.removePropertyChangeListener(this);
        super.uninstallListeners();
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
        context.getPainter().paintColorChooserBackground(context, graphics, 0, 0, component.getWidth(), component.getHeight());
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
        synthContext.getPainter().paintColorChooserBorder(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
        if (SynthLookAndFeel.shouldUpdateStyle(propertyChangeEvent)) {
            this.updateStyle((JComponent)propertyChangeEvent.getSource());
        }
    }
}
