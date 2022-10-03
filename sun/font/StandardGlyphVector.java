package sun.font;

import java.awt.geom.NoninvertibleTransformException;
import java.awt.Point;
import java.awt.font.GlyphJustificationInfo;
import java.awt.font.GlyphMetrics;
import java.awt.geom.GeneralPath;
import java.awt.Shape;
import java.awt.Rectangle;
import java.awt.font.LineMetrics;
import java.awt.geom.Rectangle2D;
import java.awt.RenderingHints;
import sun.java2d.loops.FontInfo;
import java.text.CharacterIterator;
import java.awt.geom.Point2D;
import java.lang.ref.SoftReference;
import java.awt.geom.AffineTransform;
import java.awt.font.FontRenderContext;
import java.awt.Font;
import java.awt.font.GlyphVector;

public class StandardGlyphVector extends GlyphVector
{
    private Font font;
    private FontRenderContext frc;
    private int[] glyphs;
    private int[] userGlyphs;
    private float[] positions;
    private int[] charIndices;
    private int flags;
    private static final int UNINITIALIZED_FLAGS = -1;
    private GlyphTransformInfo gti;
    private AffineTransform ftx;
    private AffineTransform dtx;
    private AffineTransform invdtx;
    private AffineTransform frctx;
    private Font2D font2D;
    private SoftReference fsref;
    private SoftReference lbcacheRef;
    private SoftReference vbcacheRef;
    public static final int FLAG_USES_VERTICAL_BASELINE = 128;
    public static final int FLAG_USES_VERTICAL_METRICS = 256;
    public static final int FLAG_USES_ALTERNATE_ORIENTATION = 512;
    
    public StandardGlyphVector(final Font font, final String s, final FontRenderContext fontRenderContext) {
        this.init(font, s.toCharArray(), 0, s.length(), fontRenderContext, -1);
    }
    
    public StandardGlyphVector(final Font font, final char[] array, final FontRenderContext fontRenderContext) {
        this.init(font, array, 0, array.length, fontRenderContext, -1);
    }
    
    public StandardGlyphVector(final Font font, final char[] array, final int n, final int n2, final FontRenderContext fontRenderContext) {
        this.init(font, array, n, n2, fontRenderContext, -1);
    }
    
    private float getTracking(final Font font) {
        if (font.hasLayoutAttributes()) {
            return ((AttributeMap)font.getAttributes()).getValues().getTracking();
        }
        return 0.0f;
    }
    
    public StandardGlyphVector(final Font font, final FontRenderContext fontRenderContext, final int[] array, final float[] array2, final int[] array3, final int n) {
        this.initGlyphVector(font, fontRenderContext, array, array2, array3, n);
        final float tracking = this.getTracking(font);
        if (tracking != 0.0f) {
            final Point2D.Float float1 = new Point2D.Float(tracking * font.getSize2D(), 0.0f);
            if (font.isTransformed()) {
                font.getTransform().deltaTransform(float1, float1);
            }
            final FontStrike strike = FontUtilities.getFont2D(font).getStrike(font, fontRenderContext);
            final float[] array4 = { float1.x, float1.y };
            for (int i = 0; i < array4.length; ++i) {
                final float n2 = array4[i];
                if (n2 != 0.0f) {
                    float n3 = 0.0f;
                    int n4 = i;
                    int j = 0;
                    while (j < array.length) {
                        if (strike.getGlyphAdvance(array[j++]) != 0.0f) {
                            final int n5 = n4;
                            array2[n5] += n3;
                            n3 += n2;
                        }
                        n4 += 2;
                    }
                    final int n6 = array2.length - 2 + i;
                    array2[n6] += n3;
                }
            }
        }
    }
    
    public void initGlyphVector(final Font font, final FontRenderContext frc, final int[] array, final float[] positions, final int[] charIndices, final int flags) {
        this.font = font;
        this.frc = frc;
        this.glyphs = array;
        this.userGlyphs = array;
        this.positions = positions;
        this.charIndices = charIndices;
        this.flags = flags;
        this.initFontData();
    }
    
    public StandardGlyphVector(final Font font, final CharacterIterator characterIterator, final FontRenderContext fontRenderContext) {
        final int beginIndex = characterIterator.getBeginIndex();
        final char[] array = new char[characterIterator.getEndIndex() - beginIndex];
        for (char c = characterIterator.first(); c != '\uffff'; c = characterIterator.next()) {
            array[characterIterator.getIndex() - beginIndex] = c;
        }
        this.init(font, array, 0, array.length, fontRenderContext, -1);
    }
    
    public StandardGlyphVector(final Font font, final int[] userGlyphs, final FontRenderContext frc) {
        this.font = font;
        this.frc = frc;
        this.flags = -1;
        this.initFontData();
        this.userGlyphs = userGlyphs;
        this.glyphs = this.getValidatedGlyphs(this.userGlyphs);
    }
    
    public static StandardGlyphVector getStandardGV(final GlyphVector glyphVector, final FontInfo fontInfo) {
        if (fontInfo.aaHint == 2) {
            final Object antiAliasingHint = glyphVector.getFontRenderContext().getAntiAliasingHint();
            if (antiAliasingHint != RenderingHints.VALUE_TEXT_ANTIALIAS_ON && antiAliasingHint != RenderingHints.VALUE_TEXT_ANTIALIAS_GASP) {
                final FontRenderContext fontRenderContext = glyphVector.getFontRenderContext();
                return new StandardGlyphVector(glyphVector, new FontRenderContext(fontRenderContext.getTransform(), RenderingHints.VALUE_TEXT_ANTIALIAS_ON, fontRenderContext.getFractionalMetricsHint()));
            }
        }
        if (glyphVector instanceof StandardGlyphVector) {
            return (StandardGlyphVector)glyphVector;
        }
        return new StandardGlyphVector(glyphVector, glyphVector.getFontRenderContext());
    }
    
    @Override
    public Font getFont() {
        return this.font;
    }
    
    @Override
    public FontRenderContext getFontRenderContext() {
        return this.frc;
    }
    
    @Override
    public void performDefaultLayout() {
        this.positions = null;
        if (this.getTracking(this.font) == 0.0f) {
            this.clearFlags(2);
        }
    }
    
    @Override
    public int getNumGlyphs() {
        return this.glyphs.length;
    }
    
    @Override
    public int getGlyphCode(final int n) {
        return this.userGlyphs[n];
    }
    
    @Override
    public int[] getGlyphCodes(final int n, final int n2, int[] array) {
        if (n2 < 0) {
            throw new IllegalArgumentException("count = " + n2);
        }
        if (n < 0) {
            throw new IndexOutOfBoundsException("start = " + n);
        }
        if (n > this.glyphs.length - n2) {
            throw new IndexOutOfBoundsException("start + count = " + (n + n2));
        }
        if (array == null) {
            array = new int[n2];
        }
        for (int i = 0; i < n2; ++i) {
            array[i] = this.userGlyphs[i + n];
        }
        return array;
    }
    
    @Override
    public int getGlyphCharIndex(final int n) {
        if (n < 0 && n >= this.glyphs.length) {
            throw new IndexOutOfBoundsException("" + n);
        }
        if (this.charIndices != null) {
            return this.charIndices[n];
        }
        if ((this.getLayoutFlags() & 0x4) != 0x0) {
            return this.glyphs.length - 1 - n;
        }
        return n;
    }
    
    @Override
    public int[] getGlyphCharIndices(final int n, final int n2, int[] array) {
        if (n < 0 || n2 < 0 || n2 > this.glyphs.length - n) {
            throw new IndexOutOfBoundsException("" + n + ", " + n2);
        }
        if (array == null) {
            array = new int[n2];
        }
        if (this.charIndices == null) {
            if ((this.getLayoutFlags() & 0x4) != 0x0) {
                for (int i = 0, n3 = this.glyphs.length - 1 - n; i < n2; ++i, --n3) {
                    array[i] = n3;
                }
            }
            else {
                for (int j = 0, n4 = n; j < n2; ++j, ++n4) {
                    array[j] = n4;
                }
            }
        }
        else {
            for (int k = 0; k < n2; ++k) {
                array[k] = this.charIndices[k + n];
            }
        }
        return array;
    }
    
    @Override
    public Rectangle2D getLogicalBounds() {
        this.setFRCTX();
        this.initPositions();
        final LineMetrics lineMetrics = this.font.getLineMetrics("", this.frc);
        final float n = 0.0f;
        final float n2 = -lineMetrics.getAscent();
        float n3 = 0.0f;
        final float n4 = lineMetrics.getDescent() + lineMetrics.getLeading();
        if (this.glyphs.length > 0) {
            n3 = this.positions[this.positions.length - 2];
        }
        return new Rectangle2D.Float(n, n2, n3 - n, n4 - n2);
    }
    
    @Override
    public Rectangle2D getVisualBounds() {
        Rectangle2D rectangle2D = null;
        for (int i = 0; i < this.glyphs.length; ++i) {
            final Rectangle2D bounds2D = this.getGlyphVisualBounds(i).getBounds2D();
            if (!bounds2D.isEmpty()) {
                if (rectangle2D == null) {
                    rectangle2D = bounds2D;
                }
                else {
                    Rectangle2D.union(rectangle2D, bounds2D, rectangle2D);
                }
            }
        }
        if (rectangle2D == null) {
            rectangle2D = new Rectangle2D.Float(0.0f, 0.0f, 0.0f, 0.0f);
        }
        return rectangle2D;
    }
    
    @Override
    public Rectangle getPixelBounds(final FontRenderContext fontRenderContext, final float n, final float n2) {
        return this.getGlyphsPixelBounds(fontRenderContext, n, n2, 0, this.glyphs.length);
    }
    
    @Override
    public Shape getOutline() {
        return this.getGlyphsOutline(0, this.glyphs.length, 0.0f, 0.0f);
    }
    
    @Override
    public Shape getOutline(final float n, final float n2) {
        return this.getGlyphsOutline(0, this.glyphs.length, n, n2);
    }
    
    @Override
    public Shape getGlyphOutline(final int n) {
        return this.getGlyphsOutline(n, 1, 0.0f, 0.0f);
    }
    
    @Override
    public Shape getGlyphOutline(final int n, final float n2, final float n3) {
        return this.getGlyphsOutline(n, 1, n2, n3);
    }
    
    @Override
    public Point2D getGlyphPosition(int n) {
        this.initPositions();
        n *= 2;
        return new Point2D.Float(this.positions[n], this.positions[n + 1]);
    }
    
    @Override
    public void setGlyphPosition(final int n, final Point2D point2D) {
        this.initPositions();
        final int n2 = n << 1;
        this.positions[n2] = (float)point2D.getX();
        this.positions[n2 + 1] = (float)point2D.getY();
        this.clearCaches(n);
        this.addFlags(2);
    }
    
    @Override
    public AffineTransform getGlyphTransform(final int n) {
        if (n < 0 || n >= this.glyphs.length) {
            throw new IndexOutOfBoundsException("ix = " + n);
        }
        if (this.gti != null) {
            return this.gti.getGlyphTransform(n);
        }
        return null;
    }
    
    @Override
    public void setGlyphTransform(final int n, final AffineTransform affineTransform) {
        if (n < 0 || n >= this.glyphs.length) {
            throw new IndexOutOfBoundsException("ix = " + n);
        }
        if (this.gti == null) {
            if (affineTransform == null || affineTransform.isIdentity()) {
                return;
            }
            this.gti = new GlyphTransformInfo(this);
        }
        this.gti.setGlyphTransform(n, affineTransform);
        if (this.gti.transformCount() == 0) {
            this.gti = null;
        }
    }
    
    @Override
    public int getLayoutFlags() {
        if (this.flags == -1) {
            this.flags = 0;
            if (this.charIndices != null && this.glyphs.length > 1) {
                boolean b = true;
                boolean b2 = true;
                int n2;
                for (int length = this.charIndices.length, n = 0; n < this.charIndices.length && (b || b2); b = (b && n2 == n), b2 = (b2 && n2 == --length), ++n) {
                    n2 = this.charIndices[n];
                }
                if (b2) {
                    this.flags |= 0x4;
                }
                if (!b2 && !b) {
                    this.flags |= 0x8;
                }
            }
        }
        return this.flags;
    }
    
    @Override
    public float[] getGlyphPositions(final int n, final int n2, final float[] array) {
        if (n2 < 0) {
            throw new IllegalArgumentException("count = " + n2);
        }
        if (n < 0) {
            throw new IndexOutOfBoundsException("start = " + n);
        }
        if (n > this.glyphs.length + 1 - n2) {
            throw new IndexOutOfBoundsException("start + count = " + (n + n2));
        }
        return this.internalGetGlyphPositions(n, n2, 0, array);
    }
    
    @Override
    public Shape getGlyphLogicalBounds(final int n) {
        if (n < 0 || n >= this.glyphs.length) {
            throw new IndexOutOfBoundsException("ix = " + n);
        }
        Shape[] array;
        if (this.lbcacheRef == null || (array = this.lbcacheRef.get()) == null) {
            array = new Shape[this.glyphs.length];
            this.lbcacheRef = new SoftReference(array);
        }
        Shape shape = array[n];
        if (shape == null) {
            this.setFRCTX();
            this.initPositions();
            final ADL adl = new ADL();
            final GlyphStrike glyphStrike = this.getGlyphStrike(n);
            glyphStrike.getADL(adl);
            final Point2D.Float glyphMetrics = glyphStrike.strike.getGlyphMetrics(this.glyphs[n]);
            final float x = glyphMetrics.x;
            final float y = glyphMetrics.y;
            final float n2 = adl.descentX + adl.leadingX + adl.ascentX;
            final float n3 = adl.descentY + adl.leadingY + adl.ascentY;
            final float n4 = this.positions[n * 2] + glyphStrike.dx - adl.ascentX;
            final float n5 = this.positions[n * 2 + 1] + glyphStrike.dy - adl.ascentY;
            final GeneralPath generalPath = new GeneralPath();
            generalPath.moveTo(n4, n5);
            generalPath.lineTo(n4 + x, n5 + y);
            generalPath.lineTo(n4 + x + n2, n5 + y + n3);
            generalPath.lineTo(n4 + n2, n5 + n3);
            generalPath.closePath();
            shape = new DelegatingShape(generalPath);
            array[n] = shape;
        }
        return shape;
    }
    
    @Override
    public Shape getGlyphVisualBounds(final int n) {
        if (n < 0 || n >= this.glyphs.length) {
            throw new IndexOutOfBoundsException("ix = " + n);
        }
        Shape[] array;
        if (this.vbcacheRef == null || (array = this.vbcacheRef.get()) == null) {
            array = new Shape[this.glyphs.length];
            this.vbcacheRef = new SoftReference(array);
        }
        Shape shape = array[n];
        if (shape == null) {
            shape = new DelegatingShape(this.getGlyphOutlineBounds(n));
            array[n] = shape;
        }
        return shape;
    }
    
    @Override
    public Rectangle getGlyphPixelBounds(final int n, final FontRenderContext fontRenderContext, final float n2, final float n3) {
        return this.getGlyphsPixelBounds(fontRenderContext, n2, n3, n, 1);
    }
    
    @Override
    public GlyphMetrics getGlyphMetrics(final int n) {
        if (n < 0 || n >= this.glyphs.length) {
            throw new IndexOutOfBoundsException("ix = " + n);
        }
        final Rectangle2D bounds2D = this.getGlyphVisualBounds(n).getBounds2D();
        final Point2D glyphPosition = this.getGlyphPosition(n);
        bounds2D.setRect(bounds2D.getMinX() - glyphPosition.getX(), bounds2D.getMinY() - glyphPosition.getY(), bounds2D.getWidth(), bounds2D.getHeight());
        final Point2D.Float glyphMetrics = this.getGlyphStrike(n).strike.getGlyphMetrics(this.glyphs[n]);
        return new GlyphMetrics(true, glyphMetrics.x, glyphMetrics.y, bounds2D, (byte)0);
    }
    
    @Override
    public GlyphJustificationInfo getGlyphJustificationInfo(final int n) {
        if (n < 0 || n >= this.glyphs.length) {
            throw new IndexOutOfBoundsException("ix = " + n);
        }
        return null;
    }
    
    @Override
    public boolean equals(final GlyphVector glyphVector) {
        if (this == glyphVector) {
            return true;
        }
        if (glyphVector == null) {
            return false;
        }
        try {
            final StandardGlyphVector standardGlyphVector = (StandardGlyphVector)glyphVector;
            if (this.glyphs.length != standardGlyphVector.glyphs.length) {
                return false;
            }
            for (int i = 0; i < this.glyphs.length; ++i) {
                if (this.glyphs[i] != standardGlyphVector.glyphs[i]) {
                    return false;
                }
            }
            if (!this.font.equals(standardGlyphVector.font)) {
                return false;
            }
            if (!this.frc.equals(standardGlyphVector.frc)) {
                return false;
            }
            if (standardGlyphVector.positions == null != (this.positions == null)) {
                if (this.positions == null) {
                    this.initPositions();
                }
                else {
                    standardGlyphVector.initPositions();
                }
            }
            if (this.positions != null) {
                for (int j = 0; j < this.positions.length; ++j) {
                    if (this.positions[j] != standardGlyphVector.positions[j]) {
                        return false;
                    }
                }
            }
            if (this.gti == null) {
                return standardGlyphVector.gti == null;
            }
            return this.gti.equals(standardGlyphVector.gti);
        }
        catch (final ClassCastException ex) {
            return false;
        }
    }
    
    @Override
    public int hashCode() {
        return this.font.hashCode() ^ this.glyphs.length;
    }
    
    @Override
    public boolean equals(final Object o) {
        try {
            return this.equals((GlyphVector)o);
        }
        catch (final ClassCastException ex) {
            return false;
        }
    }
    
    public StandardGlyphVector copy() {
        return (StandardGlyphVector)this.clone();
    }
    
    public Object clone() {
        try {
            final StandardGlyphVector standardGlyphVector = (StandardGlyphVector)super.clone();
            standardGlyphVector.clearCaches();
            if (this.positions != null) {
                standardGlyphVector.positions = this.positions.clone();
            }
            if (this.gti != null) {
                standardGlyphVector.gti = new GlyphTransformInfo(standardGlyphVector, this.gti);
            }
            return standardGlyphVector;
        }
        catch (final CloneNotSupportedException ex) {
            return this;
        }
    }
    
    public void setGlyphPositions(final float[] array, final int n, final int n2, final int n3) {
        if (n3 < 0) {
            throw new IllegalArgumentException("count = " + n3);
        }
        this.initPositions();
        for (int i = n2 * 2, n4 = i + n3 * 2, n5 = n; i < n4; ++i, ++n5) {
            this.positions[i] = array[n5];
        }
        this.clearCaches();
        this.addFlags(2);
    }
    
    public void setGlyphPositions(final float[] array) {
        final int n = this.glyphs.length * 2 + 2;
        if (array.length != n) {
            throw new IllegalArgumentException("srcPositions.length != " + n);
        }
        this.positions = array.clone();
        this.clearCaches();
        this.addFlags(2);
    }
    
    public float[] getGlyphPositions(final float[] array) {
        return this.internalGetGlyphPositions(0, this.glyphs.length + 1, 0, array);
    }
    
    public AffineTransform[] getGlyphTransforms(int n, final int n2, AffineTransform[] array) {
        if (n < 0 || n2 < 0 || n + n2 > this.glyphs.length) {
            throw new IllegalArgumentException("start: " + n + " count: " + n2);
        }
        if (this.gti == null) {
            return null;
        }
        if (array == null) {
            array = new AffineTransform[n2];
        }
        for (int i = 0; i < n2; ++i, ++n) {
            array[i] = this.gti.getGlyphTransform(n);
        }
        return array;
    }
    
    public AffineTransform[] getGlyphTransforms() {
        return this.getGlyphTransforms(0, this.glyphs.length, null);
    }
    
    public void setGlyphTransforms(final AffineTransform[] array, final int n, final int n2, final int n3) {
        for (int i = n2; i < n2 + n3; ++i) {
            this.setGlyphTransform(i, array[n + i]);
        }
    }
    
    public void setGlyphTransforms(final AffineTransform[] array) {
        this.setGlyphTransforms(array, 0, 0, this.glyphs.length);
    }
    
    public float[] getGlyphInfo() {
        this.setFRCTX();
        this.initPositions();
        final float[] array = new float[this.glyphs.length * 8];
        for (int i = 0, n = 0; i < this.glyphs.length; ++i, n += 8) {
            final float n2 = this.positions[i * 2];
            final float n3 = this.positions[i * 2 + 1];
            array[n] = n2;
            array[n + 1] = n3;
            final Point2D.Float glyphMetrics = this.getGlyphStrike(i).strike.getGlyphMetrics(this.glyphs[i]);
            array[n + 2] = glyphMetrics.x;
            array[n + 3] = glyphMetrics.y;
            final Rectangle2D bounds2D = this.getGlyphVisualBounds(i).getBounds2D();
            array[n + 4] = (float)bounds2D.getMinX();
            array[n + 5] = (float)bounds2D.getMinY();
            array[n + 6] = (float)bounds2D.getWidth();
            array[n + 7] = (float)bounds2D.getHeight();
        }
        return array;
    }
    
    public void pixellate(FontRenderContext frc, final Point2D point2D, final Point point) {
        if (frc == null) {
            frc = this.frc;
        }
        final AffineTransform transform = frc.getTransform();
        transform.transform(point2D, point2D);
        point.x = (int)point2D.getX();
        point.y = (int)point2D.getY();
        point2D.setLocation(point.x, point.y);
        try {
            transform.inverseTransform(point2D, point2D);
        }
        catch (final NoninvertibleTransformException ex) {
            throw new IllegalArgumentException("must be able to invert frc transform");
        }
    }
    
    boolean needsPositions(final double[] array) {
        return this.gti != null || (this.getLayoutFlags() & 0x2) != 0x0 || !matchTX(array, this.frctx);
    }
    
    Object setupGlyphImages(final long[] array, final float[] array2, final double[] renderTransform) {
        this.initPositions();
        this.setRenderTransform(renderTransform);
        if (this.gti != null) {
            return this.gti.setupGlyphImages(array, array2, this.dtx);
        }
        final GlyphStrike defaultStrike = this.getDefaultStrike();
        defaultStrike.strike.getGlyphImagePtrs(this.glyphs, array, this.glyphs.length);
        if (array2 != null) {
            if (this.dtx.isIdentity()) {
                System.arraycopy(this.positions, 0, array2, 0, this.glyphs.length * 2);
            }
            else {
                this.dtx.transform(this.positions, 0, array2, 0, this.glyphs.length);
            }
        }
        return defaultStrike;
    }
    
    private static boolean matchTX(final double[] array, final AffineTransform affineTransform) {
        return array[0] == affineTransform.getScaleX() && array[1] == affineTransform.getShearY() && array[2] == affineTransform.getShearX() && array[3] == affineTransform.getScaleY();
    }
    
    private static AffineTransform getNonTranslateTX(AffineTransform affineTransform) {
        if (affineTransform.getTranslateX() != 0.0 || affineTransform.getTranslateY() != 0.0) {
            affineTransform = new AffineTransform(affineTransform.getScaleX(), affineTransform.getShearY(), affineTransform.getShearX(), affineTransform.getScaleY(), 0.0, 0.0);
        }
        return affineTransform;
    }
    
    private static boolean equalNonTranslateTX(final AffineTransform affineTransform, final AffineTransform affineTransform2) {
        return affineTransform.getScaleX() == affineTransform2.getScaleX() && affineTransform.getShearY() == affineTransform2.getShearY() && affineTransform.getShearX() == affineTransform2.getShearX() && affineTransform.getScaleY() == affineTransform2.getScaleY();
    }
    
    private void setRenderTransform(final double[] array) {
        assert array.length == 4;
        if (!matchTX(array, this.dtx)) {
            this.resetDTX(new AffineTransform(array));
        }
    }
    
    private final void setDTX(final AffineTransform affineTransform) {
        if (!equalNonTranslateTX(this.dtx, affineTransform)) {
            this.resetDTX(getNonTranslateTX(affineTransform));
        }
    }
    
    private final void setFRCTX() {
        if (!equalNonTranslateTX(this.frctx, this.dtx)) {
            this.resetDTX(getNonTranslateTX(this.frctx));
        }
    }
    
    private final void resetDTX(final AffineTransform dtx) {
        this.fsref = null;
        this.dtx = dtx;
        this.invdtx = null;
        if (!this.dtx.isIdentity()) {
            try {
                this.invdtx = this.dtx.createInverse();
            }
            catch (final NoninvertibleTransformException ex) {}
        }
        if (this.gti != null) {
            this.gti.strikesRef = null;
        }
    }
    
    private StandardGlyphVector(final GlyphVector glyphVector, final FontRenderContext frc) {
        this.font = glyphVector.getFont();
        this.frc = frc;
        this.initFontData();
        final int numGlyphs = glyphVector.getNumGlyphs();
        this.userGlyphs = glyphVector.getGlyphCodes(0, numGlyphs, null);
        if (glyphVector instanceof StandardGlyphVector) {
            this.glyphs = this.userGlyphs;
        }
        else {
            this.glyphs = this.getValidatedGlyphs(this.userGlyphs);
        }
        this.flags = (glyphVector.getLayoutFlags() & 0xF);
        if ((this.flags & 0x2) != 0x0) {
            this.positions = glyphVector.getGlyphPositions(0, numGlyphs + 1, null);
        }
        if ((this.flags & 0x8) != 0x0) {
            this.charIndices = glyphVector.getGlyphCharIndices(0, numGlyphs, null);
        }
        if ((this.flags & 0x1) != 0x0) {
            final AffineTransform[] glyphTransforms = new AffineTransform[numGlyphs];
            for (int i = 0; i < numGlyphs; ++i) {
                glyphTransforms[i] = glyphVector.getGlyphTransform(i);
            }
            this.setGlyphTransforms(glyphTransforms);
        }
    }
    
    int[] getValidatedGlyphs(final int[] array) {
        final int length = array.length;
        final int[] array2 = new int[length];
        for (int i = 0; i < length; ++i) {
            if (array[i] == 65534 || array[i] == 65535) {
                array2[i] = array[i];
            }
            else {
                array2[i] = this.font2D.getValidatedGlyphCode(array[i]);
            }
        }
        return array2;
    }
    
    private void init(final Font font, char[] array, final int n, final int n2, final FontRenderContext frc, final int flags) {
        if (n < 0 || n2 < 0 || n + n2 > array.length) {
            throw new ArrayIndexOutOfBoundsException("start or count out of bounds");
        }
        this.font = font;
        this.frc = frc;
        this.flags = flags;
        if (this.getTracking(font) != 0.0f) {
            this.addFlags(2);
        }
        if (n != 0) {
            final char[] array2 = new char[n2];
            System.arraycopy(array, n, array2, 0, n2);
            array = array2;
        }
        this.initFontData();
        this.glyphs = new int[n2];
        this.userGlyphs = this.glyphs;
        this.font2D.getMapper().charsToGlyphs(n2, array, this.glyphs);
    }
    
    private void initFontData() {
        this.font2D = FontUtilities.getFont2D(this.font);
        if (this.font2D instanceof FontSubstitution) {
            this.font2D = ((FontSubstitution)this.font2D).getCompositeFont2D();
        }
        final float size2D = this.font.getSize2D();
        if (this.font.isTransformed()) {
            this.ftx = this.font.getTransform();
            if (this.ftx.getTranslateX() != 0.0 || this.ftx.getTranslateY() != 0.0) {
                this.addFlags(2);
            }
            this.ftx.setTransform(this.ftx.getScaleX(), this.ftx.getShearY(), this.ftx.getShearX(), this.ftx.getScaleY(), 0.0, 0.0);
            this.ftx.scale(size2D, size2D);
        }
        else {
            this.ftx = AffineTransform.getScaleInstance(size2D, size2D);
        }
        this.frctx = this.frc.getTransform();
        this.resetDTX(getNonTranslateTX(this.frctx));
    }
    
    private float[] internalGetGlyphPositions(final int n, final int n2, final int n3, float[] array) {
        if (array == null) {
            array = new float[n3 + n2 * 2];
        }
        this.initPositions();
        for (int i = n3, n4 = n3 + n2 * 2, n5 = n * 2; i < n4; ++i, ++n5) {
            array[i] = this.positions[n5];
        }
        return array;
    }
    
    private Rectangle2D getGlyphOutlineBounds(final int n) {
        this.setFRCTX();
        this.initPositions();
        return this.getGlyphStrike(n).getGlyphOutlineBounds(this.glyphs[n], this.positions[n * 2], this.positions[n * 2 + 1]);
    }
    
    private Shape getGlyphsOutline(final int n, final int n2, final float n3, final float n4) {
        this.setFRCTX();
        this.initPositions();
        final GeneralPath generalPath = new GeneralPath(1);
        for (int i = n, n5 = n + n2, n6 = n * 2; i < n5; ++i, n6 += 2) {
            this.getGlyphStrike(i).appendGlyphOutline(this.glyphs[i], generalPath, n3 + this.positions[n6], n4 + this.positions[n6 + 1]);
        }
        return generalPath;
    }
    
    private Rectangle getGlyphsPixelBounds(final FontRenderContext fontRenderContext, final float n, final float n2, int n3, int n4) {
        this.initPositions();
        AffineTransform dtx;
        if (fontRenderContext == null || fontRenderContext.equals(this.frc)) {
            dtx = this.frctx;
        }
        else {
            dtx = fontRenderContext.getTransform();
        }
        this.setDTX(dtx);
        if (this.gti != null) {
            return this.gti.getGlyphsPixelBounds(dtx, n, n2, n3, n4);
        }
        final FontStrike strike = this.getDefaultStrike().strike;
        Rectangle rectangle = null;
        final Rectangle rectangle2 = new Rectangle();
        final Point2D.Float float1 = new Point2D.Float();
        int n5 = n3 * 2;
        while (--n4 >= 0) {
            float1.x = n + this.positions[n5++];
            float1.y = n2 + this.positions[n5++];
            dtx.transform(float1, float1);
            strike.getGlyphImageBounds(this.glyphs[n3++], float1, rectangle2);
            if (!rectangle2.isEmpty()) {
                if (rectangle == null) {
                    rectangle = new Rectangle(rectangle2);
                }
                else {
                    rectangle.add(rectangle2);
                }
            }
        }
        return (rectangle != null) ? rectangle : rectangle2;
    }
    
    private void clearCaches(final int n) {
        if (this.lbcacheRef != null) {
            final Shape[] array = this.lbcacheRef.get();
            if (array != null) {
                array[n] = null;
            }
        }
        if (this.vbcacheRef != null) {
            final Shape[] array2 = this.vbcacheRef.get();
            if (array2 != null) {
                array2[n] = null;
            }
        }
    }
    
    private void clearCaches() {
        this.lbcacheRef = null;
        this.vbcacheRef = null;
    }
    
    private void initPositions() {
        if (this.positions == null) {
            this.setFRCTX();
            this.positions = new float[this.glyphs.length * 2 + 2];
            Point2D.Float float1 = null;
            final float tracking = this.getTracking(this.font);
            if (tracking != 0.0f) {
                float1 = new Point2D.Float(tracking * this.font.getSize2D(), 0.0f);
            }
            final Point2D.Float float2 = new Point2D.Float(0.0f, 0.0f);
            if (this.font.isTransformed()) {
                final AffineTransform transform = this.font.getTransform();
                transform.transform(float2, float2);
                this.positions[0] = float2.x;
                this.positions[1] = float2.y;
                if (float1 != null) {
                    transform.deltaTransform(float1, float1);
                }
            }
            for (int i = 0, n = 2; i < this.glyphs.length; ++i, n += 2) {
                this.getGlyphStrike(i).addDefaultGlyphAdvance(this.glyphs[i], float2);
                if (float1 != null) {
                    final Point2D.Float float3 = float2;
                    float3.x += float1.x;
                    final Point2D.Float float4 = float2;
                    float4.y += float1.y;
                }
                this.positions[n] = float2.x;
                this.positions[n + 1] = float2.y;
            }
        }
    }
    
    private void addFlags(final int n) {
        this.flags = (this.getLayoutFlags() | n);
    }
    
    private void clearFlags(final int n) {
        this.flags = (this.getLayoutFlags() & ~n);
    }
    
    private GlyphStrike getGlyphStrike(final int n) {
        if (this.gti == null) {
            return this.getDefaultStrike();
        }
        return this.gti.getStrike(n);
    }
    
    private GlyphStrike getDefaultStrike() {
        GlyphStrike create = null;
        if (this.fsref != null) {
            create = this.fsref.get();
        }
        if (create == null) {
            create = GlyphStrike.create(this, this.dtx, null);
            this.fsref = new SoftReference(create);
        }
        return create;
    }
    
    @Override
    public String toString() {
        return this.appendString(null).toString();
    }
    
    StringBuffer appendString(StringBuffer sb) {
        if (sb == null) {
            sb = new StringBuffer();
        }
        try {
            sb.append("SGV{font: ");
            sb.append(this.font.toString());
            sb.append(", frc: ");
            sb.append(this.frc.toString());
            sb.append(", glyphs: (");
            sb.append(this.glyphs.length);
            sb.append(")[");
            for (int i = 0; i < this.glyphs.length; ++i) {
                if (i > 0) {
                    sb.append(", ");
                }
                sb.append(Integer.toHexString(this.glyphs[i]));
            }
            sb.append("]");
            if (this.positions != null) {
                sb.append(", positions: (");
                sb.append(this.positions.length);
                sb.append(")[");
                for (int j = 0; j < this.positions.length; j += 2) {
                    if (j > 0) {
                        sb.append(", ");
                    }
                    sb.append(this.positions[j]);
                    sb.append("@");
                    sb.append(this.positions[j + 1]);
                }
                sb.append("]");
            }
            if (this.charIndices != null) {
                sb.append(", indices: (");
                sb.append(this.charIndices.length);
                sb.append(")[");
                for (int k = 0; k < this.charIndices.length; ++k) {
                    if (k > 0) {
                        sb.append(", ");
                    }
                    sb.append(this.charIndices[k]);
                }
                sb.append("]");
            }
            sb.append(", flags:");
            if (this.getLayoutFlags() == 0) {
                sb.append(" default");
            }
            else {
                if ((this.flags & 0x1) != 0x0) {
                    sb.append(" tx");
                }
                if ((this.flags & 0x2) != 0x0) {
                    sb.append(" pos");
                }
                if ((this.flags & 0x4) != 0x0) {
                    sb.append(" rtl");
                }
                if ((this.flags & 0x8) != 0x0) {
                    sb.append(" complex");
                }
            }
        }
        catch (final Exception ex) {
            sb.append(" " + ex.getMessage());
        }
        sb.append("}");
        return sb;
    }
    
    static final class GlyphTransformInfo
    {
        StandardGlyphVector sgv;
        int[] indices;
        double[] transforms;
        SoftReference strikesRef;
        boolean haveAllStrikes;
        
        GlyphTransformInfo(final StandardGlyphVector sgv) {
            this.sgv = sgv;
        }
        
        GlyphTransformInfo(final StandardGlyphVector sgv, final GlyphTransformInfo glyphTransformInfo) {
            this.sgv = sgv;
            this.indices = (int[])((glyphTransformInfo.indices == null) ? null : ((int[])glyphTransformInfo.indices.clone()));
            this.transforms = (double[])((glyphTransformInfo.transforms == null) ? null : ((double[])glyphTransformInfo.transforms.clone()));
            this.strikesRef = null;
        }
        
        public boolean equals(final GlyphTransformInfo glyphTransformInfo) {
            if (glyphTransformInfo == null) {
                return false;
            }
            if (glyphTransformInfo == this) {
                return true;
            }
            if (this.indices.length != glyphTransformInfo.indices.length) {
                return false;
            }
            if (this.transforms.length != glyphTransformInfo.transforms.length) {
                return false;
            }
            for (int i = 0; i < this.indices.length; ++i) {
                final int n = this.indices[i];
                final int n2 = glyphTransformInfo.indices[i];
                if (n == 0 != (n2 == 0)) {
                    return false;
                }
                if (n != 0) {
                    int n3 = n * 6;
                    int n4 = n2 * 6;
                    for (int j = 6; j > 0; --j) {
                        if (this.indices[--n3] != glyphTransformInfo.indices[--n4]) {
                            return false;
                        }
                    }
                }
            }
            return true;
        }
        
        void setGlyphTransform(final int n, final AffineTransform affineTransform) {
            final double[] transforms = new double[6];
            boolean b = true;
            if (affineTransform == null || affineTransform.isIdentity()) {
                transforms[0] = (transforms[3] = 1.0);
            }
            else {
                b = false;
                affineTransform.getMatrix(transforms);
            }
            if (this.indices == null) {
                if (b) {
                    return;
                }
                (this.indices = new int[this.sgv.glyphs.length])[n] = 1;
                this.transforms = transforms;
            }
            else {
                boolean b2 = false;
                int n2;
                if (b) {
                    n2 = 0;
                }
                else {
                    b2 = true;
                    int i = 0;
                Label_0103:
                    while (i < this.transforms.length) {
                        for (int j = 0; j < 6; ++j) {
                            if (this.transforms[i + j] != transforms[j]) {
                                i += 6;
                                continue Label_0103;
                            }
                        }
                        b2 = false;
                        break;
                    }
                    n2 = i / 6 + 1;
                }
                final int n3 = this.indices[n];
                if (n2 != n3) {
                    boolean b3 = false;
                    if (n3 != 0) {
                        b3 = true;
                        for (int k = 0; k < this.indices.length; ++k) {
                            if (this.indices[k] == n3 && k != n) {
                                b3 = false;
                                break;
                            }
                        }
                    }
                    if (b3 && b2) {
                        n2 = n3;
                        System.arraycopy(transforms, 0, this.transforms, (n2 - 1) * 6, 6);
                    }
                    else if (b3) {
                        if (this.transforms.length == 6) {
                            this.indices = null;
                            this.transforms = null;
                            this.sgv.clearCaches(n);
                            this.sgv.clearFlags(1);
                            this.strikesRef = null;
                            return;
                        }
                        final double[] transforms2 = new double[this.transforms.length - 6];
                        System.arraycopy(this.transforms, 0, transforms2, 0, (n3 - 1) * 6);
                        System.arraycopy(this.transforms, n3 * 6, transforms2, (n3 - 1) * 6, this.transforms.length - n3 * 6);
                        this.transforms = transforms2;
                        for (int l = 0; l < this.indices.length; ++l) {
                            if (this.indices[l] > n3) {
                                final int[] indices = this.indices;
                                final int n4 = l;
                                --indices[n4];
                            }
                        }
                        if (n2 > n3) {
                            --n2;
                        }
                    }
                    else if (b2) {
                        final double[] transforms3 = new double[this.transforms.length + 6];
                        System.arraycopy(this.transforms, 0, transforms3, 0, this.transforms.length);
                        System.arraycopy(transforms, 0, transforms3, this.transforms.length, 6);
                        this.transforms = transforms3;
                    }
                    this.indices[n] = n2;
                }
            }
            this.sgv.clearCaches(n);
            this.sgv.addFlags(1);
            this.strikesRef = null;
        }
        
        AffineTransform getGlyphTransform(final int n) {
            final int n2 = this.indices[n];
            if (n2 == 0) {
                return null;
            }
            final int n3 = (n2 - 1) * 6;
            return new AffineTransform(this.transforms[n3 + 0], this.transforms[n3 + 1], this.transforms[n3 + 2], this.transforms[n3 + 3], this.transforms[n3 + 4], this.transforms[n3 + 5]);
        }
        
        int transformCount() {
            if (this.transforms == null) {
                return 0;
            }
            return this.transforms.length / 6;
        }
        
        Object setupGlyphImages(final long[] array, final float[] array2, final AffineTransform affineTransform) {
            final int length = this.sgv.glyphs.length;
            final GlyphStrike[] allStrikes = this.getAllStrikes();
            for (int i = 0; i < length; ++i) {
                final GlyphStrike glyphStrike = allStrikes[this.indices[i]];
                final int n = this.sgv.glyphs[i];
                array[i] = glyphStrike.strike.getGlyphImagePtr(n);
                glyphStrike.getGlyphPosition(n, i * 2, this.sgv.positions, array2);
            }
            affineTransform.transform(array2, 0, array2, 0, length);
            return allStrikes;
        }
        
        Rectangle getGlyphsPixelBounds(final AffineTransform affineTransform, final float n, final float n2, int n3, int n4) {
            Rectangle rectangle = null;
            final Rectangle rectangle2 = new Rectangle();
            final Point2D.Float float1 = new Point2D.Float();
            int n5 = n3 * 2;
            while (--n4 >= 0) {
                final GlyphStrike strike = this.getStrike(n3);
                float1.x = n + this.sgv.positions[n5++] + strike.dx;
                float1.y = n2 + this.sgv.positions[n5++] + strike.dy;
                affineTransform.transform(float1, float1);
                strike.strike.getGlyphImageBounds(this.sgv.glyphs[n3++], float1, rectangle2);
                if (!rectangle2.isEmpty()) {
                    if (rectangle == null) {
                        rectangle = new Rectangle(rectangle2);
                    }
                    else {
                        rectangle.add(rectangle2);
                    }
                }
            }
            return (rectangle != null) ? rectangle : rectangle2;
        }
        
        GlyphStrike getStrike(final int n) {
            if (this.indices != null) {
                return this.getStrikeAtIndex(this.getStrikeArray(), this.indices[n]);
            }
            return this.sgv.getDefaultStrike();
        }
        
        private GlyphStrike[] getAllStrikes() {
            if (this.indices == null) {
                return null;
            }
            final GlyphStrike[] strikeArray = this.getStrikeArray();
            if (!this.haveAllStrikes) {
                for (int i = 0; i < strikeArray.length; ++i) {
                    this.getStrikeAtIndex(strikeArray, i);
                }
                this.haveAllStrikes = true;
            }
            return strikeArray;
        }
        
        private GlyphStrike[] getStrikeArray() {
            GlyphStrike[] array = null;
            if (this.strikesRef != null) {
                array = this.strikesRef.get();
            }
            if (array == null) {
                this.haveAllStrikes = false;
                array = new GlyphStrike[this.transformCount() + 1];
                this.strikesRef = new SoftReference(array);
            }
            return array;
        }
        
        private GlyphStrike getStrikeAtIndex(final GlyphStrike[] array, final int n) {
            GlyphStrike glyphStrike = array[n];
            if (glyphStrike == null) {
                if (n == 0) {
                    glyphStrike = this.sgv.getDefaultStrike();
                }
                else {
                    final int n2 = (n - 1) * 6;
                    glyphStrike = GlyphStrike.create(this.sgv, this.sgv.dtx, new AffineTransform(this.transforms[n2], this.transforms[n2 + 1], this.transforms[n2 + 2], this.transforms[n2 + 3], this.transforms[n2 + 4], this.transforms[n2 + 5]));
                }
                array[n] = glyphStrike;
            }
            return glyphStrike;
        }
    }
    
    public static final class GlyphStrike
    {
        StandardGlyphVector sgv;
        FontStrike strike;
        float dx;
        float dy;
        
        static GlyphStrike create(final StandardGlyphVector standardGlyphVector, final AffineTransform affineTransform, final AffineTransform affineTransform2) {
            float n = 0.0f;
            float n2 = 0.0f;
            AffineTransform access$700 = standardGlyphVector.ftx;
            if (!affineTransform.isIdentity() || affineTransform2 != null) {
                access$700 = new AffineTransform(standardGlyphVector.ftx);
                if (affineTransform2 != null) {
                    access$700.preConcatenate(affineTransform2);
                    n = (float)access$700.getTranslateX();
                    n2 = (float)access$700.getTranslateY();
                }
                if (!affineTransform.isIdentity()) {
                    access$700.preConcatenate(affineTransform);
                }
            }
            int n3 = 1;
            final Object antiAliasingHint = standardGlyphVector.frc.getAntiAliasingHint();
            if (antiAliasingHint == RenderingHints.VALUE_TEXT_ANTIALIAS_GASP && !access$700.isIdentity() && (access$700.getType() & 0xFFFFFFFE) != 0x0) {
                final double shearX = access$700.getShearX();
                if (shearX != 0.0) {
                    final double scaleY = access$700.getScaleY();
                    n3 = (int)Math.sqrt(shearX * shearX + scaleY * scaleY);
                }
                else {
                    n3 = (int)Math.abs(access$700.getScaleY());
                }
            }
            final FontStrikeDesc fontStrikeDesc = new FontStrikeDesc(affineTransform, access$700, standardGlyphVector.font.getStyle(), FontStrikeDesc.getAAHintIntVal(antiAliasingHint, standardGlyphVector.font2D, n3), FontStrikeDesc.getFMHintIntVal(standardGlyphVector.frc.getFractionalMetricsHint()));
            Font2D font2D = standardGlyphVector.font2D;
            if (font2D instanceof FontSubstitution) {
                font2D = ((FontSubstitution)font2D).getCompositeFont2D();
            }
            return new GlyphStrike(standardGlyphVector, font2D.handle.font2D.getStrike(fontStrikeDesc), n, n2);
        }
        
        private GlyphStrike(final StandardGlyphVector sgv, final FontStrike strike, final float dx, final float dy) {
            this.sgv = sgv;
            this.strike = strike;
            this.dx = dx;
            this.dy = dy;
        }
        
        void getADL(final ADL adl) {
            final StrikeMetrics fontMetrics = this.strike.getFontMetrics();
            if (this.sgv.font.isTransformed()) {
                final Point2D.Float float1 = new Point2D.Float();
                float1.x = (float)this.sgv.font.getTransform().getTranslateX();
                float1.y = (float)this.sgv.font.getTransform().getTranslateY();
            }
            adl.ascentX = -fontMetrics.ascentX;
            adl.ascentY = -fontMetrics.ascentY;
            adl.descentX = fontMetrics.descentX;
            adl.descentY = fontMetrics.descentY;
            adl.leadingX = fontMetrics.leadingX;
            adl.leadingY = fontMetrics.leadingY;
        }
        
        void getGlyphPosition(final int n, int n2, final float[] array, final float[] array2) {
            array2[n2] = array[n2] + this.dx;
            ++n2;
            array2[n2] = array[n2] + this.dy;
        }
        
        void addDefaultGlyphAdvance(final int n, final Point2D.Float float1) {
            final Point2D.Float glyphMetrics = this.strike.getGlyphMetrics(n);
            float1.x += glyphMetrics.x + this.dx;
            float1.y += glyphMetrics.y + this.dy;
        }
        
        Rectangle2D getGlyphOutlineBounds(final int n, final float n2, final float n3) {
            Rectangle2D bounds2D;
            if (this.sgv.invdtx == null) {
                bounds2D = new Rectangle2D.Float();
                bounds2D.setRect(this.strike.getGlyphOutlineBounds(n));
            }
            else {
                final GeneralPath glyphOutline = this.strike.getGlyphOutline(n, 0.0f, 0.0f);
                glyphOutline.transform(this.sgv.invdtx);
                bounds2D = glyphOutline.getBounds2D();
            }
            if (!bounds2D.isEmpty()) {
                bounds2D.setRect(bounds2D.getMinX() + n2 + this.dx, bounds2D.getMinY() + n3 + this.dy, bounds2D.getWidth(), bounds2D.getHeight());
            }
            return bounds2D;
        }
        
        void appendGlyphOutline(final int n, final GeneralPath generalPath, final float n2, final float n3) {
            GeneralPath generalPath2;
            if (this.sgv.invdtx == null) {
                generalPath2 = this.strike.getGlyphOutline(n, n2 + this.dx, n3 + this.dy);
            }
            else {
                generalPath2 = this.strike.getGlyphOutline(n, 0.0f, 0.0f);
                generalPath2.transform(this.sgv.invdtx);
                generalPath2.transform(AffineTransform.getTranslateInstance(n2 + this.dx, n3 + this.dy));
            }
            generalPath.append(generalPath2.getPathIterator(null), false);
        }
    }
    
    static class ADL
    {
        public float ascentX;
        public float ascentY;
        public float descentX;
        public float descentY;
        public float leadingX;
        public float leadingY;
        
        @Override
        public String toString() {
            return this.toStringBuffer(null).toString();
        }
        
        protected StringBuffer toStringBuffer(StringBuffer sb) {
            if (sb == null) {
                sb = new StringBuffer();
            }
            sb.append("ax: ");
            sb.append(this.ascentX);
            sb.append(" ay: ");
            sb.append(this.ascentY);
            sb.append(" dx: ");
            sb.append(this.descentX);
            sb.append(" dy: ");
            sb.append(this.descentY);
            sb.append(" lx: ");
            sb.append(this.leadingX);
            sb.append(" ly: ");
            sb.append(this.leadingY);
            return sb;
        }
    }
}
