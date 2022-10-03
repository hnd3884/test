package com.azul.crs.com.fasterxml.jackson.databind.deser.std;

import com.azul.crs.com.fasterxml.jackson.core.JsonProcessingException;
import java.io.OutputStream;
import com.azul.crs.com.fasterxml.jackson.databind.util.ByteBufferBackedOutputStream;
import java.io.IOException;
import com.azul.crs.com.fasterxml.jackson.databind.DeserializationContext;
import com.azul.crs.com.fasterxml.jackson.core.JsonParser;
import com.azul.crs.com.fasterxml.jackson.databind.type.LogicalType;
import java.nio.ByteBuffer;

public class ByteBufferDeserializer extends StdScalarDeserializer<ByteBuffer>
{
    private static final long serialVersionUID = 1L;
    
    protected ByteBufferDeserializer() {
        super(ByteBuffer.class);
    }
    
    @Override
    public LogicalType logicalType() {
        return LogicalType.Binary;
    }
    
    @Override
    public ByteBuffer deserialize(final JsonParser parser, final DeserializationContext cx) throws IOException {
        final byte[] b = parser.getBinaryValue();
        return ByteBuffer.wrap(b);
    }
    
    @Override
    public ByteBuffer deserialize(final JsonParser jp, final DeserializationContext ctxt, final ByteBuffer intoValue) throws IOException {
        final OutputStream out = new ByteBufferBackedOutputStream(intoValue);
        jp.readBinaryValue(ctxt.getBase64Variant(), out);
        out.close();
        return intoValue;
    }
}
