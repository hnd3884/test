package org.glassfish.jersey.server.internal.routing;

import org.glassfish.jersey.server.model.ResourceMethodInvoker;
import java.lang.reflect.Method;
import javax.ws.rs.core.PathSegment;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.Collections;
import java.util.List;
import org.glassfish.jersey.uri.internal.JerseyUriBuilder;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import org.glassfish.jersey.uri.UriComponent;
import java.util.Iterator;
import org.glassfish.jersey.server.internal.ServerTraceEvent;
import org.glassfish.jersey.internal.PropertiesDelegate;
import javax.ws.rs.core.MultivaluedMap;
import java.util.function.Function;
import org.glassfish.jersey.server.ContainerRequest;
import org.glassfish.jersey.server.internal.process.Endpoint;
import org.glassfish.jersey.message.internal.TracingLogger;
import org.glassfish.jersey.server.model.Resource;
import org.glassfish.jersey.server.model.ResourceMethod;
import org.glassfish.jersey.server.model.RuntimeResource;
import org.glassfish.jersey.internal.util.collection.ImmutableMultivaluedMap;
import javax.ws.rs.core.MultivaluedHashMap;
import org.glassfish.jersey.uri.UriTemplate;
import java.util.regex.MatchResult;
import java.util.LinkedList;

public class UriRoutingContext implements RoutingContext
{
    private final LinkedList<MatchResult> matchResults;
    private final LinkedList<Object> matchedResources;
    private final LinkedList<UriTemplate> templates;
    private final MultivaluedHashMap<String, String> encodedTemplateValues;
    private final ImmutableMultivaluedMap<String, String> encodedTemplateValuesView;
    private final LinkedList<String> paths;
    private final LinkedList<RuntimeResource> matchedRuntimeResources;
    private final LinkedList<ResourceMethod> matchedLocators;
    private final LinkedList<Resource> locatorSubResources;
    private final TracingLogger tracingLogger;
    private volatile ResourceMethod matchedResourceMethod;
    private volatile Throwable mappedThrowable;
    private Endpoint endpoint;
    private MultivaluedHashMap<String, String> decodedTemplateValues;
    private ImmutableMultivaluedMap<String, String> decodedTemplateValuesView;
    private ImmutableMultivaluedMap<String, String> encodedQueryParamsView;
    private ImmutableMultivaluedMap<String, String> decodedQueryParamsView;
    private final ContainerRequest requestContext;
    private static final Function<String, String> PATH_DECODER;
    
    public UriRoutingContext(final ContainerRequest requestContext) {
        this.matchResults = new LinkedList<MatchResult>();
        this.matchedResources = new LinkedList<Object>();
        this.templates = new LinkedList<UriTemplate>();
        this.encodedTemplateValues = (MultivaluedHashMap<String, String>)new MultivaluedHashMap();
        this.encodedTemplateValuesView = (ImmutableMultivaluedMap<String, String>)new ImmutableMultivaluedMap((MultivaluedMap)this.encodedTemplateValues);
        this.paths = new LinkedList<String>();
        this.matchedRuntimeResources = new LinkedList<RuntimeResource>();
        this.matchedLocators = new LinkedList<ResourceMethod>();
        this.locatorSubResources = new LinkedList<Resource>();
        this.matchedResourceMethod = null;
        this.mappedThrowable = null;
        this.requestContext = requestContext;
        this.tracingLogger = TracingLogger.getInstance((PropertiesDelegate)requestContext);
    }
    
    @Override
    public void pushMatchResult(final MatchResult matchResult) {
        this.matchResults.push(matchResult);
    }
    
    @Override
    public void pushMatchedResource(final Object resource) {
        this.tracingLogger.log((TracingLogger.Event)ServerTraceEvent.MATCH_RESOURCE, new Object[] { resource });
        this.matchedResources.push(resource);
    }
    
    @Override
    public Object peekMatchedResource() {
        return this.matchedResources.peek();
    }
    
    @Override
    public void pushMatchedLocator(final ResourceMethod resourceLocator) {
        this.tracingLogger.log((TracingLogger.Event)ServerTraceEvent.MATCH_LOCATOR, new Object[] { resourceLocator.getInvocable().getHandlingMethod() });
        this.matchedLocators.push(resourceLocator);
    }
    
    @Override
    public void pushLeftHandPath() {
        final String rightHandPath = this.getFinalMatchingGroup();
        final int rhpLength = (rightHandPath != null) ? rightHandPath.length() : 0;
        final String encodedRequestPath = this.getPath(false);
        final int length = encodedRequestPath.length() - rhpLength;
        if (length <= 0) {
            this.paths.addFirst("");
        }
        else {
            this.paths.addFirst(encodedRequestPath.substring(0, length));
        }
    }
    
    @Override
    public void pushTemplates(final UriTemplate resourceTemplate, final UriTemplate methodTemplate) {
        final Iterator<MatchResult> matchResultIterator = this.matchResults.iterator();
        this.templates.push(resourceTemplate);
        if (methodTemplate != null) {
            this.templates.push(methodTemplate);
            matchResultIterator.next();
        }
        this.pushMatchedTemplateValues(resourceTemplate, matchResultIterator.next());
        if (methodTemplate != null) {
            this.pushMatchedTemplateValues(methodTemplate, this.matchResults.peek());
        }
    }
    
    private void pushMatchedTemplateValues(final UriTemplate template, final MatchResult matchResult) {
        int i = 1;
        for (final String templateVariable : template.getTemplateVariables()) {
            final String value = matchResult.group(i++);
            this.encodedTemplateValues.addFirst((Object)templateVariable, (Object)value);
            if (this.decodedTemplateValues != null) {
                this.decodedTemplateValues.addFirst((Object)UriComponent.decode(templateVariable, UriComponent.Type.PATH_SEGMENT), (Object)UriComponent.decode(value, UriComponent.Type.PATH));
            }
        }
    }
    
    @Override
    public String getFinalMatchingGroup() {
        final MatchResult mr = this.matchResults.peek();
        if (mr == null) {
            return null;
        }
        final String finalGroup = mr.group(mr.groupCount());
        return (finalGroup == null) ? "" : finalGroup;
    }
    
    public LinkedList<MatchResult> getMatchedResults() {
        return this.matchResults;
    }
    
    @Override
    public void setEndpoint(final Endpoint endpoint) {
        this.endpoint = endpoint;
    }
    
    @Override
    public Endpoint getEndpoint() {
        return this.endpoint;
    }
    
    @Override
    public void setMatchedResourceMethod(final ResourceMethod resourceMethod) {
        this.tracingLogger.log((TracingLogger.Event)ServerTraceEvent.MATCH_RESOURCE_METHOD, new Object[] { resourceMethod.getInvocable().getHandlingMethod() });
        this.matchedResourceMethod = resourceMethod;
    }
    
    @Override
    public void pushMatchedRuntimeResource(final RuntimeResource runtimeResource) {
        if (this.tracingLogger.isLogEnabled((TracingLogger.Event)ServerTraceEvent.MATCH_RUNTIME_RESOURCE)) {
            this.tracingLogger.log((TracingLogger.Event)ServerTraceEvent.MATCH_RUNTIME_RESOURCE, new Object[] { runtimeResource.getResources().get(0).getPath(), runtimeResource.getResources().get(0).getPathPattern().getRegex(), this.matchResults.peek().group().substring(0, this.matchResults.peek().group().length() - this.getFinalMatchingGroup().length()), this.matchResults.peek().group() });
        }
        this.matchedRuntimeResources.push(runtimeResource);
    }
    
    @Override
    public void pushLocatorSubResource(final Resource subResourceFromLocator) {
        this.locatorSubResources.push(subResourceFromLocator);
    }
    
    public URI getAbsolutePath() {
        return this.requestContext.getAbsolutePath();
    }
    
    public UriBuilder getAbsolutePathBuilder() {
        return (UriBuilder)new JerseyUriBuilder().uri(this.getAbsolutePath());
    }
    
    public URI getBaseUri() {
        return this.requestContext.getBaseUri();
    }
    
    public UriBuilder getBaseUriBuilder() {
        return (UriBuilder)new JerseyUriBuilder().uri(this.getBaseUri());
    }
    
    public List<Object> getMatchedResources() {
        return Collections.unmodifiableList((List<?>)this.matchedResources);
    }
    
    public List<String> getMatchedURIs() {
        return this.getMatchedURIs(true);
    }
    
    public List<String> getMatchedURIs(final boolean decode) {
        List<String> result;
        if (decode) {
            result = this.paths.stream().map((Function<? super Object, ?>)UriRoutingContext.PATH_DECODER).collect((Collector<? super Object, ?, List<String>>)Collectors.toList());
        }
        else {
            result = this.paths;
        }
        return Collections.unmodifiableList((List<? extends String>)result);
    }
    
    public String getPath() {
        return this.requestContext.getPath(true);
    }
    
    public String getPath(final boolean decode) {
        return this.requestContext.getPath(decode);
    }
    
    public MultivaluedMap<String, String> getPathParameters() {
        return this.getPathParameters(true);
    }
    
    public MultivaluedMap<String, String> getPathParameters(final boolean decode) {
        if (!decode) {
            return (MultivaluedMap<String, String>)this.encodedTemplateValuesView;
        }
        if (this.decodedTemplateValuesView != null) {
            return (MultivaluedMap<String, String>)this.decodedTemplateValuesView;
        }
        if (this.decodedTemplateValues == null) {
            this.decodedTemplateValues = (MultivaluedHashMap<String, String>)new MultivaluedHashMap();
            for (final Map.Entry<String, List<String>> e : this.encodedTemplateValues.entrySet()) {
                this.decodedTemplateValues.put((Object)UriComponent.decode((String)e.getKey(), UriComponent.Type.PATH_SEGMENT), (List)e.getValue().stream().map(s -> UriComponent.decode(s, UriComponent.Type.PATH)).collect(Collectors.toCollection(ArrayList::new)));
            }
        }
        return (MultivaluedMap<String, String>)(this.decodedTemplateValuesView = (ImmutableMultivaluedMap<String, String>)new ImmutableMultivaluedMap((MultivaluedMap)this.decodedTemplateValues));
    }
    
    public List<PathSegment> getPathSegments() {
        return this.getPathSegments(true);
    }
    
    public List<PathSegment> getPathSegments(final boolean decode) {
        final String requestPath = this.requestContext.getPath(false);
        return Collections.unmodifiableList((List<? extends PathSegment>)UriComponent.decodePath(requestPath, decode));
    }
    
    public MultivaluedMap<String, String> getQueryParameters() {
        return this.getQueryParameters(true);
    }
    
    public MultivaluedMap<String, String> getQueryParameters(final boolean decode) {
        if (decode) {
            if (this.decodedQueryParamsView != null) {
                return (MultivaluedMap<String, String>)this.decodedQueryParamsView;
            }
            return (MultivaluedMap<String, String>)(this.decodedQueryParamsView = (ImmutableMultivaluedMap<String, String>)new ImmutableMultivaluedMap(UriComponent.decodeQuery(this.getRequestUri(), true)));
        }
        else {
            if (this.encodedQueryParamsView != null) {
                return (MultivaluedMap<String, String>)this.encodedQueryParamsView;
            }
            return (MultivaluedMap<String, String>)(this.encodedQueryParamsView = (ImmutableMultivaluedMap<String, String>)new ImmutableMultivaluedMap(UriComponent.decodeQuery(this.getRequestUri(), false)));
        }
    }
    
    public void invalidateUriComponentViews() {
        this.decodedQueryParamsView = null;
        this.encodedQueryParamsView = null;
    }
    
    public URI getRequestUri() {
        return this.requestContext.getRequestUri();
    }
    
    public UriBuilder getRequestUriBuilder() {
        return UriBuilder.fromUri(this.getRequestUri());
    }
    
    public Throwable getMappedThrowable() {
        return this.mappedThrowable;
    }
    
    @Override
    public void setMappedThrowable(final Throwable mappedThrowable) {
        this.mappedThrowable = mappedThrowable;
    }
    
    public List<UriTemplate> getMatchedTemplates() {
        return Collections.unmodifiableList((List<? extends UriTemplate>)this.templates);
    }
    
    public List<PathSegment> getPathSegments(final String name) {
        return this.getPathSegments(name, true);
    }
    
    public List<PathSegment> getPathSegments(final String name, final boolean decode) {
        final int[] bounds = this.getPathParameterBounds(name);
        if (bounds != null) {
            final String path = this.matchResults.getLast().group();
            int segmentsStart = 0;
            for (int x = 0; x < bounds[0]; ++x) {
                if (path.charAt(x) == '/') {
                    ++segmentsStart;
                }
            }
            int segmentsEnd = segmentsStart;
            for (int x2 = bounds[0]; x2 < bounds[1]; ++x2) {
                if (path.charAt(x2) == '/') {
                    ++segmentsEnd;
                }
            }
            return this.getPathSegments(decode).subList(segmentsStart - 1, segmentsEnd);
        }
        return Collections.emptyList();
    }
    
    private int[] getPathParameterBounds(final String name) {
        final Iterator<UriTemplate> templatesIterator = this.templates.iterator();
        final Iterator<MatchResult> matchResultsIterator = this.matchResults.iterator();
        while (templatesIterator.hasNext()) {
            MatchResult mr = matchResultsIterator.next();
            final int pIndex = this.getLastPathParameterIndex(name, templatesIterator.next());
            if (pIndex != -1) {
                int pathLength = mr.group().length();
                int segmentIndex = mr.end(pIndex + 1);
                final int groupLength = segmentIndex - mr.start(pIndex + 1);
                while (matchResultsIterator.hasNext()) {
                    mr = matchResultsIterator.next();
                    segmentIndex += mr.group().length() - pathLength;
                    pathLength = mr.group().length();
                }
                return new int[] { segmentIndex - groupLength, segmentIndex };
            }
        }
        return null;
    }
    
    private int getLastPathParameterIndex(final String name, final UriTemplate t) {
        int i = 0;
        int pIndex = -1;
        for (final String parameterName : t.getTemplateVariables()) {
            if (parameterName.equals(name)) {
                pIndex = i;
            }
            ++i;
        }
        return pIndex;
    }
    
    public Method getResourceMethod() {
        return (this.endpoint instanceof ResourceMethodInvoker) ? ((ResourceMethodInvoker)this.endpoint).getResourceMethod() : null;
    }
    
    public Class<?> getResourceClass() {
        return (this.endpoint instanceof ResourceMethodInvoker) ? ((ResourceMethodInvoker)this.endpoint).getResourceClass() : null;
    }
    
    public List<RuntimeResource> getMatchedRuntimeResources() {
        return this.matchedRuntimeResources;
    }
    
    public ResourceMethod getMatchedResourceMethod() {
        return this.matchedResourceMethod;
    }
    
    public List<ResourceMethod> getMatchedResourceLocators() {
        return this.matchedLocators;
    }
    
    public List<Resource> getLocatorSubResources() {
        return this.locatorSubResources;
    }
    
    public Resource getMatchedModelResource() {
        return (this.matchedResourceMethod == null) ? null : this.matchedResourceMethod.getParent();
    }
    
    public URI resolve(final URI uri) {
        return UriTemplate.resolve(this.getBaseUri(), uri);
    }
    
    public URI relativize(URI uri) {
        if (!uri.isAbsolute()) {
            uri = this.resolve(uri);
        }
        return UriTemplate.relativize(this.getRequestUri(), uri);
    }
    
    static {
        PATH_DECODER = (input -> UriComponent.decode(input, UriComponent.Type.PATH));
    }
}
