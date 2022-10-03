package org.glassfish.jersey.server.internal;

import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.Arrays;
import java.util.HashMap;
import java.lang.annotation.Annotation;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import javax.ws.rs.WebApplicationException;
import java.io.IOException;
import org.glassfish.jersey.server.JSONP;
import org.glassfish.jersey.message.MessageUtils;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.InterceptorContext;
import javax.ws.rs.ext.WriterInterceptorContext;
import javax.inject.Inject;
import org.glassfish.jersey.server.ContainerRequest;
import javax.inject.Provider;
import java.util.Set;
import java.util.Map;
import javax.annotation.Priority;
import javax.ws.rs.ext.WriterInterceptor;

@Priority(4100)
public class JsonWithPaddingInterceptor implements WriterInterceptor
{
    private static final Map<String, Set<String>> JAVASCRIPT_TYPES;
    @Inject
    private Provider<ContainerRequest> containerRequestProvider;
    
    public void aroundWriteTo(final WriterInterceptorContext context) throws IOException, WebApplicationException {
        final boolean isJavascript = this.isJavascript(context.getMediaType());
        final JSONP jsonp = this.getJsonpAnnotation((InterceptorContext)context);
        final boolean wrapIntoCallback = isJavascript && jsonp != null;
        if (wrapIntoCallback) {
            context.setMediaType(MediaType.APPLICATION_JSON_TYPE);
            context.getOutputStream().write(this.getCallbackName(jsonp).getBytes(MessageUtils.getCharset(context.getMediaType())));
            context.getOutputStream().write(40);
        }
        context.proceed();
        if (wrapIntoCallback) {
            context.getOutputStream().write(41);
        }
    }
    
    private boolean isJavascript(final MediaType mediaType) {
        if (mediaType == null) {
            return false;
        }
        final Set<String> subtypes = JsonWithPaddingInterceptor.JAVASCRIPT_TYPES.get(mediaType.getType());
        return subtypes != null && subtypes.contains(mediaType.getSubtype());
    }
    
    private String getCallbackName(final JSONP jsonp) {
        String callback = jsonp.callback();
        if (!"".equals(jsonp.queryParam())) {
            final ContainerRequest containerRequest = (ContainerRequest)this.containerRequestProvider.get();
            final UriInfo uriInfo = (UriInfo)containerRequest.getUriInfo();
            final MultivaluedMap<String, String> queryParameters = (MultivaluedMap<String, String>)uriInfo.getQueryParameters();
            final List<String> queryParameter = (List<String>)queryParameters.get((Object)jsonp.queryParam());
            callback = ((queryParameter != null && !queryParameter.isEmpty()) ? queryParameter.get(0) : callback);
        }
        return callback;
    }
    
    private JSONP getJsonpAnnotation(final InterceptorContext context) {
        final Annotation[] annotations = context.getAnnotations();
        if (annotations != null && annotations.length > 0) {
            for (final Annotation annotation : annotations) {
                if (annotation instanceof JSONP) {
                    return (JSONP)annotation;
                }
            }
        }
        return null;
    }
    
    static {
        (JAVASCRIPT_TYPES = new HashMap<String, Set<String>>(2)).put("application", Arrays.asList("x-javascript", "ecmascript", "javascript").stream().collect((Collector<? super Object, ?, Set<? super Object>>)Collectors.toSet()));
        JsonWithPaddingInterceptor.JAVASCRIPT_TYPES.put("text", (Set<String>)Arrays.asList("javascript", "x-javascript", "ecmascript", "jscript").stream().collect((Collector<? super Object, ?, Set<? super Object>>)Collectors.toSet()));
    }
}
