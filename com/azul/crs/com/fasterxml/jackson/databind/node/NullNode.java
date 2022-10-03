package com.azul.crs.com.fasterxml.jackson.databind.node;

import java.io.IOException;
import com.azul.crs.com.fasterxml.jackson.databind.SerializerProvider;
import com.azul.crs.com.fasterxml.jackson.core.JsonGenerator;
import com.azul.crs.com.fasterxml.jackson.databind.JsonNode;
import com.azul.crs.com.fasterxml.jackson.core.JsonToken;

public class NullNode extends ValueNode
{
    private static final long serialVersionUID = 1L;
    public static final NullNode instance;
    
    protected NullNode() {
    }
    
    protected Object readResolve() {
        return NullNode.instance;
    }
    
    public static NullNode getInstance() {
        return NullNode.instance;
    }
    
    @Override
    public JsonNodeType getNodeType() {
        return JsonNodeType.NULL;
    }
    
    @Override
    public JsonToken asToken() {
        return JsonToken.VALUE_NULL;
    }
    
    @Override
    public String asText(final String defaultValue) {
        return defaultValue;
    }
    
    @Override
    public String asText() {
        return "null";
    }
    
    @Override
    public JsonNode requireNonNull() {
        return this._reportRequiredViolation("requireNonNull() called on `NullNode`", new Object[0]);
    }
    
    @Override
    public final void serialize(final JsonGenerator g, final SerializerProvider provider) throws IOException {
        provider.defaultSerializeNull(g);
    }
    
    @Override
    public boolean equals(final Object o) {
        return o == this || o instanceof NullNode;
    }
    
    @Override
    public int hashCode() {
        return JsonNodeType.NULL.ordinal();
    }
    
    static {
        instance = new NullNode();
    }
}
