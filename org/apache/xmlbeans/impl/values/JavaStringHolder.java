package org.apache.xmlbeans.impl.values;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.BuiltinSchemaTypeSystem;
import org.apache.xmlbeans.SchemaType;

public class JavaStringHolder extends XmlObjectBase
{
    private String _value;
    
    @Override
    public SchemaType schemaType() {
        return BuiltinSchemaTypeSystem.ST_STRING;
    }
    
    @Override
    protected int get_wscanon_rule() {
        return 1;
    }
    
    public String compute_text(final NamespaceManager nsm) {
        return this._value;
    }
    
    @Override
    protected void set_text(final String s) {
        this._value = s;
    }
    
    @Override
    protected void set_nil() {
        this._value = null;
    }
    
    @Override
    protected boolean equal_to(final XmlObject obj) {
        return this._value.equals(((XmlObjectBase)obj).stringValue());
    }
    
    @Override
    protected int value_hash_code() {
        return this._value.hashCode();
    }
    
    @Override
    protected boolean is_defaultable_ws(final String v) {
        return false;
    }
}
