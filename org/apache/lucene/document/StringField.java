package org.apache.lucene.document;

import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.util.BytesRef;

public final class StringField extends Field
{
    public static final FieldType TYPE_NOT_STORED;
    public static final FieldType TYPE_STORED;
    
    public StringField(final String name, final String value, final Store stored) {
        super(name, value, (stored == Store.YES) ? StringField.TYPE_STORED : StringField.TYPE_NOT_STORED);
    }
    
    public StringField(final String name, final BytesRef value, final Store stored) {
        super(name, value, (stored == Store.YES) ? StringField.TYPE_STORED : StringField.TYPE_NOT_STORED);
    }
    
    static {
        TYPE_NOT_STORED = new FieldType();
        TYPE_STORED = new FieldType();
        StringField.TYPE_NOT_STORED.setOmitNorms(true);
        StringField.TYPE_NOT_STORED.setIndexOptions(IndexOptions.DOCS);
        StringField.TYPE_NOT_STORED.setTokenized(false);
        StringField.TYPE_NOT_STORED.freeze();
        StringField.TYPE_STORED.setOmitNorms(true);
        StringField.TYPE_STORED.setIndexOptions(IndexOptions.DOCS);
        StringField.TYPE_STORED.setStored(true);
        StringField.TYPE_STORED.setTokenized(false);
        StringField.TYPE_STORED.freeze();
    }
}
