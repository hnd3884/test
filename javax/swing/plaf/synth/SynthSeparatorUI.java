package javax.swing.plaf.synth;

import java.beans.PropertyChangeEvent;
import java.awt.Component;
import java.awt.Insets;
import java.awt.Graphics;
import java.awt.Dimension;
import javax.swing.plaf.DimensionUIResource;
import javax.swing.plaf.UIResource;
import javax.swing.JToolBar;
import javax.swing.JSeparator;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import java.beans.PropertyChangeListener;
import javax.swing.plaf.SeparatorUI;

public class SynthSeparatorUI extends SeparatorUI implements PropertyChangeListener, SynthUI
{
    private SynthStyle style;
    
    public static ComponentUI createUI(final JComponent component) {
        return new SynthSeparatorUI();
    }
    
    @Override
    public void installUI(final JComponent component) {
        this.installDefaults((JSeparator)component);
        this.installListeners((JSeparator)component);
    }
    
    @Override
    public void uninstallUI(final JComponent component) {
        this.uninstallListeners((JSeparator)component);
        this.uninstallDefaults((JSeparator)component);
    }
    
    public void installDefaults(final JSeparator separator) {
        this.updateStyle(separator);
    }
    
    private void updateStyle(final JSeparator separator) {
        final SynthContext context = this.getContext(separator, 1);
        final SynthStyle style = this.style;
        this.style = SynthLookAndFeel.updateStyle(context, this);
        if (this.style != style && separator instanceof JToolBar.Separator) {
            final Dimension separatorSize = ((JToolBar.Separator)separator).getSeparatorSize();
            if (separatorSize == null || separatorSize instanceof UIResource) {
                DimensionUIResource separatorSize2 = (DimensionUIResource)this.style.get(context, "ToolBar.separatorSize");
                if (separatorSize2 == null) {
                    separatorSize2 = new DimensionUIResource(10, 10);
                }
                ((JToolBar.Separator)separator).setSeparatorSize(separatorSize2);
            }
        }
        context.dispose();
    }
    
    public void uninstallDefaults(final JSeparator separator) {
        final SynthContext context = this.getContext(separator, 1);
        this.style.uninstallDefaults(context);
        context.dispose();
        this.style = null;
    }
    
    public void installListeners(final JSeparator separator) {
        separator.addPropertyChangeListener(this);
    }
    
    public void uninstallListeners(final JSeparator separator) {
        separator.removePropertyChangeListener(this);
    }
    
    @Override
    public void update(final Graphics graphics, final JComponent component) {
        final SynthContext context = this.getContext(component);
        final JSeparator separator = (JSeparator)context.getComponent();
        SynthLookAndFeel.update(context, graphics);
        context.getPainter().paintSeparatorBackground(context, graphics, 0, 0, component.getWidth(), component.getHeight(), separator.getOrientation());
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
        final JSeparator separator = (JSeparator)synthContext.getComponent();
        synthContext.getPainter().paintSeparatorForeground(synthContext, graphics, 0, 0, separator.getWidth(), separator.getHeight(), separator.getOrientation());
    }
    
    @Override
    public void paintBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        synthContext.getPainter().paintSeparatorBorder(synthContext, graphics, n, n2, n3, n4, ((JSeparator)synthContext.getComponent()).getOrientation());
    }
    
    @Override
    public Dimension getPreferredSize(final JComponent component) {
        final SynthContext context = this.getContext(component);
        final int int1 = this.style.getInt(context, "Separator.thickness", 2);
        final Insets insets = component.getInsets();
        Dimension dimension;
        if (((JSeparator)component).getOrientation() == 1) {
            dimension = new Dimension(insets.left + insets.right + int1, insets.top + insets.bottom);
        }
        else {
            dimension = new Dimension(insets.left + insets.right, insets.top + insets.bottom + int1);
        }
        context.dispose();
        return dimension;
    }
    
    @Override
    public Dimension getMinimumSize(final JComponent component) {
        return this.getPreferredSize(component);
    }
    
    @Override
    public Dimension getMaximumSize(final JComponent component) {
        return new Dimension(32767, 32767);
    }
    
    @Override
    public SynthContext getContext(final JComponent component) {
        return this.getContext(component, SynthLookAndFeel.getComponentState(component));
    }
    
    private SynthContext getContext(final JComponent component, final int n) {
        return SynthContext.getContext(component, this.style, n);
    }
    
    @Override
    public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
        if (SynthLookAndFeel.shouldUpdateStyle(propertyChangeEvent)) {
            this.updateStyle((JSeparator)propertyChangeEvent.getSource());
        }
    }
}
