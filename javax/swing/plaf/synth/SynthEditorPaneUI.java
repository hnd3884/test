package javax.swing.plaf.synth;

import java.awt.Graphics;
import java.awt.Component;
import java.beans.PropertyChangeEvent;
import javax.swing.text.JTextComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicEditorPaneUI;

public class SynthEditorPaneUI extends BasicEditorPaneUI implements SynthUI
{
    private SynthStyle style;
    private Boolean localTrue;
    
    public SynthEditorPaneUI() {
        this.localTrue = Boolean.TRUE;
    }
    
    public static ComponentUI createUI(final JComponent component) {
        return new SynthEditorPaneUI();
    }
    
    @Override
    protected void installDefaults() {
        super.installDefaults();
        final JTextComponent component = this.getComponent();
        if (component.getClientProperty("JEditorPane.honorDisplayProperties") == null) {
            component.putClientProperty("JEditorPane.honorDisplayProperties", this.localTrue);
        }
        this.updateStyle(this.getComponent());
    }
    
    @Override
    protected void uninstallDefaults() {
        final SynthContext context = this.getContext(this.getComponent(), 1);
        final JTextComponent component = this.getComponent();
        component.putClientProperty("caretAspectRatio", null);
        this.style.uninstallDefaults(context);
        context.dispose();
        this.style = null;
        if (component.getClientProperty("JEditorPane.honorDisplayProperties") == this.localTrue) {
            component.putClientProperty("JEditorPane.honorDisplayProperties", Boolean.FALSE);
        }
        super.uninstallDefaults();
    }
    
    @Override
    protected void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
        if (SynthLookAndFeel.shouldUpdateStyle(propertyChangeEvent)) {
            this.updateStyle((JTextComponent)propertyChangeEvent.getSource());
        }
        super.propertyChange(propertyChangeEvent);
    }
    
    private void updateStyle(final JTextComponent textComponent) {
        final SynthContext context = this.getContext(textComponent, 1);
        final SynthStyle style = this.style;
        this.style = SynthLookAndFeel.updateStyle(context, this);
        if (this.style != style) {
            SynthTextFieldUI.updateStyle(textComponent, context, this.getPropertyPrefix());
            if (style != null) {
                this.uninstallKeyboardActions();
                this.installKeyboardActions();
            }
        }
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
        this.paintBackground(context, graphics, component);
        this.paint(context, graphics);
        context.dispose();
    }
    
    protected void paint(final SynthContext synthContext, final Graphics graphics) {
        super.paint(graphics, this.getComponent());
    }
    
    @Override
    protected void paintBackground(final Graphics graphics) {
    }
    
    void paintBackground(final SynthContext synthContext, final Graphics graphics, final JComponent component) {
        synthContext.getPainter().paintEditorPaneBackground(synthContext, graphics, 0, 0, component.getWidth(), component.getHeight());
    }
    
    @Override
    public void paintBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        synthContext.getPainter().paintEditorPaneBorder(synthContext, graphics, n, n2, n3, n4);
    }
}
