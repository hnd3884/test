package com.lowagie.text.pdf;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import com.lowagie.text.Utilities;
import java.util.Iterator;
import java.util.HashMap;
import java.io.IOException;
import com.lowagie.text.DocumentException;
import com.lowagie.text.error_messages.MessageLocalization;
import java.util.Map;
import java.util.Comparator;

class TrueTypeFontUnicode extends TrueTypeFont implements Comparator
{
    boolean vertical;
    Map<Integer, Integer> inverseCmap;
    private static final byte[] rotbits;
    
    TrueTypeFontUnicode(final String ttFile, final String enc, final boolean emb, final byte[] ttfAfm, final boolean forceRead) throws DocumentException, IOException {
        this.vertical = false;
        final String nameBase = BaseFont.getBaseName(ttFile);
        final String ttcName = TrueTypeFont.getTTCName(nameBase);
        if (nameBase.length() < ttFile.length()) {
            this.style = ttFile.substring(nameBase.length());
        }
        this.encoding = enc;
        this.embedded = emb;
        this.fileName = ttcName;
        this.ttcIndex = "";
        if (ttcName.length() < nameBase.length()) {
            this.ttcIndex = nameBase.substring(ttcName.length() + 1);
        }
        this.fontType = 3;
        if ((!this.fileName.toLowerCase().endsWith(".ttf") && !this.fileName.toLowerCase().endsWith(".otf") && !this.fileName.toLowerCase().endsWith(".ttc")) || (!enc.equals("Identity-H") && !enc.equals("Identity-V")) || !emb) {
            throw new DocumentException(MessageLocalization.getComposedMessage("1.2.is.not.a.ttf.font.file", this.fileName, this.style));
        }
        this.process(ttfAfm, forceRead);
        if (this.os_2.fsType == 2) {
            throw new DocumentException(MessageLocalization.getComposedMessage("1.cannot.be.embedded.due.to.licensing.restrictions", this.fileName + this.style));
        }
        if ((this.cmap31 == null && !this.fontSpecific) || (this.cmap10 == null && this.fontSpecific)) {
            this.directTextToByte = true;
        }
        if (this.fontSpecific) {
            this.fontSpecific = false;
            final String tempEncoding = this.encoding;
            this.encoding = "";
            this.createEncoding();
            this.encoding = tempEncoding;
            this.fontSpecific = true;
        }
        this.vertical = enc.endsWith("V");
    }
    
    @Override
    void readCMaps() throws DocumentException, IOException {
        super.readCMaps();
        Map cmap = null;
        if (this.cmapExt != null) {
            cmap = this.cmapExt;
        }
        else if (this.cmap31 != null) {
            cmap = this.cmap31;
        }
        if (cmap != null) {
            this.inverseCmap = new HashMap<Integer, Integer>();
            for (final Map.Entry entry : cmap.entrySet()) {
                final Integer code = entry.getKey();
                final int[] metrics = entry.getValue();
                this.inverseCmap.put(metrics[0], code);
            }
        }
    }
    
    protected Integer getCharacterCode(final int code) {
        return (this.inverseCmap == null) ? null : this.inverseCmap.get(code);
    }
    
    @Override
    public int getWidth(final int char1) {
        if (this.vertical) {
            return 1000;
        }
        if (!this.fontSpecific) {
            return this.getRawWidth(char1, this.encoding);
        }
        if ((char1 & 0xFF00) == 0x0 || (char1 & 0xFF00) == 0xF000) {
            return this.getRawWidth(char1 & 0xFF, null);
        }
        return 0;
    }
    
    @Override
    public int getWidth(final String text) {
        if (this.vertical) {
            return text.length() * 1000;
        }
        int total = 0;
        if (this.fontSpecific) {
            for (final char c : text.toCharArray()) {
                if ((c & '\uff00') == 0x0 || (c & '\uff00') == 0xF000) {
                    total += this.getRawWidth(c & '\u00ff', null);
                }
            }
        }
        else {
            for (int len2 = text.length(), i = 0; i < len2; ++i) {
                if (Utilities.isSurrogatePair(text, i)) {
                    total += this.getRawWidth(Utilities.convertToUtf32(text, i), this.encoding);
                    ++i;
                }
                else {
                    total += this.getRawWidth(text.charAt(i), this.encoding);
                }
            }
        }
        return total;
    }
    
    private PdfStream getToUnicode(Object[] metrics) {
        metrics = this.filterCmapMetrics(metrics);
        if (metrics.length == 0) {
            return null;
        }
        final StringBuffer buf = new StringBuffer("/CIDInit /ProcSet findresource begin\n12 dict begin\nbegincmap\n/CIDSystemInfo\n<< /Registry (TTX+0)\n/Ordering (T42UV)\n/Supplement 0\n>> def\n/CMapName /TTX+0 def\n/CMapType 2 def\n1 begincodespacerange\n<0000><FFFF>\nendcodespacerange\n");
        int size = 0;
        for (int k = 0; k < metrics.length; ++k) {
            if (size == 0) {
                if (k != 0) {
                    buf.append("endbfrange\n");
                }
                size = Math.min(100, metrics.length - k);
                buf.append(size).append(" beginbfrange\n");
            }
            --size;
            final int[] metric = (int[])metrics[k];
            final String fromTo = toHex(metric[0]);
            buf.append(fromTo).append(fromTo).append(toHex(metric[2])).append('\n');
        }
        buf.append("endbfrange\nendcmap\nCMapName currentdict /CMap defineresource pop\nend end\n");
        final String s = buf.toString();
        final PdfStream stream = new PdfStream(PdfEncodings.convertToBytes(s, null));
        stream.flateCompress(this.compressionLevel);
        return stream;
    }
    
    private Object[] filterCmapMetrics(final Object[] metrics) {
        if (metrics.length == 0) {
            return metrics;
        }
        final List<int[]> cmapMetrics = new ArrayList<int[]>(metrics.length);
        for (int i = 0; i < metrics.length; ++i) {
            final int[] metric = (int[])metrics[i];
            if (metric.length >= 3) {
                cmapMetrics.add(metric);
            }
        }
        if (cmapMetrics.size() == metrics.length) {
            return metrics;
        }
        return cmapMetrics.toArray();
    }
    
    private static String toHex4(final int n) {
        final String s = "0000" + Integer.toHexString(n);
        return s.substring(s.length() - 4);
    }
    
    static String toHex(int n) {
        if (n < 65536) {
            return "<" + toHex4(n) + ">";
        }
        n -= 65536;
        final int high = n / 1024 + 55296;
        final int low = n % 1024 + 56320;
        return "[<" + toHex4(high) + toHex4(low) + ">]";
    }
    
    private PdfDictionary getCIDFontType2(final PdfIndirectReference fontDescriptor, final String subsetPrefix, final Object[] metrics) {
        final PdfDictionary dic = new PdfDictionary(PdfName.FONT);
        if (this.cff) {
            dic.put(PdfName.SUBTYPE, PdfName.CIDFONTTYPE0);
            dic.put(PdfName.BASEFONT, new PdfName(subsetPrefix + this.fontName + "-" + this.encoding));
        }
        else {
            dic.put(PdfName.SUBTYPE, PdfName.CIDFONTTYPE2);
            dic.put(PdfName.BASEFONT, new PdfName(subsetPrefix + this.fontName));
        }
        dic.put(PdfName.FONTDESCRIPTOR, fontDescriptor);
        if (!this.cff) {
            dic.put(PdfName.CIDTOGIDMAP, PdfName.IDENTITY);
        }
        final PdfDictionary cdic = new PdfDictionary();
        cdic.put(PdfName.REGISTRY, new PdfString("Adobe"));
        cdic.put(PdfName.ORDERING, new PdfString("Identity"));
        cdic.put(PdfName.SUPPLEMENT, new PdfNumber(0));
        dic.put(PdfName.CIDSYSTEMINFO, cdic);
        if (!this.vertical) {
            dic.put(PdfName.DW, new PdfNumber(1000));
            final StringBuffer buf = new StringBuffer("[");
            int lastNumber = -10;
            boolean firstTime = true;
            for (int k = 0; k < metrics.length; ++k) {
                final int[] metric = (int[])metrics[k];
                if (metric[1] != 1000) {
                    final int m = metric[0];
                    if (m == lastNumber + 1) {
                        buf.append(' ').append(metric[1]);
                    }
                    else {
                        if (!firstTime) {
                            buf.append(']');
                        }
                        firstTime = false;
                        buf.append(m).append('[').append(metric[1]);
                    }
                    lastNumber = m;
                }
            }
            if (buf.length() > 1) {
                buf.append("]]");
                dic.put(PdfName.W, new PdfLiteral(buf.toString()));
            }
        }
        return dic;
    }
    
    private PdfDictionary getFontBaseType(final PdfIndirectReference descendant, final String subsetPrefix, final PdfIndirectReference toUnicode) {
        final PdfDictionary dic = new PdfDictionary(PdfName.FONT);
        dic.put(PdfName.SUBTYPE, PdfName.TYPE0);
        if (this.cff) {
            dic.put(PdfName.BASEFONT, new PdfName(subsetPrefix + this.fontName + "-" + this.encoding));
        }
        else {
            dic.put(PdfName.BASEFONT, new PdfName(subsetPrefix + this.fontName));
        }
        dic.put(PdfName.ENCODING, new PdfName(this.encoding));
        dic.put(PdfName.DESCENDANTFONTS, new PdfArray(descendant));
        if (toUnicode != null) {
            dic.put(PdfName.TOUNICODE, toUnicode);
        }
        return dic;
    }
    
    @Override
    public int compare(final Object o1, final Object o2) {
        final int m1 = ((int[])o1)[0];
        final int m2 = ((int[])o2)[0];
        if (m1 < m2) {
            return -1;
        }
        if (m1 == m2) {
            return 0;
        }
        return 1;
    }
    
    @Override
    void writeFont(final PdfWriter writer, final PdfIndirectReference ref, final Object[] params) throws DocumentException, IOException {
        final HashMap longTag = (HashMap)params[0];
        this.addRangeUni(longTag, true, this.subset);
        final Object[] metrics = longTag.values().toArray();
        Arrays.sort(metrics, this);
        PdfIndirectReference ind_font = null;
        PdfObject pobj = null;
        PdfIndirectObject obj = null;
        PdfIndirectReference cidset = null;
        if (writer.getPDFXConformance() == 3 || writer.getPDFXConformance() == 4) {
            PdfStream stream;
            if (metrics.length == 0) {
                stream = new PdfStream(new byte[] { -128 });
            }
            else {
                final int top = ((int[])metrics[metrics.length - 1])[0];
                final byte[] bt = new byte[top / 8 + 1];
                for (int k = 0; k < metrics.length; ++k) {
                    final int v = ((int[])metrics[k])[0];
                    final byte[] array = bt;
                    final int n = v / 8;
                    array[n] |= TrueTypeFontUnicode.rotbits[v % 8];
                }
                stream = new PdfStream(bt);
                stream.flateCompress(this.compressionLevel);
            }
            cidset = writer.addToBody(stream).getIndirectReference();
        }
        if (this.cff) {
            byte[] b = this.readCffFont();
            if (this.subset || this.subsetRanges != null) {
                final CFFFontSubset cff = new CFFFontSubset(new RandomAccessFileOrArray(b), longTag);
                b = cff.Process(cff.getNames()[0]);
            }
            pobj = new StreamFont(b, "CIDFontType0C", this.compressionLevel);
            obj = writer.addToBody(pobj);
            ind_font = obj.getIndirectReference();
        }
        else {
            byte[] b;
            if (this.subset || this.directoryOffset != 0) {
                final TrueTypeFontSubSet sb = new TrueTypeFontSubSet(this.fileName, new RandomAccessFileOrArray(this.rf), longTag, this.directoryOffset, false, false);
                b = sb.process();
            }
            else {
                b = this.getFullFont();
            }
            final int[] lengths = { b.length };
            pobj = new StreamFont(b, lengths, this.compressionLevel);
            obj = writer.addToBody(pobj);
            ind_font = obj.getIndirectReference();
        }
        String subsetPrefix = "";
        if (this.subset) {
            subsetPrefix = BaseFont.createSubsetPrefix();
        }
        final PdfDictionary dic = this.getFontDescriptor(ind_font, subsetPrefix, cidset);
        obj = writer.addToBody(dic);
        ind_font = obj.getIndirectReference();
        pobj = this.getCIDFontType2(ind_font, subsetPrefix, metrics);
        obj = writer.addToBody(pobj);
        ind_font = obj.getIndirectReference();
        pobj = this.getToUnicode(metrics);
        PdfIndirectReference toUnicodeRef = null;
        if (pobj != null) {
            obj = writer.addToBody(pobj);
            toUnicodeRef = obj.getIndirectReference();
        }
        pobj = this.getFontBaseType(ind_font, subsetPrefix, toUnicodeRef);
        writer.addToBody(pobj, ref);
    }
    
    @Override
    public PdfStream getFullFontStream() throws IOException, DocumentException {
        if (this.cff) {
            return new StreamFont(this.readCffFont(), "CIDFontType0C", this.compressionLevel);
        }
        return super.getFullFontStream();
    }
    
    @Override
    byte[] convertToBytes(final String text) {
        return null;
    }
    
    @Override
    byte[] convertToBytes(final int char1) {
        return null;
    }
    
    @Override
    public int[] getMetricsTT(final int c) {
        if (this.cmapExt != null) {
            return this.cmapExt.get(new Integer(c));
        }
        HashMap map = null;
        if (this.fontSpecific) {
            map = this.cmap10;
        }
        else {
            map = this.cmap31;
        }
        if (map == null) {
            return null;
        }
        if (!this.fontSpecific) {
            return map.get(new Integer(c));
        }
        if ((c & 0xFFFFFF00) == 0x0 || (c & 0xFFFFFF00) == 0xF000) {
            return map.get(new Integer(c & 0xFF));
        }
        return null;
    }
    
    @Override
    public boolean charExists(final int c) {
        return this.getMetricsTT(c) != null;
    }
    
    @Override
    public boolean setCharAdvance(final int c, final int advance) {
        final int[] m = this.getMetricsTT(c);
        if (m == null) {
            return false;
        }
        m[1] = advance;
        return true;
    }
    
    @Override
    public int[] getCharBBox(final int c) {
        if (this.bboxes == null) {
            return null;
        }
        final int[] m = this.getMetricsTT(c);
        if (m == null) {
            return null;
        }
        return this.bboxes[m[0]];
    }
    
    static {
        rotbits = new byte[] { -128, 64, 32, 16, 8, 4, 2, 1 };
    }
}
