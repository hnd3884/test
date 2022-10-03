package com.azul.crs.com.fasterxml.jackson.databind.node;

import com.azul.crs.com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.azul.crs.com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import com.azul.crs.com.fasterxml.jackson.databind.SerializerProvider;
import com.azul.crs.com.fasterxml.jackson.core.JsonGenerator;
import com.azul.crs.com.fasterxml.jackson.core.JsonToken;
import com.azul.crs.com.fasterxml.jackson.databind.JsonNode;

public final class MissingNode extends ValueNode
{
    private static final long serialVersionUID = 1L;
    private static final MissingNode instance;
    
    protected MissingNode() {
    }
    
    protected Object readResolve() {
        return MissingNode.instance;
    }
    
    @Override
    public boolean isMissingNode() {
        return true;
    }
    
    @Override
    public <T extends JsonNode> T deepCopy() {
        return (T)this;
    }
    
    public static MissingNode getInstance() {
        return MissingNode.instance;
    }
    
    @Override
    public JsonNodeType getNodeType() {
        return JsonNodeType.MISSING;
    }
    
    @Override
    public JsonToken asToken() {
        return JsonToken.NOT_AVAILABLE;
    }
    
    @Override
    public String asText() {
        return "";
    }
    
    @Override
    public String asText(final String defaultValue) {
        return defaultValue;
    }
    
    @Override
    public final void serialize(final JsonGenerator g, final SerializerProvider provider) throws IOException, JsonProcessingException {
        g.writeNull();
    }
    
    @Override
    public void serializeWithType(final JsonGenerator g, final SerializerProvider provider, final TypeSerializer typeSer) throws IOException, JsonProcessingException {
        g.writeNull();
    }
    
    @Override
    public JsonNode require() {
        return this._reportRequiredViolation("require() called on `MissingNode`", new Object[0]);
    }
    
    @Override
    public JsonNode requireNonNull() {
        return this._reportRequiredViolation("requireNonNull() called on `MissingNode`", new Object[0]);
    }
    
    @Override
    public int hashCode() {
        return JsonNodeType.MISSING.ordinal();
    }
    
    @Override
    public String toString() {
        return "";
    }
    
    @Override
    public String toPrettyString() {
        return "";
    }
    
    @Override
    public boolean equals(final Object o) {
        return o == this;
    }
    
    static {
        instance = new MissingNode();
    }
}
