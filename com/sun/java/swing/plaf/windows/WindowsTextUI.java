package com.sun.java.swing.plaf.windows;

import javax.swing.text.Position;
import javax.swing.text.View;
import javax.swing.plaf.TextUI;
import java.awt.Rectangle;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import java.awt.Shape;
import java.awt.Graphics;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.plaf.UIResource;
import javax.swing.text.DefaultCaret;
import java.awt.Color;
import javax.swing.text.Caret;
import javax.swing.text.LayeredHighlighter;
import javax.swing.plaf.basic.BasicTextUI;

public abstract class WindowsTextUI extends BasicTextUI
{
    static LayeredHighlighter.LayerPainter WindowsPainter;
    
    @Override
    protected Caret createCaret() {
        return new WindowsCaret();
    }
    
    static {
        WindowsTextUI.WindowsPainter = new WindowsHighlightPainter(null);
    }
    
    static class WindowsCaret extends DefaultCaret implements UIResource
    {
        @Override
        protected Highlighter.HighlightPainter getSelectionPainter() {
            return WindowsTextUI.WindowsPainter;
        }
    }
    
    static class WindowsHighlightPainter extends DefaultHighlighter.DefaultHighlightPainter
    {
        WindowsHighlightPainter(final Color color) {
            super(color);
        }
        
        @Override
        public void paint(final Graphics graphics, final int n, final int n2, final Shape shape, final JTextComponent textComponent) {
            final Rectangle bounds = shape.getBounds();
            try {
                final TextUI ui = textComponent.getUI();
                final Rectangle modelToView = ui.modelToView(textComponent, n);
                final Rectangle modelToView2 = ui.modelToView(textComponent, n2);
                final Color color = this.getColor();
                if (color == null) {
                    graphics.setColor(textComponent.getSelectionColor());
                }
                else {
                    graphics.setColor(color);
                }
                boolean b = false;
                boolean b2 = false;
                if (textComponent.isEditable()) {
                    final int caretPosition = textComponent.getCaretPosition();
                    b = (n == caretPosition);
                    b2 = (n2 == caretPosition);
                }
                if (modelToView.y == modelToView2.y) {
                    final Rectangle union = modelToView.union(modelToView2);
                    if (union.width > 0) {
                        if (b) {
                            final Rectangle rectangle = union;
                            ++rectangle.x;
                            final Rectangle rectangle2 = union;
                            --rectangle2.width;
                        }
                        else if (b2) {
                            final Rectangle rectangle3 = union;
                            --rectangle3.width;
                        }
                    }
                    graphics.fillRect(union.x, union.y, union.width, union.height);
                }
                else {
                    int n3 = bounds.x + bounds.width - modelToView.x;
                    if (b && n3 > 0) {
                        final Rectangle rectangle4 = modelToView;
                        ++rectangle4.x;
                        --n3;
                    }
                    graphics.fillRect(modelToView.x, modelToView.y, n3, modelToView.height);
                    if (modelToView.y + modelToView.height != modelToView2.y) {
                        graphics.fillRect(bounds.x, modelToView.y + modelToView.height, bounds.width, modelToView2.y - (modelToView.y + modelToView.height));
                    }
                    if (b2 && modelToView2.x > bounds.x) {
                        final Rectangle rectangle5 = modelToView2;
                        --rectangle5.x;
                    }
                    graphics.fillRect(bounds.x, modelToView2.y, modelToView2.x - bounds.x, modelToView2.height);
                }
            }
            catch (final BadLocationException ex) {}
        }
        
        @Override
        public Shape paintLayer(final Graphics graphics, final int n, final int n2, final Shape shape, final JTextComponent textComponent, final View view) {
            final Color color = this.getColor();
            if (color == null) {
                graphics.setColor(textComponent.getSelectionColor());
            }
            else {
                graphics.setColor(color);
            }
            boolean b = false;
            boolean b2 = false;
            if (textComponent.isEditable()) {
                final int caretPosition = textComponent.getCaretPosition();
                b = (n == caretPosition);
                b2 = (n2 == caretPosition);
            }
            if (n == view.getStartOffset() && n2 == view.getEndOffset()) {
                Rectangle bounds;
                if (shape instanceof Rectangle) {
                    bounds = (Rectangle)shape;
                }
                else {
                    bounds = shape.getBounds();
                }
                if (b && bounds.width > 0) {
                    graphics.fillRect(bounds.x + 1, bounds.y, bounds.width - 1, bounds.height);
                }
                else if (b2 && bounds.width > 0) {
                    graphics.fillRect(bounds.x, bounds.y, bounds.width - 1, bounds.height);
                }
                else {
                    graphics.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
                }
                return bounds;
            }
            try {
                final Shape modelToView = view.modelToView(n, Position.Bias.Forward, n2, Position.Bias.Backward, shape);
                final Rectangle rectangle = (Rectangle)((modelToView instanceof Rectangle) ? modelToView : modelToView.getBounds());
                if (b && rectangle.width > 0) {
                    graphics.fillRect(rectangle.x + 1, rectangle.y, rectangle.width - 1, rectangle.height);
                }
                else if (b2 && rectangle.width > 0) {
                    graphics.fillRect(rectangle.x, rectangle.y, rectangle.width - 1, rectangle.height);
                }
                else {
                    graphics.fillRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
                }
                return rectangle;
            }
            catch (final BadLocationException ex) {
                return null;
            }
        }
    }
}
