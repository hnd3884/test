package org.apache.commons.httpclient.params;

public class HostParams extends DefaultHttpParams
{
    public static final String DEFAULT_HEADERS = "http.default-headers";
    
    public HostParams() {
    }
    
    public HostParams(final HttpParams defaults) {
        super(defaults);
    }
    
    public void setVirtualHost(final String hostname) {
        this.setParameter("http.virtual-host", hostname);
    }
    
    public String getVirtualHost() {
        return (String)this.getParameter("http.virtual-host");
    }
}
