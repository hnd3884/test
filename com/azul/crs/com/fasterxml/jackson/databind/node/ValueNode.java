package com.azul.crs.com.fasterxml.jackson.databind.node;

import com.azul.crs.com.fasterxml.jackson.core.TreeNode;
import java.util.List;
import java.io.IOException;
import com.azul.crs.com.fasterxml.jackson.core.type.WritableTypeId;
import com.azul.crs.com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.azul.crs.com.fasterxml.jackson.databind.SerializerProvider;
import com.azul.crs.com.fasterxml.jackson.core.JsonGenerator;
import com.azul.crs.com.fasterxml.jackson.core.JsonToken;
import com.azul.crs.com.fasterxml.jackson.databind.JsonNode;
import com.azul.crs.com.fasterxml.jackson.core.JsonPointer;

public abstract class ValueNode extends BaseJsonNode
{
    private static final long serialVersionUID = 1L;
    
    protected ValueNode() {
    }
    
    @Override
    protected JsonNode _at(final JsonPointer ptr) {
        return MissingNode.getInstance();
    }
    
    @Override
    public <T extends JsonNode> T deepCopy() {
        return (T)this;
    }
    
    @Override
    public abstract JsonToken asToken();
    
    @Override
    public void serializeWithType(final JsonGenerator g, final SerializerProvider provider, final TypeSerializer typeSer) throws IOException {
        final WritableTypeId typeIdDef = typeSer.writeTypePrefix(g, typeSer.typeId(this, this.asToken()));
        this.serialize(g, provider);
        typeSer.writeTypeSuffix(g, typeIdDef);
    }
    
    @Override
    public boolean isEmpty() {
        return true;
    }
    
    @Override
    public final JsonNode get(final int index) {
        return null;
    }
    
    @Override
    public final JsonNode path(final int index) {
        return MissingNode.getInstance();
    }
    
    @Override
    public final boolean has(final int index) {
        return false;
    }
    
    @Override
    public final boolean hasNonNull(final int index) {
        return false;
    }
    
    @Override
    public final JsonNode get(final String fieldName) {
        return null;
    }
    
    @Override
    public final JsonNode path(final String fieldName) {
        return MissingNode.getInstance();
    }
    
    @Override
    public final boolean has(final String fieldName) {
        return false;
    }
    
    @Override
    public final boolean hasNonNull(final String fieldName) {
        return false;
    }
    
    @Override
    public final JsonNode findValue(final String fieldName) {
        return null;
    }
    
    @Override
    public final ObjectNode findParent(final String fieldName) {
        return null;
    }
    
    @Override
    public final List<JsonNode> findValues(final String fieldName, final List<JsonNode> foundSoFar) {
        return foundSoFar;
    }
    
    @Override
    public final List<String> findValuesAsText(final String fieldName, final List<String> foundSoFar) {
        return foundSoFar;
    }
    
    @Override
    public final List<JsonNode> findParents(final String fieldName, final List<JsonNode> foundSoFar) {
        return foundSoFar;
    }
}
