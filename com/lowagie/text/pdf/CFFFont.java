package com.lowagie.text.pdf;

import java.util.Iterator;
import java.util.LinkedList;
import com.lowagie.text.ExceptionConverter;

public class CFFFont
{
    static final String[] operatorNames;
    static final String[] standardStrings;
    int nextIndexOffset;
    protected String key;
    protected Object[] args;
    protected int arg_count;
    protected RandomAccessFileOrArray buf;
    private int offSize;
    protected int nameIndexOffset;
    protected int topdictIndexOffset;
    protected int stringIndexOffset;
    protected int gsubrIndexOffset;
    protected int[] nameOffsets;
    protected int[] topdictOffsets;
    protected int[] stringOffsets;
    protected int[] gsubrOffsets;
    protected Font[] fonts;
    
    public String getString(final char sid) {
        if (sid < CFFFont.standardStrings.length) {
            return CFFFont.standardStrings[sid];
        }
        if (sid >= CFFFont.standardStrings.length + (this.stringOffsets.length - 1)) {
            return null;
        }
        final int j = sid - CFFFont.standardStrings.length;
        final int p = this.getPosition();
        this.seek(this.stringOffsets[j]);
        final StringBuffer s = new StringBuffer();
        for (int k = this.stringOffsets[j]; k < this.stringOffsets[j + 1]; ++k) {
            s.append(this.getCard8());
        }
        this.seek(p);
        return s.toString();
    }
    
    char getCard8() {
        try {
            final byte i = this.buf.readByte();
            return (char)(i & 0xFF);
        }
        catch (final Exception e) {
            throw new ExceptionConverter(e);
        }
    }
    
    char getCard16() {
        try {
            return this.buf.readChar();
        }
        catch (final Exception e) {
            throw new ExceptionConverter(e);
        }
    }
    
    int getOffset(final int offSize) {
        int offset = 0;
        for (int i = 0; i < offSize; ++i) {
            offset *= 256;
            offset += this.getCard8();
        }
        return offset;
    }
    
    void seek(final int offset) {
        try {
            this.buf.seek(offset);
        }
        catch (final Exception e) {
            throw new ExceptionConverter(e);
        }
    }
    
    short getShort() {
        try {
            return this.buf.readShort();
        }
        catch (final Exception e) {
            throw new ExceptionConverter(e);
        }
    }
    
    int getInt() {
        try {
            return this.buf.readInt();
        }
        catch (final Exception e) {
            throw new ExceptionConverter(e);
        }
    }
    
    int getPosition() {
        try {
            return this.buf.getFilePointer();
        }
        catch (final Exception e) {
            throw new ExceptionConverter(e);
        }
    }
    
    int[] getIndex(int nextIndexOffset) {
        this.seek(nextIndexOffset);
        final int count = this.getCard16();
        final int[] offsets = new int[count + 1];
        if (count == 0) {
            offsets[0] = -1;
            nextIndexOffset += 2;
            return offsets;
        }
        final int indexOffSize = this.getCard8();
        for (int j = 0; j <= count; ++j) {
            offsets[j] = nextIndexOffset + 2 + 1 + (count + 1) * indexOffSize - 1 + this.getOffset(indexOffSize);
        }
        return offsets;
    }
    
    protected void getDictItem() {
        for (int i = 0; i < this.arg_count; ++i) {
            this.args[i] = null;
        }
        this.arg_count = 0;
        this.key = null;
        boolean gotKey = false;
        while (!gotKey) {
            final char b0 = this.getCard8();
            if (b0 == '\u001d') {
                final int item = this.getInt();
                this.args[this.arg_count] = new Integer(item);
                ++this.arg_count;
            }
            else if (b0 == '\u001c') {
                final short item2 = this.getShort();
                this.args[this.arg_count] = new Integer(item2);
                ++this.arg_count;
            }
            else if (b0 >= ' ' && b0 <= '\u00f6') {
                final byte item3 = (byte)(b0 - '\u008b');
                this.args[this.arg_count] = new Integer(item3);
                ++this.arg_count;
            }
            else if (b0 >= '\u00f7' && b0 <= '\u00fa') {
                final char b2 = this.getCard8();
                final short item4 = (short)((b0 - '\u00f7') * 256 + b2 + 108);
                this.args[this.arg_count] = new Integer(item4);
                ++this.arg_count;
            }
            else if (b0 >= '\u00fb' && b0 <= '\u00fe') {
                final char b2 = this.getCard8();
                final short item4 = (short)(-(b0 - '\u00fb') * 256 - b2 - 108);
                this.args[this.arg_count] = new Integer(item4);
                ++this.arg_count;
            }
            else if (b0 == '\u001e') {
                String item5 = "";
                boolean done = false;
                char buffer = '\0';
                byte avail = 0;
                int nibble = 0;
                while (!done) {
                    if (avail == 0) {
                        buffer = this.getCard8();
                        avail = 2;
                    }
                    if (avail == 1) {
                        nibble = buffer / '\u0010';
                        --avail;
                    }
                    if (avail == 2) {
                        nibble = buffer % '\u0010';
                        --avail;
                    }
                    switch (nibble) {
                        case 10: {
                            item5 += ".";
                            continue;
                        }
                        case 11: {
                            item5 += "E";
                            continue;
                        }
                        case 12: {
                            item5 += "E-";
                            continue;
                        }
                        case 14: {
                            item5 += "-";
                            continue;
                        }
                        case 15: {
                            done = true;
                            continue;
                        }
                        default: {
                            if (nibble >= 0 && nibble <= 9) {
                                item5 += String.valueOf(nibble);
                                continue;
                            }
                            item5 = item5 + "<NIBBLE ERROR: " + nibble + '>';
                            done = true;
                            continue;
                        }
                    }
                }
                this.args[this.arg_count] = item5;
                ++this.arg_count;
            }
            else {
                if (b0 > '\u0015') {
                    continue;
                }
                gotKey = true;
                if (b0 != '\f') {
                    this.key = CFFFont.operatorNames[b0];
                }
                else {
                    this.key = CFFFont.operatorNames[' ' + this.getCard8()];
                }
            }
        }
    }
    
    protected RangeItem getEntireIndexRange(final int indexOffset) {
        this.seek(indexOffset);
        final int count = this.getCard16();
        if (count == 0) {
            return new RangeItem(this.buf, indexOffset, 2);
        }
        final int indexOffSize = this.getCard8();
        this.seek(indexOffset + 2 + 1 + count * indexOffSize);
        final int size = this.getOffset(indexOffSize) - 1;
        return new RangeItem(this.buf, indexOffset, 3 + (count + 1) * indexOffSize + size);
    }
    
    public byte[] getCID(final String fontName) {
        int j;
        for (j = 0; j < this.fonts.length && !fontName.equals(this.fonts[j].name); ++j) {}
        if (j == this.fonts.length) {
            return null;
        }
        final LinkedList l = new LinkedList();
        this.seek(0);
        final int major = this.getCard8();
        final int minor = this.getCard8();
        final int hdrSize = this.getCard8();
        final int offSize = this.getCard8();
        this.nextIndexOffset = hdrSize;
        l.addLast(new RangeItem(this.buf, 0, hdrSize));
        int nglyphs = -1;
        int nstrings = -1;
        if (!this.fonts[j].isCID) {
            this.seek(this.fonts[j].charstringsOffset);
            nglyphs = this.getCard16();
            this.seek(this.stringIndexOffset);
            nstrings = this.getCard16() + CFFFont.standardStrings.length;
        }
        l.addLast(new UInt16Item('\u0001'));
        l.addLast(new UInt8Item('\u0001'));
        l.addLast(new UInt8Item('\u0001'));
        l.addLast(new UInt8Item((char)(1 + this.fonts[j].name.length())));
        l.addLast(new StringItem(this.fonts[j].name));
        l.addLast(new UInt16Item('\u0001'));
        l.addLast(new UInt8Item('\u0002'));
        l.addLast(new UInt16Item('\u0001'));
        final OffsetItem topdictIndex1Ref = new IndexOffsetItem(2);
        l.addLast(topdictIndex1Ref);
        final IndexBaseItem topdictBase = new IndexBaseItem();
        l.addLast(topdictBase);
        final OffsetItem charsetRef = new DictOffsetItem();
        final OffsetItem charstringsRef = new DictOffsetItem();
        final OffsetItem fdarrayRef = new DictOffsetItem();
        final OffsetItem fdselectRef = new DictOffsetItem();
        if (!this.fonts[j].isCID) {
            l.addLast(new DictNumberItem(nstrings));
            l.addLast(new DictNumberItem(nstrings + 1));
            l.addLast(new DictNumberItem(0));
            l.addLast(new UInt8Item('\f'));
            l.addLast(new UInt8Item('\u001e'));
            l.addLast(new DictNumberItem(nglyphs));
            l.addLast(new UInt8Item('\f'));
            l.addLast(new UInt8Item('\"'));
        }
        l.addLast(fdarrayRef);
        l.addLast(new UInt8Item('\f'));
        l.addLast(new UInt8Item('$'));
        l.addLast(fdselectRef);
        l.addLast(new UInt8Item('\f'));
        l.addLast(new UInt8Item('%'));
        l.addLast(charsetRef);
        l.addLast(new UInt8Item('\u000f'));
        l.addLast(charstringsRef);
        l.addLast(new UInt8Item('\u0011'));
        this.seek(this.topdictOffsets[j]);
        while (this.getPosition() < this.topdictOffsets[j + 1]) {
            final int p1 = this.getPosition();
            this.getDictItem();
            final int p2 = this.getPosition();
            if (this.key != "Encoding" && this.key != "Private" && this.key != "FDSelect" && this.key != "FDArray" && this.key != "charset") {
                if (this.key == "CharStrings") {
                    continue;
                }
                l.add(new RangeItem(this.buf, p1, p2 - p1));
            }
        }
        l.addLast(new IndexMarkerItem(topdictIndex1Ref, topdictBase));
        if (this.fonts[j].isCID) {
            l.addLast(this.getEntireIndexRange(this.stringIndexOffset));
        }
        else {
            String fdFontName = this.fonts[j].name + "-OneRange";
            if (fdFontName.length() > 127) {
                fdFontName = fdFontName.substring(0, 127);
            }
            final String extraStrings = "AdobeIdentity" + fdFontName;
            final int origStringsLen = this.stringOffsets[this.stringOffsets.length - 1] - this.stringOffsets[0];
            final int stringsBaseOffset = this.stringOffsets[0] - 1;
            byte stringsIndexOffSize;
            if (origStringsLen + extraStrings.length() <= 255) {
                stringsIndexOffSize = 1;
            }
            else if (origStringsLen + extraStrings.length() <= 65535) {
                stringsIndexOffSize = 2;
            }
            else if (origStringsLen + extraStrings.length() <= 16777215) {
                stringsIndexOffSize = 3;
            }
            else {
                stringsIndexOffSize = 4;
            }
            l.addLast(new UInt16Item((char)(this.stringOffsets.length - 1 + 3)));
            l.addLast(new UInt8Item((char)stringsIndexOffSize));
            for (int i = 0; i < this.stringOffsets.length; ++i) {
                l.addLast(new IndexOffsetItem(stringsIndexOffSize, this.stringOffsets[i] - stringsBaseOffset));
            }
            int currentStringsOffset = this.stringOffsets[this.stringOffsets.length - 1] - stringsBaseOffset;
            currentStringsOffset += "Adobe".length();
            l.addLast(new IndexOffsetItem(stringsIndexOffSize, currentStringsOffset));
            currentStringsOffset += "Identity".length();
            l.addLast(new IndexOffsetItem(stringsIndexOffSize, currentStringsOffset));
            currentStringsOffset += fdFontName.length();
            l.addLast(new IndexOffsetItem(stringsIndexOffSize, currentStringsOffset));
            l.addLast(new RangeItem(this.buf, this.stringOffsets[0], origStringsLen));
            l.addLast(new StringItem(extraStrings));
        }
        l.addLast(this.getEntireIndexRange(this.gsubrIndexOffset));
        if (!this.fonts[j].isCID) {
            l.addLast(new MarkerItem(fdselectRef));
            l.addLast(new UInt8Item('\u0003'));
            l.addLast(new UInt16Item('\u0001'));
            l.addLast(new UInt16Item('\0'));
            l.addLast(new UInt8Item('\0'));
            l.addLast(new UInt16Item((char)nglyphs));
            l.addLast(new MarkerItem(charsetRef));
            l.addLast(new UInt8Item('\u0002'));
            l.addLast(new UInt16Item('\u0001'));
            l.addLast(new UInt16Item((char)(nglyphs - 1)));
            l.addLast(new MarkerItem(fdarrayRef));
            l.addLast(new UInt16Item('\u0001'));
            l.addLast(new UInt8Item('\u0001'));
            l.addLast(new UInt8Item('\u0001'));
            final OffsetItem privateIndex1Ref = new IndexOffsetItem(1);
            l.addLast(privateIndex1Ref);
            final IndexBaseItem privateBase = new IndexBaseItem();
            l.addLast(privateBase);
            l.addLast(new DictNumberItem(this.fonts[j].privateLength));
            final OffsetItem privateRef = new DictOffsetItem();
            l.addLast(privateRef);
            l.addLast(new UInt8Item('\u0012'));
            l.addLast(new IndexMarkerItem(privateIndex1Ref, privateBase));
            l.addLast(new MarkerItem(privateRef));
            l.addLast(new RangeItem(this.buf, this.fonts[j].privateOffset, this.fonts[j].privateLength));
            if (this.fonts[j].privateSubrs >= 0) {
                l.addLast(this.getEntireIndexRange(this.fonts[j].privateSubrs));
            }
        }
        l.addLast(new MarkerItem(charstringsRef));
        l.addLast(this.getEntireIndexRange(this.fonts[j].charstringsOffset));
        final int[] currentOffset = { 0 };
        for (final Item item : l) {
            item.increment(currentOffset);
        }
        for (final Item item : l) {
            item.xref();
        }
        final int size = currentOffset[0];
        final byte[] b = new byte[size];
        for (final Item item2 : l) {
            item2.emit(b);
        }
        return b;
    }
    
    public boolean isCID(final String fontName) {
        for (int j = 0; j < this.fonts.length; ++j) {
            if (fontName.equals(this.fonts[j].name)) {
                return this.fonts[j].isCID;
            }
        }
        return false;
    }
    
    public boolean exists(final String fontName) {
        for (int j = 0; j < this.fonts.length; ++j) {
            if (fontName.equals(this.fonts[j].name)) {
                return true;
            }
        }
        return false;
    }
    
    public String[] getNames() {
        final String[] names = new String[this.fonts.length];
        for (int i = 0; i < this.fonts.length; ++i) {
            names[i] = this.fonts[i].name;
        }
        return names;
    }
    
    public CFFFont(final RandomAccessFileOrArray inputbuffer) {
        this.args = new Object[48];
        this.arg_count = 0;
        this.buf = inputbuffer;
        this.seek(0);
        final int major = this.getCard8();
        final int minor = this.getCard8();
        final int hdrSize = this.getCard8();
        this.offSize = this.getCard8();
        this.nameIndexOffset = hdrSize;
        this.nameOffsets = this.getIndex(this.nameIndexOffset);
        this.topdictIndexOffset = this.nameOffsets[this.nameOffsets.length - 1];
        this.topdictOffsets = this.getIndex(this.topdictIndexOffset);
        this.stringIndexOffset = this.topdictOffsets[this.topdictOffsets.length - 1];
        this.stringOffsets = this.getIndex(this.stringIndexOffset);
        this.gsubrIndexOffset = this.stringOffsets[this.stringOffsets.length - 1];
        this.gsubrOffsets = this.getIndex(this.gsubrIndexOffset);
        this.fonts = new Font[this.nameOffsets.length - 1];
        for (int j = 0; j < this.nameOffsets.length - 1; ++j) {
            this.fonts[j] = new Font();
            this.seek(this.nameOffsets[j]);
            this.fonts[j].name = "";
            for (int k = this.nameOffsets[j]; k < this.nameOffsets[j + 1]; ++k) {
                final StringBuilder sb = new StringBuilder();
                final Font font = this.fonts[j];
                font.name = sb.append(font.name).append(this.getCard8()).toString();
            }
        }
        for (int j = 0; j < this.topdictOffsets.length - 1; ++j) {
            this.seek(this.topdictOffsets[j]);
            while (this.getPosition() < this.topdictOffsets[j + 1]) {
                this.getDictItem();
                if (this.key == "FullName") {
                    this.fonts[j].fullName = this.getString((char)(int)this.args[0]);
                }
                else if (this.key == "ROS") {
                    this.fonts[j].isCID = true;
                }
                else if (this.key == "Private") {
                    this.fonts[j].privateLength = (int)this.args[0];
                    this.fonts[j].privateOffset = (int)this.args[1];
                }
                else if (this.key == "charset") {
                    this.fonts[j].charsetOffset = (int)this.args[0];
                }
                else if (this.key == "Encoding") {
                    this.ReadEncoding(this.fonts[j].encodingOffset = (int)this.args[0]);
                }
                else if (this.key == "CharStrings") {
                    this.fonts[j].charstringsOffset = (int)this.args[0];
                    final int p = this.getPosition();
                    this.fonts[j].charstringsOffsets = this.getIndex(this.fonts[j].charstringsOffset);
                    this.seek(p);
                }
                else if (this.key == "FDArray") {
                    this.fonts[j].fdarrayOffset = (int)this.args[0];
                }
                else if (this.key == "FDSelect") {
                    this.fonts[j].fdselectOffset = (int)this.args[0];
                }
                else {
                    if (this.key != "CharstringType") {
                        continue;
                    }
                    this.fonts[j].CharstringType = (int)this.args[0];
                }
            }
            if (this.fonts[j].privateOffset >= 0) {
                this.seek(this.fonts[j].privateOffset);
                while (this.getPosition() < this.fonts[j].privateOffset + this.fonts[j].privateLength) {
                    this.getDictItem();
                    if (this.key == "Subrs") {
                        this.fonts[j].privateSubrs = (int)this.args[0] + this.fonts[j].privateOffset;
                    }
                }
            }
            if (this.fonts[j].fdarrayOffset >= 0) {
                final int[] fdarrayOffsets = this.getIndex(this.fonts[j].fdarrayOffset);
                this.fonts[j].fdprivateOffsets = new int[fdarrayOffsets.length - 1];
                this.fonts[j].fdprivateLengths = new int[fdarrayOffsets.length - 1];
                for (int i = 0; i < fdarrayOffsets.length - 1; ++i) {
                    this.seek(fdarrayOffsets[i]);
                    while (this.getPosition() < fdarrayOffsets[i + 1]) {
                        this.getDictItem();
                    }
                    if (this.key == "Private") {
                        this.fonts[j].fdprivateLengths[i] = (int)this.args[0];
                        this.fonts[j].fdprivateOffsets[i] = (int)this.args[1];
                    }
                }
            }
        }
    }
    
    void ReadEncoding(final int nextIndexOffset) {
        this.seek(nextIndexOffset);
        final int format = this.getCard8();
    }
    
    static {
        operatorNames = new String[] { "version", "Notice", "FullName", "FamilyName", "Weight", "FontBBox", "BlueValues", "OtherBlues", "FamilyBlues", "FamilyOtherBlues", "StdHW", "StdVW", "UNKNOWN_12", "UniqueID", "XUID", "charset", "Encoding", "CharStrings", "Private", "Subrs", "defaultWidthX", "nominalWidthX", "UNKNOWN_22", "UNKNOWN_23", "UNKNOWN_24", "UNKNOWN_25", "UNKNOWN_26", "UNKNOWN_27", "UNKNOWN_28", "UNKNOWN_29", "UNKNOWN_30", "UNKNOWN_31", "Copyright", "isFixedPitch", "ItalicAngle", "UnderlinePosition", "UnderlineThickness", "PaintType", "CharstringType", "FontMatrix", "StrokeWidth", "BlueScale", "BlueShift", "BlueFuzz", "StemSnapH", "StemSnapV", "ForceBold", "UNKNOWN_12_15", "UNKNOWN_12_16", "LanguageGroup", "ExpansionFactor", "initialRandomSeed", "SyntheticBase", "PostScript", "BaseFontName", "BaseFontBlend", "UNKNOWN_12_24", "UNKNOWN_12_25", "UNKNOWN_12_26", "UNKNOWN_12_27", "UNKNOWN_12_28", "UNKNOWN_12_29", "ROS", "CIDFontVersion", "CIDFontRevision", "CIDFontType", "CIDCount", "UIDBase", "FDArray", "FDSelect", "FontName" };
        standardStrings = new String[] { ".notdef", "space", "exclam", "quotedbl", "numbersign", "dollar", "percent", "ampersand", "quoteright", "parenleft", "parenright", "asterisk", "plus", "comma", "hyphen", "period", "slash", "zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "colon", "semicolon", "less", "equal", "greater", "question", "at", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "bracketleft", "backslash", "bracketright", "asciicircum", "underscore", "quoteleft", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "braceleft", "bar", "braceright", "asciitilde", "exclamdown", "cent", "sterling", "fraction", "yen", "florin", "section", "currency", "quotesingle", "quotedblleft", "guillemotleft", "guilsinglleft", "guilsinglright", "fi", "fl", "endash", "dagger", "daggerdbl", "periodcentered", "paragraph", "bullet", "quotesinglbase", "quotedblbase", "quotedblright", "guillemotright", "ellipsis", "perthousand", "questiondown", "grave", "acute", "circumflex", "tilde", "macron", "breve", "dotaccent", "dieresis", "ring", "cedilla", "hungarumlaut", "ogonek", "caron", "emdash", "AE", "ordfeminine", "Lslash", "Oslash", "OE", "ordmasculine", "ae", "dotlessi", "lslash", "oslash", "oe", "germandbls", "onesuperior", "logicalnot", "mu", "trademark", "Eth", "onehalf", "plusminus", "Thorn", "onequarter", "divide", "brokenbar", "degree", "thorn", "threequarters", "twosuperior", "registered", "minus", "eth", "multiply", "threesuperior", "copyright", "Aacute", "Acircumflex", "Adieresis", "Agrave", "Aring", "Atilde", "Ccedilla", "Eacute", "Ecircumflex", "Edieresis", "Egrave", "Iacute", "Icircumflex", "Idieresis", "Igrave", "Ntilde", "Oacute", "Ocircumflex", "Odieresis", "Ograve", "Otilde", "Scaron", "Uacute", "Ucircumflex", "Udieresis", "Ugrave", "Yacute", "Ydieresis", "Zcaron", "aacute", "acircumflex", "adieresis", "agrave", "aring", "atilde", "ccedilla", "eacute", "ecircumflex", "edieresis", "egrave", "iacute", "icircumflex", "idieresis", "igrave", "ntilde", "oacute", "ocircumflex", "odieresis", "ograve", "otilde", "scaron", "uacute", "ucircumflex", "udieresis", "ugrave", "yacute", "ydieresis", "zcaron", "exclamsmall", "Hungarumlautsmall", "dollaroldstyle", "dollarsuperior", "ampersandsmall", "Acutesmall", "parenleftsuperior", "parenrightsuperior", "twodotenleader", "onedotenleader", "zerooldstyle", "oneoldstyle", "twooldstyle", "threeoldstyle", "fouroldstyle", "fiveoldstyle", "sixoldstyle", "sevenoldstyle", "eightoldstyle", "nineoldstyle", "commasuperior", "threequartersemdash", "periodsuperior", "questionsmall", "asuperior", "bsuperior", "centsuperior", "dsuperior", "esuperior", "isuperior", "lsuperior", "msuperior", "nsuperior", "osuperior", "rsuperior", "ssuperior", "tsuperior", "ff", "ffi", "ffl", "parenleftinferior", "parenrightinferior", "Circumflexsmall", "hyphensuperior", "Gravesmall", "Asmall", "Bsmall", "Csmall", "Dsmall", "Esmall", "Fsmall", "Gsmall", "Hsmall", "Ismall", "Jsmall", "Ksmall", "Lsmall", "Msmall", "Nsmall", "Osmall", "Psmall", "Qsmall", "Rsmall", "Ssmall", "Tsmall", "Usmall", "Vsmall", "Wsmall", "Xsmall", "Ysmall", "Zsmall", "colonmonetary", "onefitted", "rupiah", "Tildesmall", "exclamdownsmall", "centoldstyle", "Lslashsmall", "Scaronsmall", "Zcaronsmall", "Dieresissmall", "Brevesmall", "Caronsmall", "Dotaccentsmall", "Macronsmall", "figuredash", "hypheninferior", "Ogoneksmall", "Ringsmall", "Cedillasmall", "questiondownsmall", "oneeighth", "threeeighths", "fiveeighths", "seveneighths", "onethird", "twothirds", "zerosuperior", "foursuperior", "fivesuperior", "sixsuperior", "sevensuperior", "eightsuperior", "ninesuperior", "zeroinferior", "oneinferior", "twoinferior", "threeinferior", "fourinferior", "fiveinferior", "sixinferior", "seveninferior", "eightinferior", "nineinferior", "centinferior", "dollarinferior", "periodinferior", "commainferior", "Agravesmall", "Aacutesmall", "Acircumflexsmall", "Atildesmall", "Adieresissmall", "Aringsmall", "AEsmall", "Ccedillasmall", "Egravesmall", "Eacutesmall", "Ecircumflexsmall", "Edieresissmall", "Igravesmall", "Iacutesmall", "Icircumflexsmall", "Idieresissmall", "Ethsmall", "Ntildesmall", "Ogravesmall", "Oacutesmall", "Ocircumflexsmall", "Otildesmall", "Odieresissmall", "OEsmall", "Oslashsmall", "Ugravesmall", "Uacutesmall", "Ucircumflexsmall", "Udieresissmall", "Yacutesmall", "Thornsmall", "Ydieresissmall", "001.000", "001.001", "001.002", "001.003", "Black", "Bold", "Book", "Light", "Medium", "Regular", "Roman", "Semibold" };
    }
    
    protected abstract static class Item
    {
        protected int myOffset;
        
        protected Item() {
            this.myOffset = -1;
        }
        
        public void increment(final int[] currentOffset) {
            this.myOffset = currentOffset[0];
        }
        
        public void emit(final byte[] buffer) {
        }
        
        public void xref() {
        }
    }
    
    protected abstract static class OffsetItem extends Item
    {
        public int value;
        
        public void set(final int offset) {
            this.value = offset;
        }
    }
    
    protected static final class RangeItem extends Item
    {
        public int offset;
        public int length;
        private RandomAccessFileOrArray buf;
        
        public RangeItem(final RandomAccessFileOrArray buf, final int offset, final int length) {
            this.offset = offset;
            this.length = length;
            this.buf = buf;
        }
        
        @Override
        public void increment(final int[] currentOffset) {
            super.increment(currentOffset);
            final int n = 0;
            currentOffset[n] += this.length;
        }
        
        @Override
        public void emit(final byte[] buffer) {
            try {
                this.buf.seek(this.offset);
                for (int i = this.myOffset; i < this.myOffset + this.length; ++i) {
                    buffer[i] = this.buf.readByte();
                }
            }
            catch (final Exception e) {
                throw new ExceptionConverter(e);
            }
        }
    }
    
    protected static final class IndexOffsetItem extends OffsetItem
    {
        public final int size;
        
        public IndexOffsetItem(final int size, final int value) {
            this.size = size;
            this.value = value;
        }
        
        public IndexOffsetItem(final int size) {
            this.size = size;
        }
        
        @Override
        public void increment(final int[] currentOffset) {
            super.increment(currentOffset);
            final int n = 0;
            currentOffset[n] += this.size;
        }
        
        @Override
        public void emit(final byte[] buffer) {
            int i = 0;
            switch (this.size) {
                case 4: {
                    buffer[this.myOffset + i] = (byte)(this.value >>> 24 & 0xFF);
                    ++i;
                }
                case 3: {
                    buffer[this.myOffset + i] = (byte)(this.value >>> 16 & 0xFF);
                    ++i;
                }
                case 2: {
                    buffer[this.myOffset + i] = (byte)(this.value >>> 8 & 0xFF);
                    ++i;
                }
                case 1: {
                    buffer[this.myOffset + i] = (byte)(this.value >>> 0 & 0xFF);
                    ++i;
                    break;
                }
            }
        }
    }
    
    protected static final class IndexBaseItem extends Item
    {
        public IndexBaseItem() {
        }
    }
    
    protected static final class IndexMarkerItem extends Item
    {
        private OffsetItem offItem;
        private IndexBaseItem indexBase;
        
        public IndexMarkerItem(final OffsetItem offItem, final IndexBaseItem indexBase) {
            this.offItem = offItem;
            this.indexBase = indexBase;
        }
        
        @Override
        public void xref() {
            this.offItem.set(this.myOffset - this.indexBase.myOffset + 1);
        }
    }
    
    protected static final class SubrMarkerItem extends Item
    {
        private OffsetItem offItem;
        private IndexBaseItem indexBase;
        
        public SubrMarkerItem(final OffsetItem offItem, final IndexBaseItem indexBase) {
            this.offItem = offItem;
            this.indexBase = indexBase;
        }
        
        @Override
        public void xref() {
            this.offItem.set(this.myOffset - this.indexBase.myOffset);
        }
    }
    
    protected static final class DictOffsetItem extends OffsetItem
    {
        public final int size;
        
        public DictOffsetItem() {
            this.size = 5;
        }
        
        @Override
        public void increment(final int[] currentOffset) {
            super.increment(currentOffset);
            final int n = 0;
            currentOffset[n] += this.size;
        }
        
        @Override
        public void emit(final byte[] buffer) {
            if (this.size == 5) {
                buffer[this.myOffset] = 29;
                buffer[this.myOffset + 1] = (byte)(this.value >>> 24 & 0xFF);
                buffer[this.myOffset + 2] = (byte)(this.value >>> 16 & 0xFF);
                buffer[this.myOffset + 3] = (byte)(this.value >>> 8 & 0xFF);
                buffer[this.myOffset + 4] = (byte)(this.value >>> 0 & 0xFF);
            }
        }
    }
    
    protected static final class UInt24Item extends Item
    {
        public int value;
        
        public UInt24Item(final int value) {
            this.value = value;
        }
        
        @Override
        public void increment(final int[] currentOffset) {
            super.increment(currentOffset);
            final int n = 0;
            currentOffset[n] += 3;
        }
        
        @Override
        public void emit(final byte[] buffer) {
            buffer[this.myOffset + 0] = (byte)(this.value >>> 16 & 0xFF);
            buffer[this.myOffset + 1] = (byte)(this.value >>> 8 & 0xFF);
            buffer[this.myOffset + 2] = (byte)(this.value >>> 0 & 0xFF);
        }
    }
    
    protected static final class UInt32Item extends Item
    {
        public int value;
        
        public UInt32Item(final int value) {
            this.value = value;
        }
        
        @Override
        public void increment(final int[] currentOffset) {
            super.increment(currentOffset);
            final int n = 0;
            currentOffset[n] += 4;
        }
        
        @Override
        public void emit(final byte[] buffer) {
            buffer[this.myOffset + 0] = (byte)(this.value >>> 24 & 0xFF);
            buffer[this.myOffset + 1] = (byte)(this.value >>> 16 & 0xFF);
            buffer[this.myOffset + 2] = (byte)(this.value >>> 8 & 0xFF);
            buffer[this.myOffset + 3] = (byte)(this.value >>> 0 & 0xFF);
        }
    }
    
    protected static final class UInt16Item extends Item
    {
        public char value;
        
        public UInt16Item(final char value) {
            this.value = value;
        }
        
        @Override
        public void increment(final int[] currentOffset) {
            super.increment(currentOffset);
            final int n = 0;
            currentOffset[n] += 2;
        }
        
        @Override
        public void emit(final byte[] buffer) {
            buffer[this.myOffset + 0] = (byte)(this.value >>> 8 & 0xFF);
            buffer[this.myOffset + 1] = (byte)(this.value >>> 0 & 0xFF);
        }
    }
    
    protected static final class UInt8Item extends Item
    {
        public char value;
        
        public UInt8Item(final char value) {
            this.value = value;
        }
        
        @Override
        public void increment(final int[] currentOffset) {
            super.increment(currentOffset);
            final int n = 0;
            ++currentOffset[n];
        }
        
        @Override
        public void emit(final byte[] buffer) {
            buffer[this.myOffset + 0] = (byte)(this.value >>> 0 & 0xFF);
        }
    }
    
    protected static final class StringItem extends Item
    {
        public String s;
        
        public StringItem(final String s) {
            this.s = s;
        }
        
        @Override
        public void increment(final int[] currentOffset) {
            super.increment(currentOffset);
            final int n = 0;
            currentOffset[n] += this.s.length();
        }
        
        @Override
        public void emit(final byte[] buffer) {
            for (int i = 0; i < this.s.length(); ++i) {
                buffer[this.myOffset + i] = (byte)(this.s.charAt(i) & '\u00ff');
            }
        }
    }
    
    protected static final class DictNumberItem extends Item
    {
        public final int value;
        public int size;
        
        public DictNumberItem(final int value) {
            this.size = 5;
            this.value = value;
        }
        
        @Override
        public void increment(final int[] currentOffset) {
            super.increment(currentOffset);
            final int n = 0;
            currentOffset[n] += this.size;
        }
        
        @Override
        public void emit(final byte[] buffer) {
            if (this.size == 5) {
                buffer[this.myOffset] = 29;
                buffer[this.myOffset + 1] = (byte)(this.value >>> 24 & 0xFF);
                buffer[this.myOffset + 2] = (byte)(this.value >>> 16 & 0xFF);
                buffer[this.myOffset + 3] = (byte)(this.value >>> 8 & 0xFF);
                buffer[this.myOffset + 4] = (byte)(this.value >>> 0 & 0xFF);
            }
        }
    }
    
    protected static final class MarkerItem extends Item
    {
        OffsetItem p;
        
        public MarkerItem(final OffsetItem pointerToMarker) {
            this.p = pointerToMarker;
        }
        
        @Override
        public void xref() {
            this.p.set(this.myOffset);
        }
    }
    
    protected final class Font
    {
        public String name;
        public String fullName;
        public boolean isCID;
        public int privateOffset;
        public int privateLength;
        public int privateSubrs;
        public int charstringsOffset;
        public int encodingOffset;
        public int charsetOffset;
        public int fdarrayOffset;
        public int fdselectOffset;
        public int[] fdprivateOffsets;
        public int[] fdprivateLengths;
        public int[] fdprivateSubrs;
        public int nglyphs;
        public int nstrings;
        public int CharsetLength;
        public int[] charstringsOffsets;
        public int[] charset;
        public int[] FDSelect;
        public int FDSelectLength;
        public int FDSelectFormat;
        public int CharstringType;
        public int FDArrayCount;
        public int FDArrayOffsize;
        public int[] FDArrayOffsets;
        public int[] PrivateSubrsOffset;
        public int[][] PrivateSubrsOffsetsArray;
        public int[] SubrsOffsets;
        
        protected Font() {
            this.isCID = false;
            this.privateOffset = -1;
            this.privateLength = -1;
            this.privateSubrs = -1;
            this.charstringsOffset = -1;
            this.encodingOffset = -1;
            this.charsetOffset = -1;
            this.fdarrayOffset = -1;
            this.fdselectOffset = -1;
            this.CharstringType = 2;
        }
    }
}
