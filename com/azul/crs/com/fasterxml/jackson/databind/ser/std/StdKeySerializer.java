package com.azul.crs.com.fasterxml.jackson.databind.ser.std;

import java.io.IOException;
import com.azul.crs.com.fasterxml.jackson.databind.SerializerProvider;
import com.azul.crs.com.fasterxml.jackson.core.JsonGenerator;

@Deprecated
public class StdKeySerializer extends StdSerializer<Object>
{
    public StdKeySerializer() {
        super(Object.class);
    }
    
    @Override
    public void serialize(final Object value, final JsonGenerator g, final SerializerProvider provider) throws IOException {
        g.writeFieldName(value.toString());
    }
}
