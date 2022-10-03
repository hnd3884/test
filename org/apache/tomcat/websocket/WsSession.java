package org.apache.tomcat.websocket;

import java.nio.charset.StandardCharsets;
import java.nio.channels.WritePendingException;
import java.nio.ByteBuffer;
import org.apache.tomcat.util.ExceptionUtils;
import javax.websocket.SendResult;
import java.io.IOException;
import javax.websocket.CloseReason;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.websocket.WebSocketContainer;
import org.apache.tomcat.websocket.server.DefaultServerEndpointConfigurator;
import org.apache.tomcat.InstanceManager;
import org.apache.tomcat.websocket.pojo.PojoEndpointServer;
import javax.naming.NamingException;
import javax.websocket.server.ServerEndpointConfig;
import javax.websocket.DeploymentException;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.juli.logging.LogFactory;
import javax.websocket.ClientEndpointConfig;
import javax.websocket.PongMessage;
import javax.websocket.MessageHandler;
import javax.websocket.Extension;
import javax.websocket.EndpointConfig;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.net.URI;
import javax.websocket.RemoteEndpoint;
import javax.websocket.Endpoint;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.tomcat.util.res.StringManager;
import org.apache.juli.logging.Log;
import javax.websocket.Session;

public class WsSession implements Session
{
    private final Log log;
    private static final StringManager sm;
    private static final byte[] ELLIPSIS_BYTES;
    private static final int ELLIPSIS_BYTES_LEN;
    private static final boolean SEC_CONFIGURATOR_USES_IMPL_DEFAULT;
    private static AtomicLong ids;
    private final Endpoint localEndpoint;
    private final WsRemoteEndpointImplBase wsRemoteEndpoint;
    private final RemoteEndpoint.Async remoteEndpointAsync;
    private final RemoteEndpoint.Basic remoteEndpointBasic;
    private final ClassLoader applicationClassLoader;
    private final WsWebSocketContainer webSocketContainer;
    private final URI requestUri;
    private final Map<String, List<String>> requestParameterMap;
    private final String queryString;
    private final Principal userPrincipal;
    private final EndpointConfig endpointConfig;
    private final List<Extension> negotiatedExtensions;
    private final String subProtocol;
    private final Map<String, String> pathParameters;
    private final boolean secure;
    private final String httpSessionId;
    private final String id;
    private volatile MessageHandler textMessageHandler;
    private volatile MessageHandler binaryMessageHandler;
    private volatile MessageHandler.Whole<PongMessage> pongMessageHandler;
    private volatile State state;
    private final Object stateLock;
    private final Map<String, Object> userProperties;
    private volatile int maxBinaryMessageBufferSize;
    private volatile int maxTextMessageBufferSize;
    private volatile long maxIdleTimeout;
    private volatile long lastActiveRead;
    private volatile long lastActiveWrite;
    private Map<FutureToSendHandler, FutureToSendHandler> futures;
    private WsFrameBase wsFrame;
    
    public WsSession(final ClientEndpointHolder clientEndpointHolder, final WsRemoteEndpointImplBase wsRemoteEndpoint, final WsWebSocketContainer wsWebSocketContainer, final List<Extension> negotiatedExtensions, final String subProtocol, final Map<String, String> pathParameters, final boolean secure, final ClientEndpointConfig clientEndpointConfig) throws DeploymentException {
        this.log = LogFactory.getLog((Class)WsSession.class);
        this.textMessageHandler = null;
        this.binaryMessageHandler = null;
        this.pongMessageHandler = null;
        this.state = State.OPEN;
        this.stateLock = new Object();
        this.userProperties = new ConcurrentHashMap<String, Object>();
        this.maxBinaryMessageBufferSize = Constants.DEFAULT_BUFFER_SIZE;
        this.maxTextMessageBufferSize = Constants.DEFAULT_BUFFER_SIZE;
        this.maxIdleTimeout = 0L;
        this.lastActiveRead = System.currentTimeMillis();
        this.lastActiveWrite = System.currentTimeMillis();
        this.futures = new ConcurrentHashMap<FutureToSendHandler, FutureToSendHandler>();
        (this.wsRemoteEndpoint = wsRemoteEndpoint).setSession(this);
        this.remoteEndpointAsync = (RemoteEndpoint.Async)new WsRemoteEndpointAsync(wsRemoteEndpoint);
        this.remoteEndpointBasic = (RemoteEndpoint.Basic)new WsRemoteEndpointBasic(wsRemoteEndpoint);
        this.webSocketContainer = wsWebSocketContainer;
        this.applicationClassLoader = Thread.currentThread().getContextClassLoader();
        wsRemoteEndpoint.setSendTimeout(wsWebSocketContainer.getDefaultAsyncSendTimeout());
        this.maxBinaryMessageBufferSize = this.webSocketContainer.getDefaultMaxBinaryMessageBufferSize();
        this.maxTextMessageBufferSize = this.webSocketContainer.getDefaultMaxTextMessageBufferSize();
        this.maxIdleTimeout = this.webSocketContainer.getDefaultMaxSessionIdleTimeout();
        this.requestUri = null;
        this.requestParameterMap = Collections.emptyMap();
        this.queryString = null;
        this.userPrincipal = null;
        this.httpSessionId = null;
        this.negotiatedExtensions = negotiatedExtensions;
        if (subProtocol == null) {
            this.subProtocol = "";
        }
        else {
            this.subProtocol = subProtocol;
        }
        this.pathParameters = pathParameters;
        this.secure = secure;
        this.wsRemoteEndpoint.setEncoders((EndpointConfig)clientEndpointConfig);
        this.endpointConfig = (EndpointConfig)clientEndpointConfig;
        this.userProperties.putAll(this.endpointConfig.getUserProperties());
        this.id = Long.toHexString(WsSession.ids.getAndIncrement());
        this.localEndpoint = clientEndpointHolder.getInstance(this.getInstanceManager());
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)WsSession.sm.getString("wsSession.created", new Object[] { this.id }));
        }
    }
    
    public WsSession(final WsRemoteEndpointImplBase wsRemoteEndpoint, final WsWebSocketContainer wsWebSocketContainer, final URI requestUri, final Map<String, List<String>> requestParameterMap, final String queryString, final Principal userPrincipal, final String httpSessionId, final List<Extension> negotiatedExtensions, final String subProtocol, final Map<String, String> pathParameters, final boolean secure, final ServerEndpointConfig serverEndpointConfig) throws DeploymentException {
        this.log = LogFactory.getLog((Class)WsSession.class);
        this.textMessageHandler = null;
        this.binaryMessageHandler = null;
        this.pongMessageHandler = null;
        this.state = State.OPEN;
        this.stateLock = new Object();
        this.userProperties = new ConcurrentHashMap<String, Object>();
        this.maxBinaryMessageBufferSize = Constants.DEFAULT_BUFFER_SIZE;
        this.maxTextMessageBufferSize = Constants.DEFAULT_BUFFER_SIZE;
        this.maxIdleTimeout = 0L;
        this.lastActiveRead = System.currentTimeMillis();
        this.lastActiveWrite = System.currentTimeMillis();
        this.futures = new ConcurrentHashMap<FutureToSendHandler, FutureToSendHandler>();
        (this.wsRemoteEndpoint = wsRemoteEndpoint).setSession(this);
        this.remoteEndpointAsync = (RemoteEndpoint.Async)new WsRemoteEndpointAsync(wsRemoteEndpoint);
        this.remoteEndpointBasic = (RemoteEndpoint.Basic)new WsRemoteEndpointBasic(wsRemoteEndpoint);
        this.webSocketContainer = wsWebSocketContainer;
        this.applicationClassLoader = Thread.currentThread().getContextClassLoader();
        wsRemoteEndpoint.setSendTimeout(wsWebSocketContainer.getDefaultAsyncSendTimeout());
        this.maxBinaryMessageBufferSize = this.webSocketContainer.getDefaultMaxBinaryMessageBufferSize();
        this.maxTextMessageBufferSize = this.webSocketContainer.getDefaultMaxTextMessageBufferSize();
        this.maxIdleTimeout = this.webSocketContainer.getDefaultMaxSessionIdleTimeout();
        this.requestUri = requestUri;
        if (requestParameterMap == null) {
            this.requestParameterMap = Collections.emptyMap();
        }
        else {
            this.requestParameterMap = requestParameterMap;
        }
        this.queryString = queryString;
        this.userPrincipal = userPrincipal;
        this.httpSessionId = httpSessionId;
        this.negotiatedExtensions = negotiatedExtensions;
        if (subProtocol == null) {
            this.subProtocol = "";
        }
        else {
            this.subProtocol = subProtocol;
        }
        this.pathParameters = pathParameters;
        this.secure = secure;
        this.wsRemoteEndpoint.setEncoders((EndpointConfig)serverEndpointConfig);
        this.endpointConfig = (EndpointConfig)serverEndpointConfig;
        this.userProperties.putAll(this.endpointConfig.getUserProperties());
        this.id = Long.toHexString(WsSession.ids.getAndIncrement());
        final InstanceManager instanceManager = this.getInstanceManager();
        final ServerEndpointConfig.Configurator configurator = serverEndpointConfig.getConfigurator();
        final Class<?> clazz = serverEndpointConfig.getEndpointClass();
        Object endpointInstance = null;
        try {
            Label_0418: {
                if (instanceManager == null || !this.isDefaultConfigurator(configurator)) {
                    endpointInstance = configurator.getEndpointInstance((Class)clazz);
                    if (instanceManager == null) {
                        break Label_0418;
                    }
                    try {
                        instanceManager.newInstance(endpointInstance);
                        break Label_0418;
                    }
                    catch (final ReflectiveOperationException | NamingException e) {
                        throw new DeploymentException(WsSession.sm.getString("wsSession.instanceNew"), (Throwable)e);
                    }
                }
                endpointInstance = instanceManager.newInstance((Class)clazz);
            }
        }
        catch (final ReflectiveOperationException | NamingException e) {
            throw new DeploymentException(WsSession.sm.getString("wsSession.instanceCreateFailed"), (Throwable)e);
        }
        if (endpointInstance instanceof Endpoint) {
            this.localEndpoint = (Endpoint)endpointInstance;
        }
        else {
            this.localEndpoint = new PojoEndpointServer(pathParameters, endpointInstance);
        }
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)WsSession.sm.getString("wsSession.created", new Object[] { this.id }));
        }
    }
    
    private boolean isDefaultConfigurator(final ServerEndpointConfig.Configurator configurator) {
        return configurator.getClass().equals(DefaultServerEndpointConfigurator.class) || (WsSession.SEC_CONFIGURATOR_USES_IMPL_DEFAULT && configurator.getClass().equals(ServerEndpointConfig.Configurator.class));
    }
    
    @Deprecated
    public WsSession(final Endpoint localEndpoint, final WsRemoteEndpointImplBase wsRemoteEndpoint, final WsWebSocketContainer wsWebSocketContainer, final URI requestUri, final Map<String, List<String>> requestParameterMap, final String queryString, final Principal userPrincipal, final String httpSessionId, final List<Extension> negotiatedExtensions, final String subProtocol, final Map<String, String> pathParameters, final boolean secure, final EndpointConfig endpointConfig) throws DeploymentException {
        this.log = LogFactory.getLog((Class)WsSession.class);
        this.textMessageHandler = null;
        this.binaryMessageHandler = null;
        this.pongMessageHandler = null;
        this.state = State.OPEN;
        this.stateLock = new Object();
        this.userProperties = new ConcurrentHashMap<String, Object>();
        this.maxBinaryMessageBufferSize = Constants.DEFAULT_BUFFER_SIZE;
        this.maxTextMessageBufferSize = Constants.DEFAULT_BUFFER_SIZE;
        this.maxIdleTimeout = 0L;
        this.lastActiveRead = System.currentTimeMillis();
        this.lastActiveWrite = System.currentTimeMillis();
        this.futures = new ConcurrentHashMap<FutureToSendHandler, FutureToSendHandler>();
        this.localEndpoint = localEndpoint;
        (this.wsRemoteEndpoint = wsRemoteEndpoint).setSession(this);
        this.remoteEndpointAsync = (RemoteEndpoint.Async)new WsRemoteEndpointAsync(wsRemoteEndpoint);
        this.remoteEndpointBasic = (RemoteEndpoint.Basic)new WsRemoteEndpointBasic(wsRemoteEndpoint);
        this.webSocketContainer = wsWebSocketContainer;
        this.applicationClassLoader = Thread.currentThread().getContextClassLoader();
        wsRemoteEndpoint.setSendTimeout(wsWebSocketContainer.getDefaultAsyncSendTimeout());
        this.maxBinaryMessageBufferSize = this.webSocketContainer.getDefaultMaxBinaryMessageBufferSize();
        this.maxTextMessageBufferSize = this.webSocketContainer.getDefaultMaxTextMessageBufferSize();
        this.maxIdleTimeout = this.webSocketContainer.getDefaultMaxSessionIdleTimeout();
        this.requestUri = requestUri;
        if (requestParameterMap == null) {
            this.requestParameterMap = Collections.emptyMap();
        }
        else {
            this.requestParameterMap = requestParameterMap;
        }
        this.queryString = queryString;
        this.userPrincipal = userPrincipal;
        this.httpSessionId = httpSessionId;
        this.negotiatedExtensions = negotiatedExtensions;
        if (subProtocol == null) {
            this.subProtocol = "";
        }
        else {
            this.subProtocol = subProtocol;
        }
        this.pathParameters = pathParameters;
        this.secure = secure;
        this.wsRemoteEndpoint.setEncoders(endpointConfig);
        this.endpointConfig = endpointConfig;
        this.userProperties.putAll(endpointConfig.getUserProperties());
        this.id = Long.toHexString(WsSession.ids.getAndIncrement());
        final InstanceManager instanceManager = this.getInstanceManager();
        if (instanceManager != null) {
            try {
                instanceManager.newInstance((Object)localEndpoint);
            }
            catch (final Exception e) {
                throw new DeploymentException(WsSession.sm.getString("wsSession.instanceNew"), (Throwable)e);
            }
        }
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)WsSession.sm.getString("wsSession.created", new Object[] { this.id }));
        }
    }
    
    public InstanceManager getInstanceManager() {
        return this.webSocketContainer.getInstanceManager(this.applicationClassLoader);
    }
    
    public WebSocketContainer getContainer() {
        this.checkState();
        return (WebSocketContainer)this.webSocketContainer;
    }
    
    public void addMessageHandler(final MessageHandler listener) {
        final Class<?> target = Util.getMessageType(listener);
        this.doAddMessageHandler(target, listener);
    }
    
    public <T> void addMessageHandler(final Class<T> clazz, final MessageHandler.Partial<T> handler) throws IllegalStateException {
        this.doAddMessageHandler(clazz, (MessageHandler)handler);
    }
    
    public <T> void addMessageHandler(final Class<T> clazz, final MessageHandler.Whole<T> handler) throws IllegalStateException {
        this.doAddMessageHandler(clazz, (MessageHandler)handler);
    }
    
    private void doAddMessageHandler(final Class<?> target, final MessageHandler listener) {
        this.checkState();
        final Set<MessageHandlerResult> mhResults = Util.getMessageHandlers(target, listener, this.endpointConfig, (Session)this);
        for (final MessageHandlerResult mhResult : mhResults) {
            switch (mhResult.getType()) {
                case TEXT: {
                    if (this.textMessageHandler != null) {
                        throw new IllegalStateException(WsSession.sm.getString("wsSession.duplicateHandlerText"));
                    }
                    this.textMessageHandler = mhResult.getHandler();
                    continue;
                }
                case BINARY: {
                    if (this.binaryMessageHandler != null) {
                        throw new IllegalStateException(WsSession.sm.getString("wsSession.duplicateHandlerBinary"));
                    }
                    this.binaryMessageHandler = mhResult.getHandler();
                    continue;
                }
                case PONG: {
                    if (this.pongMessageHandler != null) {
                        throw new IllegalStateException(WsSession.sm.getString("wsSession.duplicateHandlerPong"));
                    }
                    final MessageHandler handler = mhResult.getHandler();
                    if (handler instanceof MessageHandler.Whole) {
                        this.pongMessageHandler = (MessageHandler.Whole<PongMessage>)handler;
                        continue;
                    }
                    throw new IllegalStateException(WsSession.sm.getString("wsSession.invalidHandlerTypePong"));
                }
                default: {
                    throw new IllegalArgumentException(WsSession.sm.getString("wsSession.unknownHandlerType", new Object[] { listener, mhResult.getType() }));
                }
            }
        }
    }
    
    public Set<MessageHandler> getMessageHandlers() {
        this.checkState();
        final Set<MessageHandler> result = new HashSet<MessageHandler>();
        if (this.binaryMessageHandler != null) {
            result.add(this.binaryMessageHandler);
        }
        if (this.textMessageHandler != null) {
            result.add(this.textMessageHandler);
        }
        if (this.pongMessageHandler != null) {
            result.add((MessageHandler)this.pongMessageHandler);
        }
        return result;
    }
    
    public void removeMessageHandler(final MessageHandler listener) {
        this.checkState();
        if (listener == null) {
            return;
        }
        MessageHandler wrapped = null;
        if (listener instanceof WrappedMessageHandler) {
            wrapped = ((WrappedMessageHandler)listener).getWrappedHandler();
        }
        if (wrapped == null) {
            wrapped = listener;
        }
        boolean removed = false;
        if (wrapped.equals(this.textMessageHandler) || listener.equals(this.textMessageHandler)) {
            this.textMessageHandler = null;
            removed = true;
        }
        if (wrapped.equals(this.binaryMessageHandler) || listener.equals(this.binaryMessageHandler)) {
            this.binaryMessageHandler = null;
            removed = true;
        }
        if (wrapped.equals(this.pongMessageHandler) || listener.equals(this.pongMessageHandler)) {
            this.pongMessageHandler = null;
            removed = true;
        }
        if (!removed) {
            throw new IllegalStateException(WsSession.sm.getString("wsSession.removeHandlerFailed", new Object[] { listener }));
        }
    }
    
    public String getProtocolVersion() {
        this.checkState();
        return "13";
    }
    
    public String getNegotiatedSubprotocol() {
        this.checkState();
        return this.subProtocol;
    }
    
    public List<Extension> getNegotiatedExtensions() {
        this.checkState();
        return this.negotiatedExtensions;
    }
    
    public boolean isSecure() {
        this.checkState();
        return this.secure;
    }
    
    public boolean isOpen() {
        return this.state == State.OPEN;
    }
    
    public long getMaxIdleTimeout() {
        this.checkState();
        return this.maxIdleTimeout;
    }
    
    public void setMaxIdleTimeout(final long timeout) {
        this.checkState();
        this.maxIdleTimeout = timeout;
    }
    
    public void setMaxBinaryMessageBufferSize(final int max) {
        this.checkState();
        this.maxBinaryMessageBufferSize = max;
    }
    
    public int getMaxBinaryMessageBufferSize() {
        this.checkState();
        return this.maxBinaryMessageBufferSize;
    }
    
    public void setMaxTextMessageBufferSize(final int max) {
        this.checkState();
        this.maxTextMessageBufferSize = max;
    }
    
    public int getMaxTextMessageBufferSize() {
        this.checkState();
        return this.maxTextMessageBufferSize;
    }
    
    public Set<Session> getOpenSessions() {
        this.checkState();
        return this.webSocketContainer.getOpenSessions(this.getSessionMapKey());
    }
    
    public RemoteEndpoint.Async getAsyncRemote() {
        this.checkState();
        return this.remoteEndpointAsync;
    }
    
    public RemoteEndpoint.Basic getBasicRemote() {
        this.checkState();
        return this.remoteEndpointBasic;
    }
    
    public void close() throws IOException {
        this.close(new CloseReason((CloseReason.CloseCode)CloseReason.CloseCodes.NORMAL_CLOSURE, ""));
    }
    
    public void close(final CloseReason closeReason) throws IOException {
        this.doClose(closeReason, closeReason);
    }
    
    public void doClose(final CloseReason closeReasonMessage, final CloseReason closeReasonLocal) {
        this.doClose(closeReasonMessage, closeReasonLocal, false);
    }
    
    public void doClose(final CloseReason closeReasonMessage, final CloseReason closeReasonLocal, final boolean closeSocket) {
        if (this.state != State.OPEN) {
            return;
        }
        synchronized (this.stateLock) {
            if (this.state != State.OPEN) {
                return;
            }
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)WsSession.sm.getString("wsSession.doClose", new Object[] { this.id }));
            }
            try {
                this.wsRemoteEndpoint.setBatchingAllowed(false);
            }
            catch (final IOException e) {
                this.log.warn((Object)WsSession.sm.getString("wsSession.flushFailOnClose"), (Throwable)e);
                this.fireEndpointOnError(e);
            }
            this.state = State.OUTPUT_CLOSED;
            this.sendCloseMessage(closeReasonMessage);
            if (closeSocket) {
                this.wsRemoteEndpoint.close();
            }
            this.fireEndpointOnClose(closeReasonLocal);
        }
        final IOException ioe = new IOException(WsSession.sm.getString("wsSession.messageFailed"));
        final SendResult sr = new SendResult((Throwable)ioe);
        for (final FutureToSendHandler f2sh : this.futures.keySet()) {
            f2sh.onResult(sr);
        }
    }
    
    public void onClose(final CloseReason closeReason) {
        synchronized (this.stateLock) {
            if (this.state != State.CLOSED) {
                try {
                    this.wsRemoteEndpoint.setBatchingAllowed(false);
                }
                catch (final IOException e) {
                    this.log.warn((Object)WsSession.sm.getString("wsSession.flushFailOnClose"), (Throwable)e);
                    this.fireEndpointOnError(e);
                }
                if (this.state == State.OPEN) {
                    this.state = State.OUTPUT_CLOSED;
                    this.sendCloseMessage(closeReason);
                    this.fireEndpointOnClose(closeReason);
                }
                this.state = State.CLOSED;
                this.wsRemoteEndpoint.close();
            }
        }
    }
    
    private void fireEndpointOnClose(final CloseReason closeReason) {
        Throwable throwable = null;
        final InstanceManager instanceManager = this.getInstanceManager();
        final Thread t = Thread.currentThread();
        final ClassLoader cl = t.getContextClassLoader();
        t.setContextClassLoader(this.applicationClassLoader);
        try {
            this.localEndpoint.onClose((Session)this, closeReason);
        }
        catch (final Throwable t2) {
            ExceptionUtils.handleThrowable(t2);
            throwable = t2;
        }
        finally {
            if (instanceManager != null) {
                try {
                    instanceManager.destroyInstance((Object)this.localEndpoint);
                }
                catch (final Throwable t3) {
                    ExceptionUtils.handleThrowable(t3);
                    if (throwable == null) {
                        throwable = t3;
                    }
                }
            }
            t.setContextClassLoader(cl);
        }
        if (throwable != null) {
            this.fireEndpointOnError(throwable);
        }
    }
    
    private void fireEndpointOnError(final Throwable throwable) {
        final Thread t = Thread.currentThread();
        final ClassLoader cl = t.getContextClassLoader();
        t.setContextClassLoader(this.applicationClassLoader);
        try {
            this.localEndpoint.onError((Session)this, throwable);
        }
        finally {
            t.setContextClassLoader(cl);
        }
    }
    
    private void sendCloseMessage(final CloseReason closeReason) {
        final ByteBuffer msg = ByteBuffer.allocate(125);
        final CloseReason.CloseCode closeCode = closeReason.getCloseCode();
        if (closeCode == CloseReason.CloseCodes.CLOSED_ABNORMALLY) {
            msg.putShort((short)CloseReason.CloseCodes.PROTOCOL_ERROR.getCode());
        }
        else {
            msg.putShort((short)closeCode.getCode());
        }
        final String reason = closeReason.getReasonPhrase();
        if (reason != null && reason.length() > 0) {
            appendCloseReasonWithTruncation(msg, reason);
        }
        msg.flip();
        try {
            this.wsRemoteEndpoint.sendMessageBlock((byte)8, msg, true);
        }
        catch (final IOException | WritePendingException e) {
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)WsSession.sm.getString("wsSession.sendCloseFail", new Object[] { this.id }), (Throwable)e);
            }
            this.wsRemoteEndpoint.close();
            if (closeCode != CloseReason.CloseCodes.CLOSED_ABNORMALLY) {
                this.localEndpoint.onError((Session)this, (Throwable)e);
            }
        }
        finally {
            this.webSocketContainer.unregisterSession(this.getSessionMapKey(), this);
        }
    }
    
    private Object getSessionMapKey() {
        if (this.endpointConfig instanceof ServerEndpointConfig) {
            return ((ServerEndpointConfig)this.endpointConfig).getPath();
        }
        return this.localEndpoint;
    }
    
    protected static void appendCloseReasonWithTruncation(final ByteBuffer msg, final String reason) {
        final byte[] reasonBytes = reason.getBytes(StandardCharsets.UTF_8);
        if (reasonBytes.length <= 123) {
            msg.put(reasonBytes);
        }
        else {
            int remaining = 123 - WsSession.ELLIPSIS_BYTES_LEN;
            int pos = 0;
            for (byte[] bytesNext = reason.substring(pos, pos + 1).getBytes(StandardCharsets.UTF_8); remaining >= bytesNext.length; remaining -= bytesNext.length, ++pos, bytesNext = reason.substring(pos, pos + 1).getBytes(StandardCharsets.UTF_8)) {
                msg.put(bytesNext);
            }
            msg.put(WsSession.ELLIPSIS_BYTES);
        }
    }
    
    protected void registerFuture(final FutureToSendHandler f2sh) {
        this.futures.put(f2sh, f2sh);
        if (this.state == State.OPEN) {
            return;
        }
        if (f2sh.isDone()) {
            return;
        }
        final IOException ioe = new IOException(WsSession.sm.getString("wsSession.messageFailed"));
        final SendResult sr = new SendResult((Throwable)ioe);
        f2sh.onResult(sr);
    }
    
    protected void unregisterFuture(final FutureToSendHandler f2sh) {
        this.futures.remove(f2sh);
    }
    
    public URI getRequestURI() {
        this.checkState();
        return this.requestUri;
    }
    
    public Map<String, List<String>> getRequestParameterMap() {
        this.checkState();
        return this.requestParameterMap;
    }
    
    public String getQueryString() {
        this.checkState();
        return this.queryString;
    }
    
    public Principal getUserPrincipal() {
        this.checkState();
        return this.userPrincipal;
    }
    
    public Map<String, String> getPathParameters() {
        this.checkState();
        return this.pathParameters;
    }
    
    public String getId() {
        return this.id;
    }
    
    public Map<String, Object> getUserProperties() {
        this.checkState();
        return this.userProperties;
    }
    
    public Endpoint getLocal() {
        return this.localEndpoint;
    }
    
    public String getHttpSessionId() {
        return this.httpSessionId;
    }
    
    protected MessageHandler getTextMessageHandler() {
        return this.textMessageHandler;
    }
    
    protected MessageHandler getBinaryMessageHandler() {
        return this.binaryMessageHandler;
    }
    
    protected MessageHandler.Whole<PongMessage> getPongMessageHandler() {
        return this.pongMessageHandler;
    }
    
    protected void updateLastActiveRead() {
        this.lastActiveRead = System.currentTimeMillis();
    }
    
    protected void updateLastActiveWrite() {
        this.lastActiveWrite = System.currentTimeMillis();
    }
    
    protected void checkExpiration() {
        final long timeout = this.maxIdleTimeout;
        final long timeoutRead = this.getMaxIdleTimeoutRead();
        final long timeoutWrite = this.getMaxIdleTimeoutWrite();
        final long currentTime = System.currentTimeMillis();
        String key = null;
        if (timeoutRead > 0L && currentTime - this.lastActiveRead > timeoutRead) {
            key = "wsSession.timeoutRead";
        }
        else if (timeoutWrite > 0L && currentTime - this.lastActiveWrite > timeoutWrite) {
            key = "wsSession.timeoutWrite";
        }
        else if (timeout > 0L && currentTime - this.lastActiveRead > timeout && currentTime - this.lastActiveWrite > timeout) {
            key = "wsSession.timeout";
        }
        if (key != null) {
            final String msg = WsSession.sm.getString(key, new Object[] { this.getId() });
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)msg);
            }
            this.doClose(new CloseReason((CloseReason.CloseCode)CloseReason.CloseCodes.GOING_AWAY, msg), new CloseReason((CloseReason.CloseCode)CloseReason.CloseCodes.CLOSED_ABNORMALLY, msg));
        }
    }
    
    private long getMaxIdleTimeoutRead() {
        final Object timeout = this.userProperties.get("org.apache.tomcat.websocket.READ_IDLE_TIMEOUT_MS");
        if (timeout instanceof Long) {
            return (long)timeout;
        }
        return 0L;
    }
    
    private long getMaxIdleTimeoutWrite() {
        final Object timeout = this.userProperties.get("org.apache.tomcat.websocket.WRITE_IDLE_TIMEOUT_MS");
        if (timeout instanceof Long) {
            return (long)timeout;
        }
        return 0L;
    }
    
    private void checkState() {
        if (this.state == State.CLOSED) {
            throw new IllegalStateException(WsSession.sm.getString("wsSession.closed", new Object[] { this.id }));
        }
    }
    
    void setWsFrame(final WsFrameBase wsFrame) {
        this.wsFrame = wsFrame;
    }
    
    public void suspend() {
        this.wsFrame.suspend();
    }
    
    public void resume() {
        this.wsFrame.resume();
    }
    
    static {
        sm = StringManager.getManager((Class)WsSession.class);
        ELLIPSIS_BYTES = "\u2026".getBytes(StandardCharsets.UTF_8);
        ELLIPSIS_BYTES_LEN = WsSession.ELLIPSIS_BYTES.length;
        WsSession.ids = new AtomicLong(0L);
        final ServerEndpointConfig.Builder builder = ServerEndpointConfig.Builder.create((Class)Object.class, "/");
        final ServerEndpointConfig sec = builder.build();
        SEC_CONFIGURATOR_USES_IMPL_DEFAULT = sec.getConfigurator().getClass().equals(DefaultServerEndpointConfigurator.class);
    }
    
    private enum State
    {
        OPEN, 
        OUTPUT_CLOSED, 
        CLOSED;
    }
}
