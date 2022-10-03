package com.azul.crs.com.fasterxml.jackson.databind.node;

import java.io.IOException;
import com.azul.crs.com.fasterxml.jackson.databind.JsonNode;
import com.azul.crs.com.fasterxml.jackson.databind.ObjectReader;
import com.azul.crs.com.fasterxml.jackson.databind.ObjectWriter;
import com.azul.crs.com.fasterxml.jackson.databind.json.JsonMapper;

final class InternalNodeMapper
{
    private static final JsonMapper JSON_MAPPER;
    private static final ObjectWriter STD_WRITER;
    private static final ObjectWriter PRETTY_WRITER;
    private static final ObjectReader NODE_READER;
    
    public static String nodeToString(final JsonNode n) {
        try {
            return InternalNodeMapper.STD_WRITER.writeValueAsString(n);
        }
        catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static String nodeToPrettyString(final JsonNode n) {
        try {
            return InternalNodeMapper.PRETTY_WRITER.writeValueAsString(n);
        }
        catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static byte[] valueToBytes(final Object value) throws IOException {
        return InternalNodeMapper.JSON_MAPPER.writeValueAsBytes(value);
    }
    
    public static JsonNode bytesToNode(final byte[] json) throws IOException {
        return InternalNodeMapper.NODE_READER.readValue(json);
    }
    
    static {
        JSON_MAPPER = new JsonMapper();
        STD_WRITER = InternalNodeMapper.JSON_MAPPER.writer();
        PRETTY_WRITER = InternalNodeMapper.JSON_MAPPER.writer().withDefaultPrettyPrinter();
        NODE_READER = InternalNodeMapper.JSON_MAPPER.readerFor(JsonNode.class);
    }
}
