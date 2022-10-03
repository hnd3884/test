package org.apache.lucene.util.fst;

import java.util.Collections;
import java.util.Collection;
import org.apache.lucene.util.RamUsageEstimator;
import java.util.Iterator;
import java.io.IOException;
import org.apache.lucene.store.DataInput;
import java.util.ArrayList;
import java.util.List;
import org.apache.lucene.util.Accountable;
import org.apache.lucene.store.DataOutput;

class BytesStore extends DataOutput implements Accountable
{
    private static final long BASE_RAM_BYTES_USED;
    private final List<byte[]> blocks;
    private final int blockSize;
    private final int blockBits;
    private final int blockMask;
    private byte[] current;
    private int nextWrite;
    
    public BytesStore(final int blockBits) {
        this.blocks = new ArrayList<byte[]>();
        this.blockBits = blockBits;
        this.blockSize = 1 << blockBits;
        this.blockMask = this.blockSize - 1;
        this.nextWrite = this.blockSize;
    }
    
    public BytesStore(final DataInput in, final long numBytes, final int maxBlockSize) throws IOException {
        this.blocks = new ArrayList<byte[]>();
        int blockSize;
        int blockBits;
        for (blockSize = 2, blockBits = 1; blockSize < numBytes && blockSize < maxBlockSize; blockSize *= 2, ++blockBits) {}
        this.blockBits = blockBits;
        this.blockSize = blockSize;
        this.blockMask = blockSize - 1;
        int chunk;
        for (long left = numBytes; left > 0L; left -= chunk) {
            chunk = (int)Math.min(blockSize, left);
            final byte[] block = new byte[chunk];
            in.readBytes(block, 0, block.length);
            this.blocks.add(block);
        }
        this.nextWrite = this.blocks.get(this.blocks.size() - 1).length;
    }
    
    public void writeByte(final int dest, final byte b) {
        final int blockIndex = dest >> this.blockBits;
        final byte[] block = this.blocks.get(blockIndex);
        block[dest & this.blockMask] = b;
    }
    
    @Override
    public void writeByte(final byte b) {
        if (this.nextWrite == this.blockSize) {
            this.current = new byte[this.blockSize];
            this.blocks.add(this.current);
            this.nextWrite = 0;
        }
        this.current[this.nextWrite++] = b;
    }
    
    @Override
    public void writeBytes(final byte[] b, int offset, int len) {
        while (len > 0) {
            final int chunk = this.blockSize - this.nextWrite;
            if (len <= chunk) {
                assert b != null;
                assert this.current != null;
                System.arraycopy(b, offset, this.current, this.nextWrite, len);
                this.nextWrite += len;
                break;
            }
            else {
                if (chunk > 0) {
                    System.arraycopy(b, offset, this.current, this.nextWrite, chunk);
                    offset += chunk;
                    len -= chunk;
                }
                this.current = new byte[this.blockSize];
                this.blocks.add(this.current);
                this.nextWrite = 0;
            }
        }
    }
    
    int getBlockBits() {
        return this.blockBits;
    }
    
    void writeBytes(final long dest, final byte[] b, final int offset, int len) {
        assert dest + len <= this.getPosition() : "dest=" + dest + " pos=" + this.getPosition() + " len=" + len;
        final long end = dest + len;
        int blockIndex = (int)(end >> this.blockBits);
        int downTo = (int)(end & (long)this.blockMask);
        if (downTo == 0) {
            --blockIndex;
            downTo = this.blockSize;
        }
        byte[] block = this.blocks.get(blockIndex);
        while (len > 0) {
            if (len <= downTo) {
                System.arraycopy(b, offset, block, downTo - len, len);
                break;
            }
            len -= downTo;
            System.arraycopy(b, offset + len, block, 0, downTo);
            --blockIndex;
            block = this.blocks.get(blockIndex);
            downTo = this.blockSize;
        }
    }
    
    public void copyBytes(final long src, final long dest, int len) {
        assert src < dest;
        final long end = src + len;
        int blockIndex = (int)(end >> this.blockBits);
        int downTo = (int)(end & (long)this.blockMask);
        if (downTo == 0) {
            --blockIndex;
            downTo = this.blockSize;
        }
        byte[] block = this.blocks.get(blockIndex);
        while (len > 0) {
            if (len <= downTo) {
                this.writeBytes(dest, block, downTo - len, len);
                break;
            }
            len -= downTo;
            this.writeBytes(dest + len, block, 0, downTo);
            --blockIndex;
            block = this.blocks.get(blockIndex);
            downTo = this.blockSize;
        }
    }
    
    public void writeInt(final long pos, final int value) {
        int blockIndex = (int)(pos >> this.blockBits);
        int upto = (int)(pos & (long)this.blockMask);
        byte[] block = this.blocks.get(blockIndex);
        int shift = 24;
        for (int i = 0; i < 4; ++i) {
            block[upto++] = (byte)(value >> shift);
            shift -= 8;
            if (upto == this.blockSize) {
                upto = 0;
                ++blockIndex;
                block = this.blocks.get(blockIndex);
            }
        }
    }
    
    public void reverse(final long srcPos, final long destPos) {
        assert srcPos < destPos;
        assert destPos < this.getPosition();
        int srcBlockIndex = (int)(srcPos >> this.blockBits);
        int src = (int)(srcPos & (long)this.blockMask);
        byte[] srcBlock = this.blocks.get(srcBlockIndex);
        int destBlockIndex = (int)(destPos >> this.blockBits);
        int dest = (int)(destPos & (long)this.blockMask);
        byte[] destBlock = this.blocks.get(destBlockIndex);
        for (int limit = (int)(destPos - srcPos + 1L) / 2, i = 0; i < limit; ++i) {
            final byte b = srcBlock[src];
            srcBlock[src] = destBlock[dest];
            destBlock[dest] = b;
            if (++src == this.blockSize) {
                ++srcBlockIndex;
                srcBlock = this.blocks.get(srcBlockIndex);
                src = 0;
            }
            if (--dest == -1) {
                --destBlockIndex;
                destBlock = this.blocks.get(destBlockIndex);
                dest = this.blockSize - 1;
            }
        }
    }
    
    public void skipBytes(int len) {
        while (len > 0) {
            final int chunk = this.blockSize - this.nextWrite;
            if (len <= chunk) {
                this.nextWrite += len;
                break;
            }
            len -= chunk;
            this.current = new byte[this.blockSize];
            this.blocks.add(this.current);
            this.nextWrite = 0;
        }
    }
    
    public long getPosition() {
        return (this.blocks.size() - 1L) * this.blockSize + this.nextWrite;
    }
    
    public void truncate(final long newLen) {
        assert newLen <= this.getPosition();
        assert newLen >= 0L;
        int blockIndex = (int)(newLen >> this.blockBits);
        this.nextWrite = (int)(newLen & (long)this.blockMask);
        if (this.nextWrite == 0) {
            --blockIndex;
            this.nextWrite = this.blockSize;
        }
        this.blocks.subList(blockIndex + 1, this.blocks.size()).clear();
        if (newLen == 0L) {
            this.current = null;
        }
        else {
            this.current = this.blocks.get(blockIndex);
        }
        assert newLen == this.getPosition();
    }
    
    public void finish() {
        if (this.current != null) {
            final byte[] lastBuffer = new byte[this.nextWrite];
            System.arraycopy(this.current, 0, lastBuffer, 0, this.nextWrite);
            this.blocks.set(this.blocks.size() - 1, lastBuffer);
            this.current = null;
        }
    }
    
    public void writeTo(final DataOutput out) throws IOException {
        for (final byte[] block : this.blocks) {
            out.writeBytes(block, 0, block.length);
        }
    }
    
    public FST.BytesReader getForwardReader() {
        if (this.blocks.size() == 1) {
            return new ForwardBytesReader(this.blocks.get(0));
        }
        return new FST.BytesReader() {
            private byte[] current;
            private int nextBuffer;
            private int nextRead = BytesStore.this.blockSize;
            
            @Override
            public byte readByte() {
                if (this.nextRead == BytesStore.this.blockSize) {
                    this.current = BytesStore.this.blocks.get(this.nextBuffer++);
                    this.nextRead = 0;
                }
                return this.current[this.nextRead++];
            }
            
            @Override
            public void skipBytes(final long count) {
                this.setPosition(this.getPosition() + count);
            }
            
            @Override
            public void readBytes(final byte[] b, int offset, int len) {
                while (len > 0) {
                    final int chunkLeft = BytesStore.this.blockSize - this.nextRead;
                    if (len <= chunkLeft) {
                        System.arraycopy(this.current, this.nextRead, b, offset, len);
                        this.nextRead += len;
                        break;
                    }
                    if (chunkLeft > 0) {
                        System.arraycopy(this.current, this.nextRead, b, offset, chunkLeft);
                        offset += chunkLeft;
                        len -= chunkLeft;
                    }
                    this.current = BytesStore.this.blocks.get(this.nextBuffer++);
                    this.nextRead = 0;
                }
            }
            
            @Override
            public long getPosition() {
                return (this.nextBuffer - 1L) * BytesStore.this.blockSize + this.nextRead;
            }
            
            @Override
            public void setPosition(final long pos) {
                final int bufferIndex = (int)(pos >> BytesStore.this.blockBits);
                this.nextBuffer = bufferIndex + 1;
                this.current = BytesStore.this.blocks.get(bufferIndex);
                this.nextRead = (int)(pos & (long)BytesStore.this.blockMask);
                assert this.getPosition() == pos;
            }
            
            @Override
            public boolean reversed() {
                return false;
            }
        };
    }
    
    public FST.BytesReader getReverseReader() {
        return this.getReverseReader(true);
    }
    
    FST.BytesReader getReverseReader(final boolean allowSingle) {
        if (allowSingle && this.blocks.size() == 1) {
            return new ReverseBytesReader(this.blocks.get(0));
        }
        return new FST.BytesReader() {
            private byte[] current = (BytesStore.this.blocks.size() == 0) ? null : ((byte[])BytesStore.this.blocks.get(0));
            private int nextBuffer = -1;
            private int nextRead = 0;
            
            @Override
            public byte readByte() {
                if (this.nextRead == -1) {
                    this.current = BytesStore.this.blocks.get(this.nextBuffer--);
                    this.nextRead = BytesStore.this.blockSize - 1;
                }
                return this.current[this.nextRead--];
            }
            
            @Override
            public void skipBytes(final long count) {
                this.setPosition(this.getPosition() - count);
            }
            
            @Override
            public void readBytes(final byte[] b, final int offset, final int len) {
                for (int i = 0; i < len; ++i) {
                    b[offset + i] = this.readByte();
                }
            }
            
            @Override
            public long getPosition() {
                return (this.nextBuffer + 1L) * BytesStore.this.blockSize + this.nextRead;
            }
            
            @Override
            public void setPosition(final long pos) {
                final int bufferIndex = (int)(pos >> BytesStore.this.blockBits);
                this.nextBuffer = bufferIndex - 1;
                this.current = BytesStore.this.blocks.get(bufferIndex);
                this.nextRead = (int)(pos & (long)BytesStore.this.blockMask);
                assert this.getPosition() == pos : "pos=" + pos + " getPos()=" + this.getPosition();
            }
            
            @Override
            public boolean reversed() {
                return true;
            }
        };
    }
    
    @Override
    public long ramBytesUsed() {
        long size = BytesStore.BASE_RAM_BYTES_USED;
        for (final byte[] block : this.blocks) {
            size += RamUsageEstimator.sizeOf(block);
        }
        return size;
    }
    
    @Override
    public Collection<Accountable> getChildResources() {
        return (Collection<Accountable>)Collections.emptyList();
    }
    
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "(numBlocks=" + this.blocks.size() + ")";
    }
    
    static {
        BASE_RAM_BYTES_USED = RamUsageEstimator.shallowSizeOfInstance(BytesStore.class) + RamUsageEstimator.shallowSizeOfInstance(ArrayList.class);
    }
}
