package org.apache.lucene.document;

import org.apache.lucene.index.IndexOptions;

public final class FloatField extends Field
{
    public static final FieldType TYPE_NOT_STORED;
    public static final FieldType TYPE_STORED;
    
    public FloatField(final String name, final float value, final Store stored) {
        super(name, (stored == Store.YES) ? FloatField.TYPE_STORED : FloatField.TYPE_NOT_STORED);
        this.fieldsData = value;
    }
    
    public FloatField(final String name, final float value, final FieldType type) {
        super(name, type);
        if (type.numericType() != FieldType.NumericType.FLOAT) {
            throw new IllegalArgumentException("type.numericType() must be FLOAT but got " + type.numericType());
        }
        this.fieldsData = value;
    }
    
    static {
        (TYPE_NOT_STORED = new FieldType()).setTokenized(true);
        FloatField.TYPE_NOT_STORED.setOmitNorms(true);
        FloatField.TYPE_NOT_STORED.setIndexOptions(IndexOptions.DOCS);
        FloatField.TYPE_NOT_STORED.setNumericType(FieldType.NumericType.FLOAT);
        FloatField.TYPE_NOT_STORED.setNumericPrecisionStep(8);
        FloatField.TYPE_NOT_STORED.freeze();
        (TYPE_STORED = new FieldType()).setTokenized(true);
        FloatField.TYPE_STORED.setOmitNorms(true);
        FloatField.TYPE_STORED.setIndexOptions(IndexOptions.DOCS);
        FloatField.TYPE_STORED.setNumericType(FieldType.NumericType.FLOAT);
        FloatField.TYPE_STORED.setNumericPrecisionStep(8);
        FloatField.TYPE_STORED.setStored(true);
        FloatField.TYPE_STORED.freeze();
    }
}
