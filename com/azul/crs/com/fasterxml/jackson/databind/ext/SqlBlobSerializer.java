package com.azul.crs.com.fasterxml.jackson.databind.ext;

import com.azul.crs.com.fasterxml.jackson.databind.JsonMappingException;
import com.azul.crs.com.fasterxml.jackson.databind.jsonFormatVisitors.JsonArrayFormatVisitor;
import com.azul.crs.com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatTypes;
import com.azul.crs.com.fasterxml.jackson.databind.JavaType;
import com.azul.crs.com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import java.io.InputStream;
import java.sql.SQLException;
import com.azul.crs.com.fasterxml.jackson.core.type.WritableTypeId;
import com.azul.crs.com.fasterxml.jackson.core.JsonToken;
import com.azul.crs.com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import java.io.IOException;
import com.azul.crs.com.fasterxml.jackson.core.JsonGenerator;
import com.azul.crs.com.fasterxml.jackson.databind.SerializerProvider;
import com.azul.crs.com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import java.sql.Blob;
import com.azul.crs.com.fasterxml.jackson.databind.ser.std.StdScalarSerializer;

@JacksonStdImpl
public class SqlBlobSerializer extends StdScalarSerializer<Blob>
{
    public SqlBlobSerializer() {
        super(Blob.class);
    }
    
    @Override
    public boolean isEmpty(final SerializerProvider provider, final Blob value) {
        return value == null;
    }
    
    @Override
    public void serialize(final Blob value, final JsonGenerator gen, final SerializerProvider ctxt) throws IOException {
        this._writeValue(value, gen, ctxt);
    }
    
    @Override
    public void serializeWithType(final Blob value, final JsonGenerator gen, final SerializerProvider ctxt, final TypeSerializer typeSer) throws IOException {
        final WritableTypeId typeIdDef = typeSer.writeTypePrefix(gen, typeSer.typeId(value, JsonToken.VALUE_EMBEDDED_OBJECT));
        this._writeValue(value, gen, ctxt);
        typeSer.writeTypeSuffix(gen, typeIdDef);
    }
    
    protected void _writeValue(final Blob value, final JsonGenerator gen, final SerializerProvider ctxt) throws IOException {
        InputStream in = null;
        try {
            in = value.getBinaryStream();
        }
        catch (final SQLException e) {
            ctxt.reportMappingProblem(e, "Failed to access `java.sql.Blob` value to write as binary value", new Object[0]);
        }
        gen.writeBinary(ctxt.getConfig().getBase64Variant(), in, -1);
    }
    
    @Override
    public void acceptJsonFormatVisitor(final JsonFormatVisitorWrapper visitor, final JavaType typeHint) throws JsonMappingException {
        final JsonArrayFormatVisitor v2 = visitor.expectArrayFormat(typeHint);
        if (v2 != null) {
            v2.itemsFormat(JsonFormatTypes.INTEGER);
        }
    }
}
