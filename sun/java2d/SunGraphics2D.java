package sun.java2d;

import sun.java2d.pipe.RenderingEngine;
import sun.misc.PerformanceLogger;
import java.awt.image.BufferedImageOp;
import sun.awt.image.ToolkitImage;
import sun.awt.image.MultiResolutionToolkitImage;
import sun.awt.image.MultiResolutionImage;
import sun.awt.image.SurfaceManager;
import java.awt.font.GlyphVector;
import java.text.AttributedCharacterIterator;
import java.awt.font.TextLayout;
import java.awt.image.renderable.RenderContext;
import java.awt.image.renderable.RenderableImage;
import java.awt.image.WritableRaster;
import java.util.Hashtable;
import java.awt.Point;
import java.awt.image.Raster;
import java.awt.image.ImageObserver;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import sun.java2d.loops.SurfaceType;
import java.awt.geom.NoninvertibleTransformException;
import sun.java2d.pipe.ShapeSpanIterator;
import java.awt.geom.PathIterator;
import sun.java2d.pipe.SpanIterator;
import sun.java2d.pipe.LoopPipe;
import java.util.Iterator;
import java.util.Map;
import java.awt.BasicStroke;
import java.awt.TexturePaint;
import java.awt.RadialGradientPaint;
import java.awt.LinearGradientPaint;
import java.awt.GradientPaint;
import sun.java2d.loops.XORComposite;
import java.awt.AlphaComposite;
import java.awt.GraphicsConfiguration;
import java.awt.image.ColorModel;
import sun.font.FontDesignMetrics;
import sun.awt.SunHints;
import sun.font.FontUtilities;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.awt.Rectangle;
import java.awt.Graphics;
import sun.java2d.loops.Blit;
import sun.java2d.pipe.ValidatePipe;
import java.awt.font.FontRenderContext;
import sun.java2d.loops.FontInfo;
import java.awt.Shape;
import sun.java2d.pipe.Region;
import java.awt.RenderingHints;
import java.awt.FontMetrics;
import java.awt.Paint;
import java.awt.Font;
import java.awt.Composite;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.Color;
import sun.java2d.loops.CompositeType;
import sun.java2d.loops.RenderLoops;
import sun.java2d.loops.MaskFill;
import sun.java2d.pipe.TextPipe;
import sun.java2d.pipe.ShapeDrawPipe;
import sun.java2d.pipe.DrawImagePipe;
import sun.java2d.pipe.PixelFillPipe;
import sun.java2d.pipe.PixelDrawPipe;
import sun.awt.ConstrainableGraphics;
import java.awt.Graphics2D;

public final class SunGraphics2D extends Graphics2D implements ConstrainableGraphics, Cloneable, DestSurfaceProvider
{
    public static final int PAINT_CUSTOM = 6;
    public static final int PAINT_TEXTURE = 5;
    public static final int PAINT_RAD_GRADIENT = 4;
    public static final int PAINT_LIN_GRADIENT = 3;
    public static final int PAINT_GRADIENT = 2;
    public static final int PAINT_ALPHACOLOR = 1;
    public static final int PAINT_OPAQUECOLOR = 0;
    public static final int COMP_CUSTOM = 3;
    public static final int COMP_XOR = 2;
    public static final int COMP_ALPHA = 1;
    public static final int COMP_ISCOPY = 0;
    public static final int STROKE_CUSTOM = 3;
    public static final int STROKE_WIDE = 2;
    public static final int STROKE_THINDASHED = 1;
    public static final int STROKE_THIN = 0;
    public static final int TRANSFORM_GENERIC = 4;
    public static final int TRANSFORM_TRANSLATESCALE = 3;
    public static final int TRANSFORM_ANY_TRANSLATE = 2;
    public static final int TRANSFORM_INT_TRANSLATE = 1;
    public static final int TRANSFORM_ISIDENT = 0;
    public static final int CLIP_SHAPE = 2;
    public static final int CLIP_RECTANGULAR = 1;
    public static final int CLIP_DEVICE = 0;
    public int eargb;
    public int pixel;
    public SurfaceData surfaceData;
    public PixelDrawPipe drawpipe;
    public PixelFillPipe fillpipe;
    public DrawImagePipe imagepipe;
    public ShapeDrawPipe shapepipe;
    public TextPipe textpipe;
    public MaskFill alphafill;
    public RenderLoops loops;
    public CompositeType imageComp;
    public int paintState;
    public int compositeState;
    public int strokeState;
    public int transformState;
    public int clipState;
    public Color foregroundColor;
    public Color backgroundColor;
    public AffineTransform transform;
    public int transX;
    public int transY;
    protected static final Stroke defaultStroke;
    protected static final Composite defaultComposite;
    private static final Font defaultFont;
    public Paint paint;
    public Stroke stroke;
    public Composite composite;
    protected Font font;
    protected FontMetrics fontMetrics;
    public int renderHint;
    public int antialiasHint;
    public int textAntialiasHint;
    protected int fractionalMetricsHint;
    public int lcdTextContrast;
    private static int lcdTextContrastDefaultValue;
    private int interpolationHint;
    public int strokeHint;
    public int interpolationType;
    public RenderingHints hints;
    public Region constrainClip;
    public int constrainX;
    public int constrainY;
    public Region clipRegion;
    public Shape usrClip;
    protected Region devClip;
    private final int devScale;
    private int resolutionVariantHint;
    private boolean validFontInfo;
    private FontInfo fontInfo;
    private FontInfo glyphVectorFontInfo;
    private FontRenderContext glyphVectorFRC;
    private static final int slowTextTransformMask = 120;
    protected static ValidatePipe invalidpipe;
    private static final double[] IDENT_MATRIX;
    private static final AffineTransform IDENT_ATX;
    private static final int MINALLOCATED = 8;
    private static final int TEXTARRSIZE = 17;
    private static double[][] textTxArr;
    private static AffineTransform[] textAtArr;
    static final int NON_UNIFORM_SCALE_MASK = 36;
    public static final double MinPenSizeAA;
    public static final double MinPenSizeAASquared;
    public static final double MinPenSizeSquared = 1.000000001;
    static final int NON_RECTILINEAR_TRANSFORM_MASK = 48;
    Blit lastCAblit;
    Composite lastCAcomp;
    private FontRenderContext cachedFRC;
    
    public SunGraphics2D(final SurfaceData surfaceData, final Color foregroundColor, final Color backgroundColor, final Font font) {
        this.surfaceData = surfaceData;
        this.foregroundColor = foregroundColor;
        this.backgroundColor = backgroundColor;
        this.transform = new AffineTransform();
        this.stroke = SunGraphics2D.defaultStroke;
        this.composite = SunGraphics2D.defaultComposite;
        this.paint = this.foregroundColor;
        this.imageComp = CompositeType.SrcOverNoEa;
        this.renderHint = 0;
        this.antialiasHint = 1;
        this.textAntialiasHint = 0;
        this.fractionalMetricsHint = 1;
        this.lcdTextContrast = SunGraphics2D.lcdTextContrastDefaultValue;
        this.interpolationHint = -1;
        this.strokeHint = 0;
        this.resolutionVariantHint = 0;
        this.interpolationType = 1;
        this.validateColor();
        this.devScale = surfaceData.getDefaultScale();
        if (this.devScale != 1) {
            this.transform.setToScale(this.devScale, this.devScale);
            this.invalidateTransform();
        }
        this.font = font;
        if (this.font == null) {
            this.font = SunGraphics2D.defaultFont;
        }
        this.setDevClip(surfaceData.getBounds());
        this.invalidatePipe();
    }
    
    @Override
    protected Object clone() {
        try {
            final SunGraphics2D sunGraphics2D = (SunGraphics2D)super.clone();
            sunGraphics2D.transform = new AffineTransform(this.transform);
            if (this.hints != null) {
                sunGraphics2D.hints = (RenderingHints)this.hints.clone();
            }
            if (this.fontInfo != null) {
                if (this.validFontInfo) {
                    sunGraphics2D.fontInfo = (FontInfo)this.fontInfo.clone();
                }
                else {
                    sunGraphics2D.fontInfo = null;
                }
            }
            if (this.glyphVectorFontInfo != null) {
                sunGraphics2D.glyphVectorFontInfo = (FontInfo)this.glyphVectorFontInfo.clone();
                sunGraphics2D.glyphVectorFRC = this.glyphVectorFRC;
            }
            return sunGraphics2D;
        }
        catch (final CloneNotSupportedException ex) {
            return null;
        }
    }
    
    @Override
    public Graphics create() {
        return (Graphics)this.clone();
    }
    
    public void setDevClip(final int n, final int n2, final int n3, final int n4) {
        final Region constrainClip = this.constrainClip;
        if (constrainClip == null) {
            this.devClip = Region.getInstanceXYWH(n, n2, n3, n4);
        }
        else {
            this.devClip = constrainClip.getIntersectionXYWH(n, n2, n3, n4);
        }
        this.validateCompClip();
    }
    
    public void setDevClip(final Rectangle rectangle) {
        this.setDevClip(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
    }
    
    public void constrain(int n, int n2, int dimAdd, int dimAdd2, Region region) {
        if ((n | n2) != 0x0) {
            this.translate(n, n2);
        }
        if (this.transformState > 3) {
            this.clipRect(0, 0, dimAdd, dimAdd2);
            return;
        }
        final double scaleX = this.transform.getScaleX();
        final double scaleY = this.transform.getScaleY();
        final int constrainX = (int)this.transform.getTranslateX();
        this.constrainX = constrainX;
        n = constrainX;
        final int constrainY = (int)this.transform.getTranslateY();
        this.constrainY = constrainY;
        n2 = constrainY;
        dimAdd = Region.dimAdd(n, Region.clipScale(dimAdd, scaleX));
        dimAdd2 = Region.dimAdd(n2, Region.clipScale(dimAdd2, scaleY));
        final Region constrainClip = this.constrainClip;
        Region constrainClip2;
        if (constrainClip == null) {
            constrainClip2 = Region.getInstanceXYXY(n, n2, dimAdd, dimAdd2);
        }
        else {
            constrainClip2 = constrainClip.getIntersectionXYXY(n, n2, dimAdd, dimAdd2);
        }
        if (region != null) {
            region = region.getScaledRegion(scaleX, scaleY);
            region = region.getTranslatedRegion(n, n2);
            constrainClip2 = constrainClip2.getIntersection(region);
        }
        if (constrainClip2 == this.constrainClip) {
            return;
        }
        this.constrainClip = constrainClip2;
        if (!this.devClip.isInsideQuickCheck(constrainClip2)) {
            this.devClip = this.devClip.getIntersection(constrainClip2);
            this.validateCompClip();
        }
    }
    
    @Override
    public void constrain(final int n, final int n2, final int n3, final int n4) {
        this.constrain(n, n2, n3, n4, null);
    }
    
    protected void invalidatePipe() {
        this.drawpipe = SunGraphics2D.invalidpipe;
        this.fillpipe = SunGraphics2D.invalidpipe;
        this.shapepipe = SunGraphics2D.invalidpipe;
        this.textpipe = SunGraphics2D.invalidpipe;
        this.imagepipe = SunGraphics2D.invalidpipe;
        this.loops = null;
    }
    
    public void validatePipe() {
        if (!this.surfaceData.isValid()) {
            throw new InvalidPipeException("attempt to validate Pipe with invalid SurfaceData");
        }
        this.surfaceData.validatePipe(this);
    }
    
    Shape intersectShapes(final Shape shape, final Shape shape2, final boolean b, final boolean b2) {
        if (shape instanceof Rectangle && shape2 instanceof Rectangle) {
            return ((Rectangle)shape).intersection((Rectangle)shape2);
        }
        if (shape instanceof Rectangle2D) {
            return this.intersectRectShape((Rectangle2D)shape, shape2, b, b2);
        }
        if (shape2 instanceof Rectangle2D) {
            return this.intersectRectShape((Rectangle2D)shape2, shape, b2, b);
        }
        return this.intersectByArea(shape, shape2, b, b2);
    }
    
    Shape intersectRectShape(final Rectangle2D rectangle2D, Shape cloneShape, final boolean b, final boolean b2) {
        if (cloneShape instanceof Rectangle2D) {
            final Rectangle2D rectangle2D2 = (Rectangle2D)cloneShape;
            Rectangle2D rectangle2D3;
            if (!b) {
                rectangle2D3 = rectangle2D;
            }
            else if (!b2) {
                rectangle2D3 = rectangle2D2;
            }
            else {
                rectangle2D3 = new Rectangle2D.Float();
            }
            final double max = Math.max(rectangle2D.getX(), rectangle2D2.getX());
            final double min = Math.min(rectangle2D.getX() + rectangle2D.getWidth(), rectangle2D2.getX() + rectangle2D2.getWidth());
            final double max2 = Math.max(rectangle2D.getY(), rectangle2D2.getY());
            final double min2 = Math.min(rectangle2D.getY() + rectangle2D.getHeight(), rectangle2D2.getY() + rectangle2D2.getHeight());
            if (min - max < 0.0 || min2 - max2 < 0.0) {
                rectangle2D3.setFrameFromDiagonal(0.0, 0.0, 0.0, 0.0);
            }
            else {
                rectangle2D3.setFrameFromDiagonal(max, max2, min, min2);
            }
            return rectangle2D3;
        }
        if (rectangle2D.contains(cloneShape.getBounds2D())) {
            if (b2) {
                cloneShape = cloneShape(cloneShape);
            }
            return cloneShape;
        }
        return this.intersectByArea(rectangle2D, cloneShape, b, b2);
    }
    
    protected static Shape cloneShape(final Shape shape) {
        return new GeneralPath(shape);
    }
    
    Shape intersectByArea(final Shape shape, Shape shape2, final boolean b, final boolean b2) {
        Area area;
        if (!b && shape instanceof Area) {
            area = (Area)shape;
        }
        else if (!b2 && shape2 instanceof Area) {
            area = (Area)shape2;
            shape2 = shape;
        }
        else {
            area = new Area(shape);
        }
        Shape shape3;
        if (shape2 instanceof Area) {
            shape3 = shape2;
        }
        else {
            shape3 = new Area(shape2);
        }
        area.intersect((Area)shape3);
        if (area.isRectangular()) {
            return area.getBounds();
        }
        return area;
    }
    
    public Region getCompClip() {
        if (!this.surfaceData.isValid()) {
            this.revalidateAll();
        }
        return this.clipRegion;
    }
    
    @Override
    public Font getFont() {
        if (this.font == null) {
            this.font = SunGraphics2D.defaultFont;
        }
        return this.font;
    }
    
    public FontInfo checkFontInfo(FontInfo fontInfo, final Font font, final FontRenderContext fontRenderContext) {
        if (fontInfo == null) {
            fontInfo = new FontInfo();
        }
        final float size2D = font.getSize2D();
        AffineTransform transform = null;
        AffineTransform affineTransform;
        if (font.isTransformed()) {
            transform = font.getTransform();
            transform.scale(size2D, size2D);
            transform.getType();
            fontInfo.originX = (float)transform.getTranslateX();
            fontInfo.originY = (float)transform.getTranslateY();
            transform.translate(-fontInfo.originX, -fontInfo.originY);
            if (this.transformState >= 3) {
                this.transform.getMatrix(fontInfo.devTx = new double[4]);
                affineTransform = new AffineTransform(fontInfo.devTx);
                transform.preConcatenate(affineTransform);
            }
            else {
                fontInfo.devTx = SunGraphics2D.IDENT_MATRIX;
                affineTransform = SunGraphics2D.IDENT_ATX;
            }
            transform.getMatrix(fontInfo.glyphTx = new double[4]);
            final double shearX = transform.getShearX();
            double n = transform.getScaleY();
            if (shearX != 0.0) {
                n = Math.sqrt(shearX * shearX + n * n);
            }
            fontInfo.pixelHeight = (int)(Math.abs(n) + 0.5);
        }
        else {
            final FontInfo fontInfo2 = fontInfo;
            final FontInfo fontInfo3 = fontInfo;
            final float n2 = 0.0f;
            fontInfo3.originY = n2;
            fontInfo2.originX = n2;
            if (this.transformState >= 3) {
                this.transform.getMatrix(fontInfo.devTx = new double[4]);
                affineTransform = new AffineTransform(fontInfo.devTx);
                fontInfo.glyphTx = new double[4];
                for (int i = 0; i < 4; ++i) {
                    fontInfo.glyphTx[i] = fontInfo.devTx[i] * size2D;
                }
                transform = new AffineTransform(fontInfo.glyphTx);
                final double shearX2 = this.transform.getShearX();
                double n3 = this.transform.getScaleY();
                if (shearX2 != 0.0) {
                    n3 = Math.sqrt(shearX2 * shearX2 + n3 * n3);
                }
                fontInfo.pixelHeight = (int)(Math.abs(n3 * size2D) + 0.5);
            }
            else {
                final int pixelHeight = (int)size2D;
                if (size2D == pixelHeight && pixelHeight >= 8 && pixelHeight < 17) {
                    fontInfo.glyphTx = SunGraphics2D.textTxArr[pixelHeight];
                    transform = SunGraphics2D.textAtArr[pixelHeight];
                    fontInfo.pixelHeight = pixelHeight;
                }
                else {
                    fontInfo.pixelHeight = (int)(size2D + 0.5);
                }
                if (transform == null) {
                    fontInfo.glyphTx = new double[] { size2D, 0.0, 0.0, size2D };
                    transform = new AffineTransform(fontInfo.glyphTx);
                }
                fontInfo.devTx = SunGraphics2D.IDENT_MATRIX;
                affineTransform = SunGraphics2D.IDENT_ATX;
            }
        }
        fontInfo.font2D = FontUtilities.getFont2D(font);
        int fractionalMetricsHint = this.fractionalMetricsHint;
        if (fractionalMetricsHint == 0) {
            fractionalMetricsHint = 1;
        }
        fontInfo.lcdSubPixPos = false;
        int aaHint;
        if (fontRenderContext == null) {
            aaHint = this.textAntialiasHint;
        }
        else {
            aaHint = ((SunHints.Value)fontRenderContext.getAntiAliasingHint()).getIndex();
        }
        if (aaHint == 0) {
            if (this.antialiasHint == 2) {
                aaHint = 2;
            }
            else {
                aaHint = 1;
            }
        }
        else if (aaHint == 3) {
            if (fontInfo.font2D.useAAForPtSize(fontInfo.pixelHeight)) {
                aaHint = 2;
            }
            else {
                aaHint = 1;
            }
        }
        else if (aaHint >= 4) {
            if (!this.surfaceData.canRenderLCDText(this)) {
                aaHint = 2;
            }
            else {
                fontInfo.lcdRGBOrder = true;
                if (aaHint == 5) {
                    aaHint = 4;
                    fontInfo.lcdRGBOrder = false;
                }
                else if (aaHint == 7) {
                    aaHint = 6;
                    fontInfo.lcdRGBOrder = false;
                }
                fontInfo.lcdSubPixPos = (fractionalMetricsHint == 2 && aaHint == 4);
            }
        }
        if (FontUtilities.isMacOSX14 && aaHint == 1) {
            aaHint = 2;
        }
        fontInfo.aaHint = aaHint;
        fontInfo.fontStrike = fontInfo.font2D.getStrike(font, affineTransform, transform, aaHint, fractionalMetricsHint);
        return fontInfo;
    }
    
    public static boolean isRotated(final double[] array) {
        return array[0] != array[3] || array[1] != 0.0 || array[2] != 0.0 || array[0] <= 0.0;
    }
    
    @Override
    public void setFont(final Font font) {
        if (font != null && font != this.font) {
            if (this.textAntialiasHint == 3 && this.textpipe != SunGraphics2D.invalidpipe && (this.transformState > 2 || font.isTransformed() || this.fontInfo == null || this.fontInfo.aaHint == 2 != FontUtilities.getFont2D(font).useAAForPtSize(font.getSize()))) {
                this.textpipe = SunGraphics2D.invalidpipe;
            }
            this.font = font;
            this.fontMetrics = null;
            this.validFontInfo = false;
        }
    }
    
    public FontInfo getFontInfo() {
        if (!this.validFontInfo) {
            this.fontInfo = this.checkFontInfo(this.fontInfo, this.font, null);
            this.validFontInfo = true;
        }
        return this.fontInfo;
    }
    
    public FontInfo getGVFontInfo(final Font font, final FontRenderContext glyphVectorFRC) {
        if (this.glyphVectorFontInfo != null && this.glyphVectorFontInfo.font == font && this.glyphVectorFRC == glyphVectorFRC) {
            return this.glyphVectorFontInfo;
        }
        this.glyphVectorFRC = glyphVectorFRC;
        return this.glyphVectorFontInfo = this.checkFontInfo(this.glyphVectorFontInfo, font, glyphVectorFRC);
    }
    
    @Override
    public FontMetrics getFontMetrics() {
        if (this.fontMetrics != null) {
            return this.fontMetrics;
        }
        return this.fontMetrics = FontDesignMetrics.getMetrics(this.font, this.getFontRenderContext());
    }
    
    @Override
    public FontMetrics getFontMetrics(final Font font) {
        if (this.fontMetrics != null && font == this.font) {
            return this.fontMetrics;
        }
        final FontDesignMetrics metrics = FontDesignMetrics.getMetrics(font, this.getFontRenderContext());
        if (this.font == font) {
            this.fontMetrics = metrics;
        }
        return metrics;
    }
    
    @Override
    public boolean hit(Rectangle rectangle, Shape shape, final boolean b) {
        if (b) {
            shape = this.stroke.createStrokedShape(shape);
        }
        shape = this.transformShape(shape);
        if ((this.constrainX | this.constrainY) != 0x0) {
            rectangle = new Rectangle(rectangle);
            rectangle.translate(this.constrainX, this.constrainY);
        }
        return shape.intersects(rectangle);
    }
    
    public ColorModel getDeviceColorModel() {
        return this.surfaceData.getColorModel();
    }
    
    @Override
    public GraphicsConfiguration getDeviceConfiguration() {
        return this.surfaceData.getDeviceConfiguration();
    }
    
    public final SurfaceData getSurfaceData() {
        return this.surfaceData;
    }
    
    @Override
    public void setComposite(final Composite composite) {
        if (this.composite == composite) {
            return;
        }
        CompositeType imageComp;
        int compositeState;
        if (composite instanceof AlphaComposite) {
            imageComp = CompositeType.forAlphaComposite((AlphaComposite)composite);
            if (imageComp == CompositeType.SrcOverNoEa) {
                if (this.paintState == 0 || (this.paintState > 1 && this.paint.getTransparency() == 1)) {
                    compositeState = 0;
                }
                else {
                    compositeState = 1;
                }
            }
            else if (imageComp == CompositeType.SrcNoEa || imageComp == CompositeType.Src || imageComp == CompositeType.Clear) {
                compositeState = 0;
            }
            else if (this.surfaceData.getTransparency() == 1 && imageComp == CompositeType.SrcIn) {
                compositeState = 0;
            }
            else {
                compositeState = 1;
            }
        }
        else if (composite instanceof XORComposite) {
            compositeState = 2;
            imageComp = CompositeType.Xor;
        }
        else {
            if (composite == null) {
                throw new IllegalArgumentException("null Composite");
            }
            this.surfaceData.checkCustomComposite();
            compositeState = 3;
            imageComp = CompositeType.General;
        }
        if (this.compositeState != compositeState || this.imageComp != imageComp) {
            this.compositeState = compositeState;
            this.imageComp = imageComp;
            this.invalidatePipe();
            this.validFontInfo = false;
        }
        this.composite = composite;
        if (this.paintState <= 1) {
            this.validateColor();
        }
    }
    
    @Override
    public void setPaint(final Paint paint) {
        if (paint instanceof Color) {
            this.setColor((Color)paint);
            return;
        }
        if (paint == null || this.paint == paint) {
            return;
        }
        this.paint = paint;
        if (this.imageComp == CompositeType.SrcOverNoEa) {
            if (paint.getTransparency() == 1) {
                if (this.compositeState != 0) {
                    this.compositeState = 0;
                }
            }
            else if (this.compositeState == 0) {
                this.compositeState = 1;
            }
        }
        final Class<? extends Paint> class1 = paint.getClass();
        if (class1 == GradientPaint.class) {
            this.paintState = 2;
        }
        else if (class1 == LinearGradientPaint.class) {
            this.paintState = 3;
        }
        else if (class1 == RadialGradientPaint.class) {
            this.paintState = 4;
        }
        else if (class1 == TexturePaint.class) {
            this.paintState = 5;
        }
        else {
            this.paintState = 6;
        }
        this.validFontInfo = false;
        this.invalidatePipe();
    }
    
    private void validateBasicStroke(final BasicStroke basicStroke) {
        final boolean b = this.antialiasHint == 2;
        if (this.transformState < 3) {
            if (b) {
                if (basicStroke.getLineWidth() <= SunGraphics2D.MinPenSizeAA) {
                    if (basicStroke.getDashArray() == null) {
                        this.strokeState = 0;
                    }
                    else {
                        this.strokeState = 1;
                    }
                }
                else {
                    this.strokeState = 2;
                }
            }
            else if (basicStroke == SunGraphics2D.defaultStroke) {
                this.strokeState = 0;
            }
            else if (basicStroke.getLineWidth() <= 1.0f) {
                if (basicStroke.getDashArray() == null) {
                    this.strokeState = 0;
                }
                else {
                    this.strokeState = 1;
                }
            }
            else {
                this.strokeState = 2;
            }
        }
        else {
            double abs;
            if ((this.transform.getType() & 0x24) == 0x0) {
                abs = Math.abs(this.transform.getDeterminant());
            }
            else {
                final double scaleX = this.transform.getScaleX();
                final double shearX = this.transform.getShearX();
                final double shearY = this.transform.getShearY();
                final double scaleY = this.transform.getScaleY();
                final double n = scaleX * scaleX + shearY * shearY;
                final double n2 = 2.0 * (scaleX * shearX + shearY * scaleY);
                final double n3 = shearX * shearX + scaleY * scaleY;
                abs = (n + n3 + Math.sqrt(n2 * n2 + (n - n3) * (n - n3))) / 2.0;
            }
            if (basicStroke != SunGraphics2D.defaultStroke) {
                abs *= basicStroke.getLineWidth() * basicStroke.getLineWidth();
            }
            if (abs <= (b ? SunGraphics2D.MinPenSizeAASquared : 1.000000001)) {
                if (basicStroke.getDashArray() == null) {
                    this.strokeState = 0;
                }
                else {
                    this.strokeState = 1;
                }
            }
            else {
                this.strokeState = 2;
            }
        }
    }
    
    @Override
    public void setStroke(final Stroke stroke) {
        if (stroke == null) {
            throw new IllegalArgumentException("null Stroke");
        }
        final int strokeState = this.strokeState;
        this.stroke = stroke;
        if (stroke instanceof BasicStroke) {
            this.validateBasicStroke((BasicStroke)stroke);
        }
        else {
            this.strokeState = 3;
        }
        if (this.strokeState != strokeState) {
            this.invalidatePipe();
        }
    }
    
    @Override
    public void setRenderingHint(final RenderingHints.Key key, final Object o) {
        if (!key.isCompatibleValue(o)) {
            throw new IllegalArgumentException(o + " is not compatible with " + key);
        }
        if (key instanceof SunHints.Key) {
            boolean b = false;
            boolean b2 = true;
            final SunHints.Key key2 = (SunHints.Key)key;
            int n;
            if (key2 == SunHints.KEY_TEXT_ANTIALIAS_LCD_CONTRAST) {
                n = (int)o;
            }
            else {
                n = ((SunHints.Value)o).getIndex();
            }
            int n2 = 0;
            switch (key2.getIndex()) {
                case 0: {
                    n2 = ((this.renderHint != n) ? 1 : 0);
                    if (n2 == 0) {
                        break;
                    }
                    this.renderHint = n;
                    if (this.interpolationHint == -1) {
                        this.interpolationType = ((n == 2) ? 2 : 1);
                        break;
                    }
                    break;
                }
                case 1: {
                    n2 = ((this.antialiasHint != n) ? 1 : 0);
                    this.antialiasHint = n;
                    if (n2 == 0) {
                        break;
                    }
                    b = (this.textAntialiasHint == 0);
                    if (this.strokeState != 3) {
                        this.validateBasicStroke((BasicStroke)this.stroke);
                        break;
                    }
                    break;
                }
                case 2: {
                    n2 = ((b = (this.textAntialiasHint != n)) ? 1 : 0);
                    this.textAntialiasHint = n;
                    break;
                }
                case 3: {
                    n2 = ((b = (this.fractionalMetricsHint != n)) ? 1 : 0);
                    this.fractionalMetricsHint = n;
                    break;
                }
                case 100: {
                    n2 = 0;
                    this.lcdTextContrast = n;
                    break;
                }
                case 5: {
                    int interpolationType = 0;
                    switch (this.interpolationHint = n) {
                        case 2: {
                            interpolationType = 3;
                            break;
                        }
                        case 1: {
                            interpolationType = 2;
                            break;
                        }
                        default: {
                            interpolationType = 1;
                            break;
                        }
                    }
                    n2 = ((this.interpolationType != interpolationType) ? 1 : 0);
                    this.interpolationType = interpolationType;
                    break;
                }
                case 8: {
                    n2 = ((this.strokeHint != n) ? 1 : 0);
                    this.strokeHint = n;
                    break;
                }
                case 9: {
                    n2 = ((this.resolutionVariantHint != n) ? 1 : 0);
                    this.resolutionVariantHint = n;
                    break;
                }
                default: {
                    b2 = false;
                    n2 = 0;
                    break;
                }
            }
            if (b2) {
                if (n2 != 0) {
                    this.invalidatePipe();
                    if (b) {
                        this.fontMetrics = null;
                        this.cachedFRC = null;
                        this.validFontInfo = false;
                        this.glyphVectorFontInfo = null;
                    }
                }
                if (this.hints != null) {
                    this.hints.put(key, o);
                }
                return;
            }
        }
        if (this.hints == null) {
            this.hints = this.makeHints(null);
        }
        this.hints.put(key, o);
    }
    
    @Override
    public Object getRenderingHint(final RenderingHints.Key key) {
        if (this.hints != null) {
            return this.hints.get(key);
        }
        if (!(key instanceof SunHints.Key)) {
            return null;
        }
        switch (((SunHints.Key)key).getIndex()) {
            case 0: {
                return SunHints.Value.get(0, this.renderHint);
            }
            case 1: {
                return SunHints.Value.get(1, this.antialiasHint);
            }
            case 2: {
                return SunHints.Value.get(2, this.textAntialiasHint);
            }
            case 3: {
                return SunHints.Value.get(3, this.fractionalMetricsHint);
            }
            case 100: {
                return new Integer(this.lcdTextContrast);
            }
            case 5: {
                switch (this.interpolationHint) {
                    case 0: {
                        return SunHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;
                    }
                    case 1: {
                        return SunHints.VALUE_INTERPOLATION_BILINEAR;
                    }
                    case 2: {
                        return SunHints.VALUE_INTERPOLATION_BICUBIC;
                    }
                    default: {
                        return null;
                    }
                }
                break;
            }
            case 8: {
                return SunHints.Value.get(8, this.strokeHint);
            }
            case 9: {
                return SunHints.Value.get(9, this.resolutionVariantHint);
            }
            default: {
                return null;
            }
        }
    }
    
    @Override
    public void setRenderingHints(final Map<?, ?> map) {
        this.hints = null;
        this.renderHint = 0;
        this.antialiasHint = 1;
        this.textAntialiasHint = 0;
        this.fractionalMetricsHint = 1;
        this.lcdTextContrast = SunGraphics2D.lcdTextContrastDefaultValue;
        this.interpolationHint = -1;
        this.interpolationType = 1;
        boolean b = false;
        for (final Object next : map.keySet()) {
            if (next == SunHints.KEY_RENDERING || next == SunHints.KEY_ANTIALIASING || next == SunHints.KEY_TEXT_ANTIALIASING || next == SunHints.KEY_FRACTIONALMETRICS || next == SunHints.KEY_TEXT_ANTIALIAS_LCD_CONTRAST || next == SunHints.KEY_STROKE_CONTROL || next == SunHints.KEY_INTERPOLATION) {
                this.setRenderingHint((RenderingHints.Key)next, map.get(next));
            }
            else {
                b = true;
            }
        }
        if (b) {
            this.hints = this.makeHints(map);
        }
        this.invalidatePipe();
    }
    
    @Override
    public void addRenderingHints(final Map<?, ?> map) {
        boolean b = false;
        for (final Object next : map.keySet()) {
            if (next == SunHints.KEY_RENDERING || next == SunHints.KEY_ANTIALIASING || next == SunHints.KEY_TEXT_ANTIALIASING || next == SunHints.KEY_FRACTIONALMETRICS || next == SunHints.KEY_TEXT_ANTIALIAS_LCD_CONTRAST || next == SunHints.KEY_STROKE_CONTROL || next == SunHints.KEY_INTERPOLATION) {
                this.setRenderingHint((RenderingHints.Key)next, map.get(next));
            }
            else {
                b = true;
            }
        }
        if (b) {
            if (this.hints == null) {
                this.hints = this.makeHints(map);
            }
            else {
                this.hints.putAll(map);
            }
        }
    }
    
    @Override
    public RenderingHints getRenderingHints() {
        if (this.hints == null) {
            return this.makeHints(null);
        }
        return (RenderingHints)this.hints.clone();
    }
    
    RenderingHints makeHints(final Map map) {
        final RenderingHints renderingHints = new RenderingHints(map);
        renderingHints.put(SunHints.KEY_RENDERING, SunHints.Value.get(0, this.renderHint));
        renderingHints.put(SunHints.KEY_ANTIALIASING, SunHints.Value.get(1, this.antialiasHint));
        renderingHints.put(SunHints.KEY_TEXT_ANTIALIASING, SunHints.Value.get(2, this.textAntialiasHint));
        renderingHints.put(SunHints.KEY_FRACTIONALMETRICS, SunHints.Value.get(3, this.fractionalMetricsHint));
        renderingHints.put(SunHints.KEY_TEXT_ANTIALIAS_LCD_CONTRAST, this.lcdTextContrast);
        Object o = null;
        switch (this.interpolationHint) {
            case 0: {
                o = SunHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;
                break;
            }
            case 1: {
                o = SunHints.VALUE_INTERPOLATION_BILINEAR;
                break;
            }
            case 2: {
                o = SunHints.VALUE_INTERPOLATION_BICUBIC;
                break;
            }
            default: {
                o = null;
                break;
            }
        }
        if (o != null) {
            renderingHints.put(SunHints.KEY_INTERPOLATION, o);
        }
        renderingHints.put(SunHints.KEY_STROKE_CONTROL, SunHints.Value.get(8, this.strokeHint));
        return renderingHints;
    }
    
    @Override
    public void translate(final double n, final double n2) {
        this.transform.translate(n, n2);
        this.invalidateTransform();
    }
    
    @Override
    public void rotate(final double n) {
        this.transform.rotate(n);
        this.invalidateTransform();
    }
    
    @Override
    public void rotate(final double n, final double n2, final double n3) {
        this.transform.rotate(n, n2, n3);
        this.invalidateTransform();
    }
    
    @Override
    public void scale(final double n, final double n2) {
        this.transform.scale(n, n2);
        this.invalidateTransform();
    }
    
    @Override
    public void shear(final double n, final double n2) {
        this.transform.shear(n, n2);
        this.invalidateTransform();
    }
    
    @Override
    public void transform(final AffineTransform affineTransform) {
        this.transform.concatenate(affineTransform);
        this.invalidateTransform();
    }
    
    @Override
    public void translate(final int n, final int n2) {
        this.transform.translate(n, n2);
        if (this.transformState <= 1) {
            this.transX += n;
            this.transY += n2;
            this.transformState = (((this.transX | this.transY) != 0x0) ? 1 : 0);
        }
        else {
            this.invalidateTransform();
        }
    }
    
    @Override
    public void setTransform(final AffineTransform transform) {
        if ((this.constrainX | this.constrainY) == 0x0 && this.devScale == 1) {
            this.transform.setTransform(transform);
        }
        else {
            this.transform.setTransform(this.devScale, 0.0, 0.0, this.devScale, this.constrainX, this.constrainY);
            this.transform.concatenate(transform);
        }
        this.invalidateTransform();
    }
    
    protected void invalidateTransform() {
        final int type = this.transform.getType();
        final int transformState = this.transformState;
        if (type == 0) {
            this.transformState = 0;
            final int n = 0;
            this.transY = n;
            this.transX = n;
        }
        else if (type == 1) {
            final double translateX = this.transform.getTranslateX();
            final double translateY = this.transform.getTranslateY();
            this.transX = (int)Math.floor(translateX + 0.5);
            this.transY = (int)Math.floor(translateY + 0.5);
            if (translateX == this.transX && translateY == this.transY) {
                this.transformState = 1;
            }
            else {
                this.transformState = 2;
            }
        }
        else if ((type & 0x78) == 0x0) {
            this.transformState = 3;
            final int n2 = 0;
            this.transY = n2;
            this.transX = n2;
        }
        else {
            this.transformState = 4;
            final int n3 = 0;
            this.transY = n3;
            this.transX = n3;
        }
        if (this.transformState >= 3 || transformState >= 3) {
            this.cachedFRC = null;
            this.validFontInfo = false;
            this.fontMetrics = null;
            this.glyphVectorFontInfo = null;
            if (this.transformState != transformState) {
                this.invalidatePipe();
            }
        }
        if (this.strokeState != 3) {
            this.validateBasicStroke((BasicStroke)this.stroke);
        }
    }
    
    @Override
    public AffineTransform getTransform() {
        if ((this.constrainX | this.constrainY) == 0x0 && this.devScale == 1) {
            return new AffineTransform(this.transform);
        }
        final double n = 1.0 / this.devScale;
        final AffineTransform affineTransform = new AffineTransform(n, 0.0, 0.0, n, -this.constrainX * n, -this.constrainY * n);
        affineTransform.concatenate(this.transform);
        return affineTransform;
    }
    
    public AffineTransform cloneTransform() {
        return new AffineTransform(this.transform);
    }
    
    @Override
    public Paint getPaint() {
        return this.paint;
    }
    
    @Override
    public Composite getComposite() {
        return this.composite;
    }
    
    @Override
    public Color getColor() {
        return this.foregroundColor;
    }
    
    final void validateColor() {
        int rgb;
        if (this.imageComp == CompositeType.Clear) {
            rgb = 0;
        }
        else {
            rgb = this.foregroundColor.getRGB();
            if (this.compositeState <= 1 && this.imageComp != CompositeType.SrcNoEa && this.imageComp != CompositeType.SrcOverNoEa) {
                rgb = ((rgb & 0xFFFFFF) | Math.round(((AlphaComposite)this.composite).getAlpha() * (rgb >>> 24)) << 24);
            }
        }
        this.eargb = rgb;
        this.pixel = this.surfaceData.pixelFor(rgb);
    }
    
    @Override
    public void setColor(final Color color) {
        if (color == null || color == this.paint) {
            return;
        }
        this.foregroundColor = color;
        this.paint = color;
        this.validateColor();
        if (this.eargb >> 24 == -1) {
            if (this.paintState == 0) {
                return;
            }
            this.paintState = 0;
            if (this.imageComp == CompositeType.SrcOverNoEa) {
                this.compositeState = 0;
            }
        }
        else {
            if (this.paintState == 1) {
                return;
            }
            this.paintState = 1;
            if (this.imageComp == CompositeType.SrcOverNoEa) {
                this.compositeState = 1;
            }
        }
        this.validFontInfo = false;
        this.invalidatePipe();
    }
    
    @Override
    public void setBackground(final Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }
    
    @Override
    public Color getBackground() {
        return this.backgroundColor;
    }
    
    @Override
    public Stroke getStroke() {
        return this.stroke;
    }
    
    @Override
    public Rectangle getClipBounds() {
        if (this.clipState == 0) {
            return null;
        }
        return this.getClipBounds(new Rectangle());
    }
    
    @Override
    public Rectangle getClipBounds(final Rectangle rectangle) {
        if (this.clipState != 0) {
            if (this.transformState <= 1) {
                if (this.usrClip instanceof Rectangle) {
                    rectangle.setBounds((Rectangle)this.usrClip);
                }
                else {
                    rectangle.setFrame(this.usrClip.getBounds2D());
                }
                rectangle.translate(-this.transX, -this.transY);
            }
            else {
                rectangle.setFrame(this.getClip().getBounds2D());
            }
        }
        else if (rectangle == null) {
            throw new NullPointerException("null rectangle parameter");
        }
        return rectangle;
    }
    
    @Override
    public boolean hitClip(int n, int n2, int n3, int n4) {
        if (n3 <= 0 || n4 <= 0) {
            return false;
        }
        if (this.transformState > 1) {
            final double[] array = { n, n2, n + n3, n2, n, n2 + n4, n + n3, n2 + n4 };
            this.transform.transform(array, 0, array, 0, 4);
            n = (int)Math.floor(Math.min(Math.min(array[0], array[2]), Math.min(array[4], array[6])));
            n2 = (int)Math.floor(Math.min(Math.min(array[1], array[3]), Math.min(array[5], array[7])));
            n3 = (int)Math.ceil(Math.max(Math.max(array[0], array[2]), Math.max(array[4], array[6])));
            n4 = (int)Math.ceil(Math.max(Math.max(array[1], array[3]), Math.max(array[5], array[7])));
        }
        else {
            n += this.transX;
            n2 += this.transY;
            n3 += n;
            n4 += n2;
        }
        try {
            if (!this.getCompClip().intersectsQuickCheckXYXY(n, n2, n3, n4)) {
                return false;
            }
        }
        catch (final InvalidPipeException ex) {
            return false;
        }
        return true;
    }
    
    protected void validateCompClip() {
        final int clipState = this.clipState;
        if (this.usrClip == null) {
            this.clipState = 0;
            this.clipRegion = this.devClip;
        }
        else if (this.usrClip instanceof Rectangle2D) {
            this.clipState = 1;
            if (this.usrClip instanceof Rectangle) {
                this.clipRegion = this.devClip.getIntersection((Rectangle)this.usrClip);
            }
            else {
                this.clipRegion = this.devClip.getIntersection(this.usrClip.getBounds());
            }
        }
        else {
            final PathIterator pathIterator = this.usrClip.getPathIterator(null);
            final int[] array = new int[4];
            final ShapeSpanIterator fillSSI = LoopPipe.getFillSSI(this);
            try {
                fillSSI.setOutputArea(this.devClip);
                fillSSI.appendPath(pathIterator);
                fillSSI.getPathBox(array);
                final Region instance = Region.getInstance(array);
                instance.appendSpans(fillSSI);
                this.clipRegion = instance;
                this.clipState = (instance.isRectangular() ? 1 : 2);
            }
            finally {
                fillSSI.dispose();
            }
        }
        if (clipState != this.clipState && (this.clipState == 2 || clipState == 2)) {
            this.validFontInfo = false;
            this.invalidatePipe();
        }
    }
    
    protected Shape transformShape(final Shape shape) {
        if (shape == null) {
            return null;
        }
        if (this.transformState > 1) {
            return transformShape(this.transform, shape);
        }
        return transformShape(this.transX, this.transY, shape);
    }
    
    public Shape untransformShape(final Shape shape) {
        if (shape == null) {
            return null;
        }
        if (this.transformState > 1) {
            try {
                return transformShape(this.transform.createInverse(), shape);
            }
            catch (final NoninvertibleTransformException ex) {
                return null;
            }
        }
        return transformShape(-this.transX, -this.transY, shape);
    }
    
    protected static Shape transformShape(final int n, final int n2, final Shape shape) {
        if (shape == null) {
            return null;
        }
        if (shape instanceof Rectangle) {
            final Rectangle bounds = shape.getBounds();
            bounds.translate(n, n2);
            return bounds;
        }
        if (shape instanceof Rectangle2D) {
            final Rectangle2D rectangle2D = (Rectangle2D)shape;
            return new Rectangle2D.Double(rectangle2D.getX() + n, rectangle2D.getY() + n2, rectangle2D.getWidth(), rectangle2D.getHeight());
        }
        if (n == 0 && n2 == 0) {
            return cloneShape(shape);
        }
        return AffineTransform.getTranslateInstance(n, n2).createTransformedShape(shape);
    }
    
    protected static Shape transformShape(final AffineTransform affineTransform, final Shape shape) {
        if (shape == null) {
            return null;
        }
        if (shape instanceof Rectangle2D && (affineTransform.getType() & 0x30) == 0x0) {
            final Rectangle2D rectangle2D = (Rectangle2D)shape;
            final double[] array = { rectangle2D.getX(), rectangle2D.getY(), 0.0, 0.0 };
            array[2] = array[0] + rectangle2D.getWidth();
            array[3] = array[1] + rectangle2D.getHeight();
            affineTransform.transform(array, 0, array, 0, 2);
            fixRectangleOrientation(array, rectangle2D);
            return new Rectangle2D.Double(array[0], array[1], array[2] - array[0], array[3] - array[1]);
        }
        if (affineTransform.isIdentity()) {
            return cloneShape(shape);
        }
        return affineTransform.createTransformedShape(shape);
    }
    
    private static void fixRectangleOrientation(final double[] array, final Rectangle2D rectangle2D) {
        if (rectangle2D.getWidth() > 0.0 != array[2] - array[0] > 0.0) {
            final double n = array[0];
            array[0] = array[2];
            array[2] = n;
        }
        if (rectangle2D.getHeight() > 0.0 != array[3] - array[1] > 0.0) {
            final double n2 = array[1];
            array[1] = array[3];
            array[3] = n2;
        }
    }
    
    @Override
    public void clipRect(final int n, final int n2, final int n3, final int n4) {
        this.clip(new Rectangle(n, n2, n3, n4));
    }
    
    @Override
    public void setClip(final int n, final int n2, final int n3, final int n4) {
        this.setClip(new Rectangle(n, n2, n3, n4));
    }
    
    @Override
    public Shape getClip() {
        return this.untransformShape(this.usrClip);
    }
    
    @Override
    public void setClip(final Shape shape) {
        this.usrClip = this.transformShape(shape);
        this.validateCompClip();
    }
    
    @Override
    public void clip(Shape usrClip) {
        usrClip = this.transformShape(usrClip);
        if (this.usrClip != null) {
            usrClip = this.intersectShapes(this.usrClip, usrClip, true, true);
        }
        this.usrClip = usrClip;
        this.validateCompClip();
    }
    
    @Override
    public void setPaintMode() {
        this.setComposite(AlphaComposite.SrcOver);
    }
    
    @Override
    public void setXORMode(final Color color) {
        if (color == null) {
            throw new IllegalArgumentException("null XORColor");
        }
        this.setComposite(new XORComposite(color, this.surfaceData));
    }
    
    @Override
    public void copyArea(final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        try {
            this.doCopyArea(n, n2, n3, n4, n5, n6);
        }
        catch (final InvalidPipeException ex) {
            try {
                this.revalidateAll();
                this.doCopyArea(n, n2, n3, n4, n5, n6);
            }
            catch (final InvalidPipeException ex2) {}
        }
        finally {
            this.surfaceData.markDirty();
        }
    }
    
    private void doCopyArea(int n, int n2, int i, int j, int n3, int n4) {
        if (i <= 0 || j <= 0) {
            return;
        }
        final SurfaceData surfaceData = this.surfaceData;
        if (surfaceData.copyArea(this, n, n2, i, j, n3, n4)) {
            return;
        }
        if (this.transformState > 3) {
            throw new InternalError("transformed copyArea not implemented yet");
        }
        final Region compClip = this.getCompClip();
        final Composite composite = this.composite;
        if (this.lastCAcomp != composite) {
            final SurfaceType surfaceType = surfaceData.getSurfaceType();
            CompositeType compositeType = this.imageComp;
            if (CompositeType.SrcOverNoEa.equals(compositeType) && surfaceData.getTransparency() == 1) {
                compositeType = CompositeType.SrcNoEa;
            }
            this.lastCAblit = Blit.locate(surfaceType, compositeType, surfaceType);
            this.lastCAcomp = composite;
        }
        final double[] array = { n, n2, n + i, n2 + j, n + n3, n2 + n4 };
        this.transform.transform(array, 0, array, 0, 3);
        n = (int)Math.ceil(array[0] - 0.5);
        n2 = (int)Math.ceil(array[1] - 0.5);
        i = (int)Math.ceil(array[2] - 0.5) - n;
        j = (int)Math.ceil(array[3] - 0.5) - n2;
        n3 = (int)Math.ceil(array[4] - 0.5) - n;
        n4 = (int)Math.ceil(array[5] - 0.5) - n2;
        if (i < 0) {
            i *= -1;
            n -= i;
        }
        if (j < 0) {
            j *= -1;
            n2 -= j;
        }
        final Blit lastCAblit = this.lastCAblit;
        if (n4 == 0 && n3 > 0 && n3 < i) {
            while (i > 0) {
                final int min = Math.min(i, n3);
                i -= min;
                final int n5 = n + i;
                lastCAblit.Blit(surfaceData, surfaceData, composite, compClip, n5, n2, n5 + n3, n2 + n4, min, j);
            }
            return;
        }
        if (n4 > 0 && n4 < j && n3 > -i && n3 < i) {
            while (j > 0) {
                final int min2 = Math.min(j, n4);
                j -= min2;
                final int n6 = n2 + j;
                lastCAblit.Blit(surfaceData, surfaceData, composite, compClip, n, n6, n + n3, n6 + n4, i, min2);
            }
            return;
        }
        lastCAblit.Blit(surfaceData, surfaceData, composite, compClip, n, n2, n + n3, n2 + n4, i, j);
    }
    
    @Override
    public void drawLine(final int n, final int n2, final int n3, final int n4) {
        try {
            this.drawpipe.drawLine(this, n, n2, n3, n4);
        }
        catch (final InvalidPipeException ex) {
            try {
                this.revalidateAll();
                this.drawpipe.drawLine(this, n, n2, n3, n4);
            }
            catch (final InvalidPipeException ex2) {}
        }
        finally {
            this.surfaceData.markDirty();
        }
    }
    
    @Override
    public void drawRoundRect(final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        try {
            this.drawpipe.drawRoundRect(this, n, n2, n3, n4, n5, n6);
        }
        catch (final InvalidPipeException ex) {
            try {
                this.revalidateAll();
                this.drawpipe.drawRoundRect(this, n, n2, n3, n4, n5, n6);
            }
            catch (final InvalidPipeException ex2) {}
        }
        finally {
            this.surfaceData.markDirty();
        }
    }
    
    @Override
    public void fillRoundRect(final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        try {
            this.fillpipe.fillRoundRect(this, n, n2, n3, n4, n5, n6);
        }
        catch (final InvalidPipeException ex) {
            try {
                this.revalidateAll();
                this.fillpipe.fillRoundRect(this, n, n2, n3, n4, n5, n6);
            }
            catch (final InvalidPipeException ex2) {}
        }
        finally {
            this.surfaceData.markDirty();
        }
    }
    
    @Override
    public void drawOval(final int n, final int n2, final int n3, final int n4) {
        try {
            this.drawpipe.drawOval(this, n, n2, n3, n4);
        }
        catch (final InvalidPipeException ex) {
            try {
                this.revalidateAll();
                this.drawpipe.drawOval(this, n, n2, n3, n4);
            }
            catch (final InvalidPipeException ex2) {}
        }
        finally {
            this.surfaceData.markDirty();
        }
    }
    
    @Override
    public void fillOval(final int n, final int n2, final int n3, final int n4) {
        try {
            this.fillpipe.fillOval(this, n, n2, n3, n4);
        }
        catch (final InvalidPipeException ex) {
            try {
                this.revalidateAll();
                this.fillpipe.fillOval(this, n, n2, n3, n4);
            }
            catch (final InvalidPipeException ex2) {}
        }
        finally {
            this.surfaceData.markDirty();
        }
    }
    
    @Override
    public void drawArc(final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        try {
            this.drawpipe.drawArc(this, n, n2, n3, n4, n5, n6);
        }
        catch (final InvalidPipeException ex) {
            try {
                this.revalidateAll();
                this.drawpipe.drawArc(this, n, n2, n3, n4, n5, n6);
            }
            catch (final InvalidPipeException ex2) {}
        }
        finally {
            this.surfaceData.markDirty();
        }
    }
    
    @Override
    public void fillArc(final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        try {
            this.fillpipe.fillArc(this, n, n2, n3, n4, n5, n6);
        }
        catch (final InvalidPipeException ex) {
            try {
                this.revalidateAll();
                this.fillpipe.fillArc(this, n, n2, n3, n4, n5, n6);
            }
            catch (final InvalidPipeException ex2) {}
        }
        finally {
            this.surfaceData.markDirty();
        }
    }
    
    @Override
    public void drawPolyline(final int[] array, final int[] array2, final int n) {
        try {
            this.drawpipe.drawPolyline(this, array, array2, n);
        }
        catch (final InvalidPipeException ex) {
            try {
                this.revalidateAll();
                this.drawpipe.drawPolyline(this, array, array2, n);
            }
            catch (final InvalidPipeException ex2) {}
        }
        finally {
            this.surfaceData.markDirty();
        }
    }
    
    @Override
    public void drawPolygon(final int[] array, final int[] array2, final int n) {
        try {
            this.drawpipe.drawPolygon(this, array, array2, n);
        }
        catch (final InvalidPipeException ex) {
            try {
                this.revalidateAll();
                this.drawpipe.drawPolygon(this, array, array2, n);
            }
            catch (final InvalidPipeException ex2) {}
        }
        finally {
            this.surfaceData.markDirty();
        }
    }
    
    @Override
    public void fillPolygon(final int[] array, final int[] array2, final int n) {
        try {
            this.fillpipe.fillPolygon(this, array, array2, n);
        }
        catch (final InvalidPipeException ex) {
            try {
                this.revalidateAll();
                this.fillpipe.fillPolygon(this, array, array2, n);
            }
            catch (final InvalidPipeException ex2) {}
        }
        finally {
            this.surfaceData.markDirty();
        }
    }
    
    @Override
    public void drawRect(final int n, final int n2, final int n3, final int n4) {
        try {
            this.drawpipe.drawRect(this, n, n2, n3, n4);
        }
        catch (final InvalidPipeException ex) {
            try {
                this.revalidateAll();
                this.drawpipe.drawRect(this, n, n2, n3, n4);
            }
            catch (final InvalidPipeException ex2) {}
        }
        finally {
            this.surfaceData.markDirty();
        }
    }
    
    @Override
    public void fillRect(final int n, final int n2, final int n3, final int n4) {
        try {
            this.fillpipe.fillRect(this, n, n2, n3, n4);
        }
        catch (final InvalidPipeException ex) {
            try {
                this.revalidateAll();
                this.fillpipe.fillRect(this, n, n2, n3, n4);
            }
            catch (final InvalidPipeException ex2) {}
        }
        finally {
            this.surfaceData.markDirty();
        }
    }
    
    private void revalidateAll() {
        this.surfaceData = this.surfaceData.getReplacement();
        if (this.surfaceData == null) {
            this.surfaceData = NullSurfaceData.theInstance;
        }
        this.invalidatePipe();
        this.setDevClip(this.surfaceData.getBounds());
        if (this.paintState <= 1) {
            this.validateColor();
        }
        if (this.composite instanceof XORComposite) {
            this.setComposite(new XORComposite(((XORComposite)this.composite).getXorColor(), this.surfaceData));
        }
        this.validatePipe();
    }
    
    @Override
    public void clearRect(final int n, final int n2, final int n3, final int n4) {
        final Composite composite = this.composite;
        final Paint paint = this.paint;
        this.setComposite(AlphaComposite.Src);
        this.setColor(this.getBackground());
        this.fillRect(n, n2, n3, n4);
        this.setPaint(paint);
        this.setComposite(composite);
    }
    
    @Override
    public void draw(final Shape shape) {
        try {
            this.shapepipe.draw(this, shape);
        }
        catch (final InvalidPipeException ex) {
            try {
                this.revalidateAll();
                this.shapepipe.draw(this, shape);
            }
            catch (final InvalidPipeException ex2) {}
        }
        finally {
            this.surfaceData.markDirty();
        }
    }
    
    @Override
    public void fill(final Shape shape) {
        try {
            this.shapepipe.fill(this, shape);
        }
        catch (final InvalidPipeException ex) {
            try {
                this.revalidateAll();
                this.shapepipe.fill(this, shape);
            }
            catch (final InvalidPipeException ex2) {}
        }
        finally {
            this.surfaceData.markDirty();
        }
    }
    
    private static boolean isIntegerTranslation(final AffineTransform affineTransform) {
        if (affineTransform.isIdentity()) {
            return true;
        }
        if (affineTransform.getType() == 1) {
            final double translateX = affineTransform.getTranslateX();
            final double translateY = affineTransform.getTranslateY();
            return translateX == (int)translateX && translateY == (int)translateY;
        }
        return false;
    }
    
    private static int getTileIndex(int n, final int n2, final int n3) {
        n -= n2;
        if (n < 0) {
            n += 1 - n3;
        }
        return n / n3;
    }
    
    private static Rectangle getImageRegion(final RenderedImage renderedImage, final Region region, final AffineTransform affineTransform, final AffineTransform affineTransform2, final int n, final int n2) {
        final Rectangle rectangle = new Rectangle(renderedImage.getMinX(), renderedImage.getMinY(), renderedImage.getWidth(), renderedImage.getHeight());
        Rectangle intersection;
        try {
            final double[] array = new double[8];
            array[0] = (array[2] = region.getLoX());
            array[4] = (array[6] = region.getHiX());
            array[1] = (array[5] = region.getLoY());
            array[3] = (array[7] = region.getHiY());
            affineTransform.inverseTransform(array, 0, array, 0, 4);
            affineTransform2.inverseTransform(array, 0, array, 0, 4);
            double n4;
            double n3 = n4 = array[0];
            double n6;
            double n5 = n6 = array[1];
            int i = 2;
            while (i < 8) {
                final double n7 = array[i++];
                if (n7 < n4) {
                    n4 = n7;
                }
                else if (n7 > n3) {
                    n3 = n7;
                }
                final double n8 = array[i++];
                if (n8 < n6) {
                    n6 = n8;
                }
                else {
                    if (n8 <= n5) {
                        continue;
                    }
                    n5 = n8;
                }
            }
            intersection = new Rectangle((int)n4 - n, (int)n6 - n2, (int)(n3 - n4 + 2 * n), (int)(n5 - n6 + 2 * n2)).intersection(rectangle);
        }
        catch (final NoninvertibleTransformException ex) {
            intersection = rectangle;
        }
        return intersection;
    }
    
    @Override
    public void drawRenderedImage(final RenderedImage renderedImage, final AffineTransform affineTransform) {
        if (renderedImage == null) {
            return;
        }
        if (renderedImage instanceof BufferedImage) {
            this.drawImage((Image)renderedImage, affineTransform, null);
            return;
        }
        final boolean b = this.transformState <= 1 && isIntegerTranslation(affineTransform);
        final int n = b ? 0 : 3;
        Region compClip;
        try {
            compClip = this.getCompClip();
        }
        catch (final InvalidPipeException ex) {
            return;
        }
        final Rectangle imageRegion = getImageRegion(renderedImage, compClip, this.transform, affineTransform, n, n);
        if (imageRegion.width <= 0 || imageRegion.height <= 0) {
            return;
        }
        if (b) {
            this.drawTranslatedRenderedImage(renderedImage, imageRegion, (int)affineTransform.getTranslateX(), (int)affineTransform.getTranslateY());
            return;
        }
        final Raster data = renderedImage.getData(imageRegion);
        WritableRaster writableRaster = Raster.createWritableRaster(data.getSampleModel(), data.getDataBuffer(), null);
        final int minX = data.getMinX();
        final int minY = data.getMinY();
        final int width = data.getWidth();
        final int height = data.getHeight();
        final int n2 = minX - data.getSampleModelTranslateX();
        final int n3 = minY - data.getSampleModelTranslateY();
        if (n2 != 0 || n3 != 0 || width != writableRaster.getWidth() || height != writableRaster.getHeight()) {
            writableRaster = writableRaster.createWritableChild(n2, n3, width, height, 0, 0, null);
        }
        final AffineTransform affineTransform2 = (AffineTransform)affineTransform.clone();
        affineTransform2.translate(minX, minY);
        final ColorModel colorModel = renderedImage.getColorModel();
        this.drawImage(new BufferedImage(colorModel, writableRaster, colorModel.isAlphaPremultiplied(), null), affineTransform2, null);
    }
    
    private boolean clipTo(final Rectangle rectangle, final Rectangle rectangle2) {
        final int max = Math.max(rectangle.x, rectangle2.x);
        final int min = Math.min(rectangle.x + rectangle.width, rectangle2.x + rectangle2.width);
        final int max2 = Math.max(rectangle.y, rectangle2.y);
        final int min2 = Math.min(rectangle.y + rectangle.height, rectangle2.y + rectangle2.height);
        if (min - max < 0 || min2 - max2 < 0) {
            rectangle.width = -1;
            rectangle.height = -1;
            return false;
        }
        rectangle.x = max;
        rectangle.y = max2;
        rectangle.width = min - max;
        rectangle.height = min2 - max2;
        return true;
    }
    
    private void drawTranslatedRenderedImage(final RenderedImage renderedImage, final Rectangle rectangle, final int n, final int n2) {
        final int tileGridXOffset = renderedImage.getTileGridXOffset();
        final int tileGridYOffset = renderedImage.getTileGridYOffset();
        final int tileWidth = renderedImage.getTileWidth();
        final int tileHeight = renderedImage.getTileHeight();
        final int tileIndex = getTileIndex(rectangle.x, tileGridXOffset, tileWidth);
        final int tileIndex2 = getTileIndex(rectangle.y, tileGridYOffset, tileHeight);
        final int tileIndex3 = getTileIndex(rectangle.x + rectangle.width - 1, tileGridXOffset, tileWidth);
        final int tileIndex4 = getTileIndex(rectangle.y + rectangle.height - 1, tileGridYOffset, tileHeight);
        final ColorModel colorModel = renderedImage.getColorModel();
        final Rectangle rectangle2 = new Rectangle();
        for (int i = tileIndex2; i <= tileIndex4; ++i) {
            for (int j = tileIndex; j <= tileIndex3; ++j) {
                final Raster tile = renderedImage.getTile(j, i);
                rectangle2.x = j * tileWidth + tileGridXOffset;
                rectangle2.y = i * tileHeight + tileGridYOffset;
                rectangle2.width = tileWidth;
                rectangle2.height = tileHeight;
                this.clipTo(rectangle2, rectangle);
                WritableRaster writableRaster;
                if (tile instanceof WritableRaster) {
                    writableRaster = (WritableRaster)tile;
                }
                else {
                    writableRaster = Raster.createWritableRaster(tile.getSampleModel(), tile.getDataBuffer(), null);
                }
                this.copyImage(new BufferedImage(colorModel, writableRaster.createWritableChild(rectangle2.x, rectangle2.y, rectangle2.width, rectangle2.height, 0, 0, null), colorModel.isAlphaPremultiplied(), null), rectangle2.x + n, rectangle2.y + n2, 0, 0, rectangle2.width, rectangle2.height, null, null);
            }
        }
    }
    
    @Override
    public void drawRenderableImage(final RenderableImage renderableImage, final AffineTransform affineTransform) {
        if (renderableImage == null) {
            return;
        }
        final AffineTransform transform = this.transform;
        final AffineTransform affineTransform2 = new AffineTransform(affineTransform);
        affineTransform2.concatenate(transform);
        RenderContext renderContext = new RenderContext(affineTransform2);
        AffineTransform inverse;
        try {
            inverse = transform.createInverse();
        }
        catch (final NoninvertibleTransformException ex) {
            renderContext = new RenderContext(transform);
            inverse = new AffineTransform();
        }
        this.drawRenderedImage(renderableImage.createRendering(renderContext), inverse);
    }
    
    protected Rectangle transformBounds(final Rectangle rectangle, final AffineTransform affineTransform) {
        if (affineTransform.isIdentity()) {
            return rectangle;
        }
        return transformShape(affineTransform, rectangle).getBounds();
    }
    
    @Override
    public void drawString(final String s, final int n, final int n2) {
        if (s == null) {
            throw new NullPointerException("String is null");
        }
        if (!this.font.hasLayoutAttributes()) {
            try {
                this.textpipe.drawString(this, s, n, n2);
            }
            catch (final InvalidPipeException ex) {
                try {
                    this.revalidateAll();
                    this.textpipe.drawString(this, s, n, n2);
                }
                catch (final InvalidPipeException ex2) {}
            }
            finally {
                this.surfaceData.markDirty();
            }
            return;
        }
        if (s.length() == 0) {
            return;
        }
        new TextLayout(s, this.font, this.getFontRenderContext()).draw(this, (float)n, (float)n2);
    }
    
    @Override
    public void drawString(final String s, final float n, final float n2) {
        if (s == null) {
            throw new NullPointerException("String is null");
        }
        if (!this.font.hasLayoutAttributes()) {
            try {
                this.textpipe.drawString(this, s, n, n2);
            }
            catch (final InvalidPipeException ex) {
                try {
                    this.revalidateAll();
                    this.textpipe.drawString(this, s, n, n2);
                }
                catch (final InvalidPipeException ex2) {}
            }
            finally {
                this.surfaceData.markDirty();
            }
            return;
        }
        if (s.length() == 0) {
            return;
        }
        new TextLayout(s, this.font, this.getFontRenderContext()).draw(this, n, n2);
    }
    
    @Override
    public void drawString(final AttributedCharacterIterator attributedCharacterIterator, final int n, final int n2) {
        if (attributedCharacterIterator == null) {
            throw new NullPointerException("AttributedCharacterIterator is null");
        }
        if (attributedCharacterIterator.getBeginIndex() == attributedCharacterIterator.getEndIndex()) {
            return;
        }
        new TextLayout(attributedCharacterIterator, this.getFontRenderContext()).draw(this, (float)n, (float)n2);
    }
    
    @Override
    public void drawString(final AttributedCharacterIterator attributedCharacterIterator, final float n, final float n2) {
        if (attributedCharacterIterator == null) {
            throw new NullPointerException("AttributedCharacterIterator is null");
        }
        if (attributedCharacterIterator.getBeginIndex() == attributedCharacterIterator.getEndIndex()) {
            return;
        }
        new TextLayout(attributedCharacterIterator, this.getFontRenderContext()).draw(this, n, n2);
    }
    
    @Override
    public void drawGlyphVector(final GlyphVector glyphVector, final float n, final float n2) {
        if (glyphVector == null) {
            throw new NullPointerException("GlyphVector is null");
        }
        try {
            this.textpipe.drawGlyphVector(this, glyphVector, n, n2);
        }
        catch (final InvalidPipeException ex) {
            try {
                this.revalidateAll();
                this.textpipe.drawGlyphVector(this, glyphVector, n, n2);
            }
            catch (final InvalidPipeException ex2) {}
        }
        finally {
            this.surfaceData.markDirty();
        }
    }
    
    @Override
    public void drawChars(final char[] array, final int n, final int n2, final int n3, final int n4) {
        if (array == null) {
            throw new NullPointerException("char data is null");
        }
        if (n < 0 || n2 < 0 || n + n2 < n2 || n + n2 > array.length) {
            throw new ArrayIndexOutOfBoundsException("bad offset/length");
        }
        if (!this.font.hasLayoutAttributes()) {
            try {
                this.textpipe.drawChars(this, array, n, n2, n3, n4);
            }
            catch (final InvalidPipeException ex) {
                try {
                    this.revalidateAll();
                    this.textpipe.drawChars(this, array, n, n2, n3, n4);
                }
                catch (final InvalidPipeException ex2) {}
            }
            finally {
                this.surfaceData.markDirty();
            }
            return;
        }
        if (array.length == 0) {
            return;
        }
        new TextLayout(new String(array, n, n2), this.font, this.getFontRenderContext()).draw(this, (float)n3, (float)n4);
    }
    
    @Override
    public void drawBytes(final byte[] array, final int n, final int n2, final int n3, final int n4) {
        if (array == null) {
            throw new NullPointerException("byte data is null");
        }
        if (n < 0 || n2 < 0 || n + n2 < n2 || n + n2 > array.length) {
            throw new ArrayIndexOutOfBoundsException("bad offset/length");
        }
        final char[] array2 = new char[n2];
        int n5 = n2;
        while (n5-- > 0) {
            array2[n5] = (char)(array[n5 + n] & 0xFF);
        }
        if (!this.font.hasLayoutAttributes()) {
            try {
                this.textpipe.drawChars(this, array2, 0, n2, n3, n4);
            }
            catch (final InvalidPipeException ex) {
                try {
                    this.revalidateAll();
                    this.textpipe.drawChars(this, array2, 0, n2, n3, n4);
                }
                catch (final InvalidPipeException ex2) {}
            }
            finally {
                this.surfaceData.markDirty();
            }
            return;
        }
        if (array.length == 0) {
            return;
        }
        new TextLayout(new String(array2), this.font, this.getFontRenderContext()).draw(this, (float)n3, (float)n4);
    }
    
    private boolean isHiDPIImage(final Image image) {
        return SurfaceManager.getImageScale(image) != 1 || (this.resolutionVariantHint != 1 && image instanceof MultiResolutionImage);
    }
    
    private boolean drawHiDPIImage(Image image, final int n, final int n2, final int n3, final int n4, int n5, int n6, int n7, int n8, final Color color, ImageObserver imageObserver) {
        if (SurfaceManager.getImageScale(image) != 1) {
            final int imageScale = SurfaceManager.getImageScale(image);
            n5 = Region.clipScale(n5, imageScale);
            n7 = Region.clipScale(n7, imageScale);
            n6 = Region.clipScale(n6, imageScale);
            n8 = Region.clipScale(n8, imageScale);
        }
        else if (image instanceof MultiResolutionImage) {
            final int width = image.getWidth(imageObserver);
            final int height = image.getHeight(imageObserver);
            final Image resolutionVariant = this.getResolutionVariant((MultiResolutionImage)image, width, height, n, n2, n3, n4, n5, n6, n7, n8);
            if (resolutionVariant != image && resolutionVariant != null) {
                final ImageObserver resolutionVariantObserver = MultiResolutionToolkitImage.getResolutionVariantObserver(image, imageObserver, width, height, -1, -1);
                final int width2 = resolutionVariant.getWidth(resolutionVariantObserver);
                final int height2 = resolutionVariant.getHeight(resolutionVariantObserver);
                if (0 < width && 0 < height && 0 < width2 && 0 < height2) {
                    final float n9 = width2 / (float)width;
                    final float n10 = height2 / (float)height;
                    n5 = Region.clipScale(n5, n9);
                    n6 = Region.clipScale(n6, n10);
                    n7 = Region.clipScale(n7, n9);
                    n8 = Region.clipScale(n8, n10);
                    imageObserver = resolutionVariantObserver;
                    image = resolutionVariant;
                }
            }
        }
        try {
            return this.imagepipe.scaleImage(this, image, n, n2, n3, n4, n5, n6, n7, n8, color, imageObserver);
        }
        catch (final InvalidPipeException ex) {
            try {
                this.revalidateAll();
                return this.imagepipe.scaleImage(this, image, n, n2, n3, n4, n5, n6, n7, n8, color, imageObserver);
            }
            catch (final InvalidPipeException ex2) {
                final boolean b = false;
                this.surfaceData.markDirty();
                return b;
            }
        }
        finally {
            this.surfaceData.markDirty();
        }
    }
    
    private Image getResolutionVariant(final MultiResolutionImage multiResolutionImage, final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7, final int n8, final int n9, final int n10) {
        if (n <= 0 || n2 <= 0) {
            return null;
        }
        final int n11 = n9 - n7;
        final int n12 = n10 - n8;
        if (n11 == 0 || n12 == 0) {
            return null;
        }
        final int type = this.transform.getType();
        final int n13 = n5 - n3;
        final int n14 = n6 - n4;
        double n15;
        double n16;
        if ((type & 0xFFFFFFBE) == 0x0) {
            n15 = n13;
            n16 = n14;
        }
        else if ((type & 0xFFFFFFB8) == 0x0) {
            n15 = n13 * this.transform.getScaleX();
            n16 = n14 * this.transform.getScaleY();
        }
        else {
            n15 = n13 * Math.hypot(this.transform.getScaleX(), this.transform.getShearY());
            n16 = n14 * Math.hypot(this.transform.getShearX(), this.transform.getScaleY());
        }
        final Image resolutionVariant = multiResolutionImage.getResolutionVariant((int)Math.abs(n * n15 / n11), (int)Math.abs(n2 * n16 / n12));
        if (resolutionVariant instanceof ToolkitImage && ((ToolkitImage)resolutionVariant).hasError()) {
            return null;
        }
        return resolutionVariant;
    }
    
    @Override
    public boolean drawImage(final Image image, final int n, final int n2, final int n3, final int n4, final ImageObserver imageObserver) {
        return this.drawImage(image, n, n2, n3, n4, null, imageObserver);
    }
    
    public boolean copyImage(final Image image, final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final Color color, final ImageObserver imageObserver) {
        try {
            return this.imagepipe.copyImage(this, image, n, n2, n3, n4, n5, n6, color, imageObserver);
        }
        catch (final InvalidPipeException ex) {
            try {
                this.revalidateAll();
                return this.imagepipe.copyImage(this, image, n, n2, n3, n4, n5, n6, color, imageObserver);
            }
            catch (final InvalidPipeException ex2) {
                final boolean b = false;
                this.surfaceData.markDirty();
                return b;
            }
        }
        finally {
            this.surfaceData.markDirty();
        }
    }
    
    @Override
    public boolean drawImage(final Image image, final int n, final int n2, final int n3, final int n4, final Color color, final ImageObserver imageObserver) {
        if (image == null) {
            return true;
        }
        if (n3 == 0 || n4 == 0) {
            return true;
        }
        final int width = image.getWidth(null);
        final int height = image.getHeight(null);
        if (this.isHiDPIImage(image)) {
            return this.drawHiDPIImage(image, n, n2, n + n3, n2 + n4, 0, 0, width, height, color, imageObserver);
        }
        if (n3 == width && n4 == height) {
            return this.copyImage(image, n, n2, 0, 0, n3, n4, color, imageObserver);
        }
        try {
            return this.imagepipe.scaleImage(this, image, n, n2, n3, n4, color, imageObserver);
        }
        catch (final InvalidPipeException ex) {
            try {
                this.revalidateAll();
                return this.imagepipe.scaleImage(this, image, n, n2, n3, n4, color, imageObserver);
            }
            catch (final InvalidPipeException ex2) {
                final boolean b = false;
                this.surfaceData.markDirty();
                return b;
            }
        }
        finally {
            this.surfaceData.markDirty();
        }
    }
    
    @Override
    public boolean drawImage(final Image image, final int n, final int n2, final ImageObserver imageObserver) {
        return this.drawImage(image, n, n2, null, imageObserver);
    }
    
    @Override
    public boolean drawImage(final Image image, final int n, final int n2, final Color color, final ImageObserver imageObserver) {
        if (image == null) {
            return true;
        }
        if (this.isHiDPIImage(image)) {
            final int width = image.getWidth(null);
            final int height = image.getHeight(null);
            return this.drawHiDPIImage(image, n, n2, n + width, n2 + height, 0, 0, width, height, color, imageObserver);
        }
        try {
            return this.imagepipe.copyImage(this, image, n, n2, color, imageObserver);
        }
        catch (final InvalidPipeException ex) {
            try {
                this.revalidateAll();
                return this.imagepipe.copyImage(this, image, n, n2, color, imageObserver);
            }
            catch (final InvalidPipeException ex2) {
                final boolean b = false;
                this.surfaceData.markDirty();
                return b;
            }
        }
        finally {
            this.surfaceData.markDirty();
        }
    }
    
    @Override
    public boolean drawImage(final Image image, final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7, final int n8, final ImageObserver imageObserver) {
        return this.drawImage(image, n, n2, n3, n4, n5, n6, n7, n8, null, imageObserver);
    }
    
    @Override
    public boolean drawImage(final Image image, final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7, final int n8, final Color color, final ImageObserver imageObserver) {
        if (image == null) {
            return true;
        }
        if (n == n3 || n2 == n4 || n5 == n7 || n6 == n8) {
            return true;
        }
        if (this.isHiDPIImage(image)) {
            return this.drawHiDPIImage(image, n, n2, n3, n4, n5, n6, n7, n8, color, imageObserver);
        }
        if (n7 - n5 == n3 - n && n8 - n6 == n4 - n2) {
            int n9;
            int n10;
            int n11;
            if (n7 > n5) {
                n9 = n7 - n5;
                n10 = n5;
                n11 = n;
            }
            else {
                n9 = n5 - n7;
                n10 = n7;
                n11 = n3;
            }
            int n12;
            int n13;
            int n14;
            if (n8 > n6) {
                n12 = n8 - n6;
                n13 = n6;
                n14 = n2;
            }
            else {
                n12 = n6 - n8;
                n13 = n8;
                n14 = n4;
            }
            return this.copyImage(image, n11, n14, n10, n13, n9, n12, color, imageObserver);
        }
        try {
            return this.imagepipe.scaleImage(this, image, n, n2, n3, n4, n5, n6, n7, n8, color, imageObserver);
        }
        catch (final InvalidPipeException ex) {
            try {
                this.revalidateAll();
                return this.imagepipe.scaleImage(this, image, n, n2, n3, n4, n5, n6, n7, n8, color, imageObserver);
            }
            catch (final InvalidPipeException ex2) {
                final boolean b = false;
                this.surfaceData.markDirty();
                return b;
            }
        }
        finally {
            this.surfaceData.markDirty();
        }
    }
    
    @Override
    public boolean drawImage(final Image image, final AffineTransform affineTransform, final ImageObserver imageObserver) {
        if (image == null) {
            return true;
        }
        if (affineTransform == null || affineTransform.isIdentity()) {
            return this.drawImage(image, 0, 0, null, imageObserver);
        }
        if (this.isHiDPIImage(image)) {
            final int width = image.getWidth(null);
            final int height = image.getHeight(null);
            final AffineTransform transform = new AffineTransform(this.transform);
            this.transform(affineTransform);
            final boolean drawHiDPIImage = this.drawHiDPIImage(image, 0, 0, width, height, 0, 0, width, height, null, imageObserver);
            this.transform.setTransform(transform);
            this.invalidateTransform();
            return drawHiDPIImage;
        }
        try {
            return this.imagepipe.transformImage(this, image, affineTransform, imageObserver);
        }
        catch (final InvalidPipeException ex) {
            try {
                this.revalidateAll();
                return this.imagepipe.transformImage(this, image, affineTransform, imageObserver);
            }
            catch (final InvalidPipeException ex2) {
                final boolean b = false;
                this.surfaceData.markDirty();
                return b;
            }
        }
        finally {
            this.surfaceData.markDirty();
        }
    }
    
    @Override
    public void drawImage(final BufferedImage bufferedImage, final BufferedImageOp bufferedImageOp, final int n, final int n2) {
        if (bufferedImage == null) {
            return;
        }
        try {
            this.imagepipe.transformImage(this, bufferedImage, bufferedImageOp, n, n2);
        }
        catch (final InvalidPipeException ex) {
            try {
                this.revalidateAll();
                this.imagepipe.transformImage(this, bufferedImage, bufferedImageOp, n, n2);
            }
            catch (final InvalidPipeException ex2) {}
        }
        finally {
            this.surfaceData.markDirty();
        }
    }
    
    @Override
    public FontRenderContext getFontRenderContext() {
        if (this.cachedFRC == null) {
            int textAntialiasHint = this.textAntialiasHint;
            if (textAntialiasHint == 0 && this.antialiasHint == 2) {
                textAntialiasHint = 2;
            }
            AffineTransform transform = null;
            if (this.transformState >= 3) {
                if (this.transform.getTranslateX() == 0.0 && this.transform.getTranslateY() == 0.0) {
                    transform = this.transform;
                }
                else {
                    transform = new AffineTransform(this.transform.getScaleX(), this.transform.getShearY(), this.transform.getShearX(), this.transform.getScaleY(), 0.0, 0.0);
                }
            }
            this.cachedFRC = new FontRenderContext(transform, SunHints.Value.get(2, textAntialiasHint), SunHints.Value.get(3, this.fractionalMetricsHint));
        }
        return this.cachedFRC;
    }
    
    @Override
    public void dispose() {
        this.surfaceData = NullSurfaceData.theInstance;
        this.invalidatePipe();
    }
    
    @Override
    public void finalize() {
    }
    
    public Object getDestination() {
        return this.surfaceData.getDestination();
    }
    
    @Override
    public Surface getDestSurface() {
        return this.surfaceData;
    }
    
    static {
        defaultStroke = new BasicStroke();
        defaultComposite = AlphaComposite.SrcOver;
        defaultFont = new Font("Dialog", 0, 12);
        SunGraphics2D.lcdTextContrastDefaultValue = 140;
        if (PerformanceLogger.loggingEnabled()) {
            PerformanceLogger.setTime("SunGraphics2D static initialization");
        }
        SunGraphics2D.invalidpipe = new ValidatePipe();
        IDENT_MATRIX = new double[] { 1.0, 0.0, 0.0, 1.0 };
        IDENT_ATX = new AffineTransform();
        SunGraphics2D.textTxArr = new double[17][];
        SunGraphics2D.textAtArr = new AffineTransform[17];
        for (int i = 8; i < 17; ++i) {
            SunGraphics2D.textTxArr[i] = new double[] { i, 0.0, 0.0, i };
            SunGraphics2D.textAtArr[i] = new AffineTransform(SunGraphics2D.textTxArr[i]);
        }
        MinPenSizeAA = RenderingEngine.getInstance().getMinimumAAPenSize();
        MinPenSizeAASquared = SunGraphics2D.MinPenSizeAA * SunGraphics2D.MinPenSizeAA;
    }
}
