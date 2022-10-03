package sun.font;

public abstract class CharToGlyphMapper
{
    public static final int HI_SURROGATE_START = 55296;
    public static final int HI_SURROGATE_END = 56319;
    public static final int LO_SURROGATE_START = 56320;
    public static final int LO_SURROGATE_END = 57343;
    public static final int UNINITIALIZED_GLYPH = -1;
    public static final int INVISIBLE_GLYPH_ID = 65535;
    public static final int INVISIBLE_GLYPHS = 65534;
    protected int missingGlyph;
    
    public CharToGlyphMapper() {
        this.missingGlyph = -1;
    }
    
    public int getMissingGlyphCode() {
        return this.missingGlyph;
    }
    
    public boolean canDisplay(final char c) {
        return this.charToGlyph(c) != this.missingGlyph;
    }
    
    public boolean canDisplay(final int n) {
        return this.charToGlyph(n) != this.missingGlyph;
    }
    
    public int charToGlyph(final char c) {
        final char[] array = { '\0' };
        final int[] array2 = { 0 };
        array[0] = c;
        this.charsToGlyphs(1, array, array2);
        return array2[0];
    }
    
    public int charToGlyph(final int n) {
        final int[] array = { 0 };
        final int[] array2 = { 0 };
        array[0] = n;
        this.charsToGlyphs(1, array, array2);
        return array2[0];
    }
    
    public abstract int getNumGlyphs();
    
    public abstract void charsToGlyphs(final int p0, final char[] p1, final int[] p2);
    
    public abstract boolean charsToGlyphsNS(final int p0, final char[] p1, final int[] p2);
    
    public abstract void charsToGlyphs(final int p0, final int[] p1, final int[] p2);
}
