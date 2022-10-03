package org.apache.lucene.codecs.compressing;

import java.util.Arrays;
import org.apache.lucene.util.packed.PackedInts;
import org.apache.lucene.store.DataOutput;
import java.io.IOException;
import org.apache.lucene.store.DataInput;

final class LZ4
{
    static final int MEMORY_USAGE = 14;
    static final int MIN_MATCH = 4;
    static final int MAX_DISTANCE = 65536;
    static final int LAST_LITERALS = 5;
    static final int HASH_LOG_HC = 15;
    static final int HASH_TABLE_SIZE_HC = 32768;
    static final int OPTIMAL_ML = 18;
    static final /* synthetic */ boolean $assertionsDisabled;
    
    private LZ4() {
    }
    
    private static int hash(final int i, final int hashBits) {
        return i * -1640531535 >>> 32 - hashBits;
    }
    
    private static int hashHC(final int i) {
        return hash(i, 15);
    }
    
    private static int readInt(final byte[] buf, final int i) {
        return (buf[i] & 0xFF) << 24 | (buf[i + 1] & 0xFF) << 16 | (buf[i + 2] & 0xFF) << 8 | (buf[i + 3] & 0xFF);
    }
    
    private static boolean readIntEquals(final byte[] buf, final int i, final int j) {
        return readInt(buf, i) == readInt(buf, j);
    }
    
    private static int commonBytes(final byte[] b, int o1, int o2, final int limit) {
        assert o1 < o2;
        int count = 0;
        while (o2 < limit && b[o1++] == b[o2++]) {
            ++count;
        }
        return count;
    }
    
    private static int commonBytesBackward(final byte[] b, int o1, int o2, final int l1, final int l2) {
        int count = 0;
        while (o1 > l1 && o2 > l2 && b[--o1] == b[--o2]) {
            ++count;
        }
        return count;
    }
    
    public static int decompress(final DataInput compressed, final int decompressedLen, final byte[] dest, int dOff) throws IOException {
        final int destEnd = dest.length;
        do {
            final int token = compressed.readByte() & 0xFF;
            int literalLen = token >>> 4;
            if (literalLen != 0) {
                if (literalLen == 15) {
                    byte len;
                    while ((len = compressed.readByte()) == -1) {
                        literalLen += 255;
                    }
                    literalLen += (len & 0xFF);
                }
                compressed.readBytes(dest, dOff, literalLen);
                dOff += literalLen;
            }
            if (dOff >= decompressedLen) {
                break;
            }
            final int matchDec = (compressed.readByte() & 0xFF) | (compressed.readByte() & 0xFF) << 8;
            assert matchDec > 0;
            int matchLen = token & 0xF;
            if (matchLen == 15) {
                int len2;
                while ((len2 = compressed.readByte()) == -1) {
                    matchLen += 255;
                }
                matchLen += (len2 & 0xFF);
            }
            matchLen += 4;
            final int fastLen = matchLen + 7 & 0xFFFFFFF8;
            if (matchDec < matchLen || dOff + fastLen > destEnd) {
                int ref = dOff - matchDec;
                for (int end = dOff + matchLen; dOff < end; ++dOff) {
                    dest[dOff] = dest[ref];
                    ++ref;
                }
            }
            else {
                System.arraycopy(dest, dOff - matchDec, dest, dOff, fastLen);
                dOff += matchLen;
            }
        } while (dOff < decompressedLen);
        return dOff;
    }
    
    private static void encodeLen(int l, final DataOutput out) throws IOException {
        while (l >= 255) {
            out.writeByte((byte)(-1));
            l -= 255;
        }
        out.writeByte((byte)l);
    }
    
    private static void encodeLiterals(final byte[] bytes, final int token, final int anchor, final int literalLen, final DataOutput out) throws IOException {
        out.writeByte((byte)token);
        if (literalLen >= 15) {
            encodeLen(literalLen - 15, out);
        }
        out.writeBytes(bytes, anchor, literalLen);
    }
    
    private static void encodeLastLiterals(final byte[] bytes, final int anchor, final int literalLen, final DataOutput out) throws IOException {
        final int token = Math.min(literalLen, 15) << 4;
        encodeLiterals(bytes, token, anchor, literalLen, out);
    }
    
    private static void encodeSequence(final byte[] bytes, final int anchor, final int matchRef, final int matchOff, final int matchLen, final DataOutput out) throws IOException {
        final int literalLen = matchOff - anchor;
        assert matchLen >= 4;
        final int token = Math.min(literalLen, 15) << 4 | Math.min(matchLen - 4, 15);
        encodeLiterals(bytes, token, anchor, literalLen, out);
        final int matchDec = matchOff - matchRef;
        assert matchDec > 0 && matchDec < 65536;
        out.writeByte((byte)matchDec);
        out.writeByte((byte)(matchDec >>> 8));
        if (matchLen >= 19) {
            encodeLen(matchLen - 15 - 4, out);
        }
    }
    
    public static void compress(final byte[] bytes, int off, final int len, final DataOutput out, final HashTable ht) throws IOException {
        final int base = off;
        final int end = off + len;
        int anchor = off++;
        if (len > 9) {
            final int limit = end - 5;
            final int matchLimit = limit - 4;
            ht.reset(len);
            final int hashLog = ht.hashLog;
            final PackedInts.Mutable hashTable = ht.hashTable;
        Label_0052:
            while (off <= limit) {
                while (off < matchLimit) {
                    final int v = readInt(bytes, off);
                    final int h = hash(v, hashLog);
                    final int ref = base + (int)hashTable.get(h);
                    assert PackedInts.bitsRequired(off - base) <= hashTable.getBitsPerValue();
                    hashTable.set(h, off - base);
                    if (off - ref < 65536 && readInt(bytes, ref) == v) {
                        final int matchLen = 4 + commonBytes(bytes, ref + 4, off + 4, limit);
                        encodeSequence(bytes, anchor, ref, off, matchLen, out);
                        off = (anchor = off + matchLen);
                        continue Label_0052;
                    }
                    ++off;
                }
                break;
            }
        }
        final int literalLen = end - anchor;
        assert literalLen == len;
        encodeLastLiterals(bytes, anchor, end - anchor, out);
    }
    
    private static void copyTo(final Match m1, final Match m2) {
        m2.len = m1.len;
        m2.start = m1.start;
        m2.ref = m1.ref;
    }
    
    public static void compressHC(final byte[] src, final int srcOff, final int srcLen, final DataOutput out, final HCHashTable ht) throws IOException {
        final int srcEnd = srcOff + srcLen;
        final int matchLimit = srcEnd - 5;
        final int mfLimit = matchLimit - 4;
        int sOff = srcOff;
        int anchor = sOff++;
        ht.reset(srcOff);
        final Match match0 = new Match();
        final Match match2 = new Match();
        final Match match3 = new Match();
        final Match match4 = new Match();
    Label_0073:
        while (sOff <= mfLimit) {
            if (ht.insertAndFindBestMatch(src, sOff, matchLimit, match2)) {
                copyTo(match2, match0);
                while (LZ4.$assertionsDisabled || match2.start >= anchor) {
                    if (match2.end() >= mfLimit || !ht.insertAndFindWiderMatch(src, match2.end() - 2, match2.start + 1, matchLimit, match2.len, match3)) {
                        encodeSequence(src, anchor, match2.ref, match2.start, match2.len, out);
                        sOff = (anchor = match2.end());
                        continue Label_0073;
                    }
                    if (match0.start < match2.start && match3.start < match2.start + match0.len) {
                        copyTo(match0, match2);
                    }
                    assert match3.start > match2.start;
                    if (match3.start - match2.start < 3) {
                        copyTo(match3, match2);
                    }
                    else {
                        while (true) {
                            if (match3.start - match2.start < 18) {
                                int newMatchLen = match2.len;
                                if (newMatchLen > 18) {
                                    newMatchLen = 18;
                                }
                                if (match2.start + newMatchLen > match3.end() - 4) {
                                    newMatchLen = match3.start - match2.start + match3.len - 4;
                                }
                                final int correction = newMatchLen - (match3.start - match2.start);
                                if (correction > 0) {
                                    match3.fix(correction);
                                }
                            }
                            if (match3.start + match3.len >= mfLimit || !ht.insertAndFindWiderMatch(src, match3.end() - 3, match3.start, matchLimit, match3.len, match4)) {
                                if (match3.start < match2.end()) {
                                    match2.len = match3.start - match2.start;
                                }
                                encodeSequence(src, anchor, match2.ref, match2.start, match2.len, out);
                                sOff = (anchor = match2.end());
                                encodeSequence(src, anchor, match3.ref, match3.start, match3.len, out);
                                sOff = (anchor = match3.end());
                                continue Label_0073;
                            }
                            if (match4.start < match2.end() + 3) {
                                if (match4.start >= match2.end()) {
                                    if (match3.start < match2.end()) {
                                        final int correction2 = match2.end() - match3.start;
                                        match3.fix(correction2);
                                        if (match3.len < 4) {
                                            copyTo(match4, match3);
                                        }
                                    }
                                    encodeSequence(src, anchor, match2.ref, match2.start, match2.len, out);
                                    sOff = (anchor = match2.end());
                                    copyTo(match4, match2);
                                    copyTo(match3, match0);
                                    break;
                                }
                                copyTo(match4, match3);
                            }
                            else {
                                if (match3.start < match2.end()) {
                                    if (match3.start - match2.start < 15) {
                                        if (match2.len > 18) {
                                            match2.len = 18;
                                        }
                                        if (match2.end() > match3.end() - 4) {
                                            match2.len = match3.end() - match2.start - 4;
                                        }
                                        final int correction2 = match2.end() - match3.start;
                                        match3.fix(correction2);
                                    }
                                    else {
                                        match2.len = match3.start - match2.start;
                                    }
                                }
                                encodeSequence(src, anchor, match2.ref, match2.start, match2.len, out);
                                sOff = (anchor = match2.end());
                                copyTo(match3, match2);
                                copyTo(match4, match3);
                            }
                        }
                    }
                }
                throw new AssertionError();
            }
            ++sOff;
        }
        encodeLastLiterals(src, anchor, srcEnd - anchor, out);
    }
    
    static final class HashTable
    {
        private int hashLog;
        private PackedInts.Mutable hashTable;
        
        void reset(final int len) {
            final int bitsPerOffset = PackedInts.bitsRequired(len - 5);
            final int bitsPerOffsetLog = 32 - Integer.numberOfLeadingZeros(bitsPerOffset - 1);
            this.hashLog = 17 - bitsPerOffsetLog;
            if (this.hashTable == null || this.hashTable.size() < 1 << this.hashLog || this.hashTable.getBitsPerValue() < bitsPerOffset) {
                this.hashTable = PackedInts.getMutable(1 << this.hashLog, bitsPerOffset, 0.25f);
            }
            else {
                this.hashTable.clear();
            }
        }
    }
    
    private static class Match
    {
        int start;
        int ref;
        int len;
        
        void fix(final int correction) {
            this.start += correction;
            this.ref += correction;
            this.len -= correction;
        }
        
        int end() {
            return this.start + this.len;
        }
    }
    
    static final class HCHashTable
    {
        static final int MAX_ATTEMPTS = 256;
        static final int MASK = 65535;
        int nextToUpdate;
        private int base;
        private final int[] hashTable;
        private final short[] chainTable;
        
        HCHashTable() {
            this.hashTable = new int[32768];
            this.chainTable = new short[65536];
        }
        
        private void reset(final int base) {
            this.base = base;
            this.nextToUpdate = base;
            Arrays.fill(this.hashTable, -1);
            Arrays.fill(this.chainTable, (short)0);
        }
        
        private int hashPointer(final byte[] bytes, final int off) {
            final int v = readInt(bytes, off);
            final int h = hashHC(v);
            return this.hashTable[h];
        }
        
        private int next(final int off) {
            return off - (this.chainTable[off & 0xFFFF] & 0xFFFF);
        }
        
        private void addHash(final byte[] bytes, final int off) {
            final int v = readInt(bytes, off);
            final int h = hashHC(v);
            int delta = off - this.hashTable[h];
            assert delta > 0 : delta;
            if (delta >= 65536) {
                delta = 65535;
            }
            this.chainTable[off & 0xFFFF] = (short)delta;
            this.hashTable[h] = off;
        }
        
        void insert(final int off, final byte[] bytes) {
            while (this.nextToUpdate < off) {
                this.addHash(bytes, this.nextToUpdate);
                ++this.nextToUpdate;
            }
        }
        
        boolean insertAndFindBestMatch(final byte[] buf, final int off, final int matchLimit, final Match match) {
            match.start = off;
            match.len = 0;
            int delta = 0;
            int repl = 0;
            this.insert(off, buf);
            int ref = this.hashPointer(buf, off);
            if (ref >= off - 4 && ref <= off && ref >= this.base) {
                if (readIntEquals(buf, ref, off)) {
                    delta = off - ref;
                    final int len = 4 + commonBytes(buf, ref + 4, off + 4, matchLimit);
                    match.len = len;
                    repl = len;
                    match.ref = ref;
                }
                ref = this.next(ref);
            }
            for (int i = 0; i < 256 && ref >= Math.max(this.base, off - 65536 + 1) && ref <= off; ref = this.next(ref), ++i) {
                if (buf[ref + match.len] == buf[off + match.len] && readIntEquals(buf, ref, off)) {
                    final int matchLen = 4 + commonBytes(buf, ref + 4, off + 4, matchLimit);
                    if (matchLen > match.len) {
                        match.ref = ref;
                        match.len = matchLen;
                    }
                }
            }
            if (repl != 0) {
                int ptr;
                int end;
                for (ptr = off, end = off + repl - 3; ptr < end - delta; ++ptr) {
                    this.chainTable[ptr & 0xFFFF] = (short)delta;
                }
                do {
                    this.chainTable[ptr & 0xFFFF] = (short)delta;
                    this.hashTable[hashHC(readInt(buf, ptr))] = ptr;
                } while (++ptr < end);
                this.nextToUpdate = end;
            }
            return match.len != 0;
        }
        
        boolean insertAndFindWiderMatch(final byte[] buf, final int off, final int startLimit, final int matchLimit, final int minLen, final Match match) {
            match.len = minLen;
            this.insert(off, buf);
            final int delta = off - startLimit;
            for (int ref = this.hashPointer(buf, off), i = 0; i < 256 && ref >= Math.max(this.base, off - 65536 + 1) && ref <= off; ref = this.next(ref), ++i) {
                if (buf[ref - delta + match.len] == buf[startLimit + match.len] && readIntEquals(buf, ref, off)) {
                    final int matchLenForward = 4 + commonBytes(buf, ref + 4, off + 4, matchLimit);
                    final int matchLenBackward = commonBytesBackward(buf, ref, off, this.base, startLimit);
                    final int matchLen = matchLenBackward + matchLenForward;
                    if (matchLen > match.len) {
                        match.len = matchLen;
                        match.ref = ref - matchLenBackward;
                        match.start = off - matchLenBackward;
                    }
                }
            }
            return match.len > minLen;
        }
    }
}
