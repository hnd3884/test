package com.azul.crs.com.fasterxml.jackson.databind.deser.std;

import com.azul.crs.com.fasterxml.jackson.core.JsonProcessingException;
import com.azul.crs.com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.azul.crs.com.fasterxml.jackson.core.JsonParser;
import java.io.IOException;
import com.azul.crs.com.fasterxml.jackson.databind.JsonMappingException;
import com.azul.crs.com.fasterxml.jackson.databind.JsonNode;
import com.azul.crs.com.fasterxml.jackson.databind.DeserializationContext;
import com.azul.crs.com.fasterxml.jackson.databind.JavaType;
import com.azul.crs.com.fasterxml.jackson.databind.JsonDeserializer;
import com.azul.crs.com.fasterxml.jackson.databind.deser.ResolvableDeserializer;

public abstract class StdNodeBasedDeserializer<T> extends StdDeserializer<T> implements ResolvableDeserializer
{
    private static final long serialVersionUID = 1L;
    protected JsonDeserializer<Object> _treeDeserializer;
    
    protected StdNodeBasedDeserializer(final JavaType targetType) {
        super(targetType);
    }
    
    protected StdNodeBasedDeserializer(final Class<T> targetType) {
        super(targetType);
    }
    
    protected StdNodeBasedDeserializer(final StdNodeBasedDeserializer<?> src) {
        super(src);
        this._treeDeserializer = src._treeDeserializer;
    }
    
    @Override
    public void resolve(final DeserializationContext ctxt) throws JsonMappingException {
        this._treeDeserializer = ctxt.findRootValueDeserializer(ctxt.constructType(JsonNode.class));
    }
    
    public abstract T convert(final JsonNode p0, final DeserializationContext p1) throws IOException;
    
    @Override
    public T deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException {
        final JsonNode n = this._treeDeserializer.deserialize(jp, ctxt);
        return this.convert(n, ctxt);
    }
    
    @Override
    public Object deserializeWithType(final JsonParser jp, final DeserializationContext ctxt, final TypeDeserializer td) throws IOException, JsonProcessingException {
        final JsonNode n = (JsonNode)this._treeDeserializer.deserializeWithType(jp, ctxt, td);
        return this.convert(n, ctxt);
    }
}
