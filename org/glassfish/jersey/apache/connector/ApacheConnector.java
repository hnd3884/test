package org.glassfish.jersey.apache.connector;

import org.apache.http.impl.io.ChunkedOutputStream;
import org.apache.http.io.SessionOutputBuffer;
import org.apache.http.impl.conn.DefaultManagedHttpClientConnection;
import org.apache.http.HttpConnection;
import org.apache.http.conn.ManagedHttpClientConnection;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.conn.routing.HttpRoute;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.http.impl.conn.ManagedHttpClientConnectionFactory;
import java.io.FilterInputStream;
import java.io.BufferedInputStream;
import org.glassfish.jersey.message.internal.ReaderWriter;
import java.util.Iterator;
import org.apache.http.entity.BufferedHttpEntity;
import org.glassfish.jersey.message.internal.OutboundMessageContext;
import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.client.methods.RequestBuilder;
import org.glassfish.jersey.client.RequestEntityProcessing;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import org.glassfish.jersey.client.spi.AsyncConnectorCallback;
import org.apache.http.HttpEntity;
import org.apache.http.Header;
import javax.ws.rs.core.Response;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.AuthCache;
import org.apache.http.client.methods.HttpUriRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.glassfish.jersey.message.internal.Statuses;
import org.glassfish.jersey.message.internal.HeaderUtils;
import org.apache.http.protocol.HttpContext;
import org.apache.http.HttpRequest;
import org.apache.http.auth.AuthScheme;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.client.protocol.HttpClientContext;
import javax.ws.rs.core.MultivaluedMap;
import org.glassfish.jersey.client.ClientResponse;
import org.glassfish.jersey.client.ClientRequest;
import javax.ws.rs.ProcessingException;
import org.apache.http.client.HttpClient;
import org.apache.http.util.TextUtils;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import javax.net.ssl.HostnameVerifier;
import org.apache.http.conn.HttpConnectionFactory;
import org.apache.http.config.Registry;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ssl.SSLContexts;
import javax.net.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import java.net.URI;
import javax.net.ssl.SSLContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.auth.AuthScope;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.glassfish.jersey.client.ClientProperties;
import org.apache.http.HttpHost;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.CredentialsProvider;
import java.util.Map;
import org.glassfish.jersey.internal.util.PropertiesHelper;
import org.apache.http.impl.client.HttpClientBuilder;
import java.util.logging.Level;
import org.apache.http.conn.HttpClientConnectionManager;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.client.Client;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.CookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.VersionInfo;
import java.util.logging.Logger;
import org.glassfish.jersey.client.spi.Connector;

class ApacheConnector implements Connector
{
    private static final Logger LOGGER;
    private static final VersionInfo vi;
    private static final String release;
    private final CloseableHttpClient client;
    private final CookieStore cookieStore;
    private final boolean preemptiveBasicAuth;
    private final RequestConfig requestConfig;
    
    ApacheConnector(final Client client, final Configuration config) {
        final Object connectionManager = config.getProperties().get("jersey.config.apache.client.connectionManager");
        if (connectionManager != null && !(connectionManager instanceof HttpClientConnectionManager)) {
            ApacheConnector.LOGGER.log(Level.WARNING, LocalizationMessages.IGNORING_VALUE_OF_PROPERTY("jersey.config.apache.client.connectionManager", connectionManager.getClass().getName(), HttpClientConnectionManager.class.getName()));
        }
        Object reqConfig = config.getProperties().get("jersey.config.apache.client.requestConfig");
        if (reqConfig != null && !(reqConfig instanceof RequestConfig)) {
            ApacheConnector.LOGGER.log(Level.WARNING, LocalizationMessages.IGNORING_VALUE_OF_PROPERTY("jersey.config.apache.client.requestConfig", reqConfig.getClass().getName(), RequestConfig.class.getName()));
            reqConfig = null;
        }
        final SSLContext sslContext = client.getSslContext();
        final HttpClientBuilder clientBuilder = HttpClientBuilder.create();
        clientBuilder.setConnectionManager(this.getConnectionManager(client, config, sslContext));
        clientBuilder.setConnectionManagerShared((boolean)PropertiesHelper.getValue(config.getProperties(), "jersey.config.apache.client.connectionManagerShared", (Object)false, (Map)null));
        clientBuilder.setSslcontext(sslContext);
        final RequestConfig.Builder requestConfigBuilder = RequestConfig.custom();
        final Object credentialsProvider = config.getProperty("jersey.config.apache.client.credentialsProvider");
        if (credentialsProvider != null && credentialsProvider instanceof CredentialsProvider) {
            clientBuilder.setDefaultCredentialsProvider((CredentialsProvider)credentialsProvider);
        }
        final Object retryHandler = config.getProperties().get("jersey.config.apache.client.retryHandler");
        if (retryHandler != null && retryHandler instanceof HttpRequestRetryHandler) {
            clientBuilder.setRetryHandler((HttpRequestRetryHandler)retryHandler);
        }
        final Object proxyUri = config.getProperty("jersey.config.client.proxy.uri");
        if (proxyUri != null) {
            final URI u = getProxyUri(proxyUri);
            final HttpHost proxy = new HttpHost(u.getHost(), u.getPort(), u.getScheme());
            final String userName = (String)ClientProperties.getValue(config.getProperties(), "jersey.config.client.proxy.username", (Class)String.class);
            if (userName != null) {
                final String password = (String)ClientProperties.getValue(config.getProperties(), "jersey.config.client.proxy.password", (Class)String.class);
                if (password != null) {
                    final CredentialsProvider credsProvider = (CredentialsProvider)new BasicCredentialsProvider();
                    credsProvider.setCredentials(new AuthScope(u.getHost(), u.getPort()), (Credentials)new UsernamePasswordCredentials(userName, password));
                    clientBuilder.setDefaultCredentialsProvider(credsProvider);
                }
            }
            clientBuilder.setProxy(proxy);
        }
        final Boolean preemptiveBasicAuthProperty = config.getProperties().get("jersey.config.apache.client.preemptiveBasicAuthentication");
        this.preemptiveBasicAuth = (preemptiveBasicAuthProperty != null && preemptiveBasicAuthProperty);
        final boolean ignoreCookies = PropertiesHelper.isProperty(config.getProperties(), "jersey.config.apache.client.handleCookies");
        if (reqConfig != null) {
            final RequestConfig.Builder reqConfigBuilder = RequestConfig.copy((RequestConfig)reqConfig);
            if (ignoreCookies) {
                reqConfigBuilder.setCookieSpec("ignoreCookies");
            }
            this.requestConfig = reqConfigBuilder.build();
        }
        else {
            if (ignoreCookies) {
                requestConfigBuilder.setCookieSpec("ignoreCookies");
            }
            this.requestConfig = requestConfigBuilder.build();
        }
        if (this.requestConfig.getCookieSpec() == null || !this.requestConfig.getCookieSpec().equals("ignoreCookies")) {
            clientBuilder.setDefaultCookieStore(this.cookieStore = (CookieStore)new BasicCookieStore());
        }
        else {
            this.cookieStore = null;
        }
        clientBuilder.setDefaultRequestConfig(this.requestConfig);
        this.client = clientBuilder.build();
    }
    
    private HttpClientConnectionManager getConnectionManager(final Client client, final Configuration config, final SSLContext sslContext) {
        final Object cmObject = config.getProperties().get("jersey.config.apache.client.connectionManager");
        if (cmObject != null) {
            if (cmObject instanceof HttpClientConnectionManager) {
                return (HttpClientConnectionManager)cmObject;
            }
            ApacheConnector.LOGGER.log(Level.WARNING, LocalizationMessages.IGNORING_VALUE_OF_PROPERTY("jersey.config.apache.client.connectionManager", cmObject.getClass().getName(), HttpClientConnectionManager.class.getName()));
        }
        return this.createConnectionManager(client, config, sslContext, false);
    }
    
    private HttpClientConnectionManager createConnectionManager(final Client client, final Configuration config, final SSLContext sslContext, final boolean useSystemProperties) {
        final String[] supportedProtocols = (String[])(useSystemProperties ? split(System.getProperty("https.protocols")) : null);
        final String[] supportedCipherSuites = (String[])(useSystemProperties ? split(System.getProperty("https.cipherSuites")) : null);
        final HostnameVerifier hostnameVerifier = client.getHostnameVerifier();
        LayeredConnectionSocketFactory sslSocketFactory;
        if (sslContext != null) {
            sslSocketFactory = (LayeredConnectionSocketFactory)new SSLConnectionSocketFactory(sslContext, supportedProtocols, supportedCipherSuites, hostnameVerifier);
        }
        else if (useSystemProperties) {
            sslSocketFactory = (LayeredConnectionSocketFactory)new SSLConnectionSocketFactory((SSLSocketFactory)SSLSocketFactory.getDefault(), supportedProtocols, supportedCipherSuites, hostnameVerifier);
        }
        else {
            sslSocketFactory = (LayeredConnectionSocketFactory)new SSLConnectionSocketFactory(SSLContexts.createDefault(), hostnameVerifier);
        }
        final Registry<ConnectionSocketFactory> registry = (Registry<ConnectionSocketFactory>)RegistryBuilder.create().register("http", (Object)PlainConnectionSocketFactory.getSocketFactory()).register("https", (Object)sslSocketFactory).build();
        final Integer chunkSize = (Integer)ClientProperties.getValue(config.getProperties(), "jersey.config.client.chunkedEncodingSize", (Object)4096, (Class)Integer.class);
        final PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager((Registry)registry, (HttpConnectionFactory)new ConnectionFactory((int)chunkSize));
        if (useSystemProperties) {
            String s = System.getProperty("http.keepAlive", "true");
            if ("true".equalsIgnoreCase(s)) {
                s = System.getProperty("http.maxConnections", "5");
                final int max = Integer.parseInt(s);
                connectionManager.setDefaultMaxPerRoute(max);
                connectionManager.setMaxTotal(2 * max);
            }
        }
        return (HttpClientConnectionManager)connectionManager;
    }
    
    private static String[] split(final String s) {
        if (TextUtils.isBlank((CharSequence)s)) {
            return null;
        }
        return s.split(" *, *");
    }
    
    public HttpClient getHttpClient() {
        return (HttpClient)this.client;
    }
    
    public CookieStore getCookieStore() {
        return this.cookieStore;
    }
    
    private static URI getProxyUri(final Object proxy) {
        if (proxy instanceof URI) {
            return (URI)proxy;
        }
        if (proxy instanceof String) {
            return URI.create((String)proxy);
        }
        throw new ProcessingException(LocalizationMessages.WRONG_PROXY_URI_TYPE("jersey.config.client.proxy.uri"));
    }
    
    public ClientResponse apply(final ClientRequest clientRequest) throws ProcessingException {
        final HttpUriRequest request = this.getUriHttpRequest(clientRequest);
        final Map<String, String> clientHeadersSnapshot = writeOutBoundHeaders((MultivaluedMap<String, Object>)clientRequest.getHeaders(), request);
        try {
            final HttpClientContext context = HttpClientContext.create();
            if (this.preemptiveBasicAuth) {
                final AuthCache authCache = (AuthCache)new BasicAuthCache();
                final BasicScheme basicScheme = new BasicScheme();
                authCache.put(this.getHost(request), (AuthScheme)basicScheme);
                context.setAuthCache(authCache);
            }
            final CredentialsProvider credentialsProvider = (CredentialsProvider)clientRequest.resolveProperty("jersey.config.apache.client.credentialsProvider", (Class)CredentialsProvider.class);
            if (credentialsProvider != null) {
                context.setCredentialsProvider(credentialsProvider);
            }
            final CloseableHttpResponse response = this.client.execute(this.getHost(request), (HttpRequest)request, (HttpContext)context);
            HeaderUtils.checkHeaderChanges((Map)clientHeadersSnapshot, clientRequest.getHeaders(), this.getClass().getName());
            final Response.StatusType status = (response.getStatusLine().getReasonPhrase() == null) ? Statuses.from(response.getStatusLine().getStatusCode()) : Statuses.from(response.getStatusLine().getStatusCode(), response.getStatusLine().getReasonPhrase());
            final ClientResponse responseContext = new ClientResponse(status, clientRequest);
            final List<URI> redirectLocations = context.getRedirectLocations();
            if (redirectLocations != null && !redirectLocations.isEmpty()) {
                responseContext.setResolvedRequestUri((URI)redirectLocations.get(redirectLocations.size() - 1));
            }
            final Header[] respHeaders = response.getAllHeaders();
            final MultivaluedMap<String, String> headers = (MultivaluedMap<String, String>)responseContext.getHeaders();
            for (final Header header : respHeaders) {
                final String headerName = header.getName();
                List<String> list = (List<String>)headers.get((Object)headerName);
                if (list == null) {
                    list = new ArrayList<String>();
                }
                list.add(header.getValue());
                headers.put((Object)headerName, (Object)list);
            }
            final HttpEntity entity = response.getEntity();
            if (entity != null) {
                if (headers.get((Object)"Content-Length") == null) {
                    headers.add((Object)"Content-Length", (Object)String.valueOf(entity.getContentLength()));
                }
                final Header contentEncoding = entity.getContentEncoding();
                if (headers.get((Object)"Content-Encoding") == null && contentEncoding != null) {
                    headers.add((Object)"Content-Encoding", (Object)contentEncoding.getValue());
                }
            }
            try {
                responseContext.setEntityStream((InputStream)new HttpClientResponseInputStream(getInputStream(response)));
            }
            catch (final IOException e) {
                ApacheConnector.LOGGER.log(Level.SEVERE, null, e);
            }
            return responseContext;
        }
        catch (final Exception e2) {
            throw new ProcessingException((Throwable)e2);
        }
    }
    
    public Future<?> apply(final ClientRequest request, final AsyncConnectorCallback callback) {
        try {
            final ClientResponse response = this.apply(request);
            callback.response(response);
            return CompletableFuture.completedFuture(response);
        }
        catch (final Throwable t) {
            callback.failure(t);
            final CompletableFuture<Object> future = new CompletableFuture<Object>();
            future.completeExceptionally(t);
            return future;
        }
    }
    
    public String getName() {
        return "Apache HttpClient " + ApacheConnector.release;
    }
    
    public void close() {
        try {
            this.client.close();
        }
        catch (final IOException e) {
            throw new ProcessingException(LocalizationMessages.FAILED_TO_STOP_CLIENT(), (Throwable)e);
        }
    }
    
    private HttpHost getHost(final HttpUriRequest request) {
        return new HttpHost(request.getURI().getHost(), request.getURI().getPort(), request.getURI().getScheme());
    }
    
    private HttpUriRequest getUriHttpRequest(final ClientRequest clientRequest) {
        final RequestConfig.Builder requestConfigBuilder = RequestConfig.copy(this.requestConfig);
        final int connectTimeout = (int)clientRequest.resolveProperty("jersey.config.client.connectTimeout", (Object)(-1));
        final int socketTimeout = (int)clientRequest.resolveProperty("jersey.config.client.readTimeout", (Object)(-1));
        if (connectTimeout >= 0) {
            requestConfigBuilder.setConnectTimeout(connectTimeout);
        }
        if (socketTimeout >= 0) {
            requestConfigBuilder.setSocketTimeout(socketTimeout);
        }
        final Boolean redirectsEnabled = (Boolean)clientRequest.resolveProperty("jersey.config.client.followRedirects", (Object)this.requestConfig.isRedirectsEnabled());
        requestConfigBuilder.setRedirectsEnabled((boolean)redirectsEnabled);
        final Boolean bufferingEnabled = clientRequest.resolveProperty("jersey.config.client.request.entity.processing", (Class)RequestEntityProcessing.class) == RequestEntityProcessing.BUFFERED;
        final HttpEntity entity = this.getHttpEntity(clientRequest, bufferingEnabled);
        return RequestBuilder.create(clientRequest.getMethod()).setUri(clientRequest.getUri()).setConfig(requestConfigBuilder.build()).setEntity(entity).build();
    }
    
    private HttpEntity getHttpEntity(final ClientRequest clientRequest, final boolean bufferingEnabled) {
        final Object entity = clientRequest.getEntity();
        if (entity == null) {
            return null;
        }
        final AbstractHttpEntity httpEntity = new AbstractHttpEntity() {
            public boolean isRepeatable() {
                return false;
            }
            
            public long getContentLength() {
                return -1L;
            }
            
            public InputStream getContent() throws IOException, IllegalStateException {
                if (bufferingEnabled) {
                    final ByteArrayOutputStream buffer = new ByteArrayOutputStream(512);
                    this.writeTo(buffer);
                    return new ByteArrayInputStream(buffer.toByteArray());
                }
                return null;
            }
            
            public void writeTo(final OutputStream outputStream) throws IOException {
                clientRequest.setStreamProvider((OutboundMessageContext.StreamProvider)new OutboundMessageContext.StreamProvider() {
                    public OutputStream getOutputStream(final int contentLength) throws IOException {
                        return outputStream;
                    }
                });
                clientRequest.writeEntity();
            }
            
            public boolean isStreaming() {
                return false;
            }
        };
        if (bufferingEnabled) {
            try {
                return (HttpEntity)new BufferedHttpEntity((HttpEntity)httpEntity);
            }
            catch (final IOException e) {
                throw new ProcessingException(LocalizationMessages.ERROR_BUFFERING_ENTITY(), (Throwable)e);
            }
        }
        return (HttpEntity)httpEntity;
    }
    
    private static Map<String, String> writeOutBoundHeaders(final MultivaluedMap<String, Object> headers, final HttpUriRequest request) {
        final Map<String, String> stringHeaders = HeaderUtils.asStringHeadersSingleValue((MultivaluedMap)headers);
        for (final Map.Entry<String, String> e : stringHeaders.entrySet()) {
            request.addHeader((String)e.getKey(), (String)e.getValue());
        }
        return stringHeaders;
    }
    
    private static InputStream getInputStream(final CloseableHttpResponse response) throws IOException {
        InputStream inputStream;
        if (response.getEntity() == null) {
            inputStream = new ByteArrayInputStream(new byte[0]);
        }
        else {
            final InputStream i = response.getEntity().getContent();
            if (i.markSupported()) {
                inputStream = i;
            }
            else {
                inputStream = new BufferedInputStream(i, ReaderWriter.BUFFER_SIZE);
            }
        }
        return new FilterInputStream(inputStream) {
            @Override
            public void close() throws IOException {
                response.close();
                super.close();
            }
        };
    }
    
    static {
        LOGGER = Logger.getLogger(ApacheConnector.class.getName());
        vi = VersionInfo.loadVersionInfo("org.apache.http.client", HttpClientBuilder.class.getClassLoader());
        release = ((ApacheConnector.vi != null) ? ApacheConnector.vi.getRelease() : "UNAVAILABLE");
    }
    
    private static final class HttpClientResponseInputStream extends FilterInputStream
    {
        HttpClientResponseInputStream(final InputStream inputStream) throws IOException {
            super(inputStream);
        }
        
        @Override
        public void close() throws IOException {
            super.close();
        }
    }
    
    private static class ConnectionFactory extends ManagedHttpClientConnectionFactory
    {
        private static final AtomicLong COUNTER;
        private final int chunkSize;
        
        private ConnectionFactory(final int chunkSize) {
            this.chunkSize = chunkSize;
        }
        
        public ManagedHttpClientConnection create(final HttpRoute route, final ConnectionConfig config) {
            final String id = "http-outgoing-" + Long.toString(ConnectionFactory.COUNTER.getAndIncrement());
            return (ManagedHttpClientConnection)new HttpClientConnection(id, config.getBufferSize(), this.chunkSize);
        }
        
        static {
            COUNTER = new AtomicLong();
        }
    }
    
    private static class HttpClientConnection extends DefaultManagedHttpClientConnection
    {
        private final int chunkSize;
        
        private HttpClientConnection(final String id, final int buffersize, final int chunkSize) {
            super(id, buffersize);
            this.chunkSize = chunkSize;
        }
        
        protected OutputStream createOutputStream(final long len, final SessionOutputBuffer outbuffer) {
            if (len == -2L) {
                return (OutputStream)new ChunkedOutputStream(this.chunkSize, outbuffer);
            }
            return super.createOutputStream(len, outbuffer);
        }
    }
}
