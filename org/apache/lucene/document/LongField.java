package org.apache.lucene.document;

import org.apache.lucene.index.IndexOptions;

public final class LongField extends Field
{
    public static final FieldType TYPE_NOT_STORED;
    public static final FieldType TYPE_STORED;
    
    public LongField(final String name, final long value, final Store stored) {
        super(name, (stored == Store.YES) ? LongField.TYPE_STORED : LongField.TYPE_NOT_STORED);
        this.fieldsData = value;
    }
    
    public LongField(final String name, final long value, final FieldType type) {
        super(name, type);
        if (type.numericType() != FieldType.NumericType.LONG) {
            throw new IllegalArgumentException("type.numericType() must be LONG but got " + type.numericType());
        }
        this.fieldsData = value;
    }
    
    static {
        (TYPE_NOT_STORED = new FieldType()).setTokenized(true);
        LongField.TYPE_NOT_STORED.setOmitNorms(true);
        LongField.TYPE_NOT_STORED.setIndexOptions(IndexOptions.DOCS);
        LongField.TYPE_NOT_STORED.setNumericType(FieldType.NumericType.LONG);
        LongField.TYPE_NOT_STORED.freeze();
        (TYPE_STORED = new FieldType()).setTokenized(true);
        LongField.TYPE_STORED.setOmitNorms(true);
        LongField.TYPE_STORED.setIndexOptions(IndexOptions.DOCS);
        LongField.TYPE_STORED.setNumericType(FieldType.NumericType.LONG);
        LongField.TYPE_STORED.setStored(true);
        LongField.TYPE_STORED.freeze();
    }
}
