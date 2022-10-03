package com.lowagie.text.pdf;

import java.awt.Component;
import java.awt.geom.Point2D;
import java.awt.TexturePaint;
import java.awt.GradientPaint;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.ImageWriteParam;
import javax.imageio.metadata.IIOMetadata;
import java.util.List;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import java.io.ByteArrayOutputStream;
import java.awt.geom.PathIterator;
import java.awt.Polygon;
import com.lowagie.text.pdf.internal.PolylineShape;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.font.FontRenderContext;
import java.util.Arrays;
import java.awt.AlphaComposite;
import java.awt.GraphicsConfiguration;
import java.awt.Rectangle;
import java.awt.font.GlyphVector;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.geom.Line2D;
import java.util.Locale;
import java.util.Iterator;
import java.util.Set;
import java.awt.font.TextAttribute;
import java.text.AttributedCharacterIterator;
import java.awt.image.renderable.RenderableImage;
import java.awt.image.WritableRaster;
import java.awt.image.ColorModel;
import java.util.Hashtable;
import java.awt.image.RenderedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.Image;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Map;
import java.awt.Composite;
import java.awt.MediaTracker;
import java.util.ArrayList;
import java.util.HashMap;
import java.awt.Stroke;
import java.awt.RenderingHints;
import java.awt.geom.Area;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Font;
import java.awt.geom.AffineTransform;
import java.awt.BasicStroke;
import java.awt.Graphics2D;

public class PdfGraphics2D extends Graphics2D
{
    private static final int FILL = 1;
    private static final int STROKE = 2;
    private static final int CLIP = 3;
    private BasicStroke strokeOne;
    private static final AffineTransform IDENTITY;
    private Font font;
    private BaseFont baseFont;
    private float fontSize;
    private AffineTransform transform;
    private Paint paint;
    private Color background;
    private float width;
    private float height;
    private Area clip;
    private RenderingHints rhints;
    private Stroke stroke;
    private Stroke originalStroke;
    private PdfContentByte cb;
    private HashMap baseFonts;
    private boolean disposeCalled;
    private FontMapper fontMapper;
    private ArrayList kids;
    private boolean kid;
    private Graphics2D dg2;
    private boolean onlyShapes;
    private Stroke oldStroke;
    private Paint paintFill;
    private Paint paintStroke;
    private MediaTracker mediaTracker;
    protected boolean underline;
    protected PdfGState[] fillGState;
    protected PdfGState[] strokeGState;
    protected int currentFillGState;
    protected int currentStrokeGState;
    public static final int AFM_DIVISOR = 1000;
    private boolean convertImagesToJPEG;
    private float jpegQuality;
    private float alpha;
    private Composite composite;
    private Paint realPaint;
    
    private PdfGraphics2D() {
        this.strokeOne = new BasicStroke(1.0f);
        this.rhints = new RenderingHints(null);
        this.disposeCalled = false;
        this.kid = false;
        this.dg2 = new BufferedImage(2, 2, 1).createGraphics();
        this.onlyShapes = false;
        this.fillGState = new PdfGState[256];
        this.strokeGState = new PdfGState[256];
        this.currentFillGState = 255;
        this.currentStrokeGState = 255;
        this.convertImagesToJPEG = false;
        this.jpegQuality = 0.95f;
        this.dg2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        this.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        this.setRenderingHint(HyperLinkKey.KEY_INSTANCE, HyperLinkKey.VALUE_HYPERLINKKEY_OFF);
    }
    
    public PdfGraphics2D(final PdfContentByte cb, final float width, final float height) {
        this(cb, width, height, null, false, false, 0.0f);
    }
    
    public PdfGraphics2D(final PdfContentByte cb, final float width, final float height, final FontMapper fontMapper, final boolean onlyShapes, final boolean convertImagesToJPEG, final float quality) {
        this.strokeOne = new BasicStroke(1.0f);
        this.rhints = new RenderingHints(null);
        this.disposeCalled = false;
        this.kid = false;
        this.dg2 = new BufferedImage(2, 2, 1).createGraphics();
        this.onlyShapes = false;
        this.fillGState = new PdfGState[256];
        this.strokeGState = new PdfGState[256];
        this.currentFillGState = 255;
        this.currentStrokeGState = 255;
        this.convertImagesToJPEG = false;
        this.jpegQuality = 0.95f;
        this.dg2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        this.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        this.setRenderingHint(HyperLinkKey.KEY_INSTANCE, HyperLinkKey.VALUE_HYPERLINKKEY_OFF);
        this.convertImagesToJPEG = convertImagesToJPEG;
        this.jpegQuality = quality;
        this.onlyShapes = onlyShapes;
        this.transform = new AffineTransform();
        this.baseFonts = new HashMap();
        if (!onlyShapes) {
            this.fontMapper = fontMapper;
            if (this.fontMapper == null) {
                this.fontMapper = new DefaultFontMapper();
            }
        }
        this.paint = Color.black;
        this.background = Color.white;
        this.setFont(new Font("sanserif", 0, 12));
        (this.cb = cb).saveState();
        this.width = width;
        this.height = height;
        this.clip(this.clip = new Area(new Rectangle2D.Float(0.0f, 0.0f, width, height)));
        final BasicStroke strokeOne = this.strokeOne;
        this.oldStroke = strokeOne;
        this.stroke = strokeOne;
        this.originalStroke = strokeOne;
        this.setStrokeDiff(this.stroke, null);
        cb.saveState();
    }
    
    @Override
    public void draw(final Shape s) {
        this.followPath(s, 2);
    }
    
    @Override
    public boolean drawImage(final Image img, final AffineTransform xform, final ImageObserver obs) {
        return this.drawImage(img, null, xform, null, obs);
    }
    
    @Override
    public void drawImage(final BufferedImage img, final BufferedImageOp op, final int x, final int y) {
        BufferedImage result = img;
        if (op != null) {
            result = op.createCompatibleDestImage(img, img.getColorModel());
            result = op.filter(img, result);
        }
        this.drawImage(result, x, y, null);
    }
    
    @Override
    public void drawRenderedImage(final RenderedImage img, final AffineTransform xform) {
        BufferedImage image = null;
        if (img instanceof BufferedImage) {
            image = (BufferedImage)img;
        }
        else {
            final ColorModel cm = img.getColorModel();
            final int width = img.getWidth();
            final int height = img.getHeight();
            final WritableRaster raster = cm.createCompatibleWritableRaster(width, height);
            final boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
            final Hashtable properties = new Hashtable();
            final String[] keys = img.getPropertyNames();
            if (keys != null) {
                for (int i = 0; i < keys.length; ++i) {
                    properties.put(keys[i], img.getProperty(keys[i]));
                }
            }
            final BufferedImage result = new BufferedImage(cm, raster, isAlphaPremultiplied, properties);
            img.copyData(raster);
            image = result;
        }
        this.drawImage(image, xform, null);
    }
    
    @Override
    public void drawRenderableImage(final RenderableImage img, final AffineTransform xform) {
        this.drawRenderedImage(img.createDefaultRendering(), xform);
    }
    
    @Override
    public void drawString(final String s, final int x, final int y) {
        this.drawString(s, (float)x, (float)y);
    }
    
    public static double asPoints(final double d, final int i) {
        return d * i / 1000.0;
    }
    
    protected void doAttributes(final AttributedCharacterIterator iter) {
        this.underline = false;
        final Set set = iter.getAttributes().keySet();
        for (final AttributedCharacterIterator.Attribute attribute : set) {
            if (!(attribute instanceof TextAttribute)) {
                continue;
            }
            final TextAttribute textattribute = (TextAttribute)attribute;
            if (textattribute.equals(TextAttribute.FONT)) {
                final Font font = iter.getAttributes().get(textattribute);
                this.setFont(font);
            }
            else if (textattribute.equals(TextAttribute.UNDERLINE)) {
                if (iter.getAttributes().get(textattribute) != TextAttribute.UNDERLINE_ON) {
                    continue;
                }
                this.underline = true;
            }
            else if (textattribute.equals(TextAttribute.SIZE)) {
                final Object obj = iter.getAttributes().get(textattribute);
                if (obj instanceof Integer) {
                    final int i = (int)obj;
                    this.setFont(this.getFont().deriveFont(this.getFont().getStyle(), (float)i));
                }
                else {
                    if (!(obj instanceof Float)) {
                        continue;
                    }
                    final float f = (float)obj;
                    this.setFont(this.getFont().deriveFont(this.getFont().getStyle(), f));
                }
            }
            else if (textattribute.equals(TextAttribute.FOREGROUND)) {
                this.setColor(iter.getAttributes().get(textattribute));
            }
            else if (textattribute.equals(TextAttribute.FAMILY)) {
                final Font font = this.getFont();
                final Map fontAttributes = font.getAttributes();
                fontAttributes.put(TextAttribute.FAMILY, iter.getAttributes().get(textattribute));
                this.setFont(font.deriveFont(fontAttributes));
            }
            else if (textattribute.equals(TextAttribute.POSTURE)) {
                final Font font = this.getFont();
                final Map fontAttributes = font.getAttributes();
                fontAttributes.put(TextAttribute.POSTURE, iter.getAttributes().get(textattribute));
                this.setFont(font.deriveFont(fontAttributes));
            }
            else {
                if (!textattribute.equals(TextAttribute.WEIGHT)) {
                    continue;
                }
                final Font font = this.getFont();
                final Map fontAttributes = font.getAttributes();
                fontAttributes.put(TextAttribute.WEIGHT, iter.getAttributes().get(textattribute));
                this.setFont(font.deriveFont(fontAttributes));
            }
        }
    }
    
    @Override
    public void drawString(final String s, final float x, float y) {
        if (s.length() == 0) {
            return;
        }
        this.setFillPaint();
        if (this.onlyShapes) {
            this.drawGlyphVector(this.font.layoutGlyphVector(this.getFontRenderContext(), s.toCharArray(), 0, s.length(), 0), x, y);
        }
        else {
            boolean restoreTextRenderingMode = false;
            final AffineTransform at = this.getTransform();
            final AffineTransform at2 = this.getTransform();
            at2.translate(x, y);
            at2.concatenate(this.font.getTransform());
            this.setTransform(at2);
            final AffineTransform inverse = this.normalizeMatrix();
            final AffineTransform flipper = AffineTransform.getScaleInstance(1.0, -1.0);
            inverse.concatenate(flipper);
            final double[] mx = new double[6];
            inverse.getMatrix(mx);
            this.cb.beginText();
            this.cb.setFontAndSize(this.baseFont, this.fontSize);
            if (this.font.isItalic() && this.font.getFontName().equals(this.font.getName())) {
                final float angle = this.baseFont.getFontDescriptor(4, 1000.0f);
                float angle2 = this.font.getItalicAngle();
                if (angle2 == 0.0f) {
                    angle2 = 15.0f;
                }
                else {
                    angle2 = -angle2;
                }
                if (angle == 0.0f) {
                    mx[2] = angle2 / 100.0f;
                }
            }
            this.cb.setTextMatrix((float)mx[0], (float)mx[1], (float)mx[2], (float)mx[3], (float)mx[4], (float)mx[5]);
            Float fontTextAttributeWidth = (Float)this.font.getAttributes().get(TextAttribute.WIDTH);
            fontTextAttributeWidth = ((fontTextAttributeWidth == null) ? TextAttribute.WIDTH_REGULAR : fontTextAttributeWidth);
            if (!TextAttribute.WIDTH_REGULAR.equals(fontTextAttributeWidth)) {
                this.cb.setHorizontalScaling(100.0f / fontTextAttributeWidth);
            }
            if (this.baseFont.getPostscriptFontName().toLowerCase(Locale.ROOT).indexOf("bold") < 0) {
                Float weight = (Float)this.font.getAttributes().get(TextAttribute.WEIGHT);
                if (weight == null) {
                    weight = (this.font.isBold() ? TextAttribute.WEIGHT_BOLD : TextAttribute.WEIGHT_REGULAR);
                }
                if ((this.font.isBold() || weight >= TextAttribute.WEIGHT_SEMIBOLD) && this.font.getFontName().equals(this.font.getName())) {
                    final float strokeWidth = this.font.getSize2D() * (weight - TextAttribute.WEIGHT_REGULAR) / 30.0f;
                    if (strokeWidth != 1.0f && this.realPaint instanceof Color) {
                        this.cb.setTextRenderingMode(2);
                        this.cb.setLineWidth(strokeWidth);
                        final Color color = (Color)this.realPaint;
                        final int alpha = color.getAlpha();
                        if (alpha != this.currentStrokeGState) {
                            this.currentStrokeGState = alpha;
                            PdfGState gs = this.strokeGState[alpha];
                            if (gs == null) {
                                gs = new PdfGState();
                                gs.setStrokeOpacity(alpha / 255.0f);
                                this.strokeGState[alpha] = gs;
                            }
                            this.cb.setGState(gs);
                        }
                        this.cb.setColorStroke(color);
                        restoreTextRenderingMode = true;
                    }
                }
            }
            double width = 0.0;
            if (this.font.getSize2D() > 0.0f) {
                final float scale = 1000.0f / this.font.getSize2D();
                final Font derivedFont = this.font.deriveFont(AffineTransform.getScaleInstance(scale, scale));
                width = derivedFont.getStringBounds(s, this.getFontRenderContext()).getWidth();
                if (derivedFont.isTransformed()) {
                    width /= scale;
                }
            }
            final Object url = this.getRenderingHint(HyperLinkKey.KEY_INSTANCE);
            if (url != null && !url.equals(HyperLinkKey.VALUE_HYPERLINKKEY_OFF)) {
                final float scale2 = 1000.0f / this.font.getSize2D();
                final Font derivedFont2 = this.font.deriveFont(AffineTransform.getScaleInstance(scale2, scale2));
                double height = derivedFont2.getStringBounds(s, this.getFontRenderContext()).getHeight();
                if (derivedFont2.isTransformed()) {
                    height /= scale2;
                }
                final double leftX = this.cb.getXTLM();
                final double leftY = this.cb.getYTLM();
                final PdfAction action = new PdfAction(url.toString());
                this.cb.setAction(action, (float)leftX, (float)leftY, (float)(leftX + width), (float)(leftY + height));
            }
            if (s.length() > 1) {
                final float adv = ((float)width - this.baseFont.getWidthPoint(s, this.fontSize)) / (s.length() - 1);
                this.cb.setCharacterSpacing(adv);
            }
            this.cb.showText(s);
            if (s.length() > 1) {
                this.cb.setCharacterSpacing(0.0f);
            }
            if (!TextAttribute.WIDTH_REGULAR.equals(fontTextAttributeWidth)) {
                this.cb.setHorizontalScaling(100.0f);
            }
            if (restoreTextRenderingMode) {
                this.cb.setTextRenderingMode(0);
            }
            this.cb.endText();
            this.setTransform(at);
            if (this.underline) {
                final int UnderlineThickness = 50;
                final double d = asPoints(UnderlineThickness, (int)this.fontSize);
                final Stroke savedStroke = this.originalStroke;
                this.setStroke(new BasicStroke((float)d));
                y += (float)asPoints(UnderlineThickness, (int)this.fontSize);
                final Line2D line = new Line2D.Double(x, y, width + x, y);
                this.draw(line);
                this.setStroke(savedStroke);
            }
        }
    }
    
    @Override
    public void drawString(final AttributedCharacterIterator iterator, final int x, final int y) {
        this.drawString(iterator, (float)x, (float)y);
    }
    
    @Override
    public void drawString(final AttributedCharacterIterator iter, float x, final float y) {
        final StringBuffer stringbuffer = new StringBuffer(iter.getEndIndex());
        for (char c = iter.first(); c != '\uffff'; c = iter.next()) {
            if (iter.getIndex() == iter.getRunStart()) {
                if (stringbuffer.length() > 0) {
                    this.drawString(stringbuffer.toString(), x, y);
                    final FontMetrics fontmetrics = this.getFontMetrics();
                    x += (float)fontmetrics.getStringBounds(stringbuffer.toString(), this).getWidth();
                    stringbuffer.delete(0, stringbuffer.length());
                }
                this.doAttributes(iter);
            }
            stringbuffer.append(c);
        }
        this.drawString(stringbuffer.toString(), x, y);
        this.underline = false;
    }
    
    @Override
    public void drawGlyphVector(final GlyphVector g, final float x, final float y) {
        final Shape s = g.getOutline(x, y);
        this.fill(s);
    }
    
    @Override
    public void fill(final Shape s) {
        this.followPath(s, 1);
    }
    
    @Override
    public boolean hit(final Rectangle rect, Shape s, final boolean onStroke) {
        if (onStroke) {
            s = this.stroke.createStrokedShape(s);
        }
        s = this.transform.createTransformedShape(s);
        final Area area = new Area(s);
        if (this.clip != null) {
            area.intersect(this.clip);
        }
        return area.intersects(rect.x, rect.y, rect.width, rect.height);
    }
    
    @Override
    public GraphicsConfiguration getDeviceConfiguration() {
        return this.dg2.getDeviceConfiguration();
    }
    
    @Override
    public void setComposite(final Composite comp) {
        if (comp instanceof AlphaComposite) {
            final AlphaComposite composite = (AlphaComposite)comp;
            if (composite.getRule() == 3) {
                this.alpha = composite.getAlpha();
                this.composite = composite;
                if (this.realPaint != null && this.realPaint instanceof Color) {
                    final Color c = (Color)this.realPaint;
                    this.paint = new Color(c.getRed(), c.getGreen(), c.getBlue(), (int)(c.getAlpha() * this.alpha));
                }
                return;
            }
        }
        this.composite = comp;
        this.alpha = 1.0f;
    }
    
    @Override
    public void setPaint(final Paint paint) {
        if (paint == null) {
            return;
        }
        this.paint = paint;
        this.realPaint = paint;
        if (this.composite instanceof AlphaComposite && paint instanceof Color) {
            final AlphaComposite co = (AlphaComposite)this.composite;
            if (co.getRule() == 3) {
                final Color c = (Color)paint;
                this.paint = new Color(c.getRed(), c.getGreen(), c.getBlue(), (int)(c.getAlpha() * this.alpha));
                this.realPaint = paint;
            }
        }
    }
    
    private Stroke transformStroke(final Stroke stroke) {
        if (!(stroke instanceof BasicStroke)) {
            return stroke;
        }
        final BasicStroke st = (BasicStroke)stroke;
        final float scale = (float)Math.sqrt(Math.abs(this.transform.getDeterminant()));
        final float[] dash = st.getDashArray();
        if (dash != null) {
            for (int k = 0; k < dash.length; ++k) {
                final float[] array = dash;
                final int n = k;
                array[n] *= scale;
            }
        }
        return new BasicStroke(st.getLineWidth() * scale, st.getEndCap(), st.getLineJoin(), st.getMiterLimit(), dash, st.getDashPhase() * scale);
    }
    
    private void setStrokeDiff(final Stroke newStroke, final Stroke oldStroke) {
        if (newStroke == oldStroke) {
            return;
        }
        if (!(newStroke instanceof BasicStroke)) {
            return;
        }
        final BasicStroke nStroke = (BasicStroke)newStroke;
        final boolean oldOk = oldStroke instanceof BasicStroke;
        BasicStroke oStroke = null;
        if (oldOk) {
            oStroke = (BasicStroke)oldStroke;
        }
        if (!oldOk || nStroke.getLineWidth() != oStroke.getLineWidth()) {
            this.cb.setLineWidth(nStroke.getLineWidth());
        }
        if (!oldOk || nStroke.getEndCap() != oStroke.getEndCap()) {
            switch (nStroke.getEndCap()) {
                case 0: {
                    this.cb.setLineCap(0);
                    break;
                }
                case 2: {
                    this.cb.setLineCap(2);
                    break;
                }
                default: {
                    this.cb.setLineCap(1);
                    break;
                }
            }
        }
        if (!oldOk || nStroke.getLineJoin() != oStroke.getLineJoin()) {
            switch (nStroke.getLineJoin()) {
                case 0: {
                    this.cb.setLineJoin(0);
                    break;
                }
                case 2: {
                    this.cb.setLineJoin(2);
                    break;
                }
                default: {
                    this.cb.setLineJoin(1);
                    break;
                }
            }
        }
        if (!oldOk || nStroke.getMiterLimit() != oStroke.getMiterLimit()) {
            this.cb.setMiterLimit(nStroke.getMiterLimit());
        }
        boolean makeDash;
        if (oldOk) {
            if (nStroke.getDashArray() != null) {
                makeDash = (nStroke.getDashPhase() != oStroke.getDashPhase() || !Arrays.equals(nStroke.getDashArray(), oStroke.getDashArray()));
            }
            else {
                makeDash = (oStroke.getDashArray() != null);
            }
        }
        else {
            makeDash = true;
        }
        if (makeDash) {
            final float[] dash = nStroke.getDashArray();
            if (dash == null) {
                this.cb.setLiteral("[]0 d\n");
            }
            else {
                this.cb.setLiteral('[');
                for (int lim = dash.length, k = 0; k < lim; ++k) {
                    this.cb.setLiteral(dash[k]);
                    this.cb.setLiteral(' ');
                }
                this.cb.setLiteral(']');
                this.cb.setLiteral(nStroke.getDashPhase());
                this.cb.setLiteral(" d\n");
            }
        }
    }
    
    @Override
    public void setStroke(final Stroke s) {
        this.originalStroke = s;
        this.stroke = this.transformStroke(s);
    }
    
    @Override
    public void setRenderingHint(final RenderingHints.Key arg0, final Object arg1) {
        if (arg1 != null) {
            this.rhints.put(arg0, arg1);
        }
        else if (arg0 instanceof HyperLinkKey) {
            this.rhints.put(arg0, HyperLinkKey.VALUE_HYPERLINKKEY_OFF);
        }
        else {
            this.rhints.remove(arg0);
        }
    }
    
    @Override
    public Object getRenderingHint(final RenderingHints.Key arg0) {
        return this.rhints.get(arg0);
    }
    
    @Override
    public void setRenderingHints(final Map hints) {
        this.rhints.clear();
        this.rhints.putAll(hints);
    }
    
    @Override
    public void addRenderingHints(final Map hints) {
        this.rhints.putAll(hints);
    }
    
    @Override
    public RenderingHints getRenderingHints() {
        return this.rhints;
    }
    
    @Override
    public void translate(final int x, final int y) {
        this.translate(x, (double)y);
    }
    
    @Override
    public void translate(final double tx, final double ty) {
        this.transform.translate(tx, ty);
    }
    
    @Override
    public void rotate(final double theta) {
        this.transform.rotate(theta);
    }
    
    @Override
    public void rotate(final double theta, final double x, final double y) {
        this.transform.rotate(theta, x, y);
    }
    
    @Override
    public void scale(final double sx, final double sy) {
        this.transform.scale(sx, sy);
        this.stroke = this.transformStroke(this.originalStroke);
    }
    
    @Override
    public void shear(final double shx, final double shy) {
        this.transform.shear(shx, shy);
    }
    
    @Override
    public void transform(final AffineTransform tx) {
        this.transform.concatenate(tx);
        this.stroke = this.transformStroke(this.originalStroke);
    }
    
    @Override
    public void setTransform(final AffineTransform t) {
        this.transform = new AffineTransform(t);
        this.stroke = this.transformStroke(this.originalStroke);
    }
    
    @Override
    public AffineTransform getTransform() {
        return new AffineTransform(this.transform);
    }
    
    @Override
    public Paint getPaint() {
        if (this.realPaint != null) {
            return this.realPaint;
        }
        return this.paint;
    }
    
    @Override
    public Composite getComposite() {
        return this.composite;
    }
    
    @Override
    public void setBackground(final Color color) {
        this.background = color;
    }
    
    @Override
    public Color getBackground() {
        return this.background;
    }
    
    @Override
    public Stroke getStroke() {
        return this.originalStroke;
    }
    
    @Override
    public FontRenderContext getFontRenderContext() {
        final boolean antialias = RenderingHints.VALUE_TEXT_ANTIALIAS_ON.equals(this.getRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING));
        final boolean fractions = RenderingHints.VALUE_FRACTIONALMETRICS_ON.equals(this.getRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS));
        return new FontRenderContext(new AffineTransform(), antialias, fractions);
    }
    
    @Override
    public Graphics create() {
        final PdfGraphics2D g2 = new PdfGraphics2D();
        g2.rhints.putAll(this.rhints);
        g2.onlyShapes = this.onlyShapes;
        g2.transform = new AffineTransform(this.transform);
        g2.baseFonts = this.baseFonts;
        g2.fontMapper = this.fontMapper;
        g2.paint = this.paint;
        g2.fillGState = this.fillGState;
        g2.currentFillGState = this.currentFillGState;
        g2.currentStrokeGState = this.currentStrokeGState;
        g2.strokeGState = this.strokeGState;
        g2.background = this.background;
        g2.mediaTracker = this.mediaTracker;
        g2.convertImagesToJPEG = this.convertImagesToJPEG;
        g2.jpegQuality = this.jpegQuality;
        g2.setFont(this.font);
        (g2.cb = this.cb.getDuplicate()).saveState();
        g2.width = this.width;
        g2.height = this.height;
        g2.followPath(new Area(new Rectangle2D.Float(0.0f, 0.0f, this.width, this.height)), 3);
        if (this.clip != null) {
            g2.clip = new Area(this.clip);
        }
        g2.composite = this.composite;
        g2.stroke = this.stroke;
        g2.originalStroke = this.originalStroke;
        g2.strokeOne = (BasicStroke)g2.transformStroke(g2.strokeOne);
        g2.setStrokeDiff(g2.oldStroke = g2.strokeOne, null);
        g2.cb.saveState();
        if (g2.clip != null) {
            g2.followPath(g2.clip, 3);
        }
        g2.kid = true;
        if (this.kids == null) {
            this.kids = new ArrayList();
        }
        this.kids.add(new Integer(this.cb.getInternalBuffer().size()));
        this.kids.add(g2);
        return g2;
    }
    
    public PdfContentByte getContent() {
        return this.cb;
    }
    
    @Override
    public Color getColor() {
        if (this.paint instanceof Color) {
            return (Color)this.paint;
        }
        return Color.black;
    }
    
    @Override
    public void setColor(final Color color) {
        this.setPaint(color);
    }
    
    @Override
    public void setPaintMode() {
    }
    
    @Override
    public void setXORMode(final Color c1) {
    }
    
    @Override
    public Font getFont() {
        return this.font;
    }
    
    @Override
    public void setFont(final Font f) {
        if (f == null) {
            return;
        }
        if (this.onlyShapes) {
            this.font = f;
            return;
        }
        if (f == this.font) {
            return;
        }
        this.font = f;
        this.fontSize = f.getSize2D();
        this.baseFont = this.getCachedBaseFont(f);
    }
    
    private BaseFont getCachedBaseFont(final Font f) {
        synchronized (this.baseFonts) {
            BaseFont bf = this.baseFonts.get(f.getFontName());
            if (bf == null) {
                bf = this.fontMapper.awtToPdf(f);
                this.baseFonts.put(f.getFontName(), bf);
            }
            return bf;
        }
    }
    
    @Override
    public FontMetrics getFontMetrics(final Font f) {
        return this.dg2.getFontMetrics(f);
    }
    
    @Override
    public Rectangle getClipBounds() {
        if (this.clip == null) {
            return null;
        }
        return this.getClip().getBounds();
    }
    
    @Override
    public void clipRect(final int x, final int y, final int width, final int height) {
        final Rectangle2D rect = new Rectangle2D.Double(x, y, width, height);
        this.clip(rect);
    }
    
    @Override
    public void setClip(final int x, final int y, final int width, final int height) {
        final Rectangle2D rect = new Rectangle2D.Double(x, y, width, height);
        this.setClip(rect);
    }
    
    @Override
    public void clip(Shape s) {
        if (s == null) {
            this.setClip(null);
            return;
        }
        s = this.transform.createTransformedShape(s);
        if (this.clip == null) {
            this.clip = new Area(s);
        }
        else {
            this.clip.intersect(new Area(s));
        }
        this.followPath(s, 3);
    }
    
    @Override
    public Shape getClip() {
        try {
            return this.transform.createInverse().createTransformedShape(this.clip);
        }
        catch (final NoninvertibleTransformException e) {
            return null;
        }
    }
    
    @Override
    public void setClip(Shape s) {
        this.cb.restoreState();
        this.cb.saveState();
        if (s != null) {
            s = this.transform.createTransformedShape(s);
        }
        if (s == null) {
            this.clip = null;
        }
        else {
            this.clip = new Area(s);
            this.followPath(s, 3);
        }
        final Paint paint = null;
        this.paintStroke = paint;
        this.paintFill = paint;
        final int n = -1;
        this.currentStrokeGState = n;
        this.currentFillGState = n;
        this.oldStroke = this.strokeOne;
    }
    
    @Override
    public void copyArea(final int x, final int y, final int width, final int height, final int dx, final int dy) {
    }
    
    @Override
    public void drawLine(final int x1, final int y1, final int x2, final int y2) {
        final Line2D line = new Line2D.Double(x1, y1, x2, y2);
        this.draw(line);
    }
    
    @Override
    public void drawRect(final int x, final int y, final int width, final int height) {
        this.draw(new Rectangle(x, y, width, height));
    }
    
    @Override
    public void fillRect(final int x, final int y, final int width, final int height) {
        this.fill(new Rectangle(x, y, width, height));
    }
    
    @Override
    public void clearRect(final int x, final int y, final int width, final int height) {
        final Paint temp = this.paint;
        this.setPaint(this.background);
        this.fillRect(x, y, width, height);
        this.setPaint(temp);
    }
    
    @Override
    public void drawRoundRect(final int x, final int y, final int width, final int height, final int arcWidth, final int arcHeight) {
        final RoundRectangle2D rect = new RoundRectangle2D.Double(x, y, width, height, arcWidth, arcHeight);
        this.draw(rect);
    }
    
    @Override
    public void fillRoundRect(final int x, final int y, final int width, final int height, final int arcWidth, final int arcHeight) {
        final RoundRectangle2D rect = new RoundRectangle2D.Double(x, y, width, height, arcWidth, arcHeight);
        this.fill(rect);
    }
    
    @Override
    public void drawOval(final int x, final int y, final int width, final int height) {
        final Ellipse2D oval = new Ellipse2D.Float((float)x, (float)y, (float)width, (float)height);
        this.draw(oval);
    }
    
    @Override
    public void fillOval(final int x, final int y, final int width, final int height) {
        final Ellipse2D oval = new Ellipse2D.Float((float)x, (float)y, (float)width, (float)height);
        this.fill(oval);
    }
    
    @Override
    public void drawArc(final int x, final int y, final int width, final int height, final int startAngle, final int arcAngle) {
        final Arc2D arc = new Arc2D.Double(x, y, width, height, startAngle, arcAngle, 0);
        this.draw(arc);
    }
    
    @Override
    public void fillArc(final int x, final int y, final int width, final int height, final int startAngle, final int arcAngle) {
        final Arc2D arc = new Arc2D.Double(x, y, width, height, startAngle, arcAngle, 2);
        this.fill(arc);
    }
    
    @Override
    public void drawPolyline(final int[] x, final int[] y, final int nPoints) {
        final PolylineShape polyline = new PolylineShape(x, y, nPoints);
        this.draw(polyline);
    }
    
    @Override
    public void drawPolygon(final int[] xPoints, final int[] yPoints, final int nPoints) {
        final Polygon poly = new Polygon(xPoints, yPoints, nPoints);
        this.draw(poly);
    }
    
    @Override
    public void fillPolygon(final int[] xPoints, final int[] yPoints, final int nPoints) {
        final Polygon poly = new Polygon();
        for (int i = 0; i < nPoints; ++i) {
            poly.addPoint(xPoints[i], yPoints[i]);
        }
        this.fill(poly);
    }
    
    @Override
    public boolean drawImage(final Image img, final int x, final int y, final ImageObserver observer) {
        return this.drawImage(img, x, y, null, observer);
    }
    
    @Override
    public boolean drawImage(final Image img, final int x, final int y, final int width, final int height, final ImageObserver observer) {
        return this.drawImage(img, x, y, width, height, null, observer);
    }
    
    @Override
    public boolean drawImage(final Image img, final int x, final int y, final Color bgcolor, final ImageObserver observer) {
        this.waitForImage(img);
        return this.drawImage(img, x, y, img.getWidth(observer), img.getHeight(observer), bgcolor, observer);
    }
    
    @Override
    public boolean drawImage(final Image img, final int x, final int y, final int width, final int height, final Color bgcolor, final ImageObserver observer) {
        this.waitForImage(img);
        final double scalex = width / (double)img.getWidth(observer);
        final double scaley = height / (double)img.getHeight(observer);
        final AffineTransform tx = AffineTransform.getTranslateInstance(x, y);
        tx.scale(scalex, scaley);
        return this.drawImage(img, null, tx, bgcolor, observer);
    }
    
    @Override
    public boolean drawImage(final Image img, final int dx1, final int dy1, final int dx2, final int dy2, final int sx1, final int sy1, final int sx2, final int sy2, final ImageObserver observer) {
        return this.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, null, observer);
    }
    
    @Override
    public boolean drawImage(final Image img, final int dx1, final int dy1, final int dx2, final int dy2, final int sx1, final int sy1, final int sx2, final int sy2, final Color bgcolor, final ImageObserver observer) {
        this.waitForImage(img);
        final double dwidth = dx2 - (double)dx1;
        final double dheight = dy2 - (double)dy1;
        final double swidth = sx2 - (double)sx1;
        final double sheight = sy2 - (double)sy1;
        if (dwidth == 0.0 || dheight == 0.0 || swidth == 0.0 || sheight == 0.0) {
            return true;
        }
        final double scalex = dwidth / swidth;
        final double scaley = dheight / sheight;
        final double transx = sx1 * scalex;
        final double transy = sy1 * scaley;
        final AffineTransform tx = AffineTransform.getTranslateInstance(dx1 - transx, dy1 - transy);
        tx.scale(scalex, scaley);
        final BufferedImage mask = new BufferedImage(img.getWidth(observer), img.getHeight(observer), 12);
        final Graphics g = mask.getGraphics();
        g.fillRect(sx1, sy1, (int)swidth, (int)sheight);
        this.drawImage(img, mask, tx, null, observer);
        g.dispose();
        return true;
    }
    
    @Override
    public void dispose() {
        if (this.kid) {
            return;
        }
        if (!this.disposeCalled) {
            this.disposeCalled = true;
            this.cb.restoreState();
            this.cb.restoreState();
            this.dg2.dispose();
            this.dg2 = null;
            if (this.kids != null) {
                final ByteBuffer buf = new ByteBuffer();
                this.internalDispose(buf);
                final ByteBuffer buf2 = this.cb.getInternalBuffer();
                buf2.reset();
                buf2.append(buf);
            }
        }
    }
    
    private void internalDispose(final ByteBuffer buf) {
        int last = 0;
        int pos = 0;
        final ByteBuffer buf2 = this.cb.getInternalBuffer();
        if (this.kids != null) {
            for (int k = 0; k < this.kids.size(); k += 2) {
                pos = this.kids.get(k);
                final PdfGraphics2D g2 = this.kids.get(k + 1);
                g2.cb.restoreState();
                g2.cb.restoreState();
                buf.append(buf2.getBuffer(), last, pos - last);
                g2.dg2.dispose();
                g2.dg2 = null;
                g2.internalDispose(buf);
                last = pos;
            }
        }
        buf.append(buf2.getBuffer(), last, buf2.size() - last);
    }
    
    private void followPath(Shape s, final int drawType) {
        if (s == null) {
            return;
        }
        if (drawType == 2 && !(this.stroke instanceof BasicStroke)) {
            s = this.stroke.createStrokedShape(s);
            this.followPath(s, 1);
            return;
        }
        if (drawType == 2) {
            this.setStrokeDiff(this.stroke, this.oldStroke);
            this.oldStroke = this.stroke;
            this.setStrokePaint();
        }
        else if (drawType == 1) {
            this.setFillPaint();
        }
        int traces = 0;
        PathIterator points;
        if (drawType == 3) {
            points = s.getPathIterator(PdfGraphics2D.IDENTITY);
        }
        else {
            points = s.getPathIterator(this.transform);
        }
        final float[] coords = new float[6];
        while (!points.isDone()) {
            ++traces;
            final int segtype = points.currentSegment(coords);
            this.normalizeY(coords);
            switch (segtype) {
                case 4: {
                    this.cb.closePath();
                    break;
                }
                case 3: {
                    this.cb.curveTo(coords[0], coords[1], coords[2], coords[3], coords[4], coords[5]);
                    break;
                }
                case 1: {
                    this.cb.lineTo(coords[0], coords[1]);
                    break;
                }
                case 0: {
                    this.cb.moveTo(coords[0], coords[1]);
                    break;
                }
                case 2: {
                    this.cb.curveTo(coords[0], coords[1], coords[2], coords[3]);
                    break;
                }
            }
            points.next();
        }
        switch (drawType) {
            case 1: {
                if (traces <= 0) {
                    break;
                }
                if (points.getWindingRule() == 0) {
                    this.cb.eoFill();
                    break;
                }
                this.cb.fill();
                break;
            }
            case 2: {
                if (traces > 0) {
                    this.cb.stroke();
                    break;
                }
                break;
            }
            default: {
                if (traces == 0) {
                    this.cb.rectangle(0.0f, 0.0f, 0.0f, 0.0f);
                }
                if (points.getWindingRule() == 0) {
                    this.cb.eoClip();
                }
                else {
                    this.cb.clip();
                }
                this.cb.newPath();
                break;
            }
        }
    }
    
    private float normalizeY(final float y) {
        return this.height - y;
    }
    
    private void normalizeY(final float[] coords) {
        coords[1] = this.normalizeY(coords[1]);
        coords[3] = this.normalizeY(coords[3]);
        coords[5] = this.normalizeY(coords[5]);
    }
    
    private AffineTransform normalizeMatrix() {
        final double[] mx = new double[6];
        AffineTransform result = AffineTransform.getTranslateInstance(0.0, 0.0);
        result.getMatrix(mx);
        mx[3] = -1.0;
        mx[5] = this.height;
        result = new AffineTransform(mx);
        result.concatenate(this.transform);
        return result;
    }
    
    private boolean drawImage(final Image img, final Image mask, AffineTransform xform, final Color bgColor, final ImageObserver obs) {
        if (xform == null) {
            xform = new AffineTransform();
        }
        else {
            xform = new AffineTransform(xform);
        }
        xform.translate(0.0, img.getHeight(obs));
        xform.scale(img.getWidth(obs), img.getHeight(obs));
        final AffineTransform inverse = this.normalizeMatrix();
        final AffineTransform flipper = AffineTransform.getScaleInstance(1.0, -1.0);
        inverse.concatenate(xform);
        inverse.concatenate(flipper);
        final double[] mx = new double[6];
        inverse.getMatrix(mx);
        if (this.currentFillGState != 255) {
            PdfGState gs = this.fillGState[255];
            if (gs == null) {
                gs = new PdfGState();
                gs.setFillOpacity(1.0f);
                this.fillGState[255] = gs;
            }
            this.cb.setGState(gs);
        }
        try {
            com.lowagie.text.Image image = null;
            if (!this.convertImagesToJPEG) {
                image = com.lowagie.text.Image.getInstance(img, bgColor);
            }
            else {
                BufferedImage scaled = new BufferedImage(img.getWidth(null), img.getHeight(null), 1);
                final Graphics2D g3 = scaled.createGraphics();
                g3.drawImage(img, 0, 0, img.getWidth(null), img.getHeight(null), null);
                g3.dispose();
                final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                final ImageWriteParam iwparam = new JPEGImageWriteParam(Locale.getDefault());
                iwparam.setCompressionMode(2);
                iwparam.setCompressionQuality(this.jpegQuality);
                final ImageWriter iw = ImageIO.getImageWritersByFormatName("jpg").next();
                final ImageOutputStream ios = ImageIO.createImageOutputStream(baos);
                iw.setOutput(ios);
                iw.write(null, new IIOImage(scaled, null, null), iwparam);
                iw.dispose();
                ios.close();
                scaled.flush();
                scaled = null;
                image = com.lowagie.text.Image.getInstance(baos.toByteArray());
            }
            if (mask != null) {
                final com.lowagie.text.Image msk = com.lowagie.text.Image.getInstance(mask, null, true);
                msk.makeMask();
                msk.setInverted(true);
                image.setImageMask(msk);
            }
            this.cb.addImage(image, (float)mx[0], (float)mx[1], (float)mx[2], (float)mx[3], (float)mx[4], (float)mx[5]);
            final Object url = this.getRenderingHint(HyperLinkKey.KEY_INSTANCE);
            if (url != null && !url.equals(HyperLinkKey.VALUE_HYPERLINKKEY_OFF)) {
                final PdfAction action = new PdfAction(url.toString());
                this.cb.setAction(action, (float)mx[4], (float)mx[5], (float)(mx[0] + mx[4]), (float)(mx[3] + mx[5]));
            }
        }
        catch (final Exception ex) {
            throw new IllegalArgumentException();
        }
        if (this.currentFillGState != 255 && this.currentFillGState != -1) {
            final PdfGState gs = this.fillGState[this.currentFillGState];
            this.cb.setGState(gs);
        }
        return true;
    }
    
    private boolean checkNewPaint(final Paint oldPaint) {
        return this.paint != oldPaint && (!(this.paint instanceof Color) || !this.paint.equals(oldPaint));
    }
    
    private void setFillPaint() {
        if (this.checkNewPaint(this.paintFill)) {
            this.paintFill = this.paint;
            this.setPaint(false, 0.0, 0.0, true);
        }
    }
    
    private void setStrokePaint() {
        if (this.checkNewPaint(this.paintStroke)) {
            this.paintStroke = this.paint;
            this.setPaint(false, 0.0, 0.0, false);
        }
    }
    
    private void setPaint(final boolean invert, final double xoffset, final double yoffset, final boolean fill) {
        if (this.paint instanceof Color) {
            final Color color = (Color)this.paint;
            final int alpha = color.getAlpha();
            if (fill) {
                if (alpha != this.currentFillGState) {
                    this.currentFillGState = alpha;
                    PdfGState gs = this.fillGState[alpha];
                    if (gs == null) {
                        gs = new PdfGState();
                        gs.setFillOpacity(alpha / 255.0f);
                        this.fillGState[alpha] = gs;
                    }
                    this.cb.setGState(gs);
                }
                this.cb.setColorFill(color);
            }
            else {
                if (alpha != this.currentStrokeGState) {
                    this.currentStrokeGState = alpha;
                    PdfGState gs = this.strokeGState[alpha];
                    if (gs == null) {
                        gs = new PdfGState();
                        gs.setStrokeOpacity(alpha / 255.0f);
                        this.strokeGState[alpha] = gs;
                    }
                    this.cb.setGState(gs);
                }
                this.cb.setColorStroke(color);
            }
        }
        else if (this.paint instanceof GradientPaint) {
            final GradientPaint gp = (GradientPaint)this.paint;
            final Point2D p1 = gp.getPoint1();
            this.transform.transform(p1, p1);
            final Point2D p2 = gp.getPoint2();
            this.transform.transform(p2, p2);
            final Color c1 = gp.getColor1();
            final Color c2 = gp.getColor2();
            final PdfShading shading = PdfShading.simpleAxial(this.cb.getPdfWriter(), (float)p1.getX(), this.normalizeY((float)p1.getY()), (float)p2.getX(), this.normalizeY((float)p2.getY()), c1, c2);
            final PdfShadingPattern pat = new PdfShadingPattern(shading);
            if (fill) {
                this.cb.setShadingFill(pat);
            }
            else {
                this.cb.setShadingStroke(pat);
            }
        }
        else if (this.paint instanceof TexturePaint) {
            try {
                final TexturePaint tp = (TexturePaint)this.paint;
                final BufferedImage img = tp.getImage();
                final Rectangle2D rect = tp.getAnchorRect();
                final com.lowagie.text.Image image = com.lowagie.text.Image.getInstance(img, null);
                final PdfPatternPainter pattern = this.cb.createPattern(image.getWidth(), image.getHeight());
                final AffineTransform inverse = this.normalizeMatrix();
                inverse.translate(rect.getX(), rect.getY());
                inverse.scale(rect.getWidth() / image.getWidth(), -rect.getHeight() / image.getHeight());
                final double[] mx = new double[6];
                inverse.getMatrix(mx);
                pattern.setPatternMatrix((float)mx[0], (float)mx[1], (float)mx[2], (float)mx[3], (float)mx[4], (float)mx[5]);
                image.setAbsolutePosition(0.0f, 0.0f);
                pattern.addImage(image);
                if (fill) {
                    this.cb.setPatternFill(pattern);
                }
                else {
                    this.cb.setPatternStroke(pattern);
                }
            }
            catch (final Exception ex) {
                if (fill) {
                    this.cb.setColorFill(Color.gray);
                }
                else {
                    this.cb.setColorStroke(Color.gray);
                }
            }
        }
        else {
            try {
                BufferedImage img2 = null;
                int type = 6;
                if (this.paint.getTransparency() == 1) {
                    type = 5;
                }
                img2 = new BufferedImage((int)this.width, (int)this.height, type);
                Graphics2D g = (Graphics2D)img2.getGraphics();
                g.transform(this.transform);
                final AffineTransform inv = this.transform.createInverse();
                Shape fillRect = new Rectangle2D.Double(0.0, 0.0, img2.getWidth(), img2.getHeight());
                fillRect = inv.createTransformedShape(fillRect);
                g.setPaint(this.paint);
                g.fill(fillRect);
                if (invert) {
                    final AffineTransform tx = new AffineTransform();
                    tx.scale(1.0, -1.0);
                    tx.translate(-xoffset, -yoffset);
                    g.drawImage(img2, tx, null);
                }
                g.dispose();
                g = null;
                final com.lowagie.text.Image image2 = com.lowagie.text.Image.getInstance(img2, null);
                final PdfPatternPainter pattern2 = this.cb.createPattern(this.width, this.height);
                image2.setAbsolutePosition(0.0f, 0.0f);
                pattern2.addImage(image2);
                if (fill) {
                    if (this.currentFillGState != 255) {
                        this.currentFillGState = 255;
                        PdfGState gs2 = this.fillGState[255];
                        if (gs2 == null) {
                            gs2 = new PdfGState();
                            gs2.setFillOpacity(1.0f);
                            this.fillGState[255] = gs2;
                        }
                        this.cb.setGState(gs2);
                    }
                    this.cb.setPatternFill(pattern2);
                }
                else {
                    this.cb.setPatternStroke(pattern2);
                }
            }
            catch (final Exception ex) {
                if (fill) {
                    this.cb.setColorFill(Color.gray);
                }
                else {
                    this.cb.setColorStroke(Color.gray);
                }
            }
        }
    }
    
    private synchronized void waitForImage(final Image image) {
        if (this.mediaTracker == null) {
            this.mediaTracker = new MediaTracker(new FakeComponent());
        }
        this.mediaTracker.addImage(image, 0);
        try {
            this.mediaTracker.waitForID(0);
        }
        catch (final InterruptedException ex) {}
        this.mediaTracker.removeImage(image);
    }
    
    static {
        IDENTITY = new AffineTransform();
    }
    
    private static class FakeComponent extends Component
    {
        private static final long serialVersionUID = 6450197945596086638L;
    }
    
    public static class HyperLinkKey extends RenderingHints.Key
    {
        public static final HyperLinkKey KEY_INSTANCE;
        public static final Object VALUE_HYPERLINKKEY_OFF;
        
        protected HyperLinkKey(final int arg0) {
            super(arg0);
        }
        
        @Override
        public boolean isCompatibleValue(final Object val) {
            return true;
        }
        
        @Override
        public String toString() {
            return "HyperLinkKey";
        }
        
        static {
            KEY_INSTANCE = new HyperLinkKey(9999);
            VALUE_HYPERLINKKEY_OFF = "0";
        }
    }
}
