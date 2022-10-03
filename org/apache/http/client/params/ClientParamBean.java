package org.apache.http.client.params;

import org.apache.http.Header;
import java.util.Collection;
import org.apache.http.HttpHost;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpAbstractParamBean;

@Deprecated
public class ClientParamBean extends HttpAbstractParamBean
{
    public ClientParamBean(final HttpParams params) {
        super(params);
    }
    
    @Deprecated
    public void setConnectionManagerFactoryClassName(final String factory) {
        this.params.setParameter("http.connection-manager.factory-class-name", (Object)factory);
    }
    
    public void setHandleRedirects(final boolean handle) {
        this.params.setBooleanParameter("http.protocol.handle-redirects", handle);
    }
    
    public void setRejectRelativeRedirect(final boolean reject) {
        this.params.setBooleanParameter("http.protocol.reject-relative-redirect", reject);
    }
    
    public void setMaxRedirects(final int maxRedirects) {
        this.params.setIntParameter("http.protocol.max-redirects", maxRedirects);
    }
    
    public void setAllowCircularRedirects(final boolean allow) {
        this.params.setBooleanParameter("http.protocol.allow-circular-redirects", allow);
    }
    
    public void setHandleAuthentication(final boolean handle) {
        this.params.setBooleanParameter("http.protocol.handle-authentication", handle);
    }
    
    public void setCookiePolicy(final String policy) {
        this.params.setParameter("http.protocol.cookie-policy", (Object)policy);
    }
    
    public void setVirtualHost(final HttpHost host) {
        this.params.setParameter("http.virtual-host", (Object)host);
    }
    
    public void setDefaultHeaders(final Collection<Header> headers) {
        this.params.setParameter("http.default-headers", (Object)headers);
    }
    
    public void setDefaultHost(final HttpHost host) {
        this.params.setParameter("http.default-host", (Object)host);
    }
    
    public void setConnectionManagerTimeout(final long timeout) {
        this.params.setLongParameter("http.conn-manager.timeout", timeout);
    }
}
