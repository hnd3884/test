package org.apache.xmlbeans.impl.values;

import org.apache.xmlbeans.XmlAnyURI;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.common.ValidationContext;
import org.apache.xmlbeans.impl.schema.BuiltinSchemaTypeSystem;
import org.apache.xmlbeans.SchemaType;

public abstract class JavaUriHolder extends XmlObjectBase
{
    private String _value;
    
    @Override
    public SchemaType schemaType() {
        return BuiltinSchemaTypeSystem.ST_ANY_URI;
    }
    
    public String compute_text(final NamespaceManager nsm) {
        return (this._value == null) ? "" : this._value;
    }
    
    @Override
    protected void set_text(final String s) {
        if (this._validateOnSet()) {
            validateLexical(s, JavaUriHolder._voorVc);
        }
        this._value = s;
    }
    
    public static void validateLexical(final String v, final ValidationContext context) {
        if (v.startsWith("##")) {
            context.invalid("anyURI", new Object[] { v });
        }
    }
    
    @Override
    protected void set_nil() {
        this._value = null;
    }
    
    @Override
    protected boolean equal_to(final XmlObject obj) {
        return this._value.equals(((XmlAnyURI)obj).getStringValue());
    }
    
    @Override
    protected int value_hash_code() {
        return this._value.hashCode();
    }
}
