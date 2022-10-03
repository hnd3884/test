package javax.servlet;

public interface SessionCookieConfig
{
    void setName(final String p0);
    
    String getName();
    
    void setDomain(final String p0);
    
    String getDomain();
    
    void setPath(final String p0);
    
    String getPath();
    
    void setComment(final String p0);
    
    String getComment();
    
    void setHttpOnly(final boolean p0);
    
    boolean isHttpOnly();
    
    void setSecure(final boolean p0);
    
    boolean isSecure();
    
    void setMaxAge(final int p0);
    
    int getMaxAge();
}
