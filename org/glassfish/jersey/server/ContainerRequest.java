package org.glassfish.jersey.server;

import org.glassfish.jersey.server.internal.LocalizationMessages;
import org.glassfish.jersey.message.internal.AcceptableMediaType;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.MultivaluedMap;
import java.text.ParseException;
import org.glassfish.jersey.message.internal.HttpHeaderReader;
import org.glassfish.jersey.message.internal.MatchingEntityTag;
import java.util.Set;
import java.util.Date;
import javax.ws.rs.core.EntityTag;
import org.glassfish.jersey.internal.util.collection.Ref;
import org.glassfish.jersey.message.internal.VariantSelector;
import org.glassfish.jersey.internal.util.collection.Refs;
import javax.ws.rs.core.Variant;
import java.util.function.Function;
import org.glassfish.jersey.message.internal.LanguageTag;
import java.util.Locale;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import javax.ws.rs.core.MediaType;
import java.util.List;
import javax.ws.rs.core.Cookie;
import java.util.Map;
import java.io.InputStream;
import org.glassfish.jersey.internal.guava.Preconditions;
import org.glassfish.jersey.uri.UriComponent;
import org.glassfish.jersey.message.internal.OutboundJaxrsResponse;
import org.glassfish.jersey.uri.internal.JerseyUriBuilder;
import java.util.Collections;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.ReaderInterceptor;
import javax.ws.rs.container.ContainerResponseFilter;
import org.glassfish.jersey.server.internal.process.RequestProcessingContext;
import org.glassfish.jersey.process.Inflector;
import org.glassfish.jersey.server.model.ResourceMethodInvoker;
import javax.ws.rs.container.ContainerRequestFilter;
import org.glassfish.jersey.model.internal.RankedProvider;
import java.util.Collection;
import java.lang.reflect.Type;
import java.lang.annotation.Annotation;
import org.glassfish.jersey.message.internal.TracingAwarePropertiesDelegate;
import org.glassfish.jersey.server.spi.ContainerResponseWriter;
import org.glassfish.jersey.server.spi.RequestScopedInitializer;
import org.glassfish.jersey.server.internal.ProcessingProviders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import org.glassfish.jersey.server.internal.routing.UriRoutingContext;
import java.net.URI;
import org.glassfish.jersey.internal.PropertiesDelegate;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Request;
import javax.ws.rs.container.ContainerRequestContext;
import org.glassfish.jersey.message.internal.InboundMessageContext;

public class ContainerRequest extends InboundMessageContext implements ContainerRequestContext, Request, HttpHeaders, PropertiesDelegate
{
    private static final URI DEFAULT_BASE_URI;
    private final PropertiesDelegate propertiesDelegate;
    private final UriRoutingContext uriRoutingContext;
    private URI baseUri;
    private URI requestUri;
    private String encodedRelativePath;
    private String decodedRelativePath;
    private URI absolutePathUri;
    private String httpMethod;
    private SecurityContext securityContext;
    private Response abortResponse;
    private String varyValue;
    private ProcessingProviders processingProviders;
    private RequestScopedInitializer requestScopedInitializer;
    private ContainerResponseWriter responseWriter;
    private boolean inResponseProcessingPhase;
    private static final String ERROR_REQUEST_SET_ENTITY_STREAM_IN_RESPONSE_PHASE;
    private static final String ERROR_REQUEST_SET_SECURITY_CONTEXT_IN_RESPONSE_PHASE;
    private static final String ERROR_REQUEST_ABORT_IN_RESPONSE_PHASE;
    private static final String METHOD_PARAMETER_CANNOT_BE_NULL_OR_EMPTY;
    private static final String METHOD_PARAMETER_CANNOT_BE_NULL_ETAG;
    private static final String METHOD_PARAMETER_CANNOT_BE_NULL_LAST_MODIFIED;
    
    public ContainerRequest(final URI baseUri, final URI requestUri, final String httpMethod, final SecurityContext securityContext, final PropertiesDelegate propertiesDelegate) {
        super(true);
        this.encodedRelativePath = null;
        this.decodedRelativePath = null;
        this.absolutePathUri = null;
        this.baseUri = ((baseUri == null) ? ContainerRequest.DEFAULT_BASE_URI : baseUri.normalize());
        this.requestUri = requestUri;
        this.httpMethod = httpMethod;
        this.securityContext = securityContext;
        this.propertiesDelegate = (PropertiesDelegate)new TracingAwarePropertiesDelegate(propertiesDelegate);
        this.uriRoutingContext = new UriRoutingContext(this);
    }
    
    public RequestScopedInitializer getRequestScopedInitializer() {
        return this.requestScopedInitializer;
    }
    
    public void setRequestScopedInitializer(final RequestScopedInitializer requestScopedInitializer) {
        this.requestScopedInitializer = requestScopedInitializer;
    }
    
    public ContainerResponseWriter getResponseWriter() {
        return this.responseWriter;
    }
    
    public void setWriter(final ContainerResponseWriter responseWriter) {
        this.responseWriter = responseWriter;
    }
    
    public <T> T readEntity(final Class<T> rawType) {
        return (T)this.readEntity((Class)rawType, this.propertiesDelegate);
    }
    
    public <T> T readEntity(final Class<T> rawType, final Annotation[] annotations) {
        return (T)super.readEntity((Class)rawType, annotations, this.propertiesDelegate);
    }
    
    public <T> T readEntity(final Class<T> rawType, final Type type) {
        return (T)super.readEntity((Class)rawType, type, this.propertiesDelegate);
    }
    
    public <T> T readEntity(final Class<T> rawType, final Type type, final Annotation[] annotations) {
        return (T)super.readEntity((Class)rawType, type, annotations, this.propertiesDelegate);
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
    
    public PropertiesDelegate getPropertiesDelegate() {
        return this.propertiesDelegate;
    }
    
    public ExtendedUriInfo getUriInfo() {
        return this.uriRoutingContext;
    }
    
    void setProcessingProviders(final ProcessingProviders providers) {
        this.processingProviders = providers;
    }
    
    UriRoutingContext getUriRoutingContext() {
        return this.uriRoutingContext;
    }
    
    Iterable<RankedProvider<ContainerRequestFilter>> getRequestFilters() {
        final Inflector<RequestProcessingContext, ContainerResponse> inflector = this.getInflector();
        return emptyIfNull((Iterable<RankedProvider<ContainerRequestFilter>>)((inflector instanceof ResourceMethodInvoker) ? ((ResourceMethodInvoker)inflector).getRequestFilters() : null));
    }
    
    Iterable<RankedProvider<ContainerResponseFilter>> getResponseFilters() {
        final Inflector<RequestProcessingContext, ContainerResponse> inflector = this.getInflector();
        return emptyIfNull((Iterable<RankedProvider<ContainerResponseFilter>>)((inflector instanceof ResourceMethodInvoker) ? ((ResourceMethodInvoker)inflector).getResponseFilters() : null));
    }
    
    protected Iterable<ReaderInterceptor> getReaderInterceptors() {
        final Inflector<RequestProcessingContext, ContainerResponse> inflector = this.getInflector();
        return (inflector instanceof ResourceMethodInvoker) ? ((ResourceMethodInvoker)inflector).getReaderInterceptors() : this.processingProviders.getSortedGlobalReaderInterceptors();
    }
    
    Iterable<WriterInterceptor> getWriterInterceptors() {
        final Inflector<RequestProcessingContext, ContainerResponse> inflector = this.getInflector();
        return (inflector instanceof ResourceMethodInvoker) ? ((ResourceMethodInvoker)inflector).getWriterInterceptors() : this.processingProviders.getSortedGlobalWriterInterceptors();
    }
    
    private Inflector<RequestProcessingContext, ContainerResponse> getInflector() {
        return (Inflector<RequestProcessingContext, ContainerResponse>)this.uriRoutingContext.getEndpoint();
    }
    
    private static <T> Iterable<T> emptyIfNull(final Iterable<T> iterable) {
        return (Iterable<T>)((iterable == null) ? Collections.emptyList() : iterable);
    }
    
    public URI getBaseUri() {
        return this.baseUri;
    }
    
    public URI getRequestUri() {
        return this.requestUri;
    }
    
    public URI getAbsolutePath() {
        if (this.absolutePathUri != null) {
            return this.absolutePathUri;
        }
        return this.absolutePathUri = new JerseyUriBuilder().uri(this.requestUri).replaceQuery("").fragment("").build(new Object[0]);
    }
    
    public void setRequestUri(final URI requestUri) throws IllegalStateException {
        if (!this.uriRoutingContext.getMatchedURIs().isEmpty()) {
            throw new IllegalStateException("Method could be called only in pre-matching request filter.");
        }
        this.encodedRelativePath = null;
        this.decodedRelativePath = null;
        this.absolutePathUri = null;
        this.uriRoutingContext.invalidateUriComponentViews();
        this.requestUri = requestUri;
    }
    
    public void setRequestUri(final URI baseUri, final URI requestUri) throws IllegalStateException {
        if (!this.uriRoutingContext.getMatchedURIs().isEmpty()) {
            throw new IllegalStateException("Method could be called only in pre-matching request filter.");
        }
        this.encodedRelativePath = null;
        this.decodedRelativePath = null;
        this.absolutePathUri = null;
        this.uriRoutingContext.invalidateUriComponentViews();
        this.baseUri = baseUri;
        this.requestUri = requestUri;
        OutboundJaxrsResponse.Builder.setBaseUri(baseUri);
    }
    
    public String getPath(final boolean decode) {
        if (!decode) {
            return this.encodedRelativePath();
        }
        if (this.decodedRelativePath != null) {
            return this.decodedRelativePath;
        }
        return this.decodedRelativePath = UriComponent.decode(this.encodedRelativePath(), UriComponent.Type.PATH);
    }
    
    private String encodedRelativePath() {
        if (this.encodedRelativePath != null) {
            return this.encodedRelativePath;
        }
        final String requestUriRawPath = this.requestUri.getRawPath();
        if (this.baseUri == null) {
            return this.encodedRelativePath = requestUriRawPath;
        }
        final int baseUriRawPathLength = this.baseUri.getRawPath().length();
        return this.encodedRelativePath = ((baseUriRawPathLength < requestUriRawPath.length()) ? requestUriRawPath.substring(baseUriRawPathLength) : "");
    }
    
    public String getMethod() {
        return this.httpMethod;
    }
    
    public void setMethod(final String method) throws IllegalStateException {
        if (!this.uriRoutingContext.getMatchedURIs().isEmpty()) {
            throw new IllegalStateException("Method could be called only in pre-matching request filter.");
        }
        this.httpMethod = method;
    }
    
    public void setMethodWithoutException(final String method) {
        this.httpMethod = method;
    }
    
    public SecurityContext getSecurityContext() {
        return this.securityContext;
    }
    
    public void setSecurityContext(final SecurityContext context) {
        Preconditions.checkState(!this.inResponseProcessingPhase, (Object)ContainerRequest.ERROR_REQUEST_SET_SECURITY_CONTEXT_IN_RESPONSE_PHASE);
        this.securityContext = context;
    }
    
    public void setEntityStream(final InputStream input) {
        Preconditions.checkState(!this.inResponseProcessingPhase, (Object)ContainerRequest.ERROR_REQUEST_SET_ENTITY_STREAM_IN_RESPONSE_PHASE);
        super.setEntityStream(input);
    }
    
    public Request getRequest() {
        return (Request)this;
    }
    
    public void abortWith(final Response response) {
        Preconditions.checkState(!this.inResponseProcessingPhase, (Object)ContainerRequest.ERROR_REQUEST_ABORT_IN_RESPONSE_PHASE);
        this.abortResponse = response;
    }
    
    public void inResponseProcessing() {
        this.inResponseProcessingPhase = true;
    }
    
    public Response getAbortResponse() {
        return this.abortResponse;
    }
    
    public Map<String, Cookie> getCookies() {
        return super.getRequestCookies();
    }
    
    public List<MediaType> getAcceptableMediaTypes() {
        return (List)this.getQualifiedAcceptableMediaTypes().stream().map(input -> input).collect(Collectors.toList());
    }
    
    public List<Locale> getAcceptableLanguages() {
        return (List)this.getQualifiedAcceptableLanguages().stream().map(LanguageTag::getAsLocale).collect(Collectors.toList());
    }
    
    public Variant selectVariant(final List<Variant> variants) throws IllegalArgumentException {
        if (variants == null || variants.isEmpty()) {
            throw new IllegalArgumentException(ContainerRequest.METHOD_PARAMETER_CANNOT_BE_NULL_OR_EMPTY);
        }
        final Ref<String> varyValueRef = (Ref<String>)Refs.emptyRef();
        final Variant variant = VariantSelector.selectVariant((InboundMessageContext)this, (List)variants, (Ref)varyValueRef);
        this.varyValue = (String)varyValueRef.get();
        return variant;
    }
    
    public String getVaryValue() {
        return this.varyValue;
    }
    
    public Response.ResponseBuilder evaluatePreconditions(final EntityTag eTag) {
        if (eTag == null) {
            throw new IllegalArgumentException(ContainerRequest.METHOD_PARAMETER_CANNOT_BE_NULL_ETAG);
        }
        final Response.ResponseBuilder r = this.evaluateIfMatch(eTag);
        if (r != null) {
            return r;
        }
        return this.evaluateIfNoneMatch(eTag);
    }
    
    public Response.ResponseBuilder evaluatePreconditions(final Date lastModified) {
        if (lastModified == null) {
            throw new IllegalArgumentException(ContainerRequest.METHOD_PARAMETER_CANNOT_BE_NULL_LAST_MODIFIED);
        }
        final long lastModifiedTime = lastModified.getTime();
        final Response.ResponseBuilder r = this.evaluateIfUnmodifiedSince(lastModifiedTime);
        if (r != null) {
            return r;
        }
        return this.evaluateIfModifiedSince(lastModifiedTime);
    }
    
    public Response.ResponseBuilder evaluatePreconditions(final Date lastModified, final EntityTag eTag) {
        if (lastModified == null) {
            throw new IllegalArgumentException(ContainerRequest.METHOD_PARAMETER_CANNOT_BE_NULL_LAST_MODIFIED);
        }
        if (eTag == null) {
            throw new IllegalArgumentException(ContainerRequest.METHOD_PARAMETER_CANNOT_BE_NULL_ETAG);
        }
        Response.ResponseBuilder r = this.evaluateIfMatch(eTag);
        if (r != null) {
            return r;
        }
        final long lastModifiedTime = lastModified.getTime();
        r = this.evaluateIfUnmodifiedSince(lastModifiedTime);
        if (r != null) {
            return r;
        }
        final boolean isGetOrHead = "GET".equals(this.getMethod()) || "HEAD".equals(this.getMethod());
        final Set<MatchingEntityTag> matchingTags = this.getIfNoneMatch();
        if (matchingTags != null) {
            r = this.evaluateIfNoneMatch(eTag, (Set<? extends EntityTag>)matchingTags, isGetOrHead);
            if (r == null) {
                return null;
            }
        }
        final String ifModifiedSinceHeader = this.getHeaderString("If-Modified-Since");
        if (ifModifiedSinceHeader != null && !ifModifiedSinceHeader.isEmpty() && isGetOrHead) {
            r = this.evaluateIfModifiedSince(lastModifiedTime, ifModifiedSinceHeader);
            if (r != null) {
                r.tag(eTag);
            }
        }
        return r;
    }
    
    public Response.ResponseBuilder evaluatePreconditions() {
        final Set<MatchingEntityTag> matchingTags = this.getIfMatch();
        if (matchingTags == null) {
            return null;
        }
        return Response.status(Response.Status.PRECONDITION_FAILED);
    }
    
    private Response.ResponseBuilder evaluateIfMatch(final EntityTag eTag) {
        final Set<? extends EntityTag> matchingTags = this.getIfMatch();
        if (matchingTags == null) {
            return null;
        }
        if (eTag.isWeak()) {
            return Response.status(Response.Status.PRECONDITION_FAILED);
        }
        if (matchingTags != MatchingEntityTag.ANY_MATCH && !matchingTags.contains(eTag)) {
            return Response.status(Response.Status.PRECONDITION_FAILED);
        }
        return null;
    }
    
    private Response.ResponseBuilder evaluateIfNoneMatch(final EntityTag eTag) {
        final Set<MatchingEntityTag> matchingTags = this.getIfNoneMatch();
        if (matchingTags == null) {
            return null;
        }
        final String httpMethod = this.getMethod();
        return this.evaluateIfNoneMatch(eTag, (Set<? extends EntityTag>)matchingTags, "GET".equals(httpMethod) || "HEAD".equals(httpMethod));
    }
    
    private Response.ResponseBuilder evaluateIfNoneMatch(final EntityTag eTag, final Set<? extends EntityTag> matchingTags, final boolean isGetOrHead) {
        if (isGetOrHead) {
            if (matchingTags == MatchingEntityTag.ANY_MATCH) {
                return Response.notModified(eTag);
            }
            if (matchingTags.contains(eTag) || matchingTags.contains(new EntityTag(eTag.getValue(), !eTag.isWeak()))) {
                return Response.notModified(eTag);
            }
        }
        else {
            if (eTag.isWeak()) {
                return null;
            }
            if (matchingTags == MatchingEntityTag.ANY_MATCH || matchingTags.contains(eTag)) {
                return Response.status(Response.Status.PRECONDITION_FAILED);
            }
        }
        return null;
    }
    
    private Response.ResponseBuilder evaluateIfUnmodifiedSince(final long lastModified) {
        final String ifUnmodifiedSinceHeader = this.getHeaderString("If-Unmodified-Since");
        if (ifUnmodifiedSinceHeader != null && !ifUnmodifiedSinceHeader.isEmpty()) {
            try {
                final long ifUnmodifiedSince = HttpHeaderReader.readDate(ifUnmodifiedSinceHeader).getTime();
                if (roundDown(lastModified) > ifUnmodifiedSince) {
                    return Response.status(Response.Status.PRECONDITION_FAILED);
                }
            }
            catch (final ParseException ex) {}
        }
        return null;
    }
    
    private Response.ResponseBuilder evaluateIfModifiedSince(final long lastModified) {
        final String ifModifiedSinceHeader = this.getHeaderString("If-Modified-Since");
        if (ifModifiedSinceHeader == null || ifModifiedSinceHeader.isEmpty()) {
            return null;
        }
        final String httpMethod = this.getMethod();
        if ("GET".equals(httpMethod) || "HEAD".equals(httpMethod)) {
            return this.evaluateIfModifiedSince(lastModified, ifModifiedSinceHeader);
        }
        return null;
    }
    
    private Response.ResponseBuilder evaluateIfModifiedSince(final long lastModified, final String ifModifiedSinceHeader) {
        try {
            final long ifModifiedSince = HttpHeaderReader.readDate(ifModifiedSinceHeader).getTime();
            if (roundDown(lastModified) <= ifModifiedSince) {
                return Response.notModified();
            }
        }
        catch (final ParseException ex) {}
        return null;
    }
    
    private static long roundDown(final long time) {
        return time - time % 1000L;
    }
    
    public List<String> getRequestHeader(final String name) {
        return (List)this.getHeaders().get((Object)name);
    }
    
    public MultivaluedMap<String, String> getRequestHeaders() {
        return (MultivaluedMap<String, String>)this.getHeaders();
    }
    
    void checkState() throws IllegalStateException {
        if (this.securityContext == null) {
            throw new IllegalStateException("SecurityContext set in the ContainerRequestContext must not be null.");
        }
        if (this.responseWriter == null) {
            throw new IllegalStateException("ResponseWriter set in the ContainerRequestContext must not be null.");
        }
    }
    
    static {
        DEFAULT_BASE_URI = URI.create("/");
        ERROR_REQUEST_SET_ENTITY_STREAM_IN_RESPONSE_PHASE = LocalizationMessages.ERROR_REQUEST_SET_ENTITY_STREAM_IN_RESPONSE_PHASE();
        ERROR_REQUEST_SET_SECURITY_CONTEXT_IN_RESPONSE_PHASE = LocalizationMessages.ERROR_REQUEST_SET_SECURITY_CONTEXT_IN_RESPONSE_PHASE();
        ERROR_REQUEST_ABORT_IN_RESPONSE_PHASE = LocalizationMessages.ERROR_REQUEST_ABORT_IN_RESPONSE_PHASE();
        METHOD_PARAMETER_CANNOT_BE_NULL_OR_EMPTY = LocalizationMessages.METHOD_PARAMETER_CANNOT_BE_NULL_OR_EMPTY("variants");
        METHOD_PARAMETER_CANNOT_BE_NULL_ETAG = LocalizationMessages.METHOD_PARAMETER_CANNOT_BE_NULL("eTag");
        METHOD_PARAMETER_CANNOT_BE_NULL_LAST_MODIFIED = LocalizationMessages.METHOD_PARAMETER_CANNOT_BE_NULL("lastModified");
    }
}
