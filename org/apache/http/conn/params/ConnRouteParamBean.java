package org.apache.http.conn.params;

import org.apache.http.conn.routing.HttpRoute;
import java.net.InetAddress;
import org.apache.http.HttpHost;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpAbstractParamBean;

@Deprecated
public class ConnRouteParamBean extends HttpAbstractParamBean
{
    public ConnRouteParamBean(final HttpParams params) {
        super(params);
    }
    
    public void setDefaultProxy(final HttpHost defaultProxy) {
        this.params.setParameter("http.route.default-proxy", (Object)defaultProxy);
    }
    
    public void setLocalAddress(final InetAddress address) {
        this.params.setParameter("http.route.local-address", (Object)address);
    }
    
    public void setForcedRoute(final HttpRoute route) {
        this.params.setParameter("http.route.forced-route", (Object)route);
    }
}
