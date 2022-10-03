package org.apache.lucene.util;

import org.apache.lucene.store.DataOutput;
import org.apache.lucene.store.DataInput;
import java.util.Collections;
import java.util.Collection;
import java.io.IOException;
import org.apache.lucene.store.IndexInput;
import java.util.Arrays;

public final class PagedBytes implements Accountable
{
    private static final long BASE_RAM_BYTES_USED;
    private byte[][] blocks;
    private int numBlocks;
    private final int blockSize;
    private final int blockBits;
    private final int blockMask;
    private boolean didSkipBytes;
    private boolean frozen;
    private int upto;
    private byte[] currentBlock;
    private final long bytesUsedPerBlock;
    private static final byte[] EMPTY_BYTES;
    
    public PagedBytes(final int blockBits) {
        this.blocks = new byte[16][];
        assert blockBits > 0 && blockBits <= 31 : blockBits;
        this.blockSize = 1 << blockBits;
        this.blockBits = blockBits;
        this.blockMask = this.blockSize - 1;
        this.upto = this.blockSize;
        this.bytesUsedPerBlock = RamUsageEstimator.alignObjectSize(this.blockSize + RamUsageEstimator.NUM_BYTES_ARRAY_HEADER);
        this.numBlocks = 0;
    }
    
    private void addBlock(final byte[] block) {
        if (this.blocks.length == this.numBlocks) {
            this.blocks = Arrays.copyOf(this.blocks, ArrayUtil.oversize(this.numBlocks, RamUsageEstimator.NUM_BYTES_OBJECT_REF));
        }
        this.blocks[this.numBlocks++] = block;
    }
    
    public void copy(final IndexInput in, long byteCount) throws IOException {
        while (byteCount > 0L) {
            int left = this.blockSize - this.upto;
            if (left == 0) {
                if (this.currentBlock != null) {
                    this.addBlock(this.currentBlock);
                }
                this.currentBlock = new byte[this.blockSize];
                this.upto = 0;
                left = this.blockSize;
            }
            if (left >= byteCount) {
                in.readBytes(this.currentBlock, this.upto, (int)byteCount, false);
                this.upto += (int)byteCount;
                break;
            }
            in.readBytes(this.currentBlock, this.upto, left, false);
            this.upto = this.blockSize;
            byteCount -= left;
        }
    }
    
    public void copy(final BytesRef bytes, final BytesRef out) {
        int left = this.blockSize - this.upto;
        if (bytes.length > left || this.currentBlock == null) {
            if (this.currentBlock != null) {
                this.addBlock(this.currentBlock);
                this.didSkipBytes = true;
            }
            this.currentBlock = new byte[this.blockSize];
            this.upto = 0;
            left = this.blockSize;
            assert bytes.length <= this.blockSize;
        }
        out.bytes = this.currentBlock;
        out.offset = this.upto;
        out.length = bytes.length;
        System.arraycopy(bytes.bytes, bytes.offset, this.currentBlock, this.upto, bytes.length);
        this.upto += bytes.length;
    }
    
    public Reader freeze(final boolean trim) {
        if (this.frozen) {
            throw new IllegalStateException("already frozen");
        }
        if (this.didSkipBytes) {
            throw new IllegalStateException("cannot freeze when copy(BytesRef, BytesRef) was used");
        }
        if (trim && this.upto < this.blockSize) {
            final byte[] newBlock = new byte[this.upto];
            System.arraycopy(this.currentBlock, 0, newBlock, 0, this.upto);
            this.currentBlock = newBlock;
        }
        if (this.currentBlock == null) {
            this.currentBlock = PagedBytes.EMPTY_BYTES;
        }
        this.addBlock(this.currentBlock);
        this.frozen = true;
        this.currentBlock = null;
        return new Reader(this);
    }
    
    public long getPointer() {
        if (this.currentBlock == null) {
            return 0L;
        }
        return this.numBlocks * (long)this.blockSize + this.upto;
    }
    
    @Override
    public long ramBytesUsed() {
        long size = PagedBytes.BASE_RAM_BYTES_USED + RamUsageEstimator.shallowSizeOf(this.blocks);
        if (this.numBlocks > 0) {
            size += (this.numBlocks - 1) * this.bytesUsedPerBlock;
            size += RamUsageEstimator.sizeOf(this.blocks[this.numBlocks - 1]);
        }
        if (this.currentBlock != null) {
            size += RamUsageEstimator.sizeOf(this.currentBlock);
        }
        return size;
    }
    
    @Override
    public Collection<Accountable> getChildResources() {
        return (Collection<Accountable>)Collections.emptyList();
    }
    
    public long copyUsingLengthPrefix(final BytesRef bytes) {
        if (bytes.length >= 32768) {
            throw new IllegalArgumentException("max length is 32767 (got " + bytes.length + ")");
        }
        if (this.upto + bytes.length + 2 > this.blockSize) {
            if (bytes.length + 2 > this.blockSize) {
                throw new IllegalArgumentException("block size " + this.blockSize + " is too small to store length " + bytes.length + " bytes");
            }
            if (this.currentBlock != null) {
                this.addBlock(this.currentBlock);
            }
            this.currentBlock = new byte[this.blockSize];
            this.upto = 0;
        }
        final long pointer = this.getPointer();
        if (bytes.length < 128) {
            this.currentBlock[this.upto++] = (byte)bytes.length;
        }
        else {
            this.currentBlock[this.upto++] = (byte)(0x80 | bytes.length >> 8);
            this.currentBlock[this.upto++] = (byte)(bytes.length & 0xFF);
        }
        System.arraycopy(bytes.bytes, bytes.offset, this.currentBlock, this.upto, bytes.length);
        this.upto += bytes.length;
        return pointer;
    }
    
    public PagedBytesDataInput getDataInput() {
        if (!this.frozen) {
            throw new IllegalStateException("must call freeze() before getDataInput");
        }
        return new PagedBytesDataInput();
    }
    
    public PagedBytesDataOutput getDataOutput() {
        if (this.frozen) {
            throw new IllegalStateException("cannot get DataOutput after freeze()");
        }
        return new PagedBytesDataOutput();
    }
    
    static {
        BASE_RAM_BYTES_USED = RamUsageEstimator.shallowSizeOfInstance(PagedBytes.class);
        EMPTY_BYTES = new byte[0];
    }
    
    public static final class Reader implements Accountable
    {
        private static final long BASE_RAM_BYTES_USED;
        private final byte[][] blocks;
        private final int blockBits;
        private final int blockMask;
        private final int blockSize;
        private final long bytesUsedPerBlock;
        
        private Reader(final PagedBytes pagedBytes) {
            this.blocks = Arrays.copyOf(pagedBytes.blocks, pagedBytes.numBlocks);
            this.blockBits = pagedBytes.blockBits;
            this.blockMask = pagedBytes.blockMask;
            this.blockSize = pagedBytes.blockSize;
            this.bytesUsedPerBlock = pagedBytes.bytesUsedPerBlock;
        }
        
        public void fillSlice(final BytesRef b, final long start, final int length) {
            assert length >= 0 : "length=" + length;
            assert length <= this.blockSize + 1 : "length=" + length;
            if ((b.length = length) == 0) {
                return;
            }
            final int index = (int)(start >> this.blockBits);
            final int offset = (int)(start & (long)this.blockMask);
            if (this.blockSize - offset >= length) {
                b.bytes = this.blocks[index];
                b.offset = offset;
            }
            else {
                b.bytes = new byte[length];
                b.offset = 0;
                System.arraycopy(this.blocks[index], offset, b.bytes, 0, this.blockSize - offset);
                System.arraycopy(this.blocks[1 + index], 0, b.bytes, this.blockSize - offset, length - (this.blockSize - offset));
            }
        }
        
        public void fill(final BytesRef b, final long start) {
            final int index = (int)(start >> this.blockBits);
            final int offset = (int)(start & (long)this.blockMask);
            final byte[] bytes = this.blocks[index];
            b.bytes = bytes;
            final byte[] block = bytes;
            if ((block[offset] & 0x80) == 0x0) {
                b.length = block[offset];
                b.offset = offset + 1;
            }
            else {
                b.length = ((block[offset] & 0x7F) << 8 | (block[1 + offset] & 0xFF));
                b.offset = offset + 2;
                assert b.length > 0;
            }
        }
        
        @Override
        public long ramBytesUsed() {
            long size = Reader.BASE_RAM_BYTES_USED + RamUsageEstimator.shallowSizeOf(this.blocks);
            if (this.blocks.length > 0) {
                size += (this.blocks.length - 1) * this.bytesUsedPerBlock;
                size += RamUsageEstimator.sizeOf(this.blocks[this.blocks.length - 1]);
            }
            return size;
        }
        
        @Override
        public Collection<Accountable> getChildResources() {
            return (Collection<Accountable>)Collections.emptyList();
        }
        
        @Override
        public String toString() {
            return "PagedBytes(blocksize=" + this.blockSize + ")";
        }
        
        static {
            BASE_RAM_BYTES_USED = RamUsageEstimator.shallowSizeOfInstance(Reader.class);
        }
    }
    
    public final class PagedBytesDataInput extends DataInput
    {
        private int currentBlockIndex;
        private int currentBlockUpto;
        private byte[] currentBlock;
        
        PagedBytesDataInput() {
            this.currentBlock = PagedBytes.this.blocks[0];
        }
        
        @Override
        public PagedBytesDataInput clone() {
            final PagedBytesDataInput clone = PagedBytes.this.getDataInput();
            clone.setPosition(this.getPosition());
            return clone;
        }
        
        public long getPosition() {
            return this.currentBlockIndex * (long)PagedBytes.this.blockSize + this.currentBlockUpto;
        }
        
        public void setPosition(final long pos) {
            this.currentBlockIndex = (int)(pos >> PagedBytes.this.blockBits);
            this.currentBlock = PagedBytes.this.blocks[this.currentBlockIndex];
            this.currentBlockUpto = (int)(pos & (long)PagedBytes.this.blockMask);
        }
        
        @Override
        public byte readByte() {
            if (this.currentBlockUpto == PagedBytes.this.blockSize) {
                this.nextBlock();
            }
            return this.currentBlock[this.currentBlockUpto++];
        }
        
        @Override
        public void readBytes(final byte[] b, int offset, final int len) {
            assert b.length >= offset + len;
            final int offsetEnd = offset + len;
            int left;
            while (true) {
                final int blockLeft = PagedBytes.this.blockSize - this.currentBlockUpto;
                left = offsetEnd - offset;
                if (blockLeft >= left) {
                    break;
                }
                System.arraycopy(this.currentBlock, this.currentBlockUpto, b, offset, blockLeft);
                this.nextBlock();
                offset += blockLeft;
            }
            System.arraycopy(this.currentBlock, this.currentBlockUpto, b, offset, left);
            this.currentBlockUpto += left;
        }
        
        private void nextBlock() {
            ++this.currentBlockIndex;
            this.currentBlockUpto = 0;
            this.currentBlock = PagedBytes.this.blocks[this.currentBlockIndex];
        }
    }
    
    public final class PagedBytesDataOutput extends DataOutput
    {
        @Override
        public void writeByte(final byte b) {
            if (PagedBytes.this.upto == PagedBytes.this.blockSize) {
                if (PagedBytes.this.currentBlock != null) {
                    PagedBytes.this.addBlock(PagedBytes.this.currentBlock);
                }
                PagedBytes.this.currentBlock = new byte[PagedBytes.this.blockSize];
                PagedBytes.this.upto = 0;
            }
            PagedBytes.this.currentBlock[PagedBytes.this.upto++] = b;
        }
        
        @Override
        public void writeBytes(final byte[] b, int offset, final int length) {
            assert b.length >= offset + length;
            if (length == 0) {
                return;
            }
            if (PagedBytes.this.upto == PagedBytes.this.blockSize) {
                if (PagedBytes.this.currentBlock != null) {
                    PagedBytes.this.addBlock(PagedBytes.this.currentBlock);
                }
                PagedBytes.this.currentBlock = new byte[PagedBytes.this.blockSize];
                PagedBytes.this.upto = 0;
            }
            final int offsetEnd = offset + length;
            int left;
            while (true) {
                left = offsetEnd - offset;
                final int blockLeft = PagedBytes.this.blockSize - PagedBytes.this.upto;
                if (blockLeft >= left) {
                    break;
                }
                System.arraycopy(b, offset, PagedBytes.this.currentBlock, PagedBytes.this.upto, blockLeft);
                PagedBytes.this.addBlock(PagedBytes.this.currentBlock);
                PagedBytes.this.currentBlock = new byte[PagedBytes.this.blockSize];
                PagedBytes.this.upto = 0;
                offset += blockLeft;
            }
            System.arraycopy(b, offset, PagedBytes.this.currentBlock, PagedBytes.this.upto, left);
            PagedBytes.this.upto += left;
        }
        
        public long getPosition() {
            return PagedBytes.this.getPointer();
        }
    }
}
