package org.apache.lucene.util.packed;

import org.apache.lucene.util.RamUsageEstimator;
import java.util.Arrays;
import org.apache.lucene.util.LongsRef;
import java.util.Collections;
import java.util.Collection;
import org.apache.lucene.util.Accountable;
import org.apache.lucene.index.NumericDocValues;
import org.apache.lucene.store.DataOutput;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.codecs.CodecUtil;
import java.io.IOException;
import org.apache.lucene.store.DataInput;

public class PackedInts
{
    public static final float FASTEST = 7.0f;
    public static final float FAST = 0.5f;
    public static final float DEFAULT = 0.25f;
    public static final float COMPACT = 0.0f;
    public static final int DEFAULT_BUFFER_SIZE = 1024;
    public static final String CODEC_NAME = "PackedInts";
    public static final int VERSION_START = 0;
    public static final int VERSION_BYTE_ALIGNED = 1;
    public static final int VERSION_MONOTONIC_WITHOUT_ZIGZAG = 2;
    public static final int VERSION_CURRENT = 2;
    
    public static void checkVersion(final int version) {
        if (version < 0) {
            throw new IllegalArgumentException("Version is too old, should be at least 0 (got " + version + ")");
        }
        if (version > 2) {
            throw new IllegalArgumentException("Version is too new, should be at most 2 (got " + version + ")");
        }
    }
    
    public static FormatAndBits fastestFormatAndBits(int valueCount, final int bitsPerValue, float acceptableOverheadRatio) {
        if (valueCount == -1) {
            valueCount = Integer.MAX_VALUE;
        }
        acceptableOverheadRatio = Math.max(0.0f, acceptableOverheadRatio);
        acceptableOverheadRatio = Math.min(7.0f, acceptableOverheadRatio);
        final float acceptableOverheadPerValue = acceptableOverheadRatio * bitsPerValue;
        final int maxBitsPerValue = bitsPerValue + (int)acceptableOverheadPerValue;
        int actualBitsPerValue = -1;
        Format format = Format.PACKED;
        if (bitsPerValue <= 8 && maxBitsPerValue >= 8) {
            actualBitsPerValue = 8;
        }
        else if (bitsPerValue <= 16 && maxBitsPerValue >= 16) {
            actualBitsPerValue = 16;
        }
        else if (bitsPerValue <= 32 && maxBitsPerValue >= 32) {
            actualBitsPerValue = 32;
        }
        else if (bitsPerValue <= 64 && maxBitsPerValue >= 64) {
            actualBitsPerValue = 64;
        }
        else if (valueCount <= 715827882 && bitsPerValue <= 24 && maxBitsPerValue >= 24) {
            actualBitsPerValue = 24;
        }
        else if (valueCount <= 715827882 && bitsPerValue <= 48 && maxBitsPerValue >= 48) {
            actualBitsPerValue = 48;
        }
        else {
            for (int bpv = bitsPerValue; bpv <= maxBitsPerValue; ++bpv) {
                if (Format.PACKED_SINGLE_BLOCK.isSupported(bpv)) {
                    final float overhead = Format.PACKED_SINGLE_BLOCK.overheadPerValue(bpv);
                    final float acceptableOverhead = acceptableOverheadPerValue + bitsPerValue - bpv;
                    if (overhead <= acceptableOverhead) {
                        actualBitsPerValue = bpv;
                        format = Format.PACKED_SINGLE_BLOCK;
                        break;
                    }
                }
            }
            if (actualBitsPerValue < 0) {
                actualBitsPerValue = bitsPerValue;
            }
        }
        return new FormatAndBits(format, actualBitsPerValue);
    }
    
    public static Decoder getDecoder(final Format format, final int version, final int bitsPerValue) {
        checkVersion(version);
        return BulkOperation.of(format, bitsPerValue);
    }
    
    public static Encoder getEncoder(final Format format, final int version, final int bitsPerValue) {
        checkVersion(version);
        return BulkOperation.of(format, bitsPerValue);
    }
    
    public static Reader getReaderNoHeader(final DataInput in, final Format format, final int version, final int valueCount, final int bitsPerValue) throws IOException {
        checkVersion(version);
        switch (format) {
            case PACKED_SINGLE_BLOCK: {
                return Packed64SingleBlock.create(in, valueCount, bitsPerValue);
            }
            case PACKED: {
                switch (bitsPerValue) {
                    case 8: {
                        return new Direct8(version, in, valueCount);
                    }
                    case 16: {
                        return new Direct16(version, in, valueCount);
                    }
                    case 32: {
                        return new Direct32(version, in, valueCount);
                    }
                    case 64: {
                        return new Direct64(version, in, valueCount);
                    }
                    case 24: {
                        if (valueCount <= 715827882) {
                            return new Packed8ThreeBlocks(version, in, valueCount);
                        }
                        break;
                    }
                    case 48: {
                        if (valueCount <= 715827882) {
                            return new Packed16ThreeBlocks(version, in, valueCount);
                        }
                        break;
                    }
                }
                return new Packed64(version, in, valueCount, bitsPerValue);
            }
            default: {
                throw new AssertionError((Object)("Unknown Writer format: " + format));
            }
        }
    }
    
    public static Reader getReader(final DataInput in) throws IOException {
        final int version = CodecUtil.checkHeader(in, "PackedInts", 0, 2);
        final int bitsPerValue = in.readVInt();
        assert bitsPerValue > 0 && bitsPerValue <= 64 : "bitsPerValue=" + bitsPerValue;
        final int valueCount = in.readVInt();
        final Format format = Format.byId(in.readVInt());
        return getReaderNoHeader(in, format, version, valueCount, bitsPerValue);
    }
    
    public static ReaderIterator getReaderIteratorNoHeader(final DataInput in, final Format format, final int version, final int valueCount, final int bitsPerValue, final int mem) {
        checkVersion(version);
        return new PackedReaderIterator(format, version, valueCount, bitsPerValue, in, mem);
    }
    
    public static ReaderIterator getReaderIterator(final DataInput in, final int mem) throws IOException {
        final int version = CodecUtil.checkHeader(in, "PackedInts", 0, 2);
        final int bitsPerValue = in.readVInt();
        assert bitsPerValue > 0 && bitsPerValue <= 64 : "bitsPerValue=" + bitsPerValue;
        final int valueCount = in.readVInt();
        final Format format = Format.byId(in.readVInt());
        return getReaderIteratorNoHeader(in, format, version, valueCount, bitsPerValue, mem);
    }
    
    public static Reader getDirectReaderNoHeader(final IndexInput in, final Format format, final int version, final int valueCount, final int bitsPerValue) {
        checkVersion(version);
        switch (format) {
            case PACKED: {
                final long byteCount = format.byteCount(version, valueCount, bitsPerValue);
                if (byteCount == format.byteCount(2, valueCount, bitsPerValue)) {
                    return new DirectPackedReader(bitsPerValue, valueCount, in);
                }
                assert version == 0;
                final long endPointer = in.getFilePointer() + byteCount;
                return new DirectPackedReader(bitsPerValue, valueCount, in) {
                    @Override
                    public long get(final int index) {
                        final long result = super.get(index);
                        if (index == this.valueCount - 1) {
                            try {
                                this.in.seek(endPointer);
                            }
                            catch (final IOException e) {
                                throw new IllegalStateException("failed", e);
                            }
                        }
                        return result;
                    }
                };
            }
            case PACKED_SINGLE_BLOCK: {
                return new DirectPacked64SingleBlockReader(bitsPerValue, valueCount, in);
            }
            default: {
                throw new AssertionError((Object)("Unknwown format: " + format));
            }
        }
    }
    
    public static Reader getDirectReader(final IndexInput in) throws IOException {
        final int version = CodecUtil.checkHeader(in, "PackedInts", 0, 2);
        final int bitsPerValue = in.readVInt();
        assert bitsPerValue > 0 && bitsPerValue <= 64 : "bitsPerValue=" + bitsPerValue;
        final int valueCount = in.readVInt();
        final Format format = Format.byId(in.readVInt());
        return getDirectReaderNoHeader(in, format, version, valueCount, bitsPerValue);
    }
    
    public static Mutable getMutable(final int valueCount, final int bitsPerValue, final float acceptableOverheadRatio) {
        final FormatAndBits formatAndBits = fastestFormatAndBits(valueCount, bitsPerValue, acceptableOverheadRatio);
        return getMutable(valueCount, formatAndBits.bitsPerValue, formatAndBits.format);
    }
    
    public static Mutable getMutable(final int valueCount, final int bitsPerValue, final Format format) {
        assert valueCount >= 0;
        switch (format) {
            case PACKED_SINGLE_BLOCK: {
                return Packed64SingleBlock.create(valueCount, bitsPerValue);
            }
            case PACKED: {
                switch (bitsPerValue) {
                    case 8: {
                        return new Direct8(valueCount);
                    }
                    case 16: {
                        return new Direct16(valueCount);
                    }
                    case 32: {
                        return new Direct32(valueCount);
                    }
                    case 64: {
                        return new Direct64(valueCount);
                    }
                    case 24: {
                        if (valueCount <= 715827882) {
                            return new Packed8ThreeBlocks(valueCount);
                        }
                        break;
                    }
                    case 48: {
                        if (valueCount <= 715827882) {
                            return new Packed16ThreeBlocks(valueCount);
                        }
                        break;
                    }
                }
                return new Packed64(valueCount, bitsPerValue);
            }
            default: {
                throw new AssertionError();
            }
        }
    }
    
    public static Writer getWriterNoHeader(final DataOutput out, final Format format, final int valueCount, final int bitsPerValue, final int mem) {
        return new PackedWriter(format, out, valueCount, bitsPerValue, mem);
    }
    
    public static Writer getWriter(final DataOutput out, final int valueCount, final int bitsPerValue, final float acceptableOverheadRatio) throws IOException {
        assert valueCount >= 0;
        final FormatAndBits formatAndBits = fastestFormatAndBits(valueCount, bitsPerValue, acceptableOverheadRatio);
        final Writer writer = getWriterNoHeader(out, formatAndBits.format, valueCount, formatAndBits.bitsPerValue, 1024);
        writer.writeHeader();
        return writer;
    }
    
    public static int bitsRequired(final long maxValue) {
        if (maxValue < 0L) {
            throw new IllegalArgumentException("maxValue must be non-negative (got: " + maxValue + ")");
        }
        return unsignedBitsRequired(maxValue);
    }
    
    public static int unsignedBitsRequired(final long bits) {
        return Math.max(1, 64 - Long.numberOfLeadingZeros(bits));
    }
    
    public static long maxValue(final int bitsPerValue) {
        return (bitsPerValue == 64) ? Long.MAX_VALUE : (~(-1L << bitsPerValue));
    }
    
    public static void copy(final Reader src, int srcPos, final Mutable dest, int destPos, final int len, final int mem) {
        assert srcPos + len <= src.size();
        assert destPos + len <= dest.size();
        final int capacity = mem >>> 3;
        if (capacity == 0) {
            for (int i = 0; i < len; ++i) {
                dest.set(destPos++, src.get(srcPos++));
            }
        }
        else if (len > 0) {
            final long[] buf = new long[Math.min(capacity, len)];
            copy(src, srcPos, dest, destPos, len, buf);
        }
    }
    
    static void copy(final Reader src, int srcPos, final Mutable dest, int destPos, int len, final long[] buf) {
        assert buf.length > 0;
        int remaining = 0;
        while (len > 0) {
            final int read = src.get(srcPos, buf, remaining, Math.min(len, buf.length - remaining));
            assert read > 0;
            srcPos += read;
            len -= read;
            remaining += read;
            final int written = dest.set(destPos, buf, 0, remaining);
            assert written > 0;
            destPos += written;
            if (written < remaining) {
                System.arraycopy(buf, written, buf, 0, remaining - written);
            }
            remaining -= written;
        }
        while (remaining > 0) {
            final int written2 = dest.set(destPos, buf, 0, remaining);
            destPos += written2;
            remaining -= written2;
            System.arraycopy(buf, written2, buf, 0, remaining);
        }
    }
    
    static int checkBlockSize(final int blockSize, final int minBlockSize, final int maxBlockSize) {
        if (blockSize < minBlockSize || blockSize > maxBlockSize) {
            throw new IllegalArgumentException("blockSize must be >= " + minBlockSize + " and <= " + maxBlockSize + ", got " + blockSize);
        }
        if ((blockSize & blockSize - 1) != 0x0) {
            throw new IllegalArgumentException("blockSize must be a power of two, got " + blockSize);
        }
        return Integer.numberOfTrailingZeros(blockSize);
    }
    
    static int numBlocks(final long size, final int blockSize) {
        final int numBlocks = (int)(size / blockSize) + ((size % blockSize != 0L) ? 1 : 0);
        if (numBlocks * (long)blockSize < size) {
            throw new IllegalArgumentException("size is too large for this block size");
        }
        return numBlocks;
    }
    
    public enum Format
    {
        PACKED(0) {
            @Override
            public long byteCount(final int packedIntsVersion, final int valueCount, final int bitsPerValue) {
                if (packedIntsVersion < 1) {
                    return 8L * (long)Math.ceil(valueCount * (double)bitsPerValue / 64.0);
                }
                return (long)Math.ceil(valueCount * (double)bitsPerValue / 8.0);
            }
        }, 
        PACKED_SINGLE_BLOCK(1) {
            @Override
            public int longCount(final int packedIntsVersion, final int valueCount, final int bitsPerValue) {
                final int valuesPerBlock = 64 / bitsPerValue;
                return (int)Math.ceil(valueCount / (double)valuesPerBlock);
            }
            
            @Override
            public boolean isSupported(final int bitsPerValue) {
                return Packed64SingleBlock.isSupported(bitsPerValue);
            }
            
            @Override
            public float overheadPerValue(final int bitsPerValue) {
                assert this.isSupported(bitsPerValue);
                final int valuesPerBlock = 64 / bitsPerValue;
                final int overhead = 64 % bitsPerValue;
                return overhead / (float)valuesPerBlock;
            }
        };
        
        public int id;
        
        public static Format byId(final int id) {
            for (final Format format : values()) {
                if (format.getId() == id) {
                    return format;
                }
            }
            throw new IllegalArgumentException("Unknown format id: " + id);
        }
        
        private Format(final int id) {
            this.id = id;
        }
        
        public int getId() {
            return this.id;
        }
        
        public long byteCount(final int packedIntsVersion, final int valueCount, final int bitsPerValue) {
            assert bitsPerValue >= 0 && bitsPerValue <= 64 : bitsPerValue;
            return 8L * this.longCount(packedIntsVersion, valueCount, bitsPerValue);
        }
        
        public int longCount(final int packedIntsVersion, final int valueCount, final int bitsPerValue) {
            assert bitsPerValue >= 0 && bitsPerValue <= 64 : bitsPerValue;
            final long byteCount = this.byteCount(packedIntsVersion, valueCount, bitsPerValue);
            assert byteCount < 17179869176L;
            if (byteCount % 8L == 0L) {
                return (int)(byteCount / 8L);
            }
            return (int)(byteCount / 8L + 1L);
        }
        
        public boolean isSupported(final int bitsPerValue) {
            return bitsPerValue >= 1 && bitsPerValue <= 64;
        }
        
        public float overheadPerValue(final int bitsPerValue) {
            assert this.isSupported(bitsPerValue);
            return 0.0f;
        }
        
        public final float overheadRatio(final int bitsPerValue) {
            assert this.isSupported(bitsPerValue);
            return this.overheadPerValue(bitsPerValue) / bitsPerValue;
        }
    }
    
    public static class FormatAndBits
    {
        public final Format format;
        public final int bitsPerValue;
        
        public FormatAndBits(final Format format, final int bitsPerValue) {
            this.format = format;
            this.bitsPerValue = bitsPerValue;
        }
        
        @Override
        public String toString() {
            return "FormatAndBits(format=" + this.format + " bitsPerValue=" + this.bitsPerValue + ")";
        }
    }
    
    public abstract static class Reader extends NumericDocValues implements Accountable
    {
        public int get(final int index, final long[] arr, final int off, final int len) {
            assert len > 0 : "len must be > 0 (got " + len + ")";
            assert index >= 0 && index < this.size();
            assert off + len <= arr.length;
            final int gets = Math.min(this.size() - index, len);
            for (int i = index, o = off, end = index + gets; i < end; ++i, ++o) {
                arr[o] = this.get(i);
            }
            return gets;
        }
        
        public abstract int size();
        
        @Override
        public Collection<Accountable> getChildResources() {
            return (Collection<Accountable>)Collections.emptyList();
        }
    }
    
    abstract static class ReaderIteratorImpl implements ReaderIterator
    {
        protected final DataInput in;
        protected final int bitsPerValue;
        protected final int valueCount;
        
        protected ReaderIteratorImpl(final int valueCount, final int bitsPerValue, final DataInput in) {
            this.in = in;
            this.bitsPerValue = bitsPerValue;
            this.valueCount = valueCount;
        }
        
        @Override
        public long next() throws IOException {
            final LongsRef nextValues = this.next(1);
            assert nextValues.length > 0;
            final long result = nextValues.longs[nextValues.offset];
            final LongsRef longsRef = nextValues;
            ++longsRef.offset;
            final LongsRef longsRef2 = nextValues;
            --longsRef2.length;
            return result;
        }
        
        @Override
        public int getBitsPerValue() {
            return this.bitsPerValue;
        }
        
        @Override
        public int size() {
            return this.valueCount;
        }
    }
    
    public abstract static class Mutable extends Reader
    {
        public abstract int getBitsPerValue();
        
        public abstract void set(final int p0, final long p1);
        
        public int set(final int index, final long[] arr, final int off, int len) {
            assert len > 0 : "len must be > 0 (got " + len + ")";
            assert index >= 0 && index < this.size();
            len = Math.min(len, this.size() - index);
            assert off + len <= arr.length;
            for (int i = index, o = off, end = index + len; i < end; ++i, ++o) {
                this.set(i, arr[o]);
            }
            return len;
        }
        
        public void fill(final int fromIndex, final int toIndex, final long val) {
            assert val <= PackedInts.maxValue(this.getBitsPerValue());
            assert fromIndex <= toIndex;
            for (int i = fromIndex; i < toIndex; ++i) {
                this.set(i, val);
            }
        }
        
        public void clear() {
            this.fill(0, this.size(), 0L);
        }
        
        public void save(final DataOutput out) throws IOException {
            final Writer writer = PackedInts.getWriterNoHeader(out, this.getFormat(), this.size(), this.getBitsPerValue(), 1024);
            writer.writeHeader();
            for (int i = 0; i < this.size(); ++i) {
                writer.add(this.get(i));
            }
            writer.finish();
        }
        
        Format getFormat() {
            return Format.PACKED;
        }
    }
    
    abstract static class ReaderImpl extends Reader
    {
        protected final int valueCount;
        
        protected ReaderImpl(final int valueCount) {
            this.valueCount = valueCount;
        }
        
        @Override
        public abstract long get(final int p0);
        
        @Override
        public final int size() {
            return this.valueCount;
        }
    }
    
    abstract static class MutableImpl extends Mutable
    {
        protected final int valueCount;
        protected final int bitsPerValue;
        
        protected MutableImpl(final int valueCount, final int bitsPerValue) {
            this.valueCount = valueCount;
            assert bitsPerValue > 0 && bitsPerValue <= 64 : "bitsPerValue=" + bitsPerValue;
            this.bitsPerValue = bitsPerValue;
        }
        
        @Override
        public final int getBitsPerValue() {
            return this.bitsPerValue;
        }
        
        @Override
        public final int size() {
            return this.valueCount;
        }
        
        @Override
        public String toString() {
            return this.getClass().getSimpleName() + "(valueCount=" + this.valueCount + ",bitsPerValue=" + this.bitsPerValue + ")";
        }
    }
    
    public static final class NullReader extends Reader
    {
        private final int valueCount;
        
        public NullReader(final int valueCount) {
            this.valueCount = valueCount;
        }
        
        @Override
        public long get(final int index) {
            return 0L;
        }
        
        @Override
        public int get(final int index, final long[] arr, final int off, int len) {
            assert len > 0 : "len must be > 0 (got " + len + ")";
            assert index >= 0 && index < this.valueCount;
            len = Math.min(len, this.valueCount - index);
            Arrays.fill(arr, off, off + len, 0L);
            return len;
        }
        
        @Override
        public int size() {
            return this.valueCount;
        }
        
        @Override
        public long ramBytesUsed() {
            return RamUsageEstimator.alignObjectSize(RamUsageEstimator.NUM_BYTES_OBJECT_HEADER + 4);
        }
    }
    
    public abstract static class Writer
    {
        protected final DataOutput out;
        protected final int valueCount;
        protected final int bitsPerValue;
        
        protected Writer(final DataOutput out, final int valueCount, final int bitsPerValue) {
            assert bitsPerValue <= 64;
            assert valueCount == -1;
            this.out = out;
            this.valueCount = valueCount;
            this.bitsPerValue = bitsPerValue;
        }
        
        void writeHeader() throws IOException {
            assert this.valueCount != -1;
            CodecUtil.writeHeader(this.out, "PackedInts", 2);
            this.out.writeVInt(this.bitsPerValue);
            this.out.writeVInt(this.valueCount);
            this.out.writeVInt(this.getFormat().getId());
        }
        
        protected abstract Format getFormat();
        
        public abstract void add(final long p0) throws IOException;
        
        public final int bitsPerValue() {
            return this.bitsPerValue;
        }
        
        public abstract void finish() throws IOException;
        
        public abstract int ord();
    }
    
    public interface ReaderIterator
    {
        long next() throws IOException;
        
        LongsRef next(final int p0) throws IOException;
        
        int getBitsPerValue();
        
        int size();
        
        int ord();
    }
    
    public interface Encoder
    {
        int longBlockCount();
        
        int longValueCount();
        
        int byteBlockCount();
        
        int byteValueCount();
        
        void encode(final long[] p0, final int p1, final long[] p2, final int p3, final int p4);
        
        void encode(final long[] p0, final int p1, final byte[] p2, final int p3, final int p4);
        
        void encode(final int[] p0, final int p1, final long[] p2, final int p3, final int p4);
        
        void encode(final int[] p0, final int p1, final byte[] p2, final int p3, final int p4);
    }
    
    public interface Decoder
    {
        int longBlockCount();
        
        int longValueCount();
        
        int byteBlockCount();
        
        int byteValueCount();
        
        void decode(final long[] p0, final int p1, final long[] p2, final int p3, final int p4);
        
        void decode(final byte[] p0, final int p1, final long[] p2, final int p3, final int p4);
        
        void decode(final long[] p0, final int p1, final int[] p2, final int p3, final int p4);
        
        void decode(final byte[] p0, final int p1, final int[] p2, final int p3, final int p4);
    }
}
