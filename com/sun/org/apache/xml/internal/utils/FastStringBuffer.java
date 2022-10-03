package com.sun.org.apache.xml.internal.utils;

import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.SAXException;
import org.xml.sax.ContentHandler;

public class FastStringBuffer
{
    static final int DEBUG_FORCE_INIT_BITS = 0;
    static final boolean DEBUG_FORCE_FIXED_CHUNKSIZE = true;
    public static final int SUPPRESS_LEADING_WS = 1;
    public static final int SUPPRESS_TRAILING_WS = 2;
    public static final int SUPPRESS_BOTH = 3;
    private static final int CARRY_WS = 4;
    int m_chunkBits;
    int m_maxChunkBits;
    int m_rebundleBits;
    int m_chunkSize;
    int m_chunkMask;
    char[][] m_array;
    int m_lastChunk;
    int m_firstFree;
    FastStringBuffer m_innerFSB;
    static final char[] SINGLE_SPACE;
    
    public FastStringBuffer(int initChunkBits, int maxChunkBits, final int rebundleBits) {
        this.m_chunkBits = 15;
        this.m_maxChunkBits = 15;
        this.m_rebundleBits = 2;
        this.m_lastChunk = 0;
        this.m_firstFree = 0;
        this.m_innerFSB = null;
        maxChunkBits = initChunkBits;
        this.m_array = new char[16][];
        if (initChunkBits > maxChunkBits) {
            initChunkBits = maxChunkBits;
        }
        this.m_chunkBits = initChunkBits;
        this.m_maxChunkBits = maxChunkBits;
        this.m_rebundleBits = rebundleBits;
        this.m_chunkSize = 1 << initChunkBits;
        this.m_chunkMask = this.m_chunkSize - 1;
        this.m_array[0] = new char[this.m_chunkSize];
    }
    
    public FastStringBuffer(final int initChunkBits, final int maxChunkBits) {
        this(initChunkBits, maxChunkBits, 2);
    }
    
    public FastStringBuffer(final int initChunkBits) {
        this(initChunkBits, 15, 2);
    }
    
    public FastStringBuffer() {
        this(10, 15, 2);
    }
    
    public final int size() {
        return (this.m_lastChunk << this.m_chunkBits) + this.m_firstFree;
    }
    
    public final int length() {
        return (this.m_lastChunk << this.m_chunkBits) + this.m_firstFree;
    }
    
    public final void reset() {
        this.m_lastChunk = 0;
        this.m_firstFree = 0;
        FastStringBuffer innermost;
        for (innermost = this; innermost.m_innerFSB != null; innermost = innermost.m_innerFSB) {}
        this.m_chunkBits = innermost.m_chunkBits;
        this.m_chunkSize = innermost.m_chunkSize;
        this.m_chunkMask = innermost.m_chunkMask;
        this.m_innerFSB = null;
        (this.m_array = new char[16][0])[0] = new char[this.m_chunkSize];
    }
    
    public final void setLength(final int l) {
        this.m_lastChunk = l >>> this.m_chunkBits;
        if (this.m_lastChunk == 0 && this.m_innerFSB != null) {
            this.m_innerFSB.setLength(l, this);
        }
        else {
            this.m_firstFree = (l & this.m_chunkMask);
            if (this.m_firstFree == 0 && this.m_lastChunk > 0) {
                --this.m_lastChunk;
                this.m_firstFree = this.m_chunkSize;
            }
        }
    }
    
    private final void setLength(final int l, final FastStringBuffer rootFSB) {
        this.m_lastChunk = l >>> this.m_chunkBits;
        if (this.m_lastChunk == 0 && this.m_innerFSB != null) {
            this.m_innerFSB.setLength(l, rootFSB);
        }
        else {
            rootFSB.m_chunkBits = this.m_chunkBits;
            rootFSB.m_maxChunkBits = this.m_maxChunkBits;
            rootFSB.m_rebundleBits = this.m_rebundleBits;
            rootFSB.m_chunkSize = this.m_chunkSize;
            rootFSB.m_chunkMask = this.m_chunkMask;
            rootFSB.m_array = this.m_array;
            rootFSB.m_innerFSB = this.m_innerFSB;
            rootFSB.m_lastChunk = this.m_lastChunk;
            rootFSB.m_firstFree = (l & this.m_chunkMask);
        }
    }
    
    @Override
    public final String toString() {
        final int length = (this.m_lastChunk << this.m_chunkBits) + this.m_firstFree;
        return this.getString(new StringBuffer(length), 0, 0, length).toString();
    }
    
    public final void append(final char value) {
        final boolean lastchunk = this.m_lastChunk + 1 == this.m_array.length;
        char[] chunk;
        if (this.m_firstFree < this.m_chunkSize) {
            chunk = this.m_array[this.m_lastChunk];
        }
        else {
            final int i = this.m_array.length;
            if (this.m_lastChunk + 1 == i) {
                final char[][] newarray = new char[i + 16][];
                System.arraycopy(this.m_array, 0, newarray, 0, i);
                this.m_array = newarray;
            }
            chunk = this.m_array[++this.m_lastChunk];
            if (chunk == null) {
                if (this.m_lastChunk == 1 << this.m_rebundleBits && this.m_chunkBits < this.m_maxChunkBits) {
                    this.m_innerFSB = new FastStringBuffer(this);
                }
                final char[][] array = this.m_array;
                final int lastChunk = this.m_lastChunk;
                final char[] array2 = new char[this.m_chunkSize];
                array[lastChunk] = array2;
                chunk = array2;
            }
            this.m_firstFree = 0;
        }
        chunk[this.m_firstFree++] = value;
    }
    
    public final void append(final String value) {
        if (value == null) {
            return;
        }
        int strlen = value.length();
        if (0 == strlen) {
            return;
        }
        int copyfrom = 0;
        char[] chunk = this.m_array[this.m_lastChunk];
        int available = this.m_chunkSize - this.m_firstFree;
        while (strlen > 0) {
            if (available > strlen) {
                available = strlen;
            }
            value.getChars(copyfrom, copyfrom + available, this.m_array[this.m_lastChunk], this.m_firstFree);
            strlen -= available;
            copyfrom += available;
            if (strlen > 0) {
                final int i = this.m_array.length;
                if (this.m_lastChunk + 1 == i) {
                    final char[][] newarray = new char[i + 16][];
                    System.arraycopy(this.m_array, 0, newarray, 0, i);
                    this.m_array = newarray;
                }
                chunk = this.m_array[++this.m_lastChunk];
                if (chunk == null) {
                    if (this.m_lastChunk == 1 << this.m_rebundleBits && this.m_chunkBits < this.m_maxChunkBits) {
                        this.m_innerFSB = new FastStringBuffer(this);
                    }
                    final char[][] array = this.m_array;
                    final int lastChunk = this.m_lastChunk;
                    final char[] array2 = new char[this.m_chunkSize];
                    array[lastChunk] = array2;
                    chunk = array2;
                }
                available = this.m_chunkSize;
                this.m_firstFree = 0;
            }
        }
        this.m_firstFree += available;
    }
    
    public final void append(final StringBuffer value) {
        if (value == null) {
            return;
        }
        int strlen = value.length();
        if (0 == strlen) {
            return;
        }
        int copyfrom = 0;
        char[] chunk = this.m_array[this.m_lastChunk];
        int available = this.m_chunkSize - this.m_firstFree;
        while (strlen > 0) {
            if (available > strlen) {
                available = strlen;
            }
            value.getChars(copyfrom, copyfrom + available, this.m_array[this.m_lastChunk], this.m_firstFree);
            strlen -= available;
            copyfrom += available;
            if (strlen > 0) {
                final int i = this.m_array.length;
                if (this.m_lastChunk + 1 == i) {
                    final char[][] newarray = new char[i + 16][];
                    System.arraycopy(this.m_array, 0, newarray, 0, i);
                    this.m_array = newarray;
                }
                chunk = this.m_array[++this.m_lastChunk];
                if (chunk == null) {
                    if (this.m_lastChunk == 1 << this.m_rebundleBits && this.m_chunkBits < this.m_maxChunkBits) {
                        this.m_innerFSB = new FastStringBuffer(this);
                    }
                    final char[][] array = this.m_array;
                    final int lastChunk = this.m_lastChunk;
                    final char[] array2 = new char[this.m_chunkSize];
                    array[lastChunk] = array2;
                    chunk = array2;
                }
                available = this.m_chunkSize;
                this.m_firstFree = 0;
            }
        }
        this.m_firstFree += available;
    }
    
    public final void append(final char[] chars, final int start, final int length) {
        int strlen = length;
        if (0 == strlen) {
            return;
        }
        int copyfrom = start;
        char[] chunk = this.m_array[this.m_lastChunk];
        int available = this.m_chunkSize - this.m_firstFree;
        while (strlen > 0) {
            if (available > strlen) {
                available = strlen;
            }
            System.arraycopy(chars, copyfrom, this.m_array[this.m_lastChunk], this.m_firstFree, available);
            strlen -= available;
            copyfrom += available;
            if (strlen > 0) {
                final int i = this.m_array.length;
                if (this.m_lastChunk + 1 == i) {
                    final char[][] newarray = new char[i + 16][];
                    System.arraycopy(this.m_array, 0, newarray, 0, i);
                    this.m_array = newarray;
                }
                chunk = this.m_array[++this.m_lastChunk];
                if (chunk == null) {
                    if (this.m_lastChunk == 1 << this.m_rebundleBits && this.m_chunkBits < this.m_maxChunkBits) {
                        this.m_innerFSB = new FastStringBuffer(this);
                    }
                    final char[][] array = this.m_array;
                    final int lastChunk = this.m_lastChunk;
                    final char[] array2 = new char[this.m_chunkSize];
                    array[lastChunk] = array2;
                    chunk = array2;
                }
                available = this.m_chunkSize;
                this.m_firstFree = 0;
            }
        }
        this.m_firstFree += available;
    }
    
    public final void append(final FastStringBuffer value) {
        if (value == null) {
            return;
        }
        int strlen = value.length();
        if (0 == strlen) {
            return;
        }
        int copyfrom = 0;
        char[] chunk = this.m_array[this.m_lastChunk];
        int available = this.m_chunkSize - this.m_firstFree;
        while (strlen > 0) {
            if (available > strlen) {
                available = strlen;
            }
            final int sourcechunk = copyfrom + value.m_chunkSize - 1 >>> value.m_chunkBits;
            final int sourcecolumn = copyfrom & value.m_chunkMask;
            int runlength = value.m_chunkSize - sourcecolumn;
            if (runlength > available) {
                runlength = available;
            }
            System.arraycopy(value.m_array[sourcechunk], sourcecolumn, this.m_array[this.m_lastChunk], this.m_firstFree, runlength);
            if (runlength != available) {
                System.arraycopy(value.m_array[sourcechunk + 1], 0, this.m_array[this.m_lastChunk], this.m_firstFree + runlength, available - runlength);
            }
            strlen -= available;
            copyfrom += available;
            if (strlen > 0) {
                final int i = this.m_array.length;
                if (this.m_lastChunk + 1 == i) {
                    final char[][] newarray = new char[i + 16][];
                    System.arraycopy(this.m_array, 0, newarray, 0, i);
                    this.m_array = newarray;
                }
                chunk = this.m_array[++this.m_lastChunk];
                if (chunk == null) {
                    if (this.m_lastChunk == 1 << this.m_rebundleBits && this.m_chunkBits < this.m_maxChunkBits) {
                        this.m_innerFSB = new FastStringBuffer(this);
                    }
                    final char[][] array = this.m_array;
                    final int lastChunk = this.m_lastChunk;
                    final char[] array2 = new char[this.m_chunkSize];
                    array[lastChunk] = array2;
                    chunk = array2;
                }
                available = this.m_chunkSize;
                this.m_firstFree = 0;
            }
        }
        this.m_firstFree += available;
    }
    
    public boolean isWhitespace(final int start, int length) {
        int sourcechunk = start >>> this.m_chunkBits;
        int sourcecolumn = start & this.m_chunkMask;
        int available = this.m_chunkSize - sourcecolumn;
        while (length > 0) {
            final int runlength = (length <= available) ? length : available;
            boolean chunkOK;
            if (sourcechunk == 0 && this.m_innerFSB != null) {
                chunkOK = this.m_innerFSB.isWhitespace(sourcecolumn, runlength);
            }
            else {
                chunkOK = XMLCharacterRecognizer.isWhiteSpace(this.m_array[sourcechunk], sourcecolumn, runlength);
            }
            if (!chunkOK) {
                return false;
            }
            length -= runlength;
            ++sourcechunk;
            sourcecolumn = 0;
            available = this.m_chunkSize;
        }
        return true;
    }
    
    public String getString(final int start, final int length) {
        final int startColumn = start & this.m_chunkMask;
        final int startChunk = start >>> this.m_chunkBits;
        if (startColumn + length < this.m_chunkMask && this.m_innerFSB == null) {
            return this.getOneChunkString(startChunk, startColumn, length);
        }
        return this.getString(new StringBuffer(length), startChunk, startColumn, length).toString();
    }
    
    protected String getOneChunkString(final int startChunk, final int startColumn, final int length) {
        return new String(this.m_array[startChunk], startColumn, length);
    }
    
    StringBuffer getString(final StringBuffer sb, final int start, final int length) {
        return this.getString(sb, start >>> this.m_chunkBits, start & this.m_chunkMask, length);
    }
    
    StringBuffer getString(final StringBuffer sb, final int startChunk, int startColumn, final int length) {
        final int stop = (startChunk << this.m_chunkBits) + startColumn + length;
        final int stopChunk = stop >>> this.m_chunkBits;
        final int stopColumn = stop & this.m_chunkMask;
        for (int i = startChunk; i < stopChunk; ++i) {
            if (i == 0 && this.m_innerFSB != null) {
                this.m_innerFSB.getString(sb, startColumn, this.m_chunkSize - startColumn);
            }
            else {
                sb.append(this.m_array[i], startColumn, this.m_chunkSize - startColumn);
            }
            startColumn = 0;
        }
        if (stopChunk == 0 && this.m_innerFSB != null) {
            this.m_innerFSB.getString(sb, startColumn, stopColumn - startColumn);
        }
        else if (stopColumn > startColumn) {
            sb.append(this.m_array[stopChunk], startColumn, stopColumn - startColumn);
        }
        return sb;
    }
    
    public char charAt(final int pos) {
        final int startChunk = pos >>> this.m_chunkBits;
        if (startChunk == 0 && this.m_innerFSB != null) {
            return this.m_innerFSB.charAt(pos & this.m_chunkMask);
        }
        return this.m_array[startChunk][pos & this.m_chunkMask];
    }
    
    public void sendSAXcharacters(final ContentHandler ch, final int start, final int length) throws SAXException {
        final int startChunk = start >>> this.m_chunkBits;
        int startColumn = start & this.m_chunkMask;
        if (startColumn + length < this.m_chunkMask && this.m_innerFSB == null) {
            ch.characters(this.m_array[startChunk], startColumn, length);
            return;
        }
        final int stop = start + length;
        final int stopChunk = stop >>> this.m_chunkBits;
        final int stopColumn = stop & this.m_chunkMask;
        for (int i = startChunk; i < stopChunk; ++i) {
            if (i == 0 && this.m_innerFSB != null) {
                this.m_innerFSB.sendSAXcharacters(ch, startColumn, this.m_chunkSize - startColumn);
            }
            else {
                ch.characters(this.m_array[i], startColumn, this.m_chunkSize - startColumn);
            }
            startColumn = 0;
        }
        if (stopChunk == 0 && this.m_innerFSB != null) {
            this.m_innerFSB.sendSAXcharacters(ch, startColumn, stopColumn - startColumn);
        }
        else if (stopColumn > startColumn) {
            ch.characters(this.m_array[stopChunk], startColumn, stopColumn - startColumn);
        }
    }
    
    public int sendNormalizedSAXcharacters(final ContentHandler ch, final int start, final int length) throws SAXException {
        int stateForNextChunk = 1;
        final int stop = start + length;
        final int startChunk = start >>> this.m_chunkBits;
        int startColumn = start & this.m_chunkMask;
        final int stopChunk = stop >>> this.m_chunkBits;
        final int stopColumn = stop & this.m_chunkMask;
        for (int i = startChunk; i < stopChunk; ++i) {
            if (i == 0 && this.m_innerFSB != null) {
                stateForNextChunk = this.m_innerFSB.sendNormalizedSAXcharacters(ch, startColumn, this.m_chunkSize - startColumn);
            }
            else {
                stateForNextChunk = sendNormalizedSAXcharacters(this.m_array[i], startColumn, this.m_chunkSize - startColumn, ch, stateForNextChunk);
            }
            startColumn = 0;
        }
        if (stopChunk == 0 && this.m_innerFSB != null) {
            stateForNextChunk = this.m_innerFSB.sendNormalizedSAXcharacters(ch, startColumn, stopColumn - startColumn);
        }
        else if (stopColumn > startColumn) {
            stateForNextChunk = sendNormalizedSAXcharacters(this.m_array[stopChunk], startColumn, stopColumn - startColumn, ch, stateForNextChunk | 0x2);
        }
        return stateForNextChunk;
    }
    
    static int sendNormalizedSAXcharacters(final char[] ch, final int start, final int length, final ContentHandler handler, final int edgeTreatmentFlags) throws SAXException {
        final boolean processingLeadingWhitespace = (edgeTreatmentFlags & 0x1) != 0x0;
        boolean seenWhitespace = (edgeTreatmentFlags & 0x4) != 0x0;
        final boolean suppressTrailingWhitespace = (edgeTreatmentFlags & 0x2) != 0x0;
        int currPos = start;
        final int limit = start + length;
        if (processingLeadingWhitespace) {
            while (currPos < limit && XMLCharacterRecognizer.isWhiteSpace(ch[currPos])) {
                ++currPos;
            }
            if (currPos == limit) {
                return edgeTreatmentFlags;
            }
        }
        while (currPos < limit) {
            final int startNonWhitespace = currPos;
            while (currPos < limit && !XMLCharacterRecognizer.isWhiteSpace(ch[currPos])) {
                ++currPos;
            }
            if (startNonWhitespace != currPos) {
                if (seenWhitespace) {
                    handler.characters(FastStringBuffer.SINGLE_SPACE, 0, 1);
                    seenWhitespace = false;
                }
                handler.characters(ch, startNonWhitespace, currPos - startNonWhitespace);
            }
            final int startWhitespace = currPos;
            while (currPos < limit && XMLCharacterRecognizer.isWhiteSpace(ch[currPos])) {
                ++currPos;
            }
            if (startWhitespace != currPos) {
                seenWhitespace = true;
            }
        }
        return (seenWhitespace ? 4 : 0) | (edgeTreatmentFlags & 0x2);
    }
    
    public static void sendNormalizedSAXcharacters(final char[] ch, final int start, final int length, final ContentHandler handler) throws SAXException {
        sendNormalizedSAXcharacters(ch, start, length, handler, 3);
    }
    
    public void sendSAXComment(final LexicalHandler ch, final int start, final int length) throws SAXException {
        final String comment = this.getString(start, length);
        ch.comment(comment.toCharArray(), 0, length);
    }
    
    private void getChars(final int srcBegin, final int srcEnd, final char[] dst, final int dstBegin) {
    }
    
    private FastStringBuffer(final FastStringBuffer source) {
        this.m_chunkBits = 15;
        this.m_maxChunkBits = 15;
        this.m_rebundleBits = 2;
        this.m_lastChunk = 0;
        this.m_firstFree = 0;
        this.m_innerFSB = null;
        this.m_chunkBits = source.m_chunkBits;
        this.m_maxChunkBits = source.m_maxChunkBits;
        this.m_rebundleBits = source.m_rebundleBits;
        this.m_chunkSize = source.m_chunkSize;
        this.m_chunkMask = source.m_chunkMask;
        this.m_array = source.m_array;
        this.m_innerFSB = source.m_innerFSB;
        this.m_lastChunk = source.m_lastChunk - 1;
        this.m_firstFree = source.m_chunkSize;
        source.m_array = new char[16][];
        source.m_innerFSB = this;
        source.m_lastChunk = 1;
        source.m_firstFree = 0;
        source.m_chunkBits += this.m_rebundleBits;
        source.m_chunkSize = 1 << source.m_chunkBits;
        source.m_chunkMask = source.m_chunkSize - 1;
    }
    
    static {
        SINGLE_SPACE = new char[] { ' ' };
    }
}
