package org.apache.lucene.document;

import org.apache.lucene.index.DocValuesType;

public class NumericDocValuesField extends Field
{
    public static final FieldType TYPE;
    
    public NumericDocValuesField(final String name, final long value) {
        super(name, NumericDocValuesField.TYPE);
        this.fieldsData = value;
    }
    
    static {
        (TYPE = new FieldType()).setDocValuesType(DocValuesType.NUMERIC);
        NumericDocValuesField.TYPE.freeze();
    }
}
