package org.glassfish.jersey.client.internal;

import java.util.HashSet;
import java.io.OutputStream;
import java.security.PrivilegedActionException;
import java.lang.reflect.Field;
import java.net.ProtocolException;
import java.security.PrivilegedExceptionAction;
import java.util.Iterator;
import java.util.List;
import java.net.URI;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.Map;
import java.net.URISyntaxException;
import org.glassfish.jersey.message.internal.Statuses;
import javax.ws.rs.core.MultivaluedMap;
import java.util.logging.Level;
import org.glassfish.jersey.client.RequestEntityProcessing;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import org.glassfish.jersey.client.JerseyClient;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import org.glassfish.jersey.client.spi.AsyncConnectorCallback;
import javax.ws.rs.ProcessingException;
import org.glassfish.jersey.client.ClientResponse;
import org.glassfish.jersey.client.ClientRequest;
import java.io.ByteArrayInputStream;
import javax.ws.rs.core.Response;
import java.io.IOException;
import org.glassfish.jersey.internal.util.collection.UnsafeValue;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.security.PrivilegedAction;
import java.security.AccessController;
import org.glassfish.jersey.internal.util.PropertiesHelper;
import org.glassfish.jersey.internal.util.collection.Values;
import org.glassfish.jersey.internal.util.collection.Value;
import javax.ws.rs.client.Client;
import javax.net.ssl.SSLSocketFactory;
import org.glassfish.jersey.internal.util.collection.LazyValue;
import org.glassfish.jersey.client.HttpUrlConnectorProvider;
import java.util.Set;
import java.util.logging.Logger;
import org.glassfish.jersey.client.spi.Connector;

public class HttpUrlConnector implements Connector
{
    private static final Logger LOGGER;
    private static final String ALLOW_RESTRICTED_HEADERS_SYSTEM_PROPERTY = "sun.net.http.allowRestrictedHeaders";
    private static final String[] restrictedHeaders;
    private static final Set<String> restrictedHeaderSet;
    private final HttpUrlConnectorProvider.ConnectionFactory connectionFactory;
    private final int chunkSize;
    private final boolean fixLengthStreaming;
    private final boolean setMethodWorkaround;
    private final boolean isRestrictedHeaderPropertySet;
    private final LazyValue<SSLSocketFactory> sslSocketFactory;
    
    public HttpUrlConnector(final Client client, final HttpUrlConnectorProvider.ConnectionFactory connectionFactory, final int chunkSize, final boolean fixLengthStreaming, final boolean setMethodWorkaround) {
        this.sslSocketFactory = (LazyValue<SSLSocketFactory>)Values.lazy((Value)new Value<SSLSocketFactory>() {
            public SSLSocketFactory get() {
                return client.getSslContext().getSocketFactory();
            }
        });
        this.connectionFactory = connectionFactory;
        this.chunkSize = chunkSize;
        this.fixLengthStreaming = fixLengthStreaming;
        this.setMethodWorkaround = setMethodWorkaround;
        this.isRestrictedHeaderPropertySet = Boolean.valueOf(AccessController.doPrivileged((PrivilegedAction<String>)PropertiesHelper.getSystemProperty("sun.net.http.allowRestrictedHeaders", "false")));
        HttpUrlConnector.LOGGER.config(this.isRestrictedHeaderPropertySet ? LocalizationMessages.RESTRICTED_HEADER_PROPERTY_SETTING_TRUE("sun.net.http.allowRestrictedHeaders") : LocalizationMessages.RESTRICTED_HEADER_PROPERTY_SETTING_FALSE("sun.net.http.allowRestrictedHeaders"));
    }
    
    private static InputStream getInputStream(final HttpURLConnection uc) throws IOException {
        return new InputStream() {
            private final UnsafeValue<InputStream, IOException> in = Values.lazy((UnsafeValue)new UnsafeValue<InputStream, IOException>() {
                public InputStream get() throws IOException {
                    if (uc.getResponseCode() < Response.Status.BAD_REQUEST.getStatusCode()) {
                        return uc.getInputStream();
                    }
                    final InputStream ein = uc.getErrorStream();
                    return (ein != null) ? ein : new ByteArrayInputStream(new byte[0]);
                }
            });
            private volatile boolean closed = false;
            
            private void throwIOExceptionIfClosed() throws IOException {
                if (this.closed) {
                    throw new IOException("Stream closed");
                }
            }
            
            @Override
            public int read() throws IOException {
                final int result = ((InputStream)this.in.get()).read();
                this.throwIOExceptionIfClosed();
                return result;
            }
            
            @Override
            public int read(final byte[] b) throws IOException {
                final int result = ((InputStream)this.in.get()).read(b);
                this.throwIOExceptionIfClosed();
                return result;
            }
            
            @Override
            public int read(final byte[] b, final int off, final int len) throws IOException {
                final int result = ((InputStream)this.in.get()).read(b, off, len);
                this.throwIOExceptionIfClosed();
                return result;
            }
            
            @Override
            public long skip(final long n) throws IOException {
                final long result = ((InputStream)this.in.get()).skip(n);
                this.throwIOExceptionIfClosed();
                return result;
            }
            
            @Override
            public int available() throws IOException {
                final int result = ((InputStream)this.in.get()).available();
                this.throwIOExceptionIfClosed();
                return result;
            }
            
            @Override
            public void close() throws IOException {
                try {
                    ((InputStream)this.in.get()).close();
                }
                finally {
                    this.closed = true;
                }
            }
            
            @Override
            public void mark(final int readLimit) {
                try {
                    ((InputStream)this.in.get()).mark(readLimit);
                }
                catch (final IOException e) {
                    throw new IllegalStateException("Unable to retrieve the underlying input stream.", e);
                }
            }
            
            @Override
            public void reset() throws IOException {
                ((InputStream)this.in.get()).reset();
                this.throwIOExceptionIfClosed();
            }
            
            @Override
            public boolean markSupported() {
                try {
                    return ((InputStream)this.in.get()).markSupported();
                }
                catch (final IOException e) {
                    throw new IllegalStateException("Unable to retrieve the underlying input stream.", e);
                }
            }
        };
    }
    
    @Override
    public ClientResponse apply(final ClientRequest request) {
        try {
            return this._apply(request);
        }
        catch (final IOException ex) {
            throw new ProcessingException((Throwable)ex);
        }
    }
    
    @Override
    public Future<?> apply(final ClientRequest request, final AsyncConnectorCallback callback) {
        try {
            callback.response(this._apply(request));
        }
        catch (final IOException ex) {
            callback.failure((Throwable)new ProcessingException((Throwable)ex));
        }
        catch (final Throwable t) {
            callback.failure(t);
        }
        return CompletableFuture.completedFuture((Object)null);
    }
    
    @Override
    public void close() {
    }
    
    protected void secureConnection(final JerseyClient client, final HttpURLConnection uc) {
        if (uc instanceof HttpsURLConnection) {
            final HttpsURLConnection suc = (HttpsURLConnection)uc;
            final HostnameVerifier verifier = client.getHostnameVerifier();
            if (verifier != null) {
                suc.setHostnameVerifier(verifier);
            }
            if (HttpsURLConnection.getDefaultSSLSocketFactory() == suc.getSSLSocketFactory()) {
                suc.setSSLSocketFactory((SSLSocketFactory)this.sslSocketFactory.get());
            }
        }
    }
    
    private ClientResponse _apply(final ClientRequest request) throws IOException {
        final HttpURLConnection uc = this.connectionFactory.getConnection(request.getUri().toURL());
        uc.setDoInput(true);
        final String httpMethod = request.getMethod();
        if (request.resolveProperty("jersey.config.client.httpUrlConnection.setMethodWorkaround", this.setMethodWorkaround)) {
            setRequestMethodViaJreBugWorkaround(uc, httpMethod);
        }
        else {
            uc.setRequestMethod(httpMethod);
        }
        uc.setInstanceFollowRedirects(request.resolveProperty("jersey.config.client.followRedirects", true));
        uc.setConnectTimeout(request.resolveProperty("jersey.config.client.connectTimeout", uc.getConnectTimeout()));
        uc.setReadTimeout(request.resolveProperty("jersey.config.client.readTimeout", uc.getReadTimeout()));
        this.secureConnection(request.getClient(), uc);
        final Object entity = request.getEntity();
        if (entity != null) {
            final RequestEntityProcessing entityProcessing = request.resolveProperty("jersey.config.client.request.entity.processing", RequestEntityProcessing.class);
            if (entityProcessing == null || entityProcessing != RequestEntityProcessing.BUFFERED) {
                final long length = request.getLengthLong();
                if (this.fixLengthStreaming && length > 0L) {
                    if ("1.6".equals(Runtime.class.getPackage().getSpecificationVersion())) {
                        uc.setFixedLengthStreamingMode(request.getLength());
                    }
                    else {
                        uc.setFixedLengthStreamingMode(length);
                    }
                }
                else if (entityProcessing == RequestEntityProcessing.CHUNKED) {
                    uc.setChunkedStreamingMode(this.chunkSize);
                }
            }
            uc.setDoOutput(true);
            if ("GET".equalsIgnoreCase(httpMethod)) {
                final Logger logger = Logger.getLogger(HttpUrlConnector.class.getName());
                if (logger.isLoggable(Level.INFO)) {
                    logger.log(Level.INFO, LocalizationMessages.HTTPURLCONNECTION_REPLACES_GET_WITH_ENTITY());
                }
            }
            request.setStreamProvider(contentLength -> {
                this.setOutboundHeaders((MultivaluedMap<String, String>)request.getStringHeaders(), uc);
                return uc.getOutputStream();
            });
            request.writeEntity();
        }
        else {
            this.setOutboundHeaders((MultivaluedMap<String, String>)request.getStringHeaders(), uc);
        }
        final int code = uc.getResponseCode();
        final String reasonPhrase = uc.getResponseMessage();
        final Response.StatusType status = (reasonPhrase == null) ? Statuses.from(code) : Statuses.from(code, reasonPhrase);
        URI resolvedRequestUri;
        try {
            resolvedRequestUri = uc.getURL().toURI();
        }
        catch (final URISyntaxException e) {
            throw new ProcessingException((Throwable)e);
        }
        final ClientResponse responseContext = new ClientResponse(status, request, resolvedRequestUri);
        responseContext.headers((Map)uc.getHeaderFields().entrySet().stream().filter(stringListEntry -> stringListEntry.getKey() != null).collect(Collectors.toMap((Function<? super Object, ?>)Map.Entry::getKey, (Function<? super Object, ?>)Map.Entry::getValue)));
        responseContext.setEntityStream(getInputStream(uc));
        return responseContext;
    }
    
    private void setOutboundHeaders(final MultivaluedMap<String, String> headers, final HttpURLConnection uc) {
        boolean restrictedSent = false;
        for (final Map.Entry<String, List<String>> header : headers.entrySet()) {
            final String headerName = header.getKey();
            final List<String> headerValues = header.getValue();
            String headerValue;
            if (headerValues.size() == 1) {
                headerValue = headerValues.get(0);
                uc.setRequestProperty(headerName, headerValue);
            }
            else {
                final StringBuilder b = new StringBuilder();
                boolean add = false;
                for (final Object value : headerValues) {
                    if (add) {
                        b.append(',');
                    }
                    add = true;
                    b.append(value);
                }
                headerValue = b.toString();
                uc.setRequestProperty(headerName, headerValue);
            }
            if (!this.isRestrictedHeaderPropertySet && !restrictedSent && this.isHeaderRestricted(headerName, headerValue)) {
                restrictedSent = true;
            }
        }
        if (restrictedSent) {
            HttpUrlConnector.LOGGER.warning(LocalizationMessages.RESTRICTED_HEADER_POSSIBLY_IGNORED("sun.net.http.allowRestrictedHeaders"));
        }
    }
    
    private boolean isHeaderRestricted(String name, final String value) {
        name = name.toLowerCase();
        return name.startsWith("sec-") || (HttpUrlConnector.restrictedHeaderSet.contains(name) && (!"connection".equalsIgnoreCase(name) || !"close".equalsIgnoreCase(value)));
    }
    
    private static void setRequestMethodViaJreBugWorkaround(final HttpURLConnection httpURLConnection, final String method) {
        try {
            httpURLConnection.setRequestMethod(method);
        }
        catch (final ProtocolException pe) {
            try {
                AccessController.doPrivileged((PrivilegedExceptionAction<Object>)new PrivilegedExceptionAction<Object>() {
                    @Override
                    public Object run() throws NoSuchFieldException, IllegalAccessException {
                        try {
                            httpURLConnection.setRequestMethod(method);
                        }
                        catch (final ProtocolException pe) {
                            Class<?> connectionClass = httpURLConnection.getClass();
                            try {
                                final Field delegateField = connectionClass.getDeclaredField("delegate");
                                delegateField.setAccessible(true);
                                final HttpURLConnection delegateConnection = (HttpURLConnection)delegateField.get(httpURLConnection);
                                setRequestMethodViaJreBugWorkaround(delegateConnection, method);
                            }
                            catch (final NoSuchFieldException ex) {}
                            catch (final IllegalArgumentException | IllegalAccessException e) {
                                throw new RuntimeException(e);
                            }
                            try {
                                while (connectionClass != null) {
                                    Field methodField;
                                    try {
                                        methodField = connectionClass.getDeclaredField("method");
                                    }
                                    catch (final NoSuchFieldException e2) {
                                        connectionClass = connectionClass.getSuperclass();
                                        continue;
                                    }
                                    methodField.setAccessible(true);
                                    methodField.set(httpURLConnection, method);
                                    break;
                                }
                            }
                            catch (final Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                        return null;
                    }
                });
            }
            catch (final PrivilegedActionException e) {
                final Throwable cause = e.getCause();
                if (cause instanceof RuntimeException) {
                    throw (RuntimeException)cause;
                }
                throw new RuntimeException(cause);
            }
        }
    }
    
    @Override
    public String getName() {
        return "HttpUrlConnection " + AccessController.doPrivileged((PrivilegedAction<String>)PropertiesHelper.getSystemProperty("java.version"));
    }
    
    static {
        LOGGER = Logger.getLogger(HttpUrlConnector.class.getName());
        restrictedHeaders = new String[] { "Access-Control-Request-Headers", "Access-Control-Request-Method", "Connection", "Content-Length", "Content-Transfer-Encoding", "Host", "Keep-Alive", "Origin", "Trailer", "Transfer-Encoding", "Upgrade", "Via" };
        restrictedHeaderSet = new HashSet<String>(HttpUrlConnector.restrictedHeaders.length);
        for (final String headerName : HttpUrlConnector.restrictedHeaders) {
            HttpUrlConnector.restrictedHeaderSet.add(headerName.toLowerCase());
        }
    }
}
