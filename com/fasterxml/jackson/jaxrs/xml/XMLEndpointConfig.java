package com.fasterxml.jackson.jaxrs.xml;

import com.fasterxml.jackson.databind.ObjectWriter;
import java.lang.annotation.Annotation;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.jaxrs.cfg.EndpointConfigBase;

public class XMLEndpointConfig extends EndpointConfigBase<XMLEndpointConfig>
{
    protected XMLEndpointConfig(final MapperConfig<?> config) {
        super((MapperConfig)config);
    }
    
    public static XMLEndpointConfig forReading(final ObjectReader reader, final Annotation[] annotations) {
        return (XMLEndpointConfig)((XMLEndpointConfig)new XMLEndpointConfig((MapperConfig<?>)reader.getConfig()).add(annotations, false)).initReader(reader);
    }
    
    public static XMLEndpointConfig forWriting(final ObjectWriter writer, final Annotation[] annotations) {
        return (XMLEndpointConfig)((XMLEndpointConfig)new XMLEndpointConfig((MapperConfig<?>)writer.getConfig()).add(annotations, true)).initWriter(writer);
    }
    
    public Object modifyBeforeWrite(final Object value) {
        return value;
    }
}
