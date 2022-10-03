package javax.ws.rs.client;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Configuration;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.ws.rs.core.Cookie;
import java.util.Map;
import java.util.List;
import javax.ws.rs.core.MediaType;
import java.util.Locale;
import java.util.Date;
import javax.ws.rs.core.MultivaluedMap;
import java.net.URI;
import java.util.Collection;

public interface ClientRequestContext
{
    Object getProperty(final String p0);
    
    Collection<String> getPropertyNames();
    
    void setProperty(final String p0, final Object p1);
    
    void removeProperty(final String p0);
    
    URI getUri();
    
    void setUri(final URI p0);
    
    String getMethod();
    
    void setMethod(final String p0);
    
    MultivaluedMap<String, Object> getHeaders();
    
    MultivaluedMap<String, String> getStringHeaders();
    
    String getHeaderString(final String p0);
    
    Date getDate();
    
    Locale getLanguage();
    
    MediaType getMediaType();
    
    List<MediaType> getAcceptableMediaTypes();
    
    List<Locale> getAcceptableLanguages();
    
    Map<String, Cookie> getCookies();
    
    boolean hasEntity();
    
    Object getEntity();
    
    Class<?> getEntityClass();
    
    Type getEntityType();
    
    void setEntity(final Object p0);
    
    void setEntity(final Object p0, final Annotation[] p1, final MediaType p2);
    
    Annotation[] getEntityAnnotations();
    
    OutputStream getEntityStream();
    
    void setEntityStream(final OutputStream p0);
    
    Client getClient();
    
    Configuration getConfiguration();
    
    void abortWith(final Response p0);
}
