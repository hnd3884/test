package org.apache.tomcat.websocket.server;

import javax.naming.NamingException;
import java.util.Iterator;
import java.util.Collections;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import javax.websocket.Encoder;
import javax.websocket.server.ServerEndpoint;
import javax.websocket.Decoder;
import java.util.List;
import org.apache.tomcat.websocket.pojo.PojoMethodMapping;
import javax.websocket.DeploymentException;
import javax.websocket.server.ServerEndpointConfig;
import javax.servlet.FilterRegistration;
import java.util.EnumSet;
import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import org.apache.tomcat.InstanceManager;
import org.apache.tomcat.websocket.Constants;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.tomcat.websocket.WsSession;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentMap;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.websocket.CloseReason;
import org.apache.tomcat.util.res.StringManager;
import javax.websocket.server.ServerContainer;
import org.apache.tomcat.websocket.WsWebSocketContainer;

public class WsServerContainer extends WsWebSocketContainer implements ServerContainer
{
    private static final StringManager sm;
    private static final CloseReason AUTHENTICATED_HTTP_SESSION_CLOSED;
    private final WsWriteTimeout wsWriteTimeout;
    private final ServletContext servletContext;
    private final Map<String, ExactPathMatch> configExactMatchMap;
    private final ConcurrentMap<Integer, ConcurrentSkipListMap<String, TemplatePathMatch>> configTemplateMatchMap;
    private volatile boolean enforceNoAddAfterHandshake;
    private volatile boolean addAllowed;
    private final ConcurrentMap<String, Set<WsSession>> authenticatedSessions;
    private volatile boolean endpointsRegistered;
    private volatile boolean deploymentFailed;
    
    WsServerContainer(final ServletContext servletContext) {
        this.wsWriteTimeout = new WsWriteTimeout();
        this.configExactMatchMap = new ConcurrentHashMap<String, ExactPathMatch>();
        this.configTemplateMatchMap = new ConcurrentHashMap<Integer, ConcurrentSkipListMap<String, TemplatePathMatch>>();
        this.enforceNoAddAfterHandshake = Constants.STRICT_SPEC_COMPLIANCE;
        this.addAllowed = true;
        this.authenticatedSessions = new ConcurrentHashMap<String, Set<WsSession>>();
        this.endpointsRegistered = false;
        this.deploymentFailed = false;
        this.servletContext = servletContext;
        this.setInstanceManager((InstanceManager)servletContext.getAttribute(InstanceManager.class.getName()));
        String value = servletContext.getInitParameter("org.apache.tomcat.websocket.binaryBufferSize");
        if (value != null) {
            this.setDefaultMaxBinaryMessageBufferSize(Integer.parseInt(value));
        }
        value = servletContext.getInitParameter("org.apache.tomcat.websocket.textBufferSize");
        if (value != null) {
            this.setDefaultMaxTextMessageBufferSize(Integer.parseInt(value));
        }
        value = servletContext.getInitParameter("org.apache.tomcat.websocket.noAddAfterHandshake");
        if (value != null) {
            this.setEnforceNoAddAfterHandshake(Boolean.parseBoolean(value));
        }
        final FilterRegistration.Dynamic fr = servletContext.addFilter("Tomcat WebSocket (JSR356) Filter", (Filter)new WsFilter());
        fr.setAsyncSupported(true);
        final EnumSet<DispatcherType> types = EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD);
        fr.addMappingForUrlPatterns((EnumSet)types, true, new String[] { "/*" });
    }
    
    public void addEndpoint(final ServerEndpointConfig sec) throws DeploymentException {
        this.addEndpoint(sec, false);
    }
    
    void addEndpoint(final ServerEndpointConfig sec, final boolean fromAnnotatedPojo) throws DeploymentException {
        if (this.enforceNoAddAfterHandshake && !this.addAllowed) {
            throw new DeploymentException(WsServerContainer.sm.getString("serverContainer.addNotAllowed"));
        }
        if (this.servletContext == null) {
            throw new DeploymentException(WsServerContainer.sm.getString("serverContainer.servletContextMissing"));
        }
        if (this.deploymentFailed) {
            throw new DeploymentException(WsServerContainer.sm.getString("serverContainer.failedDeployment", new Object[] { this.servletContext.getContextPath(), this.servletContext.getVirtualServerName() }));
        }
        try {
            final String path = sec.getPath();
            final PojoMethodMapping methodMapping = new PojoMethodMapping(sec.getEndpointClass(), sec.getDecoders(), path, this.getInstanceManager(Thread.currentThread().getContextClassLoader()));
            if (methodMapping.getOnClose() != null || methodMapping.getOnOpen() != null || methodMapping.getOnError() != null || methodMapping.hasMessageHandlers()) {
                sec.getUserProperties().put("org.apache.tomcat.websocket.pojo.PojoEndpoint.methodMapping", methodMapping);
            }
            final UriTemplate uriTemplate = new UriTemplate(path);
            if (uriTemplate.hasParameters()) {
                final Integer key = uriTemplate.getSegmentCount();
                ConcurrentSkipListMap<String, TemplatePathMatch> templateMatches = this.configTemplateMatchMap.get(key);
                if (templateMatches == null) {
                    templateMatches = new ConcurrentSkipListMap<String, TemplatePathMatch>();
                    this.configTemplateMatchMap.putIfAbsent(key, templateMatches);
                    templateMatches = this.configTemplateMatchMap.get(key);
                }
                final TemplatePathMatch newMatch = new TemplatePathMatch(sec, uriTemplate, fromAnnotatedPojo);
                final TemplatePathMatch oldMatch = templateMatches.putIfAbsent(uriTemplate.getNormalizedPath(), newMatch);
                if (oldMatch != null) {
                    if (!oldMatch.isFromAnnotatedPojo() || newMatch.isFromAnnotatedPojo() || oldMatch.getConfig().getEndpointClass() != newMatch.getConfig().getEndpointClass()) {
                        throw new DeploymentException(WsServerContainer.sm.getString("serverContainer.duplicatePaths", new Object[] { path, sec.getEndpointClass(), sec.getEndpointClass() }));
                    }
                    templateMatches.put(path, oldMatch);
                }
            }
            else {
                final ExactPathMatch newMatch2 = new ExactPathMatch(sec, fromAnnotatedPojo);
                final ExactPathMatch oldMatch2 = this.configExactMatchMap.put(path, newMatch2);
                if (oldMatch2 != null) {
                    if (!oldMatch2.isFromAnnotatedPojo() || newMatch2.isFromAnnotatedPojo() || oldMatch2.getConfig().getEndpointClass() != newMatch2.getConfig().getEndpointClass()) {
                        throw new DeploymentException(WsServerContainer.sm.getString("serverContainer.duplicatePaths", new Object[] { path, oldMatch2.getConfig().getEndpointClass(), sec.getEndpointClass() }));
                    }
                    this.configExactMatchMap.put(path, oldMatch2);
                }
            }
            this.endpointsRegistered = true;
        }
        catch (final DeploymentException de) {
            this.failDeployment();
            throw de;
        }
    }
    
    public void addEndpoint(final Class<?> pojo) throws DeploymentException {
        this.addEndpoint(pojo, false);
    }
    
    void addEndpoint(final Class<?> pojo, final boolean fromAnnotatedPojo) throws DeploymentException {
        if (this.deploymentFailed) {
            throw new DeploymentException(WsServerContainer.sm.getString("serverContainer.failedDeployment", new Object[] { this.servletContext.getContextPath(), this.servletContext.getVirtualServerName() }));
        }
        ServerEndpointConfig sec;
        try {
            final ServerEndpoint annotation = pojo.getAnnotation(ServerEndpoint.class);
            if (annotation == null) {
                throw new DeploymentException(WsServerContainer.sm.getString("serverContainer.missingAnnotation", new Object[] { pojo.getName() }));
            }
            final String path = annotation.value();
            validateEncoders(annotation.encoders(), this.getInstanceManager(Thread.currentThread().getContextClassLoader()));
            final Class<? extends ServerEndpointConfig.Configurator> configuratorClazz = annotation.configurator();
            ServerEndpointConfig.Configurator configurator = null;
            if (!configuratorClazz.equals(ServerEndpointConfig.Configurator.class)) {
                try {
                    configurator = annotation.configurator().getConstructor((Class<?>[])new Class[0]).newInstance(new Object[0]);
                }
                catch (final ReflectiveOperationException e) {
                    throw new DeploymentException(WsServerContainer.sm.getString("serverContainer.configuratorFail", new Object[] { annotation.configurator().getName(), pojo.getClass().getName() }), (Throwable)e);
                }
            }
            sec = ServerEndpointConfig.Builder.create((Class)pojo, path).decoders((List)Arrays.asList((Class[])annotation.decoders())).encoders((List)Arrays.asList((Class[])annotation.encoders())).subprotocols((List)Arrays.asList(annotation.subprotocols())).configurator(configurator).build();
        }
        catch (final DeploymentException de) {
            this.failDeployment();
            throw de;
        }
        this.addEndpoint(sec, fromAnnotatedPojo);
    }
    
    void failDeployment() {
        this.deploymentFailed = true;
        this.endpointsRegistered = false;
        this.configExactMatchMap.clear();
        this.configTemplateMatchMap.clear();
    }
    
    boolean areEndpointsRegistered() {
        return this.endpointsRegistered;
    }
    
    public void doUpgrade(final HttpServletRequest request, final HttpServletResponse response, final ServerEndpointConfig sec, final Map<String, String> pathParams) throws ServletException, IOException {
        UpgradeUtil.doUpgrade(this, request, response, sec, pathParams);
    }
    
    public WsMappingResult findMapping(final String path) {
        if (this.addAllowed) {
            this.addAllowed = false;
        }
        final ExactPathMatch match = this.configExactMatchMap.get(path);
        if (match != null) {
            return new WsMappingResult(match.getConfig(), Collections.emptyMap());
        }
        UriTemplate pathUriTemplate = null;
        try {
            pathUriTemplate = new UriTemplate(path);
        }
        catch (final DeploymentException e) {
            return null;
        }
        final Integer key = pathUriTemplate.getSegmentCount();
        final ConcurrentSkipListMap<String, TemplatePathMatch> templateMatches = this.configTemplateMatchMap.get(key);
        if (templateMatches == null) {
            return null;
        }
        ServerEndpointConfig sec = null;
        Map<String, String> pathParams = null;
        for (final TemplatePathMatch templateMatch : templateMatches.values()) {
            pathParams = templateMatch.getUriTemplate().match(pathUriTemplate);
            if (pathParams != null) {
                sec = templateMatch.getConfig();
                break;
            }
        }
        if (sec == null) {
            return null;
        }
        return new WsMappingResult(sec, pathParams);
    }
    
    public boolean isEnforceNoAddAfterHandshake() {
        return this.enforceNoAddAfterHandshake;
    }
    
    public void setEnforceNoAddAfterHandshake(final boolean enforceNoAddAfterHandshake) {
        this.enforceNoAddAfterHandshake = enforceNoAddAfterHandshake;
    }
    
    protected WsWriteTimeout getTimeout() {
        return this.wsWriteTimeout;
    }
    
    @Override
    protected void registerSession(final Object key, final WsSession wsSession) {
        super.registerSession(key, wsSession);
        if (wsSession.isOpen() && wsSession.getUserPrincipal() != null && wsSession.getHttpSessionId() != null) {
            this.registerAuthenticatedSession(wsSession, wsSession.getHttpSessionId());
        }
    }
    
    @Override
    protected void unregisterSession(final Object key, final WsSession wsSession) {
        if (wsSession.getUserPrincipal() != null && wsSession.getHttpSessionId() != null) {
            this.unregisterAuthenticatedSession(wsSession, wsSession.getHttpSessionId());
        }
        super.unregisterSession(key, wsSession);
    }
    
    private void registerAuthenticatedSession(final WsSession wsSession, final String httpSessionId) {
        Set<WsSession> wsSessions = this.authenticatedSessions.get(httpSessionId);
        if (wsSessions == null) {
            wsSessions = Collections.newSetFromMap(new ConcurrentHashMap<WsSession, Boolean>());
            this.authenticatedSessions.putIfAbsent(httpSessionId, wsSessions);
            wsSessions = this.authenticatedSessions.get(httpSessionId);
        }
        wsSessions.add(wsSession);
    }
    
    private void unregisterAuthenticatedSession(final WsSession wsSession, final String httpSessionId) {
        final Set<WsSession> wsSessions = this.authenticatedSessions.get(httpSessionId);
        if (wsSessions != null) {
            wsSessions.remove(wsSession);
        }
    }
    
    public void closeAuthenticatedSession(final String httpSessionId) {
        final Set<WsSession> wsSessions = this.authenticatedSessions.remove(httpSessionId);
        if (wsSessions != null && !wsSessions.isEmpty()) {
            for (final WsSession wsSession : wsSessions) {
                try {
                    wsSession.close(WsServerContainer.AUTHENTICATED_HTTP_SESSION_CLOSED);
                }
                catch (final IOException ex) {}
            }
        }
    }
    
    private static void validateEncoders(final Class<? extends Encoder>[] encoders, final InstanceManager instanceManager) throws DeploymentException {
        for (final Class<? extends Encoder> encoder : encoders) {
            try {
                if (instanceManager == null) {
                    final Encoder instance = (Encoder)encoder.getConstructor((Class<?>[])new Class[0]).newInstance(new Object[0]);
                }
                else {
                    final Encoder instance = (Encoder)instanceManager.newInstance((Class)encoder);
                    instanceManager.destroyInstance((Object)instance);
                }
            }
            catch (final ReflectiveOperationException | NamingException e) {
                throw new DeploymentException(WsServerContainer.sm.getString("serverContainer.encoderFail", new Object[] { encoder.getName() }), (Throwable)e);
            }
        }
    }
    
    static {
        sm = StringManager.getManager((Class)WsServerContainer.class);
        AUTHENTICATED_HTTP_SESSION_CLOSED = new CloseReason((CloseReason.CloseCode)CloseReason.CloseCodes.VIOLATED_POLICY, "This connection was established under an authenticated HTTP session that has ended.");
    }
    
    private static class TemplatePathMatch
    {
        private final ServerEndpointConfig config;
        private final UriTemplate uriTemplate;
        private final boolean fromAnnotatedPojo;
        
        public TemplatePathMatch(final ServerEndpointConfig config, final UriTemplate uriTemplate, final boolean fromAnnotatedPojo) {
            this.config = config;
            this.uriTemplate = uriTemplate;
            this.fromAnnotatedPojo = fromAnnotatedPojo;
        }
        
        public ServerEndpointConfig getConfig() {
            return this.config;
        }
        
        public UriTemplate getUriTemplate() {
            return this.uriTemplate;
        }
        
        public boolean isFromAnnotatedPojo() {
            return this.fromAnnotatedPojo;
        }
    }
    
    private static class ExactPathMatch
    {
        private final ServerEndpointConfig config;
        private final boolean fromAnnotatedPojo;
        
        public ExactPathMatch(final ServerEndpointConfig config, final boolean fromAnnotatedPojo) {
            this.config = config;
            this.fromAnnotatedPojo = fromAnnotatedPojo;
        }
        
        public ServerEndpointConfig getConfig() {
            return this.config;
        }
        
        public boolean isFromAnnotatedPojo() {
            return this.fromAnnotatedPojo;
        }
    }
}
