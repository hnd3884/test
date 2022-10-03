package com.lowagie.text.pdf;

import java.util.Iterator;
import java.util.Map;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Document;
import java.util.ArrayList;
import java.io.File;
import java.io.IOException;
import com.lowagie.text.DocumentException;
import com.lowagie.text.error_messages.MessageLocalization;
import java.util.HashMap;

class TrueTypeFont extends BaseFont
{
    static final String[] codePages;
    protected boolean justNames;
    protected HashMap tables;
    protected RandomAccessFileOrArray rf;
    protected String fileName;
    protected boolean cff;
    protected int cffOffset;
    protected int cffLength;
    protected int directoryOffset;
    protected String ttcIndex;
    protected String style;
    protected FontHeader head;
    protected HorizontalHeader hhea;
    protected WindowsMetrics os_2;
    protected int[] GlyphWidths;
    protected int[][] bboxes;
    protected HashMap cmap10;
    protected HashMap cmap31;
    protected HashMap cmapExt;
    protected IntHashtable kerning;
    protected String fontName;
    protected String[][] fullName;
    protected String[][] allNameEntries;
    protected String[][] familyName;
    protected double italicAngle;
    protected boolean isFixedPitch;
    protected int underlinePosition;
    protected int underlineThickness;
    
    protected TrueTypeFont() {
        this.justNames = false;
        this.cff = false;
        this.style = "";
        this.head = new FontHeader();
        this.hhea = new HorizontalHeader();
        this.os_2 = new WindowsMetrics();
        this.kerning = new IntHashtable();
        this.isFixedPitch = false;
    }
    
    TrueTypeFont(final String ttFile, final String enc, final boolean emb, final byte[] ttfAfm, final boolean justNames, final boolean forceRead) throws DocumentException, IOException {
        this.justNames = false;
        this.cff = false;
        this.style = "";
        this.head = new FontHeader();
        this.hhea = new HorizontalHeader();
        this.os_2 = new WindowsMetrics();
        this.kerning = new IntHashtable();
        this.isFixedPitch = false;
        this.justNames = justNames;
        final String nameBase = BaseFont.getBaseName(ttFile);
        final String ttcName = getTTCName(nameBase);
        if (nameBase.length() < ttFile.length()) {
            this.style = ttFile.substring(nameBase.length());
        }
        this.encoding = enc;
        this.embedded = emb;
        this.fileName = ttcName;
        this.fontType = 1;
        this.ttcIndex = "";
        if (ttcName.length() < nameBase.length()) {
            this.ttcIndex = nameBase.substring(ttcName.length() + 1);
        }
        if (!this.fileName.toLowerCase().endsWith(".ttf") && !this.fileName.toLowerCase().endsWith(".otf") && !this.fileName.toLowerCase().endsWith(".ttc")) {
            throw new DocumentException(MessageLocalization.getComposedMessage("1.is.not.a.ttf.otf.or.ttc.font.file", this.fileName + this.style));
        }
        this.process(ttfAfm, forceRead);
        if (!justNames && this.embedded && this.os_2.fsType == 2) {
            throw new DocumentException(MessageLocalization.getComposedMessage("1.cannot.be.embedded.due.to.licensing.restrictions", this.fileName + this.style));
        }
        if (!this.encoding.startsWith("#")) {
            PdfEncodings.convertToBytes(" ", enc);
        }
        this.createEncoding();
    }
    
    protected static String getTTCName(final String name) {
        final int idx = name.toLowerCase().indexOf(".ttc,");
        if (idx < 0) {
            return name;
        }
        return name.substring(0, idx + 4);
    }
    
    void fillTables() throws DocumentException, IOException {
        int[] table_location = this.tables.get("head");
        if (table_location == null) {
            throw new DocumentException(MessageLocalization.getComposedMessage("table.1.does.not.exist.in.2", "head", this.fileName + this.style));
        }
        this.rf.seek(table_location[0] + 16);
        this.head.flags = this.rf.readUnsignedShort();
        this.head.unitsPerEm = this.rf.readUnsignedShort();
        this.rf.skipBytes(16);
        this.head.xMin = this.rf.readShort();
        this.head.yMin = this.rf.readShort();
        this.head.xMax = this.rf.readShort();
        this.head.yMax = this.rf.readShort();
        this.head.macStyle = this.rf.readUnsignedShort();
        table_location = this.tables.get("hhea");
        if (table_location == null) {
            throw new DocumentException(MessageLocalization.getComposedMessage("table.1.does.not.exist.in.2", "hhea", this.fileName + this.style));
        }
        this.rf.seek(table_location[0] + 4);
        this.hhea.Ascender = this.rf.readShort();
        this.hhea.Descender = this.rf.readShort();
        this.hhea.LineGap = this.rf.readShort();
        this.hhea.advanceWidthMax = this.rf.readUnsignedShort();
        this.hhea.minLeftSideBearing = this.rf.readShort();
        this.hhea.minRightSideBearing = this.rf.readShort();
        this.hhea.xMaxExtent = this.rf.readShort();
        this.hhea.caretSlopeRise = this.rf.readShort();
        this.hhea.caretSlopeRun = this.rf.readShort();
        this.rf.skipBytes(12);
        this.hhea.numberOfHMetrics = this.rf.readUnsignedShort();
        table_location = this.tables.get("OS/2");
        if (table_location == null) {
            throw new DocumentException(MessageLocalization.getComposedMessage("table.1.does.not.exist.in.2", "OS/2", this.fileName + this.style));
        }
        this.rf.seek(table_location[0]);
        final int version = this.rf.readUnsignedShort();
        this.os_2.xAvgCharWidth = this.rf.readShort();
        this.os_2.usWeightClass = this.rf.readUnsignedShort();
        this.os_2.usWidthClass = this.rf.readUnsignedShort();
        this.os_2.fsType = this.rf.readShort();
        this.os_2.ySubscriptXSize = this.rf.readShort();
        this.os_2.ySubscriptYSize = this.rf.readShort();
        this.os_2.ySubscriptXOffset = this.rf.readShort();
        this.os_2.ySubscriptYOffset = this.rf.readShort();
        this.os_2.ySuperscriptXSize = this.rf.readShort();
        this.os_2.ySuperscriptYSize = this.rf.readShort();
        this.os_2.ySuperscriptXOffset = this.rf.readShort();
        this.os_2.ySuperscriptYOffset = this.rf.readShort();
        this.os_2.yStrikeoutSize = this.rf.readShort();
        this.os_2.yStrikeoutPosition = this.rf.readShort();
        this.os_2.sFamilyClass = this.rf.readShort();
        this.rf.readFully(this.os_2.panose);
        this.rf.skipBytes(16);
        this.rf.readFully(this.os_2.achVendID);
        this.os_2.fsSelection = this.rf.readUnsignedShort();
        this.os_2.usFirstCharIndex = this.rf.readUnsignedShort();
        this.os_2.usLastCharIndex = this.rf.readUnsignedShort();
        this.os_2.sTypoAscender = this.rf.readShort();
        this.os_2.sTypoDescender = this.rf.readShort();
        if (this.os_2.sTypoDescender > 0) {
            this.os_2.sTypoDescender = (short)(-this.os_2.sTypoDescender);
        }
        this.os_2.sTypoLineGap = this.rf.readShort();
        this.os_2.usWinAscent = this.rf.readUnsignedShort();
        this.os_2.usWinDescent = this.rf.readUnsignedShort();
        this.os_2.ulCodePageRange1 = 0;
        this.os_2.ulCodePageRange2 = 0;
        if (version > 0) {
            this.os_2.ulCodePageRange1 = this.rf.readInt();
            this.os_2.ulCodePageRange2 = this.rf.readInt();
        }
        if (version > 1) {
            this.rf.skipBytes(2);
            this.os_2.sCapHeight = this.rf.readShort();
        }
        else {
            this.os_2.sCapHeight = (int)(0.7 * this.head.unitsPerEm);
        }
        table_location = this.tables.get("post");
        if (table_location == null) {
            this.italicAngle = -Math.atan2(this.hhea.caretSlopeRun, this.hhea.caretSlopeRise) * 180.0 / 3.141592653589793;
            return;
        }
        this.rf.seek(table_location[0] + 4);
        final short mantissa = this.rf.readShort();
        final int fraction = this.rf.readUnsignedShort();
        this.italicAngle = mantissa + fraction / 16384.0;
        this.underlinePosition = this.rf.readShort();
        this.underlineThickness = this.rf.readShort();
        this.isFixedPitch = (this.rf.readInt() != 0);
    }
    
    String getBaseFont() throws DocumentException, IOException {
        final int[] table_location = this.tables.get("name");
        if (table_location == null) {
            throw new DocumentException(MessageLocalization.getComposedMessage("table.1.does.not.exist.in.2", "name", this.fileName + this.style));
        }
        this.rf.seek(table_location[0] + 2);
        final int numRecords = this.rf.readUnsignedShort();
        final int startOfStorage = this.rf.readUnsignedShort();
        int k = 0;
        while (k < numRecords) {
            final int platformID = this.rf.readUnsignedShort();
            final int platformEncodingID = this.rf.readUnsignedShort();
            final int languageID = this.rf.readUnsignedShort();
            final int nameID = this.rf.readUnsignedShort();
            final int length = this.rf.readUnsignedShort();
            final int offset = this.rf.readUnsignedShort();
            if (nameID == 6) {
                this.rf.seek(table_location[0] + startOfStorage + offset);
                if (platformID == 0 || platformID == 3) {
                    return this.readUnicodeString(length);
                }
                return this.readStandardString(length);
            }
            else {
                ++k;
            }
        }
        final File file = new File(this.fileName);
        return file.getName().replace(' ', '-');
    }
    
    String[][] getNames(final int id) throws DocumentException, IOException {
        final int[] table_location = this.tables.get("name");
        if (table_location == null) {
            throw new DocumentException(MessageLocalization.getComposedMessage("table.1.does.not.exist.in.2", "name", this.fileName + this.style));
        }
        this.rf.seek(table_location[0] + 2);
        final int numRecords = this.rf.readUnsignedShort();
        final int startOfStorage = this.rf.readUnsignedShort();
        final ArrayList names = new ArrayList();
        for (int k = 0; k < numRecords; ++k) {
            final int platformID = this.rf.readUnsignedShort();
            final int platformEncodingID = this.rf.readUnsignedShort();
            final int languageID = this.rf.readUnsignedShort();
            final int nameID = this.rf.readUnsignedShort();
            final int length = this.rf.readUnsignedShort();
            final int offset = this.rf.readUnsignedShort();
            if (nameID == id) {
                final int pos = this.rf.getFilePointer();
                this.rf.seek(table_location[0] + startOfStorage + offset);
                String name;
                if (platformID == 0 || platformID == 3 || (platformID == 2 && platformEncodingID == 1)) {
                    name = this.readUnicodeString(length);
                }
                else {
                    name = this.readStandardString(length);
                }
                names.add(new String[] { String.valueOf(platformID), String.valueOf(platformEncodingID), String.valueOf(languageID), name });
                this.rf.seek(pos);
            }
        }
        final String[][] thisName = new String[names.size()][];
        for (int i = 0; i < names.size(); ++i) {
            thisName[i] = names.get(i);
        }
        return thisName;
    }
    
    String[][] getAllNames() throws DocumentException, IOException {
        final int[] table_location = this.tables.get("name");
        if (table_location == null) {
            throw new DocumentException(MessageLocalization.getComposedMessage("table.1.does.not.exist.in.2", "name", this.fileName + this.style));
        }
        this.rf.seek(table_location[0] + 2);
        final int numRecords = this.rf.readUnsignedShort();
        final int startOfStorage = this.rf.readUnsignedShort();
        final ArrayList names = new ArrayList();
        for (int k = 0; k < numRecords; ++k) {
            final int platformID = this.rf.readUnsignedShort();
            final int platformEncodingID = this.rf.readUnsignedShort();
            final int languageID = this.rf.readUnsignedShort();
            final int nameID = this.rf.readUnsignedShort();
            final int length = this.rf.readUnsignedShort();
            final int offset = this.rf.readUnsignedShort();
            final int pos = this.rf.getFilePointer();
            this.rf.seek(table_location[0] + startOfStorage + offset);
            String name;
            if (platformID == 0 || platformID == 3 || (platformID == 2 && platformEncodingID == 1)) {
                name = this.readUnicodeString(length);
            }
            else {
                name = this.readStandardString(length);
            }
            names.add(new String[] { String.valueOf(nameID), String.valueOf(platformID), String.valueOf(platformEncodingID), String.valueOf(languageID), name });
            this.rf.seek(pos);
        }
        final String[][] thisName = new String[names.size()][];
        for (int i = 0; i < names.size(); ++i) {
            thisName[i] = names.get(i);
        }
        return thisName;
    }
    
    void checkCff() {
        final int[] table_location = this.tables.get("CFF ");
        if (table_location != null) {
            this.cff = true;
            this.cffOffset = table_location[0];
            this.cffLength = table_location[1];
        }
    }
    
    void process(final byte[] ttfAfm, final boolean preload) throws DocumentException, IOException {
        this.tables = new HashMap();
        try {
            if (ttfAfm == null) {
                this.rf = new RandomAccessFileOrArray(this.fileName, preload, Document.plainRandomAccess);
            }
            else {
                this.rf = new RandomAccessFileOrArray(ttfAfm);
            }
            if (this.ttcIndex.length() > 0) {
                final int dirIdx = Integer.parseInt(this.ttcIndex);
                if (dirIdx < 0) {
                    throw new DocumentException(MessageLocalization.getComposedMessage("the.font.index.for.1.must.be.positive", this.fileName));
                }
                final String mainTag = this.readStandardString(4);
                if (!mainTag.equals("ttcf")) {
                    throw new DocumentException(MessageLocalization.getComposedMessage("1.is.not.a.valid.ttc.file", this.fileName));
                }
                this.rf.skipBytes(4);
                final int dirCount = this.rf.readInt();
                if (dirIdx >= dirCount) {
                    throw new DocumentException(MessageLocalization.getComposedMessage("the.font.index.for.1.must.be.between.0.and.2.it.was.3", this.fileName, String.valueOf(dirCount - 1), String.valueOf(dirIdx)));
                }
                this.rf.skipBytes(dirIdx * 4);
                this.directoryOffset = this.rf.readInt();
            }
            this.rf.seek(this.directoryOffset);
            final int ttId = this.rf.readInt();
            if (ttId != 65536 && ttId != 1330926671) {
                throw new DocumentException(MessageLocalization.getComposedMessage("1.is.not.a.valid.ttf.or.otf.file", this.fileName));
            }
            final int num_tables = this.rf.readUnsignedShort();
            this.rf.skipBytes(6);
            for (int k = 0; k < num_tables; ++k) {
                final String tag = this.readStandardString(4);
                this.rf.skipBytes(4);
                final int[] table_location = { this.rf.readInt(), this.rf.readInt() };
                this.tables.put(tag, table_location);
            }
            this.checkCff();
            this.fontName = this.getBaseFont();
            this.fullName = this.getNames(4);
            this.familyName = this.getNames(1);
            this.allNameEntries = this.getAllNames();
            if (!this.justNames) {
                this.fillTables();
                this.readGlyphWidths();
                this.readCMaps();
                this.readKerning();
                this.readBbox();
            }
        }
        finally {
            if (this.rf != null) {
                this.rf.close();
                if (!this.embedded) {
                    this.rf = null;
                }
            }
        }
    }
    
    protected String readStandardString(final int length) throws IOException {
        final byte[] buf = new byte[length];
        this.rf.readFully(buf);
        try {
            return new String(buf, "Cp1252");
        }
        catch (final Exception e) {
            throw new ExceptionConverter(e);
        }
    }
    
    protected String readUnicodeString(int length) throws IOException {
        final StringBuffer buf = new StringBuffer();
        length /= 2;
        for (int k = 0; k < length; ++k) {
            buf.append(this.rf.readChar());
        }
        return buf.toString();
    }
    
    protected void readGlyphWidths() throws DocumentException, IOException {
        final int[] table_location = this.tables.get("hmtx");
        if (table_location == null) {
            throw new DocumentException(MessageLocalization.getComposedMessage("table.1.does.not.exist.in.2", "hmtx", this.fileName + this.style));
        }
        this.rf.seek(table_location[0]);
        this.GlyphWidths = new int[this.hhea.numberOfHMetrics];
        for (int k = 0; k < this.hhea.numberOfHMetrics; ++k) {
            this.GlyphWidths[k] = this.rf.readUnsignedShort() * 1000 / this.head.unitsPerEm;
            this.rf.readUnsignedShort();
        }
    }
    
    protected int getGlyphWidth(int glyph) {
        if (glyph >= this.GlyphWidths.length) {
            glyph = this.GlyphWidths.length - 1;
        }
        return this.GlyphWidths[glyph];
    }
    
    private void readBbox() throws DocumentException, IOException {
        int[] tableLocation = this.tables.get("head");
        if (tableLocation == null) {
            throw new DocumentException(MessageLocalization.getComposedMessage("table.1.does.not.exist.in.2", "head", this.fileName + this.style));
        }
        this.rf.seek(tableLocation[0] + 51);
        final boolean locaShortTable = this.rf.readUnsignedShort() == 0;
        tableLocation = this.tables.get("loca");
        if (tableLocation == null) {
            return;
        }
        this.rf.seek(tableLocation[0]);
        int[] locaTable;
        if (locaShortTable) {
            final int entries = tableLocation[1] / 2;
            locaTable = new int[entries];
            for (int k = 0; k < entries; ++k) {
                locaTable[k] = this.rf.readUnsignedShort() * 2;
            }
        }
        else {
            final int entries = tableLocation[1] / 4;
            locaTable = new int[entries];
            for (int k = 0; k < entries; ++k) {
                locaTable[k] = this.rf.readInt();
            }
        }
        tableLocation = this.tables.get("glyf");
        if (tableLocation == null) {
            throw new DocumentException(MessageLocalization.getComposedMessage("table.1.does.not.exist.in.2", "glyf", this.fileName + this.style));
        }
        final int tableGlyphOffset = tableLocation[0];
        this.bboxes = new int[locaTable.length - 1][];
        for (int glyph = 0; glyph < locaTable.length - 1; ++glyph) {
            final int start = locaTable[glyph];
            if (start != locaTable[glyph + 1]) {
                this.rf.seek(tableGlyphOffset + start + 2);
                this.bboxes[glyph] = new int[] { this.rf.readShort() * 1000 / this.head.unitsPerEm, this.rf.readShort() * 1000 / this.head.unitsPerEm, this.rf.readShort() * 1000 / this.head.unitsPerEm, this.rf.readShort() * 1000 / this.head.unitsPerEm };
            }
        }
    }
    
    void readCMaps() throws DocumentException, IOException {
        final int[] table_location = this.tables.get("cmap");
        if (table_location == null) {
            throw new DocumentException(MessageLocalization.getComposedMessage("table.1.does.not.exist.in.2", "cmap", this.fileName + this.style));
        }
        this.rf.seek(table_location[0]);
        this.rf.skipBytes(2);
        final int num_tables = this.rf.readUnsignedShort();
        this.fontSpecific = false;
        int map10 = 0;
        int map11 = 0;
        int map12 = 0;
        int mapExt = 0;
        for (int k = 0; k < num_tables; ++k) {
            final int platId = this.rf.readUnsignedShort();
            final int platSpecId = this.rf.readUnsignedShort();
            final int offset = this.rf.readInt();
            if (platId == 3 && platSpecId == 0) {
                this.fontSpecific = true;
                map12 = offset;
            }
            else if (platId == 3 && platSpecId == 1) {
                map11 = offset;
            }
            else if (platId == 3 && platSpecId == 10) {
                mapExt = offset;
            }
            if (platId == 1 && platSpecId == 0) {
                map10 = offset;
            }
        }
        if (map10 > 0) {
            this.rf.seek(table_location[0] + map10);
            final int format = this.rf.readUnsignedShort();
            switch (format) {
                case 0: {
                    this.cmap10 = this.readFormat0();
                    break;
                }
                case 4: {
                    this.cmap10 = this.readFormat4();
                    break;
                }
                case 6: {
                    this.cmap10 = this.readFormat6();
                    break;
                }
            }
        }
        if (map11 > 0) {
            this.rf.seek(table_location[0] + map11);
            final int format = this.rf.readUnsignedShort();
            if (format == 4) {
                this.cmap31 = this.readFormat4();
            }
        }
        if (map12 > 0) {
            this.rf.seek(table_location[0] + map12);
            final int format = this.rf.readUnsignedShort();
            if (format == 4) {
                this.cmap10 = this.readFormat4();
            }
        }
        if (mapExt > 0) {
            this.rf.seek(table_location[0] + mapExt);
            final int format = this.rf.readUnsignedShort();
            switch (format) {
                case 0: {
                    this.cmapExt = this.readFormat0();
                    break;
                }
                case 4: {
                    this.cmapExt = this.readFormat4();
                    break;
                }
                case 6: {
                    this.cmapExt = this.readFormat6();
                    break;
                }
                case 12: {
                    this.cmapExt = this.readFormat12();
                    break;
                }
            }
        }
    }
    
    HashMap readFormat12() throws IOException {
        final HashMap h = new HashMap();
        this.rf.skipBytes(2);
        final int table_lenght = this.rf.readInt();
        this.rf.skipBytes(4);
        for (int nGroups = this.rf.readInt(), k = 0; k < nGroups; ++k) {
            final int startCharCode = this.rf.readInt();
            final int endCharCode = this.rf.readInt();
            int startGlyphID = this.rf.readInt();
            for (int i = startCharCode; i <= endCharCode; ++i) {
                final int[] r = { startGlyphID, 0 };
                r[1] = this.getGlyphWidth(r[0]);
                h.put(new Integer(i), r);
                ++startGlyphID;
            }
        }
        return h;
    }
    
    HashMap readFormat0() throws IOException {
        final HashMap h = new HashMap();
        this.rf.skipBytes(4);
        for (int k = 0; k < 256; ++k) {
            final int[] r = { this.rf.readUnsignedByte(), 0 };
            r[1] = this.getGlyphWidth(r[0]);
            h.put(new Integer(k), r);
        }
        return h;
    }
    
    HashMap readFormat4() throws IOException {
        final HashMap h = new HashMap();
        final int table_lenght = this.rf.readUnsignedShort();
        this.rf.skipBytes(2);
        final int segCount = this.rf.readUnsignedShort() / 2;
        this.rf.skipBytes(6);
        final int[] endCount = new int[segCount];
        for (int k = 0; k < segCount; ++k) {
            endCount[k] = this.rf.readUnsignedShort();
        }
        this.rf.skipBytes(2);
        final int[] startCount = new int[segCount];
        for (int i = 0; i < segCount; ++i) {
            startCount[i] = this.rf.readUnsignedShort();
        }
        final int[] idDelta = new int[segCount];
        for (int j = 0; j < segCount; ++j) {
            idDelta[j] = this.rf.readUnsignedShort();
        }
        final int[] idRO = new int[segCount];
        for (int l = 0; l < segCount; ++l) {
            idRO[l] = this.rf.readUnsignedShort();
        }
        final int[] glyphId = new int[table_lenght / 2 - 8 - segCount * 4];
        for (int m = 0; m < glyphId.length; ++m) {
            glyphId[m] = this.rf.readUnsignedShort();
        }
        for (int m = 0; m < segCount; ++m) {
            for (int j2 = startCount[m]; j2 <= endCount[m] && j2 != 65535; ++j2) {
                int glyph;
                if (idRO[m] == 0) {
                    glyph = (j2 + idDelta[m] & 0xFFFF);
                }
                else {
                    final int idx = m + idRO[m] / 2 - segCount + j2 - startCount[m];
                    if (idx >= glyphId.length) {
                        continue;
                    }
                    glyph = (glyphId[idx] + idDelta[m] & 0xFFFF);
                }
                final int[] r = { glyph, 0 };
                r[1] = this.getGlyphWidth(r[0]);
                h.put(new Integer(this.fontSpecific ? (((j2 & 0xFF00) == 0xF000) ? (j2 & 0xFF) : j2) : j2), r);
            }
        }
        return h;
    }
    
    HashMap readFormat6() throws IOException {
        final HashMap h = new HashMap();
        this.rf.skipBytes(4);
        final int start_code = this.rf.readUnsignedShort();
        for (int code_count = this.rf.readUnsignedShort(), k = 0; k < code_count; ++k) {
            final int[] r = { this.rf.readUnsignedShort(), 0 };
            r[1] = this.getGlyphWidth(r[0]);
            h.put(new Integer(k + start_code), r);
        }
        return h;
    }
    
    void readKerning() throws IOException {
        final int[] table_location = this.tables.get("kern");
        if (table_location == null) {
            return;
        }
        this.rf.seek(table_location[0] + 2);
        final int nTables = this.rf.readUnsignedShort();
        int checkpoint = table_location[0] + 4;
        int length = 0;
        for (int k = 0; k < nTables; ++k) {
            checkpoint += length;
            this.rf.seek(checkpoint);
            this.rf.skipBytes(2);
            length = this.rf.readUnsignedShort();
            final int coverage = this.rf.readUnsignedShort();
            if ((coverage & 0xFFF7) == 0x1) {
                final int nPairs = this.rf.readUnsignedShort();
                this.rf.skipBytes(6);
                for (int j = 0; j < nPairs; ++j) {
                    final int pair = this.rf.readInt();
                    final int value = this.rf.readShort() * 1000 / this.head.unitsPerEm;
                    this.kerning.put(pair, value);
                }
            }
        }
    }
    
    @Override
    public int getKerning(final int char1, final int char2) {
        int[] metrics = this.getMetricsTT(char1);
        if (metrics == null) {
            return 0;
        }
        final int c1 = metrics[0];
        metrics = this.getMetricsTT(char2);
        if (metrics == null) {
            return 0;
        }
        final int c2 = metrics[0];
        return this.kerning.get((c1 << 16) + c2);
    }
    
    @Override
    int getRawWidth(final int c, final String name) {
        final int[] metric = this.getMetricsTT(c);
        if (metric == null) {
            return 0;
        }
        return metric[1];
    }
    
    protected PdfDictionary getFontDescriptor(final PdfIndirectReference fontStream, final String subsetPrefix, final PdfIndirectReference cidset) {
        final PdfDictionary dic = new PdfDictionary(PdfName.FONTDESCRIPTOR);
        dic.put(PdfName.ASCENT, new PdfNumber(this.os_2.sTypoAscender * 1000 / this.head.unitsPerEm));
        dic.put(PdfName.CAPHEIGHT, new PdfNumber(this.os_2.sCapHeight * 1000 / this.head.unitsPerEm));
        dic.put(PdfName.DESCENT, new PdfNumber(this.os_2.sTypoDescender * 1000 / this.head.unitsPerEm));
        dic.put(PdfName.FONTBBOX, new PdfRectangle((float)(this.head.xMin * 1000 / this.head.unitsPerEm), (float)(this.head.yMin * 1000 / this.head.unitsPerEm), (float)(this.head.xMax * 1000 / this.head.unitsPerEm), (float)(this.head.yMax * 1000 / this.head.unitsPerEm)));
        if (cidset != null) {
            dic.put(PdfName.CIDSET, cidset);
        }
        if (this.cff) {
            if (this.encoding.startsWith("Identity-")) {
                dic.put(PdfName.FONTNAME, new PdfName(subsetPrefix + this.fontName + "-" + this.encoding));
            }
            else {
                dic.put(PdfName.FONTNAME, new PdfName(subsetPrefix + this.fontName + this.style));
            }
        }
        else {
            dic.put(PdfName.FONTNAME, new PdfName(subsetPrefix + this.fontName + this.style));
        }
        dic.put(PdfName.ITALICANGLE, new PdfNumber(this.italicAngle));
        dic.put(PdfName.STEMV, new PdfNumber(80));
        if (fontStream != null) {
            if (this.cff) {
                dic.put(PdfName.FONTFILE3, fontStream);
            }
            else {
                dic.put(PdfName.FONTFILE2, fontStream);
            }
        }
        int flags = 0;
        if (this.isFixedPitch) {
            flags |= 0x1;
        }
        flags |= (this.fontSpecific ? 4 : 32);
        if ((this.head.macStyle & 0x2) != 0x0) {
            flags |= 0x40;
        }
        if ((this.head.macStyle & 0x1) != 0x0) {
            flags |= 0x40000;
        }
        dic.put(PdfName.FLAGS, new PdfNumber(flags));
        return dic;
    }
    
    protected PdfDictionary getFontBaseType(final PdfIndirectReference fontDescriptor, final String subsetPrefix, int firstChar, final int lastChar, final byte[] shortTag) {
        final PdfDictionary dic = new PdfDictionary(PdfName.FONT);
        if (this.cff) {
            dic.put(PdfName.SUBTYPE, PdfName.TYPE1);
            dic.put(PdfName.BASEFONT, new PdfName(this.fontName + this.style));
        }
        else {
            dic.put(PdfName.SUBTYPE, PdfName.TRUETYPE);
            dic.put(PdfName.BASEFONT, new PdfName(subsetPrefix + this.fontName + this.style));
        }
        dic.put(PdfName.BASEFONT, new PdfName(subsetPrefix + this.fontName + this.style));
        if (!this.fontSpecific) {
            for (int k = firstChar; k <= lastChar; ++k) {
                if (!this.differences[k].equals(".notdef")) {
                    firstChar = k;
                    break;
                }
            }
            if (this.encoding.equals("Cp1252") || this.encoding.equals("MacRoman")) {
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
        if (fontDescriptor != null) {
            dic.put(PdfName.FONTDESCRIPTOR, fontDescriptor);
        }
        return dic;
    }
    
    protected byte[] getFullFont() throws IOException {
        RandomAccessFileOrArray rf2 = null;
        try {
            rf2 = new RandomAccessFileOrArray(this.rf);
            rf2.reOpen();
            final byte[] b = new byte[rf2.length()];
            rf2.readFully(b);
            return b;
        }
        finally {
            try {
                if (rf2 != null) {
                    rf2.close();
                }
            }
            catch (final Exception ex) {}
        }
    }
    
    protected static int[] compactRanges(final ArrayList ranges) {
        final ArrayList simp = new ArrayList();
        for (int k = 0; k < ranges.size(); ++k) {
            final int[] r = ranges.get(k);
            for (int j = 0; j < r.length; j += 2) {
                simp.add(new int[] { Math.max(0, Math.min(r[j], r[j + 1])), Math.min(65535, Math.max(r[j], r[j + 1])) });
            }
        }
        for (int k2 = 0; k2 < simp.size() - 1; ++k2) {
            for (int k3 = k2 + 1; k3 < simp.size(); ++k3) {
                final int[] r2 = simp.get(k2);
                final int[] r3 = simp.get(k3);
                if ((r2[0] >= r3[0] && r2[0] <= r3[1]) || (r2[1] >= r3[0] && r2[0] <= r3[1])) {
                    r2[0] = Math.min(r2[0], r3[0]);
                    r2[1] = Math.max(r2[1], r3[1]);
                    simp.remove(k3);
                    --k3;
                }
            }
        }
        final int[] s = new int[simp.size() * 2];
        for (int i = 0; i < simp.size(); ++i) {
            final int[] r4 = simp.get(i);
            s[i * 2] = r4[0];
            s[i * 2 + 1] = r4[1];
        }
        return s;
    }
    
    protected void addRangeUni(final HashMap longTag, final boolean includeMetrics, final boolean subsetp) {
        if (!subsetp && (this.subsetRanges != null || this.directoryOffset > 0)) {
            final int[] rg = (this.subsetRanges == null && this.directoryOffset > 0) ? new int[] { 0, 65535 } : compactRanges(this.subsetRanges);
            HashMap usemap;
            if (!this.fontSpecific && this.cmap31 != null) {
                usemap = this.cmap31;
            }
            else if (this.fontSpecific && this.cmap10 != null) {
                usemap = this.cmap10;
            }
            else if (this.cmap31 != null) {
                usemap = this.cmap31;
            }
            else {
                usemap = this.cmap10;
            }
            for (final Map.Entry e : usemap.entrySet()) {
                final int[] v = e.getValue();
                final Integer gi = new Integer(v[0]);
                if (longTag.containsKey(gi)) {
                    continue;
                }
                final int c = e.getKey();
                boolean skip = true;
                for (int k = 0; k < rg.length; k += 2) {
                    if (c >= rg[k] && c <= rg[k + 1]) {
                        skip = false;
                        break;
                    }
                }
                if (skip) {
                    continue;
                }
                longTag.put(gi, includeMetrics ? new int[] { v[0], v[1], c } : null);
            }
        }
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
        String subsetPrefix = "";
        if (this.embedded) {
            if (this.cff) {
                pobj = new StreamFont(this.readCffFont(), "Type1C", this.compressionLevel);
                obj = writer.addToBody(pobj);
                ind_font = obj.getIndirectReference();
            }
            else {
                if (subsetp) {
                    subsetPrefix = BaseFont.createSubsetPrefix();
                }
                final HashMap glyphs = new HashMap();
                for (int i = firstChar; i <= lastChar; ++i) {
                    if (shortTag[i] != 0) {
                        int[] metrics = null;
                        if (this.specialMap != null) {
                            final int[] cd = GlyphList.nameToUnicode(this.differences[i]);
                            if (cd != null) {
                                metrics = this.getMetricsTT(cd[0]);
                            }
                        }
                        else if (this.fontSpecific) {
                            metrics = this.getMetricsTT(i);
                        }
                        else {
                            metrics = this.getMetricsTT(this.unicodeDifferences[i]);
                        }
                        if (metrics != null) {
                            glyphs.put(new Integer(metrics[0]), null);
                        }
                    }
                }
                this.addRangeUni(glyphs, false, subsetp);
                byte[] b = null;
                if (subsetp || this.directoryOffset != 0 || this.subsetRanges != null) {
                    final TrueTypeFontSubSet sb = new TrueTypeFontSubSet(this.fileName, new RandomAccessFileOrArray(this.rf), glyphs, this.directoryOffset, true, !subsetp);
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
        }
        pobj = this.getFontDescriptor(ind_font, subsetPrefix, null);
        if (pobj != null) {
            obj = writer.addToBody(pobj);
            ind_font = obj.getIndirectReference();
        }
        pobj = this.getFontBaseType(ind_font, subsetPrefix, firstChar, lastChar, shortTag);
        writer.addToBody(pobj, ref);
    }
    
    protected byte[] readCffFont() throws IOException {
        final RandomAccessFileOrArray rf2 = new RandomAccessFileOrArray(this.rf);
        final byte[] b = new byte[this.cffLength];
        try {
            rf2.reOpen();
            rf2.seek(this.cffOffset);
            rf2.readFully(b);
        }
        finally {
            try {
                rf2.close();
            }
            catch (final Exception ex) {}
        }
        return b;
    }
    
    public PdfStream getFullFontStream() throws IOException, DocumentException {
        if (this.cff) {
            return new StreamFont(this.readCffFont(), "Type1C", this.compressionLevel);
        }
        final byte[] b = this.getFullFont();
        final int[] lengths = { b.length };
        return new StreamFont(b, lengths, this.compressionLevel);
    }
    
    @Override
    public float getFontDescriptor(final int key, final float fontSize) {
        switch (key) {
            case 1: {
                return this.os_2.sTypoAscender * fontSize / this.head.unitsPerEm;
            }
            case 2: {
                return this.os_2.sCapHeight * fontSize / this.head.unitsPerEm;
            }
            case 3: {
                return this.os_2.sTypoDescender * fontSize / this.head.unitsPerEm;
            }
            case 4: {
                return (float)this.italicAngle;
            }
            case 5: {
                return fontSize * this.head.xMin / this.head.unitsPerEm;
            }
            case 6: {
                return fontSize * this.head.yMin / this.head.unitsPerEm;
            }
            case 7: {
                return fontSize * this.head.xMax / this.head.unitsPerEm;
            }
            case 8: {
                return fontSize * this.head.yMax / this.head.unitsPerEm;
            }
            case 9: {
                return fontSize * this.hhea.Ascender / this.head.unitsPerEm;
            }
            case 10: {
                return fontSize * this.hhea.Descender / this.head.unitsPerEm;
            }
            case 11: {
                return fontSize * this.hhea.LineGap / this.head.unitsPerEm;
            }
            case 12: {
                return fontSize * this.hhea.advanceWidthMax / this.head.unitsPerEm;
            }
            case 13: {
                return (this.underlinePosition - this.underlineThickness / 2) * fontSize / this.head.unitsPerEm;
            }
            case 14: {
                return this.underlineThickness * fontSize / this.head.unitsPerEm;
            }
            case 15: {
                return this.os_2.yStrikeoutPosition * fontSize / this.head.unitsPerEm;
            }
            case 16: {
                return this.os_2.yStrikeoutSize * fontSize / this.head.unitsPerEm;
            }
            case 17: {
                return this.os_2.ySubscriptYSize * fontSize / this.head.unitsPerEm;
            }
            case 18: {
                return -this.os_2.ySubscriptYOffset * fontSize / this.head.unitsPerEm;
            }
            case 19: {
                return this.os_2.ySuperscriptYSize * fontSize / this.head.unitsPerEm;
            }
            case 20: {
                return this.os_2.ySuperscriptYOffset * fontSize / this.head.unitsPerEm;
            }
            default: {
                return 0.0f;
            }
        }
    }
    
    public int[] getMetricsTT(final int c) {
        if (this.cmapExt != null) {
            return this.cmapExt.get(new Integer(c));
        }
        if (!this.fontSpecific && this.cmap31 != null) {
            return this.cmap31.get(new Integer(c));
        }
        if (this.fontSpecific && this.cmap10 != null) {
            return this.cmap10.get(new Integer(c));
        }
        if (this.cmap31 != null) {
            return this.cmap31.get(new Integer(c));
        }
        if (this.cmap10 != null) {
            return this.cmap10.get(new Integer(c));
        }
        return null;
    }
    
    @Override
    public String getPostscriptFontName() {
        return this.fontName;
    }
    
    @Override
    public String[] getCodePagesSupported() {
        final long cp = ((long)this.os_2.ulCodePageRange2 << 32) + ((long)this.os_2.ulCodePageRange1 & 0xFFFFFFFFL);
        int count = 0;
        long bit = 1L;
        for (int k = 0; k < 64; ++k) {
            if ((cp & bit) != 0x0L && TrueTypeFont.codePages[k] != null) {
                ++count;
            }
            bit <<= 1;
        }
        final String[] ret = new String[count];
        count = 0;
        bit = 1L;
        for (int i = 0; i < 64; ++i) {
            if ((cp & bit) != 0x0L && TrueTypeFont.codePages[i] != null) {
                ret[count++] = TrueTypeFont.codePages[i];
            }
            bit <<= 1;
        }
        return ret;
    }
    
    @Override
    public String[][] getFullFontName() {
        return this.fullName;
    }
    
    @Override
    public String[][] getAllNameEntries() {
        return this.allNameEntries;
    }
    
    @Override
    public String[][] getFamilyFontName() {
        return this.familyName;
    }
    
    @Override
    public boolean hasKernPairs() {
        return this.kerning.size() > 0;
    }
    
    @Override
    public void setPostscriptFontName(final String name) {
        this.fontName = name;
    }
    
    @Override
    public boolean setKerning(final int char1, final int char2, final int kern) {
        int[] metrics = this.getMetricsTT(char1);
        if (metrics == null) {
            return false;
        }
        final int c1 = metrics[0];
        metrics = this.getMetricsTT(char2);
        if (metrics == null) {
            return false;
        }
        final int c2 = metrics[0];
        this.kerning.put((c1 << 16) + c2, kern);
        return true;
    }
    
    @Override
    protected int[] getRawCharBBox(final int c, final String name) {
        HashMap map = null;
        if (name == null || this.cmap31 == null) {
            map = this.cmap10;
        }
        else {
            map = this.cmap31;
        }
        if (map == null) {
            return null;
        }
        final int[] metric = map.get(new Integer(c));
        if (metric == null || this.bboxes == null) {
            return null;
        }
        return this.bboxes[metric[0]];
    }
    
    static {
        codePages = new String[] { "1252 Latin 1", "1250 Latin 2: Eastern Europe", "1251 Cyrillic", "1253 Greek", "1254 Turkish", "1255 Hebrew", "1256 Arabic", "1257 Windows Baltic", "1258 Vietnamese", null, null, null, null, null, null, null, "874 Thai", "932 JIS/Japan", "936 Chinese: Simplified chars--PRC and Singapore", "949 Korean Wansung", "950 Chinese: Traditional chars--Taiwan and Hong Kong", "1361 Korean Johab", null, null, null, null, null, null, null, "Macintosh Character Set (US Roman)", "OEM Character Set", "Symbol Character Set", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, "869 IBM Greek", "866 MS-DOS Russian", "865 MS-DOS Nordic", "864 Arabic", "863 MS-DOS Canadian French", "862 Hebrew", "861 MS-DOS Icelandic", "860 MS-DOS Portuguese", "857 IBM Turkish", "855 IBM Cyrillic; primarily Russian", "852 Latin 2", "775 MS-DOS Baltic", "737 Greek; former 437 G", "708 Arabic; ASMO 708", "850 WE/Latin 1", "437 US" };
    }
    
    protected static class FontHeader
    {
        int flags;
        int unitsPerEm;
        short xMin;
        short yMin;
        short xMax;
        short yMax;
        int macStyle;
    }
    
    protected static class HorizontalHeader
    {
        short Ascender;
        short Descender;
        short LineGap;
        int advanceWidthMax;
        short minLeftSideBearing;
        short minRightSideBearing;
        short xMaxExtent;
        short caretSlopeRise;
        short caretSlopeRun;
        int numberOfHMetrics;
    }
    
    protected static class WindowsMetrics
    {
        short xAvgCharWidth;
        int usWeightClass;
        int usWidthClass;
        short fsType;
        short ySubscriptXSize;
        short ySubscriptYSize;
        short ySubscriptXOffset;
        short ySubscriptYOffset;
        short ySuperscriptXSize;
        short ySuperscriptYSize;
        short ySuperscriptXOffset;
        short ySuperscriptYOffset;
        short yStrikeoutSize;
        short yStrikeoutPosition;
        short sFamilyClass;
        byte[] panose;
        byte[] achVendID;
        int fsSelection;
        int usFirstCharIndex;
        int usLastCharIndex;
        short sTypoAscender;
        short sTypoDescender;
        short sTypoLineGap;
        int usWinAscent;
        int usWinDescent;
        int ulCodePageRange1;
        int ulCodePageRange2;
        int sCapHeight;
        
        protected WindowsMetrics() {
            this.panose = new byte[10];
            this.achVendID = new byte[4];
        }
    }
}
