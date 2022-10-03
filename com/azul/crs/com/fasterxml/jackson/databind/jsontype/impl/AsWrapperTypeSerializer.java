package com.azul.crs.com.fasterxml.jackson.databind.jsontype.impl;

import com.azul.crs.com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import java.io.IOException;
import com.azul.crs.com.fasterxml.jackson.core.JsonGenerator;
import com.azul.crs.com.fasterxml.jackson.databind.util.ClassUtil;
import com.azul.crs.com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.azul.crs.com.fasterxml.jackson.databind.BeanProperty;
import com.azul.crs.com.fasterxml.jackson.databind.jsontype.TypeIdResolver;

public class AsWrapperTypeSerializer extends TypeSerializerBase
{
    public AsWrapperTypeSerializer(final TypeIdResolver idRes, final BeanProperty property) {
        super(idRes, property);
    }
    
    @Override
    public AsWrapperTypeSerializer forProperty(final BeanProperty prop) {
        return (this._property == prop) ? this : new AsWrapperTypeSerializer(this._idResolver, prop);
    }
    
    @Override
    public JsonTypeInfo.As getTypeInclusion() {
        return JsonTypeInfo.As.WRAPPER_OBJECT;
    }
    
    protected String _validTypeId(final String typeId) {
        return ClassUtil.nonNullString(typeId);
    }
    
    protected final void _writeTypeId(final JsonGenerator g, final String typeId) throws IOException {
        if (typeId != null) {
            g.writeTypeId(typeId);
        }
    }
}
