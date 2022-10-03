package org.glassfish.jersey.jackson.internal.jackson.jaxrs.json;

import org.glassfish.jersey.jackson.internal.jackson.jaxrs.cfg.EndpointConfigBase;
import com.fasterxml.jackson.databind.ObjectWriter;
import java.lang.annotation.Annotation;
import com.fasterxml.jackson.databind.ObjectReader;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.core.MediaType;
import com.fasterxml.jackson.core.Version;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Providers;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.cfg.Annotations;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.ext.Provider;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.base.ProviderBase;

@Provider
@Consumes({ "*/*" })
@Produces({ "*/*" })
public class JacksonJsonProvider extends ProviderBase<JacksonJsonProvider, ObjectMapper, JsonEndpointConfig, JsonMapperConfigurator>
{
    public static final String MIME_JAVASCRIPT = "application/javascript";
    public static final String MIME_JAVASCRIPT_MS = "application/x-javascript";
    public static final Annotations[] BASIC_ANNOTATIONS;
    protected String _jsonpFunctionName;
    @Context
    protected Providers _providers;
    
    public JacksonJsonProvider() {
        this(null, JacksonJsonProvider.BASIC_ANNOTATIONS);
    }
    
    public JacksonJsonProvider(final Annotations... annotationsToUse) {
        this(null, annotationsToUse);
    }
    
    public JacksonJsonProvider(final ObjectMapper mapper) {
        this(mapper, JacksonJsonProvider.BASIC_ANNOTATIONS);
    }
    
    public JacksonJsonProvider(final ObjectMapper mapper, final Annotations[] annotationsToUse) {
        super(new JsonMapperConfigurator(mapper, annotationsToUse));
    }
    
    public Version version() {
        return PackageVersion.VERSION;
    }
    
    public void setJSONPFunctionName(final String fname) {
        this._jsonpFunctionName = fname;
    }
    
    @Override
    protected boolean hasMatchingMediaType(final MediaType mediaType) {
        if (mediaType != null) {
            final String subtype = mediaType.getSubtype();
            return "json".equalsIgnoreCase(subtype) || subtype.endsWith("+json") || "javascript".equals(subtype) || "x-javascript".equals(subtype) || "x-json".equals(subtype);
        }
        return true;
    }
    
    @Override
    protected ObjectMapper _locateMapperViaProvider(final Class<?> type, final MediaType mediaType) {
        if (this._providers != null) {
            ContextResolver<ObjectMapper> resolver = (ContextResolver<ObjectMapper>)this._providers.getContextResolver((Class)ObjectMapper.class, mediaType);
            if (resolver == null) {
                resolver = (ContextResolver<ObjectMapper>)this._providers.getContextResolver((Class)ObjectMapper.class, (MediaType)null);
            }
            if (resolver != null) {
                return (ObjectMapper)resolver.getContext((Class)type);
            }
        }
        return null;
    }
    
    @Override
    protected JsonEndpointConfig _configForReading(final ObjectReader reader, final Annotation[] annotations) {
        return JsonEndpointConfig.forReading(reader, annotations);
    }
    
    @Override
    protected JsonEndpointConfig _configForWriting(final ObjectWriter writer, final Annotation[] annotations) {
        return JsonEndpointConfig.forWriting(writer, annotations, this._jsonpFunctionName);
    }
    
    @Deprecated
    protected boolean isJsonType(final MediaType mediaType) {
        return this.hasMatchingMediaType(mediaType);
    }
    
    static {
        BASIC_ANNOTATIONS = new Annotations[] { Annotations.JACKSON };
    }
}
