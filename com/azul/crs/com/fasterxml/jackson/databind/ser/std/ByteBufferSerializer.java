package com.azul.crs.com.fasterxml.jackson.databind.ser.std;

import com.azul.crs.com.fasterxml.jackson.databind.JsonMappingException;
import com.azul.crs.com.fasterxml.jackson.databind.jsonFormatVisitors.JsonArrayFormatVisitor;
import com.azul.crs.com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatTypes;
import com.azul.crs.com.fasterxml.jackson.databind.JavaType;
import com.azul.crs.com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import java.io.IOException;
import java.io.InputStream;
import com.azul.crs.com.fasterxml.jackson.databind.util.ByteBufferBackedInputStream;
import com.azul.crs.com.fasterxml.jackson.databind.SerializerProvider;
import com.azul.crs.com.fasterxml.jackson.core.JsonGenerator;
import java.nio.ByteBuffer;

public class ByteBufferSerializer extends StdScalarSerializer<ByteBuffer>
{
    public ByteBufferSerializer() {
        super(ByteBuffer.class);
    }
    
    @Override
    public void serialize(final ByteBuffer bbuf, final JsonGenerator gen, final SerializerProvider provider) throws IOException {
        if (bbuf.hasArray()) {
            final int pos = bbuf.position();
            gen.writeBinary(bbuf.array(), bbuf.arrayOffset() + pos, bbuf.limit() - pos);
            return;
        }
        final ByteBuffer copy = bbuf.asReadOnlyBuffer();
        if (copy.position() > 0) {
            copy.rewind();
        }
        final InputStream in = new ByteBufferBackedInputStream(copy);
        gen.writeBinary(in, copy.remaining());
        in.close();
    }
    
    @Override
    public void acceptJsonFormatVisitor(final JsonFormatVisitorWrapper visitor, final JavaType typeHint) throws JsonMappingException {
        final JsonArrayFormatVisitor v2 = visitor.expectArrayFormat(typeHint);
        if (v2 != null) {
            v2.itemsFormat(JsonFormatTypes.INTEGER);
        }
    }
}
