package com.fasterxml.jackson.dataformat.xml.deser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import java.io.IOException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;

public class XmlStringDeserializer extends StdScalarDeserializer<String>
{
    private static final long serialVersionUID = 1L;
    
    public XmlStringDeserializer() {
        super((Class)String.class);
    }
    
    public boolean isCachable() {
        return true;
    }
    
    public Object getEmptyValue(final DeserializationContext ctxt) throws JsonMappingException {
        return "";
    }
    
    public String deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        if (p.hasToken(JsonToken.VALUE_STRING)) {
            return p.getText();
        }
        final JsonToken t = p.getCurrentToken();
        if (t == JsonToken.START_ARRAY) {
            return (String)this._deserializeFromArray(p, ctxt);
        }
        if (t == JsonToken.VALUE_EMBEDDED_OBJECT) {
            final Object ob = p.getEmbeddedObject();
            if (ob == null) {
                return null;
            }
            if (ob instanceof byte[]) {
                return ctxt.getBase64Variant().encode((byte[])ob, false);
            }
            return ob.toString();
        }
        else {
            final String text = p.getValueAsString((String)null);
            if (text != null || t == JsonToken.VALUE_NULL) {
                return text;
            }
            return (String)ctxt.handleUnexpectedToken(this._valueClass, p);
        }
    }
    
    public String deserializeWithType(final JsonParser p, final DeserializationContext ctxt, final TypeDeserializer typeDeserializer) throws IOException {
        return this.deserialize(p, ctxt);
    }
}
