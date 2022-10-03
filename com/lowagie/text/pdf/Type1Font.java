package com.lowagie.text.pdf;

import java.util.StringTokenizer;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import com.lowagie.text.Document;
import java.io.ByteArrayOutputStream;
import com.lowagie.text.DocumentException;
import com.lowagie.text.error_messages.MessageLocalization;
import java.util.HashMap;
import com.lowagie.text.pdf.fonts.FontsResourceAnchor;

class Type1Font extends BaseFont
{
    private static FontsResourceAnchor resourceAnchor;
    protected byte[] pfb;
    private String FontName;
    private String FullName;
    private String FamilyName;
    private String Weight;
    private float ItalicAngle;
    private boolean IsFixedPitch;
    private String CharacterSet;
    private int llx;
    private int lly;
    private int urx;
    private int ury;
    private int UnderlinePosition;
    private int UnderlineThickness;
    private String EncodingScheme;
    private int CapHeight;
    private int XHeight;
    private int Ascender;
    private int Descender;
    private int StdHW;
    private int StdVW;
    private HashMap CharMetrics;
    private HashMap KernPairs;
    private String fileName;
    private boolean builtinFont;
    private static final int[] PFB_TYPES;
    
    Type1Font(final String afmFile, final String enc, final boolean emb, final byte[] ttfAfm, final byte[] pfb, final boolean forceRead) throws DocumentException, IOException {
        this.Weight = "";
        this.ItalicAngle = 0.0f;
        this.IsFixedPitch = false;
        this.llx = -50;
        this.lly = -200;
        this.urx = 1000;
        this.ury = 900;
        this.UnderlinePosition = -100;
        this.UnderlineThickness = 50;
        this.EncodingScheme = "FontSpecific";
        this.CapHeight = 700;
        this.XHeight = 480;
        this.Ascender = 800;
        this.Descender = -200;
        this.StdVW = 80;
        this.CharMetrics = new HashMap();
        this.KernPairs = new HashMap();
        this.builtinFont = false;
        if (emb && ttfAfm != null && pfb == null) {
            throw new DocumentException(MessageLocalization.getComposedMessage("two.byte.arrays.are.needed.if.the.type1.font.is.embedded"));
        }
        if (emb && ttfAfm != null) {
            this.pfb = pfb;
        }
        this.encoding = enc;
        this.embedded = emb;
        this.fileName = afmFile;
        this.fontType = 0;
        RandomAccessFileOrArray rf = null;
        InputStream is = null;
        if (Type1Font.BuiltinFonts14.containsKey(afmFile)) {
            this.embedded = false;
            this.builtinFont = true;
            byte[] buf = new byte[1024];
            try {
                if (Type1Font.resourceAnchor == null) {
                    Type1Font.resourceAnchor = new FontsResourceAnchor();
                }
                is = BaseFont.getResourceStream("com/lowagie/text/pdf/fonts/" + afmFile + ".afm", Type1Font.resourceAnchor.getClass().getClassLoader());
                if (is == null) {
                    final String msg = MessageLocalization.getComposedMessage("1.not.found.as.resource", afmFile);
                    System.err.println(msg);
                    throw new DocumentException(msg);
                }
                final ByteArrayOutputStream out = new ByteArrayOutputStream();
                while (true) {
                    final int size = is.read(buf);
                    if (size < 0) {
                        break;
                    }
                    out.write(buf, 0, size);
                }
                buf = out.toByteArray();
            }
            finally {
                if (is != null) {
                    try {
                        is.close();
                    }
                    catch (final Exception ex) {}
                }
            }
            try {
                rf = new RandomAccessFileOrArray(buf);
                this.process(rf);
            }
            finally {
                if (rf != null) {
                    try {
                        rf.close();
                    }
                    catch (final Exception ex2) {}
                }
            }
        }
        else if (afmFile.toLowerCase().endsWith(".afm")) {
            try {
                if (ttfAfm == null) {
                    rf = new RandomAccessFileOrArray(afmFile, forceRead, Document.plainRandomAccess);
                }
                else {
                    rf = new RandomAccessFileOrArray(ttfAfm);
                }
                this.process(rf);
            }
            finally {
                if (rf != null) {
                    try {
                        rf.close();
                    }
                    catch (final Exception ex3) {}
                }
            }
        }
        else {
            if (!afmFile.toLowerCase().endsWith(".pfm")) {
                throw new DocumentException(MessageLocalization.getComposedMessage("1.is.not.an.afm.or.pfm.font.file", afmFile));
            }
            try {
                final ByteArrayOutputStream ba = new ByteArrayOutputStream();
                if (ttfAfm == null) {
                    rf = new RandomAccessFileOrArray(afmFile, forceRead, Document.plainRandomAccess);
                }
                else {
                    rf = new RandomAccessFileOrArray(ttfAfm);
                }
                Pfm2afm.convert(rf, ba);
                rf.close();
                rf = new RandomAccessFileOrArray(ba.toByteArray());
                this.process(rf);
            }
            finally {
                if (rf != null) {
                    try {
                        rf.close();
                    }
                    catch (final Exception ex4) {}
                }
            }
        }
        this.EncodingScheme = this.EncodingScheme.trim();
        if (this.EncodingScheme.equals("AdobeStandardEncoding") || this.EncodingScheme.equals("StandardEncoding")) {
            this.fontSpecific = false;
        }
        if (!this.encoding.startsWith("#")) {
            PdfEncodings.convertToBytes(" ", enc);
        }
        this.createEncoding();
    }
    
    @Override
    int getRawWidth(final int c, final String name) {
        Object[] metrics;
        if (name == null) {
            metrics = this.CharMetrics.get(new Integer(c));
        }
        else {
            if (name.equals(".notdef")) {
                return 0;
            }
            metrics = this.CharMetrics.get(name);
        }
        if (metrics != null) {
            return (int)metrics[1];
        }
        return 0;
    }
    
    @Override
    public int getKerning(final int char1, final int char2) {
        final String first = GlyphList.unicodeToName(char1);
        if (first == null) {
            return 0;
        }
        final String second = GlyphList.unicodeToName(char2);
        if (second == null) {
            return 0;
        }
        final Object[] obj = this.KernPairs.get(first);
        if (obj == null) {
            return 0;
        }
        for (int k = 0; k < obj.length; k += 2) {
            if (second.equals(obj[k])) {
                return (int)obj[k + 1];
            }
        }
        return 0;
    }
    
    public void process(final RandomAccessFileOrArray rf) throws DocumentException, IOException {
        boolean isMetrics = false;
        String line;
        while ((line = rf.readLine()) != null) {
            final StringTokenizer tok = new StringTokenizer(line, " ,\n\r\t\f");
            if (!tok.hasMoreTokens()) {
                continue;
            }
            final String ident = tok.nextToken();
            if (ident.equals("FontName")) {
                this.FontName = tok.nextToken("\u00ff").substring(1);
            }
            else if (ident.equals("FullName")) {
                this.FullName = tok.nextToken("\u00ff").substring(1);
            }
            else if (ident.equals("FamilyName")) {
                this.FamilyName = tok.nextToken("\u00ff").substring(1);
            }
            else if (ident.equals("Weight")) {
                this.Weight = tok.nextToken("\u00ff").substring(1);
            }
            else if (ident.equals("ItalicAngle")) {
                this.ItalicAngle = Float.parseFloat(tok.nextToken());
            }
            else if (ident.equals("IsFixedPitch")) {
                this.IsFixedPitch = tok.nextToken().equals("true");
            }
            else if (ident.equals("CharacterSet")) {
                this.CharacterSet = tok.nextToken("\u00ff").substring(1);
            }
            else if (ident.equals("FontBBox")) {
                this.llx = (int)Float.parseFloat(tok.nextToken());
                this.lly = (int)Float.parseFloat(tok.nextToken());
                this.urx = (int)Float.parseFloat(tok.nextToken());
                this.ury = (int)Float.parseFloat(tok.nextToken());
            }
            else if (ident.equals("UnderlinePosition")) {
                this.UnderlinePosition = (int)Float.parseFloat(tok.nextToken());
            }
            else if (ident.equals("UnderlineThickness")) {
                this.UnderlineThickness = (int)Float.parseFloat(tok.nextToken());
            }
            else if (ident.equals("EncodingScheme")) {
                this.EncodingScheme = tok.nextToken("\u00ff").substring(1);
            }
            else if (ident.equals("CapHeight")) {
                this.CapHeight = (int)Float.parseFloat(tok.nextToken());
            }
            else if (ident.equals("XHeight")) {
                this.XHeight = (int)Float.parseFloat(tok.nextToken());
            }
            else if (ident.equals("Ascender")) {
                this.Ascender = (int)Float.parseFloat(tok.nextToken());
            }
            else if (ident.equals("Descender")) {
                this.Descender = (int)Float.parseFloat(tok.nextToken());
            }
            else if (ident.equals("StdHW")) {
                this.StdHW = (int)Float.parseFloat(tok.nextToken());
            }
            else if (ident.equals("StdVW")) {
                this.StdVW = (int)Float.parseFloat(tok.nextToken());
            }
            else {
                if (ident.equals("StartCharMetrics")) {
                    isMetrics = true;
                    break;
                }
                continue;
            }
        }
        if (!isMetrics) {
            throw new DocumentException(MessageLocalization.getComposedMessage("missing.startcharmetrics.in.1", this.fileName));
        }
        while ((line = rf.readLine()) != null) {
            StringTokenizer tok = new StringTokenizer(line);
            if (!tok.hasMoreTokens()) {
                continue;
            }
            String ident = tok.nextToken();
            if (ident.equals("EndCharMetrics")) {
                isMetrics = false;
                break;
            }
            Integer C = new Integer(-1);
            Integer WX = new Integer(250);
            String N = "";
            int[] B = null;
            tok = new StringTokenizer(line, ";");
            while (tok.hasMoreTokens()) {
                final StringTokenizer tokc = new StringTokenizer(tok.nextToken());
                if (!tokc.hasMoreTokens()) {
                    continue;
                }
                ident = tokc.nextToken();
                if (ident.equals("C")) {
                    C = Integer.valueOf(tokc.nextToken());
                }
                else if (ident.equals("WX")) {
                    WX = new Integer((int)Float.parseFloat(tokc.nextToken()));
                }
                else if (ident.equals("N")) {
                    N = tokc.nextToken();
                }
                else {
                    if (!ident.equals("B")) {
                        continue;
                    }
                    B = new int[] { Integer.parseInt(tokc.nextToken()), Integer.parseInt(tokc.nextToken()), Integer.parseInt(tokc.nextToken()), Integer.parseInt(tokc.nextToken()) };
                }
            }
            final Object[] metrics = { C, WX, N, B };
            if (C >= 0) {
                this.CharMetrics.put(C, metrics);
            }
            this.CharMetrics.put(N, metrics);
        }
        if (isMetrics) {
            throw new DocumentException(MessageLocalization.getComposedMessage("missing.endcharmetrics.in.1", this.fileName));
        }
        if (!this.CharMetrics.containsKey("nonbreakingspace")) {
            final Object[] space = this.CharMetrics.get("space");
            if (space != null) {
                this.CharMetrics.put("nonbreakingspace", space);
            }
        }
        while ((line = rf.readLine()) != null) {
            final StringTokenizer tok = new StringTokenizer(line);
            if (!tok.hasMoreTokens()) {
                continue;
            }
            final String ident = tok.nextToken();
            if (ident.equals("EndFontMetrics")) {
                return;
            }
            if (ident.equals("StartKernPairs")) {
                isMetrics = true;
                break;
            }
        }
        if (!isMetrics) {
            throw new DocumentException(MessageLocalization.getComposedMessage("missing.endfontmetrics.in.1", this.fileName));
        }
        while ((line = rf.readLine()) != null) {
            final StringTokenizer tok = new StringTokenizer(line);
            if (!tok.hasMoreTokens()) {
                continue;
            }
            final String ident = tok.nextToken();
            if (ident.equals("KPX")) {
                final String first = tok.nextToken();
                final String second = tok.nextToken();
                final Integer width = new Integer((int)Float.parseFloat(tok.nextToken()));
                final Object[] relates = this.KernPairs.get(first);
                if (relates == null) {
                    this.KernPairs.put(first, new Object[] { second, width });
                }
                else {
                    final int n = relates.length;
                    final Object[] relates2 = new Object[n + 2];
                    System.arraycopy(relates, 0, relates2, 0, n);
                    relates2[n] = second;
                    relates2[n + 1] = width;
                    this.KernPairs.put(first, relates2);
                }
            }
            else {
                if (ident.equals("EndKernPairs")) {
                    isMetrics = false;
                    break;
                }
                continue;
            }
        }
        if (isMetrics) {
            throw new DocumentException(MessageLocalization.getComposedMessage("missing.endkernpairs.in.1", this.fileName));
        }
        rf.close();
    }
    
    public PdfStream getFullFontStream() throws DocumentException {
        if (this.builtinFont || !this.embedded) {
            return null;
        }
        RandomAccessFileOrArray rf = null;
        try {
            final String filePfb = this.fileName.substring(0, this.fileName.length() - 3) + "pfb";
            if (this.pfb == null) {
                rf = new RandomAccessFileOrArray(filePfb, true, Document.plainRandomAccess);
            }
            else {
                rf = new RandomAccessFileOrArray(this.pfb);
            }
            final int fileLength = rf.length();
            final byte[] st = new byte[fileLength - 18];
            final int[] lengths = new int[3];
            int bytePtr = 0;
            for (int k = 0; k < 3; ++k) {
                if (rf.read() != 128) {
                    throw new DocumentException(MessageLocalization.getComposedMessage("start.marker.missing.in.1", filePfb));
                }
                if (rf.read() != Type1Font.PFB_TYPES[k]) {
                    throw new DocumentException(MessageLocalization.getComposedMessage("incorrect.segment.type.in.1", filePfb));
                }
                int size = rf.read();
                size += rf.read() << 8;
                size += rf.read() << 16;
                size += rf.read() << 24;
                lengths[k] = size;
                while (size != 0) {
                    final int got = rf.read(st, bytePtr, size);
                    if (got < 0) {
                        throw new DocumentException(MessageLocalization.getComposedMessage("premature.end.in.1", filePfb));
                    }
                    bytePtr += got;
                    size -= got;
                }
            }
            return new StreamFont(st, lengths, this.compressionLevel);
        }
        catch (final Exception e) {
            throw new DocumentException(e);
        }
        finally {
            if (rf != null) {
                try {
                    rf.close();
                }
                catch (final Exception ex) {}
            }
        }
    }
    
    private PdfDictionary getFontDescriptor(final PdfIndirectReference fontStream) {
        if (this.builtinFont) {
            return null;
        }
        final PdfDictionary dic = new PdfDictionary(PdfName.FONTDESCRIPTOR);
        dic.put(PdfName.ASCENT, new PdfNumber(this.Ascender));
        dic.put(PdfName.CAPHEIGHT, new PdfNumber(this.CapHeight));
        dic.put(PdfName.DESCENT, new PdfNumber(this.Descender));
        dic.put(PdfName.FONTBBOX, new PdfRectangle((float)this.llx, (float)this.lly, (float)this.urx, (float)this.ury));
        dic.put(PdfName.FONTNAME, new PdfName(this.FontName));
        dic.put(PdfName.ITALICANGLE, new PdfNumber(this.ItalicAngle));
        dic.put(PdfName.STEMV, new PdfNumber(this.StdVW));
        if (fontStream != null) {
            dic.put(PdfName.FONTFILE, fontStream);
        }
        int flags = 0;
        if (this.IsFixedPitch) {
            flags |= 0x1;
        }
        flags |= (this.fontSpecific ? 4 : 32);
        if (this.ItalicAngle < 0.0f) {
            flags |= 0x40;
        }
        if (this.FontName.indexOf("Caps") >= 0 || this.FontName.endsWith("SC")) {
            flags |= 0x20000;
        }
        if (this.Weight.equals("Bold")) {
            flags |= 0x40000;
        }
        dic.put(PdfName.FLAGS, new PdfNumber(flags));
        return dic;
    }
    
    private PdfDictionary getFontBaseType(final PdfIndirectReference fontDescriptor, int firstChar, final int lastChar, final byte[] shortTag) {
        final PdfDictionary dic = new PdfDictionary(PdfName.FONT);
        dic.put(PdfName.SUBTYPE, PdfName.TYPE1);
        dic.put(PdfName.BASEFONT, new PdfName(this.FontName));
        final boolean stdEncoding = this.encoding.equals("Cp1252") || this.encoding.equals("MacRoman");
        if (!this.fontSpecific || this.specialMap != null) {
            for (int k = firstChar; k <= lastChar; ++k) {
                if (!this.differences[k].equals(".notdef")) {
                    firstChar = k;
                    break;
                }
            }
            if (stdEncoding) {
                dic.put(PdfName.ENCODING, this.encoding.equals("Cp1252") ? PdfName.WIN_ANSI_ENCODING : PdfName.MAC_ROMAN_ENCODING);
            }
            else {
                final PdfDictionary enc = new PdfDictionary(PdfName.ENCODING);
                final PdfArray dif = new PdfArray();
                boolean gap = true;
                for (int i = firstChar; i <= lastChar; ++i) {
                    if (shortTag[i] != 0) {
                        if (gap) {
                            dif.add(new PdfNumber(i));
                            gap = false;
                        }
                        dif.add(new PdfName(this.differences[i]));
                    }
                    else {
                        gap = true;
                    }
                }
                enc.put(PdfName.DIFFERENCES, dif);
                dic.put(PdfName.ENCODING, enc);
            }
        }
        if (this.specialMap != null || this.forceWidthsOutput || !this.builtinFont || (!this.fontSpecific && !stdEncoding)) {
            dic.put(PdfName.FIRSTCHAR, new PdfNumber(firstChar));
            dic.put(PdfName.LASTCHAR, new PdfNumber(lastChar));
            final PdfArray wd = new PdfArray();
            for (int j = firstChar; j <= lastChar; ++j) {
                if (shortTag[j] == 0) {
                    wd.add(new PdfNumber(0));
                }
                else {
                    wd.add(new PdfNumber(this.widths[j]));
                }
            }
            dic.put(PdfName.WIDTHS, wd);
        }
        if (!this.builtinFont && fontDescriptor != null) {
            dic.put(PdfName.FONTDESCRIPTOR, fontDescriptor);
        }
        return dic;
    }
    
    @Override
    void writeFont(final PdfWriter writer, final PdfIndirectReference ref, final Object[] params) throws DocumentException, IOException {
        int firstChar = (int)params[0];
        int lastChar = (int)params[1];
        final byte[] shortTag = (byte[])params[2];
        final boolean subsetp = (boolean)params[3] && this.subset;
        if (!subsetp) {
            firstChar = 0;
            lastChar = shortTag.length - 1;
            for (int k = 0; k < shortTag.length; ++k) {
                shortTag[k] = 1;
            }
        }
        PdfIndirectReference ind_font = null;
        PdfObject pobj = null;
        PdfIndirectObject obj = null;
        pobj = this.getFullFontStream();
        if (pobj != null) {
            obj = writer.addToBody(pobj);
            ind_font = obj.getIndirectReference();
        }
        pobj = this.getFontDescriptor(ind_font);
        if (pobj != null) {
            obj = writer.addToBody(pobj);
            ind_font = obj.getIndirectReference();
        }
        pobj = this.getFontBaseType(ind_font, firstChar, lastChar, shortTag);
        writer.addToBody(pobj, ref);
    }
    
    @Override
    public float getFontDescriptor(final int key, final float fontSize) {
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
            case 13: {
                return this.UnderlinePosition * fontSize / 1000.0f;
            }
            case 14: {
                return this.UnderlineThickness * fontSize / 1000.0f;
            }
            default: {
                return 0.0f;
            }
        }
    }
    
    @Override
    public String getPostscriptFontName() {
        return this.FontName;
    }
    
    @Override
    public String[][] getFullFontName() {
        return new String[][] { { "", "", "", this.FullName } };
    }
    
    @Override
    public String[][] getAllNameEntries() {
        return new String[][] { { "4", "", "", "", this.FullName } };
    }
    
    @Override
    public String[][] getFamilyFontName() {
        return new String[][] { { "", "", "", this.FamilyName } };
    }
    
    @Override
    public boolean hasKernPairs() {
        return !this.KernPairs.isEmpty();
    }
    
    @Override
    public void setPostscriptFontName(final String name) {
        this.FontName = name;
    }
    
    @Override
    public boolean setKerning(final int char1, final int char2, final int kern) {
        final String first = GlyphList.unicodeToName(char1);
        if (first == null) {
            return false;
        }
        final String second = GlyphList.unicodeToName(char2);
        if (second == null) {
            return false;
        }
        Object[] obj = this.KernPairs.get(first);
        if (obj == null) {
            obj = new Object[] { second, new Integer(kern) };
            this.KernPairs.put(first, obj);
            return true;
        }
        for (int k = 0; k < obj.length; k += 2) {
            if (second.equals(obj[k])) {
                obj[k + 1] = new Integer(kern);
                return true;
            }
        }
        final int size = obj.length;
        final Object[] obj2 = new Object[size + 2];
        System.arraycopy(obj, 0, obj2, 0, size);
        obj2[size] = second;
        obj2[size + 1] = new Integer(kern);
        this.KernPairs.put(first, obj2);
        return true;
    }
    
    @Override
    protected int[] getRawCharBBox(final int c, final String name) {
        Object[] metrics;
        if (name == null) {
            metrics = this.CharMetrics.get(new Integer(c));
        }
        else {
            if (name.equals(".notdef")) {
                return null;
            }
            metrics = this.CharMetrics.get(name);
        }
        if (metrics != null) {
            return (int[])metrics[3];
        }
        return null;
    }
    
    static {
        PFB_TYPES = new int[] { 1, 2, 1 };
    }
}
