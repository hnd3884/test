package org.glassfish.jersey.client;

import javax.ws.rs.client.Client;
import org.glassfish.jersey.internal.inject.InjectionManager;
import java.util.List;
import java.io.OutputStream;
import org.glassfish.jersey.internal.util.ExceptionUtils;
import java.util.logging.Level;
import java.io.IOException;
import javax.ws.rs.core.GenericType;
import org.glassfish.jersey.internal.guava.Preconditions;
import org.glassfish.jersey.client.internal.LocalizationMessages;
import javax.ws.rs.core.Variant;
import javax.ws.rs.core.CacheControl;
import java.util.Locale;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Cookie;
import java.util.Map;
import javax.ws.rs.core.Configuration;
import java.util.Collection;
import org.glassfish.jersey.internal.util.PropertiesHelper;
import org.glassfish.jersey.internal.MapPropertiesDelegate;
import java.util.logging.Logger;
import javax.ws.rs.ext.ReaderInterceptor;
import javax.ws.rs.ext.WriterInterceptor;
import org.glassfish.jersey.message.MessageBodyWorkers;
import javax.ws.rs.core.Response;
import java.net.URI;
import org.glassfish.jersey.internal.PropertiesDelegate;
import org.glassfish.jersey.internal.inject.InjectionManagerSupplier;
import javax.ws.rs.client.ClientRequestContext;
import org.glassfish.jersey.message.internal.OutboundMessageContext;

public class ClientRequest extends OutboundMessageContext implements ClientRequestContext, InjectionManagerSupplier
{
    private final ClientConfig clientConfig;
    private final PropertiesDelegate propertiesDelegate;
    private URI requestUri;
    private String httpMethod;
    private Response abortResponse;
    private MessageBodyWorkers workers;
    private boolean asynchronous;
    private boolean entityWritten;
    private Iterable<WriterInterceptor> writerInterceptors;
    private Iterable<ReaderInterceptor> readerInterceptors;
    private boolean ignoreUserAgent;
    private static final Logger LOGGER;
    
    protected ClientRequest(final URI requestUri, final ClientConfig clientConfig, final PropertiesDelegate propertiesDelegate) {
        clientConfig.checkClient();
        this.requestUri = requestUri;
        this.clientConfig = clientConfig;
        this.propertiesDelegate = propertiesDelegate;
    }
    
    public ClientRequest(final ClientRequest original) {
        super((OutboundMessageContext)original);
        this.requestUri = original.requestUri;
        this.httpMethod = original.httpMethod;
        this.workers = original.workers;
        this.clientConfig = original.clientConfig.snapshot();
        this.asynchronous = original.isAsynchronous();
        this.readerInterceptors = original.readerInterceptors;
        this.writerInterceptors = original.writerInterceptors;
        this.propertiesDelegate = (PropertiesDelegate)new MapPropertiesDelegate(original.propertiesDelegate);
        this.ignoreUserAgent = original.ignoreUserAgent;
    }
    
    public <T> T resolveProperty(final String name, final Class<T> type) {
        return this.resolveProperty(name, null, type);
    }
    
    public <T> T resolveProperty(final String name, final T defaultValue) {
        return this.resolveProperty(name, defaultValue, defaultValue.getClass());
    }
    
    private <T> T resolveProperty(final String name, Object defaultValue, final Class<T> type) {
        Object result = this.clientConfig.getProperty(name);
        if (result != null) {
            defaultValue = result;
        }
        result = this.propertiesDelegate.getProperty(name);
        if (result == null) {
            result = defaultValue;
        }
        return (T)((result == null) ? null : PropertiesHelper.convertValue(result, (Class)type));
    }
    
    public Object getProperty(final String name) {
        return this.propertiesDelegate.getProperty(name);
    }
    
    public Collection<String> getPropertyNames() {
        return this.propertiesDelegate.getPropertyNames();
    }
    
    public void setProperty(final String name, final Object object) {
        this.propertiesDelegate.setProperty(name, object);
    }
    
    public void removeProperty(final String name) {
        this.propertiesDelegate.removeProperty(name);
    }
    
    PropertiesDelegate getPropertiesDelegate() {
        return this.propertiesDelegate;
    }
    
    ClientRuntime getClientRuntime() {
        return this.clientConfig.getRuntime();
    }
    
    public URI getUri() {
        return this.requestUri;
    }
    
    public void setUri(final URI uri) {
        this.requestUri = uri;
    }
    
    public String getMethod() {
        return this.httpMethod;
    }
    
    public void setMethod(final String method) {
        this.httpMethod = method;
    }
    
    public JerseyClient getClient() {
        return this.clientConfig.getClient();
    }
    
    public void abortWith(final Response response) {
        this.abortResponse = response;
    }
    
    public Response getAbortResponse() {
        return this.abortResponse;
    }
    
    public Configuration getConfiguration() {
        return (Configuration)this.clientConfig.getRuntime().getConfig();
    }
    
    ClientConfig getClientConfig() {
        return this.clientConfig;
    }
    
    public Map<String, Cookie> getCookies() {
        return super.getRequestCookies();
    }
    
    public MessageBodyWorkers getWorkers() {
        return this.workers;
    }
    
    public void setWorkers(final MessageBodyWorkers workers) {
        this.workers = workers;
    }
    
    public void accept(final MediaType... types) {
        this.getHeaders().addAll((Object)"Accept", (Object[])types);
    }
    
    public void accept(final String... types) {
        this.getHeaders().addAll((Object)"Accept", (Object[])types);
    }
    
    public void acceptLanguage(final Locale... locales) {
        this.getHeaders().addAll((Object)"Accept-Language", (Object[])locales);
    }
    
    public void acceptLanguage(final String... locales) {
        this.getHeaders().addAll((Object)"Accept-Language", (Object[])locales);
    }
    
    public void cookie(final Cookie cookie) {
        this.getHeaders().add((Object)"Cookie", (Object)cookie);
    }
    
    public void cacheControl(final CacheControl cacheControl) {
        this.getHeaders().add((Object)"Cache-Control", (Object)cacheControl);
    }
    
    public void encoding(final String encoding) {
        if (encoding == null) {
            this.getHeaders().remove((Object)"Content-Encoding");
        }
        else {
            this.getHeaders().putSingle((Object)"Content-Encoding", (Object)encoding);
        }
    }
    
    public void language(final String language) {
        if (language == null) {
            this.getHeaders().remove((Object)"Content-Language");
        }
        else {
            this.getHeaders().putSingle((Object)"Content-Language", (Object)language);
        }
    }
    
    public void language(final Locale language) {
        if (language == null) {
            this.getHeaders().remove((Object)"Content-Language");
        }
        else {
            this.getHeaders().putSingle((Object)"Content-Language", (Object)language);
        }
    }
    
    public void type(final MediaType type) {
        this.setMediaType(type);
    }
    
    public void type(final String type) {
        this.type((type == null) ? null : MediaType.valueOf(type));
    }
    
    public void variant(final Variant variant) {
        if (variant == null) {
            this.type((MediaType)null);
            this.language((String)null);
            this.encoding(null);
        }
        else {
            this.type(variant.getMediaType());
            this.language(variant.getLanguage());
            this.encoding(variant.getEncoding());
        }
    }
    
    public boolean isAsynchronous() {
        return this.asynchronous;
    }
    
    void setAsynchronous(final boolean async) {
        this.asynchronous = async;
    }
    
    public void enableBuffering() {
        this.enableBuffering(this.getConfiguration());
    }
    
    public void writeEntity() throws IOException {
        Preconditions.checkState(!this.entityWritten, (Object)LocalizationMessages.REQUEST_ENTITY_ALREADY_WRITTEN());
        this.entityWritten = true;
        this.ensureMediaType();
        final GenericType<?> entityType = (GenericType<?>)new GenericType(this.getEntityType());
        this.doWriteEntity(this.workers, entityType);
    }
    
    void doWriteEntity(final MessageBodyWorkers writeWorkers, final GenericType<?> entityType) throws IOException {
        OutputStream entityStream = null;
        boolean connectionFailed = false;
        boolean runtimeException = false;
        try {
            entityStream = writeWorkers.writeTo(this.getEntity(), entityType.getRawType(), entityType.getType(), this.getEntityAnnotations(), this.getMediaType(), this.getHeaders(), this.getPropertiesDelegate(), this.getEntityStream(), (Iterable)this.writerInterceptors);
            this.setEntityStream(entityStream);
        }
        catch (final IOException e) {
            connectionFailed = true;
            throw e;
        }
        catch (final RuntimeException e2) {
            runtimeException = true;
            throw e2;
        }
        finally {
            if (!connectionFailed) {
                if (entityStream != null) {
                    try {
                        entityStream.close();
                    }
                    catch (final IOException e3) {
                        ExceptionUtils.conditionallyReThrow((Exception)e3, !runtimeException, ClientRequest.LOGGER, LocalizationMessages.ERROR_CLOSING_OUTPUT_STREAM(), Level.FINE);
                    }
                    catch (final RuntimeException e4) {
                        ExceptionUtils.conditionallyReThrow((Exception)e4, !runtimeException, ClientRequest.LOGGER, LocalizationMessages.ERROR_CLOSING_OUTPUT_STREAM(), Level.FINE);
                    }
                }
                try {
                    this.commitStream();
                }
                catch (final IOException e3) {
                    ExceptionUtils.conditionallyReThrow((Exception)e3, !runtimeException, ClientRequest.LOGGER, LocalizationMessages.ERROR_COMMITTING_OUTPUT_STREAM(), Level.FINE);
                }
                catch (final RuntimeException e4) {
                    ExceptionUtils.conditionallyReThrow((Exception)e4, !runtimeException, ClientRequest.LOGGER, LocalizationMessages.ERROR_COMMITTING_OUTPUT_STREAM(), Level.FINE);
                }
            }
        }
    }
    
    private void ensureMediaType() {
        if (this.getMediaType() == null) {
            final GenericType<?> entityType = (GenericType<?>)new GenericType(this.getEntityType());
            final List<MediaType> mediaTypes = this.workers.getMessageBodyWriterMediaTypes(entityType.getRawType(), entityType.getType(), this.getEntityAnnotations());
            this.setMediaType(this.getMediaType(mediaTypes));
        }
    }
    
    private MediaType getMediaType(final List<MediaType> mediaTypes) {
        if (mediaTypes.isEmpty()) {
            return MediaType.APPLICATION_OCTET_STREAM_TYPE;
        }
        MediaType mediaType = mediaTypes.get(0);
        if (mediaType.isWildcardType() || mediaType.isWildcardSubtype()) {
            mediaType = MediaType.APPLICATION_OCTET_STREAM_TYPE;
        }
        return mediaType;
    }
    
    void setWriterInterceptors(final Iterable<WriterInterceptor> writerInterceptors) {
        this.writerInterceptors = writerInterceptors;
    }
    
    public Iterable<WriterInterceptor> getWriterInterceptors() {
        return this.writerInterceptors;
    }
    
    public Iterable<ReaderInterceptor> getReaderInterceptors() {
        return this.readerInterceptors;
    }
    
    void setReaderInterceptors(final Iterable<ReaderInterceptor> readerInterceptors) {
        this.readerInterceptors = readerInterceptors;
    }
    
    public InjectionManager getInjectionManager() {
        return this.getClientRuntime().getInjectionManager();
    }
    
    public boolean ignoreUserAgent() {
        return this.ignoreUserAgent;
    }
    
    public void ignoreUserAgent(final boolean ignore) {
        this.ignoreUserAgent = ignore;
    }
    
    static {
        LOGGER = Logger.getLogger(ClientRequest.class.getName());
    }
}
