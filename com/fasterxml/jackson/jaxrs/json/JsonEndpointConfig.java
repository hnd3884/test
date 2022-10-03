package com.fasterxml.jackson.jaxrs.json;

import com.fasterxml.jackson.databind.util.JSONPObject;
import com.fasterxml.jackson.databind.util.JSONWrappedObject;
import com.fasterxml.jackson.databind.ObjectWriter;
import java.lang.annotation.Annotation;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.jaxrs.json.annotation.JSONP;
import com.fasterxml.jackson.jaxrs.cfg.EndpointConfigBase;

public class JsonEndpointConfig extends EndpointConfigBase<JsonEndpointConfig>
{
    protected JSONP.Def _jsonp;
    
    protected JsonEndpointConfig(final MapperConfig<?> config) {
        super((MapperConfig)config);
    }
    
    public static JsonEndpointConfig forReading(final ObjectReader reader, final Annotation[] annotations) {
        return (JsonEndpointConfig)((JsonEndpointConfig)new JsonEndpointConfig((MapperConfig<?>)reader.getConfig()).add(annotations, false)).initReader(reader);
    }
    
    public static JsonEndpointConfig forWriting(final ObjectWriter writer, final Annotation[] annotations, final String defaultJsonpMethod) {
        final JsonEndpointConfig config = new JsonEndpointConfig((MapperConfig<?>)writer.getConfig());
        if (defaultJsonpMethod != null) {
            config._jsonp = new JSONP.Def(defaultJsonpMethod);
        }
        return (JsonEndpointConfig)((JsonEndpointConfig)config.add(annotations, true)).initWriter(writer);
    }
    
    protected void addAnnotation(final Class<? extends Annotation> type, final Annotation annotation, final boolean forWriting) {
        if (type == JSONP.class) {
            if (forWriting) {
                this._jsonp = new JSONP.Def((JSONP)annotation);
            }
        }
        else {
            super.addAnnotation((Class)type, annotation, forWriting);
        }
    }
    
    public Object modifyBeforeWrite(final Object value) {
        return this.applyJSONP(value);
    }
    
    public Object applyJSONP(final Object value) {
        if (this._jsonp != null) {
            if (this._jsonp.prefix != null || this._jsonp.suffix != null) {
                return new JSONWrappedObject(this._jsonp.prefix, this._jsonp.suffix, value);
            }
            if (this._jsonp.method != null) {
                return new JSONPObject(this._jsonp.method, value);
            }
        }
        return value;
    }
}
