package com.azul.crs.shared.models;

import com.azul.crs.com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Map;
import com.azul.crs.com.fasterxml.jackson.core.type.TypeReference;
import com.azul.crs.com.fasterxml.jackson.core.TreeNode;
import com.azul.crs.com.fasterxml.jackson.databind.JsonNode;
import java.io.InputStream;
import com.azul.crs.com.fasterxml.jackson.core.JsonProcessingException;
import com.azul.crs.com.fasterxml.jackson.databind.JavaType;
import java.util.Collection;
import java.util.List;
import java.io.IOException;
import com.azul.crs.com.fasterxml.jackson.databind.node.ObjectNode;
import com.azul.crs.com.fasterxml.jackson.databind.ObjectMapper;

public abstract class Payload
{
    private static ObjectMapper objectMapper;
    
    public static ObjectMapper objectMapper() {
        return Payload.objectMapper;
    }
    
    public ObjectNode toObjectNode() {
        return Payload.objectMapper.valueToTree(this);
    }
    
    public static GenericPayload genericPayload(final String json) {
        try {
            if (json == null) {
                return null;
            }
            final ObjectNode node = (ObjectNode)Payload.objectMapper.readTree(json);
            return new GenericPayload(node);
        }
        catch (final IOException ioe) {
            throw new IllegalArgumentException(ioe);
        }
    }
    
    public static <T extends Payload> List<T> fromJsonArray(final String jsonArray, final Class<T> type) throws IOException {
        return Payload.objectMapper.readValue(jsonArray, Payload.objectMapper.getTypeFactory().constructCollectionType(List.class, type));
    }
    
    public static String toJsonArray(final Collection<? extends Payload> items) throws JsonProcessingException {
        return Payload.objectMapper.writeValueAsString(items);
    }
    
    public static <T extends Payload> List<T> fromJsonArrayNullable(final String jsonArray, final Class<T> type) throws IOException {
        return (List<T>)((jsonArray != null) ? fromJsonArray(jsonArray, (Class<Payload>)type) : null);
    }
    
    public static String toJsonArrayNullable(final Collection<? extends Payload> items) throws JsonProcessingException {
        return (items != null) ? toJsonArray(items) : null;
    }
    
    public static <T extends Payload> T fromJson(final String json, final Class<T> type) throws IOException {
        return Payload.objectMapper.readValue(json, type);
    }
    
    public static <T extends Payload> T fromJson(final InputStream inputStream, final Class<T> type) throws IOException {
        return Payload.objectMapper.readValue(inputStream, type);
    }
    
    public static <T extends Payload> T fromJsonUnchecked(final String json, final Class<T> type) {
        try {
            return Payload.objectMapper.readValue(json, type);
        }
        catch (final IOException ioe) {
            throw new IllegalArgumentException(ioe);
        }
    }
    
    public static <T extends Payload> T fromJson(final JsonNode json, final Class<T> type) throws JsonProcessingException {
        return Payload.objectMapper.treeToValue(json, type);
    }
    
    public static <T extends Payload> T fromJson(final String json, final TypeReference<T> type) throws IOException {
        return Payload.objectMapper.readValue(json, type);
    }
    
    public static <T extends Payload> T fromMap(final Map map, final Class<T> type) {
        return Payload.objectMapper.convertValue(map, type);
    }
    
    public String toJson() throws JsonProcessingException {
        return Payload.objectMapper.writeValueAsString(this);
    }
    
    public String toJsonUnchecked() {
        try {
            return this.toJson();
        }
        catch (final JsonProcessingException jpe) {
            throw new IllegalStateException(jpe);
        }
    }
    
    public Object toObjectUnchecked() {
        try {
            final String json = this.toJson();
            return Payload.objectMapper.readValue(json, Object.class);
        }
        catch (final Exception e) {
            throw new IllegalStateException(e);
        }
    }
    
    public Map toMapUnchecked() {
        return Payload.objectMapper.convertValue(this, (TypeReference<Map>)new TypeReference<Map<String, Object>>() {});
    }
    
    @Override
    public String toString() {
        return this.toJsonUnchecked();
    }
    
    static {
        (Payload.objectMapper = new ObjectMapper()).setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }
}
