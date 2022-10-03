package com.azul.crs.com.fasterxml.jackson.databind.jsontype.impl;

import com.azul.crs.com.fasterxml.jackson.databind.DeserializationFeature;
import com.azul.crs.com.fasterxml.jackson.databind.JsonDeserializer;
import com.azul.crs.com.fasterxml.jackson.core.util.JsonParserSequence;
import java.io.IOException;
import com.azul.crs.com.fasterxml.jackson.databind.MapperFeature;
import com.azul.crs.com.fasterxml.jackson.databind.util.TokenBuffer;
import com.azul.crs.com.fasterxml.jackson.core.JsonToken;
import com.azul.crs.com.fasterxml.jackson.databind.DeserializationContext;
import com.azul.crs.com.fasterxml.jackson.core.JsonParser;
import com.azul.crs.com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.azul.crs.com.fasterxml.jackson.databind.BeanProperty;
import com.azul.crs.com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import com.azul.crs.com.fasterxml.jackson.databind.JavaType;
import com.azul.crs.com.fasterxml.jackson.annotation.JsonTypeInfo;

public class AsPropertyTypeDeserializer extends AsArrayTypeDeserializer
{
    private static final long serialVersionUID = 1L;
    protected final JsonTypeInfo.As _inclusion;
    
    public AsPropertyTypeDeserializer(final JavaType bt, final TypeIdResolver idRes, final String typePropertyName, final boolean typeIdVisible, final JavaType defaultImpl) {
        this(bt, idRes, typePropertyName, typeIdVisible, defaultImpl, JsonTypeInfo.As.PROPERTY);
    }
    
    public AsPropertyTypeDeserializer(final JavaType bt, final TypeIdResolver idRes, final String typePropertyName, final boolean typeIdVisible, final JavaType defaultImpl, final JsonTypeInfo.As inclusion) {
        super(bt, idRes, typePropertyName, typeIdVisible, defaultImpl);
        this._inclusion = inclusion;
    }
    
    public AsPropertyTypeDeserializer(final AsPropertyTypeDeserializer src, final BeanProperty property) {
        super(src, property);
        this._inclusion = src._inclusion;
    }
    
    @Override
    public TypeDeserializer forProperty(final BeanProperty prop) {
        return (prop == this._property) ? this : new AsPropertyTypeDeserializer(this, prop);
    }
    
    @Override
    public JsonTypeInfo.As getTypeInclusion() {
        return this._inclusion;
    }
    
    @Override
    public Object deserializeTypedFromObject(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        if (p.canReadTypeId()) {
            final Object typeId = p.getTypeId();
            if (typeId != null) {
                return this._deserializeWithNativeTypeId(p, ctxt, typeId);
            }
        }
        JsonToken t = p.currentToken();
        if (t == JsonToken.START_OBJECT) {
            t = p.nextToken();
        }
        else if (t != JsonToken.FIELD_NAME) {
            return this._deserializeTypedUsingDefaultImpl(p, ctxt, null);
        }
        TokenBuffer tb = null;
        final boolean ignoreCase = ctxt.isEnabled(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES);
        while (t == JsonToken.FIELD_NAME) {
            final String name = p.currentName();
            p.nextToken();
            if (name.equals(this._typePropertyName) || (ignoreCase && name.equalsIgnoreCase(this._typePropertyName))) {
                return this._deserializeTypedForId(p, ctxt, tb, p.getText());
            }
            if (tb == null) {
                tb = new TokenBuffer(p, ctxt);
            }
            tb.writeFieldName(name);
            tb.copyCurrentStructure(p);
            t = p.nextToken();
        }
        return this._deserializeTypedUsingDefaultImpl(p, ctxt, tb);
    }
    
    protected Object _deserializeTypedForId(JsonParser p, final DeserializationContext ctxt, TokenBuffer tb, final String typeId) throws IOException {
        final JsonDeserializer<Object> deser = this._findDeserializer(ctxt, typeId);
        if (this._typeIdVisible) {
            if (tb == null) {
                tb = new TokenBuffer(p, ctxt);
            }
            tb.writeFieldName(p.currentName());
            tb.writeString(typeId);
        }
        if (tb != null) {
            p.clearCurrentToken();
            p = JsonParserSequence.createFlattened(false, tb.asParser(p), p);
        }
        p.nextToken();
        return deser.deserialize(p, ctxt);
    }
    
    protected Object _deserializeTypedUsingDefaultImpl(JsonParser p, final DeserializationContext ctxt, final TokenBuffer tb) throws IOException {
        if (!this.hasDefaultImpl()) {
            final Object result = TypeDeserializer.deserializeIfNatural(p, ctxt, this._baseType);
            if (result != null) {
                return result;
            }
            if (p.isExpectedStartArrayToken()) {
                return super.deserializeTypedFromAny(p, ctxt);
            }
            if (p.hasToken(JsonToken.VALUE_STRING) && ctxt.isEnabled(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)) {
                final String str = p.getText().trim();
                if (str.isEmpty()) {
                    return null;
                }
            }
        }
        JsonDeserializer<Object> deser = this._findDefaultImplDeserializer(ctxt);
        if (deser == null) {
            String msg = String.format("missing type id property '%s'", this._typePropertyName);
            if (this._property != null) {
                msg = String.format("%s (for POJO property '%s')", msg, this._property.getName());
            }
            final JavaType t = this._handleMissingTypeId(ctxt, msg);
            if (t == null) {
                return null;
            }
            deser = ctxt.findContextualValueDeserializer(t, this._property);
        }
        if (tb != null) {
            tb.writeEndObject();
            p = tb.asParser(p);
            p.nextToken();
        }
        return deser.deserialize(p, ctxt);
    }
    
    @Override
    public Object deserializeTypedFromAny(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        if (p.hasToken(JsonToken.START_ARRAY)) {
            return super.deserializeTypedFromArray(p, ctxt);
        }
        return this.deserializeTypedFromObject(p, ctxt);
    }
}
