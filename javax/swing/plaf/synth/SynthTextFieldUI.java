package javax.swing.plaf.synth;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.awt.Graphics;
import java.awt.Component;
import javax.swing.text.Caret;
import java.awt.Insets;
import java.awt.Color;
import javax.swing.plaf.UIResource;
import javax.swing.text.JTextComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicTextFieldUI;

public class SynthTextFieldUI extends BasicTextFieldUI implements SynthUI
{
    private Handler handler;
    private SynthStyle style;
    
    public SynthTextFieldUI() {
        this.handler = new Handler();
    }
    
    public static ComponentUI createUI(final JComponent component) {
        return new SynthTextFieldUI();
    }
    
    private void updateStyle(final JTextComponent textComponent) {
        final SynthContext context = this.getContext(textComponent, 1);
        final SynthStyle style = this.style;
        this.style = SynthLookAndFeel.updateStyle(context, this);
        if (this.style != style) {
            updateStyle(textComponent, context, this.getPropertyPrefix());
            if (style != null) {
                this.uninstallKeyboardActions();
                this.installKeyboardActions();
            }
        }
        context.dispose();
    }
    
    static void updateStyle(final JTextComponent textComponent, final SynthContext synthContext, final String s) {
        final SynthStyle style = synthContext.getStyle();
        final Color caretColor = textComponent.getCaretColor();
        if (caretColor == null || caretColor instanceof UIResource) {
            textComponent.setCaretColor((Color)style.get(synthContext, s + ".caretForeground"));
        }
        final Color foreground = textComponent.getForeground();
        if (foreground == null || foreground instanceof UIResource) {
            final Color colorForState = style.getColorForState(synthContext, ColorType.TEXT_FOREGROUND);
            if (colorForState != null) {
                textComponent.setForeground(colorForState);
            }
        }
        final Object value = style.get(synthContext, s + ".caretAspectRatio");
        if (value instanceof Number) {
            textComponent.putClientProperty("caretAspectRatio", value);
        }
        synthContext.setComponentState(768);
        final Color selectionColor = textComponent.getSelectionColor();
        if (selectionColor == null || selectionColor instanceof UIResource) {
            textComponent.setSelectionColor(style.getColor(synthContext, ColorType.TEXT_BACKGROUND));
        }
        final Color selectedTextColor = textComponent.getSelectedTextColor();
        if (selectedTextColor == null || selectedTextColor instanceof UIResource) {
            textComponent.setSelectedTextColor(style.getColor(synthContext, ColorType.TEXT_FOREGROUND));
        }
        synthContext.setComponentState(8);
        final Color disabledTextColor = textComponent.getDisabledTextColor();
        if (disabledTextColor == null || disabledTextColor instanceof UIResource) {
            textComponent.setDisabledTextColor(style.getColor(synthContext, ColorType.TEXT_FOREGROUND));
        }
        final Insets margin = textComponent.getMargin();
        if (margin == null || margin instanceof UIResource) {
            Insets empty_UIRESOURCE_INSETS = (Insets)style.get(synthContext, s + ".margin");
            if (empty_UIRESOURCE_INSETS == null) {
                empty_UIRESOURCE_INSETS = SynthLookAndFeel.EMPTY_UIRESOURCE_INSETS;
            }
            textComponent.setMargin(empty_UIRESOURCE_INSETS);
        }
        final Caret caret = textComponent.getCaret();
        if (caret instanceof UIResource) {
            final Object value2 = style.get(synthContext, s + ".caretBlinkRate");
            if (value2 != null && value2 instanceof Integer) {
                caret.setBlinkRate((int)value2);
            }
        }
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
        this.paintBackground(context, graphics, component);
        this.paint(context, graphics);
        context.dispose();
    }
    
    protected void paint(final SynthContext synthContext, final Graphics graphics) {
        super.paint(graphics, this.getComponent());
    }
    
    void paintBackground(final SynthContext synthContext, final Graphics graphics, final JComponent component) {
        synthContext.getPainter().paintTextFieldBackground(synthContext, graphics, 0, 0, component.getWidth(), component.getHeight());
    }
    
    @Override
    public void paintBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        synthContext.getPainter().paintTextFieldBorder(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    protected void paintBackground(final Graphics graphics) {
    }
    
    @Override
    protected void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
        if (SynthLookAndFeel.shouldUpdateStyle(propertyChangeEvent)) {
            this.updateStyle((JTextComponent)propertyChangeEvent.getSource());
        }
        super.propertyChange(propertyChangeEvent);
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
