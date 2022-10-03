package org.apache.lucene.document;

import org.apache.lucene.index.DocValuesType;
import org.apache.lucene.util.BytesRef;

public class BinaryDocValuesField extends Field
{
    public static final FieldType TYPE;
    
    public BinaryDocValuesField(final String name, final BytesRef value) {
        super(name, BinaryDocValuesField.TYPE);
        this.fieldsData = value;
    }
    
    static {
        (TYPE = new FieldType()).setDocValuesType(DocValuesType.BINARY);
        BinaryDocValuesField.TYPE.freeze();
    }
}
