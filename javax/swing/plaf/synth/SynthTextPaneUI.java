package javax.swing.plaf.synth;

import java.awt.Graphics;
import javax.swing.text.Style;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.JTextComponent;
import java.awt.Font;
import java.awt.Color;
import java.beans.PropertyChangeEvent;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;

public class SynthTextPaneUI extends SynthEditorPaneUI
{
    public static ComponentUI createUI(final JComponent component) {
        return new SynthTextPaneUI();
    }
    
    @Override
    protected String getPropertyPrefix() {
        return "TextPane";
    }
    
    @Override
    public void installUI(final JComponent component) {
        super.installUI(component);
        this.updateForeground(component.getForeground());
        this.updateFont(component.getFont());
    }
    
    @Override
    protected void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
        super.propertyChange(propertyChangeEvent);
        final String propertyName = propertyChangeEvent.getPropertyName();
        if (propertyName.equals("foreground")) {
            this.updateForeground((Color)propertyChangeEvent.getNewValue());
        }
        else if (propertyName.equals("font")) {
            this.updateFont((Font)propertyChangeEvent.getNewValue());
        }
        else if (propertyName.equals("document")) {
            final JTextComponent component = this.getComponent();
            this.updateForeground(component.getForeground());
            this.updateFont(component.getFont());
        }
    }
    
    private void updateForeground(final Color color) {
        final Style style = ((StyledDocument)this.getComponent().getDocument()).getStyle("default");
        if (style == null) {
            return;
        }
        if (color == null) {
            style.removeAttribute(StyleConstants.Foreground);
        }
        else {
            StyleConstants.setForeground(style, color);
        }
    }
    
    private void updateFont(final Font font) {
        final Style style = ((StyledDocument)this.getComponent().getDocument()).getStyle("default");
        if (style == null) {
            return;
        }
        if (font == null) {
            style.removeAttribute(StyleConstants.FontFamily);
            style.removeAttribute(StyleConstants.FontSize);
            style.removeAttribute(StyleConstants.Bold);
            style.removeAttribute(StyleConstants.Italic);
        }
        else {
            StyleConstants.setFontFamily(style, font.getName());
            StyleConstants.setFontSize(style, font.getSize());
            StyleConstants.setBold(style, font.isBold());
            StyleConstants.setItalic(style, font.isItalic());
        }
    }
    
    @Override
    void paintBackground(final SynthContext synthContext, final Graphics graphics, final JComponent component) {
        synthContext.getPainter().paintTextPaneBackground(synthContext, graphics, 0, 0, component.getWidth(), component.getHeight());
    }
    
    @Override
    public void paintBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        synthContext.getPainter().paintTextPaneBorder(synthContext, graphics, n, n2, n3, n4);
    }
}
