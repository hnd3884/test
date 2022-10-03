package org.tukaani.xz;

import java.io.EOFException;
import org.tukaani.xz.common.StreamFlags;
import org.tukaani.xz.common.DecoderUtil;
import java.util.Arrays;
import java.io.InputStream;
import java.io.DataInputStream;
import java.io.IOException;
import org.tukaani.xz.check.Check;
import org.tukaani.xz.index.BlockInfo;
import org.tukaani.xz.index.IndexDecoder;
import java.util.ArrayList;

public class SeekableXZInputStream extends SeekableInputStream
{
    private final ArrayCache arrayCache;
    private SeekableInputStream in;
    private final int memoryLimit;
    private int indexMemoryUsage;
    private final ArrayList<IndexDecoder> streams;
    private int checkTypes;
    private long uncompressedSize;
    private long largestBlockSize;
    private int blockCount;
    private final BlockInfo curBlockInfo;
    private final BlockInfo queriedBlockInfo;
    private Check check;
    private final boolean verifyCheck;
    private BlockInputStream blockDecoder;
    private long curPos;
    private long seekPos;
    private boolean seekNeeded;
    private boolean endReached;
    private IOException exception;
    private final byte[] tempBuf;
    
    public SeekableXZInputStream(final SeekableInputStream seekableInputStream) throws IOException {
        this(seekableInputStream, -1);
    }
    
    public SeekableXZInputStream(final SeekableInputStream seekableInputStream, final ArrayCache arrayCache) throws IOException {
        this(seekableInputStream, -1, arrayCache);
    }
    
    public SeekableXZInputStream(final SeekableInputStream seekableInputStream, final int n) throws IOException {
        this(seekableInputStream, n, true);
    }
    
    public SeekableXZInputStream(final SeekableInputStream seekableInputStream, final int n, final ArrayCache arrayCache) throws IOException {
        this(seekableInputStream, n, true, arrayCache);
    }
    
    public SeekableXZInputStream(final SeekableInputStream seekableInputStream, final int n, final boolean b) throws IOException {
        this(seekableInputStream, n, b, ArrayCache.getDefaultCache());
    }
    
    public SeekableXZInputStream(final SeekableInputStream in, int memoryLimit, final boolean verifyCheck, final ArrayCache arrayCache) throws IOException {
        this.indexMemoryUsage = 0;
        this.streams = new ArrayList<IndexDecoder>();
        this.checkTypes = 0;
        this.uncompressedSize = 0L;
        this.largestBlockSize = 0L;
        this.blockCount = 0;
        this.blockDecoder = null;
        this.curPos = 0L;
        this.seekNeeded = false;
        this.endReached = false;
        this.exception = null;
        this.tempBuf = new byte[1];
        this.arrayCache = arrayCache;
        this.verifyCheck = verifyCheck;
        this.in = in;
        final DataInputStream dataInputStream = new DataInputStream(in);
        in.seek(0L);
        final byte[] array = new byte[XZ.HEADER_MAGIC.length];
        dataInputStream.readFully(array);
        if (!Arrays.equals(array, XZ.HEADER_MAGIC)) {
            throw new XZFormatException();
        }
        long length = in.length();
        if ((length & 0x3L) != 0x0L) {
            throw new CorruptedInputException("XZ file size is not a multiple of 4 bytes");
        }
        final byte[] array2 = new byte[12];
        long n = 0L;
        while (length > 0L) {
            if (length < 12L) {
                throw new CorruptedInputException();
            }
            in.seek(length - 12L);
            dataInputStream.readFully(array2);
            if (array2[8] == 0 && array2[9] == 0 && array2[10] == 0 && array2[11] == 0) {
                n += 4L;
                length -= 4L;
            }
            else {
                final long n2 = length - 12L;
                final StreamFlags decodeStreamFooter = DecoderUtil.decodeStreamFooter(array2);
                if (decodeStreamFooter.backwardSize >= n2) {
                    throw new CorruptedInputException("Backward Size in XZ Stream Footer is too big");
                }
                this.check = Check.getInstance(decodeStreamFooter.checkType);
                this.checkTypes |= 1 << decodeStreamFooter.checkType;
                in.seek(n2 - decodeStreamFooter.backwardSize);
                IndexDecoder indexDecoder;
                try {
                    indexDecoder = new IndexDecoder(in, decodeStreamFooter, n, memoryLimit);
                }
                catch (final MemoryLimitException ex) {
                    assert memoryLimit >= 0;
                    throw new MemoryLimitException(ex.getMemoryNeeded() + this.indexMemoryUsage, memoryLimit + this.indexMemoryUsage);
                }
                this.indexMemoryUsage += indexDecoder.getMemoryUsage();
                if (memoryLimit >= 0) {
                    memoryLimit -= indexDecoder.getMemoryUsage();
                    assert memoryLimit >= 0;
                }
                if (this.largestBlockSize < indexDecoder.getLargestBlockSize()) {
                    this.largestBlockSize = indexDecoder.getLargestBlockSize();
                }
                final long n3 = indexDecoder.getStreamSize() - 12L;
                if (n2 < n3) {
                    throw new CorruptedInputException("XZ Index indicates too big compressed size for the XZ Stream");
                }
                length = n2 - n3;
                in.seek(length);
                dataInputStream.readFully(array2);
                if (!DecoderUtil.areStreamFlagsEqual(DecoderUtil.decodeStreamHeader(array2), decodeStreamFooter)) {
                    throw new CorruptedInputException("XZ Stream Footer does not match Stream Header");
                }
                this.uncompressedSize += indexDecoder.getUncompressedSize();
                if (this.uncompressedSize < 0L) {
                    throw new UnsupportedOptionsException("XZ file is too big");
                }
                this.blockCount += indexDecoder.getRecordCount();
                if (this.blockCount < 0) {
                    throw new UnsupportedOptionsException("XZ file has over 2147483647 Blocks");
                }
                this.streams.add(indexDecoder);
                n = 0L;
            }
        }
        assert length == 0L;
        this.memoryLimit = memoryLimit;
        IndexDecoder offsets = this.streams.get(this.streams.size() - 1);
        for (int i = this.streams.size() - 2; i >= 0; --i) {
            final IndexDecoder indexDecoder2 = this.streams.get(i);
            indexDecoder2.setOffsets(offsets);
            offsets = indexDecoder2;
        }
        final IndexDecoder indexDecoder3 = this.streams.get(this.streams.size() - 1);
        this.curBlockInfo = new BlockInfo(indexDecoder3);
        this.queriedBlockInfo = new BlockInfo(indexDecoder3);
    }
    
    public int getCheckTypes() {
        return this.checkTypes;
    }
    
    public int getIndexMemoryUsage() {
        return this.indexMemoryUsage;
    }
    
    public long getLargestBlockSize() {
        return this.largestBlockSize;
    }
    
    public int getStreamCount() {
        return this.streams.size();
    }
    
    public int getBlockCount() {
        return this.blockCount;
    }
    
    public long getBlockPos(final int n) {
        this.locateBlockByNumber(this.queriedBlockInfo, n);
        return this.queriedBlockInfo.uncompressedOffset;
    }
    
    public long getBlockSize(final int n) {
        this.locateBlockByNumber(this.queriedBlockInfo, n);
        return this.queriedBlockInfo.uncompressedSize;
    }
    
    public long getBlockCompPos(final int n) {
        this.locateBlockByNumber(this.queriedBlockInfo, n);
        return this.queriedBlockInfo.compressedOffset;
    }
    
    public long getBlockCompSize(final int n) {
        this.locateBlockByNumber(this.queriedBlockInfo, n);
        return this.queriedBlockInfo.unpaddedSize + 3L & 0xFFFFFFFFFFFFFFFCL;
    }
    
    public int getBlockCheckType(final int n) {
        this.locateBlockByNumber(this.queriedBlockInfo, n);
        return this.queriedBlockInfo.getCheckType();
    }
    
    public int getBlockNumber(final long n) {
        this.locateBlockByPos(this.queriedBlockInfo, n);
        return this.queriedBlockInfo.blockNumber;
    }
    
    @Override
    public int read() throws IOException {
        return (this.read(this.tempBuf, 0, 1) == -1) ? -1 : (this.tempBuf[0] & 0xFF);
    }
    
    @Override
    public int read(final byte[] array, int n, int i) throws IOException {
        if (n < 0 || i < 0 || n + i < 0 || n + i > array.length) {
            throw new IndexOutOfBoundsException();
        }
        if (i == 0) {
            return 0;
        }
        if (this.in == null) {
            throw new XZIOException("Stream closed");
        }
        if (this.exception != null) {
            throw this.exception;
        }
        int n2 = 0;
        try {
            if (this.seekNeeded) {
                this.seek();
            }
            if (this.endReached) {
                return -1;
            }
            while (i > 0) {
                if (this.blockDecoder == null) {
                    this.seek();
                    if (this.endReached) {
                        break;
                    }
                }
                final int read = this.blockDecoder.read(array, n, i);
                if (read > 0) {
                    this.curPos += read;
                    n2 += read;
                    n += read;
                    i -= read;
                }
                else {
                    if (read != -1) {
                        continue;
                    }
                    this.blockDecoder = null;
                }
            }
        }
        catch (final IOException exception) {
            if (exception instanceof EOFException) {
                exception = new CorruptedInputException();
            }
            this.exception = exception;
            if (n2 == 0) {
                throw exception;
            }
        }
        return n2;
    }
    
    @Override
    public int available() throws IOException {
        if (this.in == null) {
            throw new XZIOException("Stream closed");
        }
        if (this.exception != null) {
            throw this.exception;
        }
        if (this.endReached || this.seekNeeded || this.blockDecoder == null) {
            return 0;
        }
        return this.blockDecoder.available();
    }
    
    @Override
    public void close() throws IOException {
        this.close(true);
    }
    
    public void close(final boolean b) throws IOException {
        if (this.in != null) {
            if (this.blockDecoder != null) {
                this.blockDecoder.close();
                this.blockDecoder = null;
            }
            try {
                if (b) {
                    this.in.close();
                }
            }
            finally {
                this.in = null;
            }
        }
    }
    
    @Override
    public long length() {
        return this.uncompressedSize;
    }
    
    @Override
    public long position() throws IOException {
        if (this.in == null) {
            throw new XZIOException("Stream closed");
        }
        return this.seekNeeded ? this.seekPos : this.curPos;
    }
    
    @Override
    public void seek(final long seekPos) throws IOException {
        if (this.in == null) {
            throw new XZIOException("Stream closed");
        }
        if (seekPos < 0L) {
            throw new XZIOException("Negative seek position: " + seekPos);
        }
        this.seekPos = seekPos;
        this.seekNeeded = true;
    }
    
    public void seekToBlock(final int n) throws IOException {
        if (this.in == null) {
            throw new XZIOException("Stream closed");
        }
        if (n < 0 || n >= this.blockCount) {
            throw new XZIOException("Invalid XZ Block number: " + n);
        }
        this.seekPos = this.getBlockPos(n);
        this.seekNeeded = true;
    }
    
    private void seek() throws IOException {
        if (!this.seekNeeded) {
            if (this.curBlockInfo.hasNext()) {
                this.curBlockInfo.setNext();
                this.initBlockDecoder();
                return;
            }
            this.seekPos = this.curPos;
        }
        this.seekNeeded = false;
        if (this.seekPos >= this.uncompressedSize) {
            this.curPos = this.seekPos;
            if (this.blockDecoder != null) {
                this.blockDecoder.close();
                this.blockDecoder = null;
            }
            this.endReached = true;
            return;
        }
        this.endReached = false;
        this.locateBlockByPos(this.curBlockInfo, this.seekPos);
        if (this.curPos <= this.curBlockInfo.uncompressedOffset || this.curPos > this.seekPos) {
            this.in.seek(this.curBlockInfo.compressedOffset);
            this.check = Check.getInstance(this.curBlockInfo.getCheckType());
            this.initBlockDecoder();
            this.curPos = this.curBlockInfo.uncompressedOffset;
        }
        if (this.seekPos > this.curPos) {
            final long n = this.seekPos - this.curPos;
            if (this.blockDecoder.skip(n) != n) {
                throw new CorruptedInputException();
            }
            this.curPos = this.seekPos;
        }
    }
    
    private void locateBlockByPos(final BlockInfo blockInfo, final long n) {
        if (n < 0L || n >= this.uncompressedSize) {
            throw new IndexOutOfBoundsException("Invalid uncompressed position: " + n);
        }
        int n2 = 0;
        IndexDecoder indexDecoder;
        while (true) {
            indexDecoder = this.streams.get(n2);
            if (indexDecoder.hasUncompressedOffset(n)) {
                break;
            }
            ++n2;
        }
        indexDecoder.locateBlock(blockInfo, n);
        assert (blockInfo.compressedOffset & 0x3L) == 0x0L;
        assert blockInfo.uncompressedSize > 0L;
        assert n >= blockInfo.uncompressedOffset;
        assert n < blockInfo.uncompressedOffset + blockInfo.uncompressedSize;
    }
    
    private void locateBlockByNumber(final BlockInfo blockInfo, final int n) {
        if (n < 0 || n >= this.blockCount) {
            throw new IndexOutOfBoundsException("Invalid XZ Block number: " + n);
        }
        if (blockInfo.blockNumber == n) {
            return;
        }
        int n2 = 0;
        IndexDecoder indexDecoder;
        while (true) {
            indexDecoder = this.streams.get(n2);
            if (indexDecoder.hasRecord(n)) {
                break;
            }
            ++n2;
        }
        indexDecoder.setBlockInfo(blockInfo, n);
    }
    
    private void initBlockDecoder() throws IOException {
        try {
            if (this.blockDecoder != null) {
                this.blockDecoder.close();
                this.blockDecoder = null;
            }
            this.blockDecoder = new BlockInputStream(this.in, this.check, this.verifyCheck, this.memoryLimit, this.curBlockInfo.unpaddedSize, this.curBlockInfo.uncompressedSize, this.arrayCache);
        }
        catch (final MemoryLimitException ex) {
            assert this.memoryLimit >= 0;
            throw new MemoryLimitException(ex.getMemoryNeeded() + this.indexMemoryUsage, this.memoryLimit + this.indexMemoryUsage);
        }
        catch (final IndexIndicatorException ex2) {
            throw new CorruptedInputException();
        }
    }
}
