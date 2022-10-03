package sun.font;

public final class Type1GlyphMapper extends CharToGlyphMapper
{
    Type1Font font;
    FontScaler scaler;
    
    public Type1GlyphMapper(final Type1Font font) {
        this.font = font;
        this.initMapper();
    }
    
    private void initMapper() {
        this.scaler = this.font.getScaler();
        try {
            this.missingGlyph = this.scaler.getMissingGlyphCode();
        }
        catch (final FontScalerException ex) {
            this.scaler = FontScaler.getNullScaler();
            try {
                this.missingGlyph = this.scaler.getMissingGlyphCode();
            }
            catch (final FontScalerException ex2) {
                this.missingGlyph = 0;
            }
        }
    }
    
    @Override
    public int getNumGlyphs() {
        try {
            return this.scaler.getNumGlyphs();
        }
        catch (final FontScalerException ex) {
            this.scaler = FontScaler.getNullScaler();
            return this.getNumGlyphs();
        }
    }
    
    @Override
    public int getMissingGlyphCode() {
        return this.missingGlyph;
    }
    
    @Override
    public boolean canDisplay(final char c) {
        try {
            return this.scaler.getGlyphCode(c) != this.missingGlyph;
        }
        catch (final FontScalerException ex) {
            this.scaler = FontScaler.getNullScaler();
            return this.canDisplay(c);
        }
    }
    
    @Override
    public int charToGlyph(final char c) {
        try {
            return this.scaler.getGlyphCode(c);
        }
        catch (final FontScalerException ex) {
            this.scaler = FontScaler.getNullScaler();
            return this.charToGlyph(c);
        }
    }
    
    @Override
    public int charToGlyph(final int n) {
        if (n < 0 || n > 65535) {
            return this.missingGlyph;
        }
        try {
            return this.scaler.getGlyphCode((char)n);
        }
        catch (final FontScalerException ex) {
            this.scaler = FontScaler.getNullScaler();
            return this.charToGlyph(n);
        }
    }
    
    @Override
    public void charsToGlyphs(final int n, final char[] array, final int[] array2) {
        for (int i = 0; i < n; ++i) {
            int n2 = array[i];
            if (n2 >= 55296 && n2 <= 56319 && i < n - 1) {
                final char c = array[i + 1];
                if (c >= '\udc00' && c <= '\udfff') {
                    n2 = (n2 - 55296) * 1024 + c - 56320 + 65536;
                    array2[i + 1] = 65535;
                }
            }
            array2[i] = this.charToGlyph(n2);
            if (n2 >= 65536) {
                ++i;
            }
        }
    }
    
    @Override
    public void charsToGlyphs(final int n, final int[] array, final int[] array2) {
        for (int i = 0; i < n; ++i) {
            array2[i] = this.charToGlyph(array[i]);
        }
    }
    
    @Override
    public boolean charsToGlyphsNS(final int n, final char[] array, final int[] array2) {
        for (int i = 0; i < n; ++i) {
            int n2 = array[i];
            if (n2 >= 55296 && n2 <= 56319 && i < n - 1) {
                final char c = array[i + 1];
                if (c >= '\udc00' && c <= '\udfff') {
                    n2 = (n2 - 55296) * 1024 + c - 56320 + 65536;
                    array2[i + 1] = 65535;
                }
            }
            array2[i] = this.charToGlyph(n2);
            if (n2 >= 768) {
                if (FontUtilities.isComplexCharCode(n2)) {
                    return true;
                }
                if (n2 >= 65536) {
                    ++i;
                }
            }
        }
        return false;
    }
}
