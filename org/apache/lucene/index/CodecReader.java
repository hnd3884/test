package org.apache.lucene.index;

import java.util.List;
import java.util.Collections;
import org.apache.lucene.util.Accountables;
import java.util.ArrayList;
import java.util.Collection;
import org.apache.lucene.util.IOUtils;
import java.io.Closeable;
import java.io.IOException;
import org.apache.lucene.codecs.FieldsProducer;
import org.apache.lucene.codecs.DocValuesProducer;
import org.apache.lucene.codecs.NormsProducer;
import org.apache.lucene.codecs.TermVectorsReader;
import org.apache.lucene.codecs.StoredFieldsReader;
import java.util.HashMap;
import org.apache.lucene.util.Bits;
import java.util.Map;
import org.apache.lucene.util.CloseableThreadLocal;
import org.apache.lucene.util.Accountable;

public abstract class CodecReader extends LeafReader implements Accountable
{
    final CloseableThreadLocal<Map<String, Object>> docValuesLocal;
    final CloseableThreadLocal<Map<String, Bits>> docsWithFieldLocal;
    final CloseableThreadLocal<Map<String, NumericDocValues>> normsLocal;
    
    protected CodecReader() {
        this.docValuesLocal = new CloseableThreadLocal<Map<String, Object>>() {
            @Override
            protected Map<String, Object> initialValue() {
                return new HashMap<String, Object>();
            }
        };
        this.docsWithFieldLocal = new CloseableThreadLocal<Map<String, Bits>>() {
            @Override
            protected Map<String, Bits> initialValue() {
                return new HashMap<String, Bits>();
            }
        };
        this.normsLocal = new CloseableThreadLocal<Map<String, NumericDocValues>>() {
            @Override
            protected Map<String, NumericDocValues> initialValue() {
                return new HashMap<String, NumericDocValues>();
            }
        };
    }
    
    public abstract StoredFieldsReader getFieldsReader();
    
    public abstract TermVectorsReader getTermVectorsReader();
    
    public abstract NormsProducer getNormsReader();
    
    public abstract DocValuesProducer getDocValuesReader();
    
    public abstract FieldsProducer getPostingsReader();
    
    @Override
    public final void document(final int docID, final StoredFieldVisitor visitor) throws IOException {
        this.checkBounds(docID);
        this.getFieldsReader().visitDocument(docID, visitor);
    }
    
    @Override
    public final Fields getTermVectors(final int docID) throws IOException {
        final TermVectorsReader termVectorsReader = this.getTermVectorsReader();
        if (termVectorsReader == null) {
            return null;
        }
        this.checkBounds(docID);
        return termVectorsReader.get(docID);
    }
    
    private void checkBounds(final int docID) {
        if (docID < 0 || docID >= this.maxDoc()) {
            throw new IndexOutOfBoundsException("docID must be >= 0 and < maxDoc=" + this.maxDoc() + " (got docID=" + docID + ")");
        }
    }
    
    @Override
    public final Fields fields() {
        return this.getPostingsReader();
    }
    
    private FieldInfo getDVField(final String field, final DocValuesType type) {
        final FieldInfo fi = this.getFieldInfos().fieldInfo(field);
        if (fi == null) {
            return null;
        }
        if (fi.getDocValuesType() == DocValuesType.NONE) {
            return null;
        }
        if (fi.getDocValuesType() != type) {
            return null;
        }
        return fi;
    }
    
    @Override
    public final NumericDocValues getNumericDocValues(final String field) throws IOException {
        this.ensureOpen();
        final Map<String, Object> dvFields = this.docValuesLocal.get();
        final Object previous = dvFields.get(field);
        if (previous != null && previous instanceof NumericDocValues) {
            return (NumericDocValues)previous;
        }
        final FieldInfo fi = this.getDVField(field, DocValuesType.NUMERIC);
        if (fi == null) {
            return null;
        }
        final NumericDocValues dv = this.getDocValuesReader().getNumeric(fi);
        dvFields.put(field, dv);
        return dv;
    }
    
    @Override
    public final Bits getDocsWithField(final String field) throws IOException {
        this.ensureOpen();
        final Map<String, Bits> dvFields = this.docsWithFieldLocal.get();
        final Bits previous = dvFields.get(field);
        if (previous != null) {
            return previous;
        }
        final FieldInfo fi = this.getFieldInfos().fieldInfo(field);
        if (fi == null) {
            return null;
        }
        if (fi.getDocValuesType() == DocValuesType.NONE) {
            return null;
        }
        final Bits dv = this.getDocValuesReader().getDocsWithField(fi);
        dvFields.put(field, dv);
        return dv;
    }
    
    @Override
    public final BinaryDocValues getBinaryDocValues(final String field) throws IOException {
        this.ensureOpen();
        final FieldInfo fi = this.getDVField(field, DocValuesType.BINARY);
        if (fi == null) {
            return null;
        }
        final Map<String, Object> dvFields = this.docValuesLocal.get();
        BinaryDocValues dvs = dvFields.get(field);
        if (dvs == null) {
            dvs = this.getDocValuesReader().getBinary(fi);
            dvFields.put(field, dvs);
        }
        return dvs;
    }
    
    @Override
    public final SortedDocValues getSortedDocValues(final String field) throws IOException {
        this.ensureOpen();
        final Map<String, Object> dvFields = this.docValuesLocal.get();
        final Object previous = dvFields.get(field);
        if (previous != null && previous instanceof SortedDocValues) {
            return (SortedDocValues)previous;
        }
        final FieldInfo fi = this.getDVField(field, DocValuesType.SORTED);
        if (fi == null) {
            return null;
        }
        final SortedDocValues dv = this.getDocValuesReader().getSorted(fi);
        dvFields.put(field, dv);
        return dv;
    }
    
    @Override
    public final SortedNumericDocValues getSortedNumericDocValues(final String field) throws IOException {
        this.ensureOpen();
        final Map<String, Object> dvFields = this.docValuesLocal.get();
        final Object previous = dvFields.get(field);
        if (previous != null && previous instanceof SortedNumericDocValues) {
            return (SortedNumericDocValues)previous;
        }
        final FieldInfo fi = this.getDVField(field, DocValuesType.SORTED_NUMERIC);
        if (fi == null) {
            return null;
        }
        final SortedNumericDocValues dv = this.getDocValuesReader().getSortedNumeric(fi);
        dvFields.put(field, dv);
        return dv;
    }
    
    @Override
    public final SortedSetDocValues getSortedSetDocValues(final String field) throws IOException {
        this.ensureOpen();
        final Map<String, Object> dvFields = this.docValuesLocal.get();
        final Object previous = dvFields.get(field);
        if (previous != null && previous instanceof SortedSetDocValues) {
            return (SortedSetDocValues)previous;
        }
        final FieldInfo fi = this.getDVField(field, DocValuesType.SORTED_SET);
        if (fi == null) {
            return null;
        }
        final SortedSetDocValues dv = this.getDocValuesReader().getSortedSet(fi);
        dvFields.put(field, dv);
        return dv;
    }
    
    @Override
    public final NumericDocValues getNormValues(final String field) throws IOException {
        this.ensureOpen();
        final Map<String, NumericDocValues> normFields = this.normsLocal.get();
        NumericDocValues norms = normFields.get(field);
        if (norms != null) {
            return norms;
        }
        final FieldInfo fi = this.getFieldInfos().fieldInfo(field);
        if (fi == null || !fi.hasNorms()) {
            return null;
        }
        norms = this.getNormsReader().getNorms(fi);
        normFields.put(field, norms);
        return norms;
    }
    
    @Override
    protected void doClose() throws IOException {
        IOUtils.close(this.docValuesLocal, this.docsWithFieldLocal, this.normsLocal);
    }
    
    @Override
    public long ramBytesUsed() {
        this.ensureOpen();
        long ramBytesUsed = this.getPostingsReader().ramBytesUsed();
        if (this.getNormsReader() != null) {
            ramBytesUsed += this.getNormsReader().ramBytesUsed();
        }
        if (this.getDocValuesReader() != null) {
            ramBytesUsed += this.getDocValuesReader().ramBytesUsed();
        }
        if (this.getFieldsReader() != null) {
            ramBytesUsed += this.getFieldsReader().ramBytesUsed();
        }
        if (this.getTermVectorsReader() != null) {
            ramBytesUsed += this.getTermVectorsReader().ramBytesUsed();
        }
        return ramBytesUsed;
    }
    
    @Override
    public Collection<Accountable> getChildResources() {
        this.ensureOpen();
        final List<Accountable> resources = new ArrayList<Accountable>(5);
        resources.add(Accountables.namedAccountable("postings", this.getPostingsReader()));
        if (this.getNormsReader() != null) {
            resources.add(Accountables.namedAccountable("norms", this.getNormsReader()));
        }
        if (this.getDocValuesReader() != null) {
            resources.add(Accountables.namedAccountable("docvalues", this.getDocValuesReader()));
        }
        if (this.getFieldsReader() != null) {
            resources.add(Accountables.namedAccountable("stored fields", this.getFieldsReader()));
        }
        if (this.getTermVectorsReader() != null) {
            resources.add(Accountables.namedAccountable("term vectors", this.getTermVectorsReader()));
        }
        return (Collection<Accountable>)Collections.unmodifiableList((List<?>)resources);
    }
    
    @Override
    public void checkIntegrity() throws IOException {
        this.ensureOpen();
        this.getPostingsReader().checkIntegrity();
        if (this.getNormsReader() != null) {
            this.getNormsReader().checkIntegrity();
        }
        if (this.getDocValuesReader() != null) {
            this.getDocValuesReader().checkIntegrity();
        }
        if (this.getFieldsReader() != null) {
            this.getFieldsReader().checkIntegrity();
        }
        if (this.getTermVectorsReader() != null) {
            this.getTermVectorsReader().checkIntegrity();
        }
    }
}
