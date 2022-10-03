package org.apache.lucene.util.packed;

import java.util.Arrays;
import org.apache.lucene.util.BitUtil;
import org.apache.lucene.store.IndexInput;
import java.io.EOFException;
import java.io.IOException;
import org.apache.lucene.util.LongsRef;
import org.apache.lucene.store.DataInput;

public final class BlockPackedReaderIterator
{
    DataInput in;
    final int packedIntsVersion;
    long valueCount;
    final int blockSize;
    final long[] values;
    final LongsRef valuesRef;
    byte[] blocks;
    int off;
    long ord;
    
    static long readVLong(final DataInput in) throws IOException {
        byte b = in.readByte();
        if (b >= 0) {
            return b;
        }
        long i = (long)b & 0x7FL;
        b = in.readByte();
        i |= ((long)b & 0x7FL) << 7;
        if (b >= 0) {
            return i;
        }
        b = in.readByte();
        i |= ((long)b & 0x7FL) << 14;
        if (b >= 0) {
            return i;
        }
        b = in.readByte();
        i |= ((long)b & 0x7FL) << 21;
        if (b >= 0) {
            return i;
        }
        b = in.readByte();
        i |= ((long)b & 0x7FL) << 28;
        if (b >= 0) {
            return i;
        }
        b = in.readByte();
        i |= ((long)b & 0x7FL) << 35;
        if (b >= 0) {
            return i;
        }
        b = in.readByte();
        i |= ((long)b & 0x7FL) << 42;
        if (b >= 0) {
            return i;
        }
        b = in.readByte();
        i |= ((long)b & 0x7FL) << 49;
        if (b >= 0) {
            return i;
        }
        b = in.readByte();
        i |= ((long)b & 0xFFL) << 56;
        return i;
    }
    
    public BlockPackedReaderIterator(final DataInput in, final int packedIntsVersion, final int blockSize, final long valueCount) {
        PackedInts.checkBlockSize(blockSize, 64, 134217728);
        this.packedIntsVersion = packedIntsVersion;
        this.blockSize = blockSize;
        this.values = new long[blockSize];
        this.valuesRef = new LongsRef(this.values, 0, 0);
        this.reset(in, valueCount);
    }
    
    public void reset(final DataInput in, final long valueCount) {
        this.in = in;
        assert valueCount >= 0L;
        this.valueCount = valueCount;
        this.off = this.blockSize;
        this.ord = 0L;
    }
    
    public void skip(long count) throws IOException {
        assert count >= 0L;
        if (this.ord + count > this.valueCount || this.ord + count < 0L) {
            throw new EOFException();
        }
        final int skipBuffer = (int)Math.min(count, this.blockSize - this.off);
        this.off += skipBuffer;
        this.ord += skipBuffer;
        count -= skipBuffer;
        if (count == 0L) {
            return;
        }
        assert this.off == this.blockSize;
        while (count >= this.blockSize) {
            final int token = this.in.readByte() & 0xFF;
            final int bitsPerValue = token >>> 1;
            if (bitsPerValue > 64) {
                throw new IOException("Corrupted");
            }
            if ((token & 0x1) == 0x0) {
                readVLong(this.in);
            }
            final long blockBytes = PackedInts.Format.PACKED.byteCount(this.packedIntsVersion, this.blockSize, bitsPerValue);
            this.skipBytes(blockBytes);
            this.ord += this.blockSize;
            count -= this.blockSize;
        }
        if (count == 0L) {
            return;
        }
        assert count < this.blockSize;
        this.refill();
        this.ord += count;
        this.off += (int)count;
    }
    
    private void skipBytes(final long count) throws IOException {
        if (this.in instanceof IndexInput) {
            final IndexInput iin = (IndexInput)this.in;
            iin.seek(iin.getFilePointer() + count);
        }
        else {
            if (this.blocks == null) {
                this.blocks = new byte[this.blockSize];
            }
            int toSkip;
            for (long skipped = 0L; skipped < count; skipped += toSkip) {
                toSkip = (int)Math.min(this.blocks.length, count - skipped);
                this.in.readBytes(this.blocks, 0, toSkip);
            }
        }
    }
    
    public long next() throws IOException {
        if (this.ord == this.valueCount) {
            throw new EOFException();
        }
        if (this.off == this.blockSize) {
            this.refill();
        }
        final long value = this.values[this.off++];
        ++this.ord;
        return value;
    }
    
    public LongsRef next(int count) throws IOException {
        assert count > 0;
        if (this.ord == this.valueCount) {
            throw new EOFException();
        }
        if (this.off == this.blockSize) {
            this.refill();
        }
        count = Math.min(count, this.blockSize - this.off);
        count = (int)Math.min(count, this.valueCount - this.ord);
        this.valuesRef.offset = this.off;
        this.valuesRef.length = count;
        this.off += count;
        this.ord += count;
        return this.valuesRef;
    }
    
    private void refill() throws IOException {
        final int token = this.in.readByte() & 0xFF;
        final boolean minEquals0 = (token & 0x1) != 0x0;
        final int bitsPerValue = token >>> 1;
        if (bitsPerValue > 64) {
            throw new IOException("Corrupted");
        }
        final long minValue = minEquals0 ? 0L : BitUtil.zigZagDecode(1L + readVLong(this.in));
        assert minValue != 0L;
        if (bitsPerValue == 0) {
            Arrays.fill(this.values, minValue);
        }
        else {
            final PackedInts.Decoder decoder = PackedInts.getDecoder(PackedInts.Format.PACKED, this.packedIntsVersion, bitsPerValue);
            final int iterations = this.blockSize / decoder.byteValueCount();
            final int blocksSize = iterations * decoder.byteBlockCount();
            if (this.blocks == null || this.blocks.length < blocksSize) {
                this.blocks = new byte[blocksSize];
            }
            final int valueCount = (int)Math.min(this.valueCount - this.ord, this.blockSize);
            final int blocksCount = (int)PackedInts.Format.PACKED.byteCount(this.packedIntsVersion, valueCount, bitsPerValue);
            this.in.readBytes(this.blocks, 0, blocksCount);
            decoder.decode(this.blocks, 0, this.values, 0, iterations);
            if (minValue != 0L) {
                for (int i = 0; i < valueCount; ++i) {
                    final long[] values = this.values;
                    final int n = i;
                    values[n] += minValue;
                }
            }
        }
        this.off = 0;
    }
    
    public long ord() {
        return this.ord;
    }
}
