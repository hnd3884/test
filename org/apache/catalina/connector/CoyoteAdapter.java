package org.apache.catalina.connector;

import org.apache.catalina.util.ServerInfo;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.buf.B2CConverter;
import org.apache.tomcat.util.http.ServerCookie;
import org.apache.tomcat.util.http.ServerCookies;
import java.nio.charset.Charset;
import org.apache.tomcat.util.buf.ByteChunk;
import org.apache.catalina.Authenticator;
import java.security.Principal;
import org.apache.catalina.authenticator.AuthenticatorBase;
import org.apache.tomcat.util.buf.CharChunk;
import org.apache.tomcat.util.buf.MessageBytes;
import java.nio.charset.StandardCharsets;
import org.apache.catalina.util.URLEncoder;
import org.apache.catalina.util.SessionConfig;
import javax.servlet.ServletException;
import org.apache.catalina.Wrapper;
import org.apache.catalina.Host;
import javax.servlet.ReadListener;
import javax.servlet.WriteListener;
import org.apache.catalina.Context;
import org.apache.catalina.core.AsyncContextImpl;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.coyote.ActionCode;
import org.apache.tomcat.util.net.SocketEvent;
import org.apache.coyote.Response;
import org.apache.coyote.Request;
import org.apache.tomcat.util.res.StringManager;
import javax.servlet.SessionTrackingMode;
import java.util.EnumSet;
import org.apache.juli.logging.Log;
import org.apache.coyote.Adapter;

public class CoyoteAdapter implements Adapter
{
    private static final Log log;
    private static final String POWERED_BY;
    private static final EnumSet<SessionTrackingMode> SSL_ONLY;
    public static final int ADAPTER_NOTES = 1;
    protected static final boolean ALLOW_BACKSLASH;
    private static final ThreadLocal<String> THREAD_NAME;
    private final Connector connector;
    protected static final StringManager sm;
    
    public CoyoteAdapter(final Connector connector) {
        this.connector = connector;
    }
    
    public boolean asyncDispatch(final Request req, final Response res, final SocketEvent status) throws Exception {
        final org.apache.catalina.connector.Request request = (org.apache.catalina.connector.Request)req.getNote(1);
        final org.apache.catalina.connector.Response response = (org.apache.catalina.connector.Response)res.getNote(1);
        if (request == null) {
            throw new IllegalStateException(CoyoteAdapter.sm.getString("coyoteAdapter.nullRequest"));
        }
        boolean success = true;
        final AsyncContextImpl asyncConImpl = request.getAsyncContextInternal();
        req.getRequestProcessor().setWorkerThreadName((String)CoyoteAdapter.THREAD_NAME.get());
        try {
            if (!request.isAsync()) {
                response.setSuspended(false);
            }
            if (status == SocketEvent.TIMEOUT) {
                if (!asyncConImpl.timeout()) {
                    asyncConImpl.setErrorState(null, false);
                }
            }
            else if (status == SocketEvent.ERROR) {
                success = false;
                final Throwable t = (Throwable)req.getAttribute("javax.servlet.error.exception");
                final Context context = request.getContext();
                ClassLoader oldCL = null;
                try {
                    oldCL = context.bind(false, (ClassLoader)null);
                    if (req.getReadListener() != null) {
                        req.getReadListener().onError(t);
                    }
                    if (res.getWriteListener() != null) {
                        res.getWriteListener().onError(t);
                    }
                    res.action(ActionCode.CLOSE_NOW, (Object)t);
                    asyncConImpl.setErrorState(t, true);
                }
                finally {
                    context.unbind(false, oldCL);
                }
            }
            if (!request.isAsyncDispatching() && request.isAsync()) {
                final WriteListener writeListener = res.getWriteListener();
                final ReadListener readListener = req.getReadListener();
                if (writeListener != null && status == SocketEvent.OPEN_WRITE) {
                    final Context context2 = request.getContext();
                    ClassLoader oldCL2 = null;
                    try {
                        oldCL2 = context2.bind(false, (ClassLoader)null);
                        res.onWritePossible();
                        if (request.isFinished() && req.sendAllDataReadEvent() && readListener != null) {
                            readListener.onAllDataRead();
                        }
                        if (response.getCoyoteResponse().isExceptionPresent()) {
                            throw response.getCoyoteResponse().getErrorException();
                        }
                    }
                    catch (final Throwable t2) {
                        ExceptionUtils.handleThrowable(t2);
                        writeListener.onError(t2);
                        res.action(ActionCode.CLOSE_NOW, (Object)t2);
                        asyncConImpl.setErrorState(t2, true);
                    }
                    finally {
                        context2.unbind(false, oldCL2);
                    }
                }
                else if (readListener != null && status == SocketEvent.OPEN_READ) {
                    final Context context2 = request.getContext();
                    ClassLoader oldCL2 = null;
                    try {
                        oldCL2 = context2.bind(false, (ClassLoader)null);
                        if (!request.isFinished()) {
                            req.onDataAvailable();
                        }
                        if (request.isFinished() && req.sendAllDataReadEvent()) {
                            readListener.onAllDataRead();
                        }
                        if (request.getCoyoteRequest().isExceptionPresent()) {
                            throw request.getCoyoteRequest().getErrorException();
                        }
                    }
                    catch (final Throwable t2) {
                        ExceptionUtils.handleThrowable(t2);
                        readListener.onError(t2);
                        res.action(ActionCode.CLOSE_NOW, (Object)t2);
                        asyncConImpl.setErrorState(t2, true);
                    }
                    finally {
                        context2.unbind(false, oldCL2);
                    }
                }
            }
            if (!request.isAsyncDispatching() && request.isAsync() && response.isErrorReportRequired()) {
                this.connector.getService().getContainer().getPipeline().getFirst().invoke(request, response);
            }
            if (request.isAsyncDispatching()) {
                this.connector.getService().getContainer().getPipeline().getFirst().invoke(request, response);
                final Throwable t = (Throwable)request.getAttribute("javax.servlet.error.exception");
                if (t != null) {
                    asyncConImpl.setErrorState(t, true);
                }
            }
            if (!request.isAsync()) {
                request.finishRequest();
                response.finishResponse();
            }
            final AtomicBoolean error = new AtomicBoolean(false);
            res.action(ActionCode.IS_ERROR, (Object)error);
            if (error.get()) {
                if (request.isAsyncCompleting()) {
                    res.action(ActionCode.ASYNC_POST_PROCESS, (Object)null);
                }
                success = false;
            }
        }
        catch (final IOException e) {
            success = false;
        }
        catch (final Throwable t) {
            ExceptionUtils.handleThrowable(t);
            success = false;
            CoyoteAdapter.log.error((Object)CoyoteAdapter.sm.getString("coyoteAdapter.asyncDispatch"), t);
        }
        finally {
            if (!success) {
                res.setStatus(500);
            }
            if (!success || !request.isAsync()) {
                long time = 0L;
                if (req.getStartTime() != -1L) {
                    time = System.currentTimeMillis() - req.getStartTime();
                }
                final Context context3 = request.getContext();
                if (context3 != null) {
                    context3.logAccess(request, response, time, false);
                }
                else {
                    this.log(req, res, time);
                }
            }
            req.getRequestProcessor().setWorkerThreadName((String)null);
            if (!success || !request.isAsync()) {
                this.updateWrapperErrorCount(request, response);
                request.recycle();
                response.recycle();
            }
        }
        return success;
    }
    
    public void service(final Request req, final Response res) throws Exception {
        org.apache.catalina.connector.Request request = (org.apache.catalina.connector.Request)req.getNote(1);
        org.apache.catalina.connector.Response response = (org.apache.catalina.connector.Response)res.getNote(1);
        if (request == null) {
            request = this.connector.createRequest();
            request.setCoyoteRequest(req);
            response = this.connector.createResponse();
            response.setCoyoteResponse(res);
            request.setResponse(response);
            response.setRequest(request);
            req.setNote(1, (Object)request);
            res.setNote(1, (Object)response);
            req.getParameters().setQueryStringCharset(this.connector.getURICharset());
        }
        if (this.connector.getXpoweredBy()) {
            response.addHeader("X-Powered-By", CoyoteAdapter.POWERED_BY);
        }
        boolean async = false;
        boolean postParseSuccess = false;
        req.getRequestProcessor().setWorkerThreadName((String)CoyoteAdapter.THREAD_NAME.get());
        try {
            postParseSuccess = this.postParseRequest(req, request, res, response);
            if (postParseSuccess) {
                request.setAsyncSupported(this.connector.getService().getContainer().getPipeline().isAsyncSupported());
                this.connector.getService().getContainer().getPipeline().getFirst().invoke(request, response);
            }
            if (request.isAsync()) {
                async = true;
                final ReadListener readListener = req.getReadListener();
                if (readListener != null && request.isFinished()) {
                    ClassLoader oldCL = null;
                    try {
                        oldCL = request.getContext().bind(false, (ClassLoader)null);
                        if (req.sendAllDataReadEvent()) {
                            req.getReadListener().onAllDataRead();
                        }
                    }
                    finally {
                        request.getContext().unbind(false, oldCL);
                    }
                }
                final Throwable throwable = (Throwable)request.getAttribute("javax.servlet.error.exception");
                if (!request.isAsyncCompleting() && throwable != null) {
                    request.getAsyncContextInternal().setErrorState(throwable, true);
                }
            }
            else {
                request.finishRequest();
                response.finishResponse();
            }
        }
        catch (final IOException ex) {}
        finally {
            final AtomicBoolean error = new AtomicBoolean(false);
            res.action(ActionCode.IS_ERROR, (Object)error);
            if (request.isAsyncCompleting() && error.get()) {
                res.action(ActionCode.ASYNC_POST_PROCESS, (Object)null);
                async = false;
            }
            if (!async && postParseSuccess) {
                final Context context = request.getContext();
                final Host host = request.getHost();
                final long time = System.currentTimeMillis() - req.getStartTime();
                if (context != null) {
                    context.logAccess(request, response, time, false);
                }
                else if (response.isError()) {
                    if (host != null) {
                        host.logAccess(request, response, time, false);
                    }
                    else {
                        this.connector.getService().getContainer().logAccess(request, response, time, false);
                    }
                }
            }
            req.getRequestProcessor().setWorkerThreadName((String)null);
            if (!async) {
                this.updateWrapperErrorCount(request, response);
                request.recycle();
                response.recycle();
            }
        }
    }
    
    private void updateWrapperErrorCount(final org.apache.catalina.connector.Request request, final org.apache.catalina.connector.Response response) {
        if (response.isError()) {
            final Wrapper wrapper = request.getWrapper();
            if (wrapper != null) {
                wrapper.incrementErrorCount();
            }
        }
    }
    
    public boolean prepare(final Request req, final Response res) throws IOException, ServletException {
        final org.apache.catalina.connector.Request request = (org.apache.catalina.connector.Request)req.getNote(1);
        final org.apache.catalina.connector.Response response = (org.apache.catalina.connector.Response)res.getNote(1);
        return this.postParseRequest(req, request, res, response);
    }
    
    public void log(final Request req, final Response res, final long time) {
        org.apache.catalina.connector.Request request = (org.apache.catalina.connector.Request)req.getNote(1);
        org.apache.catalina.connector.Response response = (org.apache.catalina.connector.Response)res.getNote(1);
        if (request == null) {
            request = this.connector.createRequest();
            request.setCoyoteRequest(req);
            response = this.connector.createResponse();
            response.setCoyoteResponse(res);
            request.setResponse(response);
            response.setRequest(request);
            req.setNote(1, (Object)request);
            res.setNote(1, (Object)response);
            req.getParameters().setQueryStringCharset(this.connector.getURICharset());
        }
        try {
            boolean logged = false;
            final Context context = request.mappingData.context;
            final Host host = request.mappingData.host;
            if (context != null) {
                logged = true;
                context.logAccess(request, response, time, true);
            }
            else if (host != null) {
                logged = true;
                host.logAccess(request, response, time, true);
            }
            if (!logged) {
                this.connector.getService().getContainer().logAccess(request, response, time, true);
            }
        }
        catch (final Throwable t) {
            ExceptionUtils.handleThrowable(t);
            CoyoteAdapter.log.warn((Object)CoyoteAdapter.sm.getString("coyoteAdapter.accesslogFail"), t);
        }
        finally {
            this.updateWrapperErrorCount(request, response);
            request.recycle();
            response.recycle();
        }
    }
    
    public void checkRecycled(final Request req, final Response res) {
        final org.apache.catalina.connector.Request request = (org.apache.catalina.connector.Request)req.getNote(1);
        final org.apache.catalina.connector.Response response = (org.apache.catalina.connector.Response)res.getNote(1);
        String messageKey = null;
        if (request != null && request.getHost() != null) {
            messageKey = "coyoteAdapter.checkRecycled.request";
        }
        else if (response != null && response.getContentWritten() != 0L) {
            messageKey = "coyoteAdapter.checkRecycled.response";
        }
        if (messageKey != null) {
            this.log(req, res, 0L);
            if (this.connector.getState().isAvailable()) {
                if (CoyoteAdapter.log.isInfoEnabled()) {
                    CoyoteAdapter.log.info((Object)CoyoteAdapter.sm.getString(messageKey), (Throwable)new RecycleRequiredException());
                }
            }
            else if (CoyoteAdapter.log.isDebugEnabled()) {
                CoyoteAdapter.log.debug((Object)CoyoteAdapter.sm.getString(messageKey), (Throwable)new RecycleRequiredException());
            }
        }
    }
    
    public String getDomain() {
        return this.connector.getDomain();
    }
    
    protected boolean postParseRequest(final Request req, final org.apache.catalina.connector.Request request, final Response res, final org.apache.catalina.connector.Response response) throws IOException, ServletException {
        if (req.scheme().isNull()) {
            req.scheme().setString(this.connector.getScheme());
            request.setSecure(this.connector.getSecure());
        }
        else {
            request.setSecure(req.scheme().equals("https"));
        }
        final String proxyName = this.connector.getProxyName();
        final int proxyPort = this.connector.getProxyPort();
        if (proxyPort != 0) {
            req.setServerPort(proxyPort);
        }
        else if (req.getServerPort() == -1) {
            if (req.scheme().equals("https")) {
                req.setServerPort(443);
            }
            else {
                req.setServerPort(80);
            }
        }
        if (proxyName != null) {
            req.serverName().setString(proxyName);
        }
        final MessageBytes undecodedURI = req.requestURI();
        if (undecodedURI.equals("*")) {
            if (req.method().equalsIgnoreCase("OPTIONS")) {
                final StringBuilder allow = new StringBuilder();
                allow.append("GET, HEAD, POST, PUT, DELETE, OPTIONS");
                if (this.connector.getAllowTrace()) {
                    allow.append(", TRACE");
                }
                res.setHeader("Allow", allow.toString());
                this.connector.getService().getContainer().logAccess(request, response, 0L, true);
                return false;
            }
            response.sendError(400, "Invalid URI");
        }
        final MessageBytes decodedURI = req.decodedURI();
        if (undecodedURI.getType() == 2) {
            decodedURI.duplicate(undecodedURI);
            this.parsePathParameters(req, request);
            try {
                req.getURLDecoder().convert(decodedURI.getByteChunk(), this.connector.getEncodedSolidusHandlingInternal());
            }
            catch (final IOException ioe) {
                response.sendError(400, "Invalid URI: " + ioe.getMessage());
            }
            if (normalize(req.decodedURI())) {
                this.convertURI(decodedURI, request);
                if (!checkNormalize(req.decodedURI())) {
                    response.sendError(400, "Invalid URI");
                }
            }
            else {
                response.sendError(400, "Invalid URI");
            }
        }
        else {
            decodedURI.toChars();
            final CharChunk uriCC = decodedURI.getCharChunk();
            final int semicolon = uriCC.indexOf(';');
            if (semicolon > 0) {
                decodedURI.setChars(uriCC.getBuffer(), uriCC.getStart(), semicolon);
            }
        }
        MessageBytes serverName;
        if (this.connector.getUseIPVHosts()) {
            serverName = req.localName();
            if (serverName.isNull()) {
                res.action(ActionCode.REQ_LOCAL_NAME_ATTRIBUTE, (Object)null);
            }
        }
        else {
            serverName = req.serverName();
        }
        String version = null;
        Context versionContext = null;
        boolean mapRequired = true;
        if (response.isError()) {
            decodedURI.recycle();
        }
        while (mapRequired) {
            this.connector.getService().getMapper().map(serverName, decodedURI, version, request.getMappingData());
            if (request.getContext() == null) {
                return true;
            }
            if (request.getServletContext().getEffectiveSessionTrackingModes().contains(SessionTrackingMode.URL)) {
                final String sessionID = request.getPathParameter(SessionConfig.getSessionUriParamName(request.getContext()));
                if (sessionID != null) {
                    request.setRequestedSessionId(sessionID);
                    request.setRequestedSessionURL(true);
                }
            }
            try {
                this.parseSessionCookiesId(request);
            }
            catch (final IllegalArgumentException e) {
                if (!response.isError()) {
                    response.setError();
                    response.sendError(400);
                }
                return true;
            }
            this.parseSessionSslId(request);
            final String sessionID = request.getRequestedSessionId();
            mapRequired = false;
            if (version == null || request.getContext() != versionContext) {
                version = null;
                versionContext = null;
                final Context[] contexts = request.getMappingData().contexts;
                if (contexts != null && sessionID != null) {
                    int i = contexts.length;
                    while (i > 0) {
                        final Context ctxt = contexts[i - 1];
                        if (ctxt.getManager().findSession(sessionID) != null) {
                            if (!ctxt.equals(request.getMappingData().context)) {
                                version = ctxt.getWebappVersion();
                                versionContext = ctxt;
                                request.getMappingData().recycle();
                                mapRequired = true;
                                request.recycleSessionInfo();
                                request.recycleCookieInfo(true);
                                break;
                            }
                            break;
                        }
                        else {
                            --i;
                        }
                    }
                }
            }
            if (mapRequired || !request.getContext().getPaused()) {
                continue;
            }
            try {
                Thread.sleep(1000L);
            }
            catch (final InterruptedException ex) {}
            request.getMappingData().recycle();
            mapRequired = true;
        }
        final MessageBytes redirectPathMB = request.getMappingData().redirectPath;
        if (!redirectPathMB.isNull()) {
            String redirectPath = URLEncoder.DEFAULT.encode(redirectPathMB.toString(), StandardCharsets.UTF_8);
            final String query = request.getQueryString();
            if (request.isRequestedSessionIdFromURL()) {
                redirectPath = redirectPath + ";" + SessionConfig.getSessionUriParamName(request.getContext()) + "=" + request.getRequestedSessionId();
            }
            if (query != null) {
                redirectPath = redirectPath + "?" + query;
            }
            response.sendRedirect(redirectPath);
            request.getContext().logAccess(request, response, 0L, true);
            return false;
        }
        if (!this.connector.getAllowTrace() && req.method().equalsIgnoreCase("TRACE")) {
            final Wrapper wrapper = request.getWrapper();
            String header = null;
            if (wrapper != null) {
                final String[] methods = wrapper.getServletMethods();
                if (methods != null) {
                    for (final String method : methods) {
                        if (!"TRACE".equals(method)) {
                            if (header == null) {
                                header = method;
                            }
                            else {
                                header = header + ", " + method;
                            }
                        }
                    }
                }
            }
            if (header != null) {
                res.addHeader("Allow", header);
            }
            response.sendError(405, "TRACE method is not allowed");
            return true;
        }
        this.doConnectorAuthenticationAuthorization(req, request);
        return true;
    }
    
    private void doConnectorAuthenticationAuthorization(final Request req, final org.apache.catalina.connector.Request request) {
        final String username = req.getRemoteUser().toString();
        if (username != null) {
            if (CoyoteAdapter.log.isDebugEnabled()) {
                CoyoteAdapter.log.debug((Object)CoyoteAdapter.sm.getString("coyoteAdapter.authenticate", new Object[] { username }));
            }
            if (req.getRemoteUserNeedsAuthorization()) {
                final Authenticator authenticator = request.getContext().getAuthenticator();
                if (!(authenticator instanceof AuthenticatorBase)) {
                    if (CoyoteAdapter.log.isDebugEnabled()) {
                        CoyoteAdapter.log.debug((Object)CoyoteAdapter.sm.getString("coyoteAdapter.authorize", new Object[] { username }));
                    }
                    request.setUserPrincipal(request.getContext().getRealm().authenticate(username));
                }
            }
            else {
                request.setUserPrincipal(new CoyotePrincipal(username));
            }
        }
        final String authType = req.getAuthType().toString();
        if (authType != null) {
            request.setAuthType(authType);
        }
    }
    
    protected void parsePathParameters(final Request req, final org.apache.catalina.connector.Request request) {
        req.decodedURI().toBytes();
        final ByteChunk uriBC = req.decodedURI().getByteChunk();
        int semicolon = uriBC.indexOf(';', 0);
        if (semicolon == -1) {
            return;
        }
        final Charset charset = this.connector.getURICharset();
        if (CoyoteAdapter.log.isDebugEnabled()) {
            CoyoteAdapter.log.debug((Object)CoyoteAdapter.sm.getString("coyoteAdapter.debug", new Object[] { "uriBC", uriBC.toString() }));
            CoyoteAdapter.log.debug((Object)CoyoteAdapter.sm.getString("coyoteAdapter.debug", new Object[] { "semicolon", String.valueOf(semicolon) }));
            CoyoteAdapter.log.debug((Object)CoyoteAdapter.sm.getString("coyoteAdapter.debug", new Object[] { "enc", charset.name() }));
        }
        while (semicolon > -1) {
            final int start = uriBC.getStart();
            final int end = uriBC.getEnd();
            final int pathParamStart = semicolon + 1;
            final int pathParamEnd = ByteChunk.findBytes(uriBC.getBuffer(), start + pathParamStart, end, new byte[] { 59, 47 });
            String pv = null;
            if (pathParamEnd >= 0) {
                if (charset != null) {
                    pv = new String(uriBC.getBuffer(), start + pathParamStart, pathParamEnd - pathParamStart, charset);
                }
                final byte[] buf = uriBC.getBuffer();
                for (int i = 0; i < end - start - pathParamEnd; ++i) {
                    buf[start + semicolon + i] = buf[start + i + pathParamEnd];
                }
                uriBC.setBytes(buf, start, end - start - pathParamEnd + semicolon);
            }
            else {
                if (charset != null) {
                    pv = new String(uriBC.getBuffer(), start + pathParamStart, end - start - pathParamStart, charset);
                }
                uriBC.setEnd(start + semicolon);
            }
            if (CoyoteAdapter.log.isDebugEnabled()) {
                CoyoteAdapter.log.debug((Object)CoyoteAdapter.sm.getString("coyoteAdapter.debug", new Object[] { "pathParamStart", String.valueOf(pathParamStart) }));
                CoyoteAdapter.log.debug((Object)CoyoteAdapter.sm.getString("coyoteAdapter.debug", new Object[] { "pathParamEnd", String.valueOf(pathParamEnd) }));
                CoyoteAdapter.log.debug((Object)CoyoteAdapter.sm.getString("coyoteAdapter.debug", new Object[] { "pv", pv }));
            }
            if (pv != null) {
                final int equals = pv.indexOf(61);
                if (equals > -1) {
                    final String name = pv.substring(0, equals);
                    final String value = pv.substring(equals + 1);
                    request.addPathParameter(name, value);
                    if (CoyoteAdapter.log.isDebugEnabled()) {
                        CoyoteAdapter.log.debug((Object)CoyoteAdapter.sm.getString("coyoteAdapter.debug", new Object[] { "equals", String.valueOf(equals) }));
                        CoyoteAdapter.log.debug((Object)CoyoteAdapter.sm.getString("coyoteAdapter.debug", new Object[] { "name", name }));
                        CoyoteAdapter.log.debug((Object)CoyoteAdapter.sm.getString("coyoteAdapter.debug", new Object[] { "value", value }));
                    }
                }
            }
            semicolon = uriBC.indexOf(';', semicolon);
        }
    }
    
    protected void parseSessionSslId(final org.apache.catalina.connector.Request request) {
        if (request.getRequestedSessionId() == null && CoyoteAdapter.SSL_ONLY.equals(request.getServletContext().getEffectiveSessionTrackingModes()) && request.connector.secure) {
            final String sessionId = (String)request.getAttribute("javax.servlet.request.ssl_session_id");
            if (sessionId != null) {
                request.setRequestedSessionId(sessionId);
                request.setRequestedSessionSSL(true);
            }
        }
    }
    
    protected void parseSessionCookiesId(final org.apache.catalina.connector.Request request) {
        final Context context = request.getMappingData().context;
        if (context != null && !context.getServletContext().getEffectiveSessionTrackingModes().contains(SessionTrackingMode.COOKIE)) {
            return;
        }
        final ServerCookies serverCookies = request.getServerCookies();
        final int count = serverCookies.getCookieCount();
        if (count <= 0) {
            return;
        }
        final String sessionCookieName = SessionConfig.getSessionCookieName(context);
        for (int i = 0; i < count; ++i) {
            final ServerCookie scookie = serverCookies.getCookie(i);
            if (scookie.getName().equals(sessionCookieName)) {
                if (!request.isRequestedSessionIdFromCookie()) {
                    this.convertMB(scookie.getValue());
                    request.setRequestedSessionId(scookie.getValue().toString());
                    request.setRequestedSessionCookie(true);
                    request.setRequestedSessionURL(false);
                    if (CoyoteAdapter.log.isDebugEnabled()) {
                        CoyoteAdapter.log.debug((Object)(" Requested cookie session id is " + request.getRequestedSessionId()));
                    }
                }
                else if (!request.isRequestedSessionIdValid()) {
                    this.convertMB(scookie.getValue());
                    request.setRequestedSessionId(scookie.getValue().toString());
                }
            }
        }
    }
    
    protected void convertURI(final MessageBytes uri, final org.apache.catalina.connector.Request request) throws IOException {
        final ByteChunk bc = uri.getByteChunk();
        final int length = bc.getLength();
        final CharChunk cc = uri.getCharChunk();
        cc.allocate(length, -1);
        final Charset charset = this.connector.getURICharset();
        B2CConverter conv = request.getURIConverter();
        if (conv == null) {
            conv = new B2CConverter(charset, true);
            request.setURIConverter(conv);
        }
        else {
            conv.recycle();
        }
        try {
            conv.convert(bc, cc, true);
            uri.setChars(cc.getBuffer(), cc.getStart(), cc.getLength());
        }
        catch (final IOException ioe) {
            request.getResponse().sendError(400);
        }
    }
    
    protected void convertMB(final MessageBytes mb) {
        if (mb.getType() != 2) {
            return;
        }
        final ByteChunk bc = mb.getByteChunk();
        final CharChunk cc = mb.getCharChunk();
        final int length = bc.getLength();
        cc.allocate(length, -1);
        final byte[] bbuf = bc.getBuffer();
        final char[] cbuf = cc.getBuffer();
        final int start = bc.getStart();
        for (int i = 0; i < length; ++i) {
            cbuf[i] = (char)(bbuf[i + start] & 0xFF);
        }
        mb.setChars(cbuf, 0, length);
    }
    
    public static boolean normalize(final MessageBytes uriMB) {
        final ByteChunk uriBC = uriMB.getByteChunk();
        final byte[] b = uriBC.getBytes();
        final int start = uriBC.getStart();
        int end = uriBC.getEnd();
        if (start == end) {
            return false;
        }
        int pos = 0;
        int index = 0;
        if (b[start] != 47 && b[start] != 92) {
            return false;
        }
        for (pos = start; pos < end; ++pos) {
            if (b[pos] == 92) {
                if (!CoyoteAdapter.ALLOW_BACKSLASH) {
                    return false;
                }
                b[pos] = 47;
            }
            else if (b[pos] == 0) {
                return false;
            }
        }
        for (pos = start; pos < end - 1; ++pos) {
            if (b[pos] == 47) {
                while (pos + 1 < end && b[pos + 1] == 47) {
                    copyBytes(b, pos, pos + 1, end - pos - 1);
                    --end;
                }
            }
        }
        if (end - start >= 2 && b[end - 1] == 46 && (b[end - 2] == 47 || (b[end - 2] == 46 && b[end - 3] == 47))) {
            b[end] = 47;
            ++end;
        }
        uriBC.setEnd(end);
        index = 0;
        while (true) {
            index = uriBC.indexOf("/./", 0, 3, index);
            if (index < 0) {
                break;
            }
            copyBytes(b, start + index, start + index + 2, end - start - index - 2);
            end -= 2;
            uriBC.setEnd(end);
        }
        index = 0;
        while (true) {
            index = uriBC.indexOf("/../", 0, 4, index);
            if (index < 0) {
                return true;
            }
            if (index == 0) {
                return false;
            }
            int index2;
            for (index2 = -1, pos = start + index - 1; pos >= 0 && index2 < 0; --pos) {
                if (b[pos] == 47) {
                    index2 = pos;
                }
            }
            copyBytes(b, start + index2, start + index + 3, end - start - index - 3);
            end = end + index2 - index - 3;
            uriBC.setEnd(end);
            index = index2;
        }
    }
    
    public static boolean checkNormalize(final MessageBytes uriMB) {
        final CharChunk uriCC = uriMB.getCharChunk();
        final char[] c = uriCC.getChars();
        final int start = uriCC.getStart();
        int end;
        int pos;
        for (end = uriCC.getEnd(), pos = 0, pos = start; pos < end; ++pos) {
            if (c[pos] == '\\') {
                return false;
            }
            if (c[pos] == '\0') {
                return false;
            }
        }
        for (pos = start; pos < end - 1; ++pos) {
            if (c[pos] == '/' && c[pos + 1] == '/') {
                return false;
            }
        }
        return (end - start < 2 || c[end - 1] != '.' || (c[end - 2] != '/' && (c[end - 2] != '.' || c[end - 3] != '/'))) && uriCC.indexOf("/./", 0, 3, 0) < 0 && uriCC.indexOf("/../", 0, 4, 0) < 0;
    }
    
    protected static void copyBytes(final byte[] b, final int dest, final int src, final int len) {
        System.arraycopy(b, src, b, dest, len);
    }
    
    static {
        log = LogFactory.getLog((Class)CoyoteAdapter.class);
        POWERED_BY = "Servlet/3.1 JSP/2.3 (" + ServerInfo.getServerInfo() + " Java/" + System.getProperty("java.vm.vendor") + "/" + System.getProperty("java.runtime.version") + ")";
        SSL_ONLY = EnumSet.of(SessionTrackingMode.SSL);
        ALLOW_BACKSLASH = Boolean.parseBoolean(System.getProperty("org.apache.catalina.connector.CoyoteAdapter.ALLOW_BACKSLASH", "false"));
        THREAD_NAME = new ThreadLocal<String>() {
            @Override
            protected String initialValue() {
                return Thread.currentThread().getName();
            }
        };
        sm = StringManager.getManager((Class)CoyoteAdapter.class);
    }
    
    private static class RecycleRequiredException extends Exception
    {
        private static final long serialVersionUID = 1L;
    }
}
