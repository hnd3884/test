package org.apache.lucene.document;

import org.apache.lucene.util.BytesRef;

public final class StoredField extends Field
{
    public static final FieldType TYPE;
    
    public StoredField(final String name, final byte[] value) {
        super(name, value, StoredField.TYPE);
    }
    
    public StoredField(final String name, final byte[] value, final int offset, final int length) {
        super(name, value, offset, length, StoredField.TYPE);
    }
    
    public StoredField(final String name, final BytesRef value) {
        super(name, value, StoredField.TYPE);
    }
    
    public StoredField(final String name, final String value) {
        super(name, value, StoredField.TYPE);
    }
    
    public StoredField(final String name, final int value) {
        super(name, StoredField.TYPE);
        this.fieldsData = value;
    }
    
    public StoredField(final String name, final float value) {
        super(name, StoredField.TYPE);
        this.fieldsData = value;
    }
    
    public StoredField(final String name, final long value) {
        super(name, StoredField.TYPE);
        this.fieldsData = value;
    }
    
    public StoredField(final String name, final double value) {
        super(name, StoredField.TYPE);
        this.fieldsData = value;
    }
    
    static {
        (TYPE = new FieldType()).setStored(true);
        StoredField.TYPE.freeze();
    }
}
