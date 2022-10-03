package com.sun.java.swing.plaf.windows;

import javax.swing.BoundedRangeModel;
import java.awt.Insets;
import javax.swing.plaf.TextUI;
import javax.swing.text.BadLocationException;
import javax.swing.text.Position;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;
import javax.swing.text.Highlighter;
import javax.swing.SwingUtilities;
import java.awt.Rectangle;
import javax.swing.plaf.UIResource;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Caret;
import java.awt.Graphics;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicTextFieldUI;

public class WindowsTextFieldUI extends BasicTextFieldUI
{
    public static ComponentUI createUI(final JComponent component) {
        return new WindowsTextFieldUI();
    }
    
    @Override
    protected void paintBackground(final Graphics graphics) {
        super.paintBackground(graphics);
    }
    
    @Override
    protected Caret createCaret() {
        return new WindowsFieldCaret();
    }
    
    static class WindowsFieldCaret extends DefaultCaret implements UIResource
    {
        public WindowsFieldCaret() {
        }
        
        @Override
        protected void adjustVisibility(final Rectangle rectangle) {
            SwingUtilities.invokeLater(new SafeScroller(rectangle));
        }
        
        @Override
        protected Highlighter.HighlightPainter getSelectionPainter() {
            return WindowsTextUI.WindowsPainter;
        }
        
        private class SafeScroller implements Runnable
        {
            private Rectangle r;
            
            SafeScroller(final Rectangle r) {
                this.r = r;
            }
            
            @Override
            public void run() {
                final JTextField textField = (JTextField)DefaultCaret.this.getComponent();
                if (textField != null) {
                    final TextUI ui = textField.getUI();
                    final int dot = WindowsFieldCaret.this.getDot();
                    final Position.Bias forward = Position.Bias.Forward;
                    Object modelToView = null;
                    try {
                        modelToView = ui.modelToView(textField, dot, forward);
                    }
                    catch (final BadLocationException ex) {}
                    final Insets insets = textField.getInsets();
                    final BoundedRangeModel horizontalVisibility = textField.getHorizontalVisibility();
                    final int n = this.r.x + horizontalVisibility.getValue() - insets.left;
                    final int n2 = horizontalVisibility.getExtent() / 4;
                    if (this.r.x < insets.left) {
                        horizontalVisibility.setValue(n - n2);
                    }
                    else if (this.r.x + this.r.width > insets.left + horizontalVisibility.getExtent()) {
                        horizontalVisibility.setValue(n - 3 * n2);
                    }
                    if (modelToView != null) {
                        try {
                            final Rectangle modelToView2 = ui.modelToView(textField, dot, forward);
                            if (modelToView2 != null && !modelToView2.equals(modelToView)) {
                                DefaultCaret.this.damage(modelToView2);
                            }
                        }
                        catch (final BadLocationException ex2) {}
                    }
                }
            }
        }
    }
}
