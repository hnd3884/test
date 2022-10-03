package org.apache.poi.sl.draw;

import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.Composite;
import java.awt.AlphaComposite;
import java.util.Map;
import java.util.Hashtable;
import java.awt.image.BufferedImage;
import java.awt.geom.Area;
import java.awt.BasicStroke;
import java.awt.image.Raster;
import java.awt.LinearGradientPaint;
import java.awt.MultipleGradientPaint;
import java.awt.geom.Point2D;
import java.awt.geom.IllegalPathStateException;
import java.awt.image.WritableRaster;
import java.awt.Shape;
import java.awt.PaintContext;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.Rectangle;
import java.awt.image.ColorModel;
import java.awt.Color;
import org.apache.poi.util.Internal;
import java.awt.Paint;

@Internal
class PathGradientPaint implements Paint
{
    private final Color[] colors;
    private final float[] fractions;
    private final int capStyle;
    private final int joinStyle;
    private final int transparency;
    
    PathGradientPaint(final float[] fractions, final Color[] colors) {
        this(fractions, colors, 1, 1);
    }
    
    private PathGradientPaint(final float[] fractions, final Color[] colors, final int capStyle, final int joinStyle) {
        this.colors = colors.clone();
        this.fractions = fractions.clone();
        this.capStyle = capStyle;
        this.joinStyle = joinStyle;
        boolean opaque = true;
        for (final Color c : colors) {
            if (c != null) {
                opaque = (opaque && c.getAlpha() == 255);
            }
        }
        this.transparency = (opaque ? 1 : 3);
    }
    
    @Override
    public PaintContext createContext(final ColorModel cm, final Rectangle deviceBounds, final Rectangle2D userBounds, final AffineTransform transform, final RenderingHints hints) {
        return new PathGradientContext(cm, deviceBounds, userBounds, transform, hints);
    }
    
    @Override
    public int getTransparency() {
        return this.transparency;
    }
    
    class PathGradientContext implements PaintContext
    {
        final Rectangle deviceBounds;
        final Rectangle2D userBounds;
        protected final AffineTransform xform;
        final RenderingHints hints;
        protected final Shape shape;
        final PaintContext pCtx;
        final int gradientSteps;
        WritableRaster raster;
        
        PathGradientContext(final ColorModel cm, final Rectangle deviceBounds, final Rectangle2D userBounds, final AffineTransform xform, final RenderingHints hints) {
            this.shape = (Shape)hints.get(Drawable.GRADIENT_SHAPE);
            if (this.shape == null) {
                throw new IllegalPathStateException("PathGradientPaint needs a shape to be set via the rendering hint Drawable.GRADIANT_SHAPE.");
            }
            this.deviceBounds = deviceBounds;
            this.userBounds = userBounds;
            this.xform = xform;
            this.hints = hints;
            this.gradientSteps = this.getGradientSteps(this.shape);
            final Point2D start = new Point2D.Double(0.0, 0.0);
            final Point2D end = new Point2D.Double(this.gradientSteps, 0.0);
            final LinearGradientPaint gradientPaint = new LinearGradientPaint(start, end, PathGradientPaint.this.fractions, PathGradientPaint.this.colors, MultipleGradientPaint.CycleMethod.NO_CYCLE, MultipleGradientPaint.ColorSpaceType.SRGB, new AffineTransform());
            final Rectangle bounds = new Rectangle(0, 0, this.gradientSteps, 1);
            this.pCtx = gradientPaint.createContext(cm, bounds, bounds, new AffineTransform(), hints);
        }
        
        @Override
        public void dispose() {
        }
        
        @Override
        public ColorModel getColorModel() {
            return this.pCtx.getColorModel();
        }
        
        @Override
        public Raster getRaster(final int xOffset, final int yOffset, final int w, final int h) {
            final ColorModel cm = this.getColorModel();
            if (this.raster == null) {
                this.createRaster();
            }
            final WritableRaster childRaster = cm.createCompatibleWritableRaster(w, h);
            final Rectangle2D childRect = new Rectangle2D.Double(xOffset, yOffset, w, h);
            if (!childRect.intersects(this.deviceBounds)) {
                return childRaster;
            }
            final Rectangle2D destRect = new Rectangle2D.Double();
            Rectangle2D.intersect(childRect, this.deviceBounds, destRect);
            int dx = (int)(destRect.getX() - this.deviceBounds.getX());
            int dy = (int)(destRect.getY() - this.deviceBounds.getY());
            final int dw = (int)destRect.getWidth();
            final int dh = (int)destRect.getHeight();
            final Object data = this.raster.getDataElements(dx, dy, dw, dh, null);
            dx = (int)(destRect.getX() - childRect.getX());
            dy = (int)(destRect.getY() - childRect.getY());
            childRaster.setDataElements(dx, dy, dw, dh, data);
            return childRaster;
        }
        
        int getGradientSteps(final Shape gradientShape) {
            final Rectangle rect = gradientShape.getBounds();
            int lower = 1;
            int upper = (int)(Math.max(rect.getWidth(), rect.getHeight()) / 2.0);
            while (lower < upper - 1) {
                final int mid = lower + (upper - lower) / 2;
                final BasicStroke bs = new BasicStroke((float)mid, PathGradientPaint.this.capStyle, PathGradientPaint.this.joinStyle);
                final Area area = new Area(bs.createStrokedShape(gradientShape));
                if (area.isSingular()) {
                    upper = mid;
                }
                else {
                    lower = mid;
                }
            }
            return upper;
        }
        
        void createRaster() {
            final ColorModel cm = this.getColorModel();
            this.raster = cm.createCompatibleWritableRaster((int)this.deviceBounds.getWidth(), (int)this.deviceBounds.getHeight());
            final BufferedImage img = new BufferedImage(cm, this.raster, false, null);
            final Graphics2D graphics = img.createGraphics();
            graphics.setRenderingHints(this.hints);
            graphics.translate(-this.deviceBounds.getX(), -this.deviceBounds.getY());
            graphics.transform(this.xform);
            final Raster img2 = this.pCtx.getRaster(0, 0, this.gradientSteps, 1);
            final int[] rgb = new int[cm.getNumComponents()];
            for (int i = this.gradientSteps - 1; i >= 0; --i) {
                img2.getPixel(this.gradientSteps - i - 1, 0, rgb);
                final Color c = new Color(rgb[0], rgb[1], rgb[2]);
                if (rgb.length == 4) {
                    graphics.setComposite(AlphaComposite.getInstance(2, rgb[3] / 255.0f));
                }
                graphics.setStroke(new BasicStroke((float)(i + 1), PathGradientPaint.this.capStyle, PathGradientPaint.this.joinStyle));
                graphics.setColor(c);
                if (i == this.gradientSteps - 1) {
                    graphics.fill(this.shape);
                }
                graphics.draw(this.shape);
            }
            graphics.dispose();
        }
    }
}
