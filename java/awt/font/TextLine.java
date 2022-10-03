package java.awt.font;

import sun.font.FontResolver;
import java.text.CharacterIterator;
import sun.text.CodePointIterator;
import sun.font.GraphicComponent;
import sun.font.ExtendedTextLabel;
import sun.font.Decoration;
import sun.font.TextLabelFactory;
import java.text.Bidi;
import sun.font.AttributeValues;
import java.text.AttributedCharacterIterator;
import java.util.Map;
import java.awt.Font;
import java.awt.geom.GeneralPath;
import sun.font.BidiUtils;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import sun.font.CoreMetrics;
import java.awt.geom.Point2D;
import java.awt.Rectangle;
import sun.font.LayoutPathImpl;
import sun.font.TextLineComponent;

final class TextLine
{
    private TextLineComponent[] fComponents;
    private float[] fBaselineOffsets;
    private int[] fComponentVisualOrder;
    private float[] locs;
    private char[] fChars;
    private int fCharsStart;
    private int fCharsLimit;
    private int[] fCharVisualOrder;
    private int[] fCharLogicalOrder;
    private byte[] fCharLevels;
    private boolean fIsDirectionLTR;
    private LayoutPathImpl lp;
    private boolean isSimple;
    private Rectangle pixelBounds;
    private FontRenderContext frc;
    private TextLineMetrics fMetrics;
    private static Function fgPosAdvF;
    private static Function fgAdvanceF;
    private static Function fgXPositionF;
    private static Function fgYPositionF;
    
    public TextLine(final FontRenderContext frc, final TextLineComponent[] fComponents, final float[] fBaselineOffsets, final char[] fChars, final int fCharsStart, final int fCharsLimit, final int[] fCharLogicalOrder, final byte[] fCharLevels, final boolean fIsDirectionLTR) {
        this.fMetrics = null;
        final int[] computeComponentOrder = computeComponentOrder(fComponents, fCharLogicalOrder);
        this.frc = frc;
        this.fComponents = fComponents;
        this.fBaselineOffsets = fBaselineOffsets;
        this.fComponentVisualOrder = computeComponentOrder;
        this.fChars = fChars;
        this.fCharsStart = fCharsStart;
        this.fCharsLimit = fCharsLimit;
        this.fCharLogicalOrder = fCharLogicalOrder;
        this.fCharLevels = fCharLevels;
        this.fIsDirectionLTR = fIsDirectionLTR;
        this.checkCtorArgs();
        this.init();
    }
    
    private void checkCtorArgs() {
        int n = 0;
        for (int i = 0; i < this.fComponents.length; ++i) {
            n += this.fComponents[i].getNumCharacters();
        }
        if (n != this.characterCount()) {
            throw new IllegalArgumentException("Invalid TextLine!  char count is different from sum of char counts of components.");
        }
    }
    
    private void init() {
        float max = 0.0f;
        float max2 = 0.0f;
        float max3 = 0.0f;
        float max4 = 0.0f;
        float max5 = 0.0f;
        boolean b = false;
        this.isSimple = true;
        for (int i = 0; i < this.fComponents.length; ++i) {
            final TextLineComponent textLineComponent = this.fComponents[i];
            this.isSimple &= textLineComponent.isSimple();
            final CoreMetrics coreMetrics = textLineComponent.getCoreMetrics();
            final byte b2 = (byte)coreMetrics.baselineIndex;
            if (b2 >= 0) {
                final float n = this.fBaselineOffsets[b2];
                max = Math.max(max, -n + coreMetrics.ascent);
                final float n2 = n + coreMetrics.descent;
                max2 = Math.max(max2, n2);
                max3 = Math.max(max3, n2 + coreMetrics.leading);
            }
            else {
                b = true;
                final float n3 = coreMetrics.ascent + coreMetrics.descent;
                final float n4 = n3 + coreMetrics.leading;
                max4 = Math.max(max4, n3);
                max5 = Math.max(max5, n4);
            }
        }
        if (b) {
            if (max4 > max + max2) {
                max2 = max4 - max;
            }
            if (max5 > max + max3) {
                max3 = max5 - max;
            }
        }
        final float n5 = max3 - max2;
        if (b) {
            this.fBaselineOffsets = new float[] { this.fBaselineOffsets[0], this.fBaselineOffsets[1], this.fBaselineOffsets[2], max2, -max };
        }
        float n6 = 0.0f;
        CoreMetrics coreMetrics2 = null;
        boolean b3 = false;
        this.locs = new float[this.fComponents.length * 2 + 2];
        for (int j = 0, n7 = 0; j < this.fComponents.length; ++j, n7 += 2) {
            final TextLineComponent textLineComponent2 = this.fComponents[this.getComponentLogicalIndex(j)];
            final CoreMetrics coreMetrics3 = textLineComponent2.getCoreMetrics();
            float effectiveBaselineOffset3;
            if (coreMetrics2 != null && (coreMetrics2.italicAngle != 0.0f || coreMetrics3.italicAngle != 0.0f) && (coreMetrics2.italicAngle != coreMetrics3.italicAngle || coreMetrics2.baselineIndex != coreMetrics3.baselineIndex || coreMetrics2.ssOffset != coreMetrics3.ssOffset)) {
                final float effectiveBaselineOffset = coreMetrics2.effectiveBaselineOffset(this.fBaselineOffsets);
                final float n8 = effectiveBaselineOffset - coreMetrics2.ascent;
                final float n9 = effectiveBaselineOffset + coreMetrics2.descent;
                final float effectiveBaselineOffset2 = coreMetrics3.effectiveBaselineOffset(this.fBaselineOffsets);
                final float n10 = effectiveBaselineOffset2 - coreMetrics3.ascent;
                final float n11 = effectiveBaselineOffset2 + coreMetrics3.descent;
                final float max6 = Math.max(n8, n10);
                final float min = Math.min(n9, n11);
                n6 += Math.max(coreMetrics2.italicAngle * (effectiveBaselineOffset - max6) - coreMetrics3.italicAngle * (effectiveBaselineOffset2 - max6), coreMetrics2.italicAngle * (effectiveBaselineOffset - min) - coreMetrics3.italicAngle * (effectiveBaselineOffset2 - min));
                effectiveBaselineOffset3 = effectiveBaselineOffset2;
            }
            else {
                effectiveBaselineOffset3 = coreMetrics3.effectiveBaselineOffset(this.fBaselineOffsets);
            }
            this.locs[n7] = n6;
            this.locs[n7 + 1] = effectiveBaselineOffset3;
            n6 += textLineComponent2.getAdvance();
            coreMetrics2 = coreMetrics3;
            b3 |= (textLineComponent2.getBaselineTransform() != null);
        }
        if (coreMetrics2.italicAngle != 0.0f) {
            final float effectiveBaselineOffset4 = coreMetrics2.effectiveBaselineOffset(this.fBaselineOffsets);
            final float n12 = effectiveBaselineOffset4 - coreMetrics2.ascent;
            final float n13 = effectiveBaselineOffset4 + coreMetrics2.descent;
            final float n14 = effectiveBaselineOffset4 + coreMetrics2.ssOffset;
            float n15;
            if (coreMetrics2.italicAngle > 0.0f) {
                n15 = n14 + coreMetrics2.ascent;
            }
            else {
                n15 = n14 - coreMetrics2.descent;
            }
            n6 += n15 * coreMetrics2.italicAngle;
        }
        this.locs[this.locs.length - 2] = n6;
        this.fMetrics = new TextLineMetrics(max, max2, n5, n6);
        if (b3) {
            this.isSimple = false;
            final Point2D.Double double1 = new Point2D.Double();
            double n16 = 0.0;
            double n17 = 0.0;
            final LayoutPathImpl.SegmentPathBuilder segmentPathBuilder = new LayoutPathImpl.SegmentPathBuilder();
            segmentPathBuilder.moveTo(this.locs[0], 0.0);
            for (int k = 0, n18 = 0; k < this.fComponents.length; ++k, n18 += 2) {
                final AffineTransform baselineTransform = this.fComponents[this.getComponentLogicalIndex(k)].getBaselineTransform();
                if (baselineTransform != null && (baselineTransform.getType() & 0x1) != 0x0) {
                    segmentPathBuilder.moveTo(n16 += baselineTransform.getTranslateX(), n17 += baselineTransform.getTranslateY());
                }
                double1.x = this.locs[n18 + 2] - this.locs[n18];
                double1.y = 0.0;
                if (baselineTransform != null) {
                    baselineTransform.deltaTransform(double1, double1);
                }
                segmentPathBuilder.lineTo(n16 += double1.x, n17 += double1.y);
            }
            this.lp = segmentPathBuilder.complete();
            if (this.lp == null) {
                final AffineTransform baselineTransform2 = this.fComponents[this.getComponentLogicalIndex(0)].getBaselineTransform();
                if (baselineTransform2 != null) {
                    this.lp = new LayoutPathImpl.EmptyPath(baselineTransform2);
                }
            }
        }
    }
    
    public Rectangle getPixelBounds(FontRenderContext fontRenderContext, final float n, final float n2) {
        Rectangle rectangle = null;
        if (fontRenderContext != null && fontRenderContext.equals(this.frc)) {
            fontRenderContext = null;
        }
        final int n3 = (int)Math.floor(n);
        final int n4 = (int)Math.floor(n2);
        final float n5 = n - n3;
        final float n6 = n2 - n4;
        final boolean b = fontRenderContext == null && n5 == 0.0f && n6 == 0.0f;
        if (b && this.pixelBounds != null) {
            final Rectangle rectangle3;
            final Rectangle rectangle2 = rectangle3 = new Rectangle(this.pixelBounds);
            rectangle3.x += n3;
            final Rectangle rectangle4 = rectangle2;
            rectangle4.y += n4;
            return rectangle2;
        }
        if (this.isSimple) {
            for (int i = 0, n7 = 0; i < this.fComponents.length; ++i, n7 += 2) {
                final Rectangle pixelBounds = this.fComponents[this.getComponentLogicalIndex(i)].getPixelBounds(fontRenderContext, this.locs[n7] + n5, this.locs[n7 + 1] + n6);
                if (!pixelBounds.isEmpty()) {
                    if (rectangle == null) {
                        rectangle = pixelBounds;
                    }
                    else {
                        rectangle.add(pixelBounds);
                    }
                }
            }
            if (rectangle == null) {
                rectangle = new Rectangle(0, 0, 0, 0);
            }
        }
        else {
            Rectangle2D rectangle2D = this.getVisualBounds();
            if (this.lp != null) {
                rectangle2D = this.lp.mapShape(rectangle2D).getBounds();
            }
            final Rectangle bounds = rectangle2D.getBounds();
            final BufferedImage bufferedImage = new BufferedImage(bounds.width + 6, bounds.height + 6, 2);
            final Graphics2D graphics = bufferedImage.createGraphics();
            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
            graphics.setColor(Color.BLACK);
            this.draw(graphics, n5 + 3.0f - bounds.x, n6 + 3.0f - bounds.y);
            final Rectangle computePixelBounds;
            rectangle = (computePixelBounds = computePixelBounds(bufferedImage));
            computePixelBounds.x -= 3 - bounds.x;
            final Rectangle rectangle5 = rectangle;
            rectangle5.y -= 3 - bounds.y;
        }
        if (b) {
            this.pixelBounds = new Rectangle(rectangle);
        }
        final Rectangle rectangle6 = rectangle;
        rectangle6.x += n3;
        final Rectangle rectangle7 = rectangle;
        rectangle7.y += n4;
        return rectangle;
    }
    
    static Rectangle computePixelBounds(final BufferedImage bufferedImage) {
        final int width = bufferedImage.getWidth();
        final int height = bufferedImage.getHeight();
        int n = -1;
        int n2 = -1;
        int n3 = width;
        int n4 = height;
        final int[] array = new int[width];
    Label_0083:
        while (++n2 < height) {
            bufferedImage.getRGB(0, n2, array.length, 1, array, 0, width);
            for (int i = 0; i < array.length; ++i) {
                if (array[i] != -1) {
                    break Label_0083;
                }
            }
        }
        final int[] array2 = new int[width];
    Label_0146:
        while (--n4 > n2) {
            bufferedImage.getRGB(0, n4, array2.length, 1, array2, 0, width);
            for (int j = 0; j < array2.length; ++j) {
                if (array2[j] != -1) {
                    break Label_0146;
                }
            }
        }
        ++n4;
    Label_0196:
        while (++n < n3) {
            for (int k = n2; k < n4; ++k) {
                if (bufferedImage.getRGB(n, k) != -1) {
                    break Label_0196;
                }
            }
        }
    Label_0244:
        while (--n3 > n) {
            for (int l = n2; l < n4; ++l) {
                if (bufferedImage.getRGB(n3, l) != -1) {
                    break Label_0244;
                }
            }
        }
        ++n3;
        return new Rectangle(n, n2, n3 - n, n4 - n2);
    }
    
    public int characterCount() {
        return this.fCharsLimit - this.fCharsStart;
    }
    
    public boolean isDirectionLTR() {
        return this.fIsDirectionLTR;
    }
    
    public TextLineMetrics getMetrics() {
        return this.fMetrics;
    }
    
    public int visualToLogical(final int n) {
        if (this.fCharLogicalOrder == null) {
            return n;
        }
        if (this.fCharVisualOrder == null) {
            this.fCharVisualOrder = BidiUtils.createInverseMap(this.fCharLogicalOrder);
        }
        return this.fCharVisualOrder[n];
    }
    
    public int logicalToVisual(final int n) {
        return (this.fCharLogicalOrder == null) ? n : this.fCharLogicalOrder[n];
    }
    
    public byte getCharLevel(final int n) {
        return (byte)((this.fCharLevels == null) ? 0 : this.fCharLevels[n]);
    }
    
    public boolean isCharLTR(final int n) {
        return (this.getCharLevel(n) & 0x1) == 0x0;
    }
    
    public int getCharType(final int n) {
        return Character.getType(this.fChars[n + this.fCharsStart]);
    }
    
    public boolean isCharSpace(final int n) {
        return Character.isSpaceChar(this.fChars[n + this.fCharsStart]);
    }
    
    public boolean isCharWhitespace(final int n) {
        return Character.isWhitespace(this.fChars[n + this.fCharsStart]);
    }
    
    public float getCharAngle(final int n) {
        return this.getCoreMetricsAt(n).italicAngle;
    }
    
    public CoreMetrics getCoreMetricsAt(final int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Negative logicalIndex.");
        }
        if (n > this.fCharsLimit - this.fCharsStart) {
            throw new IllegalArgumentException("logicalIndex too large.");
        }
        int i = 0;
        int n2 = 0;
        do {
            n2 += this.fComponents[i].getNumCharacters();
            if (n2 > n) {
                break;
            }
            ++i;
        } while (i < this.fComponents.length);
        return this.fComponents[i].getCoreMetrics();
    }
    
    public float getCharAscent(final int n) {
        return this.getCoreMetricsAt(n).ascent;
    }
    
    public float getCharDescent(final int n) {
        return this.getCoreMetricsAt(n).descent;
    }
    
    public float getCharShift(final int n) {
        return this.getCoreMetricsAt(n).ssOffset;
    }
    
    private float applyFunctionAtIndex(final int n, final Function function) {
        if (n < 0) {
            throw new IllegalArgumentException("Negative logicalIndex.");
        }
        int n2 = 0;
        for (int i = 0; i < this.fComponents.length; ++i) {
            final int n3 = n2 + this.fComponents[i].getNumCharacters();
            if (n3 > n) {
                return function.computeFunction(this, i, n - n2);
            }
            n2 = n3;
        }
        throw new IllegalArgumentException("logicalIndex too large.");
    }
    
    public float getCharAdvance(final int n) {
        return this.applyFunctionAtIndex(n, TextLine.fgAdvanceF);
    }
    
    public float getCharXPosition(final int n) {
        return this.applyFunctionAtIndex(n, TextLine.fgXPositionF);
    }
    
    public float getCharYPosition(final int n) {
        return this.applyFunctionAtIndex(n, TextLine.fgYPositionF);
    }
    
    public float getCharLinePosition(final int n) {
        return this.getCharXPosition(n);
    }
    
    public float getCharLinePosition(final int n, final boolean b) {
        return this.applyFunctionAtIndex(n, (this.isCharLTR(n) == b) ? TextLine.fgXPositionF : TextLine.fgPosAdvF);
    }
    
    public boolean caretAtOffsetIsValid(final int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Negative offset.");
        }
        int n2 = 0;
        for (int i = 0; i < this.fComponents.length; ++i) {
            final int n3 = n2 + this.fComponents[i].getNumCharacters();
            if (n3 > n) {
                return this.fComponents[i].caretAtOffsetIsValid(n - n2);
            }
            n2 = n3;
        }
        throw new IllegalArgumentException("logicalIndex too large.");
    }
    
    private int getComponentLogicalIndex(final int n) {
        if (this.fComponentVisualOrder == null) {
            return n;
        }
        return this.fComponentVisualOrder[n];
    }
    
    private int getComponentVisualIndex(final int n) {
        if (this.fComponentVisualOrder == null) {
            return n;
        }
        for (int i = 0; i < this.fComponentVisualOrder.length; ++i) {
            if (this.fComponentVisualOrder[i] == n) {
                return i;
            }
        }
        throw new IndexOutOfBoundsException("bad component index: " + n);
    }
    
    public Rectangle2D getCharBounds(final int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Negative logicalIndex.");
        }
        int n2 = 0;
        for (int i = 0; i < this.fComponents.length; ++i) {
            final int n3 = n2 + this.fComponents[i].getNumCharacters();
            if (n3 > n) {
                final Rectangle2D charVisualBounds = this.fComponents[i].getCharVisualBounds(n - n2);
                final int componentVisualIndex = this.getComponentVisualIndex(i);
                charVisualBounds.setRect(charVisualBounds.getX() + this.locs[componentVisualIndex * 2], charVisualBounds.getY() + this.locs[componentVisualIndex * 2 + 1], charVisualBounds.getWidth(), charVisualBounds.getHeight());
                return charVisualBounds;
            }
            n2 = n3;
        }
        throw new IllegalArgumentException("logicalIndex too large.");
    }
    
    private float getComponentShift(final int n) {
        return this.fComponents[n].getCoreMetrics().effectiveBaselineOffset(this.fBaselineOffsets);
    }
    
    public void draw(final Graphics2D graphics2D, final float n, final float n2) {
        if (this.lp == null) {
            for (int i = 0, n3 = 0; i < this.fComponents.length; ++i, n3 += 2) {
                this.fComponents[this.getComponentLogicalIndex(i)].draw(graphics2D, this.locs[n3] + n, this.locs[n3 + 1] + n2);
            }
        }
        else {
            final AffineTransform transform = graphics2D.getTransform();
            final Point2D.Float float1 = new Point2D.Float();
            for (int j = 0, n4 = 0; j < this.fComponents.length; ++j, n4 += 2) {
                final TextLineComponent textLineComponent = this.fComponents[this.getComponentLogicalIndex(j)];
                this.lp.pathToPoint(this.locs[n4], this.locs[n4 + 1], false, float1);
                final Point2D.Float float2 = float1;
                float2.x += n;
                final Point2D.Float float3 = float1;
                float3.y += n2;
                final AffineTransform baselineTransform = textLineComponent.getBaselineTransform();
                if (baselineTransform != null) {
                    graphics2D.translate(float1.x - baselineTransform.getTranslateX(), float1.y - baselineTransform.getTranslateY());
                    graphics2D.transform(baselineTransform);
                    textLineComponent.draw(graphics2D, 0.0f, 0.0f);
                    graphics2D.setTransform(transform);
                }
                else {
                    textLineComponent.draw(graphics2D, float1.x, float1.y);
                }
            }
        }
    }
    
    public Rectangle2D getVisualBounds() {
        Rectangle2D rectangle2D = null;
        for (int i = 0, n = 0; i < this.fComponents.length; ++i, n += 2) {
            final TextLineComponent textLineComponent = this.fComponents[this.getComponentLogicalIndex(i)];
            Rectangle2D rectangle2D2 = textLineComponent.getVisualBounds();
            final Point2D.Float float1 = new Point2D.Float(this.locs[n], this.locs[n + 1]);
            if (this.lp == null) {
                rectangle2D2.setRect(rectangle2D2.getMinX() + float1.x, rectangle2D2.getMinY() + float1.y, rectangle2D2.getWidth(), rectangle2D2.getHeight());
            }
            else {
                this.lp.pathToPoint(float1, false, float1);
                final AffineTransform baselineTransform = textLineComponent.getBaselineTransform();
                if (baselineTransform != null) {
                    final AffineTransform translateInstance = AffineTransform.getTranslateInstance(float1.x - baselineTransform.getTranslateX(), float1.y - baselineTransform.getTranslateY());
                    translateInstance.concatenate(baselineTransform);
                    rectangle2D2 = translateInstance.createTransformedShape(rectangle2D2).getBounds2D();
                }
                else {
                    rectangle2D2.setRect(rectangle2D2.getMinX() + float1.x, rectangle2D2.getMinY() + float1.y, rectangle2D2.getWidth(), rectangle2D2.getHeight());
                }
            }
            if (rectangle2D == null) {
                rectangle2D = rectangle2D2;
            }
            else {
                rectangle2D.add(rectangle2D2);
            }
        }
        if (rectangle2D == null) {
            rectangle2D = new Rectangle2D.Float(Float.MAX_VALUE, Float.MAX_VALUE, Float.MIN_VALUE, Float.MIN_VALUE);
        }
        return rectangle2D;
    }
    
    public Rectangle2D getItalicBounds() {
        float min = Float.MAX_VALUE;
        float max = -3.4028235E38f;
        float min2 = Float.MAX_VALUE;
        float max2 = -3.4028235E38f;
        for (int i = 0, n = 0; i < this.fComponents.length; ++i, n += 2) {
            final Rectangle2D italicBounds = this.fComponents[this.getComponentLogicalIndex(i)].getItalicBounds();
            final float n2 = this.locs[n];
            final float n3 = this.locs[n + 1];
            min = Math.min(min, n2 + (float)italicBounds.getX());
            max = Math.max(max, n2 + (float)italicBounds.getMaxX());
            min2 = Math.min(min2, n3 + (float)italicBounds.getY());
            max2 = Math.max(max2, n3 + (float)italicBounds.getMaxY());
        }
        return new Rectangle2D.Float(min, min2, max - min, max2 - min2);
    }
    
    public Shape getOutline(final AffineTransform affineTransform) {
        final GeneralPath generalPath = new GeneralPath(1);
        for (int i = 0, n = 0; i < this.fComponents.length; ++i, n += 2) {
            generalPath.append(this.fComponents[this.getComponentLogicalIndex(i)].getOutline(this.locs[n], this.locs[n + 1]), false);
        }
        if (affineTransform != null) {
            generalPath.transform(affineTransform);
        }
        return generalPath;
    }
    
    @Override
    public int hashCode() {
        return this.fComponents.length << 16 ^ this.fComponents[0].hashCode() << 3 ^ this.fCharsLimit - this.fCharsStart;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < this.fComponents.length; ++i) {
            sb.append(this.fComponents[i]);
        }
        return sb.toString();
    }
    
    public static TextLine fastCreateTextLine(final FontRenderContext fontRenderContext, final char[] array, final Font font, final CoreMetrics coreMetrics, final Map<? extends AttributedCharacterIterator.Attribute, ?> map) {
        boolean baseIsLeftToRight = true;
        byte[] levels = null;
        int[] inverseMap = null;
        Bidi bidi = null;
        final int length = array.length;
        boolean requiresBidi = false;
        byte[] array2 = null;
        AttributeValues fromMap = null;
        if (map != null) {
            fromMap = AttributeValues.fromMap(map);
            if (fromMap.getRunDirection() >= 0) {
                baseIsLeftToRight = (fromMap.getRunDirection() == 0);
                requiresBidi = !baseIsLeftToRight;
            }
            if (fromMap.getBidiEmbedding() != 0) {
                requiresBidi = true;
                final byte b = (byte)fromMap.getBidiEmbedding();
                array2 = new byte[length];
                for (int i = 0; i < array2.length; ++i) {
                    array2[i] = b;
                }
            }
        }
        if (!requiresBidi) {
            requiresBidi = Bidi.requiresBidi(array, 0, array.length);
        }
        if (requiresBidi) {
            bidi = new Bidi(array, 0, array2, 0, array.length, (fromMap == null) ? -2 : fromMap.getRunDirection());
            if (!bidi.isLeftToRight()) {
                levels = BidiUtils.getLevels(bidi);
                inverseMap = BidiUtils.createInverseMap(BidiUtils.createVisualToLogicalMap(levels));
                baseIsLeftToRight = bidi.baseIsLeftToRight();
            }
        }
        TextLineComponent[] componentsOnRun;
        int length2;
        for (componentsOnRun = createComponentsOnRun(0, array.length, array, inverseMap, levels, new TextLabelFactory(fontRenderContext, array, bidi, 0), font, coreMetrics, fontRenderContext, Decoration.getDecoration(fromMap), new TextLineComponent[1], 0), length2 = componentsOnRun.length; componentsOnRun[length2 - 1] == null; --length2) {}
        if (length2 != componentsOnRun.length) {
            final TextLineComponent[] array3 = new TextLineComponent[length2];
            System.arraycopy(componentsOnRun, 0, array3, 0, length2);
            componentsOnRun = array3;
        }
        return new TextLine(fontRenderContext, componentsOnRun, coreMetrics.baselineOffsets, array, 0, array.length, inverseMap, levels, baseIsLeftToRight);
    }
    
    private static TextLineComponent[] expandArray(final TextLineComponent[] array) {
        final TextLineComponent[] array2 = new TextLineComponent[array.length + 8];
        System.arraycopy(array, 0, array2, 0, array.length);
        return array2;
    }
    
    public static TextLineComponent[] createComponentsOnRun(final int n, final int n2, final char[] array, final int[] array2, final byte[] array3, final TextLabelFactory textLabelFactory, final Font font, CoreMetrics value, final FontRenderContext fontRenderContext, final Decoration decoration, TextLineComponent[] expandArray, int n3) {
        int i = n;
        do {
            final int firstVisualChunk = firstVisualChunk(array2, array3, i, n2);
            do {
                final int n4 = i;
                int numChars;
                if (value == null) {
                    final LineMetrics lineMetrics = font.getLineMetrics(array, n4, firstVisualChunk, fontRenderContext);
                    value = CoreMetrics.get(lineMetrics);
                    numChars = lineMetrics.getNumChars();
                }
                else {
                    numChars = firstVisualChunk - n4;
                }
                final ExtendedTextLabel extended = textLabelFactory.createExtended(font, value, decoration, n4, n4 + numChars);
                if (++n3 >= expandArray.length) {
                    expandArray = expandArray(expandArray);
                }
                expandArray[n3 - 1] = extended;
                i += numChars;
            } while (i < firstVisualChunk);
        } while (i < n2);
        return expandArray;
    }
    
    public static TextLineComponent[] getComponents(final StyledParagraph styledParagraph, final char[] array, final int n, final int n2, final int[] array2, final byte[] array3, final TextLabelFactory textLabelFactory) {
        final FontRenderContext fontRenderContext = textLabelFactory.getFontRenderContext();
        int length = 0;
        TextLineComponent[] array4 = { null };
        int i = n;
        do {
            final int min = Math.min(styledParagraph.getRunLimit(i), n2);
            final Decoration decoration = styledParagraph.getDecorationAt(i);
            final Object fontOrGraphic = styledParagraph.getFontOrGraphicAt(i);
            if (fontOrGraphic instanceof GraphicAttribute) {
                final AffineTransform affineTransform = null;
                final GraphicAttribute graphicAttribute = (GraphicAttribute)fontOrGraphic;
                do {
                    final int firstVisualChunk = firstVisualChunk(array2, array3, i, min);
                    final GraphicComponent graphicComponent = new GraphicComponent(graphicAttribute, decoration, array2, array3, i, firstVisualChunk, affineTransform);
                    i = firstVisualChunk;
                    if (++length >= array4.length) {
                        array4 = expandArray(array4);
                    }
                    array4[length - 1] = graphicComponent;
                } while (i < min);
            }
            else {
                array4 = createComponentsOnRun(i, min, array, array2, array3, textLabelFactory, (Font)fontOrGraphic, null, fontRenderContext, decoration, array4, length);
                i = min;
                for (length = array4.length; array4[length - 1] == null; --length) {}
            }
        } while (i < n2);
        TextLineComponent[] array5;
        if (array4.length == length) {
            array5 = array4;
        }
        else {
            array5 = new TextLineComponent[length];
            System.arraycopy(array4, 0, array5, 0, length);
        }
        return array5;
    }
    
    public static TextLine createLineFromText(final char[] array, final StyledParagraph styledParagraph, final TextLabelFactory textLabelFactory, final boolean b, final float[] array2) {
        textLabelFactory.setLineContext(0, array.length);
        final Bidi lineBidi = textLabelFactory.getLineBidi();
        int[] inverseMap = null;
        byte[] levels = null;
        if (lineBidi != null) {
            levels = BidiUtils.getLevels(lineBidi);
            inverseMap = BidiUtils.createInverseMap(BidiUtils.createVisualToLogicalMap(levels));
        }
        return new TextLine(textLabelFactory.getFontRenderContext(), getComponents(styledParagraph, array, 0, array.length, inverseMap, levels, textLabelFactory), array2, array, 0, array.length, inverseMap, levels, b);
    }
    
    private static int[] computeComponentOrder(final TextLineComponent[] array, final int[] array2) {
        int[] inverseMap = null;
        if (array2 != null && array.length > 1) {
            final int[] array3 = new int[array.length];
            int n = 0;
            for (int i = 0; i < array.length; ++i) {
                array3[i] = array2[n];
                n += array[i].getNumCharacters();
            }
            inverseMap = BidiUtils.createInverseMap(BidiUtils.createContiguousOrder(array3));
        }
        return inverseMap;
    }
    
    public static TextLine standardCreateTextLine(final FontRenderContext fontRenderContext, final AttributedCharacterIterator attributedCharacterIterator, final char[] array, final float[] array2) {
        final StyledParagraph styledParagraph = new StyledParagraph(attributedCharacterIterator, array);
        Bidi bidi = new Bidi(attributedCharacterIterator);
        if (bidi.isLeftToRight()) {
            bidi = null;
        }
        final TextLabelFactory textLabelFactory = new TextLabelFactory(fontRenderContext, array, bidi, 0);
        boolean baseIsLeftToRight = true;
        if (bidi != null) {
            baseIsLeftToRight = bidi.baseIsLeftToRight();
        }
        return createLineFromText(array, styledParagraph, textLabelFactory, baseIsLeftToRight, array2);
    }
    
    static boolean advanceToFirstFont(final AttributedCharacterIterator attributedCharacterIterator) {
        for (char c = attributedCharacterIterator.first(); c != '\uffff'; c = attributedCharacterIterator.setIndex(attributedCharacterIterator.getRunLimit())) {
            if (attributedCharacterIterator.getAttribute(TextAttribute.CHAR_REPLACEMENT) == null) {
                return true;
            }
        }
        return false;
    }
    
    static float[] getNormalizedOffsets(float[] array, final byte b) {
        if (array[b] != 0.0f) {
            final float n = array[b];
            final float[] array2 = new float[array.length];
            for (int i = 0; i < array2.length; ++i) {
                array2[i] = array[i] - n;
            }
            array = array2;
        }
        return array;
    }
    
    static Font getFontAtCurrentPos(final AttributedCharacterIterator attributedCharacterIterator) {
        final Object attribute = attributedCharacterIterator.getAttribute(TextAttribute.FONT);
        if (attribute != null) {
            return (Font)attribute;
        }
        if (attributedCharacterIterator.getAttribute(TextAttribute.FAMILY) != null) {
            return Font.getFont(attributedCharacterIterator.getAttributes());
        }
        final int next = CodePointIterator.create(attributedCharacterIterator).next();
        if (next != -1) {
            final FontResolver instance = FontResolver.getInstance();
            return instance.getFont(instance.getFontIndex(next), attributedCharacterIterator.getAttributes());
        }
        return null;
    }
    
    private static int firstVisualChunk(final int[] array, final byte[] array2, int n, final int n2) {
        if (array != null && array2 != null) {
            final byte b = array2[n];
            while (++n < n2 && array2[n] == b) {}
            return n;
        }
        return n2;
    }
    
    public TextLine getJustifiedLine(final float n, final float n2, final int n3, final int n4) {
        final TextLineComponent[] array = new TextLineComponent[this.fComponents.length];
        System.arraycopy(this.fComponents, 0, array, 0, this.fComponents.length);
        boolean b = false;
        do {
            getAdvanceBetween(array, 0, this.characterCount());
            final float n5 = (n - getAdvanceBetween(array, n3, n4)) * n2;
            final int[] array2 = new int[array.length];
            int n6 = 0;
            for (int i = 0; i < array.length; ++i) {
                final int componentLogicalIndex = this.getComponentLogicalIndex(i);
                array2[componentLogicalIndex] = n6;
                n6 += array[componentLogicalIndex].getNumJustificationInfos();
            }
            final GlyphJustificationInfo[] array3 = new GlyphJustificationInfo[n6];
            final int n7 = 0;
            for (int j = 0; j < array.length; ++j) {
                final TextLineComponent textLineComponent = array[j];
                final int numCharacters = textLineComponent.getNumCharacters();
                final int n8 = n7 + numCharacters;
                if (n8 > n3) {
                    textLineComponent.getJustificationInfos(array3, array2[j], Math.max(0, n3 - n7), Math.min(numCharacters, n4 - n7));
                    if (n8 >= n4) {
                        break;
                    }
                }
            }
            int n9;
            int n10;
            for (n9 = 0, n10 = n6; n9 < n10 && array3[n9] == null; ++n9) {}
            while (n10 > n9 && array3[n10 - 1] == null) {
                --n10;
            }
            final float[] justify = new TextJustifier(array3, n9, n10).justify(n5);
            final boolean b2 = !b;
            boolean b3 = false;
            final boolean[] array4 = { false };
            final int n11 = 0;
            for (int k = 0; k < array.length; ++k) {
                final TextLineComponent textLineComponent2 = array[k];
                final int numCharacters2 = textLineComponent2.getNumCharacters();
                final int n12 = n11 + numCharacters2;
                if (n12 > n3) {
                    Math.max(0, n3 - n11);
                    Math.min(numCharacters2, n4 - n11);
                    array[k] = textLineComponent2.applyJustificationDeltas(justify, array2[k] * 2, array4);
                    b3 |= array4[0];
                    if (n12 >= n4) {
                        break;
                    }
                }
            }
            b = (b3 && !b);
        } while (b);
        return new TextLine(this.frc, array, this.fBaselineOffsets, this.fChars, this.fCharsStart, this.fCharsLimit, this.fCharLogicalOrder, this.fCharLevels, this.fIsDirectionLTR);
    }
    
    public static float getAdvanceBetween(final TextLineComponent[] array, final int n, final int n2) {
        float n3 = 0.0f;
        int n4 = 0;
        for (int i = 0; i < array.length; ++i) {
            final TextLineComponent textLineComponent = array[i];
            final int numCharacters = textLineComponent.getNumCharacters();
            final int n5 = n4 + numCharacters;
            if (n5 > n) {
                n3 += textLineComponent.getAdvanceBetween(Math.max(0, n - n4), Math.min(numCharacters, n2 - n4));
                if (n5 >= n2) {
                    break;
                }
            }
            n4 = n5;
        }
        return n3;
    }
    
    LayoutPathImpl getLayoutPath() {
        return this.lp;
    }
    
    static {
        TextLine.fgPosAdvF = new Function() {
            @Override
            float computeFunction(final TextLine textLine, final int n, final int n2) {
                final TextLineComponent textLineComponent = textLine.fComponents[n];
                return textLine.locs[textLine.getComponentVisualIndex(n) * 2] + textLineComponent.getCharX(n2) + textLineComponent.getCharAdvance(n2);
            }
        };
        TextLine.fgAdvanceF = new Function() {
            @Override
            float computeFunction(final TextLine textLine, final int n, final int n2) {
                return textLine.fComponents[n].getCharAdvance(n2);
            }
        };
        TextLine.fgXPositionF = new Function() {
            @Override
            float computeFunction(final TextLine textLine, final int n, final int n2) {
                return textLine.locs[textLine.getComponentVisualIndex(n) * 2] + textLine.fComponents[n].getCharX(n2);
            }
        };
        TextLine.fgYPositionF = new Function() {
            @Override
            float computeFunction(final TextLine textLine, final int n, final int n2) {
                return textLine.fComponents[n].getCharY(n2) + textLine.getComponentShift(n);
            }
        };
    }
    
    static final class TextLineMetrics
    {
        public final float ascent;
        public final float descent;
        public final float leading;
        public final float advance;
        
        public TextLineMetrics(final float ascent, final float descent, final float leading, final float advance) {
            this.ascent = ascent;
            this.descent = descent;
            this.leading = leading;
            this.advance = advance;
        }
    }
    
    private abstract static class Function
    {
        abstract float computeFunction(final TextLine p0, final int p1, final int p2);
    }
}
