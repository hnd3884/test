package javax.swing.text;

import java.awt.Rectangle;
import java.awt.Component;
import java.awt.Shape;
import java.awt.Graphics;
import javax.swing.Icon;

public class IconView extends View
{
    private Icon c;
    
    public IconView(final Element element) {
        super(element);
        this.c = StyleConstants.getIcon(element.getAttributes());
    }
    
    @Override
    public void paint(final Graphics graphics, final Shape shape) {
        final Rectangle bounds = shape.getBounds();
        this.c.paintIcon(this.getContainer(), graphics, bounds.x, bounds.y);
    }
    
    @Override
    public float getPreferredSpan(final int n) {
        switch (n) {
            case 0: {
                return (float)this.c.getIconWidth();
            }
            case 1: {
                return (float)this.c.getIconHeight();
            }
            default: {
                throw new IllegalArgumentException("Invalid axis: " + n);
            }
        }
    }
    
    @Override
    public float getAlignment(final int n) {
        switch (n) {
            case 1: {
                return 1.0f;
            }
            default: {
                return super.getAlignment(n);
            }
        }
    }
    
    @Override
    public Shape modelToView(final int n, final Shape shape, final Position.Bias bias) throws BadLocationException {
        final int startOffset = this.getStartOffset();
        final int endOffset = this.getEndOffset();
        if (n >= startOffset && n <= endOffset) {
            final Rectangle bounds = shape.getBounds();
            if (n == endOffset) {
                final Rectangle rectangle = bounds;
                rectangle.x += bounds.width;
            }
            bounds.width = 0;
            return bounds;
        }
        throw new BadLocationException(n + " not in range " + startOffset + "," + endOffset, n);
    }
    
    @Override
    public int viewToModel(final float n, final float n2, final Shape shape, final Position.Bias[] array) {
        final Rectangle rectangle = (Rectangle)shape;
        if (n < rectangle.x + rectangle.width / 2) {
            array[0] = Position.Bias.Forward;
            return this.getStartOffset();
        }
        array[0] = Position.Bias.Backward;
        return this.getEndOffset();
    }
}
