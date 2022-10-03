package org.apache.poi.sl.draw;

import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import org.apache.poi.sl.usermodel.PlaceableShape;
import java.text.AttributedString;
import java.awt.font.TextLayout;
import org.apache.poi.sl.usermodel.TextParagraph;
import java.lang.annotation.Annotation;
import org.apache.poi.sl.usermodel.Sheet;
import org.apache.poi.sl.usermodel.MasterSheet;
import org.apache.poi.sl.usermodel.Slide;
import org.apache.poi.sl.usermodel.ConnectorShape;
import org.apache.poi.sl.usermodel.Background;
import org.apache.poi.sl.usermodel.GraphicalFrame;
import org.apache.poi.sl.usermodel.PictureShape;
import org.apache.poi.sl.usermodel.GroupShape;
import org.apache.poi.sl.usermodel.TableShape;
import org.apache.poi.sl.usermodel.TextShape;
import org.apache.poi.sl.usermodel.FreeformShape;
import org.apache.poi.sl.usermodel.TextBox;
import org.apache.poi.sl.usermodel.Shape;
import java.awt.RenderingHints;
import java.awt.Graphics2D;

public class DrawFactory
{
    private static final ThreadLocal<DrawFactory> defaultFactory;
    
    public static void setDefaultFactory(final DrawFactory factory) {
        if (factory == null) {
            DrawFactory.defaultFactory.remove();
        }
        else {
            DrawFactory.defaultFactory.set(factory);
        }
    }
    
    public static DrawFactory getInstance(final Graphics2D graphics) {
        DrawFactory factory = null;
        boolean isHint = false;
        if (graphics != null) {
            factory = (DrawFactory)graphics.getRenderingHint(Drawable.DRAW_FACTORY);
            isHint = (factory != null);
        }
        if (factory == null) {
            factory = DrawFactory.defaultFactory.get();
        }
        if (factory == null) {
            factory = new DrawFactory();
        }
        if (graphics != null && !isHint) {
            graphics.setRenderingHint(Drawable.DRAW_FACTORY, factory);
        }
        return factory;
    }
    
    public Drawable getDrawable(final Shape<?, ?> shape) {
        if (shape instanceof TextBox) {
            return this.getDrawable((TextBox)shape);
        }
        if (shape instanceof FreeformShape) {
            return this.getDrawable((FreeformShape)shape);
        }
        if (shape instanceof TextShape) {
            return this.getDrawable((TextShape)shape);
        }
        if (shape instanceof TableShape) {
            return this.getDrawable((TableShape)shape);
        }
        if (shape instanceof GroupShape) {
            return this.getDrawable((GroupShape)shape);
        }
        if (shape instanceof PictureShape) {
            return this.getDrawable((PictureShape)shape);
        }
        if (shape instanceof GraphicalFrame) {
            return this.getDrawable((GraphicalFrame)shape);
        }
        if (shape instanceof Background) {
            return this.getDrawable((Background)shape);
        }
        if (shape instanceof ConnectorShape) {
            return this.getDrawable((ConnectorShape)shape);
        }
        if (shape instanceof Slide) {
            return this.getDrawable((Slide)shape);
        }
        if (shape instanceof MasterSheet) {
            return this.getDrawable((MasterSheet)shape);
        }
        if (shape instanceof Sheet) {
            return this.getDrawable((Sheet)shape);
        }
        if (shape.getClass().isAnnotationPresent(DrawNotImplemented.class)) {
            return new DrawNothing(shape);
        }
        throw new IllegalArgumentException("Unsupported shape type: " + shape.getClass());
    }
    
    public DrawSlide getDrawable(final Slide<?, ?> sheet) {
        return new DrawSlide(sheet);
    }
    
    public DrawSheet getDrawable(final Sheet<?, ?> sheet) {
        return new DrawSheet(sheet);
    }
    
    public DrawMasterSheet getDrawable(final MasterSheet<?, ?> sheet) {
        return new DrawMasterSheet(sheet);
    }
    
    public DrawTextBox getDrawable(final TextBox<?, ?> shape) {
        return new DrawTextBox(shape);
    }
    
    public DrawFreeformShape getDrawable(final FreeformShape<?, ?> shape) {
        return new DrawFreeformShape(shape);
    }
    
    public DrawConnectorShape getDrawable(final ConnectorShape<?, ?> shape) {
        return new DrawConnectorShape(shape);
    }
    
    public DrawTableShape getDrawable(final TableShape<?, ?> shape) {
        return new DrawTableShape(shape);
    }
    
    public DrawTextShape getDrawable(final TextShape<?, ?> shape) {
        return new DrawTextShape(shape);
    }
    
    public DrawGroupShape getDrawable(final GroupShape<?, ?> shape) {
        return new DrawGroupShape(shape);
    }
    
    public DrawPictureShape getDrawable(final PictureShape<?, ?> shape) {
        return new DrawPictureShape(shape);
    }
    
    public DrawGraphicalFrame getDrawable(final GraphicalFrame<?, ?> shape) {
        return new DrawGraphicalFrame(shape);
    }
    
    public DrawTextParagraph getDrawable(final TextParagraph<?, ?, ?> paragraph) {
        return new DrawTextParagraph(paragraph);
    }
    
    public DrawBackground getDrawable(final Background<?, ?> shape) {
        return new DrawBackground(shape);
    }
    
    public DrawTextFragment getTextFragment(final TextLayout layout, final AttributedString str) {
        return new DrawTextFragment(layout, str);
    }
    
    public DrawPaint getPaint(final PlaceableShape<?, ?> shape) {
        return new DrawPaint(shape);
    }
    
    public void drawShape(final Graphics2D graphics, final Shape<?, ?> shape, final Rectangle2D bounds) {
        final Rectangle2D shapeBounds = shape.getAnchor();
        if (shapeBounds.isEmpty() || (bounds != null && bounds.isEmpty())) {
            return;
        }
        final AffineTransform txg = (AffineTransform)graphics.getRenderingHint(Drawable.GROUP_TRANSFORM);
        final AffineTransform tx = new AffineTransform();
        try {
            if (bounds != null) {
                final double scaleX = bounds.getWidth() / shapeBounds.getWidth();
                final double scaleY = bounds.getHeight() / shapeBounds.getHeight();
                tx.translate(bounds.getCenterX(), bounds.getCenterY());
                tx.scale(scaleX, scaleY);
                tx.translate(-shapeBounds.getCenterX(), -shapeBounds.getCenterY());
            }
            graphics.setRenderingHint(Drawable.GROUP_TRANSFORM, tx);
            final Drawable d = this.getDrawable(shape);
            d.applyTransform(graphics);
            d.draw(graphics);
        }
        finally {
            graphics.setRenderingHint(Drawable.GROUP_TRANSFORM, txg);
        }
    }
    
    public DrawFontManager getFontManager(final Graphics2D graphics) {
        final DrawFontManager fontHandler = (DrawFontManager)graphics.getRenderingHint(Drawable.FONT_HANDLER);
        return (fontHandler != null) ? fontHandler : new DrawFontManagerDefault();
    }
    
    static {
        defaultFactory = new ThreadLocal<DrawFactory>();
    }
}
