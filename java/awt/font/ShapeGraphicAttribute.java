package java.awt.font;

import java.awt.geom.AffineTransform;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.Shape;

public final class ShapeGraphicAttribute extends GraphicAttribute
{
    private Shape fShape;
    private boolean fStroke;
    public static final boolean STROKE = true;
    public static final boolean FILL = false;
    private Rectangle2D fShapeBounds;
    
    public ShapeGraphicAttribute(final Shape fShape, final int n, final boolean fStroke) {
        super(n);
        this.fShape = fShape;
        this.fStroke = fStroke;
        this.fShapeBounds = this.fShape.getBounds2D();
    }
    
    @Override
    public float getAscent() {
        return (float)Math.max(0.0, -this.fShapeBounds.getMinY());
    }
    
    @Override
    public float getDescent() {
        return (float)Math.max(0.0, this.fShapeBounds.getMaxY());
    }
    
    @Override
    public float getAdvance() {
        return (float)Math.max(0.0, this.fShapeBounds.getMaxX());
    }
    
    @Override
    public void draw(final Graphics2D graphics2D, final float n, final float n2) {
        graphics2D.translate((int)n, (int)n2);
        try {
            if (this.fStroke) {
                graphics2D.draw(this.fShape);
            }
            else {
                graphics2D.fill(this.fShape);
            }
        }
        finally {
            graphics2D.translate(-(int)n, -(int)n2);
        }
    }
    
    @Override
    public Rectangle2D getBounds() {
        final Rectangle2D.Float float1 = new Rectangle2D.Float();
        float1.setRect(this.fShapeBounds);
        if (this.fStroke) {
            final Rectangle2D.Float float2 = float1;
            ++float2.width;
            final Rectangle2D.Float float3 = float1;
            ++float3.height;
        }
        return float1;
    }
    
    @Override
    public Shape getOutline(final AffineTransform affineTransform) {
        return (affineTransform == null) ? this.fShape : affineTransform.createTransformedShape(this.fShape);
    }
    
    @Override
    public int hashCode() {
        return this.fShape.hashCode();
    }
    
    @Override
    public boolean equals(final Object o) {
        try {
            return this.equals((ShapeGraphicAttribute)o);
        }
        catch (final ClassCastException ex) {
            return false;
        }
    }
    
    public boolean equals(final ShapeGraphicAttribute shapeGraphicAttribute) {
        return shapeGraphicAttribute != null && (this == shapeGraphicAttribute || (this.fStroke == shapeGraphicAttribute.fStroke && this.getAlignment() == shapeGraphicAttribute.getAlignment() && this.fShape.equals(shapeGraphicAttribute.fShape)));
    }
}
