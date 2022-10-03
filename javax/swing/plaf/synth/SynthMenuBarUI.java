package javax.swing.plaf.synth;

import java.beans.PropertyChangeEvent;
import java.awt.Graphics;
import java.awt.Component;
import javax.swing.JMenuBar;
import java.awt.LayoutManager;
import java.awt.Container;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import java.beans.PropertyChangeListener;
import javax.swing.plaf.basic.BasicMenuBarUI;

public class SynthMenuBarUI extends BasicMenuBarUI implements PropertyChangeListener, SynthUI
{
    private SynthStyle style;
    
    public static ComponentUI createUI(final JComponent component) {
        return new SynthMenuBarUI();
    }
    
    @Override
    protected void installDefaults() {
        if (this.menuBar.getLayout() == null || this.menuBar.getLayout() instanceof UIResource) {
            this.menuBar.setLayout(new SynthMenuLayout(this.menuBar, 2));
        }
        this.updateStyle(this.menuBar);
    }
    
    @Override
    protected void installListeners() {
        super.installListeners();
        this.menuBar.addPropertyChangeListener(this);
    }
    
    private void updateStyle(final JMenuBar menuBar) {
        final SynthContext context = this.getContext(menuBar, 1);
        final SynthStyle style = this.style;
        this.style = SynthLookAndFeel.updateStyle(context, this);
        if (this.style != style && style != null) {
            this.uninstallKeyboardActions();
            this.installKeyboardActions();
        }
        context.dispose();
    }
    
    @Override
    protected void uninstallDefaults() {
        final SynthContext context = this.getContext(this.menuBar, 1);
        this.style.uninstallDefaults(context);
        context.dispose();
        this.style = null;
    }
    
    @Override
    protected void uninstallListeners() {
        super.uninstallListeners();
        this.menuBar.removePropertyChangeListener(this);
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
        context.getPainter().paintMenuBarBackground(context, graphics, 0, 0, component.getWidth(), component.getHeight());
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
        synthContext.getPainter().paintMenuBarBorder(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
        if (SynthLookAndFeel.shouldUpdateStyle(propertyChangeEvent)) {
            this.updateStyle((JMenuBar)propertyChangeEvent.getSource());
        }
    }
}
