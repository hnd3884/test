package org.apache.poi.sl.draw;

import org.apache.poi.util.POILogFactory;
import org.apache.poi.sl.usermodel.StrokeStyle;
import java.awt.image.renderable.RenderableImage;
import java.awt.image.RenderedImage;
import org.apache.poi.util.SuppressForbidden;
import java.awt.Toolkit;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.GraphicsConfiguration;
import java.awt.font.GlyphVector;
import java.awt.Composite;
import java.awt.image.BufferedImageOp;
import java.awt.image.BufferedImage;
import java.awt.font.FontRenderContext;
import java.text.AttributedCharacterIterator;
import java.awt.Polygon;
import java.awt.geom.Line2D;
import java.awt.image.ImageObserver;
import java.awt.Image;
import java.awt.geom.GeneralPath;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import org.apache.poi.util.NotImplemented;
import org.apache.poi.sl.usermodel.TextBox;
import java.awt.geom.Rectangle2D;
import java.awt.Rectangle;
import java.awt.font.TextLayout;
import org.apache.poi.sl.usermodel.VerticalAlignment;
import org.apache.poi.sl.usermodel.Insets2D;
import org.apache.poi.sl.usermodel.PaintStyle;
import org.apache.poi.sl.usermodel.TextParagraph;
import org.apache.poi.sl.usermodel.TextRun;
import org.apache.poi.sl.usermodel.FreeformShape;
import org.apache.poi.sl.usermodel.SimpleShape;
import java.awt.geom.Path2D;
import java.awt.Shape;
import java.util.Map;
import java.awt.BasicStroke;
import java.awt.RenderingHints;
import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import org.apache.poi.sl.usermodel.GroupShape;
import org.apache.poi.util.POILogger;
import java.awt.Graphics2D;

public class SLGraphics extends Graphics2D implements Cloneable
{
    private static final POILogger LOG;
    private GroupShape<?, ?> _group;
    private AffineTransform _transform;
    private Stroke _stroke;
    private Paint _paint;
    private Font _font;
    private Color _foreground;
    private Color _background;
    private RenderingHints _hints;
    
    public SLGraphics(final GroupShape<?, ?> group) {
        this._group = group;
        this._transform = new AffineTransform();
        this._stroke = new BasicStroke();
        this._paint = Color.black;
        this._font = new Font("Arial", 0, 12);
        this._background = Color.black;
        this._foreground = Color.white;
        this._hints = new RenderingHints(null);
    }
    
    public GroupShape<?, ?> getShapeGroup() {
        return this._group;
    }
    
    @Override
    public Font getFont() {
        return this._font;
    }
    
    @Override
    public void setFont(final Font font) {
        this._font = font;
    }
    
    @Override
    public Color getColor() {
        return this._foreground;
    }
    
    @Override
    public void setColor(final Color c) {
        this.setPaint(c);
    }
    
    @Override
    public Stroke getStroke() {
        return this._stroke;
    }
    
    @Override
    public void setStroke(final Stroke s) {
        this._stroke = s;
    }
    
    @Override
    public Paint getPaint() {
        return this._paint;
    }
    
    @Override
    public void setPaint(final Paint paint) {
        if (paint == null) {
            return;
        }
        this._paint = paint;
        if (paint instanceof Color) {
            this._foreground = (Color)paint;
        }
    }
    
    @Override
    public AffineTransform getTransform() {
        return new AffineTransform(this._transform);
    }
    
    @Override
    public void setTransform(final AffineTransform Tx) {
        this._transform = new AffineTransform(Tx);
    }
    
    @Override
    public void draw(final Shape shape) {
        final Path2D.Double path = new Path2D.Double(this._transform.createTransformedShape(shape));
        final FreeformShape<?, ?> p = this._group.createFreeform();
        p.setPath(path);
        p.setFillColor(null);
        this.applyStroke(p);
        if (this._paint instanceof Color) {
            p.setStrokeStyle(this._paint);
        }
    }
    
    @Override
    public void drawString(final String s, final float x, float y) {
        final TextBox<?, ?> txt = this._group.createTextBox();
        final TextRun rt = txt.getTextParagraphs().get(0).getTextRuns().get(0);
        rt.setFontSize((double)this._font.getSize());
        rt.setFontFamily(this._font.getFamily());
        if (this.getColor() != null) {
            rt.setFontColor(DrawPaint.createSolidPaint(this.getColor()));
        }
        if (this._font.isBold()) {
            rt.setBold(true);
        }
        if (this._font.isItalic()) {
            rt.setItalic(true);
        }
        txt.setText(s);
        txt.setInsets(new Insets2D(0.0, 0.0, 0.0, 0.0));
        txt.setWordWrap(false);
        txt.setHorizontalCentered(false);
        txt.setVerticalAlignment(VerticalAlignment.MIDDLE);
        final TextLayout layout = new TextLayout(s, this._font, this.getFontRenderContext());
        final float ascent = layout.getAscent();
        final float width = (float)Math.floor(layout.getAdvance());
        final float height = ascent * 2.0f;
        y -= height / 2.0f + ascent / 2.0f;
        txt.setAnchor(new Rectangle((int)x, (int)y, (int)width, (int)height));
    }
    
    @Override
    public void fill(final Shape shape) {
        final Path2D.Double path = new Path2D.Double(this._transform.createTransformedShape(shape));
        final FreeformShape<?, ?> p = this._group.createFreeform();
        p.setPath(path);
        this.applyPaint(p);
        p.setStrokeStyle(new Object[0]);
    }
    
    @Override
    public void translate(final int x, final int y) {
        this._transform.translate(x, y);
    }
    
    @NotImplemented
    @Override
    public void clip(final Shape s) {
        if (SLGraphics.LOG.check(5)) {
            SLGraphics.LOG.log(5, "Not implemented");
        }
    }
    
    @NotImplemented
    @Override
    public Shape getClip() {
        if (SLGraphics.LOG.check(5)) {
            SLGraphics.LOG.log(5, "Not implemented");
        }
        return null;
    }
    
    @Override
    public void scale(final double sx, final double sy) {
        this._transform.scale(sx, sy);
    }
    
    @Override
    public void drawRoundRect(final int x, final int y, final int width, final int height, final int arcWidth, final int arcHeight) {
        final RoundRectangle2D rect = new RoundRectangle2D.Double(x, y, width, height, arcWidth, arcHeight);
        this.draw(rect);
    }
    
    @Override
    public void drawString(final String str, final int x, final int y) {
        this.drawString(str, (float)x, (float)y);
    }
    
    @Override
    public void fillOval(final int x, final int y, final int width, final int height) {
        final Ellipse2D oval = new Ellipse2D.Double(x, y, width, height);
        this.fill(oval);
    }
    
    @Override
    public void fillRoundRect(final int x, final int y, final int width, final int height, final int arcWidth, final int arcHeight) {
        final RoundRectangle2D rect = new RoundRectangle2D.Double(x, y, width, height, arcWidth, arcHeight);
        this.fill(rect);
    }
    
    @Override
    public void fillArc(final int x, final int y, final int width, final int height, final int startAngle, final int arcAngle) {
        final Arc2D arc = new Arc2D.Double(x, y, width, height, startAngle, arcAngle, 2);
        this.fill(arc);
    }
    
    @Override
    public void drawArc(final int x, final int y, final int width, final int height, final int startAngle, final int arcAngle) {
        final Arc2D arc = new Arc2D.Double(x, y, width, height, startAngle, arcAngle, 0);
        this.draw(arc);
    }
    
    @Override
    public void drawPolyline(final int[] xPoints, final int[] yPoints, final int nPoints) {
        if (nPoints > 0) {
            final GeneralPath path = new GeneralPath();
            path.moveTo((float)xPoints[0], (float)yPoints[0]);
            for (int i = 1; i < nPoints; ++i) {
                path.lineTo((float)xPoints[i], (float)yPoints[i]);
            }
            this.draw(path);
        }
    }
    
    @Override
    public void drawOval(final int x, final int y, final int width, final int height) {
        final Ellipse2D oval = new Ellipse2D.Double(x, y, width, height);
        this.draw(oval);
    }
    
    @NotImplemented
    @Override
    public boolean drawImage(final Image img, final int x, final int y, final Color bgcolor, final ImageObserver observer) {
        if (SLGraphics.LOG.check(5)) {
            SLGraphics.LOG.log(5, "Not implemented");
        }
        return false;
    }
    
    @NotImplemented
    @Override
    public boolean drawImage(final Image img, final int x, final int y, final int width, final int height, final Color bgcolor, final ImageObserver observer) {
        if (SLGraphics.LOG.check(5)) {
            SLGraphics.LOG.log(5, "Not implemented");
        }
        return false;
    }
    
    @NotImplemented
    @Override
    public boolean drawImage(final Image img, final int dx1, final int dy1, final int dx2, final int dy2, final int sx1, final int sy1, final int sx2, final int sy2, final ImageObserver observer) {
        if (SLGraphics.LOG.check(5)) {
            SLGraphics.LOG.log(5, "Not implemented");
        }
        return false;
    }
    
    @NotImplemented
    @Override
    public boolean drawImage(final Image img, final int dx1, final int dy1, final int dx2, final int dy2, final int sx1, final int sy1, final int sx2, final int sy2, final Color bgcolor, final ImageObserver observer) {
        if (SLGraphics.LOG.check(5)) {
            SLGraphics.LOG.log(5, "Not implemented");
        }
        return false;
    }
    
    @NotImplemented
    @Override
    public boolean drawImage(final Image img, final int x, final int y, final ImageObserver observer) {
        if (SLGraphics.LOG.check(5)) {
            SLGraphics.LOG.log(5, "Not implemented");
        }
        return false;
    }
    
    @Override
    public void dispose() {
    }
    
    @Override
    public void drawLine(final int x1, final int y1, final int x2, final int y2) {
        final Line2D line = new Line2D.Double(x1, y1, x2, y2);
        this.draw(line);
    }
    
    @Override
    public void fillPolygon(final int[] xPoints, final int[] yPoints, final int nPoints) {
        final Polygon polygon = new Polygon(xPoints, yPoints, nPoints);
        this.fill(polygon);
    }
    
    @Override
    public void fillRect(final int x, final int y, final int width, final int height) {
        final Rectangle rect = new Rectangle(x, y, width, height);
        this.fill(rect);
    }
    
    @Override
    public void drawRect(final int x, final int y, final int width, final int height) {
        final Rectangle rect = new Rectangle(x, y, width, height);
        this.draw(rect);
    }
    
    @Override
    public void drawPolygon(final int[] xPoints, final int[] yPoints, final int nPoints) {
        final Polygon polygon = new Polygon(xPoints, yPoints, nPoints);
        this.draw(polygon);
    }
    
    @Override
    public void clipRect(final int x, final int y, final int width, final int height) {
        this.clip(new Rectangle(x, y, width, height));
    }
    
    @NotImplemented
    @Override
    public void setClip(final Shape clip) {
        if (SLGraphics.LOG.check(5)) {
            SLGraphics.LOG.log(5, "Not implemented");
        }
    }
    
    @Override
    public Rectangle getClipBounds() {
        if (SLGraphics.LOG.check(5)) {
            SLGraphics.LOG.log(5, "Not implemented");
        }
        return null;
    }
    
    @Override
    public void drawString(final AttributedCharacterIterator iterator, final int x, final int y) {
        this.drawString(iterator, (float)x, (float)y);
    }
    
    @Override
    public void clearRect(final int x, final int y, final int width, final int height) {
        final Paint paint = this.getPaint();
        this.setColor(this.getBackground());
        this.fillRect(x, y, width, height);
        this.setPaint(paint);
    }
    
    @Override
    public void copyArea(final int x, final int y, final int width, final int height, final int dx, final int dy) {
    }
    
    @Override
    public void setClip(final int x, final int y, final int width, final int height) {
        this.setClip(new Rectangle(x, y, width, height));
    }
    
    @Override
    public void rotate(final double theta) {
        this._transform.rotate(theta);
    }
    
    @Override
    public void rotate(final double theta, final double x, final double y) {
        this._transform.rotate(theta, x, y);
    }
    
    @Override
    public void shear(final double shx, final double shy) {
        this._transform.shear(shx, shy);
    }
    
    @Override
    public FontRenderContext getFontRenderContext() {
        final boolean isAntiAliased = RenderingHints.VALUE_TEXT_ANTIALIAS_ON.equals(this.getRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING));
        final boolean usesFractionalMetrics = RenderingHints.VALUE_FRACTIONALMETRICS_ON.equals(this.getRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS));
        return new FontRenderContext(new AffineTransform(), isAntiAliased, usesFractionalMetrics);
    }
    
    @Override
    public void transform(final AffineTransform Tx) {
        this._transform.concatenate(Tx);
    }
    
    @Override
    public void drawImage(BufferedImage img, final BufferedImageOp op, final int x, final int y) {
        img = op.filter(img, null);
        this.drawImage(img, x, y, null);
    }
    
    @Override
    public void setBackground(final Color color) {
        if (color == null) {
            return;
        }
        this._background = color;
    }
    
    @Override
    public Color getBackground() {
        return this._background;
    }
    
    @NotImplemented
    @Override
    public void setComposite(final Composite comp) {
        if (SLGraphics.LOG.check(5)) {
            SLGraphics.LOG.log(5, "Not implemented");
        }
    }
    
    @NotImplemented
    @Override
    public Composite getComposite() {
        if (SLGraphics.LOG.check(5)) {
            SLGraphics.LOG.log(5, "Not implemented");
        }
        return null;
    }
    
    @Override
    public Object getRenderingHint(final RenderingHints.Key hintKey) {
        return this._hints.get(hintKey);
    }
    
    @Override
    public void setRenderingHint(final RenderingHints.Key hintKey, final Object hintValue) {
        this._hints.put(hintKey, hintValue);
    }
    
    @Override
    public void drawGlyphVector(final GlyphVector g, final float x, final float y) {
        final Shape glyphOutline = g.getOutline(x, y);
        this.fill(glyphOutline);
    }
    
    @Override
    public GraphicsConfiguration getDeviceConfiguration() {
        return GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
    }
    
    @Override
    public void addRenderingHints(final Map<?, ?> hints) {
        this._hints.putAll(hints);
    }
    
    @Override
    public void translate(final double tx, final double ty) {
        this._transform.translate(tx, ty);
    }
    
    @NotImplemented
    @Override
    public void drawString(final AttributedCharacterIterator iterator, final float x, final float y) {
        if (SLGraphics.LOG.check(5)) {
            SLGraphics.LOG.log(5, "Not implemented");
        }
    }
    
    @Override
    public boolean hit(final Rectangle rect, Shape s, final boolean onStroke) {
        if (onStroke) {
            s = this.getStroke().createStrokedShape(s);
        }
        s = this.getTransform().createTransformedShape(s);
        return s.intersects(rect);
    }
    
    @Override
    public RenderingHints getRenderingHints() {
        return this._hints;
    }
    
    @Override
    public void setRenderingHints(final Map<?, ?> hints) {
        (this._hints = new RenderingHints(null)).putAll(hints);
    }
    
    @NotImplemented
    @Override
    public boolean drawImage(final Image img, final AffineTransform xform, final ImageObserver obs) {
        if (SLGraphics.LOG.check(5)) {
            SLGraphics.LOG.log(5, "Not implemented");
        }
        return false;
    }
    
    @NotImplemented
    @Override
    public boolean drawImage(final Image img, final int x, final int y, final int width, final int height, final ImageObserver observer) {
        if (SLGraphics.LOG.check(5)) {
            SLGraphics.LOG.log(5, "Not implemented");
        }
        return false;
    }
    
    @Override
    public Graphics create() {
        try {
            return (Graphics)this.clone();
        }
        catch (final CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
    
    @SuppressForbidden
    @Override
    public FontMetrics getFontMetrics(final Font f) {
        return Toolkit.getDefaultToolkit().getFontMetrics(f);
    }
    
    @NotImplemented
    @Override
    public void setXORMode(final Color c1) {
        if (SLGraphics.LOG.check(5)) {
            SLGraphics.LOG.log(5, "Not implemented");
        }
    }
    
    @NotImplemented
    @Override
    public void setPaintMode() {
        if (SLGraphics.LOG.check(5)) {
            SLGraphics.LOG.log(5, "Not implemented");
        }
    }
    
    @NotImplemented
    @Override
    public void drawRenderedImage(final RenderedImage img, final AffineTransform xform) {
        if (SLGraphics.LOG.check(5)) {
            SLGraphics.LOG.log(5, "Not implemented");
        }
    }
    
    @NotImplemented
    @Override
    public void drawRenderableImage(final RenderableImage img, final AffineTransform xform) {
        if (SLGraphics.LOG.check(5)) {
            SLGraphics.LOG.log(5, "Not implemented");
        }
    }
    
    protected void applyStroke(final SimpleShape<?, ?> shape) {
        if (this._stroke instanceof BasicStroke) {
            final BasicStroke bs = (BasicStroke)this._stroke;
            shape.setStrokeStyle(bs.getLineWidth());
            final float[] dash = bs.getDashArray();
            if (dash != null) {
                shape.setStrokeStyle(StrokeStyle.LineDash.DASH);
            }
        }
    }
    
    protected void applyPaint(final SimpleShape<?, ?> shape) {
        if (this._paint instanceof Color) {
            shape.setFillColor((Color)this._paint);
        }
    }
    
    static {
        LOG = POILogFactory.getLogger(SLGraphics.class);
    }
}
