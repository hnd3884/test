package javax.servlet.http;

import java.util.Enumeration;
import javax.servlet.ServletContext;

public interface HttpSession
{
    long getCreationTime();
    
    String getId();
    
    long getLastAccessedTime();
    
    ServletContext getServletContext();
    
    void setMaxInactiveInterval(final int p0);
    
    int getMaxInactiveInterval();
    
    @Deprecated
    HttpSessionContext getSessionContext();
    
    Object getAttribute(final String p0);
    
    @Deprecated
    Object getValue(final String p0);
    
    Enumeration<String> getAttributeNames();
    
    @Deprecated
    String[] getValueNames();
    
    void setAttribute(final String p0, final Object p1);
    
    @Deprecated
    void putValue(final String p0, final Object p1);
    
    void removeAttribute(final String p0);
    
    @Deprecated
    void removeValue(final String p0);
    
    void invalidate();
    
    boolean isNew();
}
