package com.lowagie.text.pdf;

import java.util.Enumeration;
import java.io.IOException;
import java.util.StringTokenizer;
import com.lowagie.text.DocumentException;
import com.lowagie.text.error_messages.MessageLocalization;
import java.io.InputStream;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Hashtable;
import java.util.Properties;

class CJKFont extends BaseFont
{
    static final String CJK_ENCODING = "UnicodeBigUnmarked";
    private static final int FIRST = 0;
    private static final int BRACKET = 1;
    private static final int SERIAL = 2;
    private static final int V1Y = 880;
    static Properties cjkFonts;
    static Properties cjkEncodings;
    Hashtable<String, char[]> allCMaps;
    static ConcurrentHashMap<String, HashMap<Object, Object>> allFonts;
    private static boolean propertiesLoaded;
    private static Object initLock;
    private String fontName;
    private String style;
    private String CMap;
    private boolean cidDirect;
    private char[] translationMap;
    private IntHashtable vMetrics;
    private IntHashtable hMetrics;
    private HashMap<Object, Object> fontDesc;
    private boolean vertical;
    
    private static void loadProperties() {
        if (CJKFont.propertiesLoaded) {
            return;
        }
        synchronized (CJKFont.initLock) {
            if (CJKFont.propertiesLoaded) {
                return;
            }
            try {
                InputStream is = BaseFont.getResourceStream("com/lowagie/text/pdf/fonts/cjkfonts.properties");
                CJKFont.cjkFonts.load(is);
                is.close();
                is = BaseFont.getResourceStream("com/lowagie/text/pdf/fonts/cjkencodings.properties");
                CJKFont.cjkEncodings.load(is);
                is.close();
            }
            catch (final Exception e) {
                CJKFont.cjkFonts = new Properties();
                CJKFont.cjkEncodings = new Properties();
            }
            CJKFont.propertiesLoaded = true;
        }
    }
    
    CJKFont(String fontName, final String enc, final boolean emb) throws DocumentException {
        this.allCMaps = new Hashtable<String, char[]>();
        this.style = "";
        this.cidDirect = false;
        this.vertical = false;
        loadProperties();
        this.fontType = 2;
        final String nameBase = BaseFont.getBaseName(fontName);
        if (!isCJKFont(nameBase, enc)) {
            throw new DocumentException(MessageLocalization.getComposedMessage("font.1.with.2.encoding.is.not.a.cjk.font", fontName, enc));
        }
        if (nameBase.length() < fontName.length()) {
            this.style = fontName.substring(nameBase.length());
            fontName = nameBase;
        }
        this.fontName = fontName;
        this.encoding = "UnicodeBigUnmarked";
        this.vertical = enc.endsWith("V");
        this.CMap = enc;
        if (enc.startsWith("Identity-")) {
            this.cidDirect = true;
            String s = CJKFont.cjkFonts.getProperty(fontName);
            s = s.substring(0, s.indexOf(95));
            char[] c = this.allCMaps.get(s);
            if (c == null) {
                c = readCMap(s);
                if (c == null) {
                    throw new DocumentException(MessageLocalization.getComposedMessage("the.cmap.1.does.not.exist.as.a.resource", s));
                }
                c[32767] = '\n';
                this.allCMaps.put(s, c);
            }
            this.translationMap = c;
        }
        else {
            char[] c2 = this.allCMaps.get(enc);
            if (c2 == null) {
                final String s2 = CJKFont.cjkEncodings.getProperty(enc);
                if (s2 == null) {
                    throw new DocumentException(MessageLocalization.getComposedMessage("the.resource.cjkencodings.properties.does.not.contain.the.encoding.1", enc));
                }
                final StringTokenizer tk = new StringTokenizer(s2);
                final String nt = tk.nextToken();
                c2 = this.allCMaps.get(nt);
                if (c2 == null) {
                    c2 = readCMap(nt);
                    this.allCMaps.put(nt, c2);
                }
                if (tk.hasMoreTokens()) {
                    final String nt2 = tk.nextToken();
                    final char[] m2 = readCMap(nt2);
                    for (int k = 0; k < 65536; ++k) {
                        if (m2[k] == '\0') {
                            m2[k] = c2[k];
                        }
                    }
                    this.allCMaps.put(enc, m2);
                    c2 = m2;
                }
            }
            this.translationMap = c2;
        }
        this.fontDesc = CJKFont.allFonts.get(fontName);
        if (this.fontDesc == null) {
            this.fontDesc = readFontProperties(fontName);
            CJKFont.allFonts.putIfAbsent(fontName, this.fontDesc);
            this.fontDesc = CJKFont.allFonts.get(fontName);
        }
        this.hMetrics = this.fontDesc.get("W");
        this.vMetrics = this.fontDesc.get("W2");
    }
    
    public static boolean isCJKFont(final String fontName, final String enc) {
        loadProperties();
        final String encodings = CJKFont.cjkFonts.getProperty(fontName);
        return encodings != null && (enc.equals("Identity-H") || enc.equals("Identity-V") || encodings.indexOf("_" + enc + "_") >= 0);
    }
    
    @Override
    public int getWidth(final int char1) {
        int c = char1;
        if (!this.cidDirect) {
            c = this.translationMap[c];
        }
        int v;
        if (this.vertical) {
            v = this.vMetrics.get(c);
        }
        else {
            v = this.hMetrics.get(c);
        }
        if (v > 0) {
            return v;
        }
        return 1000;
    }
    
    @Override
    public int getWidth(final String text) {
        int total = 0;
        for (int k = 0; k < text.length(); ++k) {
            int c = text.charAt(k);
            if (!this.cidDirect) {
                c = this.translationMap[c];
            }
            int v;
            if (this.vertical) {
                v = this.vMetrics.get(c);
            }
            else {
                v = this.hMetrics.get(c);
            }
            if (v > 0) {
                total += v;
            }
            else {
                total += 1000;
            }
        }
        return total;
    }
    
    @Override
    int getRawWidth(final int c, final String name) {
        return 0;
    }
    
    @Override
    public int getKerning(final int char1, final int char2) {
        return 0;
    }
    
    private PdfDictionary getFontDescriptor() {
        final PdfDictionary dic = new PdfDictionary(PdfName.FONTDESCRIPTOR);
        dic.put(PdfName.ASCENT, new PdfLiteral(this.fontDesc.get("Ascent")));
        dic.put(PdfName.CAPHEIGHT, new PdfLiteral(this.fontDesc.get("CapHeight")));
        dic.put(PdfName.DESCENT, new PdfLiteral(this.fontDesc.get("Descent")));
        dic.put(PdfName.FLAGS, new PdfLiteral(this.fontDesc.get("Flags")));
        dic.put(PdfName.FONTBBOX, new PdfLiteral(this.fontDesc.get("FontBBox")));
        dic.put(PdfName.FONTNAME, new PdfName(this.fontName + this.style));
        dic.put(PdfName.ITALICANGLE, new PdfLiteral(this.fontDesc.get("ItalicAngle")));
        dic.put(PdfName.STEMV, new PdfLiteral(this.fontDesc.get("StemV")));
        final PdfDictionary pdic = new PdfDictionary();
        pdic.put(PdfName.PANOSE, new PdfString(this.fontDesc.get("Panose"), null));
        dic.put(PdfName.STYLE, pdic);
        return dic;
    }
    
    private PdfDictionary getCIDFont(final PdfIndirectReference fontDescriptor, final IntHashtable cjkTag) {
        final PdfDictionary dic = new PdfDictionary(PdfName.FONT);
        dic.put(PdfName.SUBTYPE, PdfName.CIDFONTTYPE0);
        dic.put(PdfName.BASEFONT, new PdfName(this.fontName + this.style));
        dic.put(PdfName.FONTDESCRIPTOR, fontDescriptor);
        final int[] keys = cjkTag.toOrderedKeys();
        String w = convertToHCIDMetrics(keys, this.hMetrics);
        if (w != null) {
            dic.put(PdfName.W, new PdfLiteral(w));
        }
        if (this.vertical) {
            w = convertToVCIDMetrics(keys, this.vMetrics, this.hMetrics);
            if (w != null) {
                dic.put(PdfName.W2, new PdfLiteral(w));
            }
        }
        else {
            dic.put(PdfName.DW, new PdfNumber(1000));
        }
        final PdfDictionary cdic = new PdfDictionary();
        cdic.put(PdfName.REGISTRY, new PdfString(this.fontDesc.get("Registry"), null));
        cdic.put(PdfName.ORDERING, new PdfString(this.fontDesc.get("Ordering"), null));
        cdic.put(PdfName.SUPPLEMENT, new PdfLiteral(this.fontDesc.get("Supplement")));
        dic.put(PdfName.CIDSYSTEMINFO, cdic);
        return dic;
    }
    
    private PdfDictionary getFontBaseType(final PdfIndirectReference CIDFont) {
        final PdfDictionary dic = new PdfDictionary(PdfName.FONT);
        dic.put(PdfName.SUBTYPE, PdfName.TYPE0);
        String name = this.fontName;
        if (this.style.length() > 0) {
            name = name + "-" + this.style.substring(1);
        }
        name = name + "-" + this.CMap;
        dic.put(PdfName.BASEFONT, new PdfName(name));
        dic.put(PdfName.ENCODING, new PdfName(this.CMap));
        dic.put(PdfName.DESCENDANTFONTS, new PdfArray(CIDFont));
        return dic;
    }
    
    @Override
    void writeFont(final PdfWriter writer, final PdfIndirectReference ref, final Object[] params) throws DocumentException, IOException {
        final IntHashtable cjkTag = (IntHashtable)params[0];
        PdfIndirectReference ind_font = null;
        PdfObject pobj = null;
        PdfIndirectObject obj = null;
        pobj = this.getFontDescriptor();
        if (pobj != null) {
            obj = writer.addToBody(pobj);
            ind_font = obj.getIndirectReference();
        }
        pobj = this.getCIDFont(ind_font, cjkTag);
        if (pobj != null) {
            obj = writer.addToBody(pobj);
            ind_font = obj.getIndirectReference();
        }
        pobj = this.getFontBaseType(ind_font);
        writer.addToBody(pobj, ref);
    }
    
    public PdfStream getFullFontStream() {
        return null;
    }
    
    private float getDescNumber(final String name) {
        return (float)Integer.parseInt(this.fontDesc.get(name));
    }
    
    private float getBBox(final int idx) {
        final String s = this.fontDesc.get("FontBBox");
        final StringTokenizer tk = new StringTokenizer(s, " []\r\n\t\f");
        String ret = tk.nextToken();
        for (int k = 0; k < idx; ++k) {
            ret = tk.nextToken();
        }
        return (float)Integer.parseInt(ret);
    }
    
    @Override
    public float getFontDescriptor(final int key, final float fontSize) {
        switch (key) {
            case 1:
            case 9: {
                return this.getDescNumber("Ascent") * fontSize / 1000.0f;
            }
            case 2: {
                return this.getDescNumber("CapHeight") * fontSize / 1000.0f;
            }
            case 3:
            case 10: {
                return this.getDescNumber("Descent") * fontSize / 1000.0f;
            }
            case 4: {
                return this.getDescNumber("ItalicAngle");
            }
            case 5: {
                return fontSize * this.getBBox(0) / 1000.0f;
            }
            case 6: {
                return fontSize * this.getBBox(1) / 1000.0f;
            }
            case 7: {
                return fontSize * this.getBBox(2) / 1000.0f;
            }
            case 8: {
                return fontSize * this.getBBox(3) / 1000.0f;
            }
            case 11: {
                return 0.0f;
            }
            case 12: {
                return fontSize * (this.getBBox(2) - this.getBBox(0)) / 1000.0f;
            }
            default: {
                return 0.0f;
            }
        }
    }
    
    @Override
    public String getPostscriptFontName() {
        return this.fontName;
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
    public String[][] getFamilyFontName() {
        return this.getFullFontName();
    }
    
    static char[] readCMap(String name) {
        try {
            name += ".cmap";
            final InputStream is = BaseFont.getResourceStream("com/lowagie/text/pdf/fonts/" + name);
            final char[] c = new char[65536];
            for (int k = 0; k < 65536; ++k) {
                c[k] = (char)((is.read() << 8) + is.read());
            }
            is.close();
            return c;
        }
        catch (final Exception ex) {
            return null;
        }
    }
    
    static IntHashtable createMetric(final String s) {
        final IntHashtable h = new IntHashtable();
        final StringTokenizer tk = new StringTokenizer(s);
        while (tk.hasMoreTokens()) {
            final int n1 = Integer.parseInt(tk.nextToken());
            h.put(n1, Integer.parseInt(tk.nextToken()));
        }
        return h;
    }
    
    static String convertToHCIDMetrics(final int[] keys, final IntHashtable h) {
        if (keys.length == 0) {
            return null;
        }
        int lastCid = 0;
        int lastValue = 0;
        int start;
        for (start = 0; start < keys.length; ++start) {
            lastCid = keys[start];
            lastValue = h.get(lastCid);
            if (lastValue != 0) {
                ++start;
                break;
            }
        }
        if (lastValue == 0) {
            return null;
        }
        final StringBuffer buf = new StringBuffer();
        buf.append('[');
        buf.append(lastCid);
        int state = 0;
        for (int k = start; k < keys.length; ++k) {
            final int cid = keys[k];
            final int value = h.get(cid);
            if (value != 0) {
                switch (state) {
                    case 0: {
                        if (cid == lastCid + 1 && value == lastValue) {
                            state = 2;
                            break;
                        }
                        if (cid == lastCid + 1) {
                            state = 1;
                            buf.append('[').append(lastValue);
                            break;
                        }
                        buf.append('[').append(lastValue).append(']').append(cid);
                        break;
                    }
                    case 1: {
                        if (cid == lastCid + 1 && value == lastValue) {
                            state = 2;
                            buf.append(']').append(lastCid);
                            break;
                        }
                        if (cid == lastCid + 1) {
                            buf.append(' ').append(lastValue);
                            break;
                        }
                        state = 0;
                        buf.append(' ').append(lastValue).append(']').append(cid);
                        break;
                    }
                    case 2: {
                        if (cid != lastCid + 1 || value != lastValue) {
                            buf.append(' ').append(lastCid).append(' ').append(lastValue).append(' ').append(cid);
                            state = 0;
                            break;
                        }
                        break;
                    }
                }
                lastValue = value;
                lastCid = cid;
            }
        }
        switch (state) {
            case 0: {
                buf.append('[').append(lastValue).append("]]");
                break;
            }
            case 1: {
                buf.append(' ').append(lastValue).append("]]");
                break;
            }
            case 2: {
                buf.append(' ').append(lastCid).append(' ').append(lastValue).append(']');
                break;
            }
        }
        return buf.toString();
    }
    
    static String convertToVCIDMetrics(final int[] keys, final IntHashtable v, final IntHashtable h) {
        if (keys.length == 0) {
            return null;
        }
        int lastCid = 0;
        int lastValue = 0;
        int lastHValue = 0;
        int start;
        for (start = 0; start < keys.length; ++start) {
            lastCid = keys[start];
            lastValue = v.get(lastCid);
            if (lastValue != 0) {
                ++start;
                break;
            }
            lastHValue = h.get(lastCid);
        }
        if (lastValue == 0) {
            return null;
        }
        if (lastHValue == 0) {
            lastHValue = 1000;
        }
        final StringBuffer buf = new StringBuffer();
        buf.append('[');
        buf.append(lastCid);
        int state = 0;
        for (int k = start; k < keys.length; ++k) {
            final int cid = keys[k];
            final int value = v.get(cid);
            if (value != 0) {
                int hValue = h.get(lastCid);
                if (hValue == 0) {
                    hValue = 1000;
                }
                switch (state) {
                    case 0: {
                        if (cid == lastCid + 1 && value == lastValue && hValue == lastHValue) {
                            state = 2;
                            break;
                        }
                        buf.append(' ').append(lastCid).append(' ').append(-lastValue).append(' ').append(lastHValue / 2).append(' ').append(880).append(' ').append(cid);
                        break;
                    }
                    case 2: {
                        if (cid != lastCid + 1 || value != lastValue || hValue != lastHValue) {
                            buf.append(' ').append(lastCid).append(' ').append(-lastValue).append(' ').append(lastHValue / 2).append(' ').append(880).append(' ').append(cid);
                            state = 0;
                            break;
                        }
                        break;
                    }
                }
                lastValue = value;
                lastCid = cid;
                lastHValue = hValue;
            }
        }
        buf.append(' ').append(lastCid).append(' ').append(-lastValue).append(' ').append(lastHValue / 2).append(' ').append(880).append(" ]");
        return buf.toString();
    }
    
    static HashMap<Object, Object> readFontProperties(String name) {
        try {
            name += ".properties";
            final InputStream is = BaseFont.getResourceStream("com/lowagie/text/pdf/fonts/" + name);
            final Properties p = new Properties();
            p.load(is);
            is.close();
            final IntHashtable W = createMetric(p.getProperty("W"));
            p.remove("W");
            final IntHashtable W2 = createMetric(p.getProperty("W2"));
            p.remove("W2");
            final HashMap<Object, Object> map = new HashMap<Object, Object>();
            final Enumeration e = p.keys();
            while (e.hasMoreElements()) {
                final Object obj = e.nextElement();
                map.put(obj, p.getProperty((String)obj));
            }
            map.put("W", W);
            map.put("W2", W2);
            return map;
        }
        catch (final Exception ex) {
            return null;
        }
    }
    
    @Override
    public int getUnicodeEquivalent(final int c) {
        if (this.cidDirect) {
            return this.translationMap[c];
        }
        return c;
    }
    
    @Override
    public int getCidCode(final int c) {
        if (this.cidDirect) {
            return c;
        }
        return this.translationMap[c];
    }
    
    @Override
    public boolean hasKernPairs() {
        return false;
    }
    
    @Override
    public boolean charExists(final int c) {
        return this.translationMap[c] != '\0';
    }
    
    @Override
    public boolean setCharAdvance(final int c, final int advance) {
        return false;
    }
    
    @Override
    public void setPostscriptFontName(final String name) {
        this.fontName = name;
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
    
    static {
        CJKFont.cjkFonts = new Properties();
        CJKFont.cjkEncodings = new Properties();
        CJKFont.allFonts = new ConcurrentHashMap<String, HashMap<Object, Object>>(500, 0.85f, 64);
        CJKFont.propertiesLoaded = false;
        CJKFont.initLock = new Object();
    }
}
