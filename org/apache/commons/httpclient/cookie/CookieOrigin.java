package org.apache.commons.httpclient.cookie;

public final class CookieOrigin
{
    private final String host;
    private final int port;
    private final String path;
    private final boolean secure;
    
    public CookieOrigin(final String host, final int port, final String path, final boolean secure) {
        if (host == null) {
            throw new IllegalArgumentException("Host of origin may not be null");
        }
        if (host.trim().equals("")) {
            throw new IllegalArgumentException("Host of origin may not be blank");
        }
        if (port < 0) {
            throw new IllegalArgumentException("Invalid port: " + port);
        }
        if (path == null) {
            throw new IllegalArgumentException("Path of origin may not be null.");
        }
        this.host = host;
        this.port = port;
        this.path = path;
        this.secure = secure;
    }
    
    public String getHost() {
        return this.host;
    }
    
    public String getPath() {
        return this.path;
    }
    
    public int getPort() {
        return this.port;
    }
    
    public boolean isSecure() {
        return this.secure;
    }
}
