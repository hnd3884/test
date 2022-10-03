package java.awt.font;

import java.text.CharacterIterator;
import sun.font.BidiUtils;
import sun.font.TextLabelFactory;
import java.awt.Font;
import java.util.Hashtable;
import java.util.Map;
import sun.font.AttributeValues;
import java.text.AttributedCharacterIterator;
import java.text.BreakIterator;
import sun.font.TextLineComponent;
import java.text.Bidi;

public final class TextMeasurer implements Cloneable
{
    private static float EST_LINES;
    private FontRenderContext fFrc;
    private int fStart;
    private char[] fChars;
    private Bidi fBidi;
    private byte[] fLevels;
    private TextLineComponent[] fComponents;
    private int fComponentStart;
    private int fComponentLimit;
    private boolean haveLayoutWindow;
    private BreakIterator fLineBreak;
    private CharArrayIterator charIter;
    int layoutCount;
    int layoutCharCount;
    private StyledParagraph fParagraph;
    private boolean fIsDirectionLTR;
    private byte fBaseline;
    private float[] fBaselineOffsets;
    private float fJustifyRatio;
    private int formattedChars;
    private static boolean wantStats;
    private boolean collectStats;
    
    public TextMeasurer(final AttributedCharacterIterator attributedCharacterIterator, final FontRenderContext fFrc) {
        this.fLineBreak = null;
        this.charIter = null;
        this.layoutCount = 0;
        this.layoutCharCount = 0;
        this.fJustifyRatio = 1.0f;
        this.formattedChars = 0;
        this.collectStats = false;
        this.fFrc = fFrc;
        this.initAll(attributedCharacterIterator);
    }
    
    @Override
    protected Object clone() {
        TextMeasurer textMeasurer;
        try {
            textMeasurer = (TextMeasurer)super.clone();
        }
        catch (final CloneNotSupportedException ex) {
            throw new Error();
        }
        if (this.fComponents != null) {
            textMeasurer.fComponents = this.fComponents.clone();
        }
        return textMeasurer;
    }
    
    private void invalidateComponents() {
        final int length = this.fChars.length;
        this.fComponentLimit = length;
        this.fComponentStart = length;
        this.fComponents = null;
        this.haveLayoutWindow = false;
    }
    
    private void initAll(final AttributedCharacterIterator attributedCharacterIterator) {
        this.fStart = attributedCharacterIterator.getBeginIndex();
        this.fChars = new char[attributedCharacterIterator.getEndIndex() - this.fStart];
        int n = 0;
        for (char c = attributedCharacterIterator.first(); c != '\uffff'; c = attributedCharacterIterator.next()) {
            this.fChars[n++] = c;
        }
        attributedCharacterIterator.first();
        this.fBidi = new Bidi(attributedCharacterIterator);
        if (this.fBidi.isLeftToRight()) {
            this.fBidi = null;
        }
        attributedCharacterIterator.first();
        final Map<AttributedCharacterIterator.Attribute, Object> attributes = attributedCharacterIterator.getAttributes();
        final NumericShaper numericShaping = AttributeValues.getNumericShaping(attributes);
        if (numericShaping != null) {
            numericShaping.shape(this.fChars, 0, this.fChars.length);
        }
        this.fParagraph = new StyledParagraph(attributedCharacterIterator, this.fChars);
        this.fJustifyRatio = AttributeValues.getJustification(attributes);
        if (TextLine.advanceToFirstFont(attributedCharacterIterator)) {
            final Font fontAtCurrentPos = TextLine.getFontAtCurrentPos(attributedCharacterIterator);
            final int n2 = attributedCharacterIterator.getIndex() - attributedCharacterIterator.getBeginIndex();
            final LineMetrics lineMetrics = fontAtCurrentPos.getLineMetrics(this.fChars, n2, n2 + 1, this.fFrc);
            this.fBaseline = (byte)lineMetrics.getBaselineIndex();
            this.fBaselineOffsets = lineMetrics.getBaselineOffsets();
        }
        else {
            this.fBaseline = TextLayout.getBaselineFromGraphic((GraphicAttribute)attributes.get(TextAttribute.CHAR_REPLACEMENT));
            this.fBaselineOffsets = new Font(new Hashtable<AttributedCharacterIterator.Attribute, Object>(5, 0.9f)).getLineMetrics(" ", 0, 1, this.fFrc).getBaselineOffsets();
        }
        this.fBaselineOffsets = TextLine.getNormalizedOffsets(this.fBaselineOffsets, this.fBaseline);
        this.invalidateComponents();
    }
    
    private void generateComponents(final int fComponentStart, final int fComponentLimit) {
        if (this.collectStats) {
            this.formattedChars += fComponentLimit - fComponentStart;
        }
        final TextLabelFactory textLabelFactory = new TextLabelFactory(this.fFrc, this.fChars, this.fBidi, 0);
        int[] inverseMap = null;
        if (this.fBidi != null) {
            this.fLevels = BidiUtils.getLevels(this.fBidi);
            inverseMap = BidiUtils.createInverseMap(BidiUtils.createVisualToLogicalMap(this.fLevels));
            this.fIsDirectionLTR = this.fBidi.baseIsLeftToRight();
        }
        else {
            this.fLevels = null;
            this.fIsDirectionLTR = true;
        }
        try {
            this.fComponents = TextLine.getComponents(this.fParagraph, this.fChars, fComponentStart, fComponentLimit, inverseMap, this.fLevels, textLabelFactory);
        }
        catch (final IllegalArgumentException ex) {
            System.out.println("startingAt=" + fComponentStart + "; endingAt=" + fComponentLimit);
            System.out.println("fComponentLimit=" + this.fComponentLimit);
            throw ex;
        }
        this.fComponentStart = fComponentStart;
        this.fComponentLimit = fComponentLimit;
    }
    
    private int calcLineBreak(final int n, final float n2) {
        int n3 = n;
        float n4 = n2;
        int fComponentStart = this.fComponentStart;
        int i;
        for (i = 0; i < this.fComponents.length; ++i) {
            final int n5 = fComponentStart + this.fComponents[i].getNumCharacters();
            if (n5 > n3) {
                break;
            }
            fComponentStart = n5;
        }
        while (i < this.fComponents.length) {
            final TextLineComponent textLineComponent = this.fComponents[i];
            final int numCharacters = textLineComponent.getNumCharacters();
            final int lineBreakIndex = textLineComponent.getLineBreakIndex(n3 - fComponentStart, n4);
            if (lineBreakIndex != numCharacters || i >= this.fComponents.length) {
                return fComponentStart + lineBreakIndex;
            }
            n4 -= textLineComponent.getAdvanceBetween(n3 - fComponentStart, lineBreakIndex);
            fComponentStart = (n3 = fComponentStart + numCharacters);
            ++i;
        }
        if (this.fComponentLimit < this.fChars.length) {
            this.generateComponents(n, this.fChars.length);
            return this.calcLineBreak(n, n2);
        }
        return this.fChars.length;
    }
    
    private int trailingCdWhitespaceStart(final int n, final int n2) {
        if (this.fLevels != null) {
            final byte b = (byte)(this.fIsDirectionLTR ? 0 : 1);
            int n3 = n2;
            while (--n3 >= n) {
                if (this.fLevels[n3] % 2 == b || Character.getDirectionality(this.fChars[n3]) != 12) {
                    return ++n3;
                }
            }
        }
        return n;
    }
    
    private TextLineComponent[] makeComponentsOnRange(final int n, final int n2) {
        final int trailingCdWhitespaceStart = this.trailingCdWhitespaceStart(n, n2);
        int fComponentStart = this.fComponentStart;
        int i;
        for (i = 0; i < this.fComponents.length; ++i) {
            final int n3 = fComponentStart + this.fComponents[i].getNumCharacters();
            if (n3 > n) {
                break;
            }
            fComponentStart = n3;
        }
        boolean b = false;
        int n4 = fComponentStart;
        int n5 = i;
        int j = 1;
        while (j != 0) {
            final int n6 = n4 + this.fComponents[n5].getNumCharacters();
            if (trailingCdWhitespaceStart > Math.max(n4, n) && trailingCdWhitespaceStart < Math.min(n6, n2)) {
                b = true;
            }
            if (n6 >= n2) {
                j = 0;
            }
            else {
                n4 = n6;
            }
            ++n5;
        }
        int n7 = n5 - i;
        if (b) {
            ++n7;
        }
        final TextLineComponent[] array = new TextLineComponent[n7];
        int n8 = 0;
        int k = n;
        int n9 = trailingCdWhitespaceStart;
        int n10;
        if (n9 == n) {
            n10 = (this.fIsDirectionLTR ? 0 : 1);
            n9 = n2;
        }
        else {
            n10 = 2;
        }
        while (k < n2) {
            final int n11 = fComponentStart + this.fComponents[i].getNumCharacters();
            final int max = Math.max(k, fComponentStart);
            final int min = Math.min(n9, n11);
            array[n8++] = this.fComponents[i].getSubset(max - fComponentStart, min - fComponentStart, n10);
            k += min - max;
            if (k == n9) {
                n9 = n2;
                n10 = (this.fIsDirectionLTR ? 0 : 1);
            }
            if (k == n11) {
                ++i;
                fComponentStart = n11;
            }
        }
        return array;
    }
    
    private TextLine makeTextLineOnRange(final int n, final int n2) {
        int[] inverseMap = null;
        byte[] levels = null;
        if (this.fBidi != null) {
            levels = BidiUtils.getLevels(this.fBidi.createLineBidi(n, n2));
            inverseMap = BidiUtils.createInverseMap(BidiUtils.createVisualToLogicalMap(levels));
        }
        return new TextLine(this.fFrc, this.makeComponentsOnRange(n, n2), this.fBaselineOffsets, this.fChars, n, n2, inverseMap, levels, this.fIsDirectionLTR);
    }
    
    private void ensureComponents(final int n, final int n2) {
        if (n < this.fComponentStart || n2 > this.fComponentLimit) {
            this.generateComponents(n, n2);
        }
    }
    
    private void makeLayoutWindow(final int n) {
        int preceding = n;
        int n2 = this.fChars.length;
        if (this.layoutCount > 0 && !this.haveLayoutWindow) {
            n2 = Math.min(n + (int)(Math.max(this.layoutCharCount / this.layoutCount, 1) * TextMeasurer.EST_LINES), this.fChars.length);
        }
        if (n > 0 || n2 < this.fChars.length) {
            if (this.charIter == null) {
                this.charIter = new CharArrayIterator(this.fChars);
            }
            else {
                this.charIter.reset(this.fChars);
            }
            if (this.fLineBreak == null) {
                this.fLineBreak = BreakIterator.getLineInstance();
            }
            this.fLineBreak.setText(this.charIter);
            if (n > 0 && !this.fLineBreak.isBoundary(n)) {
                preceding = this.fLineBreak.preceding(n);
            }
            if (n2 < this.fChars.length && !this.fLineBreak.isBoundary(n2)) {
                n2 = this.fLineBreak.following(n2);
            }
        }
        this.ensureComponents(preceding, n2);
        this.haveLayoutWindow = true;
    }
    
    public int getLineBreakIndex(final int n, final float n2) {
        final int n3 = n - this.fStart;
        if (!this.haveLayoutWindow || n3 < this.fComponentStart || n3 >= this.fComponentLimit) {
            this.makeLayoutWindow(n3);
        }
        return this.calcLineBreak(n3, n2) + this.fStart;
    }
    
    public float getAdvanceBetween(final int n, final int n2) {
        final int n3 = n - this.fStart;
        final int n4 = n2 - this.fStart;
        this.ensureComponents(n3, n4);
        return this.makeTextLineOnRange(n3, n4).getMetrics().advance;
    }
    
    public TextLayout getLayout(final int n, final int n2) {
        final int n3 = n - this.fStart;
        final int n4 = n2 - this.fStart;
        this.ensureComponents(n3, n4);
        final TextLine textLineOnRange = this.makeTextLineOnRange(n3, n4);
        if (n4 < this.fChars.length) {
            this.layoutCharCount += n2 - n;
            ++this.layoutCount;
        }
        return new TextLayout(textLineOnRange, this.fBaseline, this.fBaselineOffsets, this.fJustifyRatio);
    }
    
    private void printStats() {
        System.out.println("formattedChars: " + this.formattedChars);
        this.collectStats = false;
    }
    
    public void insertChar(final AttributedCharacterIterator attributedCharacterIterator, final int index) {
        if (this.collectStats) {
            this.printStats();
        }
        if (TextMeasurer.wantStats) {
            this.collectStats = true;
        }
        this.fStart = attributedCharacterIterator.getBeginIndex();
        final int endIndex = attributedCharacterIterator.getEndIndex();
        if (endIndex - this.fStart != this.fChars.length + 1) {
            this.initAll(attributedCharacterIterator);
        }
        final char[] fChars = new char[endIndex - this.fStart];
        final int n = index - this.fStart;
        System.arraycopy(this.fChars, 0, fChars, 0, n);
        fChars[n] = attributedCharacterIterator.setIndex(index);
        System.arraycopy(this.fChars, n, fChars, n + 1, endIndex - index - 1);
        this.fChars = fChars;
        if (this.fBidi != null || Bidi.requiresBidi(fChars, n, n + 1) || attributedCharacterIterator.getAttribute(TextAttribute.BIDI_EMBEDDING) != null) {
            this.fBidi = new Bidi(attributedCharacterIterator);
            if (this.fBidi.isLeftToRight()) {
                this.fBidi = null;
            }
        }
        this.fParagraph = StyledParagraph.insertChar(attributedCharacterIterator, this.fChars, index, this.fParagraph);
        this.invalidateComponents();
    }
    
    public void deleteChar(final AttributedCharacterIterator attributedCharacterIterator, final int n) {
        this.fStart = attributedCharacterIterator.getBeginIndex();
        final int endIndex = attributedCharacterIterator.getEndIndex();
        if (endIndex - this.fStart != this.fChars.length - 1) {
            this.initAll(attributedCharacterIterator);
        }
        final char[] fChars = new char[endIndex - this.fStart];
        final int n2 = n - this.fStart;
        System.arraycopy(this.fChars, 0, fChars, 0, n - this.fStart);
        System.arraycopy(this.fChars, n2 + 1, fChars, n2, endIndex - n);
        this.fChars = fChars;
        if (this.fBidi != null) {
            this.fBidi = new Bidi(attributedCharacterIterator);
            if (this.fBidi.isLeftToRight()) {
                this.fBidi = null;
            }
        }
        this.fParagraph = StyledParagraph.deleteChar(attributedCharacterIterator, this.fChars, n, this.fParagraph);
        this.invalidateComponents();
    }
    
    char[] getChars() {
        return this.fChars;
    }
    
    static {
        TextMeasurer.EST_LINES = 2.1f;
        TextMeasurer.wantStats = false;
    }
}
