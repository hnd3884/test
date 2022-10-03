package org.apache.catalina.authenticator;

import javax.security.auth.message.config.ClientAuthConfig;
import org.apache.tomcat.util.http.FastHttpDateFormat;
import javax.security.auth.message.config.AuthConfigFactory;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Valve;
import javax.servlet.ServletContext;
import org.apache.catalina.util.StandardSessionIdGenerator;
import javax.security.auth.message.config.ServerAuthContext;
import javax.security.auth.message.MessageInfo;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.catalina.TomcatPrincipal;
import javax.servlet.http.Cookie;
import java.util.List;
import java.util.Set;
import org.apache.catalina.realm.GenericPrincipal;
import javax.security.auth.message.AuthStatus;
import org.apache.catalina.Contained;
import javax.security.auth.message.config.ServerAuthConfig;
import java.util.Map;
import org.apache.catalina.authenticator.jaspic.MessageInfoImpl;
import javax.security.auth.message.AuthException;
import javax.security.auth.Subject;
import org.apache.tomcat.util.descriptor.web.FilterMap;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.catalina.filters.CorsFilter;
import javax.servlet.DispatcherType;
import javax.servlet.http.HttpServletRequest;
import org.apache.tomcat.util.http.RequestUtil;
import javax.servlet.ServletException;
import java.io.IOException;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.apache.catalina.Realm;
import org.apache.catalina.Session;
import java.security.Principal;
import javax.servlet.http.HttpServletResponse;
import org.apache.catalina.connector.Response;
import org.apache.catalina.connector.Request;
import org.apache.catalina.Container;
import java.util.Locale;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.descriptor.web.LoginConfig;
import javax.security.auth.callback.CallbackHandler;
import org.apache.catalina.util.SessionIdGeneratorBase;
import org.apache.catalina.Context;
import org.apache.tomcat.util.res.StringManager;
import javax.security.auth.message.config.AuthConfigProvider;
import org.apache.juli.logging.Log;
import javax.security.auth.message.config.RegistrationListener;
import org.apache.catalina.Authenticator;
import org.apache.catalina.valves.ValveBase;

public abstract class AuthenticatorBase extends ValveBase implements Authenticator, RegistrationListener
{
    private final Log log;
    private static final String DATE_ONE;
    private static final AuthConfigProvider NO_PROVIDER_AVAILABLE;
    protected static final StringManager sm;
    protected static final String AUTH_HEADER_NAME = "WWW-Authenticate";
    protected static final String REALM_NAME = "Authentication required";
    protected boolean alwaysUseSession;
    protected boolean cache;
    protected boolean changeSessionIdOnAuthentication;
    protected Context context;
    protected boolean disableProxyCaching;
    protected boolean securePagesWithPragma;
    protected String secureRandomClass;
    protected String secureRandomAlgorithm;
    protected String secureRandomProvider;
    protected String jaspicCallbackHandlerClass;
    protected boolean sendAuthInfoResponseHeaders;
    protected SessionIdGeneratorBase sessionIdGenerator;
    protected SingleSignOn sso;
    private AllowCorsPreflight allowCorsPreflight;
    private volatile String jaspicAppContextID;
    private volatile AuthConfigProvider jaspicProvider;
    private volatile CallbackHandler jaspicCallbackHandler;
    
    protected static String getRealmName(final Context context) {
        if (context == null) {
            return "Authentication required";
        }
        final LoginConfig config = context.getLoginConfig();
        if (config == null) {
            return "Authentication required";
        }
        final String result = config.getRealmName();
        if (result == null) {
            return "Authentication required";
        }
        return result;
    }
    
    public AuthenticatorBase() {
        super(true);
        this.log = LogFactory.getLog((Class)AuthenticatorBase.class);
        this.alwaysUseSession = false;
        this.cache = true;
        this.changeSessionIdOnAuthentication = true;
        this.context = null;
        this.disableProxyCaching = true;
        this.securePagesWithPragma = false;
        this.secureRandomClass = null;
        this.secureRandomAlgorithm = "SHA1PRNG";
        this.secureRandomProvider = null;
        this.jaspicCallbackHandlerClass = "org.apache.catalina.authenticator.jaspic.CallbackHandlerImpl";
        this.sendAuthInfoResponseHeaders = false;
        this.sessionIdGenerator = null;
        this.sso = null;
        this.allowCorsPreflight = AllowCorsPreflight.NEVER;
        this.jaspicAppContextID = null;
        this.jaspicProvider = null;
        this.jaspicCallbackHandler = null;
    }
    
    public String getAllowCorsPreflight() {
        return this.allowCorsPreflight.name().toLowerCase(Locale.ENGLISH);
    }
    
    public void setAllowCorsPreflight(final String allowCorsPreflight) {
        this.allowCorsPreflight = AllowCorsPreflight.valueOf(allowCorsPreflight.trim().toUpperCase(Locale.ENGLISH));
    }
    
    public boolean getAlwaysUseSession() {
        return this.alwaysUseSession;
    }
    
    public void setAlwaysUseSession(final boolean alwaysUseSession) {
        this.alwaysUseSession = alwaysUseSession;
    }
    
    public boolean getCache() {
        return this.cache;
    }
    
    public void setCache(final boolean cache) {
        this.cache = cache;
    }
    
    @Override
    public Container getContainer() {
        return this.context;
    }
    
    @Override
    public void setContainer(final Container container) {
        if (container != null && !(container instanceof Context)) {
            throw new IllegalArgumentException(AuthenticatorBase.sm.getString("authenticator.notContext"));
        }
        super.setContainer(container);
        this.context = (Context)container;
    }
    
    public boolean getDisableProxyCaching() {
        return this.disableProxyCaching;
    }
    
    public void setDisableProxyCaching(final boolean nocache) {
        this.disableProxyCaching = nocache;
    }
    
    public boolean getSecurePagesWithPragma() {
        return this.securePagesWithPragma;
    }
    
    public void setSecurePagesWithPragma(final boolean securePagesWithPragma) {
        this.securePagesWithPragma = securePagesWithPragma;
    }
    
    public boolean getChangeSessionIdOnAuthentication() {
        return this.changeSessionIdOnAuthentication;
    }
    
    public void setChangeSessionIdOnAuthentication(final boolean changeSessionIdOnAuthentication) {
        this.changeSessionIdOnAuthentication = changeSessionIdOnAuthentication;
    }
    
    public String getSecureRandomClass() {
        return this.secureRandomClass;
    }
    
    public void setSecureRandomClass(final String secureRandomClass) {
        this.secureRandomClass = secureRandomClass;
    }
    
    public String getSecureRandomAlgorithm() {
        return this.secureRandomAlgorithm;
    }
    
    public void setSecureRandomAlgorithm(final String secureRandomAlgorithm) {
        this.secureRandomAlgorithm = secureRandomAlgorithm;
    }
    
    public String getSecureRandomProvider() {
        return this.secureRandomProvider;
    }
    
    public void setSecureRandomProvider(final String secureRandomProvider) {
        this.secureRandomProvider = secureRandomProvider;
    }
    
    public String getJaspicCallbackHandlerClass() {
        return this.jaspicCallbackHandlerClass;
    }
    
    public void setJaspicCallbackHandlerClass(final String jaspicCallbackHandlerClass) {
        this.jaspicCallbackHandlerClass = jaspicCallbackHandlerClass;
    }
    
    public boolean isSendAuthInfoResponseHeaders() {
        return this.sendAuthInfoResponseHeaders;
    }
    
    public void setSendAuthInfoResponseHeaders(final boolean sendAuthInfoResponseHeaders) {
        this.sendAuthInfoResponseHeaders = sendAuthInfoResponseHeaders;
    }
    
    public void invoke(final Request request, final Response response) throws IOException, ServletException {
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)("Security checking request " + request.getMethod() + " " + request.getRequestURI()));
        }
        if (this.cache) {
            Principal principal = request.getUserPrincipal();
            if (principal == null) {
                final Session session = request.getSessionInternal(false);
                if (session != null) {
                    principal = session.getPrincipal();
                    if (principal != null) {
                        if (this.log.isDebugEnabled()) {
                            this.log.debug((Object)("We have cached auth type " + session.getAuthType() + " for principal " + principal));
                        }
                        request.setAuthType(session.getAuthType());
                        request.setUserPrincipal(principal);
                    }
                }
            }
        }
        boolean authRequired = this.isContinuationRequired(request);
        final Realm realm = this.context.getRealm();
        final SecurityConstraint[] constraints = realm.findSecurityConstraints(request, this.context);
        final AuthConfigProvider jaspicProvider = this.getJaspicProvider();
        if (jaspicProvider != null) {
            authRequired = true;
        }
        if (constraints == null && !this.context.getPreemptiveAuthentication() && !authRequired) {
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)"Not subject to any constraint");
            }
            this.getNext().invoke(request, response);
            return;
        }
        if (constraints != null && this.disableProxyCaching && !"POST".equalsIgnoreCase(request.getMethod())) {
            if (this.securePagesWithPragma) {
                response.setHeader("Pragma", "No-cache");
                response.setHeader("Cache-Control", "no-cache");
                response.setHeader("Expires", AuthenticatorBase.DATE_ONE);
            }
            else {
                response.setHeader("Cache-Control", "private");
            }
        }
        if (constraints != null) {
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)"Calling hasUserDataPermission()");
            }
            if (!realm.hasUserDataPermission(request, response, constraints)) {
                if (this.log.isDebugEnabled()) {
                    this.log.debug((Object)"Failed hasUserDataPermission() test");
                }
                return;
            }
        }
        boolean hasAuthConstraint = false;
        if (constraints != null) {
            hasAuthConstraint = true;
            for (int i = 0; i < constraints.length && hasAuthConstraint; ++i) {
                if (!constraints[i].getAuthConstraint()) {
                    hasAuthConstraint = false;
                }
                else if (!constraints[i].getAllRoles() && !constraints[i].getAuthenticatedUsers()) {
                    final String[] roles = constraints[i].findAuthRoles();
                    if (roles == null || roles.length == 0) {
                        hasAuthConstraint = false;
                    }
                }
            }
        }
        if (!authRequired && hasAuthConstraint) {
            authRequired = true;
        }
        if (!authRequired && this.context.getPreemptiveAuthentication() && this.isPreemptiveAuthPossible(request)) {
            authRequired = true;
        }
        JaspicState jaspicState = null;
        if ((authRequired || constraints != null) && this.allowCorsPreflightBypass(request)) {
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)"CORS Preflight request bypassing authentication");
            }
            this.getNext().invoke(request, response);
            return;
        }
        if (authRequired) {
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)"Calling authenticate()");
            }
            if (jaspicProvider != null) {
                jaspicState = this.getJaspicState(jaspicProvider, request, response, hasAuthConstraint);
                if (jaspicState == null) {
                    return;
                }
            }
            if ((jaspicProvider == null && !this.doAuthenticate(request, (HttpServletResponse)response)) || (jaspicProvider != null && !this.authenticateJaspic(request, response, jaspicState, false))) {
                if (this.log.isDebugEnabled()) {
                    this.log.debug((Object)"Failed authenticate() test");
                }
                return;
            }
        }
        if (constraints != null) {
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)"Calling accessControl()");
            }
            if (!realm.hasResourcePermission(request, response, constraints, this.context)) {
                if (this.log.isDebugEnabled()) {
                    this.log.debug((Object)"Failed accessControl() test");
                }
                return;
            }
        }
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)"Successfully passed all security constraints");
        }
        this.getNext().invoke(request, response);
        if (jaspicProvider != null) {
            this.secureResponseJspic(request, response, jaspicState);
        }
    }
    
    protected boolean allowCorsPreflightBypass(final Request request) {
        boolean allowBypass = false;
        if (this.allowCorsPreflight != AllowCorsPreflight.NEVER && "OPTIONS".equals(request.getMethod())) {
            final String originHeader = request.getHeader("Origin");
            if (originHeader != null && !originHeader.isEmpty() && RequestUtil.isValidOrigin(originHeader) && !RequestUtil.isSameOrigin((HttpServletRequest)request, originHeader)) {
                final String accessControlRequestMethodHeader = request.getHeader("Access-Control-Request-Method");
                if (accessControlRequestMethodHeader != null && !accessControlRequestMethodHeader.isEmpty()) {
                    if (this.allowCorsPreflight == AllowCorsPreflight.ALWAYS) {
                        allowBypass = true;
                    }
                    else if (this.allowCorsPreflight == AllowCorsPreflight.FILTER && DispatcherType.REQUEST == request.getDispatcherType()) {
                        for (final FilterDef filterDef : request.getContext().findFilterDefs()) {
                            if (CorsFilter.class.getName().equals(filterDef.getFilterClass())) {
                                final FilterMap[] arr$2 = this.context.findFilterMaps();
                                final int len$2 = arr$2.length;
                                int i$2 = 0;
                                while (i$2 < len$2) {
                                    final FilterMap filterMap = arr$2[i$2];
                                    if (filterMap.getFilterName().equals(filterDef.getFilterName())) {
                                        if ((filterMap.getDispatcherMapping() & 0x8) > 0) {
                                            for (final String urlPattern : filterMap.getURLPatterns()) {
                                                if ("/*".equals(urlPattern)) {
                                                    allowBypass = true;
                                                    break;
                                                }
                                            }
                                            break;
                                        }
                                        break;
                                    }
                                    else {
                                        ++i$2;
                                    }
                                }
                                break;
                            }
                        }
                    }
                }
            }
        }
        return allowBypass;
    }
    
    @Override
    public boolean authenticate(final Request request, final HttpServletResponse httpResponse) throws IOException {
        final AuthConfigProvider jaspicProvider = this.getJaspicProvider();
        if (jaspicProvider == null) {
            return this.doAuthenticate(request, httpResponse);
        }
        final Response response = request.getResponse();
        final JaspicState jaspicState = this.getJaspicState(jaspicProvider, request, response, true);
        if (jaspicState == null) {
            return false;
        }
        final boolean result = this.authenticateJaspic(request, response, jaspicState, true);
        this.secureResponseJspic(request, response, jaspicState);
        return result;
    }
    
    private void secureResponseJspic(final Request request, final Response response, final JaspicState state) {
        try {
            state.serverAuthContext.secureResponse(state.messageInfo, (Subject)null);
            request.setRequest((HttpServletRequest)state.messageInfo.getRequestMessage());
            response.setResponse((HttpServletResponse)state.messageInfo.getResponseMessage());
        }
        catch (final AuthException e) {
            this.log.warn((Object)AuthenticatorBase.sm.getString("authenticator.jaspicSecureResponseFail"), (Throwable)e);
        }
    }
    
    private JaspicState getJaspicState(final AuthConfigProvider jaspicProvider, final Request request, final Response response, final boolean authMandatory) throws IOException {
        final JaspicState jaspicState = new JaspicState();
        jaspicState.messageInfo = (MessageInfo)new MessageInfoImpl(request.getRequest(), response.getResponse(), authMandatory);
        try {
            final CallbackHandler callbackHandler = this.getCallbackHandler();
            final ServerAuthConfig serverAuthConfig = jaspicProvider.getServerAuthConfig("HttpServlet", this.jaspicAppContextID, callbackHandler);
            final String authContextID = serverAuthConfig.getAuthContextID(jaspicState.messageInfo);
            jaspicState.serverAuthContext = serverAuthConfig.getAuthContext(authContextID, (Subject)null, (Map)null);
        }
        catch (final AuthException e) {
            this.log.warn((Object)AuthenticatorBase.sm.getString("authenticator.jaspicServerAuthContextFail"), (Throwable)e);
            response.sendError(500);
            return null;
        }
        return jaspicState;
    }
    
    private CallbackHandler getCallbackHandler() {
        CallbackHandler handler = this.jaspicCallbackHandler;
        if (handler == null) {
            handler = this.createCallbackHandler();
        }
        return handler;
    }
    
    private CallbackHandler createCallbackHandler() {
        CallbackHandler callbackHandler = null;
        Class<?> clazz = null;
        try {
            clazz = Class.forName(this.jaspicCallbackHandlerClass, true, Thread.currentThread().getContextClassLoader());
        }
        catch (final ClassNotFoundException ex) {}
        try {
            if (clazz == null) {
                clazz = Class.forName(this.jaspicCallbackHandlerClass);
            }
            callbackHandler = (CallbackHandler)clazz.getConstructor((Class<?>[])new Class[0]).newInstance(new Object[0]);
        }
        catch (final ReflectiveOperationException e) {
            throw new SecurityException(e);
        }
        if (callbackHandler instanceof Contained) {
            ((Contained)callbackHandler).setContainer(this.getContainer());
        }
        return this.jaspicCallbackHandler = callbackHandler;
    }
    
    protected abstract boolean doAuthenticate(final Request p0, final HttpServletResponse p1) throws IOException;
    
    protected boolean isContinuationRequired(final Request request) {
        return false;
    }
    
    protected void associate(final String ssoId, final Session session) {
        if (this.sso == null) {
            return;
        }
        this.sso.associate(ssoId, session);
    }
    
    private boolean authenticateJaspic(final Request request, final Response response, final JaspicState state, final boolean requirePrincipal) {
        final boolean cachedAuth = this.checkForCachedAuthentication(request, (HttpServletResponse)response, false);
        final Subject client = new Subject();
        AuthStatus authStatus;
        try {
            authStatus = state.serverAuthContext.validateRequest(state.messageInfo, client, (Subject)null);
        }
        catch (final AuthException e) {
            this.log.debug((Object)AuthenticatorBase.sm.getString("authenticator.loginFail"), (Throwable)e);
            return false;
        }
        request.setRequest((HttpServletRequest)state.messageInfo.getRequestMessage());
        response.setResponse((HttpServletResponse)state.messageInfo.getResponseMessage());
        if (authStatus == AuthStatus.SUCCESS) {
            final GenericPrincipal principal = this.getPrincipal(client);
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)("Authenticated user: " + principal));
            }
            if (principal == null) {
                request.setUserPrincipal(null);
                request.setAuthType(null);
                if (requirePrincipal) {
                    return false;
                }
            }
            else if (!cachedAuth || !principal.getUserPrincipal().equals(request.getUserPrincipal())) {
                Boolean register = null;
                String authType = "JASPIC";
                final Map map = state.messageInfo.getMap();
                final String registerValue = map.get("javax.servlet.http.registerSession");
                if (registerValue != null) {
                    register = Boolean.valueOf(registerValue);
                }
                final String authTypeValue = map.get("javax.servlet.http.authType");
                if (authTypeValue != null) {
                    authType = authTypeValue;
                }
                if (register != null) {
                    this.register(request, (HttpServletResponse)response, principal, authType, null, null, this.alwaysUseSession || register, register);
                }
                else {
                    this.register(request, (HttpServletResponse)response, principal, authType, null, null);
                }
            }
            request.setNote("org.apache.catalina.authenticator.jaspic.SUBJECT", client);
            return true;
        }
        return false;
    }
    
    private GenericPrincipal getPrincipal(final Subject subject) {
        if (subject == null) {
            return null;
        }
        final Set<GenericPrincipal> principals = subject.getPrivateCredentials(GenericPrincipal.class);
        if (principals.isEmpty()) {
            return null;
        }
        return principals.iterator().next();
    }
    
    protected boolean checkForCachedAuthentication(final Request request, final HttpServletResponse response, final boolean useSSO) {
        final Principal principal = request.getUserPrincipal();
        final String ssoId = (String)request.getNote("org.apache.catalina.request.SSOID");
        if (principal != null) {
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)AuthenticatorBase.sm.getString("authenticator.check.found", new Object[] { principal.getName() }));
            }
            if (ssoId != null) {
                this.associate(ssoId, request.getSessionInternal(true));
            }
            return true;
        }
        if (useSSO && ssoId != null) {
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)AuthenticatorBase.sm.getString("authenticator.check.sso", new Object[] { ssoId }));
            }
            if (this.reauthenticateFromSSO(ssoId, request)) {
                return true;
            }
        }
        if (request.getCoyoteRequest().getRemoteUserNeedsAuthorization()) {
            final String username = request.getCoyoteRequest().getRemoteUser().toString();
            if (username != null) {
                if (this.log.isDebugEnabled()) {
                    this.log.debug((Object)AuthenticatorBase.sm.getString("authenticator.check.authorize", new Object[] { username }));
                }
                Principal authorized = this.context.getRealm().authenticate(username);
                if (authorized == null) {
                    if (this.log.isDebugEnabled()) {
                        this.log.debug((Object)AuthenticatorBase.sm.getString("authenticator.check.authorizeFail", new Object[] { username }));
                    }
                    authorized = new GenericPrincipal(username, null, null);
                }
                String authType = request.getAuthType();
                if (authType == null || authType.length() == 0) {
                    authType = this.getAuthMethod();
                }
                this.register(request, response, authorized, authType, username, null);
                return true;
            }
        }
        return false;
    }
    
    protected boolean reauthenticateFromSSO(final String ssoId, final Request request) {
        if (this.sso == null || ssoId == null) {
            return false;
        }
        boolean reauthenticated = false;
        final Container parent = this.getContainer();
        if (parent != null) {
            final Realm realm = parent.getRealm();
            if (realm != null) {
                reauthenticated = this.sso.reauthenticate(ssoId, realm, request);
            }
        }
        if (reauthenticated) {
            this.associate(ssoId, request.getSessionInternal(true));
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)("Reauthenticated cached principal '" + request.getUserPrincipal().getName() + "' with auth type '" + request.getAuthType() + "'"));
            }
        }
        return reauthenticated;
    }
    
    public void register(final Request request, final HttpServletResponse response, final Principal principal, final String authType, final String username, final String password) {
        this.register(request, response, principal, authType, username, password, this.alwaysUseSession, this.cache);
    }
    
    protected void register(final Request request, final HttpServletResponse response, final Principal principal, final String authType, final String username, final String password, final boolean alwaysUseSession, final boolean cache) {
        if (this.log.isDebugEnabled()) {
            final String name = (principal == null) ? "none" : principal.getName();
            this.log.debug((Object)("Authenticated '" + name + "' with type '" + authType + "'"));
        }
        request.setAuthType(authType);
        request.setUserPrincipal(principal);
        if (this.sendAuthInfoResponseHeaders && Boolean.TRUE.equals(request.getAttribute("org.apache.tomcat.request.forwarded"))) {
            response.setHeader("remote-user", request.getRemoteUser());
            response.setHeader("auth-type", request.getAuthType());
        }
        Session session = request.getSessionInternal(false);
        if (session != null) {
            if (this.getChangeSessionIdOnAuthentication() && principal != null) {
                final String newSessionId = this.changeSessionID(request, session);
                if (session.getNote("org.apache.catalina.authenticator.SESSION_ID") != null) {
                    session.setNote("org.apache.catalina.authenticator.SESSION_ID", newSessionId);
                }
            }
        }
        else if (alwaysUseSession) {
            session = request.getSessionInternal(true);
        }
        if (session != null && cache) {
            session.setAuthType(authType);
            session.setPrincipal(principal);
        }
        if (this.sso == null) {
            return;
        }
        String ssoId = (String)request.getNote("org.apache.catalina.request.SSOID");
        if (ssoId == null) {
            ssoId = this.sessionIdGenerator.generateSessionId();
            final Cookie cookie = new Cookie(this.sso.getCookieName(), ssoId);
            cookie.setMaxAge(-1);
            cookie.setPath("/");
            cookie.setSecure(request.isSecure());
            final String ssoDomain = this.sso.getCookieDomain();
            if (ssoDomain != null) {
                cookie.setDomain(ssoDomain);
            }
            if (request.getServletContext().getSessionCookieConfig().isHttpOnly() || request.getContext().getUseHttpOnly()) {
                cookie.setHttpOnly(true);
            }
            response.addCookie(cookie);
            this.sso.register(ssoId, principal, authType, username, password);
            request.setNote("org.apache.catalina.request.SSOID", ssoId);
        }
        else {
            if (principal == null) {
                this.sso.deregister(ssoId);
                request.removeNote("org.apache.catalina.request.SSOID");
                return;
            }
            this.sso.update(ssoId, principal, authType, username, password);
        }
        if (session == null) {
            session = request.getSessionInternal(true);
        }
        this.sso.associate(ssoId, session);
    }
    
    protected String changeSessionID(final Request request, final Session session) {
        String oldId = null;
        if (this.log.isDebugEnabled()) {
            oldId = session.getId();
        }
        final String newId = request.changeSessionId();
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)AuthenticatorBase.sm.getString("authenticator.changeSessionId", new Object[] { oldId, newId }));
        }
        return newId;
    }
    
    @Override
    public void login(final String username, final String password, final Request request) throws ServletException {
        final Principal principal = this.doLogin(request, username, password);
        this.register(request, (HttpServletResponse)request.getResponse(), principal, this.getAuthMethod(), username, password);
    }
    
    protected abstract String getAuthMethod();
    
    protected Principal doLogin(final Request request, final String username, final String password) throws ServletException {
        final Principal p = this.context.getRealm().authenticate(username, password);
        if (p == null) {
            throw new ServletException(AuthenticatorBase.sm.getString("authenticator.loginFail"));
        }
        return p;
    }
    
    @Override
    public void logout(final Request request) {
        final AuthConfigProvider provider = this.getJaspicProvider();
        if (provider != null) {
            final MessageInfo messageInfo = (MessageInfo)new MessageInfoImpl((HttpServletRequest)request, (HttpServletResponse)request.getResponse(), true);
            final Subject client = (Subject)request.getNote("org.apache.catalina.authenticator.jaspic.SUBJECT");
            if (client != null) {
                try {
                    final ServerAuthConfig serverAuthConfig = provider.getServerAuthConfig("HttpServlet", this.jaspicAppContextID, this.getCallbackHandler());
                    final String authContextID = serverAuthConfig.getAuthContextID(messageInfo);
                    final ServerAuthContext serverAuthContext = serverAuthConfig.getAuthContext(authContextID, (Subject)null, (Map)null);
                    serverAuthContext.cleanSubject(messageInfo, client);
                }
                catch (final AuthException e) {
                    this.log.debug((Object)AuthenticatorBase.sm.getString("authenticator.jaspicCleanSubjectFail"), (Throwable)e);
                }
            }
        }
        final Principal p = request.getPrincipal();
        if (p instanceof TomcatPrincipal) {
            try {
                ((TomcatPrincipal)p).logout();
            }
            catch (final Throwable t) {
                ExceptionUtils.handleThrowable(t);
                this.log.debug((Object)AuthenticatorBase.sm.getString("authenticator.tomcatPrincipalLogoutFail"), t);
            }
        }
        this.register(request, (HttpServletResponse)request.getResponse(), null, null, null, null);
    }
    
    @Override
    protected synchronized void startInternal() throws LifecycleException {
        final ServletContext servletContext = this.context.getServletContext();
        this.jaspicAppContextID = servletContext.getVirtualServerName() + " " + servletContext.getContextPath();
        for (Container parent = this.context.getParent(); this.sso == null && parent != null; parent = parent.getParent()) {
            final Valve[] arr$;
            final Valve[] valves = arr$ = parent.getPipeline().getValves();
            for (final Valve valve : arr$) {
                if (valve instanceof SingleSignOn) {
                    this.sso = (SingleSignOn)valve;
                    break;
                }
            }
            if (this.sso == null) {}
        }
        if (this.log.isDebugEnabled()) {
            if (this.sso != null) {
                this.log.debug((Object)("Found SingleSignOn Valve at " + this.sso));
            }
            else {
                this.log.debug((Object)"No SingleSignOn Valve is present");
            }
        }
        (this.sessionIdGenerator = new StandardSessionIdGenerator()).setSecureRandomAlgorithm(this.getSecureRandomAlgorithm());
        this.sessionIdGenerator.setSecureRandomClass(this.getSecureRandomClass());
        this.sessionIdGenerator.setSecureRandomProvider(this.getSecureRandomProvider());
        super.startInternal();
    }
    
    @Override
    protected synchronized void stopInternal() throws LifecycleException {
        super.stopInternal();
        this.sso = null;
    }
    
    protected boolean isPreemptiveAuthPossible(final Request request) {
        return false;
    }
    
    private AuthConfigProvider getJaspicProvider() {
        AuthConfigProvider provider = this.jaspicProvider;
        if (provider == null) {
            provider = this.findJaspicProvider();
        }
        if (provider == AuthenticatorBase.NO_PROVIDER_AVAILABLE) {
            return null;
        }
        return provider;
    }
    
    private AuthConfigProvider findJaspicProvider() {
        final AuthConfigFactory factory = AuthConfigFactory.getFactory();
        AuthConfigProvider provider = null;
        if (factory != null) {
            provider = factory.getConfigProvider("HttpServlet", this.jaspicAppContextID, (RegistrationListener)this);
        }
        if (provider == null) {
            provider = AuthenticatorBase.NO_PROVIDER_AVAILABLE;
        }
        return this.jaspicProvider = provider;
    }
    
    public void notify(final String layer, final String appContext) {
        this.findJaspicProvider();
    }
    
    static {
        DATE_ONE = FastHttpDateFormat.formatDate(1L);
        NO_PROVIDER_AVAILABLE = (AuthConfigProvider)new NoOpAuthConfigProvider();
        sm = StringManager.getManager((Class)AuthenticatorBase.class);
    }
    
    private static class JaspicState
    {
        public MessageInfo messageInfo;
        public ServerAuthContext serverAuthContext;
        
        private JaspicState() {
            this.messageInfo = null;
            this.serverAuthContext = null;
        }
    }
    
    private static class NoOpAuthConfigProvider implements AuthConfigProvider
    {
        public ClientAuthConfig getClientAuthConfig(final String layer, final String appContext, final CallbackHandler handler) throws AuthException {
            return null;
        }
        
        public ServerAuthConfig getServerAuthConfig(final String layer, final String appContext, final CallbackHandler handler) throws AuthException {
            return null;
        }
        
        public void refresh() {
        }
    }
    
    protected enum AllowCorsPreflight
    {
        NEVER, 
        FILTER, 
        ALWAYS;
    }
}
