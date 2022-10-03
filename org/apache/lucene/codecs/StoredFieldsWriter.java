package org.apache.lucene.codecs;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Analyzer;
import java.io.Reader;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.index.IndexableFieldType;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.Bits;
import org.apache.lucene.index.StoredFieldVisitor;
import org.apache.lucene.index.MergeState;
import org.apache.lucene.index.FieldInfos;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.FieldInfo;
import java.io.IOException;
import java.io.Closeable;

public abstract class StoredFieldsWriter implements Closeable
{
    protected StoredFieldsWriter() {
    }
    
    public abstract void startDocument() throws IOException;
    
    public void finishDocument() throws IOException {
    }
    
    public abstract void writeField(final FieldInfo p0, final IndexableField p1) throws IOException;
    
    public abstract void finish(final FieldInfos p0, final int p1) throws IOException;
    
    public int merge(final MergeState mergeState) throws IOException {
        int docCount = 0;
        for (int i = 0; i < mergeState.storedFieldsReaders.length; ++i) {
            final StoredFieldsReader storedFieldsReader = mergeState.storedFieldsReaders[i];
            storedFieldsReader.checkIntegrity();
            final MergeVisitor visitor = new MergeVisitor(mergeState, i);
            final int maxDoc = mergeState.maxDocs[i];
            final Bits liveDocs = mergeState.liveDocs[i];
            for (int docID = 0; docID < maxDoc; ++docID) {
                if (liveDocs == null || liveDocs.get(docID)) {
                    this.startDocument();
                    storedFieldsReader.visitDocument(docID, visitor);
                    this.finishDocument();
                    ++docCount;
                }
            }
        }
        this.finish(mergeState.mergeFieldInfos, docCount);
        return docCount;
    }
    
    @Override
    public abstract void close() throws IOException;
    
    protected class MergeVisitor extends StoredFieldVisitor implements IndexableField
    {
        BytesRef binaryValue;
        String stringValue;
        Number numericValue;
        FieldInfo currentField;
        FieldInfos remapper;
        
        public MergeVisitor(final MergeState mergeState, final int readerIndex) {
            for (final FieldInfo fi : mergeState.fieldInfos[readerIndex]) {
                final FieldInfo other = mergeState.mergeFieldInfos.fieldInfo(fi.number);
                if (other == null || !other.name.equals(fi.name)) {
                    this.remapper = mergeState.mergeFieldInfos;
                    break;
                }
            }
        }
        
        @Override
        public void binaryField(final FieldInfo fieldInfo, final byte[] value) throws IOException {
            this.reset(fieldInfo);
            this.binaryValue = new BytesRef(value);
            this.write();
        }
        
        @Override
        public void stringField(final FieldInfo fieldInfo, final byte[] value) throws IOException {
            this.reset(fieldInfo);
            this.stringValue = new String(value, StandardCharsets.UTF_8);
            this.write();
        }
        
        @Override
        public void intField(final FieldInfo fieldInfo, final int value) throws IOException {
            this.reset(fieldInfo);
            this.numericValue = value;
            this.write();
        }
        
        @Override
        public void longField(final FieldInfo fieldInfo, final long value) throws IOException {
            this.reset(fieldInfo);
            this.numericValue = value;
            this.write();
        }
        
        @Override
        public void floatField(final FieldInfo fieldInfo, final float value) throws IOException {
            this.reset(fieldInfo);
            this.numericValue = value;
            this.write();
        }
        
        @Override
        public void doubleField(final FieldInfo fieldInfo, final double value) throws IOException {
            this.reset(fieldInfo);
            this.numericValue = value;
            this.write();
        }
        
        @Override
        public Status needsField(final FieldInfo fieldInfo) throws IOException {
            return Status.YES;
        }
        
        @Override
        public String name() {
            return this.currentField.name;
        }
        
        @Override
        public IndexableFieldType fieldType() {
            return StoredField.TYPE;
        }
        
        @Override
        public BytesRef binaryValue() {
            return this.binaryValue;
        }
        
        @Override
        public String stringValue() {
            return this.stringValue;
        }
        
        @Override
        public Number numericValue() {
            return this.numericValue;
        }
        
        @Override
        public Reader readerValue() {
            return null;
        }
        
        @Override
        public float boost() {
            return 1.0f;
        }
        
        @Override
        public TokenStream tokenStream(final Analyzer analyzer, final TokenStream reuse) {
            return null;
        }
        
        void reset(final FieldInfo field) {
            if (this.remapper != null) {
                this.currentField = this.remapper.fieldInfo(field.name);
            }
            else {
                this.currentField = field;
            }
            this.binaryValue = null;
            this.stringValue = null;
            this.numericValue = null;
        }
        
        void write() throws IOException {
            StoredFieldsWriter.this.writeField(this.currentField, this);
        }
    }
}
