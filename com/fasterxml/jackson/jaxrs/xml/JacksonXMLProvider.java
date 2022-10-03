package com.fasterxml.jackson.jaxrs.xml;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.cfg.EndpointConfigBase;
import java.io.IOException;
import java.io.PushbackInputStream;
import com.fasterxml.jackson.core.JsonParser;
import java.io.InputStream;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.core.MediaType;
import com.fasterxml.jackson.databind.ObjectWriter;
import java.lang.annotation.Annotation;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.jaxrs.cfg.MapperConfiguratorBase;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Providers;
import com.fasterxml.jackson.jaxrs.cfg.Annotations;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.ext.Provider;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.jaxrs.base.ProviderBase;

@Provider
@Consumes({ "*/*" })
@Produces({ "*/*" })
public class JacksonXMLProvider extends ProviderBase<JacksonXMLProvider, XmlMapper, XMLEndpointConfig, XMLMapperConfigurator>
{
    public static final Annotations[] BASIC_ANNOTATIONS;
    @Context
    protected Providers _providers;
    
    public JacksonXMLProvider() {
        this(null, JacksonXMLProvider.BASIC_ANNOTATIONS);
    }
    
    public JacksonXMLProvider(final Annotations... annotationsToUse) {
        this(null, annotationsToUse);
    }
    
    public JacksonXMLProvider(final XmlMapper mapper) {
        this(mapper, JacksonXMLProvider.BASIC_ANNOTATIONS);
    }
    
    public JacksonXMLProvider(final XmlMapper mapper, final Annotations[] annotationsToUse) {
        super((MapperConfiguratorBase)new XMLMapperConfigurator(mapper, annotationsToUse));
    }
    
    public Version version() {
        return PackageVersion.VERSION;
    }
    
    protected XMLEndpointConfig _configForReading(final ObjectReader reader, final Annotation[] annotations) {
        return XMLEndpointConfig.forReading(reader, annotations);
    }
    
    protected XMLEndpointConfig _configForWriting(final ObjectWriter writer, final Annotation[] annotations) {
        return XMLEndpointConfig.forWriting(writer, annotations);
    }
    
    protected boolean hasMatchingMediaType(final MediaType mediaType) {
        if (mediaType != null) {
            final String subtype = mediaType.getSubtype();
            return "xml".equalsIgnoreCase(subtype) || subtype.endsWith("+xml");
        }
        return true;
    }
    
    public XmlMapper _locateMapperViaProvider(final Class<?> type, final MediaType mediaType) {
        XmlMapper m = ((XMLMapperConfigurator)this._mapperConfig).getConfiguredMapper();
        if (m == null) {
            if (this._providers != null) {
                ContextResolver<XmlMapper> resolver = (ContextResolver<XmlMapper>)this._providers.getContextResolver((Class)XmlMapper.class, mediaType);
                if (resolver == null) {
                    resolver = (ContextResolver<XmlMapper>)this._providers.getContextResolver((Class)XmlMapper.class, (MediaType)null);
                }
                if (resolver != null) {
                    m = (XmlMapper)resolver.getContext((Class)type);
                }
            }
            if (m == null) {
                m = ((XMLMapperConfigurator)this._mapperConfig).getDefaultMapper();
            }
        }
        return m;
    }
    
    protected JsonParser _createParser(final ObjectReader reader, final InputStream rawStream) throws IOException {
        final PushbackInputStream wrappedStream = new PushbackInputStream(rawStream);
        final int firstByte = wrappedStream.read();
        if (firstByte == -1) {
            return null;
        }
        wrappedStream.unread(firstByte);
        return reader.getFactory().createParser((InputStream)wrappedStream);
    }
    
    static {
        BASIC_ANNOTATIONS = new Annotations[] { Annotations.JACKSON };
    }
}
