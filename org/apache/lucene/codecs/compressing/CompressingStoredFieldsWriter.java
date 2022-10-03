package org.apache.lucene.codecs.compressing;

import org.apache.lucene.store.IndexInput;
import org.apache.lucene.util.Bits;
import org.apache.lucene.codecs.StoredFieldsReader;
import org.apache.lucene.store.DataInput;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.StoredFieldVisitor;
import org.apache.lucene.index.MergeState;
import org.apache.lucene.index.FieldInfos;
import org.apache.lucene.util.BitUtil;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.util.packed.PackedInts;
import java.util.Arrays;
import org.apache.lucene.util.ArrayUtil;
import java.io.IOException;
import org.apache.lucene.util.IOUtils;
import java.io.Closeable;
import org.apache.lucene.store.DataOutput;
import org.apache.lucene.codecs.CodecUtil;
import org.apache.lucene.index.IndexFileNames;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.index.SegmentInfo;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.IndexOutput;
import org.apache.lucene.codecs.StoredFieldsWriter;

public final class CompressingStoredFieldsWriter extends StoredFieldsWriter
{
    public static final String FIELDS_EXTENSION = "fdt";
    public static final String FIELDS_INDEX_EXTENSION = "fdx";
    static final int STRING = 0;
    static final int BYTE_ARR = 1;
    static final int NUMERIC_INT = 2;
    static final int NUMERIC_FLOAT = 3;
    static final int NUMERIC_LONG = 4;
    static final int NUMERIC_DOUBLE = 5;
    static final int TYPE_BITS;
    static final int TYPE_MASK;
    static final String CODEC_SFX_IDX = "Index";
    static final String CODEC_SFX_DAT = "Data";
    static final int VERSION_START = 0;
    static final int VERSION_CHUNK_STATS = 1;
    static final int VERSION_CURRENT = 1;
    private final String segment;
    private CompressingStoredFieldsIndexWriter indexWriter;
    private IndexOutput fieldsStream;
    private final Compressor compressor;
    private final CompressionMode compressionMode;
    private final int chunkSize;
    private final int maxDocsPerChunk;
    private final GrowableByteArrayDataOutput bufferedDocs;
    private int[] numStoredFields;
    private int[] endOffsets;
    private int docBase;
    private int numBufferedDocs;
    private long numChunks;
    private long numDirtyChunks;
    private int numStoredFieldsInDoc;
    static final int NEGATIVE_ZERO_FLOAT;
    static final long NEGATIVE_ZERO_DOUBLE;
    static final long SECOND = 1000L;
    static final long HOUR = 3600000L;
    static final long DAY = 86400000L;
    static final int SECOND_ENCODING = 64;
    static final int HOUR_ENCODING = 128;
    static final int DAY_ENCODING = 192;
    static final String BULK_MERGE_ENABLED_SYSPROP;
    static final boolean BULK_MERGE_ENABLED;
    
    public CompressingStoredFieldsWriter(final Directory directory, final SegmentInfo si, final String segmentSuffix, final IOContext context, final String formatName, final CompressionMode compressionMode, final int chunkSize, final int maxDocsPerChunk, final int blockSize) throws IOException {
        assert directory != null;
        this.segment = si.name;
        this.compressionMode = compressionMode;
        this.compressor = compressionMode.newCompressor();
        this.chunkSize = chunkSize;
        this.maxDocsPerChunk = maxDocsPerChunk;
        this.docBase = 0;
        this.bufferedDocs = new GrowableByteArrayDataOutput(chunkSize);
        this.numStoredFields = new int[16];
        this.endOffsets = new int[16];
        this.numBufferedDocs = 0;
        boolean success = false;
        IndexOutput indexStream = directory.createOutput(IndexFileNames.segmentFileName(this.segment, segmentSuffix, "fdx"), context);
        try {
            this.fieldsStream = directory.createOutput(IndexFileNames.segmentFileName(this.segment, segmentSuffix, "fdt"), context);
            final String codecNameIdx = formatName + "Index";
            final String codecNameDat = formatName + "Data";
            CodecUtil.writeIndexHeader(indexStream, codecNameIdx, 1, si.getId(), segmentSuffix);
            CodecUtil.writeIndexHeader(this.fieldsStream, codecNameDat, 1, si.getId(), segmentSuffix);
            assert CodecUtil.indexHeaderLength(codecNameDat, segmentSuffix) == this.fieldsStream.getFilePointer();
            assert CodecUtil.indexHeaderLength(codecNameIdx, segmentSuffix) == indexStream.getFilePointer();
            this.indexWriter = new CompressingStoredFieldsIndexWriter(indexStream, blockSize);
            indexStream = null;
            this.fieldsStream.writeVInt(chunkSize);
            this.fieldsStream.writeVInt(2);
            success = true;
        }
        finally {
            if (!success) {
                IOUtils.closeWhileHandlingException(this.fieldsStream, indexStream, this.indexWriter);
            }
        }
    }
    
    @Override
    public void close() throws IOException {
        try {
            IOUtils.close(this.fieldsStream, this.indexWriter);
        }
        finally {
            this.fieldsStream = null;
            this.indexWriter = null;
        }
    }
    
    @Override
    public void startDocument() throws IOException {
    }
    
    @Override
    public void finishDocument() throws IOException {
        if (this.numBufferedDocs == this.numStoredFields.length) {
            final int newLength = ArrayUtil.oversize(this.numBufferedDocs + 1, 4);
            this.numStoredFields = Arrays.copyOf(this.numStoredFields, newLength);
            this.endOffsets = Arrays.copyOf(this.endOffsets, newLength);
        }
        this.numStoredFields[this.numBufferedDocs] = this.numStoredFieldsInDoc;
        this.numStoredFieldsInDoc = 0;
        this.endOffsets[this.numBufferedDocs] = this.bufferedDocs.length;
        ++this.numBufferedDocs;
        if (this.triggerFlush()) {
            this.flush();
        }
    }
    
    private static void saveInts(final int[] values, final int length, final DataOutput out) throws IOException {
        assert length > 0;
        if (length == 1) {
            out.writeVInt(values[0]);
        }
        else {
            boolean allEqual = true;
            for (int i = 1; i < length; ++i) {
                if (values[i] != values[0]) {
                    allEqual = false;
                    break;
                }
            }
            if (allEqual) {
                out.writeVInt(0);
                out.writeVInt(values[0]);
            }
            else {
                long max = 0L;
                for (int j = 0; j < length; ++j) {
                    max |= values[j];
                }
                final int bitsRequired = PackedInts.bitsRequired(max);
                out.writeVInt(bitsRequired);
                final PackedInts.Writer w = PackedInts.getWriterNoHeader(out, PackedInts.Format.PACKED, length, bitsRequired, 1);
                for (int k = 0; k < length; ++k) {
                    w.add(values[k]);
                }
                w.finish();
            }
        }
    }
    
    private void writeHeader(final int docBase, final int numBufferedDocs, final int[] numStoredFields, final int[] lengths, final boolean sliced) throws IOException {
        final int slicedBit = sliced ? 1 : 0;
        this.fieldsStream.writeVInt(docBase);
        this.fieldsStream.writeVInt(numBufferedDocs << 1 | slicedBit);
        saveInts(numStoredFields, numBufferedDocs, this.fieldsStream);
        saveInts(lengths, numBufferedDocs, this.fieldsStream);
    }
    
    private boolean triggerFlush() {
        return this.bufferedDocs.length >= this.chunkSize || this.numBufferedDocs >= this.maxDocsPerChunk;
    }
    
    private void flush() throws IOException {
        this.indexWriter.writeIndex(this.numBufferedDocs, this.fieldsStream.getFilePointer());
        final int[] lengths = this.endOffsets;
        for (int i = this.numBufferedDocs - 1; i > 0; --i) {
            lengths[i] = this.endOffsets[i] - this.endOffsets[i - 1];
            assert lengths[i] >= 0;
        }
        final boolean sliced = this.bufferedDocs.length >= 2 * this.chunkSize;
        this.writeHeader(this.docBase, this.numBufferedDocs, this.numStoredFields, lengths, sliced);
        if (sliced) {
            for (int compressed = 0; compressed < this.bufferedDocs.length; compressed += this.chunkSize) {
                this.compressor.compress(this.bufferedDocs.bytes, compressed, Math.min(this.chunkSize, this.bufferedDocs.length - compressed), this.fieldsStream);
            }
        }
        else {
            this.compressor.compress(this.bufferedDocs.bytes, 0, this.bufferedDocs.length, this.fieldsStream);
        }
        this.docBase += this.numBufferedDocs;
        this.numBufferedDocs = 0;
        this.bufferedDocs.length = 0;
        ++this.numChunks;
    }
    
    @Override
    public void writeField(final FieldInfo info, final IndexableField field) throws IOException {
        ++this.numStoredFieldsInDoc;
        int bits = 0;
        final Number number = field.numericValue();
        String string;
        BytesRef bytes;
        if (number != null) {
            if (number instanceof Byte || number instanceof Short || number instanceof Integer) {
                bits = 2;
            }
            else if (number instanceof Long) {
                bits = 4;
            }
            else if (number instanceof Float) {
                bits = 3;
            }
            else {
                if (!(number instanceof Double)) {
                    throw new IllegalArgumentException("cannot store numeric type " + number.getClass());
                }
                bits = 5;
            }
            string = null;
            bytes = null;
        }
        else {
            bytes = field.binaryValue();
            if (bytes != null) {
                bits = 1;
                string = null;
            }
            else {
                bits = 0;
                string = field.stringValue();
                if (string == null) {
                    throw new IllegalArgumentException("field " + field.name() + " is stored but does not have binaryValue, stringValue nor numericValue");
                }
            }
        }
        final long infoAndBits = (long)info.number << CompressingStoredFieldsWriter.TYPE_BITS | (long)bits;
        this.bufferedDocs.writeVLong(infoAndBits);
        if (bytes != null) {
            this.bufferedDocs.writeVInt(bytes.length);
            this.bufferedDocs.writeBytes(bytes.bytes, bytes.offset, bytes.length);
        }
        else if (string != null) {
            this.bufferedDocs.writeString(string);
        }
        else if (number instanceof Byte || number instanceof Short || number instanceof Integer) {
            this.bufferedDocs.writeZInt(number.intValue());
        }
        else if (number instanceof Long) {
            writeTLong(this.bufferedDocs, number.longValue());
        }
        else if (number instanceof Float) {
            writeZFloat(this.bufferedDocs, number.floatValue());
        }
        else {
            if (!(number instanceof Double)) {
                throw new AssertionError((Object)"Cannot get here");
            }
            writeZDouble(this.bufferedDocs, number.doubleValue());
        }
    }
    
    static void writeZFloat(final DataOutput out, final float f) throws IOException {
        final int intVal = (int)f;
        final int floatBits = Float.floatToIntBits(f);
        if (f == intVal && intVal >= -1 && intVal <= 125 && floatBits != CompressingStoredFieldsWriter.NEGATIVE_ZERO_FLOAT) {
            out.writeByte((byte)(0x80 | 1 + intVal));
        }
        else if (floatBits >>> 31 == 0) {
            out.writeInt(floatBits);
        }
        else {
            out.writeByte((byte)(-1));
            out.writeInt(floatBits);
        }
    }
    
    static void writeZDouble(final DataOutput out, final double d) throws IOException {
        final int intVal = (int)d;
        final long doubleBits = Double.doubleToLongBits(d);
        if (d == intVal && intVal >= -1 && intVal <= 124 && doubleBits != CompressingStoredFieldsWriter.NEGATIVE_ZERO_DOUBLE) {
            out.writeByte((byte)(0x80 | intVal + 1));
            return;
        }
        if (d == (float)d) {
            out.writeByte((byte)(-2));
            out.writeInt(Float.floatToIntBits((float)d));
        }
        else if (doubleBits >>> 63 == 0L) {
            out.writeLong(doubleBits);
        }
        else {
            out.writeByte((byte)(-1));
            out.writeLong(doubleBits);
        }
    }
    
    static void writeTLong(final DataOutput out, long l) throws IOException {
        int header;
        if (l % 1000L != 0L) {
            header = 0;
        }
        else if (l % 86400000L == 0L) {
            header = 192;
            l /= 86400000L;
        }
        else if (l % 3600000L == 0L) {
            header = 128;
            l /= 3600000L;
        }
        else {
            header = 64;
            l /= 1000L;
        }
        final long zigZagL = BitUtil.zigZagEncode(l);
        header = (int)((long)header | (zigZagL & 0x1FL));
        final long upperBits = zigZagL >>> 5;
        if (upperBits != 0L) {
            header |= 0x20;
        }
        out.writeByte((byte)header);
        if (upperBits != 0L) {
            out.writeVLong(upperBits);
        }
    }
    
    @Override
    public void finish(final FieldInfos fis, final int numDocs) throws IOException {
        if (this.numBufferedDocs > 0) {
            this.flush();
            ++this.numDirtyChunks;
        }
        else {
            assert this.bufferedDocs.length == 0;
        }
        if (this.docBase != numDocs) {
            throw new RuntimeException("Wrote " + this.docBase + " docs, finish called with numDocs=" + numDocs);
        }
        this.indexWriter.finish(numDocs, this.fieldsStream.getFilePointer());
        this.fieldsStream.writeVLong(this.numChunks);
        this.fieldsStream.writeVLong(this.numDirtyChunks);
        CodecUtil.writeFooter(this.fieldsStream);
        assert this.bufferedDocs.length == 0;
    }
    
    @Override
    public int merge(final MergeState mergeState) throws IOException {
        int docCount = 0;
        final int numReaders = mergeState.maxDocs.length;
        final MatchingReaders matching = new MatchingReaders(mergeState);
        for (int readerIndex = 0; readerIndex < numReaders; ++readerIndex) {
            final MergeVisitor visitor = new MergeVisitor(mergeState, readerIndex);
            CompressingStoredFieldsReader matchingFieldsReader = null;
            if (matching.matchingReaders[readerIndex]) {
                final StoredFieldsReader fieldsReader = mergeState.storedFieldsReaders[readerIndex];
                if (fieldsReader != null && fieldsReader instanceof CompressingStoredFieldsReader) {
                    matchingFieldsReader = (CompressingStoredFieldsReader)fieldsReader;
                }
            }
            final int maxDoc = mergeState.maxDocs[readerIndex];
            final Bits liveDocs = mergeState.liveDocs[readerIndex];
            if (matchingFieldsReader == null || matchingFieldsReader.getVersion() != 1 || !CompressingStoredFieldsWriter.BULK_MERGE_ENABLED) {
                final StoredFieldsReader storedFieldsReader = mergeState.storedFieldsReaders[readerIndex];
                if (storedFieldsReader != null) {
                    storedFieldsReader.checkIntegrity();
                }
                for (int docID = 0; docID < maxDoc; ++docID) {
                    if (liveDocs == null || liveDocs.get(docID)) {
                        this.startDocument();
                        storedFieldsReader.visitDocument(docID, visitor);
                        this.finishDocument();
                        ++docCount;
                    }
                }
            }
            else if (matchingFieldsReader.getCompressionMode() == this.compressionMode && matchingFieldsReader.getChunkSize() == this.chunkSize && matchingFieldsReader.getPackedIntsVersion() == 2 && liveDocs == null && !this.tooDirty(matchingFieldsReader)) {
                assert matchingFieldsReader.getVersion() == 1;
                matchingFieldsReader.checkIntegrity();
                if (this.numBufferedDocs > 0) {
                    this.flush();
                    ++this.numDirtyChunks;
                }
                final IndexInput rawDocs = matchingFieldsReader.getFieldsStream();
                final CompressingStoredFieldsIndexReader index = matchingFieldsReader.getIndexReader();
                rawDocs.seek(index.getStartPointer(0));
                int docID2 = 0;
                while (docID2 < maxDoc) {
                    final int base = rawDocs.readVInt();
                    if (base != docID2) {
                        throw new CorruptIndexException("invalid state: base=" + base + ", docID=" + docID2, rawDocs);
                    }
                    final int code = rawDocs.readVInt();
                    final int bufferedDocs = code >>> 1;
                    this.indexWriter.writeIndex(bufferedDocs, this.fieldsStream.getFilePointer());
                    this.fieldsStream.writeVInt(this.docBase);
                    this.fieldsStream.writeVInt(code);
                    docID2 += bufferedDocs;
                    this.docBase += bufferedDocs;
                    docCount += bufferedDocs;
                    if (docID2 > maxDoc) {
                        throw new CorruptIndexException("invalid state: base=" + base + ", count=" + bufferedDocs + ", maxDoc=" + maxDoc, rawDocs);
                    }
                    long end;
                    if (docID2 == maxDoc) {
                        end = matchingFieldsReader.getMaxPointer();
                    }
                    else {
                        end = index.getStartPointer(docID2);
                    }
                    this.fieldsStream.copyBytes(rawDocs, end - rawDocs.getFilePointer());
                }
                if (rawDocs.getFilePointer() != matchingFieldsReader.getMaxPointer()) {
                    throw new CorruptIndexException("invalid state: pos=" + rawDocs.getFilePointer() + ", max=" + matchingFieldsReader.getMaxPointer(), rawDocs);
                }
                this.numChunks += matchingFieldsReader.getNumChunks();
                this.numDirtyChunks += matchingFieldsReader.getNumDirtyChunks();
            }
            else {
                assert matchingFieldsReader.getVersion() == 1;
                matchingFieldsReader.checkIntegrity();
                for (int docID3 = 0; docID3 < maxDoc; ++docID3) {
                    if (liveDocs == null || liveDocs.get(docID3)) {
                        final CompressingStoredFieldsReader.SerializedDocument doc = matchingFieldsReader.document(docID3);
                        this.startDocument();
                        this.bufferedDocs.copyBytes(doc.in, doc.length);
                        this.numStoredFieldsInDoc = doc.numStoredFields;
                        this.finishDocument();
                        ++docCount;
                    }
                }
            }
        }
        this.finish(mergeState.mergeFieldInfos, docCount);
        return docCount;
    }
    
    boolean tooDirty(final CompressingStoredFieldsReader candidate) {
        return candidate.getNumDirtyChunks() > 1024L || candidate.getNumDirtyChunks() * 100L > candidate.getNumChunks();
    }
    
    static {
        TYPE_BITS = PackedInts.bitsRequired(5L);
        TYPE_MASK = (int)PackedInts.maxValue(CompressingStoredFieldsWriter.TYPE_BITS);
        NEGATIVE_ZERO_FLOAT = Float.floatToIntBits(-0.0f);
        NEGATIVE_ZERO_DOUBLE = Double.doubleToLongBits(-0.0);
        BULK_MERGE_ENABLED_SYSPROP = CompressingStoredFieldsWriter.class.getName() + ".enableBulkMerge";
        boolean v = true;
        try {
            v = Boolean.parseBoolean(System.getProperty(CompressingStoredFieldsWriter.BULK_MERGE_ENABLED_SYSPROP, "true"));
        }
        catch (final SecurityException ex) {}
        BULK_MERGE_ENABLED = v;
    }
}
