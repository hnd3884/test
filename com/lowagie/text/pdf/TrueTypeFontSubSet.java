package com.lowagie.text.pdf;

import com.lowagie.text.ExceptionConverter;
import java.util.Arrays;
import com.lowagie.text.error_messages.MessageLocalization;
import com.lowagie.text.DocumentException;
import java.io.IOException;
import java.util.Collection;
import java.util.ArrayList;
import java.util.HashMap;

class TrueTypeFontSubSet
{
    static final String[] tableNamesSimple;
    static final String[] tableNamesCmap;
    static final String[] tableNamesExtra;
    static final int[] entrySelectors;
    static final int TABLE_CHECKSUM = 0;
    static final int TABLE_OFFSET = 1;
    static final int TABLE_LENGTH = 2;
    static final int HEAD_LOCA_FORMAT_OFFSET = 51;
    static final int ARG_1_AND_2_ARE_WORDS = 1;
    static final int WE_HAVE_A_SCALE = 8;
    static final int MORE_COMPONENTS = 32;
    static final int WE_HAVE_AN_X_AND_Y_SCALE = 64;
    static final int WE_HAVE_A_TWO_BY_TWO = 128;
    protected HashMap tableDirectory;
    protected RandomAccessFileOrArray rf;
    protected String fileName;
    protected boolean includeCmap;
    protected boolean includeExtras;
    protected boolean locaShortTable;
    protected int[] locaTable;
    protected HashMap glyphsUsed;
    protected ArrayList glyphsInList;
    protected int tableGlyphOffset;
    protected int[] newLocaTable;
    protected byte[] newLocaTableOut;
    protected byte[] newGlyfTable;
    protected int glyfTableRealSize;
    protected int locaTableRealSize;
    protected byte[] outFont;
    protected int fontPtr;
    protected int directoryOffset;
    
    TrueTypeFontSubSet(final String fileName, final RandomAccessFileOrArray rf, final HashMap glyphsUsed, final int directoryOffset, final boolean includeCmap, final boolean includeExtras) {
        this.fileName = fileName;
        this.rf = rf;
        this.glyphsUsed = glyphsUsed;
        this.includeCmap = includeCmap;
        this.includeExtras = includeExtras;
        this.directoryOffset = directoryOffset;
        this.glyphsInList = new ArrayList(glyphsUsed.keySet());
    }
    
    byte[] process() throws IOException, DocumentException {
        try {
            this.rf.reOpen();
            this.createTableDirectory();
            this.readLoca();
            this.flatGlyphs();
            this.createNewGlyphTables();
            this.locaTobytes();
            this.assembleFont();
            return this.outFont;
        }
        finally {
            try {
                this.rf.close();
            }
            catch (final Exception ex) {}
        }
    }
    
    protected void assembleFont() throws IOException {
        int fullFontSize = 0;
        String[] tableNames;
        if (this.includeExtras) {
            tableNames = TrueTypeFontSubSet.tableNamesExtra;
        }
        else if (this.includeCmap) {
            tableNames = TrueTypeFontSubSet.tableNamesCmap;
        }
        else {
            tableNames = TrueTypeFontSubSet.tableNamesSimple;
        }
        int tablesUsed = 2;
        int len = 0;
        for (int k = 0; k < tableNames.length; ++k) {
            final String name = tableNames[k];
            if (!name.equals("glyf")) {
                if (!name.equals("loca")) {
                    final int[] tableLocation = this.tableDirectory.get(name);
                    if (tableLocation != null) {
                        ++tablesUsed;
                        fullFontSize += (tableLocation[2] + 3 & 0xFFFFFFFC);
                    }
                }
            }
        }
        fullFontSize += this.newLocaTableOut.length;
        fullFontSize += this.newGlyfTable.length;
        int ref = 16 * tablesUsed + 12;
        fullFontSize += ref;
        this.outFont = new byte[fullFontSize];
        this.fontPtr = 0;
        this.writeFontInt(65536);
        this.writeFontShort(tablesUsed);
        final int selector = TrueTypeFontSubSet.entrySelectors[tablesUsed];
        this.writeFontShort((1 << selector) * 16);
        this.writeFontShort(selector);
        this.writeFontShort((tablesUsed - (1 << selector)) * 16);
        for (int i = 0; i < tableNames.length; ++i) {
            final String name2 = tableNames[i];
            final int[] tableLocation = this.tableDirectory.get(name2);
            if (tableLocation != null) {
                this.writeFontString(name2);
                if (name2.equals("glyf")) {
                    this.writeFontInt(this.calculateChecksum(this.newGlyfTable));
                    len = this.glyfTableRealSize;
                }
                else if (name2.equals("loca")) {
                    this.writeFontInt(this.calculateChecksum(this.newLocaTableOut));
                    len = this.locaTableRealSize;
                }
                else {
                    this.writeFontInt(tableLocation[0]);
                    len = tableLocation[2];
                }
                this.writeFontInt(ref);
                this.writeFontInt(len);
                ref += (len + 3 & 0xFFFFFFFC);
            }
        }
        for (int i = 0; i < tableNames.length; ++i) {
            final String name2 = tableNames[i];
            final int[] tableLocation = this.tableDirectory.get(name2);
            if (tableLocation != null) {
                if (name2.equals("glyf")) {
                    System.arraycopy(this.newGlyfTable, 0, this.outFont, this.fontPtr, this.newGlyfTable.length);
                    this.fontPtr += this.newGlyfTable.length;
                    this.newGlyfTable = null;
                }
                else if (name2.equals("loca")) {
                    System.arraycopy(this.newLocaTableOut, 0, this.outFont, this.fontPtr, this.newLocaTableOut.length);
                    this.fontPtr += this.newLocaTableOut.length;
                    this.newLocaTableOut = null;
                }
                else {
                    this.rf.seek(tableLocation[1]);
                    this.rf.readFully(this.outFont, this.fontPtr, tableLocation[2]);
                    this.fontPtr += (tableLocation[2] + 3 & 0xFFFFFFFC);
                }
            }
        }
    }
    
    protected void createTableDirectory() throws IOException, DocumentException {
        this.tableDirectory = new HashMap();
        this.rf.seek(this.directoryOffset);
        final int id = this.rf.readInt();
        if (id != 65536) {
            throw new DocumentException(MessageLocalization.getComposedMessage("1.is.not.a.true.type.file", this.fileName));
        }
        final int num_tables = this.rf.readUnsignedShort();
        this.rf.skipBytes(6);
        for (int k = 0; k < num_tables; ++k) {
            final String tag = this.readStandardString(4);
            final int[] tableLocation = { this.rf.readInt(), this.rf.readInt(), this.rf.readInt() };
            this.tableDirectory.put(tag, tableLocation);
        }
    }
    
    protected void readLoca() throws IOException, DocumentException {
        int[] tableLocation = this.tableDirectory.get("head");
        if (tableLocation == null) {
            throw new DocumentException(MessageLocalization.getComposedMessage("table.1.does.not.exist.in.2", "head", this.fileName));
        }
        this.rf.seek(tableLocation[1] + 51);
        this.locaShortTable = (this.rf.readUnsignedShort() == 0);
        tableLocation = this.tableDirectory.get("loca");
        if (tableLocation == null) {
            throw new DocumentException(MessageLocalization.getComposedMessage("table.1.does.not.exist.in.2", "loca", this.fileName));
        }
        this.rf.seek(tableLocation[1]);
        if (this.locaShortTable) {
            final int entries = tableLocation[2] / 2;
            this.locaTable = new int[entries];
            for (int k = 0; k < entries; ++k) {
                this.locaTable[k] = this.rf.readUnsignedShort() * 2;
            }
        }
        else {
            final int entries = tableLocation[2] / 4;
            this.locaTable = new int[entries];
            for (int k = 0; k < entries; ++k) {
                this.locaTable[k] = this.rf.readInt();
            }
        }
    }
    
    protected void createNewGlyphTables() throws IOException {
        this.newLocaTable = new int[this.locaTable.length];
        final int[] activeGlyphs = new int[this.glyphsInList.size()];
        for (int k = 0; k < activeGlyphs.length; ++k) {
            activeGlyphs[k] = this.glyphsInList.get(k);
        }
        Arrays.sort(activeGlyphs);
        int glyfSize = 0;
        for (int i = 0; i < activeGlyphs.length; ++i) {
            final int glyph = activeGlyphs[i];
            glyfSize += this.locaTable[glyph + 1] - this.locaTable[glyph];
        }
        this.glyfTableRealSize = glyfSize;
        glyfSize = (glyfSize + 3 & 0xFFFFFFFC);
        this.newGlyfTable = new byte[glyfSize];
        int glyfPtr = 0;
        int listGlyf = 0;
        for (int j = 0; j < this.newLocaTable.length; ++j) {
            this.newLocaTable[j] = glyfPtr;
            if (listGlyf < activeGlyphs.length && activeGlyphs[listGlyf] == j) {
                ++listGlyf;
                this.newLocaTable[j] = glyfPtr;
                final int start = this.locaTable[j];
                final int len = this.locaTable[j + 1] - start;
                if (len > 0) {
                    this.rf.seek(this.tableGlyphOffset + start);
                    this.rf.readFully(this.newGlyfTable, glyfPtr, len);
                    glyfPtr += len;
                }
            }
        }
    }
    
    protected void locaTobytes() {
        if (this.locaShortTable) {
            this.locaTableRealSize = this.newLocaTable.length * 2;
        }
        else {
            this.locaTableRealSize = this.newLocaTable.length * 4;
        }
        this.newLocaTableOut = new byte[this.locaTableRealSize + 3 & 0xFFFFFFFC];
        this.outFont = this.newLocaTableOut;
        this.fontPtr = 0;
        for (int k = 0; k < this.newLocaTable.length; ++k) {
            if (this.locaShortTable) {
                this.writeFontShort(this.newLocaTable[k] / 2);
            }
            else {
                this.writeFontInt(this.newLocaTable[k]);
            }
        }
    }
    
    protected void flatGlyphs() throws IOException, DocumentException {
        final int[] tableLocation = this.tableDirectory.get("glyf");
        if (tableLocation == null) {
            throw new DocumentException(MessageLocalization.getComposedMessage("table.1.does.not.exist.in.2", "glyf", this.fileName));
        }
        final Integer glyph0 = new Integer(0);
        if (!this.glyphsUsed.containsKey(glyph0)) {
            this.glyphsUsed.put(glyph0, null);
            this.glyphsInList.add(glyph0);
        }
        this.tableGlyphOffset = tableLocation[1];
        for (int k = 0; k < this.glyphsInList.size(); ++k) {
            final int glyph2 = this.glyphsInList.get(k);
            this.checkGlyphComposite(glyph2);
        }
    }
    
    protected void checkGlyphComposite(final int glyph) throws IOException {
        final int start = this.locaTable[glyph];
        if (start == this.locaTable[glyph + 1]) {
            return;
        }
        this.rf.seek(this.tableGlyphOffset + start);
        final int numContours = this.rf.readShort();
        if (numContours >= 0) {
            return;
        }
        this.rf.skipBytes(8);
        while (true) {
            final int flags = this.rf.readUnsignedShort();
            final Integer cGlyph = new Integer(this.rf.readUnsignedShort());
            if (!this.glyphsUsed.containsKey(cGlyph)) {
                this.glyphsUsed.put(cGlyph, null);
                this.glyphsInList.add(cGlyph);
            }
            if ((flags & 0x20) == 0x0) {
                break;
            }
            int skip;
            if ((flags & 0x1) != 0x0) {
                skip = 4;
            }
            else {
                skip = 2;
            }
            if ((flags & 0x8) != 0x0) {
                skip += 2;
            }
            else if ((flags & 0x40) != 0x0) {
                skip += 4;
            }
            if ((flags & 0x80) != 0x0) {
                skip += 8;
            }
            this.rf.skipBytes(skip);
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
    
    protected void writeFontShort(final int n) {
        this.outFont[this.fontPtr++] = (byte)(n >> 8);
        this.outFont[this.fontPtr++] = (byte)n;
    }
    
    protected void writeFontInt(final int n) {
        this.outFont[this.fontPtr++] = (byte)(n >> 24);
        this.outFont[this.fontPtr++] = (byte)(n >> 16);
        this.outFont[this.fontPtr++] = (byte)(n >> 8);
        this.outFont[this.fontPtr++] = (byte)n;
    }
    
    protected void writeFontString(final String s) {
        final byte[] b = PdfEncodings.convertToBytes(s, "Cp1252");
        System.arraycopy(b, 0, this.outFont, this.fontPtr, b.length);
        this.fontPtr += b.length;
    }
    
    protected int calculateChecksum(final byte[] b) {
        final int len = b.length / 4;
        int v0 = 0;
        int v2 = 0;
        int v3 = 0;
        int v4 = 0;
        int ptr = 0;
        for (int k = 0; k < len; ++k) {
            v4 += (b[ptr++] & 0xFF);
            v3 += (b[ptr++] & 0xFF);
            v2 += (b[ptr++] & 0xFF);
            v0 += (b[ptr++] & 0xFF);
        }
        return v0 + (v2 << 8) + (v3 << 16) + (v4 << 24);
    }
    
    static {
        tableNamesSimple = new String[] { "cvt ", "fpgm", "glyf", "head", "hhea", "hmtx", "loca", "maxp", "prep" };
        tableNamesCmap = new String[] { "cmap", "cvt ", "fpgm", "glyf", "head", "hhea", "hmtx", "loca", "maxp", "prep" };
        tableNamesExtra = new String[] { "OS/2", "cmap", "cvt ", "fpgm", "glyf", "head", "hhea", "hmtx", "loca", "maxp", "name, prep" };
        entrySelectors = new int[] { 0, 0, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3, 3, 3, 3, 3, 4, 4, 4, 4, 4 };
    }
}
