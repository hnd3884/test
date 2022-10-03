package org.apache.poi.sl.draw;

import java.awt.BasicStroke;
import org.apache.poi.sl.usermodel.StrokeStyle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import org.apache.poi.sl.usermodel.PlaceableShape;
import java.awt.Graphics2D;
import java.util.Locale;
import org.apache.poi.sl.usermodel.Shape;

public class DrawShape implements Drawable
{
    protected final Shape<?, ?> shape;
    
    public DrawShape(final Shape<?, ?> shape) {
        this.shape = shape;
    }
    
    static boolean isHSLF(final Object shape) {
        return shape.getClass().getName().toLowerCase(Locale.ROOT).contains("hslf");
    }
    
    @Override
    public void applyTransform(final Graphics2D graphics) {
        if (!(this.shape instanceof PlaceableShape) || graphics == null) {
            return;
        }
        final Rectangle2D anchor = getAnchor(graphics, (PlaceableShape)this.shape);
        if (anchor == null) {
            return;
        }
        if (isHSLF(this.shape)) {
            this.flipHorizontal(graphics, anchor);
            this.flipVertical(graphics, anchor);
            this.rotate(graphics, anchor);
        }
        else {
            this.rotate(graphics, anchor);
            this.flipHorizontal(graphics, anchor);
            this.flipVertical(graphics, anchor);
        }
    }
    
    private void flipHorizontal(final Graphics2D graphics, final Rectangle2D anchor) {
        assert this.shape instanceof PlaceableShape && anchor != null;
        if (((PlaceableShape)this.shape).getFlipHorizontal()) {
            graphics.translate(anchor.getX() + anchor.getWidth(), anchor.getY());
            graphics.scale(-1.0, 1.0);
            graphics.translate(-anchor.getX(), -anchor.getY());
        }
    }
    
    private void flipVertical(final Graphics2D graphics, final Rectangle2D anchor) {
        assert this.shape instanceof PlaceableShape && anchor != null;
        if (((PlaceableShape)this.shape).getFlipVertical()) {
            graphics.translate(anchor.getX(), anchor.getY() + anchor.getHeight());
            graphics.scale(1.0, -1.0);
            graphics.translate(-anchor.getX(), -anchor.getY());
        }
    }
    
    private void rotate(final Graphics2D graphics, final Rectangle2D anchor) {
        assert this.shape instanceof PlaceableShape && anchor != null;
        final double rotation = ((PlaceableShape)this.shape).getRotation();
        if (rotation != 0.0) {
            graphics.rotate(Math.toRadians(rotation), anchor.getCenterX(), anchor.getCenterY());
        }
    }
    
    private static double safeScale(final double dim1, final double dim2) {
        return (dim1 == 0.0 || dim2 == 0.0) ? 1.0 : (dim1 / dim2);
    }
    
    @Override
    public void draw(final Graphics2D graphics) {
    }
    
    @Override
    public void drawContent(final Graphics2D graphics) {
    }
    
    public static Rectangle2D getAnchor(final Graphics2D graphics, final PlaceableShape<?, ?> shape) {
        final Rectangle2D shapeAnchor = shape.getAnchor();
        if (shapeAnchor == null) {
            return null;
        }
        final boolean isHSLF = isHSLF(shape);
        AffineTransform tx = (graphics == null) ? null : ((AffineTransform)graphics.getRenderingHint(Drawable.GROUP_TRANSFORM));
        if (tx == null) {
            tx = new AffineTransform();
        }
        final double rotation = (shape.getRotation() % 360.0 + 360.0) % 360.0;
        final int quadrant = ((int)rotation + 45) / 90 % 4;
        Rectangle2D normalizedShape;
        if (quadrant == 1 || quadrant == 3) {
            final Rectangle2D anchorO = tx.createTransformedShape(shapeAnchor).getBounds2D();
            final double centerX = anchorO.getCenterX();
            final double centerY = anchorO.getCenterY();
            final AffineTransform txs2 = new AffineTransform();
            if (!isHSLF) {
                txs2.quadrantRotate(1, centerX, centerY);
                txs2.concatenate(tx);
            }
            txs2.quadrantRotate(3, centerX, centerY);
            if (isHSLF) {
                txs2.concatenate(tx);
            }
            final Rectangle2D anchorT = txs2.createTransformedShape(shapeAnchor).getBounds2D();
            final double scaleX2 = safeScale(anchorO.getWidth(), anchorT.getWidth());
            final double scaleY2 = safeScale(anchorO.getHeight(), anchorT.getHeight());
            final double centerX2 = shapeAnchor.getCenterX();
            final double centerY2 = shapeAnchor.getCenterY();
            final AffineTransform txs3 = new AffineTransform();
            txs3.translate(centerX2, centerY2);
            txs3.scale(scaleY2, scaleX2);
            txs3.translate(-centerX2, -centerY2);
            normalizedShape = txs3.createTransformedShape(shapeAnchor).getBounds2D();
        }
        else {
            normalizedShape = shapeAnchor;
        }
        if (tx.isIdentity()) {
            return normalizedShape;
        }
        final java.awt.Shape anc = tx.createTransformedShape(normalizedShape);
        return (anc != null) ? anc.getBounds2D() : normalizedShape;
    }
    
    public static Rectangle2D getAnchor(final Graphics2D graphics, Rectangle2D anchor) {
        if (graphics == null) {
            return anchor;
        }
        final AffineTransform tx = (AffineTransform)graphics.getRenderingHint(Drawable.GROUP_TRANSFORM);
        if (tx != null && !tx.isIdentity() && tx.createTransformedShape(anchor) != null) {
            anchor = tx.createTransformedShape(anchor).getBounds2D();
        }
        return anchor;
    }
    
    protected Shape<?, ?> getShape() {
        return this.shape;
    }
    
    protected static BasicStroke getStroke(final StrokeStyle strokeStyle) {
        float lineWidth = (float)strokeStyle.getLineWidth();
        if (lineWidth == 0.0f) {
            lineWidth = 0.25f;
        }
        StrokeStyle.LineDash lineDash = strokeStyle.getLineDash();
        if (lineDash == null) {
            lineDash = StrokeStyle.LineDash.SOLID;
        }
        final int[] dashPatI = lineDash.pattern;
        final float dash_phase = 0.0f;
        float[] dashPatF = null;
        if (dashPatI != null) {
            dashPatF = new float[dashPatI.length];
            for (int i = 0; i < dashPatI.length; ++i) {
                dashPatF[i] = dashPatI[i] * Math.max(1.0f, lineWidth);
            }
        }
        StrokeStyle.LineCap lineCapE = strokeStyle.getLineCap();
        if (lineCapE == null) {
            lineCapE = StrokeStyle.LineCap.FLAT;
        }
        int lineCap = 0;
        switch (lineCapE) {
            case ROUND: {
                lineCap = 1;
                break;
            }
            case SQUARE: {
                lineCap = 2;
                break;
            }
            default: {
                lineCap = 0;
                break;
            }
        }
        final int lineJoin = 1;
        return new BasicStroke(lineWidth, lineCap, lineJoin, lineWidth, dashPatF, 0.0f);
    }
}
