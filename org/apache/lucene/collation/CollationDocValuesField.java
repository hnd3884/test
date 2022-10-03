package org.apache.lucene.collation;

import org.apache.lucene.document.SortedDocValuesField;
import org.apache.lucene.util.BytesRef;
import java.text.Collator;
import org.apache.lucene.document.Field;

public final class CollationDocValuesField extends Field
{
    private final String name;
    private final Collator collator;
    private final BytesRef bytes;
    
    public CollationDocValuesField(final String name, final Collator collator) {
        super(name, SortedDocValuesField.TYPE);
        this.bytes = new BytesRef();
        this.name = name;
        this.collator = (Collator)collator.clone();
        this.fieldsData = this.bytes;
    }
    
    public String name() {
        return this.name;
    }
    
    public void setStringValue(final String value) {
        this.bytes.bytes = this.collator.getCollationKey(value).toByteArray();
        this.bytes.offset = 0;
        this.bytes.length = this.bytes.bytes.length;
    }
}
