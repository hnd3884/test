package com.sun.java.swing.plaf.motif;

import java.awt.Color;
import javax.swing.text.BadLocationException;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.FocusEvent;
import javax.swing.plaf.UIResource;
import javax.swing.text.DefaultCaret;
import javax.swing.KeyStroke;
import javax.swing.text.Caret;
import javax.swing.text.JTextComponent;

public class MotifTextUI
{
    static final JTextComponent.KeyBinding[] defaultBindings;
    
    public static Caret createCaret() {
        return new MotifCaret();
    }
    
    static {
        defaultBindings = new JTextComponent.KeyBinding[] { new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(155, 2), "copy-to-clipboard"), new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(155, 1), "paste-from-clipboard"), new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(127, 1), "cut-to-clipboard"), new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(37, 1), "selection-backward"), new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(39, 1), "selection-forward") };
    }
    
    public static class MotifCaret extends DefaultCaret implements UIResource
    {
        static final int IBeamOverhang = 2;
        
        @Override
        public void focusGained(final FocusEvent focusEvent) {
            super.focusGained(focusEvent);
            this.getComponent().repaint();
        }
        
        @Override
        public void focusLost(final FocusEvent focusEvent) {
            super.focusLost(focusEvent);
            this.getComponent().repaint();
        }
        
        @Override
        protected void damage(final Rectangle rectangle) {
            if (rectangle != null) {
                this.x = rectangle.x - 2 - 1;
                this.y = rectangle.y;
                this.width = rectangle.width + 4 + 3;
                this.height = rectangle.height;
                this.repaint();
            }
        }
        
        @Override
        public void paint(final Graphics graphics) {
            if (this.isVisible()) {
                try {
                    final JTextComponent component = this.getComponent();
                    final Color color = component.hasFocus() ? component.getCaretColor() : component.getDisabledTextColor();
                    final Rectangle modelToView = component.getUI().modelToView(component, this.getDot());
                    final int n = modelToView.x - 2;
                    final int n2 = modelToView.x + 2;
                    final int n3 = modelToView.y + 1;
                    final int n4 = modelToView.y + modelToView.height - 2;
                    graphics.setColor(color);
                    graphics.drawLine(modelToView.x, n3, modelToView.x, n4);
                    graphics.drawLine(n, n3, n2, n3);
                    graphics.drawLine(n, n4, n2, n4);
                }
                catch (final BadLocationException ex) {}
            }
        }
    }
}
