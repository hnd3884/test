package com.azul.crs.shared.models;

import java.util.Objects;
import com.azul.crs.com.fasterxml.jackson.annotation.JsonCreator;
import com.azul.crs.com.fasterxml.jackson.annotation.JsonValue;
import com.azul.crs.com.fasterxml.jackson.databind.node.ObjectNode;

public class GenericPayload extends Payload
{
    @JsonValue
    private ObjectNode node;
    
    @JsonCreator
    public GenericPayload(final ObjectNode node) {
        this.node = node;
    }
    
    @Override
    public ObjectNode toObjectNode() {
        return this.node;
    }
    
    @Override
    public String toJson() {
        return this.node.toString();
    }
    
    @Override
    public String toString() {
        return this.node.toString();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final GenericPayload that = (GenericPayload)o;
        return Objects.equals(this.node, that.node);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.node);
    }
}
