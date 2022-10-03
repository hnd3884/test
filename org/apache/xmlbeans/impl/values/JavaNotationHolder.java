package org.apache.xmlbeans.impl.values;

import org.apache.xmlbeans.impl.schema.BuiltinSchemaTypeSystem;
import org.apache.xmlbeans.SchemaType;

public abstract class JavaNotationHolder extends XmlQNameImpl
{
    @Override
    public SchemaType schemaType() {
        return BuiltinSchemaTypeSystem.ST_NOTATION;
    }
}
