package org.apache.commons.httpclient.params;

public class HttpClientParams extends HttpMethodParams
{
    public static final String CONNECTION_MANAGER_TIMEOUT = "http.connection-manager.timeout";
    public static final String CONNECTION_MANAGER_CLASS = "http.connection-manager.class";
    public static final String PREEMPTIVE_AUTHENTICATION = "http.authentication.preemptive";
    public static final String REJECT_RELATIVE_REDIRECT = "http.protocol.reject-relative-redirect";
    public static final String MAX_REDIRECTS = "http.protocol.max-redirects";
    public static final String ALLOW_CIRCULAR_REDIRECTS = "http.protocol.allow-circular-redirects";
    private static final String[] PROTOCOL_STRICTNESS_PARAMETERS;
    
    public HttpClientParams() {
    }
    
    public HttpClientParams(final HttpParams defaults) {
        super(defaults);
    }
    
    public long getConnectionManagerTimeout() {
        return this.getLongParameter("http.connection-manager.timeout", 0L);
    }
    
    public void setConnectionManagerTimeout(final long timeout) {
        this.setLongParameter("http.connection-manager.timeout", timeout);
    }
    
    public Class getConnectionManagerClass() {
        return (Class)this.getParameter("http.connection-manager.class");
    }
    
    public void setConnectionManagerClass(final Class clazz) {
        this.setParameter("http.connection-manager.class", clazz);
    }
    
    public boolean isAuthenticationPreemptive() {
        return this.getBooleanParameter("http.authentication.preemptive", false);
    }
    
    public void setAuthenticationPreemptive(final boolean value) {
        this.setBooleanParameter("http.authentication.preemptive", value);
    }
    
    public void makeStrict() {
        super.makeStrict();
        this.setParameters(HttpClientParams.PROTOCOL_STRICTNESS_PARAMETERS, Boolean.TRUE);
    }
    
    public void makeLenient() {
        super.makeLenient();
        this.setParameters(HttpClientParams.PROTOCOL_STRICTNESS_PARAMETERS, Boolean.FALSE);
    }
    
    static {
        PROTOCOL_STRICTNESS_PARAMETERS = new String[] { "http.protocol.reject-relative-redirect", "http.protocol.allow-circular-redirects" };
    }
}
