package com.fasterxml.jackson.databind;

import java.io.IOException;
import com.fasterxml.jackson.core.format.MatchStrength;
import com.fasterxml.jackson.core.format.InputAccessor;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.JsonFactory;

public class MappingJsonFactory extends JsonFactory
{
    private static final long serialVersionUID = -1L;
    
    public MappingJsonFactory() {
        this(null);
    }
    
    public MappingJsonFactory(final ObjectMapper mapper) {
        super((ObjectCodec)mapper);
        if (mapper == null) {
            this.setCodec((ObjectCodec)new ObjectMapper(this));
        }
    }
    
    public MappingJsonFactory(final JsonFactory src, final ObjectMapper mapper) {
        super(src, (ObjectCodec)mapper);
        if (mapper == null) {
            this.setCodec((ObjectCodec)new ObjectMapper(this));
        }
    }
    
    public final ObjectMapper getCodec() {
        return (ObjectMapper)this._objectCodec;
    }
    
    public JsonFactory copy() {
        this._checkInvalidCopy((Class)MappingJsonFactory.class);
        return new MappingJsonFactory(this, null);
    }
    
    public String getFormatName() {
        return "JSON";
    }
    
    public MatchStrength hasFormat(final InputAccessor acc) throws IOException {
        if (this.getClass() == MappingJsonFactory.class) {
            return this.hasJSONFormat(acc);
        }
        return null;
    }
}
