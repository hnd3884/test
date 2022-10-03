package com.fasterxml.jackson.databind.node;

import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.JsonParser;
import java.io.Serializable;
import com.fasterxml.jackson.databind.JsonNode;

public abstract class BaseJsonNode extends JsonNode implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    Object writeReplace() {
        return NodeSerialization.from(this);
    }
    
    protected BaseJsonNode() {
    }
    
    @Override
    public final JsonNode findPath(final String fieldName) {
        final JsonNode value = this.findValue(fieldName);
        if (value == null) {
            return MissingNode.getInstance();
        }
        return value;
    }
    
    public abstract int hashCode();
    
    @Override
    public JsonNode required(final String fieldName) {
        return this._reportRequiredViolation("Node of type `%s` has no fields", this.getClass().getSimpleName());
    }
    
    @Override
    public JsonNode required(final int index) {
        return this._reportRequiredViolation("Node of type `%s` has no indexed values", this.getClass().getSimpleName());
    }
    
    public JsonParser traverse() {
        return (JsonParser)new TreeTraversingParser(this);
    }
    
    public JsonParser traverse(final ObjectCodec codec) {
        return (JsonParser)new TreeTraversingParser(this, codec);
    }
    
    public abstract JsonToken asToken();
    
    public JsonParser.NumberType numberType() {
        return null;
    }
    
    public abstract void serialize(final JsonGenerator p0, final SerializerProvider p1) throws IOException, JsonProcessingException;
    
    public abstract void serializeWithType(final JsonGenerator p0, final SerializerProvider p1, final TypeSerializer p2) throws IOException, JsonProcessingException;
    
    @Override
    public String toString() {
        return InternalNodeMapper.nodeToString(this);
    }
    
    @Override
    public String toPrettyString() {
        return InternalNodeMapper.nodeToPrettyString(this);
    }
}
