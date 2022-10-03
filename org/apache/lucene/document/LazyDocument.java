package org.apache.lucene.document;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexableFieldType;
import java.io.Reader;
import org.apache.lucene.util.BytesRef;
import java.io.IOException;
import java.util.ArrayList;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.FieldInfo;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Set;
import java.util.List;
import java.util.Map;
import org.apache.lucene.index.IndexReader;

public class LazyDocument
{
    private final IndexReader reader;
    private final int docID;
    private Document doc;
    private Map<Integer, List<LazyField>> fields;
    private Set<String> fieldNames;
    
    public LazyDocument(final IndexReader reader, final int docID) {
        this.fields = new HashMap<Integer, List<LazyField>>();
        this.fieldNames = new HashSet<String>();
        this.reader = reader;
        this.docID = docID;
    }
    
    public IndexableField getField(final FieldInfo fieldInfo) {
        this.fieldNames.add(fieldInfo.name);
        List<LazyField> values = this.fields.get(fieldInfo.number);
        if (null == values) {
            values = new ArrayList<LazyField>();
            this.fields.put(fieldInfo.number, values);
        }
        final LazyField value = new LazyField(fieldInfo.name, fieldInfo.number);
        values.add(value);
        synchronized (this) {
            this.doc = null;
        }
        return (IndexableField)value;
    }
    
    synchronized Document getDocument() {
        if (this.doc == null) {
            try {
                this.doc = this.reader.document(this.docID, (Set)this.fieldNames);
            }
            catch (final IOException ioe) {
                throw new IllegalStateException("unable to load document", ioe);
            }
        }
        return this.doc;
    }
    
    private void fetchRealValues(final String name, final int fieldNum) {
        final Document d = this.getDocument();
        final List<LazyField> lazyValues = this.fields.get(fieldNum);
        final IndexableField[] realValues = d.getFields(name);
        assert realValues.length <= lazyValues.size() : "More lazy values then real values for field: " + name;
        for (int i = 0; i < lazyValues.size(); ++i) {
            final LazyField f = lazyValues.get(i);
            if (null != f) {
                f.realValue = realValues[i];
            }
        }
    }
    
    public class LazyField implements IndexableField
    {
        private String name;
        private int fieldNum;
        volatile IndexableField realValue;
        
        private LazyField(final String name, final int fieldNum) {
            this.realValue = null;
            this.name = name;
            this.fieldNum = fieldNum;
        }
        
        public boolean hasBeenLoaded() {
            return null != this.realValue;
        }
        
        private IndexableField getRealValue() {
            if (null == this.realValue) {
                LazyDocument.this.fetchRealValues(this.name, this.fieldNum);
            }
            assert this.hasBeenLoaded() : "field value was not lazy loaded";
            assert this.realValue.name().equals(this.name()) : "realvalue name != name: " + this.realValue.name() + " != " + this.name();
            return this.realValue;
        }
        
        public String name() {
            return this.name;
        }
        
        public float boost() {
            return 1.0f;
        }
        
        public BytesRef binaryValue() {
            return this.getRealValue().binaryValue();
        }
        
        public String stringValue() {
            return this.getRealValue().stringValue();
        }
        
        public Reader readerValue() {
            return this.getRealValue().readerValue();
        }
        
        public Number numericValue() {
            return this.getRealValue().numericValue();
        }
        
        public IndexableFieldType fieldType() {
            return this.getRealValue().fieldType();
        }
        
        public TokenStream tokenStream(final Analyzer analyzer, final TokenStream reuse) {
            return this.getRealValue().tokenStream(analyzer, reuse);
        }
    }
}
