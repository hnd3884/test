package org.apache.lucene.util;

import java.util.Comparator;
import java.util.Arrays;

public final class BytesRefHash
{
    public static final int DEFAULT_CAPACITY = 16;
    final ByteBlockPool pool;
    int[] bytesStart;
    private final BytesRef scratch1;
    private int hashSize;
    private int hashHalfSize;
    private int hashMask;
    private int count;
    private int lastCount;
    private int[] ids;
    private final BytesStartArray bytesStartArray;
    private Counter bytesUsed;
    
    public BytesRefHash() {
        this(new ByteBlockPool(new ByteBlockPool.DirectAllocator()));
    }
    
    public BytesRefHash(final ByteBlockPool pool) {
        this(pool, 16, new DirectBytesStartArray(16));
    }
    
    public BytesRefHash(final ByteBlockPool pool, final int capacity, final BytesStartArray bytesStartArray) {
        this.scratch1 = new BytesRef();
        this.lastCount = -1;
        this.hashSize = capacity;
        this.hashHalfSize = this.hashSize >> 1;
        this.hashMask = this.hashSize - 1;
        this.pool = pool;
        Arrays.fill(this.ids = new int[this.hashSize], -1);
        this.bytesStartArray = bytesStartArray;
        this.bytesStart = bytesStartArray.init();
        (this.bytesUsed = ((bytesStartArray.bytesUsed() == null) ? Counter.newCounter() : bytesStartArray.bytesUsed())).addAndGet(this.hashSize * 4);
    }
    
    public int size() {
        return this.count;
    }
    
    public BytesRef get(final int bytesID, final BytesRef ref) {
        assert this.bytesStart != null : "bytesStart is null - not initialized";
        assert bytesID < this.bytesStart.length : "bytesID exceeds byteStart len: " + this.bytesStart.length;
        this.pool.setBytesRef(ref, this.bytesStart[bytesID]);
        return ref;
    }
    
    int[] compact() {
        assert this.bytesStart != null : "bytesStart is null - not initialized";
        int upto = 0;
        for (int i = 0; i < this.hashSize; ++i) {
            if (this.ids[i] != -1) {
                if (upto < i) {
                    this.ids[upto] = this.ids[i];
                    this.ids[i] = -1;
                }
                ++upto;
            }
        }
        assert upto == this.count;
        this.lastCount = this.count;
        return this.ids;
    }
    
    public int[] sort(final Comparator<BytesRef> comp) {
        final int[] compact = this.compact();
        new IntroSorter() {
            private final BytesRef pivot = new BytesRef();
            private final BytesRef scratch1 = new BytesRef();
            private final BytesRef scratch2 = new BytesRef();
            
            @Override
            protected void swap(final int i, final int j) {
                final int o = compact[i];
                compact[i] = compact[j];
                compact[j] = o;
            }
            
            @Override
            protected int compare(final int i, final int j) {
                final int id1 = compact[i];
                final int id2 = compact[j];
                assert BytesRefHash.this.bytesStart.length > id1 && BytesRefHash.this.bytesStart.length > id2;
                BytesRefHash.this.pool.setBytesRef(this.scratch1, BytesRefHash.this.bytesStart[id1]);
                BytesRefHash.this.pool.setBytesRef(this.scratch2, BytesRefHash.this.bytesStart[id2]);
                return comp.compare(this.scratch1, this.scratch2);
            }
            
            @Override
            protected void setPivot(final int i) {
                final int id = compact[i];
                assert BytesRefHash.this.bytesStart.length > id;
                BytesRefHash.this.pool.setBytesRef(this.pivot, BytesRefHash.this.bytesStart[id]);
            }
            
            @Override
            protected int comparePivot(final int j) {
                final int id = compact[j];
                assert BytesRefHash.this.bytesStart.length > id;
                BytesRefHash.this.pool.setBytesRef(this.scratch2, BytesRefHash.this.bytesStart[id]);
                return comp.compare(this.pivot, this.scratch2);
            }
        }.sort(0, this.count);
        return compact;
    }
    
    private boolean equals(final int id, final BytesRef b) {
        this.pool.setBytesRef(this.scratch1, this.bytesStart[id]);
        return this.scratch1.bytesEquals(b);
    }
    
    private boolean shrink(final int targetSize) {
        int newSize;
        for (newSize = this.hashSize; newSize >= 8 && newSize / 4 > targetSize; newSize /= 2) {}
        if (newSize != this.hashSize) {
            this.bytesUsed.addAndGet(4 * -(this.hashSize - newSize));
            this.hashSize = newSize;
            Arrays.fill(this.ids = new int[this.hashSize], -1);
            this.hashHalfSize = newSize / 2;
            this.hashMask = newSize - 1;
            return true;
        }
        return false;
    }
    
    public void clear(final boolean resetPool) {
        this.lastCount = this.count;
        this.count = 0;
        if (resetPool) {
            this.pool.reset(false, false);
        }
        this.bytesStart = this.bytesStartArray.clear();
        if (this.lastCount != -1 && this.shrink(this.lastCount)) {
            return;
        }
        Arrays.fill(this.ids, -1);
    }
    
    public void clear() {
        this.clear(true);
    }
    
    public void close() {
        this.clear(true);
        this.ids = null;
        this.bytesUsed.addAndGet(4 * -this.hashSize);
    }
    
    public int add(final BytesRef bytes) {
        assert this.bytesStart != null : "Bytesstart is null - not initialized";
        final int length = bytes.length;
        final int hashPos = this.findHash(bytes);
        int e = this.ids[hashPos];
        if (e != -1) {
            return -(e + 1);
        }
        final int len2 = 2 + bytes.length;
        if (len2 + this.pool.byteUpto > 32768) {
            if (len2 > 32768) {
                throw new MaxBytesLengthExceededException("bytes can be at most 32766 in length; got " + bytes.length);
            }
            this.pool.nextBuffer();
        }
        final byte[] buffer = this.pool.buffer;
        final int bufferUpto = this.pool.byteUpto;
        if (this.count >= this.bytesStart.length) {
            this.bytesStart = this.bytesStartArray.grow();
            assert this.count < this.bytesStart.length + 1 : "count: " + this.count + " len: " + this.bytesStart.length;
        }
        e = this.count++;
        this.bytesStart[e] = bufferUpto + this.pool.byteOffset;
        if (length < 128) {
            buffer[bufferUpto] = (byte)length;
            final ByteBlockPool pool = this.pool;
            pool.byteUpto += length + 1;
            assert length >= 0 : "Length must be positive: " + length;
            System.arraycopy(bytes.bytes, bytes.offset, buffer, bufferUpto + 1, length);
        }
        else {
            buffer[bufferUpto] = (byte)(0x80 | (length & 0x7F));
            buffer[bufferUpto + 1] = (byte)(length >> 7 & 0xFF);
            final ByteBlockPool pool2 = this.pool;
            pool2.byteUpto += length + 2;
            System.arraycopy(bytes.bytes, bytes.offset, buffer, bufferUpto + 2, length);
        }
        assert this.ids[hashPos] == -1;
        this.ids[hashPos] = e;
        if (this.count == this.hashHalfSize) {
            this.rehash(2 * this.hashSize, true);
        }
        return e;
    }
    
    public int find(final BytesRef bytes) {
        return this.ids[this.findHash(bytes)];
    }
    
    private int findHash(final BytesRef bytes) {
        assert this.bytesStart != null : "bytesStart is null - not initialized";
        int code = this.doHash(bytes.bytes, bytes.offset, bytes.length);
        int hashPos = code & this.hashMask;
        int e = this.ids[hashPos];
        if (e != -1 && !this.equals(e, bytes)) {
            do {
                hashPos = (++code & this.hashMask);
                e = this.ids[hashPos];
            } while (e != -1 && !this.equals(e, bytes));
        }
        return hashPos;
    }
    
    public int addByPoolOffset(final int offset) {
        assert this.bytesStart != null : "Bytesstart is null - not initialized";
        int code = offset;
        int hashPos = offset & this.hashMask;
        int e = this.ids[hashPos];
        if (e != -1 && this.bytesStart[e] != offset) {
            do {
                hashPos = (++code & this.hashMask);
                e = this.ids[hashPos];
            } while (e != -1 && this.bytesStart[e] != offset);
        }
        if (e != -1) {
            return -(e + 1);
        }
        if (this.count >= this.bytesStart.length) {
            this.bytesStart = this.bytesStartArray.grow();
            assert this.count < this.bytesStart.length + 1 : "count: " + this.count + " len: " + this.bytesStart.length;
        }
        e = this.count++;
        this.bytesStart[e] = offset;
        assert this.ids[hashPos] == -1;
        this.ids[hashPos] = e;
        if (this.count == this.hashHalfSize) {
            this.rehash(2 * this.hashSize, false);
        }
        return e;
    }
    
    private void rehash(final int newSize, final boolean hashOnData) {
        final int newMask = newSize - 1;
        this.bytesUsed.addAndGet(4 * newSize);
        final int[] newHash = new int[newSize];
        Arrays.fill(newHash, -1);
        for (int i = 0; i < this.hashSize; ++i) {
            final int e0 = this.ids[i];
            if (e0 != -1) {
                int code;
                if (hashOnData) {
                    final int off = this.bytesStart[e0];
                    final int start = off & 0x7FFF;
                    final byte[] bytes = this.pool.buffers[off >> 15];
                    int len;
                    int pos;
                    if ((bytes[start] & 0x80) == 0x0) {
                        len = bytes[start];
                        pos = start + 1;
                    }
                    else {
                        len = (bytes[start] & 0x7F) + ((bytes[start + 1] & 0xFF) << 7);
                        pos = start + 2;
                    }
                    code = this.doHash(bytes, pos, len);
                }
                else {
                    code = this.bytesStart[e0];
                }
                int hashPos = code & newMask;
                assert hashPos >= 0;
                if (newHash[hashPos] != -1) {
                    do {
                        hashPos = (++code & newMask);
                    } while (newHash[hashPos] != -1);
                }
                newHash[hashPos] = e0;
            }
        }
        this.hashMask = newMask;
        this.bytesUsed.addAndGet(4 * -this.ids.length);
        this.ids = newHash;
        this.hashSize = newSize;
        this.hashHalfSize = newSize / 2;
    }
    
    private int doHash(final byte[] bytes, final int offset, final int length) {
        return StringHelper.murmurhash3_x86_32(bytes, offset, length, StringHelper.GOOD_FAST_HASH_SEED);
    }
    
    public void reinit() {
        if (this.bytesStart == null) {
            this.bytesStart = this.bytesStartArray.init();
        }
        if (this.ids == null) {
            this.ids = new int[this.hashSize];
            this.bytesUsed.addAndGet(4 * this.hashSize);
        }
    }
    
    public int byteStart(final int bytesID) {
        assert this.bytesStart != null : "bytesStart is null - not initialized";
        assert bytesID >= 0 && bytesID < this.count : bytesID;
        return this.bytesStart[bytesID];
    }
    
    public static class MaxBytesLengthExceededException extends RuntimeException
    {
        MaxBytesLengthExceededException(final String message) {
            super(message);
        }
    }
    
    public abstract static class BytesStartArray
    {
        public abstract int[] init();
        
        public abstract int[] grow();
        
        public abstract int[] clear();
        
        public abstract Counter bytesUsed();
    }
    
    public static class DirectBytesStartArray extends BytesStartArray
    {
        protected final int initSize;
        private int[] bytesStart;
        private final Counter bytesUsed;
        
        public DirectBytesStartArray(final int initSize, final Counter counter) {
            this.bytesUsed = counter;
            this.initSize = initSize;
        }
        
        public DirectBytesStartArray(final int initSize) {
            this(initSize, Counter.newCounter());
        }
        
        @Override
        public int[] clear() {
            return this.bytesStart = null;
        }
        
        @Override
        public int[] grow() {
            assert this.bytesStart != null;
            return this.bytesStart = ArrayUtil.grow(this.bytesStart, this.bytesStart.length + 1);
        }
        
        @Override
        public int[] init() {
            return this.bytesStart = new int[ArrayUtil.oversize(this.initSize, 4)];
        }
        
        @Override
        public Counter bytesUsed() {
            return this.bytesUsed;
        }
    }
}
