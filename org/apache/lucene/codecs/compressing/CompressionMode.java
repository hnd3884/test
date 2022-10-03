package org.apache.lucene.codecs.compressing;

import java.util.zip.Deflater;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;
import org.apache.lucene.store.DataOutput;
import java.io.IOException;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.util.ArrayUtil;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.store.DataInput;

public abstract class CompressionMode
{
    public static final CompressionMode FAST;
    public static final CompressionMode HIGH_COMPRESSION;
    public static final CompressionMode FAST_DECOMPRESSION;
    private static final Decompressor LZ4_DECOMPRESSOR;
    
    protected CompressionMode() {
    }
    
    public abstract Compressor newCompressor();
    
    public abstract Decompressor newDecompressor();
    
    static {
        FAST = new CompressionMode() {
            @Override
            public Compressor newCompressor() {
                return new LZ4FastCompressor();
            }
            
            @Override
            public Decompressor newDecompressor() {
                return CompressionMode.LZ4_DECOMPRESSOR;
            }
            
            @Override
            public String toString() {
                return "FAST";
            }
        };
        HIGH_COMPRESSION = new CompressionMode() {
            @Override
            public Compressor newCompressor() {
                return new DeflateCompressor(6);
            }
            
            @Override
            public Decompressor newDecompressor() {
                return new DeflateDecompressor();
            }
            
            @Override
            public String toString() {
                return "HIGH_COMPRESSION";
            }
        };
        FAST_DECOMPRESSION = new CompressionMode() {
            @Override
            public Compressor newCompressor() {
                return new LZ4HighCompressor();
            }
            
            @Override
            public Decompressor newDecompressor() {
                return CompressionMode.LZ4_DECOMPRESSOR;
            }
            
            @Override
            public String toString() {
                return "FAST_DECOMPRESSION";
            }
        };
        LZ4_DECOMPRESSOR = new Decompressor() {
            @Override
            public void decompress(final DataInput in, final int originalLength, final int offset, final int length, final BytesRef bytes) throws IOException {
                assert offset + length <= originalLength;
                if (bytes.bytes.length < originalLength + 7) {
                    bytes.bytes = new byte[ArrayUtil.oversize(originalLength + 7, 1)];
                }
                final int decompressedLength = LZ4.decompress(in, offset + length, bytes.bytes, 0);
                if (decompressedLength > originalLength) {
                    throw new CorruptIndexException("Corrupted: lengths mismatch: " + decompressedLength + " > " + originalLength, in);
                }
                bytes.offset = offset;
                bytes.length = length;
            }
            
            @Override
            public Decompressor clone() {
                return this;
            }
        };
    }
    
    private static final class LZ4FastCompressor extends Compressor
    {
        private final LZ4.HashTable ht;
        
        LZ4FastCompressor() {
            this.ht = new LZ4.HashTable();
        }
        
        @Override
        public void compress(final byte[] bytes, final int off, final int len, final DataOutput out) throws IOException {
            LZ4.compress(bytes, off, len, out, this.ht);
        }
    }
    
    private static final class LZ4HighCompressor extends Compressor
    {
        private final LZ4.HCHashTable ht;
        
        LZ4HighCompressor() {
            this.ht = new LZ4.HCHashTable();
        }
        
        @Override
        public void compress(final byte[] bytes, final int off, final int len, final DataOutput out) throws IOException {
            LZ4.compressHC(bytes, off, len, out, this.ht);
        }
    }
    
    private static final class DeflateDecompressor extends Decompressor
    {
        final Inflater decompressor;
        byte[] compressed;
        
        DeflateDecompressor() {
            this.decompressor = new Inflater(true);
            this.compressed = new byte[0];
        }
        
        @Override
        public void decompress(final DataInput in, final int originalLength, final int offset, final int length, final BytesRef bytes) throws IOException {
            assert offset + length <= originalLength;
            if (length == 0) {
                bytes.length = 0;
                return;
            }
            final int compressedLength = in.readVInt();
            final int paddedLength = compressedLength + 1;
            in.readBytes(this.compressed = ArrayUtil.grow(this.compressed, paddedLength), 0, compressedLength);
            this.compressed[compressedLength] = 0;
            this.decompressor.reset();
            this.decompressor.setInput(this.compressed, 0, paddedLength);
            final int n = 0;
            bytes.length = n;
            bytes.offset = n;
            bytes.bytes = ArrayUtil.grow(bytes.bytes, originalLength);
            try {
                bytes.length = this.decompressor.inflate(bytes.bytes, bytes.length, originalLength);
            }
            catch (final DataFormatException e) {
                throw new IOException(e);
            }
            if (!this.decompressor.finished()) {
                throw new CorruptIndexException("Invalid decoder state: needsInput=" + this.decompressor.needsInput() + ", needsDict=" + this.decompressor.needsDictionary(), in);
            }
            if (bytes.length != originalLength) {
                throw new CorruptIndexException("Lengths mismatch: " + bytes.length + " != " + originalLength, in);
            }
            bytes.offset = offset;
            bytes.length = length;
        }
        
        @Override
        public Decompressor clone() {
            return new DeflateDecompressor();
        }
    }
    
    private static class DeflateCompressor extends Compressor
    {
        final Deflater compressor;
        byte[] compressed;
        
        DeflateCompressor(final int level) {
            this.compressor = new Deflater(level, true);
            this.compressed = new byte[64];
        }
        
        @Override
        public void compress(final byte[] bytes, final int off, final int len, final DataOutput out) throws IOException {
            this.compressor.reset();
            this.compressor.setInput(bytes, off, len);
            this.compressor.finish();
            if (this.compressor.needsInput()) {
                assert len == 0 : len;
                out.writeVInt(0);
            }
            else {
                int totalCount = 0;
                while (true) {
                    final int count = this.compressor.deflate(this.compressed, totalCount, this.compressed.length - totalCount);
                    totalCount += count;
                    assert totalCount <= this.compressed.length;
                    if (this.compressor.finished()) {
                        out.writeVInt(totalCount);
                        out.writeBytes(this.compressed, totalCount);
                        return;
                    }
                    this.compressed = ArrayUtil.grow(this.compressed);
                }
            }
        }
    }
}
