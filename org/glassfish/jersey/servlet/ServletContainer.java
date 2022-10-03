package org.glassfish.jersey.servlet;

import java.util.logging.Logger;
import org.glassfish.jersey.server.ApplicationHandler;
import org.glassfish.jersey.internal.inject.Providers;
import java.util.Iterator;
import org.glassfish.jersey.server.ContainerResponse;
import javax.servlet.ServletContext;
import javax.servlet.FilterChain;
import org.glassfish.jersey.servlet.spi.FilterUrlMappingsProvider;
import java.util.regex.PatternSyntaxException;
import org.glassfish.jersey.server.ContainerException;
import org.glassfish.jersey.servlet.internal.LocalizationMessages;
import org.glassfish.jersey.servlet.internal.ResponseWriter;
import org.glassfish.jersey.internal.util.collection.Value;
import javax.ws.rs.core.Response;
import java.util.logging.Level;
import java.net.URI;
import javax.ws.rs.core.UriBuilderException;
import org.glassfish.jersey.server.internal.ContainerUtils;
import javax.ws.rs.ProcessingException;
import org.glassfish.jersey.uri.UriComponent;
import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import javax.servlet.ServletException;
import org.glassfish.jersey.server.spi.ContainerLifecycleListener;
import java.util.List;
import java.util.regex.Pattern;
import org.glassfish.jersey.server.ResourceConfig;
import javax.servlet.FilterConfig;
import org.glassfish.jersey.internal.util.ExtendedLogger;
import org.glassfish.jersey.server.spi.Container;
import javax.servlet.Filter;
import javax.servlet.http.HttpServlet;

public class ServletContainer extends HttpServlet implements Filter, Container
{
    private static final long serialVersionUID = 3932047066686065219L;
    private static final ExtendedLogger LOGGER;
    private transient FilterConfig filterConfig;
    private transient WebComponent webComponent;
    private transient ResourceConfig resourceConfig;
    private transient Pattern staticContentPattern;
    private transient String filterContextPath;
    private transient List<String> filterUrlMappings;
    private transient volatile ContainerLifecycleListener containerListener;
    
    protected void init(final WebConfig webConfig) throws ServletException {
        this.webComponent = new WebComponent(webConfig, this.resourceConfig);
        (this.containerListener = (ContainerLifecycleListener)this.webComponent.appHandler).onStartup((Container)this);
    }
    
    public ServletContainer() {
    }
    
    public ServletContainer(final ResourceConfig resourceConfig) {
        this.resourceConfig = resourceConfig;
    }
    
    public void service(final ServletRequest req, final ServletResponse res) throws ServletException, IOException {
        if (!(req instanceof HttpServletRequest) || !(res instanceof HttpServletResponse)) {
            throw new ServletException("non-HTTP request or response");
        }
        final HttpServletRequest request = (HttpServletRequest)req;
        final HttpServletResponse response = (HttpServletResponse)res;
        this.service(request, response);
    }
    
    protected void service(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        final String servletPath = request.getServletPath();
        final StringBuffer requestUrl = request.getRequestURL();
        final String requestURI = request.getRequestURI();
        UriBuilder absoluteUriBuilder;
        try {
            absoluteUriBuilder = UriBuilder.fromUri(requestUrl.toString());
        }
        catch (final IllegalArgumentException iae) {
            this.setResponseForInvalidUri(response, iae);
            return;
        }
        final String decodedBasePath = request.getContextPath() + servletPath + "/";
        final String encodedBasePath = UriComponent.encode(decodedBasePath, UriComponent.Type.PATH);
        if (!decodedBasePath.equals(encodedBasePath)) {
            throw new ProcessingException("The servlet context path and/or the servlet path contain characters that are percent encoded");
        }
        URI baseUri;
        URI requestUri;
        try {
            baseUri = absoluteUriBuilder.replacePath(encodedBasePath).build(new Object[0]);
            String queryParameters = ContainerUtils.encodeUnsafeCharacters(request.getQueryString());
            if (queryParameters == null) {
                queryParameters = "";
            }
            requestUri = absoluteUriBuilder.replacePath(requestURI).replaceQuery(queryParameters).build(new Object[0]);
        }
        catch (final UriBuilderException | IllegalArgumentException ex) {
            this.setResponseForInvalidUri(response, ex);
            return;
        }
        this.service(baseUri, requestUri, request, response);
    }
    
    private void setResponseForInvalidUri(final HttpServletResponse response, final Throwable throwable) throws IOException {
        ServletContainer.LOGGER.log(Level.FINER, "Error while processing request.", throwable);
        final Response.Status badRequest = Response.Status.BAD_REQUEST;
        if (this.webComponent.configSetStatusOverSendError) {
            response.reset();
            response.setStatus(badRequest.getStatusCode(), badRequest.getReasonPhrase());
        }
        else {
            response.sendError(badRequest.getStatusCode(), badRequest.getReasonPhrase());
        }
    }
    
    public void destroy() {
        super.destroy();
        final ContainerLifecycleListener listener = this.containerListener;
        if (listener != null) {
            listener.onShutdown((Container)this);
        }
    }
    
    public void init() throws ServletException {
        this.init(new WebServletConfig(this));
    }
    
    public Value<Integer> service(final URI baseUri, final URI requestUri, final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        return this.webComponent.service(baseUri, requestUri, request, response);
    }
    
    private ResponseWriter serviceImpl(final URI baseUri, final URI requestUri, final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        return this.webComponent.serviceImpl(baseUri, requestUri, request, response);
    }
    
    public void init(final FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
        this.init(new WebFilterConfig(filterConfig));
        final String regex = (String)this.getConfiguration().getProperty("jersey.config.servlet.filter.staticContentRegex");
        if (regex != null && !regex.isEmpty()) {
            try {
                this.staticContentPattern = Pattern.compile(regex);
            }
            catch (final PatternSyntaxException ex) {
                throw new ContainerException(LocalizationMessages.INIT_PARAM_REGEX_SYNTAX_INVALID(regex, "jersey.config.servlet.filter.staticContentRegex"), (Throwable)ex);
            }
        }
        this.filterContextPath = filterConfig.getInitParameter("jersey.config.servlet.filter.contextPath");
        if (this.filterContextPath != null) {
            if (this.filterContextPath.isEmpty()) {
                this.filterContextPath = null;
            }
            else {
                if (!this.filterContextPath.startsWith("/")) {
                    this.filterContextPath = '/' + this.filterContextPath;
                }
                if (this.filterContextPath.endsWith("/")) {
                    this.filterContextPath = this.filterContextPath.substring(0, this.filterContextPath.length() - 1);
                }
            }
        }
        final FilterUrlMappingsProvider filterUrlMappingsProvider = this.getFilterUrlMappingsProvider();
        if (filterUrlMappingsProvider != null) {
            this.filterUrlMappings = filterUrlMappingsProvider.getFilterUrlMappings(filterConfig);
        }
        if (this.filterUrlMappings == null && this.filterContextPath == null) {
            ServletContainer.LOGGER.warning(LocalizationMessages.FILTER_CONTEXT_PATH_MISSING());
        }
    }
    
    public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse, final FilterChain filterChain) throws IOException, ServletException {
        try {
            this.doFilter((HttpServletRequest)servletRequest, (HttpServletResponse)servletResponse, filterChain);
        }
        catch (final ClassCastException e) {
            throw new ServletException("non-HTTP request or response", (Throwable)e);
        }
    }
    
    public ServletContext getServletContext() {
        if (this.filterConfig != null) {
            return this.filterConfig.getServletContext();
        }
        return super.getServletContext();
    }
    
    public void doFilter(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain) throws IOException, ServletException {
        if (request.getAttribute("javax.servlet.include.request_uri") != null) {
            final String includeRequestURI = (String)request.getAttribute("javax.servlet.include.request_uri");
            if (!includeRequestURI.equals(request.getRequestURI())) {
                this.doFilter(request, response, chain, includeRequestURI, (String)request.getAttribute("javax.servlet.include.servlet_path"), (String)request.getAttribute("javax.servlet.include.query_string"));
                return;
            }
        }
        final String servletPath = request.getServletPath() + ((request.getPathInfo() == null) ? "" : request.getPathInfo());
        this.doFilter(request, response, chain, request.getRequestURI(), servletPath, request.getQueryString());
    }
    
    private void doFilter(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain, final String requestURI, final String servletPath, final String queryString) throws IOException, ServletException {
        final Pattern p = this.getStaticContentPattern();
        if (p != null && p.matcher(servletPath).matches()) {
            chain.doFilter((ServletRequest)request, (ServletResponse)response);
            return;
        }
        if (this.filterContextPath != null && !servletPath.startsWith(this.filterContextPath)) {
            throw new ContainerException(LocalizationMessages.SERVLET_PATH_MISMATCH(servletPath, this.filterContextPath));
        }
        URI baseUri;
        URI requestUri;
        try {
            final UriBuilder absoluteUriBuilder = UriBuilder.fromUri(request.getRequestURL().toString());
            final String pickedUrlMapping = this.pickUrlMapping(request.getRequestURL().toString(), this.filterUrlMappings);
            final String replacingPath = (pickedUrlMapping != null) ? pickedUrlMapping : ((this.filterContextPath != null) ? this.filterContextPath : "");
            baseUri = absoluteUriBuilder.replacePath(request.getContextPath()).path(replacingPath).path("/").build(new Object[0]);
            requestUri = absoluteUriBuilder.replacePath(requestURI).replaceQuery(ContainerUtils.encodeUnsafeCharacters(queryString)).build(new Object[0]);
        }
        catch (final IllegalArgumentException iae) {
            this.setResponseForInvalidUri(response, iae);
            return;
        }
        final ResponseWriter responseWriter = this.serviceImpl(baseUri, requestUri, request, response);
        if (this.webComponent.forwardOn404 && !response.isCommitted()) {
            boolean hasEntity = false;
            Response.StatusType status = null;
            if (responseWriter.responseContextResolved()) {
                final ContainerResponse responseContext = responseWriter.getResponseContext();
                hasEntity = responseContext.hasEntity();
                status = responseContext.getStatusInfo();
            }
            if (!hasEntity && status == Response.Status.NOT_FOUND) {
                response.setStatus(200);
                chain.doFilter((ServletRequest)request, (ServletResponse)response);
            }
        }
    }
    
    private String pickUrlMapping(final String requestUri, final List<String> filterUrlMappings) {
        if (filterUrlMappings == null || filterUrlMappings.isEmpty()) {
            return null;
        }
        if (filterUrlMappings.size() == 1) {
            return filterUrlMappings.get(0);
        }
        for (final String pattern : filterUrlMappings) {
            if (requestUri.contains(pattern)) {
                return pattern;
            }
        }
        return null;
    }
    
    private FilterUrlMappingsProvider getFilterUrlMappingsProvider() {
        FilterUrlMappingsProvider filterUrlMappingsProvider = null;
        final Iterator<FilterUrlMappingsProvider> providers = Providers.getAllProviders(this.getApplicationHandler().getInjectionManager(), (Class)FilterUrlMappingsProvider.class).iterator();
        if (providers.hasNext()) {
            filterUrlMappingsProvider = providers.next();
        }
        return filterUrlMappingsProvider;
    }
    
    protected Pattern getStaticContentPattern() {
        return this.staticContentPattern;
    }
    
    public ResourceConfig getConfiguration() {
        return this.webComponent.appHandler.getConfiguration();
    }
    
    public void reload() {
        this.reload(this.getConfiguration());
    }
    
    public void reload(final ResourceConfig configuration) {
        try {
            this.containerListener.onShutdown((Container)this);
            this.webComponent = new WebComponent(this.webComponent.webConfig, configuration);
            (this.containerListener = (ContainerLifecycleListener)this.webComponent.appHandler).onReload((Container)this);
            this.containerListener.onStartup((Container)this);
        }
        catch (final ServletException ex) {
            ServletContainer.LOGGER.log(Level.SEVERE, "Reload failed", (Throwable)ex);
        }
    }
    
    public ApplicationHandler getApplicationHandler() {
        return this.webComponent.appHandler;
    }
    
    public WebComponent getWebComponent() {
        return this.webComponent;
    }
    
    static {
        LOGGER = new ExtendedLogger(Logger.getLogger(ServletContainer.class.getName()), Level.FINEST);
    }
}
