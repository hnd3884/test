package javax.ws.rs.client;

import javax.ws.rs.core.MediaType;
import java.util.Map;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import javax.ws.rs.core.Configurable;

public interface WebTarget extends Configurable<WebTarget>
{
    URI getUri();
    
    UriBuilder getUriBuilder();
    
    WebTarget path(final String p0);
    
    WebTarget resolveTemplate(final String p0, final Object p1);
    
    WebTarget resolveTemplate(final String p0, final Object p1, final boolean p2);
    
    WebTarget resolveTemplateFromEncoded(final String p0, final Object p1);
    
    WebTarget resolveTemplates(final Map<String, Object> p0);
    
    WebTarget resolveTemplates(final Map<String, Object> p0, final boolean p1);
    
    WebTarget resolveTemplatesFromEncoded(final Map<String, Object> p0);
    
    WebTarget matrixParam(final String p0, final Object... p1);
    
    WebTarget queryParam(final String p0, final Object... p1);
    
    Invocation.Builder request();
    
    Invocation.Builder request(final String... p0);
    
    Invocation.Builder request(final MediaType... p0);
}
