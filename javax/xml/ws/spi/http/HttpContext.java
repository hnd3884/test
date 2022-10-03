package javax.xml.ws.spi.http;

import java.util.Set;

public abstract class HttpContext
{
    protected HttpHandler handler;
    
    public void setHandler(final HttpHandler handler) {
        this.handler = handler;
    }
    
    public abstract String getPath();
    
    public abstract Object getAttribute(final String p0);
    
    public abstract Set<String> getAttributeNames();
}
