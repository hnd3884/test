package javax.ws.rs.core;

import java.util.Map;
import java.lang.reflect.Method;
import java.net.URI;
import javax.ws.rs.ext.RuntimeDelegate;

public abstract class UriBuilder
{
    protected UriBuilder() {
    }
    
    protected static UriBuilder newInstance() {
        return RuntimeDelegate.getInstance().createUriBuilder();
    }
    
    public static UriBuilder fromUri(final URI uri) {
        return newInstance().uri(uri);
    }
    
    public static UriBuilder fromUri(final String uriTemplate) {
        return newInstance().uri(uriTemplate);
    }
    
    public static UriBuilder fromLink(final Link link) {
        if (link == null) {
            throw new IllegalArgumentException("The provider 'link' parameter value is 'null'.");
        }
        return fromUri(link.getUri());
    }
    
    public static UriBuilder fromPath(final String path) throws IllegalArgumentException {
        return newInstance().path(path);
    }
    
    public static UriBuilder fromResource(final Class<?> resource) {
        return newInstance().path(resource);
    }
    
    public static UriBuilder fromMethod(final Class<?> resource, final String method) {
        return newInstance().path(resource, method);
    }
    
    public abstract UriBuilder clone();
    
    public abstract UriBuilder uri(final URI p0);
    
    public abstract UriBuilder uri(final String p0);
    
    public abstract UriBuilder scheme(final String p0);
    
    public abstract UriBuilder schemeSpecificPart(final String p0);
    
    public abstract UriBuilder userInfo(final String p0);
    
    public abstract UriBuilder host(final String p0);
    
    public abstract UriBuilder port(final int p0);
    
    public abstract UriBuilder replacePath(final String p0);
    
    public abstract UriBuilder path(final String p0);
    
    public abstract UriBuilder path(final Class p0);
    
    public abstract UriBuilder path(final Class p0, final String p1);
    
    public abstract UriBuilder path(final Method p0);
    
    public abstract UriBuilder segment(final String... p0);
    
    public abstract UriBuilder replaceMatrix(final String p0);
    
    public abstract UriBuilder matrixParam(final String p0, final Object... p1);
    
    public abstract UriBuilder replaceMatrixParam(final String p0, final Object... p1);
    
    public abstract UriBuilder replaceQuery(final String p0);
    
    public abstract UriBuilder queryParam(final String p0, final Object... p1);
    
    public abstract UriBuilder replaceQueryParam(final String p0, final Object... p1);
    
    public abstract UriBuilder fragment(final String p0);
    
    public abstract UriBuilder resolveTemplate(final String p0, final Object p1);
    
    public abstract UriBuilder resolveTemplate(final String p0, final Object p1, final boolean p2);
    
    public abstract UriBuilder resolveTemplateFromEncoded(final String p0, final Object p1);
    
    public abstract UriBuilder resolveTemplates(final Map<String, Object> p0);
    
    public abstract UriBuilder resolveTemplates(final Map<String, Object> p0, final boolean p1) throws IllegalArgumentException;
    
    public abstract UriBuilder resolveTemplatesFromEncoded(final Map<String, Object> p0);
    
    public abstract URI buildFromMap(final Map<String, ?> p0);
    
    public abstract URI buildFromMap(final Map<String, ?> p0, final boolean p1) throws IllegalArgumentException, UriBuilderException;
    
    public abstract URI buildFromEncodedMap(final Map<String, ?> p0) throws IllegalArgumentException, UriBuilderException;
    
    public abstract URI build(final Object... p0) throws IllegalArgumentException, UriBuilderException;
    
    public abstract URI build(final Object[] p0, final boolean p1) throws IllegalArgumentException, UriBuilderException;
    
    public abstract URI buildFromEncoded(final Object... p0) throws IllegalArgumentException, UriBuilderException;
    
    public abstract String toTemplate();
}
