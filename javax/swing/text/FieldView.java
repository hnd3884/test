package javax.swing.text;

import javax.swing.event.DocumentEvent;
import sun.swing.SwingUtilities2;
import javax.swing.JComponent;
import java.awt.Graphics;
import javax.swing.BoundedRangeModel;
import java.awt.Rectangle;
import java.awt.Component;
import javax.swing.JTextField;
import java.awt.Shape;
import java.awt.Container;
import java.awt.FontMetrics;

public class FieldView extends PlainView
{
    public FieldView(final Element element) {
        super(element);
    }
    
    protected FontMetrics getFontMetrics() {
        final Container container = this.getContainer();
        return container.getFontMetrics(container.getFont());
    }
    
    protected Shape adjustAllocation(final Shape shape) {
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
                    if (Utilities.isLeftToRight(container)) {
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
        super.paint(graphics, shape);
    }
    
    @Override
    Shape adjustPaintRegion(final Shape shape) {
        return this.adjustAllocation(shape);
    }
    
    @Override
    public float getPreferredSpan(final int n) {
        switch (n) {
            case 0: {
                final Segment sharedSegment = SegmentCache.getSharedSegment();
                final Document document = this.getDocument();
                int tabbedTextWidth;
                try {
                    final FontMetrics fontMetrics = this.getFontMetrics();
                    document.getText(0, document.getLength(), sharedSegment);
                    tabbedTextWidth = Utilities.getTabbedTextWidth(sharedSegment, fontMetrics, 0, this, 0);
                    if (sharedSegment.count > 0) {
                        final Container container = this.getContainer();
                        this.firstLineOffset = SwingUtilities2.getLeftSideBearing((container instanceof JComponent) ? ((JComponent)container) : null, fontMetrics, sharedSegment.array[sharedSegment.offset]);
                        this.firstLineOffset = Math.max(0, -this.firstLineOffset);
                    }
                    else {
                        this.firstLineOffset = 0;
                    }
                }
                catch (final BadLocationException ex) {
                    tabbedTextWidth = 0;
                }
                SegmentCache.releaseSharedSegment(sharedSegment);
                return (float)(tabbedTextWidth + this.firstLineOffset);
            }
            default: {
                return super.getPreferredSpan(n);
            }
        }
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
