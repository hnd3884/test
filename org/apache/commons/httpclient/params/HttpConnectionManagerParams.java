package org.apache.commons.httpclient.params;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.httpclient.HostConfiguration;

public class HttpConnectionManagerParams extends HttpConnectionParams
{
    public static final String MAX_HOST_CONNECTIONS = "http.connection-manager.max-per-host";
    public static final String MAX_TOTAL_CONNECTIONS = "http.connection-manager.max-total";
    
    public void setDefaultMaxConnectionsPerHost(final int maxHostConnections) {
        this.setMaxConnectionsPerHost(HostConfiguration.ANY_HOST_CONFIGURATION, maxHostConnections);
    }
    
    public void setMaxConnectionsPerHost(final HostConfiguration hostConfiguration, final int maxHostConnections) {
        if (maxHostConnections <= 0) {
            throw new IllegalArgumentException("maxHostConnections must be greater than 0");
        }
        final Map currentValues = (Map)this.getParameter("http.connection-manager.max-per-host");
        Map newValues = null;
        if (currentValues == null) {
            newValues = new HashMap();
        }
        else {
            newValues = new HashMap(currentValues);
        }
        newValues.put(hostConfiguration, new Integer(maxHostConnections));
        this.setParameter("http.connection-manager.max-per-host", newValues);
    }
    
    public int getDefaultMaxConnectionsPerHost() {
        return this.getMaxConnectionsPerHost(HostConfiguration.ANY_HOST_CONFIGURATION);
    }
    
    public int getMaxConnectionsPerHost(final HostConfiguration hostConfiguration) {
        final Map m = (Map)this.getParameter("http.connection-manager.max-per-host");
        if (m == null) {
            return 2;
        }
        final Integer max = m.get(hostConfiguration);
        if (max == null && hostConfiguration != HostConfiguration.ANY_HOST_CONFIGURATION) {
            return this.getMaxConnectionsPerHost(HostConfiguration.ANY_HOST_CONFIGURATION);
        }
        return (max == null) ? 2 : max;
    }
    
    public void setMaxTotalConnections(final int maxTotalConnections) {
        this.setIntParameter("http.connection-manager.max-total", maxTotalConnections);
    }
    
    public int getMaxTotalConnections() {
        return this.getIntParameter("http.connection-manager.max-total", 20);
    }
}
