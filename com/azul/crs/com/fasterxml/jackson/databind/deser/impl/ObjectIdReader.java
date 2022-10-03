package com.azul.crs.com.fasterxml.jackson.databind.deser.impl;

import java.io.IOException;
import com.azul.crs.com.fasterxml.jackson.databind.DeserializationContext;
import com.azul.crs.com.fasterxml.jackson.core.JsonParser;
import com.azul.crs.com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.azul.crs.com.fasterxml.jackson.databind.JsonDeserializer;
import com.azul.crs.com.fasterxml.jackson.annotation.ObjectIdResolver;
import com.azul.crs.com.fasterxml.jackson.annotation.ObjectIdGenerator;
import com.azul.crs.com.fasterxml.jackson.databind.PropertyName;
import com.azul.crs.com.fasterxml.jackson.databind.JavaType;
import java.io.Serializable;

public class ObjectIdReader implements Serializable
{
    private static final long serialVersionUID = 1L;
    protected final JavaType _idType;
    public final PropertyName propertyName;
    public final ObjectIdGenerator<?> generator;
    public final ObjectIdResolver resolver;
    protected final JsonDeserializer<Object> _deserializer;
    public final SettableBeanProperty idProperty;
    
    protected ObjectIdReader(final JavaType t, final PropertyName propName, final ObjectIdGenerator<?> gen, final JsonDeserializer<?> deser, final SettableBeanProperty idProp, final ObjectIdResolver resolver) {
        this._idType = t;
        this.propertyName = propName;
        this.generator = gen;
        this.resolver = resolver;
        this._deserializer = (JsonDeserializer<Object>)deser;
        this.idProperty = idProp;
    }
    
    public static ObjectIdReader construct(final JavaType idType, final PropertyName propName, final ObjectIdGenerator<?> generator, final JsonDeserializer<?> deser, final SettableBeanProperty idProp, final ObjectIdResolver resolver) {
        return new ObjectIdReader(idType, propName, generator, deser, idProp, resolver);
    }
    
    public JsonDeserializer<Object> getDeserializer() {
        return this._deserializer;
    }
    
    public JavaType getIdType() {
        return this._idType;
    }
    
    public boolean maySerializeAsObject() {
        return this.generator.maySerializeAsObject();
    }
    
    public boolean isValidReferencePropertyName(final String name, final JsonParser parser) {
        return this.generator.isValidReferencePropertyName(name, parser);
    }
    
    public Object readObjectReference(final JsonParser jp, final DeserializationContext ctxt) throws IOException {
        return this._deserializer.deserialize(jp, ctxt);
    }
}
