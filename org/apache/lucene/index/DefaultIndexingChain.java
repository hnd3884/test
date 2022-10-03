package org.apache.lucene.index;

import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.BytesRefHash;
import org.apache.lucene.util.AttributeSource;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.util.ArrayUtil;
import org.apache.lucene.util.RamUsageEstimator;
import java.util.Arrays;
import java.util.Iterator;
import org.apache.lucene.codecs.NormsFormat;
import org.apache.lucene.codecs.NormsConsumer;
import org.apache.lucene.codecs.DocValuesFormat;
import org.apache.lucene.codecs.DocValuesConsumer;
import org.apache.lucene.util.IOUtils;
import java.io.Closeable;
import java.util.Map;
import java.util.HashMap;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.IOContext;
import java.io.IOException;
import org.apache.lucene.codecs.StoredFieldsWriter;
import org.apache.lucene.util.Counter;

final class DefaultIndexingChain extends DocConsumer
{
    final Counter bytesUsed;
    final DocumentsWriterPerThread.DocState docState;
    final DocumentsWriterPerThread docWriter;
    final FieldInfos.Builder fieldInfos;
    final TermsHash termsHash;
    private StoredFieldsWriter storedFieldsWriter;
    private int lastStoredDocID;
    private PerField[] fieldHash;
    private int hashMask;
    private int totalFieldCount;
    private long nextFieldGen;
    private PerField[] fields;
    
    public DefaultIndexingChain(final DocumentsWriterPerThread docWriter) throws IOException {
        this.fieldHash = new PerField[2];
        this.hashMask = 1;
        this.fields = new PerField[1];
        this.docWriter = docWriter;
        this.fieldInfos = docWriter.getFieldInfosBuilder();
        this.docState = docWriter.docState;
        this.bytesUsed = docWriter.bytesUsed;
        final TermsHash termVectorsWriter = new TermVectorsConsumer(docWriter);
        this.termsHash = new FreqProxTermsWriter(docWriter, termVectorsWriter);
    }
    
    private void initStoredFieldsWriter() throws IOException {
        if (this.storedFieldsWriter == null) {
            this.storedFieldsWriter = this.docWriter.codec.storedFieldsFormat().fieldsWriter(this.docWriter.directory, this.docWriter.getSegmentInfo(), IOContext.DEFAULT);
        }
    }
    
    public void flush(final SegmentWriteState state) throws IOException, AbortingException {
        final int maxDoc = state.segmentInfo.maxDoc();
        long t0 = System.nanoTime();
        this.writeNorms(state);
        if (this.docState.infoStream.isEnabled("IW")) {
            this.docState.infoStream.message("IW", (System.nanoTime() - t0) / 1000000L + " msec to write norms");
        }
        t0 = System.nanoTime();
        this.writeDocValues(state);
        if (this.docState.infoStream.isEnabled("IW")) {
            this.docState.infoStream.message("IW", (System.nanoTime() - t0) / 1000000L + " msec to write docValues");
        }
        t0 = System.nanoTime();
        this.initStoredFieldsWriter();
        this.fillStoredFields(maxDoc);
        this.storedFieldsWriter.finish(state.fieldInfos, maxDoc);
        this.storedFieldsWriter.close();
        if (this.docState.infoStream.isEnabled("IW")) {
            this.docState.infoStream.message("IW", (System.nanoTime() - t0) / 1000000L + " msec to finish stored fields");
        }
        t0 = System.nanoTime();
        final Map<String, TermsHashPerField> fieldsToFlush = new HashMap<String, TermsHashPerField>();
        for (int i = 0; i < this.fieldHash.length; ++i) {
            for (PerField perField = this.fieldHash[i]; perField != null; perField = perField.next) {
                if (perField.invertState != null) {
                    fieldsToFlush.put(perField.fieldInfo.name, perField.termsHashPerField);
                }
            }
        }
        this.termsHash.flush(fieldsToFlush, state);
        if (this.docState.infoStream.isEnabled("IW")) {
            this.docState.infoStream.message("IW", (System.nanoTime() - t0) / 1000000L + " msec to write postings and finish vectors");
        }
        t0 = System.nanoTime();
        this.docWriter.codec.fieldInfosFormat().write(state.directory, state.segmentInfo, "", state.fieldInfos, IOContext.DEFAULT);
        if (this.docState.infoStream.isEnabled("IW")) {
            this.docState.infoStream.message("IW", (System.nanoTime() - t0) / 1000000L + " msec to write fieldInfos");
        }
    }
    
    private void writeDocValues(final SegmentWriteState state) throws IOException {
        final int maxDoc = state.segmentInfo.maxDoc();
        DocValuesConsumer dvConsumer = null;
        boolean success = false;
        try {
            for (int i = 0; i < this.fieldHash.length; ++i) {
                for (PerField perField = this.fieldHash[i]; perField != null; perField = perField.next) {
                    if (perField.docValuesWriter != null) {
                        if (perField.fieldInfo.getDocValuesType() == DocValuesType.NONE) {
                            throw new AssertionError((Object)("segment=" + state.segmentInfo + ": field=\"" + perField.fieldInfo.name + "\" has no docValues but wrote them"));
                        }
                        if (dvConsumer == null) {
                            final DocValuesFormat fmt = state.segmentInfo.getCodec().docValuesFormat();
                            dvConsumer = fmt.fieldsConsumer(state);
                        }
                        perField.docValuesWriter.finish(maxDoc);
                        perField.docValuesWriter.flush(state, dvConsumer);
                        perField.docValuesWriter = null;
                    }
                    else if (perField.fieldInfo.getDocValuesType() != DocValuesType.NONE) {
                        throw new AssertionError((Object)("segment=" + state.segmentInfo + ": field=\"" + perField.fieldInfo.name + "\" has docValues but did not write them"));
                    }
                }
            }
            success = true;
        }
        finally {
            if (success) {
                IOUtils.close(dvConsumer);
            }
            else {
                IOUtils.closeWhileHandlingException(dvConsumer);
            }
        }
        if (!state.fieldInfos.hasDocValues()) {
            if (dvConsumer != null) {
                throw new AssertionError((Object)("segment=" + state.segmentInfo + ": fieldInfos has no docValues but wrote them"));
            }
        }
        else if (dvConsumer == null) {
            throw new AssertionError((Object)("segment=" + state.segmentInfo + ": fieldInfos has docValues but did not wrote them"));
        }
    }
    
    private void fillStoredFields(final int docID) throws IOException, AbortingException {
        while (this.lastStoredDocID < docID) {
            this.startStoredFields();
            this.finishStoredFields();
        }
    }
    
    private void writeNorms(final SegmentWriteState state) throws IOException {
        boolean success = false;
        NormsConsumer normsConsumer = null;
        try {
            if (state.fieldInfos.hasNorms()) {
                final NormsFormat normsFormat = state.segmentInfo.getCodec().normsFormat();
                assert normsFormat != null;
                normsConsumer = normsFormat.normsConsumer(state);
                for (final FieldInfo fi : state.fieldInfos) {
                    final PerField perField = this.getPerField(fi.name);
                    assert perField != null;
                    if (fi.omitsNorms() || fi.getIndexOptions() == IndexOptions.NONE) {
                        continue;
                    }
                    assert perField.norms != null : "field=" + fi.name;
                    perField.norms.finish(state.segmentInfo.maxDoc());
                    perField.norms.flush(state, normsConsumer);
                }
            }
            success = true;
        }
        finally {
            if (success) {
                IOUtils.close(normsConsumer);
            }
            else {
                IOUtils.closeWhileHandlingException(normsConsumer);
            }
        }
    }
    
    public void abort() {
        IOUtils.closeWhileHandlingException(this.storedFieldsWriter);
        try {
            this.termsHash.abort();
        }
        catch (final Throwable t) {}
        Arrays.fill(this.fieldHash, null);
    }
    
    private void rehash() {
        final int newHashSize = this.fieldHash.length * 2;
        assert newHashSize > this.fieldHash.length;
        final PerField[] newHashArray = new PerField[newHashSize];
        final int newHashMask = newHashSize - 1;
        for (int j = 0; j < this.fieldHash.length; ++j) {
            PerField nextFP0;
            for (PerField fp0 = this.fieldHash[j]; fp0 != null; fp0 = nextFP0) {
                final int hashPos2 = fp0.fieldInfo.name.hashCode() & newHashMask;
                nextFP0 = fp0.next;
                fp0.next = newHashArray[hashPos2];
                newHashArray[hashPos2] = fp0;
            }
        }
        this.fieldHash = newHashArray;
        this.hashMask = newHashMask;
    }
    
    private void startStoredFields() throws IOException, AbortingException {
        try {
            this.initStoredFieldsWriter();
            this.storedFieldsWriter.startDocument();
        }
        catch (final Throwable th) {
            throw AbortingException.wrap(th);
        }
        ++this.lastStoredDocID;
    }
    
    private void finishStoredFields() throws IOException, AbortingException {
        try {
            this.storedFieldsWriter.finishDocument();
        }
        catch (final Throwable th) {
            throw AbortingException.wrap(th);
        }
    }
    
    public void processDocument() throws IOException, AbortingException {
        int fieldCount = 0;
        final long fieldGen = this.nextFieldGen++;
        this.termsHash.startDocument();
        this.fillStoredFields(this.docState.docID);
        this.startStoredFields();
        boolean aborting = false;
        try {
            for (final IndexableField field : this.docState.doc) {
                fieldCount = this.processField(field, fieldGen, fieldCount);
            }
        }
        catch (final AbortingException ae) {
            aborting = true;
            throw ae;
        }
        finally {
            if (!aborting) {
                for (int i = 0; i < fieldCount; ++i) {
                    this.fields[i].finish();
                }
                this.finishStoredFields();
            }
        }
        try {
            this.termsHash.finishDocument();
        }
        catch (final Throwable th) {
            throw AbortingException.wrap(th);
        }
    }
    
    private int processField(final IndexableField field, final long fieldGen, int fieldCount) throws IOException, AbortingException {
        final String fieldName = field.name();
        final IndexableFieldType fieldType = field.fieldType();
        PerField fp = null;
        if (fieldType.indexOptions() == null) {
            throw new NullPointerException("IndexOptions must not be null (field: \"" + field.name() + "\")");
        }
        if (fieldType.indexOptions() != IndexOptions.NONE) {
            if (fieldType.omitNorms() && field.boost() != 1.0f) {
                throw new UnsupportedOperationException("You cannot set an index-time boost: norms are omitted for field '" + field.name() + "'");
            }
            fp = this.getOrAddField(fieldName, fieldType, true);
            final boolean first = fp.fieldGen != fieldGen;
            fp.invert(field, first);
            if (first) {
                this.fields[fieldCount++] = fp;
                fp.fieldGen = fieldGen;
            }
        }
        else {
            verifyUnIndexedFieldType(fieldName, fieldType);
        }
        if (fieldType.stored()) {
            if (fp == null) {
                fp = this.getOrAddField(fieldName, fieldType, false);
            }
            if (fieldType.stored()) {
                try {
                    this.storedFieldsWriter.writeField(fp.fieldInfo, field);
                }
                catch (final Throwable th) {
                    throw AbortingException.wrap(th);
                }
            }
        }
        final DocValuesType dvType = fieldType.docValuesType();
        if (dvType == null) {
            throw new NullPointerException("docValuesType cannot be null (field: \"" + fieldName + "\")");
        }
        if (dvType != DocValuesType.NONE) {
            if (fp == null) {
                fp = this.getOrAddField(fieldName, fieldType, false);
            }
            this.indexDocValue(fp, dvType, field);
        }
        return fieldCount;
    }
    
    private static void verifyUnIndexedFieldType(final String name, final IndexableFieldType ft) {
        if (ft.storeTermVectors()) {
            throw new IllegalArgumentException("cannot store term vectors for a field that is not indexed (field=\"" + name + "\")");
        }
        if (ft.storeTermVectorPositions()) {
            throw new IllegalArgumentException("cannot store term vector positions for a field that is not indexed (field=\"" + name + "\")");
        }
        if (ft.storeTermVectorOffsets()) {
            throw new IllegalArgumentException("cannot store term vector offsets for a field that is not indexed (field=\"" + name + "\")");
        }
        if (ft.storeTermVectorPayloads()) {
            throw new IllegalArgumentException("cannot store term vector payloads for a field that is not indexed (field=\"" + name + "\")");
        }
    }
    
    private void indexDocValue(final PerField fp, final DocValuesType dvType, final IndexableField field) throws IOException {
        if (fp.fieldInfo.getDocValuesType() == DocValuesType.NONE) {
            this.fieldInfos.globalFieldNumbers.setDocValuesType(fp.fieldInfo.number, fp.fieldInfo.name, dvType);
        }
        fp.fieldInfo.setDocValuesType(dvType);
        final int docID = this.docState.docID;
        switch (dvType) {
            case NUMERIC: {
                if (fp.docValuesWriter == null) {
                    fp.docValuesWriter = new NumericDocValuesWriter(fp.fieldInfo, this.bytesUsed);
                }
                ((NumericDocValuesWriter)fp.docValuesWriter).addValue(docID, field.numericValue().longValue());
                break;
            }
            case BINARY: {
                if (fp.docValuesWriter == null) {
                    fp.docValuesWriter = new BinaryDocValuesWriter(fp.fieldInfo, this.bytesUsed);
                }
                ((BinaryDocValuesWriter)fp.docValuesWriter).addValue(docID, field.binaryValue());
                break;
            }
            case SORTED: {
                if (fp.docValuesWriter == null) {
                    fp.docValuesWriter = new SortedDocValuesWriter(fp.fieldInfo, this.bytesUsed);
                }
                ((SortedDocValuesWriter)fp.docValuesWriter).addValue(docID, field.binaryValue());
                break;
            }
            case SORTED_NUMERIC: {
                if (fp.docValuesWriter == null) {
                    fp.docValuesWriter = new SortedNumericDocValuesWriter(fp.fieldInfo, this.bytesUsed);
                }
                ((SortedNumericDocValuesWriter)fp.docValuesWriter).addValue(docID, field.numericValue().longValue());
                break;
            }
            case SORTED_SET: {
                if (fp.docValuesWriter == null) {
                    fp.docValuesWriter = new SortedSetDocValuesWriter(fp.fieldInfo, this.bytesUsed);
                }
                ((SortedSetDocValuesWriter)fp.docValuesWriter).addValue(docID, field.binaryValue());
                break;
            }
            default: {
                throw new AssertionError((Object)("unrecognized DocValues.Type: " + dvType));
            }
        }
    }
    
    private PerField getPerField(final String name) {
        final int hashPos = name.hashCode() & this.hashMask;
        PerField fp;
        for (fp = this.fieldHash[hashPos]; fp != null && !fp.fieldInfo.name.equals(name); fp = fp.next) {}
        return fp;
    }
    
    private PerField getOrAddField(final String name, final IndexableFieldType fieldType, final boolean invert) {
        final int hashPos = name.hashCode() & this.hashMask;
        PerField fp;
        for (fp = this.fieldHash[hashPos]; fp != null && !fp.fieldInfo.name.equals(name); fp = fp.next) {}
        if (fp == null) {
            final FieldInfo fi = this.fieldInfos.getOrAdd(name);
            fi.setIndexOptions(fieldType.indexOptions());
            fp = new PerField(fi, invert);
            fp.next = this.fieldHash[hashPos];
            this.fieldHash[hashPos] = fp;
            ++this.totalFieldCount;
            if (this.totalFieldCount >= this.fieldHash.length / 2) {
                this.rehash();
            }
            if (this.totalFieldCount > this.fields.length) {
                final PerField[] newFields = new PerField[ArrayUtil.oversize(this.totalFieldCount, RamUsageEstimator.NUM_BYTES_OBJECT_REF)];
                System.arraycopy(this.fields, 0, newFields, 0, this.fields.length);
                this.fields = newFields;
            }
        }
        else if (invert && fp.invertState == null) {
            fp.fieldInfo.setIndexOptions(fieldType.indexOptions());
            fp.setInvertState();
        }
        return fp;
    }
    
    private final class PerField implements Comparable<PerField>
    {
        final FieldInfo fieldInfo;
        final Similarity similarity;
        FieldInvertState invertState;
        TermsHashPerField termsHashPerField;
        DocValuesWriter docValuesWriter;
        long fieldGen;
        PerField next;
        NormValuesWriter norms;
        TokenStream tokenStream;
        IndexOptions indexOptions;
        
        public PerField(final FieldInfo fieldInfo, final boolean invert) {
            this.fieldGen = -1L;
            this.fieldInfo = fieldInfo;
            this.similarity = DefaultIndexingChain.this.docState.similarity;
            if (invert) {
                this.setInvertState();
            }
        }
        
        void setInvertState() {
            this.invertState = new FieldInvertState(this.fieldInfo.name);
            this.termsHashPerField = DefaultIndexingChain.this.termsHash.addField(this.invertState, this.fieldInfo);
            if (!this.fieldInfo.omitsNorms()) {
                assert this.norms == null;
                this.norms = new NormValuesWriter(this.fieldInfo, DefaultIndexingChain.this.docState.docWriter.bytesUsed);
            }
        }
        
        @Override
        public int compareTo(final PerField other) {
            return this.fieldInfo.name.compareTo(other.fieldInfo.name);
        }
        
        public void finish() throws IOException {
            if (!this.fieldInfo.omitsNorms() && this.invertState.length != 0) {
                this.norms.addValue(DefaultIndexingChain.this.docState.docID, this.similarity.computeNorm(this.invertState));
            }
            this.termsHashPerField.finish();
        }
        
        public void invert(final IndexableField field, final boolean first) throws IOException, AbortingException {
            if (first) {
                this.invertState.reset();
            }
            final IndexableFieldType fieldType = field.fieldType();
            final IndexOptions indexOptions = fieldType.indexOptions();
            this.fieldInfo.setIndexOptions(indexOptions);
            if (fieldType.omitNorms()) {
                this.fieldInfo.setOmitsNorms();
            }
            final boolean analyzed = fieldType.tokenized() && DefaultIndexingChain.this.docState.analyzer != null;
            final boolean checkOffsets = indexOptions == IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS;
            boolean succeededInProcessingField = false;
            try {
                final TokenStream tokenStream = field.tokenStream(DefaultIndexingChain.this.docState.analyzer, this.tokenStream);
                this.tokenStream = tokenStream;
                try (final TokenStream stream = tokenStream) {
                    stream.reset();
                    this.invertState.setAttributeSource(stream);
                    this.termsHashPerField.start(field, first);
                    while (stream.incrementToken()) {
                        final int posIncr = this.invertState.posIncrAttribute.getPositionIncrement();
                        final FieldInvertState invertState = this.invertState;
                        invertState.position += posIncr;
                        if (this.invertState.position < this.invertState.lastPosition) {
                            if (posIncr == 0) {
                                throw new IllegalArgumentException("first position increment must be > 0 (got 0) for field '" + field.name() + "'");
                            }
                            throw new IllegalArgumentException("position increments (and gaps) must be >= 0 (got " + posIncr + ") for field '" + field.name() + "'");
                        }
                        else {
                            if (this.invertState.position > 2147483519) {
                                throw new IllegalArgumentException("position " + this.invertState.position + " is too large for field '" + field.name() + "': max allowed position is " + 2147483519);
                            }
                            this.invertState.lastPosition = this.invertState.position;
                            if (posIncr == 0) {
                                final FieldInvertState invertState2 = this.invertState;
                                ++invertState2.numOverlap;
                            }
                            if (checkOffsets) {
                                final int startOffset = this.invertState.offset + this.invertState.offsetAttribute.startOffset();
                                final int endOffset = this.invertState.offset + this.invertState.offsetAttribute.endOffset();
                                if (startOffset < this.invertState.lastStartOffset || endOffset < startOffset) {
                                    throw new IllegalArgumentException("startOffset must be non-negative, and endOffset must be >= startOffset, and offsets must not go backwards startOffset=" + startOffset + ",endOffset=" + endOffset + ",lastStartOffset=" + this.invertState.lastStartOffset + " for field '" + field.name() + "'");
                                }
                                this.invertState.lastStartOffset = startOffset;
                            }
                            final FieldInvertState invertState3 = this.invertState;
                            ++invertState3.length;
                            if (this.invertState.length < 0) {
                                throw new IllegalArgumentException("too many tokens in field '" + field.name() + "'");
                            }
                            try {
                                this.termsHashPerField.add();
                            }
                            catch (final BytesRefHash.MaxBytesLengthExceededException e) {
                                final byte[] prefix = new byte[30];
                                final BytesRef bigTerm = this.invertState.termAttribute.getBytesRef();
                                System.arraycopy(bigTerm.bytes, bigTerm.offset, prefix, 0, 30);
                                final String msg = "Document contains at least one immense term in field=\"" + this.fieldInfo.name + "\" (whose UTF8 encoding is longer than the max length " + 32766 + "), all of which were skipped.  Please correct the analyzer to not produce such terms.  The prefix of the first immense term is: '" + Arrays.toString(prefix) + "...', original message: " + e.getMessage();
                                if (DefaultIndexingChain.this.docState.infoStream.isEnabled("IW")) {
                                    DefaultIndexingChain.this.docState.infoStream.message("IW", "ERROR: " + msg);
                                }
                                throw new IllegalArgumentException(msg, e);
                            }
                            catch (final Throwable th) {
                                throw AbortingException.wrap(th);
                            }
                        }
                    }
                    stream.end();
                    final FieldInvertState invertState4 = this.invertState;
                    invertState4.position += this.invertState.posIncrAttribute.getPositionIncrement();
                    final FieldInvertState invertState5 = this.invertState;
                    invertState5.offset += this.invertState.offsetAttribute.endOffset();
                    succeededInProcessingField = true;
                }
            }
            finally {
                if (!succeededInProcessingField && DefaultIndexingChain.this.docState.infoStream.isEnabled("DW")) {
                    DefaultIndexingChain.this.docState.infoStream.message("DW", "An exception was thrown while processing field " + this.fieldInfo.name);
                }
            }
            if (analyzed) {
                final FieldInvertState invertState6 = this.invertState;
                invertState6.position += DefaultIndexingChain.this.docState.analyzer.getPositionIncrementGap(this.fieldInfo.name);
                final FieldInvertState invertState7 = this.invertState;
                invertState7.offset += DefaultIndexingChain.this.docState.analyzer.getOffsetGap(this.fieldInfo.name);
            }
            final FieldInvertState invertState8 = this.invertState;
            invertState8.boost *= field.boost();
        }
    }
}
