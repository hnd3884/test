package org.tukaani.xz.index;

import java.io.IOException;
import java.io.EOFException;
import org.tukaani.xz.MemoryLimitException;
import org.tukaani.xz.UnsupportedOptionsException;
import org.tukaani.xz.common.DecoderUtil;
import java.util.zip.Checksum;
import java.io.InputStream;
import java.util.zip.CheckedInputStream;
import java.util.zip.CRC32;
import org.tukaani.xz.XZIOException;
import org.tukaani.xz.CorruptedInputException;
import org.tukaani.xz.SeekableInputStream;
import org.tukaani.xz.common.StreamFlags;

public class IndexDecoder extends IndexBase
{
    private final StreamFlags streamFlags;
    private final long streamPadding;
    private final int memoryUsage;
    private final long[] unpadded;
    private final long[] uncompressed;
    private long largestBlockSize;
    private int recordOffset;
    private long compressedOffset;
    private long uncompressedOffset;
    
    public IndexDecoder(final SeekableInputStream seekableInputStream, final StreamFlags streamFlags, final long streamPadding, final int n) throws IOException {
        super(new CorruptedInputException("XZ Index is corrupt"));
        this.largestBlockSize = 0L;
        this.recordOffset = 0;
        this.compressedOffset = 0L;
        this.uncompressedOffset = 0L;
        this.streamFlags = streamFlags;
        this.streamPadding = streamPadding;
        final long n2 = seekableInputStream.position() + streamFlags.backwardSize - 4L;
        final CRC32 crc32 = new CRC32();
        final CheckedInputStream checkedInputStream = new CheckedInputStream(seekableInputStream, crc32);
        if (checkedInputStream.read() != 0) {
            throw new CorruptedInputException("XZ Index is corrupt");
        }
        try {
            final long decodeVLI = DecoderUtil.decodeVLI(checkedInputStream);
            if (decodeVLI >= streamFlags.backwardSize / 2L) {
                throw new CorruptedInputException("XZ Index is corrupt");
            }
            if (decodeVLI > 2147483647L) {
                throw new UnsupportedOptionsException("XZ Index has over 2147483647 Records");
            }
            this.memoryUsage = 1 + (int)((16L * decodeVLI + 1023L) / 1024L);
            if (n >= 0 && this.memoryUsage > n) {
                throw new MemoryLimitException(this.memoryUsage, n);
            }
            this.unpadded = new long[(int)decodeVLI];
            this.uncompressed = new long[(int)decodeVLI];
            int n3 = 0;
            for (int i = (int)decodeVLI; i > 0; --i) {
                final long decodeVLI2 = DecoderUtil.decodeVLI(checkedInputStream);
                final long decodeVLI3 = DecoderUtil.decodeVLI(checkedInputStream);
                if (seekableInputStream.position() > n2) {
                    throw new CorruptedInputException("XZ Index is corrupt");
                }
                this.unpadded[n3] = this.blocksSum + decodeVLI2;
                this.uncompressed[n3] = this.uncompressedSum + decodeVLI3;
                ++n3;
                super.add(decodeVLI2, decodeVLI3);
                assert n3 == this.recordCount;
                if (this.largestBlockSize < decodeVLI3) {
                    this.largestBlockSize = decodeVLI3;
                }
            }
        }
        catch (final EOFException ex) {
            throw new CorruptedInputException("XZ Index is corrupt");
        }
        int indexPaddingSize = this.getIndexPaddingSize();
        if (seekableInputStream.position() + indexPaddingSize != n2) {
            throw new CorruptedInputException("XZ Index is corrupt");
        }
        while (indexPaddingSize-- > 0) {
            if (checkedInputStream.read() != 0) {
                throw new CorruptedInputException("XZ Index is corrupt");
            }
        }
        final long value = crc32.getValue();
        for (int j = 0; j < 4; ++j) {
            if ((value >>> j * 8 & 0xFFL) != seekableInputStream.read()) {
                throw new CorruptedInputException("XZ Index is corrupt");
            }
        }
    }
    
    public void setOffsets(final IndexDecoder indexDecoder) {
        this.recordOffset = indexDecoder.recordOffset + (int)indexDecoder.recordCount;
        this.compressedOffset = indexDecoder.compressedOffset + indexDecoder.getStreamSize() + indexDecoder.streamPadding;
        assert (this.compressedOffset & 0x3L) == 0x0L;
        this.uncompressedOffset = indexDecoder.uncompressedOffset + indexDecoder.uncompressedSum;
    }
    
    public int getMemoryUsage() {
        return this.memoryUsage;
    }
    
    public StreamFlags getStreamFlags() {
        return this.streamFlags;
    }
    
    public int getRecordCount() {
        return (int)this.recordCount;
    }
    
    public long getUncompressedSize() {
        return this.uncompressedSum;
    }
    
    public long getLargestBlockSize() {
        return this.largestBlockSize;
    }
    
    public boolean hasUncompressedOffset(final long n) {
        return n >= this.uncompressedOffset && n < this.uncompressedOffset + this.uncompressedSum;
    }
    
    public boolean hasRecord(final int n) {
        return n >= this.recordOffset && n < this.recordOffset + this.recordCount;
    }
    
    public void locateBlock(final BlockInfo blockInfo, long n) {
        assert n >= this.uncompressedOffset;
        n -= this.uncompressedOffset;
        assert n < this.uncompressedSum;
        int i = 0;
        int n2 = this.unpadded.length - 1;
        while (i < n2) {
            final int n3 = i + (n2 - i) / 2;
            if (this.uncompressed[n3] <= n) {
                i = n3 + 1;
            }
            else {
                n2 = n3;
            }
        }
        this.setBlockInfo(blockInfo, this.recordOffset + i);
    }
    
    public void setBlockInfo(final BlockInfo blockInfo, final int blockNumber) {
        assert blockNumber >= this.recordOffset;
        assert blockNumber - this.recordOffset < this.recordCount;
        blockInfo.index = this;
        blockInfo.blockNumber = blockNumber;
        final int n = blockNumber - this.recordOffset;
        if (n == 0) {
            blockInfo.compressedOffset = 0L;
            blockInfo.uncompressedOffset = 0L;
        }
        else {
            blockInfo.compressedOffset = (this.unpadded[n - 1] + 3L & 0xFFFFFFFFFFFFFFFCL);
            blockInfo.uncompressedOffset = this.uncompressed[n - 1];
        }
        blockInfo.unpaddedSize = this.unpadded[n] - blockInfo.compressedOffset;
        blockInfo.uncompressedSize = this.uncompressed[n] - blockInfo.uncompressedOffset;
        blockInfo.compressedOffset += this.compressedOffset + 12L;
        blockInfo.uncompressedOffset += this.uncompressedOffset;
    }
}
