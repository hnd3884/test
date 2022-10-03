package org.apache.lucene.search.highlight;

import org.apache.lucene.index.StoredFieldVisitor;
import org.apache.lucene.util.Bits;
import org.apache.lucene.index.SortedSetDocValues;
import org.apache.lucene.index.SortedNumericDocValues;
import org.apache.lucene.index.SortedDocValues;
import org.apache.lucene.index.BinaryDocValues;
import org.apache.lucene.index.NumericDocValues;
import org.apache.lucene.index.IndexReader;
import java.util.Map;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.DocValuesType;
import org.apache.lucene.index.IndexOptions;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.FieldInfos;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.LeafReader;

public class TermVectorLeafReader extends LeafReader
{
    private final Fields fields;
    private final FieldInfos fieldInfos;
    
    public TermVectorLeafReader(final String field, final Terms terms) {
        this.fields = new Fields() {
            public Iterator<String> iterator() {
                return Collections.singletonList(field).iterator();
            }
            
            public Terms terms(final String fld) throws IOException {
                if (!field.equals(fld)) {
                    return null;
                }
                return terms;
            }
            
            public int size() {
                return 1;
            }
        };
        IndexOptions indexOptions;
        if (!terms.hasFreqs()) {
            indexOptions = IndexOptions.DOCS;
        }
        else if (!terms.hasPositions()) {
            indexOptions = IndexOptions.DOCS_AND_FREQS;
        }
        else if (!terms.hasOffsets()) {
            indexOptions = IndexOptions.DOCS_AND_FREQS_AND_POSITIONS;
        }
        else {
            indexOptions = IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS;
        }
        final FieldInfo fieldInfo = new FieldInfo(field, 0, true, true, terms.hasPayloads(), indexOptions, DocValuesType.NONE, -1L, (Map)Collections.emptyMap());
        this.fieldInfos = new FieldInfos(new FieldInfo[] { fieldInfo });
    }
    
    public void addCoreClosedListener(final LeafReader.CoreClosedListener listener) {
        addCoreClosedListenerAsReaderClosedListener((IndexReader)this, listener);
    }
    
    public void removeCoreClosedListener(final LeafReader.CoreClosedListener listener) {
        removeCoreClosedListenerAsReaderClosedListener((IndexReader)this, listener);
    }
    
    protected void doClose() throws IOException {
    }
    
    public Fields fields() throws IOException {
        return this.fields;
    }
    
    public NumericDocValues getNumericDocValues(final String field) throws IOException {
        return null;
    }
    
    public BinaryDocValues getBinaryDocValues(final String field) throws IOException {
        return null;
    }
    
    public SortedDocValues getSortedDocValues(final String field) throws IOException {
        return null;
    }
    
    public SortedNumericDocValues getSortedNumericDocValues(final String field) throws IOException {
        return null;
    }
    
    public SortedSetDocValues getSortedSetDocValues(final String field) throws IOException {
        return null;
    }
    
    public Bits getDocsWithField(final String field) throws IOException {
        return null;
    }
    
    public NumericDocValues getNormValues(final String field) throws IOException {
        return null;
    }
    
    public FieldInfos getFieldInfos() {
        return this.fieldInfos;
    }
    
    public Bits getLiveDocs() {
        return null;
    }
    
    public void checkIntegrity() throws IOException {
    }
    
    public Fields getTermVectors(final int docID) throws IOException {
        if (docID != 0) {
            return null;
        }
        return this.fields();
    }
    
    public int numDocs() {
        return 1;
    }
    
    public int maxDoc() {
        return 1;
    }
    
    public void document(final int docID, final StoredFieldVisitor visitor) throws IOException {
    }
}
