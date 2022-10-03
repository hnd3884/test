package org.glassfish.jersey.jackson;

import javax.ws.rs.core.Configuration;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.MessageBodyReader;
import org.glassfish.jersey.jackson.internal.FilteringJacksonJaxbJsonProvider;
import org.glassfish.jersey.jackson.internal.JacksonFilteringFeature;
import org.glassfish.jersey.message.filtering.EntityFilteringFeature;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.base.JsonMappingExceptionMapper;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.base.JsonParseExceptionMapper;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import org.glassfish.jersey.internal.util.PropertiesHelper;
import org.glassfish.jersey.CommonProperties;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.core.Feature;

public class JacksonFeature implements Feature
{
    private static final String JSON_FEATURE;
    
    public boolean configure(final FeatureContext context) {
        final Configuration config = context.getConfiguration();
        final String jsonFeature = (String)CommonProperties.getValue(config.getProperties(), config.getRuntimeType(), "jersey.config.jsonFeature", (Object)JacksonFeature.JSON_FEATURE, (Class)String.class);
        if (!JacksonFeature.JSON_FEATURE.equalsIgnoreCase(jsonFeature)) {
            return false;
        }
        context.property(PropertiesHelper.getPropertyNameForRuntime("jersey.config.jsonFeature", config.getRuntimeType()), (Object)JacksonFeature.JSON_FEATURE);
        if (!config.isRegistered((Class)JacksonJaxbJsonProvider.class)) {
            context.register((Class)JsonParseExceptionMapper.class);
            context.register((Class)JsonMappingExceptionMapper.class);
            if (EntityFilteringFeature.enabled(config)) {
                context.register((Class)JacksonFilteringFeature.class);
                context.register((Class)FilteringJacksonJaxbJsonProvider.class, new Class[] { MessageBodyReader.class, MessageBodyWriter.class });
            }
            else {
                context.register((Class)JacksonJaxbJsonProvider.class, new Class[] { MessageBodyReader.class, MessageBodyWriter.class });
            }
        }
        return true;
    }
    
    static {
        JSON_FEATURE = JacksonFeature.class.getSimpleName();
    }
}
