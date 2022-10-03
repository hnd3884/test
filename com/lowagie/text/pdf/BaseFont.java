package com.lowagie.text.pdf;

import java.util.Iterator;
import java.io.InputStream;
import java.util.StringTokenizer;
import com.lowagie.text.error_messages.MessageLocalization;
import java.io.IOException;
import com.lowagie.text.DocumentException;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.ArrayList;

public abstract class BaseFont
{
    public static final String COURIER = "Courier";
    public static final String COURIER_BOLD = "Courier-Bold";
    public static final String COURIER_OBLIQUE = "Courier-Oblique";
    public static final String COURIER_BOLDOBLIQUE = "Courier-BoldOblique";
    public static final String HELVETICA = "Helvetica";
    public static final String HELVETICA_BOLD = "Helvetica-Bold";
    public static final String HELVETICA_OBLIQUE = "Helvetica-Oblique";
    public static final String HELVETICA_BOLDOBLIQUE = "Helvetica-BoldOblique";
    public static final String SYMBOL = "Symbol";
    public static final String TIMES_ROMAN = "Times-Roman";
    public static final String TIMES_BOLD = "Times-Bold";
    public static final String TIMES_ITALIC = "Times-Italic";
    public static final String TIMES_BOLDITALIC = "Times-BoldItalic";
    public static final String ZAPFDINGBATS = "ZapfDingbats";
    public static final int ASCENT = 1;
    public static final int CAPHEIGHT = 2;
    public static final int DESCENT = 3;
    public static final int ITALICANGLE = 4;
    public static final int BBOXLLX = 5;
    public static final int BBOXLLY = 6;
    public static final int BBOXURX = 7;
    public static final int BBOXURY = 8;
    public static final int AWT_ASCENT = 9;
    public static final int AWT_DESCENT = 10;
    public static final int AWT_LEADING = 11;
    public static final int AWT_MAXADVANCE = 12;
    public static final int UNDERLINE_POSITION = 13;
    public static final int UNDERLINE_THICKNESS = 14;
    public static final int STRIKETHROUGH_POSITION = 15;
    public static final int STRIKETHROUGH_THICKNESS = 16;
    public static final int SUBSCRIPT_SIZE = 17;
    public static final int SUBSCRIPT_OFFSET = 18;
    public static final int SUPERSCRIPT_SIZE = 19;
    public static final int SUPERSCRIPT_OFFSET = 20;
    public static final int FONT_TYPE_T1 = 0;
    public static final int FONT_TYPE_TT = 1;
    public static final int FONT_TYPE_CJK = 2;
    public static final int FONT_TYPE_TTUNI = 3;
    public static final int FONT_TYPE_DOCUMENT = 4;
    public static final int FONT_TYPE_T3 = 5;
    public static final String IDENTITY_H = "Identity-H";
    public static final String IDENTITY_V = "Identity-V";
    public static final String CP1250 = "Cp1250";
    public static final String CP1252 = "Cp1252";
    public static final String CP1257 = "Cp1257";
    public static final String WINANSI = "Cp1252";
    public static final String MACROMAN = "MacRoman";
    public static final int[] CHAR_RANGE_LATIN;
    public static final int[] CHAR_RANGE_ARABIC;
    public static final int[] CHAR_RANGE_HEBREW;
    public static final int[] CHAR_RANGE_CYRILLIC;
    public static final boolean EMBEDDED = true;
    public static final boolean NOT_EMBEDDED = false;
    public static final boolean CACHED = true;
    public static final boolean NOT_CACHED = false;
    public static final String RESOURCE_PATH = "com/lowagie/text/pdf/fonts/";
    public static final char CID_NEWLINE = '\u7fff';
    protected ArrayList<int[]> subsetRanges;
    int fontType;
    public static final String notdef = ".notdef";
    protected int[] widths;
    protected String[] differences;
    protected char[] unicodeDifferences;
    protected int[][] charBBoxes;
    protected String encoding;
    protected boolean embedded;
    protected int compressionLevel;
    protected boolean fontSpecific;
    protected static ConcurrentHashMap<String, BaseFont> fontCache;
    protected static final HashMap<String, PdfName> BuiltinFonts14;
    protected boolean forceWidthsOutput;
    protected boolean directTextToByte;
    protected boolean subset;
    protected boolean fastWinansi;
    protected IntHashtable specialMap;
    
    protected BaseFont() {
        this.widths = new int[256];
        this.differences = new String[256];
        this.unicodeDifferences = new char[256];
        this.charBBoxes = new int[256][];
        this.compressionLevel = -1;
        this.fontSpecific = true;
        this.forceWidthsOutput = false;
        this.directTextToByte = false;
        this.subset = true;
        this.fastWinansi = false;
    }
    
    public static BaseFont createFont() throws DocumentException, IOException {
        return createFont("Helvetica", "Cp1252", false);
    }
    
    public static BaseFont createFont(final String name, final String encoding, final boolean embedded) throws DocumentException, IOException {
        return createFont(name, encoding, embedded, true, null, null, false);
    }
    
    public static BaseFont createFont(final String name, final String encoding, final boolean embedded, final boolean forceRead) throws DocumentException, IOException {
        return createFont(name, encoding, embedded, true, null, null, forceRead);
    }
    
    public static BaseFont createFont(final String name, final String encoding, final boolean embedded, final boolean cached, final byte[] ttfAfm, final byte[] pfb) throws DocumentException, IOException {
        return createFont(name, encoding, embedded, cached, ttfAfm, pfb, false);
    }
    
    public static BaseFont createFont(final String name, final String encoding, final boolean embedded, final boolean cached, final byte[] ttfAfm, final byte[] pfb, final boolean noThrow) throws DocumentException, IOException {
        return createFont(name, encoding, embedded, cached, ttfAfm, pfb, false, false);
    }
    
    public static BaseFont createFont(final String name, String encoding, boolean embedded, final boolean cached, final byte[] ttfAfm, final byte[] pfb, final boolean noThrow, final boolean forceRead) throws DocumentException, IOException {
        final String nameBase = getBaseName(name);
        encoding = normalizeEncoding(encoding);
        final boolean isBuiltinFonts14 = BaseFont.BuiltinFonts14.containsKey(name);
        final boolean isCJKFont = !isBuiltinFonts14 && CJKFont.isCJKFont(nameBase, encoding);
        if (isBuiltinFonts14 || isCJKFont) {
            embedded = false;
        }
        else if (encoding.equals("Identity-H") || encoding.equals("Identity-V")) {
            embedded = true;
        }
        BaseFont fontFound = null;
        BaseFont fontBuilt = null;
        final String key = name + "\n" + encoding + "\n" + embedded;
        if (cached) {
            fontFound = BaseFont.fontCache.get(key);
            if (fontFound != null) {
                return fontFound;
            }
        }
        if (isBuiltinFonts14 || name.toLowerCase().endsWith(".afm") || name.toLowerCase().endsWith(".pfm")) {
            fontBuilt = new Type1Font(name, encoding, embedded, ttfAfm, pfb, forceRead);
            fontBuilt.fastWinansi = encoding.equals("Cp1252");
        }
        else if (nameBase.toLowerCase().endsWith(".ttf") || nameBase.toLowerCase().endsWith(".otf") || nameBase.toLowerCase().indexOf(".ttc,") > 0) {
            if (encoding.equals("Identity-H") || encoding.equals("Identity-V")) {
                fontBuilt = new TrueTypeFontUnicode(name, encoding, embedded, ttfAfm, forceRead);
            }
            else {
                fontBuilt = new TrueTypeFont(name, encoding, embedded, ttfAfm, false, forceRead);
                fontBuilt.fastWinansi = encoding.equals("Cp1252");
            }
        }
        else if (isCJKFont) {
            fontBuilt = new CJKFont(name, encoding, embedded);
        }
        else {
            if (noThrow) {
                return null;
            }
            throw new DocumentException(MessageLocalization.getComposedMessage("font.1.with.2.is.not.recognized", name, encoding));
        }
        if (cached) {
            BaseFont.fontCache.putIfAbsent(key, fontBuilt);
            return BaseFont.fontCache.get(key);
        }
        return fontBuilt;
    }
    
    public static BaseFont createFont(final PRIndirectReference fontRef) {
        return new DocumentFont(fontRef);
    }
    
    protected static String getBaseName(final String name) {
        if (name.endsWith(",Bold")) {
            return name.substring(0, name.length() - 5);
        }
        if (name.endsWith(",Italic")) {
            return name.substring(0, name.length() - 7);
        }
        if (name.endsWith(",BoldItalic")) {
            return name.substring(0, name.length() - 11);
        }
        return name;
    }
    
    protected static String normalizeEncoding(final String enc) {
        if (enc.equals("winansi") || enc.equals("")) {
            return "Cp1252";
        }
        if (enc.equals("macroman")) {
            return "MacRoman";
        }
        return enc;
    }
    
    protected void createEncoding() {
        if (this.encoding.startsWith("#")) {
            this.specialMap = new IntHashtable();
            final StringTokenizer tok = new StringTokenizer(this.encoding.substring(1), " ,\t\n\r\f");
            if (tok.nextToken().equals("full")) {
                while (tok.hasMoreTokens()) {
                    final String order = tok.nextToken();
                    final String name = tok.nextToken();
                    final char uni = (char)Integer.parseInt(tok.nextToken(), 16);
                    int orderK;
                    if (order.startsWith("'")) {
                        orderK = order.charAt(1);
                    }
                    else {
                        orderK = Integer.parseInt(order);
                    }
                    orderK %= 256;
                    this.specialMap.put(uni, orderK);
                    this.differences[orderK] = name;
                    this.unicodeDifferences[orderK] = uni;
                    this.widths[orderK] = this.getRawWidth(uni, name);
                    this.charBBoxes[orderK] = this.getRawCharBBox(uni, name);
                }
            }
            else {
                int k = 0;
                if (tok.hasMoreTokens()) {
                    k = Integer.parseInt(tok.nextToken());
                }
                while (tok.hasMoreTokens() && k < 256) {
                    final String hex = tok.nextToken();
                    final int uni2 = Integer.parseInt(hex, 16) % 65536;
                    final String name2 = GlyphList.unicodeToName(uni2);
                    if (name2 != null) {
                        this.specialMap.put(uni2, k);
                        this.differences[k] = name2;
                        this.unicodeDifferences[k] = (char)uni2;
                        this.widths[k] = this.getRawWidth(uni2, name2);
                        this.charBBoxes[k] = this.getRawCharBBox(uni2, name2);
                        ++k;
                    }
                }
            }
            for (int k = 0; k < 256; ++k) {
                if (this.differences[k] == null) {
                    this.differences[k] = ".notdef";
                }
            }
        }
        else if (this.fontSpecific) {
            for (int i = 0; i < 256; ++i) {
                this.widths[i] = this.getRawWidth(i, null);
                this.charBBoxes[i] = this.getRawCharBBox(i, null);
            }
        }
        else {
            final byte[] b = { 0 };
            for (int j = 0; j < 256; ++j) {
                b[0] = (byte)j;
                final String s = PdfEncodings.convertToString(b, this.encoding);
                char c;
                if (s.length() > 0) {
                    c = s.charAt(0);
                }
                else {
                    c = '?';
                }
                String name3 = GlyphList.unicodeToName(c);
                if (name3 == null) {
                    name3 = ".notdef";
                }
                this.differences[j] = name3;
                this.unicodeDifferences[j] = c;
                this.widths[j] = this.getRawWidth(c, name3);
                this.charBBoxes[j] = this.getRawCharBBox(c, name3);
            }
        }
    }
    
    abstract int getRawWidth(final int p0, final String p1);
    
    public abstract int getKerning(final int p0, final int p1);
    
    public abstract boolean setKerning(final int p0, final int p1, final int p2);
    
    public int getWidth(final int char1) {
        if (!this.fastWinansi) {
            int total = 0;
            final byte[] convertToBytes;
            final byte[] mbytes = convertToBytes = this.convertToBytes((char)char1);
            for (final byte mbyte : convertToBytes) {
                total += this.widths[0xFF & mbyte];
            }
            return total;
        }
        if (char1 < 128 || (char1 >= 160 && char1 <= 255)) {
            return this.widths[char1];
        }
        return this.widths[PdfEncodings.winansi.get(char1)];
    }
    
    public int getWidth(final String text) {
        int total = 0;
        if (this.fastWinansi) {
            for (int len = text.length(), k = 0; k < len; ++k) {
                final char char1 = text.charAt(k);
                if (char1 < '\u0080' || (char1 >= ' ' && char1 <= '\u00ff')) {
                    total += this.widths[char1];
                }
                else {
                    total += this.widths[PdfEncodings.winansi.get(char1)];
                }
            }
            return total;
        }
        final byte[] convertToBytes;
        final byte[] mbytes = convertToBytes = this.convertToBytes(text);
        for (final byte mbyte : convertToBytes) {
            total += this.widths[0xFF & mbyte];
        }
        return total;
    }
    
    public int getDescent(final String text) {
        int min = 0;
        final char[] charArray;
        final char[] chars = charArray = text.toCharArray();
        for (final char c : charArray) {
            final int[] bbox = this.getCharBBox(c);
            if (bbox != null && bbox[1] < min) {
                min = bbox[1];
            }
        }
        return min;
    }
    
    public int getAscent(final String text) {
        int max = 0;
        final char[] charArray;
        final char[] chars = charArray = text.toCharArray();
        for (final char c : charArray) {
            final int[] bbox = this.getCharBBox(c);
            if (bbox != null && bbox[3] > max) {
                max = bbox[3];
            }
        }
        return max;
    }
    
    public float getDescentPoint(final String text, final float fontSize) {
        return this.getDescent(text) * 0.001f * fontSize;
    }
    
    public float getAscentPoint(final String text, final float fontSize) {
        return this.getAscent(text) * 0.001f * fontSize;
    }
    
    public float getWidthPointKerned(final String text, final float fontSize) {
        final float size = this.getWidth(text) * 0.001f * fontSize;
        if (!this.hasKernPairs()) {
            return size;
        }
        final int len = text.length() - 1;
        int kern = 0;
        final char[] c = text.toCharArray();
        for (int k = 0; k < len; ++k) {
            kern += this.getKerning(c[k], c[k + 1]);
        }
        return size + kern * 0.001f * fontSize;
    }
    
    public float getWidthPoint(final String text, final float fontSize) {
        return this.getWidth(text) * 0.001f * fontSize;
    }
    
    public float getWidthPoint(final int char1, final float fontSize) {
        return this.getWidth(char1) * 0.001f * fontSize;
    }
    
    byte[] convertToBytes(final String text) {
        if (this.directTextToByte) {
            return PdfEncodings.convertToBytes(text, null);
        }
        if (this.specialMap == null) {
            return PdfEncodings.convertToBytes(text, this.encoding);
        }
        final byte[] b = new byte[text.length()];
        int ptr = 0;
        final int length = text.length();
        for (int k = 0; k < length; ++k) {
            final char c = text.charAt(k);
            if (this.specialMap.containsKey(c)) {
                b[ptr++] = (byte)this.specialMap.get(c);
            }
        }
        if (ptr < length) {
            final byte[] b2 = new byte[ptr];
            System.arraycopy(b, 0, b2, 0, ptr);
            return b2;
        }
        return b;
    }
    
    byte[] convertToBytes(final int char1) {
        if (this.directTextToByte) {
            return PdfEncodings.convertToBytes((char)char1, null);
        }
        if (this.specialMap == null) {
            return PdfEncodings.convertToBytes((char)char1, this.encoding);
        }
        if (this.specialMap.containsKey(char1)) {
            return new byte[] { (byte)this.specialMap.get(char1) };
        }
        return new byte[0];
    }
    
    abstract void writeFont(final PdfWriter p0, final PdfIndirectReference p1, final Object[] p2) throws DocumentException, IOException;
    
    abstract PdfStream getFullFontStream() throws IOException, DocumentException;
    
    public String getEncoding() {
        return this.encoding;
    }
    
    public abstract float getFontDescriptor(final int p0, final float p1);
    
    public int getFontType() {
        return this.fontType;
    }
    
    public boolean isEmbedded() {
        return this.embedded;
    }
    
    public boolean isFontSpecific() {
        return this.fontSpecific;
    }
    
    public static String createSubsetPrefix() {
        String s = "";
        for (int k = 0; k < 6; ++k) {
            s += (char)(Math.random() * 26.0 + 65.0);
        }
        return s + "+";
    }
    
    char getUnicodeDifferences(final int index) {
        return this.unicodeDifferences[index];
    }
    
    public abstract String getPostscriptFontName();
    
    public abstract void setPostscriptFontName(final String p0);
    
    public abstract String[][] getFullFontName();
    
    public abstract String[][] getAllNameEntries();
    
    public static String[][] getFullFontName(final String name, final String encoding, final byte[] ttfAfm) throws DocumentException, IOException {
        final String nameBase = getBaseName(name);
        BaseFont fontBuilt = null;
        if (nameBase.toLowerCase().endsWith(".ttf") || nameBase.toLowerCase().endsWith(".otf") || nameBase.toLowerCase().indexOf(".ttc,") > 0) {
            fontBuilt = new TrueTypeFont(name, "Cp1252", false, ttfAfm, true, false);
        }
        else {
            fontBuilt = createFont(name, encoding, false, false, ttfAfm, null);
        }
        return fontBuilt.getFullFontName();
    }
    
    public static Object[] getAllFontNames(final String name, final String encoding, final byte[] ttfAfm) throws DocumentException, IOException {
        final String nameBase = getBaseName(name);
        BaseFont fontBuilt = null;
        if (nameBase.toLowerCase().endsWith(".ttf") || nameBase.toLowerCase().endsWith(".otf") || nameBase.toLowerCase().indexOf(".ttc,") > 0) {
            fontBuilt = new TrueTypeFont(name, "Cp1252", false, ttfAfm, true, false);
        }
        else {
            fontBuilt = createFont(name, encoding, false, false, ttfAfm, null);
        }
        return new Object[] { fontBuilt.getPostscriptFontName(), fontBuilt.getFamilyFontName(), fontBuilt.getFullFontName() };
    }
    
    public static String[][] getAllNameEntries(final String name, final String encoding, final byte[] ttfAfm) throws DocumentException, IOException {
        final String nameBase = getBaseName(name);
        BaseFont fontBuilt = null;
        if (nameBase.toLowerCase().endsWith(".ttf") || nameBase.toLowerCase().endsWith(".otf") || nameBase.toLowerCase().indexOf(".ttc,") > 0) {
            fontBuilt = new TrueTypeFont(name, "Cp1252", false, ttfAfm, true, false);
        }
        else {
            fontBuilt = createFont(name, encoding, false, false, ttfAfm, null);
        }
        return fontBuilt.getAllNameEntries();
    }
    
    public abstract String[][] getFamilyFontName();
    
    public String[] getCodePagesSupported() {
        return new String[0];
    }
    
    public static String[] enumerateTTCNames(final String ttcFile) throws DocumentException, IOException {
        return new EnumerateTTC(ttcFile).getNames();
    }
    
    public static String[] enumerateTTCNames(final byte[] ttcArray) throws DocumentException, IOException {
        return new EnumerateTTC(ttcArray).getNames();
    }
    
    public int[] getWidths() {
        return this.widths;
    }
    
    public String[] getDifferences() {
        return this.differences;
    }
    
    public char[] getUnicodeDifferences() {
        return this.unicodeDifferences;
    }
    
    public boolean isForceWidthsOutput() {
        return this.forceWidthsOutput;
    }
    
    public void setForceWidthsOutput(final boolean forceWidthsOutput) {
        this.forceWidthsOutput = forceWidthsOutput;
    }
    
    public boolean isDirectTextToByte() {
        return this.directTextToByte;
    }
    
    public void setDirectTextToByte(final boolean directTextToByte) {
        this.directTextToByte = directTextToByte;
    }
    
    public boolean isSubset() {
        return this.subset;
    }
    
    public void setSubset(final boolean subset) {
        this.subset = subset;
    }
    
    public static InputStream getResourceStream(final String key) {
        return getResourceStream(key, null);
    }
    
    public static InputStream getResourceStream(String key, final ClassLoader loader) {
        if (key.startsWith("/")) {
            key = key.substring(1);
        }
        InputStream is = null;
        if (loader != null) {
            is = loader.getResourceAsStream(key);
            if (is != null) {
                return is;
            }
        }
        try {
            final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
            if (contextClassLoader != null) {
                is = contextClassLoader.getResourceAsStream(key);
            }
        }
        catch (final Throwable t) {}
        if (is == null) {
            is = BaseFont.class.getResourceAsStream("/" + key);
        }
        if (is == null) {
            is = ClassLoader.getSystemResourceAsStream(key);
        }
        return is;
    }
    
    public int getUnicodeEquivalent(final int c) {
        return c;
    }
    
    public int getCidCode(final int c) {
        return c;
    }
    
    public abstract boolean hasKernPairs();
    
    public boolean charExists(final int c) {
        final byte[] b = this.convertToBytes(c);
        return b.length > 0;
    }
    
    public boolean setCharAdvance(final int c, final int advance) {
        final byte[] b = this.convertToBytes(c);
        if (b.length == 0) {
            return false;
        }
        this.widths[0xFF & b[0]] = advance;
        return true;
    }
    
    private static void addFont(final PRIndirectReference fontRef, final IntHashtable hits, final ArrayList<Object[]> fonts) {
        final PdfObject obj = PdfReader.getPdfObject(fontRef);
        if (obj == null || !obj.isDictionary()) {
            return;
        }
        final PdfDictionary font = (PdfDictionary)obj;
        final PdfName subtype = font.getAsName(PdfName.SUBTYPE);
        if (!PdfName.TYPE1.equals(subtype) && !PdfName.TRUETYPE.equals(subtype)) {
            return;
        }
        final PdfName name = font.getAsName(PdfName.BASEFONT);
        fonts.add(new Object[] { PdfName.decodeName(name.toString()), fontRef });
        hits.put(fontRef.getNumber(), 1);
    }
    
    private static void recourseFonts(final PdfDictionary page, final IntHashtable hits, final ArrayList<Object[]> fonts, int level) {
        if (++level > 50) {
            return;
        }
        final PdfDictionary resources = page.getAsDict(PdfName.RESOURCES);
        if (resources == null) {
            return;
        }
        final PdfDictionary font = resources.getAsDict(PdfName.FONT);
        if (font != null) {
            final Iterator it = font.getKeys().iterator();
            while (it.hasNext()) {
                final PdfObject ft = font.get(it.next());
                if (ft != null) {
                    if (!ft.isIndirect()) {
                        continue;
                    }
                    final int hit = ((PRIndirectReference)ft).getNumber();
                    if (hits.containsKey(hit)) {
                        continue;
                    }
                    addFont((PRIndirectReference)ft, hits, fonts);
                }
            }
        }
        final PdfDictionary xobj = resources.getAsDict(PdfName.XOBJECT);
        if (xobj != null) {
            final Iterator it2 = xobj.getKeys().iterator();
            while (it2.hasNext()) {
                recourseFonts(xobj.getAsDict(it2.next()), hits, fonts, level);
            }
        }
    }
    
    public static ArrayList<Object[]> getDocumentFonts(final PdfReader reader) {
        final IntHashtable hits = new IntHashtable();
        final ArrayList<Object[]> fonts = new ArrayList<Object[]>();
        for (int npages = reader.getNumberOfPages(), k = 1; k <= npages; ++k) {
            recourseFonts(reader.getPageN(k), hits, fonts, 1);
        }
        return fonts;
    }
    
    public static ArrayList<Object[]> getDocumentFonts(final PdfReader reader, final int page) {
        final IntHashtable hits = new IntHashtable();
        final ArrayList<Object[]> fonts = new ArrayList<Object[]>();
        recourseFonts(reader.getPageN(page), hits, fonts, 1);
        return fonts;
    }
    
    public int[] getCharBBox(final int c) {
        final byte[] b = this.convertToBytes(c);
        if (b.length == 0) {
            return null;
        }
        return this.charBBoxes[b[0] & 0xFF];
    }
    
    protected abstract int[] getRawCharBBox(final int p0, final String p1);
    
    public void correctArabicAdvance() {
        for (char c = '\u064b'; c <= '\u0658'; ++c) {
            this.setCharAdvance(c, 0);
        }
        this.setCharAdvance(1648, 0);
        for (char c = '\u06d6'; c <= '\u06dc'; ++c) {
            this.setCharAdvance(c, 0);
        }
        for (char c = '\u06df'; c <= '\u06e4'; ++c) {
            this.setCharAdvance(c, 0);
        }
        for (char c = '\u06e7'; c <= '\u06e8'; ++c) {
            this.setCharAdvance(c, 0);
        }
        for (char c = '\u06ea'; c <= '\u06ed'; ++c) {
            this.setCharAdvance(c, 0);
        }
    }
    
    public void addSubsetRange(final int[] range) {
        if (this.subsetRanges == null) {
            this.subsetRanges = new ArrayList<int[]>();
        }
        this.subsetRanges.add(range);
    }
    
    public int getCompressionLevel() {
        return this.compressionLevel;
    }
    
    public void setCompressionLevel(final int compressionLevel) {
        if (compressionLevel < 0 || compressionLevel > 9) {
            this.compressionLevel = -1;
        }
        else {
            this.compressionLevel = compressionLevel;
        }
    }
    
    static {
        CHAR_RANGE_LATIN = new int[] { 0, 383, 8192, 8303, 8352, 8399, 64256, 64262 };
        CHAR_RANGE_ARABIC = new int[] { 0, 127, 1536, 1663, 8352, 8399, 64336, 64511, 65136, 65279 };
        CHAR_RANGE_HEBREW = new int[] { 0, 127, 1424, 1535, 8352, 8399, 64285, 64335 };
        CHAR_RANGE_CYRILLIC = new int[] { 0, 127, 1024, 1327, 8192, 8303, 8352, 8399 };
        BaseFont.fontCache = new ConcurrentHashMap<String, BaseFont>(500, 0.85f, 64);
        (BuiltinFonts14 = new HashMap<String, PdfName>()).put("Courier", PdfName.COURIER);
        BaseFont.BuiltinFonts14.put("Courier-Bold", PdfName.COURIER_BOLD);
        BaseFont.BuiltinFonts14.put("Courier-BoldOblique", PdfName.COURIER_BOLDOBLIQUE);
        BaseFont.BuiltinFonts14.put("Courier-Oblique", PdfName.COURIER_OBLIQUE);
        BaseFont.BuiltinFonts14.put("Helvetica", PdfName.HELVETICA);
        BaseFont.BuiltinFonts14.put("Helvetica-Bold", PdfName.HELVETICA_BOLD);
        BaseFont.BuiltinFonts14.put("Helvetica-BoldOblique", PdfName.HELVETICA_BOLDOBLIQUE);
        BaseFont.BuiltinFonts14.put("Helvetica-Oblique", PdfName.HELVETICA_OBLIQUE);
        BaseFont.BuiltinFonts14.put("Symbol", PdfName.SYMBOL);
        BaseFont.BuiltinFonts14.put("Times-Roman", PdfName.TIMES_ROMAN);
        BaseFont.BuiltinFonts14.put("Times-Bold", PdfName.TIMES_BOLD);
        BaseFont.BuiltinFonts14.put("Times-BoldItalic", PdfName.TIMES_BOLDITALIC);
        BaseFont.BuiltinFonts14.put("Times-Italic", PdfName.TIMES_ITALIC);
        BaseFont.BuiltinFonts14.put("ZapfDingbats", PdfName.ZAPFDINGBATS);
    }
    
    static class StreamFont extends PdfStream
    {
        public StreamFont(final byte[] contents, final int[] lengths, final int compressionLevel) throws DocumentException {
            try {
                this.bytes = contents;
                this.put(PdfName.LENGTH, new PdfNumber(this.bytes.length));
                for (int k = 0; k < lengths.length; ++k) {
                    this.put(new PdfName("Length" + (k + 1)), new PdfNumber(lengths[k]));
                }
                this.flateCompress(compressionLevel);
            }
            catch (final Exception e) {
                throw new DocumentException(e);
            }
        }
        
        public StreamFont(final byte[] contents, final String subType, final int compressionLevel) throws DocumentException {
            try {
                this.bytes = contents;
                this.put(PdfName.LENGTH, new PdfNumber(this.bytes.length));
                if (subType != null) {
                    this.put(PdfName.SUBTYPE, new PdfName(subType));
                }
                this.flateCompress(compressionLevel);
            }
            catch (final Exception e) {
                throw new DocumentException(e);
            }
        }
    }
}
