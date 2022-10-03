package javax.swing.plaf.synth;

import java.awt.event.FocusEvent;
import java.beans.PropertyChangeEvent;
import java.awt.Graphics;
import java.awt.Component;
import javax.swing.text.JTextComponent;
import java.awt.event.FocusListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicTextAreaUI;

public class SynthTextAreaUI extends BasicTextAreaUI implements SynthUI
{
    private Handler handler;
    private SynthStyle style;
    
    public SynthTextAreaUI() {
        this.handler = new Handler();
    }
    
    public static ComponentUI createUI(final JComponent component) {
        return new SynthTextAreaUI();
    }
    
    @Override
    protected void installDefaults() {
        super.installDefaults();
        this.updateStyle(this.getComponent());
        this.getComponent().addFocusListener(this.handler);
    }
    
    @Override
    protected void uninstallDefaults() {
        final SynthContext context = this.getContext(this.getComponent(), 1);
        this.getComponent().putClientProperty("caretAspectRatio", null);
        this.getComponent().removeFocusListener(this.handler);
        this.style.uninstallDefaults(context);
        context.dispose();
        this.style = null;
        super.uninstallDefaults();
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
        return this.getContext(component, SynthLookAndFeel.getComponentState(component));
    }
    
    private SynthContext getContext(final JComponent component, final int n) {
        return SynthContext.getContext(component, this.style, n);
    }
    
    @Override
    public void update(final Graphics graphics, final JComponent component) {
        final SynthContext context = this.getContext(component);
        SynthLookAndFeel.update(context, graphics);
        context.getPainter().paintTextAreaBackground(context, graphics, 0, 0, component.getWidth(), component.getHeight());
        this.paint(context, graphics);
        context.dispose();
    }
    
    protected void paint(final SynthContext synthContext, final Graphics graphics) {
        super.paint(graphics, this.getComponent());
    }
    
    @Override
    protected void paintBackground(final Graphics graphics) {
    }
    
    @Override
    public void paintBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        synthContext.getPainter().paintTextAreaBorder(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    protected void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
        if (SynthLookAndFeel.shouldUpdateStyle(propertyChangeEvent)) {
            this.updateStyle((JTextComponent)propertyChangeEvent.getSource());
        }
        super.propertyChange(propertyChangeEvent);
    }
    
    private final class Handler implements FocusListener
    {
        @Override
        public void focusGained(final FocusEvent focusEvent) {
            BasicTextUI.this.getComponent().repaint();
        }
        
        @Override
        public void focusLost(final FocusEvent focusEvent) {
            BasicTextUI.this.getComponent().repaint();
        }
    }
}
