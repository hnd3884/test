package org.apache.lucene.document;

import org.apache.lucene.index.DocValuesType;

public class SortedNumericDocValuesField extends Field
{
    public static final FieldType TYPE;
    
    public SortedNumericDocValuesField(final String name, final long value) {
        super(name, SortedNumericDocValuesField.TYPE);
        this.fieldsData = value;
    }
    
    static {
        (TYPE = new FieldType()).setDocValuesType(DocValuesType.SORTED_NUMERIC);
        SortedNumericDocValuesField.TYPE.freeze();
    }
}
