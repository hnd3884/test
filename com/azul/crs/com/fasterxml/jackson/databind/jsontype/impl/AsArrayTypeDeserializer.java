package com.azul.crs.com.fasterxml.jackson.databind.jsontype.impl;

import com.azul.crs.com.fasterxml.jackson.databind.JsonDeserializer;
import com.azul.crs.com.fasterxml.jackson.core.util.JsonParserSequence;
import com.azul.crs.com.fasterxml.jackson.core.ObjectCodec;
import com.azul.crs.com.fasterxml.jackson.databind.util.TokenBuffer;
import com.azul.crs.com.fasterxml.jackson.core.JsonToken;
import java.io.IOException;
import com.azul.crs.com.fasterxml.jackson.databind.DeserializationContext;
import com.azul.crs.com.fasterxml.jackson.core.JsonParser;
import com.azul.crs.com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.azul.crs.com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.azul.crs.com.fasterxml.jackson.databind.BeanProperty;
import com.azul.crs.com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import com.azul.crs.com.fasterxml.jackson.databind.JavaType;
import java.io.Serializable;

public class AsArrayTypeDeserializer extends TypeDeserializerBase implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    public AsArrayTypeDeserializer(final JavaType bt, final TypeIdResolver idRes, final String typePropertyName, final boolean typeIdVisible, final JavaType defaultImpl) {
        super(bt, idRes, typePropertyName, typeIdVisible, defaultImpl);
    }
    
    public AsArrayTypeDeserializer(final AsArrayTypeDeserializer src, final BeanProperty property) {
        super(src, property);
    }
    
    @Override
    public TypeDeserializer forProperty(final BeanProperty prop) {
        return (prop == this._property) ? this : new AsArrayTypeDeserializer(this, prop);
    }
    
    @Override
    public JsonTypeInfo.As getTypeInclusion() {
        return JsonTypeInfo.As.WRAPPER_ARRAY;
    }
    
    @Override
    public Object deserializeTypedFromArray(final JsonParser jp, final DeserializationContext ctxt) throws IOException {
        return this._deserialize(jp, ctxt);
    }
    
    @Override
    public Object deserializeTypedFromObject(final JsonParser jp, final DeserializationContext ctxt) throws IOException {
        return this._deserialize(jp, ctxt);
    }
    
    @Override
    public Object deserializeTypedFromScalar(final JsonParser jp, final DeserializationContext ctxt) throws IOException {
        return this._deserialize(jp, ctxt);
    }
    
    @Override
    public Object deserializeTypedFromAny(final JsonParser jp, final DeserializationContext ctxt) throws IOException {
        return this._deserialize(jp, ctxt);
    }
    
    protected Object _deserialize(JsonParser p, final DeserializationContext ctxt) throws IOException {
        if (p.canReadTypeId()) {
            final Object typeId = p.getTypeId();
            if (typeId != null) {
                return this._deserializeWithNativeTypeId(p, ctxt, typeId);
            }
        }
        final boolean hadStartArray = p.isExpectedStartArrayToken();
        final String typeId2 = this._locateTypeId(p, ctxt);
        final JsonDeserializer<Object> deser = this._findDeserializer(ctxt, typeId2);
        if (this._typeIdVisible && !this._usesExternalId() && p.hasToken(JsonToken.START_OBJECT)) {
            final TokenBuffer tb = new TokenBuffer(null, false);
            tb.writeStartObject();
            tb.writeFieldName(this._typePropertyName);
            tb.writeString(typeId2);
            p.clearCurrentToken();
            p = JsonParserSequence.createFlattened(false, tb.asParser(p), p);
            p.nextToken();
        }
        if (hadStartArray && p.currentToken() == JsonToken.END_ARRAY) {
            return deser.getNullValue(ctxt);
        }
        final Object value = deser.deserialize(p, ctxt);
        if (hadStartArray && p.nextToken() != JsonToken.END_ARRAY) {
            ctxt.reportWrongTokenException(this.baseType(), JsonToken.END_ARRAY, "expected closing END_ARRAY after type information and deserialized value", new Object[0]);
        }
        return value;
    }
    
    protected String _locateTypeId(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        if (!p.isExpectedStartArrayToken()) {
            if (this._defaultImpl != null) {
                return this._idResolver.idFromBaseType();
            }
            ctxt.reportWrongTokenException(this.baseType(), JsonToken.START_ARRAY, "need JSON Array to contain As.WRAPPER_ARRAY type information for class " + this.baseTypeName(), new Object[0]);
            return null;
        }
        else {
            final JsonToken t = p.nextToken();
            if (t == JsonToken.VALUE_STRING) {
                final String result = p.getText();
                p.nextToken();
                return result;
            }
            ctxt.reportWrongTokenException(this.baseType(), JsonToken.VALUE_STRING, "need JSON String that contains type id (for subtype of %s)", this.baseTypeName());
            return null;
        }
    }
    
    protected boolean _usesExternalId() {
        return false;
    }
}
