package com.lowagie.text.pdf;

import java.awt.font.GlyphVector;
import java.io.UnsupportedEncodingException;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Utilities;
import java.util.HashMap;

class FontDetails
{
    PdfIndirectReference indirectReference;
    PdfName fontName;
    BaseFont baseFont;
    TrueTypeFontUnicode ttu;
    CJKFont cjkFont;
    byte[] shortTag;
    HashMap longTag;
    IntHashtable cjkTag;
    int fontType;
    boolean symbolic;
    protected boolean subset;
    
    FontDetails(final PdfName fontName, final PdfIndirectReference indirectReference, final BaseFont baseFont) {
        this.subset = true;
        this.fontName = fontName;
        this.indirectReference = indirectReference;
        this.baseFont = baseFont;
        switch (this.fontType = baseFont.getFontType()) {
            case 0:
            case 1: {
                this.shortTag = new byte[256];
                break;
            }
            case 2: {
                this.cjkTag = new IntHashtable();
                this.cjkFont = (CJKFont)baseFont;
                break;
            }
            case 3: {
                this.longTag = new HashMap();
                this.ttu = (TrueTypeFontUnicode)baseFont;
                this.symbolic = baseFont.isFontSpecific();
                break;
            }
        }
    }
    
    PdfIndirectReference getIndirectReference() {
        return this.indirectReference;
    }
    
    PdfName getFontName() {
        return this.fontName;
    }
    
    BaseFont getBaseFont() {
        return this.baseFont;
    }
    
    byte[] convertToBytes(final String text) {
        byte[] b = null;
        switch (this.fontType) {
            case 5: {
                return this.baseFont.convertToBytes(text);
            }
            case 0:
            case 1: {
                b = this.baseFont.convertToBytes(text);
                for (int len = b.length, k = 0; k < len; ++k) {
                    this.shortTag[b[k] & 0xFF] = 1;
                }
                break;
            }
            case 2: {
                for (int len = text.length(), k = 0; k < len; ++k) {
                    this.cjkTag.put(this.cjkFont.getCidCode(text.charAt(k)), 0);
                }
                b = this.baseFont.convertToBytes(text);
                break;
            }
            case 4: {
                b = this.baseFont.convertToBytes(text);
                break;
            }
            case 3: {
                try {
                    int len = text.length();
                    int[] metrics = null;
                    final char[] glyph = new char[len];
                    int i = 0;
                    if (this.symbolic) {
                        b = PdfEncodings.convertToBytes(text, "symboltt");
                        len = b.length;
                        for (int j = 0; j < len; ++j) {
                            metrics = this.ttu.getMetricsTT(b[j] & 0xFF);
                            if (metrics != null) {
                                this.longTag.put(new Integer(metrics[0]), new int[] { metrics[0], metrics[1], this.ttu.getUnicodeDifferences(b[j] & 0xFF) });
                                glyph[i++] = (char)metrics[0];
                            }
                        }
                    }
                    else {
                        for (int j = 0; j < len; ++j) {
                            int val;
                            if (Utilities.isSurrogatePair(text, j)) {
                                val = Utilities.convertToUtf32(text, j);
                                ++j;
                            }
                            else {
                                val = text.charAt(j);
                            }
                            metrics = this.ttu.getMetricsTT(val);
                            if (metrics != null) {
                                final int m0 = metrics[0];
                                final Integer gl = new Integer(m0);
                                if (!this.longTag.containsKey(gl)) {
                                    this.longTag.put(gl, new int[] { m0, metrics[1], val });
                                }
                                glyph[i++] = (char)m0;
                            }
                        }
                    }
                    final String s = new String(glyph, 0, i);
                    b = s.getBytes("UnicodeBigUnmarked");
                }
                catch (final UnsupportedEncodingException e) {
                    throw new ExceptionConverter(e);
                }
                break;
            }
        }
        return b;
    }
    
    byte[] convertToBytes(final GlyphVector glyphVector) {
        if (this.fontType != 3 || this.symbolic) {
            throw new UnsupportedOperationException("Only supported for True Type Unicode fonts");
        }
        final char[] glyphs = new char[glyphVector.getNumGlyphs()];
        int glyphCount = 0;
        for (int i = 0; i < glyphs.length; ++i) {
            final int code = glyphVector.getGlyphCode(i);
            if (code != 65534) {
                if (code != 65535) {
                    glyphs[glyphCount++] = (char)code;
                    final Integer codeKey = code;
                    if (!this.longTag.containsKey(codeKey)) {
                        final int glyphWidth = this.ttu.getGlyphWidth(code);
                        final Integer charCode = this.ttu.getCharacterCode(code);
                        final int[] metrics = (charCode != null) ? new int[] { code, glyphWidth, charCode } : new int[] { code, glyphWidth };
                        this.longTag.put(codeKey, metrics);
                    }
                }
            }
        }
        final String s = new String(glyphs, 0, glyphCount);
        try {
            final byte[] b = s.getBytes("UnicodeBigUnmarked");
            return b;
        }
        catch (final UnsupportedEncodingException e) {
            throw new ExceptionConverter(e);
        }
    }
    
    void writeFont(final PdfWriter writer) {
        try {
            switch (this.fontType) {
                case 5: {
                    this.baseFont.writeFont(writer, this.indirectReference, null);
                    break;
                }
                case 0:
                case 1: {
                    int firstChar;
                    for (firstChar = 0; firstChar < 256 && this.shortTag[firstChar] == 0; ++firstChar) {}
                    int lastChar;
                    for (lastChar = 255; lastChar >= firstChar && this.shortTag[lastChar] == 0; --lastChar) {}
                    if (firstChar > 255) {
                        firstChar = 255;
                        lastChar = 255;
                    }
                    this.baseFont.writeFont(writer, this.indirectReference, new Object[] { new Integer(firstChar), new Integer(lastChar), this.shortTag, this.subset });
                    break;
                }
                case 2: {
                    this.baseFont.writeFont(writer, this.indirectReference, new Object[] { this.cjkTag });
                    break;
                }
                case 3: {
                    this.baseFont.writeFont(writer, this.indirectReference, new Object[] { this.longTag, this.subset });
                    break;
                }
            }
        }
        catch (final Exception e) {
            throw new ExceptionConverter(e);
        }
    }
    
    public boolean isSubset() {
        return this.subset;
    }
    
    public void setSubset(final boolean subset) {
        this.subset = subset;
    }
}
