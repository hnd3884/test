package org.apache.xmlbeans.impl.values;

import org.apache.xmlbeans.SchemaType;

public class XmlAnySimpleTypeRestriction extends XmlAnySimpleTypeImpl
{
    private SchemaType _schemaType;
    
    public XmlAnySimpleTypeRestriction(final SchemaType type, final boolean complex) {
        this._schemaType = type;
        this.initComplexType(complex, false);
    }
    
    @Override
    public SchemaType schemaType() {
        return this._schemaType;
    }
}
