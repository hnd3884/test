package com.fasterxml.jackson.databind.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.cfg.MapperBuilder;
import com.fasterxml.jackson.core.json.JsonWriteFeature;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.cfg.PackageVersion;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonMapper extends ObjectMapper
{
    private static final long serialVersionUID = 1L;
    
    public JsonMapper() {
        this(new JsonFactory());
    }
    
    public JsonMapper(final JsonFactory f) {
        super(f);
    }
    
    protected JsonMapper(final JsonMapper src) {
        super(src);
    }
    
    @Override
    public JsonMapper copy() {
        this._checkInvalidCopy(JsonMapper.class);
        return new JsonMapper(this);
    }
    
    public static Builder builder() {
        return new Builder(new JsonMapper());
    }
    
    public static Builder builder(final JsonFactory streamFactory) {
        return new Builder(new JsonMapper(streamFactory));
    }
    
    public Builder rebuild() {
        return new Builder(this.copy());
    }
    
    @Override
    public Version version() {
        return PackageVersion.VERSION;
    }
    
    @Override
    public JsonFactory getFactory() {
        return this._jsonFactory;
    }
    
    public boolean isEnabled(final JsonReadFeature f) {
        return this.isEnabled(f.mappedFeature());
    }
    
    public boolean isEnabled(final JsonWriteFeature f) {
        return this.isEnabled(f.mappedFeature());
    }
    
    public static class Builder extends MapperBuilder<JsonMapper, Builder>
    {
        public Builder(final JsonMapper m) {
            super(m);
        }
        
        public Builder enable(final JsonReadFeature... features) {
            for (final JsonReadFeature f : features) {
                this._mapper.enable(f.mappedFeature());
            }
            return this;
        }
        
        public Builder disable(final JsonReadFeature... features) {
            for (final JsonReadFeature f : features) {
                this._mapper.disable(f.mappedFeature());
            }
            return this;
        }
        
        public Builder configure(final JsonReadFeature f, final boolean state) {
            if (state) {
                this._mapper.enable(f.mappedFeature());
            }
            else {
                this._mapper.disable(f.mappedFeature());
            }
            return this;
        }
        
        public Builder enable(final JsonWriteFeature... features) {
            for (final JsonWriteFeature f : features) {
                this._mapper.enable(f.mappedFeature());
            }
            return this;
        }
        
        public Builder disable(final JsonWriteFeature... features) {
            for (final JsonWriteFeature f : features) {
                this._mapper.disable(f.mappedFeature());
            }
            return this;
        }
        
        public Builder configure(final JsonWriteFeature f, final boolean state) {
            if (state) {
                this._mapper.enable(f.mappedFeature());
            }
            else {
                this._mapper.disable(f.mappedFeature());
            }
            return this;
        }
    }
}
