package javax.swing.text.html;

import java.awt.Rectangle;
import javax.swing.text.View;
import java.awt.Shape;
import java.awt.Graphics;
import javax.swing.text.Element;

public class ListView extends BlockView
{
    private StyleSheet.ListPainter listPainter;
    
    public ListView(final Element element) {
        super(element, 1);
    }
    
    @Override
    public float getAlignment(final int n) {
        switch (n) {
            case 0: {
                return 0.5f;
            }
            case 1: {
                return 0.5f;
            }
            default: {
                throw new IllegalArgumentException("Invalid axis: " + n);
            }
        }
    }
    
    @Override
    public void paint(final Graphics graphics, final Shape shape) {
        super.paint(graphics, shape);
        final Rectangle bounds = shape.getBounds();
        final Rectangle clipBounds = graphics.getClipBounds();
        if (clipBounds.x + clipBounds.width < bounds.x + this.getLeftInset()) {
            final Rectangle rectangle = bounds;
            final Rectangle insideAllocation = this.getInsideAllocation(shape);
            final int viewCount = this.getViewCount();
            final int n = clipBounds.y + clipBounds.height;
            for (int i = 0; i < viewCount; ++i) {
                rectangle.setBounds(insideAllocation);
                this.childAllocation(i, rectangle);
                if (rectangle.y >= n) {
                    break;
                }
                if (rectangle.y + rectangle.height >= clipBounds.y) {
                    this.listPainter.paint(graphics, (float)rectangle.x, (float)rectangle.y, (float)rectangle.width, (float)rectangle.height, this, i);
                }
            }
        }
    }
    
    @Override
    protected void paintChild(final Graphics graphics, final Rectangle rectangle, final int n) {
        this.listPainter.paint(graphics, (float)rectangle.x, (float)rectangle.y, (float)rectangle.width, (float)rectangle.height, this, n);
        super.paintChild(graphics, rectangle, n);
    }
    
    @Override
    protected void setPropertiesFromAttributes() {
        super.setPropertiesFromAttributes();
        this.listPainter = this.getStyleSheet().getListPainter(this.getAttributes());
    }
}
