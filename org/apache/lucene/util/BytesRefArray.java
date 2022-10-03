package org.apache.lucene.util;

import java.util.Comparator;
import java.util.Arrays;

public final class BytesRefArray
{
    private final ByteBlockPool pool;
    private int[] offsets;
    private int lastElement;
    private int currentOffset;
    private final Counter bytesUsed;
    
    public BytesRefArray(final Counter bytesUsed) {
        this.offsets = new int[1];
        this.lastElement = 0;
        this.currentOffset = 0;
        (this.pool = new ByteBlockPool(new ByteBlockPool.DirectTrackingAllocator(bytesUsed))).nextBuffer();
        bytesUsed.addAndGet(RamUsageEstimator.NUM_BYTES_ARRAY_HEADER + 4);
        this.bytesUsed = bytesUsed;
    }
    
    public void clear() {
        this.lastElement = 0;
        this.currentOffset = 0;
        Arrays.fill(this.offsets, 0);
        this.pool.reset(false, true);
    }
    
    public int append(final BytesRef bytes) {
        if (this.lastElement >= this.offsets.length) {
            final int oldLen = this.offsets.length;
            this.offsets = ArrayUtil.grow(this.offsets, this.offsets.length + 1);
            this.bytesUsed.addAndGet((this.offsets.length - oldLen) * 4);
        }
        this.pool.append(bytes);
        this.offsets[this.lastElement++] = this.currentOffset;
        this.currentOffset += bytes.length;
        return this.lastElement - 1;
    }
    
    public int size() {
        return this.lastElement;
    }
    
    public BytesRef get(final BytesRefBuilder spare, final int index) {
        if (this.lastElement > index) {
            final int offset = this.offsets[index];
            final int length = (index == this.lastElement - 1) ? (this.currentOffset - offset) : (this.offsets[index + 1] - offset);
            spare.grow(length);
            spare.setLength(length);
            this.pool.readBytes(offset, spare.bytes(), 0, spare.length());
            return spare.get();
        }
        throw new IndexOutOfBoundsException("index " + index + " must be less than the size: " + this.lastElement);
    }
    
    private int[] sort(final Comparator<BytesRef> comp) {
        final int[] orderedEntries = new int[this.size()];
        for (int i = 0; i < orderedEntries.length; ++i) {
            orderedEntries[i] = i;
        }
        new IntroSorter() {
            private BytesRef pivot;
            private final BytesRefBuilder pivotBuilder = new BytesRefBuilder();
            private final BytesRefBuilder scratch1 = new BytesRefBuilder();
            private final BytesRefBuilder scratch2 = new BytesRefBuilder();
            
            @Override
            protected void swap(final int i, final int j) {
                final int o = orderedEntries[i];
                orderedEntries[i] = orderedEntries[j];
                orderedEntries[j] = o;
            }
            
            @Override
            protected int compare(final int i, final int j) {
                final int idx1 = orderedEntries[i];
                final int idx2 = orderedEntries[j];
                return comp.compare(BytesRefArray.this.get(this.scratch1, idx1), BytesRefArray.this.get(this.scratch2, idx2));
            }
            
            @Override
            protected void setPivot(final int i) {
                final int index = orderedEntries[i];
                this.pivot = BytesRefArray.this.get(this.pivotBuilder, index);
            }
            
            @Override
            protected int comparePivot(final int j) {
                final int index = orderedEntries[j];
                return comp.compare(this.pivot, BytesRefArray.this.get(this.scratch2, index));
            }
        }.sort(0, this.size());
        return orderedEntries;
    }
    
    public BytesRefIterator iterator() {
        return this.iterator(null);
    }
    
    public BytesRefIterator iterator(final Comparator<BytesRef> comp) {
        final BytesRefBuilder spare = new BytesRefBuilder();
        final int size = this.size();
        final int[] indices = (int[])((comp == null) ? null : this.sort(comp));
        return new BytesRefIterator() {
            int pos = 0;
            
            @Override
            public BytesRef next() {
                if (this.pos < size) {
                    final BytesRefArray this$0 = BytesRefArray.this;
                    final BytesRefBuilder val$spare = spare;
                    int pos;
                    if (indices == null) {
                        this.pos = (pos = this.pos) + 1;
                    }
                    else {
                        pos = indices[this.pos++];
                    }
                    return this$0.get(val$spare, pos);
                }
                return null;
            }
        };
    }
}
