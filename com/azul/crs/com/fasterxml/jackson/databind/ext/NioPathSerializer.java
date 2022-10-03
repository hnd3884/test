package com.azul.crs.com.fasterxml.jackson.databind.ext;

import com.azul.crs.com.fasterxml.jackson.core.type.WritableTypeId;
import com.azul.crs.com.fasterxml.jackson.core.JsonToken;
import com.azul.crs.com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import java.io.IOException;
import com.azul.crs.com.fasterxml.jackson.databind.SerializerProvider;
import com.azul.crs.com.fasterxml.jackson.core.JsonGenerator;
import java.nio.file.Path;
import com.azul.crs.com.fasterxml.jackson.databind.ser.std.StdScalarSerializer;

public class NioPathSerializer extends StdScalarSerializer<Path>
{
    private static final long serialVersionUID = 1L;
    
    public NioPathSerializer() {
        super(Path.class);
    }
    
    @Override
    public void serialize(final Path value, final JsonGenerator gen, final SerializerProvider serializers) throws IOException {
        gen.writeString(value.toUri().toString());
    }
    
    @Override
    public void serializeWithType(final Path value, final JsonGenerator g, final SerializerProvider provider, final TypeSerializer typeSer) throws IOException {
        final WritableTypeId typeIdDef = typeSer.writeTypePrefix(g, typeSer.typeId(value, Path.class, JsonToken.VALUE_STRING));
        this.serialize(value, g, provider);
        typeSer.writeTypeSuffix(g, typeIdDef);
    }
}
