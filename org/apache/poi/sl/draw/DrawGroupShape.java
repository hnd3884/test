package org.apache.poi.sl.draw;

import java.util.Iterator;
import java.awt.geom.Rectangle2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.Graphics2D;
import org.apache.poi.sl.usermodel.Shape;
import org.apache.poi.sl.usermodel.GroupShape;

public class DrawGroupShape extends DrawShape
{
    public DrawGroupShape(final GroupShape<?, ?> shape) {
        super(shape);
    }
    
    @Override
    public void draw(final Graphics2D graphics) {
        final Rectangle2D interior = this.getShape().getInteriorAnchor();
        final Rectangle2D exterior = this.getShape().getAnchor();
        final AffineTransform tx = (AffineTransform)graphics.getRenderingHint(Drawable.GROUP_TRANSFORM);
        final AffineTransform tx2 = new AffineTransform(tx);
        final double scaleX = (interior.getWidth() == 0.0) ? 1.0 : (exterior.getWidth() / interior.getWidth());
        final double scaleY = (interior.getHeight() == 0.0) ? 1.0 : (exterior.getHeight() / interior.getHeight());
        tx.translate(exterior.getX(), exterior.getY());
        tx.scale(scaleX, scaleY);
        tx.translate(-interior.getX(), -interior.getY());
        final DrawFactory drawFact = DrawFactory.getInstance(graphics);
        final AffineTransform at2 = graphics.getTransform();
        for (final Shape<?, ?> child : this.getShape()) {
            final AffineTransform at3 = graphics.getTransform();
            graphics.setRenderingHint(Drawable.GSAVE, true);
            final Drawable draw = drawFact.getDrawable(child);
            draw.applyTransform(graphics);
            draw.draw(graphics);
            graphics.setTransform(at3);
            graphics.setRenderingHint(Drawable.GRESTORE, true);
        }
        graphics.setTransform(at2);
        graphics.setRenderingHint(Drawable.GROUP_TRANSFORM, tx2);
    }
    
    @Override
    protected GroupShape<?, ?> getShape() {
        return (GroupShape)this.shape;
    }
}
