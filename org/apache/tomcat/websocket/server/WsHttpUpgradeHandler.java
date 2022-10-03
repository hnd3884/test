package org.apache.tomcat.websocket.server;

import org.apache.tomcat.util.net.SSLSupport;
import java.io.IOException;
import javax.websocket.CloseReason;
import org.apache.tomcat.websocket.WsIOException;
import org.apache.tomcat.util.net.AbstractEndpoint;
import org.apache.tomcat.util.net.SocketEvent;
import javax.websocket.DeploymentException;
import javax.websocket.EndpointConfig;
import javax.websocket.Session;
import org.apache.tomcat.websocket.WsWebSocketContainer;
import org.apache.tomcat.websocket.WsRemoteEndpointImplBase;
import javax.servlet.http.HttpSession;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.websocket.WsSession;
import javax.servlet.http.WebConnection;
import java.util.Map;
import org.apache.tomcat.websocket.Transformation;
import javax.websocket.Extension;
import java.util.List;
import javax.websocket.server.ServerEndpointConfig;
import javax.websocket.Endpoint;
import org.apache.coyote.http11.upgrade.UpgradeInfo;
import org.apache.tomcat.util.net.SocketWrapperBase;
import org.apache.tomcat.util.res.StringManager;
import org.apache.juli.logging.Log;
import org.apache.coyote.http11.upgrade.InternalHttpUpgradeHandler;

public class WsHttpUpgradeHandler implements InternalHttpUpgradeHandler
{
    private final Log log;
    private static final StringManager sm;
    private final ClassLoader applicationClassLoader;
    private SocketWrapperBase<?> socketWrapper;
    private UpgradeInfo upgradeInfo;
    private Endpoint ep;
    private ServerEndpointConfig serverEndpointConfig;
    private WsServerContainer webSocketContainer;
    private WsHandshakeRequest handshakeRequest;
    private List<Extension> negotiatedExtensions;
    private String subProtocol;
    private Transformation transformation;
    private Map<String, String> pathParameters;
    private boolean secure;
    private WebConnection connection;
    private WsRemoteEndpointImplServer wsRemoteEndpointServer;
    private WsFrameServer wsFrame;
    private WsSession wsSession;
    
    public WsHttpUpgradeHandler() {
        this.log = LogFactory.getLog((Class)WsHttpUpgradeHandler.class);
        this.upgradeInfo = new UpgradeInfo();
        this.applicationClassLoader = Thread.currentThread().getContextClassLoader();
    }
    
    public void setSocketWrapper(final SocketWrapperBase<?> socketWrapper) {
        this.socketWrapper = socketWrapper;
    }
    
    public void preInit(final ServerEndpointConfig serverEndpointConfig, final WsServerContainer wsc, final WsHandshakeRequest handshakeRequest, final List<Extension> negotiatedExtensionsPhase2, final String subProtocol, final Transformation transformation, final Map<String, String> pathParameters, final boolean secure) {
        this.serverEndpointConfig = serverEndpointConfig;
        this.webSocketContainer = wsc;
        this.handshakeRequest = handshakeRequest;
        this.negotiatedExtensions = negotiatedExtensionsPhase2;
        this.subProtocol = subProtocol;
        this.transformation = transformation;
        this.pathParameters = pathParameters;
        this.secure = secure;
    }
    
    public void init(final WebConnection connection) {
        this.connection = connection;
        if (this.serverEndpointConfig == null) {
            throw new IllegalStateException(WsHttpUpgradeHandler.sm.getString("wsHttpUpgradeHandler.noPreInit"));
        }
        String httpSessionId = null;
        final Object session = this.handshakeRequest.getHttpSession();
        if (session != null) {
            httpSessionId = ((HttpSession)session).getId();
        }
        final Thread t = Thread.currentThread();
        final ClassLoader cl = t.getContextClassLoader();
        t.setContextClassLoader(this.applicationClassLoader);
        try {
            this.wsRemoteEndpointServer = new WsRemoteEndpointImplServer(this.socketWrapper, this.upgradeInfo, this.webSocketContainer);
            this.wsSession = new WsSession(this.wsRemoteEndpointServer, this.webSocketContainer, this.handshakeRequest.getRequestURI(), this.handshakeRequest.getParameterMap(), this.handshakeRequest.getQueryString(), this.handshakeRequest.getUserPrincipal(), httpSessionId, this.negotiatedExtensions, this.subProtocol, this.pathParameters, this.secure, this.serverEndpointConfig);
            this.ep = this.wsSession.getLocal();
            this.wsFrame = new WsFrameServer(this.socketWrapper, this.upgradeInfo, this.wsSession, this.transformation, this.applicationClassLoader);
            this.wsRemoteEndpointServer.setTransformation(this.wsFrame.getTransformation());
            this.ep.onOpen((Session)this.wsSession, (EndpointConfig)this.serverEndpointConfig);
            this.webSocketContainer.registerSession(this.serverEndpointConfig.getPath(), this.wsSession);
        }
        catch (final DeploymentException e) {
            throw new IllegalArgumentException((Throwable)e);
        }
        finally {
            t.setContextClassLoader(cl);
        }
    }
    
    public UpgradeInfo getUpgradeInfo() {
        return this.upgradeInfo;
    }
    
    public AbstractEndpoint.Handler.SocketState upgradeDispatch(final SocketEvent status) {
        switch (status) {
            case OPEN_READ: {
                try {
                    return this.wsFrame.notifyDataAvailable();
                }
                catch (final WsIOException ws) {
                    this.close(ws.getCloseReason());
                }
                catch (final IOException ioe) {
                    this.onError(ioe);
                    final CloseReason cr = new CloseReason((CloseReason.CloseCode)CloseReason.CloseCodes.CLOSED_ABNORMALLY, ioe.getMessage());
                    this.close(cr);
                }
                return AbstractEndpoint.Handler.SocketState.CLOSED;
            }
            case OPEN_WRITE: {
                this.wsRemoteEndpointServer.onWritePossible(false);
                break;
            }
            case STOP: {
                CloseReason cr2 = new CloseReason((CloseReason.CloseCode)CloseReason.CloseCodes.GOING_AWAY, WsHttpUpgradeHandler.sm.getString("wsHttpUpgradeHandler.serverStop"));
                try {
                    this.wsSession.close(cr2);
                    break;
                }
                catch (final IOException ioe2) {
                    this.onError(ioe2);
                    cr2 = new CloseReason((CloseReason.CloseCode)CloseReason.CloseCodes.CLOSED_ABNORMALLY, ioe2.getMessage());
                    this.close(cr2);
                    return AbstractEndpoint.Handler.SocketState.CLOSED;
                }
            }
            case ERROR: {
                final String msg = WsHttpUpgradeHandler.sm.getString("wsHttpUpgradeHandler.closeOnError");
                this.wsSession.doClose(new CloseReason((CloseReason.CloseCode)CloseReason.CloseCodes.GOING_AWAY, msg), new CloseReason((CloseReason.CloseCode)CloseReason.CloseCodes.CLOSED_ABNORMALLY, msg));
            }
            case DISCONNECT:
            case TIMEOUT:
            case CONNECT_FAIL: {
                return AbstractEndpoint.Handler.SocketState.CLOSED;
            }
        }
        if (this.wsFrame.isOpen()) {
            return AbstractEndpoint.Handler.SocketState.UPGRADED;
        }
        return AbstractEndpoint.Handler.SocketState.CLOSED;
    }
    
    public void timeoutAsync(final long now) {
    }
    
    public void pause() {
    }
    
    public void destroy() {
        final WebConnection connection = this.connection;
        if (connection != null) {
            this.connection = null;
            try {
                connection.close();
            }
            catch (final Exception e) {
                this.log.error((Object)WsHttpUpgradeHandler.sm.getString("wsHttpUpgradeHandler.destroyFailed"), (Throwable)e);
            }
        }
    }
    
    private void onError(final Throwable throwable) {
        final Thread t = Thread.currentThread();
        final ClassLoader cl = t.getContextClassLoader();
        t.setContextClassLoader(this.applicationClassLoader);
        try {
            this.ep.onError((Session)this.wsSession, throwable);
        }
        finally {
            t.setContextClassLoader(cl);
        }
    }
    
    private void close(final CloseReason cr) {
        this.wsSession.onClose(cr);
    }
    
    public void setSslSupport(final SSLSupport sslSupport) {
    }
    
    static {
        sm = StringManager.getManager((Class)WsHttpUpgradeHandler.class);
    }
}
