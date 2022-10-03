package javax.swing.plaf.basic;

import javax.swing.text.ViewFactory;
import javax.swing.event.DocumentEvent;
import javax.swing.text.BadLocationException;
import javax.swing.text.Position;
import java.awt.Graphics;
import javax.swing.BoundedRangeModel;
import java.awt.Container;
import java.awt.Rectangle;
import javax.swing.JTextField;
import java.awt.Shape;
import javax.swing.text.ParagraphView;
import java.awt.Component;
import java.awt.Insets;
import javax.swing.text.JTextComponent;
import javax.swing.text.FieldView;
import javax.swing.text.GlyphView;
import javax.swing.text.View;
import javax.swing.text.Element;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;

public class BasicTextFieldUI extends BasicTextUI
{
    public static ComponentUI createUI(final JComponent component) {
        return new BasicTextFieldUI();
    }
    
    @Override
    protected String getPropertyPrefix() {
        return "TextField";
    }
    
    @Override
    public View create(final Element element) {
        if (Boolean.TRUE.equals(element.getDocument().getProperty("i18n"))) {
            final String name = element.getName();
            if (name != null) {
                if (name.equals("content")) {
                    return new GlyphView(element) {
                        @Override
                        public float getMinimumSpan(final int n) {
                            return this.getPreferredSpan(n);
                        }
                    };
                }
                if (name.equals("paragraph")) {
                    return new I18nFieldView(element);
                }
            }
        }
        return new FieldView(element);
    }
    
    @Override
    public int getBaseline(final JComponent component, final int n, int n2) {
        super.getBaseline(component, n, n2);
        final View rootView = this.getRootView((JTextComponent)component);
        if (rootView.getViewCount() > 0) {
            final Insets insets = component.getInsets();
            n2 = n2 - insets.top - insets.bottom;
            if (n2 > 0) {
                int top = insets.top;
                final View view = rootView.getView(0);
                final int n3 = (int)view.getPreferredSpan(1);
                if (n2 != n3) {
                    top += (n2 - n3) / 2;
                }
                int n4;
                if (view instanceof I18nFieldView) {
                    final int baseline = BasicHTML.getBaseline(view, n - insets.left - insets.right, n2);
                    if (baseline < 0) {
                        return -1;
                    }
                    n4 = top + baseline;
                }
                else {
                    n4 = top + component.getFontMetrics(component.getFont()).getAscent();
                }
                return n4;
            }
        }
        return -1;
    }
    
    @Override
    public Component.BaselineResizeBehavior getBaselineResizeBehavior(final JComponent component) {
        super.getBaselineResizeBehavior(component);
        return Component.BaselineResizeBehavior.CENTER_OFFSET;
    }
    
    static class I18nFieldView extends ParagraphView
    {
        I18nFieldView(final Element element) {
            super(element);
        }
        
        @Override
        public int getFlowSpan(final int n) {
            return Integer.MAX_VALUE;
        }
        
        @Override
        protected void setJustification(final int n) {
        }
        
        static boolean isLeftToRight(final Component component) {
            return component.getComponentOrientation().isLeftToRight();
        }
        
        Shape adjustAllocation(final Shape shape) {
            if (shape != null) {
                final Rectangle bounds = shape.getBounds();
                final int n = (int)this.getPreferredSpan(1);
                final int width = (int)this.getPreferredSpan(0);
                if (bounds.height != n) {
                    final int n2 = bounds.height - n;
                    final Rectangle rectangle = bounds;
                    rectangle.y += n2 / 2;
                    final Rectangle rectangle2 = bounds;
                    rectangle2.height -= n2;
                }
                final Container container = this.getContainer();
                if (container instanceof JTextField) {
                    final BoundedRangeModel horizontalVisibility = ((JTextField)container).getHorizontalVisibility();
                    final int max = Math.max(width, bounds.width);
                    int value = horizontalVisibility.getValue();
                    final int min = Math.min(max, bounds.width - 1);
                    if (value + min > max) {
                        value = max - min;
                    }
                    horizontalVisibility.setRangeProperties(value, min, horizontalVisibility.getMinimum(), max, false);
                    if (width < bounds.width) {
                        final int n3 = bounds.width - 1 - width;
                        int horizontalAlignment = ((JTextField)container).getHorizontalAlignment();
                        if (isLeftToRight(container)) {
                            if (horizontalAlignment == 10) {
                                horizontalAlignment = 2;
                            }
                            else if (horizontalAlignment == 11) {
                                horizontalAlignment = 4;
                            }
                        }
                        else if (horizontalAlignment == 10) {
                            horizontalAlignment = 4;
                        }
                        else if (horizontalAlignment == 11) {
                            horizontalAlignment = 2;
                        }
                        switch (horizontalAlignment) {
                            case 0: {
                                final Rectangle rectangle3 = bounds;
                                rectangle3.x += n3 / 2;
                                final Rectangle rectangle4 = bounds;
                                rectangle4.width -= n3;
                                break;
                            }
                            case 4: {
                                final Rectangle rectangle5 = bounds;
                                rectangle5.x += n3;
                                final Rectangle rectangle6 = bounds;
                                rectangle6.width -= n3;
                                break;
                            }
                        }
                    }
                    else {
                        bounds.width = width;
                        final Rectangle rectangle7 = bounds;
                        rectangle7.x -= horizontalVisibility.getValue();
                    }
                }
                return bounds;
            }
            return null;
        }
        
        void updateVisibilityModel() {
            final Container container = this.getContainer();
            if (container instanceof JTextField) {
                final BoundedRangeModel horizontalVisibility = ((JTextField)container).getHorizontalVisibility();
                final int n = (int)this.getPreferredSpan(0);
                final int extent = horizontalVisibility.getExtent();
                final int max = Math.max(n, extent);
                final int n2 = (extent == 0) ? max : extent;
                final int n3 = max - n2;
                int value = horizontalVisibility.getValue();
                if (value + n2 > max) {
                    value = max - n2;
                }
                horizontalVisibility.setRangeProperties(Math.max(0, Math.min(n3, value)), n2, 0, max, false);
            }
        }
        
        @Override
        public void paint(final Graphics graphics, final Shape shape) {
            final Rectangle rectangle = (Rectangle)shape;
            graphics.clipRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
            super.paint(graphics, this.adjustAllocation(shape));
        }
        
        @Override
        public int getResizeWeight(final int n) {
            if (n == 0) {
                return 1;
            }
            return 0;
        }
        
        @Override
        public Shape modelToView(final int n, final Shape shape, final Position.Bias bias) throws BadLocationException {
            return super.modelToView(n, this.adjustAllocation(shape), bias);
        }
        
        @Override
        public Shape modelToView(final int n, final Position.Bias bias, final int n2, final Position.Bias bias2, final Shape shape) throws BadLocationException {
            return super.modelToView(n, bias, n2, bias2, this.adjustAllocation(shape));
        }
        
        @Override
        public int viewToModel(final float n, final float n2, final Shape shape, final Position.Bias[] array) {
            return super.viewToModel(n, n2, this.adjustAllocation(shape), array);
        }
        
        @Override
        public void insertUpdate(final DocumentEvent documentEvent, final Shape shape, final ViewFactory viewFactory) {
            super.insertUpdate(documentEvent, this.adjustAllocation(shape), viewFactory);
            this.updateVisibilityModel();
        }
        
        @Override
        public void removeUpdate(final DocumentEvent documentEvent, final Shape shape, final ViewFactory viewFactory) {
            super.removeUpdate(documentEvent, this.adjustAllocation(shape), viewFactory);
            this.updateVisibilityModel();
        }
    }
}
