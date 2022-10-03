package org.apache.lucene.document;

import org.apache.lucene.index.DocValuesType;
import org.apache.lucene.util.BytesRef;

public class SortedSetDocValuesField extends Field
{
    public static final FieldType TYPE;
    
    public SortedSetDocValuesField(final String name, final BytesRef bytes) {
        super(name, SortedSetDocValuesField.TYPE);
        this.fieldsData = bytes;
    }
    
    static {
        (TYPE = new FieldType()).setDocValuesType(DocValuesType.SORTED_SET);
        SortedSetDocValuesField.TYPE.freeze();
    }
}
