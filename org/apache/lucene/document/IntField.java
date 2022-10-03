package org.apache.lucene.document;

import org.apache.lucene.index.IndexOptions;

public final class IntField extends Field
{
    public static final FieldType TYPE_NOT_STORED;
    public static final FieldType TYPE_STORED;
    
    public IntField(final String name, final int value, final Store stored) {
        super(name, (stored == Store.YES) ? IntField.TYPE_STORED : IntField.TYPE_NOT_STORED);
        this.fieldsData = value;
    }
    
    public IntField(final String name, final int value, final FieldType type) {
        super(name, type);
        if (type.numericType() != FieldType.NumericType.INT) {
            throw new IllegalArgumentException("type.numericType() must be INT but got " + type.numericType());
        }
        this.fieldsData = value;
    }
    
    static {
        (TYPE_NOT_STORED = new FieldType()).setTokenized(true);
        IntField.TYPE_NOT_STORED.setOmitNorms(true);
        IntField.TYPE_NOT_STORED.setIndexOptions(IndexOptions.DOCS);
        IntField.TYPE_NOT_STORED.setNumericType(FieldType.NumericType.INT);
        IntField.TYPE_NOT_STORED.setNumericPrecisionStep(8);
        IntField.TYPE_NOT_STORED.freeze();
        (TYPE_STORED = new FieldType()).setTokenized(true);
        IntField.TYPE_STORED.setOmitNorms(true);
        IntField.TYPE_STORED.setIndexOptions(IndexOptions.DOCS);
        IntField.TYPE_STORED.setNumericType(FieldType.NumericType.INT);
        IntField.TYPE_STORED.setNumericPrecisionStep(8);
        IntField.TYPE_STORED.setStored(true);
        IntField.TYPE_STORED.freeze();
    }
}
