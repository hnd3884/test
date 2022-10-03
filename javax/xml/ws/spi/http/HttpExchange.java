package javax.xml.ws.spi.http;

import java.security.Principal;
import java.util.Set;
import java.net.InetSocketAddress;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public abstract class HttpExchange
{
    public static final String REQUEST_CIPHER_SUITE = "javax.xml.ws.spi.http.request.cipher.suite";
    public static final String REQUEST_KEY_SIZE = "javax.xml.ws.spi.http.request.key.size";
    public static final String REQUEST_X509CERTIFICATE = "javax.xml.ws.spi.http.request.cert.X509Certificate";
    
    public abstract Map<String, List<String>> getRequestHeaders();
    
    public abstract String getRequestHeader(final String p0);
    
    public abstract Map<String, List<String>> getResponseHeaders();
    
    public abstract void addResponseHeader(final String p0, final String p1);
    
    public abstract String getRequestURI();
    
    public abstract String getContextPath();
    
    public abstract String getRequestMethod();
    
    public abstract HttpContext getHttpContext();
    
    public abstract void close() throws IOException;
    
    public abstract InputStream getRequestBody() throws IOException;
    
    public abstract OutputStream getResponseBody() throws IOException;
    
    public abstract void setStatus(final int p0);
    
    public abstract InetSocketAddress getRemoteAddress();
    
    public abstract InetSocketAddress getLocalAddress();
    
    public abstract String getProtocol();
    
    public abstract String getScheme();
    
    public abstract String getPathInfo();
    
    public abstract String getQueryString();
    
    public abstract Object getAttribute(final String p0);
    
    public abstract Set<String> getAttributeNames();
    
    public abstract Principal getUserPrincipal();
    
    public abstract boolean isUserInRole(final String p0);
}
