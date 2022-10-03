package com.fasterxml.jackson.module.jaxb.ser;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.JsonNode;
import java.lang.reflect.Type;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonArrayFormatVisitor;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatTypes;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.core.JsonGenerator;
import javax.activation.DataHandler;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class DataHandlerJsonSerializer extends StdSerializer<DataHandler>
{
    private static final long serialVersionUID = 1L;
    
    public DataHandlerJsonSerializer() {
        super((Class)DataHandler.class);
    }
    
    public void serialize(final DataHandler value, final JsonGenerator jgen, final SerializerProvider provider) throws IOException, JsonProcessingException {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final byte[] buffer = new byte[4096];
        final InputStream in = value.getInputStream();
        for (int len = in.read(buffer); len > 0; len = in.read(buffer)) {
            out.write(buffer, 0, len);
        }
        in.close();
        jgen.writeBinary(out.toByteArray());
    }
    
    public void acceptJsonFormatVisitor(final JsonFormatVisitorWrapper visitor, final JavaType typeHint) throws JsonMappingException {
        if (visitor != null) {
            final JsonArrayFormatVisitor v2 = visitor.expectArrayFormat(typeHint);
            if (v2 != null) {
                v2.itemsFormat(JsonFormatTypes.STRING);
            }
        }
    }
    
    public JsonNode getSchema(final SerializerProvider provider, final Type typeHint) {
        final ObjectNode o = this.createSchemaNode("array", true);
        final ObjectNode itemSchema = this.createSchemaNode("string");
        o.set("items", (JsonNode)itemSchema);
        return (JsonNode)o;
    }
}
