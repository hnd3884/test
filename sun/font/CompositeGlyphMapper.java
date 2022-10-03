package sun.font;

public class CompositeGlyphMapper extends CharToGlyphMapper
{
    public static final int SLOTMASK = -16777216;
    public static final int GLYPHMASK = 16777215;
    public static final int NBLOCKS = 216;
    public static final int BLOCKSZ = 256;
    public static final int MAXUNICODE = 55296;
    CompositeFont font;
    CharToGlyphMapper[] slotMappers;
    int[][] glyphMaps;
    private boolean hasExcludes;
    
    public CompositeGlyphMapper(final CompositeFont font) {
        this.font = font;
        this.initMapper();
        this.hasExcludes = (font.exclusionRanges != null && font.maxIndices != null);
    }
    
    public final int compositeGlyphCode(final int n, final int n2) {
        return n << 24 | (n2 & 0xFFFFFF);
    }
    
    private final void initMapper() {
        if (this.missingGlyph == -1) {
            if (this.glyphMaps == null) {
                this.glyphMaps = new int[216][];
            }
            this.slotMappers = new CharToGlyphMapper[this.font.numSlots];
            this.missingGlyph = this.font.getSlotFont(0).getMissingGlyphCode();
            this.missingGlyph = this.compositeGlyphCode(0, this.missingGlyph);
        }
    }
    
    private int getCachedGlyphCode(final int n) {
        if (n >= 55296) {
            return -1;
        }
        final int[] array;
        if ((array = this.glyphMaps[n >> 8]) == null) {
            return -1;
        }
        return array[n & 0xFF];
    }
    
    private void setCachedGlyphCode(final int n, final int n2) {
        if (n >= 55296) {
            return;
        }
        final int n3 = n >> 8;
        if (this.glyphMaps[n3] == null) {
            this.glyphMaps[n3] = new int[256];
            for (int i = 0; i < 256; ++i) {
                this.glyphMaps[n3][i] = -1;
            }
        }
        this.glyphMaps[n3][n & 0xFF] = n2;
    }
    
    private final CharToGlyphMapper getSlotMapper(final int n) {
        CharToGlyphMapper mapper = this.slotMappers[n];
        if (mapper == null) {
            mapper = this.font.getSlotFont(n).getMapper();
            this.slotMappers[n] = mapper;
        }
        return mapper;
    }
    
    private final int convertToGlyph(final int n) {
        for (int i = 0; i < this.font.numSlots; ++i) {
            if (!this.hasExcludes || !this.font.isExcludedChar(i, n)) {
                final CharToGlyphMapper slotMapper = this.getSlotMapper(i);
                final int charToGlyph = slotMapper.charToGlyph(n);
                if (charToGlyph != slotMapper.getMissingGlyphCode()) {
                    final int compositeGlyphCode = this.compositeGlyphCode(i, charToGlyph);
                    this.setCachedGlyphCode(n, compositeGlyphCode);
                    return compositeGlyphCode;
                }
            }
        }
        return this.missingGlyph;
    }
    
    @Override
    public int getNumGlyphs() {
        int n = 0;
        for (int i = 0; i < 1; ++i) {
            CharToGlyphMapper mapper = this.slotMappers[i];
            if (mapper == null) {
                mapper = this.font.getSlotFont(i).getMapper();
                this.slotMappers[i] = mapper;
            }
            n += mapper.getNumGlyphs();
        }
        return n;
    }
    
    @Override
    public int charToGlyph(final int n) {
        int n2 = this.getCachedGlyphCode(n);
        if (n2 == -1) {
            n2 = this.convertToGlyph(n);
        }
        return n2;
    }
    
    public int charToGlyph(final int n, final int n2) {
        if (n2 >= 0) {
            final CharToGlyphMapper slotMapper = this.getSlotMapper(n2);
            final int charToGlyph = slotMapper.charToGlyph(n);
            if (charToGlyph != slotMapper.getMissingGlyphCode()) {
                return this.compositeGlyphCode(n2, charToGlyph);
            }
        }
        return this.charToGlyph(n);
    }
    
    @Override
    public int charToGlyph(final char c) {
        int n = this.getCachedGlyphCode(c);
        if (n == -1) {
            n = this.convertToGlyph(c);
        }
        return n;
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
            if ((array2[i] = this.getCachedGlyphCode(n2)) == -1) {
                array2[i] = this.convertToGlyph(n2);
            }
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
    
    @Override
    public void charsToGlyphs(final int n, final char[] array, final int[] array2) {
        for (int i = 0; i < n; ++i) {
            final char c = array[i];
            if (c >= '\ud800' && c <= '\udbff' && i < n - 1) {
                final char c2 = array[i + 1];
                if (c2 >= '\udc00' && c2 <= '\udfff') {
                    final int n2 = (c - '\ud800') * 1024 + c2 - 56320 + 65536;
                    if ((array2[i] = this.getCachedGlyphCode(n2)) == -1) {
                        array2[i] = this.convertToGlyph(n2);
                    }
                    ++i;
                    array2[i] = 65535;
                    continue;
                }
            }
            if ((array2[i] = this.getCachedGlyphCode(c)) == -1) {
                array2[i] = this.convertToGlyph(c);
            }
        }
    }
    
    @Override
    public void charsToGlyphs(final int n, final int[] array, final int[] array2) {
        for (int i = 0; i < n; ++i) {
            final int n2 = array[i];
            array2[i] = this.getCachedGlyphCode(n2);
            if (array2[i] == -1) {
                array2[i] = this.convertToGlyph(n2);
            }
        }
    }
}
