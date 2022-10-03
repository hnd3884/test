package com.azul.crs.com.fasterxml.jackson.databind.ser.impl;

import java.util.Iterator;
import com.azul.crs.com.fasterxml.jackson.core.type.WritableTypeId;
import com.azul.crs.com.fasterxml.jackson.core.JsonToken;
import com.azul.crs.com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import java.io.IOException;
import com.azul.crs.com.fasterxml.jackson.databind.SerializationFeature;
import com.azul.crs.com.fasterxml.jackson.databind.SerializerProvider;
import com.azul.crs.com.fasterxml.jackson.core.JsonGenerator;
import com.azul.crs.com.fasterxml.jackson.databind.JsonMappingException;
import com.azul.crs.com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatTypes;
import com.azul.crs.com.fasterxml.jackson.databind.jsonFormatVisitors.JsonArrayFormatVisitor;
import com.azul.crs.com.fasterxml.jackson.databind.JsonNode;
import com.azul.crs.com.fasterxml.jackson.databind.JsonSerializer;
import com.azul.crs.com.fasterxml.jackson.databind.BeanProperty;
import com.azul.crs.com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import java.util.Collection;
import com.azul.crs.com.fasterxml.jackson.databind.ser.std.StaticListSerializerBase;

@JacksonStdImpl
public class StringCollectionSerializer extends StaticListSerializerBase<Collection<String>>
{
    public static final StringCollectionSerializer instance;
    
    protected StringCollectionSerializer() {
        super(Collection.class);
    }
    
    protected StringCollectionSerializer(final StringCollectionSerializer src, final Boolean unwrapSingle) {
        super(src, unwrapSingle);
    }
    
    @Override
    public JsonSerializer<?> _withResolved(final BeanProperty prop, final Boolean unwrapSingle) {
        return new StringCollectionSerializer(this, unwrapSingle);
    }
    
    @Override
    protected JsonNode contentSchema() {
        return this.createSchemaNode("string", true);
    }
    
    @Override
    protected void acceptContentVisitor(final JsonArrayFormatVisitor visitor) throws JsonMappingException {
        visitor.itemsFormat(JsonFormatTypes.STRING);
    }
    
    @Override
    public void serialize(final Collection<String> value, final JsonGenerator g, final SerializerProvider provider) throws IOException {
        final int len = value.size();
        if (len == 1 && ((this._unwrapSingle == null && provider.isEnabled(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED)) || this._unwrapSingle == Boolean.TRUE)) {
            this.serializeContents(value, g, provider);
            return;
        }
        g.writeStartArray(value, len);
        this.serializeContents(value, g, provider);
        g.writeEndArray();
    }
    
    @Override
    public void serializeWithType(final Collection<String> value, final JsonGenerator g, final SerializerProvider provider, final TypeSerializer typeSer) throws IOException {
        final WritableTypeId typeIdDef = typeSer.writeTypePrefix(g, typeSer.typeId(value, JsonToken.START_ARRAY));
        g.setCurrentValue(value);
        this.serializeContents(value, g, provider);
        typeSer.writeTypeSuffix(g, typeIdDef);
    }
    
    private final void serializeContents(final Collection<String> value, final JsonGenerator g, final SerializerProvider provider) throws IOException {
        int i = 0;
        try {
            for (final String str : value) {
                if (str == null) {
                    provider.defaultSerializeNull(g);
                }
                else {
                    g.writeString(str);
                }
                ++i;
            }
        }
        catch (final Exception e) {
            this.wrapAndThrow(provider, e, value, i);
        }
    }
    
    static {
        instance = new StringCollectionSerializer();
    }
}
