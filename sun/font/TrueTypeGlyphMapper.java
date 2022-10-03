package sun.font;

import java.util.Locale;
import java.nio.ByteBuffer;

public class TrueTypeGlyphMapper extends CharToGlyphMapper
{
    static final char REVERSE_SOLIDUS = '\\';
    static final char JA_YEN = '¥';
    static final char JA_FULLWIDTH_TILDE_CHAR = '\uff5e';
    static final char JA_WAVE_DASH_CHAR = '\u301c';
    static final boolean isJAlocale;
    private final boolean needsJAremapping;
    private boolean remapJAWaveDash;
    TrueTypeFont font;
    CMap cmap;
    int numGlyphs;
    
    public TrueTypeGlyphMapper(final TrueTypeFont font) {
        this.font = font;
        try {
            this.cmap = CMap.initialize(font);
        }
        catch (final Exception ex) {
            this.cmap = null;
        }
        if (this.cmap == null) {
            this.handleBadCMAP();
        }
        this.missingGlyph = 0;
        final ByteBuffer tableBuffer = font.getTableBuffer(1835104368);
        if (tableBuffer != null && tableBuffer.capacity() >= 6) {
            this.numGlyphs = tableBuffer.getChar(4);
        }
        else {
            this.handleBadCMAP();
        }
        if (FontUtilities.isSolaris && TrueTypeGlyphMapper.isJAlocale && font.supportsJA()) {
            this.needsJAremapping = true;
            if (FontUtilities.isSolaris8 && this.getGlyphFromCMAP(12316) == this.missingGlyph) {
                this.remapJAWaveDash = true;
            }
        }
        else {
            this.needsJAremapping = false;
        }
    }
    
    @Override
    public int getNumGlyphs() {
        return this.numGlyphs;
    }
    
    private char getGlyphFromCMAP(final int n) {
        try {
            final char glyph = this.cmap.getGlyph(n);
            if (glyph < this.numGlyphs || glyph >= '\ufffe') {
                return glyph;
            }
            if (FontUtilities.isLogging()) {
                FontUtilities.getLogger().warning(this.font + " out of range glyph id=" + Integer.toHexString(glyph) + " for char " + Integer.toHexString(n));
            }
            return (char)this.missingGlyph;
        }
        catch (final Exception ex) {
            this.handleBadCMAP();
            return (char)this.missingGlyph;
        }
    }
    
    private void handleBadCMAP() {
        if (FontUtilities.isLogging()) {
            FontUtilities.getLogger().severe("Null Cmap for " + this.font + "substituting for this font");
        }
        SunFontManager.getInstance().deRegisterBadFont(this.font);
        this.cmap = CMap.theNullCmap;
    }
    
    private final char remapJAChar(final char c) {
        switch (c) {
            case '\\': {
                return '¥';
            }
            case '\u301c': {
                if (this.remapJAWaveDash) {
                    return '\uff5e';
                }
                break;
            }
        }
        return c;
    }
    
    private final int remapJAIntChar(final int n) {
        switch (n) {
            case 92: {
                return 165;
            }
            case 12316: {
                if (this.remapJAWaveDash) {
                    return 65374;
                }
                break;
            }
        }
        return n;
    }
    
    @Override
    public int charToGlyph(char remapJAChar) {
        if (this.needsJAremapping) {
            remapJAChar = this.remapJAChar(remapJAChar);
        }
        final char glyphFromCMAP = this.getGlyphFromCMAP(remapJAChar);
        if (this.font.checkUseNatives() && glyphFromCMAP < this.font.glyphToCharMap.length) {
            this.font.glyphToCharMap[glyphFromCMAP] = remapJAChar;
        }
        return glyphFromCMAP;
    }
    
    @Override
    public int charToGlyph(int remapJAIntChar) {
        if (this.needsJAremapping) {
            remapJAIntChar = this.remapJAIntChar(remapJAIntChar);
        }
        final char glyphFromCMAP = this.getGlyphFromCMAP(remapJAIntChar);
        if (this.font.checkUseNatives() && glyphFromCMAP < this.font.glyphToCharMap.length) {
            this.font.glyphToCharMap[glyphFromCMAP] = (char)remapJAIntChar;
        }
        return glyphFromCMAP;
    }
    
    @Override
    public void charsToGlyphs(final int n, final int[] array, final int[] array2) {
        for (int i = 0; i < n; ++i) {
            if (this.needsJAremapping) {
                array2[i] = this.getGlyphFromCMAP(this.remapJAIntChar(array[i]));
            }
            else {
                array2[i] = this.getGlyphFromCMAP(array[i]);
            }
            if (this.font.checkUseNatives() && array2[i] < this.font.glyphToCharMap.length) {
                this.font.glyphToCharMap[array2[i]] = (char)array[i];
            }
        }
    }
    
    @Override
    public void charsToGlyphs(final int n, final char[] array, final int[] array2) {
        for (int i = 0; i < n; ++i) {
            char remapJAChar;
            if (this.needsJAremapping) {
                remapJAChar = this.remapJAChar(array[i]);
            }
            else {
                remapJAChar = array[i];
            }
            if (remapJAChar >= '\ud800' && remapJAChar <= '\udbff' && i < n - 1) {
                final char c = array[i + 1];
                if (c >= '\udc00' && c <= '\udfff') {
                    array2[i] = this.getGlyphFromCMAP((remapJAChar - '\ud800') * 1024 + c - 56320 + 65536);
                    ++i;
                    array2[i] = 65535;
                    continue;
                }
            }
            array2[i] = this.getGlyphFromCMAP(remapJAChar);
            if (this.font.checkUseNatives() && array2[i] < this.font.glyphToCharMap.length) {
                this.font.glyphToCharMap[array2[i]] = remapJAChar;
            }
        }
    }
    
    @Override
    public boolean charsToGlyphsNS(final int n, final char[] array, final int[] array2) {
        for (int i = 0; i < n; ++i) {
            int remapJAChar;
            if (this.needsJAremapping) {
                remapJAChar = this.remapJAChar(array[i]);
            }
            else {
                remapJAChar = array[i];
            }
            if (remapJAChar >= 55296 && remapJAChar <= 56319 && i < n - 1) {
                final char c = array[i + 1];
                if (c >= '\udc00' && c <= '\udfff') {
                    remapJAChar = (remapJAChar - 55296) * 1024 + c - 56320 + 65536;
                    array2[i + 1] = 65535;
                }
            }
            array2[i] = this.getGlyphFromCMAP(remapJAChar);
            if (this.font.checkUseNatives() && array2[i] < this.font.glyphToCharMap.length) {
                this.font.glyphToCharMap[array2[i]] = (char)remapJAChar;
            }
            if (remapJAChar >= 768) {
                if (FontUtilities.isComplexCharCode(remapJAChar)) {
                    return true;
                }
                if (remapJAChar >= 65536) {
                    ++i;
                }
            }
        }
        return false;
    }
    
    boolean hasSupplementaryChars() {
        return this.cmap instanceof CMap.CMapFormat8 || this.cmap instanceof CMap.CMapFormat10 || this.cmap instanceof CMap.CMapFormat12;
    }
    
    static {
        isJAlocale = Locale.JAPAN.equals(Locale.getDefault());
    }
}
