package org.apache.lucene.document;

import org.apache.lucene.index.DocValuesType;
import org.apache.lucene.util.BytesRef;

public class SortedDocValuesField extends Field
{
    public static final FieldType TYPE;
    
    public SortedDocValuesField(final String name, final BytesRef bytes) {
        super(name, SortedDocValuesField.TYPE);
        this.fieldsData = bytes;
    }
    
    static {
        (TYPE = new FieldType()).setDocValuesType(DocValuesType.SORTED);
        SortedDocValuesField.TYPE.freeze();
    }
}
