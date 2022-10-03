package org.apache.lucene.document;

import java.nio.charset.StandardCharsets;
import java.io.IOException;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.FieldInfo;
import java.util.HashSet;
import java.util.Set;
import org.apache.lucene.index.StoredFieldVisitor;

public class DocumentStoredFieldVisitor extends StoredFieldVisitor
{
    private final Document doc;
    private final Set<String> fieldsToAdd;
    
    public DocumentStoredFieldVisitor(final Set<String> fieldsToAdd) {
        this.doc = new Document();
        this.fieldsToAdd = fieldsToAdd;
    }
    
    public DocumentStoredFieldVisitor(final String... fields) {
        this.doc = new Document();
        this.fieldsToAdd = new HashSet<String>(fields.length);
        for (final String field : fields) {
            this.fieldsToAdd.add(field);
        }
    }
    
    public DocumentStoredFieldVisitor() {
        this.doc = new Document();
        this.fieldsToAdd = null;
    }
    
    @Override
    public void binaryField(final FieldInfo fieldInfo, final byte[] value) throws IOException {
        this.doc.add(new StoredField(fieldInfo.name, value));
    }
    
    @Override
    public void stringField(final FieldInfo fieldInfo, final byte[] value) throws IOException {
        final FieldType ft = new FieldType(TextField.TYPE_STORED);
        ft.setStoreTermVectors(fieldInfo.hasVectors());
        ft.setOmitNorms(fieldInfo.omitsNorms());
        ft.setIndexOptions(fieldInfo.getIndexOptions());
        this.doc.add(new Field(fieldInfo.name, new String(value, StandardCharsets.UTF_8), ft));
    }
    
    @Override
    public void intField(final FieldInfo fieldInfo, final int value) {
        this.doc.add(new StoredField(fieldInfo.name, value));
    }
    
    @Override
    public void longField(final FieldInfo fieldInfo, final long value) {
        this.doc.add(new StoredField(fieldInfo.name, value));
    }
    
    @Override
    public void floatField(final FieldInfo fieldInfo, final float value) {
        this.doc.add(new StoredField(fieldInfo.name, value));
    }
    
    @Override
    public void doubleField(final FieldInfo fieldInfo, final double value) {
        this.doc.add(new StoredField(fieldInfo.name, value));
    }
    
    @Override
    public Status needsField(final FieldInfo fieldInfo) throws IOException {
        return (this.fieldsToAdd == null || this.fieldsToAdd.contains(fieldInfo.name)) ? Status.YES : Status.NO;
    }
    
    public Document getDocument() {
        return this.doc;
    }
}
