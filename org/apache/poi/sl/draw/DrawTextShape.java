package org.apache.poi.sl.draw;

import org.apache.poi.sl.usermodel.Shape;
import java.util.Map;
import java.awt.image.BufferedImage;
import org.apache.poi.sl.usermodel.TextRun;
import java.util.Iterator;
import org.apache.poi.sl.usermodel.TextParagraph;
import org.apache.poi.sl.usermodel.ShapeContainer;
import java.awt.geom.AffineTransform;
import org.apache.poi.sl.usermodel.Insets2D;
import java.awt.geom.Rectangle2D;
import org.apache.poi.sl.usermodel.PlaceableShape;
import java.awt.Graphics2D;
import org.apache.poi.sl.usermodel.SimpleShape;
import org.apache.poi.sl.usermodel.TextShape;

public class DrawTextShape extends DrawSimpleShape
{
    public DrawTextShape(final TextShape<?, ?> shape) {
        super(shape);
    }
    
    @Override
    public void drawContent(final Graphics2D graphics) {
        final TextShape<?, ?> s = this.getShape();
        final Rectangle2D anchor = DrawShape.getAnchor(graphics, s);
        if (anchor == null) {
            return;
        }
        final Insets2D insets = s.getInsets();
        final double x = anchor.getX() + insets.left;
        double y = anchor.getY();
        final AffineTransform tx = graphics.getTransform();
        boolean vertFlip = s.getFlipVertical();
        boolean horzFlip = s.getFlipHorizontal();
        PlaceableShape<?, ?> ps;
        for (ShapeContainer<?, ?> sc = s.getParent(); sc instanceof PlaceableShape; sc = ps.getParent()) {
            ps = (PlaceableShape)sc;
            vertFlip ^= ps.getFlipVertical();
            horzFlip ^= ps.getFlipHorizontal();
        }
        if (horzFlip ^ vertFlip) {
            final double ax = anchor.getX();
            final double ay = anchor.getY();
            graphics.translate(ax + anchor.getWidth(), ay);
            graphics.scale(-1.0, 1.0);
            graphics.translate(-ax, -ay);
        }
        final Double textRot = s.getTextRotation();
        if (textRot != null && textRot != 0.0) {
            final double cx = anchor.getCenterX();
            final double cy = anchor.getCenterY();
            graphics.translate(cx, cy);
            graphics.rotate(Math.toRadians(textRot));
            graphics.translate(-cx, -cy);
        }
        switch (s.getVerticalAlignment()) {
            default: {
                y += insets.top;
                break;
            }
            case BOTTOM: {
                final double textHeight = this.getTextHeight(graphics);
                y += anchor.getHeight() - textHeight - insets.bottom;
                break;
            }
            case MIDDLE: {
                final double textHeight = this.getTextHeight(graphics);
                final double delta = anchor.getHeight() - textHeight - insets.top - insets.bottom;
                y += insets.top + delta / 2.0;
                break;
            }
        }
        final TextShape.TextDirection textDir = s.getTextDirection();
        if (textDir == TextShape.TextDirection.VERTICAL || textDir == TextShape.TextDirection.VERTICAL_270) {
            final double deg = (textDir == TextShape.TextDirection.VERTICAL) ? 90.0 : 270.0;
            final double cx2 = anchor.getCenterX();
            final double cy2 = anchor.getCenterY();
            graphics.translate(cx2, cy2);
            graphics.rotate(Math.toRadians(deg));
            graphics.translate(-cx2, -cy2);
            final double w = anchor.getWidth();
            final double h = anchor.getHeight();
            final double dx = (w - h) / 2.0;
            graphics.translate(dx, -dx);
        }
        this.drawParagraphs(graphics, x, y);
        graphics.setTransform(tx);
    }
    
    public double drawParagraphs(final Graphics2D graphics, final double x, double y) {
        final DrawFactory fact = DrawFactory.getInstance(graphics);
        final double y2 = y;
        final Iterator<? extends TextParagraph<?, ?, ? extends TextRun>> paragraphs = this.getShape().iterator();
        boolean isFirstLine = true;
        int autoNbrIdx = 0;
        while (paragraphs.hasNext()) {
            final TextParagraph<?, ?, ? extends TextRun> p = (TextParagraph<?, ?, ? extends TextRun>)paragraphs.next();
            final DrawTextParagraph dp = fact.getDrawable(p);
            final TextParagraph.BulletStyle bs = p.getBulletStyle();
            if (bs == null || bs.getAutoNumberingScheme() == null) {
                autoNbrIdx = -1;
            }
            else {
                Integer startAt = bs.getAutoNumberingStartAt();
                if (startAt == null) {
                    startAt = 1;
                }
                if (startAt > autoNbrIdx) {
                    autoNbrIdx = startAt;
                }
            }
            dp.setAutoNumberingIdx(autoNbrIdx);
            dp.breakText(graphics);
            if (isFirstLine) {
                y += dp.getFirstLineLeading();
            }
            else {
                Double spaceBefore = p.getSpaceBefore();
                if (spaceBefore == null) {
                    spaceBefore = 0.0;
                }
                if (spaceBefore > 0.0) {
                    y += spaceBefore * 0.01 * dp.getFirstLineHeight();
                }
                else {
                    y += -spaceBefore;
                }
            }
            isFirstLine = false;
            dp.setPosition(x, y);
            dp.draw(graphics);
            y += dp.getY();
            if (paragraphs.hasNext()) {
                Double spaceAfter = p.getSpaceAfter();
                if (spaceAfter == null) {
                    spaceAfter = 0.0;
                }
                if (spaceAfter > 0.0) {
                    y += spaceAfter * 0.01 * dp.getLastLineHeight();
                }
                else {
                    y += -spaceAfter;
                }
            }
            ++autoNbrIdx;
        }
        return y - y2;
    }
    
    public double getTextHeight() {
        return this.getTextHeight(null);
    }
    
    public double getTextHeight(final Graphics2D oldGraphics) {
        final BufferedImage img = new BufferedImage(1, 1, 1);
        final Graphics2D graphics = img.createGraphics();
        if (oldGraphics != null) {
            graphics.addRenderingHints(oldGraphics.getRenderingHints());
            graphics.setTransform(oldGraphics.getTransform());
        }
        return this.drawParagraphs(graphics, 0.0, 0.0);
    }
    
    @Override
    protected TextShape<?, ? extends TextParagraph<?, ?, ? extends TextRun>> getShape() {
        return (TextShape)this.shape;
    }
}
