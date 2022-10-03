package org.apache.lucene.bkdtree;

import java.util.Arrays;
import org.apache.lucene.util.LongBitSet;
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

@Deprecated
class BKDTreeWriter
{
    static final int BYTES_PER_DOC = 20;
    public static final int DEFAULT_MAX_POINTS_IN_LEAF_NODE = 1024;
    public static final int DEFAULT_MAX_POINTS_SORT_IN_HEAP = 131072;
    private final byte[] scratchBytes;
    private final ByteArrayDataOutput scratchBytesOutput;
    private OfflineSorter.ByteSequencesWriter writer;
    private GrowingHeapLatLonWriter heapWriter;
    private Path tempInput;
    private final int maxPointsInLeafNode;
    private final int maxPointsSortInHeap;
    private long pointCount;
    static final double MAX_LAT_INCL;
    static final double MAX_LON_INCL;
    static final double MIN_LAT_INCL = -90.0;
    static final double MIN_LON_INCL = -180.0;
    private static final int BITS = 32;
    private static final double LON_SCALE = 1.1930464702777777E7;
    private static final double LAT_SCALE = 2.3860929405555554E7;
    public static final double TOLERANCE = 1.0E-7;
    
    public BKDTreeWriter() throws IOException {
        this(1024, 131072);
    }
    
    public BKDTreeWriter(final int maxPointsInLeafNode, final int maxPointsSortInHeap) throws IOException {
        this.scratchBytes = new byte[20];
        this.scratchBytesOutput = new ByteArrayDataOutput(this.scratchBytes);
        verifyParams(maxPointsInLeafNode, maxPointsSortInHeap);
        this.maxPointsInLeafNode = maxPointsInLeafNode;
        this.maxPointsSortInHeap = maxPointsSortInHeap;
        this.heapWriter = new GrowingHeapLatLonWriter(maxPointsSortInHeap);
    }
    
    public static void verifyParams(final int maxPointsInLeafNode, final int maxPointsSortInHeap) {
        if (maxPointsInLeafNode <= 0) {
            throw new IllegalArgumentException("maxPointsInLeafNode must be > 0; got " + maxPointsInLeafNode);
        }
        if (maxPointsInLeafNode > ArrayUtil.MAX_ARRAY_LENGTH) {
            throw new IllegalArgumentException("maxPointsInLeafNode must be <= ArrayUtil.MAX_ARRAY_LENGTH (= " + ArrayUtil.MAX_ARRAY_LENGTH + "); got " + maxPointsInLeafNode);
        }
        if (maxPointsSortInHeap < maxPointsInLeafNode) {
            throw new IllegalArgumentException("maxPointsSortInHeap must be >= maxPointsInLeafNode; got " + maxPointsSortInHeap + " vs maxPointsInLeafNode=" + maxPointsInLeafNode);
        }
        if (maxPointsSortInHeap > ArrayUtil.MAX_ARRAY_LENGTH) {
            throw new IllegalArgumentException("maxPointsSortInHeap must be <= ArrayUtil.MAX_ARRAY_LENGTH (= " + ArrayUtil.MAX_ARRAY_LENGTH + "); got " + maxPointsSortInHeap);
        }
    }
    
    public void add(final double lat, final double lon, final int docID) throws IOException {
        if (!validLat(lat)) {
            throw new IllegalArgumentException("invalid lat: " + lat);
        }
        if (!validLon(lon)) {
            throw new IllegalArgumentException("invalid lon: " + lon);
        }
        this.add(encodeLat(lat), encodeLon(lon), docID);
    }
    
    private void switchToOffline() throws IOException {
        this.tempInput = Files.createTempFile(OfflineSorter.getDefaultTempDir(), "in", "", (FileAttribute<?>[])new FileAttribute[0]);
        this.writer = new OfflineSorter.ByteSequencesWriter(this.tempInput);
        for (int i = 0; i < this.pointCount; ++i) {
            this.scratchBytesOutput.reset(this.scratchBytes);
            this.scratchBytesOutput.writeInt(this.heapWriter.latEncs[i]);
            this.scratchBytesOutput.writeInt(this.heapWriter.lonEncs[i]);
            this.scratchBytesOutput.writeVInt(this.heapWriter.docIDs[i]);
            this.scratchBytesOutput.writeVLong((long)i);
            this.writer.write(this.scratchBytes, 0, this.scratchBytes.length);
        }
        this.heapWriter = null;
    }
    
    void add(final int latEnc, final int lonEnc, final int docID) throws IOException {
        assert latEnc > Integer.MIN_VALUE;
        assert latEnc < Integer.MAX_VALUE;
        assert lonEnc > Integer.MIN_VALUE;
        assert lonEnc < Integer.MAX_VALUE;
        if (this.pointCount >= this.maxPointsSortInHeap) {
            if (this.writer == null) {
                this.switchToOffline();
            }
            this.scratchBytesOutput.reset(this.scratchBytes);
            this.scratchBytesOutput.writeInt(latEnc);
            this.scratchBytesOutput.writeInt(lonEnc);
            this.scratchBytesOutput.writeVInt(docID);
            this.scratchBytesOutput.writeVLong(this.pointCount);
            this.writer.write(this.scratchBytes, 0, this.scratchBytes.length);
        }
        else {
            this.heapWriter.append(latEnc, lonEnc, this.pointCount, docID);
        }
        ++this.pointCount;
    }
    
    private LatLonWriter convertToFixedWidth(final Path in) throws IOException {
        final BytesRefBuilder scratch = new BytesRefBuilder();
        scratch.grow(20);
        final BytesRef bytes = scratch.get();
        final ByteArrayDataInput dataReader = new ByteArrayDataInput();
        OfflineSorter.ByteSequencesReader reader = null;
        LatLonWriter sortedWriter = null;
        boolean success = false;
        try {
            reader = new OfflineSorter.ByteSequencesReader(in);
            sortedWriter = this.getWriter(this.pointCount);
            for (long i = 0L; i < this.pointCount; ++i) {
                final boolean result = reader.read(scratch);
                assert result;
                dataReader.reset(bytes.bytes, bytes.offset, bytes.length);
                final int latEnc = dataReader.readInt();
                final int lonEnc = dataReader.readInt();
                final int docID = dataReader.readVInt();
                final long ord = dataReader.readVLong();
                assert docID >= 0 : "docID=" + docID;
                assert latEnc > Integer.MIN_VALUE;
                assert latEnc < Integer.MAX_VALUE;
                assert lonEnc > Integer.MIN_VALUE;
                assert lonEnc < Integer.MAX_VALUE;
                sortedWriter.append(latEnc, lonEnc, ord, docID);
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
    
    private LatLonWriter sort(final boolean lon) throws IOException {
        if (this.heapWriter != null) {
            assert this.pointCount < 2147483647L;
            new InPlaceMergeSorter() {
                protected void swap(final int i, final int j) {
                    final int docID = BKDTreeWriter.this.heapWriter.docIDs[i];
                    BKDTreeWriter.this.heapWriter.docIDs[i] = BKDTreeWriter.this.heapWriter.docIDs[j];
                    BKDTreeWriter.this.heapWriter.docIDs[j] = docID;
                    final long ord = BKDTreeWriter.this.heapWriter.ords[i];
                    BKDTreeWriter.this.heapWriter.ords[i] = BKDTreeWriter.this.heapWriter.ords[j];
                    BKDTreeWriter.this.heapWriter.ords[j] = ord;
                    final int latEnc = BKDTreeWriter.this.heapWriter.latEncs[i];
                    BKDTreeWriter.this.heapWriter.latEncs[i] = BKDTreeWriter.this.heapWriter.latEncs[j];
                    BKDTreeWriter.this.heapWriter.latEncs[j] = latEnc;
                    final int lonEnc = BKDTreeWriter.this.heapWriter.lonEncs[i];
                    BKDTreeWriter.this.heapWriter.lonEncs[i] = BKDTreeWriter.this.heapWriter.lonEncs[j];
                    BKDTreeWriter.this.heapWriter.lonEncs[j] = lonEnc;
                }
                
                protected int compare(final int i, final int j) {
                    int cmp;
                    if (lon) {
                        cmp = Integer.compare(BKDTreeWriter.this.heapWriter.lonEncs[i], BKDTreeWriter.this.heapWriter.lonEncs[j]);
                    }
                    else {
                        cmp = Integer.compare(BKDTreeWriter.this.heapWriter.latEncs[i], BKDTreeWriter.this.heapWriter.latEncs[j]);
                    }
                    if (cmp != 0) {
                        return cmp;
                    }
                    cmp = Integer.compare(BKDTreeWriter.this.heapWriter.docIDs[i], BKDTreeWriter.this.heapWriter.docIDs[j]);
                    if (cmp != 0) {
                        return cmp;
                    }
                    return Long.compare(BKDTreeWriter.this.heapWriter.ords[i], BKDTreeWriter.this.heapWriter.ords[j]);
                }
            }.sort(0, (int)this.pointCount);
            final HeapLatLonWriter sorted = new HeapLatLonWriter((int)this.pointCount);
            for (int i = 0; i < this.pointCount; ++i) {
                sorted.append(this.heapWriter.latEncs[i], this.heapWriter.lonEncs[i], this.heapWriter.ords[i], this.heapWriter.docIDs[i]);
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
                    final int latAEnc = reader.readInt();
                    final int lonAEnc = reader.readInt();
                    final int docIDA = reader.readVInt();
                    final long ordA = reader.readVLong();
                    reader.reset(b.bytes, b.offset, b.length);
                    final int latBEnc = reader.readInt();
                    final int lonBEnc = reader.readInt();
                    final int docIDB = reader.readVInt();
                    final long ordB = reader.readVLong();
                    int cmp;
                    if (lon) {
                        cmp = Integer.compare(lonAEnc, lonBEnc);
                    }
                    else {
                        cmp = Integer.compare(latAEnc, latBEnc);
                    }
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
                final OfflineSorter latSorter = new OfflineSorter((Comparator)cmp);
                latSorter.sort(this.tempInput, sorted2);
                final LatLonWriter writer = this.convertToFixedWidth(sorted2);
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
        final LongBitSet bitSet = new LongBitSet(this.pointCount);
        long countPerLeaf;
        long innerNodeCount;
        for (countPerLeaf = this.pointCount, innerNodeCount = 1L; countPerLeaf > this.maxPointsInLeafNode; countPerLeaf = (countPerLeaf + 1L) / 2L, innerNodeCount *= 2L) {}
        if (1L + 2L * innerNodeCount >= 2147483647L) {
            throw new IllegalStateException("too many nodes; increase maxPointsInLeafNode (currently " + this.maxPointsInLeafNode + ") and reindex");
        }
        --innerNodeCount;
        final int numLeaves = (int)(innerNodeCount + 1L);
        final int[] splitValues = new int[numLeaves];
        final long[] leafBlockFPs = new long[numLeaves];
        assert this.pointCount / splitValues.length <= this.maxPointsInLeafNode : "pointCount=" + this.pointCount + " splitValues.length=" + splitValues.length + " maxPointsInLeafNode=" + this.maxPointsInLeafNode;
        LatLonWriter latSortedWriter = null;
        LatLonWriter lonSortedWriter = null;
        boolean success = false;
        try {
            lonSortedWriter = this.sort(true);
            latSortedWriter = this.sort(false);
            this.heapWriter = null;
            this.build(1, numLeaves, new PathSlice(latSortedWriter, 0L, this.pointCount), new PathSlice(lonSortedWriter, 0L, this.pointCount), bitSet, out, Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE, splitValues, leafBlockFPs);
            success = true;
        }
        finally {
            if (success) {
                latSortedWriter.destroy();
                lonSortedWriter.destroy();
                IOUtils.rm(new Path[] { this.tempInput });
            }
            else {
                try {
                    latSortedWriter.destroy();
                }
                catch (final Throwable t) {}
                try {
                    lonSortedWriter.destroy();
                }
                catch (final Throwable t2) {}
                IOUtils.deleteFilesIgnoringExceptions(new Path[] { this.tempInput });
            }
        }
        final long indexFP = out.getFilePointer();
        out.writeVInt(numLeaves);
        for (int i = 0; i < splitValues.length; ++i) {
            out.writeInt(splitValues[i]);
        }
        for (int i = 0; i < leafBlockFPs.length; ++i) {
            out.writeVLong(leafBlockFPs[i]);
        }
        return indexFP;
    }
    
    private long markLeftTree(final int splitDim, final PathSlice source, final LongBitSet bitSet, final int[] splitValueRet, final int minLatEnc, final int maxLatEnc, final int minLonEnc, final int maxLonEnc) throws IOException {
        long leftCount = source.count / 2L;
        LatLonReader reader = source.writer.getReader(source.start + leftCount);
        boolean success = false;
        int splitValue;
        try {
            final boolean result = reader.next();
            assert result;
            final int latSplitEnc = reader.latEnc();
            assert latSplitEnc >= minLatEnc && latSplitEnc < maxLatEnc : "latSplitEnc=" + latSplitEnc + " minLatEnc=" + minLatEnc + " maxLatEnc=" + maxLatEnc;
            final int lonSplitEnc = reader.lonEnc();
            assert lonSplitEnc >= minLonEnc && lonSplitEnc < maxLonEnc : "lonSplitEnc=" + lonSplitEnc + " minLonEnc=" + minLonEnc + " maxLonEnc=" + maxLonEnc;
            if (splitDim == 0) {
                splitValue = latSplitEnc;
            }
            else {
                splitValue = lonSplitEnc;
            }
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
        splitValueRet[0] = splitValue;
        assert bitSet.cardinality() == 0L : "cardinality=" + bitSet.cardinality();
        success = false;
        reader = source.writer.getReader(source.start);
        try {
            int lastValue = Integer.MIN_VALUE;
            for (int i = 0; i < leftCount; ++i) {
                final boolean result2 = reader.next();
                assert result2;
                final int latEnc = reader.latEnc();
                final int lonEnc = reader.lonEnc();
                int value;
                if (splitDim == 0) {
                    value = latEnc;
                }
                else {
                    value = lonEnc;
                }
                assert value >= lastValue;
                if ((lastValue = value) == splitValue) {
                    leftCount = i;
                    break;
                }
                assert value < splitValue : "i=" + i + " value=" + value + " vs splitValue=" + splitValue;
                final long ord = reader.ord();
                final int docID = reader.docID();
                assert docID >= 0 : "docID=" + docID + " reader=" + reader;
                assert !bitSet.get(ord);
                bitSet.set(ord);
            }
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
        assert leftCount == bitSet.cardinality() : "leftCount=" + leftCount + " cardinality=" + bitSet.cardinality();
        return leftCount;
    }
    
    private void build(final int nodeID, final int leafNodeOffset, final PathSlice lastLatSorted, final PathSlice lastLonSorted, final LongBitSet bitSet, final IndexOutput out, final int minLatEnc, final int maxLatEnc, final int minLonEnc, final int maxLonEnc, final int[] splitValues, final long[] leafBlockFPs) throws IOException {
        final long latRange = maxLatEnc - (long)minLatEnc;
        final long lonRange = maxLonEnc - (long)minLonEnc;
        assert lastLatSorted.count == lastLonSorted.count;
        int splitDim;
        PathSlice source;
        PathSlice nextSource;
        if (latRange >= lonRange) {
            splitDim = 0;
            source = lastLatSorted;
            nextSource = lastLonSorted;
        }
        else {
            splitDim = 1;
            source = lastLonSorted;
            nextSource = lastLatSorted;
        }
        final long count = source.count;
        if (count == 0L) {
            if (nodeID < splitValues.length) {
                splitValues[nodeID] = Integer.MAX_VALUE;
            }
            return;
        }
        if (nodeID >= leafNodeOffset) {
            assert maxLatEnc > minLatEnc;
            assert maxLonEnc > minLonEnc;
            final LatLonReader reader = source.writer.getReader(source.start);
            final int[] docIDs = new int[(int)count];
            boolean success = false;
            try {
                for (int i = 0; i < source.count; ++i) {
                    final boolean result = reader.next();
                    assert result;
                    docIDs[i] = reader.docID();
                }
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
            Arrays.sort(docIDs);
            int lastDocID = -1;
            int uniqueCount = 0;
            for (int j = 0; j < docIDs.length; ++j) {
                final int docID = docIDs[j];
                if (docID != lastDocID) {
                    ++uniqueCount;
                    lastDocID = docID;
                }
            }
            assert uniqueCount <= count;
            final long startFP = out.getFilePointer();
            out.writeVInt(uniqueCount);
            leafBlockFPs[nodeID - leafNodeOffset] = startFP;
            lastDocID = -1;
            for (int k = 0; k < docIDs.length; ++k) {
                final int docID2 = docIDs[k];
                if (docID2 != lastDocID) {
                    out.writeInt(docID2);
                    lastDocID = docID2;
                }
            }
        }
        else {
            assert nodeID < splitValues.length : "nodeID=" + nodeID + " splitValues.length=" + splitValues.length;
            final int[] splitValueArray = { 0 };
            final long leftCount = this.markLeftTree(splitDim, source, bitSet, splitValueArray, minLatEnc, maxLatEnc, minLonEnc, maxLonEnc);
            final int splitValue = splitValueArray[0];
            LatLonWriter leftWriter = null;
            LatLonWriter rightWriter = null;
            LatLonReader reader2 = null;
            boolean success2 = false;
            int nextLeftCount = 0;
            try {
                leftWriter = this.getWriter(leftCount);
                rightWriter = this.getWriter(count - leftCount);
                reader2 = nextSource.writer.getReader(nextSource.start);
                for (int l = 0; l < count; ++l) {
                    final boolean result2 = reader2.next();
                    assert result2;
                    final int latEnc = reader2.latEnc();
                    final int lonEnc = reader2.lonEnc();
                    final long ord = reader2.ord();
                    final int docID3 = reader2.docID();
                    assert docID3 >= 0 : "docID=" + docID3 + " reader=" + reader2;
                    if (bitSet.get(ord)) {
                        if (splitDim == 0) {
                            assert latEnc < splitValue : "latEnc=" + latEnc + " splitValue=" + splitValue;
                        }
                        else {
                            assert lonEnc < splitValue : "lonEnc=" + lonEnc + " splitValue=" + splitValue;
                        }
                        leftWriter.append(latEnc, lonEnc, ord, docID3);
                        ++nextLeftCount;
                    }
                    else {
                        if (splitDim == 0) {
                            assert latEnc >= splitValue : "latEnc=" + latEnc + " splitValue=" + splitValue;
                        }
                        else {
                            assert lonEnc >= splitValue : "lonEnc=" + lonEnc + " splitValue=" + splitValue;
                        }
                        rightWriter.append(latEnc, lonEnc, ord, docID3);
                    }
                }
                bitSet.clear(0L, this.pointCount);
                success2 = true;
            }
            finally {
                if (success2) {
                    IOUtils.close(new Closeable[] { reader2, leftWriter, rightWriter });
                }
                else {
                    IOUtils.closeWhileHandlingException(new Closeable[] { reader2, leftWriter, rightWriter });
                }
            }
            assert leftCount == nextLeftCount : "leftCount=" + leftCount + " nextLeftCount=" + nextLeftCount;
            success2 = false;
            try {
                if (splitDim == 0) {
                    this.build(2 * nodeID, leafNodeOffset, new PathSlice(source.writer, source.start, leftCount), new PathSlice(leftWriter, 0L, leftCount), bitSet, out, minLatEnc, splitValue, minLonEnc, maxLonEnc, splitValues, leafBlockFPs);
                    leftWriter.destroy();
                    this.build(2 * nodeID + 1, leafNodeOffset, new PathSlice(source.writer, source.start + leftCount, count - leftCount), new PathSlice(rightWriter, 0L, count - leftCount), bitSet, out, splitValue, maxLatEnc, minLonEnc, maxLonEnc, splitValues, leafBlockFPs);
                    rightWriter.destroy();
                }
                else {
                    this.build(2 * nodeID, leafNodeOffset, new PathSlice(leftWriter, 0L, leftCount), new PathSlice(source.writer, source.start, leftCount), bitSet, out, minLatEnc, maxLatEnc, minLonEnc, splitValue, splitValues, leafBlockFPs);
                    leftWriter.destroy();
                    this.build(2 * nodeID + 1, leafNodeOffset, new PathSlice(rightWriter, 0L, count - leftCount), new PathSlice(source.writer, source.start + leftCount, count - leftCount), bitSet, out, minLatEnc, maxLatEnc, splitValue, maxLonEnc, splitValues, leafBlockFPs);
                    rightWriter.destroy();
                }
                success2 = true;
            }
            finally {
                if (!success2) {
                    try {
                        leftWriter.destroy();
                    }
                    catch (final Throwable t) {}
                    try {
                        rightWriter.destroy();
                    }
                    catch (final Throwable t2) {}
                }
            }
            splitValues[nodeID] = splitValue;
        }
    }
    
    LatLonWriter getWriter(final long count) throws IOException {
        if (count < this.maxPointsSortInHeap) {
            return new HeapLatLonWriter((int)count);
        }
        return new OfflineLatLonWriter(count);
    }
    
    static boolean validLat(final double lat) {
        return !Double.isNaN(lat) && lat >= -90.0 && lat <= BKDTreeWriter.MAX_LAT_INCL;
    }
    
    static boolean validLon(final double lon) {
        return !Double.isNaN(lon) && lon >= -180.0 && lon <= BKDTreeWriter.MAX_LON_INCL;
    }
    
    static int encodeLat(final double lat) {
        assert validLat(lat) : "lat=" + lat;
        final long x = (long)(lat * 2.3860929405555554E7);
        assert x < 2147483647L : "lat=" + lat + " mapped to Integer.MAX_VALUE + " + (x - 2147483647L);
        assert x > -2147483648L : "lat=" + lat + " mapped to Integer.MIN_VALUE";
        return (int)x;
    }
    
    static int encodeLon(final double lon) {
        assert validLon(lon) : "lon=" + lon;
        final long x = (long)(lon * 1.1930464702777777E7);
        assert x < 2147483647L;
        assert x > -2147483648L;
        return (int)x;
    }
    
    static double decodeLat(final int x) {
        return x / 2.3860929405555554E7;
    }
    
    static double decodeLon(final int x) {
        return x / 1.1930464702777777E7;
    }
    
    static {
        MAX_LAT_INCL = Math.nextAfter(90.0, Double.POSITIVE_INFINITY);
        MAX_LON_INCL = Math.nextAfter(180.0, Double.POSITIVE_INFINITY);
    }
    
    private static final class PathSlice
    {
        final LatLonWriter writer;
        final long start;
        final long count;
        
        public PathSlice(final LatLonWriter writer, final long start, final long count) {
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
