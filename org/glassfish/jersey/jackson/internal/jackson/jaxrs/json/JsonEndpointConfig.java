package org.glassfish.jersey.jackson.internal.jackson.jaxrs.json;

import com.fasterxml.jackson.databind.util.JSONPObject;
import com.fasterxml.jackson.databind.util.JSONWrappedObject;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.json.annotation.JacksonFeatures;
import com.fasterxml.jackson.databind.ObjectWriter;
import java.lang.annotation.Annotation;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.json.annotation.JSONP;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.cfg.EndpointConfigBase;

public class JsonEndpointConfig extends EndpointConfigBase<JsonEndpointConfig>
{
    protected JSONP.Def _jsonp;
    
    protected JsonEndpointConfig(final MapperConfig<?> config) {
        super(config);
    }
    
    public static JsonEndpointConfig forReading(final ObjectReader reader, final Annotation[] annotations) {
        return new JsonEndpointConfig((MapperConfig<?>)reader.getConfig()).add(annotations, false).initReader(reader);
    }
    
    public static JsonEndpointConfig forWriting(final ObjectWriter writer, final Annotation[] annotations, final String defaultJsonpMethod) {
        final JsonEndpointConfig config = new JsonEndpointConfig((MapperConfig<?>)writer.getConfig());
        if (defaultJsonpMethod != null) {
            config._jsonp = new JSONP.Def(defaultJsonpMethod);
        }
        return config.add(annotations, true).initWriter(writer);
    }
    
    @Override
    protected void addAnnotation(final Class<? extends Annotation> type, final Annotation annotation, final boolean forWriting) {
        if (type == JSONP.class) {
            if (forWriting) {
                this._jsonp = new JSONP.Def((JSONP)annotation);
            }
        }
        else if (type == JacksonFeatures.class) {
            final JacksonFeatures feats = (JacksonFeatures)annotation;
            if (forWriting) {
                this._serEnable = EndpointConfigBase.nullIfEmpty(feats.serializationEnable());
                this._serDisable = EndpointConfigBase.nullIfEmpty(feats.serializationDisable());
            }
            else {
                this._deserEnable = EndpointConfigBase.nullIfEmpty(feats.deserializationEnable());
                this._deserDisable = EndpointConfigBase.nullIfEmpty(feats.deserializationDisable());
            }
        }
        else {
            super.addAnnotation(type, annotation, forWriting);
        }
    }
    
    @Override
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
