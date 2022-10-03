package sun.font;

import java.awt.font.GlyphJustificationInfo;
import java.awt.font.GlyphVector;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Rectangle;
import java.awt.font.FontRenderContext;
import java.awt.font.TextAttribute;
import java.util.Map;
import java.awt.geom.Rectangle2D;
import java.awt.geom.AffineTransform;
import java.awt.Font;

class ExtendedTextSourceLabel extends ExtendedTextLabel implements Decoration.Label
{
    TextSource source;
    private Decoration decorator;
    private Font font;
    private AffineTransform baseTX;
    private CoreMetrics cm;
    Rectangle2D lb;
    Rectangle2D ab;
    Rectangle2D vb;
    Rectangle2D ib;
    StandardGlyphVector gv;
    float[] charinfo;
    private static final int posx = 0;
    private static final int posy = 1;
    private static final int advx = 2;
    private static final int advy = 3;
    private static final int visx = 4;
    private static final int visy = 5;
    private static final int visw = 6;
    private static final int vish = 7;
    private static final int numvals = 8;
    
    public ExtendedTextSourceLabel(final TextSource source, final Decoration decorator) {
        this.source = source;
        this.decorator = decorator;
        this.finishInit();
    }
    
    public ExtendedTextSourceLabel(final TextSource source, final ExtendedTextSourceLabel extendedTextSourceLabel, final int n) {
        this.source = source;
        this.decorator = extendedTextSourceLabel.decorator;
        this.finishInit();
    }
    
    private void finishInit() {
        this.font = this.source.getFont();
        final Map<TextAttribute, ?> attributes = this.font.getAttributes();
        this.baseTX = AttributeValues.getBaselineTransform(attributes);
        if (this.baseTX == null) {
            this.cm = this.source.getCoreMetrics();
        }
        else {
            AffineTransform charTransform = AttributeValues.getCharTransform(attributes);
            if (charTransform == null) {
                charTransform = new AffineTransform();
            }
            this.font = this.font.deriveFont(charTransform);
            this.cm = CoreMetrics.get(this.font.getLineMetrics(this.source.getChars(), this.source.getStart(), this.source.getStart() + this.source.getLength(), this.source.getFRC()));
        }
    }
    
    @Override
    public Rectangle2D getLogicalBounds() {
        return this.getLogicalBounds(0.0f, 0.0f);
    }
    
    @Override
    public Rectangle2D getLogicalBounds(final float n, final float n2) {
        if (this.lb == null) {
            this.lb = this.createLogicalBounds();
        }
        return new Rectangle2D.Float((float)(this.lb.getX() + n), (float)(this.lb.getY() + n2), (float)this.lb.getWidth(), (float)this.lb.getHeight());
    }
    
    @Override
    public float getAdvance() {
        if (this.lb == null) {
            this.lb = this.createLogicalBounds();
        }
        return (float)this.lb.getWidth();
    }
    
    @Override
    public Rectangle2D getVisualBounds(final float n, final float n2) {
        if (this.vb == null) {
            this.vb = this.decorator.getVisualBounds(this);
        }
        return new Rectangle2D.Float((float)(this.vb.getX() + n), (float)(this.vb.getY() + n2), (float)this.vb.getWidth(), (float)this.vb.getHeight());
    }
    
    @Override
    public Rectangle2D getAlignBounds(final float n, final float n2) {
        if (this.ab == null) {
            this.ab = this.createAlignBounds();
        }
        return new Rectangle2D.Float((float)(this.ab.getX() + n), (float)(this.ab.getY() + n2), (float)this.ab.getWidth(), (float)this.ab.getHeight());
    }
    
    @Override
    public Rectangle2D getItalicBounds(final float n, final float n2) {
        if (this.ib == null) {
            this.ib = this.createItalicBounds();
        }
        return new Rectangle2D.Float((float)(this.ib.getX() + n), (float)(this.ib.getY() + n2), (float)this.ib.getWidth(), (float)this.ib.getHeight());
    }
    
    @Override
    public Rectangle getPixelBounds(final FontRenderContext fontRenderContext, final float n, final float n2) {
        return this.getGV().getPixelBounds(fontRenderContext, n, n2);
    }
    
    @Override
    public boolean isSimple() {
        return this.decorator == Decoration.getPlainDecoration() && this.baseTX == null;
    }
    
    @Override
    public AffineTransform getBaselineTransform() {
        return this.baseTX;
    }
    
    @Override
    public Shape handleGetOutline(final float n, final float n2) {
        return this.getGV().getOutline(n, n2);
    }
    
    @Override
    public Shape getOutline(final float n, final float n2) {
        return this.decorator.getOutline(this, n, n2);
    }
    
    @Override
    public void handleDraw(final Graphics2D graphics2D, final float n, final float n2) {
        graphics2D.drawGlyphVector(this.getGV(), n, n2);
    }
    
    @Override
    public void draw(final Graphics2D graphics2D, final float n, final float n2) {
        this.decorator.drawTextAndDecorations(this, graphics2D, n, n2);
    }
    
    protected Rectangle2D createLogicalBounds() {
        return this.getGV().getLogicalBounds();
    }
    
    @Override
    public Rectangle2D handleGetVisualBounds() {
        return this.getGV().getVisualBounds();
    }
    
    protected Rectangle2D createAlignBounds() {
        final float[] charinfo = this.getCharinfo();
        float max = 0.0f;
        final float n = -this.cm.ascent;
        float n2 = 0.0f;
        final float n3 = this.cm.ascent + this.cm.descent;
        if (this.charinfo == null || this.charinfo.length == 0) {
            return new Rectangle2D.Float(max, n, n2, n3);
        }
        final boolean b = (this.source.getLayoutFlags() & 0x8) == 0x0;
        int n4 = charinfo.length - 8;
        if (b) {
            while (n4 > 0 && charinfo[n4 + 6] == 0.0f) {
                n4 -= 8;
            }
        }
        if (n4 >= 0) {
            int n5;
            for (n5 = 0; n5 < n4 && (charinfo[n5 + 2] == 0.0f || (!b && charinfo[n5 + 6] == 0.0f)); n5 += 8) {}
            max = Math.max(0.0f, charinfo[n5 + 0]);
            n2 = charinfo[n4 + 0] + charinfo[n4 + 2] - max;
        }
        return new Rectangle2D.Float(max, n, n2, n3);
    }
    
    public Rectangle2D createItalicBounds() {
        final float italicAngle = this.cm.italicAngle;
        final Rectangle2D logicalBounds = this.getLogicalBounds();
        float n = (float)logicalBounds.getMinX();
        final float n2 = -this.cm.ascent;
        float n3 = (float)logicalBounds.getMaxX();
        final float descent = this.cm.descent;
        if (italicAngle != 0.0f) {
            if (italicAngle > 0.0f) {
                n -= italicAngle * (descent - this.cm.ssOffset);
                n3 -= italicAngle * (n2 - this.cm.ssOffset);
            }
            else {
                n -= italicAngle * (n2 - this.cm.ssOffset);
                n3 -= italicAngle * (descent - this.cm.ssOffset);
            }
        }
        return new Rectangle2D.Float(n, n2, n3 - n, descent - n2);
    }
    
    private final StandardGlyphVector getGV() {
        if (this.gv == null) {
            this.gv = this.createGV();
        }
        return this.gv;
    }
    
    protected StandardGlyphVector createGV() {
        final FontRenderContext frc = this.source.getFRC();
        final int layoutFlags = this.source.getLayoutFlags();
        final char[] chars = this.source.getChars();
        final int start = this.source.getStart();
        final int length = this.source.getLength();
        final GlyphLayout value = GlyphLayout.get(null);
        this.gv = value.layout(this.font, frc, chars, start, length, layoutFlags, null);
        GlyphLayout.done(value);
        return this.gv;
    }
    
    @Override
    public int getNumCharacters() {
        return this.source.getLength();
    }
    
    @Override
    public CoreMetrics getCoreMetrics() {
        return this.cm;
    }
    
    @Override
    public float getCharX(final int n) {
        this.validate(n);
        final float[] charinfo = this.getCharinfo();
        final int n2 = this.l2v(n) * 8 + 0;
        if (charinfo == null || n2 >= charinfo.length) {
            return 0.0f;
        }
        return charinfo[n2];
    }
    
    @Override
    public float getCharY(final int n) {
        this.validate(n);
        final float[] charinfo = this.getCharinfo();
        final int n2 = this.l2v(n) * 8 + 1;
        if (charinfo == null || n2 >= charinfo.length) {
            return 0.0f;
        }
        return charinfo[n2];
    }
    
    @Override
    public float getCharAdvance(final int n) {
        this.validate(n);
        final float[] charinfo = this.getCharinfo();
        final int n2 = this.l2v(n) * 8 + 2;
        if (charinfo == null || n2 >= charinfo.length) {
            return 0.0f;
        }
        return charinfo[n2];
    }
    
    @Override
    public Rectangle2D handleGetCharVisualBounds(int n) {
        this.validate(n);
        final float[] charinfo = this.getCharinfo();
        n = this.l2v(n) * 8;
        if (charinfo == null || n + 7 >= charinfo.length) {
            return new Rectangle2D.Float();
        }
        return new Rectangle2D.Float(charinfo[n + 4], charinfo[n + 5], charinfo[n + 6], charinfo[n + 7]);
    }
    
    @Override
    public Rectangle2D getCharVisualBounds(final int n, final float n2, final float n3) {
        final Rectangle2D charVisualBounds = this.decorator.getCharVisualBounds(this, n);
        if (n2 != 0.0f || n3 != 0.0f) {
            charVisualBounds.setRect(charVisualBounds.getX() + n2, charVisualBounds.getY() + n3, charVisualBounds.getWidth(), charVisualBounds.getHeight());
        }
        return charVisualBounds;
    }
    
    private void validate(final int n) {
        if (n < 0) {
            throw new IllegalArgumentException("index " + n + " < 0");
        }
        if (n >= this.source.getLength()) {
            throw new IllegalArgumentException("index " + n + " < " + this.source.getLength());
        }
    }
    
    @Override
    public int logicalToVisual(final int n) {
        this.validate(n);
        return this.l2v(n);
    }
    
    @Override
    public int visualToLogical(final int n) {
        this.validate(n);
        return this.v2l(n);
    }
    
    @Override
    public int getLineBreakIndex(int n, float n2) {
        final float[] charinfo = this.getCharinfo();
        final int length = this.source.getLength();
        --n;
        while (n2 >= 0.0f && ++n < length) {
            final int n3 = this.l2v(n) * 8 + 2;
            if (n3 >= charinfo.length) {
                break;
            }
            n2 -= charinfo[n3];
        }
        return n;
    }
    
    @Override
    public float getAdvanceBetween(int n, final int n2) {
        float n3 = 0.0f;
        final float[] charinfo = this.getCharinfo();
        --n;
        while (++n < n2) {
            final int n4 = this.l2v(n) * 8 + 2;
            if (n4 >= charinfo.length) {
                break;
            }
            n3 += charinfo[n4];
        }
        return n3;
    }
    
    @Override
    public boolean caretAtOffsetIsValid(final int n) {
        if (n == 0 || n == this.source.getLength()) {
            return true;
        }
        final char c = this.source.getChars()[this.source.getStart() + n];
        if (c == '\t' || c == '\n' || c == '\r') {
            return true;
        }
        final int n2 = this.l2v(n) * 8 + 2;
        final float[] charinfo = this.getCharinfo();
        return charinfo != null && n2 < charinfo.length && charinfo[n2] != 0.0f;
    }
    
    private final float[] getCharinfo() {
        if (this.charinfo == null) {
            this.charinfo = this.createCharinfo();
        }
        return this.charinfo;
    }
    
    protected float[] createCharinfo() {
        final StandardGlyphVector gv = this.getGV();
        float[] glyphInfo = null;
        try {
            glyphInfo = gv.getGlyphInfo();
        }
        catch (final Exception ex) {
            System.out.println(this.source);
        }
        final int numGlyphs = gv.getNumGlyphs();
        if (numGlyphs == 0) {
            return glyphInfo;
        }
        final int[] glyphCharIndices = gv.getGlyphCharIndices(0, numGlyphs, null);
        final boolean b = false;
        if (b) {
            System.err.println("number of glyphs: " + numGlyphs);
            for (int i = 0; i < numGlyphs; ++i) {
                System.err.println("g: " + i + ", x: " + glyphInfo[i * 8 + 0] + ", a: " + glyphInfo[i * 8 + 2] + ", n: " + glyphCharIndices[i]);
            }
        }
        final int n = glyphCharIndices[0];
        int n2 = 0;
        int n3 = 0;
        int n4 = 0;
        int n5 = 0;
        int j = 0;
        int n6 = numGlyphs;
        int n7 = 8;
        int n8 = 1;
        final boolean b2 = (this.source.getLayoutFlags() & 0x1) == 0x0;
        if (!b2) {
            final int n9 = glyphCharIndices[numGlyphs - 1];
            n2 = 0;
            n3 = glyphInfo.length - 8;
            n4 = 0;
            n5 = glyphInfo.length - 8;
            j = numGlyphs - 1;
            n6 = -1;
            n7 = -8;
            n8 = -1;
        }
        float min = 0.0f;
        float max = 0.0f;
        float min2 = 0.0f;
        float min3 = 0.0f;
        float max2 = 0.0f;
        float max3 = 0.0f;
        final float n10 = 0.0f;
        boolean b3 = false;
        while (j != n6) {
            int n11 = 0;
            int n12;
            int max4;
            int k;
            for (n12 = 0, k = (max4 = glyphCharIndices[j]), j += n8, n5 += n7; j != n6 && (glyphInfo[n5 + 2] == 0.0f || k != n2 || glyphCharIndices[j] <= max4 || max4 - k > n12); k = Math.min(k, glyphCharIndices[j]), max4 = Math.max(max4, glyphCharIndices[j]), j += n8, n5 += n7) {
                if (n11 == 0) {
                    final int n13 = n5 - n7;
                    min = glyphInfo[n13 + 0];
                    max = min + glyphInfo[n13 + 2];
                    min2 = glyphInfo[n13 + 4];
                    min3 = glyphInfo[n13 + 5];
                    max2 = min2 + glyphInfo[n13 + 6];
                    max3 = min3 + glyphInfo[n13 + 7];
                    n11 = 1;
                }
                ++n12;
                final float n14 = glyphInfo[n5 + 2];
                if (n14 != 0.0f) {
                    final float n15 = glyphInfo[n5 + 0];
                    min = Math.min(min, n15);
                    max = Math.max(max, n15 + n14);
                }
                final float n16 = glyphInfo[n5 + 6];
                if (n16 != 0.0f) {
                    final float n17 = glyphInfo[n5 + 4];
                    final float n18 = glyphInfo[n5 + 5];
                    min2 = Math.min(min2, n17);
                    min3 = Math.min(min3, n18);
                    max2 = Math.max(max2, n17 + n16);
                    max3 = Math.max(max3, n18 + glyphInfo[n5 + 7]);
                }
            }
            if (b) {
                System.out.println("minIndex = " + k + ", maxIndex = " + max4);
            }
            n2 = max4 + 1;
            glyphInfo[n3 + 1] = n10;
            glyphInfo[n3 + 3] = 0.0f;
            if (n11 != 0) {
                glyphInfo[n3 + 2] = max - (glyphInfo[n3 + 0] = min);
                glyphInfo[n3 + 4] = min2;
                glyphInfo[n3 + 5] = min3;
                glyphInfo[n3 + 6] = max2 - min2;
                glyphInfo[n3 + 7] = max3 - min3;
                if (max4 - k < n12) {
                    b3 = true;
                }
                if (k < max4) {
                    if (!b2) {
                        max = min;
                    }
                    max2 -= min2;
                    max3 -= min3;
                    final int n19 = k;
                    final int n20 = n3 / 8;
                    while (k < max4) {
                        ++k;
                        n4 += n8;
                        n3 += n7;
                        if ((n3 < 0 || n3 >= glyphInfo.length) && b) {
                            System.out.println("minIndex = " + n19 + ", maxIndex = " + max4 + ", cp = " + n20);
                        }
                        glyphInfo[n3 + 0] = max;
                        glyphInfo[n3 + 1] = n10;
                        glyphInfo[n3 + 3] = (glyphInfo[n3 + 2] = 0.0f);
                        glyphInfo[n3 + 4] = min2;
                        glyphInfo[n3 + 5] = min3;
                        glyphInfo[n3 + 6] = max2;
                        glyphInfo[n3 + 7] = max3;
                    }
                }
            }
            else if (b3) {
                final int n21 = n5 - n7;
                glyphInfo[n3 + 0] = glyphInfo[n21 + 0];
                glyphInfo[n3 + 2] = glyphInfo[n21 + 2];
                glyphInfo[n3 + 4] = glyphInfo[n21 + 4];
                glyphInfo[n3 + 5] = glyphInfo[n21 + 5];
                glyphInfo[n3 + 6] = glyphInfo[n21 + 6];
                glyphInfo[n3 + 7] = glyphInfo[n21 + 7];
            }
            n3 += n7;
            n4 += n8;
        }
        if (b3 && !b2) {
            final int n22 = n3 - n7;
            System.arraycopy(glyphInfo, n22, glyphInfo, 0, glyphInfo.length - n22);
        }
        if (b) {
            final char[] chars = this.source.getChars();
            final int start = this.source.getStart();
            final int length = this.source.getLength();
            System.out.println("char info for " + length + " characters");
            int l = 0;
            while (l < length * 8) {
                System.out.println(" ch: " + Integer.toHexString(chars[start + this.v2l(l / 8)]) + " x: " + glyphInfo[l++] + " y: " + glyphInfo[l++] + " xa: " + glyphInfo[l++] + " ya: " + glyphInfo[l++] + " l: " + glyphInfo[l++] + " t: " + glyphInfo[l++] + " w: " + glyphInfo[l++] + " h: " + glyphInfo[l++]);
            }
        }
        return glyphInfo;
    }
    
    protected int l2v(final int n) {
        return ((this.source.getLayoutFlags() & 0x1) == 0x0) ? n : (this.source.getLength() - 1 - n);
    }
    
    protected int v2l(final int n) {
        return ((this.source.getLayoutFlags() & 0x1) == 0x0) ? n : (this.source.getLength() - 1 - n);
    }
    
    @Override
    public TextLineComponent getSubset(final int n, final int n2, final int n3) {
        return new ExtendedTextSourceLabel(this.source.getSubSource(n, n2 - n, n3), this.decorator);
    }
    
    @Override
    public String toString() {
        final TextSource source = this.source;
        final TextSource source2 = this.source;
        return source.toString(false);
    }
    
    @Override
    public int getNumJustificationInfos() {
        return this.getGV().getNumGlyphs();
    }
    
    @Override
    public void getJustificationInfos(final GlyphJustificationInfo[] array, final int n, final int n2, final int n3) {
        final StandardGlyphVector gv = this.getGV();
        final float[] charinfo = this.getCharinfo();
        final float size2D = gv.getFont().getSize2D();
        final GlyphJustificationInfo glyphJustificationInfo = new GlyphJustificationInfo(0.0f, false, 3, 0.0f, 0.0f, false, 3, 0.0f, 0.0f);
        final GlyphJustificationInfo glyphJustificationInfo2 = new GlyphJustificationInfo(size2D, true, 1, 0.0f, size2D, true, 1, 0.0f, size2D / 4.0f);
        final GlyphJustificationInfo glyphJustificationInfo3 = new GlyphJustificationInfo(size2D, true, 2, size2D, size2D, false, 3, 0.0f, 0.0f);
        final char[] chars = this.source.getChars();
        final int start = this.source.getStart();
        final int numGlyphs = gv.getNumGlyphs();
        int n4 = 0;
        int n5 = numGlyphs;
        final boolean b = (this.source.getLayoutFlags() & 0x1) == 0x0;
        if (n2 != 0 || n3 != this.source.getLength()) {
            if (b) {
                n4 = n2;
                n5 = n3;
            }
            else {
                n4 = numGlyphs - n3;
                n5 = numGlyphs - n2;
            }
        }
        for (int i = 0; i < numGlyphs; ++i) {
            GlyphJustificationInfo glyphJustificationInfo4 = null;
            if (i >= n4 && i < n5) {
                if (charinfo[i * 8 + 2] == 0.0f) {
                    glyphJustificationInfo4 = glyphJustificationInfo;
                }
                else {
                    final char c = chars[start + this.v2l(i)];
                    if (Character.isWhitespace(c)) {
                        glyphJustificationInfo4 = glyphJustificationInfo2;
                    }
                    else if ((c >= '\u4e00' && c < '\ua000') || (c >= '\uac00' && c < '\ud7b0') || (c >= '\uf900' && c < '\ufb00')) {
                        glyphJustificationInfo4 = glyphJustificationInfo3;
                    }
                    else {
                        glyphJustificationInfo4 = glyphJustificationInfo;
                    }
                }
            }
            array[n + i] = glyphJustificationInfo4;
        }
    }
    
    @Override
    public TextLineComponent applyJustificationDeltas(final float[] array, final int n, final boolean[] array2) {
        final float[] charinfo = this.getCharinfo().clone();
        array2[0] = false;
        final StandardGlyphVector gv = (StandardGlyphVector)this.getGV().clone();
        final float[] glyphPositions = gv.getGlyphPositions(null);
        final int numGlyphs = gv.getNumGlyphs();
        final char[] chars = this.source.getChars();
        final int start = this.source.getStart();
        float n2 = 0.0f;
        for (int i = 0; i < numGlyphs; ++i) {
            if (Character.isWhitespace(chars[start + this.v2l(i)])) {
                final float[] array3 = glyphPositions;
                final int n3 = i * 2;
                array3[n3] += n2;
                final float n4 = array[n + i * 2] + array[n + i * 2 + 1];
                final float[] array4 = charinfo;
                final int n5 = i * 8 + 0;
                array4[n5] += n2;
                final float[] array5 = charinfo;
                final int n6 = i * 8 + 4;
                array5[n6] += n2;
                final float[] array6 = charinfo;
                final int n7 = i * 8 + 2;
                array6[n7] += n4;
                n2 += n4;
            }
            else {
                final float n8 = n2 + array[n + i * 2];
                final float[] array7 = glyphPositions;
                final int n9 = i * 2;
                array7[n9] += n8;
                final float[] array8 = charinfo;
                final int n10 = i * 8 + 0;
                array8[n10] += n8;
                final float[] array9 = charinfo;
                final int n11 = i * 8 + 4;
                array9[n11] += n8;
                n2 = n8 + array[n + i * 2 + 1];
            }
        }
        final float[] array10 = glyphPositions;
        final int n12 = numGlyphs * 2;
        array10[n12] += n2;
        gv.setGlyphPositions(glyphPositions);
        final ExtendedTextSourceLabel extendedTextSourceLabel = new ExtendedTextSourceLabel(this.source, this.decorator);
        extendedTextSourceLabel.gv = gv;
        extendedTextSourceLabel.charinfo = charinfo;
        return extendedTextSourceLabel;
    }
}
