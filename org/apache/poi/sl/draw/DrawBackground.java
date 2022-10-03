package org.apache.poi.sl.draw;

import java.awt.Paint;
import java.awt.Dimension;
import java.awt.RenderingHints;
import org.apache.poi.sl.usermodel.Sheet;
import org.apache.poi.sl.usermodel.ShapeContainer;
import org.apache.poi.sl.usermodel.PlaceableShape;
import java.awt.geom.Rectangle2D;
import java.awt.Graphics2D;
import org.apache.poi.sl.usermodel.Shape;
import org.apache.poi.sl.usermodel.Background;

public class DrawBackground extends DrawShape
{
    public DrawBackground(final Background<?, ?> shape) {
        super(shape);
    }
    
    @Override
    public void draw(final Graphics2D graphics) {
        final Dimension pg = this.shape.getSheet().getSlideShow().getPageSize();
        final Rectangle2D anchor = new Rectangle2D.Double(0.0, 0.0, pg.getWidth(), pg.getHeight());
        final PlaceableShape<?, ?> ps = new PlaceableShape() {
            @Override
            public ShapeContainer<?, ?> getParent() {
                return null;
            }
            
            @Override
            public Rectangle2D getAnchor() {
                return anchor;
            }
            
            @Override
            public void setAnchor(final Rectangle2D newAnchor) {
            }
            
            @Override
            public double getRotation() {
                return 0.0;
            }
            
            @Override
            public void setRotation(final double theta) {
            }
            
            @Override
            public void setFlipHorizontal(final boolean flip) {
            }
            
            @Override
            public void setFlipVertical(final boolean flip) {
            }
            
            @Override
            public boolean getFlipHorizontal() {
                return false;
            }
            
            @Override
            public boolean getFlipVertical() {
                return false;
            }
            
            @Override
            public Sheet<?, ?> getSheet() {
                return DrawBackground.this.shape.getSheet();
            }
        };
        final DrawFactory drawFact = DrawFactory.getInstance(graphics);
        final DrawPaint dp = drawFact.getPaint(ps);
        final Paint fill = dp.getPaint(graphics, this.getShape().getFillStyle().getPaint());
        final Rectangle2D anchor2 = DrawShape.getAnchor(graphics, anchor);
        if (fill != null) {
            graphics.setRenderingHint(Drawable.GRADIENT_SHAPE, anchor);
            graphics.setPaint(fill);
            DrawPaint.fillPaintWorkaround(graphics, anchor2);
        }
    }
    
    @Override
    protected Background<?, ?> getShape() {
        return (Background)this.shape;
    }
}
