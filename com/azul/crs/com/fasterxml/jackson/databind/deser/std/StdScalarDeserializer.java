package com.azul.crs.com.fasterxml.jackson.databind.deser.std;

import com.azul.crs.com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import com.azul.crs.com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.azul.crs.com.fasterxml.jackson.databind.DeserializationContext;
import com.azul.crs.com.fasterxml.jackson.core.JsonParser;
import com.azul.crs.com.fasterxml.jackson.databind.util.AccessPattern;
import com.azul.crs.com.fasterxml.jackson.databind.DeserializationConfig;
import com.azul.crs.com.fasterxml.jackson.databind.type.LogicalType;
import com.azul.crs.com.fasterxml.jackson.databind.JavaType;

public abstract class StdScalarDeserializer<T> extends StdDeserializer<T>
{
    private static final long serialVersionUID = 1L;
    
    protected StdScalarDeserializer(final Class<?> vc) {
        super(vc);
    }
    
    protected StdScalarDeserializer(final JavaType valueType) {
        super(valueType);
    }
    
    protected StdScalarDeserializer(final StdScalarDeserializer<?> src) {
        super(src);
    }
    
    @Override
    public LogicalType logicalType() {
        return LogicalType.OtherScalar;
    }
    
    @Override
    public Boolean supportsUpdate(final DeserializationConfig config) {
        return Boolean.FALSE;
    }
    
    @Override
    public AccessPattern getNullAccessPattern() {
        return AccessPattern.ALWAYS_NULL;
    }
    
    @Override
    public AccessPattern getEmptyAccessPattern() {
        return AccessPattern.CONSTANT;
    }
    
    @Override
    public Object deserializeWithType(final JsonParser p, final DeserializationContext ctxt, final TypeDeserializer typeDeserializer) throws IOException {
        return typeDeserializer.deserializeTypedFromScalar(p, ctxt);
    }
    
    @Override
    public T deserialize(final JsonParser p, final DeserializationContext ctxt, final T intoValue) throws IOException {
        ctxt.handleBadMerge(this);
        return this.deserialize(p, ctxt);
    }
}
