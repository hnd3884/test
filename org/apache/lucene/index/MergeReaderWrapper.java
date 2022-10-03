package org.apache.lucene.index;

import org.apache.lucene.util.Bits;
import java.io.IOException;
import org.apache.lucene.codecs.TermVectorsReader;
import org.apache.lucene.codecs.StoredFieldsReader;
import org.apache.lucene.codecs.DocValuesProducer;
import org.apache.lucene.codecs.NormsProducer;
import org.apache.lucene.codecs.FieldsProducer;

class MergeReaderWrapper extends LeafReader
{
    final SegmentReader in;
    final FieldsProducer fields;
    final NormsProducer norms;
    final DocValuesProducer docValues;
    final StoredFieldsReader store;
    final TermVectorsReader vectors;
    
    MergeReaderWrapper(final SegmentReader in) throws IOException {
        this.in = in;
        FieldsProducer fields = in.getPostingsReader();
        if (fields != null) {
            fields = fields.getMergeInstance();
        }
        this.fields = fields;
        NormsProducer norms = in.getNormsReader();
        if (norms != null) {
            norms = norms.getMergeInstance();
        }
        this.norms = norms;
        DocValuesProducer docValues = in.getDocValuesReader();
        if (docValues != null) {
            docValues = docValues.getMergeInstance();
        }
        this.docValues = docValues;
        StoredFieldsReader store = in.getFieldsReader();
        if (store != null) {
            store = store.getMergeInstance();
        }
        this.store = store;
        TermVectorsReader vectors = in.getTermVectorsReader();
        if (vectors != null) {
            vectors = vectors.getMergeInstance();
        }
        this.vectors = vectors;
    }
    
    public void addCoreClosedListener(final LeafReader.CoreClosedListener listener) {
        this.in.addCoreClosedListener(listener);
    }
    
    public void removeCoreClosedListener(final LeafReader.CoreClosedListener listener) {
        this.in.removeCoreClosedListener(listener);
    }
    
    public Fields fields() throws IOException {
        return (Fields)this.fields;
    }
    
    public NumericDocValues getNumericDocValues(final String field) throws IOException {
        this.ensureOpen();
        final FieldInfo fi = this.getFieldInfos().fieldInfo(field);
        if (fi == null) {
            return null;
        }
        if (fi.getDocValuesType() != DocValuesType.NUMERIC) {
            return null;
        }
        return this.docValues.getNumeric(fi);
    }
    
    public BinaryDocValues getBinaryDocValues(final String field) throws IOException {
        this.ensureOpen();
        final FieldInfo fi = this.getFieldInfos().fieldInfo(field);
        if (fi == null) {
            return null;
        }
        if (fi.getDocValuesType() != DocValuesType.BINARY) {
            return null;
        }
        return this.docValues.getBinary(fi);
    }
    
    public SortedDocValues getSortedDocValues(final String field) throws IOException {
        this.ensureOpen();
        final FieldInfo fi = this.getFieldInfos().fieldInfo(field);
        if (fi == null) {
            return null;
        }
        if (fi.getDocValuesType() != DocValuesType.SORTED) {
            return null;
        }
        return this.docValues.getSorted(fi);
    }
    
    public SortedNumericDocValues getSortedNumericDocValues(final String field) throws IOException {
        this.ensureOpen();
        final FieldInfo fi = this.getFieldInfos().fieldInfo(field);
        if (fi == null) {
            return null;
        }
        if (fi.getDocValuesType() != DocValuesType.SORTED_NUMERIC) {
            return null;
        }
        return this.docValues.getSortedNumeric(fi);
    }
    
    public SortedSetDocValues getSortedSetDocValues(final String field) throws IOException {
        this.ensureOpen();
        final FieldInfo fi = this.getFieldInfos().fieldInfo(field);
        if (fi == null) {
            return null;
        }
        if (fi.getDocValuesType() != DocValuesType.SORTED_SET) {
            return null;
        }
        return this.docValues.getSortedSet(fi);
    }
    
    public Bits getDocsWithField(final String field) throws IOException {
        this.ensureOpen();
        final FieldInfo fi = this.getFieldInfos().fieldInfo(field);
        if (fi == null) {
            return null;
        }
        if (fi.getDocValuesType() == DocValuesType.NONE) {
            return null;
        }
        return this.docValues.getDocsWithField(fi);
    }
    
    public NumericDocValues getNormValues(final String field) throws IOException {
        this.ensureOpen();
        final FieldInfo fi = this.getFieldInfos().fieldInfo(field);
        if (fi == null || !fi.hasNorms()) {
            return null;
        }
        return this.norms.getNorms(fi);
    }
    
    public FieldInfos getFieldInfos() {
        return this.in.getFieldInfos();
    }
    
    public Bits getLiveDocs() {
        return this.in.getLiveDocs();
    }
    
    public void checkIntegrity() throws IOException {
        this.in.checkIntegrity();
    }
    
    public Fields getTermVectors(final int docID) throws IOException {
        this.ensureOpen();
        this.checkBounds(docID);
        if (this.vectors == null) {
            return null;
        }
        return this.vectors.get(docID);
    }
    
    public int numDocs() {
        return this.in.numDocs();
    }
    
    public int maxDoc() {
        return this.in.maxDoc();
    }
    
    public void document(final int docID, final StoredFieldVisitor visitor) throws IOException {
        this.ensureOpen();
        this.checkBounds(docID);
        this.store.visitDocument(docID, visitor);
    }
    
    protected void doClose() throws IOException {
        this.in.close();
    }
    
    public Object getCoreCacheKey() {
        return this.in.getCoreCacheKey();
    }
    
    public Object getCombinedCoreAndDeletesKey() {
        return this.in.getCombinedCoreAndDeletesKey();
    }
    
    private void checkBounds(final int docID) {
        if (docID < 0 || docID >= this.maxDoc()) {
            throw new IndexOutOfBoundsException("docID must be >= 0 and < maxDoc=" + this.maxDoc() + " (got docID=" + docID + ")");
        }
    }
    
    public String toString() {
        return "MergeReaderWrapper(" + this.in + ")";
    }
}
