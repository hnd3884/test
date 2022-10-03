package javax.ws.rs.container;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.io.InputStream;
import javax.ws.rs.core.Cookie;
import java.util.Map;
import java.util.List;
import javax.ws.rs.core.MediaType;
import java.util.Locale;
import java.util.Date;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Request;
import java.net.URI;
import javax.ws.rs.core.UriInfo;
import java.util.Collection;

public interface ContainerRequestContext
{
    Object getProperty(final String p0);
    
    Collection<String> getPropertyNames();
    
    void setProperty(final String p0, final Object p1);
    
    void removeProperty(final String p0);
    
    UriInfo getUriInfo();
    
    void setRequestUri(final URI p0);
    
    void setRequestUri(final URI p0, final URI p1);
    
    Request getRequest();
    
    String getMethod();
    
    void setMethod(final String p0);
    
    MultivaluedMap<String, String> getHeaders();
    
    String getHeaderString(final String p0);
    
    Date getDate();
    
    Locale getLanguage();
    
    int getLength();
    
    MediaType getMediaType();
    
    List<MediaType> getAcceptableMediaTypes();
    
    List<Locale> getAcceptableLanguages();
    
    Map<String, Cookie> getCookies();
    
    boolean hasEntity();
    
    InputStream getEntityStream();
    
    void setEntityStream(final InputStream p0);
    
    SecurityContext getSecurityContext();
    
    void setSecurityContext(final SecurityContext p0);
    
    void abortWith(final Response p0);
}
