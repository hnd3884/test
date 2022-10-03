package com.lowagie.text.pdf;

import com.lowagie.text.DocumentException;
import com.lowagie.text.ExceptionConverter;
import java.util.HashMap;

public class DocumentFont extends BaseFont
{
    private HashMap metrics;
    private String fontName;
    private PRIndirectReference refFont;
    private PdfDictionary font;
    private IntHashtable uni2byte;
    private IntHashtable diffmap;
    private float Ascender;
    private float CapHeight;
    private float Descender;
    private float ItalicAngle;
    private float llx;
    private float lly;
    private float urx;
    private float ury;
    private boolean isType0;
    private BaseFont cjkMirror;
    private static String[] cjkNames;
    private static String[] cjkEncs;
    private static String[] cjkNames2;
    private static String[] cjkEncs2;
    private static final int[] stdEnc;
    
    DocumentFont(final PRIndirectReference refFont) {
        this.metrics = new HashMap();
        this.uni2byte = new IntHashtable();
        this.Ascender = 800.0f;
        this.CapHeight = 700.0f;
        this.Descender = -200.0f;
        this.ItalicAngle = 0.0f;
        this.llx = -50.0f;
        this.lly = -200.0f;
        this.urx = 100.0f;
        this.ury = 900.0f;
        this.isType0 = false;
        this.encoding = "";
        this.fontSpecific = false;
        this.refFont = refFont;
        this.fontType = 4;
        this.font = (PdfDictionary)PdfReader.getPdfObject(refFont);
        final PdfName asName = this.font.getAsName(PdfName.BASEFONT);
        if (asName != null) {
            this.fontName = PdfName.decodeName(asName.toString());
        }
        else {
            this.fontName = "badFontName";
        }
        final PdfName subType = this.font.getAsName(PdfName.SUBTYPE);
        if (PdfName.TYPE1.equals(subType) || PdfName.TRUETYPE.equals(subType)) {
            this.doType1TT();
        }
        else {
            for (int k = 0; k < DocumentFont.cjkNames.length; ++k) {
                if (this.fontName.startsWith(DocumentFont.cjkNames[k])) {
                    this.fontName = DocumentFont.cjkNames[k];
                    try {
                        this.cjkMirror = BaseFont.createFont(this.fontName, DocumentFont.cjkEncs[k], false);
                    }
                    catch (final Exception e) {
                        throw new ExceptionConverter(e);
                    }
                    return;
                }
            }
            final PdfName encName = this.font.getAsName(PdfName.ENCODING);
            if (encName != null) {
                final String enc = PdfName.decodeName(encName.toString());
                for (int i = 0; i < DocumentFont.cjkEncs2.length; ++i) {
                    if (enc.startsWith(DocumentFont.cjkEncs2[i])) {
                        try {
                            if (i > 3) {
                                i -= 4;
                            }
                            this.cjkMirror = BaseFont.createFont(DocumentFont.cjkNames2[i], DocumentFont.cjkEncs2[i], false);
                        }
                        catch (final Exception e2) {
                            throw new ExceptionConverter(e2);
                        }
                        return;
                    }
                }
                this.encoding = enc;
                if (PdfName.TYPE0.equals(subType) && enc.equals("Identity-H")) {
                    this.processType0(this.font);
                    this.isType0 = true;
                }
            }
        }
    }
    
    private void processType0(final PdfDictionary font) {
        try {
            final PdfObject toUniObject = PdfReader.getPdfObjectRelease(font.get(PdfName.TOUNICODE));
            final PdfArray df = (PdfArray)PdfReader.getPdfObjectRelease(font.get(PdfName.DESCENDANTFONTS));
            final PdfDictionary cidft = (PdfDictionary)PdfReader.getPdfObjectRelease(df.getPdfObject(0));
            final PdfNumber dwo = (PdfNumber)PdfReader.getPdfObjectRelease(cidft.get(PdfName.DW));
            int dw = 1000;
            if (dwo != null) {
                dw = dwo.intValue();
            }
            final IntHashtable widths = this.readWidths((PdfArray)PdfReader.getPdfObjectRelease(cidft.get(PdfName.W)));
            final PdfDictionary fontDesc = (PdfDictionary)PdfReader.getPdfObjectRelease(cidft.get(PdfName.FONTDESCRIPTOR));
            this.fillFontDesc(fontDesc);
            if (toUniObject != null) {
                this.fillMetrics(PdfReader.getStreamBytes((PRStream)toUniObject), widths, dw);
            }
        }
        catch (final Exception e) {
            throw new ExceptionConverter(e);
        }
    }
    
    private IntHashtable readWidths(final PdfArray ws) {
        final IntHashtable hh = new IntHashtable();
        if (ws == null) {
            return hh;
        }
        for (int k = 0; k < ws.size(); ++k) {
            int c1 = ((PdfNumber)PdfReader.getPdfObjectRelease(ws.getPdfObject(k))).intValue();
            final PdfObject obj = PdfReader.getPdfObjectRelease(ws.getPdfObject(++k));
            if (obj.isArray()) {
                final PdfArray a2 = (PdfArray)obj;
                for (int j = 0; j < a2.size(); ++j) {
                    final int c2 = ((PdfNumber)PdfReader.getPdfObjectRelease(a2.getPdfObject(j))).intValue();
                    hh.put(c1++, c2);
                }
            }
            else {
                final int c3 = ((PdfNumber)obj).intValue();
                final int w = ((PdfNumber)PdfReader.getPdfObjectRelease(ws.getPdfObject(++k))).intValue();
                while (c1 <= c3) {
                    hh.put(c1, w);
                    ++c1;
                }
            }
        }
        return hh;
    }
    
    private String decodeString(final PdfString ps) {
        if (ps.isHexWriting()) {
            return PdfEncodings.convertToString(ps.getBytes(), "UnicodeBigUnmarked");
        }
        return ps.toUnicodeString();
    }
    
    private void fillMetrics(final byte[] touni, final IntHashtable widths, final int dw) {
        try {
            final PdfContentParser ps = new PdfContentParser(new PRTokeniser(touni));
            PdfObject ob = null;
            PdfObject last = null;
            while ((ob = ps.readPRObject()) != null) {
                if (ob.type() == 200) {
                    if (ob.toString().equals("beginbfchar")) {
                        for (int n = ((PdfNumber)last).intValue(), k = 0; k < n; ++k) {
                            final String cid = this.decodeString((PdfString)ps.readPRObject());
                            final String uni = this.decodeString((PdfString)ps.readPRObject());
                            if (uni.length() == 1) {
                                final int cidc = cid.charAt(0);
                                final int unic = uni.charAt(uni.length() - 1);
                                int w = dw;
                                if (widths.containsKey(cidc)) {
                                    w = widths.get(cidc);
                                }
                                this.metrics.put(new Integer(unic), new int[] { cidc, w });
                            }
                        }
                    }
                    else {
                        if (!ob.toString().equals("beginbfrange")) {
                            continue;
                        }
                        for (int n = ((PdfNumber)last).intValue(), k = 0; k < n; ++k) {
                            final String cid2 = this.decodeString((PdfString)ps.readPRObject());
                            final String cid3 = this.decodeString((PdfString)ps.readPRObject());
                            int cid1c = cid2.charAt(0);
                            final int cid2c = cid3.charAt(0);
                            final PdfObject ob2 = ps.readPRObject();
                            if (ob2.isString()) {
                                final String uni2 = this.decodeString((PdfString)ob2);
                                if (uni2.length() == 1) {
                                    for (int unic2 = uni2.charAt(uni2.length() - 1); cid1c <= cid2c; ++cid1c, ++unic2) {
                                        int w2 = dw;
                                        if (widths.containsKey(cid1c)) {
                                            w2 = widths.get(cid1c);
                                        }
                                        this.metrics.put(new Integer(unic2), new int[] { cid1c, w2 });
                                    }
                                }
                            }
                            else {
                                final PdfArray a = (PdfArray)ob2;
                                for (int j = 0; j < a.size(); ++j, ++cid1c) {
                                    final String uni3 = this.decodeString(a.getAsString(j));
                                    if (uni3.length() == 1) {
                                        final int unic3 = uni3.charAt(uni3.length() - 1);
                                        int w3 = dw;
                                        if (widths.containsKey(cid1c)) {
                                            w3 = widths.get(cid1c);
                                        }
                                        this.metrics.put(new Integer(unic3), new int[] { cid1c, w3 });
                                    }
                                }
                            }
                        }
                    }
                }
                else {
                    last = ob;
                }
            }
        }
        catch (final Exception e) {
            throw new ExceptionConverter(e);
        }
    }
    
    private void doType1TT() {
        PdfObject enc = PdfReader.getPdfObject(this.font.get(PdfName.ENCODING));
        if (enc == null) {
            this.fillEncoding(null);
        }
        else if (enc.isName()) {
            this.fillEncoding((PdfName)enc);
        }
        else {
            final PdfDictionary encDic = (PdfDictionary)enc;
            enc = PdfReader.getPdfObject(encDic.get(PdfName.BASEENCODING));
            if (enc == null) {
                this.fillEncoding(null);
            }
            else {
                this.fillEncoding((PdfName)enc);
            }
            final PdfArray diffs = encDic.getAsArray(PdfName.DIFFERENCES);
            if (diffs != null) {
                this.diffmap = new IntHashtable();
                int currentNumber = 0;
                for (int k = 0; k < diffs.size(); ++k) {
                    final PdfObject obj = diffs.getPdfObject(k);
                    if (obj.isNumber()) {
                        currentNumber = ((PdfNumber)obj).intValue();
                    }
                    else {
                        final int[] c = GlyphList.nameToUnicode(PdfName.decodeName(obj.toString()));
                        if (c != null && c.length > 0) {
                            this.uni2byte.put(c[0], currentNumber);
                            this.diffmap.put(c[0], currentNumber);
                        }
                        ++currentNumber;
                    }
                }
            }
        }
        final PdfArray newWidths = this.font.getAsArray(PdfName.WIDTHS);
        final PdfNumber first = this.font.getAsNumber(PdfName.FIRSTCHAR);
        final PdfNumber last = this.font.getAsNumber(PdfName.LASTCHAR);
        if (DocumentFont.BuiltinFonts14.containsKey(this.fontName)) {
            BaseFont bf;
            try {
                bf = BaseFont.createFont(this.fontName, "Cp1252", false);
            }
            catch (final Exception e) {
                throw new ExceptionConverter(e);
            }
            int[] e2 = this.uni2byte.toOrderedKeys();
            for (int i = 0; i < e2.length; ++i) {
                final int n = this.uni2byte.get(e2[i]);
                this.widths[n] = bf.getRawWidth(n, GlyphList.unicodeToName(e2[i]));
            }
            if (this.diffmap != null) {
                e2 = this.diffmap.toOrderedKeys();
                for (int i = 0; i < e2.length; ++i) {
                    final int n = this.diffmap.get(e2[i]);
                    this.widths[n] = bf.getRawWidth(n, GlyphList.unicodeToName(e2[i]));
                }
                this.diffmap = null;
            }
            this.Ascender = bf.getFontDescriptor(1, 1000.0f);
            this.CapHeight = bf.getFontDescriptor(2, 1000.0f);
            this.Descender = bf.getFontDescriptor(3, 1000.0f);
            this.ItalicAngle = bf.getFontDescriptor(4, 1000.0f);
            this.llx = bf.getFontDescriptor(5, 1000.0f);
            this.lly = bf.getFontDescriptor(6, 1000.0f);
            this.urx = bf.getFontDescriptor(7, 1000.0f);
            this.ury = bf.getFontDescriptor(8, 1000.0f);
        }
        if (first != null && last != null && newWidths != null) {
            final int f = first.intValue();
            for (int j = 0; j < newWidths.size(); ++j) {
                this.widths[f + j] = newWidths.getAsNumber(j).intValue();
            }
        }
        this.fillFontDesc(this.font.getAsDict(PdfName.FONTDESCRIPTOR));
    }
    
    private void fillFontDesc(final PdfDictionary fontDesc) {
        if (fontDesc == null) {
            return;
        }
        PdfNumber v = fontDesc.getAsNumber(PdfName.ASCENT);
        if (v != null) {
            this.Ascender = v.floatValue();
        }
        v = fontDesc.getAsNumber(PdfName.CAPHEIGHT);
        if (v != null) {
            this.CapHeight = v.floatValue();
        }
        v = fontDesc.getAsNumber(PdfName.DESCENT);
        if (v != null) {
            this.Descender = v.floatValue();
        }
        v = fontDesc.getAsNumber(PdfName.ITALICANGLE);
        if (v != null) {
            this.ItalicAngle = v.floatValue();
        }
        final PdfArray bbox = fontDesc.getAsArray(PdfName.FONTBBOX);
        if (bbox != null) {
            this.llx = bbox.getAsNumber(0).floatValue();
            this.lly = bbox.getAsNumber(1).floatValue();
            this.urx = bbox.getAsNumber(2).floatValue();
            this.ury = bbox.getAsNumber(3).floatValue();
            if (this.llx > this.urx) {
                final float t = this.llx;
                this.llx = this.urx;
                this.urx = t;
            }
            if (this.lly > this.ury) {
                final float t = this.lly;
                this.lly = this.ury;
                this.ury = t;
            }
        }
    }
    
    private void fillEncoding(final PdfName encoding) {
        if (PdfName.MAC_ROMAN_ENCODING.equals(encoding) || PdfName.WIN_ANSI_ENCODING.equals(encoding)) {
            final byte[] b = new byte[256];
            for (int k = 0; k < 256; ++k) {
                b[k] = (byte)k;
            }
            String enc = "Cp1252";
            if (PdfName.MAC_ROMAN_ENCODING.equals(encoding)) {
                enc = "MacRoman";
            }
            final String cv = PdfEncodings.convertToString(b, enc);
            final char[] arr = cv.toCharArray();
            for (int i = 0; i < 256; ++i) {
                this.uni2byte.put(arr[i], i);
            }
        }
        else {
            for (int j = 0; j < 256; ++j) {
                this.uni2byte.put(DocumentFont.stdEnc[j], j);
            }
        }
    }
    
    @Override
    public String[][] getFamilyFontName() {
        return this.getFullFontName();
    }
    
    @Override
    public float getFontDescriptor(final int key, final float fontSize) {
        if (this.cjkMirror != null) {
            return this.cjkMirror.getFontDescriptor(key, fontSize);
        }
        switch (key) {
            case 1:
            case 9: {
                return this.Ascender * fontSize / 1000.0f;
            }
            case 2: {
                return this.CapHeight * fontSize / 1000.0f;
            }
            case 3:
            case 10: {
                return this.Descender * fontSize / 1000.0f;
            }
            case 4: {
                return this.ItalicAngle;
            }
            case 5: {
                return this.llx * fontSize / 1000.0f;
            }
            case 6: {
                return this.lly * fontSize / 1000.0f;
            }
            case 7: {
                return this.urx * fontSize / 1000.0f;
            }
            case 8: {
                return this.ury * fontSize / 1000.0f;
            }
            case 11: {
                return 0.0f;
            }
            case 12: {
                return (this.urx - this.llx) * fontSize / 1000.0f;
            }
            default: {
                return 0.0f;
            }
        }
    }
    
    @Override
    public String[][] getFullFontName() {
        return new String[][] { { "", "", "", this.fontName } };
    }
    
    @Override
    public String[][] getAllNameEntries() {
        return new String[][] { { "4", "", "", "", this.fontName } };
    }
    
    @Override
    public int getKerning(final int char1, final int char2) {
        return 0;
    }
    
    @Override
    public String getPostscriptFontName() {
        return this.fontName;
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
    void writeFont(final PdfWriter writer, final PdfIndirectReference ref, final Object[] params) throws DocumentException {
    }
    
    public PdfStream getFullFontStream() {
        return null;
    }
    
    @Override
    public int getWidth(final int char1) {
        if (this.cjkMirror != null) {
            return this.cjkMirror.getWidth(char1);
        }
        if (!this.isType0) {
            return super.getWidth(char1);
        }
        final int[] ws = this.metrics.get(new Integer(char1));
        if (ws != null) {
            return ws[1];
        }
        return 0;
    }
    
    @Override
    public int getWidth(final String text) {
        if (this.cjkMirror != null) {
            return this.cjkMirror.getWidth(text);
        }
        if (this.isType0) {
            final char[] chars = text.toCharArray();
            final int len = chars.length;
            int total = 0;
            for (int k = 0; k < len; ++k) {
                final int[] ws = this.metrics.get(new Integer(chars[k]));
                if (ws != null) {
                    total += ws[1];
                }
            }
            return total;
        }
        return super.getWidth(text);
    }
    
    @Override
    byte[] convertToBytes(final String text) {
        if (this.cjkMirror != null) {
            return PdfEncodings.convertToBytes(text, "UnicodeBigUnmarked");
        }
        if (this.isType0) {
            final char[] chars = text.toCharArray();
            final int len = chars.length;
            final byte[] b = new byte[len * 2];
            int bptr = 0;
            for (int k = 0; k < len; ++k) {
                final int[] ws = this.metrics.get(new Integer(chars[k]));
                if (ws != null) {
                    final int g = ws[0];
                    b[bptr++] = (byte)(g / 256);
                    b[bptr++] = (byte)g;
                }
            }
            if (bptr == b.length) {
                return b;
            }
            final byte[] nb = new byte[bptr];
            System.arraycopy(b, 0, nb, 0, bptr);
            return nb;
        }
        else {
            final char[] cc = text.toCharArray();
            final byte[] b2 = new byte[cc.length];
            int ptr = 0;
            for (int i = 0; i < cc.length; ++i) {
                if (this.uni2byte.containsKey(cc[i])) {
                    b2[ptr++] = (byte)this.uni2byte.get(cc[i]);
                }
            }
            if (ptr == b2.length) {
                return b2;
            }
            final byte[] b3 = new byte[ptr];
            System.arraycopy(b2, 0, b3, 0, ptr);
            return b3;
        }
    }
    
    @Override
    byte[] convertToBytes(final int char1) {
        if (this.cjkMirror != null) {
            return PdfEncodings.convertToBytes((char)char1, "UnicodeBigUnmarked");
        }
        if (this.isType0) {
            final int[] ws = this.metrics.get(new Integer(char1));
            if (ws != null) {
                final int g = ws[0];
                return new byte[] { (byte)(g / 256), (byte)g };
            }
            return new byte[0];
        }
        else {
            if (this.uni2byte.containsKey(char1)) {
                return new byte[] { (byte)this.uni2byte.get(char1) };
            }
            return new byte[0];
        }
    }
    
    PdfIndirectReference getIndirectReference() {
        return this.refFont;
    }
    
    @Override
    public boolean charExists(final int c) {
        if (this.cjkMirror != null) {
            return this.cjkMirror.charExists(c);
        }
        if (this.isType0) {
            return this.metrics.containsKey(new Integer(c));
        }
        return super.charExists(c);
    }
    
    @Override
    public void setPostscriptFontName(final String name) {
    }
    
    @Override
    public boolean setKerning(final int char1, final int char2, final int kern) {
        return false;
    }
    
    @Override
    public int[] getCharBBox(final int c) {
        return null;
    }
    
    @Override
    protected int[] getRawCharBBox(final int c, final String name) {
        return null;
    }
    
    IntHashtable getUni2Byte() {
        return this.uni2byte;
    }
    
    static {
        DocumentFont.cjkNames = new String[] { "HeiseiMin-W3", "HeiseiKakuGo-W5", "STSong-Light", "MHei-Medium", "MSung-Light", "HYGoThic-Medium", "HYSMyeongJo-Medium", "MSungStd-Light", "STSongStd-Light", "HYSMyeongJoStd-Medium", "KozMinPro-Regular" };
        DocumentFont.cjkEncs = new String[] { "UniJIS-UCS2-H", "UniJIS-UCS2-H", "UniGB-UCS2-H", "UniCNS-UCS2-H", "UniCNS-UCS2-H", "UniKS-UCS2-H", "UniKS-UCS2-H", "UniCNS-UCS2-H", "UniGB-UCS2-H", "UniKS-UCS2-H", "UniJIS-UCS2-H" };
        DocumentFont.cjkNames2 = new String[] { "MSungStd-Light", "STSongStd-Light", "HYSMyeongJoStd-Medium", "KozMinPro-Regular" };
        DocumentFont.cjkEncs2 = new String[] { "UniCNS-UCS2-H", "UniGB-UCS2-H", "UniKS-UCS2-H", "UniJIS-UCS2-H", "UniCNS-UTF16-H", "UniGB-UTF16-H", "UniKS-UTF16-H", "UniJIS-UTF16-H" };
        stdEnc = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 32, 33, 34, 35, 36, 37, 38, 8217, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 8216, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 123, 124, 125, 126, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 161, 162, 163, 8260, 165, 402, 167, 164, 39, 8220, 171, 8249, 8250, 64257, 64258, 0, 8211, 8224, 8225, 183, 0, 182, 8226, 8218, 8222, 8221, 187, 8230, 8240, 0, 191, 0, 96, 180, 710, 732, 175, 728, 729, 168, 0, 730, 184, 0, 733, 731, 711, 8212, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 198, 0, 170, 0, 0, 0, 0, 321, 216, 338, 186, 0, 0, 0, 0, 0, 230, 0, 0, 0, 305, 0, 0, 322, 248, 339, 223, 0, 0, 0, 0 };
    }
}
