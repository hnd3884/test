package org.apache.lucene.codecs.compressing;

import org.apache.lucene.index.Fields;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.util.Bits;
import org.apache.lucene.codecs.TermVectorsReader;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.MergeState;
import org.apache.lucene.store.DataInput;
import org.apache.lucene.index.FieldInfos;
import java.util.Arrays;
import java.util.SortedSet;
import org.apache.lucene.util.packed.PackedInts;
import java.util.TreeSet;
import org.apache.lucene.util.StringHelper;
import org.apache.lucene.index.FieldInfo;
import java.io.IOException;
import org.apache.lucene.util.IOUtils;
import java.io.Closeable;
import org.apache.lucene.store.DataOutput;
import org.apache.lucene.codecs.CodecUtil;
import org.apache.lucene.index.IndexFileNames;
import org.apache.lucene.util.ArrayUtil;
import java.util.ArrayDeque;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.index.SegmentInfo;
import org.apache.lucene.store.Directory;
import java.util.Iterator;
import org.apache.lucene.util.packed.BlockPackedWriter;
import org.apache.lucene.util.BytesRef;
import java.util.Deque;
import org.apache.lucene.store.IndexOutput;
import org.apache.lucene.codecs.TermVectorsWriter;

public final class CompressingTermVectorsWriter extends TermVectorsWriter
{
    static final int MAX_DOCUMENTS_PER_CHUNK = 128;
    static final String VECTORS_EXTENSION = "tvd";
    static final String VECTORS_INDEX_EXTENSION = "tvx";
    static final String CODEC_SFX_IDX = "Index";
    static final String CODEC_SFX_DAT = "Data";
    static final int VERSION_START = 0;
    static final int VERSION_CHUNK_STATS = 1;
    static final int VERSION_CURRENT = 1;
    static final int PACKED_BLOCK_SIZE = 64;
    static final int POSITIONS = 1;
    static final int OFFSETS = 2;
    static final int PAYLOADS = 4;
    static final int FLAGS_BITS;
    private final String segment;
    private CompressingStoredFieldsIndexWriter indexWriter;
    private IndexOutput vectorsStream;
    private final CompressionMode compressionMode;
    private final Compressor compressor;
    private final int chunkSize;
    private long numChunks;
    private long numDirtyChunks;
    private int numDocs;
    private final Deque<DocData> pendingDocs;
    private DocData curDoc;
    private FieldData curField;
    private final BytesRef lastTerm;
    private int[] positionsBuf;
    private int[] startOffsetsBuf;
    private int[] lengthsBuf;
    private int[] payloadLengthsBuf;
    private final GrowableByteArrayDataOutput termSuffixes;
    private final GrowableByteArrayDataOutput payloadBytes;
    private final BlockPackedWriter writer;
    static final String BULK_MERGE_ENABLED_SYSPROP;
    static final boolean BULK_MERGE_ENABLED;
    
    private DocData addDocData(final int numVectorFields) {
        FieldData last = null;
        final Iterator<DocData> it = this.pendingDocs.descendingIterator();
        while (it.hasNext()) {
            final DocData doc = it.next();
            if (!doc.fields.isEmpty()) {
                last = doc.fields.getLast();
                break;
            }
        }
        DocData doc2;
        if (last == null) {
            doc2 = new DocData(numVectorFields, 0, 0, 0);
        }
        else {
            final int posStart = last.posStart + (last.hasPositions ? last.totalPositions : 0);
            final int offStart = last.offStart + (last.hasOffsets ? last.totalPositions : 0);
            final int payStart = last.payStart + (last.hasPayloads ? last.totalPositions : 0);
            doc2 = new DocData(numVectorFields, posStart, offStart, payStart);
        }
        this.pendingDocs.add(doc2);
        return doc2;
    }
    
    public CompressingTermVectorsWriter(final Directory directory, final SegmentInfo si, final String segmentSuffix, final IOContext context, final String formatName, final CompressionMode compressionMode, final int chunkSize, final int blockSize) throws IOException {
        assert directory != null;
        this.segment = si.name;
        this.compressionMode = compressionMode;
        this.compressor = compressionMode.newCompressor();
        this.chunkSize = chunkSize;
        this.numDocs = 0;
        this.pendingDocs = new ArrayDeque<DocData>();
        this.termSuffixes = new GrowableByteArrayDataOutput(ArrayUtil.oversize(chunkSize, 1));
        this.payloadBytes = new GrowableByteArrayDataOutput(ArrayUtil.oversize(1, 1));
        this.lastTerm = new BytesRef(ArrayUtil.oversize(30, 1));
        boolean success = false;
        IndexOutput indexStream = directory.createOutput(IndexFileNames.segmentFileName(this.segment, segmentSuffix, "tvx"), context);
        try {
            this.vectorsStream = directory.createOutput(IndexFileNames.segmentFileName(this.segment, segmentSuffix, "tvd"), context);
            final String codecNameIdx = formatName + "Index";
            final String codecNameDat = formatName + "Data";
            CodecUtil.writeIndexHeader(indexStream, codecNameIdx, 1, si.getId(), segmentSuffix);
            CodecUtil.writeIndexHeader(this.vectorsStream, codecNameDat, 1, si.getId(), segmentSuffix);
            assert CodecUtil.indexHeaderLength(codecNameDat, segmentSuffix) == this.vectorsStream.getFilePointer();
            assert CodecUtil.indexHeaderLength(codecNameIdx, segmentSuffix) == indexStream.getFilePointer();
            this.indexWriter = new CompressingStoredFieldsIndexWriter(indexStream, blockSize);
            indexStream = null;
            this.vectorsStream.writeVInt(2);
            this.vectorsStream.writeVInt(chunkSize);
            this.writer = new BlockPackedWriter(this.vectorsStream, 64);
            this.positionsBuf = new int[1024];
            this.startOffsetsBuf = new int[1024];
            this.lengthsBuf = new int[1024];
            this.payloadLengthsBuf = new int[1024];
            success = true;
        }
        finally {
            if (!success) {
                IOUtils.closeWhileHandlingException(this.vectorsStream, indexStream, this.indexWriter);
            }
        }
    }
    
    @Override
    public void close() throws IOException {
        try {
            IOUtils.close(this.vectorsStream, this.indexWriter);
        }
        finally {
            this.vectorsStream = null;
            this.indexWriter = null;
        }
    }
    
    @Override
    public void startDocument(final int numVectorFields) throws IOException {
        this.curDoc = this.addDocData(numVectorFields);
    }
    
    @Override
    public void finishDocument() throws IOException {
        this.termSuffixes.writeBytes(this.payloadBytes.bytes, this.payloadBytes.length);
        this.payloadBytes.length = 0;
        ++this.numDocs;
        if (this.triggerFlush()) {
            this.flush();
        }
        this.curDoc = null;
    }
    
    @Override
    public void startField(final FieldInfo info, final int numTerms, final boolean positions, final boolean offsets, final boolean payloads) throws IOException {
        this.curField = this.curDoc.addField(info.number, numTerms, positions, offsets, payloads);
        this.lastTerm.length = 0;
    }
    
    @Override
    public void finishField() throws IOException {
        this.curField = null;
    }
    
    @Override
    public void startTerm(final BytesRef term, final int freq) throws IOException {
        assert freq >= 1;
        final int prefix = StringHelper.bytesDifference(this.lastTerm, term);
        this.curField.addTerm(freq, prefix, term.length - prefix);
        this.termSuffixes.writeBytes(term.bytes, term.offset + prefix, term.length - prefix);
        if (this.lastTerm.bytes.length < term.length) {
            this.lastTerm.bytes = new byte[ArrayUtil.oversize(term.length, 1)];
        }
        this.lastTerm.offset = 0;
        this.lastTerm.length = term.length;
        System.arraycopy(term.bytes, term.offset, this.lastTerm.bytes, 0, term.length);
    }
    
    @Override
    public void addPosition(final int position, final int startOffset, final int endOffset, final BytesRef payload) throws IOException {
        assert this.curField.flags != 0;
        this.curField.addPosition(position, startOffset, endOffset - startOffset, (payload == null) ? 0 : payload.length);
        if (this.curField.hasPayloads && payload != null) {
            this.payloadBytes.writeBytes(payload.bytes, payload.offset, payload.length);
        }
    }
    
    private boolean triggerFlush() {
        return this.termSuffixes.length >= this.chunkSize || this.pendingDocs.size() >= 128;
    }
    
    private void flush() throws IOException {
        final int chunkDocs = this.pendingDocs.size();
        assert chunkDocs > 0 : chunkDocs;
        this.indexWriter.writeIndex(chunkDocs, this.vectorsStream.getFilePointer());
        final int docBase = this.numDocs - chunkDocs;
        this.vectorsStream.writeVInt(docBase);
        this.vectorsStream.writeVInt(chunkDocs);
        final int totalFields = this.flushNumFields(chunkDocs);
        if (totalFields > 0) {
            final int[] fieldNums = this.flushFieldNums();
            this.flushFields(totalFields, fieldNums);
            this.flushFlags(totalFields, fieldNums);
            this.flushNumTerms(totalFields);
            this.flushTermLengths();
            this.flushTermFreqs();
            this.flushPositions();
            this.flushOffsets(fieldNums);
            this.flushPayloadLengths();
            this.compressor.compress(this.termSuffixes.bytes, 0, this.termSuffixes.length, this.vectorsStream);
        }
        this.pendingDocs.clear();
        this.curDoc = null;
        this.curField = null;
        this.termSuffixes.length = 0;
        ++this.numChunks;
    }
    
    private int flushNumFields(final int chunkDocs) throws IOException {
        if (chunkDocs == 1) {
            final int numFields = this.pendingDocs.getFirst().numFields;
            this.vectorsStream.writeVInt(numFields);
            return numFields;
        }
        this.writer.reset(this.vectorsStream);
        int totalFields = 0;
        for (final DocData dd : this.pendingDocs) {
            this.writer.add(dd.numFields);
            totalFields += dd.numFields;
        }
        this.writer.finish();
        return totalFields;
    }
    
    private int[] flushFieldNums() throws IOException {
        final SortedSet<Integer> fieldNums = new TreeSet<Integer>();
        for (final DocData dd : this.pendingDocs) {
            for (final FieldData fd : dd.fields) {
                fieldNums.add(fd.fieldNum);
            }
        }
        final int numDistinctFields = fieldNums.size();
        assert numDistinctFields > 0;
        final int bitsRequired = PackedInts.bitsRequired(fieldNums.last());
        final int token = Math.min(numDistinctFields - 1, 7) << 5 | bitsRequired;
        this.vectorsStream.writeByte((byte)token);
        if (numDistinctFields - 1 >= 7) {
            this.vectorsStream.writeVInt(numDistinctFields - 1 - 7);
        }
        final PackedInts.Writer writer = PackedInts.getWriterNoHeader(this.vectorsStream, PackedInts.Format.PACKED, fieldNums.size(), bitsRequired, 1);
        for (final Integer fieldNum : fieldNums) {
            writer.add(fieldNum);
        }
        writer.finish();
        final int[] fns = new int[fieldNums.size()];
        int i = 0;
        for (final Integer key : fieldNums) {
            fns[i++] = key;
        }
        return fns;
    }
    
    private void flushFields(final int totalFields, final int[] fieldNums) throws IOException {
        final PackedInts.Writer writer = PackedInts.getWriterNoHeader(this.vectorsStream, PackedInts.Format.PACKED, totalFields, PackedInts.bitsRequired(fieldNums.length - 1), 1);
        for (final DocData dd : this.pendingDocs) {
            for (final FieldData fd : dd.fields) {
                final int fieldNumIndex = Arrays.binarySearch(fieldNums, fd.fieldNum);
                assert fieldNumIndex >= 0;
                writer.add(fieldNumIndex);
            }
        }
        writer.finish();
    }
    
    private void flushFlags(final int totalFields, final int[] fieldNums) throws IOException {
        boolean nonChangingFlags = true;
        final int[] fieldFlags = new int[fieldNums.length];
        Arrays.fill(fieldFlags, -1);
    Label_0157:
        for (final DocData dd : this.pendingDocs) {
            for (final FieldData fd : dd.fields) {
                final int fieldNumOff = Arrays.binarySearch(fieldNums, fd.fieldNum);
                assert fieldNumOff >= 0;
                if (fieldFlags[fieldNumOff] == -1) {
                    fieldFlags[fieldNumOff] = fd.flags;
                }
                else {
                    if (fieldFlags[fieldNumOff] != fd.flags) {
                        nonChangingFlags = false;
                        break Label_0157;
                    }
                    continue;
                }
            }
        }
        if (nonChangingFlags) {
            this.vectorsStream.writeVInt(0);
            final PackedInts.Writer writer = PackedInts.getWriterNoHeader(this.vectorsStream, PackedInts.Format.PACKED, fieldFlags.length, CompressingTermVectorsWriter.FLAGS_BITS, 1);
            for (final int flags : fieldFlags) {
                assert flags >= 0;
                writer.add(flags);
            }
            assert writer.ord() == fieldFlags.length - 1;
            writer.finish();
        }
        else {
            this.vectorsStream.writeVInt(1);
            final PackedInts.Writer writer = PackedInts.getWriterNoHeader(this.vectorsStream, PackedInts.Format.PACKED, totalFields, CompressingTermVectorsWriter.FLAGS_BITS, 1);
            for (final DocData dd2 : this.pendingDocs) {
                for (final FieldData fd2 : dd2.fields) {
                    writer.add(fd2.flags);
                }
            }
            assert writer.ord() == totalFields - 1;
            writer.finish();
        }
    }
    
    private void flushNumTerms(final int totalFields) throws IOException {
        int maxNumTerms = 0;
        for (final DocData dd : this.pendingDocs) {
            for (final FieldData fd : dd.fields) {
                maxNumTerms |= fd.numTerms;
            }
        }
        final int bitsRequired = PackedInts.bitsRequired(maxNumTerms);
        this.vectorsStream.writeVInt(bitsRequired);
        final PackedInts.Writer writer = PackedInts.getWriterNoHeader(this.vectorsStream, PackedInts.Format.PACKED, totalFields, bitsRequired, 1);
        for (final DocData dd2 : this.pendingDocs) {
            for (final FieldData fd2 : dd2.fields) {
                writer.add(fd2.numTerms);
            }
        }
        assert writer.ord() == totalFields - 1;
        writer.finish();
    }
    
    private void flushTermLengths() throws IOException {
        this.writer.reset(this.vectorsStream);
        for (final DocData dd : this.pendingDocs) {
            for (final FieldData fd : dd.fields) {
                for (int i = 0; i < fd.numTerms; ++i) {
                    this.writer.add(fd.prefixLengths[i]);
                }
            }
        }
        this.writer.finish();
        this.writer.reset(this.vectorsStream);
        for (final DocData dd : this.pendingDocs) {
            for (final FieldData fd : dd.fields) {
                for (int i = 0; i < fd.numTerms; ++i) {
                    this.writer.add(fd.suffixLengths[i]);
                }
            }
        }
        this.writer.finish();
    }
    
    private void flushTermFreqs() throws IOException {
        this.writer.reset(this.vectorsStream);
        for (final DocData dd : this.pendingDocs) {
            for (final FieldData fd : dd.fields) {
                for (int i = 0; i < fd.numTerms; ++i) {
                    this.writer.add(fd.freqs[i] - 1);
                }
            }
        }
        this.writer.finish();
    }
    
    private void flushPositions() throws IOException {
        this.writer.reset(this.vectorsStream);
        for (final DocData dd : this.pendingDocs) {
            for (final FieldData fd : dd.fields) {
                if (fd.hasPositions) {
                    int pos = 0;
                    for (int i = 0; i < fd.numTerms; ++i) {
                        int previousPosition = 0;
                        for (int j = 0; j < fd.freqs[i]; ++j) {
                            final int position = this.positionsBuf[fd.posStart + pos++];
                            this.writer.add(position - previousPosition);
                            previousPosition = position;
                        }
                    }
                    assert pos == fd.totalPositions;
                    continue;
                }
            }
        }
        this.writer.finish();
    }
    
    private void flushOffsets(final int[] fieldNums) throws IOException {
        boolean hasOffsets = false;
        final long[] sumPos = new long[fieldNums.length];
        final long[] sumOffsets = new long[fieldNums.length];
        for (final DocData dd : this.pendingDocs) {
            for (final FieldData fd : dd.fields) {
                hasOffsets |= fd.hasOffsets;
                if (fd.hasOffsets && fd.hasPositions) {
                    final int fieldNumOff = Arrays.binarySearch(fieldNums, fd.fieldNum);
                    int pos = 0;
                    for (int i = 0; i < fd.numTerms; ++i) {
                        int previousPos = 0;
                        int previousOff = 0;
                        for (int j = 0; j < fd.freqs[i]; ++j) {
                            final int position = this.positionsBuf[fd.posStart + pos];
                            final int startOffset = this.startOffsetsBuf[fd.offStart + pos];
                            final long[] array = sumPos;
                            final int n = fieldNumOff;
                            array[n] += position - previousPos;
                            final long[] array2 = sumOffsets;
                            final int n2 = fieldNumOff;
                            array2[n2] += startOffset - previousOff;
                            previousPos = position;
                            previousOff = startOffset;
                            ++pos;
                        }
                    }
                    assert pos == fd.totalPositions;
                    continue;
                }
            }
        }
        if (!hasOffsets) {
            return;
        }
        final float[] charsPerTerm = new float[fieldNums.length];
        for (int k = 0; k < fieldNums.length; ++k) {
            charsPerTerm[k] = ((sumPos[k] <= 0L || sumOffsets[k] <= 0L) ? 0.0f : ((float)(sumOffsets[k] / (double)sumPos[k])));
        }
        for (int k = 0; k < fieldNums.length; ++k) {
            this.vectorsStream.writeInt(Float.floatToRawIntBits(charsPerTerm[k]));
        }
        this.writer.reset(this.vectorsStream);
        for (final DocData dd2 : this.pendingDocs) {
            for (final FieldData fd2 : dd2.fields) {
                if ((fd2.flags & 0x2) != 0x0) {
                    final int fieldNumOff2 = Arrays.binarySearch(fieldNums, fd2.fieldNum);
                    final float cpt = charsPerTerm[fieldNumOff2];
                    int pos2 = 0;
                    for (int l = 0; l < fd2.numTerms; ++l) {
                        int previousPos2 = 0;
                        int previousOff2 = 0;
                        for (int m = 0; m < fd2.freqs[l]; ++m) {
                            final int position2 = fd2.hasPositions ? this.positionsBuf[fd2.posStart + pos2] : 0;
                            final int startOffset2 = this.startOffsetsBuf[fd2.offStart + pos2];
                            this.writer.add(startOffset2 - previousOff2 - (int)(cpt * (position2 - previousPos2)));
                            previousPos2 = position2;
                            previousOff2 = startOffset2;
                            ++pos2;
                        }
                    }
                }
            }
        }
        this.writer.finish();
        this.writer.reset(this.vectorsStream);
        for (final DocData dd2 : this.pendingDocs) {
            for (final FieldData fd2 : dd2.fields) {
                if ((fd2.flags & 0x2) != 0x0) {
                    int pos = 0;
                    for (int i = 0; i < fd2.numTerms; ++i) {
                        for (int j2 = 0; j2 < fd2.freqs[i]; ++j2) {
                            this.writer.add(this.lengthsBuf[fd2.offStart + pos++] - fd2.prefixLengths[i] - fd2.suffixLengths[i]);
                        }
                    }
                    assert pos == fd2.totalPositions;
                    continue;
                }
            }
        }
        this.writer.finish();
    }
    
    private void flushPayloadLengths() throws IOException {
        this.writer.reset(this.vectorsStream);
        for (final DocData dd : this.pendingDocs) {
            for (final FieldData fd : dd.fields) {
                if (fd.hasPayloads) {
                    for (int i = 0; i < fd.totalPositions; ++i) {
                        this.writer.add(this.payloadLengthsBuf[fd.payStart + i]);
                    }
                }
            }
        }
        this.writer.finish();
    }
    
    @Override
    public void finish(final FieldInfos fis, final int numDocs) throws IOException {
        if (!this.pendingDocs.isEmpty()) {
            this.flush();
            ++this.numDirtyChunks;
        }
        if (numDocs != this.numDocs) {
            throw new RuntimeException("Wrote " + this.numDocs + " docs, finish called with numDocs=" + numDocs);
        }
        this.indexWriter.finish(numDocs, this.vectorsStream.getFilePointer());
        this.vectorsStream.writeVLong(this.numChunks);
        this.vectorsStream.writeVLong(this.numDirtyChunks);
        CodecUtil.writeFooter(this.vectorsStream);
    }
    
    @Override
    public void addProx(final int numProx, final DataInput positions, final DataInput offsets) throws IOException {
        assert this.curField.hasPositions == (positions != null);
        assert this.curField.hasOffsets == (offsets != null);
        if (this.curField.hasPositions) {
            final int posStart = this.curField.posStart + this.curField.totalPositions;
            if (posStart + numProx > this.positionsBuf.length) {
                this.positionsBuf = ArrayUtil.grow(this.positionsBuf, posStart + numProx);
            }
            int position = 0;
            if (this.curField.hasPayloads) {
                final int payStart = this.curField.payStart + this.curField.totalPositions;
                if (payStart + numProx > this.payloadLengthsBuf.length) {
                    this.payloadLengthsBuf = ArrayUtil.grow(this.payloadLengthsBuf, payStart + numProx);
                }
                for (int i = 0; i < numProx; ++i) {
                    final int code = positions.readVInt();
                    if ((code & 0x1) != 0x0) {
                        final int payloadLength = positions.readVInt();
                        this.payloadLengthsBuf[payStart + i] = payloadLength;
                        this.payloadBytes.copyBytes(positions, payloadLength);
                    }
                    else {
                        this.payloadLengthsBuf[payStart + i] = 0;
                    }
                    position += code >>> 1;
                    this.positionsBuf[posStart + i] = position;
                }
            }
            else {
                for (int j = 0; j < numProx; ++j) {
                    position += positions.readVInt() >>> 1;
                    this.positionsBuf[posStart + j] = position;
                }
            }
        }
        if (this.curField.hasOffsets) {
            final int offStart = this.curField.offStart + this.curField.totalPositions;
            if (offStart + numProx > this.startOffsetsBuf.length) {
                final int newLength = ArrayUtil.oversize(offStart + numProx, 4);
                this.startOffsetsBuf = Arrays.copyOf(this.startOffsetsBuf, newLength);
                this.lengthsBuf = Arrays.copyOf(this.lengthsBuf, newLength);
            }
            int lastOffset = 0;
            for (int k = 0; k < numProx; ++k) {
                final int startOffset = lastOffset + offsets.readVInt();
                final int endOffset = lastOffset = startOffset + offsets.readVInt();
                this.startOffsetsBuf[offStart + k] = startOffset;
                this.lengthsBuf[offStart + k] = endOffset - startOffset;
            }
        }
        final FieldData curField = this.curField;
        curField.totalPositions += numProx;
    }
    
    @Override
    public int merge(final MergeState mergeState) throws IOException {
        int docCount = 0;
        final int numReaders = mergeState.maxDocs.length;
        final MatchingReaders matching = new MatchingReaders(mergeState);
        for (int readerIndex = 0; readerIndex < numReaders; ++readerIndex) {
            CompressingTermVectorsReader matchingVectorsReader = null;
            final TermVectorsReader vectorsReader = mergeState.termVectorsReaders[readerIndex];
            if (matching.matchingReaders[readerIndex] && vectorsReader != null && vectorsReader instanceof CompressingTermVectorsReader) {
                matchingVectorsReader = (CompressingTermVectorsReader)vectorsReader;
            }
            final int maxDoc = mergeState.maxDocs[readerIndex];
            final Bits liveDocs = mergeState.liveDocs[readerIndex];
            if (matchingVectorsReader != null && matchingVectorsReader.getCompressionMode() == this.compressionMode && matchingVectorsReader.getChunkSize() == this.chunkSize && matchingVectorsReader.getVersion() == 1 && matchingVectorsReader.getPackedIntsVersion() == 2 && CompressingTermVectorsWriter.BULK_MERGE_ENABLED && liveDocs == null && !this.tooDirty(matchingVectorsReader)) {
                matchingVectorsReader.checkIntegrity();
                if (!this.pendingDocs.isEmpty()) {
                    this.flush();
                    ++this.numDirtyChunks;
                }
                final IndexInput rawDocs = matchingVectorsReader.getVectorsStream();
                final CompressingStoredFieldsIndexReader index = matchingVectorsReader.getIndexReader();
                rawDocs.seek(index.getStartPointer(0));
                int docID = 0;
                while (docID < maxDoc) {
                    final int base = rawDocs.readVInt();
                    if (base != docID) {
                        throw new CorruptIndexException("invalid state: base=" + base + ", docID=" + docID, rawDocs);
                    }
                    final int bufferedDocs = rawDocs.readVInt();
                    this.indexWriter.writeIndex(bufferedDocs, this.vectorsStream.getFilePointer());
                    this.vectorsStream.writeVInt(docCount);
                    this.vectorsStream.writeVInt(bufferedDocs);
                    docID += bufferedDocs;
                    docCount += bufferedDocs;
                    this.numDocs += bufferedDocs;
                    if (docID > maxDoc) {
                        throw new CorruptIndexException("invalid state: base=" + base + ", count=" + bufferedDocs + ", maxDoc=" + maxDoc, rawDocs);
                    }
                    long end;
                    if (docID == maxDoc) {
                        end = matchingVectorsReader.getMaxPointer();
                    }
                    else {
                        end = index.getStartPointer(docID);
                    }
                    this.vectorsStream.copyBytes(rawDocs, end - rawDocs.getFilePointer());
                }
                if (rawDocs.getFilePointer() != matchingVectorsReader.getMaxPointer()) {
                    throw new CorruptIndexException("invalid state: pos=" + rawDocs.getFilePointer() + ", max=" + matchingVectorsReader.getMaxPointer(), rawDocs);
                }
                this.numChunks += matchingVectorsReader.getNumChunks();
                this.numDirtyChunks += matchingVectorsReader.getNumDirtyChunks();
            }
            else {
                if (vectorsReader != null) {
                    vectorsReader.checkIntegrity();
                }
                for (int i = 0; i < maxDoc; ++i) {
                    if (liveDocs == null || liveDocs.get(i)) {
                        Fields vectors;
                        if (vectorsReader == null) {
                            vectors = null;
                        }
                        else {
                            vectors = vectorsReader.get(i);
                        }
                        this.addAllDocVectors(vectors, mergeState);
                        ++docCount;
                    }
                }
            }
        }
        this.finish(mergeState.mergeFieldInfos, docCount);
        return docCount;
    }
    
    boolean tooDirty(final CompressingTermVectorsReader candidate) {
        return candidate.getNumDirtyChunks() > 1024L || candidate.getNumDirtyChunks() * 100L > candidate.getNumChunks();
    }
    
    static {
        FLAGS_BITS = PackedInts.bitsRequired(7L);
        BULK_MERGE_ENABLED_SYSPROP = CompressingTermVectorsWriter.class.getName() + ".enableBulkMerge";
        boolean v = true;
        try {
            v = Boolean.parseBoolean(System.getProperty(CompressingTermVectorsWriter.BULK_MERGE_ENABLED_SYSPROP, "true"));
        }
        catch (final SecurityException ex) {}
        BULK_MERGE_ENABLED = v;
    }
    
    private class DocData
    {
        final int numFields;
        final Deque<FieldData> fields;
        final int posStart;
        final int offStart;
        final int payStart;
        
        DocData(final int numFields, final int posStart, final int offStart, final int payStart) {
            this.numFields = numFields;
            this.fields = new ArrayDeque<FieldData>(numFields);
            this.posStart = posStart;
            this.offStart = offStart;
            this.payStart = payStart;
        }
        
        FieldData addField(final int fieldNum, final int numTerms, final boolean positions, final boolean offsets, final boolean payloads) {
            FieldData field;
            if (this.fields.isEmpty()) {
                field = new FieldData(fieldNum, numTerms, positions, offsets, payloads, this.posStart, this.offStart, this.payStart);
            }
            else {
                final FieldData last = this.fields.getLast();
                final int posStart = last.posStart + (last.hasPositions ? last.totalPositions : 0);
                final int offStart = last.offStart + (last.hasOffsets ? last.totalPositions : 0);
                final int payStart = last.payStart + (last.hasPayloads ? last.totalPositions : 0);
                field = new FieldData(fieldNum, numTerms, positions, offsets, payloads, posStart, offStart, payStart);
            }
            this.fields.add(field);
            return field;
        }
    }
    
    private class FieldData
    {
        final boolean hasPositions;
        final boolean hasOffsets;
        final boolean hasPayloads;
        final int fieldNum;
        final int flags;
        final int numTerms;
        final int[] freqs;
        final int[] prefixLengths;
        final int[] suffixLengths;
        final int posStart;
        final int offStart;
        final int payStart;
        int totalPositions;
        int ord;
        
        FieldData(final int fieldNum, final int numTerms, final boolean positions, final boolean offsets, final boolean payloads, final int posStart, final int offStart, final int payStart) {
            this.fieldNum = fieldNum;
            this.numTerms = numTerms;
            this.hasPositions = positions;
            this.hasOffsets = offsets;
            this.hasPayloads = payloads;
            this.flags = ((positions ? 1 : 0) | (offsets ? 2 : 0) | (payloads ? 4 : 0));
            this.freqs = new int[numTerms];
            this.prefixLengths = new int[numTerms];
            this.suffixLengths = new int[numTerms];
            this.posStart = posStart;
            this.offStart = offStart;
            this.payStart = payStart;
            this.totalPositions = 0;
            this.ord = 0;
        }
        
        void addTerm(final int freq, final int prefixLength, final int suffixLength) {
            this.freqs[this.ord] = freq;
            this.prefixLengths[this.ord] = prefixLength;
            this.suffixLengths[this.ord] = suffixLength;
            ++this.ord;
        }
        
        void addPosition(final int position, final int startOffset, final int length, final int payloadLength) {
            if (this.hasPositions) {
                if (this.posStart + this.totalPositions == CompressingTermVectorsWriter.this.positionsBuf.length) {
                    CompressingTermVectorsWriter.this.positionsBuf = ArrayUtil.grow(CompressingTermVectorsWriter.this.positionsBuf);
                }
                CompressingTermVectorsWriter.this.positionsBuf[this.posStart + this.totalPositions] = position;
            }
            if (this.hasOffsets) {
                if (this.offStart + this.totalPositions == CompressingTermVectorsWriter.this.startOffsetsBuf.length) {
                    final int newLength = ArrayUtil.oversize(this.offStart + this.totalPositions, 4);
                    CompressingTermVectorsWriter.this.startOffsetsBuf = Arrays.copyOf(CompressingTermVectorsWriter.this.startOffsetsBuf, newLength);
                    CompressingTermVectorsWriter.this.lengthsBuf = Arrays.copyOf(CompressingTermVectorsWriter.this.lengthsBuf, newLength);
                }
                CompressingTermVectorsWriter.this.startOffsetsBuf[this.offStart + this.totalPositions] = startOffset;
                CompressingTermVectorsWriter.this.lengthsBuf[this.offStart + this.totalPositions] = length;
            }
            if (this.hasPayloads) {
                if (this.payStart + this.totalPositions == CompressingTermVectorsWriter.this.payloadLengthsBuf.length) {
                    CompressingTermVectorsWriter.this.payloadLengthsBuf = ArrayUtil.grow(CompressingTermVectorsWriter.this.payloadLengthsBuf);
                }
                CompressingTermVectorsWriter.this.payloadLengthsBuf[this.payStart + this.totalPositions] = payloadLength;
            }
            ++this.totalPositions;
        }
    }
}
