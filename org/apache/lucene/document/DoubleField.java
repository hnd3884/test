package org.apache.lucene.document;

import org.apache.lucene.index.IndexOptions;

public final class DoubleField extends Field
{
    public static final FieldType TYPE_NOT_STORED;
    public static final FieldType TYPE_STORED;
    
    public DoubleField(final String name, final double value, final Store stored) {
        super(name, (stored == Store.YES) ? DoubleField.TYPE_STORED : DoubleField.TYPE_NOT_STORED);
        this.fieldsData = value;
    }
    
    public DoubleField(final String name, final double value, final FieldType type) {
        super(name, type);
        if (type.numericType() != FieldType.NumericType.DOUBLE) {
            throw new IllegalArgumentException("type.numericType() must be DOUBLE but got " + type.numericType());
        }
        this.fieldsData = value;
    }
    
    static {
        (TYPE_NOT_STORED = new FieldType()).setTokenized(true);
        DoubleField.TYPE_NOT_STORED.setOmitNorms(true);
        DoubleField.TYPE_NOT_STORED.setIndexOptions(IndexOptions.DOCS);
        DoubleField.TYPE_NOT_STORED.setNumericType(FieldType.NumericType.DOUBLE);
        DoubleField.TYPE_NOT_STORED.freeze();
        (TYPE_STORED = new FieldType()).setTokenized(true);
        DoubleField.TYPE_STORED.setOmitNorms(true);
        DoubleField.TYPE_STORED.setIndexOptions(IndexOptions.DOCS);
        DoubleField.TYPE_STORED.setNumericType(FieldType.NumericType.DOUBLE);
        DoubleField.TYPE_STORED.setStored(true);
        DoubleField.TYPE_STORED.freeze();
    }
}
