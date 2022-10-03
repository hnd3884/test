package org.apache.lucene.rangetree;

import java.util.Arrays;
import org.apache.lucene.store.IndexOutput;
import java.util.Comparator;
import org.apache.lucene.util.InPlaceMergeSorter;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.IOUtils;
import java.io.Closeable;
import org.apache.lucene.store.ByteArrayDataInput;
import org.apache.lucene.util.BytesRefBuilder;
import java.nio.file.Files;
import java.nio.file.attribute.FileAttribute;
import org.apache.lucene.util.ArrayUtil;
import java.io.IOException;
import java.nio.file.Path;
import org.apache.lucene.util.OfflineSorter;
import org.apache.lucene.store.ByteArrayDataOutput;

class RangeTreeWriter
{
    static final int BYTES_PER_DOC = 20;
    public static final int DEFAULT_MAX_VALUES_IN_LEAF_NODE = 1024;
    public static final int DEFAULT_MAX_VALUES_SORT_IN_HEAP = 131072;
    private final byte[] scratchBytes;
    private final ByteArrayDataOutput scratchBytesOutput;
    private OfflineSorter.ByteSequencesWriter writer;
    private GrowingHeapSliceWriter heapWriter;
    private Path tempInput;
    private final int maxValuesInLeafNode;
    private final int maxValuesSortInHeap;
    private long valueCount;
    private long globalMinValue;
    private long globalMaxValue;
    
    public RangeTreeWriter() throws IOException {
        this(1024, 131072);
    }
    
    public RangeTreeWriter(final int maxValuesInLeafNode, final int maxValuesSortInHeap) throws IOException {
        this.scratchBytes = new byte[20];
        this.scratchBytesOutput = new ByteArrayDataOutput(this.scratchBytes);
        this.globalMinValue = Long.MAX_VALUE;
        this.globalMaxValue = Long.MIN_VALUE;
        verifyParams(maxValuesInLeafNode, maxValuesSortInHeap);
        this.maxValuesInLeafNode = maxValuesInLeafNode;
        this.maxValuesSortInHeap = maxValuesSortInHeap;
        this.heapWriter = new GrowingHeapSliceWriter(maxValuesSortInHeap);
    }
    
    public static void verifyParams(final int maxValuesInLeafNode, final int maxValuesSortInHeap) {
        if (maxValuesInLeafNode <= 0) {
            throw new IllegalArgumentException("maxValuesInLeafNode must be > 0; got " + maxValuesInLeafNode);
        }
        if (maxValuesInLeafNode > ArrayUtil.MAX_ARRAY_LENGTH) {
            throw new IllegalArgumentException("maxValuesInLeafNode must be <= ArrayUtil.MAX_ARRAY_LENGTH (= " + ArrayUtil.MAX_ARRAY_LENGTH + "); got " + maxValuesInLeafNode);
        }
        if (maxValuesSortInHeap < maxValuesInLeafNode) {
            throw new IllegalArgumentException("maxValuesSortInHeap must be >= maxValuesInLeafNode; got " + maxValuesSortInHeap + " vs maxValuesInLeafNode=" + maxValuesInLeafNode);
        }
        if (maxValuesSortInHeap > ArrayUtil.MAX_ARRAY_LENGTH) {
            throw new IllegalArgumentException("maxValuesSortInHeap must be <= ArrayUtil.MAX_ARRAY_LENGTH (= " + ArrayUtil.MAX_ARRAY_LENGTH + "); got " + maxValuesSortInHeap);
        }
    }
    
    private void switchToOffline() throws IOException {
        this.tempInput = Files.createTempFile(OfflineSorter.getDefaultTempDir(), "in", "", (FileAttribute<?>[])new FileAttribute[0]);
        this.writer = new OfflineSorter.ByteSequencesWriter(this.tempInput);
        for (int i = 0; i < this.valueCount; ++i) {
            this.scratchBytesOutput.reset(this.scratchBytes);
            this.scratchBytesOutput.writeLong(this.heapWriter.values[i]);
            this.scratchBytesOutput.writeVInt(this.heapWriter.docIDs[i]);
            this.scratchBytesOutput.writeVLong((long)i);
            this.writer.write(this.scratchBytes, 0, this.scratchBytes.length);
        }
        this.heapWriter = null;
    }
    
    void add(final long value, final int docID) throws IOException {
        if (this.valueCount >= this.maxValuesSortInHeap) {
            if (this.writer == null) {
                this.switchToOffline();
            }
            this.scratchBytesOutput.reset(this.scratchBytes);
            this.scratchBytesOutput.writeLong(value);
            this.scratchBytesOutput.writeVInt(docID);
            this.scratchBytesOutput.writeVLong(this.valueCount);
            this.writer.write(this.scratchBytes, 0, this.scratchBytes.length);
        }
        else {
            this.heapWriter.append(value, this.valueCount, docID);
        }
        ++this.valueCount;
        this.globalMaxValue = Math.max(value, this.globalMaxValue);
        this.globalMinValue = Math.min(value, this.globalMinValue);
    }
    
    private SliceWriter convertToFixedWidth(final Path in) throws IOException {
        final BytesRefBuilder scratch = new BytesRefBuilder();
        scratch.grow(20);
        final BytesRef bytes = scratch.get();
        final ByteArrayDataInput dataReader = new ByteArrayDataInput();
        OfflineSorter.ByteSequencesReader reader = null;
        SliceWriter sortedWriter = null;
        boolean success = false;
        try {
            reader = new OfflineSorter.ByteSequencesReader(in);
            sortedWriter = this.getWriter(this.valueCount);
            for (long i = 0L; i < this.valueCount; ++i) {
                final boolean result = reader.read(scratch);
                assert result;
                dataReader.reset(bytes.bytes, bytes.offset, bytes.length);
                final long value = dataReader.readLong();
                final int docID = dataReader.readVInt();
                assert docID >= 0 : "docID=" + docID;
                final long ord = dataReader.readVLong();
                sortedWriter.append(value, ord, docID);
            }
            success = true;
        }
        finally {
            if (success) {
                IOUtils.close(new Closeable[] { sortedWriter, (Closeable)reader });
            }
            else {
                IOUtils.closeWhileHandlingException(new Closeable[] { sortedWriter, (Closeable)reader });
                try {
                    sortedWriter.destroy();
                }
                catch (final Throwable t) {}
            }
        }
        return sortedWriter;
    }
    
    private SliceWriter sort() throws IOException {
        if (this.heapWriter != null) {
            assert this.valueCount < 2147483647L;
            new InPlaceMergeSorter() {
                protected void swap(final int i, final int j) {
                    final int docID = RangeTreeWriter.this.heapWriter.docIDs[i];
                    RangeTreeWriter.this.heapWriter.docIDs[i] = RangeTreeWriter.this.heapWriter.docIDs[j];
                    RangeTreeWriter.this.heapWriter.docIDs[j] = docID;
                    final long ord = RangeTreeWriter.this.heapWriter.ords[i];
                    RangeTreeWriter.this.heapWriter.ords[i] = RangeTreeWriter.this.heapWriter.ords[j];
                    RangeTreeWriter.this.heapWriter.ords[j] = ord;
                    final long value = RangeTreeWriter.this.heapWriter.values[i];
                    RangeTreeWriter.this.heapWriter.values[i] = RangeTreeWriter.this.heapWriter.values[j];
                    RangeTreeWriter.this.heapWriter.values[j] = value;
                }
                
                protected int compare(final int i, final int j) {
                    int cmp = Long.compare(RangeTreeWriter.this.heapWriter.values[i], RangeTreeWriter.this.heapWriter.values[j]);
                    if (cmp != 0) {
                        return cmp;
                    }
                    cmp = Integer.compare(RangeTreeWriter.this.heapWriter.docIDs[i], RangeTreeWriter.this.heapWriter.docIDs[j]);
                    if (cmp != 0) {
                        return cmp;
                    }
                    return Long.compare(RangeTreeWriter.this.heapWriter.ords[i], RangeTreeWriter.this.heapWriter.ords[j]);
                }
            }.sort(0, (int)this.valueCount);
            final HeapSliceWriter sorted = new HeapSliceWriter((int)this.valueCount);
            for (int i = 0; i < this.valueCount; ++i) {
                sorted.append(this.heapWriter.values[i], this.heapWriter.ords[i], this.heapWriter.docIDs[i]);
            }
            sorted.close();
            return sorted;
        }
        else {
            assert this.tempInput != null;
            final ByteArrayDataInput reader = new ByteArrayDataInput();
            final Comparator<BytesRef> cmp = new Comparator<BytesRef>() {
                private final ByteArrayDataInput readerB = new ByteArrayDataInput();
                
                @Override
                public int compare(final BytesRef a, final BytesRef b) {
                    reader.reset(a.bytes, a.offset, a.length);
                    final long valueA = reader.readLong();
                    final int docIDA = reader.readVInt();
                    final long ordA = reader.readVLong();
                    reader.reset(b.bytes, b.offset, b.length);
                    final long valueB = reader.readLong();
                    final int docIDB = reader.readVInt();
                    final long ordB = reader.readVLong();
                    int cmp = Long.compare(valueA, valueB);
                    if (cmp != 0) {
                        return cmp;
                    }
                    cmp = Integer.compare(docIDA, docIDB);
                    if (cmp != 0) {
                        return cmp;
                    }
                    return Long.compare(ordA, ordB);
                }
            };
            final Path sorted2 = Files.createTempFile(OfflineSorter.getDefaultTempDir(), "sorted", "", (FileAttribute<?>[])new FileAttribute[0]);
            boolean success = false;
            try {
                final OfflineSorter sorter = new OfflineSorter((Comparator)cmp);
                sorter.sort(this.tempInput, sorted2);
                final SliceWriter writer = this.convertToFixedWidth(sorted2);
                success = true;
                return writer;
            }
            finally {
                if (success) {
                    IOUtils.rm(new Path[] { sorted2 });
                }
                else {
                    IOUtils.deleteFilesIgnoringExceptions(new Path[] { sorted2 });
                }
            }
        }
    }
    
    public long finish(final IndexOutput out) throws IOException {
        if (this.writer != null) {
            this.writer.close();
        }
        if (this.valueCount == 0L) {
            throw new IllegalStateException("at least one value must be indexed");
        }
        long countPerLeaf;
        long innerNodeCount;
        for (countPerLeaf = this.valueCount, innerNodeCount = 1L; countPerLeaf > this.maxValuesInLeafNode; countPerLeaf = (countPerLeaf + 1L) / 2L, innerNodeCount *= 2L) {}
        if (1L + 2L * innerNodeCount >= 2147483647L) {
            throw new IllegalStateException("too many nodes; increase maxValuesInLeafNode (currently " + this.maxValuesInLeafNode + ") and reindex");
        }
        --innerNodeCount;
        final int numLeaves = (int)(innerNodeCount + 1L);
        final long[] blockMinValues = new long[numLeaves];
        final long[] leafBlockFPs = new long[numLeaves];
        assert this.valueCount / blockMinValues.length <= this.maxValuesInLeafNode : "valueCount=" + this.valueCount + " blockMinValues.length=" + blockMinValues.length + " maxValuesInLeafNode=" + this.maxValuesInLeafNode;
        SliceWriter sortedWriter = null;
        boolean success = false;
        try {
            sortedWriter = this.sort();
            this.heapWriter = null;
            this.build(1, numLeaves, new PathSlice(sortedWriter, 0L, this.valueCount), out, this.globalMinValue, this.globalMaxValue, blockMinValues, leafBlockFPs);
            success = true;
        }
        finally {
            if (success) {
                sortedWriter.destroy();
                IOUtils.rm(new Path[] { this.tempInput });
            }
            else {
                try {
                    sortedWriter.destroy();
                }
                catch (final Throwable t) {}
                IOUtils.deleteFilesIgnoringExceptions(new Path[] { this.tempInput });
            }
        }
        final long indexFP = out.getFilePointer();
        out.writeVInt(numLeaves);
        out.writeVInt((int)(this.valueCount / numLeaves));
        for (int i = 0; i < blockMinValues.length; ++i) {
            out.writeLong(blockMinValues[i]);
        }
        for (int i = 0; i < leafBlockFPs.length; ++i) {
            out.writeVLong(leafBlockFPs[i]);
        }
        out.writeLong(this.globalMaxValue);
        return indexFP;
    }
    
    private long getSplitValue(final PathSlice source, final long leftCount, final long minValue, final long maxValue) throws IOException {
        final SliceReader reader = source.writer.getReader(source.start + leftCount);
        boolean success = false;
        long splitValue;
        try {
            final boolean result = reader.next();
            assert result;
            splitValue = reader.value();
            assert splitValue >= minValue && splitValue <= maxValue : "splitValue=" + splitValue + " minValue=" + minValue + " maxValue=" + maxValue + " reader=" + reader;
            success = true;
        }
        finally {
            if (success) {
                IOUtils.close(new Closeable[] { reader });
            }
            else {
                IOUtils.closeWhileHandlingException(new Closeable[] { reader });
            }
        }
        return splitValue;
    }
    
    private void build(final int nodeID, final int leafNodeOffset, PathSlice source, final IndexOutput out, final long minValue, final long maxValue, final long[] blockMinValues, final long[] leafBlockFPs) throws IOException {
        final long count = source.count;
        if (source.writer instanceof OfflineSliceWriter && count <= this.maxValuesSortInHeap) {
            final SliceWriter writer = new HeapSliceWriter((int)count);
            final SliceReader reader = source.writer.getReader(source.start);
            try {
                for (int i = 0; i < count; ++i) {
                    final boolean hasNext = reader.next();
                    assert hasNext;
                    writer.append(reader.value(), reader.ord(), reader.docID());
                }
            }
            finally {
                IOUtils.close(new Closeable[] { reader, writer });
            }
            source = new PathSlice(writer, 0L, count);
        }
        assert count > 0L;
        if (nodeID >= leafNodeOffset) {
            assert maxValue >= minValue;
            final SliceReader reader2 = source.writer.getReader(source.start);
            final int[] docIDs = new int[(int)count];
            boolean success = false;
            try {
                for (int j = 0; j < source.count; ++j) {
                    final boolean result = reader2.next();
                    assert result;
                    docIDs[j] = reader2.docID();
                }
                success = true;
            }
            finally {
                if (success) {
                    IOUtils.close(new Closeable[] { reader2 });
                }
                else {
                    IOUtils.closeWhileHandlingException(new Closeable[] { reader2 });
                }
            }
            Arrays.sort(docIDs);
            int lastDocID = -1;
            int uniqueCount = 0;
            for (int k = 0; k < docIDs.length; ++k) {
                final int docID = docIDs[k];
                if (docID != lastDocID) {
                    ++uniqueCount;
                    lastDocID = docID;
                }
            }
            assert uniqueCount <= count;
            final long startFP = out.getFilePointer();
            out.writeVInt(uniqueCount);
            final int blockID = nodeID - leafNodeOffset;
            leafBlockFPs[blockID] = startFP;
            blockMinValues[blockID] = minValue;
            lastDocID = -1;
            for (int l = 0; l < docIDs.length; ++l) {
                final int docID2 = docIDs[l];
                if (docID2 != lastDocID) {
                    out.writeInt(docID2);
                    lastDocID = docID2;
                }
            }
        }
        else {
            assert nodeID < blockMinValues.length : "nodeID=" + nodeID + " blockMinValues.length=" + blockMinValues.length;
            assert source.count == count;
            final long leftCount = source.count / 2L;
            final long splitValue = this.getSplitValue(source, leftCount, minValue, maxValue);
            this.build(2 * nodeID, leafNodeOffset, new PathSlice(source.writer, source.start, leftCount), out, minValue, splitValue, blockMinValues, leafBlockFPs);
            this.build(2 * nodeID + 1, leafNodeOffset, new PathSlice(source.writer, source.start + leftCount, count - leftCount), out, splitValue, maxValue, blockMinValues, leafBlockFPs);
        }
    }
    
    SliceWriter getWriter(final long count) throws IOException {
        if (count < this.maxValuesSortInHeap) {
            return new HeapSliceWriter((int)count);
        }
        return new OfflineSliceWriter(count);
    }
    
    private static final class PathSlice
    {
        final SliceWriter writer;
        final long start;
        final long count;
        
        public PathSlice(final SliceWriter writer, final long start, final long count) {
            this.writer = writer;
            this.start = start;
            this.count = count;
        }
        
        @Override
        public String toString() {
            return "PathSlice(start=" + this.start + " count=" + this.count + " writer=" + this.writer + ")";
        }
    }
}
