package com.lowagie.text.pdf;

import com.lowagie.text.Utilities;
import java.util.Collection;
import java.util.ArrayList;

public class BidiLine
{
    protected int runDirection;
    protected int pieceSize;
    protected char[] text;
    protected PdfChunk[] detailChunks;
    protected int totalTextLength;
    protected byte[] orderLevels;
    protected int[] indexChars;
    protected ArrayList chunks;
    protected int indexChunk;
    protected int indexChunkChar;
    protected int currentChar;
    protected int storedRunDirection;
    protected char[] storedText;
    protected PdfChunk[] storedDetailChunks;
    protected int storedTotalTextLength;
    protected byte[] storedOrderLevels;
    protected int[] storedIndexChars;
    protected int storedIndexChunk;
    protected int storedIndexChunkChar;
    protected int storedCurrentChar;
    protected boolean shortStore;
    protected static final IntHashtable mirrorChars;
    protected int arabicOptions;
    
    public BidiLine() {
        this.pieceSize = 256;
        this.text = new char[this.pieceSize];
        this.detailChunks = new PdfChunk[this.pieceSize];
        this.totalTextLength = 0;
        this.orderLevels = new byte[this.pieceSize];
        this.indexChars = new int[this.pieceSize];
        this.chunks = new ArrayList();
        this.indexChunk = 0;
        this.indexChunkChar = 0;
        this.currentChar = 0;
        this.storedText = new char[0];
        this.storedDetailChunks = new PdfChunk[0];
        this.storedTotalTextLength = 0;
        this.storedOrderLevels = new byte[0];
        this.storedIndexChars = new int[0];
        this.storedIndexChunk = 0;
        this.storedIndexChunkChar = 0;
        this.storedCurrentChar = 0;
    }
    
    public BidiLine(final BidiLine org) {
        this.pieceSize = 256;
        this.text = new char[this.pieceSize];
        this.detailChunks = new PdfChunk[this.pieceSize];
        this.totalTextLength = 0;
        this.orderLevels = new byte[this.pieceSize];
        this.indexChars = new int[this.pieceSize];
        this.chunks = new ArrayList();
        this.indexChunk = 0;
        this.indexChunkChar = 0;
        this.currentChar = 0;
        this.storedText = new char[0];
        this.storedDetailChunks = new PdfChunk[0];
        this.storedTotalTextLength = 0;
        this.storedOrderLevels = new byte[0];
        this.storedIndexChars = new int[0];
        this.storedIndexChunk = 0;
        this.storedIndexChunkChar = 0;
        this.storedCurrentChar = 0;
        this.runDirection = org.runDirection;
        this.pieceSize = org.pieceSize;
        this.text = org.text.clone();
        this.detailChunks = org.detailChunks.clone();
        this.totalTextLength = org.totalTextLength;
        this.orderLevels = org.orderLevels.clone();
        this.indexChars = org.indexChars.clone();
        this.chunks = new ArrayList(org.chunks);
        this.indexChunk = org.indexChunk;
        this.indexChunkChar = org.indexChunkChar;
        this.currentChar = org.currentChar;
        this.storedRunDirection = org.storedRunDirection;
        this.storedText = org.storedText.clone();
        this.storedDetailChunks = org.storedDetailChunks.clone();
        this.storedTotalTextLength = org.storedTotalTextLength;
        this.storedOrderLevels = org.storedOrderLevels.clone();
        this.storedIndexChars = org.storedIndexChars.clone();
        this.storedIndexChunk = org.storedIndexChunk;
        this.storedIndexChunkChar = org.storedIndexChunkChar;
        this.storedCurrentChar = org.storedCurrentChar;
        this.shortStore = org.shortStore;
        this.arabicOptions = org.arabicOptions;
    }
    
    public boolean isEmpty() {
        return this.currentChar >= this.totalTextLength && this.indexChunk >= this.chunks.size();
    }
    
    public void clearChunks() {
        this.chunks.clear();
        this.totalTextLength = 0;
        this.currentChar = 0;
    }
    
    public boolean getParagraph(final int runDirection) {
        this.runDirection = runDirection;
        this.currentChar = 0;
        this.totalTextLength = 0;
        boolean hasText = false;
        while (this.indexChunk < this.chunks.size()) {
            final PdfChunk ck = this.chunks.get(this.indexChunk);
            final BaseFont bf = ck.font().getFont();
            final String s = ck.toString();
            final int len = s.length();
            while (this.indexChunkChar < len) {
                final char c = s.charAt(this.indexChunkChar);
                final char uniC = (char)bf.getUnicodeEquivalent(c);
                if (uniC == '\r' || uniC == '\n') {
                    if (uniC == '\r' && this.indexChunkChar + 1 < len && s.charAt(this.indexChunkChar + 1) == '\n') {
                        ++this.indexChunkChar;
                    }
                    ++this.indexChunkChar;
                    if (this.indexChunkChar >= len) {
                        this.indexChunkChar = 0;
                        ++this.indexChunk;
                    }
                    hasText = true;
                    if (this.totalTextLength == 0) {
                        this.detailChunks[0] = ck;
                        break;
                    }
                    break;
                }
                else {
                    this.addPiece(c, ck);
                    ++this.indexChunkChar;
                }
            }
            if (hasText) {
                break;
            }
            this.indexChunkChar = 0;
            ++this.indexChunk;
        }
        if (this.totalTextLength == 0) {
            return hasText;
        }
        this.totalTextLength = this.trimRight(0, this.totalTextLength - 1) + 1;
        if (this.totalTextLength == 0) {
            return true;
        }
        this.totalTextLength = this.trimRightEx(0, this.totalTextLength - 1) + 1;
        return true;
    }
    
    public void addChunk(final PdfChunk chunk) {
        this.chunks.add(chunk);
    }
    
    public void addChunks(final ArrayList chunks) {
        this.chunks.addAll(chunks);
    }
    
    public void addPiece(final char c, final PdfChunk chunk) {
        if (this.totalTextLength >= this.pieceSize) {
            final char[] tempText = this.text;
            final PdfChunk[] tempDetailChunks = this.detailChunks;
            this.pieceSize *= 2;
            this.text = new char[this.pieceSize];
            this.detailChunks = new PdfChunk[this.pieceSize];
            System.arraycopy(tempText, 0, this.text, 0, this.totalTextLength);
            System.arraycopy(tempDetailChunks, 0, this.detailChunks, 0, this.totalTextLength);
        }
        this.text[this.totalTextLength] = c;
        this.detailChunks[this.totalTextLength++] = chunk;
    }
    
    public void save() {
        if (this.indexChunk > 0) {
            if (this.indexChunk >= this.chunks.size()) {
                this.chunks.clear();
            }
            else {
                --this.indexChunk;
                while (this.indexChunk >= 0) {
                    this.chunks.remove(this.indexChunk);
                    --this.indexChunk;
                }
            }
            this.indexChunk = 0;
        }
        this.storedRunDirection = this.runDirection;
        this.storedTotalTextLength = this.totalTextLength;
        this.storedIndexChunk = this.indexChunk;
        this.storedIndexChunkChar = this.indexChunkChar;
        this.storedCurrentChar = this.currentChar;
        if (!(this.shortStore = (this.currentChar < this.totalTextLength))) {
            if (this.storedText.length < this.totalTextLength) {
                this.storedText = new char[this.totalTextLength];
                this.storedDetailChunks = new PdfChunk[this.totalTextLength];
            }
            System.arraycopy(this.text, 0, this.storedText, 0, this.totalTextLength);
            System.arraycopy(this.detailChunks, 0, this.storedDetailChunks, 0, this.totalTextLength);
        }
        if (this.runDirection == 2 || this.runDirection == 3) {
            if (this.storedOrderLevels.length < this.totalTextLength) {
                this.storedOrderLevels = new byte[this.totalTextLength];
                this.storedIndexChars = new int[this.totalTextLength];
            }
            System.arraycopy(this.orderLevels, this.currentChar, this.storedOrderLevels, this.currentChar, this.totalTextLength - this.currentChar);
            System.arraycopy(this.indexChars, this.currentChar, this.storedIndexChars, this.currentChar, this.totalTextLength - this.currentChar);
        }
    }
    
    public void restore() {
        this.runDirection = this.storedRunDirection;
        this.totalTextLength = this.storedTotalTextLength;
        this.indexChunk = this.storedIndexChunk;
        this.indexChunkChar = this.storedIndexChunkChar;
        this.currentChar = this.storedCurrentChar;
        if (!this.shortStore) {
            System.arraycopy(this.storedText, 0, this.text, 0, this.totalTextLength);
            System.arraycopy(this.storedDetailChunks, 0, this.detailChunks, 0, this.totalTextLength);
        }
        if (this.runDirection == 2 || this.runDirection == 3) {
            System.arraycopy(this.storedOrderLevels, this.currentChar, this.orderLevels, this.currentChar, this.totalTextLength - this.currentChar);
            System.arraycopy(this.storedIndexChars, this.currentChar, this.indexChars, this.currentChar, this.totalTextLength - this.currentChar);
        }
    }
    
    public void mirrorGlyphs() {
        for (int k = 0; k < this.totalTextLength; ++k) {
            if ((this.orderLevels[k] & 0x1) == 0x1) {
                final int mirror = BidiLine.mirrorChars.get(this.text[k]);
                if (mirror != 0) {
                    this.text[k] = (char)mirror;
                }
            }
        }
    }
    
    public void doArabicShapping() {
        int src = 0;
        int dest = 0;
        while (true) {
            if (src < this.totalTextLength) {
                final char c = this.text[src];
                if (c < '\u0600' || c > '\u06ff') {
                    if (src != dest) {
                        this.text[dest] = this.text[src];
                        this.detailChunks[dest] = this.detailChunks[src];
                        this.orderLevels[dest] = this.orderLevels[src];
                    }
                    ++src;
                    ++dest;
                    continue;
                }
            }
            if (src >= this.totalTextLength) {
                break;
            }
            int startArabicIdx = src;
            ++src;
            while (src < this.totalTextLength) {
                final char c2 = this.text[src];
                if (c2 < '\u0600') {
                    break;
                }
                if (c2 > '\u06ff') {
                    break;
                }
                ++src;
            }
            final int arabicWordSize = src - startArabicIdx;
            final int size = ArabicLigaturizer.arabic_shape(this.text, startArabicIdx, arabicWordSize, this.text, dest, arabicWordSize, this.arabicOptions);
            if (startArabicIdx != dest) {
                for (int k = 0; k < size; ++k) {
                    this.detailChunks[dest] = this.detailChunks[startArabicIdx];
                    this.orderLevels[dest++] = this.orderLevels[startArabicIdx++];
                }
            }
            else {
                dest += size;
            }
        }
        this.totalTextLength = dest;
    }
    
    public PdfLine processLine(final float leftX, float width, final int alignment, final int runDirection, final int arabicOptions) {
        this.arabicOptions = arabicOptions;
        this.save();
        final boolean isRTL = runDirection == 3;
        if (this.currentChar >= this.totalTextLength) {
            final boolean hasText = this.getParagraph(runDirection);
            if (!hasText) {
                return null;
            }
            if (this.totalTextLength == 0) {
                final ArrayList ar = new ArrayList();
                final PdfChunk ck = new PdfChunk("", this.detailChunks[0]);
                ar.add(ck);
                return new PdfLine(0.0f, 0.0f, 0.0f, alignment, true, ar, isRTL);
            }
        }
        final float originalWidth = width;
        int lastSplit = -1;
        if (this.currentChar != 0) {
            this.currentChar = this.trimLeftEx(this.currentChar, this.totalTextLength - 1);
        }
        final int oldCurrentChar = this.currentChar;
        int uniC = 0;
        PdfChunk ck2 = null;
        float charWidth = 0.0f;
        PdfChunk lastValidChunk = null;
        boolean splitChar = false;
        boolean surrogate = false;
        while (this.currentChar < this.totalTextLength) {
            ck2 = this.detailChunks[this.currentChar];
            surrogate = Utilities.isSurrogatePair(this.text, this.currentChar);
            if (surrogate) {
                uniC = ck2.getUnicodeEquivalent(Utilities.convertToUtf32(this.text, this.currentChar));
            }
            else {
                uniC = ck2.getUnicodeEquivalent(this.text[this.currentChar]);
            }
            if (!PdfChunk.noPrint(uniC)) {
                if (surrogate) {
                    charWidth = ck2.getCharWidth(uniC);
                }
                else {
                    charWidth = ck2.getCharWidth(this.text[this.currentChar]);
                }
                splitChar = ck2.isExtSplitCharacter(oldCurrentChar, this.currentChar, this.totalTextLength, this.text, this.detailChunks);
                if (splitChar && Character.isWhitespace((char)uniC)) {
                    lastSplit = this.currentChar;
                }
                if (width - charWidth < 0.0f) {
                    break;
                }
                if (splitChar) {
                    lastSplit = this.currentChar;
                }
                width -= charWidth;
                lastValidChunk = ck2;
                if (ck2.isTab()) {
                    final Object[] tab = (Object[])ck2.getAttribute("TAB");
                    final float tabPosition = (float)tab[1];
                    final boolean newLine = (boolean)tab[2];
                    if (newLine && tabPosition < originalWidth - width) {
                        return new PdfLine(0.0f, originalWidth, width, alignment, true, this.createArrayOfPdfChunks(oldCurrentChar, this.currentChar - 1), isRTL);
                    }
                    this.detailChunks[this.currentChar].adjustLeft(leftX);
                    width = originalWidth - tabPosition;
                }
                if (surrogate) {
                    ++this.currentChar;
                }
            }
            ++this.currentChar;
        }
        if (lastValidChunk == null) {
            ++this.currentChar;
            if (surrogate) {
                ++this.currentChar;
            }
            return new PdfLine(0.0f, originalWidth, 0.0f, alignment, false, this.createArrayOfPdfChunks(this.currentChar - 1, this.currentChar - 1), isRTL);
        }
        if (this.currentChar >= this.totalTextLength) {
            return new PdfLine(0.0f, originalWidth, width, alignment, true, this.createArrayOfPdfChunks(oldCurrentChar, this.totalTextLength - 1), isRTL);
        }
        int newCurrentChar = this.trimRightEx(oldCurrentChar, this.currentChar - 1);
        if (newCurrentChar < oldCurrentChar) {
            return new PdfLine(0.0f, originalWidth, width, alignment, false, this.createArrayOfPdfChunks(oldCurrentChar, this.currentChar - 1), isRTL);
        }
        if (newCurrentChar == this.currentChar - 1) {
            final HyphenationEvent he = (HyphenationEvent)lastValidChunk.getAttribute("HYPHENATION");
            if (he != null) {
                final int[] word = this.getWord(oldCurrentChar, newCurrentChar);
                if (word != null) {
                    final float testWidth = width + this.getWidth(word[0], this.currentChar - 1);
                    final String pre = he.getHyphenatedWordPre(new String(this.text, word[0], word[1] - word[0]), lastValidChunk.font().getFont(), lastValidChunk.font().size(), testWidth);
                    final String post = he.getHyphenatedWordPost();
                    if (pre.length() > 0) {
                        final PdfChunk extra = new PdfChunk(pre, lastValidChunk);
                        this.currentChar = word[1] - post.length();
                        return new PdfLine(0.0f, originalWidth, testWidth - lastValidChunk.font().width(pre), alignment, false, this.createArrayOfPdfChunks(oldCurrentChar, word[0] - 1, extra), isRTL);
                    }
                }
            }
        }
        if (lastSplit == -1 || lastSplit >= newCurrentChar) {
            return new PdfLine(0.0f, originalWidth, width + this.getWidth(newCurrentChar + 1, this.currentChar - 1), alignment, false, this.createArrayOfPdfChunks(oldCurrentChar, newCurrentChar), isRTL);
        }
        this.currentChar = lastSplit + 1;
        newCurrentChar = this.trimRightEx(oldCurrentChar, lastSplit);
        if (newCurrentChar < oldCurrentChar) {
            newCurrentChar = this.currentChar - 1;
        }
        return new PdfLine(0.0f, originalWidth, originalWidth - this.getWidth(oldCurrentChar, newCurrentChar), alignment, false, this.createArrayOfPdfChunks(oldCurrentChar, newCurrentChar), isRTL);
    }
    
    public float getWidth(int startIdx, final int lastIdx) {
        char c = '\0';
        PdfChunk ck = null;
        float width = 0.0f;
        while (startIdx <= lastIdx) {
            final boolean surrogate = Utilities.isSurrogatePair(this.text, startIdx);
            if (surrogate) {
                width += this.detailChunks[startIdx].getCharWidth(Utilities.convertToUtf32(this.text, startIdx));
                ++startIdx;
            }
            else {
                c = this.text[startIdx];
                ck = this.detailChunks[startIdx];
                if (!PdfChunk.noPrint(ck.getUnicodeEquivalent(c))) {
                    width += this.detailChunks[startIdx].getCharWidth(c);
                }
            }
            ++startIdx;
        }
        return width;
    }
    
    public ArrayList createArrayOfPdfChunks(final int startIdx, final int endIdx) {
        return this.createArrayOfPdfChunks(startIdx, endIdx, null);
    }
    
    public ArrayList createArrayOfPdfChunks(int startIdx, final int endIdx, final PdfChunk extraPdfChunk) {
        final boolean bidi = this.runDirection == 2 || this.runDirection == 3;
        if (bidi) {
            this.reorder(startIdx, endIdx);
        }
        final ArrayList ar = new ArrayList();
        PdfChunk refCk = this.detailChunks[startIdx];
        PdfChunk ck = null;
        StringBuffer buf = new StringBuffer();
        int idx = 0;
        while (startIdx <= endIdx) {
            idx = (bidi ? this.indexChars[startIdx] : startIdx);
            final char c = this.text[idx];
            ck = this.detailChunks[idx];
            if (!PdfChunk.noPrint(ck.getUnicodeEquivalent(c))) {
                if (ck.isImage() || ck.isSeparator() || ck.isTab()) {
                    if (buf.length() > 0) {
                        ar.add(new PdfChunk(buf.toString(), refCk));
                        buf = new StringBuffer();
                    }
                    ar.add(ck);
                }
                else if (ck == refCk) {
                    buf.append(c);
                }
                else {
                    if (buf.length() > 0) {
                        ar.add(new PdfChunk(buf.toString(), refCk));
                        buf = new StringBuffer();
                    }
                    if (!ck.isImage() && !ck.isSeparator() && !ck.isTab()) {
                        buf.append(c);
                    }
                    refCk = ck;
                }
            }
            ++startIdx;
        }
        if (buf.length() > 0) {
            ar.add(new PdfChunk(buf.toString(), refCk));
        }
        if (extraPdfChunk != null) {
            ar.add(extraPdfChunk);
        }
        return ar;
    }
    
    public int[] getWord(final int startIdx, final int idx) {
        int last = idx;
        int first = idx;
        while (last < this.totalTextLength && Character.isLetter(this.text[last])) {
            ++last;
        }
        if (last == idx) {
            return null;
        }
        while (first >= startIdx && Character.isLetter(this.text[first])) {
            --first;
        }
        ++first;
        return new int[] { first, last };
    }
    
    public int trimRight(final int startIdx, final int endIdx) {
        int idx;
        for (idx = endIdx; idx >= startIdx; --idx) {
            final char c = (char)this.detailChunks[idx].getUnicodeEquivalent(this.text[idx]);
            if (!isWS(c)) {
                break;
            }
        }
        return idx;
    }
    
    public int trimLeft(final int startIdx, final int endIdx) {
        int idx;
        for (idx = startIdx; idx <= endIdx; ++idx) {
            final char c = (char)this.detailChunks[idx].getUnicodeEquivalent(this.text[idx]);
            if (!isWS(c)) {
                break;
            }
        }
        return idx;
    }
    
    public int trimRightEx(final int startIdx, final int endIdx) {
        int idx = endIdx;
        char c = '\0';
        while (idx >= startIdx) {
            c = (char)this.detailChunks[idx].getUnicodeEquivalent(this.text[idx]);
            if (!isWS(c) && !PdfChunk.noPrint(c)) {
                break;
            }
            --idx;
        }
        return idx;
    }
    
    public int trimLeftEx(final int startIdx, final int endIdx) {
        int idx = startIdx;
        char c = '\0';
        while (idx <= endIdx) {
            c = (char)this.detailChunks[idx].getUnicodeEquivalent(this.text[idx]);
            if (!isWS(c) && !PdfChunk.noPrint(c)) {
                break;
            }
            ++idx;
        }
        return idx;
    }
    
    public void reorder(final int start, final int end) {
        byte minLevel;
        byte onlyOddLevels;
        byte onlyEvenLevels;
        byte maxLevel = onlyEvenLevels = (onlyOddLevels = (minLevel = this.orderLevels[start]));
        for (int k = start + 1; k <= end; ++k) {
            final byte b = this.orderLevels[k];
            if (b > maxLevel) {
                maxLevel = b;
            }
            else if (b < minLevel) {
                minLevel = b;
            }
            onlyOddLevels &= b;
            onlyEvenLevels |= b;
        }
        if ((onlyEvenLevels & 0x1) == 0x0) {
            return;
        }
        if ((onlyOddLevels & 0x1) == 0x1) {
            this.flip(start, end + 1);
            return;
        }
        for (minLevel |= 0x1; maxLevel >= minLevel; --maxLevel) {
            int pstart = start;
            while (true) {
                if (pstart <= end && this.orderLevels[pstart] < maxLevel) {
                    ++pstart;
                }
                else {
                    if (pstart > end) {
                        break;
                    }
                    int pend;
                    for (pend = pstart + 1; pend <= end && this.orderLevels[pend] >= maxLevel; ++pend) {}
                    this.flip(pstart, pend);
                    pstart = pend + 1;
                }
            }
        }
    }
    
    public void flip(int start, int end) {
        final int mid = (start + end) / 2;
        --end;
        while (start < mid) {
            final int temp = this.indexChars[start];
            this.indexChars[start] = this.indexChars[end];
            this.indexChars[end] = temp;
            ++start;
            --end;
        }
    }
    
    public static boolean isWS(final char c) {
        return c <= ' ';
    }
    
    static {
        (mirrorChars = new IntHashtable()).put(40, 41);
        BidiLine.mirrorChars.put(41, 40);
        BidiLine.mirrorChars.put(60, 62);
        BidiLine.mirrorChars.put(62, 60);
        BidiLine.mirrorChars.put(91, 93);
        BidiLine.mirrorChars.put(93, 91);
        BidiLine.mirrorChars.put(123, 125);
        BidiLine.mirrorChars.put(125, 123);
        BidiLine.mirrorChars.put(171, 187);
        BidiLine.mirrorChars.put(187, 171);
        BidiLine.mirrorChars.put(8249, 8250);
        BidiLine.mirrorChars.put(8250, 8249);
        BidiLine.mirrorChars.put(8261, 8262);
        BidiLine.mirrorChars.put(8262, 8261);
        BidiLine.mirrorChars.put(8317, 8318);
        BidiLine.mirrorChars.put(8318, 8317);
        BidiLine.mirrorChars.put(8333, 8334);
        BidiLine.mirrorChars.put(8334, 8333);
        BidiLine.mirrorChars.put(8712, 8715);
        BidiLine.mirrorChars.put(8713, 8716);
        BidiLine.mirrorChars.put(8714, 8717);
        BidiLine.mirrorChars.put(8715, 8712);
        BidiLine.mirrorChars.put(8716, 8713);
        BidiLine.mirrorChars.put(8717, 8714);
        BidiLine.mirrorChars.put(8725, 10741);
        BidiLine.mirrorChars.put(8764, 8765);
        BidiLine.mirrorChars.put(8765, 8764);
        BidiLine.mirrorChars.put(8771, 8909);
        BidiLine.mirrorChars.put(8786, 8787);
        BidiLine.mirrorChars.put(8787, 8786);
        BidiLine.mirrorChars.put(8788, 8789);
        BidiLine.mirrorChars.put(8789, 8788);
        BidiLine.mirrorChars.put(8804, 8805);
        BidiLine.mirrorChars.put(8805, 8804);
        BidiLine.mirrorChars.put(8806, 8807);
        BidiLine.mirrorChars.put(8807, 8806);
        BidiLine.mirrorChars.put(8808, 8809);
        BidiLine.mirrorChars.put(8809, 8808);
        BidiLine.mirrorChars.put(8810, 8811);
        BidiLine.mirrorChars.put(8811, 8810);
        BidiLine.mirrorChars.put(8814, 8815);
        BidiLine.mirrorChars.put(8815, 8814);
        BidiLine.mirrorChars.put(8816, 8817);
        BidiLine.mirrorChars.put(8817, 8816);
        BidiLine.mirrorChars.put(8818, 8819);
        BidiLine.mirrorChars.put(8819, 8818);
        BidiLine.mirrorChars.put(8820, 8821);
        BidiLine.mirrorChars.put(8821, 8820);
        BidiLine.mirrorChars.put(8822, 8823);
        BidiLine.mirrorChars.put(8823, 8822);
        BidiLine.mirrorChars.put(8824, 8825);
        BidiLine.mirrorChars.put(8825, 8824);
        BidiLine.mirrorChars.put(8826, 8827);
        BidiLine.mirrorChars.put(8827, 8826);
        BidiLine.mirrorChars.put(8828, 8829);
        BidiLine.mirrorChars.put(8829, 8828);
        BidiLine.mirrorChars.put(8830, 8831);
        BidiLine.mirrorChars.put(8831, 8830);
        BidiLine.mirrorChars.put(8832, 8833);
        BidiLine.mirrorChars.put(8833, 8832);
        BidiLine.mirrorChars.put(8834, 8835);
        BidiLine.mirrorChars.put(8835, 8834);
        BidiLine.mirrorChars.put(8836, 8837);
        BidiLine.mirrorChars.put(8837, 8836);
        BidiLine.mirrorChars.put(8838, 8839);
        BidiLine.mirrorChars.put(8839, 8838);
        BidiLine.mirrorChars.put(8840, 8841);
        BidiLine.mirrorChars.put(8841, 8840);
        BidiLine.mirrorChars.put(8842, 8843);
        BidiLine.mirrorChars.put(8843, 8842);
        BidiLine.mirrorChars.put(8847, 8848);
        BidiLine.mirrorChars.put(8848, 8847);
        BidiLine.mirrorChars.put(8849, 8850);
        BidiLine.mirrorChars.put(8850, 8849);
        BidiLine.mirrorChars.put(8856, 10680);
        BidiLine.mirrorChars.put(8866, 8867);
        BidiLine.mirrorChars.put(8867, 8866);
        BidiLine.mirrorChars.put(8870, 10974);
        BidiLine.mirrorChars.put(8872, 10980);
        BidiLine.mirrorChars.put(8873, 10979);
        BidiLine.mirrorChars.put(8875, 10981);
        BidiLine.mirrorChars.put(8880, 8881);
        BidiLine.mirrorChars.put(8881, 8880);
        BidiLine.mirrorChars.put(8882, 8883);
        BidiLine.mirrorChars.put(8883, 8882);
        BidiLine.mirrorChars.put(8884, 8885);
        BidiLine.mirrorChars.put(8885, 8884);
        BidiLine.mirrorChars.put(8886, 8887);
        BidiLine.mirrorChars.put(8887, 8886);
        BidiLine.mirrorChars.put(8905, 8906);
        BidiLine.mirrorChars.put(8906, 8905);
        BidiLine.mirrorChars.put(8907, 8908);
        BidiLine.mirrorChars.put(8908, 8907);
        BidiLine.mirrorChars.put(8909, 8771);
        BidiLine.mirrorChars.put(8912, 8913);
        BidiLine.mirrorChars.put(8913, 8912);
        BidiLine.mirrorChars.put(8918, 8919);
        BidiLine.mirrorChars.put(8919, 8918);
        BidiLine.mirrorChars.put(8920, 8921);
        BidiLine.mirrorChars.put(8921, 8920);
        BidiLine.mirrorChars.put(8922, 8923);
        BidiLine.mirrorChars.put(8923, 8922);
        BidiLine.mirrorChars.put(8924, 8925);
        BidiLine.mirrorChars.put(8925, 8924);
        BidiLine.mirrorChars.put(8926, 8927);
        BidiLine.mirrorChars.put(8927, 8926);
        BidiLine.mirrorChars.put(8928, 8929);
        BidiLine.mirrorChars.put(8929, 8928);
        BidiLine.mirrorChars.put(8930, 8931);
        BidiLine.mirrorChars.put(8931, 8930);
        BidiLine.mirrorChars.put(8932, 8933);
        BidiLine.mirrorChars.put(8933, 8932);
        BidiLine.mirrorChars.put(8934, 8935);
        BidiLine.mirrorChars.put(8935, 8934);
        BidiLine.mirrorChars.put(8936, 8937);
        BidiLine.mirrorChars.put(8937, 8936);
        BidiLine.mirrorChars.put(8938, 8939);
        BidiLine.mirrorChars.put(8939, 8938);
        BidiLine.mirrorChars.put(8940, 8941);
        BidiLine.mirrorChars.put(8941, 8940);
        BidiLine.mirrorChars.put(8944, 8945);
        BidiLine.mirrorChars.put(8945, 8944);
        BidiLine.mirrorChars.put(8946, 8954);
        BidiLine.mirrorChars.put(8947, 8955);
        BidiLine.mirrorChars.put(8948, 8956);
        BidiLine.mirrorChars.put(8950, 8957);
        BidiLine.mirrorChars.put(8951, 8958);
        BidiLine.mirrorChars.put(8954, 8946);
        BidiLine.mirrorChars.put(8955, 8947);
        BidiLine.mirrorChars.put(8956, 8948);
        BidiLine.mirrorChars.put(8957, 8950);
        BidiLine.mirrorChars.put(8958, 8951);
        BidiLine.mirrorChars.put(8968, 8969);
        BidiLine.mirrorChars.put(8969, 8968);
        BidiLine.mirrorChars.put(8970, 8971);
        BidiLine.mirrorChars.put(8971, 8970);
        BidiLine.mirrorChars.put(9001, 9002);
        BidiLine.mirrorChars.put(9002, 9001);
        BidiLine.mirrorChars.put(10088, 10089);
        BidiLine.mirrorChars.put(10089, 10088);
        BidiLine.mirrorChars.put(10090, 10091);
        BidiLine.mirrorChars.put(10091, 10090);
        BidiLine.mirrorChars.put(10092, 10093);
        BidiLine.mirrorChars.put(10093, 10092);
        BidiLine.mirrorChars.put(10094, 10095);
        BidiLine.mirrorChars.put(10095, 10094);
        BidiLine.mirrorChars.put(10096, 10097);
        BidiLine.mirrorChars.put(10097, 10096);
        BidiLine.mirrorChars.put(10098, 10099);
        BidiLine.mirrorChars.put(10099, 10098);
        BidiLine.mirrorChars.put(10100, 10101);
        BidiLine.mirrorChars.put(10101, 10100);
        BidiLine.mirrorChars.put(10197, 10198);
        BidiLine.mirrorChars.put(10198, 10197);
        BidiLine.mirrorChars.put(10205, 10206);
        BidiLine.mirrorChars.put(10206, 10205);
        BidiLine.mirrorChars.put(10210, 10211);
        BidiLine.mirrorChars.put(10211, 10210);
        BidiLine.mirrorChars.put(10212, 10213);
        BidiLine.mirrorChars.put(10213, 10212);
        BidiLine.mirrorChars.put(10214, 10215);
        BidiLine.mirrorChars.put(10215, 10214);
        BidiLine.mirrorChars.put(10216, 10217);
        BidiLine.mirrorChars.put(10217, 10216);
        BidiLine.mirrorChars.put(10218, 10219);
        BidiLine.mirrorChars.put(10219, 10218);
        BidiLine.mirrorChars.put(10627, 10628);
        BidiLine.mirrorChars.put(10628, 10627);
        BidiLine.mirrorChars.put(10629, 10630);
        BidiLine.mirrorChars.put(10630, 10629);
        BidiLine.mirrorChars.put(10631, 10632);
        BidiLine.mirrorChars.put(10632, 10631);
        BidiLine.mirrorChars.put(10633, 10634);
        BidiLine.mirrorChars.put(10634, 10633);
        BidiLine.mirrorChars.put(10635, 10636);
        BidiLine.mirrorChars.put(10636, 10635);
        BidiLine.mirrorChars.put(10637, 10640);
        BidiLine.mirrorChars.put(10638, 10639);
        BidiLine.mirrorChars.put(10639, 10638);
        BidiLine.mirrorChars.put(10640, 10637);
        BidiLine.mirrorChars.put(10641, 10642);
        BidiLine.mirrorChars.put(10642, 10641);
        BidiLine.mirrorChars.put(10643, 10644);
        BidiLine.mirrorChars.put(10644, 10643);
        BidiLine.mirrorChars.put(10645, 10646);
        BidiLine.mirrorChars.put(10646, 10645);
        BidiLine.mirrorChars.put(10647, 10648);
        BidiLine.mirrorChars.put(10648, 10647);
        BidiLine.mirrorChars.put(10680, 8856);
        BidiLine.mirrorChars.put(10688, 10689);
        BidiLine.mirrorChars.put(10689, 10688);
        BidiLine.mirrorChars.put(10692, 10693);
        BidiLine.mirrorChars.put(10693, 10692);
        BidiLine.mirrorChars.put(10703, 10704);
        BidiLine.mirrorChars.put(10704, 10703);
        BidiLine.mirrorChars.put(10705, 10706);
        BidiLine.mirrorChars.put(10706, 10705);
        BidiLine.mirrorChars.put(10708, 10709);
        BidiLine.mirrorChars.put(10709, 10708);
        BidiLine.mirrorChars.put(10712, 10713);
        BidiLine.mirrorChars.put(10713, 10712);
        BidiLine.mirrorChars.put(10714, 10715);
        BidiLine.mirrorChars.put(10715, 10714);
        BidiLine.mirrorChars.put(10741, 8725);
        BidiLine.mirrorChars.put(10744, 10745);
        BidiLine.mirrorChars.put(10745, 10744);
        BidiLine.mirrorChars.put(10748, 10749);
        BidiLine.mirrorChars.put(10749, 10748);
        BidiLine.mirrorChars.put(10795, 10796);
        BidiLine.mirrorChars.put(10796, 10795);
        BidiLine.mirrorChars.put(10797, 10796);
        BidiLine.mirrorChars.put(10798, 10797);
        BidiLine.mirrorChars.put(10804, 10805);
        BidiLine.mirrorChars.put(10805, 10804);
        BidiLine.mirrorChars.put(10812, 10813);
        BidiLine.mirrorChars.put(10813, 10812);
        BidiLine.mirrorChars.put(10852, 10853);
        BidiLine.mirrorChars.put(10853, 10852);
        BidiLine.mirrorChars.put(10873, 10874);
        BidiLine.mirrorChars.put(10874, 10873);
        BidiLine.mirrorChars.put(10877, 10878);
        BidiLine.mirrorChars.put(10878, 10877);
        BidiLine.mirrorChars.put(10879, 10880);
        BidiLine.mirrorChars.put(10880, 10879);
        BidiLine.mirrorChars.put(10881, 10882);
        BidiLine.mirrorChars.put(10882, 10881);
        BidiLine.mirrorChars.put(10883, 10884);
        BidiLine.mirrorChars.put(10884, 10883);
        BidiLine.mirrorChars.put(10891, 10892);
        BidiLine.mirrorChars.put(10892, 10891);
        BidiLine.mirrorChars.put(10897, 10898);
        BidiLine.mirrorChars.put(10898, 10897);
        BidiLine.mirrorChars.put(10899, 10900);
        BidiLine.mirrorChars.put(10900, 10899);
        BidiLine.mirrorChars.put(10901, 10902);
        BidiLine.mirrorChars.put(10902, 10901);
        BidiLine.mirrorChars.put(10903, 10904);
        BidiLine.mirrorChars.put(10904, 10903);
        BidiLine.mirrorChars.put(10905, 10906);
        BidiLine.mirrorChars.put(10906, 10905);
        BidiLine.mirrorChars.put(10907, 10908);
        BidiLine.mirrorChars.put(10908, 10907);
        BidiLine.mirrorChars.put(10913, 10914);
        BidiLine.mirrorChars.put(10914, 10913);
        BidiLine.mirrorChars.put(10918, 10919);
        BidiLine.mirrorChars.put(10919, 10918);
        BidiLine.mirrorChars.put(10920, 10921);
        BidiLine.mirrorChars.put(10921, 10920);
        BidiLine.mirrorChars.put(10922, 10923);
        BidiLine.mirrorChars.put(10923, 10922);
        BidiLine.mirrorChars.put(10924, 10925);
        BidiLine.mirrorChars.put(10925, 10924);
        BidiLine.mirrorChars.put(10927, 10928);
        BidiLine.mirrorChars.put(10928, 10927);
        BidiLine.mirrorChars.put(10931, 10932);
        BidiLine.mirrorChars.put(10932, 10931);
        BidiLine.mirrorChars.put(10939, 10940);
        BidiLine.mirrorChars.put(10940, 10939);
        BidiLine.mirrorChars.put(10941, 10942);
        BidiLine.mirrorChars.put(10942, 10941);
        BidiLine.mirrorChars.put(10943, 10944);
        BidiLine.mirrorChars.put(10944, 10943);
        BidiLine.mirrorChars.put(10945, 10946);
        BidiLine.mirrorChars.put(10946, 10945);
        BidiLine.mirrorChars.put(10947, 10948);
        BidiLine.mirrorChars.put(10948, 10947);
        BidiLine.mirrorChars.put(10949, 10950);
        BidiLine.mirrorChars.put(10950, 10949);
        BidiLine.mirrorChars.put(10957, 10958);
        BidiLine.mirrorChars.put(10958, 10957);
        BidiLine.mirrorChars.put(10959, 10960);
        BidiLine.mirrorChars.put(10960, 10959);
        BidiLine.mirrorChars.put(10961, 10962);
        BidiLine.mirrorChars.put(10962, 10961);
        BidiLine.mirrorChars.put(10963, 10964);
        BidiLine.mirrorChars.put(10964, 10963);
        BidiLine.mirrorChars.put(10965, 10966);
        BidiLine.mirrorChars.put(10966, 10965);
        BidiLine.mirrorChars.put(10974, 8870);
        BidiLine.mirrorChars.put(10979, 8873);
        BidiLine.mirrorChars.put(10980, 8872);
        BidiLine.mirrorChars.put(10981, 8875);
        BidiLine.mirrorChars.put(10988, 10989);
        BidiLine.mirrorChars.put(10989, 10988);
        BidiLine.mirrorChars.put(10999, 11000);
        BidiLine.mirrorChars.put(11000, 10999);
        BidiLine.mirrorChars.put(11001, 11002);
        BidiLine.mirrorChars.put(11002, 11001);
        BidiLine.mirrorChars.put(12296, 12297);
        BidiLine.mirrorChars.put(12297, 12296);
        BidiLine.mirrorChars.put(12298, 12299);
        BidiLine.mirrorChars.put(12299, 12298);
        BidiLine.mirrorChars.put(12300, 12301);
        BidiLine.mirrorChars.put(12301, 12300);
        BidiLine.mirrorChars.put(12302, 12303);
        BidiLine.mirrorChars.put(12303, 12302);
        BidiLine.mirrorChars.put(12304, 12305);
        BidiLine.mirrorChars.put(12305, 12304);
        BidiLine.mirrorChars.put(12308, 12309);
        BidiLine.mirrorChars.put(12309, 12308);
        BidiLine.mirrorChars.put(12310, 12311);
        BidiLine.mirrorChars.put(12311, 12310);
        BidiLine.mirrorChars.put(12312, 12313);
        BidiLine.mirrorChars.put(12313, 12312);
        BidiLine.mirrorChars.put(12314, 12315);
        BidiLine.mirrorChars.put(12315, 12314);
        BidiLine.mirrorChars.put(65288, 65289);
        BidiLine.mirrorChars.put(65289, 65288);
        BidiLine.mirrorChars.put(65308, 65310);
        BidiLine.mirrorChars.put(65310, 65308);
        BidiLine.mirrorChars.put(65339, 65341);
        BidiLine.mirrorChars.put(65341, 65339);
        BidiLine.mirrorChars.put(65371, 65373);
        BidiLine.mirrorChars.put(65373, 65371);
        BidiLine.mirrorChars.put(65375, 65376);
        BidiLine.mirrorChars.put(65376, 65375);
        BidiLine.mirrorChars.put(65378, 65379);
        BidiLine.mirrorChars.put(65379, 65378);
    }
}
