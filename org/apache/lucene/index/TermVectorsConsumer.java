package org.apache.lucene.index;

import org.apache.lucene.util.RamUsageEstimator;
import java.util.Arrays;
import org.apache.lucene.util.ArrayUtil;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.store.FlushInfo;
import java.io.IOException;
import org.apache.lucene.util.IOUtils;
import java.io.Closeable;
import java.util.Map;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.codecs.TermVectorsWriter;

final class TermVectorsConsumer extends TermsHash
{
    TermVectorsWriter writer;
    final BytesRef flushTerm;
    final DocumentsWriterPerThread docWriter;
    final ByteSliceReader vectorSliceReaderPos;
    final ByteSliceReader vectorSliceReaderOff;
    boolean hasVectors;
    int numVectorFields;
    int lastDocID;
    private TermVectorsConsumerPerField[] perFields;
    
    public TermVectorsConsumer(final DocumentsWriterPerThread docWriter) {
        super(docWriter, false, null);
        this.flushTerm = new BytesRef();
        this.vectorSliceReaderPos = new ByteSliceReader();
        this.vectorSliceReaderOff = new ByteSliceReader();
        this.perFields = new TermVectorsConsumerPerField[1];
        this.docWriter = docWriter;
    }
    
    @Override
    void flush(final Map<String, TermsHashPerField> fieldsToFlush, final SegmentWriteState state) throws IOException {
        if (this.writer != null) {
            final int numDocs = state.segmentInfo.maxDoc();
            assert numDocs > 0;
            try {
                this.fill(numDocs);
                assert state.segmentInfo != null;
                this.writer.finish(state.fieldInfos, numDocs);
            }
            finally {
                IOUtils.close(this.writer);
                this.writer = null;
                this.lastDocID = 0;
                this.hasVectors = false;
            }
        }
    }
    
    void fill(final int docID) throws IOException {
        while (this.lastDocID < docID) {
            this.writer.startDocument(0);
            this.writer.finishDocument();
            ++this.lastDocID;
        }
    }
    
    private void initTermVectorsWriter() throws IOException {
        if (this.writer == null) {
            final IOContext context = new IOContext(new FlushInfo(this.docWriter.getNumDocsInRAM(), this.docWriter.bytesUsed()));
            this.writer = this.docWriter.codec.termVectorsFormat().vectorsWriter(this.docWriter.directory, this.docWriter.getSegmentInfo(), context);
            this.lastDocID = 0;
        }
    }
    
    @Override
    void finishDocument() throws IOException {
        if (!this.hasVectors) {
            return;
        }
        ArrayUtil.introSort(this.perFields, 0, this.numVectorFields);
        this.initTermVectorsWriter();
        this.fill(this.docState.docID);
        this.writer.startDocument(this.numVectorFields);
        for (int i = 0; i < this.numVectorFields; ++i) {
            this.perFields[i].finishDocument();
        }
        this.writer.finishDocument();
        assert this.lastDocID == this.docState.docID : "lastDocID=" + this.lastDocID + " docState.docID=" + this.docState.docID;
        ++this.lastDocID;
        super.reset();
        this.resetFields();
    }
    
    @Override
    public void abort() {
        this.hasVectors = false;
        try {
            super.abort();
        }
        finally {
            if (this.writer != null) {
                IOUtils.closeWhileHandlingException(this.writer);
                this.writer = null;
            }
            this.lastDocID = 0;
            this.reset();
        }
    }
    
    void resetFields() {
        Arrays.fill(this.perFields, null);
        this.numVectorFields = 0;
    }
    
    public TermsHashPerField addField(final FieldInvertState invertState, final FieldInfo fieldInfo) {
        return new TermVectorsConsumerPerField(invertState, this, fieldInfo);
    }
    
    void addFieldToFlush(final TermVectorsConsumerPerField fieldToFlush) {
        if (this.numVectorFields == this.perFields.length) {
            final int newSize = ArrayUtil.oversize(this.numVectorFields + 1, RamUsageEstimator.NUM_BYTES_OBJECT_REF);
            final TermVectorsConsumerPerField[] newArray = new TermVectorsConsumerPerField[newSize];
            System.arraycopy(this.perFields, 0, newArray, 0, this.numVectorFields);
            this.perFields = newArray;
        }
        this.perFields[this.numVectorFields++] = fieldToFlush;
    }
    
    @Override
    void startDocument() {
        this.resetFields();
        this.numVectorFields = 0;
    }
}
