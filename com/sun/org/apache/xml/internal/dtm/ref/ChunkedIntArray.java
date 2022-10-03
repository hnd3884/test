package com.sun.org.apache.xml.internal.dtm.ref;

import java.io.PrintStream;
import com.sun.org.apache.xml.internal.res.XMLMessages;

final class ChunkedIntArray
{
    final int slotsize = 4;
    static final int lowbits = 10;
    static final int chunkalloc = 1024;
    static final int lowmask = 1023;
    ChunksVector chunks;
    final int[] fastArray;
    int lastUsed;
    
    ChunkedIntArray(final int slotsize) {
        this.chunks = new ChunksVector();
        this.fastArray = new int[1024];
        this.lastUsed = 0;
        this.getClass();
        if (4 < slotsize) {
            throw new ArrayIndexOutOfBoundsException(XMLMessages.createXMLMessage("ER_CHUNKEDINTARRAY_NOT_SUPPORTED", new Object[] { Integer.toString(slotsize) }));
        }
        this.getClass();
        if (4 > slotsize) {
            final PrintStream out = System.out;
            final StringBuilder append = new StringBuilder().append("*****WARNING: ChunkedIntArray(").append(slotsize).append(") wasting ");
            this.getClass();
            out.println(append.append(4 - slotsize).append(" words per slot").toString());
        }
        this.chunks.addElement(this.fastArray);
    }
    
    int appendSlot(final int w0, final int w1, final int w2, final int w3) {
        final int slotsize = 4;
        final int newoffset = (this.lastUsed + 1) * 4;
        final int chunkpos = newoffset >> 10;
        final int slotpos = newoffset & 0x3FF;
        if (chunkpos > this.chunks.size() - 1) {
            this.chunks.addElement(new int[1024]);
        }
        final int[] chunk = this.chunks.elementAt(chunkpos);
        chunk[slotpos] = w0;
        chunk[slotpos + 1] = w1;
        chunk[slotpos + 2] = w2;
        chunk[slotpos + 3] = w3;
        return ++this.lastUsed;
    }
    
    int readEntry(int position, final int offset) throws ArrayIndexOutOfBoundsException {
        if (offset >= 4) {
            throw new ArrayIndexOutOfBoundsException(XMLMessages.createXMLMessage("ER_OFFSET_BIGGER_THAN_SLOT", null));
        }
        position *= 4;
        final int chunkpos = position >> 10;
        final int slotpos = position & 0x3FF;
        final int[] chunk = this.chunks.elementAt(chunkpos);
        return chunk[slotpos + offset];
    }
    
    int specialFind(final int startPos, final int position) {
        int ancestor = startPos;
        while (ancestor > 0) {
            ancestor *= 4;
            final int chunkpos = ancestor >> 10;
            final int slotpos = ancestor & 0x3FF;
            final int[] chunk = this.chunks.elementAt(chunkpos);
            ancestor = chunk[slotpos + 1];
            if (ancestor == position) {
                break;
            }
        }
        if (ancestor <= 0) {
            return position;
        }
        return -1;
    }
    
    int slotsUsed() {
        return this.lastUsed;
    }
    
    void discardLast() {
        --this.lastUsed;
    }
    
    void writeEntry(int position, final int offset, final int value) throws ArrayIndexOutOfBoundsException {
        if (offset >= 4) {
            throw new ArrayIndexOutOfBoundsException(XMLMessages.createXMLMessage("ER_OFFSET_BIGGER_THAN_SLOT", null));
        }
        position *= 4;
        final int chunkpos = position >> 10;
        final int slotpos = position & 0x3FF;
        final int[] chunk = this.chunks.elementAt(chunkpos);
        chunk[slotpos + offset] = value;
    }
    
    void writeSlot(int position, final int w0, final int w1, final int w2, final int w3) {
        position *= 4;
        final int chunkpos = position >> 10;
        final int slotpos = position & 0x3FF;
        if (chunkpos > this.chunks.size() - 1) {
            this.chunks.addElement(new int[1024]);
        }
        final int[] chunk = this.chunks.elementAt(chunkpos);
        chunk[slotpos] = w0;
        chunk[slotpos + 1] = w1;
        chunk[slotpos + 2] = w2;
        chunk[slotpos + 3] = w3;
    }
    
    void readSlot(int position, final int[] buffer) {
        position *= 4;
        final int chunkpos = position >> 10;
        final int slotpos = position & 0x3FF;
        if (chunkpos > this.chunks.size() - 1) {
            this.chunks.addElement(new int[1024]);
        }
        final int[] chunk = this.chunks.elementAt(chunkpos);
        System.arraycopy(chunk, slotpos, buffer, 0, 4);
    }
    
    class ChunksVector
    {
        final int BLOCKSIZE = 64;
        int[][] m_map;
        int m_mapSize;
        int pos;
        
        ChunksVector() {
            this.m_map = new int[64][];
            this.m_mapSize = 64;
            this.pos = 0;
        }
        
        final int size() {
            return this.pos;
        }
        
        void addElement(final int[] value) {
            if (this.pos >= this.m_mapSize) {
                final int orgMapSize = this.m_mapSize;
                while (this.pos >= this.m_mapSize) {
                    this.m_mapSize += 64;
                }
                final int[][] newMap = new int[this.m_mapSize][];
                System.arraycopy(this.m_map, 0, newMap, 0, orgMapSize);
                this.m_map = newMap;
            }
            this.m_map[this.pos] = value;
            ++this.pos;
        }
        
        final int[] elementAt(final int pos) {
            return this.m_map[pos];
        }
    }
}
