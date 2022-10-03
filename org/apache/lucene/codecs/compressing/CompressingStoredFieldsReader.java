package org.apache.lucene.codecs.compressing;

import java.io.EOFException;
import org.apache.lucene.store.ByteArrayDataInput;
import org.apache.lucene.util.packed.PackedInts;
import java.util.Arrays;
import org.apache.lucene.util.ArrayUtil;
import org.apache.lucene.util.IntsRef;
import org.apache.lucene.util.BytesRef;
import java.util.Collections;
import org.apache.lucene.util.Accountables;
import org.apache.lucene.util.Accountable;
import java.util.Collection;
import org.apache.lucene.util.BitUtil;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.StoredFieldVisitor;
import org.apache.lucene.store.AlreadyClosedException;
import java.io.IOException;
import org.apache.lucene.store.ChecksumIndexInput;
import org.apache.lucene.util.IOUtils;
import java.io.Closeable;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.store.DataInput;
import org.apache.lucene.codecs.CodecUtil;
import org.apache.lucene.index.IndexFileNames;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.index.SegmentInfo;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.index.FieldInfos;
import org.apache.lucene.codecs.StoredFieldsReader;

public final class CompressingStoredFieldsReader extends StoredFieldsReader
{
    private final int version;
    private final FieldInfos fieldInfos;
    private final CompressingStoredFieldsIndexReader indexReader;
    private final long maxPointer;
    private final IndexInput fieldsStream;
    private final int chunkSize;
    private final int packedIntsVersion;
    private final CompressionMode compressionMode;
    private final Decompressor decompressor;
    private final int numDocs;
    private final boolean merging;
    private final BlockState state;
    private final long numChunks;
    private final long numDirtyChunks;
    private boolean closed;
    
    private CompressingStoredFieldsReader(final CompressingStoredFieldsReader reader, final boolean merging) {
        this.version = reader.version;
        this.fieldInfos = reader.fieldInfos;
        this.fieldsStream = reader.fieldsStream.clone();
        this.indexReader = reader.indexReader.clone();
        this.maxPointer = reader.maxPointer;
        this.chunkSize = reader.chunkSize;
        this.packedIntsVersion = reader.packedIntsVersion;
        this.compressionMode = reader.compressionMode;
        this.decompressor = reader.decompressor.clone();
        this.numDocs = reader.numDocs;
        this.numChunks = reader.numChunks;
        this.numDirtyChunks = reader.numDirtyChunks;
        this.merging = merging;
        this.state = new BlockState();
        this.closed = false;
    }
    
    public CompressingStoredFieldsReader(final Directory d, final SegmentInfo si, final String segmentSuffix, final FieldInfos fn, final IOContext context, final String formatName, final CompressionMode compressionMode) throws IOException {
        this.compressionMode = compressionMode;
        final String segment = si.name;
        boolean success = false;
        this.fieldInfos = fn;
        this.numDocs = si.maxDoc();
        int version = -1;
        long maxPointer = -1L;
        CompressingStoredFieldsIndexReader indexReader = null;
        final String indexName = IndexFileNames.segmentFileName(segment, segmentSuffix, "fdx");
        try (final ChecksumIndexInput indexStream = d.openChecksumInput(indexName, context)) {
            Throwable priorE = null;
            try {
                final String codecNameIdx = formatName + "Index";
                version = CodecUtil.checkIndexHeader(indexStream, codecNameIdx, 0, 1, si.getId(), segmentSuffix);
                assert CodecUtil.indexHeaderLength(codecNameIdx, segmentSuffix) == indexStream.getFilePointer();
                indexReader = new CompressingStoredFieldsIndexReader(indexStream, si);
                maxPointer = indexStream.readVLong();
            }
            catch (final Throwable exception) {
                priorE = exception;
            }
            finally {
                CodecUtil.checkFooter(indexStream, priorE);
            }
        }
        this.version = version;
        this.maxPointer = maxPointer;
        this.indexReader = indexReader;
        final String fieldsStreamFN = IndexFileNames.segmentFileName(segment, segmentSuffix, "fdt");
        try {
            this.fieldsStream = d.openInput(fieldsStreamFN, context);
            final String codecNameDat = formatName + "Data";
            final int fieldsVersion = CodecUtil.checkIndexHeader(this.fieldsStream, codecNameDat, 0, 1, si.getId(), segmentSuffix);
            if (version != fieldsVersion) {
                throw new CorruptIndexException("Version mismatch between stored fields index and data: " + version + " != " + fieldsVersion, this.fieldsStream);
            }
            assert CodecUtil.indexHeaderLength(codecNameDat, segmentSuffix) == this.fieldsStream.getFilePointer();
            this.chunkSize = this.fieldsStream.readVInt();
            this.packedIntsVersion = this.fieldsStream.readVInt();
            this.decompressor = compressionMode.newDecompressor();
            this.merging = false;
            this.state = new BlockState();
            if (version >= 1) {
                this.fieldsStream.seek(maxPointer);
                this.numChunks = this.fieldsStream.readVLong();
                this.numDirtyChunks = this.fieldsStream.readVLong();
                if (this.numDirtyChunks > this.numChunks) {
                    throw new CorruptIndexException("invalid chunk counts: dirty=" + this.numDirtyChunks + ", total=" + this.numChunks, this.fieldsStream);
                }
            }
            else {
                final long n = -1L;
                this.numDirtyChunks = n;
                this.numChunks = n;
            }
            CodecUtil.retrieveChecksum(this.fieldsStream);
            success = true;
        }
        finally {
            if (!success) {
                IOUtils.closeWhileHandlingException(this);
            }
        }
    }
    
    private void ensureOpen() throws AlreadyClosedException {
        if (this.closed) {
            throw new AlreadyClosedException("this FieldsReader is closed");
        }
    }
    
    @Override
    public void close() throws IOException {
        if (!this.closed) {
            IOUtils.close(this.fieldsStream);
            this.closed = true;
        }
    }
    
    private static void readField(final DataInput in, final StoredFieldVisitor visitor, final FieldInfo info, final int bits) throws IOException {
        switch (bits & CompressingStoredFieldsWriter.TYPE_MASK) {
            case 1: {
                final int length = in.readVInt();
                final byte[] data = new byte[length];
                in.readBytes(data, 0, length);
                visitor.binaryField(info, data);
                break;
            }
            case 0: {
                final int length = in.readVInt();
                final byte[] data = new byte[length];
                in.readBytes(data, 0, length);
                visitor.stringField(info, data);
                break;
            }
            case 2: {
                visitor.intField(info, in.readZInt());
                break;
            }
            case 3: {
                visitor.floatField(info, readZFloat(in));
                break;
            }
            case 4: {
                visitor.longField(info, readTLong(in));
                break;
            }
            case 5: {
                visitor.doubleField(info, readZDouble(in));
                break;
            }
            default: {
                throw new AssertionError((Object)("Unknown type flag: " + Integer.toHexString(bits)));
            }
        }
    }
    
    private static void skipField(final DataInput in, final int bits) throws IOException {
        switch (bits & CompressingStoredFieldsWriter.TYPE_MASK) {
            case 0:
            case 1: {
                final int length = in.readVInt();
                in.skipBytes(length);
                break;
            }
            case 2: {
                in.readZInt();
                break;
            }
            case 3: {
                readZFloat(in);
                break;
            }
            case 4: {
                readTLong(in);
                break;
            }
            case 5: {
                readZDouble(in);
                break;
            }
            default: {
                throw new AssertionError((Object)("Unknown type flag: " + Integer.toHexString(bits)));
            }
        }
    }
    
    static float readZFloat(final DataInput in) throws IOException {
        final int b = in.readByte() & 0xFF;
        if (b == 255) {
            return Float.intBitsToFloat(in.readInt());
        }
        if ((b & 0x80) != 0x0) {
            return (float)((b & 0x7F) - 1);
        }
        final int bits = b << 24 | (in.readShort() & 0xFFFF) << 8 | (in.readByte() & 0xFF);
        return Float.intBitsToFloat(bits);
    }
    
    static double readZDouble(final DataInput in) throws IOException {
        final int b = in.readByte() & 0xFF;
        if (b == 255) {
            return Double.longBitsToDouble(in.readLong());
        }
        if (b == 254) {
            return Float.intBitsToFloat(in.readInt());
        }
        if ((b & 0x80) != 0x0) {
            return (b & 0x7F) - 1;
        }
        final long bits = (long)b << 56 | ((long)in.readInt() & 0xFFFFFFFFL) << 24 | ((long)in.readShort() & 0xFFFFL) << 8 | ((long)in.readByte() & 0xFFL);
        return Double.longBitsToDouble(bits);
    }
    
    static long readTLong(final DataInput in) throws IOException {
        final int header = in.readByte() & 0xFF;
        long bits = header & 0x1F;
        if ((header & 0x20) != 0x0) {
            bits |= in.readVLong() << 5;
        }
        long l = BitUtil.zigZagDecode(bits);
        switch (header & 0xC0) {
            case 64: {
                l *= 1000L;
                break;
            }
            case 128: {
                l *= 3600000L;
                break;
            }
            case 192: {
                l *= 86400000L;
                break;
            }
            case 0: {
                break;
            }
            default: {
                throw new AssertionError();
            }
        }
        return l;
    }
    
    SerializedDocument document(final int docID) throws IOException {
        if (!this.state.contains(docID)) {
            this.fieldsStream.seek(this.indexReader.getStartPointer(docID));
            this.state.reset(docID);
        }
        assert this.state.contains(docID);
        return this.state.document(docID);
    }
    
    @Override
    public void visitDocument(final int docID, final StoredFieldVisitor visitor) throws IOException {
        final SerializedDocument doc = this.document(docID);
        for (int fieldIDX = 0; fieldIDX < doc.numStoredFields; ++fieldIDX) {
            final long infoAndBits = doc.in.readVLong();
            final int fieldNumber = (int)(infoAndBits >>> CompressingStoredFieldsWriter.TYPE_BITS);
            final FieldInfo fieldInfo = this.fieldInfos.fieldInfo(fieldNumber);
            final int bits = (int)(infoAndBits & (long)CompressingStoredFieldsWriter.TYPE_MASK);
            assert bits <= 5 : "bits=" + Integer.toHexString(bits);
            switch (visitor.needsField(fieldInfo)) {
                case YES: {
                    readField(doc.in, visitor, fieldInfo, bits);
                    break;
                }
                case NO: {
                    if (fieldIDX == doc.numStoredFields - 1) {
                        return;
                    }
                    skipField(doc.in, bits);
                    break;
                }
                case STOP: {
                    return;
                }
            }
        }
    }
    
    @Override
    public StoredFieldsReader clone() {
        this.ensureOpen();
        return new CompressingStoredFieldsReader(this, false);
    }
    
    @Override
    public StoredFieldsReader getMergeInstance() {
        this.ensureOpen();
        return new CompressingStoredFieldsReader(this, true);
    }
    
    int getVersion() {
        return this.version;
    }
    
    CompressionMode getCompressionMode() {
        return this.compressionMode;
    }
    
    CompressingStoredFieldsIndexReader getIndexReader() {
        return this.indexReader;
    }
    
    long getMaxPointer() {
        return this.maxPointer;
    }
    
    IndexInput getFieldsStream() {
        return this.fieldsStream;
    }
    
    int getChunkSize() {
        return this.chunkSize;
    }
    
    long getNumChunks() {
        return this.numChunks;
    }
    
    long getNumDirtyChunks() {
        return this.numDirtyChunks;
    }
    
    int getPackedIntsVersion() {
        return this.packedIntsVersion;
    }
    
    @Override
    public long ramBytesUsed() {
        return this.indexReader.ramBytesUsed();
    }
    
    @Override
    public Collection<Accountable> getChildResources() {
        return Collections.singleton(Accountables.namedAccountable("stored field index", this.indexReader));
    }
    
    @Override
    public void checkIntegrity() throws IOException {
        CodecUtil.checksumEntireFile(this.fieldsStream);
    }
    
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "(mode=" + this.compressionMode + ",chunksize=" + this.chunkSize + ")";
    }
    
    static class SerializedDocument
    {
        final DataInput in;
        final int length;
        final int numStoredFields;
        
        private SerializedDocument(final DataInput in, final int length, final int numStoredFields) {
            this.in = in;
            this.length = length;
            this.numStoredFields = numStoredFields;
        }
    }
    
    private class BlockState
    {
        private int docBase;
        private int chunkDocs;
        private boolean sliced;
        private int[] offsets;
        private int[] numStoredFields;
        private long startPointer;
        private final BytesRef spare;
        private final BytesRef bytes;
        
        private BlockState() {
            this.offsets = IntsRef.EMPTY_INTS;
            this.numStoredFields = IntsRef.EMPTY_INTS;
            this.spare = new BytesRef();
            this.bytes = new BytesRef();
        }
        
        boolean contains(final int docID) {
            return docID >= this.docBase && docID < this.docBase + this.chunkDocs;
        }
        
        void reset(final int docID) throws IOException {
            boolean success = false;
            try {
                this.doReset(docID);
                success = true;
            }
            finally {
                if (!success) {
                    this.chunkDocs = 0;
                }
            }
        }
        
        private void doReset(final int docID) throws IOException {
            this.docBase = CompressingStoredFieldsReader.this.fieldsStream.readVInt();
            final int token = CompressingStoredFieldsReader.this.fieldsStream.readVInt();
            this.chunkDocs = token >>> 1;
            if (!this.contains(docID) || this.docBase + this.chunkDocs > CompressingStoredFieldsReader.this.numDocs) {
                throw new CorruptIndexException("Corrupted: docID=" + docID + ", docBase=" + this.docBase + ", chunkDocs=" + this.chunkDocs + ", numDocs=" + CompressingStoredFieldsReader.this.numDocs, CompressingStoredFieldsReader.this.fieldsStream);
            }
            this.sliced = ((token & 0x1) != 0x0);
            this.offsets = ArrayUtil.grow(this.offsets, this.chunkDocs + 1);
            this.numStoredFields = ArrayUtil.grow(this.numStoredFields, this.chunkDocs);
            if (this.chunkDocs == 1) {
                this.numStoredFields[0] = CompressingStoredFieldsReader.this.fieldsStream.readVInt();
                this.offsets[1] = CompressingStoredFieldsReader.this.fieldsStream.readVInt();
            }
            else {
                final int bitsPerStoredFields = CompressingStoredFieldsReader.this.fieldsStream.readVInt();
                if (bitsPerStoredFields == 0) {
                    Arrays.fill(this.numStoredFields, 0, this.chunkDocs, CompressingStoredFieldsReader.this.fieldsStream.readVInt());
                }
                else {
                    if (bitsPerStoredFields > 31) {
                        throw new CorruptIndexException("bitsPerStoredFields=" + bitsPerStoredFields, CompressingStoredFieldsReader.this.fieldsStream);
                    }
                    final PackedInts.ReaderIterator it = PackedInts.getReaderIteratorNoHeader(CompressingStoredFieldsReader.this.fieldsStream, PackedInts.Format.PACKED, CompressingStoredFieldsReader.this.packedIntsVersion, this.chunkDocs, bitsPerStoredFields, 1);
                    for (int i = 0; i < this.chunkDocs; ++i) {
                        this.numStoredFields[i] = (int)it.next();
                    }
                }
                final int bitsPerLength = CompressingStoredFieldsReader.this.fieldsStream.readVInt();
                if (bitsPerLength == 0) {
                    final int length = CompressingStoredFieldsReader.this.fieldsStream.readVInt();
                    for (int j = 0; j < this.chunkDocs; ++j) {
                        this.offsets[1 + j] = (1 + j) * length;
                    }
                }
                else {
                    if (bitsPerStoredFields > 31) {
                        throw new CorruptIndexException("bitsPerLength=" + bitsPerLength, CompressingStoredFieldsReader.this.fieldsStream);
                    }
                    final PackedInts.ReaderIterator it2 = PackedInts.getReaderIteratorNoHeader(CompressingStoredFieldsReader.this.fieldsStream, PackedInts.Format.PACKED, CompressingStoredFieldsReader.this.packedIntsVersion, this.chunkDocs, bitsPerLength, 1);
                    for (int j = 0; j < this.chunkDocs; ++j) {
                        this.offsets[j + 1] = (int)it2.next();
                    }
                    for (int j = 0; j < this.chunkDocs; ++j) {
                        final int[] offsets = this.offsets;
                        final int n = j + 1;
                        offsets[n] += this.offsets[j];
                    }
                }
                for (int i = 0; i < this.chunkDocs; ++i) {
                    final int len = this.offsets[i + 1] - this.offsets[i];
                    final int storedFields = this.numStoredFields[i];
                    if (len == 0 != (storedFields == 0)) {
                        throw new CorruptIndexException("length=" + len + ", numStoredFields=" + storedFields, CompressingStoredFieldsReader.this.fieldsStream);
                    }
                }
            }
            this.startPointer = CompressingStoredFieldsReader.this.fieldsStream.getFilePointer();
            if (CompressingStoredFieldsReader.this.merging) {
                final int totalLength = this.offsets[this.chunkDocs];
                if (this.sliced) {
                    final BytesRef bytes = this.bytes;
                    final BytesRef bytes2 = this.bytes;
                    final int n2 = 0;
                    bytes2.length = n2;
                    bytes.offset = n2;
                    int toDecompress;
                    for (int decompressed = 0; decompressed < totalLength; decompressed += toDecompress) {
                        toDecompress = Math.min(totalLength - decompressed, CompressingStoredFieldsReader.this.chunkSize);
                        CompressingStoredFieldsReader.this.decompressor.decompress(CompressingStoredFieldsReader.this.fieldsStream, toDecompress, 0, toDecompress, this.spare);
                        this.bytes.bytes = ArrayUtil.grow(this.bytes.bytes, this.bytes.length + this.spare.length);
                        System.arraycopy(this.spare.bytes, this.spare.offset, this.bytes.bytes, this.bytes.length, this.spare.length);
                        final BytesRef bytes3 = this.bytes;
                        bytes3.length += this.spare.length;
                    }
                }
                else {
                    CompressingStoredFieldsReader.this.decompressor.decompress(CompressingStoredFieldsReader.this.fieldsStream, totalLength, 0, totalLength, this.bytes);
                }
                if (this.bytes.length != totalLength) {
                    throw new CorruptIndexException("Corrupted: expected chunk size = " + totalLength + ", got " + this.bytes.length, CompressingStoredFieldsReader.this.fieldsStream);
                }
            }
        }
        
        SerializedDocument document(final int docID) throws IOException {
            if (!this.contains(docID)) {
                throw new IllegalArgumentException();
            }
            final int index = docID - this.docBase;
            final int offset = this.offsets[index];
            final int length = this.offsets[index + 1] - offset;
            final int totalLength = this.offsets[this.chunkDocs];
            final int numStoredFields = this.numStoredFields[index];
            DataInput documentInput;
            if (length == 0) {
                documentInput = new ByteArrayDataInput();
            }
            else if (CompressingStoredFieldsReader.this.merging) {
                documentInput = new ByteArrayDataInput(this.bytes.bytes, this.bytes.offset + offset, length);
            }
            else if (this.sliced) {
                CompressingStoredFieldsReader.this.fieldsStream.seek(this.startPointer);
                CompressingStoredFieldsReader.this.decompressor.decompress(CompressingStoredFieldsReader.this.fieldsStream, CompressingStoredFieldsReader.this.chunkSize, offset, Math.min(length, CompressingStoredFieldsReader.this.chunkSize - offset), this.bytes);
                documentInput = new DataInput() {
                    int decompressed = BlockState.this.bytes.length;
                    
                    void fillBuffer() throws IOException {
                        assert this.decompressed <= length;
                        if (this.decompressed == length) {
                            throw new EOFException();
                        }
                        final int toDecompress = Math.min(length - this.decompressed, CompressingStoredFieldsReader.this.chunkSize);
                        CompressingStoredFieldsReader.this.decompressor.decompress(CompressingStoredFieldsReader.this.fieldsStream, toDecompress, 0, toDecompress, BlockState.this.bytes);
                        this.decompressed += toDecompress;
                    }
                    
                    @Override
                    public byte readByte() throws IOException {
                        if (BlockState.this.bytes.length == 0) {
                            this.fillBuffer();
                        }
                        final BytesRef access$700 = BlockState.this.bytes;
                        --access$700.length;
                        return BlockState.this.bytes.bytes[BlockState.this.bytes.offset++];
                    }
                    
                    @Override
                    public void readBytes(final byte[] b, int offset, int len) throws IOException {
                        while (len > BlockState.this.bytes.length) {
                            System.arraycopy(BlockState.this.bytes.bytes, BlockState.this.bytes.offset, b, offset, BlockState.this.bytes.length);
                            len -= BlockState.this.bytes.length;
                            offset += BlockState.this.bytes.length;
                            this.fillBuffer();
                        }
                        System.arraycopy(BlockState.this.bytes.bytes, BlockState.this.bytes.offset, b, offset, len);
                        final BytesRef access$700 = BlockState.this.bytes;
                        access$700.offset += len;
                        final BytesRef access$701 = BlockState.this.bytes;
                        access$701.length -= len;
                    }
                };
            }
            else {
                CompressingStoredFieldsReader.this.fieldsStream.seek(this.startPointer);
                CompressingStoredFieldsReader.this.decompressor.decompress(CompressingStoredFieldsReader.this.fieldsStream, totalLength, offset, length, this.bytes);
                assert this.bytes.length == length;
                documentInput = new ByteArrayDataInput(this.bytes.bytes, this.bytes.offset, this.bytes.length);
            }
            return new SerializedDocument(documentInput, length, numStoredFields);
        }
    }
}
