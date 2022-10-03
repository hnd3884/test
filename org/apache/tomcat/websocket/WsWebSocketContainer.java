package org.apache.tomcat.websocket;

import javax.websocket.CloseReason;
import javax.net.ssl.SSLParameters;
import java.io.InputStream;
import javax.net.ssl.TrustManager;
import java.security.SecureRandom;
import javax.net.ssl.KeyManager;
import javax.net.ssl.TrustManagerFactory;
import org.apache.tomcat.util.security.KeyStoreUtil;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.io.File;
import javax.net.ssl.SSLContext;
import java.util.Locale;
import org.apache.tomcat.util.collections.CaseInsensitiveKeyMap;
import org.apache.tomcat.util.buf.StringUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import java.util.Collection;
import javax.websocket.HandshakeResponse;
import javax.net.ssl.SSLEngine;
import java.util.concurrent.Future;
import java.util.Iterator;
import java.net.SocketAddress;
import javax.websocket.EndpointConfig;
import java.util.Collections;
import java.net.URISyntaxException;
import java.util.concurrent.TimeoutException;
import java.io.EOFException;
import javax.net.ssl.SSLException;
import java.util.concurrent.ExecutionException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import javax.websocket.Extension;
import java.nio.ByteBuffer;
import java.io.IOException;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.ArrayList;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import javax.websocket.Endpoint;
import java.util.List;
import java.util.Arrays;
import javax.websocket.ClientEndpoint;
import javax.websocket.DeploymentException;
import javax.websocket.ClientEndpointConfig;
import java.util.HashSet;
import javax.websocket.Session;
import java.net.URI;
import org.apache.tomcat.InstanceManagerBindings;
import java.util.concurrent.ConcurrentHashMap;
import java.util.HashMap;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.InstanceManager;
import java.util.Set;
import java.util.Map;
import org.apache.juli.logging.Log;
import java.nio.channels.AsynchronousChannelGroup;
import java.util.Random;
import org.apache.tomcat.util.res.StringManager;
import javax.websocket.WebSocketContainer;

public class WsWebSocketContainer implements WebSocketContainer, BackgroundProcess
{
    private static final StringManager sm;
    private static final Random RANDOM;
    private static final byte[] CRLF;
    private static final byte[] GET_BYTES;
    private static final byte[] ROOT_URI_BYTES;
    private static final byte[] HTTP_VERSION_BYTES;
    private volatile AsynchronousChannelGroup asynchronousChannelGroup;
    private final Object asynchronousChannelGroupLock;
    private final Log log;
    private final Map<Object, Set<WsSession>> endpointSessionMap;
    private final Map<WsSession, WsSession> sessions;
    private final Object endPointSessionMapLock;
    private long defaultAsyncTimeout;
    private int maxBinaryMessageBufferSize;
    private int maxTextMessageBufferSize;
    private volatile long defaultMaxSessionIdleTimeout;
    private int backgroundProcessCount;
    private int processPeriod;
    private InstanceManager instanceManager;
    
    public WsWebSocketContainer() {
        this.asynchronousChannelGroup = null;
        this.asynchronousChannelGroupLock = new Object();
        this.log = LogFactory.getLog((Class)WsWebSocketContainer.class);
        this.endpointSessionMap = new HashMap<Object, Set<WsSession>>();
        this.sessions = new ConcurrentHashMap<WsSession, WsSession>();
        this.endPointSessionMapLock = new Object();
        this.defaultAsyncTimeout = -1L;
        this.maxBinaryMessageBufferSize = Constants.DEFAULT_BUFFER_SIZE;
        this.maxTextMessageBufferSize = Constants.DEFAULT_BUFFER_SIZE;
        this.defaultMaxSessionIdleTimeout = 0L;
        this.backgroundProcessCount = 0;
        this.processPeriod = Constants.DEFAULT_PROCESS_PERIOD;
    }
    
    protected InstanceManager getInstanceManager(final ClassLoader classLoader) {
        if (this.instanceManager != null) {
            return this.instanceManager;
        }
        return InstanceManagerBindings.get(classLoader);
    }
    
    protected void setInstanceManager(final InstanceManager instanceManager) {
        this.instanceManager = instanceManager;
    }
    
    public Session connectToServer(final Object pojo, final URI path) throws DeploymentException {
        final ClientEndpointConfig config = this.createClientEndpointConfig(pojo.getClass());
        final ClientEndpointHolder holder = new PojoHolder(pojo, config);
        return this.connectToServerRecursive(holder, config, path, new HashSet<URI>());
    }
    
    public Session connectToServer(final Class<?> annotatedEndpointClass, final URI path) throws DeploymentException {
        final ClientEndpointConfig config = this.createClientEndpointConfig(annotatedEndpointClass);
        final ClientEndpointHolder holder = new PojoClassHolder(annotatedEndpointClass, config);
        return this.connectToServerRecursive(holder, config, path, new HashSet<URI>());
    }
    
    private ClientEndpointConfig createClientEndpointConfig(final Class<?> annotatedEndpointClass) throws DeploymentException {
        final ClientEndpoint annotation = annotatedEndpointClass.getAnnotation(ClientEndpoint.class);
        if (annotation == null) {
            throw new DeploymentException(WsWebSocketContainer.sm.getString("wsWebSocketContainer.missingAnnotation", new Object[] { annotatedEndpointClass.getName() }));
        }
        final Class<? extends ClientEndpointConfig.Configurator> configuratorClazz = annotation.configurator();
        ClientEndpointConfig.Configurator configurator = null;
        if (!ClientEndpointConfig.Configurator.class.equals(configuratorClazz)) {
            try {
                configurator = (ClientEndpointConfig.Configurator)configuratorClazz.getConstructor((Class<?>[])new Class[0]).newInstance(new Object[0]);
            }
            catch (final ReflectiveOperationException e) {
                throw new DeploymentException(WsWebSocketContainer.sm.getString("wsWebSocketContainer.defaultConfiguratorFail"), (Throwable)e);
            }
        }
        final ClientEndpointConfig.Builder builder = ClientEndpointConfig.Builder.create();
        if (configurator != null) {
            builder.configurator(configurator);
        }
        final ClientEndpointConfig config = builder.decoders((List)Arrays.asList((Class[])annotation.decoders())).encoders((List)Arrays.asList((Class[])annotation.encoders())).preferredSubprotocols((List)Arrays.asList(annotation.subprotocols())).build();
        return config;
    }
    
    public Session connectToServer(final Class<? extends Endpoint> clazz, final ClientEndpointConfig clientEndpointConfiguration, final URI path) throws DeploymentException {
        final ClientEndpointHolder holder = new EndpointClassHolder(clazz);
        return this.connectToServerRecursive(holder, clientEndpointConfiguration, path, new HashSet<URI>());
    }
    
    public Session connectToServer(final Endpoint endpoint, final ClientEndpointConfig clientEndpointConfiguration, final URI path) throws DeploymentException {
        final ClientEndpointHolder holder = new EndpointHolder(endpoint);
        return this.connectToServerRecursive(holder, clientEndpointConfiguration, path, new HashSet<URI>());
    }
    
    private Session connectToServerRecursive(final ClientEndpointHolder clientEndpointHolder, final ClientEndpointConfig clientEndpointConfiguration, final URI path, final Set<URI> redirectSet) throws DeploymentException {
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)WsWebSocketContainer.sm.getString("wsWebSocketContainer.connect.entry", new Object[] { clientEndpointHolder.getClassName(), path }));
        }
        boolean secure = false;
        ByteBuffer proxyConnect = null;
        final String scheme = path.getScheme();
        URI proxyPath;
        if ("ws".equalsIgnoreCase(scheme)) {
            proxyPath = URI.create("http" + path.toString().substring(2));
        }
        else {
            if (!"wss".equalsIgnoreCase(scheme)) {
                throw new DeploymentException(WsWebSocketContainer.sm.getString("wsWebSocketContainer.pathWrongScheme", new Object[] { scheme }));
            }
            proxyPath = URI.create("https" + path.toString().substring(3));
            secure = true;
        }
        final String host = path.getHost();
        if (host == null) {
            throw new DeploymentException(WsWebSocketContainer.sm.getString("wsWebSocketContainer.pathNoHost"));
        }
        int port = path.getPort();
        SocketAddress sa = null;
        final List<Proxy> proxies = ProxySelector.getDefault().select(proxyPath);
        Proxy selectedProxy = null;
        for (final Proxy proxy : proxies) {
            if (proxy.type().equals(Proxy.Type.HTTP)) {
                sa = proxy.address();
                if (sa instanceof InetSocketAddress) {
                    final InetSocketAddress inet = (InetSocketAddress)sa;
                    if (inet.isUnresolved()) {
                        sa = new InetSocketAddress(inet.getHostName(), inet.getPort());
                    }
                }
                selectedProxy = proxy;
                break;
            }
        }
        if (port == -1) {
            if ("ws".equalsIgnoreCase(scheme)) {
                port = 80;
            }
            else {
                port = 443;
            }
        }
        if (sa == null) {
            sa = new InetSocketAddress(host, port);
        }
        else {
            proxyConnect = createProxyRequest(host, port);
        }
        final Map<String, List<String>> reqHeaders = createRequestHeaders(host, port, secure, clientEndpointConfiguration);
        clientEndpointConfiguration.getConfigurator().beforeRequest((Map)reqHeaders);
        if (Constants.DEFAULT_ORIGIN_HEADER_VALUE != null && !reqHeaders.containsKey("Origin")) {
            final List<String> originValues = new ArrayList<String>(1);
            originValues.add(Constants.DEFAULT_ORIGIN_HEADER_VALUE);
            reqHeaders.put("Origin", originValues);
        }
        final ByteBuffer request = createRequest(path, reqHeaders);
        AsynchronousSocketChannel socketChannel;
        try {
            socketChannel = AsynchronousSocketChannel.open(this.getAsynchronousChannelGroup());
        }
        catch (final IOException ioe) {
            throw new DeploymentException(WsWebSocketContainer.sm.getString("wsWebSocketContainer.asynchronousSocketChannelFail"), (Throwable)ioe);
        }
        final Map<String, Object> userProperties = clientEndpointConfiguration.getUserProperties();
        long timeout = 5000L;
        final String timeoutValue = userProperties.get("org.apache.tomcat.websocket.IO_TIMEOUT_MS");
        if (timeoutValue != null) {
            timeout = Long.valueOf(timeoutValue).intValue();
        }
        final ByteBuffer response = ByteBuffer.allocate(this.getDefaultMaxBinaryMessageBufferSize());
        boolean success = false;
        final List<Extension> extensionsAgreed = new ArrayList<Extension>();
        Transformation transformation = null;
        AsyncChannelWrapper channel = null;
        String subProtocol;
        try {
            final Future<Void> fConnect = socketChannel.connect(sa);
            if (proxyConnect != null) {
                fConnect.get(timeout, TimeUnit.MILLISECONDS);
                channel = new AsyncChannelWrapperNonSecure(socketChannel);
                writeRequest(channel, proxyConnect, timeout);
                final HttpResponse httpResponse = this.processResponse(response, channel, timeout);
                if (httpResponse.getStatus() != 200) {
                    throw new DeploymentException(WsWebSocketContainer.sm.getString("wsWebSocketContainer.proxyConnectFail", new Object[] { selectedProxy, Integer.toString(httpResponse.getStatus()) }));
                }
            }
            if (secure) {
                final SSLEngine sslEngine = this.createSSLEngine(userProperties, host, port);
                channel = new AsyncChannelWrapperSecure(socketChannel, sslEngine);
            }
            else if (channel == null) {
                channel = new AsyncChannelWrapperNonSecure(socketChannel);
            }
            fConnect.get(timeout, TimeUnit.MILLISECONDS);
            final Future<Void> fHandshake = channel.handshake();
            fHandshake.get(timeout, TimeUnit.MILLISECONDS);
            if (this.log.isDebugEnabled()) {
                SocketAddress localAddress = null;
                try {
                    localAddress = channel.getLocalAddress();
                }
                catch (final IOException ex) {}
                this.log.debug((Object)WsWebSocketContainer.sm.getString("wsWebSocketContainer.connect.write", new Object[] { request.position(), request.limit(), localAddress }));
            }
            writeRequest(channel, request, timeout);
            final HttpResponse httpResponse2 = this.processResponse(response, channel, timeout);
            int maxRedirects = 20;
            final String maxRedirectsValue = userProperties.get("org.apache.tomcat.websocket.MAX_REDIRECTIONS");
            if (maxRedirectsValue != null) {
                maxRedirects = Integer.parseInt(maxRedirectsValue);
            }
            if (httpResponse2.status != 101) {
                if (isRedirectStatus(httpResponse2.status)) {
                    final List<String> locationHeader = httpResponse2.getHandshakeResponse().getHeaders().get("Location");
                    if (locationHeader == null || locationHeader.isEmpty() || locationHeader.get(0) == null || locationHeader.get(0).isEmpty()) {
                        throw new DeploymentException(WsWebSocketContainer.sm.getString("wsWebSocketContainer.missingLocationHeader", new Object[] { Integer.toString(httpResponse2.status) }));
                    }
                    URI redirectLocation = URI.create(locationHeader.get(0)).normalize();
                    if (!redirectLocation.isAbsolute()) {
                        redirectLocation = path.resolve(redirectLocation);
                    }
                    final String redirectScheme = redirectLocation.getScheme().toLowerCase();
                    if (redirectScheme.startsWith("http")) {
                        redirectLocation = new URI(redirectScheme.replace("http", "ws"), redirectLocation.getUserInfo(), redirectLocation.getHost(), redirectLocation.getPort(), redirectLocation.getPath(), redirectLocation.getQuery(), redirectLocation.getFragment());
                    }
                    if (!redirectSet.add(redirectLocation) || redirectSet.size() > maxRedirects) {
                        throw new DeploymentException(WsWebSocketContainer.sm.getString("wsWebSocketContainer.redirectThreshold", new Object[] { redirectLocation, Integer.toString(redirectSet.size()), Integer.toString(maxRedirects) }));
                    }
                    return this.connectToServerRecursive(clientEndpointHolder, clientEndpointConfiguration, redirectLocation, redirectSet);
                }
                else {
                    if (httpResponse2.status != 401) {
                        throw new DeploymentException(WsWebSocketContainer.sm.getString("wsWebSocketContainer.invalidStatus", new Object[] { Integer.toString(httpResponse2.status) }));
                    }
                    if (userProperties.get("Authorization") != null) {
                        throw new DeploymentException(WsWebSocketContainer.sm.getString("wsWebSocketContainer.failedAuthentication", new Object[] { httpResponse2.status }));
                    }
                    final List<String> wwwAuthenticateHeaders = httpResponse2.getHandshakeResponse().getHeaders().get("WWW-Authenticate");
                    if (wwwAuthenticateHeaders == null || wwwAuthenticateHeaders.isEmpty() || wwwAuthenticateHeaders.get(0) == null || wwwAuthenticateHeaders.get(0).isEmpty()) {
                        throw new DeploymentException(WsWebSocketContainer.sm.getString("wsWebSocketContainer.missingWWWAuthenticateHeader", new Object[] { Integer.toString(httpResponse2.status) }));
                    }
                    final String authScheme = wwwAuthenticateHeaders.get(0).split("\\s+", 2)[0];
                    final String requestUri = new String(request.array(), StandardCharsets.ISO_8859_1).split("\\s", 3)[1];
                    final Authenticator auth = AuthenticatorFactory.getAuthenticator(authScheme);
                    if (auth == null) {
                        throw new DeploymentException(WsWebSocketContainer.sm.getString("wsWebSocketContainer.unsupportedAuthScheme", new Object[] { httpResponse2.status, authScheme }));
                    }
                    userProperties.put("Authorization", auth.getAuthorization(requestUri, wwwAuthenticateHeaders.get(0), userProperties));
                    return this.connectToServerRecursive(clientEndpointHolder, clientEndpointConfiguration, path, redirectSet);
                }
            }
            else {
                final HandshakeResponse handshakeResponse = httpResponse2.getHandshakeResponse();
                clientEndpointConfiguration.getConfigurator().afterResponse(handshakeResponse);
                final List<String> protocolHeaders = handshakeResponse.getHeaders().get("Sec-WebSocket-Protocol");
                if (protocolHeaders == null || protocolHeaders.size() == 0) {
                    subProtocol = null;
                }
                else {
                    if (protocolHeaders.size() != 1) {
                        throw new DeploymentException(WsWebSocketContainer.sm.getString("wsWebSocketContainer.invalidSubProtocol"));
                    }
                    subProtocol = protocolHeaders.get(0);
                }
                final List<String> extHeaders = handshakeResponse.getHeaders().get("Sec-WebSocket-Extensions");
                if (extHeaders != null) {
                    for (final String extHeader : extHeaders) {
                        Util.parseExtensionHeader(extensionsAgreed, extHeader);
                    }
                }
                final TransformationFactory factory = TransformationFactory.getInstance();
                for (final Extension extension : extensionsAgreed) {
                    final List<List<Extension.Parameter>> wrapper = new ArrayList<List<Extension.Parameter>>(1);
                    wrapper.add(extension.getParameters());
                    final Transformation t = factory.create(extension.getName(), wrapper, false);
                    if (t == null) {
                        throw new DeploymentException(WsWebSocketContainer.sm.getString("wsWebSocketContainer.invalidExtensionParameters"));
                    }
                    if (transformation == null) {
                        transformation = t;
                    }
                    else {
                        transformation.setNext(t);
                    }
                }
                success = true;
            }
        }
        catch (final ExecutionException | InterruptedException | SSLException | EOFException | TimeoutException | URISyntaxException | AuthenticationException e) {
            throw new DeploymentException(WsWebSocketContainer.sm.getString("wsWebSocketContainer.httpRequestFailed", new Object[] { path }), (Throwable)e);
        }
        finally {
            if (!success) {
                if (channel != null) {
                    channel.close();
                }
                else {
                    try {
                        socketChannel.close();
                    }
                    catch (final IOException ex2) {}
                }
            }
        }
        final WsRemoteEndpointImplClient wsRemoteEndpointClient = new WsRemoteEndpointImplClient(channel);
        final WsSession wsSession = new WsSession(clientEndpointHolder, wsRemoteEndpointClient, this, extensionsAgreed, subProtocol, Collections.emptyMap(), secure, clientEndpointConfiguration);
        final WsFrameClient wsFrameClient = new WsFrameClient(response, channel, wsSession, transformation);
        wsRemoteEndpointClient.setTransformation(wsFrameClient.getTransformation());
        wsSession.getLocal().onOpen((Session)wsSession, (EndpointConfig)clientEndpointConfiguration);
        this.registerSession(wsSession.getLocal(), wsSession);
        wsFrameClient.startInputProcessing();
        return (Session)wsSession;
    }
    
    private static void writeRequest(final AsyncChannelWrapper channel, final ByteBuffer request, final long timeout) throws TimeoutException, InterruptedException, ExecutionException {
        int toWrite = request.limit();
        Future<Integer> fWrite = channel.write(request);
        Integer thisWrite;
        for (thisWrite = fWrite.get(timeout, TimeUnit.MILLISECONDS), toWrite -= thisWrite; toWrite > 0; toWrite -= thisWrite) {
            fWrite = channel.write(request);
            thisWrite = fWrite.get(timeout, TimeUnit.MILLISECONDS);
        }
    }
    
    private static boolean isRedirectStatus(final int httpResponseCode) {
        boolean isRedirect = false;
        switch (httpResponseCode) {
            case 300:
            case 301:
            case 302:
            case 303:
            case 305:
            case 307: {
                isRedirect = true;
                break;
            }
        }
        return isRedirect;
    }
    
    private static ByteBuffer createProxyRequest(final String host, final int port) {
        final StringBuilder request = new StringBuilder();
        request.append("CONNECT ");
        request.append(host);
        request.append(':');
        request.append(port);
        request.append(" HTTP/1.1\r\nProxy-Connection: keep-alive\r\nConnection: keepalive\r\nHost: ");
        request.append(host);
        request.append(':');
        request.append(port);
        request.append("\r\n\r\n");
        final byte[] bytes = request.toString().getBytes(StandardCharsets.ISO_8859_1);
        return ByteBuffer.wrap(bytes);
    }
    
    protected void registerSession(final Object key, final WsSession wsSession) {
        if (!wsSession.isOpen()) {
            return;
        }
        synchronized (this.endPointSessionMapLock) {
            if (this.endpointSessionMap.size() == 0) {
                BackgroundProcessManager.getInstance().register(this);
            }
            Set<WsSession> wsSessions = this.endpointSessionMap.get(key);
            if (wsSessions == null) {
                wsSessions = new HashSet<WsSession>();
                this.endpointSessionMap.put(key, wsSessions);
            }
            wsSessions.add(wsSession);
        }
        this.sessions.put(wsSession, wsSession);
    }
    
    protected void unregisterSession(final Object key, final WsSession wsSession) {
        synchronized (this.endPointSessionMapLock) {
            final Set<WsSession> wsSessions = this.endpointSessionMap.get(key);
            if (wsSessions != null) {
                wsSessions.remove(wsSession);
                if (wsSessions.size() == 0) {
                    this.endpointSessionMap.remove(key);
                }
            }
            if (this.endpointSessionMap.size() == 0) {
                BackgroundProcessManager.getInstance().unregister(this);
            }
        }
        this.sessions.remove(wsSession);
    }
    
    Set<Session> getOpenSessions(final Object key) {
        final HashSet<Session> result = new HashSet<Session>();
        synchronized (this.endPointSessionMapLock) {
            final Set<WsSession> sessions = this.endpointSessionMap.get(key);
            if (sessions != null) {
                result.addAll((Collection<?>)sessions);
            }
        }
        return result;
    }
    
    private static Map<String, List<String>> createRequestHeaders(final String host, final int port, final boolean secure, final ClientEndpointConfig clientEndpointConfiguration) {
        final Map<String, List<String>> headers = new HashMap<String, List<String>>();
        final List<Extension> extensions = clientEndpointConfiguration.getExtensions();
        final List<String> subProtocols = clientEndpointConfiguration.getPreferredSubprotocols();
        final Map<String, Object> userProperties = clientEndpointConfiguration.getUserProperties();
        if (userProperties.get("Authorization") != null) {
            final List<String> authValues = new ArrayList<String>(1);
            authValues.add(userProperties.get("Authorization"));
            headers.put("Authorization", authValues);
        }
        final List<String> hostValues = new ArrayList<String>(1);
        if ((port == 80 && !secure) || (port == 443 && secure)) {
            hostValues.add(host);
        }
        else {
            hostValues.add(host + ':' + port);
        }
        headers.put("Host", hostValues);
        final List<String> upgradeValues = new ArrayList<String>(1);
        upgradeValues.add("websocket");
        headers.put("Upgrade", upgradeValues);
        final List<String> connectionValues = new ArrayList<String>(1);
        connectionValues.add("upgrade");
        headers.put("Connection", connectionValues);
        final List<String> wsVersionValues = new ArrayList<String>(1);
        wsVersionValues.add("13");
        headers.put("Sec-WebSocket-Version", wsVersionValues);
        final List<String> wsKeyValues = new ArrayList<String>(1);
        wsKeyValues.add(generateWsKeyValue());
        headers.put("Sec-WebSocket-Key", wsKeyValues);
        if (subProtocols != null && subProtocols.size() > 0) {
            headers.put("Sec-WebSocket-Protocol", subProtocols);
        }
        if (extensions != null && extensions.size() > 0) {
            headers.put("Sec-WebSocket-Extensions", generateExtensionHeaders(extensions));
        }
        return headers;
    }
    
    private static List<String> generateExtensionHeaders(final List<Extension> extensions) {
        final List<String> result = new ArrayList<String>(extensions.size());
        for (final Extension extension : extensions) {
            final StringBuilder header = new StringBuilder();
            header.append(extension.getName());
            for (final Extension.Parameter param : extension.getParameters()) {
                header.append(';');
                header.append(param.getName());
                final String value = param.getValue();
                if (value != null && value.length() > 0) {
                    header.append('=');
                    header.append(value);
                }
            }
            result.add(header.toString());
        }
        return result;
    }
    
    private static String generateWsKeyValue() {
        final byte[] keyBytes = new byte[16];
        WsWebSocketContainer.RANDOM.nextBytes(keyBytes);
        return Base64.encodeBase64String(keyBytes);
    }
    
    private static ByteBuffer createRequest(final URI uri, final Map<String, List<String>> reqHeaders) {
        ByteBuffer result = ByteBuffer.allocate(4096);
        result.put(WsWebSocketContainer.GET_BYTES);
        final String path = uri.getPath();
        if (null == path || path.isEmpty()) {
            result.put(WsWebSocketContainer.ROOT_URI_BYTES);
        }
        else {
            result.put(uri.getRawPath().getBytes(StandardCharsets.ISO_8859_1));
        }
        final String query = uri.getRawQuery();
        if (query != null) {
            result.put((byte)63);
            result.put(query.getBytes(StandardCharsets.ISO_8859_1));
        }
        result.put(WsWebSocketContainer.HTTP_VERSION_BYTES);
        for (final Map.Entry<String, List<String>> entry : reqHeaders.entrySet()) {
            result = addHeader(result, entry.getKey(), entry.getValue());
        }
        result.put(WsWebSocketContainer.CRLF);
        result.flip();
        return result;
    }
    
    private static ByteBuffer addHeader(ByteBuffer result, final String key, final List<String> values) {
        if (values.isEmpty()) {
            return result;
        }
        result = putWithExpand(result, key.getBytes(StandardCharsets.ISO_8859_1));
        result = putWithExpand(result, ": ".getBytes(StandardCharsets.ISO_8859_1));
        result = putWithExpand(result, StringUtils.join((Collection)values).getBytes(StandardCharsets.ISO_8859_1));
        result = putWithExpand(result, WsWebSocketContainer.CRLF);
        return result;
    }
    
    private static ByteBuffer putWithExpand(ByteBuffer input, final byte[] bytes) {
        if (bytes.length > input.remaining()) {
            int newSize;
            if (bytes.length > input.capacity()) {
                newSize = 2 * bytes.length;
            }
            else {
                newSize = input.capacity() * 2;
            }
            final ByteBuffer expanded = ByteBuffer.allocate(newSize);
            input.flip();
            expanded.put(input);
            input = expanded;
        }
        return input.put(bytes);
    }
    
    private HttpResponse processResponse(final ByteBuffer response, final AsyncChannelWrapper channel, final long timeout) throws InterruptedException, ExecutionException, DeploymentException, EOFException, TimeoutException {
        final Map<String, List<String>> headers = (Map<String, List<String>>)new CaseInsensitiveKeyMap();
        int status = 0;
        boolean readStatus = false;
        boolean readHeaders = false;
        String line = null;
        while (!readHeaders) {
            response.clear();
            final Future<Integer> read = channel.read(response);
            Integer bytesRead;
            try {
                bytesRead = read.get(timeout, TimeUnit.MILLISECONDS);
            }
            catch (final TimeoutException e) {
                final TimeoutException te = new TimeoutException(WsWebSocketContainer.sm.getString("wsWebSocketContainer.responseFail", new Object[] { Integer.toString(status), headers }));
                te.initCause(e);
                throw te;
            }
            if (bytesRead == -1) {
                throw new EOFException(WsWebSocketContainer.sm.getString("wsWebSocketContainer.responseFail", new Object[] { Integer.toString(status), headers }));
            }
            response.flip();
            while (response.hasRemaining() && !readHeaders) {
                if (line == null) {
                    line = this.readLine(response);
                }
                else {
                    line += this.readLine(response);
                }
                if ("\r\n".equals(line)) {
                    readHeaders = true;
                }
                else {
                    if (!line.endsWith("\r\n")) {
                        continue;
                    }
                    if (readStatus) {
                        this.parseHeaders(line, headers);
                    }
                    else {
                        status = this.parseStatus(line);
                        readStatus = true;
                    }
                    line = null;
                }
            }
        }
        return new HttpResponse(status, (HandshakeResponse)new WsHandshakeResponse(headers));
    }
    
    private int parseStatus(final String line) throws DeploymentException {
        final String[] parts = line.trim().split(" ");
        if (parts.length < 2 || (!"HTTP/1.0".equals(parts[0]) && !"HTTP/1.1".equals(parts[0]))) {
            throw new DeploymentException(WsWebSocketContainer.sm.getString("wsWebSocketContainer.invalidStatus", new Object[] { line }));
        }
        try {
            return Integer.parseInt(parts[1]);
        }
        catch (final NumberFormatException nfe) {
            throw new DeploymentException(WsWebSocketContainer.sm.getString("wsWebSocketContainer.invalidStatus", new Object[] { line }));
        }
    }
    
    private void parseHeaders(final String line, final Map<String, List<String>> headers) {
        final int index = line.indexOf(58);
        if (index == -1) {
            this.log.warn((Object)WsWebSocketContainer.sm.getString("wsWebSocketContainer.invalidHeader", new Object[] { line }));
            return;
        }
        final String headerName = line.substring(0, index).trim().toLowerCase(Locale.ENGLISH);
        final String headerValue = line.substring(index + 1).trim();
        List<String> values = headers.get(headerName);
        if (values == null) {
            values = new ArrayList<String>(1);
            headers.put(headerName, values);
        }
        values.add(headerValue);
    }
    
    private String readLine(final ByteBuffer response) {
        final StringBuilder sb = new StringBuilder();
        char c = '\0';
        while (response.hasRemaining()) {
            c = (char)response.get();
            sb.append(c);
            if (c == '\n') {
                break;
            }
        }
        return sb.toString();
    }
    
    private SSLEngine createSSLEngine(final Map<String, Object> userProperties, final String host, final int port) throws DeploymentException {
        try {
            SSLContext sslContext = userProperties.get("org.apache.tomcat.websocket.SSL_CONTEXT");
            if (sslContext == null) {
                sslContext = SSLContext.getInstance("TLS");
                final String sslTrustStoreValue = userProperties.get("org.apache.tomcat.websocket.SSL_TRUSTSTORE");
                if (sslTrustStoreValue != null) {
                    String sslTrustStorePwdValue = userProperties.get("org.apache.tomcat.websocket.SSL_TRUSTSTORE_PWD");
                    if (sslTrustStorePwdValue == null) {
                        sslTrustStorePwdValue = "changeit";
                    }
                    final File keyStoreFile = new File(sslTrustStoreValue);
                    final KeyStore ks = KeyStore.getInstance("JKS");
                    try (final InputStream is = new FileInputStream(keyStoreFile)) {
                        KeyStoreUtil.load(ks, is, sslTrustStorePwdValue.toCharArray());
                    }
                    final TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                    tmf.init(ks);
                    sslContext.init(null, tmf.getTrustManagers(), null);
                }
                else {
                    sslContext.init(null, null, null);
                }
            }
            final SSLEngine engine = sslContext.createSSLEngine(host, port);
            final String sslProtocolsValue = userProperties.get("org.apache.tomcat.websocket.SSL_PROTOCOLS");
            if (sslProtocolsValue != null) {
                engine.setEnabledProtocols(sslProtocolsValue.split(","));
            }
            engine.setUseClientMode(true);
            final SSLParameters sslParams = engine.getSSLParameters();
            sslParams.setEndpointIdentificationAlgorithm("HTTPS");
            engine.setSSLParameters(sslParams);
            return engine;
        }
        catch (final Exception e) {
            throw new DeploymentException(WsWebSocketContainer.sm.getString("wsWebSocketContainer.sslEngineFail"), (Throwable)e);
        }
    }
    
    public long getDefaultMaxSessionIdleTimeout() {
        return this.defaultMaxSessionIdleTimeout;
    }
    
    public void setDefaultMaxSessionIdleTimeout(final long timeout) {
        this.defaultMaxSessionIdleTimeout = timeout;
    }
    
    public int getDefaultMaxBinaryMessageBufferSize() {
        return this.maxBinaryMessageBufferSize;
    }
    
    public void setDefaultMaxBinaryMessageBufferSize(final int max) {
        this.maxBinaryMessageBufferSize = max;
    }
    
    public int getDefaultMaxTextMessageBufferSize() {
        return this.maxTextMessageBufferSize;
    }
    
    public void setDefaultMaxTextMessageBufferSize(final int max) {
        this.maxTextMessageBufferSize = max;
    }
    
    public Set<Extension> getInstalledExtensions() {
        return Collections.emptySet();
    }
    
    public long getDefaultAsyncSendTimeout() {
        return this.defaultAsyncTimeout;
    }
    
    public void setAsyncSendTimeout(final long timeout) {
        this.defaultAsyncTimeout = timeout;
    }
    
    public void destroy() {
        final CloseReason cr = new CloseReason((CloseReason.CloseCode)CloseReason.CloseCodes.GOING_AWAY, WsWebSocketContainer.sm.getString("wsWebSocketContainer.shutdown"));
        for (final WsSession session : this.sessions.keySet()) {
            try {
                session.close(cr);
            }
            catch (final IOException ioe) {
                this.log.debug((Object)WsWebSocketContainer.sm.getString("wsWebSocketContainer.sessionCloseFail", new Object[] { session.getId() }), (Throwable)ioe);
            }
        }
        if (this.asynchronousChannelGroup != null) {
            synchronized (this.asynchronousChannelGroupLock) {
                if (this.asynchronousChannelGroup != null) {
                    AsyncChannelGroupUtil.unregister();
                    this.asynchronousChannelGroup = null;
                }
            }
        }
    }
    
    private AsynchronousChannelGroup getAsynchronousChannelGroup() {
        AsynchronousChannelGroup result = this.asynchronousChannelGroup;
        if (result == null) {
            synchronized (this.asynchronousChannelGroupLock) {
                if (this.asynchronousChannelGroup == null) {
                    this.asynchronousChannelGroup = AsyncChannelGroupUtil.register();
                }
                result = this.asynchronousChannelGroup;
            }
        }
        return result;
    }
    
    public void backgroundProcess() {
        ++this.backgroundProcessCount;
        if (this.backgroundProcessCount >= this.processPeriod) {
            this.backgroundProcessCount = 0;
            for (final WsSession wsSession : this.sessions.keySet()) {
                wsSession.checkExpiration();
            }
        }
    }
    
    public void setProcessPeriod(final int period) {
        this.processPeriod = period;
    }
    
    public int getProcessPeriod() {
        return this.processPeriod;
    }
    
    static {
        sm = StringManager.getManager((Class)WsWebSocketContainer.class);
        RANDOM = new Random();
        CRLF = new byte[] { 13, 10 };
        GET_BYTES = "GET ".getBytes(StandardCharsets.ISO_8859_1);
        ROOT_URI_BYTES = "/".getBytes(StandardCharsets.ISO_8859_1);
        HTTP_VERSION_BYTES = " HTTP/1.1\r\n".getBytes(StandardCharsets.ISO_8859_1);
    }
    
    private static class HttpResponse
    {
        private final int status;
        private final HandshakeResponse handshakeResponse;
        
        public HttpResponse(final int status, final HandshakeResponse handshakeResponse) {
            this.status = status;
            this.handshakeResponse = handshakeResponse;
        }
        
        public int getStatus() {
            return this.status;
        }
        
        public HandshakeResponse getHandshakeResponse() {
            return this.handshakeResponse;
        }
    }
}
