package org.apache.poi.sl.draw;

import org.apache.poi.util.POILogFactory;
import org.apache.poi.sl.usermodel.Shape;
import org.apache.poi.sl.usermodel.RectAlign;
import java.awt.Dimension;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Insets;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import org.apache.poi.sl.usermodel.PictureData;
import org.apache.poi.sl.usermodel.PlaceableShape;
import java.awt.Graphics2D;
import org.apache.poi.sl.usermodel.SimpleShape;
import org.apache.poi.sl.usermodel.PictureShape;
import org.apache.poi.util.POILogger;

public class DrawPictureShape extends DrawSimpleShape
{
    private static final POILogger LOG;
    private static final String[] KNOWN_RENDERER;
    
    public DrawPictureShape(final PictureShape<?, ?> shape) {
        super(shape);
    }
    
    @Override
    public void drawContent(final Graphics2D graphics) {
        final PictureShape<?, ?> ps = this.getShape();
        final Rectangle2D anchor = DrawShape.getAnchor(graphics, ps);
        final Insets insets = ps.getClipping();
        final PictureData[] array;
        final PictureData[] pics = array = new PictureData[] { ps.getAlternativePictureData(), ps.getPictureData() };
        for (final PictureData data : array) {
            if (data != null) {
                try {
                    final String ct = data.getContentType();
                    final ImageRenderer renderer = getImageRenderer(graphics, ct);
                    if (renderer.canRender(ct)) {
                        renderer.loadImage(data.getData(), ct);
                        renderer.drawImage(graphics, anchor, insets);
                        return;
                    }
                }
                catch (final IOException e) {
                    DrawPictureShape.LOG.log(7, "image can't be loaded/rendered.", e);
                }
            }
        }
    }
    
    public static ImageRenderer getImageRenderer(final Graphics2D graphics, final String contentType) {
        final ImageRenderer renderer = (graphics != null) ? ((ImageRenderer)graphics.getRenderingHint(Drawable.IMAGE_RENDERER)) : null;
        if (renderer != null && renderer.canRender(contentType)) {
            return renderer;
        }
        final BitmapImageRenderer bir = new BitmapImageRenderer();
        if (bir.canRender(contentType)) {
            return bir;
        }
        final ClassLoader cl = ImageRenderer.class.getClassLoader();
        for (final String kr : DrawPictureShape.KNOWN_RENDERER) {
            Label_0170: {
                ImageRenderer ir;
                try {
                    ir = (ImageRenderer)cl.loadClass(kr).newInstance();
                }
                catch (final ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                    DrawPictureShape.LOG.log(3, "Known image renderer '" + kr + " not found/loaded - include poi-scratchpad jar!", e);
                    break Label_0170;
                }
                if (ir.canRender(contentType)) {
                    return ir;
                }
            }
        }
        DrawPictureShape.LOG.log(5, "No suiteable image renderer found for content-type '" + contentType + "' - include poi-scratchpad jar!");
        return bir;
    }
    
    @Override
    protected Paint getFillPaint(final Graphics2D graphics) {
        return null;
    }
    
    @Override
    protected PictureShape<?, ?> getShape() {
        return (PictureShape)this.shape;
    }
    
    public void resize() {
        final PictureShape<?, ?> ps = this.getShape();
        final Dimension dim = ps.getPictureData().getImageDimension();
        final Rectangle2D origRect = ps.getAnchor();
        final double x = origRect.getX();
        final double y = origRect.getY();
        final double w = dim.getWidth();
        final double h = dim.getHeight();
        ps.setAnchor(new Rectangle2D.Double(x, y, w, h));
    }
    
    public void resize(final Rectangle2D target) {
        this.resize(target, RectAlign.CENTER);
    }
    
    public void resize(final Rectangle2D target, final RectAlign align) {
        final PictureShape<?, ?> ps = this.getShape();
        final Dimension dim = ps.getPictureData().getImageDimension();
        if (dim.width <= 0 || dim.height <= 0) {
            ps.setAnchor(target);
            return;
        }
        double w = target.getWidth();
        double h = target.getHeight();
        final double sx = w / dim.width;
        final double sy = h / dim.height;
        double dx = 0.0;
        double dy = 0.0;
        if (sx > sy) {
            w = sy * dim.width;
            dx = target.getWidth() - w;
        }
        else {
            if (sy <= sx) {
                ps.setAnchor(target);
                return;
            }
            h = sx * dim.height;
            dy = target.getHeight() - h;
        }
        double x = target.getX();
        double y = target.getY();
        switch (align) {
            case TOP: {
                x += dx / 2.0;
                break;
            }
            case TOP_RIGHT: {
                x += dx;
                break;
            }
            case RIGHT: {
                x += dx;
                y += dy / 2.0;
                break;
            }
            case BOTTOM_RIGHT: {
                x += dx;
                y += dy;
                break;
            }
            case BOTTOM: {
                x += dx / 2.0;
                y += dy;
                break;
            }
            case BOTTOM_LEFT: {
                y += dy;
                break;
            }
            case LEFT: {
                y += dy / 2.0;
                break;
            }
            case TOP_LEFT: {
                break;
            }
            default: {
                x += dx / 2.0;
                y += dy / 2.0;
                break;
            }
        }
        ps.setAnchor(new Rectangle2D.Double(x, y, w, h));
    }
    
    static {
        LOG = POILogFactory.getLogger(DrawPictureShape.class);
        KNOWN_RENDERER = new String[] { "org.apache.poi.hwmf.draw.HwmfImageRenderer", "org.apache.poi.hemf.draw.HemfImageRenderer", "org.apache.poi.xslf.draw.SVGImageRenderer" };
    }
}
