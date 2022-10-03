package javax.ws.rs.container;

import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.ws.rs.core.Link;
import java.net.URI;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.NewCookie;
import java.util.Map;
import javax.ws.rs.core.MediaType;
import java.util.Locale;
import java.util.Date;
import java.util.Set;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

public interface ContainerResponseContext
{
    int getStatus();
    
    void setStatus(final int p0);
    
    Response.StatusType getStatusInfo();
    
    void setStatusInfo(final Response.StatusType p0);
    
    MultivaluedMap<String, Object> getHeaders();
    
    MultivaluedMap<String, String> getStringHeaders();
    
    String getHeaderString(final String p0);
    
    Set<String> getAllowedMethods();
    
    Date getDate();
    
    Locale getLanguage();
    
    int getLength();
    
    MediaType getMediaType();
    
    Map<String, NewCookie> getCookies();
    
    EntityTag getEntityTag();
    
    Date getLastModified();
    
    URI getLocation();
    
    Set<Link> getLinks();
    
    boolean hasLink(final String p0);
    
    Link getLink(final String p0);
    
    Link.Builder getLinkBuilder(final String p0);
    
    boolean hasEntity();
    
    Object getEntity();
    
    Class<?> getEntityClass();
    
    Type getEntityType();
    
    void setEntity(final Object p0);
    
    void setEntity(final Object p0, final Annotation[] p1, final MediaType p2);
    
    Annotation[] getEntityAnnotations();
    
    OutputStream getEntityStream();
    
    void setEntityStream(final OutputStream p0);
}
