package com.lowagie.text.pdf;

import java.util.Iterator;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.HashMap;

public class CFFFontSubset extends CFFFont
{
    static final String[] SubrsFunctions;
    static final String[] SubrsEscapeFuncs;
    static final byte ENDCHAR_OP = 14;
    static final byte RETURN_OP = 11;
    HashMap GlyphsUsed;
    ArrayList glyphsInList;
    HashMap FDArrayUsed;
    HashMap[] hSubrsUsed;
    ArrayList[] lSubrsUsed;
    HashMap hGSubrsUsed;
    ArrayList lGSubrsUsed;
    HashMap hSubrsUsedNonCID;
    ArrayList lSubrsUsedNonCID;
    byte[][] NewLSubrsIndex;
    byte[] NewSubrsIndexNonCID;
    byte[] NewGSubrsIndex;
    byte[] NewCharStringsIndex;
    int GBias;
    LinkedList OutputList;
    int NumOfHints;
    
    public CFFFontSubset(final RandomAccessFileOrArray rf, final HashMap GlyphsUsed) {
        super(rf);
        this.FDArrayUsed = new HashMap();
        this.hGSubrsUsed = new HashMap();
        this.lGSubrsUsed = new ArrayList();
        this.hSubrsUsedNonCID = new HashMap();
        this.lSubrsUsedNonCID = new ArrayList();
        this.GBias = 0;
        this.NumOfHints = 0;
        this.GlyphsUsed = GlyphsUsed;
        this.glyphsInList = new ArrayList(GlyphsUsed.keySet());
        for (int i = 0; i < this.fonts.length; ++i) {
            this.seek(this.fonts[i].charstringsOffset);
            this.fonts[i].nglyphs = this.getCard16();
            this.seek(this.stringIndexOffset);
            this.fonts[i].nstrings = this.getCard16() + CFFFontSubset.standardStrings.length;
            this.fonts[i].charstringsOffsets = this.getIndex(this.fonts[i].charstringsOffset);
            if (this.fonts[i].fdselectOffset >= 0) {
                this.readFDSelect(i);
                this.BuildFDArrayUsed(i);
            }
            if (this.fonts[i].isCID) {
                this.ReadFDArray(i);
            }
            this.fonts[i].CharsetLength = this.CountCharset(this.fonts[i].charsetOffset, this.fonts[i].nglyphs);
        }
    }
    
    int CountCharset(final int Offset, final int NumofGlyphs) {
        int Length = 0;
        this.seek(Offset);
        final int format = this.getCard8();
        switch (format) {
            case 0: {
                Length = 1 + 2 * NumofGlyphs;
                break;
            }
            case 1: {
                Length = 1 + 3 * this.CountRange(NumofGlyphs, 1);
                break;
            }
            case 2: {
                Length = 1 + 4 * this.CountRange(NumofGlyphs, 2);
                break;
            }
        }
        return Length;
    }
    
    int CountRange(final int NumofGlyphs, final int Type) {
        int num = 0;
        int nLeft;
        for (int i = 1; i < NumofGlyphs; i += nLeft + 1) {
            ++num;
            final char Sid = this.getCard16();
            if (Type == 1) {
                nLeft = this.getCard8();
            }
            else {
                nLeft = this.getCard16();
            }
        }
        return num;
    }
    
    protected void readFDSelect(final int Font) {
        final int NumOfGlyphs = this.fonts[Font].nglyphs;
        final int[] FDSelect = new int[NumOfGlyphs];
        this.seek(this.fonts[Font].fdselectOffset);
        switch (this.fonts[Font].FDSelectFormat = this.getCard8()) {
            case 0: {
                for (int i = 0; i < NumOfGlyphs; ++i) {
                    FDSelect[i] = this.getCard8();
                }
                this.fonts[Font].FDSelectLength = this.fonts[Font].nglyphs + 1;
                break;
            }
            case 3: {
                final int nRanges = this.getCard16();
                int l = 0;
                int first = this.getCard16();
                for (int j = 0; j < nRanges; ++j) {
                    final int fd = this.getCard8();
                    final int last = this.getCard16();
                    for (int steps = last - first, k = 0; k < steps; ++k) {
                        FDSelect[l] = fd;
                        ++l;
                    }
                    first = last;
                }
                this.fonts[Font].FDSelectLength = 3 + nRanges * 3 + 2;
                break;
            }
        }
        this.fonts[Font].FDSelect = FDSelect;
    }
    
    protected void BuildFDArrayUsed(final int Font) {
        final int[] FDSelect = this.fonts[Font].FDSelect;
        for (int i = 0; i < this.glyphsInList.size(); ++i) {
            final int glyph = this.glyphsInList.get(i);
            final int FD = FDSelect[glyph];
            this.FDArrayUsed.put(new Integer(FD), null);
        }
    }
    
    protected void ReadFDArray(final int Font) {
        this.seek(this.fonts[Font].fdarrayOffset);
        this.fonts[Font].FDArrayCount = this.getCard16();
        this.fonts[Font].FDArrayOffsize = this.getCard8();
        if (this.fonts[Font].FDArrayOffsize < 4) {
            final Font font = this.fonts[Font];
            ++font.FDArrayOffsize;
        }
        this.fonts[Font].FDArrayOffsets = this.getIndex(this.fonts[Font].fdarrayOffset);
    }
    
    public byte[] Process(final String fontName) throws IOException {
        try {
            this.buf.reOpen();
            int j;
            for (j = 0; j < this.fonts.length && !fontName.equals(this.fonts[j].name); ++j) {}
            if (j == this.fonts.length) {
                return null;
            }
            if (this.gsubrIndexOffset >= 0) {
                this.GBias = this.CalcBias(this.gsubrIndexOffset, j);
            }
            this.BuildNewCharString(j);
            this.BuildNewLGSubrs(j);
            final byte[] Ret = this.BuildNewFile(j);
            return Ret;
        }
        finally {
            try {
                this.buf.close();
            }
            catch (final Exception ex) {}
        }
    }
    
    protected int CalcBias(final int Offset, final int Font) {
        this.seek(Offset);
        final int nSubrs = this.getCard16();
        if (this.fonts[Font].CharstringType == 1) {
            return 0;
        }
        if (nSubrs < 1240) {
            return 107;
        }
        if (nSubrs < 33900) {
            return 1131;
        }
        return 32768;
    }
    
    protected void BuildNewCharString(final int FontIndex) throws IOException {
        this.NewCharStringsIndex = this.BuildNewIndex(this.fonts[FontIndex].charstringsOffsets, this.GlyphsUsed, (byte)14);
    }
    
    protected void BuildNewLGSubrs(final int Font) throws IOException {
        if (this.fonts[Font].isCID) {
            this.hSubrsUsed = new HashMap[this.fonts[Font].fdprivateOffsets.length];
            this.lSubrsUsed = new ArrayList[this.fonts[Font].fdprivateOffsets.length];
            this.NewLSubrsIndex = new byte[this.fonts[Font].fdprivateOffsets.length][];
            this.fonts[Font].PrivateSubrsOffset = new int[this.fonts[Font].fdprivateOffsets.length];
            this.fonts[Font].PrivateSubrsOffsetsArray = new int[this.fonts[Font].fdprivateOffsets.length][];
            final ArrayList FDInList = new ArrayList(this.FDArrayUsed.keySet());
            for (int j = 0; j < FDInList.size(); ++j) {
                final int FD = FDInList.get(j);
                this.hSubrsUsed[FD] = new HashMap();
                this.lSubrsUsed[FD] = new ArrayList();
                this.BuildFDSubrsOffsets(Font, FD);
                if (this.fonts[Font].PrivateSubrsOffset[FD] >= 0) {
                    this.BuildSubrUsed(Font, FD, this.fonts[Font].PrivateSubrsOffset[FD], this.fonts[Font].PrivateSubrsOffsetsArray[FD], this.hSubrsUsed[FD], this.lSubrsUsed[FD]);
                    this.NewLSubrsIndex[FD] = this.BuildNewIndex(this.fonts[Font].PrivateSubrsOffsetsArray[FD], this.hSubrsUsed[FD], (byte)11);
                }
            }
        }
        else if (this.fonts[Font].privateSubrs >= 0) {
            this.fonts[Font].SubrsOffsets = this.getIndex(this.fonts[Font].privateSubrs);
            this.BuildSubrUsed(Font, -1, this.fonts[Font].privateSubrs, this.fonts[Font].SubrsOffsets, this.hSubrsUsedNonCID, this.lSubrsUsedNonCID);
        }
        this.BuildGSubrsUsed(Font);
        if (this.fonts[Font].privateSubrs >= 0) {
            this.NewSubrsIndexNonCID = this.BuildNewIndex(this.fonts[Font].SubrsOffsets, this.hSubrsUsedNonCID, (byte)11);
        }
        this.NewGSubrsIndex = this.BuildNewIndex(this.gsubrOffsets, this.hGSubrsUsed, (byte)11);
    }
    
    protected void BuildFDSubrsOffsets(final int Font, final int FD) {
        this.fonts[Font].PrivateSubrsOffset[FD] = -1;
        this.seek(this.fonts[Font].fdprivateOffsets[FD]);
        while (this.getPosition() < this.fonts[Font].fdprivateOffsets[FD] + this.fonts[Font].fdprivateLengths[FD]) {
            this.getDictItem();
            if (this.key == "Subrs") {
                this.fonts[Font].PrivateSubrsOffset[FD] = (int)this.args[0] + this.fonts[Font].fdprivateOffsets[FD];
            }
        }
        if (this.fonts[Font].PrivateSubrsOffset[FD] >= 0) {
            this.fonts[Font].PrivateSubrsOffsetsArray[FD] = this.getIndex(this.fonts[Font].PrivateSubrsOffset[FD]);
        }
    }
    
    protected void BuildSubrUsed(final int Font, final int FD, final int SubrOffset, final int[] SubrsOffsets, final HashMap hSubr, final ArrayList lSubr) {
        final int LBias = this.CalcBias(SubrOffset, Font);
        for (int i = 0; i < this.glyphsInList.size(); ++i) {
            final int glyph = this.glyphsInList.get(i);
            final int Start = this.fonts[Font].charstringsOffsets[glyph];
            final int End = this.fonts[Font].charstringsOffsets[glyph + 1];
            if (FD >= 0) {
                this.EmptyStack();
                this.NumOfHints = 0;
                final int GlyphFD = this.fonts[Font].FDSelect[glyph];
                if (GlyphFD == FD) {
                    this.ReadASubr(Start, End, this.GBias, LBias, hSubr, lSubr, SubrsOffsets);
                }
            }
            else {
                this.ReadASubr(Start, End, this.GBias, LBias, hSubr, lSubr, SubrsOffsets);
            }
        }
        for (int i = 0; i < lSubr.size(); ++i) {
            final int Subr = lSubr.get(i);
            if (Subr < SubrsOffsets.length - 1 && Subr >= 0) {
                final int Start = SubrsOffsets[Subr];
                final int End = SubrsOffsets[Subr + 1];
                this.ReadASubr(Start, End, this.GBias, LBias, hSubr, lSubr, SubrsOffsets);
            }
        }
    }
    
    protected void BuildGSubrsUsed(final int Font) {
        int LBias = 0;
        int SizeOfNonCIDSubrsUsed = 0;
        if (this.fonts[Font].privateSubrs >= 0) {
            LBias = this.CalcBias(this.fonts[Font].privateSubrs, Font);
            SizeOfNonCIDSubrsUsed = this.lSubrsUsedNonCID.size();
        }
        for (int i = 0; i < this.lGSubrsUsed.size(); ++i) {
            final int Subr = this.lGSubrsUsed.get(i);
            if (Subr < this.gsubrOffsets.length - 1 && Subr >= 0) {
                final int Start = this.gsubrOffsets[Subr];
                final int End = this.gsubrOffsets[Subr + 1];
                if (this.fonts[Font].isCID) {
                    this.ReadASubr(Start, End, this.GBias, 0, this.hGSubrsUsed, this.lGSubrsUsed, null);
                }
                else {
                    this.ReadASubr(Start, End, this.GBias, LBias, this.hSubrsUsedNonCID, this.lSubrsUsedNonCID, this.fonts[Font].SubrsOffsets);
                    if (SizeOfNonCIDSubrsUsed < this.lSubrsUsedNonCID.size()) {
                        for (int j = SizeOfNonCIDSubrsUsed; j < this.lSubrsUsedNonCID.size(); ++j) {
                            final int LSubr = this.lSubrsUsedNonCID.get(j);
                            if (LSubr < this.fonts[Font].SubrsOffsets.length - 1 && LSubr >= 0) {
                                final int LStart = this.fonts[Font].SubrsOffsets[LSubr];
                                final int LEnd = this.fonts[Font].SubrsOffsets[LSubr + 1];
                                this.ReadASubr(LStart, LEnd, this.GBias, LBias, this.hSubrsUsedNonCID, this.lSubrsUsedNonCID, this.fonts[Font].SubrsOffsets);
                            }
                        }
                        SizeOfNonCIDSubrsUsed = this.lSubrsUsedNonCID.size();
                    }
                }
            }
        }
    }
    
    protected void ReadASubr(final int begin, final int end, final int GBias, final int LBias, final HashMap hSubr, final ArrayList lSubr, final int[] LSubrsOffsets) {
        this.EmptyStack();
        this.NumOfHints = 0;
        this.seek(begin);
        while (this.getPosition() < end) {
            this.ReadCommand();
            final int pos = this.getPosition();
            Object TopElement = null;
            if (this.arg_count > 0) {
                TopElement = this.args[this.arg_count - 1];
            }
            final int NumOfArgs = this.arg_count;
            this.HandelStack();
            if (this.key == "callsubr") {
                if (NumOfArgs <= 0) {
                    continue;
                }
                final int Subr = (int)TopElement + LBias;
                if (!hSubr.containsKey(new Integer(Subr))) {
                    hSubr.put(new Integer(Subr), null);
                    lSubr.add(new Integer(Subr));
                }
                this.CalcHints(LSubrsOffsets[Subr], LSubrsOffsets[Subr + 1], LBias, GBias, LSubrsOffsets);
                this.seek(pos);
            }
            else if (this.key == "callgsubr") {
                if (NumOfArgs <= 0) {
                    continue;
                }
                final int Subr = (int)TopElement + GBias;
                if (!this.hGSubrsUsed.containsKey(new Integer(Subr))) {
                    this.hGSubrsUsed.put(new Integer(Subr), null);
                    this.lGSubrsUsed.add(new Integer(Subr));
                }
                this.CalcHints(this.gsubrOffsets[Subr], this.gsubrOffsets[Subr + 1], LBias, GBias, LSubrsOffsets);
                this.seek(pos);
            }
            else if (this.key == "hstem" || this.key == "vstem" || this.key == "hstemhm" || this.key == "vstemhm") {
                this.NumOfHints += NumOfArgs / 2;
            }
            else {
                if (this.key != "hintmask" && this.key != "cntrmask") {
                    continue;
                }
                int SizeOfMask = this.NumOfHints / 8;
                if (this.NumOfHints % 8 != 0 || SizeOfMask == 0) {
                    ++SizeOfMask;
                }
                for (int i = 0; i < SizeOfMask; ++i) {
                    this.getCard8();
                }
            }
        }
    }
    
    protected void HandelStack() {
        int StackHandel = this.StackOpp();
        if (StackHandel < 2) {
            if (StackHandel == 1) {
                this.PushStack();
            }
            else {
                StackHandel *= -1;
                for (int i = 0; i < StackHandel; ++i) {
                    this.PopStack();
                }
            }
        }
        else {
            this.EmptyStack();
        }
    }
    
    protected int StackOpp() {
        if (this.key == "ifelse") {
            return -3;
        }
        if (this.key == "roll" || this.key == "put") {
            return -2;
        }
        if (this.key == "callsubr" || this.key == "callgsubr" || this.key == "add" || this.key == "sub" || this.key == "div" || this.key == "mul" || this.key == "drop" || this.key == "and" || this.key == "or" || this.key == "eq") {
            return -1;
        }
        if (this.key == "abs" || this.key == "neg" || this.key == "sqrt" || this.key == "exch" || this.key == "index" || this.key == "get" || this.key == "not" || this.key == "return") {
            return 0;
        }
        if (this.key == "random" || this.key == "dup") {
            return 1;
        }
        return 2;
    }
    
    protected void EmptyStack() {
        for (int i = 0; i < this.arg_count; ++i) {
            this.args[i] = null;
        }
        this.arg_count = 0;
    }
    
    protected void PopStack() {
        if (this.arg_count > 0) {
            this.args[this.arg_count - 1] = null;
            --this.arg_count;
        }
    }
    
    protected void PushStack() {
        ++this.arg_count;
    }
    
    protected void ReadCommand() {
        this.key = null;
        boolean gotKey = false;
        while (!gotKey) {
            final char b0 = this.getCard8();
            if (b0 == '\u001c') {
                final int first = this.getCard8();
                final int second = this.getCard8();
                this.args[this.arg_count] = new Integer(first << 8 | second);
                ++this.arg_count;
            }
            else if (b0 >= ' ' && b0 <= '\u00f6') {
                this.args[this.arg_count] = new Integer(b0 - '\u008b');
                ++this.arg_count;
            }
            else if (b0 >= '\u00f7' && b0 <= '\u00fa') {
                final int w = this.getCard8();
                this.args[this.arg_count] = new Integer((b0 - '\u00f7') * 256 + w + 108);
                ++this.arg_count;
            }
            else if (b0 >= '\u00fb' && b0 <= '\u00fe') {
                final int w = this.getCard8();
                this.args[this.arg_count] = new Integer(-(b0 - '\u00fb') * 256 - w - 108);
                ++this.arg_count;
            }
            else if (b0 == '\u00ff') {
                final int first = this.getCard8();
                final int second = this.getCard8();
                final int third = this.getCard8();
                final int fourth = this.getCard8();
                this.args[this.arg_count] = new Integer(first << 24 | second << 16 | third << 8 | fourth);
                ++this.arg_count;
            }
            else {
                if (b0 > '\u001f' || b0 == '\u001c') {
                    continue;
                }
                gotKey = true;
                if (b0 == '\f') {
                    int b2 = this.getCard8();
                    if (b2 > CFFFontSubset.SubrsEscapeFuncs.length - 1) {
                        b2 = CFFFontSubset.SubrsEscapeFuncs.length - 1;
                    }
                    this.key = CFFFontSubset.SubrsEscapeFuncs[b2];
                }
                else {
                    this.key = CFFFontSubset.SubrsFunctions[b0];
                }
            }
        }
    }
    
    protected int CalcHints(final int begin, final int end, final int LBias, final int GBias, final int[] LSubrsOffsets) {
        this.seek(begin);
        while (this.getPosition() < end) {
            this.ReadCommand();
            final int pos = this.getPosition();
            Object TopElement = null;
            if (this.arg_count > 0) {
                TopElement = this.args[this.arg_count - 1];
            }
            final int NumOfArgs = this.arg_count;
            this.HandelStack();
            if (this.key == "callsubr") {
                if (NumOfArgs <= 0) {
                    continue;
                }
                final int Subr = (int)TopElement + LBias;
                this.CalcHints(LSubrsOffsets[Subr], LSubrsOffsets[Subr + 1], LBias, GBias, LSubrsOffsets);
                this.seek(pos);
            }
            else if (this.key == "callgsubr") {
                if (NumOfArgs <= 0) {
                    continue;
                }
                final int Subr = (int)TopElement + GBias;
                this.CalcHints(this.gsubrOffsets[Subr], this.gsubrOffsets[Subr + 1], LBias, GBias, LSubrsOffsets);
                this.seek(pos);
            }
            else if (this.key == "hstem" || this.key == "vstem" || this.key == "hstemhm" || this.key == "vstemhm") {
                this.NumOfHints += NumOfArgs / 2;
            }
            else {
                if (this.key != "hintmask" && this.key != "cntrmask") {
                    continue;
                }
                int SizeOfMask = this.NumOfHints / 8;
                if (this.NumOfHints % 8 != 0 || SizeOfMask == 0) {
                    ++SizeOfMask;
                }
                for (int i = 0; i < SizeOfMask; ++i) {
                    this.getCard8();
                }
            }
        }
        return this.NumOfHints;
    }
    
    protected byte[] BuildNewIndex(final int[] Offsets, final HashMap Used, final byte OperatorForUnusedEntries) throws IOException {
        int unusedCount = 0;
        int Offset = 0;
        final int[] NewOffsets = new int[Offsets.length];
        for (int i = 0; i < Offsets.length; ++i) {
            NewOffsets[i] = Offset;
            if (Used.containsKey(new Integer(i))) {
                Offset += Offsets[i + 1] - Offsets[i];
            }
            else {
                ++unusedCount;
            }
        }
        final byte[] NewObjects = new byte[Offset + unusedCount];
        int unusedOffset = 0;
        for (int j = 0; j < Offsets.length - 1; ++j) {
            final int start = NewOffsets[j];
            final int end = NewOffsets[j + 1];
            NewOffsets[j] = start + unusedOffset;
            if (start != end) {
                this.buf.seek(Offsets[j]);
                this.buf.readFully(NewObjects, start + unusedOffset, end - start);
            }
            else {
                NewObjects[start + unusedOffset] = OperatorForUnusedEntries;
                ++unusedOffset;
            }
        }
        final int[] array = NewOffsets;
        final int n = Offsets.length - 1;
        array[n] += unusedOffset;
        return this.AssembleIndex(NewOffsets, NewObjects);
    }
    
    protected byte[] AssembleIndex(final int[] NewOffsets, final byte[] NewObjects) {
        final char Count = (char)(NewOffsets.length - 1);
        final int Size = NewOffsets[NewOffsets.length - 1];
        byte Offsize;
        if (Size <= 255) {
            Offsize = 1;
        }
        else if (Size <= 65535) {
            Offsize = 2;
        }
        else if (Size <= 16777215) {
            Offsize = 3;
        }
        else {
            Offsize = 4;
        }
        final byte[] NewIndex = new byte[3 + Offsize * (Count + '\u0001') + NewObjects.length];
        int Place = 0;
        NewIndex[Place++] = (byte)(Count >>> 8 & 0xFF);
        NewIndex[Place++] = (byte)(Count >>> 0 & 0xFF);
        NewIndex[Place++] = Offsize;
        for (int i = 0; i < NewOffsets.length; ++i) {
            final int Num = NewOffsets[i] - NewOffsets[0] + 1;
            switch (Offsize) {
                case 4: {
                    NewIndex[Place++] = (byte)(Num >>> 24 & 0xFF);
                }
                case 3: {
                    NewIndex[Place++] = (byte)(Num >>> 16 & 0xFF);
                }
                case 2: {
                    NewIndex[Place++] = (byte)(Num >>> 8 & 0xFF);
                }
                case 1: {
                    NewIndex[Place++] = (byte)(Num >>> 0 & 0xFF);
                    break;
                }
            }
        }
        for (int i = 0; i < NewObjects.length; ++i) {
            NewIndex[Place++] = NewObjects[i];
        }
        return NewIndex;
    }
    
    protected byte[] BuildNewFile(final int Font) {
        this.OutputList = new LinkedList();
        this.CopyHeader();
        this.BuildIndexHeader(1, 1, 1);
        this.OutputList.addLast(new UInt8Item((char)(1 + this.fonts[Font].name.length())));
        this.OutputList.addLast(new StringItem(this.fonts[Font].name));
        this.BuildIndexHeader(1, 2, 1);
        final OffsetItem topdictIndex1Ref = new IndexOffsetItem(2);
        this.OutputList.addLast(topdictIndex1Ref);
        final IndexBaseItem topdictBase = new IndexBaseItem();
        this.OutputList.addLast(topdictBase);
        final OffsetItem charsetRef = new DictOffsetItem();
        final OffsetItem charstringsRef = new DictOffsetItem();
        final OffsetItem fdarrayRef = new DictOffsetItem();
        final OffsetItem fdselectRef = new DictOffsetItem();
        final OffsetItem privateRef = new DictOffsetItem();
        if (!this.fonts[Font].isCID) {
            this.OutputList.addLast(new DictNumberItem(this.fonts[Font].nstrings));
            this.OutputList.addLast(new DictNumberItem(this.fonts[Font].nstrings + 1));
            this.OutputList.addLast(new DictNumberItem(0));
            this.OutputList.addLast(new UInt8Item('\f'));
            this.OutputList.addLast(new UInt8Item('\u001e'));
            this.OutputList.addLast(new DictNumberItem(this.fonts[Font].nglyphs));
            this.OutputList.addLast(new UInt8Item('\f'));
            this.OutputList.addLast(new UInt8Item('\"'));
        }
        this.seek(this.topdictOffsets[Font]);
        while (this.getPosition() < this.topdictOffsets[Font + 1]) {
            final int p1 = this.getPosition();
            this.getDictItem();
            final int p2 = this.getPosition();
            if (this.key != "Encoding" && this.key != "Private" && this.key != "FDSelect" && this.key != "FDArray" && this.key != "charset") {
                if (this.key == "CharStrings") {
                    continue;
                }
                this.OutputList.add(new RangeItem(this.buf, p1, p2 - p1));
            }
        }
        this.CreateKeys(fdarrayRef, fdselectRef, charsetRef, charstringsRef);
        this.OutputList.addLast(new IndexMarkerItem(topdictIndex1Ref, topdictBase));
        if (this.fonts[Font].isCID) {
            this.OutputList.addLast(this.getEntireIndexRange(this.stringIndexOffset));
        }
        else {
            this.CreateNewStringIndex(Font);
        }
        this.OutputList.addLast(new RangeItem(new RandomAccessFileOrArray(this.NewGSubrsIndex), 0, this.NewGSubrsIndex.length));
        if (this.fonts[Font].isCID) {
            this.OutputList.addLast(new MarkerItem(fdselectRef));
            if (this.fonts[Font].fdselectOffset >= 0) {
                this.OutputList.addLast(new RangeItem(this.buf, this.fonts[Font].fdselectOffset, this.fonts[Font].FDSelectLength));
            }
            else {
                this.CreateFDSelect(fdselectRef, this.fonts[Font].nglyphs);
            }
            this.OutputList.addLast(new MarkerItem(charsetRef));
            this.OutputList.addLast(new RangeItem(this.buf, this.fonts[Font].charsetOffset, this.fonts[Font].CharsetLength));
            if (this.fonts[Font].fdarrayOffset >= 0) {
                this.OutputList.addLast(new MarkerItem(fdarrayRef));
                this.Reconstruct(Font);
            }
            else {
                this.CreateFDArray(fdarrayRef, privateRef, Font);
            }
        }
        else {
            this.CreateFDSelect(fdselectRef, this.fonts[Font].nglyphs);
            this.CreateCharset(charsetRef, this.fonts[Font].nglyphs);
            this.CreateFDArray(fdarrayRef, privateRef, Font);
        }
        if (this.fonts[Font].privateOffset >= 0) {
            final IndexBaseItem PrivateBase = new IndexBaseItem();
            this.OutputList.addLast(PrivateBase);
            this.OutputList.addLast(new MarkerItem(privateRef));
            final OffsetItem Subr = new DictOffsetItem();
            this.CreateNonCIDPrivate(Font, Subr);
            this.CreateNonCIDSubrs(Font, PrivateBase, Subr);
        }
        this.OutputList.addLast(new MarkerItem(charstringsRef));
        this.OutputList.addLast(new RangeItem(new RandomAccessFileOrArray(this.NewCharStringsIndex), 0, this.NewCharStringsIndex.length));
        final int[] currentOffset = { 0 };
        for (final Item item : this.OutputList) {
            item.increment(currentOffset);
        }
        for (final Item item : this.OutputList) {
            item.xref();
        }
        final int size = currentOffset[0];
        final byte[] b = new byte[size];
        for (final Item item2 : this.OutputList) {
            item2.emit(b);
        }
        return b;
    }
    
    protected void CopyHeader() {
        this.seek(0);
        final int major = this.getCard8();
        final int minor = this.getCard8();
        final int hdrSize = this.getCard8();
        final int offSize = this.getCard8();
        this.nextIndexOffset = hdrSize;
        this.OutputList.addLast(new RangeItem(this.buf, 0, hdrSize));
    }
    
    protected void BuildIndexHeader(final int Count, final int Offsize, final int First) {
        this.OutputList.addLast(new UInt16Item((char)Count));
        this.OutputList.addLast(new UInt8Item((char)Offsize));
        switch (Offsize) {
            case 1: {
                this.OutputList.addLast(new UInt8Item((char)First));
                break;
            }
            case 2: {
                this.OutputList.addLast(new UInt16Item((char)First));
                break;
            }
            case 3: {
                this.OutputList.addLast(new UInt24Item((char)First));
                break;
            }
            case 4: {
                this.OutputList.addLast(new UInt32Item((char)First));
                break;
            }
        }
    }
    
    protected void CreateKeys(final OffsetItem fdarrayRef, final OffsetItem fdselectRef, final OffsetItem charsetRef, final OffsetItem charstringsRef) {
        this.OutputList.addLast(fdarrayRef);
        this.OutputList.addLast(new UInt8Item('\f'));
        this.OutputList.addLast(new UInt8Item('$'));
        this.OutputList.addLast(fdselectRef);
        this.OutputList.addLast(new UInt8Item('\f'));
        this.OutputList.addLast(new UInt8Item('%'));
        this.OutputList.addLast(charsetRef);
        this.OutputList.addLast(new UInt8Item('\u000f'));
        this.OutputList.addLast(charstringsRef);
        this.OutputList.addLast(new UInt8Item('\u0011'));
    }
    
    protected void CreateNewStringIndex(final int Font) {
        String fdFontName = this.fonts[Font].name + "-OneRange";
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
        this.OutputList.addLast(new UInt16Item((char)(this.stringOffsets.length - 1 + 3)));
        this.OutputList.addLast(new UInt8Item((char)stringsIndexOffSize));
        for (int i = 0; i < this.stringOffsets.length; ++i) {
            this.OutputList.addLast(new IndexOffsetItem(stringsIndexOffSize, this.stringOffsets[i] - stringsBaseOffset));
        }
        int currentStringsOffset = this.stringOffsets[this.stringOffsets.length - 1] - stringsBaseOffset;
        currentStringsOffset += "Adobe".length();
        this.OutputList.addLast(new IndexOffsetItem(stringsIndexOffSize, currentStringsOffset));
        currentStringsOffset += "Identity".length();
        this.OutputList.addLast(new IndexOffsetItem(stringsIndexOffSize, currentStringsOffset));
        currentStringsOffset += fdFontName.length();
        this.OutputList.addLast(new IndexOffsetItem(stringsIndexOffSize, currentStringsOffset));
        this.OutputList.addLast(new RangeItem(this.buf, this.stringOffsets[0], origStringsLen));
        this.OutputList.addLast(new StringItem(extraStrings));
    }
    
    protected void CreateFDSelect(final OffsetItem fdselectRef, final int nglyphs) {
        this.OutputList.addLast(new MarkerItem(fdselectRef));
        this.OutputList.addLast(new UInt8Item('\u0003'));
        this.OutputList.addLast(new UInt16Item('\u0001'));
        this.OutputList.addLast(new UInt16Item('\0'));
        this.OutputList.addLast(new UInt8Item('\0'));
        this.OutputList.addLast(new UInt16Item((char)nglyphs));
    }
    
    protected void CreateCharset(final OffsetItem charsetRef, final int nglyphs) {
        this.OutputList.addLast(new MarkerItem(charsetRef));
        this.OutputList.addLast(new UInt8Item('\u0002'));
        this.OutputList.addLast(new UInt16Item('\u0001'));
        this.OutputList.addLast(new UInt16Item((char)(nglyphs - 1)));
    }
    
    protected void CreateFDArray(final OffsetItem fdarrayRef, final OffsetItem privateRef, final int Font) {
        this.OutputList.addLast(new MarkerItem(fdarrayRef));
        this.BuildIndexHeader(1, 1, 1);
        final OffsetItem privateIndex1Ref = new IndexOffsetItem(1);
        this.OutputList.addLast(privateIndex1Ref);
        final IndexBaseItem privateBase = new IndexBaseItem();
        this.OutputList.addLast(privateBase);
        int NewSize = this.fonts[Font].privateLength;
        final int OrgSubrsOffsetSize = this.CalcSubrOffsetSize(this.fonts[Font].privateOffset, this.fonts[Font].privateLength);
        if (OrgSubrsOffsetSize != 0) {
            NewSize += 5 - OrgSubrsOffsetSize;
        }
        this.OutputList.addLast(new DictNumberItem(NewSize));
        this.OutputList.addLast(privateRef);
        this.OutputList.addLast(new UInt8Item('\u0012'));
        this.OutputList.addLast(new IndexMarkerItem(privateIndex1Ref, privateBase));
    }
    
    void Reconstruct(final int Font) {
        final OffsetItem[] fdPrivate = new DictOffsetItem[this.fonts[Font].FDArrayOffsets.length - 1];
        final IndexBaseItem[] fdPrivateBase = new IndexBaseItem[this.fonts[Font].fdprivateOffsets.length];
        final OffsetItem[] fdSubrs = new DictOffsetItem[this.fonts[Font].fdprivateOffsets.length];
        this.ReconstructFDArray(Font, fdPrivate);
        this.ReconstructPrivateDict(Font, fdPrivate, fdPrivateBase, fdSubrs);
        this.ReconstructPrivateSubrs(Font, fdPrivateBase, fdSubrs);
    }
    
    void ReconstructFDArray(final int Font, final OffsetItem[] fdPrivate) {
        this.BuildIndexHeader(this.fonts[Font].FDArrayCount, this.fonts[Font].FDArrayOffsize, 1);
        final OffsetItem[] fdOffsets = new IndexOffsetItem[this.fonts[Font].FDArrayOffsets.length - 1];
        for (int i = 0; i < this.fonts[Font].FDArrayOffsets.length - 1; ++i) {
            fdOffsets[i] = new IndexOffsetItem(this.fonts[Font].FDArrayOffsize);
            this.OutputList.addLast(fdOffsets[i]);
        }
        final IndexBaseItem fdArrayBase = new IndexBaseItem();
        this.OutputList.addLast(fdArrayBase);
        for (int k = 0; k < this.fonts[Font].FDArrayOffsets.length - 1; ++k) {
            if (this.FDArrayUsed.containsKey(new Integer(k))) {
                this.seek(this.fonts[Font].FDArrayOffsets[k]);
                while (this.getPosition() < this.fonts[Font].FDArrayOffsets[k + 1]) {
                    final int p1 = this.getPosition();
                    this.getDictItem();
                    final int p2 = this.getPosition();
                    if (this.key == "Private") {
                        int NewSize = (int)this.args[0];
                        final int OrgSubrsOffsetSize = this.CalcSubrOffsetSize(this.fonts[Font].fdprivateOffsets[k], this.fonts[Font].fdprivateLengths[k]);
                        if (OrgSubrsOffsetSize != 0) {
                            NewSize += 5 - OrgSubrsOffsetSize;
                        }
                        this.OutputList.addLast(new DictNumberItem(NewSize));
                        fdPrivate[k] = new DictOffsetItem();
                        this.OutputList.addLast(fdPrivate[k]);
                        this.OutputList.addLast(new UInt8Item('\u0012'));
                        this.seek(p2);
                    }
                    else {
                        this.OutputList.addLast(new RangeItem(this.buf, p1, p2 - p1));
                    }
                }
            }
            this.OutputList.addLast(new IndexMarkerItem(fdOffsets[k], fdArrayBase));
        }
    }
    
    void ReconstructPrivateDict(final int Font, final OffsetItem[] fdPrivate, final IndexBaseItem[] fdPrivateBase, final OffsetItem[] fdSubrs) {
        for (int i = 0; i < this.fonts[Font].fdprivateOffsets.length; ++i) {
            if (this.FDArrayUsed.containsKey(new Integer(i))) {
                this.OutputList.addLast(new MarkerItem(fdPrivate[i]));
                fdPrivateBase[i] = new IndexBaseItem();
                this.OutputList.addLast(fdPrivateBase[i]);
                this.seek(this.fonts[Font].fdprivateOffsets[i]);
                while (this.getPosition() < this.fonts[Font].fdprivateOffsets[i] + this.fonts[Font].fdprivateLengths[i]) {
                    final int p1 = this.getPosition();
                    this.getDictItem();
                    final int p2 = this.getPosition();
                    if (this.key == "Subrs") {
                        fdSubrs[i] = new DictOffsetItem();
                        this.OutputList.addLast(fdSubrs[i]);
                        this.OutputList.addLast(new UInt8Item('\u0013'));
                    }
                    else {
                        this.OutputList.addLast(new RangeItem(this.buf, p1, p2 - p1));
                    }
                }
            }
        }
    }
    
    void ReconstructPrivateSubrs(final int Font, final IndexBaseItem[] fdPrivateBase, final OffsetItem[] fdSubrs) {
        for (int i = 0; i < this.fonts[Font].fdprivateLengths.length; ++i) {
            if (fdSubrs[i] != null && this.fonts[Font].PrivateSubrsOffset[i] >= 0) {
                this.OutputList.addLast(new SubrMarkerItem(fdSubrs[i], fdPrivateBase[i]));
                this.OutputList.addLast(new RangeItem(new RandomAccessFileOrArray(this.NewLSubrsIndex[i]), 0, this.NewLSubrsIndex[i].length));
            }
        }
    }
    
    int CalcSubrOffsetSize(final int Offset, final int Size) {
        int OffsetSize = 0;
        this.seek(Offset);
        while (this.getPosition() < Offset + Size) {
            final int p1 = this.getPosition();
            this.getDictItem();
            final int p2 = this.getPosition();
            if (this.key == "Subrs") {
                OffsetSize = p2 - p1 - 1;
            }
        }
        return OffsetSize;
    }
    
    protected int countEntireIndexRange(final int indexOffset) {
        this.seek(indexOffset);
        final int count = this.getCard16();
        if (count == 0) {
            return 2;
        }
        final int indexOffSize = this.getCard8();
        this.seek(indexOffset + 2 + 1 + count * indexOffSize);
        final int size = this.getOffset(indexOffSize) - 1;
        return 3 + (count + 1) * indexOffSize + size;
    }
    
    void CreateNonCIDPrivate(final int Font, final OffsetItem Subr) {
        this.seek(this.fonts[Font].privateOffset);
        while (this.getPosition() < this.fonts[Font].privateOffset + this.fonts[Font].privateLength) {
            final int p1 = this.getPosition();
            this.getDictItem();
            final int p2 = this.getPosition();
            if (this.key == "Subrs") {
                this.OutputList.addLast(Subr);
                this.OutputList.addLast(new UInt8Item('\u0013'));
            }
            else {
                this.OutputList.addLast(new RangeItem(this.buf, p1, p2 - p1));
            }
        }
    }
    
    void CreateNonCIDSubrs(final int Font, final IndexBaseItem PrivateBase, final OffsetItem Subrs) {
        this.OutputList.addLast(new SubrMarkerItem(Subrs, PrivateBase));
        this.OutputList.addLast(new RangeItem(new RandomAccessFileOrArray(this.NewSubrsIndexNonCID), 0, this.NewSubrsIndexNonCID.length));
    }
    
    static {
        SubrsFunctions = new String[] { "RESERVED_0", "hstem", "RESERVED_2", "vstem", "vmoveto", "rlineto", "hlineto", "vlineto", "rrcurveto", "RESERVED_9", "callsubr", "return", "escape", "RESERVED_13", "endchar", "RESERVED_15", "RESERVED_16", "RESERVED_17", "hstemhm", "hintmask", "cntrmask", "rmoveto", "hmoveto", "vstemhm", "rcurveline", "rlinecurve", "vvcurveto", "hhcurveto", "shortint", "callgsubr", "vhcurveto", "hvcurveto" };
        SubrsEscapeFuncs = new String[] { "RESERVED_0", "RESERVED_1", "RESERVED_2", "and", "or", "not", "RESERVED_6", "RESERVED_7", "RESERVED_8", "abs", "add", "sub", "div", "RESERVED_13", "neg", "eq", "RESERVED_16", "RESERVED_17", "drop", "RESERVED_19", "put", "get", "ifelse", "random", "mul", "RESERVED_25", "sqrt", "dup", "exch", "index", "roll", "RESERVED_31", "RESERVED_32", "RESERVED_33", "hflex", "flex", "hflex1", "flex1", "RESERVED_REST" };
    }
}
