package java.awt.font;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.AffineTransform;
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import sun.font.LayoutPathImpl;
import java.awt.Rectangle;
import sun.font.GraphicComponent;
import sun.font.AttributeValues;
import sun.font.CoreMetrics;
import sun.text.CodePointIterator;
import sun.font.FontResolver;
import java.util.Map;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.awt.Font;
import java.awt.geom.Rectangle2D;

public final class TextLayout implements Cloneable
{
    private int characterCount;
    private boolean isVerticalLine;
    private byte baseline;
    private float[] baselineOffsets;
    private TextLine textLine;
    private TextLine.TextLineMetrics lineMetrics;
    private float visibleAdvance;
    private int hashCodeCache;
    private boolean cacheIsValid;
    private float justifyRatio;
    private static final float ALREADY_JUSTIFIED = -53.9f;
    private static float dx;
    private static float dy;
    private Rectangle2D naturalBounds;
    private Rectangle2D boundsRect;
    private boolean caretsInLigaturesAreAllowed;
    public static final CaretPolicy DEFAULT_CARET_POLICY;
    
    public TextLayout(final String s, final Font font, final FontRenderContext fontRenderContext) {
        this.isVerticalLine = false;
        this.lineMetrics = null;
        this.cacheIsValid = false;
        this.naturalBounds = null;
        this.boundsRect = null;
        this.caretsInLigaturesAreAllowed = false;
        if (font == null) {
            throw new IllegalArgumentException("Null font passed to TextLayout constructor.");
        }
        if (s == null) {
            throw new IllegalArgumentException("Null string passed to TextLayout constructor.");
        }
        if (s.length() == 0) {
            throw new IllegalArgumentException("Zero length string passed to TextLayout constructor.");
        }
        Map<? extends AttributedCharacterIterator.Attribute, ?> attributes = null;
        if (font.hasLayoutAttributes()) {
            attributes = font.getAttributes();
        }
        final char[] charArray = s.toCharArray();
        if (sameBaselineUpTo(font, charArray, 0, charArray.length) == charArray.length) {
            this.fastInit(charArray, font, attributes, fontRenderContext);
        }
        else {
            final AttributedString attributedString = (attributes == null) ? new AttributedString(s) : new AttributedString(s, attributes);
            attributedString.addAttribute(TextAttribute.FONT, font);
            this.standardInit(attributedString.getIterator(), charArray, fontRenderContext);
        }
    }
    
    public TextLayout(final String s, final Map<? extends AttributedCharacterIterator.Attribute, ?> map, final FontRenderContext fontRenderContext) {
        this.isVerticalLine = false;
        this.lineMetrics = null;
        this.cacheIsValid = false;
        this.naturalBounds = null;
        this.boundsRect = null;
        this.caretsInLigaturesAreAllowed = false;
        if (s == null) {
            throw new IllegalArgumentException("Null string passed to TextLayout constructor.");
        }
        if (map == null) {
            throw new IllegalArgumentException("Null map passed to TextLayout constructor.");
        }
        if (s.length() == 0) {
            throw new IllegalArgumentException("Zero length string passed to TextLayout constructor.");
        }
        final char[] charArray = s.toCharArray();
        final Font singleFont = singleFont(charArray, 0, charArray.length, map);
        if (singleFont != null) {
            this.fastInit(charArray, singleFont, map, fontRenderContext);
        }
        else {
            this.standardInit(new AttributedString(s, map).getIterator(), charArray, fontRenderContext);
        }
    }
    
    private static Font singleFont(final char[] array, final int n, final int n2, final Map<? extends AttributedCharacterIterator.Attribute, ?> map) {
        if (map.get(TextAttribute.CHAR_REPLACEMENT) != null) {
            return null;
        }
        Font font = null;
        try {
            font = (Font)map.get(TextAttribute.FONT);
        }
        catch (final ClassCastException ex) {}
        if (font == null) {
            if (map.get(TextAttribute.FAMILY) != null) {
                font = Font.getFont(map);
                if (font.canDisplayUpTo(array, n, n2) != -1) {
                    return null;
                }
            }
            else {
                final FontResolver instance = FontResolver.getInstance();
                final CodePointIterator create = CodePointIterator.create(array, n, n2);
                final int nextFontRunIndex = instance.nextFontRunIndex(create);
                if (create.charIndex() == n2) {
                    font = instance.getFont(nextFontRunIndex, map);
                }
            }
        }
        if (sameBaselineUpTo(font, array, n, n2) != n2) {
            return null;
        }
        return font;
    }
    
    public TextLayout(final AttributedCharacterIterator attributedCharacterIterator, final FontRenderContext fontRenderContext) {
        this.isVerticalLine = false;
        this.lineMetrics = null;
        this.cacheIsValid = false;
        this.naturalBounds = null;
        this.boundsRect = null;
        this.caretsInLigaturesAreAllowed = false;
        if (attributedCharacterIterator == null) {
            throw new IllegalArgumentException("Null iterator passed to TextLayout constructor.");
        }
        final int beginIndex = attributedCharacterIterator.getBeginIndex();
        final int endIndex = attributedCharacterIterator.getEndIndex();
        if (beginIndex == endIndex) {
            throw new IllegalArgumentException("Zero length iterator passed to TextLayout constructor.");
        }
        final int n = endIndex - beginIndex;
        attributedCharacterIterator.first();
        final char[] array = new char[n];
        int n2 = 0;
        for (char c = attributedCharacterIterator.first(); c != '\uffff'; c = attributedCharacterIterator.next()) {
            array[n2++] = c;
        }
        attributedCharacterIterator.first();
        if (attributedCharacterIterator.getRunLimit() == endIndex) {
            final Map<AttributedCharacterIterator.Attribute, Object> attributes = attributedCharacterIterator.getAttributes();
            final Font singleFont = singleFont(array, 0, n, attributes);
            if (singleFont != null) {
                this.fastInit(array, singleFont, attributes, fontRenderContext);
                return;
            }
        }
        this.standardInit(attributedCharacterIterator, array, fontRenderContext);
    }
    
    TextLayout(final TextLine textLine, final byte baseline, final float[] baselineOffsets, final float justifyRatio) {
        this.isVerticalLine = false;
        this.lineMetrics = null;
        this.cacheIsValid = false;
        this.naturalBounds = null;
        this.boundsRect = null;
        this.caretsInLigaturesAreAllowed = false;
        this.characterCount = textLine.characterCount();
        this.baseline = baseline;
        this.baselineOffsets = baselineOffsets;
        this.textLine = textLine;
        this.justifyRatio = justifyRatio;
    }
    
    private void paragraphInit(final byte baseline, final CoreMetrics coreMetrics, final Map<? extends AttributedCharacterIterator.Attribute, ?> map, final char[] array) {
        this.baseline = baseline;
        this.baselineOffsets = TextLine.getNormalizedOffsets(coreMetrics.baselineOffsets, this.baseline);
        this.justifyRatio = AttributeValues.getJustification(map);
        final NumericShaper numericShaping = AttributeValues.getNumericShaping(map);
        if (numericShaping != null) {
            numericShaping.shape(array, 0, array.length);
        }
    }
    
    private void fastInit(final char[] array, final Font font, final Map<? extends AttributedCharacterIterator.Attribute, ?> map, final FontRenderContext fontRenderContext) {
        this.isVerticalLine = false;
        final CoreMetrics value = CoreMetrics.get(font.getLineMetrics(array, 0, array.length, fontRenderContext));
        final byte baseline = (byte)value.baselineIndex;
        if (map == null) {
            this.baseline = baseline;
            this.baselineOffsets = value.baselineOffsets;
            this.justifyRatio = 1.0f;
        }
        else {
            this.paragraphInit(baseline, value, map, array);
        }
        this.characterCount = array.length;
        this.textLine = TextLine.fastCreateTextLine(fontRenderContext, array, font, value, map);
    }
    
    private void standardInit(final AttributedCharacterIterator attributedCharacterIterator, final char[] array, final FontRenderContext fontRenderContext) {
        this.characterCount = array.length;
        final Map<AttributedCharacterIterator.Attribute, Object> attributes = attributedCharacterIterator.getAttributes();
        if (TextLine.advanceToFirstFont(attributedCharacterIterator)) {
            final Font fontAtCurrentPos = TextLine.getFontAtCurrentPos(attributedCharacterIterator);
            final int n = attributedCharacterIterator.getIndex() - attributedCharacterIterator.getBeginIndex();
            final CoreMetrics value = CoreMetrics.get(fontAtCurrentPos.getLineMetrics(array, n, n + 1, fontRenderContext));
            this.paragraphInit((byte)value.baselineIndex, value, attributes, array);
        }
        else {
            final GraphicAttribute graphicAttribute = attributes.get(TextAttribute.CHAR_REPLACEMENT);
            this.paragraphInit(getBaselineFromGraphic(graphicAttribute), GraphicComponent.createCoreMetrics(graphicAttribute), attributes, array);
        }
        this.textLine = TextLine.standardCreateTextLine(fontRenderContext, attributedCharacterIterator, array, this.baselineOffsets);
    }
    
    private void ensureCache() {
        if (!this.cacheIsValid) {
            this.buildCache();
        }
    }
    
    private void buildCache() {
        this.lineMetrics = this.textLine.getMetrics();
        if (this.textLine.isDirectionLTR()) {
            int n;
            for (n = this.characterCount - 1; n != -1 && this.textLine.isCharSpace(this.textLine.visualToLogical(n)); --n) {}
            if (n == this.characterCount - 1) {
                this.visibleAdvance = this.lineMetrics.advance;
            }
            else if (n == -1) {
                this.visibleAdvance = 0.0f;
            }
            else {
                final int visualToLogical = this.textLine.visualToLogical(n);
                this.visibleAdvance = this.textLine.getCharLinePosition(visualToLogical) + this.textLine.getCharAdvance(visualToLogical);
            }
        }
        else {
            int n2;
            for (n2 = 0; n2 != this.characterCount && this.textLine.isCharSpace(this.textLine.visualToLogical(n2)); ++n2) {}
            if (n2 == this.characterCount) {
                this.visibleAdvance = 0.0f;
            }
            else if (n2 == 0) {
                this.visibleAdvance = this.lineMetrics.advance;
            }
            else {
                this.visibleAdvance = this.lineMetrics.advance - this.textLine.getCharLinePosition(this.textLine.visualToLogical(n2));
            }
        }
        this.naturalBounds = null;
        this.boundsRect = null;
        this.hashCodeCache = 0;
        this.cacheIsValid = true;
    }
    
    private Rectangle2D getNaturalBounds() {
        this.ensureCache();
        if (this.naturalBounds == null) {
            this.naturalBounds = this.textLine.getItalicBounds();
        }
        return this.naturalBounds;
    }
    
    @Override
    protected Object clone() {
        try {
            return super.clone();
        }
        catch (final CloneNotSupportedException ex) {
            throw new InternalError(ex);
        }
    }
    
    private void checkTextHit(final TextHitInfo textHitInfo) {
        if (textHitInfo == null) {
            throw new IllegalArgumentException("TextHitInfo is null.");
        }
        if (textHitInfo.getInsertionIndex() < 0 || textHitInfo.getInsertionIndex() > this.characterCount) {
            throw new IllegalArgumentException("TextHitInfo is out of range");
        }
    }
    
    public TextLayout getJustifiedLayout(final float n) {
        if (n <= 0.0f) {
            throw new IllegalArgumentException("justificationWidth <= 0 passed to TextLayout.getJustifiedLayout()");
        }
        if (this.justifyRatio == -53.9f) {
            throw new Error("Can't justify again.");
        }
        this.ensureCache();
        int characterCount;
        for (characterCount = this.characterCount; characterCount > 0 && this.textLine.isCharWhitespace(characterCount - 1); --characterCount) {}
        final TextLine justifiedLine = this.textLine.getJustifiedLine(n, this.justifyRatio, 0, characterCount);
        if (justifiedLine != null) {
            return new TextLayout(justifiedLine, this.baseline, this.baselineOffsets, -53.9f);
        }
        return this;
    }
    
    protected void handleJustify(final float n) {
    }
    
    public byte getBaseline() {
        return this.baseline;
    }
    
    public float[] getBaselineOffsets() {
        final float[] array = new float[this.baselineOffsets.length];
        System.arraycopy(this.baselineOffsets, 0, array, 0, array.length);
        return array;
    }
    
    public float getAdvance() {
        this.ensureCache();
        return this.lineMetrics.advance;
    }
    
    public float getVisibleAdvance() {
        this.ensureCache();
        return this.visibleAdvance;
    }
    
    public float getAscent() {
        this.ensureCache();
        return this.lineMetrics.ascent;
    }
    
    public float getDescent() {
        this.ensureCache();
        return this.lineMetrics.descent;
    }
    
    public float getLeading() {
        this.ensureCache();
        return this.lineMetrics.leading;
    }
    
    public Rectangle2D getBounds() {
        this.ensureCache();
        if (this.boundsRect == null) {
            final Rectangle2D visualBounds = this.textLine.getVisualBounds();
            if (TextLayout.dx != 0.0f || TextLayout.dy != 0.0f) {
                visualBounds.setRect(visualBounds.getX() - TextLayout.dx, visualBounds.getY() - TextLayout.dy, visualBounds.getWidth(), visualBounds.getHeight());
            }
            this.boundsRect = visualBounds;
        }
        final Rectangle2D.Float float1 = new Rectangle2D.Float();
        float1.setRect(this.boundsRect);
        return float1;
    }
    
    public Rectangle getPixelBounds(final FontRenderContext fontRenderContext, final float n, final float n2) {
        return this.textLine.getPixelBounds(fontRenderContext, n, n2);
    }
    
    public boolean isLeftToRight() {
        return this.textLine.isDirectionLTR();
    }
    
    public boolean isVertical() {
        return this.isVerticalLine;
    }
    
    public int getCharacterCount() {
        return this.characterCount;
    }
    
    private float[] getCaretInfo(final int n, final Rectangle2D rectangle2D, float[] array) {
        float n5;
        float n4;
        float n7;
        float n6;
        if (n == 0 || n == this.characterCount) {
            int n2;
            float charLinePosition;
            if (n == this.characterCount) {
                n2 = this.textLine.visualToLogical(this.characterCount - 1);
                charLinePosition = this.textLine.getCharLinePosition(n2) + this.textLine.getCharAdvance(n2);
            }
            else {
                n2 = this.textLine.visualToLogical(n);
                charLinePosition = this.textLine.getCharLinePosition(n2);
            }
            final float charAngle = this.textLine.getCharAngle(n2);
            final float n3 = charLinePosition + charAngle * this.textLine.getCharShift(n2);
            n4 = (n5 = n3 + charAngle * this.textLine.getCharAscent(n2));
            n6 = (n7 = n3 - charAngle * this.textLine.getCharDescent(n2));
        }
        else {
            final int visualToLogical = this.textLine.visualToLogical(n - 1);
            final float charAngle2 = this.textLine.getCharAngle(visualToLogical);
            final float n8 = this.textLine.getCharLinePosition(visualToLogical) + this.textLine.getCharAdvance(visualToLogical);
            if (charAngle2 != 0.0f) {
                final float n9 = n8 + charAngle2 * this.textLine.getCharShift(visualToLogical);
                n5 = n9 + charAngle2 * this.textLine.getCharAscent(visualToLogical);
                n7 = n9 - charAngle2 * this.textLine.getCharDescent(visualToLogical);
            }
            else {
                n7 = (n5 = n8);
            }
            final int visualToLogical2 = this.textLine.visualToLogical(n);
            final float charAngle3 = this.textLine.getCharAngle(visualToLogical2);
            final float charLinePosition2 = this.textLine.getCharLinePosition(visualToLogical2);
            if (charAngle3 != 0.0f) {
                final float n10 = charLinePosition2 + charAngle3 * this.textLine.getCharShift(visualToLogical2);
                n4 = n10 + charAngle3 * this.textLine.getCharAscent(visualToLogical2);
                n6 = n10 - charAngle3 * this.textLine.getCharDescent(visualToLogical2);
            }
            else {
                n6 = (n4 = charLinePosition2);
            }
        }
        final float n11 = (n5 + n4) / 2.0f;
        final float n12 = (n7 + n6) / 2.0f;
        if (array == null) {
            array = new float[2];
        }
        if (this.isVerticalLine) {
            array[1] = (float)((n11 - n12) / rectangle2D.getWidth());
            array[0] = (float)(n11 + array[1] * rectangle2D.getX());
        }
        else {
            array[1] = (float)((n11 - n12) / rectangle2D.getHeight());
            array[0] = (float)(n12 + array[1] * rectangle2D.getMaxY());
        }
        return array;
    }
    
    public float[] getCaretInfo(final TextHitInfo textHitInfo, final Rectangle2D rectangle2D) {
        this.ensureCache();
        this.checkTextHit(textHitInfo);
        return this.getCaretInfoTestInternal(textHitInfo, rectangle2D);
    }
    
    private float[] getCaretInfoTestInternal(final TextHitInfo textHitInfo, final Rectangle2D rectangle2D) {
        this.ensureCache();
        this.checkTextHit(textHitInfo);
        final float[] array = new float[6];
        this.getCaretInfo(this.hitToCaret(textHitInfo), rectangle2D, array);
        final int charIndex = textHitInfo.getCharIndex();
        final boolean leadingEdge = textHitInfo.isLeadingEdge();
        final boolean directionLTR = this.textLine.isDirectionLTR();
        final boolean b = !this.isVertical();
        double n2;
        double n;
        double n3;
        double n4;
        if (charIndex == -1 || charIndex == this.characterCount) {
            final TextLine.TextLineMetrics metrics = this.textLine.getMetrics();
            final boolean b2 = directionLTR == (charIndex == -1);
            if (b) {
                n = (n2 = (b2 ? 0.0 : metrics.advance));
                n3 = -metrics.ascent;
                n4 = metrics.descent;
            }
            else {
                n4 = (n3 = (b2 ? 0.0 : metrics.advance));
                n2 = metrics.descent;
                n = metrics.ascent;
            }
        }
        else {
            final CoreMetrics coreMetrics = this.textLine.getCoreMetricsAt(charIndex);
            final double n5 = coreMetrics.italicAngle;
            final double n6 = this.textLine.getCharLinePosition(charIndex, leadingEdge);
            if (coreMetrics.baselineIndex < 0) {
                final TextLine.TextLineMetrics metrics2 = this.textLine.getMetrics();
                if (b) {
                    n = (n2 = n6);
                    if (coreMetrics.baselineIndex == -1) {
                        n3 = -metrics2.ascent;
                        n4 = n3 + coreMetrics.height;
                    }
                    else {
                        n4 = metrics2.descent;
                        n3 = n4 - coreMetrics.height;
                    }
                }
                else {
                    n4 = (n3 = n6);
                    n2 = metrics2.descent;
                    n = metrics2.ascent;
                }
            }
            else {
                final float n7 = this.baselineOffsets[coreMetrics.baselineIndex];
                if (b) {
                    final double n8 = n6 + n5 * coreMetrics.ssOffset;
                    n2 = n8 + n5 * coreMetrics.ascent;
                    n = n8 - n5 * coreMetrics.descent;
                    n3 = n7 - coreMetrics.ascent;
                    n4 = n7 + coreMetrics.descent;
                }
                else {
                    final double n9 = n6 - n5 * coreMetrics.ssOffset;
                    n3 = n9 + n5 * coreMetrics.ascent;
                    n4 = n9 - n5 * coreMetrics.descent;
                    n2 = n7 + coreMetrics.ascent;
                    n = n7 + coreMetrics.descent;
                }
            }
        }
        array[2] = (float)n2;
        array[3] = (float)n3;
        array[4] = (float)n;
        array[5] = (float)n4;
        return array;
    }
    
    public float[] getCaretInfo(final TextHitInfo textHitInfo) {
        return this.getCaretInfo(textHitInfo, this.getNaturalBounds());
    }
    
    private int hitToCaret(final TextHitInfo textHitInfo) {
        final int charIndex = textHitInfo.getCharIndex();
        if (charIndex < 0) {
            return this.textLine.isDirectionLTR() ? 0 : this.characterCount;
        }
        if (charIndex >= this.characterCount) {
            return this.textLine.isDirectionLTR() ? this.characterCount : 0;
        }
        int logicalToVisual = this.textLine.logicalToVisual(charIndex);
        if (textHitInfo.isLeadingEdge() != this.textLine.isCharLTR(charIndex)) {
            ++logicalToVisual;
        }
        return logicalToVisual;
    }
    
    private TextHitInfo caretToHit(final int n) {
        if (n != 0 && n != this.characterCount) {
            final int visualToLogical = this.textLine.visualToLogical(n);
            return this.textLine.isCharLTR(visualToLogical) ? TextHitInfo.leading(visualToLogical) : TextHitInfo.trailing(visualToLogical);
        }
        if (n == this.characterCount == this.textLine.isDirectionLTR()) {
            return TextHitInfo.leading(this.characterCount);
        }
        return TextHitInfo.trailing(-1);
    }
    
    private boolean caretIsValid(final int n) {
        if (n == this.characterCount || n == 0) {
            return true;
        }
        int n2 = this.textLine.visualToLogical(n);
        if (!this.textLine.isCharLTR(n2)) {
            n2 = this.textLine.visualToLogical(n - 1);
            if (this.textLine.isCharLTR(n2)) {
                return true;
            }
        }
        return this.textLine.caretAtOffsetIsValid(n2);
    }
    
    public TextHitInfo getNextRightHit(final TextHitInfo textHitInfo) {
        this.ensureCache();
        this.checkTextHit(textHitInfo);
        int hitToCaret = this.hitToCaret(textHitInfo);
        if (hitToCaret == this.characterCount) {
            return null;
        }
        do {
            ++hitToCaret;
        } while (!this.caretIsValid(hitToCaret));
        return this.caretToHit(hitToCaret);
    }
    
    public TextHitInfo getNextRightHit(final int n, final CaretPolicy caretPolicy) {
        if (n < 0 || n > this.characterCount) {
            throw new IllegalArgumentException("Offset out of bounds in TextLayout.getNextRightHit()");
        }
        if (caretPolicy == null) {
            throw new IllegalArgumentException("Null CaretPolicy passed to TextLayout.getNextRightHit()");
        }
        final TextHitInfo afterOffset = TextHitInfo.afterOffset(n);
        final TextHitInfo nextRightHit = this.getNextRightHit(caretPolicy.getStrongCaret(afterOffset, afterOffset.getOtherHit(), this));
        if (nextRightHit != null) {
            return caretPolicy.getStrongCaret(this.getVisualOtherHit(nextRightHit), nextRightHit, this);
        }
        return null;
    }
    
    public TextHitInfo getNextRightHit(final int n) {
        return this.getNextRightHit(n, TextLayout.DEFAULT_CARET_POLICY);
    }
    
    public TextHitInfo getNextLeftHit(final TextHitInfo textHitInfo) {
        this.ensureCache();
        this.checkTextHit(textHitInfo);
        int hitToCaret = this.hitToCaret(textHitInfo);
        if (hitToCaret == 0) {
            return null;
        }
        do {
            --hitToCaret;
        } while (!this.caretIsValid(hitToCaret));
        return this.caretToHit(hitToCaret);
    }
    
    public TextHitInfo getNextLeftHit(final int n, final CaretPolicy caretPolicy) {
        if (caretPolicy == null) {
            throw new IllegalArgumentException("Null CaretPolicy passed to TextLayout.getNextLeftHit()");
        }
        if (n < 0 || n > this.characterCount) {
            throw new IllegalArgumentException("Offset out of bounds in TextLayout.getNextLeftHit()");
        }
        final TextHitInfo afterOffset = TextHitInfo.afterOffset(n);
        final TextHitInfo nextLeftHit = this.getNextLeftHit(caretPolicy.getStrongCaret(afterOffset, afterOffset.getOtherHit(), this));
        if (nextLeftHit != null) {
            return caretPolicy.getStrongCaret(this.getVisualOtherHit(nextLeftHit), nextLeftHit, this);
        }
        return null;
    }
    
    public TextHitInfo getNextLeftHit(final int n) {
        return this.getNextLeftHit(n, TextLayout.DEFAULT_CARET_POLICY);
    }
    
    public TextHitInfo getVisualOtherHit(final TextHitInfo textHitInfo) {
        this.ensureCache();
        this.checkTextHit(textHitInfo);
        final int charIndex = textHitInfo.getCharIndex();
        int n2;
        boolean charLTR;
        if (charIndex == -1 || charIndex == this.characterCount) {
            int n;
            if (this.textLine.isDirectionLTR() == (charIndex == -1)) {
                n = 0;
            }
            else {
                n = this.characterCount - 1;
            }
            n2 = this.textLine.visualToLogical(n);
            if (this.textLine.isDirectionLTR() == (charIndex == -1)) {
                charLTR = this.textLine.isCharLTR(n2);
            }
            else {
                charLTR = !this.textLine.isCharLTR(n2);
            }
        }
        else {
            int logicalToVisual = this.textLine.logicalToVisual(charIndex);
            boolean b;
            if (this.textLine.isCharLTR(charIndex) == textHitInfo.isLeadingEdge()) {
                --logicalToVisual;
                b = false;
            }
            else {
                ++logicalToVisual;
                b = true;
            }
            if (logicalToVisual > -1 && logicalToVisual < this.characterCount) {
                n2 = this.textLine.visualToLogical(logicalToVisual);
                charLTR = (b == this.textLine.isCharLTR(n2));
            }
            else {
                n2 = ((b == this.textLine.isDirectionLTR()) ? this.characterCount : -1);
                charLTR = (n2 == this.characterCount);
            }
        }
        return charLTR ? TextHitInfo.leading(n2) : TextHitInfo.trailing(n2);
    }
    
    private double[] getCaretPath(final TextHitInfo textHitInfo, final Rectangle2D rectangle2D) {
        final float[] caretInfo = this.getCaretInfo(textHitInfo, rectangle2D);
        return new double[] { caretInfo[2], caretInfo[3], caretInfo[4], caretInfo[5] };
    }
    
    private double[] getCaretPath(final int n, final Rectangle2D rectangle2D, final boolean b) {
        final float[] caretInfo = this.getCaretInfo(n, rectangle2D, null);
        final double n2 = caretInfo[0];
        final double n3 = caretInfo[1];
        double n4 = -3141.59;
        double n5 = -2.7;
        final double x = rectangle2D.getX();
        final double n6 = x + rectangle2D.getWidth();
        final double y = rectangle2D.getY();
        final double n7 = y + rectangle2D.getHeight();
        boolean b2 = false;
        double n8;
        double n9;
        double n10;
        double n11;
        if (this.isVerticalLine) {
            if (n3 >= 0.0) {
                n8 = x;
                n9 = n6;
            }
            else {
                n9 = x;
                n8 = n6;
            }
            n10 = n2 + n8 * n3;
            n11 = n2 + n9 * n3;
            if (b) {
                if (n10 < y) {
                    if (n3 <= 0.0 || n11 <= y) {
                        n11 = (n10 = y);
                    }
                    else {
                        b2 = true;
                        n10 = y;
                        n5 = y;
                        n4 = n9 + (y - n11) / n3;
                        if (n11 > n7) {
                            n11 = n7;
                        }
                    }
                }
                else if (n11 > n7) {
                    if (n3 >= 0.0 || n10 >= n7) {
                        n11 = (n10 = n7);
                    }
                    else {
                        b2 = true;
                        n11 = n7;
                        n5 = n7;
                        n4 = n8 + (n7 - n9) / n3;
                    }
                }
            }
        }
        else {
            if (n3 >= 0.0) {
                n10 = n7;
                n11 = y;
            }
            else {
                n11 = n7;
                n10 = y;
            }
            n8 = n2 - n10 * n3;
            n9 = n2 - n11 * n3;
            if (b) {
                if (n8 < x) {
                    if (n3 <= 0.0 || n9 <= x) {
                        n9 = (n8 = x);
                    }
                    else {
                        b2 = true;
                        n8 = x;
                        n4 = x;
                        n5 = n11 - (x - n9) / n3;
                        if (n9 > n6) {
                            n9 = n6;
                        }
                    }
                }
                else if (n9 > n6) {
                    if (n3 >= 0.0 || n8 >= n6) {
                        n9 = (n8 = n6);
                    }
                    else {
                        b2 = true;
                        n9 = n6;
                        n4 = n6;
                        n5 = n10 - (n6 - n8) / n3;
                    }
                }
            }
        }
        return b2 ? new double[] { n8, n10, n4, n5, n9, n11 } : new double[] { n8, n10, n9, n11 };
    }
    
    private static GeneralPath pathToShape(final double[] array, final boolean b, final LayoutPathImpl layoutPathImpl) {
        GeneralPath generalPath = new GeneralPath(0, array.length);
        generalPath.moveTo((float)array[0], (float)array[1]);
        for (int i = 2; i < array.length; i += 2) {
            generalPath.lineTo((float)array[i], (float)array[i + 1]);
        }
        if (b) {
            generalPath.closePath();
        }
        if (layoutPathImpl != null) {
            generalPath = (GeneralPath)layoutPathImpl.mapShape(generalPath);
        }
        return generalPath;
    }
    
    public Shape getCaretShape(final TextHitInfo textHitInfo, final Rectangle2D rectangle2D) {
        this.ensureCache();
        this.checkTextHit(textHitInfo);
        if (rectangle2D == null) {
            throw new IllegalArgumentException("Null Rectangle2D passed to TextLayout.getCaret()");
        }
        return pathToShape(this.getCaretPath(textHitInfo, rectangle2D), false, this.textLine.getLayoutPath());
    }
    
    public Shape getCaretShape(final TextHitInfo textHitInfo) {
        return this.getCaretShape(textHitInfo, this.getNaturalBounds());
    }
    
    private final TextHitInfo getStrongHit(final TextHitInfo textHitInfo, final TextHitInfo textHitInfo2) {
        final byte characterLevel = this.getCharacterLevel(textHitInfo.getCharIndex());
        final byte characterLevel2 = this.getCharacterLevel(textHitInfo2.getCharIndex());
        if (characterLevel != characterLevel2) {
            return (characterLevel < characterLevel2) ? textHitInfo : textHitInfo2;
        }
        if (textHitInfo2.isLeadingEdge() && !textHitInfo.isLeadingEdge()) {
            return textHitInfo2;
        }
        return textHitInfo;
    }
    
    public byte getCharacterLevel(final int n) {
        if (n < -1 || n > this.characterCount) {
            throw new IllegalArgumentException("Index is out of range in getCharacterLevel.");
        }
        this.ensureCache();
        if (n == -1 || n == this.characterCount) {
            return (byte)(this.textLine.isDirectionLTR() ? 0 : 1);
        }
        return this.textLine.getCharLevel(n);
    }
    
    public Shape[] getCaretShapes(final int n, final Rectangle2D rectangle2D, final CaretPolicy caretPolicy) {
        this.ensureCache();
        if (n < 0 || n > this.characterCount) {
            throw new IllegalArgumentException("Offset out of bounds in TextLayout.getCaretShapes()");
        }
        if (rectangle2D == null) {
            throw new IllegalArgumentException("Null Rectangle2D passed to TextLayout.getCaretShapes()");
        }
        if (caretPolicy == null) {
            throw new IllegalArgumentException("Null CaretPolicy passed to TextLayout.getCaretShapes()");
        }
        final Shape[] array = new Shape[2];
        final TextHitInfo afterOffset = TextHitInfo.afterOffset(n);
        final int hitToCaret = this.hitToCaret(afterOffset);
        final LayoutPathImpl layoutPath = this.textLine.getLayoutPath();
        final GeneralPath pathToShape = pathToShape(this.getCaretPath(afterOffset, rectangle2D), false, layoutPath);
        final TextHitInfo otherHit = afterOffset.getOtherHit();
        if (hitToCaret == this.hitToCaret(otherHit)) {
            array[0] = pathToShape;
        }
        else {
            final GeneralPath pathToShape2 = pathToShape(this.getCaretPath(otherHit, rectangle2D), false, layoutPath);
            if (caretPolicy.getStrongCaret(afterOffset, otherHit, this).equals(afterOffset)) {
                array[0] = pathToShape;
                array[1] = pathToShape2;
            }
            else {
                array[0] = pathToShape2;
                array[1] = pathToShape;
            }
        }
        return array;
    }
    
    public Shape[] getCaretShapes(final int n, final Rectangle2D rectangle2D) {
        return this.getCaretShapes(n, rectangle2D, TextLayout.DEFAULT_CARET_POLICY);
    }
    
    public Shape[] getCaretShapes(final int n) {
        return this.getCaretShapes(n, this.getNaturalBounds(), TextLayout.DEFAULT_CARET_POLICY);
    }
    
    private GeneralPath boundingShape(final double[] array, final double[] array2) {
        final GeneralPath pathToShape = pathToShape(array, false, null);
        boolean b;
        if (this.isVerticalLine) {
            b = (array[1] > array[array.length - 1] == array2[1] > array2[array2.length - 1]);
        }
        else {
            b = (array[0] > array[array.length - 2] == array2[0] > array2[array2.length - 2]);
        }
        int n;
        int length;
        int n2;
        if (b) {
            n = array2.length - 2;
            length = -2;
            n2 = -2;
        }
        else {
            n = 0;
            length = array2.length;
            n2 = 2;
        }
        for (int i = n; i != length; i += n2) {
            pathToShape.lineTo((float)array2[i], (float)array2[i + 1]);
        }
        pathToShape.closePath();
        return pathToShape;
    }
    
    private GeneralPath caretBoundingShape(int n, int n2, final Rectangle2D rectangle2D) {
        if (n > n2) {
            final int n3 = n;
            n = n2;
            n2 = n3;
        }
        return this.boundingShape(this.getCaretPath(n, rectangle2D, true), this.getCaretPath(n2, rectangle2D, true));
    }
    
    private GeneralPath leftShape(final Rectangle2D rectangle2D) {
        double[] array;
        if (this.isVerticalLine) {
            array = new double[] { rectangle2D.getX(), rectangle2D.getY(), rectangle2D.getX() + rectangle2D.getWidth(), rectangle2D.getY() };
        }
        else {
            array = new double[] { rectangle2D.getX(), rectangle2D.getY() + rectangle2D.getHeight(), rectangle2D.getX(), rectangle2D.getY() };
        }
        return this.boundingShape(array, this.getCaretPath(0, rectangle2D, true));
    }
    
    private GeneralPath rightShape(final Rectangle2D rectangle2D) {
        double[] array;
        if (this.isVerticalLine) {
            array = new double[] { rectangle2D.getX(), rectangle2D.getY() + rectangle2D.getHeight(), rectangle2D.getX() + rectangle2D.getWidth(), rectangle2D.getY() + rectangle2D.getHeight() };
        }
        else {
            array = new double[] { rectangle2D.getX() + rectangle2D.getWidth(), rectangle2D.getY() + rectangle2D.getHeight(), rectangle2D.getX() + rectangle2D.getWidth(), rectangle2D.getY() };
        }
        return this.boundingShape(this.getCaretPath(this.characterCount, rectangle2D, true), array);
    }
    
    public int[] getLogicalRangesForVisualSelection(final TextHitInfo textHitInfo, final TextHitInfo textHitInfo2) {
        this.ensureCache();
        this.checkTextHit(textHitInfo);
        this.checkTextHit(textHitInfo2);
        final boolean[] array = new boolean[this.characterCount];
        int hitToCaret = this.hitToCaret(textHitInfo);
        int hitToCaret2 = this.hitToCaret(textHitInfo2);
        if (hitToCaret > hitToCaret2) {
            final int n = hitToCaret;
            hitToCaret = hitToCaret2;
            hitToCaret2 = n;
        }
        if (hitToCaret < hitToCaret2) {
            for (int i = hitToCaret; i < hitToCaret2; ++i) {
                array[this.textLine.visualToLogical(i)] = true;
            }
        }
        int n2 = 0;
        boolean b = false;
        for (int j = 0; j < this.characterCount; ++j) {
            if (array[j] != b) {
                b = !b;
                if (b) {
                    ++n2;
                }
            }
        }
        final int[] array2 = new int[n2 * 2];
        int n3 = 0;
        boolean b2 = false;
        for (int k = 0; k < this.characterCount; ++k) {
            if (array[k] != b2) {
                array2[n3++] = k;
                b2 = !b2;
            }
        }
        if (b2) {
            array2[n3++] = this.characterCount;
        }
        return array2;
    }
    
    public Shape getVisualHighlightShape(final TextHitInfo textHitInfo, final TextHitInfo textHitInfo2, final Rectangle2D rectangle2D) {
        this.ensureCache();
        this.checkTextHit(textHitInfo);
        this.checkTextHit(textHitInfo2);
        if (rectangle2D == null) {
            throw new IllegalArgumentException("Null Rectangle2D passed to TextLayout.getVisualHighlightShape()");
        }
        GeneralPath generalPath = new GeneralPath(0);
        final int hitToCaret = this.hitToCaret(textHitInfo);
        final int hitToCaret2 = this.hitToCaret(textHitInfo2);
        generalPath.append(this.caretBoundingShape(hitToCaret, hitToCaret2, rectangle2D), false);
        if (hitToCaret == 0 || hitToCaret2 == 0) {
            final GeneralPath leftShape = this.leftShape(rectangle2D);
            if (!leftShape.getBounds().isEmpty()) {
                generalPath.append(leftShape, false);
            }
        }
        if (hitToCaret == this.characterCount || hitToCaret2 == this.characterCount) {
            final GeneralPath rightShape = this.rightShape(rectangle2D);
            if (!rightShape.getBounds().isEmpty()) {
                generalPath.append(rightShape, false);
            }
        }
        final LayoutPathImpl layoutPath = this.textLine.getLayoutPath();
        if (layoutPath != null) {
            generalPath = (GeneralPath)layoutPath.mapShape(generalPath);
        }
        return generalPath;
    }
    
    public Shape getVisualHighlightShape(final TextHitInfo textHitInfo, final TextHitInfo textHitInfo2) {
        return this.getVisualHighlightShape(textHitInfo, textHitInfo2, this.getNaturalBounds());
    }
    
    public Shape getLogicalHighlightShape(int n, int n2, final Rectangle2D rectangle2D) {
        if (rectangle2D == null) {
            throw new IllegalArgumentException("Null Rectangle2D passed to TextLayout.getLogicalHighlightShape()");
        }
        this.ensureCache();
        if (n > n2) {
            final int n3 = n;
            n = n2;
            n2 = n3;
        }
        if (n < 0 || n2 > this.characterCount) {
            throw new IllegalArgumentException("Range is invalid in TextLayout.getLogicalHighlightShape()");
        }
        GeneralPath generalPath = new GeneralPath(0);
        int[] array = new int[10];
        int n4 = 0;
        if (n < n2) {
            int i = n;
            do {
                array[n4++] = this.hitToCaret(TextHitInfo.leading(i));
                final boolean charLTR = this.textLine.isCharLTR(i);
                while (++i < n2 && this.textLine.isCharLTR(i) == charLTR) {}
                array[n4++] = this.hitToCaret(TextHitInfo.trailing(i - 1));
                if (n4 == array.length) {
                    final int[] array2 = new int[array.length + 10];
                    System.arraycopy(array, 0, array2, 0, n4);
                    array = array2;
                }
            } while (i < n2);
        }
        else {
            n4 = 2;
            array[0] = (array[1] = this.hitToCaret(TextHitInfo.leading(n)));
        }
        for (int j = 0; j < n4; j += 2) {
            generalPath.append(this.caretBoundingShape(array[j], array[j + 1], rectangle2D), false);
        }
        if (n != n2) {
            if ((this.textLine.isDirectionLTR() && n == 0) || (!this.textLine.isDirectionLTR() && n2 == this.characterCount)) {
                final GeneralPath leftShape = this.leftShape(rectangle2D);
                if (!leftShape.getBounds().isEmpty()) {
                    generalPath.append(leftShape, false);
                }
            }
            if ((this.textLine.isDirectionLTR() && n2 == this.characterCount) || (!this.textLine.isDirectionLTR() && n == 0)) {
                final GeneralPath rightShape = this.rightShape(rectangle2D);
                if (!rightShape.getBounds().isEmpty()) {
                    generalPath.append(rightShape, false);
                }
            }
        }
        final LayoutPathImpl layoutPath = this.textLine.getLayoutPath();
        if (layoutPath != null) {
            generalPath = (GeneralPath)layoutPath.mapShape(generalPath);
        }
        return generalPath;
    }
    
    public Shape getLogicalHighlightShape(final int n, final int n2) {
        return this.getLogicalHighlightShape(n, n2, this.getNaturalBounds());
    }
    
    public Shape getBlackBoxBounds(int n, int n2) {
        this.ensureCache();
        if (n > n2) {
            final int n3 = n;
            n = n2;
            n2 = n3;
        }
        if (n < 0 || n2 > this.characterCount) {
            throw new IllegalArgumentException("Invalid range passed to TextLayout.getBlackBoxBounds()");
        }
        GeneralPath generalPath = new GeneralPath(1);
        if (n < this.characterCount) {
            for (int i = n; i < n2; ++i) {
                final Rectangle2D charBounds = this.textLine.getCharBounds(i);
                if (!charBounds.isEmpty()) {
                    generalPath.append(charBounds, false);
                }
            }
        }
        if (TextLayout.dx != 0.0f || TextLayout.dy != 0.0f) {
            generalPath = (GeneralPath)AffineTransform.getTranslateInstance(TextLayout.dx, TextLayout.dy).createTransformedShape(generalPath);
        }
        final LayoutPathImpl layoutPath = this.textLine.getLayoutPath();
        if (layoutPath != null) {
            generalPath = (GeneralPath)layoutPath.mapShape(generalPath);
        }
        return generalPath;
    }
    
    private float caretToPointDistance(final float[] array, final float n, final float n2) {
        return (this.isVerticalLine ? n2 : n) - array[0] + (this.isVerticalLine ? (-n) : n2) * array[1];
    }
    
    public TextHitInfo hitTestChar(float x, float y, final Rectangle2D rectangle2D) {
        final LayoutPathImpl layoutPath = this.textLine.getLayoutPath();
        if (layoutPath != null) {
            final Point2D.Float float1 = new Point2D.Float(x, y);
            layoutPath.pointToPath(float1, float1);
            x = float1.x;
            y = float1.y;
        }
        if (this.isVertical()) {
            if (y < rectangle2D.getMinY()) {
                return TextHitInfo.leading(0);
            }
            if (y >= rectangle2D.getMaxY()) {
                return TextHitInfo.trailing(this.characterCount - 1);
            }
        }
        else {
            if (x < rectangle2D.getMinX()) {
                return this.isLeftToRight() ? TextHitInfo.leading(0) : TextHitInfo.trailing(this.characterCount - 1);
            }
            if (x >= rectangle2D.getMaxX()) {
                return this.isLeftToRight() ? TextHitInfo.trailing(this.characterCount - 1) : TextHitInfo.leading(0);
            }
        }
        double n = Double.MAX_VALUE;
        int n2 = 0;
        int characterCount = -1;
        CoreMetrics coreMetrics = null;
        float n3 = 0.0f;
        float n4 = 0.0f;
        float italicAngle = 0.0f;
        float n5 = 0.0f;
        float n6 = 0.0f;
        float n7 = 0.0f;
        for (int i = 0; i < this.characterCount; ++i) {
            if (this.textLine.caretAtOffsetIsValid(i)) {
                if (characterCount == -1) {
                    characterCount = i;
                }
                final CoreMetrics coreMetrics2 = this.textLine.getCoreMetricsAt(i);
                if (coreMetrics2 != coreMetrics) {
                    coreMetrics = coreMetrics2;
                    float n8;
                    if (coreMetrics2.baselineIndex == -1) {
                        n8 = -(this.textLine.getMetrics().ascent - coreMetrics2.ascent) + coreMetrics2.ssOffset;
                    }
                    else if (coreMetrics2.baselineIndex == -2) {
                        n8 = this.textLine.getMetrics().descent - coreMetrics2.descent + coreMetrics2.ssOffset;
                    }
                    else {
                        n8 = coreMetrics2.effectiveBaselineOffset(this.baselineOffsets) + coreMetrics2.ssOffset;
                    }
                    final float n9 = (coreMetrics2.descent - coreMetrics2.ascent) / 2.0f - n8;
                    n6 = n9 * coreMetrics2.italicAngle;
                    n5 = n8 + n9;
                    n7 = (n5 - y) * (n5 - y);
                }
                final float n10 = this.textLine.getCharXPosition(i) + (this.textLine.getCharAdvance(i) / 2.0f - n6);
                final double sqrt = Math.sqrt(4.0f * (n10 - x) * (n10 - x) + n7);
                if (sqrt < n) {
                    n = sqrt;
                    n2 = i;
                    characterCount = -1;
                    n3 = n10;
                    n4 = n5;
                    italicAngle = coreMetrics2.italicAngle;
                }
            }
        }
        final boolean b = this.textLine.isCharLTR(n2) == x < n3 - (y - n4) * italicAngle;
        if (characterCount == -1) {
            characterCount = this.characterCount;
        }
        return b ? TextHitInfo.leading(n2) : TextHitInfo.trailing(characterCount - 1);
    }
    
    public TextHitInfo hitTestChar(final float n, final float n2) {
        return this.hitTestChar(n, n2, this.getNaturalBounds());
    }
    
    @Override
    public int hashCode() {
        if (this.hashCodeCache == 0) {
            this.ensureCache();
            this.hashCodeCache = this.textLine.hashCode();
        }
        return this.hashCodeCache;
    }
    
    @Override
    public boolean equals(final Object o) {
        return o instanceof TextLayout && this.equals((TextLayout)o);
    }
    
    public boolean equals(final TextLayout textLayout) {
        if (textLayout == null) {
            return false;
        }
        if (textLayout == this) {
            return true;
        }
        this.ensureCache();
        return this.textLine.equals(textLayout.textLine);
    }
    
    @Override
    public String toString() {
        this.ensureCache();
        return this.textLine.toString();
    }
    
    public void draw(final Graphics2D graphics2D, final float n, final float n2) {
        if (graphics2D == null) {
            throw new IllegalArgumentException("Null Graphics2D passed to TextLayout.draw()");
        }
        this.textLine.draw(graphics2D, n - TextLayout.dx, n2 - TextLayout.dy);
    }
    
    TextLine getTextLineForTesting() {
        return this.textLine;
    }
    
    private static int sameBaselineUpTo(final Font font, final char[] array, final int n, final int n2) {
        return n2;
    }
    
    static byte getBaselineFromGraphic(final GraphicAttribute graphicAttribute) {
        final byte b = (byte)graphicAttribute.getAlignment();
        if (b == -2 || b == -1) {
            return 0;
        }
        return b;
    }
    
    public Shape getOutline(final AffineTransform affineTransform) {
        this.ensureCache();
        Shape shape = this.textLine.getOutline(affineTransform);
        final LayoutPathImpl layoutPath = this.textLine.getLayoutPath();
        if (layoutPath != null) {
            shape = layoutPath.mapShape(shape);
        }
        return shape;
    }
    
    public LayoutPath getLayoutPath() {
        return this.textLine.getLayoutPath();
    }
    
    public void hitToPoint(final TextHitInfo textHitInfo, final Point2D point2D) {
        if (textHitInfo == null || point2D == null) {
            throw new NullPointerException(((textHitInfo == null) ? "hit" : "point") + " can't be null");
        }
        this.ensureCache();
        this.checkTextHit(textHitInfo);
        float charYPosition = 0.0f;
        final int charIndex = textHitInfo.getCharIndex();
        final boolean leadingEdge = textHitInfo.isLeadingEdge();
        boolean b;
        float charLinePosition;
        if (charIndex == -1 || charIndex == this.textLine.characterCount()) {
            b = this.textLine.isDirectionLTR();
            charLinePosition = ((b == (charIndex == -1)) ? 0.0f : this.lineMetrics.advance);
        }
        else {
            b = this.textLine.isCharLTR(charIndex);
            charLinePosition = this.textLine.getCharLinePosition(charIndex, leadingEdge);
            charYPosition = this.textLine.getCharYPosition(charIndex);
        }
        point2D.setLocation(charLinePosition, charYPosition);
        final LayoutPathImpl layoutPath = this.textLine.getLayoutPath();
        if (layoutPath != null) {
            layoutPath.pathToPoint(point2D, b != leadingEdge, point2D);
        }
    }
    
    static {
        DEFAULT_CARET_POLICY = new CaretPolicy();
    }
    
    public static class CaretPolicy
    {
        public TextHitInfo getStrongCaret(final TextHitInfo textHitInfo, final TextHitInfo textHitInfo2, final TextLayout textLayout) {
            return textLayout.getStrongHit(textHitInfo, textHitInfo2);
        }
    }
}
