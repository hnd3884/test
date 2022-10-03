package org.apache.lucene.codecs.lucene50;

import java.util.Arrays;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.store.IndexOutput;
import org.apache.lucene.store.DataInput;
import java.io.IOException;
import org.apache.lucene.store.DataOutput;
import org.apache.lucene.util.packed.PackedInts;

final class ForUtil
{
    private static final int ALL_VALUES_EQUAL = 0;
    static final int MAX_ENCODED_SIZE = 512;
    static final int MAX_DATA_SIZE;
    private final int[] encodedSizes;
    private final PackedInts.Encoder[] encoders;
    private final PackedInts.Decoder[] decoders;
    private final int[] iterations;
    
    private static int computeIterations(final PackedInts.Decoder decoder) {
        return (int)Math.ceil(128.0f / decoder.byteValueCount());
    }
    
    private static int encodedSize(final PackedInts.Format format, final int packedIntsVersion, final int bitsPerValue) {
        final long byteCount = format.byteCount(packedIntsVersion, 128, bitsPerValue);
        assert byteCount >= 0L && byteCount <= 2147483647L : byteCount;
        return (int)byteCount;
    }
    
    ForUtil(final float acceptableOverheadRatio, final DataOutput out) throws IOException {
        out.writeVInt(2);
        this.encodedSizes = new int[33];
        this.encoders = new PackedInts.Encoder[33];
        this.decoders = new PackedInts.Decoder[33];
        this.iterations = new int[33];
        for (int bpv = 1; bpv <= 32; ++bpv) {
            final PackedInts.FormatAndBits formatAndBits = PackedInts.fastestFormatAndBits(128, bpv, acceptableOverheadRatio);
            assert formatAndBits.format.isSupported(formatAndBits.bitsPerValue);
            assert formatAndBits.bitsPerValue <= 32;
            this.encodedSizes[bpv] = encodedSize(formatAndBits.format, 2, formatAndBits.bitsPerValue);
            this.encoders[bpv] = PackedInts.getEncoder(formatAndBits.format, 2, formatAndBits.bitsPerValue);
            this.decoders[bpv] = PackedInts.getDecoder(formatAndBits.format, 2, formatAndBits.bitsPerValue);
            this.iterations[bpv] = computeIterations(this.decoders[bpv]);
            out.writeVInt(formatAndBits.format.getId() << 5 | formatAndBits.bitsPerValue - 1);
        }
    }
    
    ForUtil(final DataInput in) throws IOException {
        final int packedIntsVersion = in.readVInt();
        PackedInts.checkVersion(packedIntsVersion);
        this.encodedSizes = new int[33];
        this.encoders = new PackedInts.Encoder[33];
        this.decoders = new PackedInts.Decoder[33];
        this.iterations = new int[33];
        for (int bpv = 1; bpv <= 32; ++bpv) {
            final int code = in.readVInt();
            final int formatId = code >>> 5;
            final int bitsPerValue = (code & 0x1F) + 1;
            final PackedInts.Format format = PackedInts.Format.byId(formatId);
            assert format.isSupported(bitsPerValue);
            this.encodedSizes[bpv] = encodedSize(format, packedIntsVersion, bitsPerValue);
            this.encoders[bpv] = PackedInts.getEncoder(format, packedIntsVersion, bitsPerValue);
            this.decoders[bpv] = PackedInts.getDecoder(format, packedIntsVersion, bitsPerValue);
            this.iterations[bpv] = computeIterations(this.decoders[bpv]);
        }
    }
    
    void writeBlock(final int[] data, final byte[] encoded, final IndexOutput out) throws IOException {
        if (isAllEqual(data)) {
            out.writeByte((byte)0);
            out.writeVInt(data[0]);
            return;
        }
        final int numBits = bitsRequired(data);
        assert numBits > 0 && numBits <= 32 : numBits;
        final PackedInts.Encoder encoder = this.encoders[numBits];
        final int iters = this.iterations[numBits];
        assert iters * encoder.byteValueCount() >= 128;
        final int encodedSize = this.encodedSizes[numBits];
        assert iters * encoder.byteBlockCount() >= encodedSize;
        out.writeByte((byte)numBits);
        encoder.encode(data, 0, encoded, 0, iters);
        out.writeBytes(encoded, encodedSize);
    }
    
    void readBlock(final IndexInput in, final byte[] encoded, final int[] decoded) throws IOException {
        final int numBits = in.readByte();
        assert numBits <= 32 : numBits;
        if (numBits == 0) {
            final int value = in.readVInt();
            Arrays.fill(decoded, 0, 128, value);
            return;
        }
        final int encodedSize = this.encodedSizes[numBits];
        in.readBytes(encoded, 0, encodedSize);
        final PackedInts.Decoder decoder = this.decoders[numBits];
        final int iters = this.iterations[numBits];
        assert iters * decoder.byteValueCount() >= 128;
        decoder.decode(encoded, 0, decoded, 0, iters);
    }
    
    void skipBlock(final IndexInput in) throws IOException {
        final int numBits = in.readByte();
        if (numBits == 0) {
            in.readVInt();
            return;
        }
        assert numBits > 0 && numBits <= 32 : numBits;
        final int encodedSize = this.encodedSizes[numBits];
        in.seek(in.getFilePointer() + encodedSize);
    }
    
    private static boolean isAllEqual(final int[] data) {
        final int v = data[0];
        for (int i = 1; i < 128; ++i) {
            if (data[i] != v) {
                return false;
            }
        }
        return true;
    }
    
    private static int bitsRequired(final int[] data) {
        long or = 0L;
        for (int i = 0; i < 128; ++i) {
            assert data[i] >= 0;
            or |= data[i];
        }
        return PackedInts.bitsRequired(or);
    }
    
    static {
        int maxDataSize = 0;
        for (int version = 0; version <= 2; ++version) {
            for (final PackedInts.Format format : PackedInts.Format.values()) {
                for (int bpv = 1; bpv <= 32; ++bpv) {
                    if (format.isSupported(bpv)) {
                        final PackedInts.Decoder decoder = PackedInts.getDecoder(format, version, bpv);
                        final int iterations = computeIterations(decoder);
                        maxDataSize = Math.max(maxDataSize, iterations * decoder.byteValueCount());
                    }
                }
            }
        }
        MAX_DATA_SIZE = maxDataSize;
    }
}
