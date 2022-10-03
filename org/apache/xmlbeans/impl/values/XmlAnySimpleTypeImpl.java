package org.apache.xmlbeans.impl.values;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.BuiltinSchemaTypeSystem;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlAnySimpleType;

public class XmlAnySimpleTypeImpl extends XmlObjectBase implements XmlAnySimpleType
{
    private SchemaType _schemaType;
    String _textvalue;
    
    public XmlAnySimpleTypeImpl(final SchemaType type, final boolean complex) {
        this._textvalue = "";
        this._schemaType = type;
        this.initComplexType(complex, false);
    }
    
    public XmlAnySimpleTypeImpl() {
        this._textvalue = "";
        this._schemaType = BuiltinSchemaTypeSystem.ST_ANY_SIMPLE;
    }
    
    @Override
    public SchemaType schemaType() {
        return this._schemaType;
    }
    
    @Override
    protected int get_wscanon_rule() {
        return 1;
    }
    
    @Override
    protected String compute_text(final NamespaceManager nsm) {
        return this._textvalue;
    }
    
    @Override
    protected void set_text(final String s) {
        this._textvalue = s;
    }
    
    @Override
    protected void set_nil() {
        this._textvalue = null;
    }
    
    @Override
    protected boolean equal_to(final XmlObject obj) {
        return this._textvalue.equals(((XmlAnySimpleType)obj).getStringValue());
    }
    
    @Override
    protected int value_hash_code() {
        return (this._textvalue == null) ? 0 : this._textvalue.hashCode();
    }
}
