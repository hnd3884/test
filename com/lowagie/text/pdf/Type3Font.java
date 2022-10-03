package com.lowagie.text.pdf;

import java.io.IOException;
import com.lowagie.text.DocumentException;
import com.lowagie.text.error_messages.MessageLocalization;
import java.util.HashMap;

public class Type3Font extends BaseFont
{
    private boolean[] usedSlot;
    private IntHashtable widths3;
    private HashMap char2glyph;
    private PdfWriter writer;
    private float llx;
    private float lly;
    private float urx;
    private float ury;
    private PageResources pageResources;
    private boolean colorized;
    
    public Type3Font(final PdfWriter writer, final char[] chars, final boolean colorized) {
        this(writer, colorized);
    }
    
    public Type3Font(final PdfWriter writer, final boolean colorized) {
        this.widths3 = new IntHashtable();
        this.char2glyph = new HashMap();
        this.llx = Float.NaN;
        this.pageResources = new PageResources();
        this.writer = writer;
        this.colorized = colorized;
        this.fontType = 5;
        this.usedSlot = new boolean[256];
    }
    
    public PdfContentByte defineGlyph(final char c, final float wx, final float llx, final float lly, final float urx, final float ury) {
        if (c == '\0' || c > '\u00ff') {
            throw new IllegalArgumentException(MessageLocalization.getComposedMessage("the.char.1.doesn.t.belong.in.this.type3.font", c));
        }
        this.usedSlot[c] = true;
        final Integer ck = new Integer(c);
        Type3Glyph glyph = this.char2glyph.get(ck);
        if (glyph != null) {
            return glyph;
        }
        this.widths3.put(c, (int)wx);
        if (!this.colorized) {
            if (Float.isNaN(this.llx)) {
                this.llx = llx;
                this.lly = lly;
                this.urx = urx;
                this.ury = ury;
            }
            else {
                this.llx = Math.min(this.llx, llx);
                this.lly = Math.min(this.lly, lly);
                this.urx = Math.max(this.urx, urx);
                this.ury = Math.max(this.ury, ury);
            }
        }
        glyph = new Type3Glyph(this.writer, this.pageResources, wx, llx, lly, urx, ury, this.colorized);
        this.char2glyph.put(ck, glyph);
        return glyph;
    }
    
    @Override
    public String[][] getFamilyFontName() {
        return this.getFullFontName();
    }
    
    @Override
    public float getFontDescriptor(final int key, final float fontSize) {
        return 0.0f;
    }
    
    @Override
    public String[][] getFullFontName() {
        return new String[][] { { "", "", "", "" } };
    }
    
    @Override
    public String[][] getAllNameEntries() {
        return new String[][] { { "4", "", "", "", "" } };
    }
    
    @Override
    public int getKerning(final int char1, final int char2) {
        return 0;
    }
    
    @Override
    public String getPostscriptFontName() {
        return "";
    }
    
    @Override
    protected int[] getRawCharBBox(final int c, final String name) {
        return null;
    }
    
    @Override
    int getRawWidth(final int c, final String name) {
        return 0;
    }
    
    @Override
    public boolean hasKernPairs() {
        return false;
    }
    
    @Override
    public boolean setKerning(final int char1, final int char2, final int kern) {
        return false;
    }
    
    @Override
    public void setPostscriptFontName(final String name) {
    }
    
    @Override
    void writeFont(final PdfWriter writer, final PdfIndirectReference ref, final Object[] params) throws DocumentException, IOException {
        if (this.writer != writer) {
            throw new IllegalArgumentException(MessageLocalization.getComposedMessage("type3.font.used.with.the.wrong.pdfwriter"));
        }
        int firstChar;
        for (firstChar = 0; firstChar < this.usedSlot.length && !this.usedSlot[firstChar]; ++firstChar) {}
        if (firstChar == this.usedSlot.length) {
            throw new DocumentException(MessageLocalization.getComposedMessage("no.glyphs.defined.for.type3.font"));
        }
        int lastChar;
        for (lastChar = this.usedSlot.length - 1; lastChar >= firstChar && !this.usedSlot[lastChar]; --lastChar) {}
        final int[] widths = new int[lastChar - firstChar + 1];
        final int[] invOrd = new int[lastChar - firstChar + 1];
        int invOrdIndx = 0;
        for (int w = 0, u = firstChar; u <= lastChar; ++u, ++w) {
            if (this.usedSlot[u]) {
                invOrd[invOrdIndx++] = u;
                widths[w] = this.widths3.get(u);
            }
        }
        final PdfArray diffs = new PdfArray();
        final PdfDictionary charprocs = new PdfDictionary();
        int last = -1;
        for (int k = 0; k < invOrdIndx; ++k) {
            final int c = invOrd[k];
            if (c > last) {
                last = c;
                diffs.add(new PdfNumber(last));
            }
            ++last;
            final int c2 = invOrd[k];
            String s = GlyphList.unicodeToName(c2);
            if (s == null) {
                s = "a" + c2;
            }
            final PdfName n = new PdfName(s);
            diffs.add(n);
            final Type3Glyph glyph = this.char2glyph.get(new Integer(c2));
            final PdfStream stream = new PdfStream(glyph.toPdf(null));
            stream.flateCompress(this.compressionLevel);
            final PdfIndirectReference refp = writer.addToBody(stream).getIndirectReference();
            charprocs.put(n, refp);
        }
        final PdfDictionary font = new PdfDictionary(PdfName.FONT);
        font.put(PdfName.SUBTYPE, PdfName.TYPE3);
        if (this.colorized) {
            font.put(PdfName.FONTBBOX, new PdfRectangle(0.0f, 0.0f, 0.0f, 0.0f));
        }
        else {
            font.put(PdfName.FONTBBOX, new PdfRectangle(this.llx, this.lly, this.urx, this.ury));
        }
        font.put(PdfName.FONTMATRIX, new PdfArray(new float[] { 0.001f, 0.0f, 0.0f, 0.001f, 0.0f, 0.0f }));
        font.put(PdfName.CHARPROCS, writer.addToBody(charprocs).getIndirectReference());
        final PdfDictionary encoding = new PdfDictionary();
        encoding.put(PdfName.DIFFERENCES, diffs);
        font.put(PdfName.ENCODING, writer.addToBody(encoding).getIndirectReference());
        font.put(PdfName.FIRSTCHAR, new PdfNumber(firstChar));
        font.put(PdfName.LASTCHAR, new PdfNumber(lastChar));
        font.put(PdfName.WIDTHS, writer.addToBody(new PdfArray(widths)).getIndirectReference());
        if (this.pageResources.hasResources()) {
            font.put(PdfName.RESOURCES, writer.addToBody(this.pageResources.getResources()).getIndirectReference());
        }
        writer.addToBody(font, ref);
    }
    
    public PdfStream getFullFontStream() {
        return null;
    }
    
    @Override
    byte[] convertToBytes(final String text) {
        final char[] cc = text.toCharArray();
        final byte[] b = new byte[cc.length];
        int p = 0;
        for (int k = 0; k < cc.length; ++k) {
            final char c = cc[k];
            if (this.charExists(c)) {
                b[p++] = (byte)c;
            }
        }
        if (b.length == p) {
            return b;
        }
        final byte[] b2 = new byte[p];
        System.arraycopy(b, 0, b2, 0, p);
        return b2;
    }
    
    @Override
    byte[] convertToBytes(final int char1) {
        if (this.charExists(char1)) {
            return new byte[] { (byte)char1 };
        }
        return new byte[0];
    }
    
    @Override
    public int getWidth(final int char1) {
        if (!this.widths3.containsKey(char1)) {
            throw new IllegalArgumentException(MessageLocalization.getComposedMessage("the.char.1.is.not.defined.in.a.type3.font", char1));
        }
        return this.widths3.get(char1);
    }
    
    @Override
    public int getWidth(final String text) {
        final char[] c = text.toCharArray();
        int total = 0;
        for (int k = 0; k < c.length; ++k) {
            total += this.getWidth(c[k]);
        }
        return total;
    }
    
    @Override
    public int[] getCharBBox(final int c) {
        return null;
    }
    
    @Override
    public boolean charExists(final int c) {
        return c > 0 && c < 256 && this.usedSlot[c];
    }
    
    @Override
    public boolean setCharAdvance(final int c, final int advance) {
        return false;
    }
}
