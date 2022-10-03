package com.lowagie.text.pdf;

import com.lowagie.text.error_messages.MessageLocalization;
import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import com.lowagie.text.pdf.fonts.cmaps.CMapParser;
import com.lowagie.text.pdf.fonts.cmaps.CMap;

public class CMapAwareDocumentFont extends DocumentFont
{
    private PdfDictionary fontDic;
    private int spaceWidth;
    private CMap toUnicodeCmap;
    private char[] cidbyte2uni;
    
    public CMapAwareDocumentFont(final PRIndirectReference refFont) {
        super(refFont);
        this.fontDic = (PdfDictionary)PdfReader.getPdfObjectRelease(refFont);
        this.processToUnicode();
        if (this.toUnicodeCmap == null) {
            this.processUni2Byte();
        }
        this.spaceWidth = super.getWidth(32);
        if (this.spaceWidth == 0) {
            this.spaceWidth = this.computeAverageWidth();
        }
    }
    
    private void processToUnicode() {
        final PdfObject toUni = this.fontDic.get(PdfName.TOUNICODE);
        if (toUni != null) {
            try {
                final byte[] touni = PdfReader.getStreamBytes((PRStream)PdfReader.getPdfObjectRelease(toUni));
                final CMapParser cmapParser = new CMapParser();
                this.toUnicodeCmap = cmapParser.parse(new ByteArrayInputStream(touni));
            }
            catch (final IOException e) {
                throw new Error("Unable to process ToUnicode map - " + e.getMessage(), e);
            }
        }
    }
    
    private void processUni2Byte() {
        final IntHashtable uni2byte = this.getUni2Byte();
        final int[] e = uni2byte.toOrderedKeys();
        this.cidbyte2uni = new char[256];
        for (final int element : e) {
            final int n = uni2byte.get(element);
            if (this.cidbyte2uni[n] == '\0') {
                this.cidbyte2uni[n] = (char)element;
            }
        }
    }
    
    private int computeAverageWidth() {
        int count = 0;
        int total = 0;
        for (int i = 0; i < super.widths.length; ++i) {
            if (super.widths[i] != 0) {
                total += super.widths[i];
                ++count;
            }
        }
        return (count != 0) ? (total / count) : 0;
    }
    
    @Override
    public int getWidth(final int char1) {
        if (char1 == 32) {
            return this.spaceWidth;
        }
        return super.getWidth(char1);
    }
    
    private String decodeSingleCID(final byte[] bytes, final int offset, final int len) {
        if (this.hasUnicodeCMAP()) {
            if (offset + len > bytes.length) {
                throw new ArrayIndexOutOfBoundsException(MessageLocalization.getComposedMessage("invalid.index.1", offset + len));
            }
            return this.toUnicodeCmap.lookup(bytes, offset, len);
        }
        else {
            if (len == 1) {
                return new String(this.cidbyte2uni, 0xFF & bytes[offset], 1);
            }
            throw new Error("Multi-byte glyphs not implemented yet");
        }
    }
    
    public boolean hasUnicodeCMAP() {
        return this.toUnicodeCmap != null;
    }
    
    public String decode(final byte[] cidbytes, final int offset, final int len) {
        final StringBuffer sb = new StringBuffer();
        for (int i = offset; i < offset + len; ++i) {
            String rslt = this.decodeSingleCID(cidbytes, i, 1);
            if (rslt == null && i + 1 < offset + len) {
                rslt = this.decodeSingleCID(cidbytes, i, 2);
                ++i;
            }
            if (rslt != null) {
                sb.append(rslt);
            }
        }
        return sb.toString();
    }
    
    public String decode(final String chars) {
        final StringBuffer sb = new StringBuffer();
        for (final char c : chars.toCharArray()) {
            final String result = this.decode(c);
            if (result != null) {
                sb.append(result);
            }
        }
        return sb.toString();
    }
    
    public String decode(final char c) throws Error {
        String result;
        if (this.hasUnicodeCMAP()) {
            result = this.toUnicodeCmap.lookup(c);
        }
        else {
            if (c > '\u00ff') {
                throw new Error("Multi-byte glyphs not implemented yet");
            }
            result = new String(this.cidbyte2uni, '\u00ff' & c, 1);
        }
        return result;
    }
    
    @Deprecated
    public String encode(final byte[] bytes, final int offset, final int len) {
        return this.decode(bytes, offset, len);
    }
}
