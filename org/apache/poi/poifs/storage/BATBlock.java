package org.apache.poi.poifs.storage;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import org.apache.poi.util.LittleEndian;
import java.nio.ByteBuffer;
import java.util.Arrays;
import org.apache.poi.poifs.common.POIFSBigBlockSize;

public final class BATBlock implements BlockWritable
{
    private POIFSBigBlockSize bigBlockSize;
    private int[] _values;
    private boolean _has_free_sectors;
    private int ourBlockIndex;
    
    private BATBlock(final POIFSBigBlockSize bigBlockSize) {
        this.bigBlockSize = bigBlockSize;
        final int _entries_per_block = bigBlockSize.getBATEntriesPerBlock();
        this._values = new int[_entries_per_block];
        this._has_free_sectors = true;
        Arrays.fill(this._values, -1);
    }
    
    private void recomputeFree() {
        boolean hasFree = false;
        for (final int _value : this._values) {
            if (_value == -1) {
                hasFree = true;
                break;
            }
        }
        this._has_free_sectors = hasFree;
    }
    
    public static BATBlock createBATBlock(final POIFSBigBlockSize bigBlockSize, final ByteBuffer data) {
        final BATBlock block = new BATBlock(bigBlockSize);
        final byte[] buffer = new byte[4];
        for (int i = 0; i < block._values.length; ++i) {
            data.get(buffer);
            block._values[i] = LittleEndian.getInt(buffer);
        }
        block.recomputeFree();
        return block;
    }
    
    public static BATBlock createEmptyBATBlock(final POIFSBigBlockSize bigBlockSize, final boolean isXBAT) {
        final BATBlock block = new BATBlock(bigBlockSize);
        if (isXBAT) {
            final int _entries_per_xbat_block = bigBlockSize.getXBATEntriesPerBlock();
            block._values[_entries_per_xbat_block] = -2;
        }
        return block;
    }
    
    public static long calculateMaximumSize(final POIFSBigBlockSize bigBlockSize, final int numBATs) {
        long size = 1L;
        size += numBATs * (long)bigBlockSize.getBATEntriesPerBlock();
        return size * bigBlockSize.getBigBlockSize();
    }
    
    public static long calculateMaximumSize(final HeaderBlock header) {
        return calculateMaximumSize(header.getBigBlockSize(), header.getBATCount());
    }
    
    public static BATBlockAndIndex getBATBlockAndIndex(final int offset, final HeaderBlock header, final List<BATBlock> bats) {
        final POIFSBigBlockSize bigBlockSize = header.getBigBlockSize();
        final int entriesPerBlock = bigBlockSize.getBATEntriesPerBlock();
        final int whichBAT = offset / entriesPerBlock;
        final int index = offset % entriesPerBlock;
        return new BATBlockAndIndex(index, (BATBlock)bats.get(whichBAT));
    }
    
    public static BATBlockAndIndex getSBATBlockAndIndex(final int offset, final HeaderBlock header, final List<BATBlock> sbats) {
        return getBATBlockAndIndex(offset, header, sbats);
    }
    
    public boolean hasFreeSectors() {
        return this._has_free_sectors;
    }
    
    public int getUsedSectors(final boolean isAnXBAT) {
        int usedSectors = 0;
        int toCheck = this._values.length;
        if (isAnXBAT) {
            --toCheck;
        }
        for (int k = 0; k < toCheck; ++k) {
            if (this._values[k] != -1) {
                ++usedSectors;
            }
        }
        return usedSectors;
    }
    
    public int getValueAt(final int relativeOffset) {
        if (relativeOffset >= this._values.length) {
            throw new ArrayIndexOutOfBoundsException("Unable to fetch offset " + relativeOffset + " as the BAT only contains " + this._values.length + " entries");
        }
        return this._values[relativeOffset];
    }
    
    public void setValueAt(final int relativeOffset, final int value) {
        final int oldValue = this._values[relativeOffset];
        this._values[relativeOffset] = value;
        if (value == -1) {
            this._has_free_sectors = true;
            return;
        }
        if (oldValue == -1) {
            this.recomputeFree();
        }
    }
    
    public void setOurBlockIndex(final int index) {
        this.ourBlockIndex = index;
    }
    
    public int getOurBlockIndex() {
        return this.ourBlockIndex;
    }
    
    @Override
    public void writeBlocks(final OutputStream stream) throws IOException {
        stream.write(this.serialize());
    }
    
    public void writeData(final ByteBuffer block) {
        block.put(this.serialize());
    }
    
    private byte[] serialize() {
        final byte[] data = new byte[this.bigBlockSize.getBigBlockSize()];
        int offset = 0;
        for (final int _value : this._values) {
            LittleEndian.putInt(data, offset, _value);
            offset += 4;
        }
        return data;
    }
    
    public static final class BATBlockAndIndex
    {
        private final int index;
        private final BATBlock block;
        
        private BATBlockAndIndex(final int index, final BATBlock block) {
            this.index = index;
            this.block = block;
        }
        
        public int getIndex() {
            return this.index;
        }
        
        public BATBlock getBlock() {
            return this.block;
        }
    }
}
