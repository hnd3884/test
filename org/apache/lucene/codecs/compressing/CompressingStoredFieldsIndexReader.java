package org.apache.lucene.codecs.compressing;

import java.util.List;
import java.util.Collections;
import org.apache.lucene.util.Accountables;
import java.util.ArrayList;
import java.util.Collection;
import org.apache.lucene.util.RamUsageEstimator;
import org.apache.lucene.util.BitUtil;
import java.io.IOException;
import org.apache.lucene.store.DataInput;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.util.ArrayUtil;
import java.util.Arrays;
import org.apache.lucene.index.SegmentInfo;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.util.packed.PackedInts;
import org.apache.lucene.util.Accountable;

public final class CompressingStoredFieldsIndexReader implements Cloneable, Accountable
{
    private static final long BASE_RAM_BYTES_USED;
    final int maxDoc;
    final int[] docBases;
    final long[] startPointers;
    final int[] avgChunkDocs;
    final long[] avgChunkSizes;
    final PackedInts.Reader[] docBasesDeltas;
    final PackedInts.Reader[] startPointersDeltas;
    
    CompressingStoredFieldsIndexReader(final IndexInput fieldsIndexIn, final SegmentInfo si) throws IOException {
        this.maxDoc = si.maxDoc();
        int[] docBases = new int[16];
        long[] startPointers = new long[16];
        int[] avgChunkDocs = new int[16];
        long[] avgChunkSizes = new long[16];
        PackedInts.Reader[] docBasesDeltas = new PackedInts.Reader[16];
        PackedInts.Reader[] startPointersDeltas = new PackedInts.Reader[16];
        final int packedIntsVersion = fieldsIndexIn.readVInt();
        int blockCount = 0;
        while (true) {
            final int numChunks = fieldsIndexIn.readVInt();
            if (numChunks == 0) {
                this.docBases = Arrays.copyOf(docBases, blockCount);
                this.startPointers = Arrays.copyOf(startPointers, blockCount);
                this.avgChunkDocs = Arrays.copyOf(avgChunkDocs, blockCount);
                this.avgChunkSizes = Arrays.copyOf(avgChunkSizes, blockCount);
                this.docBasesDeltas = Arrays.copyOf(docBasesDeltas, blockCount);
                this.startPointersDeltas = Arrays.copyOf(startPointersDeltas, blockCount);
                return;
            }
            if (blockCount == docBases.length) {
                final int newSize = ArrayUtil.oversize(blockCount + 1, 8);
                docBases = Arrays.copyOf(docBases, newSize);
                startPointers = Arrays.copyOf(startPointers, newSize);
                avgChunkDocs = Arrays.copyOf(avgChunkDocs, newSize);
                avgChunkSizes = Arrays.copyOf(avgChunkSizes, newSize);
                docBasesDeltas = Arrays.copyOf(docBasesDeltas, newSize);
                startPointersDeltas = Arrays.copyOf(startPointersDeltas, newSize);
            }
            docBases[blockCount] = fieldsIndexIn.readVInt();
            avgChunkDocs[blockCount] = fieldsIndexIn.readVInt();
            final int bitsPerDocBase = fieldsIndexIn.readVInt();
            if (bitsPerDocBase > 32) {
                throw new CorruptIndexException("Corrupted bitsPerDocBase: " + bitsPerDocBase, fieldsIndexIn);
            }
            docBasesDeltas[blockCount] = PackedInts.getReaderNoHeader(fieldsIndexIn, PackedInts.Format.PACKED, packedIntsVersion, numChunks, bitsPerDocBase);
            startPointers[blockCount] = fieldsIndexIn.readVLong();
            avgChunkSizes[blockCount] = fieldsIndexIn.readVLong();
            final int bitsPerStartPointer = fieldsIndexIn.readVInt();
            if (bitsPerStartPointer > 64) {
                throw new CorruptIndexException("Corrupted bitsPerStartPointer: " + bitsPerStartPointer, fieldsIndexIn);
            }
            startPointersDeltas[blockCount] = PackedInts.getReaderNoHeader(fieldsIndexIn, PackedInts.Format.PACKED, packedIntsVersion, numChunks, bitsPerStartPointer);
            ++blockCount;
        }
    }
    
    private int block(final int docID) {
        int lo = 0;
        int hi = this.docBases.length - 1;
        while (lo <= hi) {
            final int mid = lo + hi >>> 1;
            final int midValue = this.docBases[mid];
            if (midValue == docID) {
                return mid;
            }
            if (midValue < docID) {
                lo = mid + 1;
            }
            else {
                hi = mid - 1;
            }
        }
        return hi;
    }
    
    private int relativeDocBase(final int block, final int relativeChunk) {
        final int expected = this.avgChunkDocs[block] * relativeChunk;
        final long delta = BitUtil.zigZagDecode(this.docBasesDeltas[block].get(relativeChunk));
        return expected + (int)delta;
    }
    
    private long relativeStartPointer(final int block, final int relativeChunk) {
        final long expected = this.avgChunkSizes[block] * relativeChunk;
        final long delta = BitUtil.zigZagDecode(this.startPointersDeltas[block].get(relativeChunk));
        return expected + delta;
    }
    
    private int relativeChunk(final int block, final int relativeDoc) {
        int lo = 0;
        int hi = this.docBasesDeltas[block].size() - 1;
        while (lo <= hi) {
            final int mid = lo + hi >>> 1;
            final int midValue = this.relativeDocBase(block, mid);
            if (midValue == relativeDoc) {
                return mid;
            }
            if (midValue < relativeDoc) {
                lo = mid + 1;
            }
            else {
                hi = mid - 1;
            }
        }
        return hi;
    }
    
    long getStartPointer(final int docID) {
        if (docID < 0 || docID >= this.maxDoc) {
            throw new IllegalArgumentException("docID out of range [0-" + this.maxDoc + "]: " + docID);
        }
        final int block = this.block(docID);
        final int relativeChunk = this.relativeChunk(block, docID - this.docBases[block]);
        return this.startPointers[block] + this.relativeStartPointer(block, relativeChunk);
    }
    
    public CompressingStoredFieldsIndexReader clone() {
        return this;
    }
    
    @Override
    public long ramBytesUsed() {
        long res = CompressingStoredFieldsIndexReader.BASE_RAM_BYTES_USED;
        res += RamUsageEstimator.shallowSizeOf(this.docBasesDeltas);
        for (final PackedInts.Reader r : this.docBasesDeltas) {
            res += r.ramBytesUsed();
        }
        res += RamUsageEstimator.shallowSizeOf(this.startPointersDeltas);
        for (final PackedInts.Reader r : this.startPointersDeltas) {
            res += r.ramBytesUsed();
        }
        res += RamUsageEstimator.sizeOf(this.docBases);
        res += RamUsageEstimator.sizeOf(this.startPointers);
        res += RamUsageEstimator.sizeOf(this.avgChunkDocs);
        res += RamUsageEstimator.sizeOf(this.avgChunkSizes);
        return res;
    }
    
    @Override
    public Collection<Accountable> getChildResources() {
        final List<Accountable> resources = new ArrayList<Accountable>();
        long docBaseDeltaBytes = RamUsageEstimator.shallowSizeOf(this.docBasesDeltas);
        for (final PackedInts.Reader r : this.docBasesDeltas) {
            docBaseDeltaBytes += r.ramBytesUsed();
        }
        resources.add(Accountables.namedAccountable("doc base deltas", docBaseDeltaBytes));
        long startPointerDeltaBytes = RamUsageEstimator.shallowSizeOf(this.startPointersDeltas);
        for (final PackedInts.Reader r2 : this.startPointersDeltas) {
            startPointerDeltaBytes += r2.ramBytesUsed();
        }
        resources.add(Accountables.namedAccountable("start pointer deltas", startPointerDeltaBytes));
        return (Collection<Accountable>)Collections.unmodifiableList((List<?>)resources);
    }
    
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "(blocks=" + this.docBases.length + ")";
    }
    
    static {
        BASE_RAM_BYTES_USED = RamUsageEstimator.shallowSizeOfInstance(CompressingStoredFieldsIndexReader.class);
    }
}
