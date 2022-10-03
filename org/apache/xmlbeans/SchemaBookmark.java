package org.apache.xmlbeans;

public class SchemaBookmark extends XmlCursor.XmlBookmark
{
    private Object _value;
    
    public SchemaBookmark(final Object value) {
        this._value = value;
    }
    
    public Object getValue() {
        return this._value;
    }
}
